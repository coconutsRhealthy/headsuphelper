package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;

import java.util.*;

/**
 * Created by LennartMac on 05/03/2022.
 */
public class AdjustPreflopPlayToOpp {

    private static final String WEAK_IP_2BET = "weakIp2bet";
    private static final String WEAK_OOP_2BET = "weakOop2bet";
    private static final String NON_TRASH_LIMP = "nonTrashLimp";
    private static final String NON_WEAK_IP_2BET = "nonWeakIp2bet";
    private static final String NON_WEAK_OOP_3BET = "nonWeakOop3bet";
    private static final String NON_WEAK_IP_3BET = "nonWeakIp3bet";

    public String adjustPreflopAction(String currentAction, String opponentName, boolean position,
                                      double handstrength, String opponentAction,
                                      List<String> eligibleActions, double effectiveStackBb) throws Exception {
        Map<String, Double> preOppStats = new StatsRetrieverPreflop().getPreflopStats(opponentName);
        List<String> possibleAdjustments = getPossibleAdjustments(preOppStats);
        String actionToReturn = changeActionIfNeeded(currentAction, possibleAdjustments, position,
                handstrength, opponentAction, eligibleActions, effectiveStackBb, preOppStats.get("numberOfHands"));
        return actionToReturn;
    }

    private String changeActionIfNeeded(String currentAction, List<String> possibleAdjustments,
                                     boolean position, double handstrength, String opponentAction,
                                     List<String> eligibleActions, double effectiveStackBb, double numberOfHands) {
        String actionToReturn = currentAction;

        if(position) {
            if(currentAction.equals("call") && opponentAction.equals("bet")) {
                if(possibleAdjustments.contains(NON_TRASH_LIMP)) {
                    if(handstrength < 0.2) {
                        actionToReturn = "fold";
                        ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, NON_TRASH_LIMP, 0, numberOfHands < 15);
                        System.out.println("PRE adj NON_TRASH_LIMP");
                    }
                }

                if(possibleAdjustments.contains(WEAK_IP_2BET)) {
                    if(eligibleActions.contains("raise")) {
                        if(handstrength < 0.5) {
                            actionToReturn = "raise";
                            ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, WEAK_IP_2BET, 0, numberOfHands < 15);
                            System.out.println("PRE adj WEAK_IP_2BET");
                        }
                    }
                }
            }

            if(currentAction.equals("raise") && opponentAction.equals("bet")) {
                if(possibleAdjustments.contains(NON_WEAK_IP_2BET)) {
                    if(handstrength < 0.75) {
                        if(effectiveStackBb > 10) {
                            actionToReturn = "call";
                            ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, NON_WEAK_IP_2BET, 0, numberOfHands < 15);
                            System.out.println("PRE adj NON_WEAK_IP_2BET");
                        }
                    }
                }
            }

            if(currentAction.equals("raise") && opponentAction.equals("raise")) {
                if(possibleAdjustments.contains(NON_WEAK_IP_3BET)) {
                    if(handstrength < 0.9) {
                        actionToReturn = "call";
                        ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, NON_WEAK_IP_3BET, 0, numberOfHands < 15);
                        System.out.println("PRE adj NON_WEAK_IP_3BET");
                    }
                }
            }
        } else {
            if(currentAction.equals("check")) {
                if(possibleAdjustments.contains(WEAK_OOP_2BET)) {
                    if(eligibleActions.contains("raise")) {
                        if(handstrength < 0.65) {
                            actionToReturn = "raise";
                            ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, WEAK_OOP_2BET, 0, numberOfHands < 15);
                            System.out.println("PRE adj WEAK_OOP_2BET");
                        }
                    }
                }
            }

            if(currentAction.equals("raise")) {
                if(opponentAction.equals("raise")) {
                    if(possibleAdjustments.contains(NON_WEAK_OOP_3BET)) {
                        if(handstrength < 0.8) {
                            actionToReturn = "call";
                            ContinuousTable.updateActionAdjustMap(currentAction, actionToReturn, NON_WEAK_OOP_3BET, 0, numberOfHands < 15);
                            System.out.println("PRE adj NON_WEAK_OOP_3BET");
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }

    public static void main(String[] args) throws Exception {
        ;
        List<String> possibleAdjustments = new AdjustPreflopPlayToOpp()
                .getPossibleAdjustments(new StatsRetrieverPreflop().getPreflopStats("Goingfishing"));
        System.out.println("wacht");
    }

    private List<String> getPossibleAdjustments(Map<String, Double> oppRelativeStats) {
        List<String> possibleAdjustments = new ArrayList<>();

        double _2betDeviation = oppRelativeStats.get("relative2betRatio") - 0.5;
        double shoveDeviation = oppRelativeStats.get("relativeShoveRatio") - 0.5;
        double call2betDeviation = oppRelativeStats.get("relativeCall2betRatio") - 0.5;
        double ipRaiseDeviation = oppRelativeStats.get("relativeIpRaiseRatio") - 0.5;
        double oopRaiseDeviation = oppRelativeStats.get("relativeOopRaiseRatio") - 0.5;

        //iets losser dit
        //wellicht hier geen focus op shoveDeviation
        if(oopRaiseDeviation < 0 && call2betDeviation < 0.07 && shoveDeviation < 0.07) {
            if(Math.random() < (((oopRaiseDeviation * -1) + (call2betDeviation * -1) + (shoveDeviation * -1)) * 2)) {
                possibleAdjustments.add(WEAK_IP_2BET);
            }
        }

        if(call2betDeviation < 0 && shoveDeviation < 0.05) {
            if(Math.random() < (((call2betDeviation * -1) + (shoveDeviation * -1)) * 2)) {
                possibleAdjustments.add(WEAK_OOP_2BET);
            }
        }

        //wellicht hier enkel focus op oopRaiseDeviation
        if(oopRaiseDeviation > 0 || shoveDeviation > 0) {
            if(Math.random() < ((oopRaiseDeviation + shoveDeviation) * 2)) {
                possibleAdjustments.add(NON_TRASH_LIMP);
                possibleAdjustments.add(NON_WEAK_IP_2BET);
            }
        }

        if(_2betDeviation < 0 || ipRaiseDeviation < 0) {
            if(_2betDeviation < 0) {
                _2betDeviation = _2betDeviation * -1;
            }

            if(ipRaiseDeviation < 0) {
                ipRaiseDeviation = ipRaiseDeviation * -1;
            }

            if(Math.random() < ((_2betDeviation + ipRaiseDeviation) * 2)) {
                possibleAdjustments.add(NON_WEAK_OOP_3BET);
            }
        }

        if(oopRaiseDeviation < 0) {
            if(Math.random() < ((oopRaiseDeviation * -1) * 2)) {
                possibleAdjustments.add(NON_WEAK_IP_3BET);
            }
        }

        return possibleAdjustments;
    }
}
