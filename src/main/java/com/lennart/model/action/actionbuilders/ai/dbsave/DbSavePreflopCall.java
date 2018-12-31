package com.lennart.model.action.actionbuilders.ai.dbsave;

public class DbSavePreflopCall extends DbSave {

    private String combo;
    private String position;
    private String amountToCallBb;
    private String oppAggroGroup;
    private String effectiveStack;

    public String getAmountToCallViaLogic(double amountToCallBb) {
        String sizingGroup;

        if(amountToCallBb <= 5) {
            sizingGroup = "Atc_0-5bb";
        } else if(amountToCallBb <= 13) {
            sizingGroup = "Atc_5-13bb";
        } else if(amountToCallBb <= 26) {
            sizingGroup = "Atc_13-26bb";
        } else {
            sizingGroup = "Atc_26bb_up";
        }

        return sizingGroup;
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

    public String getAmountToCallBb() {
        return amountToCallBb;
    }

    public void setAmountToCallBb(String amountToCallBb) {
        this.amountToCallBb = amountToCallBb;
    }

    public String getOppAggroGroup() {
        return oppAggroGroup;
    }

    public void setOppAggroGroup(String oppAggroGroup) {
        this.oppAggroGroup = oppAggroGroup;
    }

    public String getEffectiveStack() {
        return effectiveStack;
    }

    public void setEffectiveStack(String effectiveStack) {
        this.effectiveStack = effectiveStack;
    }
}
