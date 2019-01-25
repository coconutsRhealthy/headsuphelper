package com.lennart.model.action.actionbuilders.ai.dbsave;


public class DbSaveBluff extends DbSave {

    private String sizingGroup;
    private String street;
    private String foldStatGroup;
    private String position;
    private String bluffAction;
    private String effectiveStack;
    private String handStrength;
    private String drawWetness;
    private String boatWetness;
    private String strongDraw;
    private String opponentType;

    private String oppPostRaise;
    private String oppPostLooseness;

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

    public String getBluffActionLogic(String action) {
        String bluffAction;

        if(action.equals("bet75pct")) {
            bluffAction = "Bet";
        } else {
            bluffAction = "Raise";
        }

        return bluffAction;
    }

    public String getHandStrengthLogic(double handStrength) {
        String handStrengthString;

        if(handStrength < 0.23) {
            handStrengthString = "HS_0_23_";
        } else if(handStrength <= 0.46) {
            handStrengthString = "HS_23_46_";
        } else {
            handStrengthString = "HS_46_70_";
        }

        return handStrengthString;
    }

    public String getSizingGroup() {
        return sizingGroup;
    }

    public void setSizingGroup(String sizingGroup) {
        this.sizingGroup = sizingGroup;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getFoldStatGroup() {
        return foldStatGroup;
    }

    public void setFoldStatGroup(String foldStatGroup) {
        this.foldStatGroup = foldStatGroup;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getBluffAction() {
        return bluffAction;
    }

    public void setBluffAction(String bluffAction) {
        this.bluffAction = bluffAction;
    }

    public String getEffectiveStack() {
        return effectiveStack;
    }

    public void setEffectiveStack(String effectiveStack) {
        this.effectiveStack = effectiveStack;
    }

    public String getHandStrength() {
        return handStrength;
    }

    public void setHandStrength(String handStrength) {
        this.handStrength = handStrength;
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

    public String getStrongDraw() {
        return strongDraw;
    }

    public void setStrongDraw(String strongDraw) {
        this.strongDraw = strongDraw;
    }

    public String getOpponentType() {
        return opponentType;
    }

    public void setOpponentType(String opponentType) {
        this.opponentType = opponentType;
    }

    public String getOppPostRaise() {
        return oppPostRaise;
    }

    public void setOppPostRaise(String oppPostRaise) {
        this.oppPostRaise = oppPostRaise;
    }

    public String getOppPostLooseness() {
        return oppPostLooseness;
    }

    public void setOppPostLooseness(String oppPostLooseness) {
        this.oppPostLooseness = oppPostLooseness;
    }
}
