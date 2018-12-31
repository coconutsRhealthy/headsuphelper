package com.lennart.model.action.actionbuilders.ai.dbsave;


public class DbSaveCall extends DbSave {

    private String amountToCallGroup;
    private String street;
    private String oppAggroGroup;
    private String position;
    private String facingAction;
    private String handStrength;
    private String strongDraw;
    private String effectiveStack;

    private String drawWetness;
    private String boatWetness;

    public String getAmountToCallViaLogic(double amountToCallBb) {
        String sizingGroup;

        if(amountToCallBb <= 5) {
            sizingGroup = "Atc_0-5bb";
        } else if(amountToCallBb <= 10) {
            sizingGroup = "Atc_5-10bb";
        } else if(amountToCallBb <= 15) {
            sizingGroup = "Atc_10-15bb";
        } else if(amountToCallBb <= 20) {
            sizingGroup = "Atc_15-20bb";
        } else if(amountToCallBb <= 30) {
            sizingGroup = "Atc_20-30bb";
        } else {
            sizingGroup = "Atc_30bb_up";
        }

        return sizingGroup;
    }

    public String getFacingActionViaLogic(String opponentAction) {
        String facingAction;

        if(opponentAction.equals("bet75pct")) {
            facingAction = "FacingBet";
        } else {
            facingAction = "FacingRaise";
        }

        return facingAction;
    }

    public String getHandStrengthLogic(double handStrength) {
        String handStrengthString;

        if(handStrength < 0.3) {
            handStrengthString = "HS_0_30_";
        } else if(handStrength <= 0.5) {
            handStrengthString = "HS_30_50_";
        } else if(handStrength <= 0.6) {
            handStrengthString = "HS_50_60_";
        } else if(handStrength <= 0.7) {
            handStrengthString = "HS_60_70_";
        } else if(handStrength <= 0.8) {
            handStrengthString = "HS_70_80_";
        } else if(handStrength <= 0.9) {
            handStrengthString = "HS_80_90_";
        } else {
            handStrengthString = "HS_90_100_";
        }

        return handStrengthString;
    }

    public String getAmountToCallGroup() {
        return amountToCallGroup;
    }

    public void setAmountToCallGroup(String amountToCallGroup) {
        this.amountToCallGroup = amountToCallGroup;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getOppAggroGroup() {
        return oppAggroGroup;
    }

    public void setOppAggroGroup(String oppAggroGroup) {
        this.oppAggroGroup = oppAggroGroup;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getFacingAction() {
        return facingAction;
    }

    public void setFacingAction(String facingAction) {
        this.facingAction = facingAction;
    }

    public String getHandStrength() {
        return handStrength;
    }

    public void setHandStrength(String handStrength) {
        this.handStrength = handStrength;
    }

    public String getStrongDraw() {
        return strongDraw;
    }

    public void setStrongDraw(String strongDraw) {
        this.strongDraw = strongDraw;
    }

    public String getEffectiveStack() {
        return effectiveStack;
    }

    public void setEffectiveStack(String effectiveStack) {
        this.effectiveStack = effectiveStack;
    }

    public String getDrawWetness() {
        return drawWetness;
    }

    public void setDrawWetness(String drawWetness) {
        this.drawWetness = drawWetness;
    }

    public String getBoatWetness() {
        return boatWetness;
    }

    public void setBoatWetness(String boatWetness) {
        this.boatWetness = boatWetness;
    }
}
