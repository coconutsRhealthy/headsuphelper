package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.*;
import com.lennart.model.action.actionbuilders.ai.equityrange.BotActionBuilder;
import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;
//import com.lennart.model.action.actionbuilders.ai.oppdependent.TrickySleeps;
//import com.lennart.model.action.actionbuilders.ai.oppdependent.TwentiesRegs;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0.AdjustPostflopPlayToOpp;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0.AdjustPreflopPlayToOpp;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0.StatsRetrieverPostflop;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0.StatsRetrieverPreflop;
import com.lennart.model.action.actionbuilders.preflop.PreflopActionBuilder;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.handevaluation.PreflopHandStength;
import com.lennart.model.handtracker.ActionRequest;
import com.lennart.model.handtracker.PlayerActionRound;
import equitycalc.EquityCalculator;

import java.util.*;
import java.util.stream.Collectors;

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

    private BoardEvaluator boardEvaluator;

    private int numberOfScoresAbove80;

    private double botEquity;

    int oppNumberOfHands;

    private static double callBoundryProvidedHs = -1;
    private static BoardEvaluator callBoundryProvidedBoardEvaluator = null;

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

    public String getDummyActionOppAllIn(ContinuousTable continuousTableInput, GameVariables gameVariablesInput) throws Exception {
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
        OpponentIdentifier opponentIdentifier = new OpponentIdentifier();
        int numberOfHands = opponentIdentifier.getOpponentNumberOfHandsFromDb(gameVariables.getOpponentName());

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

        boolean strongFdInMethod = strongFlushDraw;
        boolean strongOosdInMethod = strongOosd;
        boolean strongGutshotInMethod = strongGutshot;

        opponentType = doOpponentTypeDbLogic(gameVariables.getOpponentName());
        double opponentBetsizeBb = gameVariables.getOpponentBetSize() / gameVariables.getBigBlind();
        double botBetsizeBb = gameVariables.getBotBetSize() / gameVariables.getBigBlind();
        double opponentStackBb = gameVariables.getOpponentStack() / gameVariables.getBigBlind();
        double botStackBb = gameVariables.getBotStack() / gameVariables.getBigBlind();
        boolean preflop = gameVariables.getBoard().isEmpty();
        List<Card> boardInMethod = gameVariables.getBoard();

        Sizing sizingYo = new Sizing();

        if(realGame) {
            setOpponentHasInitiative(opponentActionInMethod, continuousTable, gameVariables);
        }

        setOpponentDidPostflopFlopOrTurnRaiseOrOverbet(opponentActionInMethod, boardInMethod, continuousTable, opponentBetsizeBb, potSizeBb);
        double amountToCallBb = getAmountToCallBb(botBetsizeBb, opponentBetsizeBb, botStackBb);

        int boardWetness = getBoardWetness(continuousTable, boardInMethod);
        boolean defaultCheck = false;

        if(preflop) {
            action = new PreflopActionBuilder().getAction(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getOpponentStack(), gameVariables.getBigBlind(), gameVariables.getBotHoleCards(), gameVariables.isBotIsButton(), continuousTable, amountToCallBb, gameVariables.getOpponentName(), numberOfHandsIsBluffable(numberOfHands), effectiveStack);

            if(action.equals("raise")) {
                sizing = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
            }
        } else {
            //hier de equity logic
            doEquityLogic(boardInMethod, gameVariables.getBotHoleCards());

            if(continuousTable != null && (continuousTable.isOpponentHasInitiative() && opponentActionInMethod.equals("empty"))) {
                System.out.println("default check, opponent has initiative");
                action = "check";
                defaultCheck = true;
            } else {
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
                            sizing = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);

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
                    sizing = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
                }
            }

            if(action.equals("raise")) {
                continuousTable.setPre3betOrPostRaisedPot(true);
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

//            action = new FoldStatBluffAdjuster().doBluffAccordingToFoldStat(action, bigBlind, botIsButtonInMethod,
//                    botHandStrength, boardInMethod, continuousTable.isOpponentHasInitiative(),
//                    opponentBetsizeBb * bigBlind, botBetsizeBb * bigBlind, botStackBb * bigBlind,
//                    opponentStackBb * bigBlind, potSizeBb * bigBlind, continuousTable.isPre3betOrPostRaisedPot(),
//                    gameVariables.getOpponentName());
//
//            action = new FoldStatBluffAdjuster().preventBluffAccordingToFoldStat(action, this, eligibleActions, streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod,
//                    facingOdds, effectiveStack * bigBlind, botHasStrongDraw, botHandStrength, opponentType, opponentBetsizeBb, botBetsizeBb,
//                    opponentStackBb, botStackBb, preflop, boardInMethod, strongFlushDraw, strongOosd, strongGutshot, bigBlind, continuousTable.isOpponentDidPreflop4betPot(),
//                    continuousTable.isPre3betOrPostRaisedPot(), strongOvercards, strongBackdoorFd, strongBackdoorSd, boardWetness, continuousTable.isOpponentHasInitiative(), gameVariables.getOpponentName());
//
//            action = new FoldStatBluffAdjuster().preventBigBluffsAgainstLowFoldstat(action, this, eligibleActions, streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod,
//                    facingOdds, effectiveStack * bigBlind, botHasStrongDraw, botHandStrength, opponentType, opponentBetsizeBb, botBetsizeBb,
//                    opponentStackBb, botStackBb, preflop, boardInMethod, strongFlushDraw, strongOosd, strongGutshot, bigBlind, continuousTable.isOpponentDidPreflop4betPot(),
//                    continuousTable.isPre3betOrPostRaisedPot(), strongOvercards, strongBackdoorFd, strongBackdoorSd, boardWetness, continuousTable.isOpponentHasInitiative(), gameVariables.getOpponentName());
//
//            action = new PlayerBluffer().doOpponentBluffSuccessAction(action, gameVariables.getOpponentName(), bigBlind,
//                    botHandStrength, boardInMethod, continuousTable.isOpponentHasInitiative(), opponentBetsizeBb * bigBlind,
//                    botBetsizeBb * bigBlind, botStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, continuousTable.isPre3betOrPostRaisedPot());

            if((action.equals("bet75pct") || action.equals("raise")) && sizing == 0) {
                sizing = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
                sizing = adjustRaiseSizingToSng(sizing, action, gameVariables, effectiveStack);
            }

            action = solidifyPostflopRaises(action, boardInMethod, botHandStrength, strongFlushDraw, strongOosd, continuousTable, gameVariables, sizing);

            //machine learning
//            String actionBeforeMachineLearning = action;
//
//            double sizingForMachineLearning = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
//            action = new MachineLearning().adjustActionToDbSaveData(this, gameVariables, continuousTable, sizingForMachineLearning);
//
//            if(!actionBeforeMachineLearning.equals(action)) {
//                if(actionBeforeMachineLearning.equals("call") || actionBeforeMachineLearning.equals("raise") && action.equals("fold")) {
//                    if(facingOdds <= 0.2) {
//                        action = "call";
//                        System.out.println("Facing tiny bet, revert Machinelearning adjust to fold");
//                    }
//
//                    if(botHandStrengthInMethod > 0.7) {
//                        action = "call";
//                        System.out.println("Revert bad Machinelearning fold");
//                    }
//                }
//
//                System.out.println("---Action changed in Machinelearning from: " + actionBeforeMachineLearning + " to: " + action);
//            }
            //machine learning

            if((action.equals("bet75pct") || action.equals("raise"))) {
                sizing = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
                sizing = adjustRaiseSizingToSng(sizing, action, gameVariables, effectiveStack);
            }
        } else {
            //hier iets...
            //double sizingForMachineLearning = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
            //action = new MachineLearningPreflop().adjustActionToDbSaveData(this, gameVariables, sizingForMachineLearning);
        }

        action = preventCallIfOpponentOrBotAlmostAllInAfterCall(action, opponentStackBb, botStackBb, botBetsizeBb, potSizeBb, amountToCallBb, boardInMethod);

        if((action.equals("bet75pct") || action.equals("raise")) && sizing == 0) {
            sizing = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
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
                action = shortStackPlayAdjuster.adjustAction(action, gameVariables, this, continuousTable.isOpponentHasInitiative());
                //sizing = shortStackPlayAdjuster.adjustSizing(action, sizing, gameVariables.getBigBlind());
            } else {
                System.out.println("No shortstack play, total is wrong");
            }
        }

        action = neverFoldStrongEquity(action, boardInMethod, eligibleActions, continuousTable.isPre3betOrPostRaisedPot(),
                amountToCallBb, gameVariables.getBigBlind());

        action = preventCallIfOpponentOrBotAlmostAllInAfterCall(action, opponentStackBb, botStackBb, botBetsizeBb, potSizeBb, amountToCallBb, boardInMethod);

        if(action.equals("bet75pct") || action.equals("raise")) {
            if(sizing == 0) {
                sizing = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
            }

            sizing = adjustRaiseSizingToSng(sizing, action, gameVariables, effectiveStack);
        }

        String actionBeforeNash = action;
        double sizingBeforeNash = sizing;

        boolean gonnaDoNashAction = false;

        try {
            Nash nash = new Nash();
            boolean nashActionIsPossible = nash.nashActionIsPossible(effectiveStack, botIsButtonInMethod, botBetsizeBb,
                    boardInMethod, gameVariables.getOpponentAction(), gameVariables.getBotHoleCards(), opponentStackBb,
                    amountToCallBb);

            if(nashActionIsPossible) {
                action = nash.doNashAction(gameVariables.getBotHoleCards(), botIsButtonInMethod, effectiveStack, amountToCallBb);

                if(action.equals("raise")) {
                    gonnaDoNashAction = true;
                    sizing = 5000 * gameVariables.getBigBlind();
                    System.out.println("Set Nash action raise sizing to shove: " + sizing);
                } else if(action.equals("call")) {
                    System.out.println("Gonna do Nash call!");
                }
            }
        } catch (Exception e) {
            System.out.println("Nash error!");
            System.out.println();
            e.printStackTrace();
            System.out.println();

            action = actionBeforeNash;
            sizing = sizingBeforeNash;
        }

        action = raiseFlopAndTurnWithStrongHand(action, botHandStrengthInMethod, boardInMethod, amountToCallBb, botStackBb, opponentStackBb);
        action = doValueBet(action, continuousTable.isOpponentHasInitiative(), botHandStrength, boardInMethod, gameVariables.isBotIsButton());

        if(action.equals("bet75pct") || action.equals("raise")) {
            if(sizing == 0) {
                sizing = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
            }

            sizing = adjustRaiseSizingToSng(sizing, action, gameVariables, effectiveStack);
        }

        action = preventTooThinValueRaises(action, botHandStrength, boardInMethod, strongFlushDraw, strongOosd, strongGutshot, continuousTable, gameVariables);
        action = preventCallIfOpponentOrBotAlmostAllInAfterCall(action, opponentStackBb, botStackBb, botBetsizeBb, potSizeBb, amountToCallBb, boardInMethod);

        double sizingForBluffOdds;
        if(sizing == 0) {
            sizingForBluffOdds = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
            sizingForBluffOdds = adjustRaiseSizingToSng(sizingForBluffOdds, action, gameVariables, effectiveStack);
        } else {
            sizingForBluffOdds = sizing;
        }

        boolean bluffOddsAreOk = new MachineLearning().bluffOddsAreOk(sizingForBluffOdds, gameVariables.getOpponentBetSize(),
                gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBotStack(),
                boardInMethod, gameVariables.getBotBetSize());

        action = preventManyBluffsJudgeByBoard(action, botHandStrengthInMethod, boardWetness, boardInMethod, strongOosdInMethod, strongFdInMethod, strongGutshotInMethod,
                continuousTable, gameVariables);

        if(action.equals("bet75pct") || action.equals("raise")) {
            if(sizing == 0) {
                sizing = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
            }

            sizing = adjustRaiseSizingToSng(sizing, action, gameVariables, effectiveStack);
        }

        if(!action.equals("bet75pct") && !action.equals("raise")) {
            sizing = 0;
        }

        adjustPfSizingAfterOppLimp(action, effectiveStack, boardInMethod, gameVariables.getOpponentAction(), botIsButtonInMethod, gameVariables.getBigBlind());

        if(!numberOfHandsIsBluffable(numberOfHands)) {
            action = preventAllBluffs(action, botHandStrengthInMethod, boardInMethod, sizing, continuousTable, gameVariables, strongFdInMethod, strongOosdInMethod, strongGutshotInMethod);
        }

        action = preventBadPostCalls(action, botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod, boardInMethod, facingOdds);
        action = adjustPostFoldsToAggroness(action, boardInMethod, botHandStrengthInMethod, continuousTable.getFlopHandstrength(), continuousTable.getTurnHandstrength(), opponentStackBb, botStackBb, botBetsizeBb, potSizeBb, amountToCallBb, facingOdds, gameVariables.getOpponentName());

        action = preventPreRaiseIfBotStackBelow1bb(action, gameVariables.getBigBlind(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), boardInMethod);

        if(action.equals("bet75pct")) {
            sizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
        }

        ///////
        if(boardInMethod == null || boardInMethod.isEmpty()) {
            if(action.equals("raise")) {
                if(gameVariables.getBotBetSize() == gameVariables.getBigBlind() &&
                        gameVariables.getOpponentBetSize() == gameVariables.getBigBlind()) {
                    double hsLimit;
                    double randomLimit;

                    if(effectiveStack > 12) {
                        hsLimit = 0.6;
                        randomLimit = 0.05;
                    } else {
                        hsLimit = 0.75;
                        randomLimit = 0.1;
                    }

                    if(botHandStrength > hsLimit) {
                        if(Math.random() > randomLimit) {
                            System.out.println("keep raise vs limp cause strong hand");
                            //no change action
                        } else {
                            System.out.println("strong hand raise > check vs limp pf");
                            action = "check";
                        }
                    } else {
                        System.out.println("raise > check vs limp pf");
                        action = "check";
                    }
                } else if(gameVariables.getOpponentBetSize() == gameVariables.getBigBlind()) {
                    if(gonnaDoNashAction) {
                        //nothing, keep nash raise shove
                        System.out.println("raise > nashraise");
                    } else {
                        if(botHandStrength > 0.55) {
                            if(Math.random() > 0.42) {
                                //nothing, strong hand so keep pf raise
                                System.out.println("keep pf raise, strong hand");
                            } else {
                                System.out.println("raise > limp strong hand pf");
                                action = "call";
                            }
                        } else {
                            System.out.println("raise > limp pf");
                            action = "call";
                        }
                    }
                } else {
                    //nothing, keep 3betting etc
                }
            } else {
                //nothing
            }
        } else {
            if(continuousTable.getRangeConstructor() == null) {
                System.out.println("WTF! RangeConstructor is null");
            } else {
                String olldStyleAction = action;
                BotActionBuilder botActionBuilder = new BotActionBuilder();
                String newwStyleAction = botActionBuilder.getAction(continuousTable, gameVariables,
                        continuousTable.getRangeConstructor());

                if(olldStyleAction.equals("raise")) {
                    //always keep it raise postflop
                }

                if(newwStyleAction.equals("raise")) {
                    if(gameVariables.getPot() < 80 &&
                            ((gameVariables.getPot() == 40 && gameVariables.getBigBlind() == 20) ||
                                    (gameVariables.getPot() == 60 && gameVariables.getBigBlind() == 30)) && Math.random() < 0.5) {
                        System.out.println("no small limped pot raise");
                        newwStyleAction = olldStyleAction;
                    } else {
                        System.out.println("Newstyle raise!");

                        if(botHandStrength < 0.65) {
                            System.out.println("Newstyle bluffraise: " + botHandStrength + " bb below 50: " + (gameVariables.getBigBlind() < 50));
                        } else {
                            System.out.println("Newstyle valueraise: " + botHandStrength);
                        }
                    }
                }

                if(newwStyleAction.equals("fold")) {
                    action = olldStyleAction;
                } else if(newwStyleAction.equals("check")) {
                    action = olldStyleAction;
                } else {
                    action = newwStyleAction;
                }

                action = callRules(action, botActionBuilder.getBotEquity(), boardInMethod, facingOdds,
                        (strongFdInMethod || strongOosdInMethod || strongGutshotInMethod), botIsButtonInMethod, bluffOddsAreOk);
            }
        }
        ////

        //oop value trap shit
        if(action.equals("bet75pct") && botHandStrength > 0.8 && !botIsButtonInMethod && (boardInMethod != null && !boardInMethod.isEmpty())) {
            if(Math.random() < 0.2) {
                action = "check";
                System.out.println("Oop value trap yo");
            }
        }

        //ip value trap shit
        if(action.equals("bet75pct") && botHandStrength > 0.8 && botIsButtonInMethod && (boardInMethod != null && !boardInMethod.isEmpty())) {
            if(boardInMethod.size() == 3 || boardInMethod.size() == 4) {
                if(Math.random() < 0.1) {
                    action = "check";
                    System.out.println("Ip value trap yo");
                }
            }
        }

        action = shovePreflopWithAceHands(action, boardInMethod, gameVariables.getBotHoleCards(), effectiveStack,
                eligibleActions, gameVariables.getOpponentAction(), gameVariables.getBigBlind());
        action = shoveVersusLimpsWithStrongerHands(action, boardInMethod, gameVariables.getOpponentAction(), effectiveStack,
                gameVariables.getBigBlind(), botHandStrengthInMethod);
        action = funkyPreflopExtraShoves(action, boardInMethod, gameVariables.getOpponentAction(), gameVariables.getBigBlind(), botHandStrengthInMethod, effectiveStack);

        if(boardInMethod != null && !boardInMethod.isEmpty()) {
            if((boardInMethod.size() == 3 && botIsButtonInMethod) || boardInMethod.size() == 4 || boardInMethod.size() == 5) {
                action = preventManyPostflopBets(action, boardInMethod, botIsButtonInMethod, botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
            }
        }

        action = preventManyPostflopRaises(action, botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod, boardInMethod, botIsButtonInMethod, continuousTable, gameVariables);

        action = callLooseAfterLimpVersusShoveUndeep(action, effectiveStack, botIsButtonInMethod, botBetsizeBb, gameVariables.getOpponentAction(),
                opponentStackBb, amountToCallBb, gameVariables.getBotHoleCards(), botHandStrengthInMethod, boardInMethod);

        action = preventTooLooseCallsVersusShovesPre(action, boardInMethod, gameVariables.getOpponentStack(), gameVariables.getBotBetSize(),
                gameVariables.getBigBlind(), botHandStrengthInMethod, gameVariables.getOpponentAction(), amountToCallBb);

        action = shoveWithWeaks(action, boardInMethod, gameVariables.getOpponentAction(), botHandStrengthInMethod, effectiveStack, botIsButtonInMethod, gameVariables.getBigBlind());
        action = limpWithPremiums(action, boardInMethod, gameVariables.getOpponentAction(), botHandStrengthInMethod, effectiveStack, botIsButtonInMethod);
        action = preventFlopDonkBetsAfterCheckingVersusLimp(action, boardInMethod, botIsButtonInMethod, potSizeBb);
        action = raiseFlopsAndTurnsAndRivers(action, boardInMethod, gameVariables.getOpponentAction(), botHandStrengthInMethod, strongFdInMethod || strongFlushDraw, strongOosdInMethod || strongOosd, strongGutshotInMethod || strongGutshot, bluffOddsAreOk, botIsButtonInMethod);

        action = raiseWithWeakVersusLimps(action, boardInMethod, botIsButtonInMethod, effectiveStack, botHandStrengthInMethod, gameVariables.getBigBlind());
        action = checkWithPremiumsVersusLimps(action, boardInMethod, botIsButtonInMethod, gameVariables.getOpponentAction(), botHandStrengthInMethod, effectiveStack);
        action = call2betWithPremiumsPreOop(action, boardInMethod, gameVariables.getOpponentAction(), botIsButtonInMethod, botHandStrengthInMethod);
        action = changeOpenFoldsToLimp(action, gameVariables.getOpponentAction(), botIsButtonInMethod, boardInMethod, gameVariables.getBotHoleCards());

        action = trickyCallWithMonstersOnFlopAndTurn(action, bluffOddsAreOk, boardInMethod, botHandStrengthInMethod);
        action = adjustPlayAgainstDonkbets(action, boardInMethod, potSizeBb, botIsButtonInMethod, opponentBetsizeBb, botHandStrengthInMethod,
                gameVariables.getOpponentAction(), botBetsizeBb);
        //action = callLooseAgainst4betShovesPre(action, boardInMethod, botHandStrengthInMethod, botIsButtonInMethod, botBetsizeBb, eligibleActions);
        //action = fewerBetsIpOnTurn(action, boardInMethod, botIsButtonInMethod, botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod, strongGutshotInMethod);
        //action = moreIpRiverRaises(action, boardInMethod, botIsButtonInMethod, gameVariables.getOpponentAction(), bluffOddsAreOk, botHandStrengthInMethod);

        if(action.equals("bet75pct") || action.equals("raise")) {
            if(sizing == 0) {
                sizing = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
            }

            sizing = adjustRaiseSizingToSng(sizing, action, gameVariables, effectiveStack);
        }


        //sizing shit
        if(gameVariables.getPot() == 2 * gameVariables.getBigBlind() && action.equals("bet75pct")) {
            //if(sizing < (0.5 * gameVariables.getPot())) {
            System.out.println("xx reset sizing to 1 bigblind");
            sizing = gameVariables.getBigBlind();
            //}
        } else if(action.equals("bet75pct")) {
            //sizing = 0.5 * gameVariables.getPot();
            if(boardInMethod != null && !boardInMethod.isEmpty() && boardInMethod.size() == 3 && !botIsButtonInMethod) {
                sizing = 0.35 * gameVariables.getPot();
            } else {
                sizing = 0.5 * gameVariables.getPot();
            }
        } else if(action.equals("raise") && boardInMethod != null && !boardInMethod.isEmpty()) {
            sizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);

            //sizing = 2.06 * gameVariables.getOpponentBetSize();

            //if(sizing < 0.5 * gameVariables.getPot()) {
            //    sizing = 0.51 * gameVariables.getPot();
            //}

            System.out.println("post raisesizing eije!");
        }
        //


        if(!action.equals("bet75pct") && !action.equals("raise")) {
            sizing = 0;
        }

        if(boardInMethod != null && boardInMethod.size() >= 3 && (action.equals("bet75pct") || action.equals("raise")) && botHandStrength < 0.64) {
            continuousTable.setBotBluffActionDone(true);
        }

        if(action.equals("raise") && boardInMethod != null && boardInMethod.size() >= 3) {
            continuousTable.setPre3betOrPostRaisedPot(true);
        }

        if(boardInMethod != null && !boardInMethod.isEmpty() && action.equals("raise") && botHandStrengthInMethod < 0.8) {
            System.out.println("bluff raise here");
        }

        if(action.equals("bet75pct") && (sizing > 0.85 * gameVariables.getPot()) && boardInMethod != null) {
            String bigBetPercentageOfPot;

            if(sizing > (gameVariables.getPot() * 1.06)) {
                bigBetPercentageOfPot = "150%";
            } else {
                bigBetPercentageOfPot = "100%";
            }

            if(botHandStrengthInMethod < 0.55 && !strongFdInMethod && !strongOosdInMethod) {
                System.out.println(bigBetPercentageOfPot + " big bluff bet sizing! board size: " + boardInMethod.size() + " sizing: " + sizing);
            } else if(botHandStrengthInMethod > 0.85) {
                System.out.println(bigBetPercentageOfPot + " big value sizing! board size: " + boardInMethod.size() + " sizing: " + sizing);
            } else {
                System.out.println(bigBetPercentageOfPot + " big value draw sizing! board size: " + boardInMethod.size() + " sizing: " + sizing);
            }
        }

        if(strongGutshot || strongOosd || strongFlushDraw) {
            if(bluffOddsAreOk) {
                if(action.equals("fold") || action.equals("call")) {
                    if(botHandStrengthInMethod < 0.5) {
                        System.out.println("HARBOZ: draw bluffraise opp, now " + action + ". harboz-gs: "
                                + strongGutshot + ", harboz-fd: " + strongFlushDraw + ", harboz-oosd: " + strongOosd);
                    } else {
                        System.out.println("HARBOZ: draw valueraise opp, now " + action + ". harboz-gs: "
                                + strongGutshot + ", harboz-fd: " + strongFlushDraw + ", harboz-oosd: " + strongOosd);
                    }
                }
            }
        }

        setShoveSizingForCertainPre3betsAndPostCheckRaises(action, botIsButtonInMethod, gameVariables.getOpponentAction(), boardInMethod, gameVariables.getBigBlind());
        nonShovePre3betWithPremiums(action, gameVariables.getBotHoleCards(), boardInMethod, botIsButtonInMethod,
                gameVariables.getOpponentAction(), gameVariables.getOpponentBetSize(), effectiveStack,
                gameVariables.getBotBetSize(), gameVariables.getBigBlind(), continuousTable);
        dontShoveBut3xRaiseVsLimpsDeep(action, effectiveStack, botIsButtonInMethod, boardInMethod, gameVariables.getOpponentAction(), gameVariables.getBigBlind());
        dontOpenShoveIpDeep(action, effectiveStack, botIsButtonInMethod, gameVariables.getOpponentAction(), gameVariables.getBigBlind());
        getValueFromPremiums(action, botHandStrengthInMethod, boardInMethod, botIsButtonInMethod, gameVariables.getOpponentAction(), effectiveStack, gameVariables.getBigBlind());
        possibilityToRaiseSmallOnRiver(action, botHandStrengthInMethod, boardInMethod, eligibleActions);
        adjustSizingForWetness(action, boardInMethod, botIsButtonInMethod, gameVariables.getPot(), gameVariables.getBigBlind(),
                botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod, strongGutshotInMethod);
        action = adjustIpRiverBets(action, boardInMethod, botIsButtonInMethod, botHandStrengthInMethod, gameVariables.getPot(), potSizeBb);


        ////
        if(callBoundryProvidedBoardEvaluator == null && callBoundryProvidedHs == -1 && numberOfHands >= 15) {
            System.out.println();
            System.out.println("START ACTION ADJUST SHIZZLE");

            boolean neededToRecalculateNewSizing = true;

            try {
                String adjustedAction = "emptyadjust";
                double suggestedAdjustedSizing = sizing;

                if(boardInMethod == null || boardInMethod.isEmpty()) {
                    String actionBeforeAdjust = action;
                    action = new AdjustPreflopPlayToOpp().adjustPreflopAction(action,
                            gameVariables.getOpponentName(),
                            botIsButtonInMethod,
                            botHandStrengthInMethod,
                            gameVariables.getOpponentAction(),
                            eligibleActions,
                            effectiveStack,
                            continuousTable);

                    if(action.equals("raise") && !actionBeforeAdjust.equals("raise")) {
                        getSizingForAction(gameVariables, action, continuousTable);
                    }
                } else {
                    double callHsBoundary = -1;

                    if(action.equals("fold") || action.equals("call")) {
                        callHsBoundary = determineMinimumHsToCall(gameVariables, continuousTable, botHandStrengthInMethod, action);
                    }

                    double hypotheticalSizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
                    hypotheticalSizing = adjustRaiseSizingToSng(hypotheticalSizing, action, gameVariables, effectiveStack);

                    Map<String, Double> adjustedActionAndSizing = new AdjustPostflopPlayToOpp().adjustPostflopActionAndSizing(action,
                            eligibleActions,
                            gameVariables.getOpponentName(),
                            defaultCheck,
                            bluffOddsAreOk,
                            gameVariables.getOpponentAction(),
                            botHandStrengthInMethod,
                            gameVariables.getPot(),
                            sizing,
                            boardInMethod,
                            botIsButtonInMethod,
                            callHsBoundary,
                            gameVariables.getBigBlind(),
                            strongFdInMethod,
                            strongOosdInMethod,
                            strongGutshotInMethod,
                            gameVariables.getOpponentBetSize(),
                            hypotheticalSizing,
                            continuousTable);

                    adjustedAction = adjustedActionAndSizing.keySet().stream().findFirst().get();

                    if(adjustedAction.equals("bet75pct") || adjustedAction.equals("raise")) {
                        suggestedAdjustedSizing = adjustedActionAndSizing.values().stream().findFirst().get();

                        if(suggestedAdjustedSizing != sizing) {
                            //sizing = suggestedAdjustedSizing;
                            System.out.println("Postflop action sizing adjustment. From: " + sizing + " to: " + suggestedAdjustedSizing);
                            neededToRecalculateNewSizing = false;
                            sizing = suggestedAdjustedSizing;
                        }
                    }
                }

                if(!adjustedAction.equals("emptyadjust") && !adjustedAction.equals(action)) {
                    if(boardInMethod != null && !boardInMethod.isEmpty()) {
                        if(!action.equals("raise")) {
                            System.out.println("Actual postflop action adjustment. From: " + action + " to: " + adjustedAction);
                            action = adjustedAction;

                            if((adjustedAction.equals("bet75pct") || adjustedAction.equals("raise")) && neededToRecalculateNewSizing) {
                                getSizingForAction(gameVariables, action, continuousTable);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Action adjust error");
                e.printStackTrace();
            }

            System.out.println("END ACTION ADJUST SHIZZLE");
            System.out.println();
        }
        ////

        if(realGame && continuousTable.getBankroll() > continuousTable.getBankrollLimit20Nl()) {
            String actionBefore = action;
            action = fuckingRaiseRivers(action, boardInMethod, botHandStrengthInMethod, bluffOddsAreOk, eligibleActions, gameVariables.getOpponentAction(), botIsButtonInMethod);

            if(action.equals("raise") && !actionBefore.equals("raise")) {
                System.out.println("Setting river f-in raise sizing");
                sizing = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
                sizing = adjustRaiseSizingToSng(sizing, action, gameVariables, effectiveStack);
            //    sizing = sizing * 1.275;
            }
        }

        if(continuousTable.getBankroll() > continuousTable.getBankrollLimit20Nl()) {
            shoveWayLessAgainstLimpsVsRegs(action, boardInMethod, botIsButtonInMethod, gameVariables.getOpponentAction(), effectiveStack, gameVariables.getBigBlind());
            action = neverFoldAfter3betNotAllInPreVsRegs(action, boardInMethod, botIsButtonInMethod, botBetsizeBb);
            action = funkyRaise2xInsteadOfShoveShallowVsRegs(action, botIsButtonInMethod, boardInMethod, opponentBetsizeBb, effectiveStack, gameVariables.getBigBlind());
            action = fewerOpenFoldsVsRegs(action, boardInMethod, botIsButtonInMethod, gameVariables.getOpponentAction());
            action = funkyOpenRaisesWithWeakerHands(action, boardInMethod, botIsButtonInMethod, effectiveStack, botHandStrengthInMethod, gameVariables.getOpponentAction(), gameVariables.getBigBlind());
            action = funkyRaiseNonShoveVsLimpsWithWeakerHands(action, boardInMethod, effectiveStack, botHandStrengthInMethod, botIsButtonInMethod, gameVariables.getBigBlind());
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
            String adjustedOppType = dbSaveRaw.getAdjustedOppTypeLogic(gameVariables.getOpponentName());
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
            dbSaveRaw.setStake(String.valueOf(continuousTable.getLastBuyIn()));
            dbSaveRaw.setOpponentName(gameVariables.getOpponentName());
            dbSaveRaw.setOpponentData(opponentData);
            dbSaveRaw.setBigBlind(gameVariables.getBigBlind());
            dbSaveRaw.setStrongDraw(strongDrawString);
            dbSaveRaw.setRecentHandsWon(recentHandsWon);
            dbSaveRaw.setAdjustedOppType(adjustedOppType);
            dbSaveRaw.setPot(gameVariables.getPot());
            dbSaveRaw.setEquity(botEquity);

            continuousTable.getDbSaveList().add(dbSaveRaw);
            //DbSaveRaw

            //DbSavePreflopStats
            if(boardInMethod == null || boardInMethod.isEmpty()) {
                DbSavePreflopStats dbSavePreflopStats = new DbSavePreflopStats();

                double pre2betCount = dbSavePreflopStats.getPreXbetCountLogic(
                        gameVariables.getOpponentAction(), gameVariables.getBoard(), gameVariables.getOpponentBetSize(),
                        gameVariables.getBigBlind(), "pre2bet");
                double pre3betCount = dbSavePreflopStats.getPreXbetCountLogic(
                        gameVariables.getOpponentAction(), gameVariables.getBoard(), gameVariables.getOpponentBetSize(),
                        gameVariables.getBigBlind(), "pre3bet");
                double pre4bet_up_count = dbSavePreflopStats.getPreXbetCountLogic(
                        gameVariables.getOpponentAction(), gameVariables.getBoard(), gameVariables.getOpponentBetSize(),
                        gameVariables.getBigBlind(), "pre4bet_up");
                double preTotalCount = dbSavePreflopStats.getPreTotalCountLogic(gameVariables.getOpponentAction(), gameVariables.getBoard());

                dbSavePreflopStats.setOpponentName(gameVariables.getOpponentName());
                dbSavePreflopStats.setOppPre2betCount(pre2betCount);
                dbSavePreflopStats.setOppPre3betCount(pre3betCount);
                dbSavePreflopStats.setOppPre4bet_up_count(pre4bet_up_count);
                dbSavePreflopStats.setOppPreTotalCount(preTotalCount);

                if(action.equals("raise")) {
                    dbSavePreflopStats.setOppPreCallTotalCount(1);

                    if(sizing / gameVariables.getBigBlind() > 10) {
                        continuousTable.setBotDidPre4bet(true);
                    }
                }

                continuousTable.getDbSaveList().add(dbSavePreflopStats);
            } else if(boardInMethod.size() == 3) {
                List<ActionRequest> allActionRequestsOfHand = gameVariables.getAllActionRequestsOfHand();
                ActionRequest secondLastActionRequest = allActionRequestsOfHand.get(allActionRequestsOfHand.size() - 2);
                PlayerActionRound botLastActionRound = secondLastActionRequest.getMostRecentActionRoundOfPLayer(secondLastActionRequest.getActionsSinceLastRequest(), "bot");
                List<Card> previousBoard = botLastActionRound.getBoard();

                if(previousBoard.isEmpty() && botLastActionRound.getAction().equals("raise")) {
                    DbSavePreflopStats dbSavePreflopStats = new DbSavePreflopStats();

                    double preCall2betCount = dbSavePreflopStats.getPreXbetCallCountLogic(
                            botLastActionRound.getTotalBotBetSize(), gameVariables.getBigBlind(), "preCall2bet");
                    double preCall3betCount = dbSavePreflopStats.getPreXbetCallCountLogic(
                            botLastActionRound.getTotalBotBetSize(), gameVariables.getBigBlind(), "preCall3bet");
                    double preCall4betUpCount = dbSavePreflopStats.getPreXbetCallCountLogic(
                            botLastActionRound.getTotalBotBetSize(), gameVariables.getBigBlind(), "preCall4bet_up");

                    dbSavePreflopStats.setOpponentName(gameVariables.getOpponentName());
                    dbSavePreflopStats.setOppPreCall2betCount(preCall2betCount);
                    dbSavePreflopStats.setOppPreCall3betCount(preCall3betCount);
                    dbSavePreflopStats.setOppPreCall4bet_up_count(preCall4betUpCount);

                    continuousTable.getDbSaveList().add(dbSavePreflopStats);
                }
            }
            //DbSavePreflopStats
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

    public void getSizingForAction(GameVariables gameVariables, String action, ContinuousTable continuousTable) {
        List<String> eligibleActions = getEligibleActions(gameVariables);
        boolean botIsButtonInMethod = gameVariables.isBotIsButton();
        double potSizeBb = gameVariables.getPot() / gameVariables.getBigBlind();
        double effectiveStack = getEffectiveStackInBb(gameVariables);
        double botHandStrengthInMethod = botHandStrength;
        boolean strongFdInMethod = strongFlushDraw;
        boolean strongOosdInMethod = strongOosd;
        boolean strongGutshotInMethod = strongGutshot;
        double botBetsizeBb = gameVariables.getBotBetSize() / gameVariables.getBigBlind();
        double opponentStackBb = gameVariables.getOpponentStack() / gameVariables.getBigBlind();
        double opponentBetsizeBb = gameVariables.getOpponentBetSize() / gameVariables.getBigBlind();
        double botStackBb = gameVariables.getBotStack() / gameVariables.getBigBlind();
        List<Card> boardInMethod = gameVariables.getBoard();

        Sizing sizingYo = new Sizing();
        double amountToCallBb = getAmountToCallBb(botBetsizeBb, opponentBetsizeBb, botStackBb);

        sizing = sizingYo.getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
        sizing = adjustRaiseSizingToSng(sizing, action, gameVariables, effectiveStack);

        System.out.println("method_sizing_0: " + sizing);

        double sizingBeforeNash = sizing;

        try {
            Nash nash = new Nash();
            boolean nashActionIsPossible = nash.nashActionIsPossible(effectiveStack, botIsButtonInMethod, botBetsizeBb,
                    boardInMethod, gameVariables.getOpponentAction(), gameVariables.getBotHoleCards(), opponentStackBb,
                    amountToCallBb);

            if(nashActionIsPossible) {
                if(action.equals("raise")) {
                    sizing = 5000 * gameVariables.getBigBlind();
                    System.out.println("Set Nash action raise sizing to shove: " + sizing);
                } else if(action.equals("call")) {
                    System.out.println("Gonna do Nash call!");
                }
            }
        } catch (Exception e) {
            System.out.println("Nash error!");
            System.out.println();
            e.printStackTrace();
            System.out.println();
            sizing = sizingBeforeNash;
        }

        System.out.println("method_sizing_1: " + sizing);

        adjustPfSizingAfterOppLimp(action, effectiveStack, boardInMethod, gameVariables.getOpponentAction(), botIsButtonInMethod, gameVariables.getBigBlind());

        if(gameVariables.getPot() == 2 * gameVariables.getBigBlind() && action.equals("bet75pct")) {
            sizing = gameVariables.getBigBlind();
        } else if(action.equals("bet75pct")) {
            if(boardInMethod != null && !boardInMethod.isEmpty() && boardInMethod.size() == 3 && !botIsButtonInMethod) {
                sizing = 0.35 * gameVariables.getPot();
            } else {
                sizing = 0.5 * gameVariables.getPot();
            }
        } else if(action.equals("raise") && boardInMethod != null && !boardInMethod.isEmpty()) {
            sizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard(), botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod);
        }

        System.out.println("method_sizing_2: " + sizing);

        if(!action.equals("bet75pct") && !action.equals("raise")) {
            sizing = 0;
        }

        shovePreflopWithAceHands(action, boardInMethod, gameVariables.getBotHoleCards(), effectiveStack,
                eligibleActions, gameVariables.getOpponentAction(), gameVariables.getBigBlind());
        shoveVersusLimpsWithStrongerHands(action, boardInMethod, gameVariables.getOpponentAction(), effectiveStack,
                gameVariables.getBigBlind(), botHandStrengthInMethod);
        System.out.println("method_sizing_3: " + sizing);
        funkyPreflopExtraShoves(action, boardInMethod, gameVariables.getOpponentAction(), gameVariables.getBigBlind(), botHandStrengthInMethod, effectiveStack);
        shoveWithWeaks(action, boardInMethod, gameVariables.getOpponentAction(), botHandStrengthInMethod, effectiveStack, botIsButtonInMethod, gameVariables.getBigBlind());
        raiseWithWeakVersusLimps(action, boardInMethod, botIsButtonInMethod, effectiveStack, botHandStrengthInMethod, gameVariables.getBigBlind());
        System.out.println("method_sizing_4: " + sizing);
        setShoveSizingForCertainPre3betsAndPostCheckRaises(action, botIsButtonInMethod, gameVariables.getOpponentAction(), boardInMethod, gameVariables.getBigBlind());
        nonShovePre3betWithPremiums(action, gameVariables.getBotHoleCards(), boardInMethod, botIsButtonInMethod,
                gameVariables.getOpponentAction(), gameVariables.getOpponentBetSize(), effectiveStack,
                gameVariables.getBotBetSize(), gameVariables.getBigBlind(), continuousTable);
        dontShoveBut3xRaiseVsLimpsDeep(action, effectiveStack, botIsButtonInMethod, boardInMethod, gameVariables.getOpponentAction(), gameVariables.getBigBlind());
        System.out.println("method_sizing_5: " + sizing);
        dontOpenShoveIpDeep(action, effectiveStack, botIsButtonInMethod, gameVariables.getOpponentAction(), gameVariables.getBigBlind());
        getValueFromPremiums(action, botHandStrengthInMethod, boardInMethod, botIsButtonInMethod, gameVariables.getOpponentAction(), effectiveStack, gameVariables.getBigBlind());
        System.out.println("method_sizing_6: " + sizing);
        adjustSizingForWetness(action, boardInMethod, botIsButtonInMethod, gameVariables.getPot(), gameVariables.getBigBlind(),
                botHandStrengthInMethod, strongFdInMethod, strongOosdInMethod, strongGutshotInMethod);
        adjustIpRiverBets(action, boardInMethod, botIsButtonInMethod, botHandStrengthInMethod, gameVariables.getPot(), potSizeBb);
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
            if(callBoundryProvidedHs != -1) {
                System.out.println("Provided call shizzle... pre 1");
                botHandStrength = callBoundryProvidedHs;
                botHasStrongDraw = false;
            } else {
                PreflopHandStength preflopHandStength = new PreflopHandStength();
                botHandStrength = preflopHandStength.getPreflopHandStength(gameVariables.getBotHoleCards());
                botHasStrongDraw = false;
            }
        } else {
            if(callBoundryProvidedHs != -1) {
                System.out.println("Provided call shizzle... post 2");
                if(callBoundryProvidedBoardEvaluator == null) {
                    boardEvaluator = new BoardEvaluator(gameVariables.getBoard());
                } else {
                    System.out.println("Provided call shizzle... post 3");
                    boardEvaluator = callBoundryProvidedBoardEvaluator;
                }

                handEvaluator = new HandEvaluator(gameVariables.getBotHoleCards(), boardEvaluator);
                botEquity = callBoundryProvidedHs;
                botHandStrength = callBoundryProvidedHs;
            } else {
                boardEvaluator = new BoardEvaluator(gameVariables.getBoard());
                handEvaluator = new HandEvaluator(gameVariables.getBotHoleCards(), boardEvaluator);

                double hsOld = handEvaluator.getHandStrength(gameVariables.getBotHoleCards());

                try {
                    botEquity = new EquityCalculator().getComboEquity(gameVariables.getBotHoleCards(), gameVariables.getBoard());
                } catch (Exception e) {
                    System.out.println("botEquity calculation error!");
                    e.printStackTrace();
                    botEquity = hsOld;
                }

                botHandStrength = handEvaluator.getHsNewStyle(botEquity, gameVariables.getBoard());

                System.out.println("O: " + hsOld);
                System.out.println("N: " + botHandStrength);
                System.out.println("DIFF: " + (botHandStrength - hsOld));
            }

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
                continuousTable.setFlopHandstrength(botHandStrength);
            } else if(boardInMethod.size() == 4) {
                continuousTable.setTop10percentTurnCombos(boardEvaluator.getTop10percentCombos());
                continuousTable.setTurnHandstrength(botHandStrength);
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

        if(opponentBetSize > (botStack + botBetSize)) {
            opponentBetSize = botStack + botBetSize;
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

            if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
                eligibleActions.add("raise");
            } else {
                eligibleActions.add("bet75pct");
            }
        }

        return eligibleActions;
    }

    private String doOpponentTypeDbLogic(String opponentName) throws Exception {
        OpponentIdentifier opponentIdentifier = new OpponentIdentifier();
        int numberOfHands = opponentIdentifier.getOpponentNumberOfHandsFromDb(opponentName);
        oppNumberOfHands = numberOfHands;

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

            //List<Double> handStrengthAtRiverList = equity.getHandstrengthAtRiverList(boardCopy, botHoleCardsCopy, 25);
            //List<Double> handStrengthAtRiverList = new ArrayList<>();

            //int numberOfScoresAbove90 = equity.getNumberOfScoresAboveLimit(handStrengthAtRiverList, 0.90);
            //numberOfScoresAbove80 = equity.getNumberOfScoresAboveLimit(handStrengthAtRiverList, 0.80);

            int numberOfScoresAbove90 = 0;
            numberOfScoresAbove80 = 0;

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

                        if((botStackBbAfterCall / potSizeBbAfterCall < 0.4) || (opponentStackBb / potSizeBbAfterCall < 0.4)) {
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

                if((botHandStrength >= 0.64 && (strongFd || strongOosd)) ||
                        ((strongFd && strongOosd) || (strongFd && strongGutshot))) {
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

    private String raiseFlopAndTurnWithStrongHand(String action, double handStrength, List<Card> board, double amountToCallBb,
                                                  double botStackBb, double oppStackBb) {
        String actionToReturn;

        if(action.equals("call")) {
            if(board != null && (board.size() == 3 || board.size() == 4)) {
                if(handStrength >= 0.94) {
                    if(oppStackBb > 0) {
                        if(botStackBb > amountToCallBb) {
                            actionToReturn = "raise";
                            System.out.println("Postflop value raise!");
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

    private String trickyCallWithMonstersOnFlopAndTurn(String action, boolean bluffOddsAreOk, List<Card> board, double handstrength) {
        String actionToReturn = action;

        if(board != null && !board.isEmpty()) {
            if(action.equals("raise")) {
                if(bluffOddsAreOk) {
                    if(handstrength > 0.94) {
                        if(board.size() == 3 || board.size() == 4) {
                            if(Math.random() > 0.65) {
                                actionToReturn = "call";
                                System.out.println("Postflop monster call instead of raise. Board size: " + board.size());
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String adjustPlayAgainstDonkbets(String action, List<Card> board, double potsizeBb, boolean position,
                                             double oppBetsizeBb, double handstrength, String opponentAction, double botBetSizeBb) {
        String actionToReturn = action;

        if(action.equals("fold") && opponentAction.equals("bet75pct") && botBetSizeBb == 0) {
            if(board != null && board.size() == 3) {
                if(position) {
                    if(potsizeBb == 2) {
                        if(oppBetsizeBb <= 2) {
                            System.out.println("Facing flop limped pot donkbet yo. HS: " + handstrength);
                            if(handstrength > 0.35) {
                                actionToReturn = "call";
                                System.out.println("Change fold against flop limped pot donkbet to call");
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String fewerBetsIpOnTurn(String action, List<Card> board, boolean position, double handstrength,
                                     boolean strongFd, boolean strongOosd, boolean strongGutshot) {
        //wat je ook kan doen is gewoon 40% hiervan maken...

        String actionToReturn = action;

        if(action.equals("bet75pct")) {
            if(board != null && board.size() == 4) {
                if(position) {
                    if(Math.random() < 0.235) {
                        actionToReturn = "check";
                        System.out.println("Change IP turn bet to check. HS: " + handstrength);
                    }

//                    if(handstrength < 0.6) {
//                        if(!strongFd && !strongOosd && !strongGutshot) {
//                            actionToReturn = "check";
//                            System.out.println("Change IP turn bet to check. HS: " + handstrength);
//                        } else {
//                            System.out.println("Kept IP turn bet because draw. FD: " + strongFd + " OOSD: " +
//                                    strongOosd + " Gutshot: " + strongGutshot + " HS: " + handstrength);
//                        }
//                    }
                }
            }
        }

        return actionToReturn;
    }

    private String moreIpRiverRaises(String action, List<Card> board, boolean position, String opponentAction, boolean bluffOddsAreOk, double handstrength) {
        String actionToReturn = action;

        if(board != null && board.size() == 5) {
            if(!action.equals("raise")) {
                if(position) {
                    if(opponentAction.equals("bet75pct")) {
                        if(bluffOddsAreOk) {
                            if(handstrength > 0.85) {
                                actionToReturn = "raise";
                                System.out.println("Extra river IP raise value. HS: " + handstrength);
                            } else if(action.equals("fold")) {
                                double random = Math.random();

                                if(random < 0.066) {
                                    actionToReturn = "raise";
                                    System.out.println("Extra river IP bluff raise. HS: " + handstrength + " random: " + random);
                                }
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String callLooseAgainst4betShovesPre(String action, List<Card> board, double handstrength, boolean position, double botBetsizeBb,
                                                 List<String> eligibleActions) {
        String actionToReturn = action;

        if(botBetsizeBb > 3) {
            if(action.equals("fold")) {
                if(board == null || board.isEmpty()) {
                    if(!position) {
                        if(handstrength >= 0.8) {
                            if(eligibleActions.contains("raise")) {
                                actionToReturn = "raise";
                                System.out.println("Facing pre4bet, change fold to raise. HS: " + handstrength);
                            } else {
                                actionToReturn = "call";
                                System.out.println("Facing pre4bet, change fold to call. HS: " + handstrength);
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private void getValueFromPremiums(String action, double handstrength, List<Card> board, boolean position,
                                      String opponentAction, double effStackBb, double bigBlind) {
        double initialSizing = sizing;

        if(board == null || board.isEmpty()) {
            if(handstrength > 0.95) {
                if(position) {
                    if(opponentAction.equals("bet")) {
                        if(action.equals("raise")) {
                            if(effStackBb > 10) {
                                if(Math.random() > 0.2) {
                                    sizing = 2 * bigBlind;
                                }
                            }
                        }
                    }
                } else {
                    if(opponentAction.equals("call")) {
                        if(action.equals("raise")) {
                            if(effStackBb > 10) {
                                if(Math.random() > 0.2) {
                                    sizing = 3 * bigBlind;
                                }
                            }
                        }
                    }
                }
            }
        }

        if(sizing != initialSizing) {
            String positionString;

            if(position) {
                positionString = "IP";
            } else {
                positionString = "OOP";
            }

            System.out.println(positionString + " non shove pre raise sizing with premium hand. Sizing change from: " + initialSizing + " to: " + sizing);
        }
    }

    private void possibilityToRaiseSmallOnRiver(String action, double handstrength, List<Card> board, List<String> eligibleActions) {
        if(board != null && board.size() == 5) {
            if(handstrength > 0.82) {
                if(action.equals("call") || action.equals("fold")) {
                    if(eligibleActions.contains("raise")) {
                        System.out.println("Extra small raise possible on river");
                    }
                }
            }
        }
    }

    private void adjustSizingForWetness(String action, List<Card> board, boolean position, double pot, double bigBlind, double handstrength,
                                                 boolean strongFd, boolean strongOosd, boolean strongGutshot) {
        if(board != null && (board.size() == 3 || board.size() == 4)) {
            if(action.equals("bet75pct")) {
                if(board.size() == 3) {
                    if(position) {
                        int drawWetness = boardEvaluator.getFlushStraightWetness();
                        System.out.println("wetness flop: " + drawWetness);

                        if(drawWetness <= 112) {
                            if(pot > (2 * bigBlind)) {
                                sizing = 0.35 * pot;
                                System.out.println("adjusted sizing flop small. Wetscore: " + drawWetness);
                            }
                        } else if(drawWetness >= 183) {
                            if(handstrength > 0.8 || (strongFd || strongOosd || strongGutshot)) {
                                sizing = 0.75 * pot;
                                System.out.println("adjusted sizing flop big. Wetscore: " + drawWetness + " HS: "
                                        + handstrength + " strongDraw: " + (strongFd || strongOosd || strongGutshot));
                            }
                        }
                    }
                } else {
                    int drawWetness = boardEvaluator.getFlushStraightWetness();
                    System.out.println("wetness turn: " + drawWetness);

                    if(drawWetness <= 182) {
                        if(pot > (2 * bigBlind)) {
                            sizing = 0.35 * pot;
                            System.out.println("adjusted sizing turn small. Wetscore: " + drawWetness);
                        }
                    } else if(drawWetness >= 467) {
                        if(handstrength > 0.8 || (strongFd || strongOosd)) {
                            sizing = 0.75 * pot;
                            System.out.println("adjusted sizing turn big. Wetscore: " + drawWetness + " HS: "
                                    + handstrength + " strongDraw: " + (strongFd || strongOosd || strongGutshot));
                        }
                    }
                }
            }
        }
    }

    private String adjustIpRiverBets(String action, List<Card> board, boolean position, double handstrength, double pot, double potsizeBb) {
        String actionToReturn = action;

        if(action.equals("bet75pct")) {
            if(board != null && board.size() == 5) {
                if(position) {
                    //                if(position) {
//                    if(potsizeBb >= 3) {
//                        sizing = 0.35 * pot;
//                        System.out.println("small river IP bet sizing 35pct");
//                    }

                    if(handstrength >= 0.5 && handstrength < 0.82) {
                        if(potsizeBb >= 3) {
                            sizing = 0.35 * pot;
                            System.out.println("River IP thin value bet: HS: " + handstrength);
                        }
                    } else {
                        if(potsizeBb >= 3) {
                            double random = Math.random();

                            if(random < 0.2) {
                                sizing = 0.35 * pot;

                                if(handstrength < 0.5) {
                                    System.out.println("River IP small bluff bet. HS: " + handstrength);
                                } else {
                                    System.out.println("River IP small value bet. HS: " + handstrength);
                                }
                            } else {
                                sizing = 0.75 * pot;

                                if(handstrength < 0.5) {
                                    System.out.println("River IP big bluff bet. HS: " + handstrength);
                                } else {
                                    System.out.println("River IP big value bet. HS: " + handstrength);
                                }
                            }
                        } else {
                            //nothing, keep half pot at 2bb pot.
                        }
                    }


//                    if(handstrength >= 0.5 && handstrength < 0.82) {
//                        actionToReturn = "check";
//                        System.out.println("River IP too thin value bet, change to check. HS: " + handstrength);
//                    } else {
//                        sizing = 0.75 * pot;
//
//                        if(handstrength < 0.5) {
//                            System.out.println("River IP big bluff bet. HS: " + handstrength);
//                        } else {
//                            System.out.println("River IP big value bet. HS: " + handstrength);
//                        }
//                    }
                }
            }
        }

        return actionToReturn;
    }

    private String raiseMoreAgainstRegsRiver(String action, List<Card> board, boolean bluffOddsAreOk, double oppNumberOfHands, ContinuousTable continuousTable) {
        String actionToReturn = action;

        if(action.equals("fold")) {
            if(board != null && board.size() == 5) {
                if(bluffOddsAreOk) {
                    if(oppNumberOfHands > 200) {
                        if(continuousTable.getBankroll() > continuousTable.getBankrollLimit20Nl()) {
                            if(Math.random() < 0.1) {
                                actionToReturn = "raise";
                                System.out.println("River bluff raise against reg!");
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String fuckingRaiseRivers(String action, List<Card> board, double handstrength, boolean bluffOddsAreOk, List<String> eligibleActions, String opponentAction, boolean position) {
        try {
            String actionToReturn = action;

            if(eligibleActions.contains("raise")) {
                if(board != null && board.size() == 5) {
                    if(opponentAction.equals("bet75pct")) {
                        if(action.equals("fold")) {
//                            if(bluffOddsAreOk) {
//                                //if(Math.random() < 0.163) {
//                                if(position) {
//                                    if(Math.random() < 0.205) {
//                                        actionToReturn = "raise";
//                                        System.out.println("River f-in bluff raise IP!");
//                                    }
//                                } else {
//                                    if(Math.random() < 0.125) {
//                                        actionToReturn = "raise";
//                                        System.out.println("River f-in bluff raise OOP!");
//                                    }
//                                }
//                            }
                        } else if(action.equals("call")) {
                            if(handstrength > 0.86) {
                                actionToReturn = "raise";
                                System.out.println("River f-in value raise! HS: " +handstrength);
                            }
                        }
                    }
                }
            }

            return actionToReturn;
        } catch (Exception e) {
            System.out.println("error in fuckingRaiseRivers()");
            e.printStackTrace();
            return action;
        }
    }

    private void shoveWayLessAgainstLimpsVsRegs(String action, List<Card> board, boolean position, String opponentAction, double effStackBb, double bigBlind) {
        if(action.equals("raise")) {
            if(board == null || board.isEmpty()) {
                if(!position) {
                    if(opponentAction.equals("call")) {
                        if(sizing > 500) {
                            if(Math.random() < 0.82) {
                                if(effStackBb >= 15) {
                                    sizing = 2.7 * bigBlind;
                                    System.out.println("Non shove but 2.7x vs limp");
                                } else if(effStackBb >= 10) {
                                    sizing = 2.35 * bigBlind;
                                    System.out.println("Non shove but 2.35x vs limp");
                                } else if(effStackBb > 4) {
                                    sizing = (2 * bigBlind) + 1;
                                    System.out.println("Non shove but 2x vs limp");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String funkyRaise2xInsteadOfShoveShallowVsRegs(String action, boolean position, List<Card> board, double opponentBetsizeBb, double effStackBb, double bigBlind) {
        try {
            String actionToReturn = action;

            if(position) {
                if(board == null || board.isEmpty()) {
                    if(opponentBetsizeBb == 1) {
                        if(effStackBb >= 4) {
                            if(action.equals("raise")) {
                                if(sizing >= 500) {
                                    if(Math.random() < 0.7) {
                                        if(effStackBb > 11) {
                                            sizing = (2 * bigBlind);
                                            System.out.println("A-funkyRaise2xInsteadOfShoveShallowVsRegs");
                                        } else {
                                            sizing = (2 * bigBlind) + 1;
                                            System.out.println("B-funkyRaise2xInsteadOfShoveShallowVsRegs");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return actionToReturn;
        } catch (Exception e) {
            System.out.println("Error in funkyRaise2xInsteadOfShoveShallowVsRegs()");
            e.printStackTrace();
            return action;
        }
    }

    private String fewerOpenFoldsVsRegs(String action, List<Card> board, boolean position, String opponentAction) {
        String actionToReturn = action;

        if(action.equals("fold")) {
            if(board == null || board.isEmpty()) {
                if(position) {
                    if(opponentAction.equals("bet")) {
                        if(Math.random() < 0.58) {
                            actionToReturn = "call";
                            System.out.println("Less openfolding vs regs -> limp");
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String funkyOpenRaisesWithWeakerHands(String action, List<Card> board, boolean position, double effStackBb, double handstrength, String opponentAction, double bigBlind) {
        String actionToReturn = action;

        if(action.equals("fold") || action.equals("call")) {
            if(board == null || board.isEmpty()) {
                if(position) {
                    if(opponentAction.equals("bet")) {
                        if(handstrength < 0.5) {
                            if(effStackBb >= 4) {
                                if(action.equals("call")) {
                                    if(Math.random() < 0.35) {
                                        actionToReturn = "raise";
                                        System.out.println("Openraise instead of limp with weak holding " + (effStackBb > 11));
                                    }
                                }

                                if(action.equals("fold")) {
                                    if(Math.random() < 0.35) {
                                        actionToReturn = "raise";
                                        System.out.println("Openraise instead of openfold with weak holding " + (effStackBb > 11));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if(!action.equals("raise") && actionToReturn.equals("raise")) {
            if(effStackBb > 11) {
                sizing = (2 * bigBlind);
            } else {
                sizing = (2 * bigBlind) + 1;
            }
        }

        return actionToReturn;
    }

    private String funkyRaiseNonShoveVsLimpsWithWeakerHands(String action, List<Card> board, double effStackBb, double handstrength, boolean position, double bigBlind) {
        String actionToReturn = action;

        if(action.equals("check")) {
            if(board == null || board.isEmpty()) {
                if(!position) {
                    if(effStackBb > 4) {
                        if(handstrength < 0.5) {
                            if(Math.random() < 0.18) {
                                actionToReturn = "raise";
                                System.out.println("Extra non shove bluff raise vs limp");

                                if(effStackBb >= 20) {
                                    sizing = 3 * bigBlind;
                                } else if(effStackBb >= 15) {
                                    sizing = 2.7 * bigBlind;
                                } else if(effStackBb >= 10) {
                                    sizing = 2.35 * bigBlind;
                                } else {
                                    sizing = (2 * bigBlind) + 1;
                                }
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String neverFoldAfter3betNotAllInPreVsRegs(String action, List<Card> board, boolean position, double botBetSizeBb) {
        try {
            String actionToReturn = action;

            if(action.equals("fold")) {
                if(board == null || board.isEmpty()) {
                    if(!position) {
                        if(botBetSizeBb >= 5) {
                            actionToReturn = "call";
                            System.out.println("Never fold after Oop pre3bet not allin, call");
                        }
                    }
                }
            }

            return actionToReturn;
        } catch (Exception e) {
            System.out.println("Error in neverFoldAfter3betNotAllInPre()");
            e.printStackTrace();
            return action;
        }
    }

    private String solidifyPostflopRaises(String action, List<Card> board, double handStrength,
                                          boolean strongFd, boolean strongOosd, ContinuousTable continuousTable,
                                          GameVariables gameVariables, double sizing) throws Exception {
        String actionToReturn;

        if(action.equals("raise")) {
            if(board != null && !board.isEmpty()) {
                if(board.size() == 3 || board.size() == 4) {
                    if(sizing < 300) {
                        actionToReturn = action;

                        if(handStrength < 0.6) {
                            System.out.println("Kept small flop or turn bluffraise as is");
                        }
                    } else {
                        if(handStrength < 0.83) {
                            if (strongFd || strongOosd) {
                                actionToReturn = action;
                                System.out.println("Keep flop or turn raise cause strong draw!");
                            } else {
                                System.out.println("Change flop or turn spew raise to fold or call!");
                                actionToReturn = getDummyActionOppAllIn(continuousTable, gameVariables);
                            }
                        } else {
                            actionToReturn = action;
                        }
                    }
                } else {
                    if(sizing < 300) {
                        actionToReturn = action;

                        if(handStrength < 0.6) {
                            System.out.println("Kept small river bluffraise as is");
                        }
                    } else {
                        if(handStrength < 0.78) {
                            System.out.println("Change river big spew raise to fold or call!");
                            actionToReturn = getDummyActionOppAllIn(continuousTable, gameVariables);
                        } else {
                            actionToReturn = action;
                        }
                    }
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
                        if(effectiveStackBb <= 65) {
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
                        if(gameVariables.getBotBetSize() < 2 * gameVariables.getBigBlind()) {
                            //you did limp
                            if(gameVariables.getBigBlind() < 40) {
                                sngSizingToReturn = currentSizing;
                                System.out.println("Sizing for preflop limp facing raise and then 3bet...");
                            } else {
                                sngSizingToReturn = 5000 * gameVariables.getBigBlind();
                                System.out.println("bigblind above 40...");
                            }
                        } else {
                            //4bet
                            sngSizingToReturn = 5000 * gameVariables.getBigBlind();
                            System.out.println("Change pre4bet sizing to shove in adjustRaiseSizingToSng(). Q");
                        }
                    } else {
                        sngSizingToReturn = currentSizing;
                    }
                }
            } else {
                //postflop
                if(currentSizing > 700 || effectiveStackBb <= 12) {
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

    private String preventTooThinValueRaises(String action, double handstrength, List<Card> board,
                                             boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot,
                                             ContinuousTable continuousTable, GameVariables gameVariables) throws Exception {
        String actionToReturn;

        if(action.equals("raise")) {
            if(board != null && !board.isEmpty()) {
                if(!strongFlushDraw && !strongOosd && !strongGutshot) {
                    if(handstrength >= 0.74 && handstrength < 0.9) {
                        System.out.println("Change postflop too thin value raise...");
                        actionToReturn = getDummyActionOppAllIn(continuousTable, gameVariables);
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

    private String doValueBet(String action, boolean opponentHasInitiative, double handstrength, List<Card> board, boolean position) {
        String actionToReturn;

        if(action.equals("check")) {
            if(!opponentHasInitiative) {
                if(board != null && !board.isEmpty()) {
                    if(handstrength > 0.83) {
                        if(board.size() == 3 || board.size() == 4) {
                            double random = Math.random();

                            if(random > 0.1) {
                                actionToReturn = "bet75pct";
                                System.out.println("Flop or Turn value bet");
                            } else {
                                actionToReturn = action;
                                System.out.println("Flop or Turn kept trapping check with good hand");
                            }
                        } else {
                            if(position) {
                                actionToReturn = action;
                            } else {
                                if(handstrength >= 0.9) {
                                    double random = Math.random();

                                    if(random > 0.1) {
                                        actionToReturn = "bet75pct";
                                        System.out.println("River value bet");
                                    } else {
                                        actionToReturn = action;
                                        System.out.println("River kept trapping check with good hand");
                                    }
                                } else {
                                    actionToReturn = action;
                                }
                            }
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

    private String preventManyBluffsJudgeByBoard(String action, double handstrength, double boardWetness, List<Card> board,
                                                 boolean strongOosd, boolean strongFd, boolean strongGutshot, ContinuousTable continuousTable,
                                                 GameVariables gameVariables) throws Exception {
        String actionToReturn;

        if(board != null && !board.isEmpty()) {
            if(action.equals("bet75pct") || action.equals("raise")) {
                if(handstrength < 0.7) {
                    if(!strongOosd && !strongFd && !strongGutshot && numberOfScoresAbove80 < 4) {
                        if(board.size() == 3) {
                            int flopDryness = boardEvaluator.getFlopDryness();

                            if(flopDryness > 195) {
                                if(action.equals("bet75pct")) {
                                    actionToReturn = "check";
                                    System.out.println("avg Change flop bluff bet on non favourable board to check");
                                } else {
                                    System.out.print("avg Change flop bluff raise on non favourable board to fold or call");
                                    actionToReturn = getDummyActionOppAllIn(continuousTable, gameVariables);
                                }
                            } else {
                                actionToReturn = action;
                                System.out.println("maintained bluff action");
                            }
                        } else {
                            if(boardWetness > 80) {
                                if(action.equals("bet75pct")) {
                                    actionToReturn = "check";
                                    System.out.println("avg Change turn/river bluff bet on non favourable board to check " + board.size());
                                } else {
                                    System.out.print("avg Change turn/river bluff raise on non favourable board to fold or call " + board.size());
                                    actionToReturn = getDummyActionOppAllIn(continuousTable, gameVariables);
                                }
                            } else {
                                actionToReturn = action;
                                System.out.println("maintained bluff action");
                            }
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

    private void adjustPfSizingAfterOppLimp(String action, double effStackBb, List<Card> board, String oppAction, boolean position, double bigBlind) {
        if(board == null || board.isEmpty()) {
            if(effStackBb < 11) {
                if(!position) {
                    if(oppAction.equals("call")) {
                        if(action.equals("raise")) {
                            if(sizing < 1000) {
                                System.out.println("Change pf sizing to shove after opplimp shortstack. Current sizing: " + sizing);
                                sizing = 5000 * bigBlind;
                            }
                        }
                    }
                }
            }
        }
    }

    private String shovePreflopWithAceHands(String action, List<Card> board, List<Card> botHolecards, double effectiveStackBb,
                                          List<String> eligibleActions, String opponentAction, double bigBlind) {
        String actionToReturn = action;

        if(board == null || board.isEmpty()) {
            if(!opponentAction.equals("raise")) {
                if(eligibleActions.contains("raise")) {
                    if(effectiveStackBb <= 20) {
                        List<Set<Card>> shovableHands = new PreflopHandStength().getSpecificPreflopShovableHands();
                        Set<Card> botHolecardsAsSet = botHolecards.stream().collect(Collectors.toSet());

                        if(shovableHands.contains(botHolecardsAsSet)) {
                            if(Math.random() < 0.5) {
                                String variable;

                                if(action.equals("raise")) {
                                    if(sizing > 500) {
                                        variable = "a";
                                    } else {
                                        variable = "b";
                                    }
                                } else {
                                    variable = "c";
                                }

                                System.out.println(variable + " gonna do shove with shovable hands!");
                                actionToReturn = "raise";
                                sizing = 5000 * bigBlind;
                                System.out.println("shovablehands set shove sizing: " + sizing);
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String shoveVersusLimpsWithStrongerHands(String action, List<Card> board, String opponentAction,
                                                     double effectiveStackBb, double bigBlind, double handstrength) {
        String actionToReturn = action;

        if(board == null || board.isEmpty()) {
            if(opponentAction.equals("call")) {
                if(action.equals("check")) {
                    if(effectiveStackBb < 16.7) {
                        if(handstrength >= 0.4) {
                            if(Math.random() < 0.73) {
                                actionToReturn = "raise";
                                sizing = 5000 * bigBlind;
                                System.out.println("shove more versus limps");

                                if(effectiveStackBb >= 13) {
                                    System.out.println("kmk vs limp 13 - 16.7 bb shove");
                                }
                            }
                        }
                    }
                } else if(action.equals("raise")) {
                    if(sizing < 500) {
                        System.out.println("extra shove vs limp. Old sizing: " + sizing + " bigblind: " + bigBlind);
                        sizing = 5000 * bigBlind;
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String funkyPreflopExtraShoves(String action, List<Card> board, String opponentAction, double bigBlind,
                                           double handstrength, double effectiveStackBb) {
        String actionToReturn = action;

        if(board == null || board.isEmpty()) {
            if(!action.equals("raise")) {
                if(!opponentAction.equals("raise")) {
                    if(effectiveStackBb < 16.7) {
                        if(handstrength >= 0.5) {
                            if(Math.random() < 0.62) {
                                actionToReturn = "raise";
                                System.out.println("Funky extra preflop shove!");
                                sizing = 5000 * bigBlind;

                                if(effectiveStackBb >= 13) {
                                    System.out.println("kmk 13 - 16.7 bb shove");
                                }
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String limpWithPremiums(String action, List<Card> board, String opponentAction, double handstrength,
                                    double effectiveStackBb, boolean position) {
        String actionToReturn = action;

        if(board == null || board.isEmpty()) {
            if(action.equals("raise")) {
                if(position) {
                    if(opponentAction.equals("bet")) {
                        if(effectiveStackBb <= 10) {
                            if(handstrength >= 0.95) {
                                if(Math.random() < 0.7) {
                                    actionToReturn = "call";
                                    System.out.println("rrrt limp with superpremium! HS: " + handstrength);
                                }
                            } else if(handstrength >= 0.80) {
                                if(Math.random() < 0.37) {
                                    actionToReturn = "call";
                                    System.out.println("rrrt limp with premium! HS: " + handstrength);
                                }
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String shoveWithWeaks(String action, List<Card> board, String opponentAction, double handstrength,
                                  double effectiveStackBb, boolean position, double bigBlind) {
        String actionToReturn = action;

        if(board == null || board.isEmpty()) {
            if(action.equals("call")) {
                if(position) {
                    if(opponentAction.equals("bet")) {
                        if(effectiveStackBb <= 10) {
                            if(handstrength >= 0.45) {
                                actionToReturn = "raise";
                                System.out.println("rrrt shove with weak! HS: " + handstrength);
                                sizing = 5000 * bigBlind;
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String checkWithPremiumsVersusLimps(String action, List<Card> board, boolean position, String opponentAction,
                                                double handstrength, double effectiveStackBb) {
        String actionToReturn = action;

        if(board == null || board.isEmpty()) {
            if(!position) {
                if(action.equals("raise")) {
                    if(opponentAction.equals("call")) {
                        if(effectiveStackBb >= 13) {
                            if(handstrength > 0.8) {
                                if(Math.random() < 0.3) {
                                    actionToReturn = "check";
                                    System.out.println("nnbb check with premium versus limp deep");
                                }
                            }
                        } else {
                            if(handstrength > 0.95) {
                                if(Math.random() < 0.57) {
                                    actionToReturn = "check";
                                    System.out.println("nnbb check with superpremium versus limp shallow");
                                }
                            } else if(handstrength > 0.8) {
                                if(Math.random() < 0.31) {
                                    actionToReturn = "check";
                                    System.out.println("nnbb check with premium versus limp shallow");
                                }
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String raiseWithWeakVersusLimps(String action, List<Card> board, boolean position, double effectiveStackBb,
                                            double handstrength, double bigBlind) {
        String actionToReturn = action;

        if(board == null || board.isEmpty()) {
            if(!position) {
                if(action.equals("check")) {
                    if(effectiveStackBb >= 13) {
                        if(handstrength > 0.5) {
                            if(Math.random() < 0.42) {
                                actionToReturn = "raise";
                                System.out.println("nnbb raise with weak versus limp deep");
                                sizing = 5000 * bigBlind;
                            }
                        }
                    } else {
                        if(handstrength > 0.45) {
                            if(Math.random() < 0.8) {
                                actionToReturn = "raise";
                                System.out.println("nnbb raise with weak versus limp shallow");
                                sizing = 5000 * bigBlind;
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String call2betWithPremiumsPreOop(String action, List<Card> board, String opponentAction, boolean position,
                                              double handstrength) {
        String actionToReturn = action;

        if(board == null || board.isEmpty()) {
            if(!position) {
                if(action.equals("raise")) {
                    if(opponentAction.equals("raise")) {
                        if(handstrength > 0.95) {
                            if(Math.random() < 0.35) {
                                actionToReturn = "call";
                                System.out.println("Call with superpremium versus 2bet pre");
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String changeOpenFoldsToLimp(String action, String opponentAction, boolean position, List<Card> board, List<Card> botHoleCards) {
        String actionToReturn = action;

        if(action.equals("fold")) {
            if(position) {
                if(opponentAction.equals("bet")) {
                    if(board == null || board.isEmpty()) {
                        String combo = new DbSave().getComboLogic(botHoleCards);

                        if(combo.equals("92o") || combo.equals("72o") || combo.equals("43o") || combo.equals("42o")) {
                            if(Math.random() >= 0.83) {
                                actionToReturn = "call";
                                System.out.println("Change pre openfold to limp. New combo logic! Occasional bad combo call: " + combo);
                            }
                        } else {
                            actionToReturn = "call";
                            System.out.println("Change pre openfold to limp. New combo logic! Combo: " + combo);
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String preventFlopDonkBetsAfterCheckingVersusLimp(String action, List<Card> board, boolean position, double potSizeBb) {
        String actionToReturn = action;

        if(action.equals("bet75pct")) {
            if(!position) {
                if(board != null && !board.isEmpty()) {
                    if(board.size() == 3) {
                        if(potSizeBb == 2) {
                            actionToReturn = "check";
                            System.out.println("vxd Prevent flop donkbet after check versus limp pre");
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String raiseFlopsAndTurnsAndRivers(String action, List<Card> board, String opponentAction, double handstrength,
                                               boolean strongFd, boolean strongOosd, boolean strongGutshot, boolean bluffOddsAreOk, boolean position) {
        String actionToReturn = action;

        if(action.equals("call") || action.equals("fold")) {
            if(board != null && !board.isEmpty()) {
                if(opponentAction.equals("bet75pct")) {
                    if(bluffOddsAreOk) {
                        if(board.size() == 3 || board.size() == 4) {
                            if((handstrength > 0.82 && opponentAction.equals("bet75pct") || handstrength > 0.86 && opponentAction.equals("raise"))
                                    || strongFd || strongOosd || (strongGutshot && board.size() == 3)) {
                                if(Math.random() > 0.2) {
                                    actionToReturn = "raise";
                                    System.out.println("" + board.size() + "Doing kinky postflop raise! Action was: " + action + " HS: " + handstrength +
                                            " strongFd: " + strongFd + " strongOosd: " + strongOosd + " strongGutshot: " + strongGutshot);

                                    if(opponentAction.equals("raise")) {
                                        System.out.println("kinky raise versus opp raise!");
                                    }
                                } else {
                                    System.out.println("20% shizzle no kinky raise versus " + opponentAction);
                                }
                            } else if(handstrength > 0.6) {
                                if(board.size() == 3) {
                                    if(Math.random() > 0.66) {
                                        //actionToReturn = "raise";
                                        //System.out.println("" + board.size() + "Doing bluffy kinky flop postflop raise! Action was: " + action + " HS: " + handstrength +
                                        //        " strongFd: " + strongFd + " strongOosd: " + strongOosd + " strongGutshot: " + strongGutshot);
                                        System.out.println("Skip bluffy kinky flop postflop raise");
                                    }
                                }
                            }
                        } else {
                            if(position) {
                                if(handstrength > 0.8) {
                                    if(Math.random() > 0.2) {
                                        //actionToReturn = "raise";
                                        //System.out.println("Doing kinky river raise! Handstrength: " + handstrength);
                                        System.out.println("Skip kinky river postflop raise");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private void setShoveSizingForCertainPre3betsAndPostCheckRaises(String action, boolean position, String opponentAction,
                                                                    List<Card> board, double bigBlind) {
        if(sizing < 500) {
            if(action.equals("raise")) {
                if(board == null || board.isEmpty()) {
                    if(opponentAction.equals("raise")) {
                        if(position) {
                            System.out.println("mm Shove IP pre 3bet! Old sizing: " + sizing);
                            sizing = 5000 * bigBlind;
                        }
                    }
                } else {
                    System.out.println("Nope, we gaan dus niet shove raisen post! Board size: " + board.size());
                    //System.out.println("mm Shove postflop raise! Old sizing: " + sizing);
                    //sizing = 5000 * bigBlind;
                }
            }
        }
    }

    private void nonShovePre3betWithPremiums(String action, List<Card> botHoleCards, List<Card> board, boolean position,
                                             String opponentAction, double opponentBetSize, double effectiveStackBb, double botBetSize,
                                             double bigBlind, ContinuousTable continuousTable) {
        double effStackBoundry;

        if(continuousTable.getBankroll() < continuousTable.getBankrollLimit20Nl()) {
            effStackBoundry = 10;
        } else {
            effStackBoundry = 4;
        }

        if(board == null || board.isEmpty()) {
            if(action.equals("raise")) {
                if(opponentAction.equals("raise")) {
                    if(!position) {
                        if(botBetSize == bigBlind) {
                            System.out.println("potential low 3bet opp 1");
                            //if(opponentBetSize <= 60) {
                            if(effectiveStackBb > effStackBoundry) {
                                //System.out.println("potential low 3bet opp 2");
                                String combo = new DbSave().getComboLogic(botHoleCards);

                                if((continuousTable.getBankroll() >= continuousTable.getBankrollLimit20Nl()) || combo.equals("AA") || combo.equals("KK") || combo.equals("JJ") || combo.equals("TT")
                                        || combo.equals("AKs") || combo.equals("AKo") || combo.equals("AQs") || combo.equals("AQo")
                                        || combo.equals("AJs") || combo.equals("87s")) {
                                        if(Math.random() > 0.35) {
                                            sizing = 2.5 * opponentBetSize;
                                            //System.out.println("pre3bet non shove! With premium");
                                            System.out.println("pre3bet non shove!");
                                        } else {
                                            System.out.println("potential low 3bet but random below 0.35");
                                        }
                                    } else {
                                        System.out.println("potential low 3bet not desired combo");
                                    }
                                }
                            //}
                        }
                    }
                }
            }
        }
    }

    private String dontShoveBut3xRaiseVsLimpsDeep(String action, double effStackBb, boolean position, List<Card> board, String opponentAction, double bigBlind) {
        String actionToReturn = action;

        if(effStackBb >= 18) {
            if(board == null || board.isEmpty()) {
                if(!position) {
                    if(opponentAction.equals("call")) {
                        if(action.equals("raise")) {
                            if(effStackBb >= 20 || Math.random() > 0.22) {
                                System.out.println("18_25bb OOP raise vs limp, but non shove");
                                sizing = 3 * bigBlind;
                            } else {
                                System.out.println("Kept deep shove vs limp. Eff stack: " + effStackBb);
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private void dontOpenShoveIpDeep(String action, double effStackBb, boolean position, String opponentAction, double bigBlind) {
        if(opponentAction.equals("bet")) {
            if(action.equals("raise")) {
                if(position) {
                    if(sizing > 500) {
                        if(effStackBb >= 11) {
                            sizing = 2 * bigBlind;
                            System.out.println("Don't open shove IP deep. Bigblind: " + bigBlind + " EffStackBb: " + effStackBb);
                        }
                    }
                }
            }
        }
    }

    private String preventManyPostflopBets(String action, List<Card> board, boolean position, double handstrength, boolean strongFd, boolean strongSd) {
        String actionToReturn = action;

        if(action.equals("bet75pct")) {
            if(board != null && !board.isEmpty()) {
                if(position) {
                    if(board.size() == 3) {
                        if(handstrength < 0.7 && !strongFd && !strongSd) {
                            double random = Math.random();

                            if(random < 0.275) {
                                actionToReturn = "check";

                                if(random >= 0.1) {
                                    System.out.println("eije extra flop IP check");
                                }
                            }
                        }
                    } else if(board.size() == 4) {
                        if(handstrength < 0.7 && !strongFd && !strongSd) {
                            double random = Math.random();

                            if(random < 0.39) {
                                actionToReturn = "check";
                            }

                            if(random > 0.39 && random < 0.45) {
                                System.out.println("eije extra turn IP bet");
                            }
                        }
                    } else {
                        if(handstrength < 0.65) {
                            double random = Math.random();

                            if(random < 0.44) {
                                actionToReturn = "check";
                            }

                            if(random > 0.44 && random < 0.4875) {
                                System.out.println("eije extra river IP bet");
                            }
                        }
                    }
                } else {
                    if(board.size() == 3) {
                        actionToReturn = "check";
                    } else if(board.size() == 4) {
                        if(handstrength < 0.7) {
                            if(Math.random() < 0.86) {
                                actionToReturn = "check";
                            } else {
                                System.out.println("eije extra turn oop bluffbet");
                            }
                        }
                    } else {
                        if(handstrength < 0.7) {
                            double random = Math.random();

                            if(random < 0.52) {
                                actionToReturn = "check";
                            } else if(random < 0.65) {
                                System.out.println("eije extra river oop bluffbet");
                            }
                        }
                    }
                }
            }
        }

        if(action.equals("bet75pct") && actionToReturn.equals("check")) {
            System.out.println("prevent postbet. position: " + position + " Board size: " + board.size());
        }

        return actionToReturn;
    }

    private String preventManyPostflopRaises(String action, double handstrength, boolean strongFlushDraw, boolean strongOosd,
                                             List<Card> board, boolean position, ContinuousTable continuousTable, GameVariables gameVariables) throws Exception {
        String actionToReturn = action;

        if(action.equals("raise")) {
            if(board != null && !board.isEmpty()) {
                double limit;

                if(board.size() == 3 && !position) {
                    limit = 0.792;
                } else {
                    limit = 0.88;
                }

                if(handstrength < limit && !strongFlushDraw && !strongOosd) {
                    actionToReturn = getDummyActionOppAllIn(continuousTable, gameVariables);
                    System.out.println("prevent funky postflop raise. Board size: " + board.size() + " Position: " + position);
                }
            }
        }

         return actionToReturn;
    }

    private String callLooseAfterLimpVersusShoveUndeep(String action, double effectiveStackBb, boolean position,
                                                       double botBetSizeBb, String oppAction, double opponentStackBb,
                                                       double amountToCallBb, List<Card> botHolecards, double handstrength,
                                                       List<Card> board) {
        String actionToReturn = action;

        if(board == null || board.isEmpty()) {
            if(action.equals("fold")) {
                if(position) {
                    if(botBetSizeBb == 1) {
                        if(oppAction.equals("raise")) {
                            if(effectiveStackBb <= 12) {
                                if(opponentStackBb == 0 || (amountToCallBb >= effectiveStackBb)) {
                                    //use opposite position
                                    String nashAction = new Nash().doNashAction(botHolecards, !position, effectiveStackBb, amountToCallBb);

                                    if(nashAction.equals("call")) {
                                        actionToReturn = "call";
                                        System.out.println("tjek - Changed to limp Nash call! Shallow. Eff stack: " + effectiveStackBb);
                                    }
                                }
                            } else {
                                if(handstrength > 0.8) {
                                    actionToReturn = "call";
                                    System.out.println("tjek - Changed to call! Deep. HS: " + handstrength + " Eff stack: " + effectiveStackBb);
                                }
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private int getBoardWetness(ContinuousTable continuousTable, List<Card> board) {
        int boardWetness = 200;

        if(board != null && !board.isEmpty()) {
            if(board.size() == 4) {
                boardWetness = BoardEvaluator.getBoardWetness(continuousTable.getTop10percentFlopCombos(), continuousTable.getTop10percentTurnCombos());
            } else if(board.size() == 5) {
                boardWetness = BoardEvaluator.getBoardWetness(continuousTable.getTop10percentTurnCombos(), continuousTable.getTop10percentRiverCombos());
            }
        }

        return boardWetness;
    }

    private String preventAllBluffs(String action, double handstrength, List<Card> board, double sizing,
                                    ContinuousTable continuousTable, GameVariables gameVariables, boolean strongFd,
                                    boolean strongOosd, boolean strongGutshot) throws Exception {
        String actionToReturn;

        if(board != null && !board.isEmpty()) {
            if(action.equals("bet75pct") || action.equals("raise")) {
                if(handstrength < 0.8) {
                    double limit;

                    if(strongFd || strongOosd) {
                        limit = 0.71;
                    } else if(strongGutshot) {
                        limit = 0.32;
                    } else {
                        limit = 0;
                    }

                    if(sizing <= 120) {
                        if(handstrength >= 0.7) {
                            actionToReturn = action;
                        } else {
                            if(action.equals("bet75pct")) {
                                if(Math.random() >= limit) {
                                    actionToReturn = "check";
                                    System.out.println("prevent bluff A");
                                } else {
                                    actionToReturn = action;
                                }
                            } else {
                                System.out.println("prevent bluff B");
                                actionToReturn = getDummyActionOppAllIn(continuousTable, gameVariables);
                            }
                        }
                    } else {
                        if(action.equals("bet75pct")) {
                            if(Math.random() >= limit) {
                                actionToReturn = "check";
                                System.out.println("prevent bluff C");
                            } else {
                                actionToReturn = action;
                            }
                        } else {
                            System.out.println("prevent bluff D");
                            actionToReturn = getDummyActionOppAllIn(continuousTable, gameVariables);
                        }
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

    private boolean numberOfHandsIsBluffable(int numberOfHands) {
        boolean numberOfHandsIsBluffable = true;

        if((numberOfHands + 9) % 20 == 0 || (numberOfHands + 8) % 20 == 0 ||
                (numberOfHands + 7) % 20 == 0 || (numberOfHands + 6) % 20 == 0 ||
                (numberOfHands + 5) % 20 == 0 || (numberOfHands + 4) % 20 == 0 ||
                (numberOfHands + 3) % 20 == 0 || (numberOfHands + 2) % 20 == 0 ||
                (numberOfHands + 1) % 20 == 0 || (numberOfHands ) % 20 == 0) {

            numberOfHandsIsBluffable = false;
        }

        return numberOfHandsIsBluffable;
    }

    private String preventBadPostCalls(String action, double handstrength, boolean strongFd, boolean strongOosd,
                                       List<Card> board, double facingOdds) {
        String actionToReturn;

        if(action.equals("call")) {
            if(board != null && !board.isEmpty()) {
                if(handstrength < 0.6) {
                    if(!strongFd && !strongOosd) {
                        if(facingOdds > 0.2) {
                            actionToReturn = "fold";
                            System.out.println("Change bad postflop call to fold. Handstrength: " + handstrength);
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

    private String adjustPostFoldsToAggroness(String action, List<Card> board, double handstrength,
                                              double flopHandstrength, double turnHandstrength, double oppStackBb,
                                              double botStackBb, double botBetsizeBb, double potsizeBb,
                                              double amountToCallBb, double facingOdds, String oppName) throws Exception {
        String actionToReturn;

        if(action.equals("fold")) {
            if(board != null && !board.isEmpty()) {
                if(facingOdds < 0.448) {
                    if(handstrength >= 0.64 || (flopHandstrength >= 0.64 && handstrength > 0.45) || (turnHandstrength >= 0.64 && handstrength > 0.45)) {
                        double oppPostAggroness = new OpponentIdentifier2_0(oppName).getOppPostAggroness();

                        if(oppPostAggroness > 0.6) {
                            actionToReturn = "call";
                            System.out.println("Change postflop fold to call! " + " Flophs: " + flopHandstrength + " Turnhs: " + turnHandstrength + " currentHS: " + handstrength + " oppPostAggroness: " + oppPostAggroness);
                            actionToReturn = preventCallIfOpponentOrBotAlmostAllInAfterCall(actionToReturn, oppStackBb, botStackBb, botBetsizeBb, potsizeBb, amountToCallBb, board);
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

    private String callRules(String action, double botEquity, List<Card> board, double facingOdds, boolean strongDraw,
                             boolean position, boolean bluffOddsAreOk) {
        String actionToReturn;

        if(action.equals("call")) {
            if(board != null && !board.isEmpty()) {
                if(botEquity < 0.34 && !strongDraw) {
                    if(position && bluffOddsAreOk && (board.size() == 3 || board.size() == 4)) {
                        System.out.println("Keep floating, no overrule change call to fold");
                        actionToReturn = action;
                    } else {
                        if(facingOdds > 0.1) {
                            System.out.println("Overrule change call to fold. Equity: " + botEquity + " Facingodds: " + facingOdds);
                            actionToReturn = "fold";
                        } else {
                            if(botEquity < 0.1) {
                                System.out.println("Nut low: do fold despite amazing odds. Equity: " + botEquity + " Facingodds: " + facingOdds);
                                actionToReturn = "fold";
                            } else {
                                System.out.println("Kept call cause of incredibly good odds. Equity: " + botEquity + " Facingodds: " + facingOdds);
                                actionToReturn = action;
                            }
                        }
                    }
                } else {
                    actionToReturn = action;

                    if(botEquity < 0.53 && !strongDraw) {
                        System.out.println("No overrule call to fold. Equity: " + botEquity + " Facingodds: " + facingOdds);
                    }
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    private String preventPreRaiseIfBotStackBelow1bb(String action, double bigBlind, double botBetSize, double botStack, List<Card> board) {
        String actionToReturn = action;

        if(action.equals("raise")) {
            if(board == null || board.isEmpty()) {
                if(bigBlind > (botBetSize + botStack)) {
                    System.out.println("preflop raise impossible because super low stack below 1bb, change to call");
                    actionToReturn = "call";
                }
            }
        }

        return actionToReturn;
    }

    private String preventTooLooseCallsVersusShovesPre(String action, List<Card> board, double oppStack, double botBetSize,
                                                       double bigBlind, double handstrength, String opponentAction, double amountToCallBb) {
        String actionToReturn = action;

        if(action.equals("call")) {
            if(amountToCallBb > 3) {
                if(board == null || board.isEmpty()) {
                    if(opponentAction != null && opponentAction.equals("raise")) {
                        if(oppStack == 0) {
                            System.out.println("zerker1");

                            double botBetSizeBb = botBetSize / bigBlind;

                            if(botBetSizeBb <= 1) {
                                System.out.println("zerker2");
                                if(handstrength < 0.45) {
                                    actionToReturn = "fold";
                                    System.out.println("zerker3 change pre call vs shove to fold, because too weak hand and above 3bb to call");
                                }
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    public double determineMinimumHsToCall(GameVariables gameVariables, ContinuousTable continuousTable, double trueHandstrength,
                                           String currentAction) throws Exception {
        System.out.println("Start call boundary method");
        long startTime = new Date().getTime();

        double lowBoundry = 0;
        double highBoundry = 1;
        double hsAttempt = -1;

        for(int i = 0; i < 5; i++) {
            hsAttempt = (lowBoundry + highBoundry) / 2;
            callBoundryProvidedHs = hsAttempt;

            ActionVariables actionVariables = new ActionVariables(gameVariables, continuousTable, false);
            callBoundryProvidedBoardEvaluator = actionVariables.getBoardEvaluator();
            String action = actionVariables.getAction();

            if(action.equals("fold")) {
                lowBoundry = hsAttempt;
            } else if(action.equals("call") || action.equals("raise")) {
                highBoundry = hsAttempt;
            }
        }

        hsAttempt = (lowBoundry + highBoundry) / 2;

        System.out.println();
        System.out.println();
        System.out.println("Call boundry is something like: " + hsAttempt);
        System.out.println("up: " + highBoundry);
        System.out.println("low: " + lowBoundry);
        System.out.println("True hs: " + trueHandstrength);
        System.out.println();

        if(currentAction.equals("fold") && trueHandstrength >= hsAttempt) {
            System.out.println("HS above callboundary but still fold...");
        }

        callBoundryProvidedHs = -1;
        callBoundryProvidedBoardEvaluator = null;

        long endTime = new Date().getTime();
        long duration = endTime - startTime;
        System.out.println("Method duration: " + duration);
        System.out.println("End call boundary method");

        return hsAttempt;
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

    public BoardEvaluator getBoardEvaluator() {
        return boardEvaluator;
    }

    public void setBoardEvaluator(BoardEvaluator boardEvaluator) {
        this.boardEvaluator = boardEvaluator;
    }

    public int getOppNumberOfHands() {
        return oppNumberOfHands;
    }

    public static double getCallBoundryProvidedHs() {
        return callBoundryProvidedHs;
    }
}
