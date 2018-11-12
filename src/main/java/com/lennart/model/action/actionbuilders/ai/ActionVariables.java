package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.foldstats.AdjustToFoldStats;
import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
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

    public ActionVariables(GameVariables gameVariables, ContinuousTable continuousTable, boolean newHand) throws Exception {
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

        setOpponentHasInitiative(opponentActionInMethod, continuousTable, gameVariables);
        setOpponentDidPostflopFlopOrTurnRaiseOrOverbet(opponentActionInMethod, boardInMethod, continuousTable, opponentBetsizeBb, potSizeBb);
        double amountToCallBb = getAmountToCallBb(botBetsizeBb, opponentBetsizeBb, botStackBb);

        //int boardWetness = getBoardWetness(continuousTable);
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

            if(botFoldStat > 0.43) {
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

            if(boardInMethod != null && boardInMethod.size() >=3 && !defaultCheck) {
                rangeTracker.updateRangeMapInDbSimple(action, sizing, gameVariables.getBigBlind(), botIsButtonInMethod, botHandStrengthInMethod, boardInMethod);
                rangeTracker.updateRangeMapInDbExtensive(action, sizing, gameVariables.getBigBlind(), botIsButtonInMethod, botHandStrengthInMethod, boardInMethod, drawWetness, boatWetness);
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

            if((action.equals("bet75pct") || action.equals("raise")) && sizing == 0) {
                sizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard());
            }
        }

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

    private double getFacingOdds(GameVariables gameVariables) {
        double opponentBetSize = gameVariables.getOpponentBetSize();
        double botBetSize = gameVariables.getBotBetSize();
        double botStack = gameVariables.getBotStack();

        if((opponentBetSize - botBetSize) > botStack) {
            opponentBetSize = botStack;
        }

        double facingOdds = (opponentBetSize - botBetSize) / (gameVariables.getPot() + botBetSize + opponentBetSize);
        return facingOdds;
    }

    private String getStreet(GameVariables gameVariables) {
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
            boardWetnessRiver = BoardEvaluator.getBoardWetnessGroup(top10PercentTurnCombosCopy, top10PercentRiverCombosCopy);
        }

        if(!continuousTable.getTop10percentTurnCombos().isEmpty()) {
            boardWetnessTurn = BoardEvaluator.getBoardWetnessGroup(top10PercentFlopCombosCopy, top10PercentTurnCombosCopy);
        }

        if(boardWetnessRiver < boardWetnessTurn) {
            boardWetnessToReturn = boardWetnessRiver;
        } else {
            boardWetnessToReturn = boardWetnessTurn;
        }

        System.out.println("BoardWetness: " + boardWetnessToReturn);

        return boardWetnessToReturn;
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
