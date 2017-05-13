package com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop;

import com.lennart.model.card.Card;
import com.lennart.model.action.actionbuilders.ActionBuilderUtil;

import java.util.*;

/**
 * Created by lpo10346 on 10/21/2016.
 */
public class _3bet {

    private Map<Integer, Map<Integer, Set<Card>>> comboMap95Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap70Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap50Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap35Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap20Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap10Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap5Percent = new HashMap<>();

    private ActionBuilderUtil p;

    public _3bet(ActionBuilderUtil p) {
        this.p = p;

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
        comboMap70Percent.put(10, p.getPocketPairCombosOfGivenRank(9));
        comboMap70Percent.put(11, p.getPocketPairCombosOfGivenRank(8));

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
        comboMap50Percent.put(13, p.getPocketPairCombosOfGivenRank(7));
        comboMap50Percent.put(14, p.getSuitedCombosOfGivenRanks(7, 6));
        comboMap50Percent.put(15, p.getSuitedCombosOfGivenRanks(14, 5));
        comboMap50Percent.put(16, p.getSuitedCombosOfGivenRanks(6, 5));

        comboMap35Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 8));
        comboMap35Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 10));
        comboMap35Percent.put(3, p.getOffSuitCombosOfGivenRanks(12, 11));
        comboMap35Percent.put(4, p.getOffSuitCombosOfGivenRanks(12, 10));
        comboMap35Percent.put(5, p.getOffSuitCombosOfGivenRanks(11, 10));
        comboMap35Percent.put(6, p.getSuitedCombosOfGivenRanks(13, 9));
        comboMap35Percent.put(7, p.getSuitedCombosOfGivenRanks(13, 8));
        comboMap35Percent.put(8, p.getSuitedCombosOfGivenRanks(12, 8));
        comboMap35Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 8));
        comboMap35Percent.put(10, p.getSuitedCombosOfGivenRanks(13, 7));
        comboMap35Percent.put(11, p.getSuitedCombosOfGivenRanks(12, 7));
        comboMap35Percent.put(12, p.getSuitedCombosOfGivenRanks(11, 7));
        comboMap35Percent.put(13, p.getSuitedCombosOfGivenRanks(10, 7));
        comboMap35Percent.put(14, p.getSuitedCombosOfGivenRanks(9, 7));
        comboMap35Percent.put(15, p.getSuitedCombosOfGivenRanks(14, 6));
        comboMap35Percent.put(16, p.getSuitedCombosOfGivenRanks(8, 6));
        comboMap35Percent.put(17, p.getPocketPairCombosOfGivenRank(6));
        comboMap35Percent.put(18, p.getSuitedCombosOfGivenRanks(7, 5));
        comboMap35Percent.put(19, p.getPocketPairCombosOfGivenRank(5));
        comboMap35Percent.put(20, p.getSuitedCombosOfGivenRanks(14, 4));
        comboMap35Percent.put(21, p.getSuitedCombosOfGivenRanks(5, 4));
        comboMap35Percent.put(22, p.getSuitedCombosOfGivenRanks(14, 3));
        comboMap35Percent.put(23, p.getSuitedCombosOfGivenRanks(4, 3));
        comboMap35Percent.put(24, p.getSuitedCombosOfGivenRanks(14, 2));

        comboMap20Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 7));
        comboMap20Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 6));
        comboMap20Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 5));
        comboMap20Percent.put(4, p.getOffSuitCombosOfGivenRanks(14, 4));
        comboMap20Percent.put(5, p.getOffSuitCombosOfGivenRanks(14, 3));
        comboMap20Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 2));
        comboMap20Percent.put(7, p.getOffSuitCombosOfGivenRanks(13, 9));
        comboMap20Percent.put(8, p.getOffSuitCombosOfGivenRanks(13, 8));
        comboMap20Percent.put(9, p.getOffSuitCombosOfGivenRanks(12, 9));
        comboMap20Percent.put(10, p.getOffSuitCombosOfGivenRanks(12, 8));
        comboMap20Percent.put(11, p.getOffSuitCombosOfGivenRanks(11, 9));
        comboMap20Percent.put(12, p.getOffSuitCombosOfGivenRanks(11, 8));
        comboMap20Percent.put(13, p.getOffSuitCombosOfGivenRanks(10, 9));
        comboMap20Percent.put(14, p.getOffSuitCombosOfGivenRanks(10, 8));
        comboMap20Percent.put(15, p.getOffSuitCombosOfGivenRanks(9, 8));
        comboMap20Percent.put(16, p.getSuitedCombosOfGivenRanks(13, 6));
        comboMap20Percent.put(17, p.getSuitedCombosOfGivenRanks(12, 6));
        comboMap20Percent.put(18, p.getSuitedCombosOfGivenRanks(9, 6));
        comboMap20Percent.put(19, p.getSuitedCombosOfGivenRanks(13, 5));
        comboMap20Percent.put(20, p.getSuitedCombosOfGivenRanks(12, 5));
        comboMap20Percent.put(21, p.getSuitedCombosOfGivenRanks(8, 5));
        comboMap20Percent.put(22, p.getSuitedCombosOfGivenRanks(6, 4));
        comboMap20Percent.put(23, p.getPocketPairCombosOfGivenRank(4));
        comboMap20Percent.put(24, p.getSuitedCombosOfGivenRanks(5, 3));

        comboMap10Percent.put(1, p.getOffSuitCombosOfGivenRanks(13, 7));
        comboMap10Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 6));
        comboMap10Percent.put(3, p.getOffSuitCombosOfGivenRanks(13, 5));
        comboMap10Percent.put(4, p.getOffSuitCombosOfGivenRanks(13, 4));
        comboMap10Percent.put(5, p.getOffSuitCombosOfGivenRanks(13, 3));
        comboMap10Percent.put(6, p.getOffSuitCombosOfGivenRanks(13, 2));
        comboMap10Percent.put(7, p.getOffSuitCombosOfGivenRanks(12, 7));
        comboMap10Percent.put(8, p.getOffSuitCombosOfGivenRanks(9, 7));
        comboMap10Percent.put(9, p.getOffSuitCombosOfGivenRanks(8, 7));
        comboMap10Percent.put(10, p.getOffSuitCombosOfGivenRanks(8, 6));
        comboMap10Percent.put(11, p.getOffSuitCombosOfGivenRanks(7, 6));
        comboMap10Percent.put(12, p.getOffSuitCombosOfGivenRanks(7, 5));
        comboMap10Percent.put(13, p.getSuitedCombosOfGivenRanks(11, 6));
        comboMap10Percent.put(14, p.getSuitedCombosOfGivenRanks(10, 6));
        comboMap10Percent.put(15, p.getOffSuitCombosOfGivenRanks(6, 5));
        comboMap10Percent.put(16, p.getSuitedCombosOfGivenRanks(11, 5));
        comboMap10Percent.put(17, p.getSuitedCombosOfGivenRanks(9, 5));
        comboMap10Percent.put(18, p.getOffSuitCombosOfGivenRanks(5, 4));
        comboMap10Percent.put(19, p.getSuitedCombosOfGivenRanks(13, 4));
        comboMap10Percent.put(20, p.getSuitedCombosOfGivenRanks(12, 4));
        comboMap10Percent.put(21, p.getSuitedCombosOfGivenRanks(8, 4));
        comboMap10Percent.put(22, p.getSuitedCombosOfGivenRanks(7, 4));
        comboMap10Percent.put(23, p.getSuitedCombosOfGivenRanks(13, 3));
        comboMap10Percent.put(24, p.getSuitedCombosOfGivenRanks(12, 3));
        comboMap10Percent.put(25, p.getSuitedCombosOfGivenRanks(7, 3));
        comboMap10Percent.put(26, p.getSuitedCombosOfGivenRanks(6, 3));
        comboMap10Percent.put(27, p.getPocketPairCombosOfGivenRank(3));
        comboMap10Percent.put(28, p.getSuitedCombosOfGivenRanks(13, 2));
        comboMap10Percent.put(29, p.getSuitedCombosOfGivenRanks(12, 2));
        comboMap10Percent.put(30, p.getSuitedCombosOfGivenRanks(5, 2));
        comboMap10Percent.put(31, p.getSuitedCombosOfGivenRanks(4, 2));
        comboMap10Percent.put(32, p.getSuitedCombosOfGivenRanks(3, 2));
        comboMap10Percent.put(33, p.getPocketPairCombosOfGivenRank(2));

        comboMap5Percent.put(1, p.getOffSuitCombosOfGivenRanks(10, 7));
        comboMap5Percent.put(2, p.getOffSuitCombosOfGivenRanks(9, 6));
        comboMap5Percent.put(3, p.getOffSuitCombosOfGivenRanks(8, 5));
        comboMap5Percent.put(4, p.getOffSuitCombosOfGivenRanks(7, 4));
        comboMap5Percent.put(5, p.getOffSuitCombosOfGivenRanks(6, 4));
        comboMap5Percent.put(6, p.getOffSuitCombosOfGivenRanks(6, 3));
        comboMap5Percent.put(7, p.getSuitedCombosOfGivenRanks(10, 5));
        comboMap5Percent.put(8, p.getOffSuitCombosOfGivenRanks(5, 3));
        comboMap5Percent.put(9, p.getOffSuitCombosOfGivenRanks(5, 2));
        comboMap5Percent.put(10, p.getSuitedCombosOfGivenRanks(11, 4));
        comboMap5Percent.put(11, p.getSuitedCombosOfGivenRanks(10, 4));
        comboMap5Percent.put(12, p.getSuitedCombosOfGivenRanks(9, 4));
        comboMap5Percent.put(13, p.getOffSuitCombosOfGivenRanks(4, 3));
        comboMap5Percent.put(14, p.getOffSuitCombosOfGivenRanks(4, 2));
        comboMap5Percent.put(15, p.getSuitedCombosOfGivenRanks(11, 3));
        comboMap5Percent.put(16, p.getSuitedCombosOfGivenRanks(10, 3));
        comboMap5Percent.put(17, p.getSuitedCombosOfGivenRanks(9, 3));
        comboMap5Percent.put(18, p.getSuitedCombosOfGivenRanks(8, 3));
        comboMap5Percent.put(19, p.getSuitedCombosOfGivenRanks(11, 2));
        comboMap5Percent.put(20, p.getSuitedCombosOfGivenRanks(10, 2));
        comboMap5Percent.put(21, p.getSuitedCombosOfGivenRanks(9, 2));
        comboMap5Percent.put(22, p.getSuitedCombosOfGivenRanks(8, 2));
        comboMap5Percent.put(23, p.getSuitedCombosOfGivenRanks(7, 2));
        comboMap5Percent.put(24, p.getSuitedCombosOfGivenRanks(6, 2));
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap95Percent() {
        return comboMap95Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap70Percent() {
        return comboMap70Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap50Percent() {
        return comboMap50Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap35Percent() {
        return comboMap35Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap20Percent() {
        return comboMap20Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap10Percent() {
        return comboMap10Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap5Percent() {
        return comboMap5Percent;
    }
}
