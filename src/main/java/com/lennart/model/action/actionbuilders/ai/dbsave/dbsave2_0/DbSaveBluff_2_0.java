package com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0;

import com.lennart.model.card.Card;

import java.util.List;

/**
 * Created by LennartMac on 16/01/2019.
 */
public class DbSaveBluff_2_0 {

    public String getBoardWetnessGroupLogic(List<Card> board, int boardWetness) {
        String boardWetnessGroup = "";

        if(board.size() == 3) {
            //flushStraightWetness number
            if(boardWetness <= 112) {
                boardWetnessGroup = "dry";
            } else if(boardWetness <= 167) {
                boardWetnessGroup = "medium";
            } else {
                boardWetnessGroup = "wet";
            }
        } else if(board.size() == 4) {
            if(boardWetness <= 67) {
                boardWetnessGroup = "wet";
            } else if(boardWetness <= 99) {
                boardWetnessGroup = "medium";
            } else {
                boardWetnessGroup = "dry";
            }
        } else if(board.size() == 5) {
            if(boardWetness <= 66) {
                boardWetnessGroup = "wet";
            } else if(boardWetness <= 94) {
                boardWetnessGroup = "medium";
            } else {
                boardWetnessGroup = "dry";
            }
        }

        return boardWetnessGroup;
    }
}
