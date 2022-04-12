package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.Logger;
import com.lennart.model.action.actionbuilders.ai.dbsave.*;
import com.lennart.model.action.actionbuilders.ai.equityrange.InputProvider;
import com.lennart.model.action.actionbuilders.ai.equityrange.OpponentRangeSetter;
import com.lennart.model.action.actionbuilders.ai.equityrange.RangeConstructor;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0.RecentHandsPersister;
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
    private double botStartOfHandStack = -1;
    private double oppStartOfHandStack = -1;
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

    private double lastBuyIn = 1;
    private double newBuyInToSelect = 1;
    private double bankroll = 82.30;
    private List<String> sngResults = new ArrayList<>();
    private Map<String, List<Long>> botActionDurations = initialzeBotActionDurationsMap();
    private long sessionStartTime;

    private List<Integer> allNumberOfHands = new ArrayList<>();
    private List<String> oppTypes = new ArrayList<>();
    private List<String> numberOfHandWithOppType = new ArrayList<>();

    private Map<Double, Map<String, Integer>> handsOfOpponentsPerStake = new HashMap<>();
    private String lastOppName = "initial";

    private static Map<String, Integer> actionAdjustments = new HashMap<>();

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

        sessionStartTime = new Date().getTime();

        while(true) {
            TimeUnit.MILLISECONDS.sleep(100);
            milliSecondsTotal = milliSecondsTotal + 100;

            if(game.equals("sng") && milliSecondsTotal >= 4900) {
                doSngContinuousLogic(sessionStartTime);
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
                        bigBlind = new PartyTableReader().readBigBlindFromSngScreen();

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

                        if(currentTime - sessionStartTime > 25_400_000) {
                            new DbSavePersister().doDbSaveUpdate(this, bigBlind);
                            new DbSavePersisterPreflop().doDbSaveUpdate(this, bigBlind);
                            new DbSavePersisterRawData().doBigDbSaveUpdate(this);
                            new DbSavePersisterPreflopStats().doDbSaveUpdate(this);
                            System.out.println("3.4 hours have passed, force quit");
                            printSessionResults();
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

                    try {
                        new RecentHandsPersister().updateRecentHandsPreflopStats(this);
                    } catch (Exception e) {
                        System.out.println("Recent hands error... pfstats");
                        e.printStackTrace();
                    }

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

                        try {
                            new RecentHandsPersister().updateRecentHands(opponentPlayerNameOfLastHand, botWasButtonInLastHand, gameVariables.getAllActionRequestsOfHand());
                        } catch (Exception e) {
                            System.out.println("Recent hands error... normal");
                            e.printStackTrace();
                        }
                    }

                    gameVariables = new GameVariables(bigBlind, game.equals("sng"));
                    bigBlind = gameVariables.getBigBlind();

                    botStartOfHandStack = gameVariables.getBotStack() + gameVariables.getBotBetSize();
                    oppStartOfHandStack = gameVariables.getOpponentStack() + gameVariables.getOpponentBetSize();
                    System.out.println("Bot start stack: " + botStartOfHandStack);
                    System.out.println("Opp start stack: " + oppStartOfHandStack);

                    allHandsPlayedAndPlayerNames.add(gameVariables.getOpponentName());
                } else {
                    gameVariables.fillFieldsSubsequent(true);
                }

                lastOppName = gameVariables.getOpponentName();
                System.out.println("lastOppName: " + lastOppName);
                rangeConstructor = new RangeConstructor();
                new OpponentRangeSetter(rangeConstructor, new InputProvider()).setOpponentRange(this, gameVariables);

                System.out.println("AAGG: size: " + oppRange.size());

                ActionVariables actionVariables = new ActionVariables(gameVariables, this, true);
                String action = actionVariables.getAction();

                if(action.equals("bet75pct") || action.equals("raise")) {
                    opponentHasInitiative = false;
                }

                double sizing = actionVariables.getSizing();

                //doLogging(gameVariables, actionVariables, numberOfActionRequests);
                Logger.doLogging(gameVariables, actionVariables, numberOfActionRequests);

                System.out.println();
                System.out.println("********************");
                System.out.println("Counter: " + numberOfActionRequests);
                System.out.println("Opponent Name: " + gameVariables.getOpponentName());
                System.out.println("Suggested action: "+ action);
                System.out.println("Sizing: " + sizing);
                System.out.println("Route: " + actionVariables.getRoute());
                System.out.println("Table: " + actionVariables.getTable());
                //System.out.println("OppType: " + new GameFlow().getOpponentGroup(gameVariables.getOpponentName()));
                System.out.println("OppType: " + actionVariables.getOpponentType());
                System.out.println("Hands: " + actionVariables.getOppNumberOfHands());
                System.out.println("********************");
                System.out.println();

                if(gameVariables.getBoard() != null && gameVariables.getBoard().size() >= 3) {
                    allHandStrenghts.add(actionVariables.getBotHandStrength());
                }

                allNumberOfHands.add(actionVariables.getOppNumberOfHands());
                oppTypes.add(actionVariables.getOpponentType());
                numberOfHandWithOppType.add("" + actionVariables.getOppNumberOfHands() + "_" + actionVariables.getOpponentType());
                updateHandsOfOpponentsPerStake(lastBuyIn, gameVariables.getOpponentName());

                PartyTableReader.performActionOnSite(action, sizing, gameVariables.getPot(),
                        gameVariables.getBoard(), gameVariables.getBigBlind(), gameVariables.getBotStack());

                timeOfLastDoneAction = new Date().getTime();
                gonnaDoFirstActionOfNewSng = false;

                long botActEndtime = new Date().getTime();
                long botActDuration = botActEndtime - botActStarttime;
                System.out.println("**BOT ACTION DURATION: " + botActDuration + " **");
                addActionDurationToMap(botActDuration, gameVariables.getBoard());
                Logger.printActionDurationsToTextFile(botActionDurations, sessionStartTime);
                Logger.printOppTypeData(allNumberOfHands, oppTypes, numberOfHandWithOppType, sessionStartTime);
                Logger.printOpponentNames(handsOfOpponentsPerStake, sessionStartTime);

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

        PrintWriter writer = new PrintWriter("/Users/lennartmac/Documents/logging/" + numberOfActionRequests + ".txt", "UTF-8");

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

        //doRangeLogging(numberOfActionRequests);
    }

    private void doRangeLogging(int numberOfActionRequests) throws Exception {
        PrintWriter writer = new PrintWriter("/Users/lennartmac/Documents/logging/" + numberOfActionRequests + "-range.txt", "UTF-8");

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
            String botWonSngString = getBotWonSngString();
            adjustBankroll(botWonSngString, lastBuyIn);

            long currentTime = new Date().getTime();

            if(currentTime - startTime > 25_400_000) {
                System.out.println("3.4 hours have passed, force quit");
                printSessionResults();
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

//                if(counter2 >= 1350 && PartyTableReader.notRegisteredForAnyTournament()) {
//                    counterToQuit++;
//                    System.out.println("Something is wrong in registering sng part, might close session. Counter: " + counterToQuit);
//
//                    if(counterToQuit >= 5) {
//                        System.out.println("Something is wrong in registering sng part, close session.");
//                        PartyTableReader.saveScreenshotOfEntireScreen(new Date().getTime());
//                        throw new RuntimeException();
//                    }
//                }

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

    private void decideBuyIn() {
        if(bankroll > 800) {
            newBuyInToSelect = 10;
        } else if(bankroll > 300) {
            newBuyInToSelect = 10;
        } else if(bankroll > 130) {
            newBuyInToSelect = 10;
        } else if(bankroll > 80) {
            newBuyInToSelect = 5;
        } else if(bankroll > 50) {
            newBuyInToSelect = 2;
        } else {
            newBuyInToSelect = 1;
        }

        if(lastOppName != null && lastOppName.equals("Trickysleeps")) {
            if(newBuyInToSelect >= 20) {
                newBuyInToSelect = 10;
                System.out.println("Don't play against Trickysleeps... switch to buyin $10");
            }
        }
    }

    private String getBotWonSngString() {
        String botWonSng = "";

        if(botStartOfHandStack > oppStartOfHandStack) {
            System.out.println("Bot won sng! botStartOfHandStack: " + botStartOfHandStack + " oppStartOfHandStack: " + oppStartOfHandStack);
            botWonSng = "win";
            sngResults.add("win");
        } else if(botStartOfHandStack == oppStartOfHandStack) {
            System.out.println("Unclear who won sng. Equal stacks. botStartOfHandStack: " + botStartOfHandStack + " oppStartOfHandStack: " + oppStartOfHandStack);
            botWonSng = "unclear";
            sngResults.add("unclear");
        } else {
            System.out.println("Opp won sng! botStartOfHandStack: " + botStartOfHandStack + " oppStartOfHandStack: " + oppStartOfHandStack);
            botWonSng = "loss";
            sngResults.add("loss");
        }

        return botWonSng;
    }

    private void adjustBankroll(String winLossOrUnclear, double stake) throws Exception {
        double adjustment = 0;

        if(winLossOrUnclear.equals("win")) {
            if(stake == 1) {
                adjustment = 0.88;
            } else if(stake == 2) {
                adjustment = 1.8;
            } else if(stake == 5) {
                adjustment = 4.6;
            } else if(stake == 10) {
                adjustment = 9.2;
            } else if(stake == 20) {
                adjustment = 18.4;
            }
        } else if(winLossOrUnclear.equals("unclear")) {
            if(stake == 1) {
                adjustment = -0.06;
            } else if(stake == 2) {
                adjustment = -0.1;
            } else if(stake == 5) {
                adjustment = -0.2;
            } else if(stake == 10) {
                adjustment = -0.4;
            } else if(stake == 20) {
                adjustment = -0.8;
            }
        } else if(winLossOrUnclear.equals("loss")) {
            if(stake == 1) {
                adjustment = -1;
            } else if(stake == 2) {
                adjustment = -2;
            } else if(stake == 5) {
                adjustment = -5;
            } else if(stake == 10) {
                adjustment = -10;
            } else if(stake == 20) {
                adjustment = -20;
            }
        }

        bankroll = bankroll + adjustment;
        System.out.println("Estimated bankroll: " + bankroll);
    }

    private void printSessionResults() {
        int wins = Collections.frequency(sngResults, "win");
        int unclears = Collections.frequency(sngResults, "unclear");
        int losses = Collections.frequency(sngResults, "loss");
        int totalGames = wins + unclears + losses;

        System.out.println();
        System.out.println("************");
        System.out.println("Games played: " + totalGames);
        System.out.println("Bot wins: " + wins);
        System.out.println("Unclears: " + unclears);
        System.out.println("Bot losses: " + losses);
        System.out.println("************");
        System.out.println();
    }

    private void addActionDurationToMap(long actionDuration, List<Card> board) {
        if(board == null || board.isEmpty()) {
            botActionDurations.get("preflop").add(actionDuration);
        } else {
            if(board.size() == 3) {
                botActionDurations.get("flop").add(actionDuration);
            } else if(board.size() == 4) {
                botActionDurations.get("turn").add(actionDuration);
            } else if(board.size() == 5) {
                botActionDurations.get("river").add(actionDuration);
            }
        }
    }

    private Map<String, List<Long>> initialzeBotActionDurationsMap() {
        return new HashMap<String, List<Long>>() {{
            put("preflop", new ArrayList<>());
            put("flop", new ArrayList<>());
            put("turn", new ArrayList<>());
            put("river", new ArrayList<>());
        }};
    }

    private void updateHandsOfOpponentsPerStake(double stake, String oppName) {
        try {
            if(handsOfOpponentsPerStake.get(stake) == null) {
                handsOfOpponentsPerStake.put(stake, new HashMap<>());
            }

            Map<String, Integer> handsPerOpponentForGivenStake = handsOfOpponentsPerStake.get(stake);

            if(handsPerOpponentForGivenStake.get(oppName) == null) {
                handsPerOpponentForGivenStake.put(oppName, 0);
            }

            int currentAmountOfHands = handsPerOpponentForGivenStake.get(oppName);
            int newAmountOfHands = currentAmountOfHands + 1;

            handsPerOpponentForGivenStake.put(oppName, newAmountOfHands);
            handsOfOpponentsPerStake.put(stake, handsPerOpponentForGivenStake);
        } catch (Exception e) {
            System.out.println("Error in updateHandsOfOpponentsPerStake");
            e.printStackTrace();
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

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public static Map<String, Integer> getActionAdjustments() {
        return actionAdjustments;
    }
}
