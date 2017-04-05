package com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop;

import com.lennart.model.card.Card;
import com.lennart.model.action.actionbuilders.ActionBuilderUtil;

import java.util.*;

/**
 * Created by lpo10346 on 10/21/2016.
 */
public class _3bet {

    private Map<Integer, Map<Integer, Set<Card>>> comboMap95Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap90Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap50Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap35Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap30Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap15Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap8Percent = new HashMap<>();

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

        comboMap90Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 11));
        comboMap90Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 12));
        comboMap90Percent.put(3, p.getSuitedCombosOfGivenRanks(13, 12));
        comboMap90Percent.put(4, p.getSuitedCombosOfGivenRanks(13, 11));
        comboMap90Percent.put(5, p.getSuitedCombosOfGivenRanks(12, 11));
        comboMap90Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 10));
        comboMap90Percent.put(7, p.getSuitedCombosOfGivenRanks(13, 10));
        comboMap90Percent.put(8, p.getSuitedCombosOfGivenRanks(12, 10));
        comboMap90Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 10));
        comboMap90Percent.put(10, p.getPocketPairCombosOfGivenRank(9));
        comboMap90Percent.put(11, p.getPocketPairCombosOfGivenRank(8));
        comboMap90Percent.put(12, p.getOffSuitCombosOfGivenRanks(14, 10));
        comboMap90Percent.put(13, p.getOffSuitCombosOfGivenRanks(14, 9));
        comboMap90Percent.put(14, p.getOffSuitCombosOfGivenRanks(13, 11));
        comboMap90Percent.put(15, p.getSuitedCombosOfGivenRanks(14, 9));
        comboMap90Percent.put(16, p.getSuitedCombosOfGivenRanks(12, 9));
        comboMap90Percent.put(17, p.getSuitedCombosOfGivenRanks(11, 9));
        comboMap90Percent.put(18, p.getSuitedCombosOfGivenRanks(10, 9));
        comboMap90Percent.put(19, p.getSuitedCombosOfGivenRanks(14, 8));
        comboMap90Percent.put(20, p.getSuitedCombosOfGivenRanks(10, 8));
        comboMap90Percent.put(21, p.getSuitedCombosOfGivenRanks(9, 8));
        comboMap90Percent.put(22, p.getSuitedCombosOfGivenRanks(14, 7));
        comboMap90Percent.put(23, p.getSuitedCombosOfGivenRanks(8, 7));
        comboMap90Percent.put(24, p.getPocketPairCombosOfGivenRank(7));
        comboMap90Percent.put(25, p.getSuitedCombosOfGivenRanks(7, 6));
        comboMap90Percent.put(26, p.getSuitedCombosOfGivenRanks(14, 5));
        comboMap90Percent.put(27, p.getSuitedCombosOfGivenRanks(6, 5));
        comboMap90Percent.put(28, p.getSuitedCombosOfGivenRanks(13, 9));
        comboMap90Percent.put(29, p.getSuitedCombosOfGivenRanks(11, 8));
        comboMap90Percent.put(30, p.getSuitedCombosOfGivenRanks(11, 7));
        comboMap90Percent.put(31, p.getSuitedCombosOfGivenRanks(10, 7));
        comboMap90Percent.put(32, p.getSuitedCombosOfGivenRanks(9, 7));
        comboMap90Percent.put(33, p.getSuitedCombosOfGivenRanks(8, 6));
        comboMap90Percent.put(34, p.getSuitedCombosOfGivenRanks(14, 4));
        comboMap90Percent.put(35, p.getSuitedCombosOfGivenRanks(5, 4));
        comboMap90Percent.put(36, p.getSuitedCombosOfGivenRanks(14, 3));
        comboMap90Percent.put(37, p.getSuitedCombosOfGivenRanks(4, 3));
        comboMap90Percent.put(38, p.getSuitedCombosOfGivenRanks(14, 2));
        comboMap90Percent.put(39, p.getSuitedCombosOfGivenRanks(7, 5));
        comboMap90Percent.put(40, p.getSuitedCombosOfGivenRanks(9, 6));
        comboMap90Percent.put(41, p.getSuitedCombosOfGivenRanks(8, 5));
        comboMap90Percent.put(42, p.getSuitedCombosOfGivenRanks(6, 4));
        comboMap90Percent.put(43, p.getSuitedCombosOfGivenRanks(5, 3));
        comboMap90Percent.put(44, p.getSuitedCombosOfGivenRanks(7, 4));
        comboMap90Percent.put(45, p.getSuitedCombosOfGivenRanks(6, 3));
        comboMap90Percent.put(46, p.getSuitedCombosOfGivenRanks(5, 2));
        comboMap90Percent.put(47, p.getSuitedCombosOfGivenRanks(4, 2));
        comboMap90Percent.put(48, p.getSuitedCombosOfGivenRanks(3, 2));

        comboMap50Percent.put(1, p.getSuitedCombosOfGivenRanks(12, 8));
        comboMap50Percent.put(2, p.getSuitedCombosOfGivenRanks(13, 7));
        comboMap50Percent.put(3, p.getSuitedCombosOfGivenRanks(12, 7));
        comboMap50Percent.put(4, p.getSuitedCombosOfGivenRanks(13, 8));
        comboMap50Percent.put(5, p.getSuitedCombosOfGivenRanks(14, 6));
        comboMap50Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 8));
        comboMap50Percent.put(7, p.getOffSuitCombosOfGivenRanks(13, 10));
        comboMap50Percent.put(8, p.getOffSuitCombosOfGivenRanks(12, 11));
        comboMap50Percent.put(9, p.getOffSuitCombosOfGivenRanks(12, 10));
        comboMap50Percent.put(10, p.getOffSuitCombosOfGivenRanks(11, 10));
        comboMap50Percent.put(11, p.getPocketPairCombosOfGivenRank(6));
        comboMap50Percent.put(12, p.getPocketPairCombosOfGivenRank(5));
        comboMap50Percent.put(13, p.getSuitedCombosOfGivenRanks(10, 6));
        comboMap50Percent.put(14, p.getSuitedCombosOfGivenRanks(9, 5));
        comboMap50Percent.put(15, p.getSuitedCombosOfGivenRanks(8, 4));
        comboMap50Percent.put(16, p.getSuitedCombosOfGivenRanks(7, 3));

        comboMap30Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 7));
        comboMap30Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 6));
        comboMap30Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 5));
        comboMap30Percent.put(4, p.getOffSuitCombosOfGivenRanks(14, 4));
        comboMap30Percent.put(5, p.getOffSuitCombosOfGivenRanks(14, 3));
        comboMap30Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 2));
        comboMap30Percent.put(7, p.getOffSuitCombosOfGivenRanks(13, 9));
        comboMap30Percent.put(8, p.getOffSuitCombosOfGivenRanks(13, 8));
        comboMap30Percent.put(9, p.getOffSuitCombosOfGivenRanks(12, 9));
        comboMap30Percent.put(10, p.getOffSuitCombosOfGivenRanks(12, 8));
        comboMap30Percent.put(11, p.getOffSuitCombosOfGivenRanks(11, 9));
        comboMap30Percent.put(12, p.getOffSuitCombosOfGivenRanks(11, 8));
        comboMap30Percent.put(13, p.getOffSuitCombosOfGivenRanks(10, 9));
        comboMap30Percent.put(14, p.getOffSuitCombosOfGivenRanks(10, 8));
        comboMap30Percent.put(15, p.getOffSuitCombosOfGivenRanks(9, 8));
        comboMap30Percent.put(16, p.getSuitedCombosOfGivenRanks(13, 6));
        comboMap30Percent.put(17, p.getSuitedCombosOfGivenRanks(12, 6));
        comboMap30Percent.put(18, p.getSuitedCombosOfGivenRanks(13, 5));
        comboMap30Percent.put(19, p.getSuitedCombosOfGivenRanks(12, 5));
        comboMap30Percent.put(20, p.getPocketPairCombosOfGivenRank(4));

        comboMap15Percent.put(1, p.getOffSuitCombosOfGivenRanks(13, 7));
        comboMap15Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 6));
        comboMap15Percent.put(3, p.getOffSuitCombosOfGivenRanks(13, 5));
        comboMap15Percent.put(4, p.getOffSuitCombosOfGivenRanks(13, 4));
        comboMap15Percent.put(5, p.getOffSuitCombosOfGivenRanks(13, 3));
        comboMap15Percent.put(6, p.getOffSuitCombosOfGivenRanks(13, 2));
        comboMap15Percent.put(7, p.getOffSuitCombosOfGivenRanks(12, 7));
        comboMap15Percent.put(8, p.getOffSuitCombosOfGivenRanks(9, 7));
        comboMap15Percent.put(9, p.getOffSuitCombosOfGivenRanks(8, 7));
        comboMap15Percent.put(10, p.getOffSuitCombosOfGivenRanks(8, 6));
        comboMap15Percent.put(11, p.getOffSuitCombosOfGivenRanks(7, 6));
        comboMap15Percent.put(12, p.getOffSuitCombosOfGivenRanks(7, 5));
        comboMap15Percent.put(13, p.getSuitedCombosOfGivenRanks(11, 6));
        comboMap15Percent.put(14, p.getOffSuitCombosOfGivenRanks(6, 5));
        comboMap15Percent.put(15, p.getSuitedCombosOfGivenRanks(11, 5));
        comboMap15Percent.put(16, p.getOffSuitCombosOfGivenRanks(5, 4));
        comboMap15Percent.put(17, p.getSuitedCombosOfGivenRanks(13, 4));
        comboMap15Percent.put(18, p.getSuitedCombosOfGivenRanks(12, 4));
        comboMap15Percent.put(19, p.getSuitedCombosOfGivenRanks(13, 3));
        comboMap15Percent.put(20, p.getSuitedCombosOfGivenRanks(12, 3));
        comboMap15Percent.put(21, p.getPocketPairCombosOfGivenRank(3));
        comboMap15Percent.put(22, p.getSuitedCombosOfGivenRanks(13, 2));
        comboMap15Percent.put(23, p.getSuitedCombosOfGivenRanks(12, 2));
        comboMap15Percent.put(24, p.getPocketPairCombosOfGivenRank(2));

        comboMap8Percent.put(1, p.getOffSuitCombosOfGivenRanks(10, 7));
        comboMap8Percent.put(2, p.getOffSuitCombosOfGivenRanks(9, 6));
        comboMap8Percent.put(3, p.getOffSuitCombosOfGivenRanks(8, 5));
        comboMap8Percent.put(4, p.getOffSuitCombosOfGivenRanks(7, 4));
        comboMap8Percent.put(5, p.getOffSuitCombosOfGivenRanks(6, 4));
        comboMap8Percent.put(6, p.getOffSuitCombosOfGivenRanks(6, 3));
        comboMap8Percent.put(7, p.getSuitedCombosOfGivenRanks(10, 5));
        comboMap8Percent.put(8, p.getOffSuitCombosOfGivenRanks(5, 3));
        comboMap8Percent.put(9, p.getOffSuitCombosOfGivenRanks(5, 2));
        comboMap8Percent.put(10, p.getSuitedCombosOfGivenRanks(11, 4));
        comboMap8Percent.put(11, p.getSuitedCombosOfGivenRanks(10, 4));
        comboMap8Percent.put(12, p.getSuitedCombosOfGivenRanks(9, 4));
        comboMap8Percent.put(13, p.getOffSuitCombosOfGivenRanks(4, 3));
        comboMap8Percent.put(14, p.getOffSuitCombosOfGivenRanks(4, 2));
        comboMap8Percent.put(15, p.getSuitedCombosOfGivenRanks(11, 3));
        comboMap8Percent.put(16, p.getSuitedCombosOfGivenRanks(10, 3));
        comboMap8Percent.put(17, p.getSuitedCombosOfGivenRanks(9, 3));
        comboMap8Percent.put(18, p.getSuitedCombosOfGivenRanks(8, 3));
        comboMap8Percent.put(19, p.getSuitedCombosOfGivenRanks(11, 2));
        comboMap8Percent.put(20, p.getSuitedCombosOfGivenRanks(10, 2));
        comboMap8Percent.put(21, p.getSuitedCombosOfGivenRanks(9, 2));
        comboMap8Percent.put(22, p.getSuitedCombosOfGivenRanks(8, 2));
        comboMap8Percent.put(23, p.getSuitedCombosOfGivenRanks(7, 2));
        comboMap8Percent.put(24, p.getSuitedCombosOfGivenRanks(6, 2));
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap95Percent() {
        return comboMap95Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap90Percent() {
        return comboMap90Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap50Percent() {
        return comboMap50Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap35Percent() {
        return comboMap35Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap30Percent() {
        return comboMap30Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap15Percent() {
        return comboMap15Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap8Percent() {
        return comboMap8Percent;
    }
}
