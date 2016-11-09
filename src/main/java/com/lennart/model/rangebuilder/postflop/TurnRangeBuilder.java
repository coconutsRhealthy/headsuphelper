package com.lennart.model.rangebuilder.postflop;

import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LPO10346 on 10/17/2016.
 */
public class TurnRangeBuilder {

    RangeBuilder rangeBuilder = new RangeBuilder();
    FlopRangeBuilder flopRangeBuilder = new FlopRangeBuilder();

    //IP

    public Map<Integer, Set<Set<Card>>> get2bet1betFCheck(List<Card> board, List<Card> holeCards) {
        Map<Integer, Set<Card>> flopRange =
                rangeBuilder.convertPreviousStreetRangeToCorrectFormat(flopRangeBuilder.get2bet1bet(board, holeCards));

        //turn
        Map<Integer, Map<Integer, Set<Card>>> turnRange = new HashMap<>();

        return rangeBuilder.createRange(flopRange, turnRange, holeCards);
    }

    //OOP

    public Map<Integer, Set<Set<Card>>> getCall2betCall1betF1bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> turnRange = new HashMap<>();

        Map<Integer, Set<Card>> flopRange =
                rangeBuilder.convertPreviousStreetRangeToCorrectFormat(flopRangeBuilder.getCall2betCall1bet(board, holeCards));

        //turn

        //value range

        //tricky range

        //draws

        //air

        return null;
    }

}
