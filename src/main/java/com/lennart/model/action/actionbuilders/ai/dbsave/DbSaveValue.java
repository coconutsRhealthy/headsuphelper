package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;

public class DbSaveValue extends DbSave {

    private String sizingGroup;
    private String street;
    private String oppLoosenessGroup;
    private String position;
    private String valueAction;
    private String handStrength;
    private String strongDraw;
    private String effectiveStack;
    private String drawWetness;
    private String boatWetness;

    public String getOppLoosenessGroupViaLogic(String opponentName) throws Exception {
        String oppLoosenessGroup;

        OpponentIdentifier opponentIdentifier = new OpponentIdentifier();
        int numberOfHands = opponentIdentifier.getOpponentNumberOfHandsFromDb(opponentName);

        if(numberOfHands < 20) {
            oppLoosenessGroup = "Looseness_unknown";
        } else {
            double oppAggressiveness = opponentIdentifier.getOppAggressiveness(opponentName);

            if(oppAggressiveness <= 0.5) {
                oppLoosenessGroup = "Looseness_0_33_";
            } else if(oppAggressiveness < 0.66) {
                oppLoosenessGroup = "Looseness_33_66_";
            } else {
                oppLoosenessGroup = "Looseness_66_100_";
            }
        }

        return oppLoosenessGroup;
    }

    public String getValueActionLogic(String action) {
        String valueAction;

        if(action.equals("bet75pct")) {
            valueAction = "Bet";
        } else {
            valueAction = "Raise";
        }

        return valueAction;
    }

    public String getHandStrengthLogic(double handStrength) {
        String handStrengthString;

        if(handStrength < 0.7) {
            handStrengthString = "HS_-1_";
        } else if(handStrength <= 0.75) {
            handStrengthString = "HS_70_75_";
        } else if(handStrength <= 0.8) {
            handStrengthString = "HS_75_80_";
        } else if(handStrength <= 0.85) {
            handStrengthString = "HS_80_85_";
        } else if(handStrength <= 0.9) {
            handStrengthString = "HS_85_90_";
        } else if(handStrength <= 0.95) {
            handStrengthString = "HS_90_95_";
        } else {
            handStrengthString = "HS_95_100_";
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

    public String getOppLoosenessGroup() {
        return oppLoosenessGroup;
    }

    public void setOppLoosenessGroup(String oppLoosenessGroup) {
        this.oppLoosenessGroup = oppLoosenessGroup;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getValueAction() {
        return valueAction;
    }

    public void setValueAction(String valueAction) {
        this.valueAction = valueAction;
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
