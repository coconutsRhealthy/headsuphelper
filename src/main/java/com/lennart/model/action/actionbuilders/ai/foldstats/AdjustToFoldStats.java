package com.lennart.model.action.actionbuilders.ai.foldstats;

/**
 * Created by LennartMac on 24/05/2018.
 */
public class AdjustToFoldStats {

    public static String adjustPlayToBotFoldStat(String action) {
        String actionToReturn;

        if(action.equals("fold") || action.equals("call")) {
            double botFoldStat = FoldStatsKeeper.getFoldStat("bot");

            System.out.println("botFoldStat: " + botFoldStat);

            if(botFoldStat > 0.5) {
                double random = Math.random();

                if(botFoldStat < 0.6) {
                    if(random > 0.8) {
                        actionToReturn = "raise";
                    } else {
                        actionToReturn = action;
                    }
                } else if(botFoldStat < 0.7) {
                    if(random > 0.65) {
                        actionToReturn = "raise";
                    } else {
                        actionToReturn = action;
                    }
                } else {
                    if(random > 0.5) {
                        actionToReturn = "raise";
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

    public String adjustPlayToOpponentFoldStat() {
        return null;
    }


}
