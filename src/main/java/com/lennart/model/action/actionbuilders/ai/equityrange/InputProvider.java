package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OppIdentifierPreflopStats;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;

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
        String oppPreCall2betGroup;

        try {
            oppPreCall2betGroup = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName).get("preCall2betGroup");
        } catch (Exception e) {
            e.printStackTrace();
            oppPreCall2betGroup = MEDIUM;
        }

        return oppPreCall2betGroup;
    }

    public static String getOppPreCall3betGroup(String oppName) {
        String oppPreCall3betGroup;

        try {
            oppPreCall3betGroup = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName).get("preCall3betGroup");
        } catch (Exception e) {
            e.printStackTrace();
            oppPreCall3betGroup = MEDIUM;
        }

        return oppPreCall3betGroup;
    }

    public static String getOppPreCall4betUpGroup(String oppName) {
        String oppPreCall4betUpGroup;

        try {
            oppPreCall4betUpGroup = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName).get("preCall4bet_up_group");
        } catch (Exception e) {
            e.printStackTrace();
            oppPreCall4betUpGroup = MEDIUM;
        }

        return oppPreCall4betUpGroup;
    }

    public static String getOppPre2betGroup(String oppName) {
        String oppPre2betGroup;

        try {
            oppPre2betGroup = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName).get("pre2betGroup");
        } catch (Exception e) {
            e.printStackTrace();
            oppPre2betGroup = MEDIUM;
        }

        return oppPre2betGroup;
    }

    public static String getOppPre3betGroup(String oppName) {
        String oppPre3betGroup;

        try {
            oppPre3betGroup = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName).get("pre3betGroup");
        } catch (Exception e) {
            e.printStackTrace();
            oppPre3betGroup = MEDIUM;
        }

        return oppPre3betGroup;
    }

    public static String getOppPre4betUpGroup(String oppName) {
        String oppPre4betGroup;

        try {
            oppPre4betGroup = new OppIdentifierPreflopStats().getOppPreGroupMap(oppName).get("pre4bet_up_group");
        } catch (Exception e) {
            e.printStackTrace();
            oppPre4betGroup = MEDIUM;
        }

        return oppPre4betGroup;
    }

    public static String getOppPostAggroness(String oppName) throws Exception {
        String postAggronessGroup;

        OpponentIdentifier2_0 identifier = new OpponentIdentifier2_0(oppName);

        if(identifier.getNumberOfHands() >= 10) {
            double postAggroness = identifier.getOppPostAggroness();

            if(postAggroness < 0) {
                postAggronessGroup = MEDIUM;
            } else if(postAggroness < 0.3055555555555556) {
                postAggronessGroup = LOW;
            } else if(postAggroness < 0.43902439024390244) {
                postAggronessGroup = MEDIUM;
            } else {
                postAggronessGroup = HIGH;
            }
        } else {
            postAggronessGroup = MEDIUM;
        }

        return postAggronessGroup;
    }

    public static String getOppPostLooseness(String oppName) throws Exception {
        String postLoosenessGroup;

        OpponentIdentifier2_0 identifier = new OpponentIdentifier2_0(oppName);

        if(identifier.getNumberOfHands() >= 10) {
            double postLooseness = identifier.getOppPostLooseness();

            if(postLooseness < 0) {
                postLoosenessGroup = MEDIUM;
            } else if(postLooseness < 0.4) {
                postLoosenessGroup = LOW;
            } else if(postLooseness < 0.5652173913043478) {
                postLoosenessGroup = MEDIUM;
            } else {
                postLoosenessGroup = HIGH;
            }
        } else {
            postLoosenessGroup = MEDIUM;
        }

        return postLoosenessGroup;
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
