package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.*;
import com.lennart.model.action.actionbuilders.ai.dbstatsraw.DbStatsRawBluffPostflopMigrator;
import com.lennart.model.action.actionbuilders.ai.foldstats.AdjustToFoldStats;
import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;
import com.lennart.model.action.actionbuilders.preflop.PreflopActionBuilder;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.handevaluation.PreflopHandStength;
import com.lennart.model.handtracker.ActionRequest;
import com.lennart.model.handtracker.PlayerActionRound;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by LennartMac on 05/03/2018.
 */
public class ActionVariables {

    private String action;
    private double sizing;
    private String opponentType;
    private String route;
    private String table;

    private double botHandStrength;
    private boolean botHasStrongDraw;

    private boolean strongFlushDraw;
    private boolean strongOosd;
    private boolean strongGutshot;
    private boolean strongOvercards;
    private boolean strongBackdoorFd;
    private boolean strongBackdoorSd;

    private HandEvaluator handEvaluator;

    private String actionBeforeFoldStat;

    private BoardEvaluator boardEvaluator;

    public ActionVariables() {
        //default constructor
    }

    public static void main(String[] args) throws Exception {
        ActionVariables actionVariables = new ActionVariables();

        for(int i = 0; i < 20; i++) {
            actionVariables.testMethod();
        }
    }

    private void testMethod() throws Exception {
        ContinuousTable continuousTable = new ContinuousTable();

        continuousTable.setOpponentHasInitiative(true);
        continuousTable.setPre3betOrPostRaisedPot(false);
        continuousTable.setOpponentDidPreflop4betPot(false);

        GameVariables gameVariables = new GameVariables();

        gameVariables.setOpponentStack(7696);
        gameVariables.setOpponentBetSize(798);
        gameVariables.setPot(1690);
        gameVariables.setBotBetSize(0);
        gameVariables.setBotStack(26055);
        gameVariables.setBigBlind(100);
        gameVariables.setBotIsButton(false);

        gameVariables.setOpponentAction("bet75pct");

        List<Card> holeCards = new ArrayList<>();

        holeCards.add(new Card(6, 'c'));
        holeCards.add(new Card(3, 'd'));

        Card flopCard1 = new Card(14, 'd');
        Card flopCard2 = new Card(8, 'd');
        Card flopCard3 = new Card(5, 'h');
        Card turnCard = new Card(12, 'h');
        //Card riverCard = new Card(8, 'c');

        gameVariables.setFlopCard1(flopCard1);
        gameVariables.setFlopCard2(flopCard2);
        gameVariables.setFlopCard3(flopCard3);
        gameVariables.setTurnCard(turnCard);
        //gameVariables.setRiverCard(riverCard);

        List<Card> board = new ArrayList<>();
        board.add(flopCard1);
        board.add(flopCard2);
        board.add(flopCard3);
        board.add(turnCard);
        //board.add(riverCard);

        gameVariables.setBotHoleCards(holeCards);
        gameVariables.setBoard(board);

        ActionVariables actionVariables = new ActionVariables(gameVariables, continuousTable, false);
    }

    public String getDummyAction(ContinuousTable continuousTableInput, GameVariables gameVariablesInput) throws Exception {
        ContinuousTable continuousTableInMethod = new ContinuousTable();

        continuousTableInMethod.setOpponentHasInitiative(continuousTableInput.isOpponentHasInitiative());
        continuousTableInMethod.setPre3betOrPostRaisedPot(continuousTableInput.isPre3betOrPostRaisedPot());
        continuousTableInMethod.setOpponentDidPreflop4betPot(continuousTableInput.isOpponentDidPreflop4betPot());

        GameVariables gameVariablesInMethod = new GameVariables();

        gameVariablesInMethod.setOpponentStack(0);
        gameVariablesInMethod.setOpponentBetSize(gameVariablesInput.getOpponentBetSize());
        gameVariablesInMethod.setPot(gameVariablesInput.getPot());
        gameVariablesInMethod.setBotBetSize(gameVariablesInput.getBotBetSize());
        gameVariablesInMethod.setBotStack(gameVariablesInput.getBotStack());
        gameVariablesInMethod.setBigBlind(gameVariablesInput.getBigBlind());
        gameVariablesInMethod.setBotIsButton(gameVariablesInput.isBotIsButton());

        gameVariablesInMethod.setOpponentAction(gameVariablesInput.getOpponentAction());
        gameVariablesInMethod.setOpponentName(gameVariablesInput.getOpponentName());

        gameVariablesInMethod.setFlopCard1(gameVariablesInput.getFlopCard1());
        gameVariablesInMethod.setFlopCard2(gameVariablesInput.getFlopCard2());
        gameVariablesInMethod.setFlopCard3(gameVariablesInput.getFlopCard3());
        gameVariablesInMethod.setTurnCard(gameVariablesInput.getTurnCard());
        gameVariablesInMethod.setRiverCard(gameVariablesInput.getRiverCard());

        gameVariablesInMethod.setBotHoleCards(gameVariablesInput.getBotHoleCards());
        gameVariablesInMethod.setBoard(gameVariablesInput.getBoard());

        ActionVariables actionVariables = new ActionVariables(gameVariablesInMethod, continuousTableInMethod, false);

        return actionVariables.getAction();
    }

    public ActionVariables(GameVariables gameVariables, ContinuousTable continuousTable, boolean realGame) throws Exception {
        calculateHandStrengthAndDraws(gameVariables, continuousTable);

        List<String> eligibleActions = getEligibleActions(gameVariables);
        String streetInMethod = getStreet(gameVariables);
        boolean botIsButtonInMethod = gameVariables.isBotIsButton();
        double potSizeBb = gameVariables.getPot() / gameVariables.getBigBlind();
        String opponentActionInMethod = gameVariables.getOpponentAction();
        double facingOdds = getFacingOdds(gameVariables);
        double effectiveStack = getEffectiveStackInBb(gameVariables);
        boolean botHasStrongDrawInMethod = botHasStrongDraw;
        double botHandStrengthInMethod = botHandStrength;
        opponentType = doOpponentTypeDbLogic(gameVariables.getOpponentName());
        double opponentBetsizeBb = gameVariables.getOpponentBetSize() / gameVariables.getBigBlind();
        double botBetsizeBb = gameVariables.getBotBetSize() / gameVariables.getBigBlind();
        double opponentStackBb = gameVariables.getOpponentStack() / gameVariables.getBigBlind();
        double botStackBb = gameVariables.getBotStack() / gameVariables.getBigBlind();
        boolean preflop = gameVariables.getBoard().isEmpty();
        List<Card> boardInMethod = gameVariables.getBoard();

        if(realGame) {
            setOpponentHasInitiative(opponentActionInMethod, continuousTable, gameVariables);
        }

        setOpponentDidPostflopFlopOrTurnRaiseOrOverbet(opponentActionInMethod, boardInMethod, continuousTable, opponentBetsizeBb, potSizeBb);
        double amountToCallBb = getAmountToCallBb(botBetsizeBb, opponentBetsizeBb, botStackBb);

        int boardWetness = 200;
        boolean defaultCheck = false;

        if(preflop) {
            action = new PreflopActionBuilder().getAction(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getOpponentStack(), gameVariables.getBigBlind(), gameVariables.getBotHoleCards(), gameVariables.isBotIsButton(), continuousTable, opponentType, amountToCallBb);

            if(action.equals("raise")) {
                sizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard());
            }
        } else {
            if(continuousTable != null && (continuousTable.isOpponentHasInitiative() && opponentActionInMethod.equals("empty"))) {
                System.out.println("default check, opponent has initiative");
                action = "check";
                defaultCheck = true;
            } else {
                //hier de equity logic
                doEquityLogic(boardInMethod, gameVariables.getBotHoleCards());
                botHasStrongDrawInMethod = botHasStrongDraw;

                String actionAgainstLa = new Poker().getAction(this, eligibleActions, streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod, facingOdds, effectiveStack, botHasStrongDrawInMethod, botHandStrengthInMethod, "la", opponentBetsizeBb, botBetsizeBb, opponentStackBb, botStackBb, preflop, boardInMethod, strongFlushDraw, strongOosd, strongGutshot, gameVariables.getBigBlind(), continuousTable.isOpponentDidPreflop4betPot(), continuousTable.isPre3betOrPostRaisedPot(), strongOvercards, strongBackdoorFd, strongBackdoorSd, boardWetness, continuousTable.isOpponentHasInitiative());

                if(opponentType.equals("la")) {
                    action = actionAgainstLa;
                } else {
                    if(streetInMethod.equals("flopOrTurn")) {
                        if(opponentStackBb == 0) {
                            action = "toDetermine";
                        }

                        if(actionAgainstLa.equals("raise") && !botHasStrongDraw && botHandStrength < 0.5) {
                            action = "toDetermine";
                        }

                        if(actionAgainstLa.equals("call") && !botHasStrongDraw && opponentBetsizeBb > 15) {
                            action = "toDetermine";
                        }
                    }

                    if(streetInMethod.equals("river")) {
                        if(actionAgainstLa.equals("bet75pct") || actionAgainstLa.equals("raise")) {
                            sizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard());

                            if(sizing / gameVariables.getBigBlind() > 15) {
                                action = "toDetermine";
                            }
                        }

                        if(actionAgainstLa.equals("call")) {
                            action = "toDetermine";
                        }
                    }

                    if(action != null && action.equals("toDetermine")) {
                        action = new Poker().getAction(this, eligibleActions, streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod, facingOdds, effectiveStack, botHasStrongDrawInMethod, botHandStrengthInMethod, opponentType, opponentBetsizeBb, botBetsizeBb, opponentStackBb, botStackBb, preflop, boardInMethod, strongFlushDraw, strongOosd, strongGutshot, gameVariables.getBigBlind(), continuousTable.isOpponentDidPreflop4betPot(), continuousTable.isPre3betOrPostRaisedPot(), strongOvercards, strongBackdoorFd, strongBackdoorSd, boardWetness, continuousTable.isOpponentHasInitiative());
                    } else {
                        action = actionAgainstLa;
                    }
                }

                if((action.equals("bet75pct") || action.equals("raise")) && sizing == 0) {
                    sizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard());
                }
            }

            if(action.equals("raise")) {
                continuousTable.setPre3betOrPostRaisedPot(true);
            }
        }

        actionBeforeFoldStat = action;

        //now follows the adjustToFoldStat logic
        AdjustToFoldStats adjustToFoldStats = new AdjustToFoldStats();

        if(action.equals("fold")) {
            double botFoldStat = new FoldStatsKeeper().getFoldStatFromDb("bot-V-" + gameVariables.getOpponentName());

            System.out.println("botFoldStat against " + gameVariables.getOpponentName() + ": " + botFoldStat);

            if(botFoldStat > 1.2) {
                double handStrengthRequiredToCall = adjustToFoldStats.getHandStrengthRequiredToCall(this, eligibleActions,
                        streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod, facingOdds, effectiveStack,
                        botHasStrongDrawInMethod, botHandStrengthInMethod, opponentType, opponentBetsizeBb, botBetsizeBb,
                        opponentStackBb, botStackBb, preflop, boardInMethod, strongFlushDraw, strongOosd, strongGutshot,
                        gameVariables.getBigBlind(), continuousTable.isOpponentDidPreflop4betPot(),
                        continuousTable.isPre3betOrPostRaisedPot(), false, false, false, boardWetness, continuousTable.isOpponentHasInitiative());

                action = adjustToFoldStats.adjustPlayToBotFoldStat(action, botHandStrengthInMethod, handStrengthRequiredToCall, gameVariables.getBotHoleCards(), boardInMethod, botIsButtonInMethod, gameVariables.getOpponentName(), botBetsizeBb, opponentBetsizeBb, false);

                if(action.equals("call") && streetInMethod.equals("preflop") && opponentBetsizeBb == 1) {
                    action = "fold";
                }

                if(action.equals("call")) {
                    System.out.println();
                    System.out.println("CHANGED FROM FOLD TO CALL!");
                    System.out.println("street: " + streetInMethod);
                    System.out.println();
                }

                if(action.equals("call") && streetInMethod.equals("preflop") && opponentBetsizeBb > 4) {
                    continuousTable.setPre3betOrPostRaisedPot(true);
                }

                if(action.equals("call") && streetInMethod.equals("preflop") && opponentBetsizeBb > 16) {
                    continuousTable.setOpponentDidPreflop4betPot(true);
                }
            }
        }

        if(boardInMethod != null && boardInMethod.size() >= 3) {
            double bigBlind = gameVariables.getBigBlind();
            RangeTracker rangeTracker = new RangeTracker();
            int drawWetness = boardEvaluator.getFlushStraightWetness();
            int boatWetness = boardEvaluator.getBoatWetness();
            boolean strongFd = handEvaluator.hasDrawOfType("strongFlushDraw");
            boolean strongOosd = handEvaluator.hasDrawOfType("strongOosd");
            boolean strongGutshot = handEvaluator.hasDrawOfType("strongGutshot");

            double randomLimit;

            if(strongFd || strongOosd) {
                randomLimit = 0.7;
            } else if(strongGutshot) {
                if(botIsButtonInMethod) {
                    randomLimit = 0.33;
                } else {
                    randomLimit = 0.23;
                }
            } else {
                randomLimit = 0;
            }

            if(Math.random() > randomLimit) {
                action = rangeTracker.balancePlayDoBluff(action, bigBlind, botIsButtonInMethod, botHandStrength, boardInMethod,
                        continuousTable.isOpponentHasInitiative(), opponentBetsizeBb * bigBlind, botBetsizeBb * bigBlind, botStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, continuousTable.isPre3betOrPostRaisedPot());

                action = rangeTracker.balancePlayPreventBluff(action, this, eligibleActions, streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod,
                        facingOdds, effectiveStack * bigBlind, botHasStrongDraw, botHandStrength, opponentType, opponentBetsizeBb, botBetsizeBb,
                        opponentStackBb, botStackBb, preflop, boardInMethod, strongFlushDraw, strongOosd, strongGutshot, bigBlind, continuousTable.isOpponentDidPreflop4betPot(),
                        continuousTable.isPre3betOrPostRaisedPot(), strongOvercards, strongBackdoorFd, strongBackdoorSd, boardWetness, continuousTable.isOpponentHasInitiative());
            }

            if(realGame) {
                if(boardInMethod != null && boardInMethod.size() >=3 && !defaultCheck) {
                    rangeTracker.updateRangeMapInDbSimple(action, sizing, gameVariables.getBigBlind(), botIsButtonInMethod, botHandStrengthInMethod, boardInMethod);
                    rangeTracker.updateRangeMapInDbExtensive(action, sizing, gameVariables.getBigBlind(), botIsButtonInMethod, botHandStrengthInMethod, boardInMethod, drawWetness, boatWetness);
                }
            }

            action = new FoldStatBluffAdjuster().doBluffAccordingToFoldStat(action, bigBlind, botIsButtonInMethod,
                    botHandStrength, boardInMethod, continuousTable.isOpponentHasInitiative(),
                    opponentBetsizeBb * bigBlind, botBetsizeBb * bigBlind, botStackBb * bigBlind,
                    opponentStackBb * bigBlind, potSizeBb * bigBlind, continuousTable.isPre3betOrPostRaisedPot(),
                    gameVariables.getOpponentName());

            action = new FoldStatBluffAdjuster().preventBluffAccordingToFoldStat(action, this, eligibleActions, streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod,
                    facingOdds, effectiveStack * bigBlind, botHasStrongDraw, botHandStrength, opponentType, opponentBetsizeBb, botBetsizeBb,
                    opponentStackBb, botStackBb, preflop, boardInMethod, strongFlushDraw, strongOosd, strongGutshot, bigBlind, continuousTable.isOpponentDidPreflop4betPot(),
                    continuousTable.isPre3betOrPostRaisedPot(), strongOvercards, strongBackdoorFd, strongBackdoorSd, boardWetness, continuousTable.isOpponentHasInitiative(), gameVariables.getOpponentName());

            action = new FoldStatBluffAdjuster().preventBigBluffsAgainstLowFoldstat(action, this, eligibleActions, streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod,
                    facingOdds, effectiveStack * bigBlind, botHasStrongDraw, botHandStrength, opponentType, opponentBetsizeBb, botBetsizeBb,
                    opponentStackBb, botStackBb, preflop, boardInMethod, strongFlushDraw, strongOosd, strongGutshot, bigBlind, continuousTable.isOpponentDidPreflop4betPot(),
                    continuousTable.isPre3betOrPostRaisedPot(), strongOvercards, strongBackdoorFd, strongBackdoorSd, boardWetness, continuousTable.isOpponentHasInitiative(), gameVariables.getOpponentName());

            action = new PlayerBluffer().doOpponentBluffSuccessAction(action, gameVariables.getOpponentName(), bigBlind,
                    botHandStrength, boardInMethod, continuousTable.isOpponentHasInitiative(), opponentBetsizeBb * bigBlind,
                    botBetsizeBb * bigBlind, botStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, continuousTable.isPre3betOrPostRaisedPot());

            //machine learning
            String actionBeforeMachineLearning = action;

            double sizingForMachineLearning = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard());
            action = new MachineLearning().adjustActionToDbSaveData(this, gameVariables, continuousTable, sizingForMachineLearning);

            if(!actionBeforeMachineLearning.equals(action)) {
                System.out.println("---Action changed in Machinelearning from: " + actionBeforeMachineLearning + " to: " + action);
            }
            //machine learning

            if((action.equals("bet75pct") || action.equals("raise"))) {
                sizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard());
            }
        } else {
            double sizingForMachineLearning = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard());
            action = new MachineLearningPreflop().adjustActionToDbSaveData(this, gameVariables, sizingForMachineLearning);
        }

        action = preventCallIfOpponentOrBotAlmostAllInAfterCall(action, opponentStackBb, botStackBb, botBetsizeBb, potSizeBb, amountToCallBb, boardInMethod);

        if((action.equals("bet75pct") || action.equals("raise")) && sizing == 0) {
            sizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard());
        }

        if((botStackBb <= 10) || (opponentStackBb + opponentBetsizeBb <= 10)) {
            double bigBlind = gameVariables.getBigBlind();

            double botStack = botStackBb * bigBlind;
            double oppStack = opponentStackBb * bigBlind;
            double botBetSize = botBetsizeBb * bigBlind;
            double oppBetSize = opponentBetsizeBb * bigBlind;
            double potSize = potSizeBb * bigBlind;
            double total = botStack + oppStack + botBetSize + oppBetSize + potSize;

            System.out.println("Shortstack play");
            System.out.println("botstack: " + botStack);
            System.out.println("oppstack: " + oppStack);
            System.out.println("botbetsize: " + botBetSize);
            System.out.println("oppbetsize: " + oppBetSize);
            System.out.println("potsize: " + potSize);
            System.out.println("total: " + total);

            //sng specific
            if(total > 2500 && total < 3500) {
                ShortStackPlayAdjuster shortStackPlayAdjuster = new ShortStackPlayAdjuster();
                action = shortStackPlayAdjuster.adjustAction(action, gameVariables, this);
                sizing = shortStackPlayAdjuster.adjustSizing(action, sizing, gameVariables.getBigBlind());
            } else {
                System.out.println("No shortstack play, total is wrong");
            }
        }

        action = neverFoldStrongEquity(action, boardInMethod, eligibleActions, continuousTable.isPre3betOrPostRaisedPot(),
                amountToCallBb, gameVariables.getBigBlind());

        action = preventCallIfOpponentOrBotAlmostAllInAfterCall(action, opponentStackBb, botStackBb, botBetsizeBb, potSizeBb, amountToCallBb, boardInMethod);

        if(action.equals("bet75pct") || action.equals("raise")) {
            if(sizing == 0) {
                sizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard());
            }

            sizing = adjustRaiseSizingToSng(sizing, action, gameVariables, effectiveStack);
        }

        if(boardInMethod != null && boardInMethod.size() >= 3 && (action.equals("bet75pct") || action.equals("raise")) && botHandStrength < 0.64) {
            continuousTable.setBotBluffActionDone(true);
        }

        if(action.equals("raise") && boardInMethod != null && boardInMethod.size() >= 3) {
            continuousTable.setPre3betOrPostRaisedPot(true);
        }

        if(realGame) {
            //fill dbsave
            if(boardInMethod != null && boardInMethod.size() >= 3) {
                //postflop
                if(action.equals("call") || action.equals("bet75pct") || action.equals("raise")) {
                    int drawWetness = boardEvaluator.getFlushStraightWetness();
                    int boatWetness = boardEvaluator.getBoatWetness();

                    if((action.equals("bet75pct") || action.equals("raise")) && botHandStrength < 0.7) {
                        DbSaveBluff dbSaveBluff = new DbSaveBluff();

                        String sizingGroup = dbSaveBluff.getSizingGroupViaLogic(sizing / gameVariables.getBigBlind());
                        String street = dbSaveBluff.getStreetViaLogic(boardInMethod);
                        String foldStatGroup = dbSaveBluff.getFoldStatGroupLogic(new FoldStatsKeeper().getFoldStatFromDb(gameVariables.getOpponentName()));
                        String position = dbSaveBluff.getPositionLogic(botIsButtonInMethod);
                        String bluffAction = dbSaveBluff.getBluffActionLogic(action);
                        String effectiveStackString = dbSaveBluff.getEffectiveStackLogic(botStackBb, opponentStackBb);
                        String handStrength = dbSaveBluff.getHandStrengthLogic(botHandStrength);
                        String drawWetnessString = dbSaveBluff.getDrawWetnessLogic(boardInMethod, drawWetness);
                        String boatWetnessString = dbSaveBluff.getBoatWetnessLogic(boardInMethod, boatWetness);
                        String strongDraw = dbSaveBluff.getStrongDrawLogic(handEvaluator.hasDrawOfType("strongFlushDraw"), handEvaluator.hasDrawOfType("strongOosd"));

                        dbSaveBluff.setSizingGroup(sizingGroup);
                        dbSaveBluff.setStreet(street);
                        dbSaveBluff.setFoldStatGroup(foldStatGroup);
                        dbSaveBluff.setPosition(position);
                        dbSaveBluff.setBluffAction(bluffAction);
                        dbSaveBluff.setEffectiveStack(effectiveStackString);
                        dbSaveBluff.setHandStrength(handStrength);
                        dbSaveBluff.setDrawWetness(drawWetnessString);
                        dbSaveBluff.setBoatWetness(boatWetnessString);
                        dbSaveBluff.setStrongDraw(strongDraw);

                        continuousTable.getDbSaveList().add(dbSaveBluff);
                    }

                    if(action.equals("call")) {
                        DbSaveCall dbSaveCall = new DbSaveCall();

                        String amountToCallGroup = dbSaveCall.getAmountToCallViaLogic(amountToCallBb);
                        String street = dbSaveCall.getStreetViaLogic(boardInMethod);
                        String oppAggroGroup = dbSaveCall.getOppAggroGroupViaLogic(gameVariables.getOpponentName());
                        String postion = dbSaveCall.getPositionLogic(botIsButtonInMethod);
                        String facingAction = dbSaveCall.getFacingActionViaLogic(opponentActionInMethod);
                        String handStrength = dbSaveCall.getHandStrengthLogic(botHandStrength);
                        String strongDraw = dbSaveCall.getStrongDrawLogic(handEvaluator.hasDrawOfType("strongFlushDraw"), handEvaluator.hasDrawOfType("strongOosd"));
                        String effectiveStackString = dbSaveCall.getEffectiveStackLogic(botStackBb, opponentStackBb);
                        String drawWetnessString = dbSaveCall.getDrawWetnessLogic(boardInMethod, drawWetness);
                        String boatWetnessString = dbSaveCall.getBoatWetnessLogic(boardInMethod, boatWetness);

                        dbSaveCall.setAmountToCallGroup(amountToCallGroup);
                        dbSaveCall.setStreet(street);
                        dbSaveCall.setOppAggroGroup(oppAggroGroup);
                        dbSaveCall.setPosition(postion);
                        dbSaveCall.setFacingAction(facingAction);
                        dbSaveCall.setHandStrength(handStrength);
                        dbSaveCall.setStrongDraw(strongDraw);
                        dbSaveCall.setEffectiveStack(effectiveStackString);
                        dbSaveCall.setDrawWetness(drawWetnessString);
                        dbSaveCall.setBoatWetness(boatWetnessString);

                        continuousTable.getDbSaveList().add(dbSaveCall);
                    }

                    if((action.equals("bet75pct") || action.equals("raise")) && botHandStrength >= 0.7) {
                        DbSaveValue dbSaveValue = new DbSaveValue();

                        String sizingGroup = dbSaveValue.getSizingGroupViaLogic(sizing / gameVariables.getBigBlind());
                        String street = dbSaveValue.getStreetViaLogic(boardInMethod);
                        String oppLoosenessGroup = dbSaveValue.getOppLoosenessGroupViaLogic(gameVariables.getOpponentName());
                        String postion = dbSaveValue.getPositionLogic(botIsButtonInMethod);
                        String valueAction = dbSaveValue.getValueActionLogic(action);
                        String handStrength = dbSaveValue.getHandStrengthLogic(botHandStrength);
                        String strongDraw = dbSaveValue.getStrongDrawLogic(handEvaluator.hasDrawOfType("strongFlushDraw"), handEvaluator.hasDrawOfType("strongOosd"));
                        String effectiveStackString = dbSaveValue.getEffectiveStackLogic(botStackBb, opponentStackBb);
                        String drawWetnessString = dbSaveValue.getDrawWetnessLogic(boardInMethod, drawWetness);
                        String boatWetnessString = dbSaveValue.getBoatWetnessLogic(boardInMethod, boatWetness);

                        dbSaveValue.setSizingGroup(sizingGroup);
                        dbSaveValue.setStreet(street);
                        dbSaveValue.setOppLoosenessGroup(oppLoosenessGroup);
                        dbSaveValue.setPosition(postion);
                        dbSaveValue.setValueAction(valueAction);
                        dbSaveValue.setHandStrength(handStrength);
                        dbSaveValue.setStrongDraw(strongDraw);
                        dbSaveValue.setEffectiveStack(effectiveStackString);
                        dbSaveValue.setDrawWetness(drawWetnessString);
                        dbSaveValue.setBoatWetness(boatWetnessString);

                        continuousTable.getDbSaveList().add(dbSaveValue);
                    }
                }
            } else {
                //preflop
                if(action.equals("raise")) {
                    DbSavePreflopRaise dbSavePreflopRaise = new DbSavePreflopRaise();

                    String combo = dbSavePreflopRaise.getComboLogic(gameVariables.getBotHoleCards());
                    String postion = dbSavePreflopRaise.getPositionLogic(botIsButtonInMethod);
                    String sizingGroup = dbSavePreflopRaise.getSizingLogic(sizing / gameVariables.getBigBlind());
                    String foldStatGroup = dbSavePreflopRaise.getFoldStatGroupLogic(new FoldStatsKeeper().getFoldStatFromDb(gameVariables.getOpponentName()));
                    String effectiveStackString = dbSavePreflopRaise.getEffectiveStackLogic(botStackBb, opponentStackBb);

                    dbSavePreflopRaise.setCombo(combo);
                    dbSavePreflopRaise.setPosition(postion);
                    dbSavePreflopRaise.setSizing(sizingGroup);
                    dbSavePreflopRaise.setFoldStatGroup(foldStatGroup);
                    dbSavePreflopRaise.setEffectiveStack(effectiveStackString);

                    continuousTable.getDbSaveList().add(dbSavePreflopRaise);
                } else if(action.equals("call")) {
                    DbSavePreflopCall dbSavePreflopCall = new DbSavePreflopCall();

                    String combo = dbSavePreflopCall.getComboLogic(gameVariables.getBotHoleCards());
                    String postion = dbSavePreflopCall.getPositionLogic(botIsButtonInMethod);
                    String amountToCallGroup = dbSavePreflopCall.getAmountToCallViaLogic(amountToCallBb);
                    String oppAggroGroup = dbSavePreflopCall.getOppAggroGroupViaLogic(gameVariables.getOpponentName());
                    String effectiveStackString = dbSavePreflopCall.getEffectiveStackLogic(botStackBb, opponentStackBb);

                    dbSavePreflopCall.setCombo(combo);
                    dbSavePreflopCall.setPosition(postion);
                    dbSavePreflopCall.setAmountToCallBb(amountToCallGroup);
                    dbSavePreflopCall.setOppAggroGroup(oppAggroGroup);
                    dbSavePreflopCall.setEffectiveStack(effectiveStackString);

                    continuousTable.getDbSaveList().add(dbSavePreflopCall);
                }
            }

            //DbSaveRaw
            DbSaveRaw dbSaveRaw = new DbSaveRaw();

            String boardString = dbSaveRaw.getBoardLogic(gameVariables.getBoard());
            String holeCardsString = dbSaveRaw.getHoleCardsLogic(gameVariables.getBotHoleCards());
            String positionString = dbSaveRaw.getPositionLogic(botIsButtonInMethod);
            String opponentData = dbSaveRaw.getOpponentDataLogic(gameVariables.getOpponentName());
            double recentHandsWon = dbSaveRaw.getRecentHandsWonLogic(gameVariables.getOpponentName());
            String strongDrawString;

            if(handEvaluator == null) {
                strongDrawString = "StrongDrawFalse";
            } else {
                strongDrawString = dbSaveRaw.getStrongDrawLogic(handEvaluator.hasDrawOfType("strongFlushDraw"), handEvaluator.hasDrawOfType("strongOosd"));
            }


            dbSaveRaw.setBotAction(action);
            dbSaveRaw.setOppAction(gameVariables.getOpponentAction());
            dbSaveRaw.setBoard(boardString);
            dbSaveRaw.setHoleCards(holeCardsString);
            dbSaveRaw.setHandStrength(botHandStrength);
            dbSaveRaw.setBotStack(gameVariables.getBotStack());
            dbSaveRaw.setOpponentStack(gameVariables.getOpponentStack());
            dbSaveRaw.setBotTotalBetSize(gameVariables.getBotBetSize());
            dbSaveRaw.setOpponentTotalBetSize(gameVariables.getOpponentBetSize());
            dbSaveRaw.setSizing(sizing);
            dbSaveRaw.setPosition(positionString);
            dbSaveRaw.setStake("1.50sng");
            dbSaveRaw.setOpponentName(gameVariables.getOpponentName());
            dbSaveRaw.setOpponentData(opponentData);
            dbSaveRaw.setBigBlind(gameVariables.getBigBlind());
            dbSaveRaw.setStrongDraw(strongDrawString);
            dbSaveRaw.setRecentHandsWon(recentHandsWon);

            continuousTable.getDbSaveList().add(dbSaveRaw);
            //DbSaveRaw
        }

        if(realGame) {
            double totalBotBetSizeForPlayerActionRound;

            if(sizing == 0) {
                totalBotBetSizeForPlayerActionRound = gameVariables.getBotBetSize();
            } else {
                totalBotBetSizeForPlayerActionRound = sizing;
            }

            List<Card> currentBoardCopy = new ArrayList<>();
            currentBoardCopy.addAll(gameVariables.getBoard());
            double opponentBetSizeCopy = gameVariables.getOpponentBetSize();
            String actionCopy = action;

            PlayerActionRound botPlayerActionRound = new PlayerActionRound("bot", currentBoardCopy, totalBotBetSizeForPlayerActionRound, opponentBetSizeCopy, "theCorrectStreet", actionCopy);
            List<ActionRequest> allActionRequestsOfHand = gameVariables.getAllActionRequestsOfHand();
            ActionRequest lastActionRequest = allActionRequestsOfHand.get(allActionRequestsOfHand.size() - 1);
            lastActionRequest.getActionsSinceLastRequest().add(botPlayerActionRound);

            double updatedBotStack = getUpdatedBotStack(actionCopy, gameVariables, totalBotBetSizeForPlayerActionRound);
            gameVariables.setBotStack(updatedBotStack);
            gameVariables.setBotBetSize(totalBotBetSizeForPlayerActionRound);
        }
    }

    private double getUpdatedBotStack(String action, GameVariables gameVariables, double newBotBetSize) {
        double updatedBotStack;
        double botStackBeforeUpdate = gameVariables.getBotStack();

        double totalOpponentBetSize = gameVariables.getOpponentBetSize();
        double previousBotBetSize = gameVariables.getBotBetSize();

        if(action.equals("call")) {
            //botstack = botstack - (totalopponentbetsize - totalbotbetsize)
            updatedBotStack = botStackBeforeUpdate - (totalOpponentBetSize - newBotBetSize);
        } else if(action.equals("bet75pct")) {
            //botstack = botstack - totalbotbetsize
            updatedBotStack = botStackBeforeUpdate - newBotBetSize;
        } else if(action.equals("raise")) {
            //botstack = botstack - (totalbotbetsize - previoustotalbotbetsize)
            updatedBotStack = botStackBeforeUpdate - (newBotBetSize - previousBotBetSize);
        } else {
            updatedBotStack = botStackBeforeUpdate;
        }

        System.out.println();
        System.out.println("BA: " + updatedBotStack);
        System.out.println();

        return updatedBotStack;
    }

    private void setOpponentHasInitiative(String opponentAction, ContinuousTable continuousTable, GameVariables gameVariables) {
        if(continuousTable != null) {
            if(opponentAction != null) {
                if(opponentAction.equals("empty")) {
                    List<ActionRequest> allActionRequestsOfHand = gameVariables.getAllActionRequestsOfHand();
                    ActionRequest secondLastActionRequest = allActionRequestsOfHand.get(allActionRequestsOfHand.size() - 2);
                    PlayerActionRound botLastActionRound = secondLastActionRequest.getMostRecentActionRoundOfPLayer(secondLastActionRequest.getActionsSinceLastRequest(), "bot");
                    String botLastAction = botLastActionRound.getAction();

                    if(botLastAction.equals("call")) {
                        continuousTable.setOpponentHasInitiative(true);
                    } else {
                        continuousTable.setOpponentHasInitiative(false);
                    }
                } else {
                    if(opponentAction.equals("bet75pct") || opponentAction.equals("raise")) {
                        continuousTable.setOpponentHasInitiative(true);
                    } else {
                        continuousTable.setOpponentHasInitiative(false);
                    }
                }
            }
        }
    }

    private void setOpponentDidPostflopFlopOrTurnRaiseOrOverbet(String opponentAction, List<Card> board, ContinuousTable continuousTable, double opponentBetsizeBb, double potSizeBb) {
       if(continuousTable != null) {
           if(opponentAction.equals("raise")) {
               if(board != null && (board.size() == 3 || board.size() == 4)) {
                   continuousTable.setPre3betOrPostRaisedPot(true);
               }
           }

           //overbet check
           if(opponentAction.equals("bet75pct")) {
               if(opponentBetsizeBb > (potSizeBb * 0.84)) {
                   System.out.println("overbet done by opponent: " + opponentBetsizeBb + " in pot: " + potSizeBb);
                   continuousTable.setPre3betOrPostRaisedPot(true);
               }
           }
       }
    }

    private void calculateHandStrengthAndDraws(GameVariables gameVariables, ContinuousTable continuousTable) {
        if(gameVariables.getBoard().isEmpty()) {
            PreflopHandStength preflopHandStength = new PreflopHandStength();
            botHandStrength = preflopHandStength.getPreflopHandStength(gameVariables.getBotHoleCards());
            botHasStrongDraw = false;
        } else {
            boardEvaluator = new BoardEvaluator(gameVariables.getBoard());
            handEvaluator = new HandEvaluator(gameVariables.getBotHoleCards(), boardEvaluator);
            botHandStrength = handEvaluator.getHandStrength(gameVariables.getBotHoleCards());

            strongFlushDraw = handEvaluator.hasDrawOfType("strongFlushDraw");
            strongOosd = handEvaluator.hasDrawOfType("strongOosd");
            strongGutshot = handEvaluator.hasDrawOfType("strongGutshot");
            strongOvercards = handEvaluator.hasDrawOfType("strongOvercards");
            strongBackdoorFd = handEvaluator.hasDrawOfType("strongBackDoorFlush");
            strongBackdoorSd = handEvaluator.hasDrawOfType("strongBackDoorStraight");

            //botHasStrongDraw = strongFlushDraw || strongOosd || strongGutshot || strongBackdoorFd || (strongBackdoorSd && Math.random() < 0.5);
            botHasStrongDraw = strongFlushDraw || strongOosd || strongGutshot || strongBackdoorFd || strongBackdoorSd;

            List<Card> boardInMethod = gameVariables.getBoard();

            if(boardInMethod.size() == 3) {
                continuousTable.setTop10percentFlopCombos(boardEvaluator.getTop10percentCombos());
            } else if(boardInMethod.size() == 4) {
                continuousTable.setTop10percentTurnCombos(boardEvaluator.getTop10percentCombos());
            } else if(boardInMethod.size() == 5) {
                continuousTable.setTop10percentRiverCombos(boardEvaluator.getTop10percentCombos());
            }
        }
    }

    private double getEffectiveStackInBb(GameVariables gameVariables) {
        if(gameVariables.getBotStack() > gameVariables.getOpponentStack()) {
            return gameVariables.getOpponentStack() / gameVariables.getBigBlind();
        }
        return gameVariables.getBotStack() / gameVariables.getBigBlind();
    }

    public double getFacingOdds(GameVariables gameVariables) {
        double opponentBetSize = gameVariables.getOpponentBetSize();
        double botBetSize = gameVariables.getBotBetSize();
        double botStack = gameVariables.getBotStack();

        if((opponentBetSize - botBetSize) > botStack) {
            opponentBetSize = botStack;
        }

        double facingOdds = (opponentBetSize - botBetSize) / (gameVariables.getPot() + botBetSize + opponentBetSize);
        return facingOdds;
    }

    public String getStreet(GameVariables gameVariables) {
        String street = "";

        if(gameVariables.getFlopCard1() == null) {
            street = "preflop";
        }
        if(gameVariables.getFlopCard1() != null && gameVariables.getTurnCard() == null) {
            street = "flopOrTurn";
        }
        if(gameVariables.getTurnCard() != null && gameVariables.getRiverCard() == null) {
            street = "flopOrTurn";
        }
        if(gameVariables.getRiverCard() != null) {
            street = "river";
        }

        return street;
    }

    private List<String> getEligibleActions(GameVariables gameVariables) {
        List<String> eligibleActions = new ArrayList<>();

        if(gameVariables.getOpponentAction().contains("bet") || gameVariables.getOpponentAction().contains("raise")) {
            if(gameVariables.getOpponentStack() == 0 ||
                    (gameVariables.getBotStack() + gameVariables.getBotBetSize()) <= gameVariables.getOpponentBetSize()) {
                eligibleActions.add("fold");
                eligibleActions.add("call");
            } else {
                eligibleActions.add("fold");
                eligibleActions.add("call");
                eligibleActions.add("raise");
            }
        } else {
            eligibleActions.add("check");
            eligibleActions.add("bet75pct");
        }

        return eligibleActions;
    }

    private String doOpponentTypeDbLogic(String opponentName) throws Exception {
        OpponentIdentifier opponentIdentifier = new OpponentIdentifier();
        int numberOfHands = opponentIdentifier.getOpponentNumberOfHandsFromDb(opponentName);

        if(numberOfHands == 0) {
            opponentIdentifier.updateNumberOfHandsPerOpponentMapInDb(opponentName);
        }

        return new OpponentIdentifier().getOpponentTypeFromDb(opponentName, numberOfHands);
    }

    private double getAmountToCallBb(double botBetSizeBb, double opponentBetSizeBb, double botStackBb) {
        double amountToCallBb = opponentBetSizeBb - botBetSizeBb;

        if(amountToCallBb > botStackBb) {
            amountToCallBb = botStackBb;
        }

        return amountToCallBb;
    }

    private void doEquityLogic(List<Card> board, List<Card> botHoleCards) {
        if(board != null && (board.size() == 3 || board.size() == 4)) {
            Equity equity = new Equity();
            List<Card> botHoleCardsCopy = new ArrayList<>();
            List<Card> boardCopy = new ArrayList<>();

            botHoleCardsCopy.addAll(botHoleCards);
            boardCopy.addAll(board);

            List<Double> handStrengthAtRiverList = equity.getHandstrengthAtRiverList(boardCopy, botHoleCardsCopy, 25);

            int numberOfScoresAbove90 = equity.getNumberOfScoresAboveLimit(handStrengthAtRiverList, 0.90);

            if(board.size() == 3) {
                if(numberOfScoresAbove90 >= 8) {
                    if(!botHasStrongDraw) {
                        if(boardIsUnpairedAndNonSuited(board)) {
                            System.out.println("equity aaa");
                            botHasStrongDraw = true;
                            strongFlushDraw = true;
                        }
                    } else {
                        botHasStrongDraw = true;
                        strongFlushDraw = true;
                    }
                } else if(numberOfScoresAbove90 >= 7) {
                    if(!botHasStrongDraw) {
                        if(boardIsUnpairedAndNonSuited(board)) {
                            System.out.println("equity bbb");
                            botHasStrongDraw = true;
                            strongOosd = true;
                        }
                    } else {
                        botHasStrongDraw = true;
                        strongOosd = true;
                    }
                } else if(numberOfScoresAbove90 >= 4) {
                    if(!botHasStrongDraw) {
                        if(boardIsUnpairedAndNonSuited(board)) {
                            System.out.println("equity ccc");
                            botHasStrongDraw = true;
                            strongGutshot = true;
                        }
                    } else {
                        botHasStrongDraw = true;
                        strongGutshot = true;
                    }
                }
            } else {
                if(numberOfScoresAbove90 >= 5) {
                    if(!botHasStrongDraw) {
                        if(boardIsUnpairedAndNonSuited(board)) {
                            System.out.println("equity ddd");
                            botHasStrongDraw = true;
                            strongFlushDraw = true;
                        }
                    } else {
                        botHasStrongDraw = true;
                        strongFlushDraw = true;
                    }
                } else if(numberOfScoresAbove90 >= 4) {
                    if(!botHasStrongDraw) {
                        if(boardIsUnpairedAndNonSuited(board)) {
                            System.out.println("equity eee");
                            botHasStrongDraw = true;
                            strongOosd = true;
                        }
                    } else {
                        botHasStrongDraw = true;
                        strongOosd = true;
                    }
                } else if(numberOfScoresAbove90 >= 3) {
                    if(!botHasStrongDraw) {
                        if(boardIsUnpairedAndNonSuited(board)) {
                            System.out.println("equity fff");
                            botHasStrongDraw = true;
                            strongGutshot = true;
                        }
                    } else {
                        botHasStrongDraw = true;
                        strongGutshot = true;
                    }
                }
            }
        }
    }

    private boolean boardIsUnpairedAndNonSuited(List<Card> board) {
        BoardEvaluator boardEvaluator = new BoardEvaluator();

        boolean boardIsUnpaired = boardEvaluator.getNumberOfPairsOnBoard(board) == 0;
        boolean hasMaxTwoSuitedCards = boardEvaluator.getNumberOfSuitedCardsOnBoard(board) <= 2;

        return boardIsUnpaired && hasMaxTwoSuitedCards;
    }

    private int getBoardWetness(ContinuousTable continuousTable) {
        List<Set<Card>> top10PercentFlopCombosCopy = new ArrayList<>();
        List<Set<Card>> top10PercentTurnCombosCopy = new ArrayList<>();
        List<Set<Card>> top10PercentRiverCombosCopy = new ArrayList<>();

        if(continuousTable.getTop10percentFlopCombos() != null) {
            top10PercentFlopCombosCopy.addAll(continuousTable.getTop10percentFlopCombos());
        }

        if(continuousTable.getTop10percentTurnCombos() != null) {
            top10PercentTurnCombosCopy.addAll(continuousTable.getTop10percentTurnCombos());
        }

        if(continuousTable.getTop10percentRiverCombos() != null) {
            top10PercentRiverCombosCopy.addAll(continuousTable.getTop10percentRiverCombos());
        }

        int boardWetnessToReturn;
        int boardWetnessRiver = 200;
        int boardWetnessTurn = 200;

        if(!continuousTable.getTop10percentRiverCombos().isEmpty()) {
            boardWetnessRiver = BoardEvaluator.getBoardWetness(top10PercentTurnCombosCopy, top10PercentRiverCombosCopy);
        }

        if(!continuousTable.getTop10percentTurnCombos().isEmpty()) {
            boardWetnessTurn = BoardEvaluator.getBoardWetness(top10PercentFlopCombosCopy, top10PercentTurnCombosCopy);
        }

        if(boardWetnessRiver < boardWetnessTurn) {
            boardWetnessToReturn = boardWetnessRiver;
        } else {
            boardWetnessToReturn = boardWetnessTurn;
        }

        System.out.println("BoardWetness: " + boardWetnessToReturn);

        return boardWetnessToReturn;
    }

    private String preventCallIfOpponentOrBotAlmostAllInAfterCall(String action, double opponentStackBb, double botStackBb,
                                                                  double botTotalBetSizeBb, double potSizeBb, double amountToCallBb,
                                                                  List<Card> board) {
        String actionToReturn;

        if(action.equals("call")) {
            if(board == null || board.size() < 5) {
                if(opponentStackBb > 0) {
                    double botStackBbAfterCall = botStackBb - amountToCallBb;

                    if(botStackBbAfterCall > 0) {
                        double potSizeBbAfterCall = potSizeBb + (2 * botTotalBetSizeBb) + (2 * amountToCallBb);

                        if((botStackBbAfterCall / potSizeBbAfterCall < 0.5) || (opponentStackBb / potSizeBbAfterCall < 0.5)) {
                            actionToReturn = "raise";
                            System.out.println("abc-- change action to raise in preventCallIfOpponentOrBotAlmostAllInAfterCall()");
                        } else {
                            actionToReturn = action;
                        }
                    } else {
                        actionToReturn = action;
                    }
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    private String neverFoldStrongEquity(String action, List<Card> board, List<String> eligibleActions, boolean pre3betOrPostRaisedPot,
                                         double amountToCallBb, double bigBlind) {
        String actionToReturn;

        if(action.equals("fold") && amountToCallBb < 100) {
            if(board != null && (board.size() == 3 || board.size() == 4)) {
                boolean strongFd = handEvaluator.hasDrawOfType("strongFlushDraw");
                boolean strongOosd = handEvaluator.hasDrawOfType("strongOosd");
                boolean strongGutshot = handEvaluator.hasDrawOfType("strongGutshot");
                boolean strongOvercards = handEvaluator.hasDrawOfType("strongOvercards");

                if((botHandStrength >= 0.64 && (strongFd || strongOosd || strongGutshot || strongOvercards)) ||
                        ((strongFd && strongOosd) || (strongFd && strongGutshot) || (strongFd && strongOvercards) || (strongOosd && strongOvercards) || (strongGutshot && strongOvercards))) {
                    if(eligibleActions.contains("raise") && !pre3betOrPostRaisedPot && sizing / bigBlind < 100) {
                        double random = Math.random();

                        if(random <= 0.5) {
                            actionToReturn = "call";
                            System.out.println("A neverFoldStrongEquity() -> call");
                        } else {
                            actionToReturn = "raise";
                            System.out.println("B neverFoldStrongEquity() -> call");
                        }
                    } else {
                        actionToReturn = "call";
                        System.out.println("C neverFoldStrongEquity() -> call");
                    }
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    private double adjustRaiseSizingToSng(double currentSizing, String action, GameVariables gameVariables,
                                         double effectiveStackBb) {
        double sngSizingToReturn;

        if(action.equals("raise")) {
            if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
                if(!gameVariables.isBotIsButton()) {
                    if(gameVariables.getOpponentAction().equals("raise")) {
                        if(effectiveStackBb <= 30) {
                            sngSizingToReturn = 5000 * gameVariables.getBigBlind();
                            System.out.println("Change pre3bet sizing to shove in adjustRaiseSizingToSng(). P");
                        } else {
                            sngSizingToReturn = currentSizing;
                        }
                    } else {
                        sngSizingToReturn = currentSizing;
                    }
                } else {
                    if(gameVariables.getOpponentAction().equals("raise")) {
                        //4bet
                        sngSizingToReturn = 5000 * gameVariables.getBigBlind();
                        System.out.println("Change pre4bet sizing to shove in adjustRaiseSizingToSng(). Q");
                    } else {
                        sngSizingToReturn = currentSizing;
                    }
                }
            } else {
                //postflop
                if(currentSizing > 300 || effectiveStackBb <= 30) {
                    sngSizingToReturn = 5000 * gameVariables.getBigBlind();
                    System.out.println("Change postRaise sizing to shove in adjustRaiseSizingToSng(). R");
                } else {
                    sngSizingToReturn = currentSizing;
                }
            }
        } else {
            sngSizingToReturn = currentSizing;
        }

        return sngSizingToReturn;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setSizing(double sizing) {
        this.sizing = sizing;
    }

    public String getOpponentType() {
        return opponentType;
    }

    public void setOpponentType(String opponentType) {
        this.opponentType = opponentType;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public double getBotHandStrength() {
        return botHandStrength;
    }

    public void setBotHandStrength(double botHandStrength) {
        this.botHandStrength = botHandStrength;
    }

    public boolean isBotHasStrongDraw() {
        return botHasStrongDraw;
    }

    public void setBotHasStrongDraw(boolean botHasStrongDraw) {
        this.botHasStrongDraw = botHasStrongDraw;
    }

    public String getAction() {
        return action;
    }

    public double getSizing() {
        return sizing;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public boolean isStrongFlushDraw() {
        return strongFlushDraw;
    }

    public void setStrongFlushDraw(boolean strongFlushDraw) {
        this.strongFlushDraw = strongFlushDraw;
    }

    public boolean isStrongOosd() {
        return strongOosd;
    }

    public void setStrongOosd(boolean strongOosd) {
        this.strongOosd = strongOosd;
    }

    public boolean isStrongGutshot() {
        return strongGutshot;
    }

    public void setStrongGutshot(boolean strongGutshot) {
        this.strongGutshot = strongGutshot;
    }

    public boolean isStrongOvercards() {
        return strongOvercards;
    }

    public void setStrongOvercards(boolean strongOvercards) {
        this.strongOvercards = strongOvercards;
    }

    public boolean isStrongBackdoorFd() {
        return strongBackdoorFd;
    }

    public void setStrongBackdoorFd(boolean strongBackdoorFd) {
        this.strongBackdoorFd = strongBackdoorFd;
    }

    public boolean isStrongBackdoorSd() {
        return strongBackdoorSd;
    }

    public void setStrongBackdoorSd(boolean strongBackdoorSd) {
        this.strongBackdoorSd = strongBackdoorSd;
    }

    public HandEvaluator getHandEvaluator() {
        return handEvaluator;
    }

    public void setHandEvaluator(HandEvaluator handEvaluator) {
        this.handEvaluator = handEvaluator;
    }

    public String getActionBeforeFoldStat() {
        return actionBeforeFoldStat;
    }

    public void setActionBeforeFoldStat(String actionBeforeFoldStat) {
        this.actionBeforeFoldStat = actionBeforeFoldStat;
    }

    public BoardEvaluator getBoardEvaluator() {
        return boardEvaluator;
    }

    public void setBoardEvaluator(BoardEvaluator boardEvaluator) {
        this.boardEvaluator = boardEvaluator;
    }
}
