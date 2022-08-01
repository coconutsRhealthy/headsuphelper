package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;

import java.util.*;

/**
 * Created by LennartMac on 05/03/2022.
 */
public class AdjustPreflopPlayToOpp {

    private static final String WEAK_IP_2BET = "weakIp2bet";
    private static final String WEAK_OOP_2BET = "weaOop2bet";
    private static final String NON_TRASH_LIMP = "nonTrashLimp";
    private static final String NON_WEAK_OOP_3BET = "nonWeakOop3bet";
    private static final String VALUE_LIMP_INSTEAD_OF_SHOVE = "valueLimpInsteadOfShove";
    private static final String TRASH_LIMP_INSTEAD_OF_SHOVE = "trashLimpInsteadOfShove";

    public String adjustPreflopAction(String currentAction, String opponentName, boolean position,
                                      double handstrength, String opponentAction,
                                      List<String> eligibleActions, double effectiveStackBb, ContinuousTable continuousTable) throws Exception {
        Map<String, Double> preOppStats = new StatsRetrieverPreflop().getPreflopStats(opponentName, continuousTable);
        List<String> possibleAdjustments = getPossibleAdjustments(preOppStats, position, handstrength, effectiveStackBb);
        String actionToReturn = changeActionIfNeeded(currentAction, possibleAdjustments, position,
                opponentAction, eligibleActions, preOppStats.get("numberOfHands"));
        return actionToReturn;
    }

    private String changeActionIfNeeded(String currentAction, List<String> possibleAdjustments,
                                     boolean position, String opponentAction,
                                     List<String> eligibleActions, double numberOfHands) {
        String actionToReturn = currentAction;

        if(position) {
            if(currentAction.equals("call") && opponentAction.equals("bet")) {
                if(possibleAdjustments.contains(NON_TRASH_LIMP)) {
                    actionToReturn = "fold";
                    ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, NON_TRASH_LIMP, 0, numberOfHands < 15);
                    System.out.println("PRE adj NON_TRASH_LIMP");
                }

                if(possibleAdjustments.contains(WEAK_IP_2BET)) {
                    if(eligibleActions.contains("raise")) {
                        actionToReturn = "raise";
                        ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, WEAK_IP_2BET, 0, numberOfHands < 15);
                        System.out.println("PRE adj WEAK_IP_2BET");
                    }
                }
            }

            if(currentAction.equals("raise") && opponentAction.equals("bet")) {
                if(possibleAdjustments.contains(VALUE_LIMP_INSTEAD_OF_SHOVE)) {
                    actionToReturn = "call";
                    ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, VALUE_LIMP_INSTEAD_OF_SHOVE, 0, numberOfHands < 15);
                    System.out.println("PRE adj VALUE_LIMP_INSTEAD_OF_SHOVE");
                }

                if(possibleAdjustments.contains(TRASH_LIMP_INSTEAD_OF_SHOVE)) {
                    actionToReturn = "call";
                    ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, TRASH_LIMP_INSTEAD_OF_SHOVE, 0, numberOfHands < 15);
                    System.out.println("PRE adj TRASH_LIMP_INSTEAD_OF_SHOVE");
                }

            }
        } else {
            if(currentAction.equals("raise")) {
                if(opponentAction.equals("raise")) {
                    if(possibleAdjustments.contains(NON_WEAK_OOP_3BET)) {
                        actionToReturn = "call";
                        ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, NON_WEAK_OOP_3BET, 0, numberOfHands < 15);
                        System.out.println("PRE adj NON_WEAK_OOP_3BET");
                    }
                }
            }

            if(currentAction.equals("check")) {
                if(possibleAdjustments.contains(WEAK_OOP_2BET)) {
                    actionToReturn = "raise";
                    ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, WEAK_OOP_2BET, 0, numberOfHands < 15);
                    System.out.println("PRE adj WEAK_OOP_2BET");
                }
            }
        }

        return actionToReturn;
    }

//    public static void main(String[] args) throws Exception {
//        ;
//        List<String> possibleAdjustments = new AdjustPreflopPlayToOpp()
//                .getPossibleAdjustments(new StatsRetrieverPreflop().getPreflopStats("Goingfishing"));
//        System.out.println("wacht");
//    }


//    private static final String WEAK_IP_2BET = "weakIp2bet";
//    private static final String WEAK_OOP_2BET = "weakOop2bet";
//    private static final String NON_TRASH_LIMP = "nonTrashLimp";
//    private static final String NON_WEAK_IP_2BET = "nonWeakIp2bet";
//    private static final String NON_WEAK_OOP_3BET = "nonWeakOop3bet";
//    private static final String NON_WEAK_IP_3BET = "nonWeakIp3bet";



    //actions to adjust
        //IP
            //bluff open 2bets
                //tegen iemand die weinig oop3bet en die veel fold pre
                //

            //non trash limps
                //tegen iemand die veel raist pre oop


            //limp instead of shove
                //bij zwakke hand
                    //tegen iemand met een hoge call amount en een lage oop raise amount

                //bij sterke hand
                    //tegen iemand met een lage call amount


        //OOP
            //non weak oop3bets
                //tegen iemand met een lage ip raise amount

    private List<String> getPossibleAdjustments(Map<String, Double> oppRelativeStats, boolean position, double handstrength,
                                                double effStackBb) {
        List<String> possibleAdjustments = new ArrayList<>();

        double call2betDeviation = oppRelativeStats.get("relativeCall2betRatio") - 0.5;
        double ipRaiseDeviation = oppRelativeStats.get("relativeIpRaiseRatio") - 0.5;
        double oopRaiseDeviation = oppRelativeStats.get("relativeOopRaiseRatio") - 0.5;
        double overallCallDeviation = oppRelativeStats.get("relativeOverallCallRatio") - 0.5;
        double shoveDeviation = oppRelativeStats.get("relativeShoveRatio") - 0.5;

        if(position) {
            if(handstrength < 0.5) {
                if(oopRaiseDeviation < 0 && call2betDeviation < 0) {
                    if(effStackBb >= 14) {
                        if(Math.random() < (((oopRaiseDeviation * -1) + (call2betDeviation * -1)) * 2)) {
                            possibleAdjustments.add(WEAK_IP_2BET);
                        }
                    }
                }
            }

            if(handstrength < 0.2) {
                if(oopRaiseDeviation > 0) {
                    if(Math.random() < (oopRaiseDeviation * 2)) {
                        possibleAdjustments.add(NON_TRASH_LIMP);
                    }
                }
            }

            if(effStackBb < 12) {
                if(handstrength > 0.7) {
                    if(overallCallDeviation < 0) {
                        if(Math.random() < ((overallCallDeviation * -1) * 2)) {
                            //possibleAdjustments.add(VALUE_LIMP_INSTEAD_OF_SHOVE);
                        }
                    }
                } else {
                    if(overallCallDeviation > 0 && oopRaiseDeviation < 0) {
                        if(Math.random() < (overallCallDeviation + (oopRaiseDeviation * -1)) * 2) {
                            //possibleAdjustments.add(TRASH_LIMP_INSTEAD_OF_SHOVE);
                        }
                    }
                }
            }
        } else {
            if(handstrength < 0.8) {
                if(ipRaiseDeviation < 0) {
                    if(Math.random() < ((ipRaiseDeviation * -1) * 2)) {
                        possibleAdjustments.add(NON_WEAK_OOP_3BET);
                    }
                }
            }


            if(handstrength < 0.55) {
                //if(effStackBb >= 15) {
                if(effStackBb >= 20) {
                    if(call2betDeviation < 0 && shoveDeviation < 0) {
                        if(Math.random() < (((call2betDeviation * -1) + (shoveDeviation * -1)) * 2)) {
                            possibleAdjustments.add(WEAK_OOP_2BET);
                        }
                    }
                }
            }
        }

        return possibleAdjustments;
    }
}
