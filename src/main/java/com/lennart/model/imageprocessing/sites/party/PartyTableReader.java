package com.lennart.model.imageprocessing.sites.party;

import com.lennart.model.action.actionbuilders.ai.HandHistoryReaderParty;
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
public class PartyTableReader {

    private int regNewSngWaitCouner = 0;

    public double getBotStackFromImage() throws Exception {
        return 0;
    }

    public double getOpponentStackFromImage() throws Exception {
        return 0;
    }

    public double getTopPotsizeFromImage() throws Exception {
        return 0;
    }

    public Card getBotHoleCard1FromImage() {
        return null;
    }

    public Card getBotHoleCard2FromImage() {
        return null;
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
        return null;
    }

    public static void performActionOnSite(String botAction, double sizing, List<Card> boardBeforePerfomingAction) throws Exception {

    }

    //helper methods
    private static void clickFoldActionButton() throws Exception {

    }

    private static void clickCheckActionButton(List<Card> boardBeforePerformingAction) throws Exception {

    }

    private static void clickCallActionButton() throws Exception {

    }

    private static void clickBetActionButton() throws Exception {

    }

    private static void clickRaiseActionButton() throws Exception {

    }

    private static String readLeftActionButton() {
        return null;
    }

    private static String readMiddleActionButton() {
        return null;
    }

    private static String readRightActionButton() {
        return null;
    }

    private String readFirstHoleCardRank() {
        return null;
    }

    private String readSecondHoleCardRank() {
        return null;
    }

    public static void main(String[] args) {
        PartyTableReader partyTableReader = new PartyTableReader();

        partyTableReader.testMethodje();

//        char suit1 = partyTableReader.readFirstFlopCardSuitFromBoard();
//        char suit2 = partyTableReader.readSecondFlopCardSuitFromBoard();
//        char suit3 = partyTableReader.readThirdFlopCardSuitFromBoard();
//        char suit4 = partyTableReader.readTurnCardSuitFromBoard();
//        char suit5 = partyTableReader.readRiverCardSuitFromBoard();

        //clubs
        //suit1: -15500223

        //spades
        //suit2: -13356237

        //hearts
        //suit2: -7861227
        //suit3: -7533546
        //suit3: -7468010

        //diamonds:
        //suit4: -14728023
        //suit3: -15255135

        //suit3: -14662487


//        int flopCard1rank = partyTableReader.getIntCardRank(partyTableReader.readFirstFlopCardRankFromBoard());
//        int flopCard2rank = partyTableReader.getIntCardRank(partyTableReader.readSecondFlopCardRankFromBoard());
//        int flopCard3rank = partyTableReader.getIntCardRank(partyTableReader.readThirdFlopCardRankFromBoard());
//
//        int turnCardRank = partyTableReader.getIntCardRank(partyTableReader.readTurnCardRankFromBoard());
//        int riverCardRank = partyTableReader.getIntCardRank(partyTableReader.readRiverCardRankFromBoard());
//
//
//        System.out.println("Board: " + flopCard1rank + " " + flopCard2rank + " " + flopCard3rank + " " + turnCardRank + " " + riverCardRank);
    }

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
        return 'a';
    }

    private char readSecondHoleCardSuit() {
        return 'a';
    }

    private char readFirstFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(357, 278, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        System.out.println("suit1: " + suitRgb);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(431, 278, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        System.out.println("suit2: " + suitRgb);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readThirdFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(505, 278, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        System.out.println("suit3: " + suitRgb);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readTurnCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(579, 278, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        System.out.println("suit4: " + suitRgb);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readRiverCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(653, 278, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        System.out.println("suit5: " + suitRgb);
        return getSuitFromIntRgb(suitRgb);
    }

    private String readBottomPlayerStack() {
        return null;
    }

    private String readTopPlayerStack() {
        return null;
    }

    private static String readTopPlayerStackBase() {
        return null;
    }

    private String readTotalPotSize() {
        return null;
    }

    public double readBigBlindFromSngScreen() throws Exception {
        return -1;
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
        return false;
    }

    private char getSuitFromIntRgb(int rgb) {
        char suit = 'x';
        rgb = rgb / 100_000;

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


        if(rgb == -155 || rgb == -156 || rgb == -1) {
            suit = 'c';
        } else if(rgb == -78 || rgb == -75 || rgb == -74 || rgb == -100 || rgb == -77 || rgb == -79) {
            suit = 'h';
        } else if(rgb == -133) {
            suit = 's';
        } else if(rgb == -147 || rgb == -152 || rgb == -146) {
            suit = 'd';
        }
        return suit;
    }


    //330, 280
    //405, 280


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
