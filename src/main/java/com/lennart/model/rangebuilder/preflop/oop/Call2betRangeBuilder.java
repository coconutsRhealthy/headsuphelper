package com.lennart.model.rangebuilder.preflop.oop;

import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.*;

/**
 * Created by lpo10346 on 10/21/2016.
 */
public class Call2betRangeBuilder {

    private Map<Integer, Map<Integer, Set<Card>>> comboMap90Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap80Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap65Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap50Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap33Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap30Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap10Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap5Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMapRest7Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMapAllPossibleStartHands = new HashMap<>();
    private List<Map<Integer, Map<Integer, Set<Card>>>> allCombosNoRestCombos = new ArrayList<>();

    private PreflopRangeBuilderUtil p;

    public Call2betRangeBuilder(PreflopRangeBuilderUtil p) {
        this.p = p;

        comboMap90Percent.put(1, p.getSuitedCombosOfGivenRanks(10, 6));
        comboMap90Percent.put(2, p.getSuitedCombosOfGivenRanks(9, 5));
        comboMap90Percent.put(3, p.getSuitedCombosOfGivenRanks(8, 4));
        comboMap90Percent.put(4, p.getSuitedCombosOfGivenRanks(7, 4));
        comboMap90Percent.put(5, p.getSuitedCombosOfGivenRanks(7, 3));
        comboMap90Percent.put(6, p.getSuitedCombosOfGivenRanks(6, 3));
        comboMap90Percent.put(7, p.getPocketPairCombosOfGivenRank(3));
        comboMap90Percent.put(8, p.getSuitedCombosOfGivenRanks(5, 2));
        comboMap90Percent.put(9, p.getSuitedCombosOfGivenRanks(4, 2));
        comboMap90Percent.put(10, p.getSuitedCombosOfGivenRanks(3, 2));
        comboMap90Percent.put(11, p.getPocketPairCombosOfGivenRank(2));

        comboMap80Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 7));
        comboMap80Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 6));
        comboMap80Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 5));
        comboMap80Percent.put(4, p.getOffSuitCombosOfGivenRanks(14, 4));
        comboMap80Percent.put(5, p.getOffSuitCombosOfGivenRanks(14, 3));
        comboMap80Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 2));
        comboMap80Percent.put(7, p.getOffSuitCombosOfGivenRanks(13, 9));
        comboMap80Percent.put(8, p.getOffSuitCombosOfGivenRanks(13, 8));
        comboMap80Percent.put(9, p.getOffSuitCombosOfGivenRanks(12, 9));
        comboMap80Percent.put(10, p.getOffSuitCombosOfGivenRanks(12, 8));
        comboMap80Percent.put(11, p.getOffSuitCombosOfGivenRanks(11, 9));
        comboMap80Percent.put(12, p.getOffSuitCombosOfGivenRanks(11, 8));
        comboMap80Percent.put(13, p.getOffSuitCombosOfGivenRanks(10, 9));
        comboMap80Percent.put(14, p.getOffSuitCombosOfGivenRanks(10, 8));
        comboMap80Percent.put(15, p.getOffSuitCombosOfGivenRanks(9, 8));
        comboMap80Percent.put(16, p.getSuitedCombosOfGivenRanks(13, 6));
        comboMap80Percent.put(17, p.getSuitedCombosOfGivenRanks(12, 6));
        comboMap80Percent.put(18, p.getSuitedCombosOfGivenRanks(9, 6));
        comboMap80Percent.put(19, p.getSuitedCombosOfGivenRanks(13, 5));
        comboMap80Percent.put(20, p.getSuitedCombosOfGivenRanks(12, 5));
        comboMap80Percent.put(21, p.getSuitedCombosOfGivenRanks(8, 5));
        comboMap80Percent.put(22, p.getSuitedCombosOfGivenRanks(6, 4));
        comboMap80Percent.put(23, p.getPocketPairCombosOfGivenRank(4));
        comboMap80Percent.put(24, p.getSuitedCombosOfGivenRanks(5, 3));

        comboMap65Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 8));
        comboMap65Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 10));
        comboMap65Percent.put(3, p.getOffSuitCombosOfGivenRanks(12, 11));
        comboMap65Percent.put(4, p.getOffSuitCombosOfGivenRanks(12, 10));
        comboMap65Percent.put(5, p.getOffSuitCombosOfGivenRanks(11, 10));
        comboMap65Percent.put(6, p.getSuitedCombosOfGivenRanks(13, 9));
        comboMap65Percent.put(7, p.getSuitedCombosOfGivenRanks(13, 8));
        comboMap65Percent.put(8, p.getSuitedCombosOfGivenRanks(12, 8));
        comboMap65Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 8));
        comboMap65Percent.put(10, p.getSuitedCombosOfGivenRanks(13, 7));
        comboMap65Percent.put(11, p.getSuitedCombosOfGivenRanks(12, 7));
        comboMap65Percent.put(12, p.getSuitedCombosOfGivenRanks(11, 7));
        comboMap65Percent.put(13, p.getSuitedCombosOfGivenRanks(10, 7));
        comboMap65Percent.put(14, p.getSuitedCombosOfGivenRanks(9, 7));
        comboMap65Percent.put(15, p.getSuitedCombosOfGivenRanks(14, 6));
        comboMap65Percent.put(16, p.getSuitedCombosOfGivenRanks(8, 6));
        comboMap65Percent.put(17, p.getPocketPairCombosOfGivenRank(6));
        comboMap65Percent.put(18, p.getSuitedCombosOfGivenRanks(7, 5));
        comboMap65Percent.put(19, p.getPocketPairCombosOfGivenRank(5));
        comboMap65Percent.put(20, p.getSuitedCombosOfGivenRanks(14, 4));
        comboMap65Percent.put(21, p.getSuitedCombosOfGivenRanks(5, 4));
        comboMap65Percent.put(22, p.getSuitedCombosOfGivenRanks(14, 3));
        comboMap65Percent.put(23, p.getSuitedCombosOfGivenRanks(4, 3));
        comboMap65Percent.put(24, p.getSuitedCombosOfGivenRanks(14, 2));

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

        comboMap33Percent.put(1, p.getOffSuitCombosOfGivenRanks(13, 7));
        comboMap33Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 6));
        comboMap33Percent.put(3, p.getOffSuitCombosOfGivenRanks(9, 7));
        comboMap33Percent.put(4, p.getOffSuitCombosOfGivenRanks(8, 7));
        comboMap33Percent.put(5, p.getOffSuitCombosOfGivenRanks(8, 6));
        comboMap33Percent.put(6, p.getOffSuitCombosOfGivenRanks(7, 6));
        comboMap33Percent.put(7, p.getSuitedCombosOfGivenRanks(11, 6));
        comboMap33Percent.put(8, p.getOffSuitCombosOfGivenRanks(6, 5));
        comboMap33Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 5));
        comboMap33Percent.put(10, p.getOffSuitCombosOfGivenRanks(5, 4));
        comboMap33Percent.put(11, p.getSuitedCombosOfGivenRanks(13, 4));
        comboMap33Percent.put(12, p.getSuitedCombosOfGivenRanks(12, 4));
        comboMap33Percent.put(13, p.getSuitedCombosOfGivenRanks(13, 3));
        comboMap33Percent.put(14, p.getSuitedCombosOfGivenRanks(12, 3));
        comboMap33Percent.put(15, p.getSuitedCombosOfGivenRanks(13, 2));
        comboMap33Percent.put(16, p.getSuitedCombosOfGivenRanks(12, 2));
        comboMap33Percent.put(17, p.getSuitedCombosOfGivenRanks(6, 2));

        comboMap30Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 11));
        comboMap30Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 12));
        comboMap30Percent.put(3, p.getSuitedCombosOfGivenRanks(13, 12));
        comboMap30Percent.put(4, p.getSuitedCombosOfGivenRanks(13, 11));
        comboMap30Percent.put(5, p.getSuitedCombosOfGivenRanks(12, 11));
        comboMap30Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 10));
        comboMap30Percent.put(7, p.getSuitedCombosOfGivenRanks(13, 10));
        comboMap30Percent.put(8, p.getSuitedCombosOfGivenRanks(12, 10));
        comboMap30Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 10));
        comboMap30Percent.put(10, p.getPocketPairCombosOfGivenRank(9));
        comboMap30Percent.put(11, p.getPocketPairCombosOfGivenRank(8));

        comboMap10Percent.put(1, p.getOffSuitCombosOfGivenRanks(13, 5));
        comboMap10Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 4));
        comboMap10Percent.put(3, p.getOffSuitCombosOfGivenRanks(13, 3));
        comboMap10Percent.put(4, p.getOffSuitCombosOfGivenRanks(13, 2));
        comboMap10Percent.put(5, p.getOffSuitCombosOfGivenRanks(12, 7));
        comboMap10Percent.put(6, p.getOffSuitCombosOfGivenRanks(10, 7));
        comboMap10Percent.put(7, p.getOffSuitCombosOfGivenRanks(9, 6));
        comboMap10Percent.put(8, p.getOffSuitCombosOfGivenRanks(8, 5));
        comboMap10Percent.put(9, p.getOffSuitCombosOfGivenRanks(7, 5));
        comboMap10Percent.put(10, p.getOffSuitCombosOfGivenRanks(7, 4));
        comboMap10Percent.put(11, p.getOffSuitCombosOfGivenRanks(6, 4));
        comboMap10Percent.put(12, p.getOffSuitCombosOfGivenRanks(6, 3));
        comboMap10Percent.put(13, p.getSuitedCombosOfGivenRanks(10, 5));
        comboMap10Percent.put(14, p.getOffSuitCombosOfGivenRanks(5, 3));
        comboMap10Percent.put(15, p.getSuitedCombosOfGivenRanks(11, 4));
        comboMap10Percent.put(16, p.getSuitedCombosOfGivenRanks(10, 4));
        comboMap10Percent.put(17, p.getSuitedCombosOfGivenRanks(9, 4));
        comboMap10Percent.put(18, p.getOffSuitCombosOfGivenRanks(4, 3));
        comboMap10Percent.put(19, p.getSuitedCombosOfGivenRanks(11, 3));
        comboMap10Percent.put(20, p.getSuitedCombosOfGivenRanks(10, 3));
        comboMap10Percent.put(21, p.getSuitedCombosOfGivenRanks(11, 2));

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

        allCombosNoRestCombos.add(comboMap90Percent);
        allCombosNoRestCombos.add(comboMap80Percent);
        allCombosNoRestCombos.add(comboMap65Percent);
        allCombosNoRestCombos.add(comboMap50Percent);
        allCombosNoRestCombos.add(comboMap33Percent);
        allCombosNoRestCombos.add(comboMap30Percent);
        allCombosNoRestCombos.add(comboMap10Percent);
        allCombosNoRestCombos.add(comboMap5Percent);

        comboMapRest7Percent.put(1, p.removeCombosThatCouldBeInOtherMapsFromRestMap(allCombosNoRestCombos));

        comboMapAllPossibleStartHands.put(1, p.getAllPossibleStartHandsAsSets());
    }

    public Map<Integer, Set<Card>> getOpponentCall2betRange() {
        Map<Integer, Set<Card>> opponentCall2betRange = new HashMap<>();

        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap90Percent, 0.90);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap80Percent, 0.80);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap65Percent, 0.65);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap50Percent, 0.50);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap33Percent, 0.33);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap30Percent, 0.30);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap10Percent, 0.10);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap5Percent, 0.05);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMapRest7Percent, 0.07);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMapAllPossibleStartHands, 0.03);

        opponentCall2betRange = p.removeDoubleCombos(opponentCall2betRange);

        return opponentCall2betRange;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap90Percent() {
        return comboMap90Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap80Percent() {
        return comboMap80Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap65Percent() {
        return comboMap65Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap50Percent() {
        return comboMap50Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap33Percent() {
        return comboMap33Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap30Percent() {
        return comboMap30Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap10Percent() {
        return comboMap10Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap5Percent() {
        return comboMap5Percent;
    }
}
