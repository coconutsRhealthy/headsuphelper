package com.lennart.model.rangebuilder.preflop.ip;

import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lennart on 24-10-16.
 */
public class Call3betRangeBuilder {

    private static Map<Integer, Map<Integer, Set<Card>>> comboMap100Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap94Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap89Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap80Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap73Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap50Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap34Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap29Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap27Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap19Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap8Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap7Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap5Percent = new HashMap<>();

    static {
        PreflopRangeBuilderUtil p = new PreflopRangeBuilderUtil();

        comboMap100Percent.put(1, p.getSuitedCombosOfGivenRanks(4, 3));
        comboMap100Percent.put(2, p.getSuitedCombosOfGivenRanks(4, 2));
        comboMap100Percent.put(3, p.getSuitedCombosOfGivenRanks(3, 2));

        comboMap94Percent.put(1, p.getSuitedCombosOfGivenRanks(6, 4));
        comboMap94Percent.put(2, p.getSuitedCombosOfGivenRanks(5, 3));

        comboMap89Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 10));
        comboMap89Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 12));
        comboMap89Percent.put(3, p.getOffSuitCombosOfGivenRanks(13, 11));
        comboMap89Percent.put(4, p.getOffSuitCombosOfGivenRanks(12, 11));
        comboMap89Percent.put(5, p.getOffSuitCombosOfGivenRanks(12, 10));
        comboMap89Percent.put(6, p.getOffSuitCombosOfGivenRanks(11, 10));
        comboMap89Percent.put(7, p.getSuitedCombosOfGivenRanks(13, 9));
        comboMap89Percent.put(8, p.getSuitedCombosOfGivenRanks(12, 9));
        comboMap89Percent.put(9, p.getSuitedCombosOfGivenRanks(13, 8));
        comboMap89Percent.put(10, p.getSuitedCombosOfGivenRanks(12, 8));
        comboMap89Percent.put(11, p.getSuitedCombosOfGivenRanks(11, 8));

        comboMap80Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 11));
        comboMap80Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 9));
        comboMap80Percent.put(3, p.getSuitedCombosOfGivenRanks(13, 12));
        comboMap80Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 11));
        comboMap80Percent.put(5, p.getSuitedCombosOfGivenRanks(13, 11));
        comboMap80Percent.put(6, p.getSuitedCombosOfGivenRanks(12, 11));
        comboMap80Percent.put(7, p.getSuitedCombosOfGivenRanks(14, 10));
        comboMap80Percent.put(8, p.getSuitedCombosOfGivenRanks(13, 10));
        comboMap80Percent.put(9, p.getSuitedCombosOfGivenRanks(12, 10));
        comboMap80Percent.put(10, p.getSuitedCombosOfGivenRanks(11, 10));
        comboMap80Percent.put(11, p.getSuitedCombosOfGivenRanks(14, 9));
        comboMap80Percent.put(12, p.getSuitedCombosOfGivenRanks(11, 9));
        comboMap80Percent.put(13, p.getSuitedCombosOfGivenRanks(10, 9));
        comboMap80Percent.put(14, p.getSuitedCombosOfGivenRanks(14, 8));
        comboMap80Percent.put(15, p.getSuitedCombosOfGivenRanks(10, 8));
        comboMap80Percent.put(16, p.getSuitedCombosOfGivenRanks(9, 8));
        comboMap80Percent.put(17, p.getSuitedCombosOfGivenRanks(14, 7));
        comboMap80Percent.put(18, p.getSuitedCombosOfGivenRanks(9, 7));
        comboMap80Percent.put(19, p.getSuitedCombosOfGivenRanks(8, 7));
        comboMap80Percent.put(20, p.getPocketPairCombosOfGivenRank(7));
        comboMap80Percent.put(21, p.getSuitedCombosOfGivenRanks(14, 6));
        comboMap80Percent.put(22, p.getSuitedCombosOfGivenRanks(8, 6));
        comboMap80Percent.put(23, p.getSuitedCombosOfGivenRanks(7, 6));
        comboMap80Percent.put(24, p.getPocketPairCombosOfGivenRank(6));
        comboMap80Percent.put(25, p.getSuitedCombosOfGivenRanks(14, 5));
        comboMap80Percent.put(26, p.getSuitedCombosOfGivenRanks(7, 5));
        comboMap80Percent.put(27, p.getSuitedCombosOfGivenRanks(6, 5));
        comboMap80Percent.put(28, p.getSuitedCombosOfGivenRanks(14, 4));
        comboMap80Percent.put(29, p.getSuitedCombosOfGivenRanks(5, 4));
        comboMap80Percent.put(30, p.getSuitedCombosOfGivenRanks(14, 3));
        comboMap80Percent.put(31, p.getSuitedCombosOfGivenRanks(14, 2));





    }


}
