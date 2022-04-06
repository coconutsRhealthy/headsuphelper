package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0;

import com.lennart.model.card.Card;

import java.util.*;

/**
 * Created by LennartMac on 01/03/2022.
 */
public class AdjustPostflopPlayToOpp {
    
    private static final String BLUFF_BET = "bluffBet";
    private static final String BIG_VALUE_BET = "bigValueBet";
    private static final String NON_BLUFF_BET = "nonBluffBet";
    private static final String VALUE_CHECK = "valueCheck";
    private static final String BLUFF_RAISE = "bluffRaise";
    private static final String BLUFF_3BET = "bluff3bet";
    private static final String NON_BLUFF_RAISE = "nonBluffRaise";
    private static final String NON_VALUE_RAISE = "nonValueRaise";
    
    private static final String NON_VALUE_CHECK = "nonValueCheck";
    private static final String NON_BIG_VALUE_BET = "nonBigValueBet";
    private static final String VALUE_RAISE = "valueRaise";

    public Map<String, Double> adjustPostflopActionAndSizing(String currentAction, List<String> eligbileActions,
                                                    String opponentName, boolean defaultCheck, boolean bluffOddsAreOk,
                                                    String oppAction, double handstrength, double pot, double currentSizing,
                                                    List<Card> board, boolean position) throws Exception {
        Map<String, Double> postOppStats = new StatsRetrieverPostflop().getPostflopStats(opponentName);
        List<String> possibleAdjustments = getPossibleAdjustments(postOppStats);
        Map<String, Double> actionAndSizingToReturn = changeActionAndSizingIfNeeded(currentAction, possibleAdjustments, eligbileActions,
                defaultCheck, bluffOddsAreOk, oppAction, handstrength, pot, currentSizing, board, position);
        return actionAndSizingToReturn;
    }

    private Map<String, Double> changeActionAndSizingIfNeeded(String currentAction, List<String> possibleAdjustments, List<String> eligbileActions,
                                                     boolean defaultCheck, boolean bluffOddsAreOk, String oppAction, double handstrength,
                                                     double pot, double currentSizing, List<Card> board, boolean position) {
        Map<String, Double> actionAndSizingToReturn = new HashMap<>();
        String actionToReturn = currentAction;
        double sizingToReturn = currentSizing;

        if(currentAction.equals("check")) {
            if(!defaultCheck) {
                if(possibleAdjustments.contains(BLUFF_BET)) {
                    if(bluffOddsAreOk) {
                        if(handstrength < 0.5) {
                            actionToReturn = "bet75pct";
                        }
                    }
                }

                if(possibleAdjustments.contains(NON_VALUE_CHECK)) {
                    if(handstrength > 0.83) {
                        actionToReturn = "bet75pct";
                    }
                }

                if(possibleAdjustments.contains(BIG_VALUE_BET)) {
                    if(handstrength > 0.83) {
                        actionToReturn = "bet75pct";
                        sizingToReturn = 0.75 * pot;
                    }
                }
            }
        } else if(currentAction.equals("bet75pct")) {
            if(possibleAdjustments.contains(NON_BLUFF_BET)) {
                if(handstrength < 0.5) {
                    actionToReturn = "check";
                }
            }

            if(possibleAdjustments.contains(VALUE_CHECK)) {
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

            if(possibleAdjustments.contains(BIG_VALUE_BET)) {
                if(handstrength > 0.83) {
                    actionToReturn = "bet75pct";
                    sizingToReturn = 0.75 * pot;
                }
            }

            if(possibleAdjustments.contains(NON_BIG_VALUE_BET)) {
                if(handstrength > 0.83) {
                    if(currentSizing > 0.5 * pot) {
                        actionToReturn = "bet75pct";
                        sizingToReturn = 0.5 * pot;
                    }
                }
            }
        } else if(currentAction.equals("fold")) {
            if(eligbileActions.contains("raise")) {
                if(bluffOddsAreOk) {
                    if(possibleAdjustments.contains(BLUFF_RAISE)) {
                        actionToReturn = "raise";
                    }

                    if(possibleAdjustments.contains(BLUFF_3BET)) {
                        if(oppAction.equals("raise")) {
                            actionToReturn = "raise";
                        }
                    }
                }
            }
        } else if(currentAction.equals("call")) {
            if(eligbileActions.contains("raise")) {
              if(handstrength > 0.82) {
                  if(possibleAdjustments.contains(VALUE_RAISE)) {
                    actionToReturn = "raise";
                  }
              }
            }
        } else if(currentAction.equals("raise")) {
            if(possibleAdjustments.contains(NON_BLUFF_RAISE)) {
                if(handstrength < 0.8) {
                    actionToReturn = "call";
                }
            }

            if(possibleAdjustments.contains(NON_VALUE_RAISE)) {
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

    public static void main(String[] args) throws Exception {
        new AdjustPostflopPlayToOpp().getPossibleAdjustments(new StatsRetrieverPostflop().getPostflopStats("Goingfishing"));
    }
    
    private List<String> getPossibleAdjustments(Map<String, Double> oppRelativeStats) {
        List<String> possibleAdjustments = new ArrayList<>();

        double betDeviation = oppRelativeStats.get("relativeBetRatio") - 0.5;
        double callDeviation = oppRelativeStats.get("relativeCallRatio") - 0.5;
        double raiseDeviation = oppRelativeStats.get("relativeRaiseRatio") - 0.5;

        if(betDeviation > 0) {
            if(callDeviation > 0) {
                if(raiseDeviation > 0) {
                    //BuCuRu
                    if(Math.random() < ((callDeviation + raiseDeviation) * 2)) {
                        possibleAdjustments.add(NON_BLUFF_BET);
                    }

                    if(Math.random() < (callDeviation * 2)) {
                        possibleAdjustments.add(BIG_VALUE_BET);
                        possibleAdjustments.add(NON_BLUFF_RAISE);
                    }

                    if(Math.random() < ((betDeviation + callDeviation) * 2)) {
                        possibleAdjustments.add(VALUE_RAISE);
                    }
                } else {
                    //BuCuRd
                    if(Math.random() < (callDeviation * 2)) {
                        possibleAdjustments.add(NON_BLUFF_BET);
                        possibleAdjustments.add(BIG_VALUE_BET);
                        possibleAdjustments.add(NON_BLUFF_RAISE);
                    }

                    if(Math.random() < ((betDeviation + callDeviation) * 2)) {
                        possibleAdjustments.add(VALUE_RAISE);
                    }
                }
            } else {
                if(raiseDeviation > 0) {
                    //BuCdRu
                    if(Math.random() < ((betDeviation + (callDeviation * -1)) * 2)) {
                        possibleAdjustments.add(VALUE_CHECK);
                        possibleAdjustments.add(BLUFF_RAISE);
                    }

                    if(Math.random() < (((callDeviation * -1) - raiseDeviation) * 2)) {
                        possibleAdjustments.add(BLUFF_BET);
                    }

                    if(Math.random() < (callDeviation * -1) * 2) {
                        possibleAdjustments.add(NON_BIG_VALUE_BET);
                        possibleAdjustments.add(NON_VALUE_RAISE);
                    }

                    if(Math.random() < (((betDeviation + raiseDeviation) + (callDeviation * -1)) * 2)) {
                        possibleAdjustments.add(BLUFF_3BET);
                    }
                } else {
                    //BuCdRd
                    if(Math.random() < ((betDeviation + (callDeviation * -1)) * 2)) {
                        possibleAdjustments.add(VALUE_CHECK);
                        possibleAdjustments.add(BLUFF_RAISE);
                    }

                    if(Math.random() < (((callDeviation * -1) + (raiseDeviation * -1)) * 2)) {
                        possibleAdjustments.add(BLUFF_BET);
                    }

                    if(Math.random() < (callDeviation * -1) * 2) {
                        possibleAdjustments.add(NON_BIG_VALUE_BET);
                        possibleAdjustments.add(NON_VALUE_RAISE);
                    }
                }
            }
        } else {
            if(callDeviation > 0) {
                if(raiseDeviation > 0) {
                    //BdCuRu
                    if(Math.random() < (((betDeviation * -1) + callDeviation) * 2)) {
                        possibleAdjustments.add(NON_VALUE_CHECK);
                        possibleAdjustments.add(NON_BLUFF_RAISE);
                    }

                    if(Math.random() < ((callDeviation + raiseDeviation) * 2)) {
                        possibleAdjustments.add(NON_BLUFF_BET);
                    }

                    if(Math.random() < (callDeviation * 2)) {
                        possibleAdjustments.add(BIG_VALUE_BET);
                    }

                    if(Math.random() < (((betDeviation * -1) - callDeviation) * 2)) {
                        possibleAdjustments.add(NON_VALUE_RAISE);
                    }
                } else {
                    //BdCuRd
                    if(Math.random() < (((betDeviation * -1) + callDeviation) * 2)) {
                        possibleAdjustments.add(NON_VALUE_CHECK);
                        possibleAdjustments.add(NON_BLUFF_RAISE);
                    }

                    if(Math.random() < (callDeviation * 2)) {
                        possibleAdjustments.add(NON_BLUFF_BET);
                        possibleAdjustments.add(BIG_VALUE_BET);
                    }

                    if(Math.random() < (((betDeviation * -1) - callDeviation) * 2)) {
                        possibleAdjustments.add(NON_VALUE_RAISE);
                    }
                }
            } else {
                if(raiseDeviation > 0) {
                    //BdCdRu
                    if(Math.random() < (betDeviation * -1) * 2) {
                        possibleAdjustments.add(NON_VALUE_CHECK);
                        possibleAdjustments.add(NON_BLUFF_RAISE);
                    }

                    if(Math.random() < (((callDeviation * -1) - raiseDeviation) * 2)) {
                        possibleAdjustments.add(BLUFF_BET);
                    }

                    if(Math.random() < (callDeviation * -1) * 2) {
                        possibleAdjustments.add(NON_BIG_VALUE_BET);
                    }

                    if(Math.random() < (((betDeviation * -1) + (callDeviation * -1)) * 2)) {
                        possibleAdjustments.add(NON_VALUE_RAISE);
                    }
                } else {
                    //BdCdRd
                    if(Math.random() < (betDeviation * -1) * 2) {
                        possibleAdjustments.add(NON_VALUE_CHECK);
                        possibleAdjustments.add(NON_BLUFF_RAISE);
                    }

                    if(Math.random() < (((callDeviation * -1) + (raiseDeviation * -1)) * 2)) {
                        possibleAdjustments.add(BLUFF_BET);
                    }

                    if(Math.random() < (callDeviation * -1) * 2) {
                        possibleAdjustments.add(NON_BIG_VALUE_BET);
                    }

                    if(Math.random() < (((betDeviation * -1) + (callDeviation * -1)) * 2)) {
                        possibleAdjustments.add(NON_VALUE_RAISE);
                    }
                }
            }
        }
        
        return possibleAdjustments;
    }
}
