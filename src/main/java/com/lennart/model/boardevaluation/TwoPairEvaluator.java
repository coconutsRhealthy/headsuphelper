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

            Integer rankOfPairOnBoard = getRanksOfPairsOnBoard(board).get(0);
            boardRanks.removeAll(Collections.singleton(rankOfPairOnBoard));
            int initialSizeBoardRanks = boardRanks.size();

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                List<Integer> copyCombo = new ArrayList<>();
                copyCombo.addAll(entry.getValue());

                List<Integer> copyBoardRanks = new ArrayList<>();
                copyBoardRanks.addAll(boardRanks);

                copyBoardRanks.removeAll(copyCombo);

                if(copyBoardRanks.size() == initialSizeBoardRanks - 1 && !entry.getValue().contains(rankOfPairOnBoard)) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                }
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

            boardRanks = getSortedCardRanksFromCardList(board);

            for (Map.Entry<Integer, List<Card>> pocketPairEntry : allPocketPairs.entrySet()) {
                if(!boardRanks.contains(pocketPairEntry.getValue().get(0).getRank())) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), pocketPairEntry.getValue());
                }
            }
            return combosThatMakeTwoPair;
        } else if (getNumberOfPairsOnBoard(board) == 2) {
            //alle combos die niet een boat maken. Dus op 4499J ook J combos.
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnlyCorrectedForBoard(allPossibleStartHands, board);


            //get card die niet met andere boardCards pairt, indien aanwezig
            Integer rankOfUnpairedBoardCard = 0;
            List<Integer> ranksOfPairsOnBoard = getRanksOfPairsOnBoard(board);
            boardRanks.removeAll(ranksOfPairsOnBoard);
            if(boardRanks.size() == 1) {
                rankOfUnpairedBoardCard = boardRanks.get(0);
            }

            if(rankOfUnpairedBoardCard > ranksOfPairsOnBoard.get(0) || rankOfUnpairedBoardCard > ranksOfPairsOnBoard.get(1)) {
                for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                    List<Integer> comboCopy = new ArrayList<>();
                    comboCopy.addAll(entry.getValue());
                    comboCopy.removeAll(Collections.singleton(rankOfUnpairedBoardCard));

                    if(comboCopy.size() == 1 && !ranksOfPairsOnBoard.contains(comboCopy.get(0))) {
                        combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                    }
                }
            }

            //voeg alle pocket pairs toe hoger dan het laagste pair on board
            //get laagste boardpair kaart
            int rankOfLowestPairOnBoard = Collections.min(ranksOfPairsOnBoard);

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


            for(Iterator<Map.Entry<Integer, List<Card>>> it = allPocketPairs.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Integer, List<Card>> entry = it.next();
                if(entry.getValue().get(0).getRank() > rankOfLowestPairOnBoard) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), entry.getValue());
                }
            }

            //voeg alle combos toe die niet pairen en geen boat maken
            boardRanks = getSortedCardRanksFromCardList(board);

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                if(Collections.disjoint(entry.getValue(), boardRanks) && entry.getValue().get(0) != entry.getValue().get(1)) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                }
            }
            return combosThatMakeTwoPair;
        }
        return combosThatMakeTwoPair;
    }
}
