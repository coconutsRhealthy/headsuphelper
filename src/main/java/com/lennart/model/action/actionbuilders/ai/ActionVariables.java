package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.foldstats.AdjustToFoldStats;
import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.action.actionbuilders.preflop.PreflopActionBuilder;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.handevaluation.PreflopHandStength;

import java.util.ArrayList;
import java.util.List;

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

    public ActionVariables() {
        //default constructor
    }

    public ActionVariables(GameVariables gameVariables, ContinuousTable continuousTable) {
        calculateHandStrengthAndDraws(gameVariables);

        List<String> eligibleActions = getEligibleActions(gameVariables);
        String streetInMethod = getStreet(gameVariables);
        boolean botIsButtonInMethod = gameVariables.isBotIsButton();
        double potSizeBb = gameVariables.getPot() / gameVariables.getBigBlind();
        String opponentActionInMethod = gameVariables.getOpponentAction();
        double facingOdds = getFacingOdds(gameVariables);
        double effectiveStack = getEffectiveStackInBb(gameVariables);
        boolean botHasStrongDrawInMethod = botHasStrongDraw;
        double botHandStrengthInMethod = botHandStrength;
        opponentType = doOpponentTypeLogic(gameVariables.getOpponentName());
        double opponentBetsizeBb = gameVariables.getOpponentBetSize() / gameVariables.getBigBlind();
        double botBetsizeBb = gameVariables.getBotBetSize() / gameVariables.getBigBlind();
        double opponentStackBb = gameVariables.getOpponentStack() / gameVariables.getBigBlind();
        double botStackBb = gameVariables.getBotStack() / gameVariables.getBigBlind();
        boolean preflop = gameVariables.getBoard().isEmpty();
        List<Card> boardInMethod = gameVariables.getBoard();

        setOpponentHasInitiative(opponentActionInMethod, continuousTable);
        setOpponentDidPostflopFlopOrTurnRaiseOrOverbet(opponentActionInMethod, boardInMethod, continuousTable, opponentBetsizeBb, potSizeBb);
        double amountToCallBb = getAmountToCallBb(botBetsizeBb, opponentBetsizeBb, botStackBb);

        if(preflop) {
            action = new PreflopActionBuilder().getAction(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getOpponentStack(), gameVariables.getBigBlind(), gameVariables.getBotHoleCards(), gameVariables.isBotIsButton(), continuousTable, opponentType, amountToCallBb);

            if(action.equals("raise")) {
                sizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard());
            }
        } else {
            if(continuousTable != null && (continuousTable.isOpponentHasInitiative() && opponentActionInMethod.equals("empty"))) {
                System.out.println("default check, opponent has initiative");
                action = "check";
            } else {
                //hier de equity logic
                doEquityLogic(boardInMethod, gameVariables.getBotHoleCards());

                String actionAgainstLa = new Poker().getAction(this, eligibleActions, streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod, facingOdds, effectiveStack, botHasStrongDrawInMethod, botHandStrengthInMethod, "la", opponentBetsizeBb, botBetsizeBb, opponentStackBb, botStackBb, preflop, boardInMethod, strongFlushDraw, strongOosd, strongGutshot, gameVariables.getBigBlind(), continuousTable.isOpponentDidPreflop4betPot(), continuousTable.isPre3betOrPostRaisedPot(), strongOvercards, strongBackdoorFd, strongBackdoorSd, 0);

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
                        action = new Poker().getAction(this, eligibleActions, streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod, facingOdds, effectiveStack, botHasStrongDrawInMethod, botHandStrengthInMethod, opponentType, opponentBetsizeBb, botBetsizeBb, opponentStackBb, botStackBb, preflop, boardInMethod, strongFlushDraw, strongOosd, strongGutshot, gameVariables.getBigBlind(), continuousTable.isOpponentDidPreflop4betPot(), continuousTable.isPre3betOrPostRaisedPot(), strongOvercards, strongBackdoorFd, strongBackdoorSd, 0);
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

//        action = adjustToFoldStats.adjustPlayToBotFoldStatRaise(action, botHandStrengthInMethod,
//                opponentBetsizeBb * gameVariables.getBigBlind(), botBetsizeBb * gameVariables.getBigBlind(),
//                botStackBb, opponentStackBb, potSizeBb * gameVariables.getBigBlind(), gameVariables.getBigBlind(),
//                boardInMethod, strongFlushDraw, strongOosd, strongGutshot, gameVariables.getOpponentName());

        if(action.equals("fold")) {
            double botFoldStat = FoldStatsKeeper.getFoldStat("bot-V-" + gameVariables.getOpponentName());

            if(botFoldStat > 0.43) {
                double handStrengthRequiredToCall = adjustToFoldStats.getHandStrengthRequiredToCall(this, eligibleActions,
                        streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod, facingOdds, effectiveStack,
                        botHasStrongDrawInMethod, botHandStrengthInMethod, opponentType, opponentBetsizeBb, botBetsizeBb,
                        opponentStackBb, botStackBb, preflop, boardInMethod, strongFlushDraw, strongOosd, strongGutshot,
                        gameVariables.getBigBlind(), continuousTable.isOpponentDidPreflop4betPot(),
                        continuousTable.isPre3betOrPostRaisedPot(), false, false, false, 0);

                action = adjustToFoldStats.adjustPlayToBotFoldStat(action, botHandStrengthInMethod, handStrengthRequiredToCall, gameVariables.getBotHoleCards(), boardInMethod, botIsButtonInMethod, gameVariables.getOpponentName());

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

//        if(action.equals("check") || action.equals("fold")) {
//            double bigBlind = gameVariables.getBigBlind();
//
////            action = adjustToFoldStats.adjustPlayToOpponentFoldStat(action,
////                    continuousTable.isOpponentHasInitiative(),
////                    opponentBetsizeBb * bigBlind,
////                    botBetsizeBb * bigBlind,
////                    botStackBb * bigBlind,
////                    opponentStackBb * bigBlind,
////                    potSizeBb * bigBlind,
////                    bigBlind,
////                    boardInMethod,
////                    gameVariables.getOpponentName(),
////                    botHandStrengthInMethod,
////                    strongOosd,
////                    strongFlushDraw,
////                    strongGutshot,
////                    strongOvercards,
////                    strongBackdoorSd,
////                    strongBackdoorFd,
////                    40,
////                    opponentActionInMethod);
//        }

//        if((action.equals("bet75pct") || action.equals("raise")) && sizing == 0) {
//            sizing = new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(), gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(), gameVariables.getBoard());
//        }
    }

    private void setOpponentHasInitiative(String opponentAction, ContinuousTable continuousTable) {
        if(continuousTable != null) {
            if(opponentAction != null && !opponentAction.equals("empty")) {
                if(opponentAction.equals("bet75pct") || opponentAction.equals("raise")) {
                    continuousTable.setOpponentHasInitiative(true);
                } else {
                    continuousTable.setOpponentHasInitiative(false);
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
               if(opponentBetsizeBb > potSizeBb) {
                   continuousTable.setPre3betOrPostRaisedPot(true);
               }
           }
       }
    }

    private void calculateHandStrengthAndDraws(GameVariables gameVariables) {
        if(gameVariables.getBoard().isEmpty()) {
            PreflopHandStength preflopHandStength = new PreflopHandStength();
            botHandStrength = preflopHandStength.getPreflopHandStength(gameVariables.getBotHoleCards());
            botHasStrongDraw = false;
        } else {
            BoardEvaluator boardEvaluator = new BoardEvaluator(gameVariables.getBoard());
            handEvaluator = new HandEvaluator(gameVariables.getBotHoleCards(), boardEvaluator);
            botHandStrength = handEvaluator.getHandStrength(gameVariables.getBotHoleCards());

            strongFlushDraw = handEvaluator.hasDrawOfType("strongFlushDraw");
            strongOosd = handEvaluator.hasDrawOfType("strongOosd");
            strongGutshot = handEvaluator.hasDrawOfType("strongGutshot");
            strongOvercards = handEvaluator.hasDrawOfType("strongOvercards");
            strongBackdoorFd = handEvaluator.hasDrawOfType("strongBackDoorFlush");
            strongBackdoorSd = handEvaluator.hasDrawOfType("strongBackDoorStraight");

            botHasStrongDraw = strongFlushDraw || strongOosd || strongGutshot || strongBackdoorFd || (strongBackdoorSd && Math.random() < 0.5);
        }
    }

    private double getEffectiveStackInBb(GameVariables gameVariables) {
        if(gameVariables.getBotStack() > gameVariables.getOpponentStack()) {
            return gameVariables.getOpponentStack() / gameVariables.getBigBlind();
        }
        return gameVariables.getBotStack() / gameVariables.getBigBlind();
    }

    private double getFacingOdds(GameVariables gameVariables) {
        double facingOdds = (gameVariables.getOpponentBetSize() - gameVariables.getBotBetSize())
                / (gameVariables.getPot() + gameVariables.getBotBetSize() + gameVariables.getOpponentBetSize());
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

    private String doOpponentTypeLogic(String opponentName) {
        if(OpponentIdentifier.getNumberOfHandsPerOpponentMap().get(opponentName) == null) {
            OpponentIdentifier.updateNumberOfHandsPerOpponentMap(opponentName);
        }

        return new OpponentIdentifier().getOpponentType(opponentName,
                OpponentIdentifier.getNumberOfHandsPerOpponentMap().get(opponentName));
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
                if(numberOfScoresAbove90 >= 4) {
                    botHasStrongDraw = true;
                    strongGutshot = true;
                } else if(numberOfScoresAbove90 >= 7) {
                    botHasStrongDraw = true;
                    strongOosd = true;
                } else if(numberOfScoresAbove90 >= 8) {
                    botHasStrongDraw = true;
                    strongFlushDraw = true;
                }
            } else {
                if(numberOfScoresAbove90 >= 2) {
                    botHasStrongDraw = true;
                    strongGutshot = true;
                } else if(numberOfScoresAbove90 >= 4) {
                    botHasStrongDraw = true;
                    strongOosd = true;
                } else if(numberOfScoresAbove90 >= 5) {
                    botHasStrongDraw = true;
                    strongFlushDraw = true;
                }
            }
        }
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
}
