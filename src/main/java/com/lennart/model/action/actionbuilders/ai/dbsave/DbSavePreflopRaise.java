package com.lennart.model.action.actionbuilders.ai.dbsave;

public class DbSavePreflopRaise {

    private String handStrength;
    private String position;
    private String sizing;
    private String foldStatGroup;
    private String effectiveStack;

    public String getHandStrengthLogic(double handStrength) {
        String handStrengthString;

        if(handStrength < 0.2) {
            handStrengthString = "HS_0_20_";
        } else if(handStrength <= 0.35) {
            handStrengthString = "HS_20_35_";
        } else if(handStrength <= 0.5) {
            handStrengthString = "HS_35_50_";
        } else if(handStrength <= 0.6) {
            handStrengthString = "HS_50_60_";
        } else if(handStrength <= 0.7) {
            handStrengthString = "HS_60_70_";
        } else if(handStrength <= 0.75) {
            handStrengthString = "HS_70_75_";
        } else if(handStrength <= 0.80) {
            handStrengthString = "HS_75_80_";
        } else if(handStrength <= 0.85) {
            handStrengthString = "HS_80_85_";
        } else if(handStrength <= 0.90) {
            handStrengthString = "HS_85_90_";
        } else if(handStrength <= 0.95) {
            handStrengthString = "HS_90_95_";
        } else {
            handStrengthString = "HS_95_100_";
        }

        return handStrengthString;
    }

    public String getPositionLogic(boolean position) {
        String positionString;

        if(position) {
            positionString = "Ip";
        } else {
            positionString = "Oop";
        }

        return positionString;
    }

    public String getSizingLogic(double sizingBb) {
        String sizingString;

        if(sizingBb <= 5) {
            sizingString = "Sizing_0-5bb";
        } else if(sizingBb <= 13) {
            sizingString = "Sizing_5-13bb";
        } else if(sizingBb <= 26) {
            sizingString = "Sizing_13-26bb";
        } else {
            sizingString = "Sizing_26bb_up";
        }

        return sizingString;
    }

    public String getFoldStatGroupLogic(double foldStat) {
        String foldStatGroup;

        if(foldStat < 0.26) {
            foldStatGroup = "Foldstat_0_33_";
        } else if(foldStat <= 0.4) {
            foldStatGroup = "Foldstat_33_66_";
        } else if(foldStat == 0.43) {
            foldStatGroup = "Foldstat_unknown";
        } else {
            foldStatGroup = "Foldstat_66_100_";
        }

        return foldStatGroup;
    }

    public String getEffectiveStackLogic(double botStackBb, double opponentStackBb) {
        String effectiveStackBbString;
        double effectiveStackBb;

        if(botStackBb > opponentStackBb) {
            effectiveStackBb = opponentStackBb;
        } else {
            effectiveStackBb = botStackBb;
        }

        if(effectiveStackBb <= 10) {
            effectiveStackBbString = "EffStack_0_10bb";
        } else if(effectiveStackBb <= 30) {
            effectiveStackBbString = "EffStack_10_30bb";
        } else if(effectiveStackBb <= 50) {
            effectiveStackBbString = "Effstack_30-50bb";
        } else if(effectiveStackBb <= 75) {
            effectiveStackBbString = "Effstack_50-75bb";
        } else if(effectiveStackBb <= 110) {
            effectiveStackBbString = "Effstack_75-110bb";
        } else {
            effectiveStackBbString = "Effstack_110bb_up";
        }

        return effectiveStackBbString;
    }
}
