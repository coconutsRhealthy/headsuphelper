package com.lennart.model.action.actionbuilders.ai.dbsave;

public class DbSavePreflopRaise extends DbSave {

    private String combo;
    private String position;
    private String sizing;
    private String foldStatGroup;
    private String effectiveStack;

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

    @Override
    public String getEffectiveStackLogic(double botStackBb, double opponentStackBb) {
        String effectiveStackBbString;
        double effectiveStackBb;

        if(botStackBb > opponentStackBb) {
            effectiveStackBb = opponentStackBb;
        } else {
            effectiveStackBb = botStackBb;
        }

        if(effectiveStackBb <= 10) {
            effectiveStackBbString = "Effstack_0-10bb";
        } else if(effectiveStackBb <= 30) {
            effectiveStackBbString = "Effstack_10-30bb";
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

    public String getCombo() {
        return combo;
    }

    public void setCombo(String combo) {
        this.combo = combo;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSizing() {
        return sizing;
    }

    public void setSizing(String sizing) {
        this.sizing = sizing;
    }

    public String getFoldStatGroup() {
        return foldStatGroup;
    }

    public void setFoldStatGroup(String foldStatGroup) {
        this.foldStatGroup = foldStatGroup;
    }

    public String getEffectiveStack() {
        return effectiveStack;
    }

    public void setEffectiveStack(String effectiveStack) {
        this.effectiveStack = effectiveStack;
    }
}
