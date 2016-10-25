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

        comboMap50Percent.put(1, p.getPocketPairCombosOfGivenRank(10));
        comboMap50Percent.put(2, p.getPocketPairCombosOfGivenRank(9));
        comboMap50Percent.put(3, p.getPocketPairCombosOfGivenRank(8));

        comboMap20Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 11));
        comboMap20Percent.put(2, p.getSuitedCombosOfGivenRanks(13, 12));
        comboMap20Percent.put(3, p.getSuitedCombosOfGivenRanks(14, 11));
        comboMap20Percent.put(4, p.getSuitedCombosOfGivenRanks(13, 11));
        comboMap20Percent.put(5, p.getSuitedCombosOfGivenRanks(12, 11));
        comboMap20Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 10));
        comboMap20Percent.put(7, p.getSuitedCombosOfGivenRanks(13, 10));
        comboMap20Percent.put(8, p.getSuitedCombosOfGivenRanks(12, 10));
        comboMap20Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 10));
        comboMap20Percent.put(10, p.getSuitedCombosOfGivenRanks(14, 9));
        comboMap20Percent.put(11, p.getSuitedCombosOfGivenRanks(11, 9));
        comboMap20Percent.put(12, p.getSuitedCombosOfGivenRanks(10, 9));
        comboMap20Percent.put(13, p.getSuitedCombosOfGivenRanks(14, 8));
        comboMap20Percent.put(14, p.getSuitedCombosOfGivenRanks(10, 8));
        comboMap20Percent.put(15, p.getSuitedCombosOfGivenRanks(9, 8));
        comboMap20Percent.put(16, p.getSuitedCombosOfGivenRanks(14, 7));
        comboMap20Percent.put(17, p.getSuitedCombosOfGivenRanks(9, 7));
        comboMap20Percent.put(18, p.getSuitedCombosOfGivenRanks(8, 7));
        comboMap20Percent.put(19, p.getPocketPairCombosOfGivenRank(7));
        comboMap20Percent.put(20, p.getSuitedCombosOfGivenRanks(14, 6));
        comboMap20Percent.put(21, p.getSuitedCombosOfGivenRanks(8, 6));
        comboMap20Percent.put(22, p.getSuitedCombosOfGivenRanks(7, 6));
        comboMap20Percent.put(23, p.getPocketPairCombosOfGivenRank(6));
        comboMap20Percent.put(24, p.getSuitedCombosOfGivenRanks(14, 5));
        comboMap20Percent.put(25, p.getSuitedCombosOfGivenRanks(7, 5));
        comboMap20Percent.put(26, p.getSuitedCombosOfGivenRanks(6, 5));
        comboMap20Percent.put(27, p.getPocketPairCombosOfGivenRank(5));
        comboMap20Percent.put(28, p.getSuitedCombosOfGivenRanks(14, 4));
        comboMap20Percent.put(29, p.getSuitedCombosOfGivenRanks(5, 4));
        comboMap20Percent.put(30, p.getPocketPairCombosOfGivenRank(4));
        comboMap20Percent.put(31, p.getSuitedCombosOfGivenRanks(14, 3));
        comboMap20Percent.put(32, p.getSuitedCombosOfGivenRanks(14, 2));

        comboMap11Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 10));
        comboMap11Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 9));
        comboMap11Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 8));
        comboMap11Percent.put(4, p.getOffSuitCombosOfGivenRanks(14, 7));
        comboMap11Percent.put(5, p.getOffSuitCombosOfGivenRanks(14, 6));
        comboMap11Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 5));
        comboMap11Percent.put(7, p.getOffSuitCombosOfGivenRanks(14, 4));
        comboMap11Percent.put(8, p.getOffSuitCombosOfGivenRanks(14, 3));
        comboMap11Percent.put(9, p.getOffSuitCombosOfGivenRanks(14, 2));
        comboMap11Percent.put(10, p.getOffSuitCombosOfGivenRanks(13, 12));
        comboMap11Percent.put(11, p.getOffSuitCombosOfGivenRanks(13, 11));
        comboMap11Percent.put(12, p.getOffSuitCombosOfGivenRanks(13, 10));
        comboMap11Percent.put(13, p.getOffSuitCombosOfGivenRanks(12, 11));
        comboMap11Percent.put(14, p.getOffSuitCombosOfGivenRanks(12, 10));
        comboMap11Percent.put(15, p.getOffSuitCombosOfGivenRanks(11, 10));
        comboMap11Percent.put(16, p.getOffSuitCombosOfGivenRanks(11, 9));
        comboMap11Percent.put(17, p.getOffSuitCombosOfGivenRanks(10, 9));
        comboMap11Percent.put(18, p.getSuitedCombosOfGivenRanks(13, 9));
        comboMap11Percent.put(19, p.getSuitedCombosOfGivenRanks(12, 9));
        comboMap11Percent.put(20, p.getSuitedCombosOfGivenRanks(13, 8));
        comboMap11Percent.put(21, p.getSuitedCombosOfGivenRanks(12, 8));
        comboMap11Percent.put(22, p.getSuitedCombosOfGivenRanks(11, 8));
        comboMap11Percent.put(23, p.getSuitedCombosOfGivenRanks(13, 7));
        comboMap11Percent.put(24, p.getSuitedCombosOfGivenRanks(13, 6));
        comboMap11Percent.put(25, p.getSuitedCombosOfGivenRanks(13, 5));
        comboMap11Percent.put(26, p.getSuitedCombosOfGivenRanks(13, 4));
        comboMap11Percent.put(27, p.getSuitedCombosOfGivenRanks(13, 3));
        comboMap11Percent.put(28, p.getSuitedCombosOfGivenRanks(13, 2));

        comboMap6Percent.put(1, p.getOffSuitCombosOfGivenRanks(13, 9));
        comboMap6Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 8));
        comboMap6Percent.put(3, p.getOffSuitCombosOfGivenRanks(13, 7));
        comboMap6Percent.put(4, p.getOffSuitCombosOfGivenRanks(13, 6));
        comboMap6Percent.put(5, p.getOffSuitCombosOfGivenRanks(12, 9));
        comboMap6Percent.put(6, p.getOffSuitCombosOfGivenRanks(9, 8));
        comboMap6Percent.put(7, p.getOffSuitCombosOfGivenRanks(8, 7));
        comboMap6Percent.put(8, p.getSuitedCombosOfGivenRanks(12, 7));
        comboMap6Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 7));
        comboMap6Percent.put(10, p.getSuitedCombosOfGivenRanks(10, 7));
        comboMap6Percent.put(11, p.getOffSuitCombosOfGivenRanks(7, 6));
        comboMap6Percent.put(12, p.getSuitedCombosOfGivenRanks(12, 6));
        comboMap6Percent.put(13, p.getSuitedCombosOfGivenRanks(10, 6));
        comboMap6Percent.put(14, p.getSuitedCombosOfGivenRanks(9, 6));
        comboMap6Percent.put(15, p.getOffSuitCombosOfGivenRanks(6, 5));
        comboMap6Percent.put(16, p.getSuitedCombosOfGivenRanks(9, 5));
        comboMap6Percent.put(17, p.getSuitedCombosOfGivenRanks(8, 5));
        comboMap6Percent.put(18, p.getOffSuitCombosOfGivenRanks(5, 4));
        comboMap6Percent.put(19, p.getSuitedCombosOfGivenRanks(7, 4));
        comboMap6Percent.put(20, p.getSuitedCombosOfGivenRanks(6, 4));
        comboMap6Percent.put(21, p.getSuitedCombosOfGivenRanks(5, 3));
        comboMap6Percent.put(22, p.getPocketPairCombosOfGivenRank(3));
        comboMap6Percent.put(23, p.getPocketPairCombosOfGivenRank(2));
    }
}
