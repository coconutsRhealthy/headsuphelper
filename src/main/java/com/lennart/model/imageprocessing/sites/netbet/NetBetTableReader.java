package com.lennart.model.imageprocessing.sites.netbet;

import com.lennart.model.action.Action;
import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.ImageProcessor;
import org.apache.commons.math3.util.Precision;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
        String potSizeAsString = readPotSize();
        if(potSizeAsString.matches(".*\\d.*")){
            potSize = Double.parseDouble(potSizeAsString);
        } else {
            potSizeAsString = readPotSize();
            if(potSizeAsString.matches(".*\\d.*")){
                potSize = Double.parseDouble(potSizeAsString);
            } else {
                potSize = -1;
                System.out.println("potSize not read well: -1");
            }
        }

        if(potSize / bigBlind > 50) {
            String timeStamp = getCurrentTimeStamp();
            System.out.println("potSize: " + potSize + " ---bigger than 50bb, screenshot saved: " + timeStamp);
            //todo: fill in path
            ImageProcessor.createPartialSreenShot(430, 255, 167, 28, "path" + timeStamp);
        }
        return potSize;
    }

    public double getOpponentStackFromImage() {
        double opponentStack;
        String opponentStackAsString = readTopPlayerStack();
        if(opponentStackAsString.matches(".*\\d.*")){
            opponentStack = Double.parseDouble(opponentStackAsString);
        } else {
            if(opponentStackAsString.contains("in")) {
                //opponent is all-in
                opponentStack = 0;
            } else {
                opponentStackAsString = readTopPlayerStack();
                if(opponentStackAsString.matches(".*\\d.*")){
                    opponentStack = Double.parseDouble(opponentStackAsString);
                } else {
                    opponentStack = -1;
                    System.out.println("opponentStack not read well: -1");
                }
            }
        }

        if(opponentStack / bigBlind > 300) {
            String timeStamp = getCurrentTimeStamp();
            System.out.println("opponentStack: " + opponentStack + " ---bigger than 300bb, screenshot saved: " + timeStamp);
            //todo: fill in path
            ImageProcessor.createPartialSreenShot(500, 147, 109, 28, "path" + timeStamp);
        }
        return opponentStack;
    }

    public double getBotStackFromImage() {
        double botStack;
        String botStackAsString = readBottomPlayerStack();
        if(botStackAsString.matches(".*\\d.*")){
            botStack = Double.parseDouble(botStackAsString);
        } else{
            botStackAsString = readBottomPlayerStack();
            if(botStackAsString.matches(".*\\d.*")){
                botStack = Double.parseDouble(botStackAsString);
            } else {
                botStack = -1;
                System.out.println("botStack not read well: -1");
            }
        }

        if(botStack / bigBlind > 300) {
            String timeStamp = getCurrentTimeStamp();
            System.out.println("botStack: " + botStack + " ---bigger than 300bb, screenshot saved: " + timeStamp);
            //todo: fill in path
            ImageProcessor.createPartialSreenShot(500, 574, 109, 28, "path" + timeStamp);
        }
        return botStack;
    }

    public double getBotTotalBetSizeFromImage() {
        double botTotalBetSize;
        String botTotalBetSizeAsString = readBottomPlayerTotalBetSize();
        if(botTotalBetSizeAsString.matches(".*\\d.*")){
            botTotalBetSize = Double.parseDouble(botTotalBetSizeAsString);
        } else {
            botTotalBetSize = 0;
        }

        if(botTotalBetSize / bigBlind > 40) {
            String timeStamp = getCurrentTimeStamp();
            System.out.println("botTotalBetSize: " + botTotalBetSize + " ---bigger than 40bb, screenshot saved: " + timeStamp);
            //todo: fill in path
            ImageProcessor.createPartialSreenShot(460, 448, 80, 23, "path" + timeStamp);
        }
        return botTotalBetSize;
    }

    public double getOpponentTotalBetSizeFromImage() {
        double opponentTotalBetSize;
        String opponentTotalBetSizeAsString = readTopPlayerTotalBetSize();
        if(opponentTotalBetSizeAsString.matches(".*\\d.*")){
            opponentTotalBetSize = Double.parseDouble(opponentTotalBetSizeAsString);
        } else {
            opponentTotalBetSize = 0;
        }

        if(opponentTotalBetSize / bigBlind > 40) {
            String timeStamp = getCurrentTimeStamp();
            System.out.println("opponentTotalBetSize: " + opponentTotalBetSize + " ---bigger than 40bb, screenshot saved: " + timeStamp);
            //todo: fill in path
            ImageProcessor.createPartialSreenShot(451, 191, 66, 18, "path" + timeStamp);
        }
        return opponentTotalBetSize;
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
        if(suitRgb / 100_000 == -167) {
            //expected rgb: -16743748
            return true;
        }
        System.out.print(".");
        return false;
    }

    public String getOpponentPlayerNameFromImage() {
        //todo: fill in coordinates
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(0, 0, 0, 0);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);
    }

    public static void performActionOnSite(Action botAction) {
        if(botAction != null && botAction.getSizing() != 0) {
            try {
                MouseKeyboard.click(674, 647);
                TimeUnit.MILLISECONDS.sleep(150);
                MouseKeyboard.enterText(String.valueOf(Precision.round(botAction.getSizing(), 2)));
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String action;
        if(botAction != null) {
            action = botAction.getAction();
        } else {
            action = null;
        }

        if(action == null) {
            MouseKeyboard.click(841, 682);
        } else if(action.equals("fold")) {
            MouseKeyboard.click(721, 685);
        } else if(action.equals("check")) {
            MouseKeyboard.click(841, 682);
        } else if(action.equals("call")) {
            MouseKeyboard.click(837, 686);
        } else if(action.equals("bet")) {
            MouseKeyboard.click(957, 683);
        } else if(action.equals("raise")) {
            MouseKeyboard.click(959, 687);
        }
        MouseKeyboard.moveMouseToLocation(20, 20);
    }

    //helper methods
    private String readTopChatLine() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(13, 604, 309, 24);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        return ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
    }

    private String readMiddleChatLine() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(13, 630, 309, 24);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        return ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
    }

    public String readBottomChatLine() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(13, 654, 309, 24);
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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(500, 147, 109, 28);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);
        return ImageProcessor.removeAllNonNumericCharacters(topPlayerStack);
    }

    private String readBottomPlayerStack() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(500, 574, 109, 28);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String bottomPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        bottomPlayerStack = ImageProcessor.removeEmptySpacesFromString(bottomPlayerStack);
        return ImageProcessor.removeAllNonNumericCharacters(bottomPlayerStack);
    }

    private String readPotSize() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(430, 255, 167, 28);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String potSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        potSize = ImageProcessor.removeEmptySpacesFromString(potSize);
        return ImageProcessor.removeAllNonNumericCharacters(potSize);
    }

    private String readBottomPlayerTotalBetSize() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(460, 448, 80, 23);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String bottomPlayerTotalBetSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        bottomPlayerTotalBetSize = ImageProcessor.removeEmptySpacesFromString(bottomPlayerTotalBetSize);
        return ImageProcessor.removeAllNonNumericCharacters(bottomPlayerTotalBetSize);
    }

    private String readTopPlayerTotalBetSize() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(451, 191, 66, 18);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        String topPlayerTotalBetSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerTotalBetSize = ImageProcessor.removeEmptySpacesFromString(topPlayerTotalBetSize);
        return ImageProcessor.removeAllNonNumericCharacters(topPlayerTotalBetSize);
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
        int cardRank = 0;

        switch(stringCardRank) {
            case "2":
                cardRank = 2;
                break;
            case "3":
                cardRank = 3;
                break;
            case "4":
                cardRank = 4;
                break;
            case "5":
                cardRank = 5;
                break;
            case "6":
                cardRank = 6;
                break;
            case "7":
                cardRank = 7;
                break;
            case "8":
                cardRank = 8;
                break;
            case "9":
                cardRank = 9;
                break;
            case "10":
                cardRank = 10;
                break;
            case "J":
                cardRank = 11;
                break;
            case "Q":
                cardRank = 12;
                break;
            case "K":
                cardRank = 13;
                break;
            case "A":
                cardRank = 14;
                break;
            default:
                cardRank = -1;
        }
        return cardRank;
    }

    private String getActionFromChatLine(String chatLine) {
        String action = null;

        if(chatLine.contains("check")) {
            action = "check";
        } else if(chatLine.contains("call")) {
            action = "call";
        } else if(chatLine.contains("bet")) {
            action = "bet";
        } else if(chatLine.contains("raise")) {
            action = "raise";
        } else if(chatLine.contains("post")) {
            action = "post";
        } else if(chatLine.contains("Deal") && chatLine.contains("car")) {
            action = "deal";
        }
        return action;
    }

    private String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    }
}
