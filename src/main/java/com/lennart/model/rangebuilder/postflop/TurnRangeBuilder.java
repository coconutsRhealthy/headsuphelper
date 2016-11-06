package com.lennart.model.rangebuilder.postflop;

import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.RangeBuilder;

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

    //je wil de range van je opponent inschatten, gegeven dat hij je preflop 2bet gecallt heeft en je flop cbet gecallt heeft
    public Map<Integer, Set<Set<Card>>> get2bet1betFCheck(List<Card> board, List<Card> holeCards) {
        //range resulting from previous actions:

        
        Map<Integer, Set<Card>> flopRange =
                rangeBuilder.convertPreviousStreetRangeToCorrectFormat(flopRangeBuilder.get2bet1bet(board, holeCards));

        return null;
    }

}
