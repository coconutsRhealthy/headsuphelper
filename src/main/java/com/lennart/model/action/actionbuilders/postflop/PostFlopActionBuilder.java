package com.lennart.model.action.actionbuilders.postflop;

import com.lennart.model.action.Actionable;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.card.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LPO21630 on 2-12-2016.
 */
public class PostFlopActionBuilder {

    private final String FOLD = "fold";
    private final String CHECK = "check";
    private final String BET = "bet";
    private final String CALL = "call";
    private final String RAISE = "raise";

    private double bigBlind;
    private List<Card> board;
    private double sizing;
    private double potSize;

    private BoardEvaluator boardEvaluator;
    private HandEvaluator handEvaluator;
    private Actionable actionable;

    private double handStrength;

    private String opponentType;

    Map<Integer, Double> tightPassiveBet;
    Map<Integer, Double> tightMediumBet;
    Map<Integer, Double> tightAggressiveBet;
    Map<Integer, Double> mediumPassiveBet;
    Map<Integer, Double> mediumMediumBet;
    Map<Integer, Double> mediumAggressiveBet;
    Map<Integer, Double> loosePassiveBet;
    Map<Integer, Double> looseMediumBet;
    Map<Integer, Double> looseAggressiveBet;

    Map<Integer, Double> tightPassiveRaise;
    Map<Integer, Double> tightMediumRaise;
    Map<Integer, Double> tightAggressiveRaise;
    Map<Integer, Double> mediumPassiveRaise;
    Map<Integer, Double> mediumMediumRaise;
    Map<Integer, Double> mediumAggressiveRaise;
    Map<Integer, Double> loosePassiveRaise;
    Map<Integer, Double> looseMediumRaise;
    Map<Integer, Double> looseAggressiveRaise;

    Map<Integer, Double> tightPassiveCall;
    Map<Integer, Double> tightMediumCall;
    Map<Integer, Double> tightAggressiveCall;
    Map<Integer, Double> mediumPassiveCall;
    Map<Integer, Double> mediumMediumCall;
    Map<Integer, Double> mediumAggressiveCall;
    Map<Integer, Double> loosePassiveCall;
    Map<Integer, Double> looseMediumCall;
    Map<Integer, Double> looseAggressiveCall;

    public PostFlopActionBuilder(BoardEvaluator boardEvaluator, HandEvaluator handEvaluator, Actionable actionable) {
        this.boardEvaluator = boardEvaluator;
        this.handEvaluator = handEvaluator;
        this.actionable = actionable;

        bigBlind = actionable.getBigBlind();
        board = actionable.getBoard();
        sizing = getSizing();
        potSize = actionable.getPotSize();
    }

    private void initializeOpponentTypeMaps() {
        tightPassiveBet = new HashMap<>();
        tightMediumBet = new HashMap<>();
        tightAggressiveBet = new HashMap<>();
        mediumPassiveBet = new HashMap<>();
        mediumMediumBet = new HashMap<>();
        mediumAggressiveBet = new HashMap<>();
        loosePassiveBet = new HashMap<>();
        looseMediumBet = new HashMap<>();
        looseAggressiveBet = new HashMap<>();

        tightPassiveRaise = new HashMap<>();
        tightMediumRaise = new HashMap<>();
        tightAggressiveRaise = new HashMap<>();
        mediumPassiveRaise = new HashMap<>();
        mediumMediumRaise = new HashMap<>();
        mediumAggressiveRaise = new HashMap<>();
        loosePassiveRaise = new HashMap<>();
        looseMediumRaise = new HashMap<>();
        looseAggressiveRaise = new HashMap<>();

        tightPassiveCall = new HashMap<>();
        tightMediumCall = new HashMap<>();
        tightAggressiveCall = new HashMap<>();
        mediumPassiveCall = new HashMap<>();
        mediumMediumCall = new HashMap<>();
        mediumAggressiveCall = new HashMap<>();
        loosePassiveCall = new HashMap<>();
        looseMediumCall = new HashMap<>();
        looseAggressiveCall = new HashMap<>();

        tightPassiveBet.put(5, 0.50);
        tightPassiveBet.put(20, 0.70);
        tightPassiveBet.put(40, 0.80);
        tightPassiveBet.put(70, 0.85);
        tightPassiveBet.put(71, 0.87);

        tightMediumBet.put(5, 0.50);
        tightMediumBet.put(20, 0.70);
        tightMediumBet.put(40, 0.80);
        tightMediumBet.put(70, 0.85);
        tightMediumBet.put(71, 0.87);

        tightAggressiveBet.put(5, 0.50);
        tightAggressiveBet.put(20, 0.70);
        tightAggressiveBet.put(40, 0.80);
        tightAggressiveBet.put(70, 0.85);
        tightAggressiveBet.put(71, 0.87);

        mediumPassiveBet.put(5, 0.50);
        mediumPassiveBet.put(20, 0.60);
        mediumPassiveBet.put(40, 0.75);
        mediumPassiveBet.put(70, 0.80);
        mediumPassiveBet.put(71, 0.85);

        mediumMediumBet.put(5, 0.50);
        mediumMediumBet.put(20, 0.60);
        mediumMediumBet.put(40, 0.75);
        mediumMediumBet.put(70, 0.80);
        mediumMediumBet.put(71, 0.85);

        mediumAggressiveBet.put(5, 0.50);
        mediumAggressiveBet.put(20, 0.60);
        mediumAggressiveBet.put(40, 0.75);
        mediumAggressiveBet.put(70, 0.80);
        mediumAggressiveBet.put(71, 0.85);

        loosePassiveBet.put(5, 0.50);
        loosePassiveBet.put(20, 0.60);
        loosePassiveBet.put(40, 0.67);
        loosePassiveBet.put(70, 0.75);
        loosePassiveBet.put(71, 0.80);

        looseMediumBet.put(5, 0.50);
        looseMediumBet.put(20, 0.60);
        looseMediumBet.put(40, 0.67);
        looseMediumBet.put(70, 0.75);
        looseMediumBet.put(71, 0.80);

        looseAggressiveBet.put(5, 0.50);
        looseAggressiveBet.put(20, 0.60);
        looseAggressiveBet.put(40, 0.65);
        looseAggressiveBet.put(70, 0.75);
        looseAggressiveBet.put(71, 0.80);


        tightPassiveRaise.put(5, 0.85);
        tightPassiveRaise.put(20, 0.87);
        tightPassiveRaise.put(40, 0.90);
        tightPassiveRaise.put(70, 0.90);
        tightPassiveRaise.put(71, 0.90);

        tightMediumRaise.put(5, 0.85);
        tightMediumRaise.put(20, 0.85);
        tightMediumRaise.put(40, 0.90);
        tightMediumRaise.put(70, 0.90);
        tightMediumRaise.put(71, 0.90);

        tightAggressiveRaise.put(5, 0.85);
        tightAggressiveRaise.put(20, 0.85);
        tightAggressiveRaise.put(40, 0.90);
        tightAggressiveRaise.put(70, 0.90);
        tightAggressiveRaise.put(71, 0.90);


    }

    public String getAction() {
        String action = null;
        String opponentAction = actionable.getOpponentAction();

        handStrength = handEvaluator.getHandStrength(actionable.getBotHoleCards());

        System.out.println("Computer handstrength: " + handStrength);

        if(opponentAction == null || opponentAction.contains(CHECK)) {
            action = getFcheckOrFirstToAct(handStrength);
        }
        if(opponentAction != null && opponentAction.contains(BET)) {
            action = getFbet(handStrength);
        }
        if(opponentAction != null && opponentAction.contains(RAISE)) {
            action = getFraise(handStrength);
        }
        return action;
    }

    private String getFcheckOrFirstToAct(double handStrength) {
        String action = getValueAction(BET);

        if(action == null) {
            action = getDrawBettingAction(BET);
        }
        if(action == null) {
            action = getBluffAction(BET, handStrength);
        }
        if(action == null) {
            System.out.println("default check in getFcheckOrFirstToAct()");
            action = CHECK;
        }
        return action;
    }

    private String getFbet(double handStrength) {
        String action = getValueAction(RAISE);

        if(action == null) {
            action = getDrawBettingAction(RAISE);
        }
        if(action == null) {
            action = getTrickyRaiseAction(handStrength);
        }
        if(action == null) {
            action = getBluffAction(RAISE, handStrength);
        }
        if(action == null) {
            action = getValueCallAction(handStrength);
        }
        if(action == null) {
            action = getDrawCallingAction();
        }
        if(action == null) {
            System.out.println("default fold in getFbet()");
            action = FOLD;
        }
        return action;
    }

    private String getFraise(double handStrength) {
        String action = getValueAction(RAISE);

        if(action == null) {
            action = getDrawBettingAction(RAISE);
        }
        if(action == null) {
            action = getBluffAction(RAISE, handStrength);
        }
        if(action == null) {
            action = getValueCallAction(handStrength);
        }
        if(action == null) {
            action = getDrawCallingAction();
        }
        if(action == null) {
            System.out.println("default fold in getFraise()");
            action = FOLD;
        }
        return action;
    }

    private String getValueAction(String bettingAction) {
        String valueAction = null;

        if(getAmountToCall() < actionable.getBotStack() && actionable.getOpponentStack() > 0) {
            if(bettingAction.equals(BET)) {
                valueAction = getBetValueAction();
            } else if(bettingAction.equals(RAISE)) {
                valueAction = getRaiseValueAction();
            }

            if(valueAction != null) {
                System.out.println("value action");
            }
        }
        return valueAction;
    }

    private String getBetValueAction() {
        String betValueAction = null;

        switch(opponentType) {
            case "tightPassive":
                betValueAction = getBetValueActionVsTightPassive();
                break;
            case "tightMedium":
                betValueAction = getBetValueActionVsTightMedium();
                break;
            case "tightAggressive":
                betValueAction = getBetValueActionVsTightAggressive();
                break;
            case "mediumPassive":
                betValueAction = getBetValueActionVsMediumPassive();
                break;
            case "mediumMedium":
                betValueAction = getBetValueActionVsMediumMedium();
                break;
            case "mediumAggressive":
                betValueAction = getBetValueActionVsMediumAggressive();
                break;
            case "loosePassive":
                betValueAction = getBetValueActionVsLoosePassive();
                break;
            case "looseMedium":
                betValueAction = getBetValueActionVsLooseMedium();
                break;
            case "looseAggressive":
                betValueAction = getBetValueAtionVsLooseAggressive();
                break;
        }
        return betValueAction;
    }

    private void fillMaps() {
        Map<Integer, Double> tightPassiveBet = new HashMap<>();

        tightPassiveBet.put(5, 0.50);
        tightPassiveBet.put(20, 0.70);
        tightPassiveBet.put(40, 0.80);
        tightPassiveBet.put(70, 0.85);
        tightPassiveBet.put(71, 0.87);






    }

    private String getBetValueAction(Map<Integer, Double> opponentTypeMap) {
        String betValueActionVsTightPassive = null;

        if(sizing / bigBlind <= 5) {
            if(handStrength > opponentTypeMap.get(5)) {
                betValueActionVsTightPassive = getPassiveOrAggressiveValueAction(BET);
            }
        } else if (sizing / bigBlind <= 20){
            if(handStrength > opponentTypeMap.get(20)) {
                betValueActionVsTightPassive = getPassiveOrAggressiveValueAction(BET);
            }
        } else if (sizing / bigBlind <= 40) {
            if(handStrength > opponentTypeMap.get(40)) {
                betValueActionVsTightPassive = getPassiveOrAggressiveValueAction(BET);
            }
        } else if (sizing / bigBlind <= 70) {
            if(handStrength > opponentTypeMap.get(85)) {
                betValueActionVsTightPassive = getPassiveOrAggressiveValueAction(BET);
            }
        } else {
            if(handStrength > opponentTypeMap.get(87)) {
                betValueActionVsTightPassive = getPassiveOrAggressiveValueAction(BET);
            }
        }
        return betValueActionVsTightPassive;
    }


    private String getBetValueActionVsTightPassive() {
        String betValueActionVsTightPassive = null;

        if(sizing / bigBlind <= 5) {
            if(handStrength > 0.50) {
                betValueActionVsTightPassive = getPassiveOrAggressiveValueAction(BET);
            }
        } else if (sizing / bigBlind <= 20){
            if(handStrength > 0.70) {
                betValueActionVsTightPassive = getPassiveOrAggressiveValueAction(BET);
            }
        } else if (sizing / bigBlind <= 40) {
            if(handStrength > 0.80) {
                betValueActionVsTightPassive = getPassiveOrAggressiveValueAction(BET);
            }
        } else if (sizing / bigBlind <= 70) {
            if(handStrength > 0.85) {
                betValueActionVsTightPassive = getPassiveOrAggressiveValueAction(BET);
            }
        } else {
            if(handStrength > 0.87) {
                betValueActionVsTightPassive = getPassiveOrAggressiveValueAction(BET);
            }
        }
        return betValueActionVsTightPassive;
    }

    private String getBetValueActionVsTightMedium() {
        String betValueActionVsTightMedium = null;

        if(sizing / bigBlind <= 5) {
            if(handStrength > 0.50) {
                betValueActionVsTightMedium = getPassiveOrAggressiveValueAction(BET);
            }
        } else if (sizing / bigBlind <= 20){
            if(handStrength > 0.70) {
                betValueActionVsTightMedium = getPassiveOrAggressiveValueAction(BET);
            }
        } else if (sizing / bigBlind <= 40) {
            if(handStrength > 0.80) {
                betValueActionVsTightMedium = getPassiveOrAggressiveValueAction(BET);
            }
        } else if (sizing / bigBlind <= 70) {
            if(handStrength > 0.85) {
                betValueActionVsTightMedium = getPassiveOrAggressiveValueAction(BET);
            }
        } else {
            if(handStrength >= 0.87) {
                betValueActionVsTightMedium = getPassiveOrAggressiveValueAction(BET);
            }
        }
        return betValueActionVsTightMedium;
    }

    private String getBetValueActionVsTightAggressive() {
        String betValueActionVsTightAggressive = null;

        if(sizing / bigBlind <= 5) {
            if(handStrength > 0.50) {
                betValueActionVsTightAggressive = getPassiveOrAggressiveValueAction(BET);
            }
        } else if (sizing / bigBlind <= 20){
            if(handStrength > 0.70) {
                betValueActionVsTightAggressive = getPassiveOrAggressiveValueAction(BET);
            }
        } else if (sizing / bigBlind <= 40) {
            if(handStrength > 0.80) {
                betValueActionVsTightAggressive = getPassiveOrAggressiveValueAction(BET);
            }
        } else if (sizing / bigBlind <= 70) {
            if(handStrength > 0.85) {
                betValueActionVsTightAggressive = getPassiveOrAggressiveValueAction(BET);
            }
        } else {
            if(handStrength >= 0.87) {
                betValueActionVsTightAggressive = getPassiveOrAggressiveValueAction(BET);
            }
        }
        return betValueActionVsTightAggressive;
    }

    private String getBetValueActionVsMediumPassive() {
        return null;
    }

    private String getBetValueActionVsMediumMedium() {
        return null;
    }

    private String getBetValueActionVsMediumAggressive() {
        return null;
    }

    private String getBetValueActionVsLoosePassive() {
        return null;
    }

    private String getBetValueActionVsLooseMedium() {
        return null;
    }

    private String getBetValueAtionVsLooseAggressive() {
        return null;
    }

    private String getRaiseValueAction() {
        return null;
    }

    private String getDrawBettingAction(String bettingAction) {
        String drawBettingAction = null;

        if(getAmountToCall() < actionable.getBotStack() && actionable.getOpponentStack() > 0) {
            drawBettingAction = getDraw2ndBarrelAction(bettingAction);

            if(drawBettingAction == null) {
                drawBettingAction = getDrawBettingInitializeAction(bettingAction);
            }

            if(drawBettingAction == null) {
                actionable.setDrawBettingActionDone(false);
            } else {
                System.out.println("draw betting action");
            }
        }
        return drawBettingAction;
    }

    private String getDraw2ndBarrelAction(String bettingAction) {
        String draw2ndBarrelAction = null;

        if(actionable.isDrawBettingActionDone()) {
            if(bettingAction.equals(BET)) {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(Math.random() < 0.85) {
                        draw2ndBarrelAction = BET;
                    }
                } else if(handEvaluator.hasDrawOfType("strongGutshot")) {
                    if(Math.random() < 0.6) {
                        draw2ndBarrelAction = BET;
                    }
                }
            }
        }
        return draw2ndBarrelAction;
    }

    private String getDrawBettingInitializeAction(String bettingAction) {
        String drawBettingInitializeAction = null;

        if(board.size() == 3 || board.size() == 4) {
            if(sizing / bigBlind <= 5) {
                if(handEvaluator.hasAnyDrawNonBackDoor()) {
                    if(Math.random() < 0.5) {
                        drawBettingInitializeAction = bettingAction;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                    if(Math.random() < 0.01) {
                        drawBettingInitializeAction = bettingAction;
                    }
                }
            } else if (sizing / bigBlind > 5 && sizing / bigBlind <= 20){
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(Math.random() < 0.80) {
                        drawBettingInitializeAction = bettingAction;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongGutshot")) {
                    if(Math.random() < 0.22) {
                        drawBettingInitializeAction = bettingAction;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongOvercards")) {
                    if(Math.random() < 0.15) {
                        drawBettingInitializeAction = bettingAction;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                    if(Math.random() < 0.07) {
                        drawBettingInitializeAction = bettingAction;
                    }
                }
            } else {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(Math.random() < 0.90) {
                        drawBettingInitializeAction = bettingAction;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongGutshot")) {
                    if(Math.random() < 0.27) {
                        drawBettingInitializeAction = bettingAction;
                    }
                }
            }
        }

        if(drawBettingInitializeAction != null) {
            actionable.setDrawBettingActionDone(true);
        }
        return drawBettingInitializeAction;
    }

    private String getTrickyRaiseAction(double handStrength) {
        String trickyRaiseAction = null;

        if(getAmountToCall() < actionable.getBotStack() && actionable.getOpponentStack() > 0) {
            if(handStrength >= 0.6 && handStrength < 0.8) {
                if(board.size() == 3) {
                    if(sizing / bigBlind <= 20) {
                        if(Math.random() < 0.2) {
                            trickyRaiseAction = RAISE;
                        }
                    }
                }
                if(board.size() == 4) {
                    if(sizing / bigBlind <= 15) {
                        if(Math.random() < 0.1) {
                            trickyRaiseAction = RAISE;
                        }
                    }
                }
            }

            if(trickyRaiseAction != null) {
                System.out.println("tricky raise action");
            }
        }
        return trickyRaiseAction;
    }

    private String getBluffAction(String bettingAction, double handStrength) {
        String bluffAction = null;

        if(getAmountToCall() < actionable.getBotStack() && actionable.getOpponentStack() > 0) {
            bluffAction = getBluffBarrelAction(bettingAction, handStrength);

            if(bluffAction == null) {
                bluffAction = getBluffAfterMissedDrawAction(bettingAction);
            }
            if(bluffAction == null) {
                bluffAction = getBluffInitializeAction(bettingAction, handStrength);
            }

            if(bluffAction != null) {
                System.out.println("bluff action");
            }
        }
        return bluffAction;
    }

    private String getBluffBarrelAction(String bettingAction, double handStrength) {
        String bluffBarrelAction = null;

        if(actionable.isPreviousBluffAction()) {
            if (bluffOddsAreOk() && handStrength < 0.62) {
                if (bettingAction.equals(BET)) {
                    if (actionable.isBotIsButton()) {
                        if (Math.random() <= 0.75) {
                            bluffBarrelAction = bettingAction;
                        }
                    } else {
                        if (Math.random() <= 0.45) {
                            bluffBarrelAction = bettingAction;
                        }
                    }
                } else {
                    if (board.size() == 5) {
                        if (sizing / bigBlind < 70) {
                            if (Math.random() < 0.05) {
                                bluffBarrelAction = bettingAction;
                                actionable.setPreviousBluffAction(true);
                            }
                        } else {
                            if (Math.random() < 0.02) {
                                bluffBarrelAction = bettingAction;
                                actionable.setPreviousBluffAction(true);
                            }
                        }
                    }
                }
            }
        }
        return bluffBarrelAction;
    }

    private String getBluffAfterMissedDrawAction(String bettingAction) {
        String bluffAfterMissedDrawAction = null;

        if(actionable.isDrawBettingActionDone() && bettingAction.equals(BET) && bluffOddsAreOk()) {
            if(actionable.isBotIsButton()) {
                if(Math.random() <= 0.65) {
                    bluffAfterMissedDrawAction = bettingAction;
                }
            } else {
                if(Math.random() <= 0.35) {
                    bluffAfterMissedDrawAction = bettingAction;
                }
            }
        }
        return bluffAfterMissedDrawAction;
    }

    private String getBluffInitializeAction(String bettingAction, double handStrength) {
        String bluffInitializeAction = null;

        if(bluffOddsAreOk() && handStrength < 0.65) {
            if(bettingAction.equals(BET)) {
                if(potSize / bigBlind < 10) {
                    if(actionable.isBotIsButton()) {
                        if(Math.random() < 0.18) {
                            bluffInitializeAction = bettingAction;
                            actionable.setPreviousBluffAction(true);
                        }
                    } else {
                        if(Math.random() < 0.07) {
                            bluffInitializeAction = bettingAction;
                            actionable.setPreviousBluffAction(true);
                        }
                    }
                } else if(potSize / bigBlind < 25) {
                    if(Math.random() < 0.40) {
                        bluffInitializeAction = bettingAction;
                        actionable.setPreviousBluffAction(true);
                    }
                } else if(potSize / bigBlind < 50) {
                    if(Math.random() < 0.50) {
                        bluffInitializeAction = bettingAction;
                        actionable.setPreviousBluffAction(true);
                    }
                } else {
                    if(Math.random() < 0.60) {
                        bluffInitializeAction = bettingAction;
                        actionable.setPreviousBluffAction(true);
                    }
                }
            } else {
                if(board.size() == 5) {
                    if(potSize / bigBlind < 10) {
                        if(Math.random() < 0.10) {
                            bluffInitializeAction = bettingAction;
                            actionable.setPreviousBluffAction(true);
                        }
                    } else if(potSize / bigBlind < 25) {
                        if(Math.random() < 0.25) {
                            bluffInitializeAction = bettingAction;
                            actionable.setPreviousBluffAction(true);
                        }
                    } else if(potSize / bigBlind < 50) {
                        if(Math.random() < 0.30) {
                            bluffInitializeAction = bettingAction;
                            actionable.setPreviousBluffAction(true);
                        }
                    } else {
                        if(Math.random() < 0.35) {
                            bluffInitializeAction = bettingAction;
                            actionable.setPreviousBluffAction(true);
                        }
                    }
                }
            }
        }
        return bluffInitializeAction;
    }

    private String getValueCallAction(double handStrength) {
        String valueCallAction = null;

        double amountToCallBb = (actionable.getOpponentTotalBetSize() - actionable.getBotTotalBetSize()) / bigBlind;

        if(amountToCallBb / actionable.getPotSize() > 0 && amountToCallBb / actionable.getPotSize() <= 0.2) {
            if(handStrength >= 30) {
                valueCallAction = CALL;
            }
        }

        if(amountToCallBb <= 5) {
            if(handStrength >= 0.50) {
                valueCallAction = CALL;
            }
        } else if (amountToCallBb <= 20){
            if(handStrength >= 0.60) {
                valueCallAction = CALL;
            }
        } else if (amountToCallBb <= 40) {
            if(handStrength >= 0.65) {
                valueCallAction = CALL;
            }
        } else if (amountToCallBb <= 70) {
            if(handStrength >= 0.70) {
                valueCallAction = CALL;
            }
        } else {
            if(handStrength >= 0.75) {
                valueCallAction = CALL;
            }
        }

        if(valueCallAction != null) {
            System.out.println("value call action");
        }
        return valueCallAction;
    }

    private String getDrawCallingAction() {
        String drawCallingAction = null;

        double amountToCall = (actionable.getOpponentTotalBetSize() - actionable.getBotTotalBetSize());
        double odds = amountToCall / (actionable.getPotSize() + actionable.getOpponentTotalBetSize() + actionable.getBotTotalBetSize());
        boolean botIsButton = actionable.isBotIsButton();

        if(board.size() == 3 || board.size() == 4) {
            if(amountToCall / bigBlind < 4) {
                if(handEvaluator.hasAnyDrawNonBackDoor()) {
                    drawCallingAction = CALL;
                }
                if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                    drawCallingAction = CALL;
                }
            } else if(amountToCall / bigBlind >= 4 && amountToCall / bigBlind < 20) {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    drawCallingAction = CALL;
                }
                if(handEvaluator.hasDrawOfType("strongGutshot")) {
                    if(odds <= 0.45) {
                        if(board.size() == 3) {
                            drawCallingAction = CALL;
                        } else {
                            if(botIsButton) {
                                drawCallingAction = CALL;
                            } else {
                                if(Math.random() < 0.3) {
                                    drawCallingAction = CALL;
                                }
                            }
                        }
                    }
                }
                if(handEvaluator.hasDrawOfType("strongOvercards")) {
                    if(odds <= 0.45) {
                        if(board.size() == 3) {
                            if(botIsButton) {
                                drawCallingAction = CALL;
                            } else {
                                if(Math.random() < 0.3) {
                                    drawCallingAction = CALL;
                                }
                            }
                        } else {
                            if(botIsButton) {
                                if(Math.random() < 0.5) {
                                    drawCallingAction = CALL;
                                }
                            } else {
                                if(Math.random() < 0.15) {
                                    drawCallingAction = CALL;
                                }
                            }
                        }
                    }
                }
            } else if (amountToCall / bigBlind > 20 && amountToCall / bigBlind < 40) {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(odds <= 0.22) {
                        drawCallingAction = CALL;
                    } else if(odds <= 0.45) {
                        if(board.size() == 3) {
                            drawCallingAction = CALL;
                        } else {
                            if(botIsButton) {
                                drawCallingAction = CALL;
                            } else {
                                if(Math.random() < 0.23) {
                                    drawCallingAction = CALL;
                                }
                            }
                        }
                    }
                }
            } else {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(odds <= 0.22) {
                        drawCallingAction = CALL;
                    } else if(odds <= 0.45) {
                        if(board.size() == 3) {
                            if(afterDrawCall66PercentBetStillPossible()) {
                                if(botIsButton) {
                                    drawCallingAction = CALL;
                                }
                            }
                        } else {
                            if(afterDrawCall66PercentBetStillPossible()) {
                                if(botIsButton) {
                                    if(Math.random() <= 0.12) {
                                        drawCallingAction = CALL;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if(drawCallingAction != null) {
            System.out.println("draw call action");
        }
        return drawCallingAction;
    }

    private boolean afterDrawCall66PercentBetStillPossible() {
        double potSizeAfterCall = potSize + (2 * actionable.getOpponentTotalBetSize());
        double botStackAfterCall = actionable.getBotStack() -
                (actionable.getOpponentTotalBetSize() - actionable.getBotTotalBetSize());

        if(botStackAfterCall >= (0.66 * potSizeAfterCall) && actionable.getOpponentStack() >= (0.66 * potSizeAfterCall)) {
            return true;
        }
        return false;
    }

    private String getPassiveOrAggressiveValueAction(String bettingAction) {
        if(board.size() != 5) {
            if(Math.random() < 0.92) {
                return bettingAction;
            } else {
                return null;
            }
        } else {
            String opponentAction = actionable.getOpponentAction();
            if(!actionable.isBotIsButton() && opponentAction == null) {
                if(Math.random() < 0.92) {
                    return bettingAction;
                } else {
                    return null;
                }
            } else {
                return bettingAction;
            }
        }
    }

    private double getHandStrengthNeededToCall() {
        double amountToCall = actionable.getOpponentTotalBetSize() - actionable.getBotTotalBetSize();
        double potSize = actionable.getPotSize();
        return (0.01 + amountToCall) / (potSize + amountToCall);
    }

    private boolean bluffOddsAreOk() {
        double potSize = actionable.getPotSize();
        double opponentBetSize = actionable.getOpponentTotalBetSize();
        double opponentStack = actionable.getOpponentStack();
        double amountOpponentHasToCall = sizing - opponentBetSize;
        double sizingCopy = sizing;

        if(amountOpponentHasToCall > opponentStack) {
            amountOpponentHasToCall = opponentStack;
            sizingCopy = opponentStack;
        }

        double opponentOdds = amountOpponentHasToCall / (potSize + opponentBetSize + sizingCopy);

        if(opponentOdds >= 0.40) {
            return true;
        }
        return false;
    }

    public double getSizing() {
        double sizing = 0;

        if(board.size() == 3) {
            sizing = getFlopSizing();
        } else if(board.size() == 4) {
            sizing = getTurnSizing();
        } else if(board.size() == 5) {
            sizing = getRiverSizing();
        }
        return sizing;
    }

    private double getFlopSizing() {
        double flopSizing;

        double opponentBetSize = actionable.getOpponentTotalBetSize();
        double potSize = actionable.getPotSize();
        double potSizeBb = potSize / bigBlind;
        double botStack = actionable.getBotStack();
        double opponentStack = actionable.getOpponentStack();
        double effectiveStack = getEffectiveStack(botStack, opponentStack);

        if(botStack <= 1.2 * potSize) {
            flopSizing = botStack;
        } else {
            if(opponentBetSize == 0) {
                if(potSizeBb <= 8) {
                    flopSizing = 0.75 * potSize;
                } else if(potSizeBb > 8 && potSizeBb <= 24) {
                    double flopBetPercentage = getFlopBetPercentage(effectiveStack, potSize, 0.7, 0.75);

                    if(flopBetPercentage < 0.37) {
                        flopBetPercentage = 0.5;
                    }
                    if(flopBetPercentage > 0.75) {
                        flopBetPercentage = 0.75;
                    }

                    flopSizing = flopBetPercentage * potSize;
                } else {
                    double flopBetPercentage = getFlopBetPercentage(effectiveStack, potSize, 0.33, 0.51);

                    if(flopBetPercentage < 0.2) {
                        flopBetPercentage = 0.2;
                    }
                    if(flopBetPercentage > 0.75) {
                        flopBetPercentage = 0.75;
                    }

                    flopSizing = flopBetPercentage * potSize;
                }
            } else {
                flopSizing = calculateRaiseAmount(opponentBetSize, potSize, effectiveStack, botStack, 2.33);
            }
        }
        if(flopSizing > botStack) {
            flopSizing = botStack;
        }

        return flopSizing;
    }

    private double getTurnSizing() {
        double turnSizing;

        double opponentBetSize = actionable.getOpponentTotalBetSize();
        double potSize = actionable.getPotSize();
        double botStack = actionable.getBotStack();
        double opponentStack = actionable.getOpponentStack();
        double effectiveStack = getEffectiveStack(botStack, opponentStack);

        if(botStack <= 1.2 * potSize) {
            turnSizing = botStack;
        } else {
            if(opponentBetSize == 0) {
                double turnBetPercentage3bet = getTurnBetPercentage(effectiveStack, potSize, 0.75);
                double turnBetPercentage4bet = getTurnBetPercentage(effectiveStack, potSize, 0.51);

                if(turnBetPercentage3bet > 0.75) {
                    turnSizing = 0.75 * potSize;
                } else if(turnBetPercentage3bet > 0.5) {
                    turnSizing = turnBetPercentage3bet * potSize;
                } else if(turnBetPercentage3bet > 0.4) {
                    turnSizing = getTurnBetPercentage(effectiveStack, potSize, 0.67) * potSize;
                } else if(turnBetPercentage4bet > 0.2) {
                    turnSizing = turnBetPercentage4bet * potSize;
                } else {
                    turnSizing = 0.2 * potSize;
                }
            } else {
                turnSizing = calculateRaiseAmount(opponentBetSize, potSize, effectiveStack, botStack, 2.33);
            }
        }
        if(turnSizing > botStack) {
            turnSizing = botStack;
        }

        return turnSizing;
    }

    private double getRiverSizing() {
        double riverSizing;

        double opponentBetSize = actionable.getOpponentTotalBetSize();
        double potSize = actionable.getPotSize();
        double botStack = actionable.getBotStack();
        double opponentStack = actionable.getOpponentStack();
        double effectiveStack = getEffectiveStack(botStack, opponentStack);

        if(opponentBetSize == 0) {
            if(botStack <= 1.2 * potSize) {
                riverSizing = botStack;
            } else {
                riverSizing = 0.75 * potSize;
            }
        } else {
            riverSizing = calculateRaiseAmount(opponentBetSize, potSize, effectiveStack, botStack, 2.33);
        }
        if(riverSizing > botStack) {
            riverSizing = botStack;
        }

        return riverSizing;
    }

    private double getEffectiveStack(double botStack, double opponentStack) {
        if(botStack > opponentStack) {
            return opponentStack;
        } else {
            return botStack;
        }
    }

    private double getAmountToCall() {
        double botBetSize = actionable.getBotTotalBetSize();
        double opponentBetSize = actionable.getOpponentTotalBetSize();
        return opponentBetSize - botBetSize;
    }

    private double calculateRaiseAmount(double facingBetSize, double potSize, double effectiveStack, double botStack, double odds) {
        double raiseAmount = (potSize / (odds - 1)) + (((odds + 1) * facingBetSize) / (odds - 1));
        double potSizeAfterRaiseAndCall = potSize + raiseAmount + raiseAmount;
        double effectiveStackRemainingAfterRaise = effectiveStack - raiseAmount;

        if(effectiveStackRemainingAfterRaise / potSizeAfterRaiseAndCall < 0.51) {
            if(Math.random() < 0.5) {
                raiseAmount = reduceRaiseAmountIfNecessary(raiseAmount, facingBetSize, potSize, effectiveStack);
            } else {
                raiseAmount = botStack;
            }
        }
        return raiseAmount;
    }

    private double reduceRaiseAmountIfNecessary(double raiseAmount, double facingBetSize, double potSize, double effectiveStack) {
        double minimumRaiseAmount = 2 * facingBetSize;

        if(board.size() == 3) {
            double flopBetSizeIn4betPot = getFlopBetPercentage(effectiveStack, potSize, 0.33, 0.51) * potSize;
            if(raiseAmount > flopBetSizeIn4betPot) {
                if(flopBetSizeIn4betPot > minimumRaiseAmount) {
                    raiseAmount = flopBetSizeIn4betPot;
                }
            }
        } else if(board.size() == 4) {
            double turnBetSizeIn4betPot = getTurnBetPercentage(effectiveStack, potSize, 0.51);
            if(raiseAmount > turnBetSizeIn4betPot) {
                if(turnBetSizeIn4betPot > minimumRaiseAmount) {
                    raiseAmount = turnBetSizeIn4betPot;
                }
            }
        }
        return raiseAmount;
    }

    private double getFlopBetPercentage(double effectiveStackSize, double potSize, double turnBetPercentage, double riverBetPercentage) {
        double flopBetPercentage;

        double s = effectiveStackSize;
        double p = potSize;
        double t = turnBetPercentage;
        double r = riverBetPercentage;

        flopBetPercentage = (s - (2 * t * p * r) - (p * r) - (t * p)) / ((4 * t * p * r) + (2 * p * r) + (2 * t * p) + p);

        if(flopBetPercentage <= 0) {
            return 0;
        } else {
            return flopBetPercentage;
        }
    }

    private double getTurnBetPercentage(double effectiveStackSize, double potSize, double riverBetPercentage) {
        double turnBetPercentage;

        double s = effectiveStackSize;
        double p = potSize;
        double r = riverBetPercentage;

        turnBetPercentage = (s - (r * p)) / ((2 * r * p) + p);

        if (turnBetPercentage <= 0) {
            return 0;
        } else {
            return turnBetPercentage;
        }
    }

    public double getHandStrength() {
        return handStrength;
    }
}
