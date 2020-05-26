package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.GameVariables;

import java.util.List;

/**
 * Created by LennartMac on 25/05/2020.
 */
public class Rules {


    //sizing blijft zoals ie is

    //altijd pre2betten bij blinds lager dan 50

    //altijd pre limpen bij blinds vanaf 50

    //callen met draws en goede odds

    //altijd callen met super goede odds

    private boolean valueTrap = false;


    public String getInitialRuleAction(GameVariables gameVariables, boolean opponentHasInitiative, List<String> eligibleActions) {
        String initialRuleAction = null;

        initialRuleAction = defaultCheckWhenOppHasInitiative();

        if(initialRuleAction == null) {
            initialRuleAction = preOpen();
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
                    }
                } else {
                    actionToReturn = valueTrapActionToUse;
                }
            } else {
                actionToReturn = currentAction;
            }
        } else {
            actionToReturn = currentAction;
        }

        return actionToReturn;
    }

    public String getAfterRuleAction() {
        return null;
    }

    public boolean isValueTrap() {
        return valueTrap;
    }

    private String defaultCheckWhenOppHasInitiative() {
        return null;
    }

    private String preOpen() {
        return null;
    }

    private String callWithStrongDrawAndGoodOdds() {
        return null;
    }

    private String callWithFavorableOdds() {
        return null;
    }
}
