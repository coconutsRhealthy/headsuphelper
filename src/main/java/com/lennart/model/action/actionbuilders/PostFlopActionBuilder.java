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

    private BoardEvaluator boardEvaluator;
    private HandEvaluator handEvaluator;
    private Actionable actionable;

    public PostFlopActionBuilder(BoardEvaluator boardEvaluator, HandEvaluator handEvaluator, Actionable actionable) {
        bigBlind = actionable.getBigBlind();
        this.boardEvaluator = boardEvaluator;
        this.handEvaluator = handEvaluator;
        this.actionable = actionable;
    }

    public String getAction(Set<Set<Card>> opponentRange) {
        double handStrengthAgainstRange = handEvaluator.getHandStrengthAgainstRange(actionable.getBotHoleCards(),
                opponentRange, boardEvaluator.getSortedCombosNew());

        System.out.println("Computer handstrength: " + handStrengthAgainstRange);

        if(actionable.isBotIsButton()) {
            return getIpAction(handStrengthAgainstRange);
        }

        if(!actionable.isBotIsButton()) {
            return getOopAction(handStrengthAgainstRange);
        }
        return null;
    }

    private String getIpAction(double handStrengthAgainstRange) {
        String opponentAction = actionable.getOpponentAction();

        if(opponentAction.contains(CHECK)) {
            return getIpFcheck(handStrengthAgainstRange);
        }
        if(opponentAction.contains(BET)) {
            return getIpFbet(handStrengthAgainstRange);
        }
        if(opponentAction.contains(RAISE)) {
            return getIpFraise(handStrengthAgainstRange);
        }
        return null;
    }

    private String getOopAction(double handStrengthAgainstRange) {
        String opponentAction = actionable.getOpponentAction();

        if(opponentAction == null) {
            return getOopFirstToAct(handStrengthAgainstRange);
        }
        if(opponentAction.contains(BET)) {
            return getOopFbet(handStrengthAgainstRange);
        }
        if(opponentAction.contains(RAISE)) {
            return getOopFraise(handStrengthAgainstRange);
        }
        return null;
    }

    private String getIpFcheck(double handStrengthAgainstRange) {
        return getFcheckOrFirstToAct(handStrengthAgainstRange);
    }

    private String getIpFbet(double handStrengthAgainstRange) {
        return getFbet(handStrengthAgainstRange);
    }

    private String getIpFraise(double handStrengthAgainstRange) {
        return getFraise(handStrengthAgainstRange);
    }

    private String getOopFirstToAct(double handStrengthAgainstRange) {
        return getFcheckOrFirstToAct(handStrengthAgainstRange);
    }

    private String getOopFbet(double handStrengthAgainstRange) {
        return getFbet(handStrengthAgainstRange);
    }

    private String getOopFraise(double handStrengthAgainstRange) {
        return getFraise(handStrengthAgainstRange);
    }

    private String getFcheckOrFirstToAct(double handStrengthAgainstRange) {
        String action = getValueAction(handStrengthAgainstRange, BET);

        if(action == null) {
            action = getDrawBettingAction(BET);
        }
        if(action == null) {
            action = getBluffAction(handStrengthAgainstRange);
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
            action = getBluffAction(handStrengthAgainstRange);
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
        String action;

        if(actionable.getBoard().size() < 5) {
            action = getValueCallAction(handStrengthAgainstRange);

            if(action == null) {
                action = getDrawCallingAction();
            }
            if(action == null) {
                System.out.println("default fold in getFraise()");
                action = FOLD;
            }
        } else {
            action = getValueAction(handStrengthAgainstRange, RAISE);

            if(action == null) {
                action = getBluffAction(handStrengthAgainstRange);
            }
            if(action == null) {
                action = getValueCallAction(handStrengthAgainstRange);
            }
            if(action == null) {
                System.out.println("default fold in getFraise()");
                action = FOLD;
            }
        }
        return action;
    }

    private String getValueAction(double handStrengthAgainstRange, String bettingAction) {
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
            System.out.println("value action");
        }
        return valueAction;
    }

    private String getDrawBettingAction(String bettingAction) {
        String drawBettingAction = null;
        List<Card> board = boardEvaluator.getBoard();

        if(board.size() == 3 || board.size() == 4) {
            double sizing = getSizing();

            if(sizing / bigBlind <= 5) {
                if(handEvaluator.hasAnyDrawNonBackDoor()) {
                    if(Math.random() < 0.5) {
                        drawBettingAction = bettingAction;
                    }
                }
            } else if (sizing / bigBlind > 5 && sizing / bigBlind <= 20){
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(Math.random() < 0.5) {
                        drawBettingAction = bettingAction;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongGutshot")) {
                    if(Math.random() < 0.2) {
                        drawBettingAction = bettingAction;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongOvercards")) {
                    if(Math.random() < 0.15) {
                        drawBettingAction = bettingAction;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                    if(Math.random() < 0.07) {
                        drawBettingAction = bettingAction;
                    }
                }
            } else if (sizing / bigBlind > 20 && sizing / bigBlind <= 40) {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(Math.random() < 0.2) {
                        drawBettingAction = bettingAction;
                    }
                }
                if(handEvaluator.hasDrawOfType("strongGutshot")) {
                    if(Math.random() < 0.07) {
                        drawBettingAction = bettingAction;
                    }
                }
            } else {
                if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                    if(Math.random() < 0.1) {
                        drawBettingAction = bettingAction;
                    }
                }
            }
        }

        if(drawBettingAction != null) {
            System.out.println("draw betting action");
        }
        return drawBettingAction;
    }

    private String getTrickyRaiseAction(double handStrengthAgainstRange) {
        String trickyRaiseAction = null;
        List<Card> board = actionable.getBoard();
        double sizing = getSizing();

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
        return trickyRaiseAction;
    }

    private String getBluffAction(double handStrengthAgainstRange) {
        String bluffAction = null;
        double sizing = getSizing();

        if(actionable.getBoard().size() == 5 && bluffOddsAreOk(sizing)) {
            if(handStrengthAgainstRange < 0.7) {
                if(sizing / bigBlind <= 20) {
                    if(Math.random() < 0.21) {
                        bluffAction = RAISE;
                    }
                } else if(sizing / bigBlind > 20 && sizing <= 40) {
                    if(Math.random() < 0.15) {
                        bluffAction = RAISE;
                    }
                } else {
                    if(Math.random() < 0.10) {
                        bluffAction = RAISE;
                    }
                }
            }
        }

        if(bluffAction != null) {
            System.out.println("bluff action");
        }
        return bluffAction;
    }

    private String getValueCallAction(double handStrengthAgainstRange) {
        String valueCallAction = null;

        if(handStrengthAgainstRange > getHandStrengthNeededToCall()) {
            valueCallAction = CALL;
        }

        if(valueCallAction != null) {
            System.out.println("value call action");
        }
        return valueCallAction;
    }

    private String getDrawCallingAction() {
        String drawCallingAction = null;
        List<Card> board = boardEvaluator.getBoard();

        double amountToCall = (actionable.getOpponentTotalBetSize() - actionable.getBotTotalBetSize());
        double odds = amountToCall / actionable.getPotSize();
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
                    if(odds <= 0.75) {
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
                    if(odds <= 0.75) {
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
                    if(odds <= 0.75) {
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
        if(actionable.getBoard().size() != 5) {
            if(Math.random() < 0.8) {
                return bettingAction;
            } else {
                return null;
            }
        } else {
            String opponentAction = actionable.getOpponentAction();
            if(!actionable.isBotIsButton() && opponentAction == null) {
                if(Math.random() < 0.8) {
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

        if(opponentOdds >= 0.7) {
            return true;
        }
        return false;
    }

    public double getSizing() {
        double sizing = 0;
        List<Card> board = boardEvaluator.getBoard();

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
                double raiseAmount = calculateRaiseAmount(opponentBetSize, potSize, 2.33);

                if(botStack <= 1.2 * raiseAmount) {
                    flopSizing = botStack;
                } else {
                    flopSizing = raiseAmount;
                }
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
                double raiseAmount = calculateRaiseAmount(opponentBetSize, potSize, 2.33);

                if(botStack <= 1.2 * raiseAmount) {
                    turnSizing = botStack;
                } else {
                    turnSizing = raiseAmount;
                }
            }
        }
        return turnSizing;
    }

    private double getRiverSizing() {
        double riverSizing;

        double opponentBetSize = actionable.getOpponentTotalBetSize();
        double potSize = actionable.getPotSize();
        double botStack = actionable.getBotStack();

        if(opponentBetSize == 0) {
            if(botStack <= 1.2 * potSize) {
                riverSizing = botStack;
            } else {
                riverSizing = 0.75 * potSize;
            }
        } else {
            double raiseAmount = calculateRaiseAmount(opponentBetSize, potSize, 2.33);

            if(botStack <= 1.2 * raiseAmount) {
                riverSizing = botStack;
            } else {
                riverSizing = raiseAmount;
            }
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

    private double calculateRaiseAmount(double facingBetSize, double potSize, double odds) {
        return (potSize / (odds - 1)) + (((odds + 1) * facingBetSize) / (odds - 1));
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
