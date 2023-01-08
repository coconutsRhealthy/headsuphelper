package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.util.List;
import java.util.Map;

/**
 * Created by LennartMac on 08/01/2023.
 */
public class RiverRegBluffRaiser {

    public static String bluffRaiseRiverVsRegs(String action, List<Card> board, Map<String, List<String>> botActionsOfHand,
                                         int oppNumberOfHands, boolean bluffOddsAreOk, String opponentAction, boolean position, double buyIn) {
        String actionToReturn = action;

        try {
            if(action.equals("fold")) {
                if(oppNumberOfHands > 60) {
                    if(buyIn >= 20) {
                        if(board != null && board.size() == 5) {
                            if(opponentAction.equals("bet75pct")) {
                                if(bluffOddsAreOk) {
                                    if(position) {
                                        List<String> flopActions = botActionsOfHand.get("IP_flop");
                                        List<String> turnActions = botActionsOfHand.get("IP_turn");

                                        if(flopActions.size() == 1 && turnActions.size() == 1) {
                                            String botFlopAction = flopActions.get(0);
                                            String botTurnAction = turnActions.get(0);

                                            if(botFlopAction.equals("check")) {
                                                if(botTurnAction.equals("check")) {
                                                    //here we go for 50% bluffs in range
                                                    //fold: 873, raise: 39, extra needed: 39, %of fold: 4,46%
                                                    if(Math.random() < 0.0446) {
                                                        actionToReturn = "raise";
                                                    }
                                                } else if(botTurnAction.equals("bet75pct")) {
                                                    //fold: 207, raise: 32, extra needed: 21, %of fold: 10,3%
                                                    if(Math.random() < 0.103) {
                                                        actionToReturn = "raise";
                                                    }
                                                } else if(botTurnAction.equals("call")) {
                                                    //fold: 195, raise: 39, extra needed: 26, %of fold: 13,3%
                                                    if(Math.random() < 0.133) {
                                                        actionToReturn = "raise";
                                                    }
                                                }
                                            } else if(botFlopAction.equals("bet75pct")) {
                                                if(botTurnAction.equals("check")) {
                                                    //fold: 1548, raise: 135, extra needed: 90, %of fold: 5,8%
                                                    if(Math.random() < 0.0581) {
                                                        actionToReturn = "raise";
                                                    }
                                                } else if(botTurnAction.equals("bet75pct")) {
                                                    //fold: 409, raise: 102, extra needed: 68, %of fold: 16,6%
                                                    if(Math.random() < 0.166) {
                                                        actionToReturn = "raise";
                                                    }
                                                } else if(botTurnAction.equals("call")) {
                                                    //fold: 168, raise: 25, extra needed: 17, %of fold: 10%
                                                    if(Math.random() < 0.1) {
                                                        actionToReturn = "raise";
                                                    }
                                                }
                                            } else if(botFlopAction.equals("call")) {
                                                if(botTurnAction.equals("check")) {
                                                    //fold: 126, raise: 10, extra needed: 7, %of fold: 5,5%
                                                    if(Math.random() < 0.055) {
                                                        actionToReturn = "raise";
                                                    }
                                                } else if(botTurnAction.equals("bet75pct")) {
                                                    //fold: 42, raise: 8, extra needed: 5, %of fold: 11,9%
                                                    if(Math.random() < 0.119) {
                                                        actionToReturn = "raise";
                                                    }
                                                } else if(botTurnAction.equals("call")) {
                                                    //fold: 442, raise: 52, extra needed: 35, %of fold: 7,9%
                                                    if(Math.random() < 0.079) {
                                                        actionToReturn = "raise";
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        List<String> flopActions = botActionsOfHand.get("OOP_flop");
                                        List<String> turnActions = botActionsOfHand.get("IP_turn");

                                        if((flopActions.size() == 1  || flopActions.size() == 2) &&
                                                (turnActions.size() == 1 || turnActions.size() == 2)) {
                                            String firstBotFlopAction = flopActions.get(0);
                                            String firstBotTurnAction = turnActions.get(0);

                                            String secondBotFlopAction = null;
                                            String secondBotTurnAction = null;

                                            if(flopActions.size() == 2) {
                                                secondBotFlopAction = flopActions.get(1);
                                            }

                                            if(turnActions.size() == 2) {
                                                secondBotTurnAction = turnActions.get(1);
                                            }

                                            if(firstBotFlopAction.equals("check")) {
                                                if(secondBotFlopAction == null) {
                                                    if(firstBotTurnAction.equals("check")) {
                                                        if(secondBotTurnAction == null) {
                                                            //fold: 1319, raise: 22, extra needed: 14, %of fold: 1,06%
                                                            if(Math.random() < 0.0106) {
                                                                actionToReturn = "raise";
                                                            }
                                                        } else {
                                                            if(secondBotTurnAction.equals("call")) {
                                                                //fold: 209, raise: 27, extra needed: 18, %of fold: 8,6%
                                                                if(Math.random() < 0.086) {
                                                                    actionToReturn = "raise";
                                                                }
                                                            }
                                                        }
                                                    } else if(firstBotTurnAction.equals("bet75pct")) {
                                                        if(secondBotTurnAction == null) {
                                                            //fold: 163, raise: 40, extra needed: 26, %of fold: 15,9%
                                                            if(Math.random() < 0.159) {
                                                                actionToReturn = "raise";
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    if(secondBotFlopAction.equals("call")) {
                                                        if(firstBotTurnAction.equals("check")) {
                                                            if(secondBotTurnAction == null) {
                                                                //fold: 160, raise: 13, extra needed: 8, %of fold: 5%
                                                                if(Math.random() < 0.05) {
                                                                    actionToReturn = "raise";
                                                                }
                                                            } else {
                                                                if(secondBotTurnAction.equals("call")) {
                                                                    //fold: 208, raise: 30, extra needed: 20, %of fold: 9,6%
                                                                    if(Math.random() < 0.096) {
                                                                        actionToReturn = "raise";
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if(firstBotFlopAction.equals("bet75pct")) {
                                                if(secondBotFlopAction == null) {
                                                    if(firstBotTurnAction.equals("check")) {
                                                        if(secondBotTurnAction == null) {
                                                            //fold: 268, raise: 4, extra needed: 2, %of fold: 0,74%
                                                            if(Math.random() < 0.0074) {
                                                                actionToReturn = "raise";
                                                            }
                                                        } else {
                                                            if(secondBotTurnAction.equals("call")) {
                                                                //fold: 86, raise: 9, extra needed: 6, %of fold: 7%
                                                                if(Math.random() < 0.07) {
                                                                    actionToReturn = "raise";
                                                                }
                                                            }
                                                        }
                                                    } else if(firstBotTurnAction.equals("bet75pct")) {
                                                        if(secondBotTurnAction == null) {
                                                            //fold: 21, raise: 5, extra needed: 3, %of fold: 14,2%
                                                            if(Math.random() < 0.142) {
                                                                actionToReturn = "raise";
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(action.equals("fold") && actionToReturn.equals("raise")) {
                System.out.println("River bluff raise against reg! Position: " + position + " Number of hands: " + oppNumberOfHands);
            }
        } catch (Exception e) {
            System.out.println("Error in bluffRaiseRiverVsRegs");
            e.printStackTrace();
        }

        return actionToReturn;
    }
}
