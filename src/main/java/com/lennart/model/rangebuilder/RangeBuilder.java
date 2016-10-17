package com.lennart.model.rangebuilder;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilder;

import java.util.*;

/**
 * Created by LPO10346 on 9/2/2016.
 */
public class RangeBuilder {
    //Future class which will create estimated opponent ranges based on type of game (single raised, 3bet, 4bet,
    //ch-raised, etc. Then, to estimate the strength of your hand, you will evaluate your hand against this range.
    //For example if your hand beats 60% of the estimated range of your opponent, the hand is 'medium strong'

    //to evaluate your hand against a range, make a map of all possible starthands, sorted from strongest to weakest.
    //see how high your hand ranks in this map. To make this map, first add all combos that getCombosThatMakeRoyalFlush(),
    //then getCombosThatMakeStraightFlush, then getCombosThatMakeQuads(), etc. To correct this map for ranges, remove all
    //combos from this map that do not fall in the range. Of course, the combos that getCombosThatMakeRoyalFlush() and
    //the other methods return, should first be sorted from strongest to weakest, before added to the map.

    BoardEvaluator boardEvaluator = new BoardEvaluator();
    StraightDrawEvaluator straightDrawEvaluator = new StraightDrawEvaluator();
    FlushDrawEvaluator flushDrawEvaluator = new FlushDrawEvaluator();
    HighCardDrawEvaluator highCardDrawEvaluator = new HighCardDrawEvaluator();
    PreflopRangeBuilder preflopRangeBuilder = new PreflopRangeBuilder();

    public Map<Integer, Set<Set<Card>>> getRange(String handPath, List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> preflopRange = new HashMap<>();
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        if(handPath.equals("2bet2betFcheck")) {
            //preflop
            preflopRange.put(preflopRange.size(), preflopRangeBuilder.getPocketPairs(2));
            preflopRange.put(preflopRange.size(), preflopRangeBuilder.getSuitedHoleCards(2, 2));
            preflopRange.put(preflopRange.size(), preflopRangeBuilder.getOffSuitConnectors(4));
            preflopRange.put(preflopRange.size(), preflopRangeBuilder.getOffSuitOneGappers(6));
            preflopRange.put(preflopRange.size(), preflopRangeBuilder.getOffSuitTwoGappers(8));
            preflopRange.put(preflopRange.size(), preflopRangeBuilder.getOffSuitThreeGappers(8));
            preflopRange.put(preflopRange.size(), preflopRangeBuilder.getOffSuitHoleCards(12, 2));

            //postflop
            flopRange.put(flopRange.size(), boardEvaluator.getCombosAboveDesignatedStrengthLevel(0.52, board));
            flopRange.put(flopRange.size(), straightDrawEvaluator.getStrongOosdCombos(board));
            flopRange.put(flopRange.size(), straightDrawEvaluator.getMediumOosdCombos(board));
            flopRange.put(flopRange.size(), straightDrawEvaluator.getStrongGutshotCombos(board));
            flopRange.put(flopRange.size(), straightDrawEvaluator.getMediumGutshotCombos(board));
            flopRange.put(flopRange.size(), flushDrawEvaluator.getStrongFlushDrawCombos(board));
            flopRange.put(flopRange.size(), flushDrawEvaluator.getMediumFlushDrawCombos(board));
            flopRange.put(flopRange.size(), highCardDrawEvaluator.getStrongTwoOvercards(board));
            flopRange.put(flopRange.size(), highCardDrawEvaluator.getMediumTwoOvercards(board));

            return createRange(preflopRange, flopRange, holeCards);
        }

        return null;
    }

    //helper methods
    private Map<Integer, Set<Set<Card>>> createRange(Map<Integer, Map<Integer, Set<Card>>> preflopRange,
                                                    Map<Integer, Map<Integer, Set<Card>>> flopRange, List<Card> holeCards) {
        Map<Integer, Set<Set<Card>>> allSortedCombosClearedForRange = boardEvaluator.getCopyOfSortedCombos();
        allSortedCombosClearedForRange = removeHoleCardCombosFromAllSortedCombos(allSortedCombosClearedForRange, holeCards);

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombosClearedForRange.entrySet()) {
            loop: for (Iterator<Set<Card>> it = entry.getValue().iterator(); it.hasNext(); ) {
                Set<Card> comboFromAllSortedCombos = it.next();
                for (Map.Entry<Integer, Map<Integer, Set<Card>>> preflopRangeMapEntry : preflopRange.entrySet()) {
                    if(!preflopRangeMapEntry.getValue().isEmpty()) {
                        for (Map.Entry<Integer, Set<Card>> comboToRetainInRange : preflopRangeMapEntry.getValue().entrySet()) {
                            if (comboFromAllSortedCombos.equals(comboToRetainInRange.getValue())) {
                                continue loop;
                            }
                        }
                    }
                }
                it.remove();
            }
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombosClearedForRange.entrySet()) {
            loop: for (Iterator<Set<Card>> it = entry.getValue().iterator(); it.hasNext(); ) {
                Set<Card> comboFromAllSortedCombos = it.next();
                for (Map.Entry<Integer, Map<Integer, Set<Card>>> flopRangeMapEntry : flopRange.entrySet()) {
                    if(!flopRangeMapEntry.getValue().isEmpty()) {
                        for (Map.Entry<Integer, Set<Card>> comboToRetainInRange : flopRangeMapEntry.getValue().entrySet()) {
                            if (comboFromAllSortedCombos.equals(comboToRetainInRange.getValue())) {
                                continue loop;
                            }
                        }
                    }
                }
                it.remove();
            }
        }
        return allSortedCombosClearedForRange;
    }

    private Map<Integer, Set<Set<Card>>> removeHoleCardCombosFromAllSortedCombos(Map<Integer, Set<Set<Card>>> allSortedCombos,
                                                                                 List<Card> holeCards) {
        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombos.entrySet()) {
            for(Iterator<Set<Card>> it = entry.getValue().iterator(); it.hasNext(); ) {
                Set<Card> comboToCheck = it.next();
                if(comboToCheck.contains(holeCards.get(0)) || comboToCheck.contains(holeCards.get(1))) {
                    it.remove();
                }
            }
        }
        return allSortedCombos;
    }

    private int countNumberOfCombos(Map<Integer, Set<Set<Card>>> combos) {
        int counter = 0;

        for (Map.Entry<Integer, Set<Set<Card>>> entry : combos.entrySet()) {
            for(Set<Card> combo : entry.getValue()) {
                counter++;
            }
        }
        return counter;
    }
}