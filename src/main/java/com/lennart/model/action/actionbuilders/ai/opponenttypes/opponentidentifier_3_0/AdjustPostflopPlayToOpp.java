package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.card.Card;

import java.util.*;

/**
 * Created by LennartMac on 01/03/2022.
 */
public class AdjustPostflopPlayToOpp {

    //iets doen ivm bluff-raisen / light value raisen vs overbets...
        //sowieso nadenken over light value raises vs unknowns...
    //en wil je raises cancelen als je een strongdraw hebt?
    //wil je eventueel donken in limped pots oop?
        //ja.. misschien soms wel.. maar niet tegen unknowns?

    //het lichter callen versus overbets...

    //het niet bluffbetten post vs unknowns...

    //soms ligt de callboundry onder je eigen hs en toch fold je.. zoals bij 905 vs een raise

    //shoves...



    //iets met draws bij non_bluff_bet
    //raisen tegen overbets?
    //bluff best niet als donk


    //kijk naar opp raise amount bij postflop value checks..


    private static final String BLUFF_BET = "bluffBet";
    private static final String BIG_VALUE_BET = "bigValueBet";
    private static final String NON_BLUFF_BET = "nonBluffBet";
    private static final String VALUE_CHECK = "valueCheck";
    //private static final String BLUFF_RAISE = "bluffRaise";
    //private static final String BLUFF_3BET = "bluff3bet";
    private static final String NON_BLUFF_RAISE = "nonBluffRaise";
    private static final String NON_VALUE_RAISE = "nonValueRaise";

    private static final String NON_VALUE_CHECK = "nonValueCheck";
    private static final String NON_BIG_VALUE_BET = "nonBigValueBet";
    private static final String VALUE_RAISE = "valueRaise";

    private static final String LOOSE_VALUE_CALL_VS_BET = "looseValueCallVsBet";
    private static final String LOOSE_VALUE_CALL_VS_RAISE = "looseValueCallVsRaise";
    private static final String TIGHT_VALUE_FOLD_VS_BET = "tightValueFoldVsBet";
    private static final String TIGHT_VALUE_FOLD_VS_RAISE = "tightValueFoldVsRaise";
    private static final double MAX_DEVIATION_FROM_CALL_HS_BOUNDRY = 0.15;

    public Map<String, Double> adjustPostflopActionAndSizing(String currentAction, List<String> eligbileActions,
                                                     String opponentName, boolean defaultCheck, boolean bluffOddsAreOk,
                                                     String oppAction, double handstrength, double pot, double currentSizing,
                                                     List<Card> board, boolean position, double callHsBoundry, double bigBlind,
                                                     boolean strongFd, boolean strongOosd, boolean strongGutshot, double opponentBetSize,
                                                     double hypotheticalSizing, ContinuousTable continuousTable) throws Exception {
        Map<String, Double> postOppStats = new StatsRetrieverPostflop().getPostflopStats(opponentName, continuousTable);
        List<String> possibleAdjustments = getPossibleAdjustments(postOppStats, handstrength, callHsBoundry, hypotheticalSizing, board);
        Map<String, Double> actionAndSizingToReturn = changeActionAndSizingIfNeeded(currentAction, possibleAdjustments, eligbileActions,
                defaultCheck, bluffOddsAreOk, oppAction, handstrength, pot, currentSizing, board, position, postOppStats.get("numberOfHands"),
                bigBlind, strongFd, strongOosd, strongGutshot, opponentBetSize, hypotheticalSizing, continuousTable);
        return actionAndSizingToReturn;
    }

    private Map<String, Double> changeActionAndSizingIfNeeded(String currentAction, List<String> possibleAdjustments, List<String> eligbileActions,
                                                      boolean defaultCheck, boolean bluffOddsAreOk, String oppAction, double handstrength,
                                                      double pot, double currentSizing, List<Card> board, boolean position, double numberOfHands,
                                                      double bigBlind, boolean strongFd, boolean strongOosd, boolean strongGutshot, double opponentBetSize,
                                                      double hypotheticalSizing, ContinuousTable continuousTable) {
        Map<String, Double> actionAndSizingToReturn = new HashMap<>();
        String actionToReturn = currentAction;
        double sizingToReturn = currentSizing;

        if(currentAction.equals("check")) {
            boolean limpedPotFlopOop = board.size() == 3 && !position && pot / bigBlind == 2;

            if(!defaultCheck && !limpedPotFlopOop) {
                if(possibleAdjustments.contains(BLUFF_BET)) {
                    if(bluffOddsAreOk) {
                        if(handstrength < 0.5) {
                            actionToReturn = "bet75pct";
                            ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, BLUFF_BET, board.size(), numberOfHands < 15);
                            System.out.println("POST adj BLUFF_BET");
                        }
                    }
                }

                if(possibleAdjustments.contains(NON_VALUE_CHECK)) {
                    if(handstrength > 0.83) {
                        actionToReturn = "bet75pct";
                        ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, NON_VALUE_CHECK, board.size(), numberOfHands < 15);
                        System.out.println("POST adj NON_VALUE_CHECK");
                    }
                }

                if(possibleAdjustments.contains(BIG_VALUE_BET)) {
                    if(handstrength > 0.83) {
                        actionToReturn = "bet75pct";
                        sizingToReturn = 0.75 * pot;
                        ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, BIG_VALUE_BET, board.size(), numberOfHands < 15);
                        System.out.println("POST adj BIG_VALUE_BET");
                    }
                }
            }
        } else if(currentAction.equals("bet75pct")) {
            if(possibleAdjustments.contains(NON_BLUFF_BET)) {
                if(handstrength < 0.5 && !strongFd && !strongOosd && !(strongGutshot && board.size() == 3)) {
                    if(Math.random() > 0.28 && continuousTable.getBankroll() > continuousTable.getBankrollLimit20Nl()) {
                        actionToReturn = "check";
                        ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, NON_BLUFF_BET, board.size(), numberOfHands < 15);
                        System.out.println("POST adj NON_BLUFF_BET");
                    } else {
                        System.out.println("Post adj NON_BLUFF_BET ignored");
                    }
                }
            }

            if(possibleAdjustments.contains(VALUE_CHECK)) {
                if(handstrength > 0.8) {
                    if(board != null && board.size() == 5) {
                        if(!position) {
                            actionToReturn = "check";
                            ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, VALUE_CHECK, board.size(), numberOfHands < 15);
                            System.out.println("POST adj VALUE_CHECK");
                        }
                    } else {
                        actionToReturn = "check";
                        ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, VALUE_CHECK, board.size(), numberOfHands < 15);
                        System.out.println("POST adj VALUE_CHECK");
                    }
                }
            }

            if(possibleAdjustments.contains(BIG_VALUE_BET)) {
                if(handstrength > 0.83) {
                    actionToReturn = "bet75pct";
                    sizingToReturn = 0.75 * pot;
                    ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, BIG_VALUE_BET, board.size(), numberOfHands < 15);
                    System.out.println("POST adj BIG_VALUE_BET");
                }
            }

            if(possibleAdjustments.contains(NON_BIG_VALUE_BET)) {
                if(handstrength > 0.83) {
                    if(currentSizing > 0.5 * pot) {
                        actionToReturn = "bet75pct";
                        sizingToReturn = 0.5 * pot;
                        ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, NON_BIG_VALUE_BET, board.size(), numberOfHands < 15);
                        System.out.println("POST adj NON_BIG_VALUE_BET");
                    }
                }
            }
        } else if(currentAction.equals("fold")) {
            if(eligbileActions.contains("raise")) {
//                if(bluffOddsAreOk) {
//                    if(possibleAdjustments.contains(BLUFF_RAISE)) {
//                        if(opponentBetSize <= pot && oppAction.equals("bet75pct")) {
//                            actionToReturn = "raise";
//                            ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, BLUFF_RAISE, board.size(), numberOfHands < 15);
//                            System.out.println("POST adj BLUFF_RAISE , hypothetical sizing: " + hypotheticalSizing);
//                        }
//                    }
//
//                    if(possibleAdjustments.contains(BLUFF_3BET)) {
//                        if(oppAction.equals("raise")) {
//                            actionToReturn = "raise";
//                            ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, BLUFF_3BET, board.size(), numberOfHands < 15);
//                            System.out.println("POST adj BLUFF_3BET");
//                        }
//                    }
//                }
            }

            if(actionToReturn.equals("fold")) {
                if(oppAction.equals("bet75pct")) {
                    if(possibleAdjustments.contains(LOOSE_VALUE_CALL_VS_BET)) {
                        if(opponentBetSize <= pot) {
                            actionToReturn = "call";
                            ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, LOOSE_VALUE_CALL_VS_BET, board.size(), numberOfHands < 15);
                            System.out.println("POST adj LOOSE_VALUE_CALL_VS_BET");
                        }
                    }
                }

                if(oppAction.equals("raise")) {
                    if(possibleAdjustments.contains(LOOSE_VALUE_CALL_VS_RAISE)) {
                        actionToReturn = "call";
                        ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, LOOSE_VALUE_CALL_VS_RAISE, board.size(), numberOfHands < 15);
                        System.out.println("POST adj LOOSE_VALUE_CALL_VS_RAISE");
                    }
                }
            }
        } else if(currentAction.equals("call")) {
            if(eligbileActions.contains("raise")) {
              if(handstrength > 0.82) {
                  if(possibleAdjustments.contains(VALUE_RAISE)) {
                      if(opponentBetSize <= pot) {
                          actionToReturn = "raise";
                          ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, VALUE_RAISE, board.size(), numberOfHands < 15);
                          System.out.println("POST adj VALUE_RAISE");
                      }
                  }
              }
            }

            if(actionToReturn.equals("call")) {
                if(oppAction.equals("bet75pct")) {
                    if(possibleAdjustments.contains(TIGHT_VALUE_FOLD_VS_BET)) {
                        if(handstrength < 0.87) {
                            actionToReturn = "fold";
                            ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, TIGHT_VALUE_FOLD_VS_BET, board.size(), numberOfHands < 15);
                            System.out.println("POST adj TIGHT_VALUE_FOLD_VS_BET");
                        }
                    }
                }

                if(oppAction.equals("raise")) {
                    if(possibleAdjustments.contains(TIGHT_VALUE_FOLD_VS_RAISE)) {
                        actionToReturn = "fold";
                        ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, TIGHT_VALUE_FOLD_VS_RAISE, board.size(), numberOfHands < 15);
                        System.out.println("POST adj TIGHT_VALUE_FOLD_VS_RAISE");
                    }
                }
            }
        } else if(currentAction.equals("raise")) {
            if(possibleAdjustments.contains(NON_BLUFF_RAISE)) {
                if(handstrength < 0.8) {
                    actionToReturn = "call";
                    ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, NON_BLUFF_RAISE, board.size(), numberOfHands < 15);
                    System.out.println("POST adj NON_BLUFF_RAISE");
                }
            }

            if(possibleAdjustments.contains(NON_VALUE_RAISE)) {
                if(handstrength > 0.83) {
                    if(board != null && board.size() == 5) {
                        if(!position) {
                            actionToReturn = "call";
                            ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, NON_VALUE_RAISE, board.size(), numberOfHands < 15);
                            System.out.println("POST adj NON_VALUE_RAISE");
                        }
                    } else {
                        actionToReturn = "call";
                        ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, NON_VALUE_RAISE, board.size(), numberOfHands < 15);
                        System.out.println("POST adj NON_VALUE_RAISE");
                    }
                }
            }
        }

        actionAndSizingToReturn.put(actionToReturn, sizingToReturn);
        return actionAndSizingToReturn;
    }

//    public static void main(String[] args) throws Exception {
//        new AdjustPostflopPlayToOpp().getPossibleAdjustments(new StatsRetrieverPostflop().getPostflopStats("Goingfishing"));
//    }

    private List<String> getPossibleAdjustments(Map<String, Double> oppRelativeStats, double handstrength, double callHsBoundry, double hypotheticalSizing, List<Card> board) {
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

                        //if(hypotheticalSizing >= 500 && board.size() == 5) {
                        //    possibleAdjustments.add(BLUFF_RAISE);
                        //}
                    }

                    //if(hypotheticalSizing < 500) {
                    //    if(Math.random() < ((betDeviation + (callDeviation * -1) - raiseDeviation) * 2)) {
                    //        possibleAdjustments.add(BLUFF_RAISE);
                    //    }
                    //}

                    if(Math.random() < (((callDeviation * -1) - raiseDeviation) * 2)) {
                        possibleAdjustments.add(BLUFF_BET);
                    }

                    if(Math.random() < (callDeviation * -1) * 2) {
                        possibleAdjustments.add(NON_BIG_VALUE_BET);
                        possibleAdjustments.add(NON_VALUE_RAISE);
                    }

                    if(Math.random() < (((betDeviation + raiseDeviation) + (callDeviation * -1)) * 2)) {
                        //possibleAdjustments.add(BLUFF_3BET);
                    }
                } else {
                    //BuCdRd
                    if(Math.random() < ((betDeviation + (callDeviation * -1)) * 2)) {
                        possibleAdjustments.add(VALUE_CHECK);
                        //possibleAdjustments.add(BLUFF_RAISE);
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

        if(callHsBoundry != -1) {
            possibleAdjustments = addCallAdjustments(possibleAdjustments, betDeviation, raiseDeviation, handstrength, callHsBoundry);
        }


        return possibleAdjustments;
    }

    private List<String> addCallAdjustments(List<String> possibleAdjustments, double betDeviation, double raiseDeviation,
                                            double handstrength, double callHsBoundry) {
        List<String> possibleAdjustmentsIncludingCallAdjustments = new ArrayList<>(possibleAdjustments);

        if(betDeviation > 0) {
            if(handstrength >= callHsBoundry - MAX_DEVIATION_FROM_CALL_HS_BOUNDRY) {
                double allowedCallHsBoundry = calculateAllowedCallHsBoundry(betDeviation, callHsBoundry, "call");

                if(handstrength >= allowedCallHsBoundry) {
                    possibleAdjustmentsIncludingCallAdjustments.add(LOOSE_VALUE_CALL_VS_BET);
                }
            }
        }

        if(raiseDeviation > 0) {
            if(handstrength >= callHsBoundry - MAX_DEVIATION_FROM_CALL_HS_BOUNDRY) {
                double allowedCallHsBoundry = calculateAllowedCallHsBoundry(raiseDeviation, callHsBoundry, "call");

                if(handstrength >= allowedCallHsBoundry) {
                    possibleAdjustmentsIncludingCallAdjustments.add(LOOSE_VALUE_CALL_VS_RAISE);
                }
            }
        }

        if(betDeviation < 0) {
            if(handstrength <= callHsBoundry + MAX_DEVIATION_FROM_CALL_HS_BOUNDRY) {
                double allowedCallHsBoundry = calculateAllowedCallHsBoundry(betDeviation, callHsBoundry, "fold");

                if(handstrength <= allowedCallHsBoundry) {
                    possibleAdjustmentsIncludingCallAdjustments.add(TIGHT_VALUE_FOLD_VS_BET);
                }
            }
        }

        if(betDeviation < 0) {
            if(handstrength <= callHsBoundry + MAX_DEVIATION_FROM_CALL_HS_BOUNDRY) {
                double allowedCallHsBoundry = calculateAllowedCallHsBoundry(raiseDeviation, callHsBoundry, "fold");

                if(handstrength <= allowedCallHsBoundry) {
                    possibleAdjustmentsIncludingCallAdjustments.add(TIGHT_VALUE_FOLD_VS_RAISE);
                }
            }
        }

        return possibleAdjustmentsIncludingCallAdjustments;
    }

    private double calculateAllowedCallHsBoundry(double deviation, double callHsBoundry, String potentialAction) {
        double allowedHsBoundry = callHsBoundry;

        if(potentialAction.equals("call")) {
            double deviationPctOfMaxDeviation = deviation / 0.5;
            double amountToDistractFromCallHsBoundry = deviationPctOfMaxDeviation * MAX_DEVIATION_FROM_CALL_HS_BOUNDRY;
            allowedHsBoundry = callHsBoundry - amountToDistractFromCallHsBoundry;
            double lowestAllowedBoundry = 0.45;

            if(allowedHsBoundry < lowestAllowedBoundry) {
                if(callHsBoundry < lowestAllowedBoundry) {
                    allowedHsBoundry = callHsBoundry;
                } else {
                    allowedHsBoundry = lowestAllowedBoundry;
                }
            }
        }

        if(potentialAction.equals("fold")) {
            double deviationPctOfMaxDeviation = deviation / -0.5;
            double amountToAddToCallHsBoundry = deviationPctOfMaxDeviation * MAX_DEVIATION_FROM_CALL_HS_BOUNDRY;
            allowedHsBoundry = callHsBoundry + amountToAddToCallHsBoundry;
            double highestAllowedBoundry = 0.88;

            if(allowedHsBoundry > highestAllowedBoundry) {
                if(callHsBoundry > highestAllowedBoundry) {
                    allowedHsBoundry = callHsBoundry;
                } else {
                    allowedHsBoundry = highestAllowedBoundry;
                }
            }
        }

        return allowedHsBoundry;
    }
}
