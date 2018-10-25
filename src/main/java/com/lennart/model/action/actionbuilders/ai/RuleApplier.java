package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.computergame.ComputerGameNew;
import com.lennart.model.handevaluation.HandEvaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public String moderateBluffRaises(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                      double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                      double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                      boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                      boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                      int boardWetness, boolean opponentHasInitiative, Map<Integer, List<Card>> botRange, ContinuousTable continuousTable,
                                      GameVariables gameVariables, BoardEvaluator boardEvaluator, ComputerGameNew computerGameNew) {
        String actionToReturn;

        if(action.equals("raise")) {
            if(street.equals("flopOrTurn")) {
                if(!strongDraw) {
                    if(handStrength < 0.7) {
                        if(handStrength < 0.5) {
                            if(opponentBetSizeBb > 4) {
                                List<String> eligibleActionsNew = new ArrayList<>();
                                eligibleActionsNew.add("fold");
                                eligibleActionsNew.add("call");

                                //set both opponentstack and effective stack to zero to force either fold or call
                                actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                                        opponentAction, facingOdds, 0, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                        ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                        bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                        strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
                            } else {
                                actionToReturn = action;
                            }
                        } else {
                            List<String> eligibleActionsNew = new ArrayList<>();
                            eligibleActionsNew.add("fold");
                            eligibleActionsNew.add("call");

                            //set both opponentstack and effective stack to zero to force either fold or call
                            actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                                    opponentAction, facingOdds, 0, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                    ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                    bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                    strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
                        }
                    } else {
                        if(opponentBetSizeBb >= 10) {
                            if(handStrength < 0.8) {
                                List<String> eligibleActionsNew = new ArrayList<>();
                                eligibleActionsNew.add("fold");
                                eligibleActionsNew.add("call");

                                //set both opponentstack and effective stack to zero to force either fold or call
                                actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                                        opponentAction, facingOdds, 0, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                        ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                        bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                        strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
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

    public String moderateGutshotRaises(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                        double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                        double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                        boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                        boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                        int boardWetness, boolean opponentHasInitiative, Map<Integer, List<Card>> botRange, ContinuousTable continuousTable,
                                        GameVariables gameVariables, BoardEvaluator boardEvaluator, ComputerGameNew computerGameNew) {
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
                                    List<String> eligibleActionsNew = new ArrayList<>();
                                    eligibleActionsNew.add("fold");
                                    eligibleActionsNew.add("call");

                                    //set both opponentstack and effective stack to zero to force either fold or call
                                    actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                                            opponentAction, facingOdds, 0, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                            ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                            bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                            strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
                                }
                            } else {
                                List<String> eligibleActionsNew = new ArrayList<>();
                                eligibleActionsNew.add("fold");
                                eligibleActionsNew.add("call");

                                //set both opponentstack and effective stack to zero to force either fold or call
                                actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                                        opponentAction, facingOdds, 0, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                        ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                        bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                        strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
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

    public String valueBet(String action, double handStength, String opponentType, boolean preflop, String street, boolean position) {
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

        if(action.equals("check") && !preflop && handStength >= 0.95) {
            if(street.equals("river") && position) {
                actionToReturn = "bet75pct";
            } else {
                double random = Math.random();

                if(random > 0.10) {
                    actionToReturn = "bet75pct";
                } else {
                    actionToReturn = action;
                }
            }
        }

        return actionToReturn;
    }

    public String neverFoldTheNuts(String action, double handStrength, List<String> eligibleActions) {
        String actionToReturn;

        if(action.equals("fold") && handStrength >= 0.992) {
            if(eligibleActions != null && eligibleActions.contains("raise")) {
                actionToReturn = "raise";
            } else {
                actionToReturn = "call";
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String moderateDeepPostflopValueBettingAndRaising(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                                             double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                                             double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                                             boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                                             boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                                             int boardWetness, boolean opponentHasInitiative, Map<Integer, List<Card>> botRange, ContinuousTable continuousTable,
                                                             GameVariables gameVariables, BoardEvaluator boardEvaluator, ComputerGameNew computerGameNew) {
        String actionToReturn;

        if(board != null && board.size() >= 3) {
            if(!strongFlushDraw && !strongOosd) {
                if(handStrength > 0.63 && handStrength < 0.95) {
                    if(action.equals("bet75pct")) {
                        double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind, ownBetSizeBb * bigBlind,
                                ownStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, bigBlind, board);

                        if(sizing > ((opponentBetSizeBb * bigBlind) + (opponentStackBb * bigBlind))) {
                            sizing = (opponentBetSizeBb * bigBlind) + (opponentStackBb * bigBlind);
                        }

                        if(sizing / bigBlind >= 90) {
                            actionToReturn = "check";
                        } else {
                            actionToReturn = action;
                        }
                    } else if(action.equals("raise")) {
                        double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind, ownBetSizeBb * bigBlind,
                                ownStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, bigBlind, board);

                        if(sizing > ((opponentBetSizeBb * bigBlind) + (opponentStackBb * bigBlind))) {
                            sizing = (opponentBetSizeBb * bigBlind) + (opponentStackBb * bigBlind);
                        }

                        if(sizing / bigBlind >= 90) {
                            if(handStrength > 0.8) {
                                actionToReturn = "call";
                            } else {
                                List<String> eligibleActionsNew = new ArrayList<>();
                                eligibleActionsNew.add("fold");
                                eligibleActionsNew.add("call");

                                //set both opponentstack and effective stack to zero to force either fold or call
                                actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                                        opponentAction, facingOdds, 0, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                        ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                        bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                        strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
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
                            double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind, ownBetSizeBb * bigBlind,
                                    ownStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, bigBlind, board);

                            if(sizing > ((opponentBetSizeBb * bigBlind) + (opponentStackBb * bigBlind))) {
                                sizing = (opponentBetSizeBb * bigBlind) + (opponentStackBb * bigBlind);
                            }

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
                            double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind, ownBetSizeBb * bigBlind,
                                    ownStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, bigBlind, board);

                            if(sizing > ((opponentBetSizeBb * bigBlind) + (opponentStackBb * bigBlind))) {
                                sizing = (opponentBetSizeBb * bigBlind) + (opponentStackBb * bigBlind);
                            }

                            if(sizing / bigBlind >= 90) {
                                if(handStrength >= 0.992) {
                                    actionToReturn = action;
                                } else {
                                    //check if this is good, was first hardcoded call...

                                    List<String> eligibleActionsNew = new ArrayList<>();
                                    eligibleActionsNew.add("fold");
                                    eligibleActionsNew.add("call");

                                    //set both opponentstack and effective stack to zero to force either fold or call
                                    actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                                            opponentAction, facingOdds, 0, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                            ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                            bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                            strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
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
                                    boolean strongFlushDraw, boolean strongOosd, double bigBlind, boolean opponentDidPreflop4betPot, boolean pre3betOrPostRaisedPot,
                                    boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd, int boardWetness, boolean strongGutshot, boolean opponentHasInitiative,
                                    Map<Integer, List<Card>> botRange, ContinuousTable continuousTable, GameVariables gameVariables,
                                    BoardEvaluator boardEvaluator, ComputerGameNew computerGameNew) {
        String actionToReturn;
        boolean strongDrawCopy = strongDraw;

        if(!strongFlushDraw && !strongOosd && !strongGutshot && strongDrawCopy) {
            //strongdraw because of backdoor, set to false
            strongDrawCopy = false;
        }

        if(action.equals("call")) {
            if(strongDrawCopy) {
                String actionWhenNotStrongDraw = getActionWhenStrongDrawIsSetToFalse(actionVariables, eligibleActions,
                        street, position, potSizeBb, opponentAction, facingOdds, effectiveStackBb, handStrength,
                        opponentType, opponentBetSizeBb, ownBetSizeBb, opponentStackBb, ownStackBb, preflop, board,
                        bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                        strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);

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
                        double amountToCallBb = opponentBetSizeBb - ownBetSizeBb;
                        if(amountToCallBb < 10 && board.size() == 3) {
                            actionToReturn = action;
                        } else {
                            if(facingOdds <= 0.48) {
                                actionToReturn = action;
                            } else {
                                actionToReturn = actionWhenNotStrongDraw;
                            }
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

        return actionToReturn;
    }

    public String moderateDrawFolds(String action, boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot,
                                    double handStrength, double myBetSizeBb, double facingBetSizeBb, double facingOdds,
                                    List<Card> board) {
        String actionToReturn;

        if(action.equals("fold")) {
            if(strongGutshot) {
                if(handStrength >= 0.7 && board != null && board.size() == 3) {
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
                if(handStrength >= 0.5 && board != null && board.size() == 3) {
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
                                                       double bigBlind, boolean opponentDidPreflop4betPot, boolean pre3betOrPostRaisedPot, boolean strongOvercards,
                                                       boolean strongBackdoorFd, boolean strongBackdoorSd, int boardWetness, boolean opponentHasInitiative, Map<Integer,
                                                       List<Card>> botRange, ContinuousTable continuousTable, GameVariables gameVariables,
                                                       BoardEvaluator boardEvaluator, ComputerGameNew computerGameNew) {
        return new Poker().getAction(actionVariables, eligibleActions, street, position, potSizeBb, opponentAction,
                facingOdds, effectiveStackBb, false, handStrength, opponentType, opponentBetSizeBb, ownBetSizeBb,
                opponentStackBb, ownStackBb, preflop, board, false, false, false, bigBlind, opponentDidPreflop4betPot,
                pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd, strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
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
                                                     boolean opponentDidPreflop4betPot, boolean pre3betOrPostRaisedPot,
                                                     boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                                     int boardWetness, boolean opponentHasInitiative, Map<Integer, List<Card>> botRange,
                                                     ContinuousTable continuousTable, GameVariables gameVariables,
                                                     BoardEvaluator boardEvaluator, ComputerGameNew computerGameNew) {
        String actionToReturn;

        if(opponentDidPreflop4betPot) {
            if(!opponentType.equals("la") && !opponentType.equals("tp")) {
                //play as if against tp
                actionToReturn = new Poker().getAction(actionVariables, eligibleActions, street, position, potSizeBb,
                        opponentAction, facingOdds, effectiveStackBb, strongDraw, handStrength, "tp", opponentBetSizeBb,
                        ownBetSizeBb, opponentStackBb, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                        bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                        strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String noFlopOrTurnRaisesInPre3betOrPostRaisedPot(String action, ActionVariables actionVariables,
                                                             List<String> eligibleActions, String street, boolean position,
                                                             double potSizeBb, String opponentAction, double facingOdds,
                                                             double effectiveStackBb, boolean strongDraw, double handStrength,
                                                             String opponentType, double opponentBetSizeBb, double ownBetSizeBb,
                                                             double opponentStackBb, double ownStackBb, boolean preflop,
                                                             List<Card> board, boolean strongFlushDraw, boolean strongOosd,
                                                             boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                                             boolean pre3betOrPostRaisedPot, boolean strongOvercards,
                                                             boolean strongBackdoorFd, boolean strongBackdoorSd,
                                                             int boardWetness, boolean opponentHasInitiative,
                                                             Map<Integer, List<Card>> botRange, ContinuousTable continuousTable,
                                                             GameVariables gameVariables, BoardEvaluator boardEvaluator,
                                                             ComputerGameNew computerGameNew) {
        String actionToReturn;

        if(pre3betOrPostRaisedPot) {
            if(action.equals("raise")) {
                if(board != null && (board.size() == 3 || board.size() == 4)) {
                    if(eligibleActions.contains("fold") && eligibleActions.contains("call") &&
                            !eligibleActions.contains("raise") && opponentStackBb != 0 &&
                            (ownStackBb + ownBetSizeBb > opponentBetSizeBb)) {
                        //je komt hier voor de 2e keer met aangepaste eligible actions, en weer is het raise.

                        //set opponentStack to 0
                        actionToReturn = new Poker().getAction(actionVariables, eligibleActions, street, position, potSizeBb,
                                opponentAction, facingOdds, effectiveStackBb, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
                    } else {
                        List<String> eligibleActionsNew = new ArrayList<>();
                        eligibleActionsNew.add("fold");
                        eligibleActionsNew.add("call");

                        actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                                opponentAction, facingOdds, effectiveStackBb, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                ownBetSizeBb, opponentStackBb, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
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

    public String moderateRaisesBasedOnEquity(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position,
                                              double potSizeBb, String opponentAction, double facingOdds, double effectiveStackBb, boolean strongDraw,
                                              double handStrength, String opponentType, double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb,
                                              double ownStackBb, boolean preflop, List<Card> board, boolean strongFlushDraw, boolean strongOosd,
                                              boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot, boolean pre3betOrPostRaisedPot,
                                              boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd, int boardWetness, boolean opponentHasInitiative,
                                              Map<Integer, List<Card>> botRange, ContinuousTable continuousTable, GameVariables gameVariables,
                                              BoardEvaluator boardEvaluator, ComputerGameNew computerGameNew) {
        String actionToReturn;

        if(action.equals("raise")) {
            if(!preflop) {
                if(handStrength < 0.9) {
                    if(board.size() == 3 || board.size() == 4) {
                        if(strongDraw || strongBackdoorFd || strongBackdoorSd) {
                            actionToReturn = action;
                        } else {
                            double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind,
                                    ownBetSizeBb * bigBlind, ownStackBb * bigBlind, opponentStackBb * bigBlind,
                                    potSizeBb * bigBlind, bigBlind, board);

                            if(sizing / bigBlind > 20) {
                                List<String> eligibleActionsNoRaise = new ArrayList<>();
                                eligibleActionsNoRaise.add("fold");
                                eligibleActionsNoRaise.add("call");

                                double opponentStackBbAsZero = 0;

                                actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNoRaise, street,
                                        position, potSizeBb, opponentAction, facingOdds, effectiveStackBb, strongDraw,
                                        handStrength, opponentType, opponentBetSizeBb, ownBetSizeBb, opponentStackBbAsZero,
                                        ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot, bigBlind,
                                        opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                        strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);

                                //System.out.println("changed raise to: " + actionToReturn + " moderateRaisesBasedOnEquity()");
                            } else {
                                actionToReturn = action;
                            }
                        }
                    } else {
                        double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind,
                                ownBetSizeBb * bigBlind, ownStackBb * bigBlind, opponentStackBb * bigBlind,
                                potSizeBb * bigBlind, bigBlind, board);

                        if(sizing / bigBlind > 20) {
                            List<String> eligibleActionsNoRaise = new ArrayList<>();
                            eligibleActionsNoRaise.add("fold");
                            eligibleActionsNoRaise.add("call");

                            double opponentStackBbAsZero = 0;

                            String actionWhenRaiseNotPossible = new Poker().getAction(actionVariables, eligibleActionsNoRaise, street,
                                    position, potSizeBb, opponentAction, facingOdds, effectiveStackBb, strongDraw,
                                    handStrength, opponentType, opponentBetSizeBb, ownBetSizeBb, opponentStackBbAsZero,
                                    ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot, bigBlind,
                                    opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                    strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);

                            if(actionWhenRaiseNotPossible.equals("fold")) {
                                actionToReturn = action;
                                //System.out.println("Kept river raise as raise, since else it would be a fold");
                            } else if(actionWhenRaiseNotPossible.equals("call")) {
                                actionToReturn = "call";
                                //System.out.println("Changed river raise to call in moderateRaisesBasedOnEquity()");
                            } else {
                                actionToReturn = null;
                                //System.out.println("Should not come here in moderateRaisesBasedOnEquity()!");
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

        return actionToReturn;
    }

    public String moderateBetBasedOnEquity(String action, double handStrength, boolean preflop, boolean strongDraw,
                                           double facingBetSize, double myBetSize, double myStack, double facingStack,
                                           double pot, double bigBlind, List<Card> board) {
        String actionToReturn;

        if(action.equals("bet75pct")) {
            if(!preflop) {
                double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);
                double sizingInBb = sizing / bigBlind;

                if(board.size() == 3 || board.size() == 4) {
                    if(sizingInBb > 25) {
                        if(strongDraw || handStrength >= 0.82) {
                            actionToReturn = action;
                        } else {
                            actionToReturn = "check";
                            //System.out.println("changed action to check in moderateBetBasedOnEquity(). Board size: " + board.size());
                        }
                    } else {
                        actionToReturn = action;
                    }
                } else {
                    if(sizingInBb > 25) {
                        if(handStrength >= 0.82 || handStrength < 0.56) {
                            actionToReturn = action;
                        } else {
                            actionToReturn = "check";
                            //System.out.println("changed action to check in moderateBetBasedOnEquity(). Board size: " + board.size());
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

    public String moderateBackdoorRaises(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                         double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                         double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                         boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                         boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                         int boardWetness, boolean opponentHasInitiative, Map<Integer, List<Card>> botRange, ContinuousTable continuousTable,
                                         GameVariables gameVariables, BoardEvaluator boardEvaluator, ComputerGameNew computerGameNew) {
        String actionToReturn;

        if(action.equals("raise")) {
            if(handStrength < 0.85) {
                if(strongDraw && !strongFlushDraw && !strongOosd && !strongGutshot && (strongBackdoorFd || strongBackdoorSd)) {
                    if(effectiveStackBb >= 35) {
                        if(!strongBackdoorFd && strongBackdoorSd) {
                            double random = Math.random();

                            if(position) {
                                if(random < 0.35) {
                                    actionToReturn = new Poker().getAction(actionVariables, eligibleActions, street,
                                            position, potSizeBb, opponentAction, facingOdds, effectiveStackBb, false,
                                            handStrength, opponentType, opponentBetSizeBb, ownBetSizeBb, opponentStackBb, ownStackBb,
                                            preflop, board, strongFlushDraw, strongOosd, strongGutshot, bigBlind, opponentDidPreflop4betPot,
                                            pre3betOrPostRaisedPot, strongOvercards, false, false, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
                                } else {
                                    actionToReturn = action;
                                }
                            } else {
                                if(random < 0.5) {
                                    actionToReturn = new Poker().getAction(actionVariables, eligibleActions, street,
                                            position, potSizeBb, opponentAction, facingOdds, effectiveStackBb, false,
                                            handStrength, opponentType, opponentBetSizeBb, ownBetSizeBb, opponentStackBb, ownStackBb,
                                            preflop, board, strongFlushDraw, strongOosd, strongGutshot, bigBlind, opponentDidPreflop4betPot,
                                            pre3betOrPostRaisedPot, strongOvercards, false, false, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
                                } else {
                                    actionToReturn = action;
                                }
                            }
                        } else {
                            actionToReturn = action;
                        }
                    } else {
                        actionToReturn = new Poker().getAction(actionVariables, eligibleActions, street,
                                position, potSizeBb, opponentAction, facingOdds, effectiveStackBb, false,
                                handStrength, opponentType, opponentBetSizeBb, ownBetSizeBb, opponentStackBb, ownStackBb,
                                preflop, board, strongFlushDraw, strongOosd, strongGutshot, bigBlind, opponentDidPreflop4betPot,
                                pre3betOrPostRaisedPot, strongOvercards, false, false, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
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

    public String moderateBackdoorCalls(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                        double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                        double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                        boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                        boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                        int boardWetness, boolean opponentHasInitiative, Map<Integer, List<Card>> botRange, ContinuousTable continuousTable,
                                        GameVariables gameVariables, BoardEvaluator boardEvaluator, ComputerGameNew computerGameNew) {
        String actionToReturn;

        if(action.equals("call")) {
            if(strongDraw && !strongFlushDraw && !strongOosd && !strongGutshot && (strongBackdoorFd || strongBackdoorSd)) {
                if(effectiveStackBb >= 50) {
                    if(position) {
                        //if(opponentBetSizeBb / potSizeBb <= 0.8 && opponentBetSizeBb <= 20) {
                        //    actionToReturn = action;
                        //} else {
                            actionToReturn = new Poker().getAction(actionVariables, eligibleActions, street,
                                    position, potSizeBb, opponentAction, facingOdds, effectiveStackBb, false,
                                    handStrength, opponentType, opponentBetSizeBb, ownBetSizeBb, opponentStackBb, ownStackBb,
                                    preflop, board, strongFlushDraw, strongOosd, strongGutshot, bigBlind, opponentDidPreflop4betPot,
                                    pre3betOrPostRaisedPot, strongOvercards, false, false, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
                        //}
                    } else {
                        actionToReturn = new Poker().getAction(actionVariables, eligibleActions, street,
                                position, potSizeBb, opponentAction, facingOdds, effectiveStackBb, false,
                                handStrength, opponentType, opponentBetSizeBb, ownBetSizeBb, opponentStackBb, ownStackBb,
                                preflop, board, strongFlushDraw, strongOosd, strongGutshot, bigBlind, opponentDidPreflop4betPot,
                                pre3betOrPostRaisedPot, strongOvercards, false, false, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
                    }
                } else {
                    actionToReturn = new Poker().getAction(actionVariables, eligibleActions, street,
                            position, potSizeBb, opponentAction, facingOdds, effectiveStackBb, false,
                            handStrength, opponentType, opponentBetSizeBb, ownBetSizeBb, opponentStackBb, ownStackBb,
                            preflop, board, strongFlushDraw, strongOosd, strongGutshot, bigBlind, opponentDidPreflop4betPot,
                            pre3betOrPostRaisedPot, strongOvercards, false, false, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String playCautiousAgainstPostflop3bet(String action, double potSizeBb, double botTotalBetsizeBb,
                                                  double opponentTotalBetsizeBb, double handStrength, boolean strongOosd,
                                                  boolean strongFd, double botStackBb, double opponentStackBb) {
        String actionToReturn;

        if(facingPostFlop3bet(potSizeBb, botTotalBetsizeBb, opponentTotalBetsizeBb)) {
            if(action.equals("call")) {
                double opponentStackPlusBetSizePlusHalfPotBb = opponentStackBb + opponentTotalBetsizeBb + (potSizeBb / 2);
                double botStackPlusBetSizePlusHalfPotBb = botStackBb + botTotalBetsizeBb + (potSizeBb / 2);

                if(opponentStackPlusBetSizePlusHalfPotBb <= 50 || botStackPlusBetSizePlusHalfPotBb <= 50) {
                    if(handStrength >= 0.8 || strongFd || strongOosd) {
                        actionToReturn = action;
                    } else {
                        actionToReturn = "fold";
                        //System.out.println("changed to fold in postflop facing 3bet pot");
                    }
                } else {
                    if(handStrength >= 0.9 || strongFd || strongOosd) {
                        actionToReturn = action;
                    } else {
                        actionToReturn = "fold";
                        //System.out.println("changed to fold in postflop facing 3bet pot");
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

    private boolean facingPostFlop3bet(double potSizeBb, double botTotalBetsizeBb, double opponentTotalBetsizeBb) {
        boolean facingPostFlop3bet = false;

        if(botTotalBetsizeBb > potSizeBb && opponentTotalBetsizeBb > botTotalBetsizeBb) {
            facingPostFlop3bet = true;
            System.out.println("facing postflop 3bet");
        }

        return facingPostFlop3bet;
    }

    public String moderateCheckRaises(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                      double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                      double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                      boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                      boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                      int boardWetness, boolean opponentHasInitiative, Map<Integer, List<Card>> botRange, ContinuousTable continuousTable,
                                      GameVariables gameVariables, BoardEvaluator boardEvaluator, ComputerGameNew computerGameNew) {
        String actionToReturn;

        if(action.equals("raise")) {
            if(board != null && (board.size() == 3 || board.size() == 4)) {
                if(handStrength < 0.9) {
                    HandEvaluator handEvaluator;

                    if(actionVariables != null) {
                        handEvaluator = actionVariables.getHandEvaluator();
                    } else {
                        handEvaluator = ComputerGameNew.getHandEvaluator();
                    }

                    boolean strongFlushDrawInMethod = handEvaluator.hasDrawOfType("strongFlushDraw");
                    boolean strongOosdInMethod = handEvaluator.hasDrawOfType("strongOosd");
                    boolean strongGutshotInMethod = handEvaluator.hasDrawOfType("strongGutshot");
                    boolean strongBackdoorFdInMethod = handEvaluator.hasDrawOfType("strongBackDoorFlush");
                    boolean strongBackdoorSdInMethod = handEvaluator.hasDrawOfType("strongBackDoorStraight");

                    if(strongFlushDrawInMethod || strongOosdInMethod || strongGutshotInMethod || strongBackdoorFdInMethod || strongBackdoorSdInMethod) {
                        actionToReturn = action;
                    } else {
                        List<String> eligibleActionsNew = new ArrayList<>();
                        eligibleActionsNew.add("fold");
                        eligibleActionsNew.add("call");

                        //set both opponentstack and effective stack to zero to force either fold or call
                        actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                                opponentAction, facingOdds, 0, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);

                        //System.out.println("changed raise to either fold or call in moderateCheckRaises(). Action is now: " + actionToReturn);
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

    public String dontCallWithAir(String action, List<Card> board, double handStrength, double facingOdds, boolean strongDraw, HandEvaluator handEvaluator) {
        String actionToReturn;

        if(action.equals("call")) {
            if(board != null && board.size() >= 3) {
                if(handStrength < 0.5) {
                    if(facingOdds >= 0.42) {
                        boolean strongFlushDrawInMethod = handEvaluator.hasDrawOfType("strongFlushDraw");
                        boolean strongOosdInMethod = handEvaluator.hasDrawOfType("strongOosd");

                        if(strongFlushDrawInMethod || strongOosdInMethod) {
                            actionToReturn = action;
                        } else {
                            actionToReturn = "fold";
                            //System.out.println("Changed action to fold in dontCallWithAir() A");
                        }
                    } else if(facingOdds >= 0.375) {
                        if(!strongDraw) {
                            actionToReturn = "fold";
                            //System.out.println("Changed action to fold in dontCallWithAir() B");
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

    public String alwaysBetOrRaiseAboveHs80(String action, double handStrength, double opponentBetSizeBb, double ownBetSizeBb,
                                            double ownStackBb, double opponentStackBb, double potSizeBb, double bigBlind,
                                            String opponentAction, List<Card> board) {
        String actionToReturn;

        if(board != null && board.size() >= 3) {
            double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind, ownBetSizeBb * bigBlind,
                    ownStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, bigBlind, board);

            double limitBet;
            double limitRaise;

            if(sizing * bigBlind < 20) {
                limitBet = 0.8;
                limitRaise = 0.83;
            } else if(sizing * bigBlind < 40) {
                limitBet = 0.83;
                limitRaise = 0.9;
            } else {
                limitBet = 0.88;
                limitRaise = 0.95;
            }

            if(action.equals("check")) {
                if(handStrength >= limitBet) {
                    if(bluffOddsAreOk(sizing, opponentBetSizeBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind)) {
                        if(Math.random() > 0.15) {
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
            } else if((action.equals("fold") || action.equals("call")) && !opponentAction.equals("raise")) {
                if(handStrength >= limitRaise) {
                    if(bluffOddsAreOk(sizing, opponentBetSizeBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind)) {
                        if(Math.random() > 0.15) {
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

        return actionToReturn;
    }

    public String prepareForBalanceMethod(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                          double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                          double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                          boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                          boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                          int boardWetness, boolean opponentHasInitiative, Map<Integer, List<Card>> botRange, ContinuousTable continuousTable,
                                          GameVariables gameVariables, BoardEvaluator boardEvaluator, ComputerGameNew computerGameNew) {
        String actionToReturn;

        if(action.equals("bet75pct") || action.equals("raise")) {
            double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind, ownBetSizeBb * bigBlind,
                    ownStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, bigBlind, board);

            double valueHsLimit;

            if(sizing <= 4 * bigBlind) {
                valueHsLimit = 0.55;
            } else if(sizing <= 10 * bigBlind) {
                valueHsLimit = 0.72;
            } else {
                valueHsLimit = 0.8;
            }

            if(handStrength < valueHsLimit) {
                if(action.equals("bet75pct")) {
                    actionToReturn = "check";
                } else {
                    List<String> eligibleActionsNew = new ArrayList<>();
                    eligibleActionsNew.add("fold");
                    eligibleActionsNew.add("call");

                    //set both opponentstack and effective stack to zero to force either fold or call
                    actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                            opponentAction, facingOdds, 0, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                            ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                            bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                            strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String balancePlayWithBotRange(String action, Map<Integer, List<Card>> botRange, ContinuousTable continuousTable,
                                          GameVariables gameVariables, BoardEvaluator boardEvaluator, String opponentType,
                                          double handStrength, double opponentBetSizeBb, double ownBetSizeBb, double ownStackBb,
                                          double opponentStackBb, double potSizeBb, double bigBlind, boolean strongFd,
                                          boolean strongOosd, boolean strongGutshot, boolean strongBackdoorFd,
                                          boolean strongBackdoorSd, String opponentAction, List<Card> board,
                                          ComputerGameNew computerGameNew, boolean position) throws Exception {
        String actionToReturn = null;

        if(board != null && board.size() >= 3) {
            if(handStrength < 0.8) {
                if(action.equals("check")) {
                    double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind, ownBetSizeBb * bigBlind,
                            ownStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, bigBlind, board);

                    if(bluffOddsAreOk(sizing, opponentBetSizeBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind)) {
                        actionToReturn = balanceHelperMethod(action, botRange, continuousTable, gameVariables,
                                boardEvaluator, opponentType, strongFd, strongOosd, strongGutshot, strongBackdoorFd,
                                strongBackdoorSd, "bet75pct", computerGameNew, position);
                    } else {
                        actionToReturn = action;
                    }
                } else if(action.equals("fold") && opponentAction.equals("bet75pct")) {
                    double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind, ownBetSizeBb * bigBlind,
                            ownStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, bigBlind, board);

                    if(bluffOddsAreOk(sizing, opponentBetSizeBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind)) {
                        actionToReturn = balanceHelperMethod(action, botRange, continuousTable, gameVariables,
                                boardEvaluator, opponentType, strongFd, strongOosd, strongGutshot, strongBackdoorFd,
                                strongBackdoorSd, "raise", computerGameNew, position);
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

        return actionToReturn;
    }

    private String balanceHelperMethod(String action, Map<Integer, List<Card>> botRange, ContinuousTable continuousTable,
                                       GameVariables gameVariables, BoardEvaluator boardEvaluator, String opponentType,
                                       boolean strongFd, boolean strongOosd, boolean strongGutshot,
                                       boolean strongBackdoorFd, boolean strongBackdoorSd, String actionToUse,
                                       ComputerGameNew computerGameNew, boolean position) throws Exception {
        String actionToReturn = null;

        Map<Integer, List<String>> hsAndActionPerCombo;

        if(continuousTable != null) {
            hsAndActionPerCombo = new BotRange().getHsAndActionPerCombo(botRange, continuousTable, gameVariables,
                    boardEvaluator, opponentType);
        } else {
            hsAndActionPerCombo = new BotRange().getHsAndActionPerComboComputerGame(botRange, computerGameNew, boardEvaluator);
        }

        double valueCombos = new BotRange().getNumberOfValueBetRaiseCombos(hsAndActionPerCombo, actionToUse);

        if(position) {
            valueCombos = valueCombos * 2;
        }

        List<Double> strongDrawCombosCountList = new BotRange().getStrongDrawCombosCountList(botRange, boardEvaluator);
        double strongDrawsNonBackdoorCombos = new BotRange().getNumberOfStrongDrawsNonBackdoor(strongDrawCombosCountList);
        double strongBackdoorDrawCombos = new BotRange().getNumberOfBackdoorStrongDraws(strongDrawCombosCountList);

        if(valueCombos - strongDrawsNonBackdoorCombos >= 0) {
            if(strongFd || strongOosd || strongGutshot) {
                actionToReturn = actionToUse;
                //System.out.println("changed fold to " + actionToUse + " in balance play method 1a");
            }
        } else {
            if(strongFd || strongOosd || strongGutshot) {
                double randomLimitStrongDraws = valueCombos / strongDrawsNonBackdoorCombos;
                double randomStrongDraws = Math.random();

                randomLimitStrongDraws = randomLimitStrongDraws * 2;

                if(randomStrongDraws < randomLimitStrongDraws) {
                    actionToReturn = actionToUse;
                    //System.out.println("changed fold to " + actionToUse + " in balance play method 1b");
                }
            }
        }

        if(actionToReturn == null) {
            if(valueCombos - strongDrawsNonBackdoorCombos - strongBackdoorDrawCombos >= 0) {
                if(strongBackdoorFd || strongBackdoorSd) {
                    actionToReturn = actionToUse;
                    //System.out.println("changed check to " + actionToUse + " in balance play method 2a");
                }
            } else {
                if(strongBackdoorFd || strongBackdoorSd) {
                    double randomLimitStrongBackdoorDraws = (valueCombos - strongDrawsNonBackdoorCombos)
                            / strongBackdoorDrawCombos;
                    double randomBackdoorStrongDraws = Math.random();

                    randomLimitStrongBackdoorDraws = randomLimitStrongBackdoorDraws * 2;

                    if(randomBackdoorStrongDraws < randomLimitStrongBackdoorDraws) {
                        actionToReturn = actionToUse;
                        //System.out.println("changed check to " + actionToUse + " in balance play method 2b");
                    }
                }
            }
        }

        if(actionToReturn == null) {
            double combosBelowLimitHs = new BotRange().getNumberOfCombosBelowHsLimit(hsAndActionPerCombo, 0.73);

            if(valueCombos - strongDrawsNonBackdoorCombos - strongBackdoorDrawCombos - combosBelowLimitHs >= 0) {
                actionToReturn = actionToUse;
                //System.out.println("changed check to " + actionToUse + " in balance play method 3a");
            } else {
                double randomLimit = (valueCombos - strongDrawsNonBackdoorCombos - strongBackdoorDrawCombos)
                        / combosBelowLimitHs;
                double random = Math.random();

                randomLimit = randomLimit * 2;

                if(random < randomLimit) {
                    actionToReturn = actionToUse;
                    //System.out.println("changed check to " + actionToUse + " in balance play method 3b");
                } else {
                    actionToReturn = action;
                }
            }
        }

        return actionToReturn;
    }

    private boolean bluffOddsAreOk(double sizing, double facingBetSize, double facingStackSize, double pot) {
        double sizingInMethod;

        if(sizing > (facingBetSize + facingStackSize)) {
            sizingInMethod = facingBetSize + facingStackSize;
        } else {
            sizingInMethod = sizing;
        }

        double odds = (sizingInMethod - facingBetSize) / (facingBetSize + sizingInMethod + pot);
        return odds > 0.36;
    }
}
