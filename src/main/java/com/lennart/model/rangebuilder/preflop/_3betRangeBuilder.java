package com.lennart.model.rangebuilder.preflop;

import com.lennart.model.pokergame.Card;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lpo10346 on 10/21/2016.
 */
public class _3betRangeBuilder {

    private static Map<Integer, Map<Integer, Set<Card>>> comboMap95Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap70Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap50Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap35Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap20Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap10Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap5Percent = new HashMap<>();

    static {
        PreflopRangeBuilderUtil p = new PreflopRangeBuilderUtil();

        comboMap95Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
        comboMap95Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 13));
        comboMap95Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 12));
        comboMap95Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 13));
        comboMap95Percent.put(5, p.getPocketPairCombosOfGivenRank(13));
        comboMap95Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 12));
        comboMap95Percent.put(7, p.getPocketPairCombosOfGivenRank(12));
        comboMap95Percent.put(8, p.getSuitedCombosOfGivenRanks(14, 11));
        comboMap95Percent.put(9, p.getPocketPairCombosOfGivenRank(11));
        comboMap95Percent.put(10, p.getPocketPairCombosOfGivenRank(10));

        comboMap70Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 11));
        comboMap70Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 12));
        comboMap70Percent.put(3, p.getSuitedCombosOfGivenRanks(13, 12));
        comboMap70Percent.put(4, p.getSuitedCombosOfGivenRanks(13, 11));
        comboMap70Percent.put(5, p.getSuitedCombosOfGivenRanks(12, 11));
        comboMap70Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 10));
        comboMap70Percent.put(7, p.getSuitedCombosOfGivenRanks(13, 10));
        comboMap70Percent.put(8, p.getSuitedCombosOfGivenRanks(12, 10));
        comboMap70Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 10));
        comboMap95Percent.put(10, p.getPocketPairCombosOfGivenRank(9));
        comboMap95Percent.put(10, p.getPocketPairCombosOfGivenRank(8));

        comboMap50Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 10));
        comboMap50Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 9));
        comboMap50Percent.put(3, p.getOffSuitCombosOfGivenRanks(13, 11));
        comboMap50Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 9));
        comboMap50Percent.put(5, p.getSuitedCombosOfGivenRanks(12, 9));
        comboMap50Percent.put(6, p.getSuitedCombosOfGivenRanks(11, 9));
        comboMap50Percent.put(7, p.getSuitedCombosOfGivenRanks(10, 9));
        comboMap50Percent.put(8, p.getSuitedCombosOfGivenRanks(14, 8));
        comboMap50Percent.put(9, p.getSuitedCombosOfGivenRanks(10, 8));
        comboMap50Percent.put(10, p.getSuitedCombosOfGivenRanks(9, 8));
        comboMap50Percent.put(11, p.getSuitedCombosOfGivenRanks(14, 7));
        comboMap50Percent.put(12, p.getSuitedCombosOfGivenRanks(8, 7));
        comboMap95Percent.put(13, p.getPocketPairCombosOfGivenRank(7));
        comboMap50Percent.put(14, p.getSuitedCombosOfGivenRanks(7, 6));
        comboMap50Percent.put(15, p.getSuitedCombosOfGivenRanks(14, 5));
        comboMap50Percent.put(16, p.getSuitedCombosOfGivenRanks(6, 5));

        comboMap35Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 8));
        comboMap35Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 10));
        comboMap35Percent.put(3, p.getOffSuitCombosOfGivenRanks(12, 11));
        comboMap35Percent.put(4, p.getOffSuitCombosOfGivenRanks(12, 10));
        comboMap35Percent.put(4, p.getOffSuitCombosOfGivenRanks(11, 10));

    }

}
