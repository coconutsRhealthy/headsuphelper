package com.lennart.model.imageprocessing;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;

/**
 * Created by LPO21630 on 7-2-2017.
 */
public class ImageProcessor {

    public void createPartialSreenShot(int x, int y, int width, int height) {
        Point point = new Point(x, y);
        Dimension dimension = new Dimension(width, height);
        Rectangle rectangle = new Rectangle(point, dimension);

        try {
            BufferedImage screenCapture = new Robot().createScreenCapture(rectangle);
            //ImageIO.write(screenCapture, "bmp", new File("D:/screenshot.bmp"));
            ImageIO.write(screenCapture, "png", new File("D:/screenshot.png"));
        } catch (IOException | AWTException e) {
            System.out.println("Exception occured in createScreenShot: " + e.getMessage());
        }
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

    public String getStringFromImageWithTesseract(String pathOfImage) {
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

    public static void main(String[] args) {
        ImageProcessor imageProcessor = new ImageProcessor();

        imageProcessor.createPartialSreenShot(310, 300, 300, 63);
        String string = imageProcessor.getStringFromImageWithTesseract("D:/screenshot.png");

        System.out.println(string);
    }
}
