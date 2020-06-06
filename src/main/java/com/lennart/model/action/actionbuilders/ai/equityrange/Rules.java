package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.GameVariables;


/**
 * Created by LennartMac on 25/05/2020.
 */
public class Rules {

    private boolean valueTrap = false;

    public String getInitialRuleAction(GameVariables gameVariables, boolean opponentHasInitiative) {
        String initialRuleAction = null;

        initialRuleAction = defaultCheckWhenOppHasInitiative(opponentHasInitiative, gameVariables.getOpponentAction());

        if(initialRuleAction == null) {
            initialRuleAction = preOpen(gameVariables.getOpponentAction(), gameVariables.getBigBlind(), gameVariables.getBotStack());
        }

        return initialRuleAction;
    }

    public String getValueTrapAction(String currentAction, GameVariables gameVariables) {
        String actionToReturn;

        if(currentAction.equals("bet75pct") || currentAction.equals("raise")) {
            double valueTrapDecimal = 0.065;

            if(Math.random() < valueTrapDecimal) {
                String valueTrapActionToUse;

                if(currentAction.equals("bet75pct")) {
                    valueTrapActionToUse = "check";
                } else {
                    valueTrapActionToUse = "call";
                }

                if(gameVariables.getBoard() != null && gameVariables.getBoard().size() == 5) {
                    if(gameVariables.isBotIsButton()) {
                        actionToReturn = currentAction;
                    } else {
                        actionToReturn = valueTrapActionToUse;
                        valueTrap = true;
                    }
                } else {
                    actionToReturn = valueTrapActionToUse;
                    valueTrap = true;
                }
            } else {
                actionToReturn = currentAction;
            }
        } else {
            actionToReturn = currentAction;
        }

        return actionToReturn;
    }

    public String getAfterRuleAction(String action, double facingOdds) {
        String actionToReturn;

        if(action.equals("fold")) {
            actionToReturn = callWithFavorableOdds(action, facingOdds);
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public boolean isValueTrap() {
        return valueTrap;
    }

    private String defaultCheckWhenOppHasInitiative(boolean oppHasInitiative, String oppAction) {
        String actionToReturn = null;

        if(oppHasInitiative && oppAction.equals("empty")) {
            actionToReturn = "check";
        }

        return actionToReturn;
    }

    private String preOpen(String oppAction, double bigBlind, double botStack) {
        String actionToReturn = null;

        if(oppAction.equals("bet")) {
            if(bigBlind < 40) {
                //todo: add handstrength

                if(botStack > bigBlind) {
                    System.out.println("pre open 2bet rule");
                    actionToReturn = "raise";
                } else {
                    actionToReturn = "call";
                }
            } else {
                actionToReturn = "call";
            }
        }

        return actionToReturn;
    }

    private String callWithFavorableOdds(String action, double facingOdds) {
        String actionToReturn;

        if(action.equals("fold")) {
            System.out.println("facingodds: " + facingOdds);

            if(facingOdds <= 0.2) {
                actionToReturn = "call";
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }
}
