package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by lennart on 3-9-16.
 */
public class TripsEvaluator extends BoardEvaluator{

    //check welke combos three of a kind geven, gegeven een bepaald board

    public Map<Integer, List<Card>> getThreeOfAKindCombos(List<Card> board) {
        Map<Integer, List<Card>> threeOfAKindCombos = new HashMap<>();
        Map<Integer, List<Card>> allPocketPairStartHands = getAllPocketPairStartHands();
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        //als het board unpaired is
        if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            for (Map.Entry<Integer, List<Card>> entry : allPocketPairStartHands.entrySet()) {
                if(boardRanks.contains(entry.getValue().get(0).getRank())) {
                    threeOfAKindCombos.put(threeOfAKindCombos.size(), entry.getValue());
                }
            }
            threeOfAKindCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(threeOfAKindCombos, board);
            return threeOfAKindCombos;
        } else if(getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnlyCorrectedForBoard(allPossibleStartHands, board);
            Integer rankOfPairOnBoard = getRanksOfPairsOnBoard(board).get(0);

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                List<Integer> comboCopy = new ArrayList<>();
                comboCopy.addAll(entry.getValue());
                comboCopy.removeAll(Collections.singleton(rankOfPairOnBoard));
                if(comboCopy.size() == 1) {
                    if(!boardRanks.contains(comboCopy.get(0))) {
                        threeOfAKindCombos.put(threeOfAKindCombos.size(), allPossibleStartHands.get(entry.getKey()));
                    }
                }
            }
            return threeOfAKindCombos;
        } else if(boardContainsTrips(board) && !boardContainsQuads(board)) {
            //alle combos die niet met de andere kaarten op het board pairen, geen pocket pair zijn, en niet met de trips
            //op het board pairen

            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, Integer> frequencyOfRanksOnBoard = getFrequencyOfRanksOnBoard(board);
            Integer rankOfTripsOnBoard = 0;

            for (Map.Entry<Integer, Integer> entry : frequencyOfRanksOnBoard.entrySet()) {
                if(entry.getValue() == 3) {
                    rankOfTripsOnBoard = entry.getKey();
                }
            }

            //remove pocket pairs
            for(Iterator<Map.Entry<Integer, List<Card>>> it = allPossibleStartHands.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Integer, List<Card>> entry = it.next();
                if(entry.getValue().get(0).getRank() == entry.getValue().get(1).getRank()) {
                    it.remove();
                }
            }

            //remove combos die met trips op het board pairen
            for(Iterator<Map.Entry<Integer, List<Card>>> it = allPossibleStartHands.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Integer, List<Card>> entry = it.next();
                if(entry.getValue().get(0).getRank() == rankOfTripsOnBoard || entry.getValue().get(1).getRank() == rankOfTripsOnBoard) {
                    it.remove();
                }
            }

            //remove combos die met de andere kaarten op het board pairen
            List<Integer> boardRanksTripsOnBoardRemoved = new ArrayList<>();
            boardRanksTripsOnBoardRemoved.addAll(boardRanks);
            boardRanksTripsOnBoardRemoved.removeAll(Collections.singleton(rankOfTripsOnBoard));

            for(Iterator<Map.Entry<Integer, List<Card>>> it = allPossibleStartHands.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Integer, List<Card>> entry = it.next();
                if(boardRanksTripsOnBoardRemoved.size() == 1) {
                    if(entry.getValue().get(0).getRank() == boardRanksTripsOnBoardRemoved.get(0)) {
                        it.remove();
                    }
                } else if(boardRanksTripsOnBoardRemoved.size() == 2) {
                    if(entry.getValue().get(0).getRank() == boardRanksTripsOnBoardRemoved.get(0) || entry.getValue().get(0).getRank() == boardRanksTripsOnBoardRemoved.get(1)) {
                        it.remove();
                    }
                }
            }

            threeOfAKindCombos = allPossibleStartHands;
            return threeOfAKindCombos;
        }
        return threeOfAKindCombos;
    }
}

