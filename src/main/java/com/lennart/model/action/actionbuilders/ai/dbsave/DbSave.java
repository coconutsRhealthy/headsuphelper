package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.card.Card;

import java.util.List;

public class DbSave {

    public String getStreetViaLogic(List<Card> board) {
        String street;

        if(board != null) {
            if(board.size() >= 3) {
                if(board.size() == 3) {
                    street = "Flop";
                } else if(board.size() == 4) {
                    street = "Turn";
                } else {
                    street = "River";
                }
            } else {
                street = "Preflop";
            }
        } else {
            street = "Preflop";
        }

        return street;
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

    public String getSizingGroupViaLogic(double sizingBb) {
        String sizingGroup;

        if(sizingBb <= 5) {
            sizingGroup = "Sizing_0-5bb";
        } else if(sizingBb <= 10) {
            sizingGroup = "Sizing_5-10bb";
        } else if(sizingBb <= 15) {
            sizingGroup = "Sizing_10-15bb";
        } else if(sizingBb <= 20) {
            sizingGroup = "Sizing_15-20bb";
        } else if(sizingBb <= 30) {
            sizingGroup = "Sizing_20-30bb";
        } else {
            sizingGroup = "Sizing_30bb_up";
        }

        return sizingGroup;
    }

    public String getStrongDrawLogic(boolean strongFlushDraw, boolean strongOosd) {
        String strongDraw;

        if(strongFlushDraw || strongOosd) {
            strongDraw = "StrongDrawTrue";
        } else {
            strongDraw = "StrongDrawFalse";
        }

        return strongDraw;
    }
}
