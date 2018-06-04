package com.lennart.model.action.actionbuilders.ai.foldstats;

import com.lennart.model.action.actionbuilders.ai.ActionVariables;
import com.lennart.model.action.actionbuilders.ai.Poker;
import com.lennart.model.action.actionbuilders.ai.Sizing;
import com.lennart.model.card.Card;

import java.util.List;

/**
 * Created by LennartMac on 24/05/2018.
 */
public class AdjustToFoldStats {

    public String adjustPlayToBotFoldStatRaise(String action, double handStrength, double facingBetSize,
                                                      double myBetSize, double myStack, double facingStack,
                                                      double pot, double bigBlind, List<Card> board, boolean strongFd, boolean strongOosd, boolean strongGutshot, String opponentPlayerName) {
        String actionToReturn;

        if(action.equals("fold") || action.equals("call")) {
            double botFoldStat = FoldStatsKeeper.getFoldStat("bot-V-" + opponentPlayerName);

            double differenceBotFoldStatAndDefault = botFoldStat - 0.43;

            if(differenceBotFoldStatAndDefault > 0.07) {
                if(board != null && !board.isEmpty()) {
                    if(betOrRaiseOddsAreOk(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board)) {
                        if(strongGutshot) {
                            System.out.println("strongGutshot!");
                        }

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
                        } else if(strongFd || strongOosd) {
                            actionToReturn = "raise";
                        } else if(strongGutshot) {
                            double random = Math.random();

                            if(random < 0.43) {
                                actionToReturn = "raise";
                            } else {
                                actionToReturn = action;
                            }
                        } else if(handStrength < 0.5) {
                            if(board.size() == 5) {
                                double random1 = Math.random();

                                if(random1 <= 0.20) {
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
                } else {
                    actionToReturn = action;
                }
            } else {
                if(differenceBotFoldStatAndDefault > 0) {
                    if(board != null && !board.isEmpty()) {
                        if(betOrRaiseOddsAreOk(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board)) {
                            if(handStrength >= 0.95) {
                                double random = Math.random();

                                if(random > 0.5) {
                                    actionToReturn = "raise";
                                } else {
                                    actionToReturn = action;
                                }
                            } else if(strongFd || strongOosd) {
                                double random = Math.random();

                                if(random > 0.8) {
                                    actionToReturn = "raise";
                                } else {
                                    actionToReturn = action;
                                }
                            } else {
                                if(board.size() == 5) {
                                    double random = Math.random();

                                    if(random > 0.96) {
                                        actionToReturn = "raise";
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
                } else {
                    actionToReturn = action;
                }
            }

            if(actionToReturn.equals("raise")) {
                System.out.println();
                System.out.println("changed fold or call to raise!");
                System.out.println("handstrength: " + handStrength);
                System.out.println("old action: " + action);

                if(board != null) {
                    System.out.println("board size: " + board.size());
                }

                System.out.println("strong fd: " + strongFd);
                System.out.println("strong oosd: " + strongOosd);
                System.out.println();
            }

        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String adjustPlayToBotFoldStat(String action, double handStrength, double requiredHandStrength,
                                                 List<Card> holeCards, List<Card> board, boolean position, String opponentPlayerName) {
        String actionToReturn;

        double botFoldStat = FoldStatsKeeper.getFoldStat("bot-V-" + opponentPlayerName);

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

        return actionToReturn;
    }

    public String adjustPlayToOpponentFoldStat(String action, boolean opponentHasInitiative, double facingBetSize,
                                          double myBetSize, double myStack, double facingStack, double pot,
                                          double bigBlind, List<Card> board, String opponentPlayerName, double handStrength) {
        String actionToReturn;

        double opponentFoldStat = FoldStatsKeeper.getFoldStat(opponentPlayerName);

        double differenceOpponentFoldStatAndDefault = opponentFoldStat - 0.43;

        double x;

        if(differenceOpponentFoldStatAndDefault >= 0.2) {
            x = 1;
        } else {
            x = differenceOpponentFoldStatAndDefault / 0.2;
        }

        if(differenceOpponentFoldStatAndDefault > 0) {
            if(board != null && !board.isEmpty()) {
                if(action.equals("check")) {
                    if(handStrength < 0.5) {
                        if(!opponentHasInitiative) {
                            if(betOrRaiseOddsAreOk(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board)) {
                                double random = Math.random();

                                if(random <= x) {
                                    actionToReturn = "bet75pct";
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

        if(actionToReturn.equals("bet75pct") && !action.equals("bet75pct")) {
            System.out.println("changed check to bet!");
            System.out.println("board size: " + board.size());
            System.out.println("opponent fold stat: " + opponentFoldStat);
        }

        return actionToReturn;
    }

    public double getHandStrengthRequiredToCall(ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                                 double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                                 double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                                 boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                                 boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                                 int boardWetness) {
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

    private boolean holeCardsAreBluffable(List<Card> holeCards) {
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

    private boolean betOrRaiseOddsAreOk(double facingBetSize, double myBetSize, double myStack, double facingStack,
                                        double pot, double bigBlind, List<Card> board) {

        double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);

        double oddsForOpponent = (sizing - facingBetSize) / (sizing + facingBetSize + pot);

        return oddsForOpponent > 0.39;
    }

}
