package com.lennart.model.boardevaluation;

import com.lennart.model.card.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.*;

/**
 * Created by lennart on 28-8-16.
 */
public class PairEvaluator extends BoardEvaluator implements ComboComparatorRankOnly {

    private Map<Integer, Set<Set<Card>>> combosThatMakePair;
    private TwoPairEvaluator twoPairEvaluator;
    private ThreeOfAKindEvaluator threeOfAKindEvaluator;
    private StraightEvaluator straightEvaluator;
    private FlushEvaluator flushEvaluator;
    private FullHouseEvaluator fullHouseEvaluator;
    private FourOfAKindEvaluator fourOfAKindEvaluator;
    private StraightFlushEvaluator straightFlushEvaluator;

    public PairEvaluator(List<Card> board, TwoPairEvaluator twoPairEvaluator, ThreeOfAKindEvaluator threeOfAKindEvaluator,
                         StraightEvaluator straightEvaluator, FlushEvaluator flushEvaluator,
                         FullHouseEvaluator fullHouseEvaluator, FourOfAKindEvaluator fourOfAKindEvaluator,
                         StraightFlushEvaluator straightFlushEvaluator) {
        this.twoPairEvaluator = twoPairEvaluator;
        this.threeOfAKindEvaluator = threeOfAKindEvaluator;
        this.straightEvaluator = straightEvaluator;
        this.flushEvaluator = flushEvaluator;
        this.fullHouseEvaluator = fullHouseEvaluator;
        this.fourOfAKindEvaluator = fourOfAKindEvaluator;
        this.straightFlushEvaluator = straightFlushEvaluator;
        getCombosThatMakePairInitialize(board);
    }

    public Map<Integer, Set<Set<Card>>> getCombosThatMakePair() {
        return combosThatMakePair;
    }

    private void getCombosThatMakePairInitialize (List<Card> board) {
        Map<Integer, List<Card>> combosThatMakePair = new HashMap<>();
        Map<Integer, Set<Set<Card>>> sortedCombos;

        if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = PreflopRangeBuilderUtil.getAllPossibleStartHandsAsList();
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

            Map<Integer, List<Card>> allPocketPairs = PreflopRangeBuilderUtil.getAllPocketPairStartHands();
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

            Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(combosThatMakePair, board, this);
            sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
            sortedCombos = removeDuplicateCombos(sortedCombos);

            this.combosThatMakePair = sortedCombos;
            return;
        } else if (getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = PreflopRangeBuilderUtil.getAllPossibleStartHandsAsList();
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

            Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(combosThatMakePair, board, this);
            sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
            sortedCombos = removeDuplicateCombos(sortedCombos);

            this.combosThatMakePair = sortedCombos;
            return;
        }
        combosThatMakePair = getSortedComboMap(combosThatMakePair);
        Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(combosThatMakePair, board, this);
        sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
        sortedCombos = removeDuplicateCombos(sortedCombos);

        this.combosThatMakePair = sortedCombos;
    }

    @Override
    public Comparator<List<Integer>> getComboComparatorRankOnly(List<Card> board) {
        return new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> combo1, List<Integer> combo2) {
                List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

                List<Integer> boardPlusCombo1 = new ArrayList<>();
                boardPlusCombo1.addAll(boardRanks);
                boardPlusCombo1.addAll(combo1);

                List<Integer> boardPlusCombo2 = new ArrayList<>();
                boardPlusCombo2.addAll(boardRanks);
                boardPlusCombo2.addAll(combo2);

                Collections.sort(boardPlusCombo1, Collections.reverseOrder());
                Collections.sort(boardPlusCombo2, Collections.reverseOrder());

                int pairRankCombo1 = 0;
                int pairRankCombo2 = 0;

                for(int i = 0; i < boardPlusCombo1.size() - 1; i++) {
                    if(boardPlusCombo1.get(i).equals(boardPlusCombo1.get(i + 1))) {
                        pairRankCombo1 = boardPlusCombo1.get(i);
                        break;
                    }
                }

                for(int i = 0; i < boardPlusCombo2.size() - 1; i++) {
                    if(boardPlusCombo2.get(i).equals(boardPlusCombo2.get(i + 1))) {
                        pairRankCombo2 = boardPlusCombo2.get(i);
                        break;
                    }
                }

                boardPlusCombo1.removeAll(Collections.singleton(pairRankCombo1));
                boardPlusCombo2.removeAll(Collections.singleton(pairRankCombo2));

                List<Integer> highestThreeCardsNoPairCombo1;
                List<Integer> highestThreeCardsNoPairCombo2;

                if(boardPlusCombo1.size() > 3) {
                    highestThreeCardsNoPairCombo1 = boardPlusCombo1.subList(0, 3);
                    highestThreeCardsNoPairCombo2 = boardPlusCombo2.subList(0, 3);
                } else {
                    highestThreeCardsNoPairCombo1 = boardPlusCombo1;
                    highestThreeCardsNoPairCombo2 = boardPlusCombo2;
                }

                if(pairRankCombo2 > pairRankCombo1) {
                    return 1;
                } else if(pairRankCombo2 == pairRankCombo1) {
                    if(highestThreeCardsNoPairCombo2.get(0) > highestThreeCardsNoPairCombo1.get(0)) {
                        return 1;
                    } else if(highestThreeCardsNoPairCombo2.get(0) == highestThreeCardsNoPairCombo1.get(0)) {
                        if(highestThreeCardsNoPairCombo2.get(1) > highestThreeCardsNoPairCombo1.get(1)) {
                            return 1;
                        } else if(highestThreeCardsNoPairCombo2.get(1) == highestThreeCardsNoPairCombo1.get(1)) {
                            if(highestThreeCardsNoPairCombo2.get(2) > highestThreeCardsNoPairCombo1.get(2)) {
                                return 1;
                            } else if(highestThreeCardsNoPairCombo2.get(2) == highestThreeCardsNoPairCombo1.get(2)) {
                                return 0;
                            } else {
                                return -1;
                            }
                        } else {
                            return -1;
                        }
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
            }
        };
    }

    private Map<Integer, Set<Set<Card>>> removeDuplicateCombos(Map<Integer, Set<Set<Card>>> sortedCombos) {
        Map<Integer, Set<Set<Card>>> twoPairCombos = twoPairEvaluator.getCombosThatMakeTwoPair();
        Map<Integer, Set<Set<Card>>> threeOfAKindCombos = threeOfAKindEvaluator.getThreeOfAKindCombos();
        Map<Integer, Set<Set<Card>>> straightCombos = straightEvaluator.getMapOfStraightCombos();
        Map<Integer, Set<Set<Card>>> flushCombos = flushEvaluator.getFlushCombos();
        Map<Integer, Set<Set<Card>>> fullHouseCombos = fullHouseEvaluator.getFullHouseCombos();
        Map<Integer, Set<Set<Card>>> fourOfAKindCombos = fourOfAKindEvaluator.getFourOfAKindCombos();
        Map<Integer, Set<Set<Card>>> straightFlushCombos = straightFlushEvaluator.getStraightFlushCombos();

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
