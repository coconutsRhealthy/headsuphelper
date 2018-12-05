package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.util.List;

public class ShortStackedPreflopPlay {

    private void adjustPlay(String initialAction, List<Card> holeCards, List<Card> board, double effectiveStackBb) {
        if(board == null || board.isEmpty()) {
            if(effectiveStackBb <= 10) {
                if(initialAction.equals("fold")) {




                } else if(initialAction.equals("check")) {



                } else if(initialAction.equals("call")) {



                } else if(initialAction.equals("raise")) {
                    //change sizing to

                }





            }




        }




    }

    private double adjustSizing(String action, double botStack) {
        return 0;
    }


}
