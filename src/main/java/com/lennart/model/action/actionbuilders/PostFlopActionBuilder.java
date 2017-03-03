package com.lennart.model.action.actionbuilders;

import com.lennart.model.action.Actionable;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.card.Card;

import java.util.List;
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

    private BoardEvaluator boardEvaluator;
    private HandEvaluator handEvaluator;
    private Actionable actionable;

    public PostFlopActionBuilder(BoardEvaluator boardEvaluator, HandEvaluator handEvaluator, Actionable actionable) {
        bigBlind = actionable.getBigBlind();
        board = actionable.getBoard();
        this.boardEvaluator = boardEvaluator;
        this.handEvaluator = handEvaluator;
        this.actionable = actionable;
    }

    public String getAction(Set<Set<Card>> opponentRange) {
        String action = null;
        double handStrengthAgainstRange = handEvaluator.getHandStrengthAgainstRange(actionable.getBotHoleCards(),
                opponentRange, boardEvaluator.getSortedCombosNew());
        String opponentAction = actionable.getOpponentAction();

        System.out.println("Computer handstrength: " + handStrengthAgainstRange);

        if(opponentAction == null || opponentAction.contains(CHECK)) {
            action = getFcheckOrFirstToAct(handStrengthAgainstRange);
        } else if(opponentAction.contains(BET)) {
            action = getFbet(handStrengthAgainstRange);
        } else if(opponentAction.contains(RAISE)) {
            action = getFraise(handStrengthAgainstRange);
        }
        return action;
    }

    private String getFcheckOrFirstToAct(double handStrengthAgainstRange) {
        String action = getValueAction(handStrengthAgainstRange, BET);

        if(action == null) {
            action = getDrawBettingAction(BET);
        }
        if(action == null) {
            action = getBluffAction(BET, handStrengthAgainstRange);
        }
        if(action != null) {
            actionable.setPreviousValueAction(true);
        }
        if(action == null) {
            System.out.println("default check in getFcheckOrFirstToAct()");
            action = CHECK;
        }
        return action;
    }

    private String getFbet(double handStrengthAgainstRange) {
        String action = getValueAction(handStrengthAgainstRange, RAISE);

        if(action == null) {
            action = getDrawBettingAction(RAISE);
        }
        if(action == null) {
            action = getBluffAction(RAISE, handStrengthAgainstRange);
        }
        if(action == null) {
            action = getValueCallAction(handStrengthAgainstRange);
        }
        if(action == null) {
            action = getDrawCallingAction();
        }
        if(action == null) {
            action = getFloatAction(BET);
        }
        if(action == null) {
            System.out.println("default fold in getFbet()");
            action = FOLD;
        }
        return action;
    }

    private String getFraise(double handStrengthAgainstRange) {
        String action = getValueAction(handStrengthAgainstRange, RAISE);

        if(action == null) {
            action = getDrawBettingAction(RAISE);
        }
        if(action == null) {
            action = getBluffAction(RAISE, handStrengthAgainstRange);
        }
        if(action == null) {
            action = getValueCallAction(handStrengthAgainstRange);
        }
        if(action == null) {
            action = getDrawCallingAction();
        }
        if(action == null) {
            action = getFloatAction(RAISE);
        }
        if(action == null) {
            System.out.println("default fold in getFraise()");
            action = FOLD;
        }
        return action;
    }

    private String getValueAction(double handStrengthAgainstRange, String bettingAction) {
        String valueAction;
        if(actionable.isBotIsButton()) {
            valueAction = getIpValueAction(handStrengthAgainstRange, bettingAction);
        } else {
            valueAction = getOopValueAction(handStrengthAgainstRange, bettingAction);
        }
        return valueAction;
    }

    private String getDrawBettingAction(String bettingAction) {
        String drawBettingAction;
        if(actionable.isBotIsButton()) {
            drawBettingAction = getIpDrawBettingAction(bettingAction);
        } else {
            drawBettingAction = getOopDrawBettingAction(bettingAction);
        }
        if(drawBettingAction == null) {
            resetPreviousDrawBettingActionIfNeccesary();
        }
        return drawBettingAction;
    }

    private String getBluffAction(String bettingAction, double handStrengthAgainstRange) {
        double sizing = getSizing();
        String bluffAction = getBluffBarrelAction(bettingAction, sizing, handStrengthAgainstRange);

        if(bluffAction == null) {
            bluffAction = getContinueWithBluffAfterValue(bettingAction, sizing, handStrengthAgainstRange);
        }
        if(bluffAction == null) {
            bluffAction = getRiverBluffAfterMissedDraw(bettingAction, sizing, handStrengthAgainstRange);
        }
        if(bluffAction == null) {
            bluffAction = getBluffAfterFloat(bettingAction, sizing, handStrengthAgainstRange);
        }
        if(bluffAction == null) {
            bluffAction = getBluffInitializeAction(bettingAction, sizing, handStrengthAgainstRange);
        }
        if(bluffAction == null) {
            resetPreviousBluffActionIfNecessary();
            resetPreviousValueActionIfNecessary();
            resetPreviousFloatActionIfNecessary();
        }
        return bluffAction;
    }

    private String getIpValueAction(double handStrengthAgainstRange, String bettingAction) {
        String valueAction = null;
        double sizing = getSizing();

        if(sizing / bigBlind <= 5) {
            if(handStrengthAgainstRange > 0.40) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction);
            }
        } else if (sizing / bigBlind > 5 && sizing / bigBlind <= 20){
            if(handStrengthAgainstRange > 0.60) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction);
            }
        } else if (sizing / bigBlind > 20 && sizing / bigBlind <= 40) {
            if(handStrengthAgainstRange > 0.69) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction);
            }
        } else if (sizing / bigBlind > 40 && sizing / bigBlind <= 70) {
            if(handStrengthAgainstRange > 0.73) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction);
            }
        } else {
            if(handStrengthAgainstRange > 0.95) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction);
            }
        }

        if(valueAction != null) {
            System.out.println("IP value action");
        }
        return valueAction;
    }

    private String getOopValueAction(double handStrengthAgainstRange, String bettingAction) {
        String valueAction = null;
        double sizing = getSizing();
        if(sizing / bigBlind <= 5) {
            if(handStrengthAgainstRange > 0.44) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction);
            }
        } else if (sizing / bigBlind > 5 && sizing / bigBlind <= 20){
            if(handStrengthAgainstRange > 0.66) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction);
            }
        } else if (sizing / bigBlind > 20 && sizing / bigBlind <= 40) {
            if(handStrengthAgainstRange > 0.75) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction);
            }
        } else if (sizing / bigBlind > 40 && sizing / bigBlind <= 70) {
            if(handStrengthAgainstRange > 0.80) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction);
            }
        } else {
            if(handStrengthAgainstRange > 0.95) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction);
            }
        }

        if(valueAction != null) {
            System.out.println("OOP value action");
        }
        return valueAction;
    }

    private String getIpDrawBettingAction(String bettingAction) {
        String drawBettingAction = getDraw2ndBarrelAction(bettingAction);

        if(drawBettingAction == null) {
            if(board.size() == 3 || board.size() == 4) {
                double sizing = getSizing();

                if(sizing / bigBlind < 4) {
                    if(handEvaluator.hasAnyDrawNonBackDoor()) {
                        if(Math.random() < 0.7) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                } else if (sizing / bigBlind >= 4 && sizing / bigBlind <= 20){
                    if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                        if(Math.random() < 0.8) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                    if(handEvaluator.hasDrawOfType("strongGutshot")) {
                        if(Math.random() < 0.4) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                    if(handEvaluator.hasDrawOfType("strongOvercards")) {
                        if(Math.random() < 0.27) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                    if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                        if(Math.random() < 0.14) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                } else if (sizing / bigBlind > 20 && sizing / bigBlind <= 40) {
                    if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                        if(Math.random() < 0.33) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                    if(handEvaluator.hasDrawOfType("strongGutshot")) {
                        if(Math.random() < 0.15) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                } else {
                    if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                        if(Math.random() < 0.17) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                }
            }
        }

        if(drawBettingAction == null) {
            actionable.setPreviousDrawBettingAction(false);
        } else {
            System.out.println("draw betting action");
        }
        return drawBettingAction;
    }

    private String getOopDrawBettingAction(String bettingAction) {
        String drawBettingAction = getDraw2ndBarrelAction(bettingAction);

        if(drawBettingAction == null) {
            if(board.size() == 3 || board.size() == 4) {
                double sizing = getSizing();

                if(sizing / bigBlind < 4) {
                    if(handEvaluator.hasAnyDrawNonBackDoor()) {
                        if(Math.random() < 0.5) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                } else if (sizing / bigBlind >= 4 && sizing / bigBlind <= 20){
                    if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                        if(Math.random() < 0.5) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                    if(handEvaluator.hasDrawOfType("strongGutshot")) {
                        if(Math.random() < 0.2) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                    if(handEvaluator.hasDrawOfType("strongOvercards")) {
                        if(Math.random() < 0.15) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                    if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                        if(Math.random() < 0.07) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                } else if (sizing / bigBlind > 20 && sizing / bigBlind <= 40) {
                    if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                        if(Math.random() < 0.2) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                    if(handEvaluator.hasDrawOfType("strongGutshot")) {
                        if(Math.random() < 0.07) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                } else {
                    if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                        if(Math.random() < 0.1) {
                            drawBettingAction = bettingAction;
                            actionable.setPreviousDrawBettingAction(true);
                        }
                    }
                }
            }
        }

        if(drawBettingAction == null) {
            actionable.setPreviousDrawBettingAction(false);
        } else {
            System.out.println("draw betting action");
        }
        return drawBettingAction;
    }

    private String getDraw2ndBarrelAction(String bettingAction) {
        String draw2ndBarrelAction = null;

        if(actionable.isPreviousDrawBettingAction()) {
            if(bettingAction.equals(BET)) {
                if(actionable.isBotIsButton()) {
                    if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                        if(Math.random() < 0.92) {
                            draw2ndBarrelAction = BET;
                        }
                    } else if(handEvaluator.hasDrawOfType("strongGutshot")) {
                        if(Math.random() < 0.7) {
                            draw2ndBarrelAction = BET;
                        }
                    }
                } else {
                    if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                        if(Math.random() < 0.67) {
                            draw2ndBarrelAction = BET;
                        }
                    } else if(handEvaluator.hasDrawOfType("strongGutshot")) {
                        if(Math.random() < 0.38) {
                            draw2ndBarrelAction = BET;
                        }
                    }
                }
            }
        }

        if(draw2ndBarrelAction != null) {
            System.out.println("draw 2nd barrel action");
        }
        return draw2ndBarrelAction;
    }

    private void resetPreviousDrawBettingActionIfNeccesary() {
        if(actionable.isPreviousDrawBettingAction()) {
            actionable.setPreviousDrawBettingAction(false);
        }
    }

    private String getContinueWithBluffAfterValue(String bettingAction, double sizing, double handStrengthAgainstRange) {
        String bluffAfterValueAction = null;

        if(actionable.isPreviousValueAction()) {
            actionable.setPreviousValueAction(false);
            if(bettingAction.equals(BET) && bluffOddsAreOk(sizing) && handStrengthAgainstRange < 0.4) {
                if(actionable.isBotIsButton()) {
                    if(Math.random() < 0.45) {
                        bluffAfterValueAction = bettingAction;
                        actionable.setPreviousBluffAction(true);
                    }
                } else {
                    if(Math.random() < 0.10) {
                        bluffAfterValueAction = bettingAction;
                        actionable.setPreviousBluffAction(true);
                    }
                }
            }
        }

        if(bluffAfterValueAction != null) {
            System.out.println("continue with bluff after value action");
        }
        return bluffAfterValueAction;
    }

    private String getBluffBarrelAction(String bettingAction, double sizing, double handStrengthAgainstRange) {
        String bluffBarrelAction = null;
        if(actionable.isPreviousBluffAction()) {
            if(bluffOddsAreOk(sizing) && handStrengthAgainstRange < 0.7) {
                if(bettingAction.equals(BET)) {
                    if(actionable.isBotIsButton()) {
                        if(Math.random() <= 0.9) {
                            bluffBarrelAction = bettingAction;
                        }
                    } else {
                        if(Math.random() <= 0.5) {
                            bluffBarrelAction = bettingAction;
                        }
                    }
                }
            }
        }

        if(bluffBarrelAction != null) {
            System.out.println("bluff barrel action");
        }
        return bluffBarrelAction;
    }

    private String getRiverBluffAfterMissedDraw(String bettingAction, double sizing, double handStrengthAgainstRange) {
        String bluffAfterMissedDraw = null;
        if(board.size() == 5) {
            if(actionable.isPreviousDrawBettingAction()) {
                if(bluffOddsAreOk(sizing) && handStrengthAgainstRange < 0.7) {
                    if (actionable.isBotIsButton()) {
                        if (Math.random() <= 0.70 && bettingAction.equals(BET)) {
                            bluffAfterMissedDraw = bettingAction;
                        }
                    } else {
                        if (Math.random() <= 0.35 && bettingAction.equals(BET)) {
                            bluffAfterMissedDraw = bettingAction;
                        }
                    }
                }
            }
        }

        if(bluffAfterMissedDraw != null) {
            System.out.println("river bluff after missed draw");
        }
        return bluffAfterMissedDraw;
    }

    private String getBluffAfterFloat(String bettingAction, double sizing, double handStrengthAgainstRange) {
        String bluffAfterFloat = null;

        if(actionable.isPreviousFloatAction()) {
            if(bluffOddsAreOk(sizing) && handStrengthAgainstRange < 0.7) {
                if(actionable.isBotIsButton()) {
                    if (Math.random() <= 0.80 && bettingAction.equals(BET)) {
                        bluffAfterFloat = bettingAction;
                        actionable.setPreviousBluffAction(true);
                    }
                }
            }
        }

        if(bluffAfterFloat != null) {
            System.out.println("bluff after float");
        }
        return bluffAfterFloat;
    }

    private String getBluffInitializeAction(String bettingAction, double sizing, double handStrengthAgainstRange) {
        String bluffAction = null;
        if(bluffOddsAreOk(sizing) && handStrengthAgainstRange < 0.7) {
            if(bettingAction.equals(BET)) {
                if(actionable.isBotIsButton()) {
                    if(Math.random() < 0.30) {
                        bluffAction = bettingAction;
                        actionable.setPreviousBluffAction(true);
                    }
                } else {
                    if(Math.random() < 0.10) {
                        bluffAction = bettingAction;
                        actionable.setPreviousBluffAction(true);
                    }
                }
            } else {
                if(board.size() == 5) {
                    if(sizing / bigBlind < 20) {
                        if(Math.random() < 0.10) {
                            bluffAction = bettingAction;
                            actionable.setPreviousBluffAction(true);
                        }
                    } else if(sizing / bigBlind < 40) {
                        if(Math.random() < 0.07) {
                            bluffAction = bettingAction;
                            actionable.setPreviousBluffAction(true);
                        }
                    } else {
                        if(Math.random() < 0.04) {
                            bluffAction = bettingAction;
                            actionable.setPreviousBluffAction(true);
                        }
                    }
                }
            }
        }

        if(bluffAction != null) {
            System.out.println("initial bluff action");
        }
        return bluffAction;
    }

    private void resetPreviousBluffActionIfNecessary() {
        if(actionable.isPreviousBluffAction()) {
            actionable.setPreviousBluffAction(false);
        }
    }

    private void resetPreviousValueActionIfNecessary() {
        if(actionable.isPreviousValueAction()) {
            actionable.setPreviousValueAction(false);
        }
    }

    private void resetPreviousFloatActionIfNecessary() {
        if(actionable.isPreviousFloatAction()) {
            actionable.setPreviousFloatAction(false);
        }
    }

    private String getValueCallAction(double handStrengthAgainstRange) {
        String valueCallAction = null;

        double amountToCallBb = (actionable.getOpponentTotalBetSize() - actionable.getBotTotalBetSize()) / bigBlind;
        double handStrengthNeededToCall = getHandStrengthNeededToCall();

        if(amountToCallBb < 4) {
            if(handStrengthAgainstRange > handStrengthNeededToCall) {
                valueCallAction = CALL;
            }
        } else if(amountToCallBb <= 75) {
            if(handStrengthAgainstRange > handStrengthNeededToCall) {
                if(handStrengthAgainstRange >= 0.4) {
                    valueCallAction = CALL;
                }
            }
        } else {
            if(handStrengthAgainstRange > handStrengthNeededToCall && handStrengthAgainstRange >= 0.93) {
                valueCallAction = CALL;
            }
        }

        if(valueCallAction != null) {
            System.out.println("value call action");
        }
        return valueCallAction;
    }

    private String getDrawCallingAction() {
        String drawCallingAction;
        double amountToCall = (actionable.getOpponentTotalBetSize() - actionable.getBotTotalBetSize());
        double odds = amountToCall / (actionable.getPotSize() + actionable.getOpponentTotalBetSize() + actionable.getBotTotalBetSize());

        if(actionable.isBotIsButton()) {
            drawCallingAction = getIpDrawCallingAction(amountToCall, odds);
        } else {
            drawCallingAction = getOopDrawCallingAction(amountToCall, odds);
        }

        if(drawCallingAction != null) {
            System.out.println("draw call action");
        }
        return drawCallingAction;
    }

    private String getIpDrawCallingAction(double amountToCall, double odds) {
        String ipDrawCallingAction = null;

        if(board.size() == 3 || board.size() == 4) {
            if(amountToCall / bigBlind < 4) {
                if(handEvaluator.hasAnyDrawNonBackDoor()) {
                    ipDrawCallingAction = CALL;
                }
                if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                    ipDrawCallingAction = CALL;
                }
            } else if(amountToCall / bigBlind >= 4 && amountToCall / bigBlind < 20) {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    ipDrawCallingAction = CALL;
                }
                if(handEvaluator.hasDrawOfType("strongGutshot") || handEvaluator.hasDrawOfType("strongOvercards")) {
                    if(odds <= 0.45) {
                        ipDrawCallingAction = CALL;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                    if(odds <= 0.45) {
                        if(Math.random() < 0.06) {
                            ipDrawCallingAction = CALL;
                        }
                    }
                }
            } else if (amountToCall / bigBlind > 20 && amountToCall / bigBlind < 40) {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(odds <= 0.45) {
                        ipDrawCallingAction = CALL;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongGutshot")) {
                    if(odds <= 0.45) {
                        if(Math.random() < 0.5) {
                            ipDrawCallingAction = CALL;
                        }
                    }
                }
            }
        }
        return ipDrawCallingAction;
    }

    private String getOopDrawCallingAction(double amountToCall, double odds) {
        String oopDrawCallingAction = null;

        if(board.size() == 3 || board.size() == 4) {
            if(amountToCall / bigBlind < 4) {
                if(handEvaluator.hasAnyDrawNonBackDoor()) {
                    oopDrawCallingAction = CALL;
                }
                if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                    oopDrawCallingAction = CALL;
                }
            } else if(amountToCall / bigBlind >= 4 && amountToCall / bigBlind < 20) {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(odds <= 0.48) {
                        if(board.size() == 3) {
                            oopDrawCallingAction = CALL;
                        } else {
                            if(Math.random() < 0.2) {
                                oopDrawCallingAction = CALL;
                            }
                        }
                    }
                }
                if(handEvaluator.hasDrawOfType("strongGutshot")) {
                    if(odds <= 0.46) {
                        if(board.size() == 3) {
                            if(Math.random() < 0.3) {
                                oopDrawCallingAction = CALL;
                            }
                        }
                    }
                }
                if(handEvaluator.hasDrawOfType("strongOvercards")) {
                    if(odds <= 0.46) {
                        if(board.size() == 3) {
                            if(Math.random() < 0.18) {
                                oopDrawCallingAction = CALL;
                            }
                        }
                    }
                }
            } else if (amountToCall / bigBlind > 20 && amountToCall / bigBlind < 40) {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(odds <= 0.46) {
                        if(Math.random() < 0.35) {
                            oopDrawCallingAction = CALL;
                        }
                    }
                }
            }
        }
        return oopDrawCallingAction;
    }

    private String getFloatAction(String bettingAction) {
        String floatAction = null;

        if(board.size() == 3 || board.size() == 4) {
            if(actionable.isBotIsButton()) {
                double amountToCall = 0;

                if(bettingAction.equals(BET)) {
                    amountToCall = actionable.getOpponentTotalBetSize();
                } else if(bettingAction.equals(RAISE)) {
                    amountToCall = actionable.getOpponentTotalBetSize() - actionable.getBotTotalBetSize();
                }

                if(enoughRemainingAfterFloat()) {
                    if(amountToCall / bigBlind < 5) {
                        if(Math.random() < 0.35) {
                            floatAction = CALL;
                            actionable.setPreviousFloatAction(true);
                        }
                    } else if(amountToCall / bigBlind < 10) {
                        if(Math.random() < 0.30) {
                            floatAction = CALL;
                            actionable.setPreviousFloatAction(true);
                        }
                    } else if(amountToCall / bigBlind < 20) {
                        if(Math.random() < 0.20) {
                            floatAction = CALL;
                            actionable.setPreviousFloatAction(true);
                        }
                    } else if(amountToCall / bigBlind < 30) {
                        if(Math.random() < 0.10) {
                            floatAction = CALL;
                            actionable.setPreviousFloatAction(true);
                        }
                    }
                    else if(amountToCall / bigBlind < 40) {
                        if(Math.random() < 0.06) {
                            floatAction = CALL;
                            actionable.setPreviousFloatAction(true);
                        }
                    }
                }
            }
        }

        if(floatAction != null) {
            System.out.println("float call action");
        }
        return floatAction;
    }

    private boolean enoughRemainingAfterFloat() {
        double percentageBetRemainingAfterFloat;
        double potSize = actionable.getPotSize();
        double opponentTotalBetSize = actionable.getOpponentTotalBetSize();
        double botTotalBetSize = actionable.getBotTotalBetSize();
        double botStack = actionable.getBotStack();
        double opponentStack = actionable.getOpponentStack();

        double potSizeAfterCall = potSize + (2 * opponentTotalBetSize);
        double botStackAfterCall = botStack - (opponentTotalBetSize - botTotalBetSize);

        if(botStackAfterCall > opponentStack) {
            percentageBetRemainingAfterFloat = opponentStack / potSizeAfterCall;
        } else {
            percentageBetRemainingAfterFloat = botStackAfterCall / potSizeAfterCall;
        }

        if(percentageBetRemainingAfterFloat >= 0.7) {
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

    private boolean bluffOddsAreOk(double sizing) {
        double potSize = actionable.getPotSize();
        double opponentBetSize = actionable.getOpponentTotalBetSize();
        double opponentStack = actionable.getOpponentStack();
        double amountOpponentHasToCall = sizing - opponentBetSize;

        if(amountOpponentHasToCall > opponentStack) {
            amountOpponentHasToCall = opponentStack;
        }

        double opponentOdds = amountOpponentHasToCall / (potSize + opponentBetSize + sizing);

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
        return riverSizing;
    }

    private double getEffectiveStack(double botStack, double opponentStack) {
        if(botStack > opponentStack) {
            return opponentStack;
        } else {
            return botStack;
        }
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
}
