package com.lennart.model.action.actionbuilders.ai.foldstats;

import com.lennart.model.action.actionbuilders.ai.ActionVariables;
import com.lennart.model.action.actionbuilders.ai.Poker;
import com.lennart.model.action.actionbuilders.ai.Sizing;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LennartMac on 24/05/2018.
 */
public class AdjustToFoldStats {

    public static String adjustPlayToBotFoldStatRaise(String action, double handStrength, double facingBetSize,
                                                      double myBetSize, double myStack, double facingStack,
                                                      double pot, double bigBlind, List<Card> board) {
        String actionToReturn;

        if(action.equals("fold") || action.equals("call")) {
            double botFoldStat = FoldStatsKeeper.getFoldStatNew("bot");

            double differenceBotFoldStatAndDefault = botFoldStat - 0.43;

            if(differenceBotFoldStatAndDefault > 0.07) {
                if(raiseOddsAreOk(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board)) {
                    double x;

                    if(differenceBotFoldStatAndDefault >= 0.2) {
                        x = 1;
                    } else {
                        x = differenceBotFoldStatAndDefault / 0.2;
                    }

                    if(handStrength > 0.8) {
                        double random = Math.random();

                        if(random <= x) {
                            actionToReturn = "raise";
                        } else {
                            actionToReturn = action;
                        }
                    } else if(handStrength < 0.5) {
                        double random1 = Math.random();

                        if(random1 <= 0.37) {
                            double random2 = Math.random();

                            if(random2 <= x) {
                                actionToReturn = "raise";
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

            if(actionToReturn.equals("raise")) {
                System.out.println("changed fold or call to raise!");
                System.out.println("handstrength: " + handStrength);
                System.out.println("old action: " + action);
            }

        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public static String adjustPlayToBotFoldStat(String action, double handStrength, double requiredHandStrength,
                                                 List<Card> holeCards, List<Card> board, boolean position) {
        String actionToReturn;

        double botFoldStat = FoldStatsKeeper.getFoldStatNew("bot");

        System.out.println("botFoldStat: " + botFoldStat);

        double differenceBotFoldStatAndDefault = botFoldStat - 0.43;

        if(differenceBotFoldStatAndDefault > 0) {
            if(board == null || board.isEmpty()) {
                if(holeCardsAreBluffable(holeCards) && position) {
                    //bij 63% alles...
                    System.out.println("differenceBotFoldStatAndDefault: " + differenceBotFoldStatAndDefault);
                    if(differenceBotFoldStatAndDefault >= 0.2) {
                        actionToReturn = "call";
                    } else {
                        double percentageToUseBluffablePreflop = differenceBotFoldStatAndDefault / 0.2;
                        System.out.println("percentageToUseBluffablePreflop: " + percentageToUseBluffablePreflop);
                        double random = Math.random();

                        if(random <= percentageToUseBluffablePreflop) {
                            actionToReturn = "call";
                        } else {
                            actionToReturn = action;
                        }
                    }
                } else {
                    actionToReturn = action;
                }
            } else {
                if(differenceBotFoldStatAndDefault <= 0.13) {
                    differenceBotFoldStatAndDefault = differenceBotFoldStatAndDefault * 1.15;
                }

                double acceptableHandStrengthToCall = requiredHandStrength - (differenceBotFoldStatAndDefault);

                if(handStrength >= acceptableHandStrengthToCall) {
                    actionToReturn = "call";
                } else {
                    actionToReturn = action;
                }
            }
        } else {
            actionToReturn = action;
        }




        //////////

//        if(action.equals("fold") || action.equals("call")) {
//            double botFoldStat = FoldStatsKeeper.getFoldStat("bot");
//
//            System.out.println("botFoldStat: " + botFoldStat);
//
//            if(botFoldStat > 0.5) {
//                double random = Math.random();
//
//                if(botFoldStat < 0.6) {
//                    if(random > 0.8) {
//                        actionToReturn = "raise";
//                    } else {
//                        actionToReturn = action;
//                    }
//                } else if(botFoldStat < 0.7) {
//                    if(random > 0.65) {
//                        actionToReturn = "raise";
//                    } else {
//                        actionToReturn = action;
//                    }
//                } else {
//                    if(random > 0.5) {
//                        actionToReturn = "raise";
//                    } else {
//                        actionToReturn = action;
//                    }
//                }
//            } else {
//                actionToReturn = action;
//            }
//        } else {
//            actionToReturn = action;
//        }

        return actionToReturn;
    }

    public String adjustPlayToOpponentFoldStat() {
        return null;
    }

    public static double getHandStrengthRequiredToCall(ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                                 double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                                 double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                                 boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                                 boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                                 int boardWetness) {
        boolean identified = false;
        double downLimit = 0;
        double upLimit = 1;
        int counter = 0;

        for(int i = 0; i < 11; i++) {
            double numberInTheMiddle = ((downLimit + upLimit) / 2);

            eligibleActions.clear();
            eligibleActions.add("fold");
            eligibleActions.add("call");

            String action = new Poker().getAction(actionVariables, eligibleActions, street, position, potSizeBb,
                    opponentAction, facingOdds, effectiveStackBb, strongDraw, numberInTheMiddle, opponentType, opponentBetSizeBb,
                    ownBetSizeBb, opponentStackBb, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                    bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                    strongBackdoorSd, boardWetness);

            if(action.equals("fold")) {
                downLimit = numberInTheMiddle;
                counter++;

                if(counter == 6) {
                    break;
                }
            } else {
                if(!strongDraw) {
                    upLimit = numberInTheMiddle;
                    counter++;

                    if(counter == 6) {
                        break;
                    }
                }
            }
        }

        System.out.println();
        System.out.println("downLimit: " + downLimit);
        System.out.println("upLimit: " + upLimit);
        System.out.println();

        double valueToReturn = (downLimit + upLimit) / 2;
        return valueToReturn;
    }




    ////

//    private double getHandStrengthRequiredToCall(Map<Integer, Set<Set<Card>>> sortedCombos, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
//                                                 double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
//                                                 double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
//                                                 boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
//                                                 boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
//                                                 int boardWetness) {
//
//        boolean turnPointIdentified = false;
//        double downLimit = 0;
//        double upLimit = 1;
//
//        for(int i = 0; i < 7; i++) {
//            double numberInTheMiddle = getNumberInTheMiddle(downLimit, upLimit);
//
//            eligibleActions.clear();
//            eligibleActions.add("fold");
//            eligibleActions.add("call");
//
//            String action = new Poker().getAction(actionVariables, eligibleActions, street, position, potSizeBb,
//                    opponentAction, facingOdds, effectiveStackBb, strongDraw, numberInTheMiddle, opponentType, opponentBetSizeBb,
//                    ownBetSizeBb, opponentStackBb, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
//                    bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
//                    strongBackdoorSd, boardWetness);
//
//            if(action.equals("fold")) {
//                downLimit = numberInTheMiddle;
//            } else {
//                upLimit = numberInTheMiddle;
//            }
//        }
//
//        System.out.println("downlimit: " + downLimit);
//        System.out.println("uplimit: " + upLimit);
//
//
//
//
//        while(!turnPointIdentified) {
//            double numberInTheMiddle = getNumberInTheMiddle(downLimit, upLimit);
//
//            eligibleActions.clear();
//            eligibleActions.add("fold");
//            eligibleActions.add("call");
//
//            String action = new Poker().getAction(actionVariables, eligibleActions, street, position, potSizeBb,
//                    opponentAction, facingOdds, effectiveStackBb, strongDraw, numberInTheMiddle, opponentType, opponentBetSizeBb,
//                    ownBetSizeBb, opponentStackBb, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
//                    bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
//                    strongBackdoorSd, boardWetness);
//
//            if(action.equals("fold")) {
//                downLimit = numberInTheMiddle;
//
//                action = new Poker().getAction(actionVariables, eligibleActions, street, position, potSizeBb,
//                        opponentAction, facingOdds, effectiveStackBb, strongDraw, numberInTheMiddle, opponentType, opponentBetSizeBb,
//                        ownBetSizeBb, opponentStackBb, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
//                        bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
//                        strongBackdoorSd, boardWetness);
//            } else {
//
//
//
//            }
//
//        }
//
//
//
//
//        Set<Card> beginSet = BoardEvaluator.getComboOfSpecificHandStrength(sortedCombos, 0.5, "flop");
//
//        eligibleActions.clear();
//        eligibleActions.add("fold");
//        eligibleActions.add("call");
//
//        String action = new Poker().getAction(actionVariables, eligibleActions, street, position, potSizeBb,
//                opponentAction, facingOdds, effectiveStackBb, strongDraw, handStrength, opponentType, opponentBetSizeBb,
//                ownBetSizeBb, opponentStackBb, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
//                bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
//                strongBackdoorSd, boardWetness);
//
//        if(action.equals("fold")) {
//            while(action.equals("fold")) {
//                double value = getNumberInTheMiddle()
//
//
//            }
//        } else {
//            if(!strongDraw) {
//
//            }
//
//        }
//
//
//
//
//    }


    private double getNumberInTheMiddle(double downLimit, double upLimit) {
        return ((downLimit + upLimit) / 2);
    }

    private static boolean holeCardsAreBluffable(List<Card> holeCards) {
        boolean holeCardsAreBluffable = false;

        if(holeCards != null && holeCards.size() == 2) {
            //ace
            if(holeCards.get(0).getRank() == 14 || holeCards.get(1).getRank() == 14) {
                holeCardsAreBluffable = true;
            }

            //suited
            if(!holeCardsAreBluffable && (holeCards.get(0).getSuit() == holeCards.get(1).getSuit())) {
                holeCardsAreBluffable = true;
            }

            //one gapper
            if(!holeCardsAreBluffable &&
                    (holeCards.get(0).getRank() - holeCards.get(1).getRank() == 1 || holeCards.get(0).getRank() - holeCards.get(1).getRank() == -1)) {
                holeCardsAreBluffable = true;
            }

            //pocket pairs
            if(!holeCardsAreBluffable && (holeCards.get(0).getRank() == holeCards.get(1).getRank())) {
                holeCardsAreBluffable = true;
            }
        }

        return holeCardsAreBluffable;
    }

    private static boolean raiseOddsAreOk(double facingBetSize, double myBetSize, double myStack, double facingStack,
                                   double pot, double bigBlind, List<Card> board) {

        double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);

        double oddsForOpponent = (sizing - facingBetSize) / (sizing + facingBetSize + pot);

        return oddsForOpponent > 0.39;
    }

}
