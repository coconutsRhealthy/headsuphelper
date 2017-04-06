package com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop;

import com.lennart.model.card.Card;
import com.lennart.model.action.actionbuilders.ActionBuilderUtil;

import java.util.*;

/**
 * Created by lpo10346 on 10/21/2016.
 */
public class Call2bet {

    private Map<Integer, Map<Integer, Set<Card>>> comboMap85Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap70Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap50Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap100Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap10Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap5Percent = new HashMap<>();

    private ActionBuilderUtil p;

    public Call2bet(ActionBuilderUtil p) {
        this.p = p;

        comboMap85Percent.put(1, p.getPocketPairCombosOfGivenRank(3));
        comboMap85Percent.put(2, p.getPocketPairCombosOfGivenRank(2));

        comboMap70Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 7));
        comboMap70Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 6));
        comboMap70Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 5));
        comboMap70Percent.put(4, p.getOffSuitCombosOfGivenRanks(14, 4));
        comboMap70Percent.put(5, p.getOffSuitCombosOfGivenRanks(14, 3));
        comboMap70Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 2));
        comboMap70Percent.put(7, p.getOffSuitCombosOfGivenRanks(13, 9));
        comboMap70Percent.put(8, p.getOffSuitCombosOfGivenRanks(13, 8));
        comboMap70Percent.put(9, p.getOffSuitCombosOfGivenRanks(12, 9));
        comboMap70Percent.put(10, p.getOffSuitCombosOfGivenRanks(12, 8));
        comboMap70Percent.put(11, p.getOffSuitCombosOfGivenRanks(11, 9));
        comboMap70Percent.put(12, p.getOffSuitCombosOfGivenRanks(11, 8));
        comboMap70Percent.put(13, p.getOffSuitCombosOfGivenRanks(10, 9));
        comboMap70Percent.put(14, p.getOffSuitCombosOfGivenRanks(10, 8));
        comboMap70Percent.put(15, p.getOffSuitCombosOfGivenRanks(9, 8));
        comboMap70Percent.put(16, p.getSuitedCombosOfGivenRanks(13, 6));
        comboMap70Percent.put(17, p.getSuitedCombosOfGivenRanks(12, 6));
        comboMap70Percent.put(18, p.getSuitedCombosOfGivenRanks(13, 5));
        comboMap70Percent.put(19, p.getSuitedCombosOfGivenRanks(12, 5));
        comboMap70Percent.put(20, p.getPocketPairCombosOfGivenRank(4));

        comboMap50Percent.put(1, p.getSuitedCombosOfGivenRanks(10, 6));
        comboMap50Percent.put(2, p.getSuitedCombosOfGivenRanks(9, 5));
        comboMap50Percent.put(3, p.getSuitedCombosOfGivenRanks(8, 4));
        comboMap50Percent.put(4, p.getSuitedCombosOfGivenRanks(7, 3));
        comboMap50Percent.put(5, p.getOffSuitCombosOfGivenRanks(14, 8));
        comboMap50Percent.put(6, p.getOffSuitCombosOfGivenRanks(13, 10));
        comboMap50Percent.put(7, p.getOffSuitCombosOfGivenRanks(12, 11));
        comboMap50Percent.put(8, p.getOffSuitCombosOfGivenRanks(12, 10));
        comboMap50Percent.put(9, p.getOffSuitCombosOfGivenRanks(11, 10));
        comboMap50Percent.put(10, p.getSuitedCombosOfGivenRanks(13, 8));
        comboMap50Percent.put(11, p.getSuitedCombosOfGivenRanks(12, 8));
        comboMap50Percent.put(12, p.getSuitedCombosOfGivenRanks(13, 7));
        comboMap50Percent.put(13, p.getSuitedCombosOfGivenRanks(12, 7));
        comboMap50Percent.put(14, p.getPocketPairCombosOfGivenRank(5));
        comboMap50Percent.put(15, p.getSuitedCombosOfGivenRanks(14, 6));
        comboMap50Percent.put(16, p.getPocketPairCombosOfGivenRank(6));

        comboMap100Percent.put(1, p.getOffSuitCombosOfGivenRanks(13, 7));
        comboMap100Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 6));
        comboMap100Percent.put(3, p.getOffSuitCombosOfGivenRanks(9, 7));
        comboMap100Percent.put(4, p.getOffSuitCombosOfGivenRanks(8, 7));
        comboMap100Percent.put(5, p.getOffSuitCombosOfGivenRanks(8, 6));
        comboMap100Percent.put(6, p.getOffSuitCombosOfGivenRanks(7, 6));
        comboMap100Percent.put(7, p.getSuitedCombosOfGivenRanks(11, 6));
        comboMap100Percent.put(8, p.getOffSuitCombosOfGivenRanks(6, 5));
        comboMap100Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 5));
        comboMap100Percent.put(10, p.getOffSuitCombosOfGivenRanks(5, 4));
        comboMap100Percent.put(11, p.getSuitedCombosOfGivenRanks(13, 4));
        comboMap100Percent.put(12, p.getSuitedCombosOfGivenRanks(12, 4));
        comboMap100Percent.put(13, p.getSuitedCombosOfGivenRanks(13, 3));
        comboMap100Percent.put(14, p.getSuitedCombosOfGivenRanks(12, 3));
        comboMap100Percent.put(15, p.getSuitedCombosOfGivenRanks(13, 2));
        comboMap100Percent.put(16, p.getSuitedCombosOfGivenRanks(12, 2));
        comboMap100Percent.put(17, p.getSuitedCombosOfGivenRanks(6, 2));

        comboMap10Percent.put(1, p.getSuitedCombosOfGivenRanks(5, 2));
        comboMap10Percent.put(2, p.getSuitedCombosOfGivenRanks(4, 2));
        comboMap10Percent.put(3, p.getSuitedCombosOfGivenRanks(3, 2));
        comboMap10Percent.put(4, p.getSuitedCombosOfGivenRanks(7, 4));
        comboMap10Percent.put(5, p.getSuitedCombosOfGivenRanks(6, 3));
        comboMap10Percent.put(6, p.getSuitedCombosOfGivenRanks(9, 6));
        comboMap10Percent.put(7, p.getSuitedCombosOfGivenRanks(8, 5));
        comboMap10Percent.put(8, p.getSuitedCombosOfGivenRanks(6, 4));
        comboMap10Percent.put(9, p.getSuitedCombosOfGivenRanks(5, 3));
        comboMap10Percent.put(10, p.getSuitedCombosOfGivenRanks(13, 9));
        comboMap10Percent.put(11, p.getSuitedCombosOfGivenRanks(11, 8));
        comboMap10Percent.put(12, p.getSuitedCombosOfGivenRanks(11, 7));
        comboMap10Percent.put(13, p.getSuitedCombosOfGivenRanks(10, 7));
        comboMap10Percent.put(14, p.getSuitedCombosOfGivenRanks(9, 7));
        comboMap10Percent.put(15, p.getSuitedCombosOfGivenRanks(8, 6));
        comboMap10Percent.put(16, p.getSuitedCombosOfGivenRanks(7, 5));
        comboMap10Percent.put(17, p.getSuitedCombosOfGivenRanks(14, 4));
        comboMap10Percent.put(18, p.getSuitedCombosOfGivenRanks(5, 4));
        comboMap10Percent.put(19, p.getSuitedCombosOfGivenRanks(14, 3));
        comboMap10Percent.put(20, p.getSuitedCombosOfGivenRanks(4, 3));
        comboMap10Percent.put(21, p.getSuitedCombosOfGivenRanks(14, 2));
        comboMap10Percent.put(22, p.getOffSuitCombosOfGivenRanks(14, 10));
        comboMap10Percent.put(23, p.getOffSuitCombosOfGivenRanks(14, 9));
        comboMap10Percent.put(24, p.getOffSuitCombosOfGivenRanks(13, 11));
        comboMap10Percent.put(25, p.getSuitedCombosOfGivenRanks(14, 9));
        comboMap10Percent.put(26, p.getSuitedCombosOfGivenRanks(12, 9));
        comboMap10Percent.put(27, p.getSuitedCombosOfGivenRanks(11, 9));
        comboMap10Percent.put(28, p.getSuitedCombosOfGivenRanks(10, 9));
        comboMap10Percent.put(29, p.getSuitedCombosOfGivenRanks(14, 8));
        comboMap10Percent.put(30, p.getSuitedCombosOfGivenRanks(10, 8));
        comboMap10Percent.put(31, p.getSuitedCombosOfGivenRanks(9, 8));
        comboMap10Percent.put(32, p.getSuitedCombosOfGivenRanks(14, 7));
        comboMap10Percent.put(33, p.getSuitedCombosOfGivenRanks(8, 7));
        comboMap10Percent.put(34, p.getPocketPairCombosOfGivenRank(7));
        comboMap10Percent.put(35, p.getSuitedCombosOfGivenRanks(7, 6));
        comboMap10Percent.put(36, p.getSuitedCombosOfGivenRanks(14, 5));
        comboMap10Percent.put(37, p.getSuitedCombosOfGivenRanks(6, 5));
        comboMap10Percent.put(38, p.getOffSuitCombosOfGivenRanks(14, 11));
        comboMap10Percent.put(39, p.getOffSuitCombosOfGivenRanks(13, 12));
        comboMap10Percent.put(40, p.getSuitedCombosOfGivenRanks(13, 12));
        comboMap10Percent.put(41, p.getSuitedCombosOfGivenRanks(13, 11));
        comboMap10Percent.put(42, p.getSuitedCombosOfGivenRanks(12, 11));
        comboMap10Percent.put(43, p.getSuitedCombosOfGivenRanks(14, 10));
        comboMap10Percent.put(44, p.getSuitedCombosOfGivenRanks(13, 10));
        comboMap10Percent.put(45, p.getSuitedCombosOfGivenRanks(12, 10));
        comboMap10Percent.put(46, p.getSuitedCombosOfGivenRanks(11, 10));
        comboMap10Percent.put(47, p.getPocketPairCombosOfGivenRank(9));
        comboMap10Percent.put(48, p.getPocketPairCombosOfGivenRank(8));
        comboMap10Percent.put(49, p.getOffSuitCombosOfGivenRanks(13, 5));
        comboMap10Percent.put(50, p.getOffSuitCombosOfGivenRanks(13, 4));
        comboMap10Percent.put(51, p.getOffSuitCombosOfGivenRanks(13, 3));
        comboMap10Percent.put(52, p.getOffSuitCombosOfGivenRanks(13, 2));
        comboMap10Percent.put(53, p.getOffSuitCombosOfGivenRanks(12, 7));
        comboMap10Percent.put(54, p.getOffSuitCombosOfGivenRanks(10, 7));
        comboMap10Percent.put(55, p.getOffSuitCombosOfGivenRanks(9, 6));
        comboMap10Percent.put(56, p.getOffSuitCombosOfGivenRanks(8, 5));
        comboMap10Percent.put(57, p.getOffSuitCombosOfGivenRanks(7, 5));
        comboMap10Percent.put(58, p.getOffSuitCombosOfGivenRanks(7, 4));
        comboMap10Percent.put(59, p.getOffSuitCombosOfGivenRanks(6, 4));
        comboMap10Percent.put(60, p.getOffSuitCombosOfGivenRanks(6, 3));
        comboMap10Percent.put(61, p.getSuitedCombosOfGivenRanks(10, 5));
        comboMap10Percent.put(62, p.getOffSuitCombosOfGivenRanks(5, 3));
        comboMap10Percent.put(63, p.getSuitedCombosOfGivenRanks(11, 4));
        comboMap10Percent.put(64, p.getSuitedCombosOfGivenRanks(10, 4));
        comboMap10Percent.put(65, p.getSuitedCombosOfGivenRanks(9, 4));
        comboMap10Percent.put(66, p.getOffSuitCombosOfGivenRanks(4, 3));
        comboMap10Percent.put(67, p.getSuitedCombosOfGivenRanks(11, 3));
        comboMap10Percent.put(68, p.getSuitedCombosOfGivenRanks(10, 3));
        comboMap10Percent.put(69, p.getSuitedCombosOfGivenRanks(11, 2));

        comboMap5Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
        comboMap5Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 13));
        comboMap5Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 12));
        comboMap5Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 13));
        comboMap5Percent.put(5, p.getPocketPairCombosOfGivenRank(13));
        comboMap5Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 12));
        comboMap5Percent.put(7, p.getPocketPairCombosOfGivenRank(12));
        comboMap5Percent.put(8, p.getSuitedCombosOfGivenRanks(14, 11));
        comboMap5Percent.put(9, p.getPocketPairCombosOfGivenRank(11));
        comboMap5Percent.put(10, p.getPocketPairCombosOfGivenRank(10));
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap85Percent() {
        return comboMap85Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap70Percent() {
        return comboMap70Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap50Percent() {
        return comboMap50Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap100Percent() {
        return comboMap100Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap10Percent() {
        return comboMap10Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap5Percent() {
        return comboMap5Percent;
    }
}
