package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by LPO10346 on 9/27/2016.
 */
public class FullHouseEvaluator extends BoardEvaluator implements ComboComparator {

    public Map<Integer, Set<Set<Card>>> getFullHouseCombos(List<Card> board) {
        Map<Integer, List<Card>> comboMap = new HashMap<>();
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
        Map<Integer, List<Card>> allPocketPairStartHands = getAllPocketPairStartHands();

        //een pair op board
        if(getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board) && !boardContainsQuads(board)) {

            //verwijder het boardpair
            int rankOfPairOnBoard = getRanksOfPairsOnBoard(board).get(0);
            boardRanks.removeAll(Collections.singleton(rankOfPairOnBoard));

            //ook nog, alle combos die trips maken met pair on board én pairen met een van de andere kaarten op het board
            for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
                List<Integer> comboRankOnly = getSortedCardRanksFromCardList(entry.getValue());

                if(comboRankOnly.contains(rankOfPairOnBoard)) {
                    comboRankOnly.removeAll(Collections.singleton(rankOfPairOnBoard));
                    if(comboRankOnly.size() == 1) {
                        if(boardRanks.contains(comboRankOnly.get(0))) {
                            comboMap.put(comboMap.size(), entry.getValue());
                        }
                    }
                }
            }

            //get de set combos van de andere kaarten
            for (Map.Entry<Integer, List<Card>> entry : allPocketPairStartHands.entrySet()) {
                if(boardRanks.contains(entry.getValue().get(0).getRank())) {
                    comboMap.put(comboMap.size(), entry.getValue());
                }
            }
            comboMap = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(comboMap, board);
            return getSortedCardComboMap(comboMap, board, new FullHouseEvaluator());
        }

        //twee pair op board
        if(getNumberOfPairsOnBoard(board) == 2 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            //alle combos die pairen met een van de twee boardpairs
            List<Integer> rankOfPairsOnBoard = getRanksOfPairsOnBoard(board);
            int rankOfBoardPair1 = rankOfPairsOnBoard.get(0);
            int rankOfBoardPair2 = rankOfPairsOnBoard.get(1);

            for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
                List<Integer> comboRankOnly = getSortedCardRanksFromCardList(entry.getValue());

                if(comboRankOnly.contains(Integer.valueOf(rankOfBoardPair1)) && comboRankOnly.get(0) != comboRankOnly.get(1)) {
                    comboMap.put(comboMap.size(), entry.getValue());
                    continue;
                }
                if(comboRankOnly.contains(Integer.valueOf(rankOfBoardPair2)) && comboRankOnly.get(0) != comboRankOnly.get(1)) {
                    comboMap.put(comboMap.size(), entry.getValue());
                }
            }

            //als er 5 kaarten liggen dan ook de combos die set maken met de 5e kaart
            if(board.size() == 5) {
                boardRanks.removeAll(Collections.singleton(rankOfBoardPair1));
                boardRanks.removeAll(Collections.singleton(rankOfBoardPair2));

                for (Map.Entry<Integer, List<Card>> entry : allPocketPairStartHands.entrySet()) {
                    if(boardRanks.contains(entry.getValue().get(0).getRank())) {
                        comboMap.put(comboMap.size(), entry.getValue());
                    }
                }
            }
            comboMap = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(comboMap, board);
            return getSortedCardComboMap(comboMap, board, new FullHouseEvaluator());
        }

        //trips op board
        if(getNumberOfPairsOnBoard(board) == 0 && boardContainsTrips(board) && !boardContainsBoat(board) && !boardContainsQuads(board)) {
            //alle combos die pairen of set maken met een van de twee andere boardkaarten
            Map<Integer, Integer> frequencyOfRanksOnBoard = getFrequencyOfRanksOnBoard(board);

            int rankOfTripsOnBoard = 0;

            for (Map.Entry<Integer, Integer> entry : frequencyOfRanksOnBoard.entrySet()) {
                if(entry.getValue() == 3) {
                    rankOfTripsOnBoard = entry.getKey();
                }
            }

            boardRanks.removeAll(Collections.singleton(rankOfTripsOnBoard));

            for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
                List<Integer> comboRankOnly = getSortedCardRanksFromCardList(entry.getValue());

                if(boardRanks.contains(comboRankOnly.get(0)) && !comboRankOnly.contains(rankOfTripsOnBoard)) {
                    comboMap.put(comboMap.size(), entry.getValue());
                    continue;
                }
                if(boardRanks.contains(comboRankOnly.get(1)) && !comboRankOnly.contains(rankOfTripsOnBoard)) {
                    comboMap.put(comboMap.size(), entry.getValue());
                }
            }

            //alle pocketpairs die niet met het board matchen
            for (Map.Entry<Integer, List<Card>> entry : allPocketPairStartHands.entrySet()) {
                if(!boardRanks.contains(entry.getValue().get(0).getRank())) {
                    comboMap.put(comboMap.size(), entry.getValue());
                }
            }
            comboMap = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(comboMap, board);
            return getSortedCardComboMap(comboMap, board, new FullHouseEvaluator());
        }

        //boat op board
        if(boardContainsBoat(board)) {
            //alle combos die niet met het board matchen, hierin zitten ook alle pocket pairs
            for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
                if(!boardRanks.contains(entry.getValue().get(0).getRank()) &&
                        !boardRanks.contains(entry.getValue().get(1).getRank())) {
                    comboMap.put(comboMap.size(), entry.getValue());
                }
            }

            //als het pair hoger dan de trips op board is, dan alle combos die trips maken met het pair
            Map<Integer, Integer> frequencyOfRanksOnBoard = getFrequencyOfRanksOnBoard(board);
            int rankOfTripsOnBoard = 0;

            for (Map.Entry<Integer, Integer> entry : frequencyOfRanksOnBoard.entrySet()) {
                if(entry.getValue() == 3) {
                    rankOfTripsOnBoard = entry.getKey();
                }
            }

            boardRanks.removeAll(Collections.singleton(rankOfTripsOnBoard));
            int rankOfPairOnBoard = boardRanks.get(0);

            if(rankOfPairOnBoard > rankOfTripsOnBoard) {
                for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
                    List<Integer> comboRankOnly = getSortedCardRanksFromCardList(entry.getValue());
                    if(comboRankOnly.contains(rankOfPairOnBoard) && !comboRankOnly.contains(rankOfTripsOnBoard) &&
                            comboRankOnly.get(0) != comboRankOnly.get(1)) {
                        comboMap.put(comboMap.size(), entry.getValue());
                    }

                }
            }
            comboMap = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(comboMap, board);
            return getSortedCardComboMap(comboMap, board, new FullHouseEvaluator());
        }
        return new HashMap<>();
    }

    @Override
    public Comparator<Set<Card>> getComboComparator(List<Card> board) {
        return new Comparator<Set<Card>>() {
            @Override
            public int compare(Set<Card> xCombo1, Set<Card> xCombo2) {
                List<Card> combo1C = new ArrayList<>();
                List<Card> combo2C = new ArrayList<>();

                combo1C.addAll(xCombo1);
                combo2C.addAll(xCombo2);

                List<Integer> combo1 = getSortedCardRanksFromCardList(combo1C);
                List<Integer> combo2 = getSortedCardRanksFromCardList(combo2C);

                //als één pair op het board
                if(getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
                    //6 6 8 K J
                        //voeg beide combos toe aan het board. Check welk getal trips is van beide combos.
                            //als trips van combo2 > combo1

                            //als trips van combo2 == combo1

                            //als trips van combo1 > combo2

                }

                //als twee pair op het board
                if(getNumberOfPairsOnBoard(board) == 2 && !boardContainsTrips(board) && !boardContainsQuads(board)) {


                }

                //als threeOfAKind op het board
                if(getNumberOfPairsOnBoard(board) == 0 && boardContainsTrips(board) && !boardContainsBoat(board) && !boardContainsQuads(board)) {


                }

                //als boat op het board
                if(boardContainsBoat(board)) {


                }

                return 1;
            }
        };
    }
}
