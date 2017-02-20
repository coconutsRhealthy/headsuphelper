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

    public double getSize() {
        double opponentBetSize = actionable.getOpponentTotalBetSize();
        double potSize = actionable.getPotSize();
        double computerStack = actionable.getBotStack();
        double size;

        if(opponentBetSize == 0) {
            if(0.75 * potSize < computerStack) {
                size = 0.75 * potSize;
            } else {
                size = computerStack;
            }
        } else {
            if((1.75 * opponentBetSize) + (0.75 * potSize) < computerStack) {
                size = (1.75 * opponentBetSize) + (0.75 * potSize);
            } else {
                size = computerStack;
            }

        }
        return size;
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
        double sizing = getSize();
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
        } else {
            if(handStrengthAgainstRange > 0.80) {
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
