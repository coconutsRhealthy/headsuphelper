package com.lennart.model.imageprocessing;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;
import static org.bytedeco.javacpp.lept.pixReadMem;

/**
 * Created by LPO21630 on 7-2-2017.
 */
public class ImageProcessor {

    public void createPartialSreenShot(int x, int y, int width, int height, String path) {
        Point point = new Point(x, y);
        Dimension dimension = new Dimension(width, height);
        Rectangle rectangle = new Rectangle(point, dimension);

        try {
            BufferedImage screenCapture = new Robot().createScreenCapture(rectangle);
            //ImageIO.write(screenCapture, "png", new File("D:/screenshot.png"));
            ImageIO.write(screenCapture, "png", new File(path));

            //zoomInImage(screenCapture.getWidth(), screenCapture.getHeight(), 3, screenCapture);
        } catch (IOException | AWTException e) {
            System.out.println("Exception occured in createScreenShot: " + e.getMessage());
        }
    }

    public BufferedImage getBufferedImageScreenShot(int x, int y, int width, int height) {
        Point point = new Point(x, y);
        Dimension dimension = new Dimension(width, height);
        Rectangle rectangle = new Rectangle(point, dimension);

        BufferedImage screenCapture = null;

        try {
            screenCapture = new Robot().createScreenCapture(rectangle);
        } catch (AWTException e) {
            System.out.println("Exception occured in createScreenShot: " + e.getMessage());
        }
        return screenCapture;
    }

    public int getIntRgbInScreenShot(int x, int y, String path) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Exception occured in getRgbInScreenShot: " + e.getMessage());
        }
        return bufferedImage.getRGB(x, y);
    }

    public List<Integer> getRgbInScreenShot(int x, int y, String path) {
        List<Integer> rgbList = new ArrayList<>();
        int intRgb = getIntRgbInScreenShot(x, y, path);
        Color color = new Color(intRgb);

        rgbList.add(color.getRed());
        rgbList.add(color.getGreen());
        rgbList.add(color.getBlue());

        return rgbList;
    }

    public BufferedImage zoomInImage(BufferedImage originalImage, int zoomLevel) {
        int newImageWidth = originalImage.getWidth() * zoomLevel;
        int newImageHeight = originalImage.getHeight() * zoomLevel;
        BufferedImage resizedImage = new BufferedImage(newImageWidth , newImageHeight, 1);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newImageWidth , newImageHeight , null);
        g.dispose();
        return resizedImage;
    }

    public String getStringFromSavedImageWithTesseract(String pathOfImage) {
        BytePointer outText;

        tesseract.TessBaseAPI api = new tesseract.TessBaseAPI();
        // Initialize tesseract-ocr with English, without specifying tessdata path
        if (api.Init("src/main/java/com/lennart/model/imageprocessing/tessdata", "ENG") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }

        // Open input image with leptonica library
        lept.PIX image = pixRead(pathOfImage);
        api.SetImage(image);
        // Get OCR result
        outText = api.GetUTF8Text();
        String string = outText.getString();

        // Destroy used object and release memory
        api.End();
        outText.deallocate();
        pixDestroy(image);

        return string;
    }

    public String getStringFromBufferedImageWithTesseract(BufferedImage bufferedImage) throws IOException {
        BytePointer outText;

        tesseract.TessBaseAPI api = new tesseract.TessBaseAPI();
        if (api.Init("src/main/java/com/lennart/model/imageprocessing/tessdata", "ENG") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }

        lept.PIX image = convertBufferedImageToPIX(bufferedImage);
        api.SetImage(image);
        outText = api.GetUTF8Text();
        String string = outText.getString();
        api.End();
        outText.deallocate();
        pixDestroy(image);

        return string;
    }

    private lept.PIX convertBufferedImageToPIX(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "tiff", baos);
        byte[] bytes = baos.toByteArray();

        return pixReadMem(bytes, bytes.length);
    }

    private BufferedImage makeBufferedImageBlackAndWhite(BufferedImage bufferedImage) {
        BufferedImage blackAndWhite = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g = blackAndWhite.createGraphics();
        g.drawImage(bufferedImage, 0, 0, null);
        return blackAndWhite;
    }

    private BufferedImage invertBufferedImageColours(BufferedImage bufferedImage) {
       short[] invertTable = new short[256];

       for (int i = 0; i < 256; i++) {
           invertTable[i] = (short) (255 - i);
       }

       BufferedImage invertedColors = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);

       BufferedImageOp invertOp = new LookupOp(new ShortLookupTable(0, invertTable), null);
       return invertOp.filter(bufferedImage, invertedColors);
    }

    private void saveBufferedImage(BufferedImage bufferedImage, String path) throws IOException {
        ImageIO.write(bufferedImage, "png", new File(path));
    }

    public double getMouseXcoordinate() {
        return MouseInfo.getPointerInfo().getLocation().getX();
    }

    public double getMouseYcoordinate() {
        return MouseInfo.getPointerInfo().getLocation().getY();
    }

    private void createTopPlayerUserNameScreenShot() throws IOException {
        int x = 486;
        int y = 187;
        int width = 115;
        int height = 27;
        String path = "/Users/LennartMac/Desktop/topPlayerUserName.png";

        BufferedImage topPlayerUserName = getBufferedImageScreenShot(x, y, width, height);
        saveBufferedImage(topPlayerUserName, path);
    }

    private void createTopPlayerStackScreenShot() throws IOException {
        int x = 496;
        int y = 223;
        int width = 95;
        int height = 25;
        String path = "/Users/LennartMac/Desktop/topPlayerStack.png";

        BufferedImage topPlayerUserName = getBufferedImageScreenShot(x, y, width, height);
        saveBufferedImage(topPlayerUserName, path);
    }

    private void createPotSizeScreenShot() throws IOException{
        int x = 440;
        int y = 327;
        int width = 130;
        int height = 25;
        String path = "/Users/LennartMac/Desktop/potSize.png";

        BufferedImage potSizeScreenShot = getBufferedImageScreenShot(x, y, width, height);
        potSizeScreenShot = zoomInImage(potSizeScreenShot, 2);
        saveBufferedImage(potSizeScreenShot, path);
    }

    private BufferedImage getTopPlayerUserNameImage() throws IOException {
        int x = 486;
        int y = 187;
        int width = 115;
        int height = 27;

        return getBufferedImageScreenShot(x, y, width, height);
    }

    private BufferedImage getTopPlayerStackImage() throws IOException {
        int x = 496;
        int y = 223;
        int width = 95;
        int height = 25;

        return getBufferedImageScreenShot(x, y, width, height);
    }

    private BufferedImage getPotSizeImage() throws IOException{
        int x = 440;
        int y = 327;
        int width = 130;
        int height = 25;

        BufferedImage potSizeScreenShot = getBufferedImageScreenShot(x, y, width, height);
        return zoomInImage(potSizeScreenShot, 2);
    }

    private BufferedImage getTopPlayerBetSizeImage() throws IOException {
        int x = 444;
        int y = 266;
        int width = 65;
        int height = 17;

        BufferedImage topPlayerBetSize = getBufferedImageScreenShot(x, y, width, height);
        return zoomInImage(topPlayerBetSize, 2);
    }

    private BufferedImage getBottomPlayerStackImage() throws IOException {
        int x = 505;
        int y = 640;
        int width = 85;
        int height = 25;

        return getBufferedImageScreenShot(x, y, width, height);
    }

    private String getFlopCard1Rank(String street) throws IOException {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;

        if(street.equals("flop")) {

        } else if(street.equals("turn")) {

        } else if(street.equals("river")) {
            x = 325;
            y = 360;
            width = 16;
            height = 19;
        }
        BufferedImage b = getBufferedImageScreenShot(x, y, width, height);
        return getStringFromBufferedImageWithTesseract(b);
    }

    private String getFlopCard2Rank(String street) throws IOException {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;

        if(street.equals("flop")) {

        } else if(street.equals("turn")) {

        } else if(street.equals("river")) {
            x = 394;
            y = 360;
            width = 16;
            height = 19;
        }
        BufferedImage b = getBufferedImageScreenShot(x, y, width, height);
        return getStringFromBufferedImageWithTesseract(b);
    }

    private String getFlopCard3Rank(String street) throws IOException {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;

        if(street.equals("flop")) {

        } else if(street.equals("turn")) {

        } else if(street.equals("river")) {
            x = 463;
            y = 360;
            width = 16;
            height = 19;
        }
        BufferedImage b = getBufferedImageScreenShot(x, y, width, height);
        return getStringFromBufferedImageWithTesseract(b);
    }

    private String getTurnCardRank(String street) throws IOException {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;

        if(street.equals("turn")) {

        } else if(street.equals("river")) {
            x = 532;
            y = 360;
            width = 16;
            height = 19;
        }
        BufferedImage b = getBufferedImageScreenShot(x, y, width, height);
        return getStringFromBufferedImageWithTesseract(b);
    }


    private String getRiverCardRank() throws IOException {
        int x = 601;
        int y = 360;
        int width = 16;
        int height = 40;

        BufferedImage b = getBufferedImageScreenShot(x, y, width, height);
        return getStringFromBufferedImageWithTesseract(b);
    }

    private char getFlopCard1Suit() throws AWTException {
        int x = 334;
        int y = 388;

        Robot robot = new Robot();
        return compareColors(robot.getPixelColor(x, y));
    }

    private char getFlopCard2Suit() throws AWTException {
        int x = 403;
        int y = 389;

        Robot robot = new Robot();
        return compareColors(robot.getPixelColor(x, y));
    }

    private char getFlopCard3Suit() throws AWTException {
        int x = 472;
        int y = 389;

        Robot robot = new Robot();
        return compareColors(robot.getPixelColor(x, y));
    }

    private char getTurnCardSuit() throws AWTException {
        int x = 541;
        int y = 388;

        Robot robot = new Robot();
        return compareColors(robot.getPixelColor(x, y));
    }

    private char getRiverCardSuit() throws AWTException {
        int x = 611;
        int y = 388;

        Robot robot = new Robot();
        return compareColors(robot.getPixelColor(x, y));
    }

    private char compareColors(Color color) {
        Color clubs = new Color(40, 122, 4);
        Color spades = new Color(1, 1, 1);
        Color hearts = new Color(200, 0, 27);

        if(color.equals(clubs)) {
            return 'c';
        } else if(color.equals(spades)) {
            return 's';
        } else if(color.equals(hearts)) {
            return 'h';
        }
        return 0;
    }



    public static void main(String[] args) throws Exception {
        ImageProcessor imageProcessor = new ImageProcessor();

        TimeUnit.SECONDS.sleep(3);

        double x = imageProcessor.getMouseXcoordinate();
        double y = imageProcessor.getMouseYcoordinate();

        System.out.println(x);
        System.out.println(y);

        Robot robot = new Robot();
        Color c = robot.getPixelColor(541, 388);
    }
}
