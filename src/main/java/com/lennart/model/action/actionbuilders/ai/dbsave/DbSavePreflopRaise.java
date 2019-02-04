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
        String effectiveStack;
        double effectiveStackBb;

        if(botStackBb > opponentStackBb) {
            effectiveStackBb = opponentStackBb;
        } else {
            effectiveStackBb = botStackBb;
        }

        if(effectiveStackBb <= 35) {
            effectiveStack = "EffStack_0_35_";
        } else {
            effectiveStack = "EffStack_35_up_";
        }

        return effectiveStack;
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
