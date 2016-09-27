package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LPO10346 on 9/27/2016.
 */
public class FullHouseEvaluator extends BoardEvaluator {

    public Map<Integer, Set<Set<Card>>> getFullHouseCombos(List<Card> board) {

        //een pair op board
        if(getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            //verwijder het boardpair

            //get de trips combos van de andere kaarten

        }

        //twee pair op board
        if(getNumberOfPairsOnBoard(board) == 2 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            //alle kaarten die pairen met een van de twee boardpairs

            //als er 5 kaarten liggen dan ook de combos die set maken met de 5e kaart
        }

        //trips op board
        if(getNumberOfPairsOnBoard(board) == 0 && boardContainsTrips(board) && !boardContainsQuads(board)) {
            //alle combos die pairen met een van de twee andere boardkaarten

            //alle pocketpairs die niet met het board matchen

        }

        //boat op board
        if(getNumberOfPairsOnBoard(board) == 1 && boardContainsTrips(board) && !boardContainsQuads(board)) {
            //alle combos die niet met het board matchen

            //alle pocket pairs die de FH op het board niet verbeteren

        }

        return null;
    }
}
