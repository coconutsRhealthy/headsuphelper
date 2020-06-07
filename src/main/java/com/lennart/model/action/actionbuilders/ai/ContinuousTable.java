package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.*;
import com.lennart.model.action.actionbuilders.ai.equityrange.BotActionBuilder;
import com.lennart.model.action.actionbuilders.ai.equityrange.InputProvider;
import com.lennart.model.action.actionbuilders.ai.equityrange.OpponentRangeSetter;
import com.lennart.model.action.actionbuilders.ai.equityrange.RangeConstructor;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;
import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.sites.stars.StarsTableReader;

import java.io.PrintWriter;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * Created by Lennart on 3/12/2018.
 */
public class ContinuousTable implements ContinuousTableable {

    private boolean opponentHasInitiative = false;
    private boolean pre3betOrPostRaisedPot = false;
    private boolean opponentDidPreflop4betPot = false;
    private List<String> allHandsPlayedAndPlayerNames = new ArrayList<>();
    private String starsLastHandNumber = "0";

    private List<Set<Card>> top10percentFlopCombos;
    private List<Set<Card>> top10percentTurnCombos;
    private List<Set<Card>> top10percentRiverCombos;

    private List<Double> allBotEquities = new ArrayList<>();

    private boolean botBluffActionDone;

    private List<DbSave> dbSaveList = new ArrayList<>();

    private double bigBlind;

    private String game;

    private double flopHandstrength;
    private double turnHandstrength;

    private int botSittingOutCounter = 0;

    private List<List<Card>> oppRange = null;
    private Map<List<Card>, List<List<Card>>> allCombosPostflopEquitySorted = null;

    private boolean botDidPre4bet;

    public static void main(String[] args) throws Exception {
        ContinuousTable continuousTable = new ContinuousTable();
        continuousTable.setBigBlind(100);
        continuousTable.setGame("sng");
        continuousTable.runTableContinously();
    }

    public void runTableContinously() throws Exception {
        doSngStartSessionLogic();

        GameVariables gameVariables = new GameVariables();
        int numberOfActionRequests = 0;
        int milliSecondsTotal = 0;
        int printDotTotal = 0;

        long startTime = new Date().getTime();

        while(true) {
            TimeUnit.MILLISECONDS.sleep(100);
            milliSecondsTotal = milliSecondsTotal + 100;

            if(game.equals("sng")) {
                doSngContinuousLogic(startTime);
            }

            if(StarsTableReader.botIsToAct()) {
                numberOfActionRequests++;

                boolean isNewHand = isNewHand();

                if(isNewHand) {
                    oppRange = null;
                    allCombosPostflopEquitySorted = null;

                    flopHandstrength = -1;
                    turnHandstrength = -1;

                    if(game.equals("sng")) {
                        double previousBigBlind = bigBlind;
                        bigBlind = new StarsTableReader().readBigBlindFromSngScreen();

                        if(bigBlind < 0) {
                            System.out.println("Error in reading bb. Set it to previous value: " + previousBigBlind);
                            bigBlind = previousBigBlind;
                        }
                    }

                    int numberOfEquitiesAbove65 = getNumberOfEquitiesAbove65();
                    int allEquitiesSize = allBotEquities.size();

                    System.out.println("^^^^a " + numberOfEquitiesAbove65 + " ^^^^");
                    System.out.println("^^^^b " + allEquitiesSize + " ^^^^");
                    System.out.println("^ratio: " + (double) numberOfEquitiesAbove65 / (double) allEquitiesSize + " ^^^^");

                    if(!game.equals("sng")) {
                        long currentTime = new Date().getTime();

                        if(currentTime - startTime > 13_920_000) {
                            new DbSavePersister().doDbSaveUpdate(this, bigBlind);
                            new DbSavePersisterPreflop().doDbSaveUpdate(this, bigBlind);
                            new DbSavePersisterRawData().doBigDbSaveUpdate(this, bigBlind);
                            new DbSavePersisterPreflopStats().doDbSaveUpdate(this);
                            System.out.println("3.4 hours have passed, force quit");
                            throw new RuntimeException();
                        }
                    }

                    System.out.println("is new hand");
                    opponentDidPreflop4betPot = false;
                    pre3betOrPostRaisedPot = false;
                    top10percentFlopCombos = new ArrayList<>();
                    top10percentTurnCombos = new ArrayList<>();
                    top10percentRiverCombos = new ArrayList<>();

                    new DbSavePersister().doDbSaveUpdate(this, bigBlind);
                    new DbSavePersisterPreflop().doDbSaveUpdate(this, bigBlind);
                    new DbSavePersisterRawData().doBigDbSaveUpdate(this, bigBlind);
                    new DbSavePersisterPreflopStats().doDbSaveUpdate(this);

                    opponentDidPreflop4betPot = false;

                    boolean botWasButtonInLastHand = botWasButtonInLastHand();
                    dbSaveList = new ArrayList<>();

                    if(botBluffActionDone) {
                        boolean bluffActionWasSuccessful = wasBluffSuccessful(bigBlind);
                        String opponentPlayerNameOfLastHand = allHandsPlayedAndPlayerNames.get(allHandsPlayedAndPlayerNames.size() - 1);
                        new PlayerBluffer().updateBluffDb(opponentPlayerNameOfLastHand, bluffActionWasSuccessful);
                        botBluffActionDone = false;
                    }

                    if(!allHandsPlayedAndPlayerNames.isEmpty()) {
                        String opponentPlayerNameOfLastHand = allHandsPlayedAndPlayerNames.get(allHandsPlayedAndPlayerNames.size() - 1);
                        new OpponentIdentifier().updateCountsFromHandhistoryDbLogic(opponentPlayerNameOfLastHand, bigBlind);
                        new OpponentIdentifier2_0().updateOpponentIdentifier2_0_db(opponentPlayerNameOfLastHand, bigBlind, botWasButtonInLastHand);
                    }

                    gameVariables = new GameVariables(bigBlind, game.equals("sng"));
                    bigBlind = gameVariables.getBigBlind();

                    allHandsPlayedAndPlayerNames.add(gameVariables.getOpponentName());
                } else {
                    gameVariables.fillFieldsSubsequent(true);
                }

                ActionVariables actionVariables = new ActionVariables(gameVariables, this, true);
                String action = actionVariables.getAction();

                RangeConstructor rangeConstructor = new RangeConstructor();
                InputProvider inputProvider = new InputProvider();
                new OpponentRangeSetter(rangeConstructor, inputProvider).setOpponentRange(this, gameVariables);

                if(oppRange == null) {
                    System.out.println("OPPRANGE = NULL");
                } else {
                    System.out.println("A pre3bet: " + inputProvider.getOppPre3betGroup(gameVariables.getOpponentName()));
                    System.out.println("A aggro: " + inputProvider.getOppPostAggroness(gameVariables.getOpponentName()));
                    System.out.println("A loose: " + inputProvider.getOppPostLooseness(gameVariables.getOpponentName()));
                    System.out.println("OPPRANGE SIZE: " + oppRange.size());
                }

                if(gameVariables.getBoard() != null && !gameVariables.getBoard().isEmpty()) {
                    if(action.equals("fold")) {
                        BotActionBuilder botActionBuilder = new BotActionBuilder();
                        String actionNewStyle = botActionBuilder.getAction(this, gameVariables, rangeConstructor);

                        if(actionNewStyle.equals("call") || actionNewStyle.equals("raise")) {
                            action = actionNewStyle;
                            System.out.println("Change fold to call new style!");
                        }
                    }
                }

                if(action.equals("bet75pct") || action.equals("raise")) {
                    opponentHasInitiative = false;
                }

                double sizing = actionVariables.getSizing();

                doLogging(gameVariables, action, sizing, numberOfActionRequests);

                System.out.println();
                System.out.println("********************");
                System.out.println("Counter: " + numberOfActionRequests);
                System.out.println("Opponent Name: " + gameVariables.getOpponentName());
                System.out.println("Suggested action: "+ action);
                System.out.println("Sizing: " + sizing);
                System.out.println("********************");
                System.out.println();

                if(gameVariables.getBoard() != null && gameVariables.getBoard().size() >= 3) {
                    allBotEquities.add(actionVariables.getBotHandStrength());
                }

                StarsTableReader.performActionOnSite(action, sizing);

                TimeUnit.MILLISECONDS.sleep(100);
            }

            if(milliSecondsTotal == 5000) {
                milliSecondsTotal = 0;
                System.out.print(".");
                printDotTotal++;

                if(printDotTotal == 30) {
                    StarsTableReader.saveScreenshotOfEntireScreen(new Date().getTime());

                    MouseKeyboard.moveMouseToLocation(1565, 909);
                    TimeUnit.MILLISECONDS.sleep(300);
                    MouseKeyboard.click(1565, 909);
                    TimeUnit.MILLISECONDS.sleep(500);
                    MouseKeyboard.moveMouseToLocation(20, 20);

                    printDotTotal = 0;
                    System.out.println();
                }

                if(StarsTableReader.botIsSittingOut()) {
                    botSittingOutCounter++;
                    StarsTableReader.closeAbnAmroUpdateScreenIfNecessary();

                    if(botSittingOutCounter >= 15) {
                        System.out.println("too many bot sitting outs. Something wrong. Quit program.");
                        throw new RuntimeException();
                    }

                    System.out.println("bot is SittingOut!");
                    TimeUnit.MILLISECONDS.sleep(300);
                    StarsTableReader.endBotIsSittingOut();
                    TimeUnit.MILLISECONDS.sleep(500);
                }
            }
        }
    }

    private void doLogging(GameVariables gameVariables, String suggestedAction, double sizing, int numberOfActionRequests) throws Exception {
        StarsTableReader.saveScreenshotOfEntireScreen(numberOfActionRequests);

        String opponentStack = String.valueOf(gameVariables.getOpponentStack());
        String opponentBetSize = String.valueOf(gameVariables.getOpponentBetSize());
        String board = getCardListAsString(gameVariables.getBoard());
        String potSize = String.valueOf(gameVariables.getPot());
        String botBetSize = String.valueOf(gameVariables.getBotBetSize());
        String botStack = String.valueOf(gameVariables.getBotStack());
        String botHoleCards = getCardListAsString(gameVariables.getBotHoleCards());
        String opponentAction = gameVariables.getOpponentAction();
        String sizingString = String.valueOf(sizing);

        PrintWriter writer = new PrintWriter("/Users/LennartMac/Documents/logging/" + numberOfActionRequests + ".txt", "UTF-8");

        writer.println("OpponentStack: " + opponentStack);
        writer.println("OpponentBetSize: " + opponentBetSize);
        writer.println("Board: " + board);
        writer.println("Potsize: " + potSize);
        writer.println("BotBetSize: " + botBetSize);
        writer.println("BotStack: " + botStack);
        writer.println("BotHoleCards: " + botHoleCards);
        writer.println("OpponentAction: " + opponentAction);
        writer.println();

        writer.println("------------------------");
        writer.println();

        writer.println("Action: " + suggestedAction);
        writer.println("Sizing: " + sizingString);

        writer.close();
    }

    private int getNumberOfEquitiesAbove65() {
        int counter = 0;

        for(double d : allBotEquities) {
            if(d >= 0.65) {
                counter++;
            }
        }

        return counter;
    }

    private String getCardListAsString(List<Card> cardList) {
        String cardListAsString = "initial";

        if(cardList != null && !cardList.isEmpty()) {
            cardListAsString = "";

            for(Card card : cardList) {
                cardListAsString = cardListAsString + card.getRank() + card.getSuit() + " ";
            }
        }
        return cardListAsString;
    }

    private boolean isNewHand() throws Exception {
        boolean isNewHand;

        HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();
        List<String> total = handHistoryReaderStars.readTextFile();
        List<String> lastHandNonRecursive = handHistoryReaderStars.getLinesOfLastGameNonRecursive(total);
        double bigBlindInMethod = handHistoryReaderStars.getBigBlindFromLastHandHh(lastHandNonRecursive);

        List<String> lastHand = handHistoryReaderStars.getLinesOfLastGame(total, 1, bigBlindInMethod);

        String lastHandNumber;

        if(!lastHand.isEmpty()) {
            lastHandNumber = handHistoryReaderStars.getHandNumber(lastHand.get(0));
        } else {
            System.out.println("lasthand was empty in isNewHand()");
            lastHandNumber = "-2";
        }

        isNewHand = !starsLastHandNumber.equals(lastHandNumber);
        starsLastHandNumber = lastHandNumber;

        return isNewHand;
    }

    private boolean wasBluffSuccessful(double bigBlind) throws Exception {
        boolean bluffSuccessful = false;

        HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();
        List<String> total = handHistoryReaderStars.readTextFile();
        List<String> lastHand = handHistoryReaderStars.getLinesOfLastGame(total, 1, bigBlind);
        Collections.reverse(lastHand);

        for(String line : lastHand) {
            if(line.contains("folds") && !line.contains("vegeta11223")) {
                bluffSuccessful = true;
                break;
            }
        }

        return bluffSuccessful;
    }

    private boolean botWasButtonInLastHand() {
        boolean botWasButtonInLastHand = false;

        for(DbSave dbSave : dbSaveList) {
            if(dbSave instanceof DbSaveRaw) {
                DbSaveRaw dbSaveRaw = (DbSaveRaw) dbSave;

                if(dbSaveRaw.getPosition().equals("Ip")) {
                    botWasButtonInLastHand = true;
                }

                break;
            }
        }

        return botWasButtonInLastHand;
    }

    private void doSngContinuousLogic(long startTime) throws Exception {
        if(StarsTableReader.sngIsFinished()) {
            long currentTime = new Date().getTime();

            if(currentTime - startTime > 13_920_000) {
                System.out.println("3.4 hours have passed, force quit");
                throw new RuntimeException();
            }

            System.out.println("SNG is finished, staring new game");

            TimeUnit.SECONDS.sleep(14);

            StarsTableReader starsTableReader = new StarsTableReader();

            TimeUnit.MILLISECONDS.sleep(100);
            starsTableReader.closeRematchScreen();
            TimeUnit.MILLISECONDS.sleep(500);
            starsTableReader.closeSpinOfTheDayScreenIfNecessary();
            TimeUnit.MILLISECONDS.sleep(1200);
            starsTableReader.clickTopSngInList();
            TimeUnit.MILLISECONDS.sleep(100);
            starsTableReader.registerNewSng();

            int counter = 0;
            int counter2 = 0;

            boolean newTableNotYetOpened = true;
            while(newTableNotYetOpened) {
                if(counter == 30) {
                    MouseKeyboard.moveMouseToLocation(7, 100);
                    MouseKeyboard.click(7, 100);
                    System.out.println();
                    counter = 0;
                    StarsTableReader.saveScreenshotOfEntireScreen(new Date().getTime());
                }

                System.out.println(",");

                if(counter2 >= 1350) {
                    System.out.println("Something is wrong in registering sng part, close session.");
                    StarsTableReader.saveScreenshotOfEntireScreen(new Date().getTime());
                    throw new RuntimeException();
                }

                TimeUnit.MILLISECONDS.sleep(1100);

                if(starsTableReader.newSngTableIsOpened()) {
                    newTableNotYetOpened = false;
                    System.out.println("New sng table is opened b");
                } else {
                    counter++;
                    counter2++;
                    newTableNotYetOpened = true;
                }
            }

            TimeUnit.MILLISECONDS.sleep(100);
            starsTableReader.maximizeNewSngTable();

            TimeUnit.SECONDS.sleep(6);
        }
    }

    private void doSngStartSessionLogic() throws Exception {
        StarsTableReader starsTableReader = new StarsTableReader();
        starsTableReader.registerNewSng();

        int counter = 0;

        boolean newTableNotYetOpened = true;
        while(newTableNotYetOpened) {
            if(counter == 30) {
                MouseKeyboard.moveMouseToLocation(7, 100);
                MouseKeyboard.click(7, 100);
                System.out.println();
                counter = 0;
            }

            System.out.println(",");

            TimeUnit.MILLISECONDS.sleep(1100);

            if(starsTableReader.newSngTableIsOpened()) {
                newTableNotYetOpened = false;
                System.out.println("New sng table is opened b");
            } else {
                counter++;
                newTableNotYetOpened = true;
            }
        }

        TimeUnit.MILLISECONDS.sleep(100);
        starsTableReader.maximizeNewSngTable();

        TimeUnit.SECONDS.sleep(6);
    }

    @Override
    public boolean isOpponentHasInitiative() {
        return opponentHasInitiative;
    }

    @Override
    public void setOpponentHasInitiative(boolean opponentHasInitiative) {
        this.opponentHasInitiative = opponentHasInitiative;
    }

    @Override
    public boolean isOpponentDidPreflop4betPot() {
        return opponentDidPreflop4betPot;
    }

    @Override
    public void setOpponentDidPreflop4betPot(boolean opponentDidPreflop4betPot) {
        this.opponentDidPreflop4betPot = opponentDidPreflop4betPot;
    }

    @Override
    public boolean isPre3betOrPostRaisedPot() {
        return pre3betOrPostRaisedPot;
    }

    @Override
    public void setPre3betOrPostRaisedPot(boolean pre3betOrPostRaisedPot) {
        this.pre3betOrPostRaisedPot = pre3betOrPostRaisedPot;
    }

    public void setStarsLastHandNumber(String starsLastHandNumber) {
        this.starsLastHandNumber = starsLastHandNumber;
    }

    public List<Set<Card>> getTop10percentFlopCombos() {
        return top10percentFlopCombos;
    }

    public void setTop10percentFlopCombos(List<Set<Card>> top10percentFlopCombos) {
        this.top10percentFlopCombos = top10percentFlopCombos;
    }

    public List<Set<Card>> getTop10percentTurnCombos() {
        return top10percentTurnCombos;
    }

    public void setTop10percentTurnCombos(List<Set<Card>> top10percentTurnCombos) {
        this.top10percentTurnCombos = top10percentTurnCombos;
    }

    public List<Set<Card>> getTop10percentRiverCombos() {
        return top10percentRiverCombos;
    }

    public void setTop10percentRiverCombos(List<Set<Card>> top10percentRiverCombos) {
        this.top10percentRiverCombos = top10percentRiverCombos;
    }

    public boolean isBotBluffActionDone() {
        return botBluffActionDone;
    }

    public void setBotBluffActionDone(boolean botBluffActionDone) {
        this.botBluffActionDone = botBluffActionDone;
    }

    public List<DbSave> getDbSaveList() {
        return dbSaveList;
    }

    public void setDbSaveList(List<DbSave> dbSaveList) {
        this.dbSaveList = dbSaveList;
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(double bigBlind) {
        this.bigBlind = bigBlind;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public double getFlopHandstrength() {
        return flopHandstrength;
    }

    public void setFlopHandstrength(double flopHandstrength) {
        this.flopHandstrength = flopHandstrength;
    }

    public double getTurnHandstrength() {
        return turnHandstrength;
    }

    public void setTurnHandstrength(double turnHandstrength) {
        this.turnHandstrength = turnHandstrength;
    }

    public List<List<Card>> getOppRange() {
        return oppRange;
    }

    public void setOppRange(List<List<Card>> oppRange) {
        this.oppRange = oppRange;
    }

    public Map<List<Card>, List<List<Card>>> getAllCombosPostflopEquitySorted() {
        return allCombosPostflopEquitySorted;
    }

    public void setAllCombosPostflopEquitySorted(Map<List<Card>, List<List<Card>>> allCombosPostflopEquitySorted) {
        this.allCombosPostflopEquitySorted = allCombosPostflopEquitySorted;
    }

    public boolean isBotDidPre4bet() {
        return botDidPre4bet;
    }

    public void setBotDidPre4bet(boolean botDidPre4bet) {
        this.botDidPre4bet = botDidPre4bet;
    }
}
