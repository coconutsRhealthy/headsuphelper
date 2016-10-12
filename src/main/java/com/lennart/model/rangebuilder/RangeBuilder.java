package com.lennart.model.rangebuilder;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.pokergame.Card;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public Map<Integer, Set<Set<Card>>> getRange(String handPath, List<Card> board) {
        if(handPath.equals("2bet2bet")) {

            BoardEvaluator boardEvaluator = new BoardEvaluator();

            //alle handen boven 50% op flop
            Map<Integer, Set<Set<Card>>> sortedCombos = boardEvaluator.getSortedCombos(board);

            //alle strong en medium straight draws op flop

            //alle strong en medium flushdraws op flop

            //alle strong en medium overcarddraws op flop


        }

        return null;
    }
}
