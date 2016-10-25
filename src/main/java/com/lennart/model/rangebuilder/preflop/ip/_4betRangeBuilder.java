package com.lennart.model.rangebuilder.preflop.ip;

import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lpo10346 on 10/25/2016.
 */
public class _4betRangeBuilder {

    private static Map<Integer, Map<Integer, Set<Card>>> comboMap95Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap50Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap20Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap11Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap6Percent = new HashMap<>();

    static {
        PreflopRangeBuilderUtil p = new PreflopRangeBuilderUtil();

        comboMap95Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
        comboMap95Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 13));
        comboMap95Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 12));
        comboMap95Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 13));
        comboMap95Percent.put(5, p.getPocketPairCombosOfGivenRank(13));
        comboMap95Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 12));
        comboMap95Percent.put(7, p.getPocketPairCombosOfGivenRank(12));
        comboMap95Percent.put(8, p.getPocketPairCombosOfGivenRank(11));

    }


}
