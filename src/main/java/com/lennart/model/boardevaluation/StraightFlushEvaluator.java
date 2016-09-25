package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by lennart on 25-9-16.
 */
public class StraightFlushEvaluator extends BoardEvaluator implements ComboComparator {

    public Map<Integer, Set<Set<Card>>> getStraightFlushCombos(List<Card> board) {
        StraightEvaluator straightEvaluator = new StraightEvaluator();
        FlushEvaluator flushEvaluator = new FlushEvaluator();

        Map<Integer, Set<Set<Card>>> straightCombos = straightEvaluator.getMapOfStraightCombos(board);
        Map<Integer, Set<Set<Card>>> flushCombos = flushEvaluator.getFlushCombos(board);

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

        Map<Integer, List<Card>> combosThatMakeStraightAndFlush = new HashMap<>();

        for(Set<Card> s : straightCombosList) {
            List<Card> l = new ArrayList<>();
            l.addAll(s);
            combosThatMakeStraightAndFlush.put(combosThatMakeStraightAndFlush.size(), l);
        }

        if(board.size() == 3 && !combosThatMakeStraightAndFlush.isEmpty()) {
            return getSortedCardComboMap(combosThatMakeStraightAndFlush, board, new StraightFlushEvaluator());
        }

        if(board.size() > 3 && !combosThatMakeStraightAndFlush.isEmpty()) {
            Map<Integer, List<Integer>> allPossibleFiveConnectingCardRanks = getAllPossibleFiveConnectingCards();
            Map<Integer, Set<Card>> allPossibleStraightFlushes = new HashMap<>();
            Map<Integer, List<Card>> combosThatMakeStraightFlush = new HashMap<>();

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleFiveConnectingCardRanks.entrySet()) {
                allPossibleStraightFlushes.put(allPossibleStraightFlushes.size(), new HashSet<>());
                for (Integer rank : entry.getValue()) {
                    Card c = new Card(rank, 's');
                    allPossibleStraightFlushes.get(allPossibleStraightFlushes.size() - 1).add(c);
                }

                allPossibleStraightFlushes.put(allPossibleStraightFlushes.size(), new HashSet<>());
                for (Integer rank : entry.getValue()) {
                    Card c = new Card(rank, 'c');
                    allPossibleStraightFlushes.get(allPossibleStraightFlushes.size() - 1).add(c);
                }

                allPossibleStraightFlushes.put(allPossibleStraightFlushes.size(), new HashSet<>());
                for (Integer rank : entry.getValue()) {
                    Card c = new Card(rank, 'd');
                    allPossibleStraightFlushes.get(allPossibleStraightFlushes.size() - 1).add(c);
                }

                allPossibleStraightFlushes.put(allPossibleStraightFlushes.size(), new HashSet<>());
                for (Integer rank : entry.getValue()) {
                    Card c = new Card(rank, 'h');
                    allPossibleStraightFlushes.get(allPossibleStraightFlushes.size() - 1).add(c);
                }
            }

            if (board.size() == 5) {
                for (Map.Entry<Integer, Set<Card>> entry : allPossibleStraightFlushes.entrySet()) {
                    if (board.containsAll(entry.getValue())) {
                        return getSortedCardComboMap(combosThatMakeStraightAndFlush, board, new StraightFlushEvaluator());
                    }
                }
            }

            for (Map.Entry<Integer, List<Card>> entry : combosThatMakeStraightAndFlush.entrySet()) {
                List<Card> boardPlusCombo = new ArrayList<>();
                boardPlusCombo.addAll(board);
                boardPlusCombo.addAll(entry.getValue());

                for (Map.Entry<Integer, Set<Card>> entry2 : allPossibleStraightFlushes.entrySet()) {
                    if (boardPlusCombo.containsAll(entry2.getValue())) {
                        combosThatMakeStraightFlush.put(combosThatMakeStraightFlush.size(), entry.getValue());
                    }
                }
            }
            return getSortedCardComboMap(combosThatMakeStraightFlush, board, new StraightFlushEvaluator());
        }
        return null;
    }


    @Override
    public Comparator<Set<Card>> getComboComparator(List<Card> board) {
        return null;
    }
}
