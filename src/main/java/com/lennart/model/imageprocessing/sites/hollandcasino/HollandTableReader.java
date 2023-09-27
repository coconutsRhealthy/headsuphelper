package com.lennart.model.imageprocessing.sites.hollandcasino;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
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
public class HollandTableReader {

    private int regNewSngWaitCouner = 0;

    public double getBotStackFromImage() throws Exception {
        String botStackAsString = readBottomPlayerStack();

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
        String opponentStackAsString = readTopPlayerStack();

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
        //eventueel ook al rgbs loggen..
        System.out.println("HC1SUIT: " + cardSuit);
        return new Card(cardRank, cardSuit);
    }

    public Card getBotHoleCard2FromImage() {
        int cardRank = getIntCardRank(readSecondHoleCardRank());
        char cardSuit = readSecondHoleCardSuit();
        //eventueel ook al rgbs loggen..
        System.out.println("HC2SUIT: " + cardSuit);
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
            //TimeUnit.SECONDS.sleep(3);

            MouseKeyboard.moveMouseToLocation(1565, 909);
            TimeUnit.MILLISECONDS.sleep(300);
            MouseKeyboard.click(1565, 909);
            TimeUnit.MILLISECONDS.sleep(500);
            MouseKeyboard.moveMouseToLocation(20, 20);
            TimeUnit.MILLISECONDS.sleep(2200);

            System.out.println("End waiting a bit for first action in new sng");
            saveScreenshotOfEntireScreen("botTxoActFirstActionNewSng", new Date().getTime());
        }

        BufferedImage bufferedImage1 = ImageProcessor.getBufferedImageScreenShot(712, 786, 1, 1);
        int checkRgb1 = bufferedImage1.getRGB(0, 0);

        BufferedImage bufferedImage2 = ImageProcessor.getBufferedImageScreenShot(891, 792, 1, 1);
        int checkRgb2 = bufferedImage2.getRGB(0, 0);

        BufferedImage bufferedImage3 = ImageProcessor.getBufferedImageScreenShot(941, 791, 1, 1);
        int checkRgb3 = bufferedImage3.getRGB(0, 0);

        if(checkRgb1 / 1000 == -10083 || checkRgb2 / 1000 == -14147 || checkRgb3 / 1000 == -8903) {
            //expected1: -10_083_257
            //expected2: -14_147_019
            //expected3: -8_903_347
            System.out.println();
            System.out.println("Bot is to act");
            return true;
        }

        return false;
    }
    //todo
    public static boolean sngIsFinished(long timeOfLastAction) throws Exception {
        long currentTime = new Date().getTime();

        if(timeOfLastAction != -1 && currentTime - timeOfLastAction > 35_000) {
            if(currentTime - timeOfLastAction > 360_000) {
                System.out.println("assume sng finished, 6 minutes passed since action");
                saveScreenshotOfEntireScreen("sngFinishedAssumedForced", new Date().getTime());
                return true;
            }

            System.out.println("sng could be finished, 35 sec passed since action");

            HollandTableReader partyTableReader = new HollandTableReader();

            int hc1Rank = partyTableReader.getIntCardRank(partyTableReader.readFirstHoleCardRank());
            char hc1Suit = partyTableReader.readFirstHoleCardSuit();

            if(hc1Rank == -1 || hc1Suit == 'x') {
                BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(452, 606, 1, 1);
                int extraCheckSuitRgb1 = bufferedImage.getRGB(0, 0);
                extraCheckSuitRgb1 = extraCheckSuitRgb1 / 1_000_000;

                if(extraCheckSuitRgb1 == -9)  {
                    System.out.println("sng indeed finished, you have no first holecard. hc1suit: " + extraCheckSuitRgb1);
                    saveScreenshotOfEntireScreen("sngFinished", new Date().getTime());
                    return true;
                } else {
                    System.out.println("not sure if sng finished, weird shit. hc1suit: " + extraCheckSuitRgb1);
                    saveScreenshotOfEntireScreen("sngFinishedExtraCheckNeeded", new Date().getTime());
                    TimeUnit.SECONDS.sleep(5);
                    return sngIsFinished(timeOfLastAction);
                }
            } else {
                HollandTableReader.saveScreenshotOfEntireScreen(currentTime);
                System.out.println("same hand, sng not finished.. probably opp sitting out.. Time: " + currentTime);
            }
        }

        return false;
    }
    //todo
    public static boolean botIsSittingOut() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(636, 695, 730, 724);
        String textInButton = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        if(textInButton.contains("am") && textInButton.contains("back")) {
            System.out.println("Bot is sitting out! Button text: " + textInButton);
            return true;
        }

        return false;
    }
    //todo
    public static void endBotIsSittingOut() {
        System.out.println("Clicking end bot is sitting out...");
        MouseKeyboard.click(682, 706);
    }
    //todo
    public void closeSorryNoRematchPopUp() {
        MouseKeyboard.click(473, 473);
    }
    //todo
    public void closeTableOfEndedSng() {
        MouseKeyboard.click(13, 52);
    }
    //todo
    public void clickTopSngInList(String positionOnTop) {
        if(positionOnTop.equals("first")) {
            MouseKeyboard.click(208, 385);
        } else if(positionOnTop.equals("second")) {
            MouseKeyboard.click(275, 403);
        }
    }
    //todo
    public void selectAndUnselect6PlayerPerTableFilter() throws Exception {
        MouseKeyboard.click(587, 271);
        TimeUnit.MILLISECONDS.sleep(2000);
        MouseKeyboard.click(587, 273);

        TimeUnit.MILLISECONDS.sleep(2000);

        MouseKeyboard.click(306, 272);
        TimeUnit.MILLISECONDS.sleep(2000);
        MouseKeyboard.click(308, 275);
    }
    //todo
    public void registerNewSng(String positionOfSngInListOfClient, ContinuousTable continuousTable) throws Exception {
        if(continuousTable.getLastBuyIn() != continuousTable.getNewBuyInToSelect()) {
            System.out.println("gonna go for new buyin: " + continuousTable.getNewBuyInToSelect() + " prev was: " + continuousTable.getLastBuyIn());
            selectBuyIn(continuousTable);
        } else {
            System.out.println("keep same buyin, both previous and new one are: " + continuousTable.getNewBuyInToSelect());
        }

        clickTopSngInList(positionOfSngInListOfClient);
        clickTopSngInList(positionOfSngInListOfClient);
        TimeUnit.MILLISECONDS.sleep(2000);

        int extraClickUnclickCounter = 0;

        while(!noPlayerIsReggedYet()) {
            regNewSngWaitCouner++;
            extraClickUnclickCounter++;

            if(regNewSngWaitCouner == 12) {
                saveScreenshotOfEntireScreen("noEmptyTable" + regNewSngWaitCouner, new Date().getTime());
                MouseKeyboard.moveMouseToLocation(7, 100);
                MouseKeyboard.click(7, 100);
                System.out.println("click action in waiting for sng registration");
                regNewSngWaitCouner = 0;

                if(extraClickUnclickCounter > 36) {
                    TimeUnit.MILLISECONDS.sleep(1200);
                    selectAndUnselect6PlayerPerTableFilter();
                    System.out.println("select unselect action in waiting for sng registration");
                    extraClickUnclickCounter = 0;
                }
            }

            System.out.println("Already one regged player, wait...");
            TimeUnit.SECONDS.sleep(5);
        }

        clickTopSngInList(positionOfSngInListOfClient);
        clickTopSngInList(positionOfSngInListOfClient);

        //click register button
        TimeUnit.MILLISECONDS.sleep(250);
        System.out.println("registering new sng");
        saveScreenshotOfEntireScreen("registerEmptySng__bankroll: " + continuousTable.getBankroll() + "__", new Date().getTime());
        TimeUnit.MILLISECONDS.sleep(1744);
        MouseKeyboard.click(1095, 673);
        TimeUnit.MILLISECONDS.sleep(2000);

        if(dollarBuyInPopUpIsOpen()) {
            System.out.println("Normal registration attempt pop up OK");

            //click dollar buy-in option
            TimeUnit.MILLISECONDS.sleep(2157);
            MouseKeyboard.click(706, 374);
            TimeUnit.MILLISECONDS.sleep(600);
            MouseKeyboard.click(706, 374);

            //click OK button on registration confirm pop-up
            TimeUnit.MILLISECONDS.sleep(2002);
            MouseKeyboard.click(566, 498);
            TimeUnit.MILLISECONDS.sleep(600);
            MouseKeyboard.click(566, 496);

            //hier nog een check dat je daadwerkelijk geregged bent.. anders opnieuw deze methode callen..
            TimeUnit.SECONDS.sleep(2);
            if(notRegisteredForAnyTournament()) {
                System.out.println("You thought you successfully registered, but not. Call registersng() again");
                saveScreenshotOfEntireScreen("retryRegisterBecauseNotRegistered", new Date().getTime());
                //heel ff niet...
                //registerNewSng(positionOfSngInListOfClient);
            }
        } else {
            System.out.println("Trying second top sng registration attempt, because pop up did not open...");
            checkIfRegistrationConfirmPopUpIsGoneAndIfNotClickOkToRemoveIt();
            registerNewSng("second", continuousTable);
        }
    }
    //todo
    private boolean dollarBuyInPopUpIsOpen() throws Exception {
        boolean buyInPopUpOpen = false;

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(645, 377, 1, 1);
        int pixelRgb = bufferedImage.getRGB(0, 0);

        System.out.println("popup pixel: " + pixelRgb);
        long currentTime = new Date().getTime();

        if(pixelRgb / 1_000_000 == -4) {
            //expected rgb when popup opened: -4.178.939
            //expected when not opened: -14.737.884

            System.out.println("dollarBuyInPopup opened. Time: " + currentTime);
            saveScreenshotOfEntireScreen("dollarBuyInPopupOpen_", currentTime);
            buyInPopUpOpen = true;
        } else {
            for(int i = 0; i < 3; i++) {
                TimeUnit.SECONDS.sleep(5);
                System.out.println("another dollarBuyInPopup attempt: " + i);
                bufferedImage = ImageProcessor.getBufferedImageScreenShot(645, 377, 1, 1);
                pixelRgb = bufferedImage.getRGB(0, 0);
                if(pixelRgb / 1_000_000 == -4) {
                    buyInPopUpOpen = true;
                    break;
                }
            }
        }

        if(!buyInPopUpOpen) {
            System.out.println("dollarBuyInPopup dit not open!. Time: " + currentTime);
            saveScreenshotOfEntireScreen("dollarBuyInPopupDidNotOpen_",currentTime);
        }

        return buyInPopUpOpen;
    }
    //todo
    public void checkIfRegistrationConfirmPopUpIsGoneAndIfNotClickOkToRemoveIt() throws Exception {
        TimeUnit.SECONDS.sleep(1);

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(566, 495, 1, 1);
        int pixelRgb = bufferedImage.getRGB(0, 0);

        long currTime = new Date().getTime();

        if(pixelRgb / 100_000 == -38 || pixelRgb / 100_000 == -151) {
            //when closed: -657_931
            //when still open: -3_815_994
            //or when still open:... -15_169_537
            saveScreenshotOfEntireScreen(currTime);
            System.out.println("register confirm popup still open, gonna close it... RGB: " + pixelRgb + " currtime: " + currTime);

            TimeUnit.MILLISECONDS.sleep(2002);
            MouseKeyboard.click(566, 498);
            TimeUnit.MILLISECONDS.sleep(600);
            MouseKeyboard.click(566, 496);
            TimeUnit.SECONDS.sleep(1);
        } else {
            saveScreenshotOfEntireScreen(currTime);
            System.out.println("register confirm popup is not there. RGB: " + pixelRgb + " currtime: " + currTime);
        }

    }
    //todo
    public static boolean notRegisteredForAnyTournament() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(1052, 192, 1, 1);
        int pixelRgb = bufferedImage.getRGB(0, 0);

        if(pixelRgb == -1) {
            //expected: -1
            System.out.println("Not registered for any tournament... Pixel: " + pixelRgb);
            return true;
        } else {
            //expected: -1.498.367
            System.out.println("Registered for at least one tournament... Pixel: " + pixelRgb);
            return false;
        }
    }
    //todo
    private boolean noPlayerIsReggedYet() throws Exception {
        boolean noPlayerIsReggedYet = false;

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(972, 436, 1010, 454);

        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String playersRegged = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        playersRegged = ImageProcessor.removeEmptySpacesFromString(playersRegged);

        if(playersRegged.startsWith("O") || playersRegged.startsWith("0")) {
            noPlayerIsReggedYet = true;
        } else {
            System.out.println("Already one player regged. String: " + playersRegged);
        }

        return noPlayerIsReggedYet;
    }
    //todo
    public boolean newSngTableIsOpened() throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(157, 284, 1, 1);
        int pixelRgb = bufferedImage.getRGB(0, 0);

        if(pixelRgb / 1_000_000 == -16) {
            //expected rgb when table opened: -16.448.251
            //expected when not opened: -1
            System.out.println("new sng table is opened a");
            return true;
        }

        return false;
    }

    public String getOpponentPlayerNameFromImage() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(446, 211, 576, 232);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String opponentPlayerName = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        String fullPlayerName = ImageProcessor.removeEmptySpacesFromString(opponentPlayerName);

        fullPlayerName = fullPlayerName.replaceAll("'", "");
        fullPlayerName = fullPlayerName.replaceAll("1‘", "");
        fullPlayerName = fullPlayerName.replaceAll("ﬂ", "");
        fullPlayerName = fullPlayerName.replaceAll("ﬁ", "");
        fullPlayerName = fullPlayerName.replaceAll("ﬀ", "");
        fullPlayerName = fullPlayerName.replaceAll("ﬃ", "");
        fullPlayerName = fullPlayerName.replaceAll("ﬄ", "");

        if(fullPlayerName.endsWith("_")) {
            fullPlayerName = fullPlayerName.substring(0, fullPlayerName.length() - 1);
        }

        while(fullPlayerName.endsWith("\\")) {
            fullPlayerName = fullPlayerName.substring(0, fullPlayerName.length() - 1);
        }

        System.out.println("Oppname: " + fullPlayerName);

        fullPlayerName = "hc_" + fullPlayerName;

        return fullPlayerName;
    }

    public static void performActionOnSite(String botAction, double sizing, double pot, List<Card> board, double bigBlind,
                                           double botStack) throws Exception {
        if(botAction != null && sizing != 0) {
            if(botAction.equals("bet75pct")) {
                double currBotstack = botStack + sizing;
                System.out.println("BS: " + currBotstack);

                if(sizing / pot < 0.4) {
                    if(currBotstack < (0.35 * pot)) {
                        System.out.println("Botstack too small for 35%bet. Press shove button");
                        clickShoveSizingButton();
                    }

                    click35pctPotSizingButton();
                } else if(sizing / pot < 0.6) {
                    if(currBotstack < (0.5 * pot)) {
                        System.out.println("Botstack too small for 50%bet. Press shove button");
                        clickShoveSizingButton();
                    }

                    clickHalfPotSizingButton();
                } else {
                    if(currBotstack < (0.75 * pot)) {
                        System.out.println("Botstack too small for 75%bet. Press shove button");
                        clickShoveSizingButton();
                    }

                    click75pctPotSizingButton();
                }
            } else if(botAction.equals("raise")) {
                if(board == null || board.isEmpty()) {
                    if(sizing == 2 * bigBlind) {
                        clickHalfPotSizingButton();
                    } else if(sizing == 3 * bigBlind) {
                        click75pctPotSizingButton();
                    } else if(sizing > 500) {
                        clickShoveSizingButton();
                    } else {
                        System.out.println("Should enter manual preflop raise amount...");
                        manualEnterBetOrRaiseAmount(sizing);
                    }
                } else {
                    if(sizing < 500) {
                        if(board.size() < 5) {
                            //clickHalfPotSizingButton();

                            manualEnterBetOrRaiseAmount(sizing);
                        } else {
                            //click75pctPotSizingButton();

                            manualEnterBetOrRaiseAmount(sizing);

                            System.out.println("river raise sizing action tablereader. Sizing, entered manually: " + sizing);
                        }
                    } else {
                        clickShoveSizingButton();
                    }
                }
            }

            TimeUnit.MILLISECONDS.sleep(100);
        }

        if(botAction == null) {
            clickCheckActionButton();
        } else if(botAction.contains("fold")) {
            clickFoldActionButton();
        } else if(botAction.contains("check")) {
            clickCheckActionButton();
        } else if(botAction.contains("call")) {
            if(StringUtils.containsIgnoreCase(readRightActionButton(), "ALL")) {
                System.out.println("All in call, will click right action button");
                clickRaiseActionButton();
            }

            clickCallActionButton();
        } else if(botAction.contains("bet")) {
            clickBetActionButton();
        } else if(botAction.contains("raise")) {
            String rightActionButton = readRightActionButton();

            if(StringUtils.containsIgnoreCase(rightActionButton, "raise") || StringUtils.containsIgnoreCase(rightActionButton, "allin") ||
                    StringUtils.containsIgnoreCase(rightActionButton, "Alllrl")) {
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

    private static void manualEnterBetOrRaiseAmount(double sizing) {
        try {
            MouseKeyboard.click(737, 721);
            TimeUnit.MILLISECONDS.sleep(20);
            MouseKeyboard.click(737, 721);
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
    private static void click35pctPotSizingButton() {
        MouseKeyboard.click(701, 693);
        MouseKeyboard.click(701, 693);
        MouseKeyboard.click(701, 693);
    }

    private static void clickHalfPotSizingButton() {
        MouseKeyboard.click(798, 691);
        MouseKeyboard.click(798, 691);
        MouseKeyboard.click(798, 691);
    }

    private static void click75pctPotSizingButton() {
        MouseKeyboard.click(875, 692);
        MouseKeyboard.click(875, 692);
        MouseKeyboard.click(875, 692);
    }

    private static void clickShoveSizingButton() {
        MouseKeyboard.click(947, 697);
        MouseKeyboard.click(947, 697);
        MouseKeyboard.click(947, 697);
    }

    private static void clickFoldActionButton() {
        MouseKeyboard.click(689, 776);
        MouseKeyboard.click(689, 776);
        MouseKeyboard.click(689, 776);
    }

    private static void clickCheckActionButton() {
        MouseKeyboard.click(799, 759);
        MouseKeyboard.click(799, 759);
        MouseKeyboard.click(799, 759);
    }

    private static void clickCallActionButton() {
        //MouseKeyboard.click(976, 759);
        //MouseKeyboard.click(976, 759);
        //MouseKeyboard.click(976, 759);

        MouseKeyboard.click(799, 759);
        MouseKeyboard.click(799, 759);
        MouseKeyboard.click(799, 759);
    }

    private static void clickBetActionButton() {
        MouseKeyboard.click(978, 763);
        MouseKeyboard.click(978, 763);
        MouseKeyboard.click(978, 763);
    }

    private static void clickRaiseActionButton() {
        MouseKeyboard.click(985, 770);
        MouseKeyboard.click(985, 770);
        MouseKeyboard.click(985, 770);
    }

    private static String readRightActionButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(908, 748, 998, 791);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String rightActionButton = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        return ImageProcessor.removeEmptySpacesFromString(rightActionButton);
    }

    private String readFirstHoleCardRank() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(447, 572, 469, 607);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String secondHoleCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        secondHoleCardRank = ImageProcessor.removeEmptySpacesFromString(secondHoleCardRank);

        System.out.println("HC1rnk: " + secondHoleCardRank);

        return secondHoleCardRank;
    }

    private String readSecondHoleCardRank() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(513, 572, 537, 607);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String secondHoleCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        secondHoleCardRank = ImageProcessor.removeEmptySpacesFromString(secondHoleCardRank);

        System.out.println("HC2rnk: " + secondHoleCardRank);

        return secondHoleCardRank;
    }

    private String readFirstFlopCardRankFromBoard() {
        //zzz
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(338, 349, 365, 385);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board1: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private String readSecondFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(414, 349, 431, 385);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board2: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private String readThirdFlopCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(485, 349, 504, 385);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board3: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private String readTurnCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(556, 349, 574, 385);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board4: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private String readRiverCardRankFromBoard() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(627, 349, 646, 385);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String firstFlopCardRank = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        firstFlopCardRank = ImageProcessor.removeEmptySpacesFromString(firstFlopCardRank);

        System.out.println("%board5: " + firstFlopCardRank);

        return firstFlopCardRank;
    }

    private char readFirstHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(468, 609, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        System.out.println("hc1suit rgb: " + suitRgb);
        return getHoleCardSuitFromIntRgb(suitRgb);
    }

    private char readSecondHoleCardSuit() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(539, 609, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);
        System.out.println("hc2suit rgb: " + suitRgb);
        return getHoleCardSuitFromIntRgb(suitRgb);
    }

    private char readFirstFlopCardSuitFromBoard() {
        BufferedImage bufferedImage1 = ImageProcessor.getBufferedImageScreenShot(364, 390, 1, 1);
        int suitRgb = bufferedImage1.getRGB(0, 0);
        System.out.println("suit1: " + suitRgb);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readSecondFlopCardSuitFromBoard() {
        BufferedImage bufferedImage1 = ImageProcessor.getBufferedImageScreenShot(437, 390, 1, 1);
        int suitRgb = bufferedImage1.getRGB(0, 0);
        System.out.println("suit2: " + suitRgb);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readThirdFlopCardSuitFromBoard() {
        BufferedImage bufferedImage1 = ImageProcessor.getBufferedImageScreenShot(508, 390, 1, 1);
        int suitRgb = bufferedImage1.getRGB(0, 0);
        System.out.println("suit3: " + suitRgb);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readTurnCardSuitFromBoard() {
        BufferedImage bufferedImage1 = ImageProcessor.getBufferedImageScreenShot(578, 390, 1, 1);
        int suitRgb = bufferedImage1.getRGB(0, 0);
        System.out.println("suit4: " + suitRgb);
        return getSuitFromIntRgb(suitRgb);
    }

    private char readRiverCardSuitFromBoard() {
        BufferedImage bufferedImage1 = ImageProcessor.getBufferedImageScreenShot(650, 390, 1, 1);
        int suitRgb = bufferedImage1.getRGB(0, 0);
        System.out.println("suit5: " + suitRgb);
        return getSuitFromIntRgb(suitRgb);
    }

    private String readBottomPlayerStack() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(475, 670, 541, 690);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String bottomPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        bottomPlayerStack = ImageProcessor.removeEmptySpacesFromString(bottomPlayerStack);

        if(bottomPlayerStack.contains("o")) {
            bottomPlayerStack = bottomPlayerStack.replace("o", "0");
            System.out.println("bottom stack contains 'o', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.contains("O")) {
            bottomPlayerStack = bottomPlayerStack.replace("O", "0");
            System.out.println("bottom stack contains 'O', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.contains("B")) {
            bottomPlayerStack = bottomPlayerStack.replace("B", "8");
            System.out.println("bottom stack contains 'B', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.contains("a")) {
            bottomPlayerStack = bottomPlayerStack.replace("a", "8");
            System.out.println("bottom stack contains 'a', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.contains("D")) {
            bottomPlayerStack = bottomPlayerStack.replace("D", "0");
            System.out.println("bottom stack contains 'D', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.contains("s")) {
            bottomPlayerStack = bottomPlayerStack.replace("s", "6");
            System.out.println("error bottom stack contains 's', new value: " + bottomPlayerStack);
        }

        if(bottomPlayerStack.contains("l]")) {
            bottomPlayerStack = bottomPlayerStack.replace("l]", "0");
            System.out.println("bottom stack contains 'l]', new value: " + bottomPlayerStack);
        }

        String bottomPlayerStackNonNumericRemoved = ImageProcessor.removeAllNonNumericCharacters(bottomPlayerStack);

        while(StringUtils.countMatches(bottomPlayerStackNonNumericRemoved, ".") > 1) {
            bottomPlayerStackNonNumericRemoved = bottomPlayerStackNonNumericRemoved.replaceFirst("\\.", "");
        }

        if(!bottomPlayerStack.equals(bottomPlayerStackNonNumericRemoved)) {
            System.out.println("bottomstack non numeric values removed!");
            System.out.println("bottomstack before remove: " + bottomPlayerStack);
            System.out.println("bottomstack after remove: " + bottomPlayerStackNonNumericRemoved);
        }

        return bottomPlayerStackNonNumericRemoved;
    }

    private String readTopPlayerStack() {
        String topPlayerStack = readTopPlayerStackBase();
        String topPlayerStackNonNumericRemoved = ImageProcessor.removeAllNonNumericCharacters(topPlayerStack);

        if(!topPlayerStack.equals(topPlayerStackNonNumericRemoved)) {
            System.out.println("topstack non numeric values removed!");
            System.out.println("topstack before remove: "+ topPlayerStack);
            System.out.println("topstack after remove: " + topPlayerStackNonNumericRemoved);
        }

        return topPlayerStackNonNumericRemoved;
    }

    private static String readTopPlayerStackBase() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(482, 236, 541, 257);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String topPlayerStack = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        topPlayerStack = ImageProcessor.removeEmptySpacesFromString(topPlayerStack);
        topPlayerStack = topPlayerStack.replace("B", "8");
        topPlayerStack = topPlayerStack.replace("D", "0");

        if(topPlayerStack.contains("B")) {
            topPlayerStack = topPlayerStack.replace("B", "8");
            System.out.println("topstack contains 'B', new value: " + topPlayerStack);
        }

        if(topPlayerStack.contains("a")) {
            topPlayerStack = topPlayerStack.replace("a", "8");
            System.out.println("topstack contains 'a', new value: " + topPlayerStack);
        }

        if(topPlayerStack.contains("D")) {
            topPlayerStack = topPlayerStack.replace("D", "0");
            System.out.println("topstack contains 'D', new value: " + topPlayerStack);
        }

        if(topPlayerStack.contains("o")) {
            topPlayerStack = topPlayerStack.replace("o", "0");
            System.out.println("topstack contains 'o', new value: " + topPlayerStack);
        }

        if(topPlayerStack.contains("O")) {
            topPlayerStack = topPlayerStack.replace("O", "0");
            System.out.println("topstack contains 'O', new value: " + topPlayerStack);
        }

        if(topPlayerStack.contains("s")) {
            topPlayerStack = topPlayerStack.replace("s", "8");
            System.out.println("topstack contains 's', new value: " + topPlayerStack);
        }

        if(topPlayerStack.contains("e")) {
            topPlayerStack = topPlayerStack.replace("e", "6");
            System.out.println("topstack contains 'e', new value: " + topPlayerStack);
        }

        if(topPlayerStack.toLowerCase().contains("all")) {
            System.out.println("top: player allin!");
            topPlayerStack = "0";
        }

        if(topPlayerStack.toLowerCase().contains("sitting") || topPlayerStack.toLowerCase().contains("isconnect") || topPlayerStack.toLowerCase().contains("connecte")) {
            System.out.println("top: top player sitting out!");
            topPlayerStack = "1500";
        }

        return topPlayerStack;
    }



//    public static void main(String[] args) throws Exception {
//        HollandTableReader hollandTableReader = new HollandTableReader();
//        String firstFlopCard = hollandTableReader.readFirstFlopCardRankFromBoard();
//        firstFlopCard = firstFlopCard + hollandTableReader.readFirstFlopCardSuitFromBoard();
//
//        String secondFlopCard = hollandTableReader.readSecondFlopCardRankFromBoard();
//        secondFlopCard = secondFlopCard + hollandTableReader.readSecondFlopCardSuitFromBoard();
//
//        String thirdFlopCard = hollandTableReader.readThirdFlopCardRankFromBoard();
//        thirdFlopCard = thirdFlopCard + hollandTableReader.readThirdFlopCardSuitFromBoard();
//
//        String turnCard = hollandTableReader.readTurnCardRankFromBoard();
//        turnCard = turnCard + hollandTableReader.readTurnCardSuitFromBoard();
//
//        String riverCard = hollandTableReader.readRiverCardRankFromBoard();
//        riverCard = riverCard + hollandTableReader.readRiverCardSuitFromBoard();
//
//        System.out.println();
//        System.out.println();
//        System.out.println("BOARD: " + firstFlopCard + " " + secondFlopCard + " " + thirdFlopCard + " " + turnCard + " " + riverCard);
//
//
//        //System.out.println("TOP: " + new HollandTableReader().readTopPlayerStack());
//        //System.out.println("BOTTOM: " + new HollandTableReader().readBottomPlayerStack());
//        //System.out.println(new HollandTableReader().readFirstHoleCardSuit());
//        //System.out.println(new HollandTableReader().readSecondHoleCardSuit());
//        //System.out.println(new HollandTableReader().readRightActionButton());
//        //HollandTableReader.manualEnterBetOrRaiseAmount(0.04);
//        //System.out.println(HollandTableReader.botIsToAct(false));
//
//
//    }

    private String readTotalPotSize() {
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(468, 320, 558, 344);
        // -> (maar misschien die hieronder nog beter) BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(506, 317, 555, 344);
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(451, 315, 575, 346);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        //bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 3);
        //bufferedImage = ImageProcessor.invertBufferedImageColours(bufferedImage);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String totalPotSize = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        totalPotSize = ImageProcessor.removeEmptySpacesFromString(totalPotSize);

        if(totalPotSize.contains("|]")) {
            totalPotSize = totalPotSize.replace("|]", "0");
            System.out.println("potsize contains '|]', new value: " + totalPotSize);
        }

        if(totalPotSize.contains("Z")) {
            totalPotSize = totalPotSize.replace("Z", "2");
            System.out.println("potsize contains 'Z', new value: " + totalPotSize);
        }

        if(totalPotSize.contains("O")) {
            totalPotSize = totalPotSize.replace("O", "0");
            System.out.println("potsize contains 'O' instead of zero, new value: " + totalPotSize);
        }

        if(totalPotSize.contains("B")) {
            totalPotSize = totalPotSize.replace("B", "8");
            System.out.println("error exception Yo potsize contains B, what is it!?");
        }

        if(totalPotSize.contains("S")) {
            totalPotSize = totalPotSize.replace("S", "8");
            System.out.println("potsize contains 'S' instead of 8, new value: " + totalPotSize);
        }

        if(totalPotSize.contains("lJ")) {
            totalPotSize = totalPotSize.replace("lJ", "0");
            System.out.println("potsize contains 'lJ' instead of 0, new value: " + totalPotSize);
        }

        if(totalPotSize.contains("D")) {
            totalPotSize = totalPotSize.replace("D", "0");
            System.out.println("potsize contains 'D' instead of 0, new value: " + totalPotSize);
        }

        if(totalPotSize.contains("?")) {
            totalPotSize = totalPotSize.replace("?", "7");
            System.out.println("error exception Yo potsize contains ?, what is it!? Now replaced for 7");
        }

        String totalPotSizeNonNumericRemoved = ImageProcessor.removeAllNonNumericCharacters(totalPotSize);

        if(!totalPotSize.equals("Pot:" + totalPotSizeNonNumericRemoved)) {
            System.out.println("potsize non numeric values removed!");

            //? is 7...
            System.out.println("potsize before remove: " + totalPotSize);
            System.out.println("potsize after remove: " + totalPotSizeNonNumericRemoved);
        }

        return totalPotSizeNonNumericRemoved;
    }

    public double readBigBlindFromSngScreen() {
        //-> new BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(348, 80, 364, 98);
        //BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(271, 79, 325, 104);
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(311, 79, 362, 100);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 3);
        bufferedImage = ImageProcessor.makeBufferedImageBlackAndWhite(bufferedImage);
        String bigBlindLevel = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);

        System.out.println("bigblind level string: " + bigBlindLevel);

        bigBlindLevel = ImageProcessor.removeAllNonNumericCharacters(bigBlindLevel);
        bigBlindLevel = ImageProcessor.removeEmptySpacesFromString(bigBlindLevel);

        System.out.println("read base bigblind level: " + bigBlindLevel);

        double bigBlind;

        switch (bigBlindLevel) {
            case "1":
                bigBlind = 20;
                break;
            case "2":
                bigBlind = 30;
                break;
            case "3":
                bigBlind = 40;
                break;
            case "4":
                bigBlind = 50;
                break;
            case "5":
                bigBlind = 60;
                break;
            case "6":
                bigBlind = 80;
                break;
            case "7":
            case "i\"":
                bigBlind = 100;
                break;
            case "8":
                bigBlind = 120;
                break;
            case "9":
                bigBlind = 150;
                break;
            case "10":
                bigBlind = 180;
                break;
            default:
                bigBlind = -1;
                break;
        }

        System.out.println("BIGBLIND: " + bigBlind);

        return bigBlind;
    }
    //todo
    public static double readBankroll() throws Exception {
        double bankroll = -1;

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShotCoordinates(1118, 126, 1202, 151);
        bufferedImage = ImageProcessor.zoomInImage(bufferedImage, 2);
        String bankrollString = ImageProcessor.getStringFromBufferedImageWithTesseract(bufferedImage);
        System.out.println("Read base bankrollstring: " + bankrollString);
        bankrollString = bankrollString.replace("?", "7");
        bankrollString = ImageProcessor.removeAllNonNumericCharacters(bankrollString);

        if(bankrollString.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            bankroll = Double.parseDouble(bankrollString);
        } else {
            System.out.println("Weird bankroll string: " + bankrollString);
        }

        System.out.println("Read bankroll digit: " + bankroll);
        saveScreenshotOfEntireScreen("bankroll_" + bankroll + "_", new Date().getTime());

        return bankroll;
    }
    //todo
    private void selectBuyIn(ContinuousTable continuousTable) throws Exception {
        double previousBuyIn = continuousTable.getLastBuyIn();
        double buyInToSelect = continuousTable.getNewBuyInToSelect();

        System.out.println("BUYIN SHIZZLE. Prev was: " + previousBuyIn + " New buy in will be: " + buyInToSelect);

        TimeUnit.SECONDS.sleep(2);
        pressCategoryCollapseButton();

        TimeUnit.SECONDS.sleep(2);
        pressBuyinDropdownButton();

        TimeUnit.SECONDS.sleep(2);
        pressBuyInCheckBox(buyInToSelect);

        TimeUnit.SECONDS.sleep(2);
        pressBuyInCheckBox(previousBuyIn);

        TimeUnit.SECONDS.sleep(2);
        pressCategoryExpandButton();

        TimeUnit.SECONDS.sleep(2);

        selectAndUnselect6PlayerPerTableFilter();

        saveScreenshotOfEntireScreen("buyin_" + buyInToSelect + "__bankroll: " + continuousTable.getBankroll() + "__" , new Date().getTime());

        continuousTable.setLastBuyIn(buyInToSelect);
    }
    //todo
    private void pressBuyInCheckBox(double buyInToSelect) {
        if(buyInToSelect == 1 || buyInToSelect == -1) {
            //MouseKeyboard.click(226, 408);
            MouseKeyboard.click(225, 386);
        } else if(buyInToSelect == 2) {
            //MouseKeyboard.click(227, 438);
            MouseKeyboard.click(224, 417);
        } else if(buyInToSelect == 5) {
            //MouseKeyboard.click(224, 468);
            MouseKeyboard.click(226, 447);
        } else if(buyInToSelect == 10) {
            //MouseKeyboard.click(225, 497);
            MouseKeyboard.click(226, 477);
        } else if(buyInToSelect == 20) {
            //MouseKeyboard.click(225, 526);
            MouseKeyboard.click(225, 507);
        } else if(buyInToSelect == 50) {
            MouseKeyboard.click(240, 529);
        }
    }
    //todo
    private void pressCategoryExpandButton() {
        //MouseKeyboard.click(597, 264);
        MouseKeyboard.click(600, 246);
    }
    //todo
    private void pressCategoryCollapseButton() {
        //MouseKeyboard.click(592, 330);
        MouseKeyboard.click(604, 312);
    }
    //todo
    private void pressBuyinDropdownButton() {
        //MouseKeyboard.click(565, 245);
        MouseKeyboard.click(561, 235);
    }

    public static void saveScreenshotOfEntireScreen(int numberOfActionRequests) throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(0, 0, 3000, 1250);
        ImageProcessor.saveBufferedImage(bufferedImage, "/Users/lennartmac/Documents/logging/" + numberOfActionRequests + ".png");
    }

    public static void saveScreenshotOfEntireScreen(long time) throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(0, 0, 3000, 1250);
        ImageProcessor.saveBufferedImage(bufferedImage, "/Users/lennartmac/Documents/logging/" + time + ".png");
    }

    public static void saveScreenshotOfEntireScreen(String prefix, long time) throws Exception {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(0, 0, 3000, 1250);
        ImageProcessor.saveBufferedImage(bufferedImage, "/Users/lennartmac/Documents/logging/" + prefix + "__" + time + ".png");
    }

    public boolean bottomPlayerIsButton() {
        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(414, 577, 1, 1);
        int suitRgb = bufferedImage.getRGB(0, 0);

        //button: -1
        //not button: -1

        System.out.println("suitjeee: " + suitRgb);

        if(suitRgb / 100 == -99) {
            System.out.println("SUITRGB: " + suitRgb);
            System.out.println("botIsButton true");
            return true;
        }
        System.out.println("botIsButton false");
        return false;
    }

    private char getHoleCardSuitFromIntRgb(int rgb) {
        //C: -13_994_452
        //S: -10_330_012
        //H: -6_609_372
        //D: -14_254_439

        char suit = 'x';
        int rgbMillionDiv = rgb / 1_000_000;

        if(rgbMillionDiv == -14) {
            suit = 'd';
        } else if(rgbMillionDiv == -13) {
            suit = 'c';
        } else if(rgbMillionDiv == -10) {
            suit = 's';
        } else if(rgbMillionDiv == -6) {
            suit = 'h';
        }

        return suit;
    }

    private char getSuitFromIntRgb(int rgb) {
        //C: -14_060_246
        //S: -10_461_597
        //H: -6_740_702
        //D: -14_386_024

        char suit = 'x';
        int rgbMillionDiv = rgb / 1_000_000;

        if(rgbMillionDiv == -14) {
            int rgb100kDiv = rgb / 100_000;

            if(rgb100kDiv == -140) {
                suit = 'c';
            } else if(rgb100kDiv == -143) {
                suit = 'd';
            }
        } else if(rgbMillionDiv == -6) {
            suit = 'h';
        } else if(rgbMillionDiv == -10) {
            suit = 's';
        }

        return suit;
    }

    private int getIntCardRank(String stringCardRank) {
        int cardRank = -1;

        if(stringCardRank.equals("2") || stringCardRank.equals("Z")) {
            cardRank = 2;
        } else if(stringCardRank.equals("3")) {
            cardRank = 3;
        } else if(stringCardRank.equals("4")) {
            cardRank = 4;
        } else if(stringCardRank.equals("5") || stringCardRank.equals("S")) {
            cardRank = 5;
        } else if(stringCardRank.equals("6")) {
            cardRank = 6;
        } else if(stringCardRank.equals("7")) {
            cardRank = 7;
        } else if(stringCardRank.equals("8")) {
            cardRank = 8;
        } else if(stringCardRank.equals("9")) {
            cardRank = 9;
        } else if(stringCardRank.equals("T")) {
            cardRank = 10;
        } else if(stringCardRank.equals("J")) {
            cardRank = 11;
        } else if(stringCardRank.equals("Q")) {
            cardRank = 12;
        } else if(stringCardRank.equals("K")) {
            cardRank = 13;
        } else if(stringCardRank.equals("A")) {
            cardRank = 14;
        }
        return cardRank;
    }
}
