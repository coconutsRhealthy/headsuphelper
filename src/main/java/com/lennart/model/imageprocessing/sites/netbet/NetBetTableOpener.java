package com.lennart.model.imageprocessing.sites.netbet;

import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.imageprocessing.ImageProcessor;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lennart on 5/4/2017.
 */
public class NetBetTableOpener {

    public static void startNewTable(double bigBlind) {
        try {
            clickNoWhenAskedToPlayAgainAtSitOutTable();
            TimeUnit.SECONDS.sleep(1);
            bringLobbyToFront();
            TimeUnit.SECONDS.sleep(1);
            clickMinimizeLeftTopTable();
            TimeUnit.SECONDS.sleep(1);
            while(!tableOpenedLeftTop()) {
                TimeUnit.SECONDS.sleep(1);
                if(tableOpenedRightTop()) {
                    TimeUnit.SECONDS.sleep(1);
                    closeRightTopTable();
                }
                TimeUnit.SECONDS.sleep(1);
                if(tableOpenedRightBottom()) {
                    TimeUnit.SECONDS.sleep(1);
                    closeRightBottomTable();
                }
                TimeUnit.SECONDS.sleep(1);
                if(tableOpenedLeftBottom()) {
                    TimeUnit.SECONDS.sleep(1);
                    closeLeftBottomTable();
                }

                clickSortByNumberOfPlayers();
                TimeUnit.SECONDS.sleep(1);
                if(tablePlrsIsZero()) {
                    TimeUnit.SECONDS.sleep(1);
                    openTable();
                }
            }
            TimeUnit.SECONDS.sleep(2);
            clickSeatToBeSeated();
            TimeUnit.SECONDS.sleep(2);
            clickOkToBuyIn();
            TimeUnit.SECONDS.sleep(2);
            clickOpenChatBox();
            TimeUnit.SECONDS.sleep(1);

            if(initialBotStackIsAbove100bb(bigBlind)) {
                TimeUnit.SECONDS.sleep(1);
                clickSitOutButton1();
                TimeUnit.SECONDS.sleep(1);
                clickSitOutButton2();
                TimeUnit.SECONDS.sleep(1);
                startNewTable(bigBlind);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean tableOpenedLeftTop() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(27, 709, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        return suitRgb == -1;
    }

    private static boolean tableOpenedRightTop() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(683, 708, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        return suitRgb == -1;
    }

    private static boolean tableOpenedLeftBottom() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(27, 991, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        return suitRgb == -1;
    }

    private static boolean tableOpenedRightBottom() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(682, 991, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        return suitRgb == -1;
    }

    private static boolean tablePlrsIsZero() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(772, 402, 37, 23);
        String numberOfPlayers = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        numberOfPlayers = ImageProcessor.removeEmptySpacesFromString(numberOfPlayers);

        return numberOfPlayers.equals("0/2");
    }

    private static void openTable() {
        MouseKeyboard.click(769, 414);
        MouseKeyboard.click(769, 414);
    }

    private static void clickSeatToBeSeated() {
        MouseKeyboard.click(528, 566);
    }

    private static void clickOkToBuyIn() {
        MouseKeyboard.click(537, 543);
    }

    private static void clickOpenChatBox() {
        MouseKeyboard.click(72, 709);
    }

    private static void closeRightTopTable() {
        MouseKeyboard.click(1660, 18);
    }

    private static void closeLeftBottomTable() {
        MouseKeyboard.click(1004, 300);
    }

    private static void closeRightBottomTable() {
        MouseKeyboard.click(1661, 299);
    }

    private static void clickSortByNumberOfPlayers() {
        MouseKeyboard.click(801, 378);
    }

    private static void clickMinimizeLeftTopTable() {
        MouseKeyboard.click(959, 16);
    }

    private static boolean initialBotStackIsAbove100bb(double bigBlind) {
        NetBetTableReader netBetTableReader = new NetBetTableReader(bigBlind);
        return netBetTableReader.getBotStackFromImage() > 100 * bigBlind;
    }

    private static void clickSitOutButton1() {
        MouseKeyboard.click(48, 580);
    }

    private static void clickSitOutButton2() {
        MouseKeyboard.click(117, 450);
    }

    private static void bringLobbyToFront() {
        MouseKeyboard.click(1243, 232);
    }

    private static void clickNoWhenAskedToPlayAgainAtSitOutTable() {
        MouseKeyboard.click(693, 466);
    }
}
