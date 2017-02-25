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

    private List<String> actionHistory;

    public PostFlopActionBuilder(BoardEvaluator boardEvaluator, HandEvaluator handEvaluator, Actionable actionable) {
        bigBlind = actionable.getBigBlind();
        this.boardEvaluator = boardEvaluator;
        this.handEvaluator = handEvaluator;
        this.actionable = actionable;
        actionHistory = actionable.getActionHistory();
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

    private String getIpAction(double handStrengthAgainstRange) {
        String opponentAction = actionable.getOpponentAction();

        if(opponentAction.contains(CHECK)) {
            return getIpFCheck(handStrengthAgainstRange);
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

    private String getIpFCheck(double handStrengthAgainstRange) {
        String valueAction = getValueAction(handStrengthAgainstRange, BET, CHECK);

        if(valueAction != null) {
            return valueAction;
        } else {
            String drawAction = getDrawAction(BET);

            if(drawAction != null && !drawAction.contains(CHECK)) {
                return drawAction;
            } else {
                return getBluffAction(BET, CHECK, handStrengthAgainstRange);
            }
        }
    }

    private String getIpFbet(double handStrengthAgainstRange) {
        return getFbet(handStrengthAgainstRange);
    }

    private String getIpFraise(double handStrengthAgainstRange) {
        return getFraise(handStrengthAgainstRange);
    }

    private String getOopFirstToAct(double handStrengthAgainstRange) {
        String valueAction = getValueAction(handStrengthAgainstRange, BET, CHECK);

        if(valueAction != null) {
            return valueAction;
        } else {
            String drawAction = getDrawAction(BET);

            if(drawAction != null && !drawAction.contains(CHECK)) {
                return drawAction;
            } else {
                return getBluffAction(BET, CHECK, handStrengthAgainstRange);
            }
        }
    }

    private String getOopFbet(double handStrengthAgainstRange) {
        return getFbet(handStrengthAgainstRange);
    }

    private String getOopFraise(double handStrengthAgainstRange) {
        return getFraise(handStrengthAgainstRange);
    }

    private String getFbet(double handStrengthAgainstRange) {
        if(handEvaluator.isSingleBetPot(actionHistory)) {
            String valueAction = getValueAction(handStrengthAgainstRange, RAISE, CALL);

            if(valueAction != null) {
                return valueAction;
            } else {
                String drawAction = getDrawAction(RAISE);
                if(handStrengthAgainstRange > getHandStrengthNeededToCall()) {
                    if(drawAction != null && !drawAction.contains(FOLD)) {
                        return drawAction;
                    } else {
                        if(actionable.getBoard().size() != 5) {
                            if(Math.random() < 0.8) {
                                System.out.println("Value call of bet");
                                return CALL;
                            } else {
                                System.out.println("Tricky raise against bet");
                                return RAISE;
                            }
                        }
                        System.out.println("Value call of bet");
                        return CALL;
                    }
                } else {
                    if(drawAction != null && !drawAction.contains(FOLD)) {
                        return drawAction;
                    } else {
                        return getBluffAction(RAISE, FOLD, handStrengthAgainstRange);
                    }
                }
            }
        } else {
            if(actionable.getBoard().size() == 5) {
                String valueAction = getValueAction(handStrengthAgainstRange, RAISE, CALL);
                if(valueAction != null) {
                    return valueAction;
                }
            }

            if(handStrengthAgainstRange > getHandStrengthNeededToCall()) {
                System.out.println("Value call in bigger pot");
                return CALL;
            }
            if(getDrawCallingAction().contains(CALL)) {
                return CALL;
            }
            double bigBluffLessProbableFactor = 0.24;
            if(Math.random() < bigBluffLessProbableFactor) {
                return getBluffAction(RAISE, FOLD, handStrengthAgainstRange);
            }

            System.out.println("No value call, no draw-call and no bluffraise in bigger pot. Fold.");
            return FOLD;
        }
    }

    private String getValueAction(double handStrengthAgainstRange, String bettingAction, String passiveAction) {
        String valueAction;
        double sizing = getSizing();
        if(sizing / bigBlind <= 5) {
            if(handStrengthAgainstRange > 0.44) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction, passiveAction);
            } else {
                valueAction = null;
            }
        } else if (sizing / bigBlind > 5 && sizing / bigBlind <= 20){
            if(handStrengthAgainstRange > 0.66) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction, passiveAction);
            } else {
                valueAction = null;
            }
        } else if (sizing / bigBlind > 20 && sizing / bigBlind <= 40) {
            if(handStrengthAgainstRange > 0.75) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction, passiveAction);
            } else {
                valueAction = null;
            }
        } else if (sizing / bigBlind > 40 && sizing / bigBlind <= 70) {
            if(handStrengthAgainstRange > 0.80) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction, passiveAction);
            } else {
                valueAction = null;
            }
        } else {
            if(handStrengthAgainstRange > 0.95) {
                valueAction = getPassiveOrAggressiveValueAction(bettingAction, passiveAction);
            } else {
                valueAction = null;
            }
        }
        return valueAction;
    }

    private String getFraise(double handStrengthAgainstRange) {
        if(handStrengthAgainstRange > getHandStrengthNeededToCall()) {
            System.out.println("Value call of raise");
            return CALL;
        } else {
            if(getDrawCallingAction().contains("call")) {
                return CALL;
            } else {
                System.out.println("No value call and no draw call against raise. Fold.");
                return FOLD;
            }
        }
    }

    private String getPassiveOrAggressiveValueAction(String bettingAction, String passiveAction) {
        if(actionable.getBoard().size() != 5) {
            if(Math.random() < 0.8) {
                System.out.println("Betting value action");
                return bettingAction;
            } else {
                System.out.println("Passive value action");
                return passiveAction;
            }
        } else {
            String opponentAction = actionable.getOpponentAction();
            if(!actionable.isBotIsButton() && opponentAction == null) {
                if(Math.random() < 0.8) {
                    System.out.println("Betting value action at river OOP");
                    return bettingAction;
                } else {
                    System.out.println("Passive value action at river OOP");
                    return passiveAction;
                }
            } else {
                System.out.println("River IP pure value betting action");
                return bettingAction;
            }
        }
    }

    private String getDrawAction(String bettingAction) {
        if (bettingAction.equals(BET) && actionable.getPotSize() / actionable.getBigBlind() < 7) {
            if (handEvaluator.hasAnyDrawNonBackDoor()) {
                if (Math.random() < 0.68) {
                    System.out.println("Betting action with any draw non backdoor");
                    return bettingAction;
                } else {
                    System.out.println("Check action with any draw non backdoor");
                    return CHECK;
                }
            }
            if (handEvaluator.hasDrawOfType("strongBackDoor")) {
                if (Math.random() < 0.20) {
                    System.out.println("Betting action with strongBackDoor");
                    return bettingAction;
                } else {
                    System.out.println("Check action with strongBackDoor");
                    return CHECK;
                }
            }
            if (handEvaluator.hasDrawOfType("mediumBackDoor")) {
                if (Math.random() < 0.10) {
                    System.out.println("Betting action with mediumBackDoor");
                    return bettingAction;
                } else {
                    System.out.println("Check action with mediumBackDoor");
                    return CHECK;
                }
            }
            if (handEvaluator.hasDrawOfType("weakBackDoor")) {
                if (Math.random() < 0.05) {
                    System.out.println("Betting action with weakBackDoor");
                    return bettingAction;
                } else {
                    System.out.println("Check action with weakBackDoor");
                    return CHECK;
                }
            }
        } else {
            if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd") ||
                    handEvaluator.hasDrawOfType("strongOvercards")) {
                if (Math.random() < 0.50) {
                    System.out.println(bettingAction + " with strongFd, StrongOosd, strongOvercards");
                    return bettingAction;
                } else {
                    if (bettingAction.equals(BET)) {
                        System.out.println("Check action with strongFd, StrongOosd, strongOvercards");
                        return CHECK;
                    } else {
                        return getDrawCallingAction();
                    }
                }
            }
            if(handEvaluator.hasDrawOfType("strongGutshot")) {
                if (Math.random() < 0.38) {
                    System.out.println("Betting action with strongGutshot");
                    return bettingAction;
                } else {
                    if (bettingAction.equals(BET)) {
                        System.out.println("Checking action with strongGutshot");
                        return CHECK;
                    } else {
                        return getDrawCallingAction();
                    }
                }
            }
            if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                if (Math.random() < 0.18) {
                    System.out.println("Betting action with strongBackDoor");
                    return bettingAction;
                } else {
                    if (bettingAction.equals(BET)) {
                        System.out.println("Check action with strongBackDoor");
                        return CHECK;
                    } else {
                        return getDrawCallingAction();
                    }
                }
            }
        }
        return null;
    }

    private String getBluffAction(String bettingAction, String passiveAction, double handStrengthAgainstRange) {
        if(Math.random() < 0.21) {
            System.out.println("Bluff betting action!");
            return bettingAction;
        } else {
            System.out.println("getBluffAction() resulted in check or fold. No bluff");
            return passiveAction;
        }

        //use this implementation later..
//        int numberOfArrivedDraws = boardEvaluator.getNumberOfArrivedDraws();
//        int numberOfArrivedDrawsInYourPerceivedRange =
//                handEvaluator.getNumberOfArrivedDrawsInRange("myPerceivedRange");
//        int numberOfArrivedDrawsInOpponentRange =
//                handEvaluator.getNumberOfArrivedDrawsInRange("opponentRange");
//        double percentageOfYourPerceivedRangeThatHitsFlopRanks =
//                handEvaluator.getPercentageOfYourPerceivedRangeThatHitsFlopRanks();
//        double percentageOfYourPerceivedRangeThatHitsNewCard =
//                handEvaluator.getPercentageOfYourPerceivedRangeThatHitsNewCard();
//
//        if(handStrengthAgainstRange < 0.45) {
//            if(boardEvaluator.boardIsDry()&& boardIsSingleRaisedAndNoBettingPostFlop()) {
//                if(Math.random() < 0.7) {
//                    return bettingAction;
//                }
//            }
//
//            if(numberOfArrivedDraws > 3 && numberOfArrivedDrawsInYourPerceivedRange > (numberOfArrivedDraws / 3) &&
//                    numberOfArrivedDrawsInYourPerceivedRange > numberOfArrivedDrawsInOpponentRange) {
//                if(Math.random() < 0.8) {
//                    return bettingAction;
//                }
//            }
//
//            if(actionable.getBoard().size() == 3) {
//                if(percentageOfYourPerceivedRangeThatHitsFlopRanks > 0.5) {
//                    if(Math.random() < 0.8) {
//                        return bettingAction;
//                    }
//                }
//            } else {
//                if(percentageOfYourPerceivedRangeThatHitsNewCard > 0.5) {
//                    if(Math.random() < 0.8) {
//                        return bettingAction;
//                    }
//                }
//            }
//        }
//        return passiveAction;
    }

    private boolean boardIsSingleRaisedAndNoBettingPostFlop() {
        //TODO: implement this method
        return false;
    }

    private double getHandStrengthNeededToCall() {
        double amountToCall = actionable.getOpponentTotalBetSize() - actionable.getBotTotalBetSize();
        double potSize = actionable.getPotSize();
        return (0.01 + amountToCall) / (potSize + amountToCall);
    }

    private String getDrawCallingAction() {
        double potSizeInBb = actionable.getPotSize() / actionable.getBigBlind();

        if(potSizeInBb <= 7) {
            if(handEvaluator.hasAnyDrawNonBackDoor()) {
                System.out.println("Draw call: any non backdoor draw");
                return "call";
            }
            if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                System.out.println("Draw call: strong backdoor");
                return "call";
            }
        }

        if(potSizeInBb > 7 && potSizeInBb <= 15) {
            if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd") ||
                    handEvaluator.hasDrawOfType("strongGutshot") || handEvaluator.hasDrawOfType("strongOvercards")) {
                System.out.println("Draw call: strongFd, strongOosd, strongGutshot, strongOvercards");
                return "call";
            }
            if(handEvaluator.hasDrawOfType("mediumFlushDraw") || handEvaluator.hasDrawOfType("mediumOosd") ||
                    handEvaluator.hasDrawOfType("mediumGutshot") || handEvaluator.hasDrawOfType("mediumOvercards")) {
                if(Math.random() < 0.50) {
                    System.out.println("Draw call: mediumFd, mediumOosd, mediumGutshot, mediumOvercards");
                    return "call";
                }
            }
            if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                if(Math.random() < 0.15) {
                    System.out.println("Draw call: strongBackDoor");
                    return "call";
                }
            }
        }

        if(potSizeInBb > 15 && potSizeInBb <= 25) {
            if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd") ||
                    handEvaluator.hasDrawOfType("strongGutshot")) {
                System.out.println("Draw call: strongFd, strongOosd, strongGutshot");
                return "call";
            }
        }

        if(potSizeInBb > 25) {
            if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                System.out.println("Draw call: strongFd, strongOosd");
                return "call";
            }
        }

        System.out.println("getDrawCallingAction() resulted in fold");
        return "fold";
    }
}
