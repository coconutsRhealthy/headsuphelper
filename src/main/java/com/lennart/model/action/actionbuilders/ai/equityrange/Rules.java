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



        return null;
    }

    public String getValueTrapAction() {
        return null;
    }

    public String getAfterRuleAction() {
        return null;
    }

    private String getInitialPreflopAction() {


        return null;
    }

    public boolean isValueTrap() {
        return valueTrap;
    }
}
