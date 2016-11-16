package com.lennart.model.rangebuilder.postflop;

import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.RangeBuilder;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilder;

import java.util.*;

/**
 * Created by LPO10346 on 10/17/2016.
 */
public class FlopRangeBuilder {

    RangeBuilder rangeBuilder = new RangeBuilder();
    private PreflopRangeBuilder preflopRangeBuilder = new PreflopRangeBuilder();
    private Map<Integer, Set<Card>> opponent2betRange = preflopRangeBuilder.getOpponent2betRange();
    private Map<Integer, Set<Card>> opponent3betRange = preflopRangeBuilder.getOpponent3betRange();
    private Map<Integer, Set<Card>> opponentCall3betRange = preflopRangeBuilder.getOpponentCall3betRange();

    //IP

    public Map<Integer, Set<Set<Card>>> get2betCheck(List<Card> holeCards) {
        //preflop
        Map<Integer, Set<Card>> preflopRange = preflopRangeBuilder.getOpponentCall2betRange();

        //postflop
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        return rangeBuilder.createRange(preflopRange, flopRange, holeCards);
    }

    //40% -> 50% gemaakt
    public Map<Integer, Set<Set<Card>>> get2bet1bet(List<Card> board, List<Card> holeCards) {
        //preflop
        Map<Integer, Set<Card>> preflopRange = preflopRangeBuilder.getOpponentCall2betRange();

        //postflop
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.87, 1, 0.2, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.55, 0.87, 0.85, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.48, 0.54, 0.25, holeCards, preflopRange));

        //alle draws
        flopRange.put(flopRange.size(), rangeBuilder.getStrongOosdCombos(flopRange, board, 0.65, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumOosdCombos(flopRange, board, 0.5, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getWeakOosdCombos(flopRange, board, 0.5, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongGutshotCombos(flopRange, board, 0.7, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumGutshotCombos(flopRange, board, 0.5, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getWeakGutshotCombos(flopRange, board, 0.5, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongFlushDrawCombos(flopRange, board, 0.65, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumFlushDrawCombos(flopRange, board, 0.5, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getWeakFlushDrawCombos(flopRange, board, 0.5, holeCards, preflopRange));

        //check
        flopRange.put(flopRange.size(), rangeBuilder.getStrongTwoOvercardCombos(flopRange, board, 0.7, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumTwoOvercardCombos(flopRange, board, 0.5, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getWeakTwoOvercardCombos(flopRange, board, 0.5, holeCards, preflopRange));

        //beetje air
        flopRange = rangeBuilder.addXPercentAirCombos(holeCards, board, flopRange, preflopRange, 0.1);

        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        int numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }

    //20%
    public double get2betCall2bet(List<Card> board, List<Card> holeCards) {
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
        flopRange.put(flopRange.size(), rangeBuilder.getStrongOosdCombos(flopRange, board, 0.5, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumOosdCombos(flopRange, board, 0.2, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongGutshotCombos(flopRange, board, 0.5, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongFlushDrawCombos(flopRange, board, 0.5, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumFlushDrawCombos(flopRange, board, 0.5, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongTwoOvercardCombos(flopRange, board, 0.2, holeCards, preflopRange));

        //de air combos
        flopRange = rangeBuilder.addXPercentAirCombos(holeCards, board, flopRange, preflopRange, 0.18);

        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        double numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return numberOfCombosToReturn / preflopRange.size();
    }

    public Map<Integer, Set<Set<Card>>> getCall3betCheck(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();
        Map<Integer, Set<Card>> preflopRange = opponent3betRange;
        Map<Integer, Set<Card>> oppositeRange = rangeBuilder.getOppositeRangeAtFlop(getCall3betCall1bet(board, holeCards), preflopRange);
        flopRange.put(flopRange.size(), oppositeRange);
        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        int numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }

    //65% van preflop range
    public Map<Integer, Set<Set<Card>>> getCall3betCall1bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //preflop
        Map<Integer, Set<Card>> preflopRange = opponent3betRange;

        //postflop

        //de value range
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.87, 1, 0.8));

        //60% met de mid pair combos en bottom pair combos
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.64, 0.87, 0.6));

        //de tricky range?
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.5, 0.64, 0.2));

        //de draws
        flopRange.put(flopRange.size(), rangeBuilder.getStrongOosdCombos(flopRange, board, 0.75, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumOosdCombos(flopRange, board, 0.75, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongGutshotCombos(flopRange, board, 0.75, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongFlushDrawCombos(flopRange, board, 0.75, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumFlushDrawCombos(flopRange, board, 0.75, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongTwoOvercardCombos(flopRange, board, 0.75, holeCards, preflopRange));

        //de air combos
        flopRange = rangeBuilder.addXPercentAirCombos(holeCards, board, flopRange, preflopRange, 0.2);

        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        int numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }

    public Map<Integer, Set<Set<Card>>> getCall3bet1bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //range thus far
        Map<Integer, Set<Card>> rangeResultingFromPreviousActions =
                rangeBuilder.convertPreviousActionOrStreetRangeToCorrectFormat(getCall3betCheck(board, holeCards));

        //de value range
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.64, 1, 1));

        //de tricky range
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.5, 0.64, 0.5));

        //de draws
        flopRange.put(flopRange.size(), rangeBuilder.getStrongOosdCombos(flopRange, board, 1, holeCards, rangeResultingFromPreviousActions));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumOosdCombos(flopRange, board, 1, holeCards, rangeResultingFromPreviousActions));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongGutshotCombos(flopRange, board, 1, holeCards, rangeResultingFromPreviousActions));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongFlushDrawCombos(flopRange, board, 1, holeCards, rangeResultingFromPreviousActions));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumFlushDrawCombos(flopRange, board, 1, holeCards, rangeResultingFromPreviousActions));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongTwoOvercardCombos(flopRange, board, 1, holeCards, rangeResultingFromPreviousActions));

        //de air combos
        flopRange = rangeBuilder.addXPercentAirCombos(holeCards, board, flopRange, rangeResultingFromPreviousActions, 0.16);

        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(rangeResultingFromPreviousActions, flopRange, holeCards);

        int numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }



    //OOP

    //37% -> 30%
    public Map<Integer, Set<Set<Card>>> getCall2betCheck(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();
        Map<Integer, Set<Card>> preflopRange = opponent2betRange;
        Map<Integer, Set<Card>> oppositeRange = rangeBuilder.getOppositeRangeAtFlop(getCall2betCall1bet(board, holeCards), preflopRange);
        flopRange.put(flopRange.size(), oppositeRange);
        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        int x = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }

    //63% -> 70%
    public double getCall2betCall1bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //preflop
        Map<Integer, Set<Card>> preflopRange = opponent2betRange;

        //postflop

        //value range
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.5, 1, 0.8));

        //draws
        flopRange.put(flopRange.size(), rangeBuilder.getStrongOosdCombos(flopRange, board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumOosdCombos(flopRange, board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getWeakOosdCombos(flopRange, board, 0.8, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongGutshotCombos(flopRange, board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumGutshotCombos(flopRange, board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getWeakGutshotCombos(flopRange, board, 0.8, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongFlushDrawCombos(flopRange, board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumFlushDrawCombos(flopRange, board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getWeakFlushDrawCombos(flopRange, board, 0.8, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongTwoOvercardCombos(flopRange, board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumTwoOvercardCombos(flopRange, board, 0.8, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getWeakTwoOvercardCombos(flopRange, board, 0.8, holeCards, preflopRange));

        //de air combos
        flopRange = rangeBuilder.addXPercentAirCombos(holeCards, board, flopRange, preflopRange, 0.2);

        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        double numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return numberOfCombosToReturn / preflopRange.size();
    }

    public Map<Integer, Set<Set<Card>>> getCall2bet2bet(List<Card> board, List<Card> holeCards) {
        //startrange is denk ik getCall2betCall1bet
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //range thus far
        Map<Integer, Set<Card>> rangeResultingFromPreviousActions =
                rangeBuilder.convertPreviousActionOrStreetRangeToCorrectFormat(getCall2betCall1bet(board, holeCards));

        //de value range
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.88, 1, 0.77));
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.78, 0.88, 1));
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.64, 0.78, 0.7));

        //de tricky range
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.5, 0.64, 0.25));

        //de draws
        flopRange.put(flopRange.size(), rangeBuilder.getStrongOosdCombos(flopRange, board, 0.7, holeCards, rangeResultingFromPreviousActions));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumOosdCombos(flopRange, board, 0.6, holeCards, rangeResultingFromPreviousActions));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongGutshotCombos(flopRange, board, 0.75, holeCards, rangeResultingFromPreviousActions));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongFlushDrawCombos(flopRange, board, 0.7, holeCards, rangeResultingFromPreviousActions));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumFlushDrawCombos(flopRange, board, 0.6, holeCards, rangeResultingFromPreviousActions));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongTwoOvercardCombos(flopRange, board, 0.8, holeCards, rangeResultingFromPreviousActions));

        //de air combos
        flopRange = rangeBuilder.addXPercentAirCombos(holeCards, board, flopRange, rangeResultingFromPreviousActions, 0.15);

        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(rangeResultingFromPreviousActions, flopRange, holeCards);

        int numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }

    public Map<Integer, Set<Set<Card>>> get3betCheck(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();
        Map<Integer, Set<Card>> preflopRange = opponentCall3betRange;
        Map<Integer, Set<Card>> oppositeRange = rangeBuilder.getOppositeRangeAtFlop(get3betCall1bet(board, holeCards), preflopRange);
        flopRange.put(flopRange.size(), oppositeRange);
        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        int x = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }

    //55% van de preflopcallrange
    public Map<Integer, Set<Set<Card>>> get3bet1bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //preflop
        Map<Integer, Set<Card>> preflopRange = preflopRangeBuilder.getOpponentCall3betRange();

        //postflop

        //value range
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.6, 1, 1));
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.55, 0.6, 0.5));

        //de draws
        flopRange.put(flopRange.size(), rangeBuilder.getStrongOosdCombos(flopRange, board, 1, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumOosdCombos(flopRange, board, 0.75, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongGutshotCombos(flopRange, board, 1, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongFlushDrawCombos(flopRange, board, 1, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumFlushDrawCombos(flopRange, board, 0.75, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongTwoOvercardCombos(flopRange, board, 0.83, holeCards, preflopRange));

        //de backdoors en air
        flopRange = rangeBuilder.addXPercentAirCombos(holeCards, board, flopRange, preflopRange, 0.1);

        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        int numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }

    //50%
    public Map<Integer, Set<Set<Card>>> get3betCall1bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //preflop
        Map<Integer, Set<Card>> preflopRange = opponentCall3betRange;

        //postflop

        //value range
        //80% met alle 87%+ combos
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.87, 1, 0.8));

        //50% met de mid pair combos en bottom pair combos
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.64, 0.87, 0.53));

        //de tricky range?
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.5, 0.64, 0.22));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongOosdCombos(flopRange, board, 0.67, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumOosdCombos(flopRange, board, 0.67, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongGutshotCombos(flopRange, board, 0.67, holeCards, preflopRange));

        flopRange.put(flopRange.size(), rangeBuilder.getStrongFlushDrawCombos(flopRange, board, 0.67, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getMediumFlushDrawCombos(flopRange, board, 0.67, holeCards, preflopRange));
        flopRange.put(flopRange.size(), rangeBuilder.getStrongTwoOvercardCombos(flopRange, board, 0.67, holeCards, preflopRange));

        //de air combos
        flopRange = rangeBuilder.addXPercentAirCombos(holeCards, board, flopRange, preflopRange, 0.18);

        Map<Integer, Set<Set<Card>>> flopRangeToReturn = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        int numberOfCombosToReturn = rangeBuilder.countNumberOfCombos(flopRangeToReturn);

        return flopRangeToReturn;
    }
}
