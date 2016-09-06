package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by lennart on 28-8-16.
 */
public class PairEvaluator extends BoardEvaluator {

    public Map<Integer, List<Card>> getCombosThatMakePair (List<Card> board) {
        Map<Integer, List<Card>> combosThatMakePair = new HashMap<>();

        if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnlyCorrectedForBoard(allPossibleStartHands, board);

            List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
            int initialSizeBoardRanks = boardRanks.size();

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                List<Integer> copyCombo = new ArrayList<>();
                copyCombo.addAll(entry.getValue());

                List<Integer> copyBoardRanks = new ArrayList<>();
                copyBoardRanks.addAll(boardRanks);

                copyBoardRanks.removeAll(copyCombo);

                if(copyBoardRanks.size() == initialSizeBoardRanks - 1 && entry.getValue().get(0) != entry.getValue().get(1)) {
                    combosThatMakePair.put(combosThatMakePair.size(), allPossibleStartHands.get(entry.getKey()));
                }
            }

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

            for (Map.Entry<Integer, List<Card>> entry : allPocketPairs.entrySet()) {
                combosThatMakePair.put(combosThatMakePair.size(), entry.getValue());
            }

            combosThatMakePair = getSortedComboMap(combosThatMakePair);
            Map<Integer, List<Integer>> rankMap = getSortedComboMapRankOnly(combosThatMakePair);
            return combosThatMakePair;
        } else if (getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnlyCorrectedForBoard(allPossibleStartHands, board);

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

            for(Iterator<Map.Entry<Integer, List<Card>>> it = combosThatMakePair.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Integer, List<Card>> entry = it.next();
                if(entry.getValue().get(0).getRank() == entry.getValue().get(1).getRank()) {
                    it.remove();
                }
            }
            combosThatMakePair = getSortedComboMap(combosThatMakePair);
            Map<Integer, List<Integer>> rankMap = getSortedComboMapRankOnly(combosThatMakePair);
            return combosThatMakePair;
        }
        combosThatMakePair = getSortedComboMap(combosThatMakePair);
        Map<Integer, List<Integer>> rankMap = getSortedComboMapRankOnly(combosThatMakePair);
        return combosThatMakePair;
    }


    public static Comparator<List<Integer>> getPairComboComparatorRankOnly(List<Card> board) {
        return new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> combo1, List<Integer> combo2) {
                BoardEvaluator boardEvaluator = new BoardEvaluator();
                List<Integer> boardRanks = boardEvaluator.getSortedCardRanksFromCardList(board);
                Collections.sort(boardRanks, Collections.reverseOrder());

                Map<Integer, Integer> boardRanksMap = new HashMap<>();

                for(Integer i : boardRanks) {
                    boardRanksMap.put(boardRanksMap.size(), i);
                }

                //geen pair op het board
                if(boardEvaluator.getNumberOfPairsOnBoard(board) == 0) {

                    //board.size() = 3
                    if(board.size() == 3) {
                        //als beide combos geen pocket pair
                        if(combo1.get(0) != combo1.get(1) && combo2.get(0) != combo2.get(1)) {

                        }

                        //als combo1 is pocketpair en combo2 niet
                        if(combo1.get(0) == combo1.get(1) && combo2.get(0) != combo2.get(1)) {
                            //als beide kaarten van combo2 hoger zijn dan combo1, dan wint combo2
                            if(combo2.get(0) > combo1.get(0) && combo2.get(1) > combo1.get(0)) {
                                return 1;
                            }

                            //als een kaart van combo2 hoger is dan combo1, en dit de gepairde kaart is dan wint combo2
                            if(combo2.get(0) > combo1.get(0) && combo2.get(1) <= combo1.get(0))

                            //als een kaart van combo2 hoger is dan combo1, maar dit is niet de gepairde kaart, dan wint combo1

                            //als beide kaarten van combo2 lager zijn dan combo1, dan wint combo1
                            if(combo2.get(0) < combo1.get(0) && combo2.get(1) < combo1.get(0)) {
                                return -1;
                            }
                        }

                        //als combo2 is pocketpair en combo1 niet
                        if(combo1.get(0) != combo1.get(1) && combo2.get(0) == combo2.get(1)) {

                        }

                        //als beide combos pocket pair
                        if(combo1.get(0) == combo1.get(1) && combo2.get(0) == combo2.get(1)) {
                            if(combo2.get(0) > combo1.get(0)) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }



                        //bovenaan alle pocket pairs boven 1e boardcard

                        //dan alle combos die pairen met 1e boardcard

                        //dan alle pocket pairs tussen 1e boardcard en 2e boardcard

                        //dan alle combos die pairen met 2e boardcard

                        //dan alle pocket pairs tussen 2e en 3e boardcard

                        //dan alle combos die pairen met 3e boardcard
                    }

                    //board.size() = 4
                    if(board.size() == 4) {
                        //bovenaan alle pocket pairs boven 1e boardcard

                        //dan alle combos die pairen met 1e boardcard

                        //dan alle pocket pairs tussen hoogste boardcard en 2e boardcard

                        //dan alle combos die pairen met 2e boardcard

                        //dan alle pocket pairs tussen 2e en 3e boardcard

                        //dan alle combos die pairen met 3e boardcard

                        //dan alle pocket pairs tussen 3e en 4e boardcard

                        //dan alle combos die pairen met 4e boardcard
                    }

                    //board.size() = 5
                    if(board.size() == 5) {
                        //bovenaan alle pocket pairs boven 1e boardcard

                        //dan alle combos die pairen met 1e boardcard

                        //dan alle pocket pairs tussen hoogste boardcard en 2e boardcard

                        //dan alle combos die pairen met 2e boardcard

                        //dan alle pocket pairs tussen 2e en 3e boardcard

                        //dan alle combos die pairen met 3e boardcard

                        //dan alle pocket pairs tussen 3e en 4e boardcard

                        //dan alle combos die pairen met 4e boardcard

                        //dan alle pocket pairs tussen 4e en 5e boardcard

                        //dan alle combos die pairen met 5e boardcard
                    }
                }



                //wel een pair op het board

                        //de gebruikelijke sortering



                Collections.sort(combo1, Collections.reverseOrder());
                Collections.sort(combo2, Collections.reverseOrder());

                if(combo2.get(0) > combo1.get(0)) {
                    return 1;
                } else if(combo2.get(0) == combo1.get(0)) {
                    if(combo2.get(1) > combo1.get(1)) {
                        return 1;
                    } else if(combo2.get(1) == combo1.get(1)) {
                        return 0;
                    }
                }
                return -1;
            }
        };
    }



}
