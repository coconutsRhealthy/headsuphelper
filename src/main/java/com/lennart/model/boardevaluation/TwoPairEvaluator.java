package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by LPO10346 on 9/1/2016.
 */
public class TwoPairEvaluator extends BoardEvaluator {

    //class gaat na of je two pair hebt.

    public Map<Integer, List<Card>> getCombosThatMakeTwoPair (List<Card> board) {
        Map<Integer, List<Card>> combosThatMakeTwoPair = new HashMap<>();

        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnlyCorrectedForBoard(allPossibleStartHands, board);

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                if (boardRanks.containsAll(entry.getValue())) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                }
            }

            //verwijder pocket pairs
            for(Iterator<Map.Entry<Integer, List<Card>>> it = combosThatMakeTwoPair.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Integer, List<Card>> entry = it.next();
                if(entry.getValue().get(0).getRank() == entry.getValue().get(1).getRank()) {
                    it.remove();
                }
            }
            return combosThatMakeTwoPair;
        } else if (getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnlyCorrectedForBoard(allPossibleStartHands, board);

            //4 4 5 8 K
            //4 7 7 9 J
            //alle combos die pairen met één andere kaart op het board
            //alle combos die pairen met twee kaarten van het board, allebei boven het pair dat er al ligt
            int counter = 0;
            int rankOfPairOnBoard = 0;
            Map<Integer, Integer> frequencyOfRanksOnBoard = getFrequencyOfRanksOnBoard(board);

            for (Map.Entry<Integer, Integer> entry : frequencyOfRanksOnBoard.entrySet()) {
                if (entry.getValue() == 2) {
                    rankOfPairOnBoard = entry.getKey();
                }
            }

            Map<Integer, Integer> rankOfComboCardsThatPairWithBoard = new HashMap<>();

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                for(Card c : board) {
                    if (Collections.frequency(entry.getValue(), c.getRank()) == 1 && c.getRank() != rankOfPairOnBoard) {
                        combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                        //rankOfComboCardsThatPairWithBoard.put(rankOfComboCardsThatPairWithBoard.size(), c.getRank());
                        //counter++;
                    }
                }
//                if(counter == 1) {
//                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
//                }
//
//                if(counter == 2) {
//                    int rankOfFirstPair = rankOfComboCardsThatPairWithBoard.get(0);
//                    int rankOfSecondPair = rankOfComboCardsThatPairWithBoard.get(1);
//                    if(rankOfFirstPair > rankOfPairOnBoard && rankOfSecondPair > rankOfPairOnBoard) {
//                        combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
//                    }
//                }
            }

            //voeg pocket pairs toe
            Map<Integer, List<Card>> allPocketPairs = getAllPocketPairStartHands();
            allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(0).getRank());
            allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(1).getRank());
            allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(2).getRank());

            if(board.size() == 4) {
                allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(3).getRank());
            }

            if(board.size() == 5) {
                allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(3).getRank());
                allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(4).getRank());
            }

            //nog even bekijken
            for (Map.Entry<Integer, List<Card>> pocketPairEntry : allPocketPairs.entrySet()) {
                if(!boardRanks.contains(pocketPairEntry.getValue().get(0).getRank())) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), pocketPairEntry.getValue());
                }
            }

            return combosThatMakeTwoPair;

        } else if (getNumberOfPairsOnBoard(board) == 2) {
            //alle combos die niet een boat maken. Dus op 4499J ook J combos.
        }
        return combosThatMakeTwoPair;
    }
}
