package com.lennart.model.rangebuilder.preflop.ip;

import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lennart on 24-10-16.
 */
public class _2betRangeBuilder {

    private Map<Integer, Map<Integer, Set<Card>>> comboMap100Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap5Percent = new HashMap<>();

    private PreflopRangeBuilderUtil p;

    public _2betRangeBuilder(PreflopRangeBuilderUtil p) {
        this.p = p;

        comboMap100Percent.put(1, p.getSuitedHoleCards(2, 2, 100));
        comboMap100Percent.put(2, p.getPocketPairs(2, 100));
        comboMap100Percent.put(6, p.getOffSuitHoleCards(11, 2, 100));
        comboMap100Percent.put(7, p.getOffSuitCombosOfGivenRanks(10, 9));
        comboMap100Percent.put(8, p.getOffSuitCombosOfGivenRanks(10, 8));
        comboMap100Percent.put(9, p.getOffSuitCombosOfGivenRanks(10, 7));
        comboMap100Percent.put(10, p.getOffSuitCombosOfGivenRanks(10, 6));
        comboMap100Percent.put(11, p.getOffSuitCombosOfGivenRanks(9, 8));
        comboMap100Percent.put(12, p.getOffSuitCombosOfGivenRanks(9, 7));
        comboMap100Percent.put(13, p.getOffSuitCombosOfGivenRanks(9, 6));
        comboMap100Percent.put(14, p.getOffSuitCombosOfGivenRanks(8, 7));
        comboMap100Percent.put(15, p.getOffSuitCombosOfGivenRanks(8, 6));
        comboMap100Percent.put(16, p.getOffSuitCombosOfGivenRanks(8, 5));
        comboMap100Percent.put(17, p.getOffSuitCombosOfGivenRanks(7, 6));
        comboMap100Percent.put(18, p.getOffSuitCombosOfGivenRanks(7, 5));
        comboMap100Percent.put(19, p.getOffSuitCombosOfGivenRanks(7, 4));
        comboMap100Percent.put(20, p.getOffSuitCombosOfGivenRanks(6, 5));
        comboMap100Percent.put(21, p.getOffSuitCombosOfGivenRanks(6, 4));
        comboMap100Percent.put(22, p.getOffSuitCombosOfGivenRanks(6, 3));
        comboMap100Percent.put(23, p.getOffSuitCombosOfGivenRanks(5, 4));
        comboMap100Percent.put(24, p.getOffSuitCombosOfGivenRanks(5, 3));
        comboMap100Percent.put(25, p.getOffSuitCombosOfGivenRanks(5, 2));
        comboMap100Percent.put(26, p.getOffSuitCombosOfGivenRanks(4, 3));
        comboMap100Percent.put(27, p.getOffSuitCombosOfGivenRanks(4, 2));
        comboMap100Percent.put(28, p.getOffSuitCombosOfGivenRanks(3, 2));

        comboMap5Percent.put(1, p.getOffSuitCombosOfGivenRanks(10, 5));
        comboMap5Percent.put(2, p.getOffSuitCombosOfGivenRanks(10, 4));
        comboMap5Percent.put(3, p.getOffSuitCombosOfGivenRanks(10, 3));
        comboMap5Percent.put(4, p.getOffSuitCombosOfGivenRanks(10, 2));
        comboMap5Percent.put(5, p.getOffSuitCombosOfGivenRanks(9, 5));
        comboMap5Percent.put(6, p.getOffSuitCombosOfGivenRanks(9, 4));
        comboMap5Percent.put(7, p.getOffSuitCombosOfGivenRanks(9, 3));
        comboMap5Percent.put(8, p.getOffSuitCombosOfGivenRanks(9, 2));
        comboMap5Percent.put(9, p.getOffSuitCombosOfGivenRanks(8, 4));
        comboMap5Percent.put(10, p.getOffSuitCombosOfGivenRanks(8, 3));
        comboMap5Percent.put(11, p.getOffSuitCombosOfGivenRanks(8, 2));
        comboMap5Percent.put(12, p.getOffSuitCombosOfGivenRanks(7, 3));
        comboMap5Percent.put(13, p.getOffSuitCombosOfGivenRanks(7, 2));
        comboMap5Percent.put(14, p.getOffSuitCombosOfGivenRanks(6, 2));
    }

    public Map<Integer, Set<Card>> getOpponent2betRange() {
        Map<Integer, Set<Card>> opponent2betRange = new HashMap<>();

        opponent2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponent2betRange, comboMap100Percent, 1);
        opponent2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponent2betRange, comboMap5Percent, 0.05);

        return opponent2betRange;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap100Percent() {
        return comboMap100Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap5Percent() {
        return comboMap5Percent;
    }
}
