package com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop;

import com.lennart.model.card.Card;
import com.lennart.model.action.actionbuilders.ActionBuilderUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lpo10346 on 10/25/2016.
 */
public class Call4bet {

    private Map<Integer, Map<Integer, Set<Card>>> comboMap100Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap5Percent = new HashMap<>();

    private ActionBuilderUtil p;

    public Call4bet(ActionBuilderUtil p) {
        this.p = p;

        comboMap100Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 12));
        comboMap100Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 11));
        comboMap100Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 10));
        comboMap100Percent.put(4, p.getOffSuitCombosOfGivenRanks(14, 9));
        comboMap100Percent.put(5, p.getOffSuitCombosOfGivenRanks(13, 12));
        comboMap100Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 12));
        comboMap100Percent.put(7, p.getSuitedCombosOfGivenRanks(13, 12));
        comboMap100Percent.put(8, p.getSuitedCombosOfGivenRanks(14, 11));
        comboMap100Percent.put(9, p.getSuitedCombosOfGivenRanks(13, 11));
        comboMap100Percent.put(10, p.getSuitedCombosOfGivenRanks(12, 11));
        comboMap100Percent.put(11, p.getSuitedCombosOfGivenRanks(14, 10));
        comboMap100Percent.put(12, p.getSuitedCombosOfGivenRanks(13, 10));
        comboMap100Percent.put(13, p.getSuitedCombosOfGivenRanks(12, 10));
        comboMap100Percent.put(14, p.getPocketPairCombosOfGivenRank(10));
        comboMap100Percent.put(15, p.getSuitedCombosOfGivenRanks(14, 9));
        comboMap100Percent.put(16, p.getSuitedCombosOfGivenRanks(14, 8));
        comboMap100Percent.put(17, p.getSuitedCombosOfGivenRanks(14, 7));
        comboMap100Percent.put(18, p.getSuitedCombosOfGivenRanks(14, 5));
        comboMap100Percent.put(19, p.getPocketPairCombosOfGivenRank(9));
        comboMap100Percent.put(20, p.getPocketPairCombosOfGivenRank(8));
        comboMap100Percent.put(21, p.getPocketPairCombosOfGivenRank(7));
        comboMap100Percent.put(22, p.getSuitedCombosOfGivenRanks(12, 9));
        comboMap100Percent.put(23, p.getOffSuitCombosOfGivenRanks(13, 11));
        comboMap100Percent.put(24, p.getOffSuitCombosOfGivenRanks(11, 10));
        comboMap100Percent.put(25, p.getSuitedCombosOfGivenRanks(11, 8));
        comboMap100Percent.put(26, p.getSuitedCombosOfGivenRanks(10, 7));
        comboMap100Percent.put(27, p.getSuitedCombosOfGivenRanks(9, 6));
        comboMap100Percent.put(28, p.getSuitedCombosOfGivenRanks(8, 5));
        comboMap100Percent.put(29, p.getSuitedCombosOfGivenRanks(14, 4));
        comboMap100Percent.put(30, p.getSuitedCombosOfGivenRanks(7, 4));
        comboMap100Percent.put(31, p.getSuitedCombosOfGivenRanks(14, 3));
        comboMap100Percent.put(32, p.getSuitedCombosOfGivenRanks(6, 3));
        comboMap100Percent.put(33, p.getSuitedCombosOfGivenRanks(14, 2));
        comboMap100Percent.put(34, p.getSuitedCombosOfGivenRanks(5, 2));
        comboMap100Percent.put(35, p.getPocketPairCombosOfGivenRank(11));
        comboMap100Percent.put(36, p.getSuitedCombosOfGivenRanks(11, 10));
        comboMap100Percent.put(37, p.getOffSuitCombosOfGivenRanks(10, 9));
        comboMap100Percent.put(38, p.getOffSuitCombosOfGivenRanks(10, 8));
        comboMap100Percent.put(39, p.getSuitedCombosOfGivenRanks(11, 9));
        comboMap100Percent.put(40, p.getSuitedCombosOfGivenRanks(10, 9));
        comboMap100Percent.put(41, p.getOffSuitCombosOfGivenRanks(9, 8));
        comboMap100Percent.put(42, p.getSuitedCombosOfGivenRanks(12, 8));
        comboMap100Percent.put(43, p.getSuitedCombosOfGivenRanks(10, 8));
        comboMap100Percent.put(44, p.getSuitedCombosOfGivenRanks(9, 8));
        comboMap100Percent.put(45, p.getOffSuitCombosOfGivenRanks(8, 7));
        comboMap100Percent.put(46, p.getSuitedCombosOfGivenRanks(9, 7));
        comboMap100Percent.put(47, p.getSuitedCombosOfGivenRanks(8, 7));
        comboMap100Percent.put(48, p.getOffSuitCombosOfGivenRanks(7, 6));
        comboMap100Percent.put(49, p.getSuitedCombosOfGivenRanks(10, 6));
        comboMap100Percent.put(50, p.getSuitedCombosOfGivenRanks(8, 6));
        comboMap100Percent.put(51, p.getSuitedCombosOfGivenRanks(7, 6));
        comboMap100Percent.put(52, p.getPocketPairCombosOfGivenRank(6));
        comboMap100Percent.put(53, p.getPocketPairCombosOfGivenRank(5));
        comboMap100Percent.put(54, p.getPocketPairCombosOfGivenRank(4));
        comboMap100Percent.put(55, p.getPocketPairCombosOfGivenRank(3));
        comboMap100Percent.put(56, p.getPocketPairCombosOfGivenRank(2));
        comboMap100Percent.put(57, p.getSuitedCombosOfGivenRanks(9, 5));
        comboMap100Percent.put(58, p.getSuitedCombosOfGivenRanks(7, 5));
        comboMap100Percent.put(59, p.getSuitedCombosOfGivenRanks(6, 5));
        comboMap100Percent.put(60, p.getSuitedCombosOfGivenRanks(8, 4));
        comboMap100Percent.put(61, p.getSuitedCombosOfGivenRanks(6, 4));
        comboMap100Percent.put(62, p.getSuitedCombosOfGivenRanks(5, 4));
        comboMap100Percent.put(63, p.getSuitedCombosOfGivenRanks(7, 3));
        comboMap100Percent.put(64, p.getSuitedCombosOfGivenRanks(5, 3));
        comboMap100Percent.put(65, p.getSuitedCombosOfGivenRanks(4, 3));
        comboMap100Percent.put(66, p.getSuitedCombosOfGivenRanks(4, 2));
        comboMap100Percent.put(67, p.getSuitedCombosOfGivenRanks(3, 2));

        comboMap5Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
        comboMap5Percent.put(2, p.getPocketPairCombosOfGivenRank(13));
        comboMap5Percent.put(3, p.getPocketPairCombosOfGivenRank(12));
        comboMap5Percent.put(4, p.getOffSuitCombosOfGivenRanks(14, 13));
        comboMap5Percent.put(5, p.getSuitedCombosOfGivenRanks(14, 13));
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap100Percent() {
        return comboMap100Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap5Percent() {
        return comboMap5Percent;
    }
}
