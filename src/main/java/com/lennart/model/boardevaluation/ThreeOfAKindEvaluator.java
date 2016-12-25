package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by lennart on 3-9-16.
 */
public class ThreeOfAKindEvaluator extends BoardEvaluator implements ComboComparatorRankOnly {

    private Map<Integer, Set<Set<Card>>> combosThatMakeThreeOfAKind;
    private StraightEvaluator straightEvaluator;
    private FlushEvaluator flushEvaluator;
    private FullHouseEvaluator fullHouseEvaluator;
    private FourOfAKindEvaluator fourOfAKindEvaluator;
    private StraightFlushEvaluator straightFlushEvaluator;

    public ThreeOfAKindEvaluator(List<Card> board, StraightEvaluator straightEvaluator, FlushEvaluator flushEvaluator,
                                 FullHouseEvaluator fullHouseEvaluator, FourOfAKindEvaluator fourOfAKindEvaluator,
                                 StraightFlushEvaluator straightFlushEvaluator) {
        this.straightEvaluator = straightEvaluator;
        this.flushEvaluator = flushEvaluator;
        this.fullHouseEvaluator = fullHouseEvaluator;
        this.fourOfAKindEvaluator = fourOfAKindEvaluator;
        this.straightFlushEvaluator = straightFlushEvaluator;
        getThreeOfAKindCombosInitialize(board);
    }

    public Map<Integer, Set<Set<Card>>> getThreeOfAKindCombos() {
        return combosThatMakeThreeOfAKind;
    }

    private void getThreeOfAKindCombosInitialize(List<Card> board) {
        Map<Integer, List<Card>> threeOfAKindCombos = new HashMap<>();
        Map<Integer, List<Card>> allPocketPairStartHands = getAllPocketPairStartHands();
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        Map<Integer, Set<Set<Card>>> sortedCombos;

        //als het board unpaired is
        if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            for (Map.Entry<Integer, List<Card>> entry : allPocketPairStartHands.entrySet()) {
                if(boardRanks.contains(entry.getValue().get(0).getRank())) {
                    threeOfAKindCombos.put(threeOfAKindCombos.size(), entry.getValue());
                }
            }
            threeOfAKindCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(threeOfAKindCombos, board);
            Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(threeOfAKindCombos, board, this);
            sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
            sortedCombos = removeDuplicateCombos(sortedCombos);

            combosThatMakeThreeOfAKind = sortedCombos;
            return;
        } else if(getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHandsNew();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnly(allPossibleStartHands);
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
            Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(threeOfAKindCombos, board, this);
            sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
            sortedCombos = removeDuplicateCombos(sortedCombos);

            combosThatMakeThreeOfAKind = sortedCombos;
            return;
        } else if(boardContainsTrips(board) && !boardContainsQuads(board)) {
            //alle combos die niet met de andere kaarten op het board pairen, geen pocket pair zijn, en niet met de trips
            //op het board pairen

            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHandsNew();
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
                    if(boardRanksTripsOnBoardRemoved.contains(entry.getValue().get(0).getRank()) ||
                            boardRanksTripsOnBoardRemoved.contains(entry.getValue().get(1).getRank())) {
                        it.remove();
                    }
                } else if(boardRanksTripsOnBoardRemoved.size() == 2) {
                    if(boardRanksTripsOnBoardRemoved.contains(entry.getValue().get(0).getRank()) ||
                            boardRanksTripsOnBoardRemoved.contains(entry.getValue().get(1).getRank())) {
                        it.remove();
                    }
                }
            }

            threeOfAKindCombos = allPossibleStartHands;
            Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(threeOfAKindCombos, board, this);
            sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
            sortedCombos = removeDuplicateCombos(sortedCombos);

            combosThatMakeThreeOfAKind = sortedCombos;
            return;
        }
        sortedCombos = removeDuplicateCombos(new HashMap<>());

        combosThatMakeThreeOfAKind = sortedCombos;
    }

    @Override
    public Comparator<List<Integer>> getComboComparatorRankOnly(List<Card> board) {
        return new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> combo1, List<Integer> combo2) {
                if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
                    return compareCombosUnpairedBoard(combo1, combo2, board);
                } else if(getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
                    return compareCombosOnePairOnBoard(combo1, combo2, board);
                } else if(getNumberOfPairsOnBoard(board) == 0 && boardContainsTrips(board) && !boardContainsQuads(board)) {
                    return compareCombosThreeOfAKindOnBoard(combo1, combo2, board);
                } else {
                    System.out.println("should never come here, getComboComparatorRankOnly() -> ThreeOfAKindEvaluator");
                    return 0;
                }
            }
        };
    }

    private int compareCombosUnpairedBoard(List<Integer> combo1, List<Integer> combo2, List<Card> board) {
        if(combo2.get(0) > combo1.get(0)) {
            return 1;
        } else if(combo2.get(0) == combo1.get(0)) {
            return 0;
        } else {
            return -1;
        }
    }

    private int compareCombosOnePairOnBoard(List<Integer> combo1, List<Integer> combo2, List<Card> board) {
        int kickerCardCombo1;
        int kickerCardCombo2;

        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        if(!boardRanks.contains(combo1.get(0))) {
            kickerCardCombo1 = combo1.get(0);
        } else {
            kickerCardCombo1 = combo1.get(1);
        }

        if(!boardRanks.contains(combo2.get(0))) {
            kickerCardCombo2 = combo2.get(0);
        } else {
            kickerCardCombo2 = combo2.get(1);
        }

        if(board.size() == 3) {
            if(kickerCardCombo2 > kickerCardCombo1) {
                return 1;
            } else if(kickerCardCombo2 == kickerCardCombo1) {
                return 0;
            } else {
                return -1;
            }
        } else {
            int rankOfPairOnBoard = getRanksOfPairsOnBoard(board).get(0);
            boardRanks.removeAll(Collections.singleton(rankOfPairOnBoard));

            int boardKickerCard;

            if(board.size() == 4) {
                boardKickerCard = Collections.min(boardRanks);
            } else {
                boardRanks.remove(Integer.valueOf(Collections.min(boardRanks)));
                boardKickerCard = Collections.min(boardRanks);
            }

            if(kickerCardCombo2 > kickerCardCombo1) {
                if(kickerCardCombo2 > boardKickerCard) {
                    return 1;
                } else if (kickerCardCombo2 == boardKickerCard) {
                    System.out.println("should never come here, compareCombosOnePairOnBoard, ThreeOfAKindEvaluator");
                    return 0;
                } else {
                    return 0;
                }
            } else if(kickerCardCombo2 == kickerCardCombo1) {
                return 0;
            } else {
                if(kickerCardCombo1 > boardKickerCard) {
                    return -1;
                } else if (kickerCardCombo1 == boardKickerCard) {
                    System.out.println("should never come here, compareCombosOnePairOnBoard, ThreeOfAKindEvaluator");
                    return 0;
                } else {
                    return 0;
                }
            }
        }
    }

    private int compareCombosThreeOfAKindOnBoard(List<Integer> combo1, List<Integer> combo2, List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        Map<Integer, Integer> frequencyOfRanksOnBoard = getFrequencyOfRanksOnBoard(board);
        Integer rankOfTripsOnBoard = 0;

        for (Map.Entry<Integer, Integer> entry : frequencyOfRanksOnBoard.entrySet()) {
            if(entry.getValue() == 3) {
                rankOfTripsOnBoard = entry.getKey();
            }
        }

        if(boardRanks.size() == 3) {
            if(Collections.max(combo2) > Collections.max(combo1)) {
                return 1;
            } else if(Collections.max(combo2) == Collections.max(combo1)) {
                if(Collections.min(combo2) > Collections.min(combo1)) {
                    return 1;
                } else if(Collections.min(combo2) == Collections.min(combo1)) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } else {
            boardRanks.removeAll(Collections.singleton(rankOfTripsOnBoard));

            if(Collections.max(combo2) > Collections.max(combo1)) {
                if(board.size() == 4) {
                    return 1;
                } else {
                    if(Collections.max(combo2) > Collections.min(boardRanks)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            } else if(Collections.max(combo2) == Collections.max(combo1)) {
                if(Collections.min(combo2) > Collections.min(combo1)) {
                    if(Collections.min(combo2) > Collections.min(boardRanks)) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else if (Collections.min(combo2) == Collections.min(combo1)) {
                    return 0;
                } else {
                    if(Collections.min(combo1) > Collections.min(boardRanks)) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            } else {
                if(board.size() == 4) {
                    return -1;
                } else {
                    if(Collections.max(combo1) > Collections.min(boardRanks)) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }

    private Map<Integer, Set<Set<Card>>> removeDuplicateCombos(Map<Integer, Set<Set<Card>>> sortedCombos) {
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

        return sortedCombos;
    }
}

