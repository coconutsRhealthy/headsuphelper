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
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.87, 0.90, 0.3));
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.90, 0.95, 0.5));
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.95, 1, 0.9));

        //de tricky range
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.65, 0.87, 0.16, holeCards, preflopRange));

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
        flopRange.put(flopRange.size(), rangeBuilder.getAirRange(flopRange, preflopRange, 0.14, board, holeCards));

        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        int numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }

    public Map<Integer, Set<Set<Card>>> getCall3betF1bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //preflop
        Map<Integer, Set<Card>> preflopRange = preflopRangeBuilder.getOpponent3betRange();

        //postflop

        //de value range
        //test op Ks9d7d board. Je hebt ge3bet. Met welke handen wil je value 1betten...
        //Ik denk met elke K..

        //80% met alle 87%+ combos
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.87, 1, 0.8));

        //60% met de mid pair combos en bottom pair combos
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.64, 0.87, 0.6));

        //de tricky range?
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.5, 0.64, 0.2));

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
        flopRange.put(flopRange.size(), rangeBuilder.getAirRange(flopRange, preflopRange, 0.14, board, holeCards));

        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        int numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }

    public Map<Integer, Set<Set<Card>>> get4betFCheck(List<Card> board, List<Card> holeCards) {
        return null;
    }

    //OOP
    public Map<Integer, Set<Set<Card>>> getCall2betF1bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //preflop
        Map<Integer, Set<Card>> preflopRange = preflopRangeBuilder.getOpponent2betRange();

        //postflop

        //value range
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.5, 1, 0.8));

        //draws
        flopRange.put(flopRange.size(), rangeBuilder.getStrongOosdCombos(board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumOosdCombos(board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getWeakOosdCombos(board, 0.8, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongGutshotCombos(board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumGutshotCombos(board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getWeakGutshotCombos(board, 0.8, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongFlushDrawCombos(board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumFlushDrawCombos(board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getWeakFlushDrawCombos(board, 0.8, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongTwoOvercardCombos(board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumTwoOvercardCombos(board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getWeakTwoOvercardCombos(board, 0.8, holeCards, preflopRange));

        //de air combos
        flopRange.put(flopRange.size(), rangeBuilder.getCombosThatAreBothStrongBdFlushAndBdStraightDraw(flopRange,
                preflopRange, 0.05, board, holeCards));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongBackDoorFlushDrawCombos(flopRange, preflopRange, 0.03,
                board, holeCards));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongBackDoorStraightDrawCombos(flopRange, preflopRange, 0.03,
                board, holeCards));
        flopRange.put(flopRange.size(), rangeBuilder.getAirRange(flopRange, preflopRange, 0.14, board, holeCards));

        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        int numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }

    //gets range of opponent when opponent calls preflop 3bet and bets flop. Should return 50%
    //of the previous range combos in default scenario.
    public Map<Integer, Set<Set<Card>>> get3betF1bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //preflop
        Map<Integer, Set<Card>> preflopRange = preflopRangeBuilder.getOpponentCall3betRange();

        //postflop

        //value range
        //80% met alle 87%+ combos
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.87, 1, 0.8));

        //50% met de mid pair combos en bottom pair combos
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.64, 0.87, 0.55));

        //de tricky range?
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.5, 0.64, 0.2));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongOosdCombos(board, 0.72, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumOosdCombos(board, 0.72, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongGutshotCombos(board, 0.72, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongFlushDrawCombos(board, 0.72, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumFlushDrawCombos(board, 0.72, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongTwoOvercardCombos(board, 0.72, holeCards, preflopRange));

        //de air combos
        flopRange.put(flopRange.size(), rangeBuilder.getCombosThatAreBothStrongBdFlushAndBdStraightDraw(flopRange,
                preflopRange, 0.10, board, holeCards));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongBackDoorFlushDrawCombos(flopRange, preflopRange, 0.06,
                board, holeCards));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongBackDoorStraightDrawCombos(flopRange, preflopRange, 0.06,
                board, holeCards));
        flopRange.put(flopRange.size(), rangeBuilder.getAirRange(flopRange, preflopRange, 0.14, board, holeCards));

        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        int numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }

}
