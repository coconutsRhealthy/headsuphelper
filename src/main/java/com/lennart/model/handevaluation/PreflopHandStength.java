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

        List<Card> a = new ArrayList<>();
        a.add(new Card(8, 'c'));
        a.add(new Card(5, 'd'));

        List<Card> b = new ArrayList<>();



        return 0.0;
    }






    //1326
        //14 groepen van 66 combos
        //6 groepen van 67 combos



    //********************************

    //0-5
        //32o
        //42o
        //52o
        //62o
        //72o
        //82o

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

    //5-10
        //92o
        //43o
        //63o
        //73o
        //83o

    private Map<Integer, Set<Card>> get5_10_group() {
        Map<Integer, Set<Card>> group_5_10 = new HashMap<>();

        group_5_10.putAll(getOffSuitCombosOfGivenRanks(9, 2));
        group_5_10.putAll(getOffSuitCombosOfGivenRanks(4, 3));
        group_5_10.putAll(getOffSuitCombosOfGivenRanks(6, 3));
        group_5_10.putAll(getOffSuitCombosOfGivenRanks(7, 3));
        group_5_10.putAll(getOffSuitCombosOfGivenRanks(8, 3));

        return group_5_10;
    }

    //10-15
        //93o
        //74o
        //84o
        //94o
        //53o

    private Map<Integer, Set<Card>> get10_15_group() {
        Map<Integer, Set<Card>> group_10_15 = new HashMap<>();

        group_10_15.putAll(getOffSuitCombosOfGivenRanks(9, 3));
        group_10_15.putAll(getOffSuitCombosOfGivenRanks(7, 4));
        group_10_15.putAll(getOffSuitCombosOfGivenRanks(8, 4));
        group_10_15.putAll(getOffSuitCombosOfGivenRanks(9, 4));
        group_10_15.putAll(getOffSuitCombosOfGivenRanks(5, 3));

        return group_10_15;
    }

    //15-20
        //T2o
        //J2o
        //T3o
        //T4o
        //64o
        //32s
        //42s
        //72s

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

    //20-25
        //J3o
        //85o
        //95o
        //T5o
        //54o
        //62s
        //82s

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

    //25-30
        //Q2o
        //J4o
        //75o
        //65o
        //52s
        //92s
        //83s
        //73s

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

    //30-35
        //Q3o
        //J5o
        //T6o
        //96o
        //93s
        //63s
        //53s
        //43s

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

    //35-40
        //K2o
        //Q4o
        //J6o
        //86o
        //76o
        //T2s
        //94s
        //84s
        //74s

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

    //40-45
        //K3o
        //Q5o
        //97o
        //22
        //J2s
        //T3s
        //T4s
        //64s
        //54s

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

    //45-50
        //K4o
        //Q6o
        //T7o
        //87o
        //J3s
        //T5s
        //95s
        //85s
        //75s

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

    //50-55
        //K5o
        //Q7o
        //J7o
        //33
        //Q2s
        //Q3s
        //J4s
        //J5s
        //65s

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

    //55-60
        //A2o
        //K6o
        //T8o
        //98o
        //J6s
        //T6s
        //96s
        //86s
        //76s

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

    //60-65
        //A3o
        //K7o
        //Q8o
        //J8o
        //44
        //K2s
        //Q4s
        //Q5s

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

    //65-70
        //A4o
        //A6o
        //K8o
        //K3s
        //K4s
        //Q6s
        //J7s
        //T7s
        //97s
        //87s

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

    //70-75
        //A5o
        //Q9o
        //J9o
        //T9o
        //55
        //K5s
        //Q7s
        //98s

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

    //75-80
        //A7o
        //A8o
        //K9o
        //A2s
        //A3s
        //K6s
        //K7s
        //T8s
        //J8s
        //Q8s

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

    //80-85
        //A9o
        //QTo
        //JTo
        //66
        //A4s
        //A5s
        //A6s
        //K8s
        //T9s
        //J9s
        //Q9s

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

    //85-90
        //ATo
        //KTo
        //KJo
        //QJo
        //77
        //A7s
        //A8s
        //K9s
        //JTs

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

    //90-95
        //AJo
        //AQo
        //KQo
        //88
        //A9s
        //QTs
        //KTs
        //ATs
        //QJs
        //KJs

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

    //95-100
        //AKo
        //99
        //TT
        //JJ
        //QQ
        //KK
        //AA
        //AKs
        //KQs
        //AQs
        //AJs

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






    public static void main(String[] args) {
        PreflopHandStength preflopHandStength = new PreflopHandStength();

        System.out.println(preflopHandStength.getPocketPairCombosOfGivenRank(6).size());
        System.out.println(preflopHandStength.getSuitedCombosOfGivenRanks(7, 8).size());
        System.out.println(preflopHandStength.getOffSuitCombosOfGivenRanks(3, 8).size());

    }



//    private Map<Integer, List<Card>> getAllPossibleStartHandsInitialize() {
//        Map<Integer, List<Card>> allPossibleStartHands = new HashMap<>();
//        List<Card> completeCardDeck = getCompleteCardDeck();
//
//        int i = 1;
//        for(int z = 0; z < 52; z++) {
//            for(int q = 0; q < 52; q++) {
//                if(!completeCardDeck.get(z).equals(completeCardDeck.get(q))) {
//                    allPossibleStartHands.put(i, new ArrayList<>());
//                    allPossibleStartHands.get(i).add(completeCardDeck.get(z));
//                    allPossibleStartHands.get(i).add(completeCardDeck.get(q));
//                    i++;
//                }
//            }
//        }
//
//        List<List<Card>> asList = new ArrayList<>(allPossibleStartHands.values());
//        Set<Set<Card>> asSet = new HashSet<>();
//
//        allPossibleStartHands.clear();
//
//        for(List<Card> l : asList) {
//            Set<Card> s = new HashSet<>();
//            s.addAll(l);
//            asSet.add(s);
//        }
//
//        for(Set<Card> startHand : asSet) {
//            List<Card> l = new ArrayList<>();
//            l.addAll(startHand);
//            allPossibleStartHands.put(allPossibleStartHands.size(), l);
//        }
//        return allPossibleStartHands;
//    }
//
//    public List<Card> getCompleteCardDeck() {
//        List<Card> completeCardDeck = new ArrayList<>();
//
//        for(int i = 2; i <= 14; i++) {
//            for(int z = 1; z <= 4; z++) {
//                if(z == 1) {
//                    completeCardDeck.add(new Card(i, 's'));
//                }
//                if(z == 2) {
//                    completeCardDeck.add(new Card(i, 'c'));
//                }
//                if(z == 3) {
//                    completeCardDeck.add(new Card(i, 'd'));
//                }
//                if(z == 4) {
//                    completeCardDeck.add(new Card(i, 'h'));
//                }
//            }
//        }
//        return completeCardDeck;
//    }




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
