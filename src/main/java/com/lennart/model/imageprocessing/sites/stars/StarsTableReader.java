package com.lennart.model.imageprocessing.sites.stars;

import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.ImageProcessor;
import org.apache.commons.math3.util.Precision;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lennart on 3/12/2017.
 */
public class StarsTableReader {

    private double bigBlind;

    public StarsTableReader(double bigBlind) {
        this.bigBlind = bigBlind;
    }

    public double getBotStackFromImage() throws Exception {
        String botStackAsString = readLeftPlayerStack();

        if(botStackAsString.endsWith(".")) {
            botStackAsString.replaceAll(".", "");
        }

        if(botStackAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            return Double.parseDouble(botStackAsString);
        } else {
            return -1;
        }
    }

    public double getOpponentStackFromImage() throws Exception {
        String opponentStackAsString = readRightPlayerStack();

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
        String topPotsize = readTopTotalPotSize();

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
        //to implement

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(926, 965, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        if(suitRgb / 10_000 == -1674) {
            //expected rgb: -16743748
            return true;
        }
        return false;
    }

    public static boolean isNewHand() {
        //to implement

        return false;
    }

    public String getOpponentPlayerNameFromImage() {
        //to implement

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(687, 152, 165, 39);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        String fullPlayerName = ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);

        if(fullPlayerName.length() >= 4) {
            return fullPlayerName.substring(0, 4);
        }
        return fullPlayerName;
    }

    public static void performActionOnSite(String botAction, double sizing) {
        //to implement

        if(botAction != null && sizing != 0) {
            try {
                MouseKeyboard.click(674, 647);
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
        //to implement


    }

    private static void clickCheckActionButton() {
        //to implement


    }

    private static void clickCallActionButton() {
        //to implement


    }

    private static void clickBetActionButton() {
        //to implement


    }

    private static void clickRaiseActionButton() {
        //to implement


    }

    private String readFirstHoleCardRank() {
        //to implement

        return null;
    }

    private String readSecondHoleCardRank() {
        //to implement

        return null;
    }

    private String readFirstFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(368, 259, 19, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

    private String readSecondFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(444, 259, 19, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String secondFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(secondFlopCardRank);
    }

    private String readThirdFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(520, 259, 19, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String thirdFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(thirdFlopCardRank);
    }

    private String readTurnCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(596, 259, 19, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String turnCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(turnCardRank);
    }

    private String readRiverCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(672, 259, 19, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String riverCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(riverCardRank);
    }

    private char readFirstHoleCardSuit() {
        //to implement

        return 'a';
    }

    private char readSecondHoleCardSuit() {
        //to implement

        return 'a';
    }



    private char readFirstFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(378, 294, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        //System.out.println("F1: " + suitRgb);

        //return 'a';
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(454, 294, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        //System.out.println("F2: " + suitRgb);

        //return 'a';
        return getSuitFromIntRgb(suitRgb);
    }

    private char readThirdFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(530, 294, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        //System.out.println("F3: " + suitRgb);

        //return 'a';
        return getSuitFromIntRgb(suitRgb);
    }

    private char readTurnCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(606, 294, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        //System.out.println("T: " + suitRgb);

        //return 'a';
        return getSuitFromIntRgb(suitRgb);
    }

    private char readRiverCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(682, 294, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        //System.out.println("R: " + suitRgb);

        //return 'a';
        return getSuitFromIntRgb(suitRgb);
    }

    private String readRightPlayerStack() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(50, 421, 123, 21);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);
        return ImageProcessor.removeAllNonNumericCharacters(topPlayerStack);
    }

    private String readLeftPlayerStack() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(924, 421, 123, 21);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String bottomPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        bottomPlayerStack = ImageProcessor.removeEmptySpacesFromString(bottomPlayerStack);
        return ImageProcessor.removeAllNonNumericCharacters(bottomPlayerStack);
    }


    //welke potsize methode kan weg?
    private static String readPotSize() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(472, 407, 126, 17);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);

        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String potSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        potSize = ImageProcessor.removeEmptySpacesFromString(potSize);
        return ImageProcessor.removeAllNonNumericCharacters(potSize);
    }

    private String readTopTotalPotSize() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(469, 61, 147, 28);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String topPlayerTotalBetSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerTotalBetSize = ImageProcessor.removeEmptySpacesFromString(topPlayerTotalBetSize);
        return ImageProcessor.removeAllNonNumericCharacters(topPlayerTotalBetSize);
    }

    private String readNormalTotalPotSize() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(463, 406, 105, 18);
        //bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 3);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String topPlayerTotalBetSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerTotalBetSize = ImageProcessor.removeEmptySpacesFromString(topPlayerTotalBetSize);
        return ImageProcessor.removeAllNonNumericCharacters(topPlayerTotalBetSize);
    }

    public static void saveScreenshotOfEntireScreen(int numberOfActionRequests) throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(0, 0, 3000, 1250);
        ImageProcessor.saveBufferedImage(bufferedImage, "C:/Users/Lennart/Documents/develop/logging/" + numberOfActionRequests + ".png");
    }

    public static void saveScreenshotOfEntireScreen(long time) throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(0, 0, 3000, 1250);
        ImageProcessor.saveBufferedImage(bufferedImage, "C:/Users/Lennart/Documents/develop/logging/" + time + ".png");
    }

    public boolean leftPlayerIsButton() {
        //to implement

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(640, 705, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        if(suitRgb / 1000 == -10) {
            //expected rgb: -10240
            return true;
        }
        return false;
    }

    private char getSuitFromIntRgb(int rgb) {
        char suit = 'x';
        rgb = rgb / 1_000_000;

        //spades: -6250336
        //clubs: -8012917
        //hearts: -4128753
        //diamonds: -16121712

        if(rgb == -8) {
            suit = 'c';
        } else if(rgb == -4) {
            suit = 'h';
        } else if(rgb == -6) {
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
        } else if(stringCardRank.contains("3")) {
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
        } else if(stringCardRank.contains("10")) {
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
