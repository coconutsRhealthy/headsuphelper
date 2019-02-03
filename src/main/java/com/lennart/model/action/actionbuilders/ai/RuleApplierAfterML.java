package com.lennart.model.action.actionbuilders.ai;

/**
 * Created by LennartMac on 03/02/2019.
 */
public class RuleApplierAfterML {

    public String moderateBluffInOpp3betPostRaisedPost(String action, double handStrength, ContinuousTable continuousTable,
                                                       GameVariables gameVariables) throws Exception {
        String actionToReturn;

        if(continuousTable.isOppDidPre3betPostRaise()) {
            if(action.equals("bet75pct")) {
                if(handStrength < 0.7) {
                    double random = Math.random();

                    if(random < 0.8) {
                        actionToReturn = "check";
                        System.out.println("RuleApplierAfterML prevent bluff bet");
                    } else {
                        actionToReturn = action;
                        System.out.println("RuleApplierAfterML zzz bluff bet");
                    }
                } else {
                    actionToReturn = action;
                }
            } else if(action.equals("raise")) {
                if(handStrength < 0.7) {
                    double random = Math.random();

                    if(random < 0.8) {
                        actionToReturn = new ActionVariables().getDummyAction(continuousTable, gameVariables);
                        System.out.println("RuleApplierAfterML prevent bluff raise, call dummy ActionVariables method");
                    } else {
                        actionToReturn = action;
                        System.out.println("RuleApplierAfterML zzz bluff raise");
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
                if(handStrength < 0.9) {
                    double random = Math.random();

                    if(random < 0.8) {
                        actionToReturn = "check";
                        System.out.println("RuleApplierAfterML prevent value bet");
                    } else {
                        actionToReturn = action;
                        System.out.println("RuleApplierAfterML zzz value bet");
                    }
                } else {
                    actionToReturn = action;
                }
            } else if(action.equals("raise")) {
                if(handStrength < 0.9) {
                    double random = Math.random();

                    if(random < 0.8) {
                        actionToReturn = new ActionVariables().getDummyAction(continuousTable, gameVariables);
                        System.out.println("RuleApplierAfterML prevent value raise, call dummy ActionVariables method");
                    } else {
                        actionToReturn = action;
                        System.out.println("RuleApplierAfterML zzz value raise");
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
