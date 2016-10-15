package com.lennart.model.rangebuilder.preflop;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by LennartMac on 15/10/16.
 */
public class PreflopRange {

    private static Map<Integer, Set<Card>> allStartHands = new HashMap<>();

    static {
        Map<Integer, List<Card>> allPossibleStartHands = new BoardEvaluator().getAllPossibleStartHands();

        List<List<Card>> asList = new ArrayList<>(allPossibleStartHands.values());
        Set<Set<Card>> asSet = new HashSet<>();

        for(List<Card> l : asList) {
            Set<Card> s = new HashSet<>();
            s.addAll(l);
            asSet.add(s);
        }

        for(Set<Card> combo : asSet) {
            allStartHands.put(allStartHands.size(), combo);
        }
    }

    public Map<Integer, Set<Card>> getBroadWayHoleCards() {
        Map<Integer, Set<Card>> offSuitBroadWayCards = getOffSuitHoleCards(10, 10);
        Map<Integer, Set<Card>> suitedBroadWayCards = getSuitedHoleCards(10, 10);

        Map<Integer, Set<Card>> broadWayHoleCards = new HashMap<>();

        for (Map.Entry<Integer, Set<Card>> entry : suitedBroadWayCards.entrySet()) {
            broadWayHoleCards.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Integer, Set<Card>> entry : offSuitBroadWayCards.entrySet()) {
            broadWayHoleCards.put(entry.getKey(), entry.getValue());
        }
        return broadWayHoleCards;
    }

    public Map<Integer, Set<Card>> getOffSuitHoleCards(int minimumRankOfHighestCard, int minimumRankOfLowestCard) {
        return getSuitedOrOffSuitHoleCards(minimumRankOfHighestCard, minimumRankOfLowestCard, false);
    }

    public Map<Integer, Set<Card>> getOffSuitConnectors(int minimumRankOfHighestCard) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 1, false);
    }

    public Map<Integer, Set<Card>> getOffSuitOneGappers(int minimumRankOfHighestCard) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 2, false);
    }

    public Map<Integer, Set<Card>> getOffSuitTwoGappers(int minimumRankOfHighestCard) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 3, false);
    }

    public Map<Integer, Set<Card>> getOffSuitThreeGappers(int minimumRankOfHighestCard) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 4, false);
    }

    public Map<Integer, Set<Card>> getPocketPairs(int minimumRankOfHighestCard) {
        Map<Integer, List<Card>> allPocketPairStartHands = new BoardEvaluator().getAllPocketPairStartHands();
        Map<Integer, Set<Card>> pocketPairs = new HashMap<>();

        List<List<Card>> asList = new ArrayList<>(allPocketPairStartHands.values());
        Set<Set<Card>> asSet = new HashSet<>();

        for(List<Card> l : asList) {
            if (l.get(0).getRank() >= minimumRankOfHighestCard) {
                Set<Card> s = new HashSet<>();
                s.addAll(l);
                asSet.add(s);
            }
        }
        for(Set<Card> pocketPairCombo : asSet) {
            pocketPairs.put(pocketPairs.size(), pocketPairCombo);
        }
        return pocketPairs;
    }

    public Map<Integer, Set<Card>> getSuitedHoleCards(int minimumRankOfHighestCard, int minimumRankOfLowestCard) {
        return getSuitedOrOffSuitHoleCards(minimumRankOfHighestCard, minimumRankOfLowestCard, true);
    }

    public Map<Integer, Set<Card>> getSuitedConnectors(int minimumRankOfHighestCard) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 1, true);
    }

    public Map<Integer, Set<Card>> getSuitedOneGappers(int minimumRankOfHighestCard) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 2, true);
    }

    public Map<Integer, Set<Card>> getSuitedTwoGappers(int minimumRankOfHighestCard) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 3, true);
    }

    public Map<Integer, Set<Card>> getSuitedThreeGappers(int minimumRankOfHighestCard) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 4, true);
    }


    //helper methods
    private Map<Integer, Set<Card>> getCopyOfStaticAllStartHands() {
        Map<Integer, Set<Card>> allStartHandsCopy = new HashMap<>();

        for (Map.Entry<Integer, Set<Card>> entry : allStartHands.entrySet()) {
            allStartHandsCopy.put(entry.getKey(), entry.getValue());
        }
        return allStartHandsCopy;
    }

    private Map<Integer, Set<Card>> getSuitedOrOffSuitConnectingCards(int rankOfHighestCard, int gapBetweenCards, boolean suited) {
        Map<Integer, Set<Card>> suitedOrOffSuitConnectors = new HashMap<>();

        for(Map.Entry<Integer, Set<Card>> entry : allStartHands.entrySet()) {
            List<Card> asList = new ArrayList<>(entry.getValue());
            if(suited) {
                if(asList.get(0).getSuit() == asList.get(1).getSuit()) {
                    List<Integer> comboRanks = new ArrayList<>();
                    comboRanks.add(asList.get(0).getRank());
                    comboRanks.add(asList.get(1).getRank());

                    if(Collections.max(comboRanks) >= rankOfHighestCard &&
                            Collections.max(comboRanks) == Collections.min(comboRanks) + gapBetweenCards) {
                        suitedOrOffSuitConnectors.put(suitedOrOffSuitConnectors.size(), entry.getValue());
                    }
                }
            } else {
                if(asList.get(0).getSuit() != asList.get(1).getSuit()) {
                    List<Integer> comboRanks = new ArrayList<>();
                    comboRanks.add(asList.get(0).getRank());
                    comboRanks.add(asList.get(1).getRank());

                    if(Collections.max(comboRanks) >= rankOfHighestCard &&
                            Collections.max(comboRanks) == Collections.min(comboRanks) + gapBetweenCards) {
                        suitedOrOffSuitConnectors.put(suitedOrOffSuitConnectors.size(), entry.getValue());
                    }
                }
            }
        }
        return suitedOrOffSuitConnectors;
    }

    private Map<Integer, Set<Card>> getSuitedOrOffSuitHoleCards(int rankOfHighestCard, int rankOfLowestCard, boolean suited) {
        Map<Integer, Set<Card>> suitedOrOffSuitHoleCards = new HashMap<>();

        for(Map.Entry<Integer, Set<Card>> entry : allStartHands.entrySet()) {
            List<Card> asList = new ArrayList<>(entry.getValue());
            if(suited) {
                if(asList.get(0).getSuit() == asList.get(1).getSuit()) {
                    List<Integer> comboRanks = new ArrayList<>();
                    comboRanks.add(asList.get(0).getRank());
                    comboRanks.add(asList.get(1).getRank());

                    if(Collections.max(comboRanks) >= rankOfHighestCard && Collections.min(comboRanks) >= rankOfLowestCard) {
                        suitedOrOffSuitHoleCards.put(suitedOrOffSuitHoleCards.size(), entry.getValue());
                    }
                }
            } else {
                if(asList.get(0).getSuit() != asList.get(1).getSuit()) {
                    List<Integer> comboRanks = new ArrayList<>();
                    comboRanks.add(asList.get(0).getRank());
                    comboRanks.add(asList.get(1).getRank());

                    if(Collections.max(comboRanks) >= rankOfHighestCard && Collections.min(comboRanks) >= rankOfLowestCard) {
                        suitedOrOffSuitHoleCards.put(suitedOrOffSuitHoleCards.size(), entry.getValue());
                    }
                }
            }
        }
        return suitedOrOffSuitHoleCards;
    }
}
