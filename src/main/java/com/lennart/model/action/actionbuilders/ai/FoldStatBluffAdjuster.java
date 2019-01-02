package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;
import com.lennart.model.card.Card;

import java.util.ArrayList;
import java.util.List;

public class FoldStatBluffAdjuster {

    public String doBluffAccordingToFoldStat(String action, double bigBlind, boolean position, double handStrength,
                                             List<Card> board, boolean opponentHasInitiative, double facingBetSize,
                                             double myBetSize, double myStack, double facingStack, double pot,
                                             boolean pre3betOrPostRaisedPot, String opponentName) throws Exception {
        String actionToReturn = null;

        if(board != null && board.size() >= 3) {
            if(action.equals("check") || action.equals("fold")) {
                if(action.equals("check") && opponentHasInitiative) {
                    actionToReturn = action;
                } else {
                    if(handStrength < 0.64) {
                        double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);
                        RangeTracker rangeTracker = new RangeTracker();

                        if(sizing > (facingBetSize + facingStack)) {
                            sizing = (facingBetSize + facingStack);
                        }

                        if(sizing / bigBlind <= 70) {
                            if(bluffOddsAreOk(sizing, facingBetSize, facingStack, pot, myStack, board, myBetSize)) {
                                String actionToUse;

                                if(action.equals("check")) {
                                    actionToUse = "bet75pct";
                                } else {
                                    actionToUse = "raise";
                                }

                                String rangeRoute = rangeTracker.getRangeRoute(actionToUse, position, sizing, bigBlind, board);
                                double currentRatio = rangeTracker.getRangeRouteBluffValueRatio(rangeRoute, 0);

                                if(currentRatio >= 0) {
                                    double opponentFoldStat = new FoldStatsKeeper().getFoldStatFromDb(opponentName);
                                    System.out.println("opponentFoldStat-A: " + opponentFoldStat);
                                    String addition = "AA";

                                    if(opponentFoldStat == 0.43) {
                                        addition = "BB";
                                        opponentFoldStat = 0.31;
                                    }

                                    double targetRatio = getTargetRatio(position, opponentFoldStat);

                                    double bluffAmount = rangeTracker.getBluffAmount();
                                    double nonBluffAmount = rangeTracker.getNonBluffAmount();
                                    double valueAmount = rangeTracker.getValueAmount();

                                    if(currentRatio < targetRatio) {
                                        if(actionToUse.equals("raise")) {
                                            if (board.size() == 3 || board.size() == 4) {
                                                if (pre3betOrPostRaisedPot) {
                                                    actionToReturn = action;
                                                }
                                            }
                                        }

                                        if(actionToReturn == null && valueAmount > 0 && nonBluffAmount > 0) {
                                            double targetBluffAmount = valueAmount * targetRatio;
                                            double difference = targetBluffAmount - bluffAmount;
                                            double extraBluffPercentage = difference / nonBluffAmount;
                                            double random = Math.random();

                                            if(random < extraBluffPercentage) {
                                                actionToReturn = actionToUse;
                                                System.out.println(addition + " **changed action to " + actionToUse + " in doBluffAccordingToFoldStat()");
                                                System.out.println("current ratio: " + currentRatio);
                                                System.out.println("target ratio: " + targetRatio);
                                                System.out.println("bluffAmount: " + bluffAmount);
                                                System.out.println("nonBluffAmount: " + nonBluffAmount);
                                                System.out.println("valueAmount: " + valueAmount);
                                                System.out.println("targetBluffAmount: " + targetBluffAmount);
                                                System.out.println("difference: " + difference);
                                                System.out.println("extraBluffPercentage: " + extraBluffPercentage);
                                            } else {
                                                actionToReturn = action;
                                            }
                                        }
                                    } else {
                                        actionToReturn = action;
                                    }
                                } else {
                                    actionToReturn = action;
                                }
                            } else {
                                actionToReturn = action;
                            }
                        } else {
                            actionToReturn = action;
                        }
                    } else {
                        actionToReturn = action;
                    }
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        if(actionToReturn == null) {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String preventBluffAccordingToFoldStat(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                                  double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                                  double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                                  boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                                  boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                                  int boardWetness, boolean opponentHasInitiative, String opponentName) throws Exception {
        String actionToReturn;

        if(board != null && board.size() >= 3) {
            if(action.equals("bet75pct") || action.equals("raise")) {
                if(handStrength < 0.64) {
                    double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind, ownBetSizeBb * bigBlind,
                            ownStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, bigBlind, board);

                    RangeTracker rangeTracker = new RangeTracker();
                    String rangeRoute = rangeTracker.getRangeRoute(action, position, sizing, bigBlind, board);
                    double currentRatio = rangeTracker.getRangeRouteBluffValueRatio(rangeRoute, 0);

                    double opponentFoldStat = new FoldStatsKeeper().getFoldStatFromDb(opponentName);

                    if(currentRatio >= 0 || currentRatio == -1) {

                        System.out.println("opponentFoldStat-B: " + opponentFoldStat);
                        String addition = "AA";

                        if(opponentFoldStat == 0.43) {
                            addition = "BB";
                            opponentFoldStat = 0.31;
                        }

                        double targetRatio = getTargetRatio(position, opponentFoldStat);

                        if(currentRatio >= targetRatio) {
                            double bluffAmount = rangeTracker.getBluffAmount();
                            double valueAmount = rangeTracker.getValueAmount();

                            if(bluffAmount > 0 && valueAmount > 0) {
                                double targetBluffAmount = valueAmount * targetRatio;
                                double difference = bluffAmount - targetBluffAmount;
                                double preventBluffPercentage = difference / bluffAmount;
                                double random = Math.random();

                                if(random < preventBluffPercentage) {
                                    if(action.equals("bet75pct")) {
                                        actionToReturn = "check";
                                        System.out.println(addition + " **changed action to check in preventBluffAccordingToFoldStat()");
                                        System.out.println("currentRatio: " + currentRatio);
                                        System.out.println("targetRatio: " + targetRatio);
                                        System.out.println("bluffAmount: " + bluffAmount);
                                        System.out.println("valueAmount: " + valueAmount);
                                        System.out.println("targetBluffAmount: " + targetBluffAmount);
                                        System.out.println("difference: " + difference);
                                        System.out.println("preventBluffPercentage: " + preventBluffPercentage);
                                    } else {
                                        List<String> eligibleActionsNew = new ArrayList<>();
                                        eligibleActionsNew.add("fold");
                                        eligibleActionsNew.add("call");

                                        //set both opponentstack and effective stack to zero to force either fold or call
                                        actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                                                opponentAction, facingOdds, 0, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                                ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                                bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                                strongBackdoorSd, boardWetness, opponentHasInitiative);

                                        System.out.println(addition + " **changed action to " + actionToReturn + " in preventBluffAccordingToFoldStat()");
                                    }
                                } else {
                                    actionToReturn = action;
                                }
                            } else {
                                actionToReturn = action;
                            }
                        } else {
                            actionToReturn = action;
                        }
                    } else {
                        actionToReturn = action;
                    }
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String preventBigBluffsAgainstLowFoldstat(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                                      double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                                      double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                                      boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                                      boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                                      int boardWetness, boolean opponentHasInitiative, String opponentName) throws Exception {
        String actionToReturn;

        if(action.equals("bet75pct") || action.equals("raise")) {
            if(handStrength < 0.64) {
                double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind, ownBetSizeBb * bigBlind,
                        ownStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, bigBlind, board);

                if(sizing / bigBlind >= 17.5) {
                    double opponentFoldStat = new FoldStatsKeeper().getFoldStatFromDb(opponentName);

                    if(opponentFoldStat < 0.46) {
                        if(action.equals("bet75pct")) {
                            actionToReturn = "check";
                            System.out.println("ZZZ1 sizing changed action to check in preventBigBluffsAgainstLowFoldstat()");
                        } else {
                            List<String> eligibleActionsNew = new ArrayList<>();
                            eligibleActionsNew.add("fold");
                            eligibleActionsNew.add("call");

                            //set both opponentstack and effective stack to zero to force either fold or call
                            actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                                    opponentAction, facingOdds, 0, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                    ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                    bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                    strongBackdoorSd, boardWetness, opponentHasInitiative);

                            System.out.println("ZZZ2 sizing changed action to " + actionToReturn + " in preventBigBluffsAgainstLowFoldstat()");
                        }
                    } else {
                        actionToReturn = action;
                    }
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    private double getTargetRatio(boolean position, double opponentFoldStat) {
        double targetRatio;

        if(position) {
            if(opponentFoldStat <= 0.31) {
                targetRatio = 0.22;
            } else if(opponentFoldStat < 0.55) {
                targetRatio = opponentFoldStat - 0.31;
                targetRatio = targetRatio / 0.24;
                targetRatio = targetRatio * 0.38;
                targetRatio = targetRatio + 0.22;
            } else {
                targetRatio = 0.60;
            }
        } else {
            if(opponentFoldStat <= 0.31) {
                targetRatio = 0.14;
            } else if(opponentFoldStat < 0.55) {
                targetRatio = opponentFoldStat - 0.31;
                targetRatio = targetRatio / 0.24;
                targetRatio = targetRatio * 0.38;
                targetRatio = targetRatio + 0.14;
            } else {
                targetRatio = 0.52;
            }
        }

        return targetRatio;
    }

    private boolean bluffOddsAreOk(double sizing, double facingBetSize, double facingStackSize, double pot,
                                   double ownStackSize, List<Card> board, double ownBetSize) {
        return new MachineLearning().bluffOddsAreOk(sizing, facingBetSize, facingStackSize, pot, ownStackSize, board, ownBetSize);
    }
}
