package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;

import java.util.concurrent.TimeUnit;

/**
 * Created by Lennart on 3/12/2018.
 */
public class ContinuousTable {

    public void runTableContinously() throws Exception {

        GameVariables gameVariables = new GameVariables();

        while(true) {
            TimeUnit.MILLISECONDS.sleep(200);
            if(NetBetTableReader.botIsToAct()) {
                if(NetBetTableReader.isNewHand()) {
                    gameVariables = new GameVariables("preventDefault");
                } else {
                    gameVariables.fillFieldsSubsequent();
                }

                ActionVariables actionVariables = new ActionVariables(gameVariables);
                String action = actionVariables.getAction();
                double sizing = actionVariables.getSizing();

                NetBetTableReader.performActionOnSite(action, sizing);
            }
        }
    }



}
