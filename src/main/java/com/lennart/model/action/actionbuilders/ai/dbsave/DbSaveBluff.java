package com.lennart.model.action.actionbuilders.ai.dbsave;


public class DbSaveBluff extends DbSave {

    private String sizingGroup;
    private String street;
    private String foldStatGroup;
    private String position;
    private String bluffAction;
    private String strongDraw;

    public String getFoldStatGroupLogic(double foldStat) {
        String foldStatGroup;

        if(foldStat < 0.43) {
            foldStatGroup = "Foldstat_below_0.43";
        } else if(foldStat == 0.43) {
            foldStatGroup = "Foldstat_exactly_0.43";
        } else {
            foldStatGroup = "Foldstat_up_0.43";
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

    public String getStrongDraw() {
        return strongDraw;
    }

    public void setStrongDraw(String strongDraw) {
        this.strongDraw = strongDraw;
    }
}
