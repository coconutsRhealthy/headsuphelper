package com.lennart.model.handevaluation;

import com.lennart.model.card.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lpo21630 on 18-1-2018.
 */
public class PreflopHandStength {

    public double getPreflopHandStength(List<Card> holeCards) {
        double handstrength = -1;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(holeCards);

        Map<Double, List<Set<Card>>> allGroups = getMapWithAllPreflopHandstrengthGroups();

        loop: for (Map.Entry<Double, List<Set<Card>>> entry : allGroups.entrySet()) {
            List<Set<Card>> group = entry.getValue();

            for (Set<Card> combo : group) {
                if(holeCardsAsSet.equals(combo)) {
                    handstrength = entry.getKey();
                    break loop;
                }
            }
        }

        return handstrength;
    }

    public Map<Double, List<Set<Card>>> getMapWithAllPreflopHandstrengthGroups() {
        Map<Double, List<Set<Card>>> allGroups = new HashMap<>();

        allGroups.put(0.02, get0_5_group());
        allGroups.put(0.07, get5_10_group());
        allGroups.put(0.12, get10_15_group());
        allGroups.put(0.17, get15_20_group());
        allGroups.put(0.22, get20_25_group());
        allGroups.put(0.27, get25_30_group());
        allGroups.put(0.32, get30_35_group());
        allGroups.put(0.37, get35_40_group());
        allGroups.put(0.42, get40_45_group());
        allGroups.put(0.47, get45_50_group());
        allGroups.put(0.52, get50_55_group());
        allGroups.put(0.57, get55_60_group());
        allGroups.put(0.62, get60_65_group());
        allGroups.put(0.67, get65_70_group());
        allGroups.put(0.72, get70_75_group());
        allGroups.put(0.77, get75_80_group());
        allGroups.put(0.82, get80_85_group());
        allGroups.put(0.87, get85_90_group());
        allGroups.put(0.92, get90_95_group());
        allGroups.put(0.97, get95_100_group());

        return allGroups;
    }

    private List<Set<Card>> get95_100_group() {
        List<Set<Card>> group_95_100 = new ArrayList<>();

        group_95_100.addAll(getPocketPairCombosOfGivenRank(14).values());
        group_95_100.addAll(getPocketPairCombosOfGivenRank(13).values());
        group_95_100.addAll(getPocketPairCombosOfGivenRank(12).values());
        group_95_100.addAll(getPocketPairCombosOfGivenRank(11).values());
        group_95_100.addAll(getPocketPairCombosOfGivenRank(10).values());
        group_95_100.addAll(getSuitedCombosOfGivenRanks(14, 13).values());
        group_95_100.addAll(getOffSuitCombosOfGivenRanks(14, 13).values());
        group_95_100.addAll(getPocketPairCombosOfGivenRank(9).values());
        group_95_100.addAll(getSuitedCombosOfGivenRanks(14, 12).values());
        group_95_100.addAll(getPocketPairCombosOfGivenRank(8).values());

        return group_95_100;
    }

    private List<Set<Card>> get90_95_group() {
        List<Set<Card>> group_90_95 = new ArrayList<>();

        group_90_95.addAll(getOffSuitCombosOfGivenRanks(14, 12).values());
        group_90_95.addAll(getSuitedCombosOfGivenRanks(14, 11).values());
        group_90_95.addAll(getPocketPairCombosOfGivenRank(7).values());
        group_90_95.addAll(getOffSuitCombosOfGivenRanks(14, 11).values());
        group_90_95.addAll(getSuitedCombosOfGivenRanks(14, 10).values());
        group_90_95.addAll(getOffSuitCombosOfGivenRanks(14, 10).values());
        group_90_95.addAll(getPocketPairCombosOfGivenRank(6).values());
        group_90_95.addAll(getSuitedCombosOfGivenRanks(14, 9).values());

        return group_90_95;
    }

    private List<Set<Card>> get85_90_group() {
        List<Set<Card>> group_85_90 = new ArrayList<>();

        group_85_90.addAll(getOffSuitCombosOfGivenRanks(14, 9).values());
        group_85_90.addAll(getSuitedCombosOfGivenRanks(14, 8).values());
        group_85_90.addAll(getOffSuitCombosOfGivenRanks(14, 8).values());
        group_85_90.addAll(getSuitedCombosOfGivenRanks(14, 7).values());
        group_85_90.addAll(getOffSuitCombosOfGivenRanks(14, 7).values());
        group_85_90.addAll(getSuitedCombosOfGivenRanks(14, 6).values());
        group_85_90.addAll(getOffSuitCombosOfGivenRanks(14, 6).values());
        group_85_90.addAll(getPocketPairCombosOfGivenRank(5).values());

        return group_85_90;
    }

    private List<Set<Card>> get80_85_group() {
        List<Set<Card>> group_80_85 = new ArrayList<>();

        group_80_85.addAll(getSuitedCombosOfGivenRanks(14, 5).values());
        group_80_85.addAll(getOffSuitCombosOfGivenRanks(14, 5).values());
        group_80_85.addAll(getSuitedCombosOfGivenRanks(14, 4).values());
        group_80_85.addAll(getSuitedCombosOfGivenRanks(14, 3).values());
        group_80_85.addAll(getSuitedCombosOfGivenRanks(14, 2).values());
        group_80_85.addAll(getSuitedCombosOfGivenRanks(13, 12).values());
        group_80_85.addAll(getOffSuitCombosOfGivenRanks(13, 12).values());
        group_80_85.addAll(getSuitedCombosOfGivenRanks(13, 11).values());
        group_80_85.addAll(getOffSuitCombosOfGivenRanks(13, 11).values());
        group_80_85.addAll(getSuitedCombosOfGivenRanks(13, 10).values());

        return group_80_85;
    }

    private List<Set<Card>> get75_80_group() {
        List<Set<Card>> group_75_80 = new ArrayList<>();

        group_75_80.addAll(getPocketPairCombosOfGivenRank(4).values());
        group_75_80.addAll(getOffSuitCombosOfGivenRanks(13, 10).values());
        group_75_80.addAll(getSuitedCombosOfGivenRanks(13, 9).values());
        group_75_80.addAll(getSuitedCombosOfGivenRanks(12, 11).values());
        group_75_80.addAll(getSuitedCombosOfGivenRanks(12, 10).values());
        group_75_80.addAll(getPocketPairCombosOfGivenRank(3).values());
        group_75_80.addAll(getOffSuitCombosOfGivenRanks(12, 11).values());
        group_75_80.addAll(getOffSuitCombosOfGivenRanks(14, 4).values());
        group_75_80.addAll(getSuitedCombosOfGivenRanks(11, 10).values());
        group_75_80.addAll(getSuitedCombosOfGivenRanks(13, 8).values());

        return group_75_80;
    }

    private List<Set<Card>> get70_75_group() {
        List<Set<Card>> group_70_75 = new ArrayList<>();

        group_70_75.addAll(getOffSuitCombosOfGivenRanks(13, 9).values());
        group_70_75.addAll(getOffSuitCombosOfGivenRanks(14, 3).values());
        group_70_75.addAll(getSuitedCombosOfGivenRanks(12, 9).values());
        group_70_75.addAll(getOffSuitCombosOfGivenRanks(14, 2).values());
        group_70_75.addAll(getOffSuitCombosOfGivenRanks(12, 10).values());
        group_70_75.addAll(getSuitedCombosOfGivenRanks(13, 7).values());
        group_70_75.addAll(getPocketPairCombosOfGivenRank(2).values());
        group_70_75.addAll(getSuitedCombosOfGivenRanks(13, 6).values());

        return group_70_75;
    }

    private List<Set<Card>> get65_70_group() {
        List<Set<Card>> group_65_70 = new ArrayList<>();

        group_65_70.addAll(getOffSuitCombosOfGivenRanks(13, 8).values());
        group_65_70.addAll(getSuitedCombosOfGivenRanks(11, 9).values());
        group_65_70.addAll(getSuitedCombosOfGivenRanks(13, 5).values());
        group_65_70.addAll(getSuitedCombosOfGivenRanks(12, 8).values());
        group_65_70.addAll(getOffSuitCombosOfGivenRanks(11, 10).values());
        group_65_70.addAll(getOffSuitCombosOfGivenRanks(13, 7).values());
        group_65_70.addAll(getSuitedCombosOfGivenRanks(13, 4).values());
        group_65_70.addAll(getOffSuitCombosOfGivenRanks(12, 9).values());

        return group_65_70;
    }

    private List<Set<Card>> get60_65_group() {
        List<Set<Card>> group_60_65 = new ArrayList<>();

        group_60_65.addAll(getSuitedCombosOfGivenRanks(10, 9).values());
        group_60_65.addAll(getSuitedCombosOfGivenRanks(13, 3).values());
        group_60_65.addAll(getOffSuitCombosOfGivenRanks(13, 6).values());
        group_60_65.addAll(getSuitedCombosOfGivenRanks(13, 2).values());
        group_60_65.addAll(getSuitedCombosOfGivenRanks(11, 8).values());
        group_60_65.addAll(getSuitedCombosOfGivenRanks(12, 7).values());
        group_60_65.addAll(getOffSuitCombosOfGivenRanks(13, 5).values());
        group_60_65.addAll(getSuitedCombosOfGivenRanks(12, 6).values());
        group_60_65.addAll(getOffSuitCombosOfGivenRanks(12, 8).values());

        return group_60_65;
    }

    private List<Set<Card>> get55_60_group() {
        List<Set<Card>> group_55_60 = new ArrayList<>();

        group_55_60.addAll(getOffSuitCombosOfGivenRanks(11, 9).values());
        group_55_60.addAll(getSuitedCombosOfGivenRanks(10, 8).values());
        group_55_60.addAll(getOffSuitCombosOfGivenRanks(13, 4).values());
        group_55_60.addAll(getSuitedCombosOfGivenRanks(12, 5).values());
        group_55_60.addAll(getSuitedCombosOfGivenRanks(11, 7).values());
        group_55_60.addAll(getOffSuitCombosOfGivenRanks(13, 3).values());
        group_55_60.addAll(getSuitedCombosOfGivenRanks(12, 4).values());
        group_55_60.addAll(getOffSuitCombosOfGivenRanks(10, 9).values());
        group_55_60.addAll(getSuitedCombosOfGivenRanks(9, 8).values());

        return group_55_60;
    }

    private List<Set<Card>> get50_55_group() {
        List<Set<Card>> group_50_55 = new ArrayList<>();

        group_50_55.addAll(getOffSuitCombosOfGivenRanks(13, 2).values());
        group_50_55.addAll(getOffSuitCombosOfGivenRanks(12, 7).values());
        group_50_55.addAll(getSuitedCombosOfGivenRanks(12, 3).values());
        group_50_55.addAll(getOffSuitCombosOfGivenRanks(11, 8).values());
        group_50_55.addAll(getSuitedCombosOfGivenRanks(10, 7).values());
        group_50_55.addAll(getOffSuitCombosOfGivenRanks(12, 6).values());
        group_50_55.addAll(getSuitedCombosOfGivenRanks(12, 2).values());
        group_50_55.addAll(getSuitedCombosOfGivenRanks(11, 6).values());
        group_50_55.addAll(getSuitedCombosOfGivenRanks(9, 7).values());

        group_50_55.addAll(getSuitedCombosOfGivenRanks(8, 7).values());
        group_50_55.addAll(getSuitedCombosOfGivenRanks(8, 6).values());
        group_50_55.addAll(getSuitedCombosOfGivenRanks(7, 6).values());
        group_50_55.addAll(getSuitedCombosOfGivenRanks(7, 5).values());
        group_50_55.addAll(getSuitedCombosOfGivenRanks(6, 5).values());

        return group_50_55;
    }

    private List<Set<Card>> get45_50_group() {
        List<Set<Card>> group_45_50 = new ArrayList<>();

        group_45_50.addAll(getSuitedCombosOfGivenRanks(11, 5).values());
        group_45_50.addAll(getOffSuitCombosOfGivenRanks(12, 5).values());
        group_45_50.addAll(getOffSuitCombosOfGivenRanks(10, 8).values());
        group_45_50.addAll(getOffSuitCombosOfGivenRanks(11, 7).values());
        group_45_50.addAll(getSuitedCombosOfGivenRanks(10, 6).values());
        group_45_50.addAll(getOffSuitCombosOfGivenRanks(12, 4).values());
        group_45_50.addAll(getSuitedCombosOfGivenRanks(11, 4).values());

        return group_45_50;
    }

    private List<Set<Card>> get40_45_group() {
        List<Set<Card>> group_40_45 = new ArrayList<>();

        group_40_45.addAll(getOffSuitCombosOfGivenRanks(9, 8).values());
        group_40_45.addAll(getOffSuitCombosOfGivenRanks(12, 3).values());
        group_40_45.addAll(getSuitedCombosOfGivenRanks(11, 3).values());
        group_40_45.addAll(getSuitedCombosOfGivenRanks(9, 6).values());
        group_40_45.addAll(getOffSuitCombosOfGivenRanks(12, 2).values());
        group_40_45.addAll(getSuitedCombosOfGivenRanks(11, 2).values());
        group_40_45.addAll(getOffSuitCombosOfGivenRanks(10, 7).values());

        return group_40_45;
    }

    private List<Set<Card>> get35_40_group() {
        List<Set<Card>> group_35_40 = new ArrayList<>();

        group_35_40.addAll(getOffSuitCombosOfGivenRanks(11, 6).values());
        group_35_40.addAll(getSuitedCombosOfGivenRanks(10, 5).values());
        group_35_40.addAll(getSuitedCombosOfGivenRanks(10, 4).values());
        group_35_40.addAll(getOffSuitCombosOfGivenRanks(11, 5).values());
        group_35_40.addAll(getOffSuitCombosOfGivenRanks(9, 7).values());
        group_35_40.addAll(getSuitedCombosOfGivenRanks(9, 5).values());
        group_35_40.addAll(getSuitedCombosOfGivenRanks(10, 3).values());
        group_35_40.addAll(getSuitedCombosOfGivenRanks(8, 5).values());

        return group_35_40;
    }

    private List<Set<Card>> get30_35_group() {
        List<Set<Card>> group_30_35 = new ArrayList<>();

        group_30_35.addAll(getOffSuitCombosOfGivenRanks(11, 4).values());
        group_30_35.addAll(getOffSuitCombosOfGivenRanks(8, 7).values());
        group_30_35.addAll(getOffSuitCombosOfGivenRanks(10, 6).values());
        group_30_35.addAll(getSuitedCombosOfGivenRanks(5, 4).values());
        group_30_35.addAll(getOffSuitCombosOfGivenRanks(11, 3).values());
        group_30_35.addAll(getSuitedCombosOfGivenRanks(10, 2).values());
        group_30_35.addAll(getSuitedCombosOfGivenRanks(9, 4).values());
        group_30_35.addAll(getSuitedCombosOfGivenRanks(6, 4).values());

        return group_30_35;
    }

    private List<Set<Card>> get25_30_group() {
        List<Set<Card>> group_25_30 = new ArrayList<>();

        group_25_30.addAll(getOffSuitCombosOfGivenRanks(11, 2).values());
        group_25_30.addAll(getOffSuitCombosOfGivenRanks(9, 6).values());
        group_25_30.addAll(getSuitedCombosOfGivenRanks(9, 3).values());
        group_25_30.addAll(getOffSuitCombosOfGivenRanks(8, 6).values());
        group_25_30.addAll(getSuitedCombosOfGivenRanks(8, 4).values());
        group_25_30.addAll(getSuitedCombosOfGivenRanks(7, 4).values());
        group_25_30.addAll(getOffSuitCombosOfGivenRanks(10, 5).values());
        group_25_30.addAll(getOffSuitCombosOfGivenRanks(7, 6).values());

        return group_25_30;
    }

    private List<Set<Card>> get20_25_group() {
        List<Set<Card>> group_20_25 = new ArrayList<>();

        group_20_25.addAll(getSuitedCombosOfGivenRanks(5, 3).values());
        group_20_25.addAll(getSuitedCombosOfGivenRanks(9, 2).values());
        group_20_25.addAll(getOffSuitCombosOfGivenRanks(10, 4).values());
        group_20_25.addAll(getSuitedCombosOfGivenRanks(6, 3).values());
        group_20_25.addAll(getSuitedCombosOfGivenRanks(4, 3).values());
        group_20_25.addAll(getOffSuitCombosOfGivenRanks(9, 5).values());
        group_20_25.addAll(getOffSuitCombosOfGivenRanks(6, 5).values());
        group_20_25.addAll(getOffSuitCombosOfGivenRanks(8, 5).values());

        return group_20_25;
    }

    private List<Set<Card>> get15_20_group() {
        List<Set<Card>> group_15_20 = new ArrayList<>();

        group_15_20.addAll(getOffSuitCombosOfGivenRanks(10, 3).values());
        group_15_20.addAll(getSuitedCombosOfGivenRanks(8, 3).values());
        group_15_20.addAll(getOffSuitCombosOfGivenRanks(7, 5).values());
        group_15_20.addAll(getSuitedCombosOfGivenRanks(7, 3).values());
        group_15_20.addAll(getSuitedCombosOfGivenRanks(5, 2).values());
        group_15_20.addAll(getOffSuitCombosOfGivenRanks(10, 2).values());
        group_15_20.addAll(getSuitedCombosOfGivenRanks(8, 2).values());
        group_15_20.addAll(getOffSuitCombosOfGivenRanks(5, 4).values());
        group_15_20.addAll(getSuitedCombosOfGivenRanks(4, 2).values());
        group_15_20.addAll(getSuitedCombosOfGivenRanks(3, 2).values());

        return group_15_20;
    }

    private List<Set<Card>> get10_15_group() {
        List<Set<Card>> group_10_15 = new ArrayList<>();

        group_10_15.addAll(getOffSuitCombosOfGivenRanks(9, 4).values());
        group_10_15.addAll(getSuitedCombosOfGivenRanks(7, 2).values());
        group_10_15.addAll(getOffSuitCombosOfGivenRanks(6, 4).values());
        group_10_15.addAll(getSuitedCombosOfGivenRanks(6, 2).values());
        group_10_15.addAll(getOffSuitCombosOfGivenRanks(8, 4).values());
        group_10_15.addAll(getOffSuitCombosOfGivenRanks(7, 4).values());
        group_10_15.addAll(getOffSuitCombosOfGivenRanks(9, 3).values());

        return group_10_15;
    }

    private List<Set<Card>> get5_10_group() {
        List<Set<Card>> group_5_10 = new ArrayList<>();

        group_5_10.addAll(getOffSuitCombosOfGivenRanks(5, 3).values());
        group_5_10.addAll(getOffSuitCombosOfGivenRanks(9, 2).values());
        group_5_10.addAll(getOffSuitCombosOfGivenRanks(4, 3).values());
        group_5_10.addAll(getOffSuitCombosOfGivenRanks(8, 3).values());
        group_5_10.addAll(getOffSuitCombosOfGivenRanks(7, 3).values());
        group_5_10.addAll(getOffSuitCombosOfGivenRanks(6, 3).values());

        return group_5_10;
    }

    private List<Set<Card>> get0_5_group() {
        List<Set<Card>> group_0_5 = new ArrayList<>();

        group_0_5.addAll(getOffSuitCombosOfGivenRanks(8, 2).values());
        group_0_5.addAll(getOffSuitCombosOfGivenRanks(5, 2).values());
        group_0_5.addAll(getOffSuitCombosOfGivenRanks(6, 2).values());
        group_0_5.addAll(getOffSuitCombosOfGivenRanks(4, 2).values());
        group_0_5.addAll(getOffSuitCombosOfGivenRanks(7, 2).values());
        group_0_5.addAll(getOffSuitCombosOfGivenRanks(3, 2).values());

        return group_0_5;
    }

    private Map<Integer, Set<Card>> getSuitedCombosOfGivenRanks(int rankCard1, int rankCard2) {
        Map<Integer, Set<Card>> suitedCombosOfGivenRanks = new HashMap<>();
        List<Character> suits = new ArrayList<>();

        suits.add('s');
        suits.add('c');
        suits.add('d');
        suits.add('h');

        for(Character suit : suits) {
            Set<Card> combo = new HashSet<>();
            Card holeCard1 = new Card(rankCard1, suit);
            Card holeCard2 = new Card(rankCard2, suit);

            combo.add(holeCard1);
            combo.add(holeCard2);
            suitedCombosOfGivenRanks.put(suitedCombosOfGivenRanks.size(), combo);
        }
        return suitedCombosOfGivenRanks;
    }

    private Map<Integer, Set<Card>> getOffSuitCombosOfGivenRanks(int rankCard1, int rankCard2) {
        Map<Integer, Set<Card>> offSuitCombosOfGivenRanks = new HashMap<>();
        List<Character> suits = new ArrayList<>();

        suits.add('s');
        suits.add('c');
        suits.add('d');
        suits.add('h');

        for(Character suit1 : suits) {
            for(Character suit2 : suits) {
                if(suit1 != suit2) {
                    Set<Card> combo = new HashSet<>();
                    Card holeCard1 = new Card(rankCard1, suit1);
                    Card holeCard2 = new Card(rankCard2, suit2);

                    combo.add(holeCard1);
                    combo.add(holeCard2);
                    offSuitCombosOfGivenRanks.put(offSuitCombosOfGivenRanks.size(), combo);
                }
            }
        }
        return offSuitCombosOfGivenRanks;
    }

    private Map<Integer, Set<Card>> getPocketPairCombosOfGivenRank(int rank) {
        Map<Integer, Set<Card>> pocketPairCombosOfGivenRanks = new HashMap<>();
        Set<Set<Card>> setToTestForUniqueness = new HashSet<>();
        List<Character> suits = new ArrayList<>();

        suits.add('s');
        suits.add('c');
        suits.add('d');
        suits.add('h');

        for(Character suit1 : suits) {
            for(Character suit2 : suits) {
                if(suit1 != suit2) {
                    Set<Card> combo = new HashSet<>();
                    Card holeCard1 = new Card(rank, suit1);
                    Card holeCard2 = new Card(rank, suit2);
                    combo.add(holeCard1);
                    combo.add(holeCard2);
                    if (setToTestForUniqueness.add(combo)) {
                        pocketPairCombosOfGivenRanks.put(pocketPairCombosOfGivenRanks.size(), combo);
                    }
                }
            }
        }
        return pocketPairCombosOfGivenRanks;
    }

    public List<Set<Card>> getSpecificPreflopShovableHands() {
        List<Set<Card>> shovable = new ArrayList<>();

        shovable.addAll(getOffSuitCombosOfGivenRanks(14, 8).values());
        shovable.addAll(getOffSuitCombosOfGivenRanks(14, 9).values());
        shovable.addAll(getOffSuitCombosOfGivenRanks(14, 10).values());
        shovable.addAll(getOffSuitCombosOfGivenRanks(14, 11).values());
        shovable.addAll(getOffSuitCombosOfGivenRanks(14, 12).values());
        shovable.addAll(getOffSuitCombosOfGivenRanks(14, 13).values());

        shovable.addAll(getSuitedCombosOfGivenRanks(14, 2).values());
        shovable.addAll(getSuitedCombosOfGivenRanks(14, 3).values());
        shovable.addAll(getSuitedCombosOfGivenRanks(14, 4).values());
        shovable.addAll(getSuitedCombosOfGivenRanks(14, 5).values());
        shovable.addAll(getSuitedCombosOfGivenRanks(14, 6).values());
        shovable.addAll(getSuitedCombosOfGivenRanks(14, 7).values());
        shovable.addAll(getSuitedCombosOfGivenRanks(14, 8).values());
        shovable.addAll(getSuitedCombosOfGivenRanks(14, 9).values());
        shovable.addAll(getSuitedCombosOfGivenRanks(14, 10).values());
        shovable.addAll(getSuitedCombosOfGivenRanks(14, 11).values());
        shovable.addAll(getSuitedCombosOfGivenRanks(14, 12).values());
        shovable.addAll(getSuitedCombosOfGivenRanks(14, 13).values());

        shovable.addAll(getPocketPairCombosOfGivenRank(8).values());
        shovable.addAll(getPocketPairCombosOfGivenRank(9).values());
        shovable.addAll(getPocketPairCombosOfGivenRank(10).values());
        shovable.addAll(getPocketPairCombosOfGivenRank(11).values());
        shovable.addAll(getPocketPairCombosOfGivenRank(12).values());
        shovable.addAll(getPocketPairCombosOfGivenRank(13).values());
        shovable.addAll(getPocketPairCombosOfGivenRank(14).values());

        return shovable;
    }
}
