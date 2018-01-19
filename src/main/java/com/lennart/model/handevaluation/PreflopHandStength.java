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

    public static void main(String[] args) {
        PreflopHandStength preflopHandStength = new PreflopHandStength();

//        System.out.println(preflopHandStength.getPocketPairCombosOfGivenRank(6).size());
//        System.out.println(preflopHandStength.getSuitedCombosOfGivenRanks(7, 8).size());
//        System.out.println(preflopHandStength.getOffSuitCombosOfGivenRanks(3, 8).size());


        List<Card> test = new ArrayList<>();
        test.add(new Card(3, 'd'));
        test.add(new Card(3, 'c'));

        double d = preflopHandStength.getPreflopHandStength(test);
        System.out.println(d);
    }

    public double getPreflopHandStength(List<Card> holeCards) {
        double handstrength = -1;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(holeCards);

        Map<Double, Map<Integer, Set<Card>>> allGroups = getMapWithAllPreflopHandstrengthGroups();

        loop: for (Map.Entry<Double, Map<Integer, Set<Card>>> entry : allGroups.entrySet()) {
            Map<Integer, Set<Card>> group = entry.getValue();

            for (Map.Entry<Integer, Set<Card>> entry2 : group.entrySet()) {
                if(holeCardsAsSet.equals(entry2.getValue())) {
                    handstrength = entry.getKey();
                    break loop;
                }
            }
        }

        return handstrength;
    }


    private Map<Double, Map<Integer, Set<Card>>> getMapWithAllPreflopHandstrengthGroups() {
        Map<Double, Map<Integer, Set<Card>>> allGroups = new HashMap<>();

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

    private Map<Integer, Set<Card>> get0_5_group() {
        Map<Integer, Set<Card>> group_0_5 = new HashMap<>();

        group_0_5.putAll(getOffSuitCombosOfGivenRanks(3, 2));
        group_0_5.putAll(getOffSuitCombosOfGivenRanks(4, 2));
        group_0_5.putAll(getOffSuitCombosOfGivenRanks(5, 2));
        group_0_5.putAll(getOffSuitCombosOfGivenRanks(6, 2));
        group_0_5.putAll(getOffSuitCombosOfGivenRanks(7, 2));
        group_0_5.putAll(getOffSuitCombosOfGivenRanks(8, 2));

        return group_0_5;
    }

    private Map<Integer, Set<Card>> get5_10_group() {
        Map<Integer, Set<Card>> group_5_10 = new HashMap<>();

        group_5_10.putAll(getOffSuitCombosOfGivenRanks(9, 2));
        group_5_10.putAll(getOffSuitCombosOfGivenRanks(4, 3));
        group_5_10.putAll(getOffSuitCombosOfGivenRanks(6, 3));
        group_5_10.putAll(getOffSuitCombosOfGivenRanks(7, 3));
        group_5_10.putAll(getOffSuitCombosOfGivenRanks(8, 3));

        return group_5_10;
    }

    private Map<Integer, Set<Card>> get10_15_group() {
        Map<Integer, Set<Card>> group_10_15 = new HashMap<>();

        group_10_15.putAll(getOffSuitCombosOfGivenRanks(9, 3));
        group_10_15.putAll(getOffSuitCombosOfGivenRanks(7, 4));
        group_10_15.putAll(getOffSuitCombosOfGivenRanks(8, 4));
        group_10_15.putAll(getOffSuitCombosOfGivenRanks(9, 4));
        group_10_15.putAll(getOffSuitCombosOfGivenRanks(5, 3));

        return group_10_15;
    }

    private Map<Integer, Set<Card>> get15_20_group() {
        Map<Integer, Set<Card>> group_15_20 = new HashMap<>();

        group_15_20.putAll(getOffSuitCombosOfGivenRanks(10, 2));
        group_15_20.putAll(getOffSuitCombosOfGivenRanks(11, 2));
        group_15_20.putAll(getOffSuitCombosOfGivenRanks(10, 3));
        group_15_20.putAll(getOffSuitCombosOfGivenRanks(10, 4));
        group_15_20.putAll(getOffSuitCombosOfGivenRanks(6, 4));
        group_15_20.putAll(getSuitedCombosOfGivenRanks(3, 2));
        group_15_20.putAll(getSuitedCombosOfGivenRanks(4, 2));
        group_15_20.putAll(getSuitedCombosOfGivenRanks(7, 2));

        return group_15_20;
    }

    private Map<Integer, Set<Card>> get20_25_group() {
        Map<Integer, Set<Card>> group_20_25 = new HashMap<>();

        group_20_25.putAll(getOffSuitCombosOfGivenRanks(11, 3));
        group_20_25.putAll(getOffSuitCombosOfGivenRanks(8, 5));
        group_20_25.putAll(getOffSuitCombosOfGivenRanks(9, 5));
        group_20_25.putAll(getOffSuitCombosOfGivenRanks(10, 5));
        group_20_25.putAll(getOffSuitCombosOfGivenRanks(5, 4));
        group_20_25.putAll(getSuitedCombosOfGivenRanks(6, 2));
        group_20_25.putAll(getSuitedCombosOfGivenRanks(8, 2));

        return group_20_25;
    }

    private Map<Integer, Set<Card>> get25_30_group() {
        Map<Integer, Set<Card>> group_25_30 = new HashMap<>();

        group_25_30.putAll(getOffSuitCombosOfGivenRanks(12, 2));
        group_25_30.putAll(getOffSuitCombosOfGivenRanks(11, 4));
        group_25_30.putAll(getOffSuitCombosOfGivenRanks(7, 5));
        group_25_30.putAll(getOffSuitCombosOfGivenRanks(6, 5));
        group_25_30.putAll(getSuitedCombosOfGivenRanks(5, 2));
        group_25_30.putAll(getSuitedCombosOfGivenRanks(9, 2));
        group_25_30.putAll(getSuitedCombosOfGivenRanks(8, 3));
        group_25_30.putAll(getSuitedCombosOfGivenRanks(7, 3));

        return group_25_30;
    }

    private Map<Integer, Set<Card>> get30_35_group() {
        Map<Integer, Set<Card>> group_30_35 = new HashMap<>();

        group_30_35.putAll(getOffSuitCombosOfGivenRanks(12, 3));
        group_30_35.putAll(getOffSuitCombosOfGivenRanks(11, 5));
        group_30_35.putAll(getOffSuitCombosOfGivenRanks(10, 6));
        group_30_35.putAll(getOffSuitCombosOfGivenRanks(9, 6));
        group_30_35.putAll(getSuitedCombosOfGivenRanks(9, 3));
        group_30_35.putAll(getSuitedCombosOfGivenRanks(6, 3));
        group_30_35.putAll(getSuitedCombosOfGivenRanks(5, 3));
        group_30_35.putAll(getSuitedCombosOfGivenRanks(4, 3));

        return group_30_35;
    }

    private Map<Integer, Set<Card>> get35_40_group() {
        Map<Integer, Set<Card>> group_35_40 = new HashMap<>();

        group_35_40.putAll(getOffSuitCombosOfGivenRanks(13, 2));
        group_35_40.putAll(getOffSuitCombosOfGivenRanks(12, 4));
        group_35_40.putAll(getOffSuitCombosOfGivenRanks(11, 6));
        group_35_40.putAll(getOffSuitCombosOfGivenRanks(8, 6));
        group_35_40.putAll(getOffSuitCombosOfGivenRanks(7, 6));
        group_35_40.putAll(getSuitedCombosOfGivenRanks(10, 2));
        group_35_40.putAll(getSuitedCombosOfGivenRanks(9, 4));
        group_35_40.putAll(getSuitedCombosOfGivenRanks(8, 4));
        group_35_40.putAll(getSuitedCombosOfGivenRanks(7, 4));

        return group_35_40;
    }

    private Map<Integer, Set<Card>> get40_45_group() {
        Map<Integer, Set<Card>> group_40_45 = new HashMap<>();

        group_40_45.putAll(getOffSuitCombosOfGivenRanks(13, 3));
        group_40_45.putAll(getOffSuitCombosOfGivenRanks(12, 5));
        group_40_45.putAll(getOffSuitCombosOfGivenRanks(9, 7));
        group_40_45.putAll(getSuitedCombosOfGivenRanks(11, 2));
        group_40_45.putAll(getSuitedCombosOfGivenRanks(10, 3));
        group_40_45.putAll(getSuitedCombosOfGivenRanks(10, 4));
        group_40_45.putAll(getSuitedCombosOfGivenRanks(6, 4));
        group_40_45.putAll(getSuitedCombosOfGivenRanks(5, 4));
        group_40_45.putAll(getPocketPairCombosOfGivenRank(2));

        return group_40_45;
    }

    private Map<Integer, Set<Card>> get45_50_group() {
        Map<Integer, Set<Card>> group_45_50 = new HashMap<>();

        group_45_50.putAll(getOffSuitCombosOfGivenRanks(13, 4));
        group_45_50.putAll(getOffSuitCombosOfGivenRanks(12, 6));
        group_45_50.putAll(getOffSuitCombosOfGivenRanks(10, 7));
        group_45_50.putAll(getOffSuitCombosOfGivenRanks(8, 7));
        group_45_50.putAll(getSuitedCombosOfGivenRanks(11, 3));
        group_45_50.putAll(getSuitedCombosOfGivenRanks(10, 5));
        group_45_50.putAll(getSuitedCombosOfGivenRanks(9, 5));
        group_45_50.putAll(getSuitedCombosOfGivenRanks(8, 5));
        group_45_50.putAll(getSuitedCombosOfGivenRanks(7, 5));

        return group_45_50;
    }

    private Map<Integer, Set<Card>> get50_55_group() {
        Map<Integer, Set<Card>> group_50_55 = new HashMap<>();

        group_50_55.putAll(getOffSuitCombosOfGivenRanks(13, 5));
        group_50_55.putAll(getOffSuitCombosOfGivenRanks(12, 7));
        group_50_55.putAll(getOffSuitCombosOfGivenRanks(11, 7));
        group_50_55.putAll(getSuitedCombosOfGivenRanks(12, 2));
        group_50_55.putAll(getSuitedCombosOfGivenRanks(12, 3));
        group_50_55.putAll(getSuitedCombosOfGivenRanks(11, 4));
        group_50_55.putAll(getSuitedCombosOfGivenRanks(11, 5));
        group_50_55.putAll(getSuitedCombosOfGivenRanks(6, 5));
        group_50_55.putAll(getPocketPairCombosOfGivenRank(3));

        return group_50_55;
    }

    private Map<Integer, Set<Card>> get55_60_group() {
        Map<Integer, Set<Card>> group_55_60 = new HashMap<>();

        group_55_60.putAll(getOffSuitCombosOfGivenRanks(14, 2));
        group_55_60.putAll(getOffSuitCombosOfGivenRanks(13, 6));
        group_55_60.putAll(getOffSuitCombosOfGivenRanks(10, 8));
        group_55_60.putAll(getOffSuitCombosOfGivenRanks(9, 8));
        group_55_60.putAll(getSuitedCombosOfGivenRanks(11, 6));
        group_55_60.putAll(getSuitedCombosOfGivenRanks(10, 6));
        group_55_60.putAll(getSuitedCombosOfGivenRanks(9, 6));
        group_55_60.putAll(getSuitedCombosOfGivenRanks(8, 6));
        group_55_60.putAll(getSuitedCombosOfGivenRanks(7, 6));

        return group_55_60;
    }

    private Map<Integer, Set<Card>> get60_65_group() {
        Map<Integer, Set<Card>> group_60_65 = new HashMap<>();

        group_60_65.putAll(getOffSuitCombosOfGivenRanks(14, 3));
        group_60_65.putAll(getOffSuitCombosOfGivenRanks(13, 7));
        group_60_65.putAll(getOffSuitCombosOfGivenRanks(12, 8));
        group_60_65.putAll(getOffSuitCombosOfGivenRanks(11, 8));
        group_60_65.putAll(getSuitedCombosOfGivenRanks(13, 2));
        group_60_65.putAll(getSuitedCombosOfGivenRanks(12, 4));
        group_60_65.putAll(getSuitedCombosOfGivenRanks(12, 5));
        group_60_65.putAll(getPocketPairCombosOfGivenRank(4));

        return group_60_65;
    }

    private Map<Integer, Set<Card>> get65_70_group() {
        Map<Integer, Set<Card>> group_65_70 = new HashMap<>();

        group_65_70.putAll(getOffSuitCombosOfGivenRanks(14, 4));
        group_65_70.putAll(getOffSuitCombosOfGivenRanks(14, 6));
        group_65_70.putAll(getOffSuitCombosOfGivenRanks(13, 8));
        group_65_70.putAll(getSuitedCombosOfGivenRanks(13, 3));
        group_65_70.putAll(getSuitedCombosOfGivenRanks(13, 4));
        group_65_70.putAll(getSuitedCombosOfGivenRanks(12, 6));
        group_65_70.putAll(getSuitedCombosOfGivenRanks(11, 7));
        group_65_70.putAll(getSuitedCombosOfGivenRanks(10, 7));
        group_65_70.putAll(getSuitedCombosOfGivenRanks(9, 7));
        group_65_70.putAll(getSuitedCombosOfGivenRanks(8, 7));

        return group_65_70;
    }

    private Map<Integer, Set<Card>> get70_75_group() {
        Map<Integer, Set<Card>> group_70_75 = new HashMap<>();

        group_70_75.putAll(getOffSuitCombosOfGivenRanks(14, 5));
        group_70_75.putAll(getOffSuitCombosOfGivenRanks(12, 9));
        group_70_75.putAll(getOffSuitCombosOfGivenRanks(11, 9));
        group_70_75.putAll(getOffSuitCombosOfGivenRanks(10, 9));
        group_70_75.putAll(getSuitedCombosOfGivenRanks(13, 5));
        group_70_75.putAll(getSuitedCombosOfGivenRanks(12, 7));
        group_70_75.putAll(getSuitedCombosOfGivenRanks(9, 8));
        group_70_75.putAll(getPocketPairCombosOfGivenRank(5));

        return group_70_75;
    }

    private Map<Integer, Set<Card>> get75_80_group() {
        Map<Integer, Set<Card>> group_75_80 = new HashMap<>();

        group_75_80.putAll(getOffSuitCombosOfGivenRanks(14, 7));
        group_75_80.putAll(getOffSuitCombosOfGivenRanks(14, 8));
        group_75_80.putAll(getOffSuitCombosOfGivenRanks(13, 9));
        group_75_80.putAll(getSuitedCombosOfGivenRanks(14, 2));
        group_75_80.putAll(getSuitedCombosOfGivenRanks(14, 3));
        group_75_80.putAll(getSuitedCombosOfGivenRanks(13, 6));
        group_75_80.putAll(getSuitedCombosOfGivenRanks(13, 7));
        group_75_80.putAll(getSuitedCombosOfGivenRanks(10, 8));
        group_75_80.putAll(getSuitedCombosOfGivenRanks(11, 8));
        group_75_80.putAll(getSuitedCombosOfGivenRanks(12, 8));

        return group_75_80;
    }

    private Map<Integer, Set<Card>> get80_85_group() {
        Map<Integer, Set<Card>> group_80_85 = new HashMap<>();

        group_80_85.putAll(getOffSuitCombosOfGivenRanks(14, 9));
        group_80_85.putAll(getOffSuitCombosOfGivenRanks(12, 10));
        group_80_85.putAll(getOffSuitCombosOfGivenRanks(11, 10));
        group_80_85.putAll(getSuitedCombosOfGivenRanks(14, 4));
        group_80_85.putAll(getSuitedCombosOfGivenRanks(14, 5));
        group_80_85.putAll(getSuitedCombosOfGivenRanks(14, 6));
        group_80_85.putAll(getSuitedCombosOfGivenRanks(13, 8));
        group_80_85.putAll(getSuitedCombosOfGivenRanks(10, 9));
        group_80_85.putAll(getSuitedCombosOfGivenRanks(11, 9));
        group_80_85.putAll(getSuitedCombosOfGivenRanks(12, 9));
        group_80_85.putAll(getPocketPairCombosOfGivenRank(6));

        return group_80_85;
    }

    private Map<Integer, Set<Card>> get85_90_group() {
        Map<Integer, Set<Card>> group_85_90 = new HashMap<>();

        group_85_90.putAll(getOffSuitCombosOfGivenRanks(14, 10));
        group_85_90.putAll(getOffSuitCombosOfGivenRanks(13, 10));
        group_85_90.putAll(getOffSuitCombosOfGivenRanks(13, 11));
        group_85_90.putAll(getOffSuitCombosOfGivenRanks(12, 11));
        group_85_90.putAll(getSuitedCombosOfGivenRanks(14, 7));
        group_85_90.putAll(getSuitedCombosOfGivenRanks(14, 8));
        group_85_90.putAll(getSuitedCombosOfGivenRanks(13, 9));
        group_85_90.putAll(getSuitedCombosOfGivenRanks(11, 10));
        group_85_90.putAll(getPocketPairCombosOfGivenRank(7));

        return group_85_90;
    }

    private Map<Integer, Set<Card>> get90_95_group() {
        Map<Integer, Set<Card>> group_90_95 = new HashMap<>();

        group_90_95.putAll(getOffSuitCombosOfGivenRanks(14, 11));
        group_90_95.putAll(getOffSuitCombosOfGivenRanks(14, 12));
        group_90_95.putAll(getOffSuitCombosOfGivenRanks(13, 12));
        group_90_95.putAll(getSuitedCombosOfGivenRanks(14, 9));
        group_90_95.putAll(getSuitedCombosOfGivenRanks(12, 10));
        group_90_95.putAll(getSuitedCombosOfGivenRanks(13, 10));
        group_90_95.putAll(getSuitedCombosOfGivenRanks(14, 10));
        group_90_95.putAll(getSuitedCombosOfGivenRanks(12, 11));
        group_90_95.putAll(getSuitedCombosOfGivenRanks(13, 11));
        group_90_95.putAll(getPocketPairCombosOfGivenRank(8));

        return group_90_95;
    }

    private Map<Integer, Set<Card>> get95_100_group() {
        Map<Integer, Set<Card>> group_95_100 = new HashMap<>();

        group_95_100.putAll(getOffSuitCombosOfGivenRanks(14, 13));
        group_95_100.putAll(getSuitedCombosOfGivenRanks(14, 13));
        group_95_100.putAll(getSuitedCombosOfGivenRanks(13, 12));
        group_95_100.putAll(getSuitedCombosOfGivenRanks(14, 12));
        group_95_100.putAll(getSuitedCombosOfGivenRanks(14, 11));
        group_95_100.putAll(getPocketPairCombosOfGivenRank(9));
        group_95_100.putAll(getPocketPairCombosOfGivenRank(10));
        group_95_100.putAll(getPocketPairCombosOfGivenRank(11));
        group_95_100.putAll(getPocketPairCombosOfGivenRank(12));
        group_95_100.putAll(getPocketPairCombosOfGivenRank(13));
        group_95_100.putAll(getPocketPairCombosOfGivenRank(14));

        return group_95_100;
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
}
