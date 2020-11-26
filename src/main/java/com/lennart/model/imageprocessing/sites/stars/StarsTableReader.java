package com.lennart.model.imageprocessing.sites.stars;

import com.lennart.model.action.actionbuilders.ai.HandHistoryReaderStars;
import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.ImageProcessor;
import org.apache.commons.math3.util.Precision;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;
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

        System.out.println("BOTSTACK: " + botStackAsString);

        double botStack;

        if(botStackAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            botStack = Double.parseDouble(botStackAsString);
        } else {
            botStack = -1;
        }

        return botStack;
    }

    public double getOpponentStackFromImage() throws Exception {
        String opponentStackAsString = readBottomPlayerStack();

        if(opponentStackAsString.endsWith(".")) {
            opponentStackAsString.replaceAll(".", "");
        }

        System.out.println("OPPSTACK: " + opponentStackAsString);

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

            if(cardSuit != 'x') {
                return new Card(cardRank, cardSuit);
            }
        }
        return null;
    }

    public Card getFlopCard2FromImage() {
        int cardRank = getIntCardRank(readSecondFlopCardRankFromBoard());

        if(cardRank != -1) {
            char cardSuit = readSecondFlopCardSuitFromBoard();

            if(cardSuit != 'x') {
                return new Card(cardRank, cardSuit);
            }
        }
        return null;
    }

    public Card getFlopCard3FromImage() {
        int cardRank = getIntCardRank(readThirdFlopCardRankFromBoard());

        if(cardRank != -1) {
            char cardSuit = readThirdFlopCardSuitFromBoard();

            if(cardSuit != 'x') {
                return new Card(cardRank, cardSuit);
            }
        }
        return null;
    }

    public Card getTurnCardFromImage() {
        int cardRank = getIntCardRank(readTurnCardRankFromBoard());

        if(cardRank != -1) {
            char cardSuit = readTurnCardSuitFromBoard();

            if(cardSuit != 'x') {
                return new Card(cardRank, cardSuit);
            }
        }
        return null;
    }

    public Card getRiverCardFromImage() {
        int cardRank = getIntCardRank(readRiverCardRankFromBoard());

        if(cardRank != -1) {
            char cardSuit = readRiverCardSuitFromBoard();

            if(cardSuit != 'x') {
                return new Card(cardRank, cardSuit);
            }
        }
        return null;
    }

    public static boolean botIsToAct() throws Exception {
        //hier veranderen, nu herkent ie niet, als je all in facet, dat je to act bent, want middelste knop (call) is er dan niet. Alleen rechterknop gebruiken?
        //die is er altijd?

        boolean firstCheck = false;
        boolean secondCheck = false;
        boolean thirdCheck = false;

        boolean fourthCheck = true;
        boolean fifthCheck = true;
        boolean sixthCheck = true;

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(1252, 740, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        if(suitRgb / 1_000 == -3486) {
            //expected rgb: -3.486.258
            firstCheck = true;
        }

        BufferedImage bufferedImage2 = ImageProcessor.getBufferedImageScreenShot(1254, 779, 1, 1);
        int suitRgb2 = bufferedImage2.getRGB(0, 0);

        if(suitRgb2 / 1_000 == -3486) {
            //expected rgb: -3.486.258
            secondCheck = true;
        }

        //third check.. the timebox below your name...
        BufferedImage bufferedImage3 = ImageProcessor.getBufferedImageScreenShot(1118, 757, 1, 1);
        int suitRgb3 = bufferedImage3.getRGB(0, 0);

        if(suitRgb3 / 1_000 == -3486) {
            //expected rgb: -3.486.258
            thirdCheck = true;
        }

        if(firstCheck && secondCheck && thirdCheck) {
            TimeUnit.MILLISECONDS.sleep(84);

//            BufferedImage bufferedImage4 = ImageProcessor.getBufferedImageScreenShot(1118, 757, 1, 1);
//            int suitRgb4 = bufferedImage4.getRGB(0, 0);
//
//            if(suitRgb4 / 1_000 == -3486) {
//                fourthCheck = true;
//            }
//
//            BufferedImage bufferedImage5 = ImageProcessor.getBufferedImageScreenShot(1252, 740, 1, 1);
//            int suitRgb5 = bufferedImage5.getRGB(0, 0);
//
//            if(suitRgb5 / 1_000 == -3486) {
//                fifthCheck = true;
//            }
//
//            BufferedImage bufferedImage6 = ImageProcessor.getBufferedImageScreenShot(1254, 779, 1, 1);
//            int suitRgb6 = bufferedImage6.getRGB(0, 0);
//
//            if(suitRgb6 / 1_000 == -3486) {
//                sixthCheck = true;
//            }

            if(fourthCheck && fifthCheck && sixthCheck) {
                System.out.println();
                System.out.println("Bot is to act");
                System.out.println();

                return true;
            } else {
                System.out.println("Bot is to act first check true second not!");
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean sngIsFinished() throws Exception {
        boolean sngIsFinished = false;

        HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();
        List<String> total = handHistoryReaderStars.readTextFile();
        List<String> lastHand = handHistoryReaderStars.getLinesOfLastGameNonRecursive(total);

        for(String line : lastHand) {
            if(line.contains("the tournament")) {
                sngIsFinished = true;
                System.out.println("sng is finished, starting new game");
                break;
            }
        }

        return sngIsFinished;
    }

    public static boolean botIsSittingOut() {
        String botStack = readTopPlayerStackBase();

        if(botStack.toLowerCase().contains("sitting")) {
            return true;
        }

        return false;
    }

    public static void endBotIsSittingOut() {
        //MouseKeyboard.click(926, 710);
        MouseKeyboard.click(944, 748);
    }

    public void closeRematchScreen() {
        System.out.println("closing rematch screen");
        //MouseKeyboard.click(510, 643);

        //playMoney
        MouseKeyboard.click(510, 619);
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

    public static void closeAbnAmroUpdateScreenIfNecessary() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(711, 269, 1, 1);
        int pixelRgb = bufferedImage.getRGB(0, 0);

        if(pixelRgb / 100 == -15_790) {
            System.out.println("Abn Amro update screen is showing. Click close");
            MouseKeyboard.click(683, 306);
        }
    }

    private void efkes() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(403, 331, 1, 1);
        int pixelRgb = bufferedImage.getRGB(0, 0);
        System.out.println(pixelRgb);
    }

    public void clickTopSngInList() {
        System.out.println("clicking top sng in list");
        MouseKeyboard.click(206, 282);
    }

    public void registerNewSng() throws Exception {
        //close chest reward screen
        MouseKeyboard.click(222, 44);

        if(noPlayerIsReggedYet()) {
            TimeUnit.MILLISECONDS.sleep(2150);
            clickTopSngInList();
            TimeUnit.MILLISECONDS.sleep(250);
            System.out.println("registering new sng");
            MouseKeyboard.click(782, 566);

            TimeUnit.MILLISECONDS.sleep(1050);

            BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(428, 354, 1, 1);
            int pixelRgb = bufferedImage.getRGB(0, 0);

            if(pixelRgb / 100 == -156_429 ) {
                System.out.println("registration was already closed. Click OK and call method again");
                MouseKeyboard.click(494, 408);

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
            clickTopSngInList();
            registerNewSng();
        }
    }

    private boolean noPlayerIsReggedYet() throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(929, 438, 1, 1);

        int pixelRgb = bufferedImage.getRGB(0, 0);
        boolean noPlayerIsRegged = true;

        if(pixelRgb / 1000 == -16777) {
            //expected: =16_777_216
            noPlayerIsRegged = false;
        } else {
            System.out.println("empty sng, will register");
        }

        return noPlayerIsRegged;
    }

    public boolean newSngTableIsOpened() {
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(197, 489, 1, 1);
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(1179, 256, 1, 1);
        int pixelRgb = bufferedImage.getRGB(0, 0);

        if(pixelRgb / 1000 == -14670) {
            //expected rgb: -14.670.548
            System.out.println("new sng table is opened a");
            return true;
        }
        return false;
    }

    public void maximizeNewSngTable() throws Exception {
        MouseKeyboard.click(238, 15);
        TimeUnit.SECONDS.sleep(1);
        MouseKeyboard.click(259, 76);
        TimeUnit.MILLISECONDS.sleep(50);
        MouseKeyboard.click(259, 76);
        TimeUnit.MILLISECONDS.sleep(50);
        MouseKeyboard.click(259, 76);
    }

    public String getOpponentPlayerNameFromImage() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(695, 568, 133, 28);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        String fullPlayerName = ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);

        fullPlayerName = fullPlayerName.replaceAll("'", "");
        fullPlayerName = fullPlayerName.replaceAll("1‘", "");

        if(fullPlayerName.endsWith("_")) {
            fullPlayerName = fullPlayerName.substring(0, fullPlayerName.length() - 1);
        }

        while(fullPlayerName.endsWith("\\")) {
            fullPlayerName = fullPlayerName.substring(0, fullPlayerName.length() - 1);
        }

        return fullPlayerName;
    }

    public static void performActionOnSite(String botAction, double sizing) throws Exception {
        if(botAction != null && sizing != 0) {
            try {
                MouseKeyboard.click(975, 701);
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
    private static void clickFoldActionButton() throws Exception {
        MouseKeyboard.click(779, 764);
        TimeUnit.MILLISECONDS.sleep(100);
        MouseKeyboard.click(779, 764);
    }

    private static void clickCheckActionButton() throws Exception {
        MouseKeyboard.click(1002, 747);
        TimeUnit.MILLISECONDS.sleep(100);
        //hier iets
        MouseKeyboard.click(1002, 747);
    }

    private static void clickCallActionButton() throws Exception {
        if(readMiddleActionButton().toLowerCase().contains("call")) {
            MouseKeyboard.click(1009, 752);
            TimeUnit.MILLISECONDS.sleep(100);

            if(readMiddleActionButton().toLowerCase().contains("call")) {
                MouseKeyboard.click(1009, 752);
            } else {
                System.out.println("One call button click was enough!");
            }
        } else {
            System.out.println("Could not read 'call' in middle action button. So click right action button to call");
            MouseKeyboard.click(1157, 750);
            TimeUnit.MILLISECONDS.sleep(100);
            MouseKeyboard.click(1157, 750);
        }
    }

    private static void clickBetActionButton() throws Exception {
        MouseKeyboard.click(1157, 761);
        TimeUnit.MILLISECONDS.sleep(100);
        MouseKeyboard.click(1157, 761);
    }

    private static void clickRaiseActionButton() throws Exception {
        MouseKeyboard.click(1143, 768);
        TimeUnit.MILLISECONDS.sleep(100);
        MouseKeyboard.click(1143, 768);
    }

    private static String readLeftActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(579, 731, 125, 44);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);
    }

    private static String readMiddleActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(946, 727, 119, 49);
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
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(669, 64, 25, 32);
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(669, 64, 25, 24);
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(669, 64, 25, 26);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstHoleCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        firstHoleCardRank = ImageProcessor.removeEmptySpacesFromString(firstHoleCardRank);

        System.out.println("HC1rnk: " + firstHoleCardRank);

        return ImageProcessor.removeEmptySpacesFromString(firstHoleCardRank);
    }

    private String readSecondHoleCardRank() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(737, 64, 25, 32);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String secondHoleCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        secondHoleCardRank = ImageProcessor.removeEmptySpacesFromString(secondHoleCardRank);

        System.out.println("HC2rnk: " + secondHoleCardRank);

        return secondHoleCardRank;
    }

    private String extraTestBoardRead() {
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(558, 326, 349, 61);
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(558, 303, 359, 88);
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(558, 292, 64, 95);
        //bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        return firstFlopCardRank;

        //return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

    private String readFirstFlopCardRankFromBoard() {
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(557, 326, 71, 61);
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(557, 323, 71, 64);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board1: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private String readSecondFlopCardRankFromBoard() {
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(628, 326, 71, 61);
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(628, 323, 71, 64);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board2: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private String readThirdFlopCardRankFromBoard() {
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(699, 326, 71, 61);
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(710, 324, 53, 59);
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(715, 325, 48, 59);
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(715, 323, 48, 64);
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(699, 323, 71, 64);
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(715, 323, 71, 58);
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(715, 323, 71, 50);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board3: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private String readTurnCardRankFromBoard() {
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(770, 326, 71, 61);
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(770, 323, 71, 64);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board4: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private String readRiverCardRankFromBoard() {
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(841, 326, 71, 61);
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(841, 323, 71, 64);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board5: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private char readFirstHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(711, 111, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(775, 103, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readFirstFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(567, 347, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(640, 345, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readThirdFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(710, 343, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readTurnCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(782, 347, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readRiverCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(853, 349, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private String readBottomPlayerStack() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(707, 598, 87, 22);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String bottomPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        bottomPlayerStack = ImageProcessor.removeEmptySpacesFromString(bottomPlayerStack);

        if(bottomPlayerStack.contains("s")) {
            bottomPlayerStack = bottomPlayerStack.replace("s", "8");
            System.out.println("opp stack contains 's', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.contains("?")) {
            bottomPlayerStack = bottomPlayerStack.replace("?", "7");
            System.out.println("opp stack contains '?', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.toLowerCase().contains("all")) {
            System.out.println("oppstack: opp allin!");
            bottomPlayerStack = "0";
        }

        if(bottomPlayerStack.toLowerCase().contains("sitting") || bottomPlayerStack.toLowerCase().contains("isconnect")) {
            System.out.println("oppstack: opp sitting out!");
            bottomPlayerStack = "1500";
        }

        return ImageProcessor.removeAllNonNumericCharacters(bottomPlayerStack);
    }

    private String readTopPlayerStack() {
        String topPlayerStack = readTopPlayerStackBase();

        if(topPlayerStack.contains("s")) {
            topPlayerStack = topPlayerStack.replace("s", "8");
            System.out.println("botstack contains 's', new value: " + topPlayerStack);
        }

        if(topPlayerStack.contains("?")) {
            topPlayerStack = topPlayerStack.replace("?", "7");
            System.out.println("botstack contains '?', new value: " + topPlayerStack);
        }

        return ImageProcessor.removeAllNonNumericCharacters(topPlayerStack);
    }

    private static String readTopPlayerStackBase() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(671, 153, 90, 20);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);
        topPlayerStack = topPlayerStack.replace("I", "1");
        return topPlayerStack;
    }

    private String readTotalPotSize() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(692, 265, 85, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String totalPotSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        totalPotSize = ImageProcessor.removeEmptySpacesFromString(totalPotSize);
        return ImageProcessor.removeAllNonNumericCharacters(totalPotSize);
    }

//    public static void main(String[] args) throws Exception {
//        System.out.println("grr: " + new StarsTableReader().readBigBlindFromSngScreen());
//    }
//
//    private void testBbStringFromTopTournamentScreen() {
//        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(614, 26, 72, 19);
//        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(558, 25, 199, 19);
//        String testBigBlindString = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
//        System.out.println(testBigBlindString);
//
//    }

    public double readBigBlindFromSngScreen() throws Exception {
        double bigBlind;

        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(538, 26, 179, 21);
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(558, 25, 199, 19);

        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);

        String bigBlindString = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        System.out.println(bigBlindString);

        int indexOfMinusSign = bigBlindString.indexOf("-");

        if(indexOfMinusSign == -1) {
            System.out.println("weird bigblind string!");
            return -2;
        }

        bigBlindString = bigBlindString.substring(indexOfMinusSign);
        bigBlindString = bigBlindString.replaceAll("\\s+","");
        bigBlindString = bigBlindString.replaceAll("\\-", "");

        if(bigBlindString.charAt(2) != '0') {
            bigBlindString = bigBlindString.substring(0, 2);
        } else {
            if(bigBlindString.charAt(3) == '0') {
                bigBlindString = bigBlindString.substring(0, 4);
            } else {
                bigBlindString = bigBlindString.substring(0, 3);
            }
        }

        if(bigBlindString.equals("sa") || bigBlindString.equals("au")) {
            bigBlindString = "30";
        }

        if(bigBlindString.equals("?5") || bigBlindString.equals("IF")) {
            bigBlindString = "75";
        }

        if(bigBlindString.equals("4O")) {
            System.out.println("bb 80 reading...");
            bigBlindString = "40";
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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(863, 245, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        if(suitRgb / 1_000_000 == -4) {
            //expected rgb: -4.079.167
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
        rgb = rgb / 100_000;

        //spades: -8.882.835
        //clubs: -16.475.036
        //hearts: -113.620
        //diamonds: -10.255.934

        if(rgb == -164) {
            suit = 'c';
        } else if(rgb == -1) {
            suit = 'h';
        } else if(rgb == -88) {
            suit = 's';
        } else if(rgb == -102) {
            suit = 'd';
        }
        return suit;
    }

    private int getIntCardRank(String stringCardRank) {
        stringCardRank = stringCardRank.replace(".", "");
        stringCardRank = stringCardRank.replace("'", "");
        stringCardRank = stringCardRank.replace("‘", "");
        stringCardRank = stringCardRank.replace("_", "");

        int cardRank = -1;

        if(stringCardRank.contains("2") || stringCardRank.contains("Z") || stringCardRank.contains("z")) {
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
        } else if(stringCardRank.contains("l0") || stringCardRank.contains("ll]") || stringCardRank.contains("'0") || stringCardRank.contains("I0") || stringCardRank.contains("IO") || stringCardRank.contains("ll]") || stringCardRank.contains("IU") || stringCardRank.contains("ID")) {
            cardRank = 10;
        } else if(stringCardRank.contains("J")) {
            cardRank = 11;
        } else if(stringCardRank.contains("Q") || stringCardRank.contains("£1")) {
            cardRank = 12;
        } else if(stringCardRank.contains("K")) {
            cardRank = 13;
        } else if(stringCardRank.contains("A")) {
            cardRank = 14;
        }
        return cardRank;
    }
}
