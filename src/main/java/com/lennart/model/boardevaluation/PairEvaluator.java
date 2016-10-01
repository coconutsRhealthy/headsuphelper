package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by lennart on 28-8-16.
 */
public class PairEvaluator extends BoardEvaluator implements ComboComparatorRankOnly {

    public Map<Integer, Set<Set<Card>>> getCombosThatMakePair (List<Card> board) {
        Map<Integer, List<Card>> combosThatMakePair = new HashMap<>();
        Map<Integer, Set<Set<Card>>> sortedCombos;

        if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnly(allPossibleStartHands);

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

            Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(combosThatMakePair, board, new PairEvaluator());
            sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
            sortedCombos = removeDuplicateCombos(sortedCombos, board);
            return sortedCombos;
        } else if (getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnly(allPossibleStartHands);

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

            Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(combosThatMakePair, board, new PairEvaluator());
            sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
            sortedCombos = removeDuplicateCombos(sortedCombos, board);
            return sortedCombos;
        }
        combosThatMakePair = getSortedComboMap(combosThatMakePair);
        Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(combosThatMakePair, board, new PairEvaluator());
        sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
        sortedCombos = removeDuplicateCombos(sortedCombos, board);
        return sortedCombos;
    }

    @Override
    public Comparator<List<Integer>> getComboComparatorRankOnly(List<Card> board) {
        return new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> combo1, List<Integer> combo2) {
                BoardEvaluator boardEvaluator = new BoardEvaluator();
                List<Integer> boardRanks = boardEvaluator.getSortedCardRanksFromCardList(board);

                if(boardEvaluator.getNumberOfPairsOnBoard(board) == 0) {
                    int pairedCardCombo1;
                    int kickerCombo1;

                    int pairedCardCombo2;
                    int kickerCombo2;

                    if(combo1.get(0) == combo1.get(1)) {
                        pairedCardCombo1 = combo1.get(0);
                        kickerCombo1 = combo1.get(1);
                    } else if (boardRanks.contains(combo1.get(0))) {
                        pairedCardCombo1 = combo1.get(0);
                        kickerCombo1 = combo1.get(1);
                    } else {
                        pairedCardCombo1 = combo1.get(1);
                        kickerCombo1 = combo1.get(0);
                    }

                    if(combo2.get(0) == combo2.get(1)) {
                        pairedCardCombo2 = combo2.get(0);
                        kickerCombo2 = combo2.get(1);
                    } else if (boardRanks.contains(combo2.get(0))) {
                        pairedCardCombo2 = combo2.get(0);
                        kickerCombo2 = combo2.get(1);
                    } else {
                        pairedCardCombo2 = combo2.get(1);
                        kickerCombo2 = combo2.get(0);
                    }

                    if(pairedCardCombo2 > pairedCardCombo1) {
                        return 1;
                    } else if (pairedCardCombo2 == pairedCardCombo1) {
                        if(board.size() > 3) {
                            if(boardRanks.contains(pairedCardCombo1)) {
                                boardRanks.remove(Integer.valueOf(pairedCardCombo1));
                            }

                            int lowestRankedCardOnBoard = Collections.min(boardRanks);

                            if(kickerCombo2 > kickerCombo1) {
                                if(kickerCombo2 > lowestRankedCardOnBoard) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            } else if(kickerCombo2 == kickerCombo1) {
                                return 0;
                            } else {
                                if(kickerCombo1 > lowestRankedCardOnBoard) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        } else {
                            if(kickerCombo2 > kickerCombo1) {
                                return 1;
                            } else if(kickerCombo2 == kickerCombo1) {
                                return 0;
                            } else {
                                return -1;
                            }
                        }
                    } else {
                        return -1;
                    }
                } else if(boardEvaluator.getNumberOfPairsOnBoard(board) == 1) {
                    if(board.size() > 3) {
                        int rankOfPairOnBoard = getRanksOfPairsOnBoard(board).get(0);
                        boardRanks.removeAll(Collections.singleton(rankOfPairOnBoard));

                        int lowestRankedCardOnBoard = Collections.min(boardRanks);

                        Collections.sort(combo1, Collections.reverseOrder());
                        Collections.sort(combo2, Collections.reverseOrder());

                        if (combo2.get(0) > combo1.get(0)) {
                            if (combo2.get(0) > lowestRankedCardOnBoard) {
                                return 1;
                            } else {
                                if(board.size() == 4) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        } else if (combo2.get(0) == combo1.get(0)) {
                            if (combo2.get(1) > combo1.get(1)) {
                                if (combo2.get(1) > lowestRankedCardOnBoard) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            } else if (combo2.get(1) == combo1.get(1)) {
                                return 0;
                            } else {
                                if (combo1.get(1) > lowestRankedCardOnBoard) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        } else {
                            if (combo1.get(0) > lowestRankedCardOnBoard) {
                                return -1;
                            } else {
                                if(board.size() == 4) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        }
                    } else {
                        Collections.sort(combo1, Collections.reverseOrder());
                        Collections.sort(combo2, Collections.reverseOrder());

                        if(combo2.get(0) > combo1.get(0)) {
                            return 1;
                        } else if(combo2.get(0) == combo1.get(0)) {
                            if(combo2.get(1) > combo1.get(1)) {
                                return 1;
                            } else if(combo2.get(1) == combo1.get(1)) {
                                return 0;
                            } else {
                                return -1;
                            }
                        }
                        return -1;
                    }
                }
                return 0;
            }
        };
    }

    private Map<Integer, Set<Set<Card>>> removeDuplicateCombos(Map<Integer, Set<Set<Card>>> sortedCombos, List<Card> board) {
        Map<Integer, Set<Set<Card>>> twoPairCombos = new TwoPairEvaluator().getCombosThatMakeTwoPair(board);
        Map<Integer, Set<Set<Card>>> threeOfAKindCombos = new ThreeOfAKindEvaluator().getThreeOfAKindCombos(board);
        Map<Integer, Set<Set<Card>>> straightCombos = new StraightEvaluator().getMapOfStraightCombos(board);
        Map<Integer, Set<Set<Card>>> flushCombos = new FlushEvaluator().getFlushCombos(board);
        Map<Integer, Set<Set<Card>>> fullHouseCombos = new FullHouseEvaluator().getFullHouseCombos(board);
        Map<Integer, Set<Set<Card>>> fourOfAKindCombos = new FourOfAKindEvaluator().getFourOfAKindCombos(board);
        Map<Integer, Set<Set<Card>>> straightFlushCombos = new StraightFlushEvaluator().getStraightFlushCombos(board);

        sortedCombos = removeDuplicateCombosPerCategory(straightFlushCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(fourOfAKindCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(fullHouseCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(flushCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(straightCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(threeOfAKindCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(twoPairCombos, sortedCombos);

        return sortedCombos;
    }
}
