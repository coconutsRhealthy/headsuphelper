package com.lennart.model.imageprocessing.sites.stars;

import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.ImageProcessor;
import org.apache.commons.math3.util.Precision;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lennart on 3/12/2017.
 */
public class StarsTableReader {

    private int regNewSngWaitCouner = 0;

    public double getBotStackFromImage() throws Exception {
        String botStackAsString = readTopPlayerStack();

        if(botStackAsString.endsWith(".")) {
            botStackAsString.replaceAll(".", "");
        }

        double botStack;

        if(botStackAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            botStack = Double.parseDouble(botStackAsString);
        } else {
            botStack = -1;
        }

        if(botStack == -1) {
            botStack = changeSeaftsAndReadBottomPlayerStack();
        }

        return botStack;
    }

    private double changeSeaftsAndReadBottomPlayerStack() throws Exception {
        TimeUnit.MILLISECONDS.sleep(400);
        MouseKeyboard.rightClick(630, 583);
        TimeUnit.MILLISECONDS.sleep(250);
        MouseKeyboard.click(705, 744);
        TimeUnit.MILLISECONDS.sleep(400);

        String botStackAsString = readBottomPlayerStack();

        if(botStackAsString.endsWith(".")) {
            botStackAsString.replaceAll(".", "");
        }

        double botStack;

        if(botStackAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            botStack = Double.parseDouble(botStackAsString);
        } else {
            botStack = -1;
        }

        TimeUnit.MILLISECONDS.sleep(400);
        MouseKeyboard.rightClick(444, 155);
        TimeUnit.MILLISECONDS.sleep(250);
        MouseKeyboard.click(506, 312);
        TimeUnit.MILLISECONDS.sleep(400);

        return botStack;
    }


    public double getOpponentStackFromImage() throws Exception {
        String opponentStackAsString = readBottomPlayerStack();

        if(opponentStackAsString.endsWith(".")) {
            opponentStackAsString.replaceAll(".", "");
        }

        if(opponentStackAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            return Double.parseDouble(opponentStackAsString);
        } else {
            return -1;
        }
    }

    public double getTopPotsizeFromImage() throws Exception {
        String topPotsize = readTotalPotSize();

        if(topPotsize.endsWith(".")) {
            topPotsize.replaceAll(".", "");
        }

        if(topPotsize.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            return Double.parseDouble(topPotsize);
        } else {
            return -1;
        }
    }

    public Card getBotHoleCard1FromImage() {
        int cardRank = getIntCardRank(readFirstHoleCardRank());
        char cardSuit = readFirstHoleCardSuit();
        return new Card(cardRank, cardSuit);
    }

    public Card getBotHoleCard2FromImage() {
        int cardRank = getIntCardRank(readSecondHoleCardRank());
        char cardSuit = readSecondHoleCardSuit();
        return new Card(cardRank, cardSuit);
    }

    public Card getFlopCard1FromImage() {
        int cardRank = getIntCardRank(readFirstFlopCardRankFromBoard());

        if(cardRank != -1) {
            char cardSuit = readFirstFlopCardSuitFromBoard();
            return new Card(cardRank, cardSuit);
        }
        return null;
    }

    public Card getFlopCard2FromImage() {
        int cardRank = getIntCardRank(readSecondFlopCardRankFromBoard());

        if(cardRank != -1) {
            char cardSuit = readSecondFlopCardSuitFromBoard();
            return new Card(cardRank, cardSuit);
        }
        return null;
    }

    public Card getFlopCard3FromImage() {
        int cardRank = getIntCardRank(readThirdFlopCardRankFromBoard());

        if(cardRank != -1) {
            char cardSuit = readThirdFlopCardSuitFromBoard();
            return new Card(cardRank, cardSuit);
        }
        return null;
    }

    public Card getTurnCardFromImage() {
        int cardRank = getIntCardRank(readTurnCardRankFromBoard());

        if(cardRank != -1) {
            char cardSuit = readTurnCardSuitFromBoard();
            return new Card(cardRank, cardSuit);
        }
        return null;
    }

    public Card getRiverCardFromImage() {
        int cardRank = getIntCardRank(readRiverCardRankFromBoard());

        if(cardRank != -1) {
            char cardSuit = readRiverCardSuitFromBoard();
            return new Card(cardRank, cardSuit);
        }
        return null;
    }

    public static boolean botIsToAct() {
        boolean firstCheck = false;
        boolean secondCheck = false;
        boolean thirdCheck = false;

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(1048, 773, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        if(suitRgb / 1_000 == -14614) {
            //expected rgb: -14.614.527
            firstCheck = true;
        }

        BufferedImage bufferedImage2 = ImageProcessor.getBufferedImageScreenShot(928, 737, 1, 1);
        int suitRgb2 = bufferedImage2.getRGB(0, 0);

        if(suitRgb2 / 1_000 == -10745) {
            //expected rgb: -10.745.589
            secondCheck = true;
        }

        //third check.. the timebox below your name...
        BufferedImage bufferedImage3 = ImageProcessor.getBufferedImageScreenShot(474, 194, 1, 1);
        int suitRgb3 = bufferedImage3.getRGB(0, 0);

        if(suitRgb3 / 1_000 == -8132) {
            //expected rgb: -8.132.603
            thirdCheck = true;
        }

        if(firstCheck && secondCheck && !thirdCheck) {
            System.out.println("weird bot is to act!");
        }

        if(firstCheck && secondCheck && thirdCheck) {
            System.out.println();
            System.out.println("Bot is to act");
            System.out.println();
        }

        return firstCheck && secondCheck && thirdCheck;
    }

    public static boolean sngIsFinished() throws Exception {
        boolean sngIsFinished = false;

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(383, 640, 1, 1);
        int pixelRgb = bufferedImage.getRGB(0, 0);

        if(pixelRgb / 100 == -15790) {
            //expected rgb: -1.579.033

            System.out.println("sng is finished A1");
            sngIsFinished = true;
        }

        if(!sngIsFinished) {
            BufferedImage bufferedImage2 = ImageProcessor.getBufferedImageScreenShot(74, 167, 1, 1);
            int pixelRgb2 = bufferedImage2.getRGB(0, 0);

            if(pixelRgb2 / 100 == -31580) {
                //expected rgb: -3.158.065

                System.out.println("sng is finished A2");
                sngIsFinished = true;
            }
        }

        //temp for spin of the day
        if(sngIsFinished) {
            sngIsFinished = false;

            TimeUnit.MILLISECONDS.sleep(5590);

            BufferedImage bufferedImage3 = ImageProcessor.getBufferedImageScreenShot(383, 640, 1, 1);
            int pixelRgb3 = bufferedImage3.getRGB(0, 0);

            if(pixelRgb3 / 100 == -15790) {
                //expected rgb: -1.579.033

                System.out.println("sng is finished B1");
                sngIsFinished = true;
            }

            if(!sngIsFinished) {
                BufferedImage bufferedImage4 = ImageProcessor.getBufferedImageScreenShot(74, 167, 1, 1);
                int pixelRgb4 = bufferedImage4.getRGB(0, 0);

                if(pixelRgb4 / 100 == -31580) {
                    //expected rgb: -3.158.065

                    System.out.println("sng is finished B2");
                    sngIsFinished = true;
                }
            }

        }

        return sngIsFinished;
    }

    public void closeRematchScreen() {
        System.out.println("closing rematch screen");
        MouseKeyboard.click(483, 648);

        //playMoney
        //MouseKeyboard.click(480, 637);
    }

    public void closeSpinOfTheDayScreenIfNecessary() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(321, 468, 1, 1);
        int pixelRgb = bufferedImage.getRGB(0, 0);

        if(pixelRgb / 1000 == -986) {
            //expected -986896
            System.out.println("closing spinOfTheDay screen");
            MouseKeyboard.click(281, 317);
        }
    }

    public void clickTopSngInList() {
        System.out.println("clicking top sng in list");
        MouseKeyboard.click(206, 278);
    }

    public void registerNewSng() throws Exception {
        if(noPlayerIsReggedYet()) {
            TimeUnit.SECONDS.sleep(2);
            System.out.println("registering new sng");
            MouseKeyboard.click(782, 603);

            TimeUnit.MILLISECONDS.sleep(950);

            BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(441, 426, 1, 1);
            int pixelRgb = bufferedImage.getRGB(0, 0);

            if(pixelRgb / 100 == -13158 ) {
                System.out.println("registration was already closed. Click OK and call method again");
                MouseKeyboard.click(489, 449);

                TimeUnit.MILLISECONDS.sleep(200);
                registerNewSng();
            }
        } else {
            regNewSngWaitCouner++;

            if(regNewSngWaitCouner == 12) {
                MouseKeyboard.moveMouseToLocation(7, 100);
                MouseKeyboard.click(7, 100);
                System.out.println("click action in waiting for sng registration");
                regNewSngWaitCouner = 0;
            }

            System.out.println("Already one regged player, wait...");
            TimeUnit.SECONDS.sleep(5);
            registerNewSng();
        }
    }

    private boolean noPlayerIsReggedYet() throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(929, 438, 1, 1);

        int pixelRgb = bufferedImage.getRGB(0, 0);
        boolean noPlayerIsRegged = true;

        if(pixelRgb / 1000 == -9934) {
            //expected: -9.934.744
            noPlayerIsRegged = false;
        } else {
            System.out.println("empty sng, will register");
        }

        return noPlayerIsRegged;
    }

    public boolean newSngTableIsOpened() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(197, 489, 1, 1);
        int pixelRgb = bufferedImage.getRGB(0, 0);

        if(pixelRgb / 1000 == -15832) {
            //expected rgb: -15.832.802
            System.out.println("new sng table is opened a");
            return true;
        }
        return false;
    }

    public void maximizeNewSngTable() {
        System.out.println("maximizing sng table");
        MouseKeyboard.click(55, 240);
    }

    public String getOpponentPlayerNameFromImage() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(512, 553, 124, 28);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        String fullPlayerName = ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);

        fullPlayerName = fullPlayerName.replaceAll("'", "");

        if(fullPlayerName.endsWith("_")) {
            fullPlayerName = fullPlayerName.substring(0, fullPlayerName.length() - 1);
        }

        return fullPlayerName;
    }

    public static void performActionOnSite(String botAction, double sizing) {
        if(botAction != null && sizing != 0) {
            try {
                MouseKeyboard.click(789, 696);
                TimeUnit.MILLISECONDS.sleep(150);
                MouseKeyboard.pressBackSpace();
                MouseKeyboard.pressBackSpace();
                MouseKeyboard.pressBackSpace();
                MouseKeyboard.pressBackSpace();
                MouseKeyboard.pressBackSpace();
                MouseKeyboard.pressBackSpace();
                MouseKeyboard.pressBackSpace();
                MouseKeyboard.pressBackSpace();
                MouseKeyboard.pressBackSpace();
                TimeUnit.MILLISECONDS.sleep(150);

                MouseKeyboard.enterText(String.valueOf(Precision.round(sizing, 2)));
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(botAction == null) {
            clickCheckActionButton();
        } else if(botAction.contains("fold")) {
            clickFoldActionButton();
        } else if(botAction.contains("check")) {
            clickCheckActionButton();
        } else if(botAction.contains("call")) {
            clickCallActionButton();
        } else if(botAction.contains("bet")) {
            clickBetActionButton();
        } else if(botAction.contains("raise")) {
            clickRaiseActionButton();
        }

        MouseKeyboard.moveMouseToLocation(20, 20);
    }

    //helper methods
    private static void clickFoldActionButton() {
        MouseKeyboard.click(609, 755);
    }

    private static void clickCheckActionButton() {
        MouseKeyboard.click(810, 748);
    }

    private static void clickCallActionButton() {
        if(readMiddleActionButton().toLowerCase().contains("call")) {
            MouseKeyboard.click(810, 748);
        } else {
            System.out.println("Could not read 'call' in middle action button. So click right action button to call");
            MouseKeyboard.click(958, 754);
        }
    }

    private static void clickBetActionButton() {
        MouseKeyboard.click(1008, 749);
    }

    private static void clickRaiseActionButton() {
        MouseKeyboard.click(958, 754);
    }

    private static String readLeftActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(579, 731, 125, 44);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);
    }

    private static String readMiddleActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(727, 732, 151, 47);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        String fullPlayerName = ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);

        return fullPlayerName;
    }

    private static String readRightActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(941, 729, 123, 46);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);
    }

    private String readFirstHoleCardRank() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(479, 62, 19, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

    private String readSecondHoleCardRank() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(547, 63, 19, 22);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

    private String readFirstFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(361, 295, 19, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

    private String readSecondFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(437, 295, 19, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String secondFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(secondFlopCardRank);
    }

    private String readThirdFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(513, 295, 19, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String thirdFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(thirdFlopCardRank);
    }

    private String readTurnCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(589, 295, 19, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String turnCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(turnCardRank);
    }

    private String readRiverCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(665, 295, 19, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String riverCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(riverCardRank);
    }

    private char readFirstHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(485, 93, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgbHole(suitRgb);
    }

    private char readSecondHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(553, 93, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgbHole(suitRgb);
    }

    private char readFirstFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(370, 328, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(446, 328, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readThirdFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(522, 328, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readTurnCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(598, 328, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readRiverCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(674, 328, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private String readBottomPlayerStack() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(514, 585, 107, 25);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String bottomPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        bottomPlayerStack = ImageProcessor.removeEmptySpacesFromString(bottomPlayerStack);

        System.out.println("read opp stack: " + bottomPlayerStack);

        if(bottomPlayerStack.contains("?")) {
            bottomPlayerStack = bottomPlayerStack.replace("?", "7");
            System.out.println("opp stack contains '?', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.toLowerCase().contains("all")) {
            bottomPlayerStack = "0";
        }

        return ImageProcessor.removeAllNonNumericCharacters(bottomPlayerStack);
    }

    private String readTopPlayerStack() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(468, 159, 107, 25);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);

        System.out.println("read botstack: " + topPlayerStack);

        if(topPlayerStack.contains("?")) {
            topPlayerStack = topPlayerStack.replace("?", "7");
            System.out.println("botstack contains '?', new value: " + topPlayerStack);
        }

        return ImageProcessor.removeAllNonNumericCharacters(topPlayerStack);
    }

    private String readTotalPotSize() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(448, 243, 191, 38);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String totalPotSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        totalPotSize = ImageProcessor.removeEmptySpacesFromString(totalPotSize);
        return ImageProcessor.removeAllNonNumericCharacters(totalPotSize);
    }

    public double readBigBlindFromSngScreen() throws Exception {
        double bigBlind;

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(383, 25, 199, 19);

        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);

        String bigBlindString = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        System.out.println(bigBlindString);

        int indexOfDollar = bigBlindString.indexOf("$");

        if(indexOfDollar == -1) {
            System.out.println("weird bigblind string!");
            return -2;
        }

        bigBlindString = bigBlindString.substring(indexOfDollar);
        bigBlindString = bigBlindString.replaceAll("\\s+","");
        bigBlindString = bigBlindString.replaceAll("\\$", "");

        if(bigBlindString.charAt(2) != '0') {
            bigBlindString = bigBlindString.substring(0, 2);
        } else {
            if(bigBlindString.charAt(3) == '0') {
                bigBlindString = bigBlindString.substring(0, 4);
            } else {
                bigBlindString = bigBlindString.substring(0, 3);
            }
        }

        try {
            bigBlind = Double.valueOf(bigBlindString);
        } catch (Exception e) {
            System.out.println();
            System.out.println("Error in reading bigblind!");
            System.out.println("bb String: " + bigBlindString);
            long currentTime = new Date().getTime();
            System.out.println("Screenshot saved with time: " + currentTime);
            saveScreenshotOfEntireScreen(currentTime);
            System.out.println();

            BufferedImage bufferedImage2 = ImageProcessor.getBufferedImageScreenShot(361, 25, 199, 19);
            bufferedImage2 = ImageProcessor.zoomInImage(bufferedImage2, 2);
            bufferedImage2 = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage2);
            String bigBlindString2 = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage2);

            System.out.println("new bigblindstring: " + bigBlindString2);
            System.out.println();
            e.printStackTrace();

            bigBlind = -1;
        }

        return bigBlind * 2;
    }

    public static void saveScreenshotOfEntireScreen(int numberOfActionRequests) throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(0, 0, 3000, 1250);
        ImageProcessor.saveBufferedImage(bufferedImage, "/Users/LennartMac/Documents/logging/" + numberOfActionRequests + ".png");
    }

    public static void saveScreenshotOfEntireScreen(long time) throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(0, 0, 3000, 1250);
        ImageProcessor.saveBufferedImage(bufferedImage, "/Users/LennartMac/Documents/logging/" + time + ".png");
    }

    public boolean topPlayerIsButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(438, 223, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        if(suitRgb / 1_000_000 == -1) {
            //expected rgb: -1.114.100
            return true;
        }
        return false;
    }

    private char getSuitFromIntRgbHole(int rgb) {
        char suit = 'x';
        rgb = rgb / 100_000;

        //S: -16.777.216
        //C: -14.715.610 of -2.627.367
        //H: -4.128.753
        //D: -16.121.711

        if(rgb == -147 || rgb == -26) {
            suit = 'c';
        } else if(rgb == -41) {
            suit = 'h';
        } else if(rgb == -167) {
            suit = 's';
        } else if(rgb == -161) {
            suit = 'd';
        }
        return suit;
    }

    private char getSuitFromIntRgb(int rgb) {
        char suit = 'x';
        rgb = rgb / 1_000_000;

        //spades: -15.856.114
        //clubs: -14.781.405
        //hearts: -4.128.753
        //diamonds: -16.121.711

        if(rgb == -14) {
            suit = 'c';
        } else if(rgb == -4) {
            suit = 'h';
        } else if(rgb == -15) {
            suit = 's';
        } else if(rgb == -16) {
            suit = 'd';
        }
        return suit;
    }

    private int getIntCardRank(String stringCardRank) {
        int cardRank = -1;

        if(stringCardRank.contains("2")) {
            cardRank = 2;
        } else if(stringCardRank.contains("3") && !stringCardRank.contains("3kg_\\:")) {
            cardRank = 3;
        } else if(stringCardRank.contains("4")) {
            cardRank = 4;
        } else if(stringCardRank.contains("5")) {
            cardRank = 5;
        } else if(stringCardRank.contains("6")) {
            cardRank = 6;
        } else if(stringCardRank.contains("7")) {
            cardRank = 7;
        } else if(stringCardRank.contains("8")) {
            cardRank = 8;
        } else if(stringCardRank.contains("9")) {
            cardRank = 9;
        } else if(stringCardRank.contains("ll]") || stringCardRank.contains("I0") || stringCardRank.contains("IO") || stringCardRank.contains("ll]") || stringCardRank.contains("l0") || stringCardRank.contains("IU")) {
            cardRank = 10;
        } else if(stringCardRank.contains("J")) {
            cardRank = 11;
        } else if(stringCardRank.contains("Q")) {
            cardRank = 12;
        } else if(stringCardRank.contains("K")) {
            cardRank = 13;
        } else if(stringCardRank.contains("A")) {
            cardRank = 14;
        }
        return cardRank;
    }
}
