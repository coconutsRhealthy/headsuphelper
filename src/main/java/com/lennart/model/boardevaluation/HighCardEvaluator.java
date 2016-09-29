package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by lpo10346 on 9/13/2016.
 */
public class HighCardEvaluator extends BoardEvaluator implements ComboComparator {

    PairEvaluator pairEvaluator = new PairEvaluator();
    TwoPairEvaluator twoPairEvaluator = new TwoPairEvaluator();
    ThreeOfAKindEvaluator threeOfAKindEvaluator = new ThreeOfAKindEvaluator();
    StraightEvaluator straightEvaluator = new StraightEvaluator();
    FlushEvaluator flushEvaluator = new FlushEvaluator();
    FullHouseEvaluator fullHouseEvaluator = new FullHouseEvaluator();
    FourOfAKindEvaluator fourOfAKindEvaluator = new FourOfAKindEvaluator();
    StraightFlushEvaluator straightFlushEvaluator = new StraightFlushEvaluator();


    public Map<Integer, Set<Set<Card>>> getHighCardCombos(List<Card> board) {
        //get alle mogelijke starthanden
        Map<Integer, List<Card>> highCardCombos = getAllPossibleStartHands();

        //verwijder alle combos die al in de andere klassen naar voren komen
        Map<Integer, Set<Set<Card>>> pairCombos = pairEvaluator.getCombosThatMakePair(board);
        Map<Integer, Set<Set<Card>>> twoPairCombos = twoPairEvaluator.getCombosThatMakeTwoPair(board);
        Map<Integer, Set<Set<Card>>> threeOfAKindCombos = threeOfAKindEvaluator.getThreeOfAKindCombos(board);
        Map<Integer, Set<Set<Card>>> straightCombos = straightEvaluator.getMapOfStraightCombos(board);
        Map<Integer, Set<Set<Card>>> flushCombos = flushEvaluator.getFlushCombos(board);
        Map<Integer, Set<Set<Card>>> fullHouseCombos = fullHouseEvaluator.getFullHouseCombos(board);
        Map<Integer, Set<Set<Card>>> fourOfAKindCombos = fourOfAKindEvaluator.getFourOfAKindCombos(board);
        Map<Integer, Set<Set<Card>>> straightFlushCombos = straightFlushEvaluator.getStraightFlushCombos(board);

        //remove pairCombos
        highCardCombos = removeCombos(highCardCombos, pairCombos);

        //remove twoPairCombos
        highCardCombos = removeCombos(highCardCombos, twoPairCombos);

        //remove threeOfAKindCombos
        highCardCombos = removeCombos(highCardCombos, threeOfAKindCombos);

        //remove straightCombos
        highCardCombos = removeCombos(highCardCombos, straightCombos);

        //remove flushCombos
        highCardCombos = removeCombos(highCardCombos, flushCombos);

        //remove fullHouseCombos
        highCardCombos = removeCombos(highCardCombos, fullHouseCombos);

        //remove fourOfAKindCombos
        highCardCombos = removeCombos(highCardCombos, fourOfAKindCombos);

        //remove straightFlushCombos
        highCardCombos = removeCombos(highCardCombos, straightFlushCombos);

        //corrigeer ook voor het board
        highCardCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(highCardCombos, board);

        return getSortedCardComboMap(highCardCombos, board, new HighCardEvaluator());
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

                List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
                List<Integer> boardPlusCombo1 = new ArrayList<>();
                List<Integer> boardPlusCombo2 = new ArrayList<>();

                boardPlusCombo1.addAll(boardRanks);
                boardPlusCombo1.addAll(combo1);
                boardPlusCombo2.addAll(boardRanks);
                boardPlusCombo2.addAll(combo2);

                Collections.sort(boardPlusCombo1);
                Collections.sort(boardPlusCombo2);

                if(board.size() == 4) {
                    boardPlusCombo1.remove(0);
                    boardPlusCombo2.remove(0);
                }
                if(board.size() == 5) {
                    boardPlusCombo1.remove(0);
                    boardPlusCombo1.remove(0);
                    boardPlusCombo2.remove(0);
                    boardPlusCombo2.remove(0);
                }

                int combo1rank1 = boardPlusCombo1.get(4);
                int combo1rank2 = boardPlusCombo1.get(3);
                int combo1rank3 = boardPlusCombo1.get(2);
                int combo1rank4 = boardPlusCombo1.get(1);
                int combo1rank5 = boardPlusCombo1.get(0);
                int combo2rank1 = boardPlusCombo2.get(4);
                int combo2rank2 = boardPlusCombo2.get(3);
                int combo2rank3 = boardPlusCombo2.get(2);
                int combo2rank4 = boardPlusCombo2.get(1);
                int combo2rank5 = boardPlusCombo2.get(0);

                if(combo2rank1 > combo1rank1) {
                    return 1;
                } else if(combo2rank1 == combo1rank1) {
                    if(combo2rank2 > combo1rank2) {
                        return 1;
                    } else if(combo2rank2 == combo1rank2) {
                        if(combo2rank3 > combo1rank3) {
                            return 1;
                        } else if(combo2rank3 == combo1rank3) {
                            if(combo2rank4 > combo1rank4) {
                                return 1;
                            } else if(combo2rank4 == combo1rank4) {
                                if(combo2rank5 > combo1rank5) {
                                    return 1;
                                } else if(combo2rank5 == combo1rank5) {
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
                } else {
                    return -1;
                }
            }
        };
    }

    private Map<Integer, List<Card>> removeCombos(Map<Integer, List<Card>> allPossibleStartHands, Map<Integer, Set<Set<Card>>> combosToRemove) {
        for(Iterator<Map.Entry<Integer, List<Card>>> it = allPossibleStartHands.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, List<Card>> entry = it.next();
            for(Map.Entry<Integer, Set<Set<Card>>> comboToRemove : combosToRemove.entrySet()) {
                for(Set<Card> s : comboToRemove.getValue()) {
                    if(entry.getValue().containsAll(s)) {
                        it.remove();
                    }
                }
            }
        }

        return allPossibleStartHands;
    }
}
