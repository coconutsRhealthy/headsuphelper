package com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop;

import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.card.Card;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by LPO21630 on 7-12-2016.
 */
public class _5bet {

    private Map<Integer, Map<Integer, Set<Card>>> comboMap95Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap50Percent = new HashMap<>();

    private ActionBuilderUtil p;

    public _5bet(ActionBuilderUtil p) {
        this.p = p;

        comboMap95Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
        comboMap95Percent.put(2, p.getPocketPairCombosOfGivenRank(13));
        comboMap95Percent.put(3, p.getPocketPairCombosOfGivenRank(12));
        comboMap95Percent.put(4, p.getPocketPairCombosOfGivenRank(11));
        comboMap95Percent.put(5, p.getSuitedCombosOfGivenRanks(14, 13));
        comboMap95Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 13));
        comboMap95Percent.put(7, p.getPocketPairCombosOfGivenRank(10));
        comboMap95Percent.put(8, p.getPocketPairCombosOfGivenRank(9));
        comboMap95Percent.put(9, p.getPocketPairCombosOfGivenRank(8));
        comboMap95Percent.put(10, p.getPocketPairCombosOfGivenRank(7));

        comboMap50Percent.put(1, p.getSuitedCombosOfGivenRanks(14, 12));
        comboMap50Percent.put(2, p.getSuitedCombosOfGivenRanks(14, 11));
        comboMap50Percent.put(3, p.getSuitedCombosOfGivenRanks(14, 10));
        comboMap50Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 9));
        comboMap50Percent.put(5, p.getSuitedCombosOfGivenRanks(14, 8));
        comboMap50Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 12));
        comboMap50Percent.put(7, p.getOffSuitCombosOfGivenRanks(14, 11));
        comboMap50Percent.put(8, p.getOffSuitCombosOfGivenRanks(14, 10));
        comboMap50Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 10));
        comboMap50Percent.put(10, p.getSuitedCombosOfGivenRanks(10, 9));
        comboMap50Percent.put(11, p.getSuitedCombosOfGivenRanks(9, 8));
        comboMap50Percent.put(12, p.getSuitedCombosOfGivenRanks(8, 7));
        comboMap50Percent.put(13, p.getSuitedCombosOfGivenRanks(7, 6));
        comboMap50Percent.put(14, p.getSuitedCombosOfGivenRanks(6, 5));
        comboMap50Percent.put(15, p.getSuitedCombosOfGivenRanks(5, 4));
        comboMap50Percent.put(16, p.getSuitedCombosOfGivenRanks(11, 9));
        comboMap50Percent.put(17, p.getSuitedCombosOfGivenRanks(10, 8));
        comboMap50Percent.put(18, p.getSuitedCombosOfGivenRanks(9, 7));
        comboMap50Percent.put(19, p.getSuitedCombosOfGivenRanks(8, 6));
        comboMap50Percent.put(20, p.getSuitedCombosOfGivenRanks(7, 5));
        comboMap50Percent.put(21, p.getSuitedCombosOfGivenRanks(6, 4));
        comboMap50Percent.put(22, p.getSuitedCombosOfGivenRanks(12, 9));
        comboMap50Percent.put(23, p.getSuitedCombosOfGivenRanks(11, 8));
        comboMap50Percent.put(24, p.getSuitedCombosOfGivenRanks(10, 7));
        comboMap50Percent.put(25, p.getSuitedCombosOfGivenRanks(9, 6));
        comboMap50Percent.put(26, p.getSuitedCombosOfGivenRanks(8, 5));
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap95Percent() {
        return comboMap95Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap50Percent() {
        return comboMap50Percent;
    }
}
