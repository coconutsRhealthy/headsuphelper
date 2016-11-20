package com.lennart.model.rangebuilder.preflop;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.pokergame.Game;
import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.*;

/**
 * Created by LennartMac on 15/10/16.
 */
public class PreflopRangeBuilderUtil {

    private RangeBuilder rangeBuilder = new RangeBuilder();
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

    public Map<Integer, Set<Card>> getBroadWayHoleCards(double percentage) {
        Map<Integer, Set<Card>> offSuitBroadWayCards = getOffSuitHoleCards(10, 10, 1);
        Map<Integer, Set<Card>> suitedBroadWayCards = getSuitedHoleCards(10, 10, 1);

        Map<Integer, Set<Card>> broadWayHoleCards = new HashMap<>();

        for (Map.Entry<Integer, Set<Card>> entry : suitedBroadWayCards.entrySet()) {
            double randomNumber = Math.random();
            if(randomNumber < percentage) {
                broadWayHoleCards.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<Integer, Set<Card>> entry : offSuitBroadWayCards.entrySet()) {
            double randomNumber = Math.random();
            if(randomNumber < percentage) {
                broadWayHoleCards.put(entry.getKey(), entry.getValue());
            }
        }
        return broadWayHoleCards;
    }

    public Map<Integer, Set<Card>> getOffSuitHoleCards(int minimumRankOfHighestCard, int minimumRankOfLowestCard,
                                                       double percentage) {
        return getSuitedOrOffSuitHoleCards(minimumRankOfHighestCard, minimumRankOfLowestCard, false, percentage);
    }

    public Map<Integer, Set<Card>> getOffSuitConnectors(int minimumRankOfHighestCard, double percentage) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 1, false, percentage);
    }

    public Map<Integer, Set<Card>> getOffSuitOneGappers(int minimumRankOfHighestCard, double percentage) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 2, false, percentage);
    }

    public Map<Integer, Set<Card>> getOffSuitTwoGappers(int minimumRankOfHighestCard, double percentage) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 3, false, percentage);
    }

    public Map<Integer, Set<Card>> getOffSuitThreeGappers(int minimumRankOfHighestCard, double percentage) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 4, false, percentage);
    }

    public Map<Integer, Set<Card>> getPocketPairs(int minimumRankOfHighestCard, double percentage) {
        Map<Integer, List<Card>> allPocketPairStartHands = new BoardEvaluator().getAllPocketPairStartHands();
        Map<Integer, Set<Card>> pocketPairs = new HashMap<>();

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(Game.getKnownGameCards());

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(Game.getKnownGameCards());

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
            List<Card> coboCheck = new ArrayList<>(pocketPairCombo);

            if(knownGameCardsCopy.add(coboCheck.get(0)) && knownGameCardsCopy.add(coboCheck.get(1))) {
                double randomNumber = Math.random();
                if(randomNumber < percentage) {
                    pocketPairs.put(pocketPairs.size(), pocketPairCombo);
                }
            }
            knownGameCardsCopy.clear();
            knownGameCardsCopy.addAll(knownGameCards);
        }
        return pocketPairs;
    }

    public Map<Integer, Set<Card>> getSuitedHoleCards(int minimumRankOfHighestCard, int minimumRankOfLowestCard,
                                                      double percentage) {
        return getSuitedOrOffSuitHoleCards(minimumRankOfHighestCard, minimumRankOfLowestCard, true, percentage);
    }

    public Map<Integer, Set<Card>> getSuitedConnectors(int minimumRankOfHighestCard, double percentage) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 1, true, percentage);
    }

    public Map<Integer, Set<Card>> getSuitedOneGappers(int minimumRankOfHighestCard, double percentage) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 2, true, percentage);
    }

    public Map<Integer, Set<Card>> getSuitedTwoGappers(int minimumRankOfHighestCard, double percentage) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 3, true, percentage);
    }

    public Map<Integer, Set<Card>> getSuitedThreeGappers(int minimumRankOfHighestCard, double percentage) {
        return getSuitedOrOffSuitConnectingCards(minimumRankOfHighestCard, 4, true, percentage);
    }

    public Map<Integer, Set<Card>> getSuitedCombosOfGivenRanks(int rankCard1, int rankCard2) {
        Map<Integer, Set<Card>> suitedCombosOfGivenRanks = new HashMap<>();
        List<Character> suits = new ArrayList<>();

        suits.add('s');
        suits.add('c');
        suits.add('d');
        suits.add('h');

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(Game.getKnownGameCards());

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(Game.getKnownGameCards());

        for(Character suit : suits) {
            Set<Card> combo = new HashSet<>();
            Card holeCard1 = new Card(rankCard1, suit);
            Card holeCard2 = new Card(rankCard2, suit);
            if(knownGameCardsCopy.add(holeCard1) && knownGameCardsCopy.add(holeCard2)) {
                combo.add(holeCard1);
                combo.add(holeCard2);

                if (combo.size() == 2) {
                    suitedCombosOfGivenRanks.put(suitedCombosOfGivenRanks.size(), combo);
                }
            }
            knownGameCardsCopy.clear();
            knownGameCardsCopy.addAll(knownGameCards);
        }
        return suitedCombosOfGivenRanks;
    }

    public Map<Integer, Set<Card>> getOffSuitCombosOfGivenRanks(int rankCard1, int rankCard2) {
        Map<Integer, Set<Card>> offSuitCombosOfGivenRanks = new HashMap<>();
        List<Character> suits = new ArrayList<>();

        suits.add('s');
        suits.add('c');
        suits.add('d');
        suits.add('h');

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(Game.getKnownGameCards());

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(Game.getKnownGameCards());

        for(Character suit1 : suits) {
            for(Character suit2 : suits) {
                if(suit1 != suit2) {
                    Set<Card> combo = new HashSet<>();
                    Card holeCard1 = new Card(rankCard1, suit1);
                    Card holeCard2 = new Card(rankCard2, suit2);
                    if(knownGameCardsCopy.add(holeCard1) && knownGameCardsCopy.add(holeCard2)) {
                        combo.add(holeCard1);
                        combo.add(holeCard2);
                        offSuitCombosOfGivenRanks.put(offSuitCombosOfGivenRanks.size(), combo);
                    }
                    knownGameCardsCopy.clear();
                    knownGameCardsCopy.addAll(knownGameCards);
                }
            }
        }
        return offSuitCombosOfGivenRanks;
    }

    public Map<Integer, Set<Card>> getPocketPairCombosOfGivenRank(int rank) {
        Map<Integer, Set<Card>> pocketPairCombosOfGivenRanks = new HashMap<>();
        Set<Set<Card>> setToTestForUniqueness = new HashSet<>();
        List<Character> suits = new ArrayList<>();

        suits.add('s');
        suits.add('c');
        suits.add('d');
        suits.add('h');

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(Game.getKnownGameCards());

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(Game.getKnownGameCards());

        for(Character suit1 : suits) {
            for(Character suit2 : suits) {
                if(suit1 != suit2) {
                    Set<Card> combo = new HashSet<>();
                    Card holeCard1 = new Card(rank, suit1);
                    Card holeCard2 = new Card(rank, suit2);
                    if(knownGameCardsCopy.add(holeCard1) && knownGameCardsCopy.add(holeCard2)) {
                        combo.add(holeCard1);
                        combo.add(holeCard2);
                        if (setToTestForUniqueness.add(combo)) {
                            pocketPairCombosOfGivenRanks.put(pocketPairCombosOfGivenRanks.size(), combo);
                        }
                    }
                    knownGameCardsCopy.clear();
                    knownGameCardsCopy.addAll(knownGameCards);
                }
            }
        }
        return pocketPairCombosOfGivenRanks;
    }

    public Map<Integer, Set<Card>> addCombosToIncludeInOpponentPreflopRange(Map<Integer, Set<Card>> allCombosThusFar,
                                                                            Map<Integer, Map<Integer, Set<Card>>> comboPercentageMap,
                                                                            double percentage) {
        for (Map.Entry<Integer, Map<Integer, Set<Card>>> entry : comboPercentageMap.entrySet()) {
            for (Map.Entry<Integer, Set<Card>> entry2 : entry.getValue().entrySet()) {
                if(Math.random() <= percentage) {
                    Set<Card> comboToInclude = new HashSet<>();
                    comboToInclude.addAll(entry2.getValue());
                    allCombosThusFar.put(allCombosThusFar.size(), comboToInclude);
                }
            }
        }
        return allCombosThusFar;
    }

    public Map<Integer, Set<Card>> removeCombosThatCouldBeInOtherMapsFromRestMap(List<Map<Integer, Map<Integer, Set<Card>>>>
                                                                                         allCombosNoRestCombos) {
        Map<Integer, Set<Card>> mapToReturn = new HashMap<>();
        Set<Set<Card>> allCombosNoRestCombosAsSet = new HashSet<>();
        Map<Integer, Set<Card>> allCombos = new BoardEvaluator().getAllPossibleStartHandsAsSets();
        Set<Set<Card>> allCombosAsSet = new HashSet<>();

        for(Map<Integer, Map<Integer, Set<Card>>> comboMapOuter : allCombosNoRestCombos) {
            for(Map.Entry<Integer, Map<Integer, Set<Card>>> comboMapInner : comboMapOuter.entrySet()) {
                for(Map.Entry<Integer, Set<Card>> entry : comboMapInner.getValue().entrySet()) {
                    allCombosNoRestCombosAsSet.add(entry.getValue());
                }
            }
        }

        for(Map.Entry<Integer, Set<Card>> entry : allCombos.entrySet()) {
            allCombosAsSet.add(entry.getValue());
        }

        allCombosAsSet.removeAll(allCombosNoRestCombosAsSet);

        for(Set<Card> combo : allCombosAsSet) {
            mapToReturn.put(mapToReturn.size(), combo);
        }

        return mapToReturn;
    }

    public Map<Integer, Set<Card>> removeDoubleCombos(Map<Integer, Set<Card>> comboMap) {
        Set<Set<Card>> comboMapAsSet = new HashSet<>();

        for (Map.Entry<Integer, Set<Card>> entry : comboMap.entrySet()) {
            comboMapAsSet.add(entry.getValue());
        }

        comboMap.clear();

        for(Set<Card> s : comboMapAsSet) {
            comboMap.put(comboMap.size(), s);
        }
        return comboMap;
    }

    public Map<Integer, Set<Card>> convertPreflopComboMapToSimpleComboMap(Map<Integer, Map<Integer, Set<Card>>> preflopComboMap) {
        Map<Integer, Set<Card>> simpleComboMap = new HashMap<>();

        for (Map.Entry<Integer, Map<Integer, Set<Card>>> entry : preflopComboMap.entrySet()) {
            for (Map.Entry<Integer, Set<Card>> entry2 : entry.getValue().entrySet()) {
                Set<Card> combo = new HashSet<>();
                combo.addAll(entry2.getValue());
                simpleComboMap.put(simpleComboMap.size(), combo);
            }
        }
        return simpleComboMap;
    }

    //helper methods
    private Map<Integer, Set<Card>> getSuitedOrOffSuitConnectingCards(int rankOfHighestCard, int gapBetweenCards, boolean suited,
                                                                      double percentage) {
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
                        double randomNumber = Math.random();
                        if(randomNumber < percentage) {
                            suitedOrOffSuitConnectors.put(suitedOrOffSuitConnectors.size(), entry.getValue());
                        }
                    }
                }
            } else {
                if(asList.get(0).getSuit() != asList.get(1).getSuit()) {
                    List<Integer> comboRanks = new ArrayList<>();
                    comboRanks.add(asList.get(0).getRank());
                    comboRanks.add(asList.get(1).getRank());

                    if(Collections.max(comboRanks) >= rankOfHighestCard &&
                            Collections.max(comboRanks) == Collections.min(comboRanks) + gapBetweenCards) {
                        double randomNumber = Math.random();
                        if(randomNumber < percentage) {
                            suitedOrOffSuitConnectors.put(suitedOrOffSuitConnectors.size(), entry.getValue());
                        }
                    }
                }
            }
        }
        return suitedOrOffSuitConnectors;
    }

    private Map<Integer, Set<Card>> getSuitedOrOffSuitHoleCards(int rankOfHighestCard, int rankOfLowestCard, boolean suited,
                                                                double percentage) {
        Map<Integer, Set<Card>> suitedOrOffSuitHoleCards = new HashMap<>();

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(Game.getKnownGameCards());

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(Game.getKnownGameCards());

        for(Map.Entry<Integer, Set<Card>> entry : allStartHands.entrySet()) {
            List<Card> asList = new ArrayList<>(entry.getValue());

            if(knownGameCardsCopy.add(asList.get(0)) && knownGameCardsCopy.add(asList.get(1))) {
                if(suited) {
                    if(asList.get(0).getSuit() == asList.get(1).getSuit()) {
                        List<Integer> comboRanks = new ArrayList<>();
                        comboRanks.add(asList.get(0).getRank());
                        comboRanks.add(asList.get(1).getRank());

                        if(Collections.max(comboRanks) >= rankOfHighestCard && Collections.min(comboRanks) >= rankOfLowestCard) {
                            double randomNumber = Math.random();
                            if(randomNumber < percentage) {
                                suitedOrOffSuitHoleCards.put(suitedOrOffSuitHoleCards.size(), entry.getValue());
                            }
                        }
                    }
                } else {
                    if(asList.get(0).getSuit() != asList.get(1).getSuit()) {
                        List<Integer> comboRanks = new ArrayList<>();
                        comboRanks.add(asList.get(0).getRank());
                        comboRanks.add(asList.get(1).getRank());

                        if(Collections.max(comboRanks) >= rankOfHighestCard && Collections.min(comboRanks) >= rankOfLowestCard
                                && comboRanks.get(0) != comboRanks.get(1)) {
                            double randomNumber = Math.random();
                            if(randomNumber < percentage) {
                                suitedOrOffSuitHoleCards.put(suitedOrOffSuitHoleCards.size(), entry.getValue());
                            }
                        }
                    }
                }
            }
            knownGameCardsCopy.clear();
            knownGameCardsCopy.addAll(knownGameCards);
        }
        return suitedOrOffSuitHoleCards;
    }
}