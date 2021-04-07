package com.lennart.model.imageprocessing.sites.party;

import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.ImageProcessor;
import org.apache.commons.lang3.StringUtils;
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
        String botStackAsString = readTopPlayerStack();

        if(botStackAsString.contains(".")) {
            botStackAsString = botStackAsString.replace(".", "");
        }

        if(botStackAsString.contains(",")) {
            botStackAsString = botStackAsString.replace(",", "");
        }

        System.out.println("BOTSTACK: " + botStackAsString);

        double botStack;

        if(botStackAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            botStack = Double.parseDouble(botStackAsString);
        } else {
            System.out.println("BOTSTACK WRONG! It is: " + botStackAsString + "     ...try again...");
            TimeUnit.MILLISECONDS.sleep(500);
            return getBotStackFromImage();
        }

        return botStack;
    }

    public double getOpponentStackFromImage() throws Exception {
        String opponentStackAsString = readBottomPlayerStack();

        System.out.println("OPPSTACK: " + opponentStackAsString);

        if(opponentStackAsString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            return Double.parseDouble(opponentStackAsString);
        } else {
            return -1;
        }
    }

    public double getTopPotsizeFromImage() throws Exception {
        String topPotsize = readTotalPotSize();

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

    public static boolean botIsToAct(boolean gonnaDoFirstActionOfNewSng) throws Exception {
        if(gonnaDoFirstActionOfNewSng) {
            System.out.println("Start waiting a bit for first action in new sng");
            TimeUnit.SECONDS.sleep(3);
            System.out.println("End waiting a bit for first action in new sng");
        }

        BufferedImage bufferedImage1 = ImageProcessor.getBufferedImageScreenShot(595, 114, 1, 1);
        int suitRgb1 = bufferedImage1.getRGB(0, 0);

        BufferedImage bufferedImage2 = ImageProcessor.getBufferedImageScreenShot(565, 125, 1, 1);
        int suitRgb2 = bufferedImage2.getRGB(0, 0);

        if(suitRgb1 / 1000 == -14_211 && suitRgb2 / 1_000_000 == -11) {
            //expected1: -14_211_289
            //expected2: -11_880_184
            System.out.println();
            System.out.println("Bot is to act");
            return true;
        }

        return false;
    }

    public static boolean sngIsFinished(long timeOfLastAction) throws Exception {
        long currentTime = new Date().getTime();

        if(timeOfLastAction != -1 && currentTime - timeOfLastAction > 35_000) {
            System.out.println("sng could be finished, 35 sec passed since action");

            PartyTableReader partyTableReader = new PartyTableReader();

            int hc1Rank = partyTableReader.getIntCardRank(partyTableReader.readFirstHoleCardRank());
            char hc1Suit = partyTableReader.readFirstHoleCardSuit();

            if(hc1Rank == -1 || hc1Suit == 'x') {
                System.out.println("sng indeed finished, you have no first holecard");
                return true;
            } else {
                System.out.println("same hand, sng not finished.. probably opp sitting out..");
            }
        }

        return false;
    }

    public static boolean botIsSittingOut() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(561, 699, 694, 736);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String textInButton = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        if(textInButton.contains("am") && textInButton.contains("back")) {
            System.out.println("Bot is sitting out! Button text: " + textInButton);
            return true;
        }

        return false;
    }

    public static void endBotIsSittingOut() {
        System.out.println("Clicking end bot is sitting out...");
        MouseKeyboard.click(590, 719);
    }

    public void closeSorryNoRematchPopUp() {
        MouseKeyboard.click(647, 147);
    }

    public void closeTableOfEndedSng() {
        MouseKeyboard.click(13, 33);
    }

    public void clickTopSngInList() {
        MouseKeyboard.click(208, 403);
    }

    public void selectAndUnselect6PlayerPerTableFilter() throws Exception {
        MouseKeyboard.click(586, 290);
        TimeUnit.MILLISECONDS.sleep(2000);
        MouseKeyboard.click(587, 292);

        TimeUnit.MILLISECONDS.sleep(2000);

        MouseKeyboard.click(305, 290);
        TimeUnit.MILLISECONDS.sleep(2000);
        MouseKeyboard.click(308, 293);
    }

    public void registerNewSng() throws Exception {
        clickTopSngInList();
        TimeUnit.MILLISECONDS.sleep(500);

        if(noPlayerIsReggedYet()) {
            clickTopSngInList();

            //click register button
            TimeUnit.MILLISECONDS.sleep(250);
            System.out.println("registering new sng");
            TimeUnit.MILLISECONDS.sleep(1744);
            MouseKeyboard.click(1095, 653);
            TimeUnit.MILLISECONDS.sleep(2000);

            if(dollarBuyInPopUpIsOpen()) {
                System.out.println("Normal registration attempt pop up OK");

                //click dollar buy-in option
                TimeUnit.MILLISECONDS.sleep(2157);
                MouseKeyboard.click(706, 354);
                TimeUnit.MILLISECONDS.sleep(600);
                MouseKeyboard.click(706, 354);

                //click OK button on registration confirm pop-up
                TimeUnit.MILLISECONDS.sleep(2002);
                MouseKeyboard.click(744, 153);
                TimeUnit.MILLISECONDS.sleep(600);
                MouseKeyboard.click(744, 153);
            } else {
                System.out.println("Trying alternative registration attempt, because pop up did not open...");
                alternativeRegisterAttempt();
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

    private void alternativeRegisterAttempt() throws Exception {
        //click gamelobby button
        TimeUnit.SECONDS.sleep(3);
        MouseKeyboard.click(965, 651);
        TimeUnit.SECONDS.sleep(5);

        if(ImageProcessor.getBufferedImageScreenShot(730, 282, 1, 1).getRGB(0, 0) == -3_854_332) {
            System.out.println("Alternative reg variant 1. Time: " + new Date().getTime());
            PartyTableReader.saveScreenshotOfEntireScreen(new Date().getTime());

            //click register in gamelobby
            MouseKeyboard.click(730, 282);
            TimeUnit.SECONDS.sleep(4);

            //click dollar buy-in option
            MouseKeyboard.click(593, 319);
            TimeUnit.MILLISECONDS.sleep(600);
            MouseKeyboard.click(593, 319);

            //click OK button on registration confirm pop-up
            TimeUnit.MILLISECONDS.sleep(2002);
            MouseKeyboard.click(682, 189);
            TimeUnit.MILLISECONDS.sleep(600);
            MouseKeyboard.click(682, 189);

            //close the gamelobby screen...
            TimeUnit.MILLISECONDS.sleep(1000);
            MouseKeyboard.click(880, 89);

        } else {
            System.out.println("Alternative reg variant 2. Time: " + new Date().getTime());
            PartyTableReader.saveScreenshotOfEntireScreen(new Date().getTime());

            //click register in gamelobby
            MouseKeyboard.click(767, 303);
            TimeUnit.SECONDS.sleep(4);

            //click dollar buy-in option
            MouseKeyboard.click(611, 341);
            TimeUnit.MILLISECONDS.sleep(600);
            MouseKeyboard.click(611, 341);

            //click OK button on registration confirm pop-up
            TimeUnit.MILLISECONDS.sleep(2002);
            MouseKeyboard.click(703, 212);
            TimeUnit.MILLISECONDS.sleep(600);
            MouseKeyboard.click(703, 212);

            //close the gamelobby screen...
            TimeUnit.MILLISECONDS.sleep(1000);
            MouseKeyboard.click(907, 111);
        }
    }

    private boolean dollarBuyInPopUpIsOpen() throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(641, 358, 1, 1);
        int pixelRgb = bufferedImage.getRGB(0, 0);

        System.out.println("popup pixel: " + pixelRgb);
        long currentTime = new Date().getTime();

        if(pixelRgb / 1_000_000 == -5) {
            //expected rgb when popup opened: -5.232.375
            //expected when not opened: -15.263.973

            System.out.println("dollarBuyInPopup opened. Time: " + currentTime);
            saveScreenshotOfEntireScreen(currentTime);
            return true;
        } else {
            System.out.println("dollarBuyInPopup dit not open!. Time: " + currentTime);
            saveScreenshotOfEntireScreen(currentTime);
        }

        return false;
    }

    private void switchToSupportTabAndBack() throws Exception {
        TimeUnit.MILLISECONDS.sleep(600);
        MouseKeyboard.click(396, 128);
        TimeUnit.SECONDS.sleep(100);

        long time = new Date().getTime();
        System.out.println("Switched to support screen. Saving screenshot at time: " + time);
        saveScreenshotOfEntireScreen(time);

        TimeUnit.SECONDS.sleep(20);
        MouseKeyboard.click(47, 134);
        TimeUnit.SECONDS.sleep(7);
    }

    private boolean noPlayerIsReggedYet() throws Exception {
        boolean noPlayerIsReggedYet = false;

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(968, 453, 1012, 473);

        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String playersRegged = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        playersRegged = ImageProcessor.removeEmptySpacesFromString(playersRegged);

        if(playersRegged.startsWith("0")) {
            noPlayerIsReggedYet = true;
        } else {
            System.out.println("Already one player regged. String: " + playersRegged);
        }

        return noPlayerIsReggedYet;
    }

    public boolean newSngTableIsOpened() throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(157, 284, 1, 1);
        int pixelRgb = bufferedImage.getRGB(0, 0);

        if(pixelRgb / 1_000_000 == -15) {
            //expected rgb when table opened: -15.395.563
            //expected when not opened: -1
            System.out.println("new sng table is opened a");
            return true;
        }

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
            String rightActionButton = readRightActionButton();

            if(StringUtils.containsIgnoreCase(rightActionButton, "raise")) {
                clickRaiseActionButton();
            } else {
                System.out.println("WTFzxz! Raise and right button does not contain raise!");
                System.out.println("Right action button: " + rightActionButton);
                System.out.println("So you want to raise but button is not there, so you should press call... Gonna press call");
                clickCallActionButton();
            }
        }

        MouseKeyboard.moveMouseToLocation(20, 20);
    }

    public boolean isNewHand(List<Card> previousBotHoleCards, List<Card> previousBoard) {
        boolean isNewHand;

        if(previousBotHoleCards == null || previousBotHoleCards.size() < 2) {
            isNewHand = true;
        } else {
            Card holeCard1 = getBotHoleCard1FromImage();
            Card holeCard2 = getBotHoleCard2FromImage();

            if(previousBotHoleCards.get(0).equals(holeCard1) && previousBotHoleCards.get(1).equals(holeCard2)) {
                if(previousBoard == null || previousBoard.size() < 3) {
                    isNewHand = false;
                } else {
                    Card flopCard1 = getFlopCard1FromImage();
                    Card flopCard2 = getFlopCard2FromImage();
                    Card flopCard3 = getFlopCard3FromImage();

                    if(previousBoard.get(0).equals(flopCard1) && previousBoard.get(1).equals(flopCard2) && previousBoard.get(2).equals(flopCard3)) {
                        isNewHand = false;
                    } else {
                        isNewHand = true;
                    }
                }
            } else {
                isNewHand = true;
            }
        }

        return isNewHand;
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

    private static String readLeftActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(547, 703, 679, 749);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String lefActionButton = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(lefActionButton);
    }

    private static String readMiddleActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(711, 703, 842, 749);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String middleActionButton = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(middleActionButton);
    }

    private static String readRightActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(879, 703, 1012, 749);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String rightActionButton = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(rightActionButton);
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

        if(bottomPlayerStack.contains("o")) {
            bottomPlayerStack = bottomPlayerStack.replace("o", "0");
            System.out.println("opp stack contains 'o', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.contains("O")) {
            bottomPlayerStack = bottomPlayerStack.replace("O", "0");
            System.out.println("opp stack contains 'O', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.contains("B")) {
            bottomPlayerStack = bottomPlayerStack.replace("B", "8");
            System.out.println("opp stack contains 'B', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.contains("a")) {
            bottomPlayerStack = bottomPlayerStack.replace("a", "8");
            System.out.println("opp stack contains 'a', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.contains("D")) {
            bottomPlayerStack = bottomPlayerStack.replace("D", "0");
            System.out.println("opp stack contains 'D', new value: " + bottomPlayerStack);
        }

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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(443, 135, 511, 162);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);
        topPlayerStack = topPlayerStack.replace("B", "8");
        topPlayerStack = topPlayerStack.replace("D", "0");

        if(topPlayerStack.contains("B")) {
            topPlayerStack = topPlayerStack.replace("B", "8");
            System.out.println("botstack contains 'B', new value: " + topPlayerStack);
        }

        if(topPlayerStack.contains("a")) {
            topPlayerStack = topPlayerStack.replace("a", "8");
            System.out.println("botstack contains 'a', new value: " + topPlayerStack);
        }

        if(topPlayerStack.contains("D")) {
            topPlayerStack = topPlayerStack.replace("D", "0");
            System.out.println("botstack contains 'D', new value: " + topPlayerStack);
        }

        if(topPlayerStack.contains("o")) {
            topPlayerStack = topPlayerStack.replace("o", "0");
            System.out.println("botstack contains 'o', new value: " + topPlayerStack);
        }

        if(topPlayerStack.contains("O")) {
            topPlayerStack = topPlayerStack.replace("O", "0");
            System.out.println("botstack contains 'O', new value: " + topPlayerStack);
        }

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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(508, 169, 630, 225);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String bigBlindString = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        System.out.println("bb ff: " + bigBlindString);

        bigBlindString = bigBlindString.substring(bigBlindString.indexOf(System.lineSeparator()) + 1, bigBlindString.length());

        bigBlindString = bigBlindString.replaceAll("o", "0");
        bigBlindString = bigBlindString.replaceAll("O", "0");

        if(bigBlindString.contains("s")) {
            System.out.println("Bigblind String contains 's'! Replace to 6");
            bigBlindString = bigBlindString.replaceAll("s", "6");
        }

        System.out.println("bb ff2: " + bigBlindString);

        bigBlindString = ImageProcessor.removeAllNonNumericCharacters(bigBlindString);

        System.out.println("bb string: " + bigBlindString);

        double bigBlind;

        if(bigBlindString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            bigBlind = Double.parseDouble(bigBlindString);
        } else {
            bigBlind = -1;
        }

        if(botIsButton) {
            bigBlind = bigBlind * 2;
        }

        System.out.println("BIGBLIND: " + bigBlind);

        return bigBlind;
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
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(515, 178, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        if(suitRgb / 10_000 == -105) {
            //expected rgb: -1.052.689
            System.out.println("SUITRGB: " + suitRgb);
            System.out.println("botIsButton true");
            return true;
        }
        System.out.println("botIsButton false");
        return false;
    }

    private char getSuitFromIntRgb(int rgb) {
        char suit = 'x';
        rgb = rgb / 1_000_000;

        if(rgb == -15) {
            suit = 'c';
        } else if(rgb == -7) {
            suit = 'h';
        } else if(rgb == -13 || rgb == -12) {
            suit = 's';
        } else if(rgb == -14) {
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
        } else if(stringCardRank.equals("l0") || stringCardRank.equals("I0") || stringCardRank.equals("ll]")) {
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
