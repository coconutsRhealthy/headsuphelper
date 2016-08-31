package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by lennart on 28-8-16.
 */
public class PairEvaluator extends BoardEvaluator {

    //returnt alle combos die één pair leveren
    public Map<Integer, List<Card>> getCombosThatMakePair (List<Card> board) {
        Map<Integer, List<Card>> combosThatMakePair = new HashMap<>();
        Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
        allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);

        Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = new HashMap<>();
        for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
            allPossibleStartHandsRankOnly.put(entry.getKey(), new ArrayList<>());
            allPossibleStartHandsRankOnly.get(entry.getKey()).add(entry.getValue().get(0).getRank());
            allPossibleStartHandsRankOnly.get(entry.getKey()).add(entry.getValue().get(1).getRank());
        }


        //als het board unpaired is, dan...

        if(getNumberOfPairsOnBoard(board) == 0) {
            //je wil checken dat van jouw lijstje slechts een value overeenkomt met de boardkaart
            for(Card c : board) {
                for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                    if(Collections.frequency(entry.getValue(), c.getRank()) == 1) {
                        combosThatMakePair.put(combosThatMakePair.size(), allPossibleStartHands.get(entry.getKey()));
                    }
                }
            }

            //voeg ook nog de pocket pairs toe
            Map<Integer, List<Card>> allPocketPairs = getAllPocketPairStartHands();
            //allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPocketPairs, board);

            allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(0).getRank());
            allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(1).getRank());
            allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(2).getRank());

            for (Map.Entry<Integer, List<Card>> entry : allPocketPairs.entrySet()) {
                combosThatMakePair.put(combosThatMakePair.size(), entry.getValue());
            }

            return combosThatMakePair;
        }

        //als het board een pair heeft, dan...
        //leveren alle combos die verder niet pairen met het board of trips maken, een pair..
        if(getNumberOfPairsOnBoard(board) == 1) {
            int counter = 0;
            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                for(Card c : board) {
                    if (Collections.frequency(entry.getValue(), c.getRank()) == 0) {
                        counter++;
                    }
                    if (counter == board.size()) {
                        combosThatMakePair.put(combosThatMakePair.size(), allPossibleStartHands.get(entry.getKey()));
                    }
                }
                counter = 0;
            }

            //remove nu nog alle pocketpairs
            System.out.println("eije");
            for(Iterator<Map.Entry<Integer, List<Card>>> it = combosThatMakePair.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Integer, List<Card>> entry = it.next();
                if(entry.getValue().get(0).getRank() == entry.getValue().get(1).getRank()) {
                    it.remove();
                }
            }
            return combosThatMakePair;
        }

        //als het board twee pair heeft, dan...

        //als het board trips heeft, dan...

        //etc
        return null;
    }

    //helper methods
    public Map<Integer, List<Card>> clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank (Map<Integer, List<Card>> startHandMap, int rank) {
        for(Iterator<Map.Entry<Integer, List<Card>>> it = startHandMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, List<Card>> entry = it.next();
            if(entry.getValue().get(0).getRank() == rank || entry.getValue().get(1).getRank() == rank) {
                it.remove();
            }
        }
        return startHandMap;
    }
}
