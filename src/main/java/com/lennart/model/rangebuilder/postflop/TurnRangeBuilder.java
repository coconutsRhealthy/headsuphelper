package com.lennart.model.rangebuilder.postflop;

import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.*;

/**
 * Created by LPO10346 on 10/17/2016.
 */
public class TurnRangeBuilder {

    RangeBuilder rangeBuilder;
    FlopRangeBuilder flopRangeBuilder;

    public TurnRangeBuilder(RangeBuilder rangeBuilder, FlopRangeBuilder flopRangeBuilder) {
        this.rangeBuilder = rangeBuilder;
        this.flopRangeBuilder = flopRangeBuilder;
    }


    //IP

    //Excel: 53%
    //CHECK: 66%
    public double get2bet1bet1bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> turnRange = new HashMap<>();

        Map<Integer, Set<Card>> rangeResultingFromPreviousActions =
                rangeBuilder.convertPreviousActionOrStreetRangeToCorrectFormat(flopRangeBuilder.get2bet1bet(board, holeCards));

        //turn

        //value range
        turnRange.put(turnRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.87, 1, 0.2));
        turnRange.put(turnRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.64, 0.87, 0.9));
        turnRange.put(turnRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.5, 0.64, 0.45));

        //draws
        turnRange.put(turnRange.size(), rangeBuilder.getStrongOosdCombos(turnRange, board, 0.5, holeCards, rangeResultingFromPreviousActions));
        turnRange.put(turnRange.size(), rangeBuilder.getMediumOosdCombos(turnRange, board, 0.25, holeCards, rangeResultingFromPreviousActions));

        turnRange.put(turnRange.size(), rangeBuilder.getStrongGutshotCombos(turnRange, board, 0.5, holeCards, rangeResultingFromPreviousActions));
        turnRange.put(turnRange.size(), rangeBuilder.getMediumGutshotCombos(turnRange, board, 0.17, holeCards, rangeResultingFromPreviousActions));

        turnRange.put(turnRange.size(), rangeBuilder.getStrongFlushDrawCombos(turnRange, board, 0.5, holeCards, rangeResultingFromPreviousActions));
        turnRange.put(turnRange.size(), rangeBuilder.getMediumFlushDrawCombos(turnRange, board, 0.25, holeCards, rangeResultingFromPreviousActions));
        turnRange.put(turnRange.size(), rangeBuilder.getStrongTwoOvercardCombos(turnRange, board, 0.4, holeCards, rangeResultingFromPreviousActions));

        //air
        turnRange = rangeBuilder.addXPercentAirCombos(holeCards, board, turnRange, rangeResultingFromPreviousActions, 0.1);

        Map<Integer, Set<Set<Card>>> turnRangeToReturn = rangeBuilder.createRange(rangeResultingFromPreviousActions, turnRange);

        double numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(turnRangeToReturn);

        return numberOfCombosToReturn / rangeResultingFromPreviousActions.size();
    }

    //OOP

    //45%
    //Excel: 45%
    //CHECK: 53%
    public double getCall2betCall1betCall1bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> turnRange = new HashMap<>();

        List<Card> boardPreviousStreet = new ArrayList<>();
        boardPreviousStreet.addAll(board);
        boardPreviousStreet.remove(boardPreviousStreet.size()-1);

        Map<Integer, Set<Card>> rangeResultingFromPreviousActions =
                rangeBuilder.convertPreviousActionOrStreetRangeToCorrectFormat(flopRangeBuilder.getCall2betCall1bet(boardPreviousStreet, holeCards));

        //turn

        //value range
        turnRange.put(turnRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.9, 1, 0.8));
        turnRange.put(turnRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.84, 0.9, 0.7));
        turnRange.put(turnRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.75, 0.84, 0.5));
        turnRange.put(turnRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.64, 0.75, 0.4));
        turnRange.put(turnRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.5, 0.64, 0.2));

        //tricky range

        //draws
        turnRange.put(turnRange.size(), rangeBuilder.getStrongOosdCombos(turnRange, board, 0.6, holeCards, rangeResultingFromPreviousActions));
        turnRange.put(turnRange.size(), rangeBuilder.getMediumOosdCombos(turnRange, board, 0.4, holeCards, rangeResultingFromPreviousActions));

        turnRange.put(turnRange.size(), rangeBuilder.getStrongGutshotCombos(turnRange, board, 0.6, holeCards, rangeResultingFromPreviousActions));

        turnRange.put(turnRange.size(), rangeBuilder.getStrongFlushDrawCombos(turnRange, board, 0.6, holeCards, rangeResultingFromPreviousActions));
        turnRange.put(turnRange.size(), rangeBuilder.getMediumFlushDrawCombos(turnRange, board, 0.4, holeCards, rangeResultingFromPreviousActions));
        turnRange.put(turnRange.size(), rangeBuilder.getStrongTwoOvercardCombos(turnRange, board, 0.55, holeCards, rangeResultingFromPreviousActions));

        //air
        turnRange = rangeBuilder.addXPercentAirCombos(holeCards, board, turnRange, rangeResultingFromPreviousActions, 0.15);

        Map<Integer, Set<Set<Card>>> turnRangeToReturn = rangeBuilder.createRange(rangeResultingFromPreviousActions, turnRange);

        double numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(turnRangeToReturn);

        return numberOfCombosToReturn / rangeResultingFromPreviousActions.size();
    }

}
