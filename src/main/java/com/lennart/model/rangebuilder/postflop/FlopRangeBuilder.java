package com.lennart.model.rangebuilder.postflop;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.RangeBuilder;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LPO10346 on 10/17/2016.
 */
public class FlopRangeBuilder {

    PreflopRangeBuilder preflopRangeBuilder = new PreflopRangeBuilder();
    RangeBuilder rangeBuilder = new RangeBuilder();
    BoardEvaluator boardEvaluator = new BoardEvaluator();
    StraightDrawEvaluator straightDrawEvaluator = new StraightDrawEvaluator();
    FlushDrawEvaluator flushDrawEvaluator = new FlushDrawEvaluator();
    HighCardDrawEvaluator highCardDrawEvaluator = new HighCardDrawEvaluator();

    //IP
    public Map<Integer, Set<Set<Card>>> get2betFCheck(List<Card> holeCards) {
        //preflop
        Map<Integer, Set<Card>> preflopRange = preflopRangeBuilder.getOpponentCall2betRange();

        //postflop
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        return rangeBuilder.createRange(preflopRange, flopRange, holeCards);
    }

    public Map<Integer, Set<Set<Card>>> get2betF2bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //preflop
        Map<Integer, Set<Card>> preflopRange = preflopRangeBuilder.getOpponentCall2betRange();

        //postflop

        //de value range
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.87, 1, 0.9));

        //de tricky range
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.65, 0.87, 0.2, holeCards, preflopRange));

        //de draws
        flopRange.put(flopRange.size(), rangeBuilder.getStrongOosdCombos(board, 0.5, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumOosdCombos(board, 0.5, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongGutshotCombos(board, 0.5, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongFlushDrawCombos(board, 0.5, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumFlushDrawCombos(board, 0.5, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongTwoOvercardCombos(board, 0.5, holeCards, preflopRange));

        //de air combos
        flopRange.put(flopRange.size(), rangeBuilder.getCombosThatAreBothStrongBdFlushAndBdStraightDraw(flopRange,
                preflopRange, 0.05, board, holeCards));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongBackDoorFlushDrawCombos(flopRange, preflopRange, 0.03,
                board, holeCards));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongBackDoorStraightDrawCombos(flopRange, preflopRange, 0.03,
                board, holeCards));
        flopRange.put(flopRange.size(), rangeBuilder.getAirRangeNew(flopRange, preflopRange, 0.14, board, holeCards));

        Map<Integer, Set<Set<Card>>> eije = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        int x = rangeBuilder.countNumberOfCombos(eije);

        return eije;
    }

    public Map<Integer, Set<Set<Card>>> getCall3betF1bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //preflop
        Map<Integer, Set<Card>> preflopRange = preflopRangeBuilder.getOpponent3betRange();

        //postflop

        //de value range

        return null;
    }
}
