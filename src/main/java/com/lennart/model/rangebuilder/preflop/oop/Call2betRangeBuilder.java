package com.lennart.model.rangebuilder.preflop.oop;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.*;

/**
 * Created by lpo10346 on 10/21/2016.
 */
public class Call2betRangeBuilder {

    private static Map<Integer, Map<Integer, Set<Card>>> comboMap100Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap90Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap80Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap70Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap45Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap40Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap25Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMap20Percent = new HashMap<>();
    private static Map<Integer, Map<Integer, Set<Card>>> comboMapRest10Percent = new HashMap<>();
    private static List<Map<Integer, Map<Integer, Set<Card>>>> allCombosNoRestCombos = new ArrayList<>();

    static {
        PreflopRangeBuilderUtil p = new PreflopRangeBuilderUtil();

        comboMap100Percent.put(1, p.getSuitedCombosOfGivenRanks(10, 6));
        comboMap100Percent.put(2, p.getSuitedCombosOfGivenRanks(9, 5));
        comboMap100Percent.put(3, p.getSuitedCombosOfGivenRanks(8, 4));
        comboMap100Percent.put(4, p.getSuitedCombosOfGivenRanks(7, 4));
        comboMap100Percent.put(5, p.getSuitedCombosOfGivenRanks(7, 3));
        comboMap100Percent.put(6, p.getSuitedCombosOfGivenRanks(6, 3));
        comboMap100Percent.put(7, p.getPocketPairCombosOfGivenRank(3));
        comboMap100Percent.put(8, p.getSuitedCombosOfGivenRanks(5, 2));
        comboMap100Percent.put(9, p.getSuitedCombosOfGivenRanks(4, 2));
        comboMap100Percent.put(10, p.getSuitedCombosOfGivenRanks(3, 2));
        comboMap100Percent.put(11, p.getPocketPairCombosOfGivenRank(2));

        comboMap90Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 7));
        comboMap90Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 6));
        comboMap90Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 5));
        comboMap90Percent.put(4, p.getOffSuitCombosOfGivenRanks(14, 4));
        comboMap90Percent.put(5, p.getOffSuitCombosOfGivenRanks(14, 3));
        comboMap90Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 2));
        comboMap90Percent.put(7, p.getOffSuitCombosOfGivenRanks(13, 9));
        comboMap90Percent.put(8, p.getOffSuitCombosOfGivenRanks(13, 8));
        comboMap90Percent.put(9, p.getOffSuitCombosOfGivenRanks(12, 9));
        comboMap90Percent.put(10, p.getOffSuitCombosOfGivenRanks(12, 8));
        comboMap90Percent.put(11, p.getOffSuitCombosOfGivenRanks(11, 9));
        comboMap90Percent.put(12, p.getOffSuitCombosOfGivenRanks(11, 8));
        comboMap90Percent.put(13, p.getOffSuitCombosOfGivenRanks(10, 9));
        comboMap90Percent.put(14, p.getOffSuitCombosOfGivenRanks(10, 8));
        comboMap90Percent.put(15, p.getOffSuitCombosOfGivenRanks(9, 8));
        comboMap90Percent.put(16, p.getSuitedCombosOfGivenRanks(13, 6));
        comboMap90Percent.put(17, p.getSuitedCombosOfGivenRanks(12, 6));
        comboMap90Percent.put(18, p.getSuitedCombosOfGivenRanks(9, 6));
        comboMap90Percent.put(19, p.getSuitedCombosOfGivenRanks(13, 5));
        comboMap90Percent.put(20, p.getSuitedCombosOfGivenRanks(12, 5));
        comboMap90Percent.put(21, p.getSuitedCombosOfGivenRanks(8, 5));
        comboMap90Percent.put(22, p.getSuitedCombosOfGivenRanks(6, 4));
        comboMap90Percent.put(23, p.getPocketPairCombosOfGivenRank(4));
        comboMap90Percent.put(24, p.getSuitedCombosOfGivenRanks(5, 3));

        comboMap80Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 8));
        comboMap80Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 10));
        comboMap80Percent.put(3, p.getOffSuitCombosOfGivenRanks(12, 11));
        comboMap80Percent.put(4, p.getOffSuitCombosOfGivenRanks(12, 10));
        comboMap80Percent.put(5, p.getOffSuitCombosOfGivenRanks(11, 10));
        comboMap80Percent.put(6, p.getSuitedCombosOfGivenRanks(13, 9));
        comboMap80Percent.put(7, p.getSuitedCombosOfGivenRanks(13, 8));
        comboMap80Percent.put(8, p.getSuitedCombosOfGivenRanks(12, 8));
        comboMap80Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 8));
        comboMap80Percent.put(10, p.getSuitedCombosOfGivenRanks(13, 7));
        comboMap80Percent.put(11, p.getSuitedCombosOfGivenRanks(12, 7));
        comboMap80Percent.put(12, p.getSuitedCombosOfGivenRanks(11, 7));
        comboMap80Percent.put(13, p.getSuitedCombosOfGivenRanks(10, 7));
        comboMap80Percent.put(14, p.getSuitedCombosOfGivenRanks(9, 7));
        comboMap80Percent.put(15, p.getSuitedCombosOfGivenRanks(14, 6));
        comboMap80Percent.put(16, p.getSuitedCombosOfGivenRanks(8, 6));
        comboMap80Percent.put(17, p.getPocketPairCombosOfGivenRank(6));
        comboMap80Percent.put(18, p.getSuitedCombosOfGivenRanks(7, 5));
        comboMap80Percent.put(19, p.getPocketPairCombosOfGivenRank(5));
        comboMap80Percent.put(20, p.getSuitedCombosOfGivenRanks(14, 4));
        comboMap80Percent.put(21, p.getSuitedCombosOfGivenRanks(5, 4));
        comboMap80Percent.put(22, p.getSuitedCombosOfGivenRanks(14, 3));
        comboMap80Percent.put(23, p.getSuitedCombosOfGivenRanks(4, 3));
        comboMap80Percent.put(24, p.getSuitedCombosOfGivenRanks(14, 2));

        comboMap70Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 10));
        comboMap70Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 9));
        comboMap70Percent.put(3, p.getOffSuitCombosOfGivenRanks(13, 11));
        comboMap70Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 9));
        comboMap70Percent.put(5, p.getSuitedCombosOfGivenRanks(12, 9));
        comboMap70Percent.put(6, p.getSuitedCombosOfGivenRanks(11, 9));
        comboMap70Percent.put(7, p.getSuitedCombosOfGivenRanks(10, 9));
        comboMap70Percent.put(8, p.getSuitedCombosOfGivenRanks(14, 8));
        comboMap70Percent.put(9, p.getSuitedCombosOfGivenRanks(10, 8));
        comboMap70Percent.put(10, p.getSuitedCombosOfGivenRanks(9, 8));
        comboMap70Percent.put(11, p.getSuitedCombosOfGivenRanks(14, 7));
        comboMap70Percent.put(12, p.getSuitedCombosOfGivenRanks(8, 7));
        comboMap70Percent.put(13, p.getPocketPairCombosOfGivenRank(7));
        comboMap70Percent.put(14, p.getSuitedCombosOfGivenRanks(7, 6));
        comboMap70Percent.put(15, p.getSuitedCombosOfGivenRanks(14, 5));
        comboMap70Percent.put(16, p.getSuitedCombosOfGivenRanks(6, 5));

        comboMap45Percent.put(1, p.getOffSuitCombosOfGivenRanks(13, 7));
        comboMap45Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 6));
        comboMap45Percent.put(3, p.getOffSuitCombosOfGivenRanks(9, 7));
        comboMap45Percent.put(4, p.getOffSuitCombosOfGivenRanks(8, 7));
        comboMap45Percent.put(5, p.getOffSuitCombosOfGivenRanks(8, 6));
        comboMap45Percent.put(6, p.getOffSuitCombosOfGivenRanks(7, 6));
        comboMap45Percent.put(7, p.getSuitedCombosOfGivenRanks(11, 6));
        comboMap45Percent.put(8, p.getOffSuitCombosOfGivenRanks(6, 5));
        comboMap45Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 5));
        comboMap45Percent.put(10, p.getOffSuitCombosOfGivenRanks(5, 4));
        comboMap45Percent.put(11, p.getSuitedCombosOfGivenRanks(13, 4));
        comboMap45Percent.put(12, p.getSuitedCombosOfGivenRanks(12, 4));
        comboMap45Percent.put(13, p.getSuitedCombosOfGivenRanks(13, 3));
        comboMap45Percent.put(14, p.getSuitedCombosOfGivenRanks(12, 3));
        comboMap45Percent.put(15, p.getSuitedCombosOfGivenRanks(13, 2));
        comboMap45Percent.put(16, p.getSuitedCombosOfGivenRanks(12, 2));
        comboMap45Percent.put(17, p.getSuitedCombosOfGivenRanks(6, 2));

        comboMap40Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 11));
        comboMap40Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 12));
        comboMap40Percent.put(3, p.getSuitedCombosOfGivenRanks(13, 12));
        comboMap40Percent.put(4, p.getSuitedCombosOfGivenRanks(13, 11));
        comboMap40Percent.put(5, p.getSuitedCombosOfGivenRanks(12, 11));
        comboMap40Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 10));
        comboMap40Percent.put(7, p.getSuitedCombosOfGivenRanks(13, 10));
        comboMap40Percent.put(8, p.getSuitedCombosOfGivenRanks(12, 10));
        comboMap40Percent.put(9, p.getSuitedCombosOfGivenRanks(11, 10));
        comboMap40Percent.put(10, p.getPocketPairCombosOfGivenRank(9));
        comboMap40Percent.put(11, p.getPocketPairCombosOfGivenRank(8));

        comboMap25Percent.put(1, p.getOffSuitCombosOfGivenRanks(13, 5));
        comboMap25Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 4));
        comboMap25Percent.put(3, p.getOffSuitCombosOfGivenRanks(13, 3));
        comboMap25Percent.put(4, p.getOffSuitCombosOfGivenRanks(13, 2));
        comboMap25Percent.put(5, p.getOffSuitCombosOfGivenRanks(12, 7));
        comboMap25Percent.put(6, p.getOffSuitCombosOfGivenRanks(10, 7));
        comboMap25Percent.put(7, p.getOffSuitCombosOfGivenRanks(9, 6));
        comboMap25Percent.put(8, p.getOffSuitCombosOfGivenRanks(8, 5));
        comboMap25Percent.put(9, p.getOffSuitCombosOfGivenRanks(7, 5));
        comboMap25Percent.put(10, p.getOffSuitCombosOfGivenRanks(7, 4));
        comboMap25Percent.put(11, p.getOffSuitCombosOfGivenRanks(6, 4));
        comboMap25Percent.put(12, p.getOffSuitCombosOfGivenRanks(6, 3));
        comboMap25Percent.put(13, p.getSuitedCombosOfGivenRanks(10, 5));
        comboMap25Percent.put(14, p.getOffSuitCombosOfGivenRanks(5, 3));
        comboMap25Percent.put(15, p.getSuitedCombosOfGivenRanks(11, 4));
        comboMap25Percent.put(16, p.getSuitedCombosOfGivenRanks(10, 4));
        comboMap25Percent.put(17, p.getSuitedCombosOfGivenRanks(9, 4));
        comboMap25Percent.put(18, p.getOffSuitCombosOfGivenRanks(4, 3));
        comboMap25Percent.put(19, p.getSuitedCombosOfGivenRanks(11, 3));
        comboMap25Percent.put(20, p.getSuitedCombosOfGivenRanks(10, 3));
        comboMap25Percent.put(21, p.getSuitedCombosOfGivenRanks(11, 2));

        comboMap20Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
        comboMap20Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 13));
        comboMap20Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 12));
        comboMap20Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 13));
        comboMap20Percent.put(5, p.getPocketPairCombosOfGivenRank(13));
        comboMap20Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 12));
        comboMap20Percent.put(7, p.getPocketPairCombosOfGivenRank(12));
        comboMap20Percent.put(8, p.getSuitedCombosOfGivenRanks(14, 11));
        comboMap20Percent.put(9, p.getPocketPairCombosOfGivenRank(11));
        comboMap20Percent.put(10, p.getPocketPairCombosOfGivenRank(10));

        //comboMapRest10Percent.put(1, new BoardEvaluator().getAllPossibleStartHandsAsSets());

        allCombosNoRestCombos.add(comboMap100Percent);
        allCombosNoRestCombos.add(comboMap90Percent);
        allCombosNoRestCombos.add(comboMap80Percent);
        allCombosNoRestCombos.add(comboMap70Percent);
        allCombosNoRestCombos.add(comboMap45Percent);
        allCombosNoRestCombos.add(comboMap40Percent);
        allCombosNoRestCombos.add(comboMap25Percent);
        allCombosNoRestCombos.add(comboMap20Percent);

        comboMapRest10Percent.put(1,
                new PreflopRangeBuilderUtil().removeCombosThatCouldBeInOtherMapsFromRestMap(allCombosNoRestCombos));
    }

    public Map<Integer, Set<Card>> getOpponentCall2betRange() {
        PreflopRangeBuilderUtil p = new PreflopRangeBuilderUtil();
        Map<Integer, Set<Card>> opponentCall2betRange = new HashMap<>();

        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap100Percent, 1.0);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap90Percent, 0.90);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap80Percent, 0.80);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap70Percent, 0.70);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap45Percent, 0.45);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap40Percent, 0.40);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap25Percent, 0.25);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMap20Percent, 0.10);
        opponentCall2betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall2betRange, comboMapRest10Percent, 0.08);

        Set<Set<Card>> completeCall2betRangeAsSet = new HashSet<>();

        for (Map.Entry<Integer, Set<Card>> entry : opponentCall2betRange.entrySet()) {
            completeCall2betRangeAsSet.add(entry.getValue());
        }

        opponentCall2betRange.clear();

        for(Set<Card> s : completeCall2betRangeAsSet) {
            opponentCall2betRange.put(opponentCall2betRange.size(), s);
        }

        return opponentCall2betRange;
    }
}
