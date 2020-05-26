package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.GameVariables;
import java.util.List;

/**
 * Created by LennartMac on 25/05/2020.
 */
public class BotActionBuilder {

    public String getAction(ContinuousTable continuousTable, GameVariables gameVariables,
                               List<String> eligibleActions, double sizing, boolean opponentHasInitiative) {
        String action = null;

        Rules rules = new Rules();

        action = rules.getInitialRuleAction(gameVariables, opponentHasInitiative, eligibleActions);

        if(action == null) {
            InputProvider inputProvider = new InputProvider();
            EquityAction equityAction = new EquityAction(inputProvider);

            action = equityAction.getValueAction(continuousTable, gameVariables, eligibleActions, sizing);

            action = rules.getValueTrapAction();

            if(action.equals("fold") || action.equals("check")) {
                action = new BluffAction(equityAction).getBluffAction(
                        action,
                        rules.isValueTrap(),
                        eligibleActions,
                        continuousTable,
                        gameVariables,
                        sizing);
            }

            action = rules.getAfterRuleAction();
        }

        return action;
    }
}
