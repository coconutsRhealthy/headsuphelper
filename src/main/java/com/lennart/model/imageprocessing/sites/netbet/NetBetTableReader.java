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

    public static void main(String[] args) {
        new NetBetTableReader(0.0).getPotSizeFromImage();
    }

    public double getPotSizeFromImage() {
        double potSize;
        List<String> readValues = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            readValues.add(readPotSize(1, false, false));
            readValues.add(readPotSize(1, false, true));
            readValues.add(readPotSize(1, true, false));
            readValues.add(readPotSize(1, true, true));
        }

        for(int i = 0; i < 3; i++) {
            readValues.add(readPotSize(2, false, false));
            readValues.add(readPotSize(2, false, true));
            readValues.add(readPotSize(2, true, false));
            readValues.add(readPotSize(2, true, true));
        }

        potSize = getDoubleFromStringList(readValues);
        return potSize;
    }

    public double getOpponentStackFromImage() {
        double opponentStack;
        List<String> readValues = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            readValues.add(readTopPlayerStack(1, false, false));
            readValues.add(readTopPlayerStack(1, false, true));
            readValues.add(readTopPlayerStack(1, true, false));
            readValues.add(readTopPlayerStack(1, true, true));
        }

        for(int i = 0; i < 3; i++) {
            readValues.add(readTopPlayerStack(2, false, false));
            readValues.add(readTopPlayerStack(2, false, true));
            readValues.add(readTopPlayerStack(2, true, false));
            readValues.add(readTopPlayerStack(2, true, true));
        }

        opponentStack = getDoubleFromStringList(readValues);
        return opponentStack;
    }

    public double getBotStackFromImage() {
        double botStack;
        List<String> readValues = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            readValues.add(readBottomPlayerStack(1, false, false));
            readValues.add(readBottomPlayerStack(1, false, true));
            readValues.add(readBottomPlayerStack(1, true, false));
            readValues.add(readBottomPlayerStack(1, true, true));
        }

        for(int i = 0; i < 3; i++) {
            readValues.add(readBottomPlayerStack(2, false, false));
            readValues.add(readBottomPlayerStack(2, false, true));
            readValues.add(readBottomPlayerStack(2, true, false));
            readValues.add(readBottomPlayerStack(2, true, true));
        }

        botStack = getDoubleFromStringList(readValues);
        return botStack;
    }

    public double getBotTotalBetSizeFromImage() {
        double botTotalBetSize;
        List<String> readValues = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            readValues.add(readBottomPlayerTotalBetSize(1, false, false));
            readValues.add(readBottomPlayerTotalBetSize(1, false, true));
            readValues.add(readBottomPlayerTotalBetSize(1, true, false));
            readValues.add(readBottomPlayerTotalBetSize(1, true, true));
        }

        for(int i = 0; i < 3; i++) {
            readValues.add(readBottomPlayerTotalBetSize(2, false, false));
            readValues.add(readBottomPlayerTotalBetSize(2, false, true));
            readValues.add(readBottomPlayerTotalBetSize(2, true, false));
            readValues.add(readBottomPlayerTotalBetSize(2, true, true));
        }

        botTotalBetSize = getDoubleFromStringList(readValues);
        return botTotalBetSize;
    }

    public double getOpponentTotalBetSizeFromImage() {
        double opponentTotalBetSize;
        List<String> readValues = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            readValues.add(readTopPlayerTotalBetSize(1, false, false));
            readValues.add(readTopPlayerTotalBetSize(1, false, true));
            readValues.add(readTopPlayerTotalBetSize(1, true, false));
            readValues.add(readTopPlayerTotalBetSize(1, true, true));
        }

        for(int i = 0; i < 3; i++) {
            readValues.add(readTopPlayerTotalBetSize(2, false, false));
            readValues.add(readTopPlayerTotalBetSize(2, false, true));
            readValues.add(readTopPlayerTotalBetSize(2, true, false));
            readValues.add(readTopPlayerTotalBetSize(2, true, true));
        }

        opponentTotalBetSize = getDoubleFromStringList(readValues);
        return opponentTotalBetSize;
    }

    public String getOpponentAction() {
        String opponentAction = null;

        String bottomChatLine = readBottomChatLine();

        if(bottomChatLine.contains("COCONUT")) {
            opponentAction = "empty";
        } else if(bottomChatLine.contains("posts")) {
            opponentAction = "bet";
        } else if(bottomChatLine.contains("checks")) {
            opponentAction = "check";
        } else if(bottomChatLine.contains("calls")) {
            opponentAction = "call";
        } else if(bottomChatLine.contains("bets")) {
            opponentAction = "bet75pct";
        } else if(bottomChatLine.contains("raise")) {
            opponentAction = "raise";
        }

        return opponentAction;
    }

    public Map<String, String> getActionsFromLastThreeChatLines() {
        Map<String, String> actionsFromChat = new HashMap<>();
        String topChatLine = readTopChatLine();
        String middleChatLine = readMiddleChatLine();
        String bottomChatLine = readBottomChatLine();

        actionsFromChat.put("top", getActionFromChatLine(topChatLine));
        actionsFromChat.put("middle", getActionFromChatLine(middleChatLine));
        actionsFromChat.put("bottom", getActionFromChatLine(bottomChatLine));

        return actionsFromChat;
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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(495, 110, 117, 28);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        String fullPlayerName = ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);

        if(fullPlayerName.length() >= 4) {
            return fullPlayerName.substring(0, 4);
        }
        return null;
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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(19, 873, 441, 34);
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

    private String readTopPlayerStack(int zoomLevel,  boolean invertedColours, boolean blackAndWhite) {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(708, 205, 122, 34);

        if(zoomLevel != 1) {
            bufferedImage = ImageProcessor.zoomInImage(bufferedImage, zoomLevel);
        }

        if(invertedColours) {
            bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        }

        if(blackAndWhite) {
            bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        }

        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);
        return ImageProcessor.removeAllNonNumericCharacters(topPlayerStack);
    }

    private String readBottomPlayerStack(int zoomLevel,  boolean invertedColours, boolean blackAndWhite) {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(704, 799, 126, 36);

        if(zoomLevel != 1) {
            bufferedImage = ImageProcessor.zoomInImage(bufferedImage, zoomLevel);
        }

        if(invertedColours) {
            bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        }

        if(blackAndWhite) {
            bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        }

        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);
        return ImageProcessor.removeAllNonNumericCharacters(topPlayerStack);
    }

    private static String readPotSize(int zoomLevel,  boolean invertedColours, boolean blackAndWhite) {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(709, 547, 72, 29);

        if(zoomLevel != 1) {
            bufferedImage = ImageProcessor.zoomInImage(bufferedImage, zoomLevel);
        }

        if(invertedColours) {
            bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        }

        if(blackAndWhite) {
            bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        }

        String potSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        potSize = ImageProcessor.removeEmptySpacesFromString(potSize);
        return ImageProcessor.removeAllNonNumericCharacters(potSize);
    }

    private String readBottomPlayerTotalBetSize(int zoomLevel,  boolean invertedColours, boolean blackAndWhite) {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(673, 626, 80, 26);

        if(zoomLevel != 1) {
            bufferedImage = ImageProcessor.zoomInImage(bufferedImage, zoomLevel);
        }

        if(invertedColours) {
            bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        }

        if(blackAndWhite) {
            bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        }

        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);
        return ImageProcessor.removeAllNonNumericCharacters(topPlayerStack);
    }

    private String readTopPlayerTotalBetSize(int zoomLevel,  boolean invertedColours, boolean blackAndWhite) {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(628, 266, 78, 24);

        if(zoomLevel != 1) {
            bufferedImage = ImageProcessor.zoomInImage(bufferedImage, zoomLevel);
        }

        if(invertedColours) {
            bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        }

        if(blackAndWhite) {
            bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        }

        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);
        return ImageProcessor.removeAllNonNumericCharacters(topPlayerStack);
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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(461, 506, 1, 1);
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

    private String getActionFromChatLine(String chatLine) {
        String action = null;

        if(chatLine.contains("checks")) {
            action = "check";
        } else if(chatLine.contains("calls")) {
            action = "call";
        } else if(chatLine.contains("bets")) {
            action = "bet";
        } else if(chatLine.contains("raises")) {
            action = "raise";
        } else if(chatLine.contains("post")) {
            action = "post";
        } else if(chatLine.contains("Deal") && chatLine.contains("car")) {
            action = "deal";
        }
        return action;
    }

    private double getDoubleFromStringList(List<String> readValues) {
        Map<Integer, Double> readDoublesMap = new HashMap<>();

        int counter = 0;

        for(String value : readValues) {
            if(value.matches(".*\\d.*")) {
                readDoublesMap.put(counter, Double.parseDouble(value));
            } else {
                readDoublesMap.put(counter, 0.0);
            }
            counter++;
        }

        readDoublesMap = removeOutliers(readDoublesMap);
        Map<Double, Integer> frequencyMap = getFrequencyMap(readDoublesMap);
        frequencyMap = sortByValueHighToLow(frequencyMap);

        double toReturn = frequencyMap.entrySet().iterator().next().getKey();
        return toReturn;
    }

    private Map<Double, Integer> getFrequencyMap(Map<Integer, Double> mapToAnalyse) {
        Map<Double, Integer> frequencyMap = new HashMap<>();
        List<Double> valuesAsList = new ArrayList<>(mapToAnalyse.values());

        for (Map.Entry<Integer, Double> entry : mapToAnalyse.entrySet()) {
            int frequency =  Collections.frequency(valuesAsList, entry.getValue());
            frequencyMap.put(entry.getValue(), frequency);
        }
        return frequencyMap;
    }

    private Map<Integer, Double> removeOutliers(Map<Integer, Double> readDoublesMap) {
        Map<Integer, Double> mapToReturn = new HashMap<>();

        for (Map.Entry<Integer, Double> entry : readDoublesMap.entrySet()) {
            if(entry.getValue() / bigBlind < 2000 || entry.getValue() < 0) {
                mapToReturn.put(entry.getKey(), entry.getValue());
            }
        }

        return mapToReturn;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
