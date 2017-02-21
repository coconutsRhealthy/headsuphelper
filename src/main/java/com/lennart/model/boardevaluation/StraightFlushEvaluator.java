package com.lennart.model.boardevaluation;

import com.lennart.model.card.Card;

import java.util.*;

/**
 * Created by lennart on 25-9-16.
 */
public class StraightFlushEvaluator extends BoardEvaluator implements ComboComparator {

    private Map<Integer, Set<Set<Card>>> combosThatMakeStraightFlush;
    private Map<Integer, List<Card>> allPossibleFiveConnectingSuitedCards;

    public StraightFlushEvaluator(List<Card> board) {
        getStraightFlushCombosInitialize(board);
    }

    public Map<Integer, Set<Set<Card>>> getStraightFlushCombos() {
        return combosThatMakeStraightFlush;
    }

    private void getStraightFlushCombosInitialize(List<Card> board) {
        Map<Integer, Set<Set<Card>>> straightCombos = new StraightEvaluator().getMapOfStraightCombosForStraightFLushEvaluator(board);
        Map<Integer, Set<Set<Card>>> flushCombos = new FlushEvaluator().getMapOfFlushCombosForStraightFLushEvaluator(board);

        List<Set<Card>> straightCombosList = new ArrayList<>();
        List<Set<Card>> flushCombosList = new ArrayList<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : straightCombos.entrySet()) {
            for(Set<Card> s : entry.getValue()) {
                straightCombosList.add(s);
            }
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : flushCombos.entrySet()) {
            for(Set<Card> s : entry.getValue()) {
                flushCombosList.add(s);
            }
        }

        straightCombosList.retainAll(flushCombosList);
        allPossibleFiveConnectingSuitedCards = getAllPossibleFiveConnectingSuitedCards();
        Map<Integer, List<Card>> combosThatMakeStraightFlush = new HashMap<>();

        loop: for(Set<Card> combo : straightCombosList) {
            List<Card> comboPlusBoard = new ArrayList<>();
            comboPlusBoard.addAll(board);
            comboPlusBoard.addAll(combo);

            for (Map.Entry<Integer, List<Card>> entry : allPossibleFiveConnectingSuitedCards.entrySet()) {
                if(comboPlusBoard.containsAll(entry.getValue())) {
                    List<Card> l = new ArrayList<>();
                    l.addAll(combo);
                    combosThatMakeStraightFlush.put(combosThatMakeStraightFlush.size(), l);
                    continue loop;
                }
            }
        }
        this.combosThatMakeStraightFlush = getSortedCardComboMap(combosThatMakeStraightFlush, board, this);
    }

    @Override
    public Comparator<Set<Card>> getComboComparator(List<Card> board) {
        return new Comparator<Set<Card>>() {
            @Override
            public int compare(Set<Card> combo1, Set<Card> combo2) {
                List<Card> combo1PlusBoard = new ArrayList<>();
                List<Card> combo2PlusBoard = new ArrayList<>();

                combo1PlusBoard.addAll(combo1);
                combo1PlusBoard.addAll(board);
                combo2PlusBoard.addAll(combo2);
                combo2PlusBoard.addAll(board);

                int highestStraightThatIsPresentInCombo1PlusBoard = 0;
                int highestStraightThatIsPresentInCombo2PlusBoard = 0;

                for (Map.Entry<Integer, List<Card>> entry : allPossibleFiveConnectingSuitedCards.entrySet()) {
                    if(combo1PlusBoard.containsAll(entry.getValue())) {
                        highestStraightThatIsPresentInCombo1PlusBoard = entry.getKey();
                    }

                    if(combo2PlusBoard.containsAll(entry.getValue())) {
                        highestStraightThatIsPresentInCombo2PlusBoard = entry.getKey();
                    }
                }

                if(highestStraightThatIsPresentInCombo2PlusBoard > highestStraightThatIsPresentInCombo1PlusBoard) {
                    return 1;
                } else if (highestStraightThatIsPresentInCombo2PlusBoard == highestStraightThatIsPresentInCombo1PlusBoard) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };
    }
}
