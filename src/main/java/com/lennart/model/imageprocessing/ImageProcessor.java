package com.lennart.model.imageprocessing;

import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;
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

    static {
        System.setProperty("java.awt.headless", "false");
    }

    public static void createPartialSreenShot(int x, int y, int width, int height, String path) {
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

    public static BufferedImage getBufferedImageScreenShot(int x, int y, int width, int height) {
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

    public static String getStringFromBufferedImageWithTesseract(BufferedImage bufferedImage) {
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

    public static BufferedImage zoomInImage(BufferedImage originalImage, int zoomLevel) {
        int newImageWidth = originalImage.getWidth() * zoomLevel;
        int newImageHeight = originalImage.getHeight() * zoomLevel;
        BufferedImage resizedImage = new BufferedImage(newImageWidth , newImageHeight, 1);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newImageWidth , newImageHeight , null);
        g.dispose();
        return resizedImage;
    }

    public static BufferedImage makeBufferedImageBlackAndWhite(BufferedImage bufferedImage) {
        BufferedImage blackAndWhite = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D g = blackAndWhite.createGraphics();
        g.drawImage(bufferedImage, 0, 0, null);
        return blackAndWhite;
    }

    public static BufferedImage invertBufferedImageColours(BufferedImage bufferedImage) {
        short[] invertTable = new short[256];

        for (int i = 0; i < 256; i++) {
            invertTable[i] = (short) (255 - i);
        }

        BufferedImage invertedColors = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        BufferedImageOp invertOp = new LookupOp(new ShortLookupTable(0, invertTable), null);
        return invertOp.filter(bufferedImage, invertedColors);
    }

    public static String removeEmptySpacesFromString(String string) {
        return string.replaceAll("\\s+","");
    }

    public static String removeAllNonNumericCharacters(String string) {
        return string.replaceAll("[^\\d.]", "");
    }

    public static String getStringFromSavedImageWithTesseract(String pathOfImage) {
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

    public static void saveBufferedImage(BufferedImage bufferedImage, String path) throws IOException {
        ImageIO.write(bufferedImage, "png", new File(path));
    }

    public static int getIntRgbInScreenShot(int x, int y, String path) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Exception occured in getRgbInScreenShot: " + e.getMessage());
        }
        return bufferedImage.getRGB(x, y);
    }

    public static List<Integer> getRgbInScreenShot(int x, int y, String path) {
        List<Integer> rgbList = new ArrayList<>();
        int intRgb = getIntRgbInScreenShot(x, y, path);
        Color color = new Color(intRgb);

        rgbList.add(color.getRed());
        rgbList.add(color.getGreen());
        rgbList.add(color.getBlue());

        return rgbList;
    }

    public static double getMouseXcoordinate() {
        return MouseInfo.getPointerInfo().getLocation().getX();
    }

    public static double getMouseYcoordinate() {
        return MouseInfo.getPointerInfo().getLocation().getY();
    }

    //helper methods
    private static lept.PIX convertBufferedImageToPIX(BufferedImage bufferedImage) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "tiff", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();

        return pixReadMem(bytes, bytes.length);
    }

//    public static void main(String[] args) throws Exception {
//
////        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(461, 506, 1, 1);
////        int suitRgb = bufferedImage.getRGB(0, 0);
////        System.out.println(suitRgb);
//
//        NetBetTableReader netBetTableReader = new NetBetTableReader();
//
//        System.out.println(netBetTableReader.getPotSizeFromImage());
//
//        //createPartialSreenShot(238, 9, 79, 18, "C:/Users/Lennart/screenshot.png");
//
////        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(238, 9, 79, 18);
////        //bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 3);
////        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
////        System.out.println(ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage));
//
//
////        String firstFlopCardRank = imageProcessor.readFirstFlopCardRankFromBoard();
////        firstFlopCardRank = firstFlopCardRank.replaceAll("\\s+","");
////        String secondFlopCardRank = imageProcessor.readSecondFlopCardRankFromBoard();
////        secondFlopCardRank = secondFlopCardRank.replaceAll("\\s+","");
////        String thirdFlopCardRank = imageProcessor.readThirdFlopCardRankFromBoard();
////        thirdFlopCardRank = thirdFlopCardRank.replaceAll("\\s+","");
////
////        char firstFlopCardSuit = imageProcessor.readFirstFlopCardSuitFromBoard();
////        char secondFlopCardSuit = imageProcessor.readSecondFlopCardSuitFromBoard();
////        char thirdFlopCardSuit = imageProcessor.readThirdFlopCardSuitFromBoard();
////
////        System.out.println("Flop: " + firstFlopCardRank + firstFlopCardSuit + secondFlopCardRank + secondFlopCardSuit +
////                thirdFlopCardRank + thirdFlopCardSuit);
////        imageProcessor.readTopPlayerStack();
////        imageProcessor.readBottomPlayerStack();
//
////        TimeUnit.SECONDS.sleep(3);
////
////        double x = ImageProcessor.getMouseXcoordinate();
////        double y = ImageProcessor.getMouseYcoordinate();
////
////        System.out.println(x);
////        System.out.println(y);
//    }
}
