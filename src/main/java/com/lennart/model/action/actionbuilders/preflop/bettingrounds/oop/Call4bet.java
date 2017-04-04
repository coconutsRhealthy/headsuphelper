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
    private Map<Integer, Map<Integer, Set<Card>>> comboMap80Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap70Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap62Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap60Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap50Percent = new HashMap<>();
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

        comboMap80Percent.put(1, p.getSuitedCombosOfGivenRanks(12, 9));

        comboMap70Percent.put(1, p.getOffSuitCombosOfGivenRanks(13, 11));
        comboMap70Percent.put(2, p.getOffSuitCombosOfGivenRanks(11, 10));

        comboMap62Percent.put(1, p.getSuitedCombosOfGivenRanks(11, 8));

        comboMap60Percent.put(1, p.getSuitedCombosOfGivenRanks(10, 7));
        comboMap60Percent.put(2, p.getSuitedCombosOfGivenRanks(9, 6));
        comboMap60Percent.put(3, p.getSuitedCombosOfGivenRanks(8, 5));
        comboMap60Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 4));
        comboMap60Percent.put(5, p.getSuitedCombosOfGivenRanks(7, 4));
        comboMap60Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 3));
        comboMap60Percent.put(7, p.getSuitedCombosOfGivenRanks(6, 3));
        comboMap60Percent.put(8, p.getSuitedCombosOfGivenRanks(14, 2));
        comboMap60Percent.put(9, p.getSuitedCombosOfGivenRanks(5, 2));

        comboMap50Percent.put(1, p.getPocketPairCombosOfGivenRank(11));
        comboMap50Percent.put(2, p.getSuitedCombosOfGivenRanks(11, 10));
        comboMap50Percent.put(3, p.getOffSuitCombosOfGivenRanks(10, 9));
        comboMap50Percent.put(4, p.getOffSuitCombosOfGivenRanks(10, 8));
        comboMap50Percent.put(5, p.getSuitedCombosOfGivenRanks(11, 9));
        comboMap50Percent.put(6, p.getSuitedCombosOfGivenRanks(10, 9));
        comboMap50Percent.put(7, p.getOffSuitCombosOfGivenRanks(9, 8));
        comboMap50Percent.put(8, p.getSuitedCombosOfGivenRanks(12, 8));
        comboMap50Percent.put(9, p.getSuitedCombosOfGivenRanks(10, 8));
        comboMap50Percent.put(10, p.getSuitedCombosOfGivenRanks(9, 8));
        comboMap50Percent.put(11, p.getOffSuitCombosOfGivenRanks(8, 7));
        comboMap50Percent.put(12, p.getSuitedCombosOfGivenRanks(9, 7));
        comboMap50Percent.put(13, p.getSuitedCombosOfGivenRanks(8, 7));
        comboMap50Percent.put(14, p.getOffSuitCombosOfGivenRanks(7, 6));
        comboMap50Percent.put(15, p.getSuitedCombosOfGivenRanks(10, 6));
        comboMap50Percent.put(16, p.getSuitedCombosOfGivenRanks(8, 6));
        comboMap50Percent.put(17, p.getSuitedCombosOfGivenRanks(7, 6));
        comboMap50Percent.put(18, p.getPocketPairCombosOfGivenRank(6));
        comboMap50Percent.put(19, p.getPocketPairCombosOfGivenRank(5));
        comboMap50Percent.put(20, p.getPocketPairCombosOfGivenRank(4));
        comboMap50Percent.put(21, p.getPocketPairCombosOfGivenRank(3));
        comboMap50Percent.put(22, p.getPocketPairCombosOfGivenRank(2));
        comboMap50Percent.put(23, p.getSuitedCombosOfGivenRanks(9, 5));
        comboMap50Percent.put(24, p.getSuitedCombosOfGivenRanks(7, 5));
        comboMap50Percent.put(25, p.getSuitedCombosOfGivenRanks(6, 5));
        comboMap50Percent.put(26, p.getSuitedCombosOfGivenRanks(8, 4));
        comboMap50Percent.put(27, p.getSuitedCombosOfGivenRanks(6, 4));
        comboMap50Percent.put(28, p.getSuitedCombosOfGivenRanks(5, 4));
        comboMap50Percent.put(29, p.getSuitedCombosOfGivenRanks(7, 3));
        comboMap50Percent.put(30, p.getSuitedCombosOfGivenRanks(5, 3));
        comboMap50Percent.put(31, p.getSuitedCombosOfGivenRanks(4, 3));
        comboMap50Percent.put(32, p.getSuitedCombosOfGivenRanks(4, 2));
        comboMap50Percent.put(33, p.getSuitedCombosOfGivenRanks(3, 2));

        comboMap5Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
        comboMap5Percent.put(2, p.getPocketPairCombosOfGivenRank(13));
        comboMap5Percent.put(3, p.getPocketPairCombosOfGivenRank(12));
        comboMap5Percent.put(4, p.getOffSuitCombosOfGivenRanks(14, 13));
        comboMap5Percent.put(5, p.getSuitedCombosOfGivenRanks(14, 13));
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap100Percent() {
        return comboMap100Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap80Percent() {
        return comboMap80Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap70Percent() {
        return comboMap70Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap62Percent() {
        return comboMap62Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap60Percent() {
        return comboMap60Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap50Percent() {
        return comboMap50Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap5Percent() {
        return comboMap5Percent;
    }
}
