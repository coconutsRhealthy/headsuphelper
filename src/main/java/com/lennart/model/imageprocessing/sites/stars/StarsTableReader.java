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

//    public static void main(String[] args) {
//        StarsTableReader starsTableReader = new StarsTableReader();
//
//        Card firstHoleCard = starsTableReader.getBotHoleCard1FromImage();
//        Card secondHoleCard = starsTableReader.getBotHoleCard2FromImage();
//
////        Card firstFlopCard = starsTableReader.getFlopCard1FromImage();
////        Card secondFlopCard = starsTableReader.getFlopCard2FromImage();
////        Card thirdFlopCard = starsTableReader.getFlopCard3FromImage();
////        Card turnCard = starsTableReader.getTurnCardFromImage();
////        Card riverCard = starsTableReader.getRiverCardFromImage();
//
//        System.out.println(
//                "" +
//                    firstHoleCard.getRank() + firstHoleCard.getSuit() + " " +
//                    secondHoleCard.getRank() + secondHoleCard.getSuit()
//        );
//
////        System.out.println(
////                "" +
////                    firstFlopCard.getRank() + firstFlopCard.getSuit() + " " +
////                    secondFlopCard.getRank() + secondFlopCard.getSuit() + " " +
////                    thirdFlopCard.getRank() + thirdFlopCard.getSuit() + " " +
////                    turnCard.getRank() + turnCard.getSuit() + " " +
////                    riverCard.getRank() + riverCard.getSuit()
////                );
//
//    }

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

    public static boolean botIsToAct() throws Exception {
        boolean firstCheck = false;
        boolean secondCheck = false;
        boolean thirdCheck = false;

        boolean fourthCheck = false;
        boolean fifthCheck = false;
        boolean sixthCheck = false;

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

        if(firstCheck && secondCheck && thirdCheck) {
            TimeUnit.MILLISECONDS.sleep(84);

            BufferedImage bufferedImage4 = ImageProcessor.getBufferedImageScreenShot(1048, 773, 1, 1);
            int suitRgb4 = bufferedImage4.getRGB(0, 0);

            if(suitRgb4 / 1_000 == -14614) {
                fourthCheck = true;
            }

            BufferedImage bufferedImage5 = ImageProcessor.getBufferedImageScreenShot(928, 737, 1, 1);
            int suitRgb5 = bufferedImage5.getRGB(0, 0);

            if(suitRgb5 / 1_000 == -10745) {
                fifthCheck = true;
            }

            BufferedImage bufferedImage6 = ImageProcessor.getBufferedImageScreenShot(474, 194, 1, 1);
            int suitRgb6 = bufferedImage6.getRGB(0, 0);

            if(suitRgb6 / 1_000 == -8132) {
                sixthCheck = true;
            }

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

        if(botStack.toLowerCase().equals("sittingout")) {
            return true;
        }
        return false;
    }

    public static void endBotIsSittingOut() {
        MouseKeyboard.click(926, 710);
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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(600, 561, 135, 24);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        String fullPlayerName = ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);

        fullPlayerName = fullPlayerName.replaceAll("'", "");

        if(fullPlayerName.endsWith("_")) {
            fullPlayerName = fullPlayerName.substring(0, fullPlayerName.length() - 1);
        }

        while(fullPlayerName.endsWith("\\")) {
            fullPlayerName = fullPlayerName.substring(0, fullPlayerName.length() - 1);
        }

        return fullPlayerName;
    }

    public static void main(String[] args) throws Exception {
        new StarsTableReader().textEnterTestMethode();
    }

    //////
    //247
    //12

    //257
    //76

    private void newMaximizeScreen() throws Exception {
        TimeUnit.SECONDS.sleep(4);

        MouseKeyboard.click(471, 178);
        TimeUnit.MILLISECONDS.sleep(500);
        MouseKeyboard.click(247, 12);
        TimeUnit.MILLISECONDS.sleep(500);
        MouseKeyboard.click(257, 76);
    }

    private void textEnterTestMethode() throws Exception {
        TimeUnit.SECONDS.sleep(1);

       // MouseKeyboard.click(667, 617);
        //MouseKeyboard.click(1003, 618);
        //MouseKeyboard.click(959, 698);
        //MouseKeyboard.click(959, 698);
        MouseKeyboard.click(980, 700);
        MouseKeyboard.click(980, 700);

        TimeUnit.SECONDS.sleep(1);
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

        MouseKeyboard.enterText(String.valueOf(Precision.round(466, 2)));

        TimeUnit.SECONDS.sleep(1);

        MouseKeyboard.click(1139, 756);
    }

    public static void performActionOnSite(String botAction, double sizing) {
        if(botAction != null && sizing != 0) {
            try {
                MouseKeyboard.click(920, 698);
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
        MouseKeyboard.click(703, 765);
    }

    private static void clickCheckActionButton() {
        MouseKeyboard.click(901, 757);
    }

    private static void clickCallActionButton() {
        if(readMiddleActionButton().toLowerCase().contains("call")) {
            MouseKeyboard.click(901, 757);
        } else {
            System.out.println("Could not read 'call' in middle action button. So click right action button to call");
            MouseKeyboard.click(1099, 744);
        }
    }

    private static void clickBetActionButton() {
        MouseKeyboard.click(1116, 768);
    }

    private static void clickRaiseActionButton() {
        MouseKeyboard.click(1099, 749);
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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(570, 22, 31, 34);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        System.out.println(firstFlopCardRank);
        return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

//    public static void main(String[] args) {
//        System.out.println(new StarsTableReader().readSecondHoleCardRank());
//    }

    private String readSecondHoleCardRank() {
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(645, 22, 31, 34);
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(643, 20, 31, 36);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);


        String secondFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        //System.out.println("second flop cardrank: " + secondFlopCardRank);
        return ImageProcessor.removeEmptySpacesFromString(secondFlopCardRank);
    }

    private String readFirstFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(452, 299, 66, 61);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

    private String readSecondFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(529, 299, 66, 61);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

    private String readThirdFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(606, 299, 66, 61);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

    private String readTurnCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(683, 299, 66, 61);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

    private String readRiverCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(760, 299, 66, 61);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

    private char readFirstHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(616, 78, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(690, 74, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readFirstFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(467, 330, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(541, 322, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readThirdFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(616, 323, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readTurnCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(694, 323, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readRiverCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(768, 328, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private String readBottomPlayerStack() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(606, 585, 110, 24);
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
            bottomPlayerStack = "0";
        }

        if(bottomPlayerStack.toLowerCase().equals("sittingout") || bottomPlayerStack.toLowerCase().contains("isconnect")) {
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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(559, 113, 109, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);
        return topPlayerStack;
    }

    private String readTotalPotSize() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(574, 230, 135, 29);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String totalPotSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        totalPotSize = ImageProcessor.removeEmptySpacesFromString(totalPotSize);
        totalPotSize = totalPotSize.replace("I", "1");
        return ImageProcessor.removeAllNonNumericCharacters(totalPotSize);
    }

    public double readBigBlindFromSngScreen() throws Exception {
        double bigBlind;

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(383, 25, 199, 19);

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
        } else if(stringCardRank.contains("l0") || stringCardRank.contains("ll]") || stringCardRank.contains("'0") || stringCardRank.contains("I0") || stringCardRank.contains("IO") || stringCardRank.contains("ll]") || stringCardRank.contains("IU")) {
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
