package com.lennart.model.imageprocessing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

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
            ImageIO.write(screenCapture, "bmp", new File("D:/screenshot.bmp"));
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
}
