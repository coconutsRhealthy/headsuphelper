package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.util.List;

public class RegAdjuster {

    public String adjustActionToReg(String action, boolean position, List<Card> board, double handstrength, String opponentAction,
                                    ActionVariables actionVariables, ContinuousTable continuousTable, GameVariables gameVariables) {
        try {
            String actionToReturn;

            if(board == null || board.isEmpty()) {
                if(position) {
                    actionToReturn = adjustIpPreflopAction(action, handstrength, opponentAction);
                } else {
                    actionToReturn = adjustOopPreflopAction(action, handstrength, opponentAction);
                }
            } else {
                if(position) {
                    actionToReturn = adjustIpPostflopAction(action, handstrength, board, actionVariables, continuousTable, gameVariables);
                } else {
                    actionToReturn = adjustOopPostflopAction(action, handstrength, board, actionVariables, continuousTable, gameVariables);
                }
            }

            return actionToReturn;
        } catch (Exception e) {
            System.out.println("Exception in RegAdjuster");
            e.printStackTrace();
            return action;
        }
    }

    private String adjustIpPreflopAction(String action, double handstrength, String opponentAction) {
        String actionToReturn = action;

        if(action.equals("raise")) {
            if(opponentAction.equals("bet")) {
                if(handstrength < 0.75) {
                    actionToReturn = "call";
                    System.out.println("RegAdjuster limp instead of openraise");
                }
            }
        }

        return actionToReturn;
    }

    private String adjustOopPreflopAction(String action, double handstrength, String opponentAction) {
        String actionToReturn = action;

        if(action.equals("raise")) {
            if(opponentAction.equals("call")) {
                if(handstrength < 0.65) {
                    actionToReturn = "check";
                    System.out.println("RegAdjuster check instead of raise vs limp");
                }
            }
        }

        return actionToReturn;
    }

//                OOP
//                FLOP BET VANAF hs 0.6
//                TURN BET VANAF hs 0.75
//                RIVER BET VANAF hs 0.65
//
//                FLOP RAISE VANAF hs 0.8
//                TURN RAISE VANAF hs 0.9
//                RIVER RAISE VANAF ...
//
//
//                IP
//                FLOP BET VANAF hs 0.35
//                TURN BET VANAF hs 0.7
//                RIVER BET VANAF hs 0.45
//
//                FLOP RAISE VANAF hs 0.8
//                TURN RAISE VANAF hs 0.9
//                RIVER RAISE VANAF

    private String adjustIpPostflopAction(String action, double handstrength, List<Card> board, ActionVariables actionVariables,
                                          ContinuousTable continuousTable, GameVariables gameVariables) throws Exception {
        String actionToReturn = action;

        if(action.equals("bet75pct")) {
            if(board.size() == 3) {
                if(handstrength < 0.35) {
                    actionToReturn = "check";
                    System.out.println("RegAdjuster postflop IP flop bet to check");
                }
            } else if(board.size() == 4) {
                if(handstrength < 0.7) {
                    actionToReturn = "check";
                    System.out.println("RegAdjuster postflop IP turn bet to check");
                }
            } else if(board.size() == 5) {
                if(handstrength < 0.45) {
                    actionToReturn = "check";
                    System.out.println("RegAdjuster postflop IP river bet to check");
                }
            }
        } else if(action.equals("raise")) {
            if(board.size() == 3 || board.size() == 4) {
                if(handstrength < 0.8) {
                    actionToReturn = actionVariables.getDummyActionOppAllIn(continuousTable, gameVariables);
                    System.out.println("RegAdjuster postflop IP raise to fold or call");
                }
            }
        }

        return actionToReturn;
    }

    private String adjustOopPostflopAction(String action, double handstrength, List<Card> board, ActionVariables actionVariables,
                                           ContinuousTable continuousTable, GameVariables gameVariables) throws Exception {
        String actionToReturn = action;

        if(action.equals("bet75pct")) {
            if(board.size() == 3) {
                if(handstrength < 0.6) {
                    actionToReturn = "check";
                    System.out.println("RegAdjuster postflop Oop flop bet to check");
                }
            } else if(board.size() == 4) {
                if(handstrength < 0.75) {
                    actionToReturn = "check";
                    System.out.println("RegAdjuster postflop Oop turn bet to check");
                }
            } else if(board.size() == 5) {
                if(handstrength < 0.65) {
                    actionToReturn = "check";
                    System.out.println("RegAdjuster postflop Oop river bet to check");
                }
            }
        } else if(action.equals("raise")) {
            if(board.size() == 3 || board.size() == 4) {
                if(handstrength < 0.8) {
                    actionToReturn = actionVariables.getDummyActionOppAllIn(continuousTable, gameVariables);
                    System.out.println("RegAdjuster postflop Oop raise to fold or call");
                }
            }
        }

        return actionToReturn;
    }
}
