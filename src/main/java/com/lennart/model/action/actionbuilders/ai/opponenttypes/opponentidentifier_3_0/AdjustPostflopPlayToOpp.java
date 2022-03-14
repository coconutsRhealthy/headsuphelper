package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0;

import com.lennart.model.card.Card;

import java.util.*;

/**
 * Created by LennartMac on 01/03/2022.
 */
public class AdjustPostflopPlayToOpp {


    private void adjustPlay(String baseAction, String oppName) {

        //get oppData
            //for example: hands in the range of 20hands
            //and then: where is opp in that subgroup regarding betting?
                //lets say its on the 16 percentile... so 84% of the opps bet more than this opp
                    //if its on the 50 percentile, action shouldnt change
                        //if its on the 0 or 100 percentile, action should change with 100% certainty
                            //so if its on the 16 percentile, action should change with: (50 - 16) * 2 = 68% certainty



    }


    private String adjustBaseBetAction(double oppRelativeCallRatio, double oppRelativeRaiseRatio, double handstrength,
                                 boolean strongdraw) {
        String actionToReturn = "bet75pct";

        if(handstrength < 0.5 && !strongdraw) {
            if(oppRelativeCallRatio > 0.5) {
                double diffToCallMedian = oppRelativeCallRatio - 0.5;
                double changeBoundry = diffToCallMedian * 2;

                if(Math.random() < changeBoundry) {
                    actionToReturn = "check";
                }
            } else {
                if(oppRelativeRaiseRatio > 0.5) {
                    double diffToRaiseMedian = oppRelativeRaiseRatio - 0.5;
                    double changeBoundry = diffToRaiseMedian * 2;

                    if(Math.random() < changeBoundry) {
                        actionToReturn = "check";
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String adjustBaseCheckAction() {
        //misschien wil je wel gaan betten...
            //maar je base bot die bet al heel veel... dus wellicht is dit niet nodig nu...


        return null;
    }

    private String adjustBaseCallAction(String opponentAction, double oppRelativeBetRatio, double handstrength,
                                        boolean strongdraw, double facingOods) {
        String actionToReturn = "call";

        if(opponentAction.equals("bet75pct")) {
            if(oppRelativeBetRatio <= 0.2) {
                if(handstrength < 0.7) {
                    if(!strongdraw) {
                        if(facingOods > 0.2) {
                            actionToReturn = "fold";
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    private String adjustBaseRaiseAction() {
        //misschien minder raisen tegen hele loose gasten... en ook minder tegen gasten die weinig betten...

        return null;
    }

    private String adjustBaseFoldAction(double oppRelativeBetRatio, double oppRelativeCallRatio, boolean bluffOddsAreOk,
                                        List<String> eligibleActions, double facingOdds, double handstrength) {
        //misschien meer bluffraisen tegen lui die heel veel betten en weinig callen...
        //en ook meer callen tegen lui die veel betten...

        String actionToReturn = "fold";

        if(oppRelativeBetRatio > 0.7) {
            if(oppRelativeCallRatio < 0.5) {
                if(bluffOddsAreOk && eligibleActions.contains("raise")) {
                    if(Math.random() < 0.18) {
                        actionToReturn = "raise";
                    }
                }
            }
        }

        if(oppRelativeBetRatio > 0.7) {
            if(facingOdds <= 0.5) {
                if(handstrength > 0.5) {
                    actionToReturn = "call";
                }
            }
        }

        return actionToReturn;
    }

    private void bigValueBetting(double oppRelativeCallRatio, double handstrength, double pot) {
        double sizing = -1;

        if(oppRelativeCallRatio >= 0.75) {
            if(handstrength > 0.82) {
                sizing = 0.75 * pot;
            }
        }
    }





    /////////////////////////



    //hoog raise?
        //less likely to bluffbet
        //more likely to call vs raise
            //hs grens 67%

    //hoog bet
        //more likely to bluffraise
        //more likely to call
            //hs grens 50%
        //more likely to check with valuehand

    //hoog call
        //more likely to valuebet, with bigger size
        //less likely to bluff bet/raise

    //laag raise
        //more likely to bluffbet

    //laag bet
        //less likely to call
            //hs grens 80%
        //less likely to check with valuehand
        //less likely to raise

    //laag call
        //more likely to bluffbet / raise


    private Map<String, Double> adjustPostflopAction(String currentAction, List<String> possibleAdjustments, List<String> eligbileActions,
                                boolean defaultCheck, boolean bluffOddsAreOk, double handstrength, double pot,
                                double currentSizing, List<Card> board, boolean position) {
        Map<String, Double> actionAndSizingToReturn = new HashMap<>();
        String actionToReturn = currentAction;
        double sizingToReturn = currentSizing;

        if(currentAction.equals("check")) {
            if(!defaultCheck) {
                if(possibleAdjustments.contains("bluffBet")) {
                    if(bluffOddsAreOk) {
                        actionToReturn = "bet75pct";
                    }
                }

                if(possibleAdjustments.contains("bigValueBet")) {
                    if(handstrength > 0.83) {
                        actionToReturn = "bet75pct";
                        sizingToReturn = 0.75 * pot;
                    }
                }
            }
        } else if(currentAction.equals("bet75pct")) {
            if(possibleAdjustments.contains("nonBluffBet")) {
                if(handstrength < 0.5) {
                    actionToReturn = "check";
                }
            }

            if(possibleAdjustments.contains("valueCheck")) {
                if(handstrength > 0.8) {
                    if(board != null && board.size() == 5) {
                        if(!position) {
                            actionToReturn = "check";
                        }
                    } else {
                        actionToReturn = "check";
                    }
                }
            }

            if(possibleAdjustments.contains("bigValueBet")) {
                if(handstrength > 0.83) {
                    actionToReturn = "bet75pct";
                    sizingToReturn = 0.75 * pot;
                }
            }
        } else if(currentAction.equals("fold")) {
            if(eligbileActions.contains("raise")) {
                if(bluffOddsAreOk) {
                    if(possibleAdjustments.contains("bluffRaise")) {
                        actionToReturn = "raise";
                    }
                }
            }
        } else if(currentAction.equals("raise")) {
            if(possibleAdjustments.contains("nonBluffRaise")) {
                if(handstrength < 0.8) {
                    actionToReturn = "call";
                }
            }

            if(possibleAdjustments.contains("nonValueRaise")) {
                if(handstrength > 0.83) {
                    if(board != null && board.size() == 5) {
                        if(!position) {
                            actionToReturn = "call";
                        }
                    } else {
                        actionToReturn = "call";
                    }
                }
            }
        }

        actionAndSizingToReturn.put(actionToReturn, sizingToReturn);
        return actionAndSizingToReturn;
    }

    private List<String> getPossibleAdjustments(Map<String, Double> oppRelativeStats) {
        Set<String> possibleAdjustments = new HashSet<>();

        double betDeviation = oppRelativeStats.get("relativeBetRatio") - 0.5;
        double callDeviation = oppRelativeStats.get("relativeCallRatio") - 0.5;
        double raiseDeviation = oppRelativeStats.get("relativeRaiseRatio") - 0.5;

        if(betDeviation > 0) {
            double random = Math.random();

            if(random < (2 * betDeviation)) {
                possibleAdjustments.add("valueCheck");
            }

            if(betDeviation >= 0.15) {
                if(random < (2 * betDeviation)) {
                    possibleAdjustments.add("bluffRaise");
                    possibleAdjustments.add("nonValueRaise");
                }
            }
        } else if(betDeviation < 0) {
            double betDeviationAbsolute = betDeviation * -1;
            double random = Math.random();

            if(random < (2 * betDeviationAbsolute)) {
                possibleAdjustments.add("nonValueCheck");
                possibleAdjustments.add("nonBluffRaise");
            }
        }

        if(callDeviation > 0) {
            double random = Math.random();

            if(random < (2 * callDeviation)) {
                possibleAdjustments.add("bigValueBet");
                possibleAdjustments.add("nonBluffBet");
                possibleAdjustments.add("nonBluffRaise");
            }
        } else if(callDeviation < 0) {
            double callDeviationAbsolute = callDeviation * -1;
            double random = Math.random();

            if(random < (2 * callDeviationAbsolute)) {
                possibleAdjustments.add("bluffBet");

                if(callDeviationAbsolute >= 0.15) {
                    possibleAdjustments.add("bluffRaise");
                }
            }
        }

        if(raiseDeviation > 0) {
            double random = Math.random();

            if(random < (2 * raiseDeviation)) {
                possibleAdjustments.add("nonBluffBet");
            }
        } else if(raiseDeviation < 0) {
            double raiseDeviationAbsolute = raiseDeviation * -1;
            double random = Math.random();

            if(random < (2 * raiseDeviationAbsolute)) {
                possibleAdjustments.add("bluffBet");
            }
        }

        List<String> possibleAdjustmentsOppositesRemoved = removeOppositeAdjustments(possibleAdjustments);
        return possibleAdjustmentsOppositesRemoved;
    }

    private List<String> removeOppositeAdjustments(Set<String> possibleAdjustments) {
        List<String> possibleAdjustmentsToReturn = new ArrayList<>();
        possibleAdjustmentsToReturn.addAll(possibleAdjustments);

        if(possibleAdjustments.contains("valueCheck") && possibleAdjustments.contains("nonValueCheck")) {
            possibleAdjustmentsToReturn.remove("valueCheck");
            possibleAdjustmentsToReturn.remove("nonValueCheck");
        }

        if(possibleAdjustments.contains("bluffBet") && possibleAdjustments.contains("nonBluffBet")) {
            possibleAdjustmentsToReturn.remove("bluffBet");
            possibleAdjustmentsToReturn.remove("nonBluffBet");
        }

        if(possibleAdjustments.contains("bluffRaise") && possibleAdjustments.contains("nonBluffRaise")) {
            possibleAdjustmentsToReturn.remove("bluffRaise");
            possibleAdjustmentsToReturn.remove("nonBluffRaise");
        }

        return possibleAdjustmentsToReturn;
    }

}
