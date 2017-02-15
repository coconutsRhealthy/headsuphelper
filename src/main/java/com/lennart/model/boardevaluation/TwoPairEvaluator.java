package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.*;

/**
 * Created by LPO10346 on 9/1/2016.
 */
public class TwoPairEvaluator extends BoardEvaluator implements ComboComparatorRankOnly {

    private Map<Integer, Set<Set<Card>>> combosThatMakeTwoPair;
    private ThreeOfAKindEvaluator threeOfAKindEvaluator;
    private StraightEvaluator straightEvaluator;
    private FlushEvaluator flushEvaluator;
    private FullHouseEvaluator fullHouseEvaluator;
    private FourOfAKindEvaluator fourOfAKindEvaluator;
    private StraightFlushEvaluator straightFlushEvaluator;

    public TwoPairEvaluator(List<Card> board, ThreeOfAKindEvaluator threeOfAKindEvaluator,
                            StraightEvaluator straightEvaluator, FlushEvaluator flushEvaluator,
                            FullHouseEvaluator fullHouseEvaluator, FourOfAKindEvaluator fourOfAKindEvaluator,
                            StraightFlushEvaluator straightFlushEvaluator) {
        this.threeOfAKindEvaluator = threeOfAKindEvaluator;
        this.straightEvaluator = straightEvaluator;
        this.flushEvaluator = flushEvaluator;
        this.fullHouseEvaluator = fullHouseEvaluator;
        this.fourOfAKindEvaluator = fourOfAKindEvaluator;
        this.straightFlushEvaluator = straightFlushEvaluator;
        getCombosThatMakeTwoPairInitialize(board);
    }

    public Map<Integer, Set<Set<Card>>> getCombosThatMakeTwoPair() {
        return combosThatMakeTwoPair;
    }

    private void getCombosThatMakeTwoPairInitialize(List<Card> board) {
        Map<Integer, List<Card>> combosThatMakeTwoPair = new HashMap<>();
        Map<Integer, Set<Set<Card>>> sortedCombos;
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = PreflopRangeBuilderUtil.getAllPossibleStartHandsAsList();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnly(allPossibleStartHands);

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

            Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(combosThatMakeTwoPair, board, this);
            sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
            sortedCombos = removeDuplicateCombos(sortedCombos);

            this.combosThatMakeTwoPair = sortedCombos;
            return;
        } else if (getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = PreflopRangeBuilderUtil.getAllPossibleStartHandsAsList();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnly(allPossibleStartHands);

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

                if((copyBoardRanks.size() == initialSizeBoardRanks - 1 || copyBoardRanks.size() == initialSizeBoardRanks - 2)
                        && !entry.getValue().contains(rankOfPairOnBoard) && entry.getValue().get(0) != entry.getValue().get(1)) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                }
            }

            //voeg pocket pairs toe
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

            boardRanks = getSortedCardRanksFromCardList(board);

            for (Map.Entry<Integer, List<Card>> pocketPairEntry : allPocketPairs.entrySet()) {
                if(!boardRanks.contains(pocketPairEntry.getValue().get(0).getRank())) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), pocketPairEntry.getValue());
                }
            }

            Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(combosThatMakeTwoPair, board, this);
            sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
            sortedCombos = removeDuplicateCombos(sortedCombos);

            this.combosThatMakeTwoPair = sortedCombos;
            return;
        } else if (getNumberOfPairsOnBoard(board) == 2) {
            //alle combos die niet een boat maken. Dus op 4499J ook J combos.
            Map<Integer, List<Card>> allPossibleStartHands = PreflopRangeBuilderUtil.getAllPossibleStartHandsAsList();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnly(allPossibleStartHands);

            //get card die niet met andere boardCards pairt, indien aanwezig
            Integer rankOfUnpairedBoardCard = 0;
            List<Integer> ranksOfPairsOnBoard = getRanksOfPairsOnBoard(board);
            boardRanks.removeAll(ranksOfPairsOnBoard);
            if(boardRanks.size() == 1) {
                rankOfUnpairedBoardCard = boardRanks.get(0);
            }

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                List<Integer> comboCopy = new ArrayList<>();
                comboCopy.addAll(entry.getValue());
                comboCopy.removeAll(Collections.singleton(rankOfUnpairedBoardCard));

                if(comboCopy.size() == 1 && !ranksOfPairsOnBoard.contains(comboCopy.get(0))) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                }
            }

            //voeg alle pocket pairs toe hoger dan het laagste pair on board
            //get laagste boardpair kaart
            int rankOfLowestPairOnBoard = Collections.min(ranksOfPairsOnBoard);

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


            for(Iterator<Map.Entry<Integer, List<Card>>> it = allPocketPairs.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Integer, List<Card>> entry = it.next();
//                if(entry.getValue().get(0).getRank() > rankOfLowestPairOnBoard) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), entry.getValue());
//                }
            }

            //voeg alle combos toe die niet pairen en geen boat maken
            boardRanks = getSortedCardRanksFromCardList(board);

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                if(Collections.disjoint(entry.getValue(), boardRanks) && entry.getValue().get(0) != entry.getValue().get(1)) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                }
            }
            Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(combosThatMakeTwoPair, board, this);
            sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
            sortedCombos = removeDuplicateCombos(sortedCombos);

            this.combosThatMakeTwoPair = sortedCombos;
            return;
        }
        sortedCombos = removeDuplicateCombos(new HashMap<>());

        this.combosThatMakeTwoPair = sortedCombos;
    }

    @Override
    public Comparator<List<Integer>> getComboComparatorRankOnly(List<Card> board) {
        return new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> combo1, List<Integer> combo2) {
                List<Integer> combo1highestPairsAndKicker = getHighestPairsAndKicker(board, combo1);
                List<Integer> combo2highestPairsAndKicker = getHighestPairsAndKicker(board, combo2);

                int combo1HighestPair = getHighestPairValue(combo1highestPairsAndKicker);
                int combo1SecondHighestPair = getSecondHighestPairValue(combo1highestPairsAndKicker);
                int combo1Kicker = getKicker(combo1highestPairsAndKicker, combo1HighestPair, combo1SecondHighestPair);

                int combo2HighestPair = getHighestPairValue(combo2highestPairsAndKicker);
                int combo2SecondHighestPair = getSecondHighestPairValue(combo2highestPairsAndKicker);
                int combo2Kicker = getKicker(combo2highestPairsAndKicker, combo2HighestPair, combo2SecondHighestPair);

                if(combo2HighestPair > combo1HighestPair) {
                    return 1;
                } else if(combo2HighestPair == combo1HighestPair) {
                    if(combo2SecondHighestPair > combo1SecondHighestPair) {
                        return 1;
                    } else if(combo2SecondHighestPair == combo1SecondHighestPair) {
                        if(combo2Kicker > combo1Kicker) {
                            return 1;
                        } else if(combo2Kicker == combo1Kicker) {
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
            }
        };
    }

    private List<Integer> getHighestPairsAndKicker(List<Card> board, List<Integer> combo) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        List<Integer> comboAndBoardCombined = new ArrayList<>();
        comboAndBoardCombined.addAll(boardRanks);
        comboAndBoardCombined.addAll(combo);
        Collections.sort(comboAndBoardCombined, Collections.reverseOrder());

        if(comboAndBoardCombined.size() > 5) {
            List<Integer> highestPairsAndKicker = new ArrayList<>();

            int pairRank1 = 0;
            int pairRank2 = 0;
            int pairCounter = 0;
            for(int i = 0; i < comboAndBoardCombined.size() - 1; i++) {
                if(comboAndBoardCombined.get(i).equals(comboAndBoardCombined.get(i + 1))) {
                    if(pairCounter < 2) {
                        if(pairRank1 == 0) {
                            pairRank1 = comboAndBoardCombined.get(i);
                        } else {
                            pairRank2 = comboAndBoardCombined.get(i);
                        }
                        highestPairsAndKicker.add(comboAndBoardCombined.get(i));
                        highestPairsAndKicker.add(comboAndBoardCombined.get(i + 1));
                        pairCounter++;
                    }
                }
            }

            comboAndBoardCombined.removeAll(Collections.singleton(pairRank1));
            comboAndBoardCombined.removeAll(Collections.singleton(pairRank2));

            Collections.sort(comboAndBoardCombined, Collections.reverseOrder());
            highestPairsAndKicker.add(comboAndBoardCombined.get(0));

            Collections.sort(highestPairsAndKicker, Collections.reverseOrder());
            return highestPairsAndKicker;
        }
        return comboAndBoardCombined;
    }

    private int getHighestPairValue(List<Integer> highestPairsAndKickers) {
        int highestPairValue = 0;

        for(int i = 0; i < highestPairsAndKickers.size() - 1; i++) {
            if(highestPairsAndKickers.get(i).equals(highestPairsAndKickers.get(i + 1))) {
                highestPairValue = highestPairsAndKickers.get(i);
                break;
            }
        }
        return highestPairValue;
    }

    private int getSecondHighestPairValue(List<Integer> highestPairsAndKickers) {
        int secondHighestPairValue = 0;
        int pairCounter = 0;

        for(int i = 0; i < highestPairsAndKickers.size() - 1; i++) {
            if(highestPairsAndKickers.get(i).equals(highestPairsAndKickers.get(i + 1))) {
                if(pairCounter > 0) {
                    secondHighestPairValue = highestPairsAndKickers.get(i);
                    break;
                }
                pairCounter++;
            }
        }
        return secondHighestPairValue;
    }

    private int getKicker(List<Integer> highestPairsAndKickers, int highestPairValue, int secondHighestPairValue) {
        highestPairsAndKickers.removeAll(Collections.singleton(highestPairValue));
        highestPairsAndKickers.removeAll(Collections.singleton(secondHighestPairValue));
        Collections.sort(highestPairsAndKickers, Collections.reverseOrder());
        return highestPairsAndKickers.get(0);
    }

    private Map<Integer, Set<Set<Card>>> removeDuplicateCombos(Map<Integer, Set<Set<Card>>> sortedCombos) {
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

        return sortedCombos;
    }
}
