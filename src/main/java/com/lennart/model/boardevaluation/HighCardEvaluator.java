package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by lpo10346 on 9/13/2016.
 */
public class HighCardEvaluator extends BoardEvaluator implements ComboComparatorRankOnly {

    PairEvaluator pairEvaluator = new PairEvaluator();
    TwoPairEvaluator twoPairEvaluator = new TwoPairEvaluator();
    ThreeOfAKindEvaluator threeOfAKindEvaluator = new ThreeOfAKindEvaluator();
    StraightEvaluator straightEvaluator = new StraightEvaluator();
    FlushEvaluator flushEvaluator = new FlushEvaluator();
    FourOfAKindEvaluator fourOfAKindEvaluator = new FourOfAKindEvaluator();
    StraightFlushEvaluator straightFlushEvaluator = new StraightFlushEvaluator();


    public Map<Integer, Set<Set<Card>>> getHighCardCombos(List<Card> board) {

        //get alle mogelijke starthanden
        Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();

        //verwijder alle combos die al in de andere klassen naar voren komen
        Map<Integer, Set<Set<Card>>> pairCombos = pairEvaluator.getCombosThatMakePair(board);
        Map<Integer, Set<Set<Card>>> twoPairCombos = twoPairEvaluator.getCombosThatMakeTwoPair(board);
        Map<Integer, Set<Set<Card>>> threeOfAKindCombos = threeOfAKindEvaluator.getThreeOfAKindCombos(board);
        Map<Integer, Set<Set<Card>>> straightCombos = straightEvaluator.getMapOfStraightCombos(board);
        Map<Integer, Set<Set<Card>>> flushCombos = flushEvaluator.getFlushCombos(board);
        Map<Integer, Set<Set<Card>>> fourOfAKindCombos = fourOfAKindEvaluator.getFourOfAKindCombos(board);
        Map<Integer, Set<Set<Card>>> straightFlushCombos = straightFlushEvaluator.getStraightFlushCombos(board);

        //remove pairCombos
        allPossibleStartHands = removeCombos(allPossibleStartHands, pairCombos);

        //remove twoPairCombos
        allPossibleStartHands = removeCombos(allPossibleStartHands, twoPairCombos);

        //remove threeOfAKindCombos
        allPossibleStartHands = removeCombos(allPossibleStartHands, threeOfAKindCombos);

        //remove straightCombos
        allPossibleStartHands = removeCombos(allPossibleStartHands, straightCombos);

        //remove flushCombos
        allPossibleStartHands = removeCombos(allPossibleStartHands, flushCombos);

        //remove fourOfAKindCombos
        allPossibleStartHands = removeCombos(allPossibleStartHands, fourOfAKindCombos);

        //remove straightFlushCombos
        allPossibleStartHands = removeCombos(allPossibleStartHands, straightFlushCombos);

        //corrigeer ook voor het board
        allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);

        //wat overlblijft zijn highCardCombos
        return null;
    }


    @Override
    public Comparator<List<Integer>> getComboComparatorRankOnly(List<Card> board) {
        return new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> combo1, List<Integer> combo2) {
                //To implement

                return 1;
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
