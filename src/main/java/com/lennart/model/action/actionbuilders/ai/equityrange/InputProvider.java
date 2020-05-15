package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OppIdentifierPreflopStats;

/**
 * Created by LennartMac on 13/05/2020.
 */
public class InputProvider {

    private static final String LOW = "low";
    private static final String MEDIUM = "medium";
    private static final String HIGH = "high";
    private static final String SMALL = "small";
    private static final String LARGE = "large";

    public static String getOppPreCall2betGroup(String oppName) {
        return null;
    }

    public static String getOppPreCall3betGroup(String oppName) {
        return null;
    }

    public static String getOppPreCall4betUpGroup(String oppName) {
        return null;
    }

    public static String getOppPre2betGroup(String oppName) {
        String oppPre2betGroup;

        try {
            oppPre2betGroup = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName).get("pre2bet");
        } catch (Exception e) {
            e.printStackTrace();
            oppPre2betGroup = MEDIUM;
        }

        return oppPre2betGroup;
    }

    public static String getOppPre3betGroup(String oppName) {
        String oppPre3betGroup;

        try {
            oppPre3betGroup = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName).get("pre3bet");
        } catch (Exception e) {
            e.printStackTrace();
            oppPre3betGroup = MEDIUM;
        }

        return oppPre3betGroup;
    }

    public static String getOppPre4betUpGroup(String oppName) {
        String oppPre4betGroup;

        try {
            oppPre4betGroup = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName).get("pre4bet_up");
        } catch (Exception e) {
            e.printStackTrace();
            oppPre4betGroup = MEDIUM;
        }

        return oppPre4betGroup;
    }

    public static String getOppPostAggroness(String oppName) {
        return null;
    }

    public static String getOppPostLooseness(String oppName) {
        return null;
    }

    public static String getBotSizingGroup(double sizing) {
        return null;
    }

    public static String getOppSizingGroup(double oppTotalBetsize) {
        String oppSizingGroup;

        if(oppTotalBetsize <= 60) {
            oppSizingGroup = "small";
        } else if(oppTotalBetsize <= 160) {
            oppSizingGroup = MEDIUM;
        } else {
            oppSizingGroup = LARGE;
        }

        return oppSizingGroup;
    }

    public static String getPotSizeGroup(double potSize) {
        return null;
    }

    public static String determinBotPreflopRaiseType() {
        return null;
    }

    public static String determineOppPreflopRaiseType() {
        return null;
    }
}
