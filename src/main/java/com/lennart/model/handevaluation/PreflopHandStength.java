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

    private List<List<Card>> get0_5_group() {





        return null;
    }




    //1326
        //14 groepen van 66 combos
        //6 groepen van 67 combos





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
