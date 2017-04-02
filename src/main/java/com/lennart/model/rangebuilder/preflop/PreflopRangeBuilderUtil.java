package com.lennart.model.rangebuilder.preflop;

import com.lennart.model.card.Card;

import java.util.*;

/**
 * Created by LennartMac on 15/10/16.
 */
public class PreflopRangeBuilderUtil {

    private static final Map<Integer, List<Card>> allPossibleStartHandsAsList = getAllPossibleStartHandsInitialize();
    private static final Map<Integer, Set<Card>> allStartHandsAsSet = fillAllStartHands();
    private Set<Card> knownGameCards;

    public PreflopRangeBuilderUtil(Set<Card> knownGameCards) {
        this.knownGameCards = knownGameCards;
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
        Map<Integer, List<Card>> allPocketPairStartHands = getAllPocketPairStartHands();
        Map<Integer, Set<Card>> pocketPairs = new HashMap<>();

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

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
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

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

    public Map<Integer, Set<Card>> getSuitedCombosOfGivenRanksIgnoreKnownGameCards(int rankCard1, int rankCard2) {
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

            if (combo.size() == 2) {
                suitedCombosOfGivenRanks.put(suitedCombosOfGivenRanks.size(), combo);
            }
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
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

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

    public Map<Integer, Set<Card>> getOffSuitCombosOfGivenRanksIgnoreKnownGameCards(int rankCard1, int rankCard2) {
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

    public Map<Integer, Set<Card>> getPocketPairCombosOfGivenRank(int rank) {
        Map<Integer, Set<Card>> pocketPairCombosOfGivenRanks = new HashMap<>();
        Set<Set<Card>> setToTestForUniqueness = new HashSet<>();
        List<Character> suits = new ArrayList<>();

        suits.add('s');
        suits.add('c');
        suits.add('d');
        suits.add('h');

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

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

    public Map<Integer, Set<Card>> getPocketPairCombosOfGivenRankIgnoreKnownGameCards(int rank) {
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
        Map<Integer, Set<Card>> allCombos = getAllPossibleStartHandsAsSets();
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

    //Corrected for known boardCards, only use this method in RangeBuilder classes
    public Map<Integer, Set<Card>> getAllPossibleStartHandsAsSets() {
        Map<Integer, List<Card>> allPossibleStartHandsAsAlist = allPossibleStartHandsAsList;
        Map<Integer, Set<Card>> allPossibleStartHandsAsSet = new HashMap<>();

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

        for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHandsAsAlist.entrySet()) {
            if(knownGameCardsCopy.add(entry.getValue().get(0)) && knownGameCardsCopy.add(entry.getValue().get(1))) {
                Set<Card> combo = new HashSet<>();
                combo.addAll(entry.getValue());
                allPossibleStartHandsAsSet.put(allPossibleStartHandsAsSet.size(), combo);
            }
            knownGameCardsCopy.clear();
            knownGameCardsCopy.addAll(knownGameCards);
        }
        return allPossibleStartHandsAsSet;
    }

    //helper methods

    //previously this was a static block in top of class
    private static Map<Integer, Set<Card>> fillAllStartHands() {
        Map<Integer, Set<Card>> allStartHandsAsSets = new HashMap<>();
        Map<Integer, List<Card>> allPossibleStartHands = allPossibleStartHandsAsList;

        List<List<Card>> asList = new ArrayList<>(allPossibleStartHands.values());
        Set<Set<Card>> asSet = new HashSet<>();

        for(List<Card> l : asList) {
            Set<Card> s = new HashSet<>();
            s.addAll(l);
            asSet.add(s);
        }

        for(Set<Card> combo : asSet) {
            allStartHandsAsSets.put(allStartHandsAsSets.size(), combo);
        }
        return allStartHandsAsSets;
    }

    private Map<Integer, Set<Card>> getSuitedOrOffSuitConnectingCards(int rankOfHighestCard, int gapBetweenCards, boolean suited,
                                                                      double percentage) {
        Map<Integer, Set<Card>> suitedOrOffSuitConnectors = new HashMap<>();

        for(Map.Entry<Integer, Set<Card>> entry : allStartHandsAsSet.entrySet()) {
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
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

        for(Map.Entry<Integer, Set<Card>> entry : allStartHandsAsSet.entrySet()) {
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

    private static Map<Integer, List<Card>> getAllPossibleStartHandsInitialize() {
        Map<Integer, List<Card>> allPossibleStartHands = new HashMap<>();
        List<Card> completeCardDeck = getCompleteCardDeck();

        int i = 1;
        for(int z = 0; z < 52; z++) {
            for(int q = 0; q < 52; q++) {
                if(!completeCardDeck.get(z).equals(completeCardDeck.get(q))) {
                    allPossibleStartHands.put(i, new ArrayList<>());
                    allPossibleStartHands.get(i).add(completeCardDeck.get(z));
                    allPossibleStartHands.get(i).add(completeCardDeck.get(q));
                    i++;
                }
            }
        }

        List<List<Card>> asList = new ArrayList<>(allPossibleStartHands.values());
        Set<Set<Card>> asSet = new HashSet<>();

        allPossibleStartHands.clear();

        for(List<Card> l : asList) {
            Set<Card> s = new HashSet<>();
            s.addAll(l);
            asSet.add(s);
        }

        for(Set<Card> startHand : asSet) {
            List<Card> l = new ArrayList<>();
            l.addAll(startHand);
            allPossibleStartHands.put(allPossibleStartHands.size(), l);
        }
        return allPossibleStartHands;
    }

    public static List<Card> getCompleteCardDeck() {
        List<Card> completeCardDeck = new ArrayList<>();

        for(int i = 2; i <= 14; i++) {
            for(int z = 1; z <= 4; z++) {
                if(z == 1) {
                    completeCardDeck.add(new Card(i, 's'));
                }
                if(z == 2) {
                    completeCardDeck.add(new Card(i, 'c'));
                }
                if(z == 3) {
                    completeCardDeck.add(new Card(i, 'd'));
                }
                if(z == 4) {
                    completeCardDeck.add(new Card(i, 'h'));
                }
            }
        }
        return completeCardDeck;
    }

    public static Map<Integer, List<Card>> getAllPocketPairStartHands() {
        Map<Integer, List<Card>> allPocketPairStartHands = new HashMap<>();
        Map<Integer, List<Card>> allPossibleStartHands = PreflopRangeBuilderUtil.getAllPossibleStartHandsAsList();

        for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
            if(entry.getValue().get(0).getRank() == entry.getValue().get(1).getRank()) {
                allPocketPairStartHands.put(allPocketPairStartHands.size(), entry.getValue());
            }
        }
        return allPocketPairStartHands;
    }

    public static Map<Integer, List<Card>> getAllPossibleStartHandsAsList() {
        Map<Integer, List<Card>> allPossibleStartHandsAsListCopy = new HashMap<>();

        for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHandsAsList.entrySet()) {
            List<Card> comboCopy = new ArrayList<>();
            comboCopy.addAll(entry.getValue());

            allPossibleStartHandsAsListCopy.put(allPossibleStartHandsAsListCopy.size(), comboCopy);
        }
        return allPossibleStartHandsAsListCopy;
    }

    public static Map<Integer, Set<Card>> getAllStartHandsAsSet() {
        Map<Integer, Set<Card>> allPossibleStartHandsAsSetCopy = new HashMap<>();

        for (Map.Entry<Integer, Set<Card>> entry : allStartHandsAsSet.entrySet()) {
            Set<Card> comboCopy = new HashSet<>();
            comboCopy.addAll(entry.getValue());

            allPossibleStartHandsAsSetCopy.put(allPossibleStartHandsAsSetCopy.size(), comboCopy);
        }
        return allPossibleStartHandsAsSetCopy;
    }

    public static boolean handIsJjPlusOrAk(List<Card> hand, Set<Card> knownGameCards) {
        Set<Set<Card>> combosJjPlusOrAk = new HashSet<>();
        Set<Card> handAsSet = new HashSet<>();
        handAsSet.addAll(hand);

        PreflopRangeBuilderUtil p = new PreflopRangeBuilderUtil(knownGameCards);
        Map<Integer, Set<Card>> combosAa = p.getPocketPairCombosOfGivenRankIgnoreKnownGameCards(14);
        Map<Integer, Set<Card>> combosKk = p.getPocketPairCombosOfGivenRankIgnoreKnownGameCards(13);
        Map<Integer, Set<Card>> combosQq = p.getPocketPairCombosOfGivenRankIgnoreKnownGameCards(12);
        Map<Integer, Set<Card>> combosJj = p.getPocketPairCombosOfGivenRankIgnoreKnownGameCards(11);
        Map<Integer, Set<Card>> combosAks = p.getSuitedCombosOfGivenRanksIgnoreKnownGameCards(14, 13);
        Map<Integer, Set<Card>> combosAko = p.getOffSuitCombosOfGivenRanksIgnoreKnownGameCards(14, 13);

        for (Map.Entry<Integer, Set<Card>> entry : combosAa.entrySet()) {
            combosJjPlusOrAk.add(entry.getValue());
        }
        for (Map.Entry<Integer, Set<Card>> entry : combosKk.entrySet()) {
            combosJjPlusOrAk.add(entry.getValue());
        }
        for (Map.Entry<Integer, Set<Card>> entry : combosQq.entrySet()) {
            combosJjPlusOrAk.add(entry.getValue());
        }
        for (Map.Entry<Integer, Set<Card>> entry : combosJj.entrySet()) {
            combosJjPlusOrAk.add(entry.getValue());
        }
        for (Map.Entry<Integer, Set<Card>> entry : combosAks.entrySet()) {
            combosJjPlusOrAk.add(entry.getValue());
        }
        for (Map.Entry<Integer, Set<Card>> entry : combosAko.entrySet()) {
            combosJjPlusOrAk.add(entry.getValue());
        }

        if(!combosJjPlusOrAk.add(handAsSet)) {
            //combo is JJ+ or AK
            return true;
        }
        return false;
    }
}