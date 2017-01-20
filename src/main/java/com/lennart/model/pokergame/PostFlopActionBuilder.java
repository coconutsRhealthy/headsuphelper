package com.lennart.model.pokergame;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import org.apache.commons.lang3.StringUtils;

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

    private BoardEvaluator boardEvaluator;
    private HandEvaluator handEvaluator;
    private ComputerGame computerGame;

    public PostFlopActionBuilder(BoardEvaluator boardEvaluator, HandEvaluator handEvaluator, ComputerGame computerGame) {
        this.boardEvaluator = boardEvaluator;
        this.handEvaluator = handEvaluator;
        this.computerGame = computerGame;
    }

    public String getAction(Set<Set<Card>> opponentRange) {
        double handStrengthAgainstRange = handEvaluator.getHandStrengthAgainstRange(computerGame.getComputerHoleCards(),
                opponentRange, boardEvaluator.getSortedCombosNew());

        if(computerGame.isComputerIsButton()) {
            return getIpAction(handStrengthAgainstRange);
        }

        if(!computerGame.isComputerIsButton()) {
            return getOopAction(handStrengthAgainstRange);
        }
        return null;
    }

    public double getSize() {
        double opponentBetSize = computerGame.getMyTotalBetSize();
        double potSize = computerGame.getPotSize();
        double size;

        if(opponentBetSize == 0) {
            size = 0.75 * potSize;
        } else {
            size = (1.75 * opponentBetSize) + (0.75 * potSize);
        }
        return size;
    }

    private String getIpAction(double handStrengthAgainstRange) {
        String opponentAction = computerGame.getMyAction();

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
        String opponentAction = computerGame.getMyAction();

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
        if(handStrengthAgainstRange > 0.6) {
            return getValueAction(BET, CHECK);
        }

        String drawAction = getDrawAction(BET);

        if(drawAction != null) {
            return drawAction;
        } else {
            return getBluffAction(BET, CHECK, handStrengthAgainstRange);
        }
    }

    private String getIpFbet(double handStrengthAgainstRange) {
        return getFbet(handStrengthAgainstRange);
    }

    private String getIpFraise(double handStrengthAgainstRange) {
        return getFraise(handStrengthAgainstRange, CALL);
    }

    private String getOopFirstToAct(double handStrengthAgainstRange) {
        if(myLastActionWasCall()) {
            return CHECK;
        } else {
            if (handStrengthAgainstRange > 0.6) {
                return getValueAction(BET, CHECK);
            }

            String drawAction = getDrawAction(BET);

            if(drawAction != null) {
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
        return getFraise(handStrengthAgainstRange, CALL);
    }

    private String getFbet(double handStrengthAgainstRange) {
        if(handEvaluator.isSingleBetPot()) {
            if(handStrengthAgainstRange > 0.7) {
                return getValueAction(RAISE, CALL);
            } else {
                String drawAction = getDrawAction(RAISE);
                if(handStrengthAgainstRange > getHandStrengthNeededToCall()) {
                    if(drawAction != null) {
                        return drawAction;
                    } else {
                        if(computerGame.getBoard().size() != 5) {
                            if(Math.random() < 0.8) {
                                return CALL;
                            } else {
                                return RAISE;
                            }
                        }
                        return CALL;
                    }
                } else {
                    if(drawAction != null) {
                        return drawAction;
                    } else {
                        return getBluffAction(RAISE, FOLD, handStrengthAgainstRange);
                    }
                }
            }
        } else {
            if(handStrengthAgainstRange > getHandStrengthNeededToCall()) {
                return CALL;
            }
            if(getDrawCallingAction().contains("call")) {
                return CALL;
            }
            return FOLD;
        }
    }

    private String getFraise(double handStrengthAgainstRange, String callAction) {
        if(handStrengthAgainstRange > getHandStrengthNeededToCall()) {
            return callAction;
        } else {
            if(getDrawCallingAction().contains("call")) {
                return callAction;
            } else {
                return FOLD;
            }
        }
    }

    private String getValueAction(String bettingAction, String passiveAction) {
        if(computerGame.getBoard().size() != 5) {
            if(Math.random() < 0.8) {
                return bettingAction;
            } else {
                return passiveAction;
            }
        } else {
            String opponentAction = computerGame.getMyAction();
            if(!computerGame.isComputerIsButton() && opponentAction == null) {
                if(Math.random() < 0.8) {
                    return bettingAction;
                } else {
                    return passiveAction;
                }
            } else {
                return bettingAction;
            }
        }
    }

    private String getDrawAction(String bettingAction) {
        if (bettingAction.equals(BET)) {
            if(computerGame.getPotSize() / computerGame.getBigBlind() < 7) {
                if (handEvaluator.hasAnyDrawNonBackDoor()) {
                    if (Math.random() < 0.68) {
                        return bettingAction;
                    } else {
                        return CHECK;
                    }
                }
                if (handEvaluator.hasDrawOfType("strongBackDoor")) {
                    if (Math.random() < 0.20) {
                        return bettingAction;
                    } else {
                        return CHECK;
                    }
                }
                if (handEvaluator.hasDrawOfType("mediumBackDoor")) {
                    if (Math.random() < 0.10) {
                        return bettingAction;
                    } else {
                        return CHECK;
                    }
                }
                if (handEvaluator.hasDrawOfType("weakBackDoor")) {
                    if (Math.random() < 0.05) {
                        return bettingAction;
                    } else {
                        return CHECK;
                    }
                }
            }
        } else {
            if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd") ||
                    handEvaluator.hasDrawOfType("strongOvercards")) {
                if (Math.random() < 0.50) {
                    return bettingAction;
                } else {
                    if (bettingAction.equals(BET)) {
                        return CHECK;
                    } else {
                        return getDrawCallingAction();
                    }
                }
            }
            if(handEvaluator.hasDrawOfType("strongGutshot")) {
                if (Math.random() < 0.38) {
                    return bettingAction;
                } else {
                    if (bettingAction.equals(BET)) {
                        return CHECK;
                    } else {
                        return getDrawCallingAction();
                    }
                }
            }
            if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                if (Math.random() < 0.18) {
                    return bettingAction;
                } else {
                    if (bettingAction.equals(BET)) {
                        return CHECK;
                    } else {
                        return getDrawCallingAction();
                    }
                }
            }
        }
        if (bettingAction.equals(BET)) {
            return CHECK;
        } else {
            return getDrawCallingAction();
        }
    }

    private String getBluffAction(String bettingAction, String passiveAction, double handStrengthAgainstRange) {
        if(Math.random() < 0.21) {
            return bettingAction;
        } else {
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
//            if(computerGame.getBoard().size() == 3) {
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

    private boolean myLastActionWasCall() {
        if(StringUtils.containsIgnoreCase(computerGame.getComputerWrittenAction(), "call")) {
            return true;
        }
        return false;
    }

    private boolean boardIsSingleRaisedAndNoBettingPostFlop() {
        //TODO: implement this method
        return false;
    }

    private double getHandStrengthNeededToCall() {
        double amountToCall = computerGame.getMyTotalBetSize() - computerGame.getComputerTotalBetSize();
        double potSize = computerGame.getPotSize();
        return (0.01 + amountToCall) / (potSize + amountToCall);
    }

    private String getDrawCallingAction() {
        double potSizeInBb = computerGame.getPotSize() / computerGame.getBigBlind();

        if(potSizeInBb <= 7) {
            if(handEvaluator.hasAnyDrawNonBackDoor()) {
                return "call";
            }
            if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                return "call";
            }
        }

        if(potSizeInBb > 7 && potSizeInBb <= 15) {
            if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd") ||
                    handEvaluator.hasDrawOfType("strongGutshot") || handEvaluator.hasDrawOfType("strongOvercards")) {
                return "call";
            }
            if(handEvaluator.hasDrawOfType("mediumFlushDraw") || handEvaluator.hasDrawOfType("mediumOosd") ||
                    handEvaluator.hasDrawOfType("mediumGutshot") || handEvaluator.hasDrawOfType("mediumOvercards")) {
                if(Math.random() < 0.50) {
                    return "call";
                }
            }
            if(handEvaluator.hasDrawOfType("strongBackDoor")) {
                if(Math.random() < 0.15) {
                    return "call";
                }
            }
        }

        if(potSizeInBb > 15 && potSizeInBb <= 25) {
            if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd") ||
                    handEvaluator.hasDrawOfType("strongGutshot")) {
                return "call";
            }
        }

        if(potSizeInBb > 25) {
            if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")) {
                return "call";
            }
        }

        return "fold";
    }
}
