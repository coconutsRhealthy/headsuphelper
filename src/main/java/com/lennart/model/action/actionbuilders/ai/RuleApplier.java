package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.util.List;

/**
 * Created by LennartMac on 01/03/2018.
 */
public class RuleApplier {

    public String moderateBluffingAndRandomizeValue(String action, double handStrength, String street, boolean position, boolean strongDraw, String opponentType) {
        String actionToReturn;

        if(opponentType.equals("ta") || opponentType.equals("la")) {
            if(handStrength < 0.6) {
                if(action.equals("bet75pct") && !strongDraw) {
                    double random = Math.random();

                    if(random < 0.2) {
                        actionToReturn = action;
                    } else {
                        actionToReturn = "check";
                    }
                } else {
                    actionToReturn = action;
                }
            } else if(handStrength > 0.8) {
                if(action.equals("bet75pct")) {
                    double random = Math.random();

                    if(random > 0.2) {
                        actionToReturn = action;
                    } else {
                        if(street.equals("river") && position) {
                            //river ip value bet
                            actionToReturn = action;
                        } else {
                            actionToReturn = "check";
                        }
                    }
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        } else if(opponentType.equals("tp")){
            if(handStrength < 0.6) {
                if(action.equals("bet75pct") && !strongDraw) {
                    double random = Math.random();

                    if(random < 0.45) {
                        actionToReturn = action;
                    } else {
                        actionToReturn = "check";
                    }
                } else {
                    actionToReturn = action;
                }
            } else if(handStrength > 0.8) {
                if(action.equals("bet75pct")) {
                    double random = Math.random();

                    if(random > 0.15) {
                        actionToReturn = action;
                    } else {
                        if(street.equals("river") && position) {
                            //river ip value bet
                            actionToReturn = action;
                        } else {
                            actionToReturn = "check";
                        }
                    }
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        } else {
            //lp
            if(handStrength < 0.6) {
                if(action.equals("bet75pct") && !strongDraw) {
                    double random = Math.random();

                    if(random < 0.2) {
                        actionToReturn = action;
                    } else {
                        actionToReturn = "check";
                    }
                } else {
                    actionToReturn = action;
                }
            } else if(handStrength > 0.8) {
                if(action.equals("bet75pct")) {
                    double random = Math.random();

                    if(random > 0.04) {
                        actionToReturn = action;
                    } else {
                        if(street.equals("river") && position) {
                            //river ip value bet
                            actionToReturn = action;
                        } else {
                            actionToReturn = "check";
                        }
                    }
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        }

        return actionToReturn;
    }

    public String moderateBluffRaises(String action, double handStrength, String street, boolean strongDraw, double opponentBetSizeBb) {
        String actionToReturn;

        if(action.equals("raise")) {
            if(street.equals("flopOrTurn")) {
                if(!strongDraw) {
                    if(handStrength < 0.7) {
                        if(handStrength < 0.5) {
                            if(opponentBetSizeBb > 4) {
                                actionToReturn = "fold";
                            } else {
                                actionToReturn = action;
                            }
                        } else {
                            if(opponentBetSizeBb < 20) {
                                actionToReturn = "call";
                            } else {
                                actionToReturn = "fold";
                            }
                        }
                    } else {
                        if(opponentBetSizeBb >= 10) {
                            if(handStrength < 0.8) {
                                actionToReturn = "call";
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

//        System.out.println("params in moderateBLUFFRaises(): ");
//        System.out.println("action: " + action);
//        System.out.println("handStrength: " + handStrength);
//        System.out.println("street: " + street);
//        System.out.println("strongDraw: " + strongDraw);
//        System.out.println("opponentBetSizeBb: " + opponentBetSizeBb);
//        System.out.println("ACTION TO RETURN: " + actionToReturn);
//        System.out.println();

        return actionToReturn;
    }

    public String randomizePre3betAction(String action, String route, double handStrength, double myBetSizeBb) {
        String actionToReturn;

        if(route.contains("StreetPreflop") && route.contains("PositionBB")) {
            if(myBetSizeBb == 1) {
                if(action.equals("raise")) {
                    double random = Math.random();

                    if(random > 0.20) {
                        if(handStrength < 0.35) {
                            actionToReturn = "fold";
                        } else {
                            if(handStrength >= 0.8) {
                                double random2 = Math.random();

                                if(random2 > 0.2) {
                                    actionToReturn = action;
                                } else {
                                    actionToReturn = "call";
                                }
                            } else {
                                actionToReturn = "call";
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

        return actionToReturn;
    }

    public String moderateIpOpenPre(String action, String route, double handStrength, double myBetSizeBb) {
        String actionToReturn = action;

        if(route.contains("StreetPreflop") && route.contains("PositionBTN")) {
            if(myBetSizeBb == 0.5) {
                if(action.equals("fold")) {
                    if(handStrength > 0.15) {
                        actionToReturn = "raise";
                    }
                } else if(action.equals("call")) {
                    if(handStrength >= 0.9) {
                        actionToReturn = "raise";
                    }
                }
            }
        }

        return actionToReturn;
    }

    public String moderateGutshotRaises(String action, boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double opponentBetSizeBb, boolean position, double handStrength) {
        String actionToReturn;

        if(action.equals("raise")) {
            if(handStrength < 0.5) {
                if(opponentBetSizeBb > 4) {
                    if(!strongFlushDraw) {
                        if(!strongOosd) {
                            if(strongGutshot) {
                                double random = Math.random();

                                if(random > 0.6) {
                                    actionToReturn = action;
                                } else {
                                    if(opponentBetSizeBb > 20) {
                                        actionToReturn = "fold";
                                    } else {
                                        if(position) {
                                            actionToReturn = "call";
                                        } else {
                                            double random2 = Math.random();

                                            if(random2 > 0.7) {
                                                actionToReturn = "call";
                                            } else {
                                                actionToReturn = "fold";
                                            }
                                        }
                                    }
                                }
                            } else {
                                actionToReturn = "fold";
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

//        System.out.println("params in moderateGUTSHOTRaises(): ");
//        System.out.println("action: " + action);
//        System.out.println("strongFlushDraw: " + strongFlushDraw);
//        System.out.println("strongOosd: " + strongOosd);
//        System.out.println("strongGutshot: " + strongGutshot);
//        System.out.println("opponentBetSizeBb: " + opponentBetSizeBb);
//        System.out.println("position: " + position);
//        System.out.println("handStrength: " + handStrength);
//        System.out.println("ACTION TO RETURN: " + actionToReturn);
//        System.out.println();

        return actionToReturn;
    }

    public String monsterValueBetLogic() {
        return null;
    }

    public String callWithFavorableOddsLogic(String action, double facingOdds) {
        String actionToReturn = action;

        if(action.equals("fold") && facingOdds < 0.15) {
            actionToReturn = "call";
        }

        return actionToReturn;
    }

    public String valueBet(String action, double handStength, String opponentType, boolean preflop) {
        String actionToReturn = action;

        if(action.equals("check") && !preflop) {
            if(opponentType.equals("lp") || opponentType.equals("tp")) {
                if(handStength > 0.8) {
                    actionToReturn = "bet75pct";
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        }

        return actionToReturn;
    }

    public String moderateDeepPostflopValueBettingAndRaising(String action, double handStrength, boolean postFlop,
                                                             boolean strongFlushDraw, boolean strongOosd, double facingBetSize,
                                                             double myBetSize, double myStack, double facingStack,
                                                             double pot, double bigBlind, List<Card> board) {
        String actionToReturn;

        if(postFlop) {
            if(!strongFlushDraw && !strongOosd) {
                if(handStrength > 0.63 && handStrength < 0.95) {
                    if(action.equals("bet75pct")) {
                        double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);

                        if(sizing / bigBlind >= 90) {
                            actionToReturn = "check";
                        } else {
                            actionToReturn = action;
                        }
                    } else if(action.equals("raise")) {
                        double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);

                        if(sizing / bigBlind >= 90) {
                            if(handStrength > 0.8) {
                                actionToReturn = "call";
                            } else {
                                actionToReturn = "fold";
                            }
                        } else {
                            actionToReturn = action;
                        }
                    } else {
                        actionToReturn = action;
                    }
                } else {
                    if(handStrength >= 0.95) {
                        if(action.equals("bet75pct")) {
                            double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);

                            if(sizing / bigBlind >= 90) {
                                if(handStrength >= 0.96) {
                                    actionToReturn = action;
                                } else {
                                    actionToReturn = "check";
                                }
                            } else {
                                actionToReturn = action;
                            }
                        } else if(action.equals("raise")) {
                            double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);

                            if(sizing / bigBlind >= 90) {
                                if(handStrength >= 0.992) {
                                    actionToReturn = action;
                                } else {
                                    actionToReturn = "call";
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

        return actionToReturn;
    }

    public String moderateDeepValueCalls(String action, double myBetSizeBb, double facingBetSizeBb, double myStackBb, double handStrength, boolean postflop) {
        String actionToReturn;
        double amountToCallBb = facingBetSizeBb - myBetSizeBb;

        if(amountToCallBb > myStackBb) {
            amountToCallBb = myStackBb;
        }

        if(action.equals("call")) {
            if(postflop) {
                if(amountToCallBb > 100) {
                    if(handStrength < 0.96) {
                        actionToReturn = "fold";
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

    public String moderateDrawCalls(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                    double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                    double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                    boolean strongFlushDraw, boolean strongOosd, double bigBlind, boolean opponentDidPreflop4betPot) {
        String actionToReturn;

        if(action.equals("call")) {
            if(strongDraw) {
                String actionWhenNotStrongDraw = getActionWhenStrongDrawIsSetToFalse(actionVariables, eligibleActions,
                        street, position, potSizeBb, opponentAction, facingOdds, effectiveStackBb, handStrength,
                        opponentType, opponentBetSizeBb, ownBetSizeBb, opponentStackBb, ownStackBb, preflop, board,
                        bigBlind, opponentDidPreflop4betPot);

                if(actionWhenNotStrongDraw.equals("fold") || (actionWhenNotStrongDraw.equals("raise") && handStrength < 0.6)) {
                    if(!strongFlushDraw && !strongOosd) {
                        if(facingOdds <= 0.17) {
                            actionToReturn = "call";
                        } else if(facingOdds <= 0.34) {
                            if(board.size() == 3) {
                                actionToReturn = "call";
                            } else {
                                if(position) {
                                    actionToReturn = "call";
                                } else {
                                    if(opponentBetSizeBb <= 10) {
                                        actionToReturn = "call";
                                    } else {
                                        actionToReturn = "fold";
                                    }
                                }
                            }
                        } else if(facingOdds <= 0.45) {
                            if(board.size() == 3) {
                                if(opponentBetSizeBb <= 10) {
                                    actionToReturn = "call";
                                } else {
                                    actionToReturn = "fold";
                                }
                            } else {
                                actionToReturn = "fold";
                            }
                        } else {
                            actionToReturn = "fold";
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

    public String moderateDrawFolds(String action, boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot,
                                    double handStrength, double myBetSizeBb, double facingBetSizeBb, double facingOdds,
                                    List<Card> board) {
        String actionToReturn;

        if(action.equals("fold")) {
            if(strongGutshot) {
                if(handStrength >= 0.7 && board.size() == 3) {
                    if(facingOdds <= 0.5) {
                        if(facingBetSizeBb - myBetSizeBb <= 40) {
                            actionToReturn = "call";
                        } else {
                            actionToReturn = action;
                        }
                    } else {
                        actionToReturn = action;
                    }
                } else {
                    actionToReturn = action;
                }
            } else if(strongFlushDraw || strongOosd) {
                if(handStrength >= 0.5 && board.size() == 3) {
                    if(facingOdds <= 0.5) {
                        if(facingBetSizeBb - myBetSizeBb <= 40) {
                            actionToReturn = "call";
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

    private String getActionWhenStrongDrawIsSetToFalse(ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                                      double facingOdds, double effectiveStackBb, double handStrength, String opponentType,
                                                      double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                                      double bigBlind, boolean opponentDidPreflop4betPot) {
        return new Poker().getAction(actionVariables, eligibleActions, street, position, potSizeBb, opponentAction,
                facingOdds, effectiveStackBb, false, handStrength, opponentType, opponentBetSizeBb, ownBetSizeBb,
                opponentStackBb, ownStackBb, preflop, board, false, false, false, bigBlind, opponentDidPreflop4betPot);
    }

    public String doBettingAgainstTp(String action, double handStrength, double facingBetSize,
                                        double myBetSize, double myStack, double facingStack,
                                        double pot, double bigBlind, List<Card> board, String opponentType,
                                        boolean strongDraw, boolean position) {
        String actionToReturn;

        if(action.equals("check")) {
            if(opponentType.equals("tp")) {
                if(strongDraw) {
                    double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);

                    if(sizing / bigBlind < 20) {
                        double random = Math.random();

                        if(random <= 0.8) {
                            actionToReturn = "bet75pct";
                        } else {
                            actionToReturn = action;
                        }
                    } else {
                        actionToReturn = action;
                    }
                } else if(board.size() == 3 || board.size() == 4) {
                    if(handStrength > 0.5 && handStrength < 0.75) {
                        double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);

                        if(sizing / bigBlind < 13) {
                            double random = Math.random();

                            if(random <= 0.5) {
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
                } else if(board.size() == 5) {
                    if(handStrength < 0.4) {
                        if(position) {
                            double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);

                            if(sizing / bigBlind < 12) {
                                double random = Math.random();

                                if(random <= 0.4) {
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

        return actionToReturn;
    }

    public String playCautiouslyInOpponentPre4betPot(String action, ActionVariables actionVariables, List<String> eligibleActions,
                                                      String street, boolean position, double potSizeBb, String opponentAction,
                                                      double facingOdds, double effectiveStackBb, boolean strongDraw,
                                                      double handStrength, String opponentType, double opponentBetSizeBb,
                                                      double ownBetSizeBb, double opponentStackBb, double ownStackBb,
                                                      boolean preflop, List<Card> board, boolean strongFlushDraw,
                                                      boolean strongOosd, boolean strongGutshot, double bigBlind,
                                                      boolean opponentDidPreflop4betPot) {
        String actionToReturn;

        if(opponentDidPreflop4betPot) {
            if(!opponentType.equals("la")) {
                //play as if against tp
                actionToReturn = new Poker().getAction(actionVariables, eligibleActions, street, position, potSizeBb,
                        opponentAction, facingOdds, effectiveStackBb, strongDraw, handStrength, "tp", opponentBetSizeBb,
                        ownBetSizeBb, opponentStackBb, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                        bigBlind, opponentDidPreflop4betPot);
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }
}
