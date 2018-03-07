package com.lennart.model.imageprocessing.sites.netbet;

import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.ImageProcessor;
import org.apache.commons.math3.util.Precision;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lennart on 3/12/2017.
 */
public class NetBetTableReader {

    private double bigBlind;

    public NetBetTableReader(double bigBlind) {
        this.bigBlind = bigBlind;
    }

    public double getPotSizeFromImage() {
        double potSize;
        String potSizeAsString = readPotSize(true);

        if(potSizeAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            potSize = Double.parseDouble(potSizeAsString);
        } else {
            potSize = 0.0;
        }

        potSize = validateReadNumber(potSize);
        return potSize;
    }

    public double getOpponentStackFromImage() throws Exception {
        List<Double> foundValues = new ArrayList<>();

        for(int i = 0; i < 2; i++) {
            String botStackAsString = readTopPlayerStack();

            if(botStackAsString.endsWith(".")) {
                botStackAsString.replaceAll(".", "");
            }

            if(botStackAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                double botStack = Double.parseDouble(botStackAsString);
                botStack = validateReadNumber(botStack);
                foundValues.add(botStack);
            }
        }

        Collections.sort(foundValues, Collections.reverseOrder());

        if(!foundValues.isEmpty()) {
            return foundValues.get(0);
        }
        return -1;
    }

    public double getBotStackFromImage() throws Exception {
        List<Double> foundValues = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            String botStackAsString = readBottomPlayerStack();

            System.out.println(botStackAsString);

            if(botStackAsString.endsWith(".")) {
                botStackAsString.replaceAll(".", "");
            }

            if(botStackAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                double botStack = Double.parseDouble(botStackAsString);
                botStack = validateReadNumber(botStack);
                foundValues.add(botStack);
            }
        }

        Collections.sort(foundValues, Collections.reverseOrder());

        if(!foundValues.isEmpty()) {
            return foundValues.get(0);
        }
        return -1;
    }

    public double getBotTotalBetSizeFromImage() {
        double botTotalBetSize;
        String botTotalBetSizeAsString = readBottomPlayerTotalBetSize();

        if(botTotalBetSizeAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            botTotalBetSize = Double.parseDouble(botTotalBetSizeAsString);
        } else {
            botTotalBetSize = 0.0;
        }

        botTotalBetSize = validateReadNumber(botTotalBetSize);
        return botTotalBetSize;
    }

    public double getOpponentTotalBetSizeFromImage() {
        double opponentTotalBetSize;
        String opponentTotalBetSizeAsString = readTopPlayerTotalBetSize();

        if(opponentTotalBetSizeAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            opponentTotalBetSize = Double.parseDouble(opponentTotalBetSizeAsString);
        } else {
            opponentTotalBetSize = 0.0;
        }

        opponentTotalBetSize = validateReadNumber(opponentTotalBetSize);
        return opponentTotalBetSize;
    }

    public String getOpponentAction() {
        String bottomChatLine = readBottomChatLine();
        String opponentAction = getActionFromChatLine(bottomChatLine);

        if(opponentAction == null) {
            String middleChatLine = readMiddleChatLine();
            opponentAction = getActionFromChatLine(middleChatLine);
        }

        return opponentAction;
    }

    private String getActionFromChatLine(String chatLine) {
        String action = null;

        if(chatLine.contains("COCONUT")) {
            action = "empty";
        } else if(chatLine.contains("posts")) {
            action = "bet";
        } else if(chatLine.contains("checks")) {
            action = "check";
        } else if(chatLine.contains("calls")) {
            action = "call";
        } else if(chatLine.contains("bets")) {
            action = "bet75pct";
        } else if(chatLine.contains("raise")) {
            action = "raise";
        }

        return action;
    }

    public boolean isBotButtonFromImage() {
        return bottomPlayerIsButton();
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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(686, 679, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        if(suitRgb / 10_000 == -1674) {
            //expected rgb: -16743748
            return true;
        }
        System.out.print(".");
        return false;
    }

    public String getOpponentPlayerNameFromImage() {
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

    public static boolean middleActionButtonIsNotPresent() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(786, 712, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        if(suitRgb / 1000 == -16673) {
            //expected: -16673794
            return false;
        }
        //when not present, expected: -16641770
        return true;
    }

    //helper methods
    private static boolean clickFoldActionButton() {
        if(readLeftActionButton().contains("Fold")) {
            MouseKeyboard.click(721, 685);
            return true;
        }
        return false;
    }

    private static boolean clickCheckActionButton() {
        if(readMiddleActionButton().contains("Check")) {
            MouseKeyboard.click(841, 682);
            return true;
        }
        return false;
    }

    private static boolean clickCallActionButton() {
        String middleActionButton = readMiddleActionButton();
        String rightActionButton = readRightActionButton();

        if(middleActionButton.contains("Call")) {
            MouseKeyboard.click(841, 682);
            return true;
        } else if(middleActionButtonIsNotPresent() && rightActionButton.contains("All")) {
            MouseKeyboard.click(959, 687);
            return true;
        }
        return false;
    }

    private static boolean clickBetActionButton() {
        String middleActionButton = readMiddleActionButton();
        String rightActionButton = readRightActionButton();

        boolean clickActionDone = false;

        if(rightActionButton.contains("Bet")) {
            MouseKeyboard.click(959, 687);
            clickActionDone = true;
        } else if(middleActionButton.contains("Check") && rightActionButton.contains("All")) {
            MouseKeyboard.click(959, 687);
            clickActionDone = true;
        }

        return clickActionDone;
    }

    private static boolean clickRaiseActionButton() {
        String rightActionButton = readRightActionButton();

        boolean clickActionDone = false;

        if(rightActionButton.contains("Raise")) {
            MouseKeyboard.click(959, 687);
            clickActionDone = true;
        } else if(rightActionButton.contains("All")) {
            MouseKeyboard.click(959, 687);
            clickActionDone = true;
        }

        return clickActionDone;
    }

    private String readTopChatLine() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(19, 840, 441, 34);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        return ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
    }

    private String readMiddleChatLine() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(19, 869, 441, 38);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        return ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
    }

    public String readBottomChatLine() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(19, 906, 441, 34);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        return ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
    }

    private String readFirstHoleCardRank() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(684, 664, 26, 24);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String firstHoleCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstHoleCardRank);
    }

    private String readSecondHoleCardRank() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(774, 663, 24, 26);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String secondHoleCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(secondHoleCardRank);
    }

    private String readFirstFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(458, 401, 25, 26);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

    private String readSecondFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(558, 401, 27, 27);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String secondFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(secondFlopCardRank);
    }

    private String readThirdFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(658, 401, 27, 27);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String thirdFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(thirdFlopCardRank);
    }

    private String readTurnCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(758, 401, 27, 27);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String turnCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(turnCardRank);
    }

    private String readRiverCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(858, 401, 27, 27);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String riverCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(riverCardRank);
    }

    private char readFirstHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(697, 702, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(786, 699, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readFirstFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(473, 437, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(572, 440, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readThirdFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(671, 440, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readTurnCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(770, 441, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readRiverCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(870, 440, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private String readTopPlayerStack() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(708, 205, 122, 34);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);
        return ImageProcessor.removeAllNonNumericCharacters(topPlayerStack);
    }

    private String readBottomPlayerStack() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(704, 799, 126, 36);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String bottomPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        bottomPlayerStack = ImageProcessor.removeEmptySpacesFromString(bottomPlayerStack);
        return ImageProcessor.removeAllNonNumericCharacters(bottomPlayerStack);
    }

    private static String readPotSize(boolean includingZoom) {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(709, 547, 72, 29);

        if(includingZoom) {
            bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        }

        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String potSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        potSize = ImageProcessor.removeEmptySpacesFromString(potSize);
        return ImageProcessor.removeAllNonNumericCharacters(potSize);
    }

    private String readBottomPlayerTotalBetSize() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(673, 626, 80, 26);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String bottomPlayerTotalBetSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        bottomPlayerTotalBetSize = ImageProcessor.removeEmptySpacesFromString(bottomPlayerTotalBetSize);
        return ImageProcessor.removeAllNonNumericCharacters(bottomPlayerTotalBetSize);
    }

    private String readTopPlayerTotalBetSize() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(628, 266, 78, 24);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String topPlayerTotalBetSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerTotalBetSize = ImageProcessor.removeEmptySpacesFromString(topPlayerTotalBetSize);
        return ImageProcessor.removeAllNonNumericCharacters(topPlayerTotalBetSize);
    }

    private static String readLeftActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(664, 662, 111, 54);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String leftActionButton = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(leftActionButton);
    }

    public static String readMiddleActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(783, 662, 111, 54);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String leftActionButton = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(leftActionButton);
    }

    public static String readRightActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(903, 662, 111, 54);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String leftActionButton = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(leftActionButton);
    }

    private boolean bottomPlayerIsButton() {
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

        if(rgb == -13) {
            suit = 'c';
        } else if(rgb == -2) {
            suit = 'h';
        } else if(rgb == -16) {
            suit = 's';
        } else if(rgb == -15) {
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

    private double validateReadNumber(double value) {
        double valueToReturn;

        double valueInBb = value / bigBlind;

        if(valueInBb < 0) {
            valueToReturn = 0;
        } else if(valueInBb <= 300) {
            valueToReturn = value;
        } else {
            double adjustedValueInBb1 = (value / 10) / bigBlind;

            if(adjustedValueInBb1 > 300) {
                double adjustedValueInBb2 = (value / 100) / bigBlind;

                if(adjustedValueInBb2 > 300) {
                    double adjustedValueInBb3 = (value / 1000) / bigBlind;

                    if(adjustedValueInBb3 > 300) {
                        valueToReturn = 0.0;
                    } else {
                        valueToReturn = value / 1000;
                    }
                } else {
                    valueToReturn = value / 100;
                }
            } else {
                valueToReturn = value / 10;
            }
        }

        return valueToReturn;
    }
}
