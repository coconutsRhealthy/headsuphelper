package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.*;
import com.lennart.model.action.actionbuilders.ai.equityrange.InputProvider;
import com.lennart.model.action.actionbuilders.ai.equityrange.OpponentRangeSetter;
import com.lennart.model.action.actionbuilders.ai.equityrange.RangeConstructor;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;
import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.sites.party.PartyTableReader;

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

    private List<Set<Card>> top10percentFlopCombos;
    private List<Set<Card>> top10percentTurnCombos;
    private List<Set<Card>> top10percentRiverCombos;

    private List<Double> allHandStrenghts = new ArrayList<>();

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

    private RangeConstructor rangeConstructor;

    private long timeOfLastDoneAction = -1;
    private boolean gonnaDoFirstActionOfNewSng = false;

    private double lastBuyIn = 10;
    private double newBuyInToSelect = 10;
    private double bankroll = 265.66;

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

            if(game.equals("sng") && milliSecondsTotal >= 4900) {
                doSngContinuousLogic(startTime);
            }

            if(PartyTableReader.botIsToAct(gonnaDoFirstActionOfNewSng)) {
                long botActStarttime = new Date().getTime();

                numberOfActionRequests++;

                boolean isNewHand = isNewHand(gameVariables.getBotHoleCards(), gameVariables.getBoard()) || gonnaDoFirstActionOfNewSng;

                if(isNewHand) {
                    oppRange = null;
                    allCombosPostflopEquitySorted = null;

                    flopHandstrength = -1;
                    turnHandstrength = -1;

                    if(game.equals("sng")) {
                        double previousBigBlind = bigBlind;
                        PartyTableReader partyTableReader = new PartyTableReader();
                        boolean botIsButton = partyTableReader.topPlayerIsButton();
                        bigBlind = new PartyTableReader().readBigBlindFromSngScreen(botIsButton);

                        if(bigBlind < 0) {
                            System.out.println("Error in reading bb. Set it to previous value: " + previousBigBlind);
                            bigBlind = previousBigBlind;
                        }
                    }

                    int numberOfHsAbove85 = getNumberOfHsAbove85();
                    int allHs = allHandStrenghts.size();

                    System.out.println("^^^^a " + numberOfHsAbove85 + " ^^^^");
                    System.out.println("^^^^b " + allHs + " ^^^^");
                    System.out.println("^ratio: " + (double) numberOfHsAbove85 / (double) allHs + " ^^^^");

                    if(!game.equals("sng")) {
                        long currentTime = new Date().getTime();

                        if(currentTime - startTime > 19_920_000) {
                            new DbSavePersister().doDbSaveUpdate(this, bigBlind);
                            new DbSavePersisterPreflop().doDbSaveUpdate(this, bigBlind);
                            new DbSavePersisterRawData().doBigDbSaveUpdate(this);
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
                    new DbSavePersisterRawData().doBigDbSaveUpdate(this);
                    new DbSavePersisterPreflopStats().doDbSaveUpdate(this);
                    //new DbSavePersisterPostflop_2_0().doDbSaveUpdate(this, bigBlind);

                    opponentDidPreflop4betPot = false;

                    boolean botWasButtonInLastHand = botWasButtonInLastHand();
                    dbSaveList = new ArrayList<>();

                    //if(botBluffActionDone) {
                    //    boolean bluffActionWasSuccessful = wasBluffSuccessful(bigBlind);
                    //    String opponentPlayerNameOfLastHand = allHandsPlayedAndPlayerNames.get(allHandsPlayedAndPlayerNames.size() - 1);
                    //    new PlayerBluffer().updateBluffDb(opponentPlayerNameOfLastHand, bluffActionWasSuccessful);
                    //    botBluffActionDone = false;
                    //}

                    if(!allHandsPlayedAndPlayerNames.isEmpty()) {
                        String opponentPlayerNameOfLastHand = allHandsPlayedAndPlayerNames.get(allHandsPlayedAndPlayerNames.size() - 1);
                        new OpponentIdentifier().updateCountsFromHandhistoryDbLogic(opponentPlayerNameOfLastHand, gameVariables.getAllActionRequestsOfHand());
                        new OpponentIdentifier2_0().updateOpponentIdentifier2_0_db(opponentPlayerNameOfLastHand, botWasButtonInLastHand, gameVariables.getAllActionRequestsOfHand());
                    }

                    gameVariables = new GameVariables(bigBlind, game.equals("sng"));
                    bigBlind = gameVariables.getBigBlind();

                    allHandsPlayedAndPlayerNames.add(gameVariables.getOpponentName());
                } else {
                    gameVariables.fillFieldsSubsequent(true);
                }

                rangeConstructor = new RangeConstructor();
                new OpponentRangeSetter(rangeConstructor, new InputProvider()).setOpponentRange(this, gameVariables);

                System.out.println("AAGG: size: " + oppRange.size());

                ActionVariables actionVariables = new ActionVariables(gameVariables, this, true);
                String action = actionVariables.getAction();

                if(action.equals("bet75pct") || action.equals("raise")) {
                    opponentHasInitiative = false;
                }

                double sizing = actionVariables.getSizing();

                doLogging(gameVariables, actionVariables, numberOfActionRequests);

                System.out.println();
                System.out.println("********************");
                System.out.println("Counter: " + numberOfActionRequests);
                System.out.println("Opponent Name: " + gameVariables.getOpponentName());
                System.out.println("Suggested action: "+ action);
                System.out.println("Sizing: " + sizing);
                System.out.println("Route: " + actionVariables.getRoute());
                System.out.println("Table: " + actionVariables.getTable());
                //System.out.println("OppType: " + new GameFlow().getOpponentGroup(gameVariables.getOpponentName()));
                System.out.println("********************");
                System.out.println();

                if(gameVariables.getBoard() != null && gameVariables.getBoard().size() >= 3) {
                    allHandStrenghts.add(actionVariables.getBotHandStrength());
                }

                PartyTableReader.performActionOnSite(action, sizing);

                timeOfLastDoneAction = new Date().getTime();
                gonnaDoFirstActionOfNewSng = false;

                long botActEndtime = new Date().getTime();
                long botActDuration = botActEndtime - botActStarttime;
                System.out.println("**BOT ACTION DURATION: " + botActDuration + " **");

                TimeUnit.MILLISECONDS.sleep(1000);
            }

            if(milliSecondsTotal == 5000) {
                milliSecondsTotal = 0;
                System.out.print(".");
                printDotTotal++;

                if(printDotTotal == 30) {
                    PartyTableReader.saveScreenshotOfEntireScreen(new Date().getTime());

                    MouseKeyboard.moveMouseToLocation(1565, 909);
                    TimeUnit.MILLISECONDS.sleep(300);
                    MouseKeyboard.click(1565, 909);
                    TimeUnit.MILLISECONDS.sleep(500);
                    MouseKeyboard.moveMouseToLocation(20, 20);

                    printDotTotal = 0;
                    System.out.println();
                }

                if(PartyTableReader.botIsSittingOut()) {
                    botSittingOutCounter++;

                    if(botSittingOutCounter >= 15) {
                        System.out.println("too many bot sitting outs. Something wrong. Quit program.");
                        throw new RuntimeException();
                    }

                    System.out.println("bot is SittingOut!");
                    TimeUnit.MILLISECONDS.sleep(300);
                    PartyTableReader.endBotIsSittingOut();
                    TimeUnit.MILLISECONDS.sleep(500);
                }
            }
        }
    }

    private void doLogging(GameVariables gameVariables, ActionVariables actionVariables, int numberOfActionRequests) throws Exception {
        PartyTableReader.saveScreenshotOfEntireScreen(numberOfActionRequests);

        String opponentStack = String.valueOf(gameVariables.getOpponentStack());
        String opponentBetSize = String.valueOf(gameVariables.getOpponentBetSize());
        String board = getCardListAsString(gameVariables.getBoard());
        String potSize = String.valueOf(gameVariables.getPot());
        String botBetSize = String.valueOf(gameVariables.getBotBetSize());
        String botStack = String.valueOf(gameVariables.getBotStack());
        String botHoleCards = getCardListAsString(gameVariables.getBotHoleCards());
        String opponentAction = gameVariables.getOpponentAction();
        String route = actionVariables.getRoute();
        String table = actionVariables.getTable();
        String suggestedAction = actionVariables.getAction();
        String sizing = String.valueOf(actionVariables.getSizing());

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

        writer.println("Route: " + route);
        writer.println("Table: " + table);
        writer.println("Action: " + suggestedAction);
        writer.println("Sizing: " + sizing);

        writer.close();

        doRangeLogging(numberOfActionRequests);
    }

    private void doRangeLogging(int numberOfActionRequests) throws Exception {
        PrintWriter writer = new PrintWriter("/Users/LennartMac/Documents/logging/" + numberOfActionRequests + "-range.txt", "UTF-8");

        int counter = 1;

        if(oppRange != null) {
            for(List<Card> combo : oppRange) {
                Card card1 = combo.get(0);
                Card card2 = combo.get(1);

                writer.println("" + counter + ")  " + card1.getRank() + card1.getSuit() + " " + card2.getRank() + card2.getSuit() + "");
                counter++;
            }
        } else {
            System.out.println("oppRange is null!");
        }

        writer.close();
    }

    private int getNumberOfHsAbove85() {
        int counter = 0;

        for(double d : allHandStrenghts) {
            if(d >= 0.85) {
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

    private boolean isNewHand(List<Card> previousBotHoleCards, List<Card> previousBoard) {
        return new PartyTableReader().isNewHand(previousBotHoleCards, previousBoard);
    }

    private void printBigPotValue(String line) {
        try {
            String potPartOfLine = line.substring(line.indexOf("pot") + 4);
            potPartOfLine = potPartOfLine.substring(0, potPartOfLine.indexOf(" "));
            Double pot = Double.valueOf(potPartOfLine);

            if(pot >= 200) {
                System.out.println("big pot: " + pot);
            }

            if(pot >= 800) {
                System.out.println("really big pot: " + pot);
            }
        } catch (Exception e) {
            System.out.println("Pot value printing error");
            e.printStackTrace();
        }
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
        if(PartyTableReader.sngIsFinished(timeOfLastDoneAction)) {
            long currentTime = new Date().getTime();

            if(currentTime - startTime > 19_920_000) {
                System.out.println("3.4 hours have passed, force quit");
                throw new RuntimeException();
            }

            System.out.println("SNG is finished, staring new game");

            PartyTableReader partyTableReader = new PartyTableReader();

            TimeUnit.MILLISECONDS.sleep(100);
            partyTableReader.closeSorryNoRematchPopUp();
            TimeUnit.MILLISECONDS.sleep(1200);
            partyTableReader.closeTableOfEndedSng();
            TimeUnit.MILLISECONDS.sleep(2800);
            partyTableReader.checkIfRegistrationConfirmPopUpIsGoneAndIfNotClickOkToRemoveIt();
            TimeUnit.MILLISECONDS.sleep(1200);
            partyTableReader.selectAndUnselect6PlayerPerTableFilter();

            TimeUnit.MILLISECONDS.sleep(1500);
            decideBuyIn();
            partyTableReader.registerNewSng("first", this);

            gonnaDoFirstActionOfNewSng = true;
            timeOfLastDoneAction = -1;

            int counter = 0;
            int counter2 = 0;
            int counterToQuit = 0;

            boolean newTableNotYetOpened = true;
            boolean weirdBotSittingOut = false;

            while(newTableNotYetOpened) {
                if(counter % 5 == 0) {
                    if(PartyTableReader.botIsSittingOut()) {
                        System.out.println("Weird bot sitting out! sitting back in...");
                        TimeUnit.MILLISECONDS.sleep(300);
                        PartyTableReader.endBotIsSittingOut();
                        TimeUnit.MILLISECONDS.sleep(500);
                        weirdBotSittingOut = true;
                        break;
                    }
                }

                if(counter == 30) {
                    MouseKeyboard.moveMouseToLocation(7, 100);
                    MouseKeyboard.click(7, 100);
                    System.out.println();
                    counter = 0;
                    PartyTableReader.saveScreenshotOfEntireScreen(new Date().getTime());
                }

                System.out.println(",");

                if(counter2 >= 1350 && PartyTableReader.notRegisteredForAnyTournament()) {
                    counterToQuit++;
                    System.out.println("Something is wrong in registering sng part, might close session. Counter: " + counterToQuit);

                    if(counterToQuit >= 5) {
                        System.out.println("Something is wrong in registering sng part, close session.");
                        PartyTableReader.saveScreenshotOfEntireScreen(new Date().getTime());
                        throw new RuntimeException();
                    }
                }

                TimeUnit.MILLISECONDS.sleep(1100);

                if(partyTableReader.newSngTableIsOpened()) {
                    newTableNotYetOpened = false;
                    System.out.println("New sng table is opened b");
                } else {
                    counter++;
                    counter2++;
                    newTableNotYetOpened = true;
                }
            }

            if(!weirdBotSittingOut) {
                TimeUnit.SECONDS.sleep(6);
            }
        }
    }

    private void doSngStartSessionLogic() throws Exception {
        PartyTableReader partyTableReader = new PartyTableReader();
        partyTableReader.registerNewSng("first", this);
        gonnaDoFirstActionOfNewSng = true;

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

            if(partyTableReader.newSngTableIsOpened()) {
                newTableNotYetOpened = false;
                System.out.println("New sng table is opened b");
            } else {
                counter++;
                newTableNotYetOpened = true;
            }
        }

        TimeUnit.SECONDS.sleep(6);
    }

    private void decideBuyIn() throws Exception {
        double bankrollReadFromScreen = PartyTableReader.readBankroll();

        if((bankrollReadFromScreen < 0.88 * this.bankroll) || (bankrollReadFromScreen > 1.12 * this.bankroll)) {
            System.out.println("read bankroll can't be right, too big diff with previous. Newly read: " + bankrollReadFromScreen + " previous: " + this.bankroll);
        } else {
            bankroll = bankrollReadFromScreen;
        }

        if(bankroll > 800) {
            newBuyInToSelect = 20;
        } else if(bankroll > 300) {
            newBuyInToSelect = 20;
        } else if(bankroll > 100) {
            newBuyInToSelect = 10;
        } else if(bankroll > 60) {
            newBuyInToSelect = 5;
        } else if(bankroll > 30) {
            newBuyInToSelect = 2;
        } else {
            newBuyInToSelect = 1;
        }
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

    public RangeConstructor getRangeConstructor() {
        return rangeConstructor;
    }

    public double getLastBuyIn() {
        return lastBuyIn;
    }

    public void setLastBuyIn(double lastBuyIn) {
        this.lastBuyIn = lastBuyIn;
    }

    public double getNewBuyInToSelect() {
        return newBuyInToSelect;
    }

    public void setNewBuyInToSelect(double newBuyInToSelect) {
        this.newBuyInToSelect = newBuyInToSelect;
    }

    public double getBankroll() {
        return bankroll;
    }

    public void setBankroll(double bankroll) {
        this.bankroll = bankroll;
    }
}
