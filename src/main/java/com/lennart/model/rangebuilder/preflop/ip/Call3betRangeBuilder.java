package com.lennart.model.rangebuilder.preflop.ip;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.*;

/**
 * Created by lennart on 24-10-16.
 */
public class Call3betRangeBuilder {

    private Map<Integer, Map<Integer, Set<Card>>> comboMap100Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap94Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap89Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap80Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap73Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap50Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap34Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap29Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap27Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap19Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap8Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap7Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMap5Percent = new HashMap<>();

    private Map<Integer, Map<Integer, Set<Card>>> comboMapRest7Percent = new HashMap<>();
    private Map<Integer, Map<Integer, Set<Card>>> comboMapAllPossibleStartHands = new HashMap<>();
    private List<Map<Integer, Map<Integer, Set<Card>>>> allCombosNoRestCombos = new ArrayList<>();

    public Call3betRangeBuilder() {
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

        comboMap73Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 8));
        comboMap73Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 10));
        comboMap73Percent.put(3, p.getOffSuitCombosOfGivenRanks(12, 9));
        comboMap73Percent.put(4, p.getOffSuitCombosOfGivenRanks(11, 9));
        comboMap73Percent.put(5, p.getOffSuitCombosOfGivenRanks(10, 9));
        comboMap73Percent.put(6, p.getOffSuitCombosOfGivenRanks(9, 8));
        comboMap73Percent.put(7, p.getOffSuitCombosOfGivenRanks(8, 7));
        comboMap73Percent.put(8, p.getSuitedCombosOfGivenRanks(13, 7));
        comboMap73Percent.put(9, p.getSuitedCombosOfGivenRanks(12, 7));
        comboMap73Percent.put(10, p.getSuitedCombosOfGivenRanks(11, 7));
        comboMap73Percent.put(11, p.getSuitedCombosOfGivenRanks(10, 7));
        comboMap73Percent.put(12, p.getSuitedCombosOfGivenRanks(13, 6));
        comboMap73Percent.put(13, p.getSuitedCombosOfGivenRanks(9, 6));
        comboMap73Percent.put(14, p.getSuitedCombosOfGivenRanks(8, 5));
        comboMap73Percent.put(15, p.getPocketPairCombosOfGivenRank(5));
        comboMap73Percent.put(16, p.getSuitedCombosOfGivenRanks(7, 4));
        comboMap73Percent.put(17, p.getPocketPairCombosOfGivenRank(4));

        comboMap50Percent.put(1, p.getPocketPairCombosOfGivenRank(10));
        comboMap50Percent.put(2, p.getPocketPairCombosOfGivenRank(9));
        comboMap50Percent.put(3, p.getPocketPairCombosOfGivenRank(8));

        comboMap34Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 7));
        comboMap34Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 6));
        comboMap34Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 5));
        comboMap34Percent.put(4, p.getOffSuitCombosOfGivenRanks(14, 4));
        comboMap34Percent.put(5, p.getOffSuitCombosOfGivenRanks(14, 3));
        comboMap34Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 2));
        comboMap34Percent.put(7, p.getOffSuitCombosOfGivenRanks(13, 9));
        comboMap34Percent.put(8, p.getOffSuitCombosOfGivenRanks(12, 8));
        comboMap34Percent.put(9, p.getOffSuitCombosOfGivenRanks(11, 8));
        comboMap34Percent.put(10, p.getOffSuitCombosOfGivenRanks(10, 8));
        comboMap34Percent.put(11, p.getOffSuitCombosOfGivenRanks(9, 7));
        comboMap34Percent.put(12, p.getOffSuitCombosOfGivenRanks(8, 6));
        comboMap34Percent.put(13, p.getOffSuitCombosOfGivenRanks(7, 6));
        comboMap34Percent.put(14, p.getSuitedCombosOfGivenRanks(12, 6));
        comboMap34Percent.put(15, p.getSuitedCombosOfGivenRanks(11, 6));
        comboMap34Percent.put(16, p.getSuitedCombosOfGivenRanks(10, 6));
        comboMap34Percent.put(17, p.getSuitedCombosOfGivenRanks(13, 5));
        comboMap34Percent.put(18, p.getSuitedCombosOfGivenRanks(12, 5));
        comboMap34Percent.put(19, p.getSuitedCombosOfGivenRanks(11, 5));
        comboMap34Percent.put(20, p.getSuitedCombosOfGivenRanks(9, 5));
        comboMap34Percent.put(21, p.getSuitedCombosOfGivenRanks(13, 4));
        comboMap34Percent.put(22, p.getSuitedCombosOfGivenRanks(12, 4));
        comboMap34Percent.put(23, p.getSuitedCombosOfGivenRanks(8, 4));
        comboMap34Percent.put(24, p.getSuitedCombosOfGivenRanks(13, 3));
        comboMap34Percent.put(25, p.getSuitedCombosOfGivenRanks(12, 3));
        comboMap34Percent.put(26, p.getSuitedCombosOfGivenRanks(7, 3));
        comboMap34Percent.put(27, p.getSuitedCombosOfGivenRanks(6, 3));
        comboMap34Percent.put(28, p.getPocketPairCombosOfGivenRank(3));
        comboMap34Percent.put(29, p.getSuitedCombosOfGivenRanks(13, 2));
        comboMap34Percent.put(30, p.getSuitedCombosOfGivenRanks(5, 2));
        comboMap34Percent.put(31, p.getPocketPairCombosOfGivenRank(2));

        comboMap29Percent.put(1, p.getOffSuitCombosOfGivenRanks(6, 5));

        comboMap27Percent.put(1, p.getOffSuitCombosOfGivenRanks(7, 5));

        comboMap19Percent.put(1, p.getOffSuitCombosOfGivenRanks(5, 4));

        comboMap8Percent.put(1, p.getOffSuitCombosOfGivenRanks(4, 3));

        comboMap7Percent.put(1, p.getOffSuitCombosOfGivenRanks(5, 3));

        comboMap5Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
        comboMap5Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 13));
        comboMap5Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 12));
        comboMap5Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 13));
        comboMap5Percent.put(5, p.getPocketPairCombosOfGivenRank(13));
        comboMap5Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 12));
        comboMap5Percent.put(7, p.getPocketPairCombosOfGivenRank(12));
        comboMap5Percent.put(8, p.getPocketPairCombosOfGivenRank(11));

        allCombosNoRestCombos.add(comboMap100Percent);
        allCombosNoRestCombos.add(comboMap94Percent);
        allCombosNoRestCombos.add(comboMap89Percent);
        allCombosNoRestCombos.add(comboMap80Percent);
        allCombosNoRestCombos.add(comboMap73Percent);
        allCombosNoRestCombos.add(comboMap50Percent);
        allCombosNoRestCombos.add(comboMap34Percent);
        allCombosNoRestCombos.add(comboMap29Percent);
        allCombosNoRestCombos.add(comboMap27Percent);
        allCombosNoRestCombos.add(comboMap19Percent);
        allCombosNoRestCombos.add(comboMap8Percent);
        allCombosNoRestCombos.add(comboMap7Percent);
        allCombosNoRestCombos.add(comboMap5Percent);

        comboMapRest7Percent.put(1, p.removeCombosThatCouldBeInOtherMapsFromRestMap(allCombosNoRestCombos));

        comboMapAllPossibleStartHands.put(1, new BoardEvaluator().getAllPossibleStartHandsAsSets());
    }

//    static {
//        PreflopRangeBuilderUtil p = new PreflopRangeBuilderUtil();
//
//        comboMap100Percent.put(1, p.getSuitedCombosOfGivenRanks(4, 3));
//        comboMap100Percent.put(2, p.getSuitedCombosOfGivenRanks(4, 2));
//        comboMap100Percent.put(3, p.getSuitedCombosOfGivenRanks(3, 2));
//
//        comboMap94Percent.put(1, p.getSuitedCombosOfGivenRanks(6, 4));
//        comboMap94Percent.put(2, p.getSuitedCombosOfGivenRanks(5, 3));
//
//        comboMap89Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 10));
//        comboMap89Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 12));
//        comboMap89Percent.put(3, p.getOffSuitCombosOfGivenRanks(13, 11));
//        comboMap89Percent.put(4, p.getOffSuitCombosOfGivenRanks(12, 11));
//        comboMap89Percent.put(5, p.getOffSuitCombosOfGivenRanks(12, 10));
//        comboMap89Percent.put(6, p.getOffSuitCombosOfGivenRanks(11, 10));
//        comboMap89Percent.put(7, p.getSuitedCombosOfGivenRanks(13, 9));
//        comboMap89Percent.put(8, p.getSuitedCombosOfGivenRanks(12, 9));
//        comboMap89Percent.put(9, p.getSuitedCombosOfGivenRanks(13, 8));
//        comboMap89Percent.put(10, p.getSuitedCombosOfGivenRanks(12, 8));
//        comboMap89Percent.put(11, p.getSuitedCombosOfGivenRanks(11, 8));
//
//        comboMap80Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 11));
//        comboMap80Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 9));
//        comboMap80Percent.put(3, p.getSuitedCombosOfGivenRanks(13, 12));
//        comboMap80Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 11));
//        comboMap80Percent.put(5, p.getSuitedCombosOfGivenRanks(13, 11));
//        comboMap80Percent.put(6, p.getSuitedCombosOfGivenRanks(12, 11));
//        comboMap80Percent.put(7, p.getSuitedCombosOfGivenRanks(14, 10));
//        comboMap80Percent.put(8, p.getSuitedCombosOfGivenRanks(13, 10));
//        comboMap80Percent.put(9, p.getSuitedCombosOfGivenRanks(12, 10));
//        comboMap80Percent.put(10, p.getSuitedCombosOfGivenRanks(11, 10));
//        comboMap80Percent.put(11, p.getSuitedCombosOfGivenRanks(14, 9));
//        comboMap80Percent.put(12, p.getSuitedCombosOfGivenRanks(11, 9));
//        comboMap80Percent.put(13, p.getSuitedCombosOfGivenRanks(10, 9));
//        comboMap80Percent.put(14, p.getSuitedCombosOfGivenRanks(14, 8));
//        comboMap80Percent.put(15, p.getSuitedCombosOfGivenRanks(10, 8));
//        comboMap80Percent.put(16, p.getSuitedCombosOfGivenRanks(9, 8));
//        comboMap80Percent.put(17, p.getSuitedCombosOfGivenRanks(14, 7));
//        comboMap80Percent.put(18, p.getSuitedCombosOfGivenRanks(9, 7));
//        comboMap80Percent.put(19, p.getSuitedCombosOfGivenRanks(8, 7));
//        comboMap80Percent.put(20, p.getPocketPairCombosOfGivenRank(7));
//        comboMap80Percent.put(21, p.getSuitedCombosOfGivenRanks(14, 6));
//        comboMap80Percent.put(22, p.getSuitedCombosOfGivenRanks(8, 6));
//        comboMap80Percent.put(23, p.getSuitedCombosOfGivenRanks(7, 6));
//        comboMap80Percent.put(24, p.getPocketPairCombosOfGivenRank(6));
//        comboMap80Percent.put(25, p.getSuitedCombosOfGivenRanks(14, 5));
//        comboMap80Percent.put(26, p.getSuitedCombosOfGivenRanks(7, 5));
//        comboMap80Percent.put(27, p.getSuitedCombosOfGivenRanks(6, 5));
//        comboMap80Percent.put(28, p.getSuitedCombosOfGivenRanks(14, 4));
//        comboMap80Percent.put(29, p.getSuitedCombosOfGivenRanks(5, 4));
//        comboMap80Percent.put(30, p.getSuitedCombosOfGivenRanks(14, 3));
//        comboMap80Percent.put(31, p.getSuitedCombosOfGivenRanks(14, 2));
//
//        comboMap73Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 8));
//        comboMap73Percent.put(2, p.getOffSuitCombosOfGivenRanks(13, 10));
//        comboMap73Percent.put(3, p.getOffSuitCombosOfGivenRanks(12, 9));
//        comboMap73Percent.put(4, p.getOffSuitCombosOfGivenRanks(11, 9));
//        comboMap73Percent.put(5, p.getOffSuitCombosOfGivenRanks(10, 9));
//        comboMap73Percent.put(6, p.getOffSuitCombosOfGivenRanks(9, 8));
//        comboMap73Percent.put(7, p.getOffSuitCombosOfGivenRanks(8, 7));
//        comboMap73Percent.put(8, p.getSuitedCombosOfGivenRanks(13, 7));
//        comboMap73Percent.put(9, p.getSuitedCombosOfGivenRanks(12, 7));
//        comboMap73Percent.put(10, p.getSuitedCombosOfGivenRanks(11, 7));
//        comboMap73Percent.put(11, p.getSuitedCombosOfGivenRanks(10, 7));
//        comboMap73Percent.put(12, p.getSuitedCombosOfGivenRanks(13, 6));
//        comboMap73Percent.put(13, p.getSuitedCombosOfGivenRanks(9, 6));
//        comboMap73Percent.put(14, p.getSuitedCombosOfGivenRanks(8, 5));
//        comboMap73Percent.put(15, p.getPocketPairCombosOfGivenRank(5));
//        comboMap73Percent.put(16, p.getSuitedCombosOfGivenRanks(7, 4));
//        comboMap73Percent.put(17, p.getPocketPairCombosOfGivenRank(4));
//
//        comboMap50Percent.put(1, p.getPocketPairCombosOfGivenRank(10));
//        comboMap50Percent.put(2, p.getPocketPairCombosOfGivenRank(9));
//        comboMap50Percent.put(3, p.getPocketPairCombosOfGivenRank(8));
//
//        comboMap34Percent.put(1, p.getOffSuitCombosOfGivenRanks(14, 7));
//        comboMap34Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 6));
//        comboMap34Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 5));
//        comboMap34Percent.put(4, p.getOffSuitCombosOfGivenRanks(14, 4));
//        comboMap34Percent.put(5, p.getOffSuitCombosOfGivenRanks(14, 3));
//        comboMap34Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 2));
//        comboMap34Percent.put(7, p.getOffSuitCombosOfGivenRanks(13, 9));
//        comboMap34Percent.put(8, p.getOffSuitCombosOfGivenRanks(12, 8));
//        comboMap34Percent.put(9, p.getOffSuitCombosOfGivenRanks(11, 8));
//        comboMap34Percent.put(10, p.getOffSuitCombosOfGivenRanks(10, 8));
//        comboMap34Percent.put(11, p.getOffSuitCombosOfGivenRanks(9, 7));
//        comboMap34Percent.put(12, p.getOffSuitCombosOfGivenRanks(8, 6));
//        comboMap34Percent.put(13, p.getOffSuitCombosOfGivenRanks(7, 6));
//        comboMap34Percent.put(14, p.getSuitedCombosOfGivenRanks(12, 6));
//        comboMap34Percent.put(15, p.getSuitedCombosOfGivenRanks(11, 6));
//        comboMap34Percent.put(16, p.getSuitedCombosOfGivenRanks(10, 6));
//        comboMap34Percent.put(17, p.getSuitedCombosOfGivenRanks(13, 5));
//        comboMap34Percent.put(18, p.getSuitedCombosOfGivenRanks(12, 5));
//        comboMap34Percent.put(19, p.getSuitedCombosOfGivenRanks(11, 5));
//        comboMap34Percent.put(20, p.getSuitedCombosOfGivenRanks(9, 5));
//        comboMap34Percent.put(21, p.getSuitedCombosOfGivenRanks(13, 4));
//        comboMap34Percent.put(22, p.getSuitedCombosOfGivenRanks(12, 4));
//        comboMap34Percent.put(23, p.getSuitedCombosOfGivenRanks(8, 4));
//        comboMap34Percent.put(24, p.getSuitedCombosOfGivenRanks(13, 3));
//        comboMap34Percent.put(25, p.getSuitedCombosOfGivenRanks(12, 3));
//        comboMap34Percent.put(26, p.getSuitedCombosOfGivenRanks(7, 3));
//        comboMap34Percent.put(27, p.getSuitedCombosOfGivenRanks(6, 3));
//        comboMap34Percent.put(28, p.getPocketPairCombosOfGivenRank(3));
//        comboMap34Percent.put(29, p.getSuitedCombosOfGivenRanks(13, 2));
//        comboMap34Percent.put(30, p.getSuitedCombosOfGivenRanks(5, 2));
//        comboMap34Percent.put(31, p.getPocketPairCombosOfGivenRank(2));
//
//        comboMap29Percent.put(1, p.getOffSuitCombosOfGivenRanks(6, 5));
//
//        comboMap27Percent.put(1, p.getOffSuitCombosOfGivenRanks(7, 5));
//
//        comboMap19Percent.put(1, p.getOffSuitCombosOfGivenRanks(5, 4));
//
//        comboMap8Percent.put(1, p.getOffSuitCombosOfGivenRanks(4, 3));
//
//        comboMap7Percent.put(1, p.getOffSuitCombosOfGivenRanks(5, 3));
//
//        comboMap5Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
//        comboMap5Percent.put(2, p.getOffSuitCombosOfGivenRanks(14, 13));
//        comboMap5Percent.put(3, p.getOffSuitCombosOfGivenRanks(14, 12));
//        comboMap5Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 13));
//        comboMap5Percent.put(5, p.getPocketPairCombosOfGivenRank(13));
//        comboMap5Percent.put(6, p.getSuitedCombosOfGivenRanks(14, 12));
//        comboMap5Percent.put(7, p.getPocketPairCombosOfGivenRank(12));
//        comboMap5Percent.put(8, p.getPocketPairCombosOfGivenRank(11));
//
//        allCombosNoRestCombos.add(comboMap100Percent);
//        allCombosNoRestCombos.add(comboMap94Percent);
//        allCombosNoRestCombos.add(comboMap89Percent);
//        allCombosNoRestCombos.add(comboMap80Percent);
//        allCombosNoRestCombos.add(comboMap73Percent);
//        allCombosNoRestCombos.add(comboMap50Percent);
//        allCombosNoRestCombos.add(comboMap34Percent);
//        allCombosNoRestCombos.add(comboMap29Percent);
//        allCombosNoRestCombos.add(comboMap27Percent);
//        allCombosNoRestCombos.add(comboMap19Percent);
//        allCombosNoRestCombos.add(comboMap8Percent);
//        allCombosNoRestCombos.add(comboMap7Percent);
//        allCombosNoRestCombos.add(comboMap5Percent);
//
//        comboMapRest7Percent.put(1, p.removeCombosThatCouldBeInOtherMapsFromRestMap(allCombosNoRestCombos));
//
//        comboMapAllPossibleStartHands.put(1, new BoardEvaluator().getAllPossibleStartHandsAsSets());
//    }

    public Map<Integer, Set<Card>> getOpponentCall3betRange() {
        PreflopRangeBuilderUtil p = new PreflopRangeBuilderUtil();
        Map<Integer, Set<Card>> opponentCall3betRange = new HashMap<>();

        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap100Percent, 1);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap94Percent, 0.94);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap89Percent, 0.89);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap80Percent, 0.8);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap73Percent, 0.73);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap50Percent, 0.5);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap34Percent, 0.34);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap29Percent, 0.29);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap27Percent, 0.27);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap19Percent, 0.19);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap8Percent, 0.08);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap7Percent, 0.07);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMap5Percent, 0.05);

        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMapRest7Percent, 0.07);
        opponentCall3betRange = p.addCombosToIncludeInOpponentPreflopRange(opponentCall3betRange, comboMapAllPossibleStartHands, 0.03);

        opponentCall3betRange = p.removeDoubleCombos(opponentCall3betRange);

        return opponentCall3betRange;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap100Percent() {
        return comboMap100Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap94Percent() {
        return comboMap94Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap89Percent() {
        return comboMap89Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap80Percent() {
        return comboMap80Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap73Percent() {
        return comboMap73Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap50Percent() {
        return comboMap50Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap34Percent() {
        return comboMap34Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap29Percent() {
        return comboMap29Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap27Percent() {
        return comboMap27Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap19Percent() {
        return comboMap19Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap8Percent() {
        return comboMap8Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap7Percent() {
        return comboMap7Percent;
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap5Percent() {
        return comboMap5Percent;
    }
}
