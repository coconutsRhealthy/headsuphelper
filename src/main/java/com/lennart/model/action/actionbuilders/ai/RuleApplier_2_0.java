package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbstatsraw.DbStatsRawBluffPostflopMigrator;

/**
 * Created by LennartMac on 03/02/2019.
 */
public class RuleApplier_2_0 {

    //new method to prevent bluff raising against opp type D, unless route proves otherwise later

    public String moderateBluffInOpp3betPostRaisedPost(String action, double handStrength, ContinuousTable continuousTable,
                                                       GameVariables gameVariables) throws Exception {
        String actionToReturn;

        if(continuousTable.isOppDidPre3betPostRaise()) {
            if(action.equals("bet75pct")) {
                if(handStrength < 0.7) {
                    double random = Math.random();

                    if(random < 0.8) {
                        actionToReturn = "check";
                        System.out.println("RuleApplier_2_0 prevent bluff bet");
                    } else {
                        actionToReturn = action;
                        System.out.println("RuleApplier_2_0 zzz bluff bet");
                    }
                } else {
                    actionToReturn = action;
                }
            } else if(action.equals("raise")) {
                if(handStrength < 0.7) {
                    double random = Math.random();

                    if(random < 0.8) {
                        actionToReturn = new ActionVariables().getDummyAction(continuousTable, gameVariables);
                        System.out.println("RuleApplier_2_0 prevent bluff raise, call dummy ActionVariables method");
                    } else {
                        actionToReturn = action;
                        System.out.println("RuleApplier_2_0 zzz bluff raise");
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

    public String moderateValueBettingInOpp3betPostRaisedPot(String action, double handStrength, ContinuousTable continuousTable,
                                                             GameVariables gameVariables) throws Exception {
        String actionToReturn;

        if(continuousTable.isOppDidPre3betPostRaise()) {
            if(action.equals("bet75pct")) {
                if(handStrength < 0.9 && handStrength >= 0.7) {
                    double random = Math.random();

                    if(random < 0.8) {
                        actionToReturn = "check";
                        System.out.println("RuleApplier_2_0 prevent value bet");
                    } else {
                        actionToReturn = action;
                        System.out.println("RuleApplier_2_0 zzz value bet");
                    }
                } else {
                    actionToReturn = action;
                }
            } else if(action.equals("raise")) {
                if(handStrength < 0.9 && handStrength >= 0.7 && gameVariables.getBoard() != null && !gameVariables.getBoard().isEmpty()) {
                    double random = Math.random();

                    if(random < 0.8) {
                        actionToReturn = new ActionVariables().getDummyAction(continuousTable, gameVariables);
                        System.out.println("RuleApplier_2_0 prevent value raise, call dummy ActionVariables method");
                    } else {
                        actionToReturn = action;
                        System.out.println("RuleApplier_2_0 zzz value raise");
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

    public String moderatePre3betCalls(String action, GameVariables gameVariables, ActionVariables actionVariables, double effectiveStackBb) throws Exception {
        String actionToReturn;

        if(action.equals("call")) {
            if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
                if(gameVariables.isBotIsButton()) {
                    if(effectiveStackBb > 15) {
                        String opponentType = new DbStatsRawBluffPostflopMigrator().getOpponentGroup(gameVariables.getOpponentName());

                        if(opponentType.equals("OppTypeA") || opponentType.equals("OppTypeB")) {
                            if(actionVariables.getBotHandStrength() < 0.8) {
                                double random = Math.random();

                                if(random < 0.8) {
                                    actionToReturn = "fold";
                                    System.out.println("RuleApplier_2_0-A-B prevent pre3bet call");
                                } else {
                                    actionToReturn = action;
                                    System.out.println("RuleApplier_2_0-A-B zzz pre3bet call");
                                }
                            } else {
                                actionToReturn = action;
                            }
                        } else {
                            if(actionVariables.getBotHandStrength() < 0.9) {
                                double random = Math.random();

                                if(random < 0.8) {
                                    actionToReturn = "fold";
                                    System.out.println("RuleApplier_2_0-C-D prevent pre3bet call");
                                } else {
                                    actionToReturn = action;
                                    System.out.println("RuleApplier_2_0-C-D zzz pre3bet call");
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
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String moderatePostRaiseCalls(String action, GameVariables gameVariables, ActionVariables actionVariables, double effectiveStackBb,
                                         double facingOdds) throws Exception {
        String actionToReturn;

        if(action.equals("call")) {
            if(gameVariables.getBoard() != null && !gameVariables.getBoard().isEmpty()) {
                if(gameVariables.getOpponentAction().equals("raise")) {
                    if(effectiveStackBb > 15) {
                        if(actionVariables.getBotHandStrength() < 0.9) {
                            if(facingOdds > 0.23) {
                                double random = Math.random();

                                if(random < 0.8) {
                                    actionToReturn = "fold";
                                    System.out.println("RuleApplier_2_0 prevent post raise call");
                                } else {
                                    actionToReturn = action;
                                    System.out.println("RuleApplier_2_0 zzz post raise call");
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
}
