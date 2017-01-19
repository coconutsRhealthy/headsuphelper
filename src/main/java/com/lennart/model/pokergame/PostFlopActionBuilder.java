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
    private final String _1BET = "1bet";
    private final String _2BET = "2bet";
    private final String CALL_1_BET = "call1bet";
    private final String CALL_2_BET = "call2bet";
    private final String CALL_3_BET = "call3bet";

    private BoardEvaluator boardEvaluator;
    private HandEvaluator handEvaluator;
    private String handPath;
    private ComputerGame computerGame;

    public PostFlopActionBuilder(BoardEvaluator boardEvaluator, HandEvaluator handEvaluator, ComputerGame computerGame) {
        this.boardEvaluator = boardEvaluator;
        this.handEvaluator = handEvaluator;
        this.computerGame = computerGame;
        handPath = computerGame.getHandPath();
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

    public double getSize(double potSize) {
        //dit maken zodat je altijd tussen 2.3 en 2.5 potodds geeft..

        return 0.75 * potSize;
    }

    private String getIpAction(double handStrengthAgainstRange) {
        if(handPath.contains("Fcheck")) {
            return getIpFCheck(handStrengthAgainstRange);
        }
        if(handPath.contains("F1bet")) {
            return getIpF1bet(handStrengthAgainstRange);
        }
        if(handPath.contains("F2bet")) {
            return getIpF2bet(handStrengthAgainstRange);
        }
        if(handPath.contains("F3bet")) {
            return getIpF3bet(handStrengthAgainstRange);
        }
        return null;
    }

    private String getOopAction(double handStrengthAgainstRange) {
        if(!handPath.contains("F")) {
            return getOopFirstToAct(handStrengthAgainstRange);
        }
        if(handPath.contains("F1bet")) {
            return getOopF1bet(handStrengthAgainstRange);
        }
        if(handPath.contains("F2bet")) {
            return getOopF2bet(handStrengthAgainstRange);
        }
        if(handPath.contains("F3bet")) {
            return getOopF3bet(handStrengthAgainstRange);
        }
        return null;
    }

    private String getIpFCheck(double handStrengthAgainstRange) {
        if(handStrengthAgainstRange > 0.6) {
            return getValueAction(_1BET, CHECK);
        }

        String drawAction = getDrawAction(_1BET);

        if(drawAction != null) {
            return drawAction;
        } else {
            return getBluffAction(_1BET, CHECK, handStrengthAgainstRange);
        }
    }

    private String getIpF1bet(double handStrengthAgainstRange) {
        return getF1bet(handStrengthAgainstRange);
    }

    private String getIpF2bet(double handStrengthAgainstRange) {
        return getFhigherThan1Bet(handStrengthAgainstRange, CALL_2_BET);
    }

    private String getIpF3bet(double handStrengthAgainstRange) {
        return getFhigherThan1Bet(handStrengthAgainstRange, CALL_3_BET);
    }

    private String getOopFirstToAct(double handStrengthAgainstRange) {
        if(myLastActionWasCall()) {
            return CHECK;
        } else {
            if (handStrengthAgainstRange > 0.6) {
                return getValueAction(_1BET, CHECK);
            }

            String drawAction = getDrawAction(_1BET);

            if(drawAction != null) {
                return drawAction;
            } else {
                return getBluffAction(_1BET, CHECK, handStrengthAgainstRange);
            }
        }
    }

    private String getOopF1bet(double handStrengthAgainstRange) {
        return getF1bet(handStrengthAgainstRange);
    }

    private String getOopF2bet(double handStrengthAgainstRange) {
        return getFhigherThan1Bet(handStrengthAgainstRange, CALL_2_BET);
    }

    private String getOopF3bet(double handStrengthAgainstRange) {
        return getFhigherThan1Bet(handStrengthAgainstRange, CALL_3_BET);
    }

    private String getF1bet(double handStrengthAgainstRange) {
        if(handEvaluator.isSingleBetPot()) {
            if(handStrengthAgainstRange > 0.7) {
                return getValueAction(_2BET, CALL_1_BET);
            } else {
                String drawAction = getDrawAction(_2BET);
                if(handStrengthAgainstRange > getHandStrengthNeededToCall()) {
                    if(drawAction != null) {
                        return drawAction;
                    } else {
                        if(computerGame.getBoard().size() != 5) {
                            if(Math.random() < 0.8) {
                                return CALL_1_BET;
                            } else {
                                return _2BET;
                            }
                        }
                        return CALL_1_BET;
                    }
                } else {
                    if(drawAction != null) {
                        return drawAction;
                    } else {
                        return getBluffAction(_2BET, FOLD, handStrengthAgainstRange);
                    }
                }
            }
        } else {
            if(handStrengthAgainstRange > getHandStrengthNeededToCall()) {
                return CALL_1_BET;
            }
            if(getDrawCallingAction().contains("call")) {
                return CALL_1_BET;
            }
            return FOLD;
        }
    }

    private String getFhigherThan1Bet(double handStrengthAgainstRange, String callAction) {
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
            if(!computerGame.isComputerIsButton() && !handPath.contains("F")) {
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
        if (bettingAction.equals(_1BET)) {
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
                    if (bettingAction.equals(_1BET)) {
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
                    if (bettingAction.equals(_1BET)) {
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
                    if (bettingAction.equals(_1BET)) {
                        return CHECK;
                    } else {
                        return getDrawCallingAction();
                    }
                }
            }
        }
        if (bettingAction.equals(_1BET)) {
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
