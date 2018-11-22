package com.lennart.model.action.actionbuilders.ai.dbsave;


public class DbSaveBluff extends DbSave {

    private String sizingGroup;
    private String street;
    private String foldStatGroup;
    private String position;
    private String bluffAction;
    private String effectiveStack;

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
}
