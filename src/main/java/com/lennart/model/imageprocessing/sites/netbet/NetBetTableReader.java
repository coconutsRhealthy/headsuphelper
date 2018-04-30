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

    public double getPotSizeFromImage(boolean postFlop, double opponentTotalBetSize, double botTotalBetSize) throws Exception {
        double potSize;
        String potSizeAsString = readPotSize(true);

        if(potSizeAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            potSize = Double.parseDouble(potSizeAsString);
        } else {
            potSize = 0.0;
        }

        potSize = validateReadNumber(potSize);

        if(postFlop && potSize == 0) {
            TimeUnit.MILLISECONDS.sleep(60);
            mediumSizeTable();
            TimeUnit.MILLISECONDS.sleep(60);
            potSizeAsString = readPotSizeOld();
            potSize = getCorrectValueFromReadPotSizeOld(potSizeAsString, opponentTotalBetSize, botTotalBetSize);
            TimeUnit.MILLISECONDS.sleep(60);
            maximizeTable();
            TimeUnit.MILLISECONDS.sleep(60);
        }
        return potSize;
    }

    public double getOpponentStackFromImage() throws Exception {
        List<Double> foundValues = new ArrayList<>();

        for(int i = 0; i < 2; i++) {
            String opponentStackAsString = readTopPlayerStack();

            if(opponentStackAsString.endsWith(".")) {
                opponentStackAsString.replaceAll(".", "");
            }

            if(opponentStackAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                double opponentStack = Double.parseDouble(opponentStackAsString);
                opponentStack = validateStackSizeReadNumber(opponentStack);
                foundValues.add(opponentStack);
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

            if(botStackAsString.endsWith(".")) {
                botStackAsString.replaceAll(".", "");
            }

            if(botStackAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
                double botStack = Double.parseDouble(botStackAsString);
                botStack = validateStackSizeReadNumber(botStack);
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
        for(int i = 0; i < 10; i++) {
            String bottomChatLine = readBottomChatLine();
            String opponentAction = getActionFromChatLine(bottomChatLine);

            System.out.println("bottomChatline raw: " + bottomChatLine);
            System.out.println("opponentAction from bottomChatline: " + opponentAction);

            if(opponentAction != null) {
                return opponentAction;
            }
        }

        String bottomChatLine = readBottomChatLine();
        bottomChatLine = ImageProcessor.removeEmptySpacesFromString(bottomChatLine);

        if(bottomChatLine.equals("")) {
            //first hand of table...
            String middleChatLine = readMiddleChatLine();
            String opponentAction = getActionFromChatLine(middleChatLine);

            System.out.println("First action of table... action read from middle chatline: " + opponentAction);
            return opponentAction;
        }

        String actionToReturn = readMiddleChatLine();

        if(actionToReturn != null) {
            return getActionFromChatLine(actionToReturn);
        }

        return null;
    }

    private String getActionFromChatLine(String chatLine) {
        String action = null;

        if(chatLine.contains("COCONUT")) {
            action = "empty";
        } else if(chatLine.contains("posts") || chatLine.contains("blind")) {
            action = "bet";
        } else if(chatLine.contains("checks") || chatLine.contains("checls")) {
            action = "check";
        } else if(chatLine.contains("calls") || chatLine.contains("ca lls") || chatLine.contains("cal ls")) {
            action = "call";
        } else if(chatLine.contains("bets") || chatLine.contains("beis") || chatLine.contains("be'ls")) {
            action = "bet75pct";
        } else if(chatLine.contains("raise") || chatLine.contains("goes ") || chatLine.contains("laise")) {
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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(926, 965, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        if(suitRgb / 10_000 == -1674) {
            //expected rgb: -16743748
            return true;
        }
        return false;
    }

    public static boolean isNewHand() {
        String middleChatLine = readMiddleChatLine();
        String bottomChatLine = readBottomChatLine();

        if(middleChatLine.contains("posts") || bottomChatLine.contains("posts")
                || middleChatLine.contains("blind") || bottomChatLine.contains("blind")) {
            return true;
        }
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
    private static void clickFoldActionButton() {
        if(readLeftActionButton().contains("Fold")) {
            MouseKeyboard.click(721, 685);
        } else {
            System.out.println("Clicking Fold button failed");
        }
    }

    private static void clickCheckActionButton() {
        if(readMiddleActionButton().contains("Check")) {
            MouseKeyboard.click(841, 682);
        } else {
            System.out.println("Clicking Check button failed");
        }
    }

    private static void clickCallActionButton() {
        String middleActionButton = readMiddleActionButton();
        String rightActionButton = readRightActionButton();

        if(middleActionButton.contains("Call")) {
            MouseKeyboard.click(841, 682);
        } else if(middleActionButtonIsNotPresent() && rightActionButton.contains("All")) {
            MouseKeyboard.click(959, 687);
        } else {
            System.out.println("Clicking Call button failed");
        }
    }

    private static void clickBetActionButton() {
        String middleActionButton = readMiddleActionButton();
        String rightActionButton = readRightActionButton();

        if(rightActionButton.contains("Bet")) {
            MouseKeyboard.click(959, 687);
        } else if(middleActionButton.contains("Check") && rightActionButton.contains("All")) {
            MouseKeyboard.click(959, 687);
        } else {
            System.out.println("Clicking Bet button failed");
        }
    }

    private static void clickRaiseActionButton() {
        String rightActionButton = readRightActionButton();

        if(rightActionButton.contains("Raise")) {
            MouseKeyboard.click(959, 687);
        } else if(rightActionButton.contains("All")) {
            MouseKeyboard.click(959, 687);
        } else {
            System.out.println("Clicking Raise button failed");
        }
    }

    private static String readMiddleChatLine() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(19, 869, 441, 38);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        return ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
    }

    private static String readBottomChatLine() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(19, 906, 441, 34);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        return ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
    }

    private String readFirstHoleCardRank() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(493, 478, 17, 19);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String firstHoleCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstHoleCardRank);
    }

    private String readSecondHoleCardRank() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(557, 478, 17, 19);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String secondHoleCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(secondHoleCardRank);
    }

    private String readFirstFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(332, 289, 17, 19);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);
    }

    private String readSecondFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(403, 289, 17, 19);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String secondFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(secondFlopCardRank);
    }

    private String readThirdFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(474, 289, 17, 19);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String thirdFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(thirdFlopCardRank);
    }

    private String readTurnCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(545, 289, 17, 19);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String turnCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(turnCardRank);
    }

    private String readRiverCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(616, 289, 17, 19);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String riverCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(riverCardRank);
    }

    private char readFirstHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(501, 503, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(566, 506, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readFirstFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(341, 318, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(411, 318, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readThirdFlopCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(481, 318, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readTurnCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(551, 318, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readRiverCardSuitFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(621, 318, 1, 1);
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

        if(potSize.contains("molt")) {
            return "0.04";
        }

        potSize = ImageProcessor.removeEmptySpacesFromString(potSize);
        return ImageProcessor.removeAllNonNumericCharacters(potSize);
    }

    private static String readPotSizeOld() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(430, 255, 167, 28);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);

        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String potSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        potSize = ImageProcessor.removeEmptySpacesFromString(potSize);
        return ImageProcessor.removeAllNonNumericCharacters(potSize);
    }

    private double getCorrectValueFromReadPotSizeOld(String potSizeAsString, double opponentTotalBetSize,
                                                     double botTotalBetSize) {
        double potSize;

        if(potSizeAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            potSize = Double.parseDouble(potSizeAsString);
        } else {
            potSize = 0.0;
        }

        potSize = validateStackSizeReadNumber(potSize);

        if(potSize != 0) {
            potSize = potSize - opponentTotalBetSize - botTotalBetSize;
        }

        return potSize;
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

    public static void saveScreenshotOfEntireScreen(int numberOfActionRequests) throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(0, 0, 3000, 1250);
        ImageProcessor.saveBufferedImage(bufferedImage, "C:/Users/Lennart/Documents/develop/logging/" + numberOfActionRequests + ".png");
    }

    public static void saveScreenshotOfEntireScreen(long time) throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(0, 0, 3000, 1250);
        ImageProcessor.saveBufferedImage(bufferedImage, "C:/Users/Lennart/Documents/develop/logging/" + time + ".png");
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
            valueToReturn = -1;
        } else if(valueInBb <= 500) {
            valueToReturn = value;
        } else {
            double adjustedValueInBb1 = (value / 10) / bigBlind;

            if(adjustedValueInBb1 > 500) {
                double adjustedValueInBb2 = (value / 100) / bigBlind;

                if(adjustedValueInBb2 > 500) {
                    double adjustedValueInBb3 = (value / 1000) / bigBlind;

                    if(adjustedValueInBb3 > 500) {
                        valueToReturn = -1;
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

    private double validateStackSizeReadNumber(double value) {
        double valueToReturn;

        double valueInBb = value / bigBlind;

        if(valueInBb < 0) {
            valueToReturn = -1;
        } else if(valueInBb <= 600) {
            valueToReturn = value;
        } else {
            double adjustedValueInBb1 = (value / 10) / bigBlind;

            if(adjustedValueInBb1 > 600) {
                double adjustedValueInBb2 = (value / 100) / bigBlind;

                if(adjustedValueInBb2 > 600) {
                    double adjustedValueInBb3 = (value / 1000) / bigBlind;

                    if(adjustedValueInBb3 > 600) {
                        valueToReturn = -1;
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

    private void mediumSizeTable() {
        MouseKeyboard.click(1269, 25);
    }

    private void maximizeTable() {
        MouseKeyboard.click(983, 16);
    }
}
