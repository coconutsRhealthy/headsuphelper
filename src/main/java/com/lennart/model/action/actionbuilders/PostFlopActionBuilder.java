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
    private double sizing;

    private BoardEvaluator boardEvaluator;
    private HandEvaluator handEvaluator;
    private Actionable actionable;

    public PostFlopActionBuilder(BoardEvaluator boardEvaluator, HandEvaluator handEvaluator, Actionable actionable) {
        this.boardEvaluator = boardEvaluator;
        this.handEvaluator = handEvaluator;
        this.actionable = actionable;

        bigBlind = actionable.getBigBlind();
        board = actionable.getBoard();
        sizing = getSizing();
    }

    public String getAction(Set<Set<Card>> opponentRange) {
        String action = null;
        String opponentAction = actionable.getOpponentAction();

        double handStrengthAgainstRange = handEvaluator.getHandStrengthAgainstRange(actionable.getBotHoleCards(),
                opponentRange, boardEvaluator.getSortedCombosNew());

        System.out.println("Computer handstrength: " + handStrengthAgainstRange);

        if(opponentAction == null || opponentAction.contains(CHECK)) {
            action = getFcheckOrFirstToAct(handStrengthAgainstRange);
        }
        if(opponentAction != null && opponentAction.contains(BET)) {
            action = getFbet(handStrengthAgainstRange);
        }
        if(opponentAction != null && opponentAction.contains(RAISE)) {
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
            action = getTrickyRaiseAction(handStrengthAgainstRange);
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
            System.out.println("default fold in getFraise()");
            action = FOLD;
        }
        return action;
    }

    private String getValueAction(double handStrengthAgainstRange, String bettingAction) {
        String valueAction = null;

        if(getAmountToCall() < actionable.getBotStack()) {
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
                System.out.println("value action");
            }
        }
        return valueAction;
    }

    private String getDrawBettingAction(String bettingAction) {
        String drawBettingAction = null;

        if(getAmountToCall() < actionable.getBotStack()) {
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
                    if(Math.random() < 0.5) {
                        drawBettingInitializeAction = bettingAction;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongGutshot")) {
                    if(Math.random() < 0.2) {
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
            } else if (sizing / bigBlind > 20 && sizing / bigBlind <= 40) {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(Math.random() < 0.2) {
                        drawBettingInitializeAction = bettingAction;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongGutshot")) {
                    if(Math.random() < 0.07) {
                        drawBettingInitializeAction = bettingAction;
                    }
                }
            } else {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(Math.random() < 0.1) {
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

    private String getTrickyRaiseAction(double handStrengthAgainstRange) {
        String trickyRaiseAction = null;

        if(getAmountToCall() < actionable.getBotStack()) {
            if(handStrengthAgainstRange >= 0.6 && handStrengthAgainstRange < 0.8) {
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

    private String getBluffAction(String bettingAction, double handStrengthAgainstRange) {
        String bluffAction = null;

        if(getAmountToCall() < actionable.getBotStack()) {
            bluffAction = getBluffBarrelAction(bettingAction, handStrengthAgainstRange);

            if(bluffAction == null) {
                bluffAction = getBluffAfterMissedDrawAction(bettingAction);
            }
            if(bluffAction == null) {
                bluffAction = getBluffInitializeAction(bettingAction, handStrengthAgainstRange);
            }

            if(bluffAction != null) {
                System.out.println("bluff action");
            }
        }
        return bluffAction;
    }

    private String getBluffBarrelAction(String bettingAction, double handStrengthAgainstRange) {
        String bluffBarrelAction = null;

        if(actionable.isPreviousBluffAction()) {
            if (bluffOddsAreOk() && handStrengthAgainstRange < 0.7) {
                if (bettingAction.equals(BET)) {
                    if (actionable.isBotIsButton()) {
                        if (Math.random() <= 0.9) {
                            bluffBarrelAction = bettingAction;
                        }
                    } else {
                        if (Math.random() <= 0.5) {
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

    private String getBluffInitializeAction(String bettingAction, double handStrengthAgainstRange) {
        String bluffInitializeAction = null;

        if(bluffOddsAreOk() && handStrengthAgainstRange < 0.7) {
            if(bettingAction.equals(BET)) {
                if(sizing / bigBlind < 70) {
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
                } else {
                    if(Math.random() < 0.02) {
                        bluffInitializeAction = bettingAction;
                        actionable.setPreviousBluffAction(true);
                    }
                }
            } else {
                if(board.size() == 5) {
                    if(sizing / bigBlind < 70) {
                        if(Math.random() < 0.10) {
                            bluffInitializeAction = bettingAction;
                            actionable.setPreviousBluffAction(true);
                        }
                    } else {
                        if(Math.random() < 0.02) {
                            bluffInitializeAction = bettingAction;
                            actionable.setPreviousBluffAction(true);
                        }
                    }
                }
            }
        }
        return bluffInitializeAction;
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
                                if(Math.random() < 0.7) {
                                    drawCallingAction = CALL;
                                }
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
                            if(Math.random() < 0.3) {
                                drawCallingAction = CALL;
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
                    if(odds <= 0.45) {
                        if(board.size() == 3) {
                            drawCallingAction = CALL;
                        } else {
                            if(botIsButton) {
                                if(Math.random() < 0.67) {
                                    drawCallingAction = CALL;
                                }
                            } else {
                                if(Math.random() < 0.23) {
                                    drawCallingAction = CALL;
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
}
