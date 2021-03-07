package com.lennart.model.imageprocessing.sites.party;

import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.ImageProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Precision;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lennart on 3/12/2017.
 */
public class PartyTableReader {

    public double getBotStackFromImage() {
        String botStackAsString = readTopPlayerStack();

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
        return 0;
    }

    public double getTopPotsizeFromImage() throws Exception {
        return 0;
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
        return false;
    }

    public static boolean sngIsFinished() throws Exception {
        return false;
    }

    public static boolean botIsSittingOut() {
        return false;
    }

    public static void endBotIsSittingOut() {

    }

    public void closeRematchScreen() {

    }

    public void clickTopSngInList() {

    }

    public void registerNewSng() throws Exception {

    }

    private boolean noPlayerIsReggedYet() throws Exception {
        return false;
    }

    public boolean newSngTableIsOpened() throws Exception {
        return false;
    }

    public void maximizeNewSngTable() throws Exception {

    }

    public boolean sngTableIsMaximized() {
        return false;
    }

    public String getOpponentPlayerNameFromImage() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(473, 516, 604, 543);
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
                MouseKeyboard.click(998, 680);
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
        MouseKeyboard.click(579, 725);
    }

    private static void clickCheckActionButton() throws Exception {
        MouseKeyboard.click(745, 722);
    }

    private static void clickCallActionButton() throws Exception {
        MouseKeyboard.click(742, 725);
    }

    private static void clickBetActionButton() throws Exception {
        MouseKeyboard.click(909, 728);
    }

    private static void clickRaiseActionButton() throws Exception {
        MouseKeyboard.click(909, 727);
    }

    public static void main(String[] args) {
//        System.out.println("LEFT: " + new PartyTableReader().readLeftActionButton());
//        System.out.println("MIDDLE: " + new PartyTableReader().readMiddleActionButton());
//        System.out.println("RIGHT: " + new PartyTableReader().readRightActionButton());

        //PartyTableReader partyTableReader = new PartyTableReader();


        //System.out.println("" + partyTableReader.readFirstHoleCardRank() + partyTableReader.readFirstHoleCardSuit() + " " + partyTableReader.readSecondHoleCardRank() + partyTableReader.readSecondHoleCardSuit());
        //System.out.println(new PartyTableReader().readSecondHoleCardRank());

        new PartyTableReader().readBigBlindFromSngScreen(false);
    }

    private void efkesTest() {
        System.out.println(topPlayerIsButton());
        System.out.println(getBotStackFromImage());

//        Card holeCard1 = getBotHoleCard1FromImage();
//        Card holeCard2 = getBotHoleCard2FromImage();
//
//        Card flopCard1 = getFlopCard1FromImage();
//        Card flopCard2 = getFlopCard2FromImage();
//        Card flopCard3 = getFlopCard3FromImage();
//        Card turnCard = getTurnCardFromImage();
//        Card riverCard = getRiverCardFromImage();
//
//
//        System.out.println("holecards: " + holeCard1.getRank() + holeCard1.getSuit() + " " + holeCard2.getRank() + holeCard2.getSuit());
//
//        System.out.print("board: " + flopCard1.getRank() + flopCard1.getSuit() + " " + flopCard2.getRank() + flopCard2.getSuit() + " " + flopCard3.getRank() + flopCard3.getSuit());
//
//        if(turnCard != null) {
//            System.out.print(" " + turnCard.getRank() + turnCard.getSuit());
//        }
//
//        if(riverCard != null) {
//            System.out.print(" " + riverCard.getRank() + riverCard.getSuit());
//        }
    }

    private static String readLeftActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(547, 707, 682, 745);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);
    }

    private static String readMiddleActionButton() {
        //711, 699
        //849, 754

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(711, 699, 849, 754);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);
    }

    private static String readRightActionButton() {
        //879, 700
        //1015, 750

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(879, 700, 1015, 750);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);
    }

    private String readFirstHoleCardRank() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(427, 56, 455, 85);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String secondHoleCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        secondHoleCardRank = ImageProcessor.removeEmptySpacesFromString(secondHoleCardRank);

        System.out.println("HC1rnk: " + secondHoleCardRank);

        return secondHoleCardRank;
    }

    private String readSecondHoleCardRank() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(498, 56, 525, 85);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String secondHoleCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        secondHoleCardRank = ImageProcessor.removeEmptySpacesFromString(secondHoleCardRank);

        System.out.println("HC2rnk: " + secondHoleCardRank);

        return secondHoleCardRank;
    }

//    public static void main(String[] args) {
//        PartyTableReader partyTableReader = new PartyTableReader();
//
//        System.out.println(partyTableReader.readTotalPotSize());
//        //System.out.println(partyTableReader.getOpponentPlayerNameFromImage());
//
////        char suit1 = partyTableReader.readFirstFlopCardSuitFromBoard();
////        char suit2 = partyTableReader.readSecondFlopCardSuitFromBoard();
////        char suit3 = partyTableReader.readThirdFlopCardSuitFromBoard();
////        char suit4 = partyTableReader.readTurnCardSuitFromBoard();
////        char suit5 = partyTableReader.readRiverCardSuitFromBoard();
//
//        //clubs
//        //suit1: -15500223
//
//        //spades
//        //suit2: -13356237
//
//        //hearts
//        //suit2: -7861227
//        //suit3: -7533546
//        //suit3: -7468010
//
//        //diamonds:
//        //suit4: -14728023
//        //suit3: -15255135
//
//        //suit3: -14662487
//
//
////        int flopCard1rank = partyTableReader.getIntCardRank(partyTableReader.readFirstFlopCardRankFromBoard());
////        int flopCard2rank = partyTableReader.getIntCardRank(partyTableReader.readSecondFlopCardRankFromBoard());
////        int flopCard3rank = partyTableReader.getIntCardRank(partyTableReader.readThirdFlopCardRankFromBoard());
////
////        int turnCardRank = partyTableReader.getIntCardRank(partyTableReader.readTurnCardRankFromBoard());
////        int riverCardRank = partyTableReader.getIntCardRank(partyTableReader.readRiverCardRankFromBoard());
////
////
////        System.out.println("Board: " + flopCard1rank + " " + flopCard2rank + " " + flopCard3rank + " " + turnCardRank + " " + riverCardRank);
//    }

    private void testMethodje() {
        Card flopCard1 = getFlopCard1FromImage();
        Card flopCard2 = getFlopCard2FromImage();
        Card flopCard3 = getFlopCard3FromImage();
        Card turnCard = getTurnCardFromImage();
        Card riverCard = getRiverCardFromImage();

        System.out.println();
        System.out.println();
        System.out.println();

        System.out.print("" + flopCard1.getRank() + flopCard1.getSuit() + " " + flopCard2.getRank() + flopCard2.getSuit() + " " + flopCard3.getRank() + flopCard3.getSuit());

        if(turnCard != null) {
            System.out.print(" " + turnCard.getRank() + turnCard.getSuit());
        } else {
            System.out.print(" null");
        }

        if(riverCard != null) {
            System.out.print(" " + riverCard.getRank() + riverCard.getSuit());
        } else {
            System.out.print(" null");
        }
    }

    private String readFirstFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(346, 321, 392, 367);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board1: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private String readSecondFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(425, 321, 467, 367);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board2: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private String readThirdFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(501, 321, 545, 367);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board3: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private String readTurnCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(576, 321, 618, 367);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board4: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private String readRiverCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(652, 321, 698, 367);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board5: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private char readFirstHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(490, 62, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(562, 62, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readFirstFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(329, 280, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);

    }

    private char readSecondFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(405, 280, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);

    }

    private char readThirdFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(481, 280, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);

    }

    private char readTurnCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(556, 280, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);

    }

    private char readRiverCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(633, 280, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private String readBottomPlayerStack() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(477, 548, 595, 573);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String bottomPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        bottomPlayerStack = ImageProcessor.removeEmptySpacesFromString(bottomPlayerStack);

        if(bottomPlayerStack.toLowerCase().contains("all")) {
            System.out.println("oppstack: opp allin!");
            bottomPlayerStack = "0";
        }

        if(bottomPlayerStack.toLowerCase().contains("sitting") || bottomPlayerStack.toLowerCase().contains("isconnect") || bottomPlayerStack.toLowerCase().contains("connecte")) {
            System.out.println("oppstack: opp sitting out!");
            bottomPlayerStack = "1500";
        }

        String bottomPlayerStackNonNumericRemoved = ImageProcessor.removeAllNonNumericCharacters(bottomPlayerStack);

        while(StringUtils.countMatches(bottomPlayerStackNonNumericRemoved, ".") > 1) {
            bottomPlayerStackNonNumericRemoved = bottomPlayerStackNonNumericRemoved.replaceFirst("\\.", "");
        }

        if(!bottomPlayerStack.equals(bottomPlayerStackNonNumericRemoved)) {
            System.out.println("oppstack non numeric values removed!");
            System.out.println("oppstack before remove: " + bottomPlayerStack);
            System.out.println("oppstack after remove: " + bottomPlayerStackNonNumericRemoved);
        }

        return bottomPlayerStackNonNumericRemoved;
    }



    private String readTopPlayerStack() {
        String topPlayerStack = readTopPlayerStackBase();

        String topPlayerStackNonNumericRemoved = ImageProcessor.removeAllNonNumericCharacters(topPlayerStack);

        if(!topPlayerStack.equals(topPlayerStackNonNumericRemoved)) {
            System.out.println("botstack non numeric values removed!");
            System.out.println("botstack before remove: "+ topPlayerStack);
            System.out.println("botstack after remove: " + topPlayerStackNonNumericRemoved);
        }

        return topPlayerStackNonNumericRemoved;
    }

    private static String readTopPlayerStackBase() {
        //still needs to be verified

        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(477, 136, 595, 164);

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(421, 136, 548, 164);


        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);
        return topPlayerStack;
    }

    private String readTotalPotSize() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(416, 227, 603, 271);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String totalPotSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        totalPotSize = ImageProcessor.removeEmptySpacesFromString(totalPotSize);

        String totalPotSizeNonNumericRemoved = ImageProcessor.removeAllNonNumericCharacters(totalPotSize);

        if(!totalPotSize.equals("Pot:" + totalPotSizeNonNumericRemoved)) {
            System.out.println("potsize non numeric values removed!");
            System.out.println("potsize before remove: " + totalPotSize);
            System.out.println("potsize after remove: " + totalPotSizeNonNumericRemoved);
        }

        return totalPotSizeNonNumericRemoved;
    }

    public double readBigBlindFromSngScreen(boolean botIsButton) {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(490, 184, 617, 218);

        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String bigBlindString = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        //return ImageProcessor.removeEmptySpacesFromString(bigBlindString);

        System.out.println("BBB: " + bigBlindString);

        return 0;




        //825, 45
        //908, 67

        //kan ook via getal in call button...
        //of via 'Level xx in 4 min, bovenin


//        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(825, 45, 908, 67);
//
//        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
//        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
//        String bigBlindString = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
//        //return ImageProcessor.removeEmptySpacesFromString(bigBlindString);
//
//        return 0;

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
        //396, 177

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(396, 177, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        System.out.println("BUTTON PIXEL: " + suitRgb);

        if(suitRgb / 10_000 == -105) {
            //expected rgb: -1.052.689
            return true;
        }
        return false;
    }

    private char getSuitFromIntRgb(int rgb) {
        char suit = 'x';
        rgb = rgb / 1_000_000;

        //clubs
        //suit1: -15_500_223
        //suit5: -15_699_401
        //suit5: -131331

        //spades
        //suit2: -13_356_237

        //hearts
        //suit2: -7_861_227
        //suit3: -7_533_546
        //suit3: -7_468_010
        //suit1: -10023918
        //suit5: -7_731_951
        //suit5: -7_928_303

        //diamonds:
        //suit4: -14_728_023
        //suit3: -15_255_135

        //suit3: -14_662_487


        if(rgb == -15) {
            //-15499967
            //-15500223
            suit = 'c';
        } else if(rgb == -7) {
            suit = 'h';
            //-7664106
            //-7661025
            //-7599852
            //-7598570
        } else if(rgb == -13) {
            //-13356237
            //-13027272
            //-13290701
            suit = 's';
        } else if(rgb == -14) {
            //-14662232
            suit = 'd';
        }
        return suit;
    }


    private int getIntCardRank(String stringCardRank) {
        int cardRank = -1;

        if(stringCardRank.equals("2")) {
            cardRank = 2;
        } else if(stringCardRank.equals("3")) {
            cardRank = 3;
        } else if(stringCardRank.equals("4")) {
            cardRank = 4;
        } else if(stringCardRank.equals("5")) {
            cardRank = 5;
        } else if(stringCardRank.equals("6")) {
            cardRank = 6;
        } else if(stringCardRank.equals("7")) {
            cardRank = 7;
        } else if(stringCardRank.equals("8")) {
            cardRank = 8;
        } else if(stringCardRank.equals("9")) {
            cardRank = 9;
        } else if(stringCardRank.equals("l0") || stringCardRank.equals("I0")) {
            cardRank = 10;
        } else if(stringCardRank.equals("I") || stringCardRank.equals("l")) {
            cardRank = 11;
        } else if(stringCardRank.equals("0")) {
            cardRank = 12;
        } else if(stringCardRank.equals("K")) {
            cardRank = 13;
        } else if(stringCardRank.equals("A")) {
            cardRank = 14;
        }
        return cardRank;
    }
}
