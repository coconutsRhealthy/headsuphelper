package com.lennart.model.boardevaluation;

import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by Lennart Popma on 21-6-2016.
 */
public class BoardEvaluator {

    private List<Card> board;
    private Map<Integer, Set<Set<Card>>> sortedCombosNew;
    private HighCardEvaluator highCardEvaluator;
    private PairEvaluator pairEvaluator;
    private TwoPairEvaluator twoPairEvaluator;
    private ThreeOfAKindEvaluator threeOfAKindEvaluator;
    private StraightEvaluator straightEvaluator;
    private FlushEvaluator flushEvaluator;
    private FullHouseEvaluator fullHouseEvaluator;
    private FourOfAKindEvaluator fourOfAKindEvaluator;
    private StraightFlushEvaluator straightFlushEvaluator;

    private HighCardDrawEvaluator highCardDrawEvaluator;
    private StraightDrawEvaluator straightDrawEvaluator;
    private FlushDrawEvaluator flushDrawEvaluator;

    public BoardEvaluator() {

    }

    public BoardEvaluator(List<Card> board) {
        this.board = board;

        straightFlushEvaluator = new StraightFlushEvaluator(board);
        fourOfAKindEvaluator = new FourOfAKindEvaluator(board, straightFlushEvaluator);
        fullHouseEvaluator = new FullHouseEvaluator(board, fourOfAKindEvaluator, straightFlushEvaluator);
        flushEvaluator = new FlushEvaluator(board, fullHouseEvaluator, fourOfAKindEvaluator, straightFlushEvaluator);
        straightEvaluator = new StraightEvaluator(board, flushEvaluator, fullHouseEvaluator, fourOfAKindEvaluator,
                straightFlushEvaluator);
        threeOfAKindEvaluator = new ThreeOfAKindEvaluator(board, straightEvaluator, flushEvaluator, fullHouseEvaluator,
                fourOfAKindEvaluator, straightFlushEvaluator);
        twoPairEvaluator = new TwoPairEvaluator(board, threeOfAKindEvaluator, straightEvaluator, flushEvaluator,
                fullHouseEvaluator, fourOfAKindEvaluator, straightFlushEvaluator);
        pairEvaluator = new PairEvaluator(board, twoPairEvaluator, threeOfAKindEvaluator, straightEvaluator,
                flushEvaluator, fullHouseEvaluator, fourOfAKindEvaluator, straightFlushEvaluator);
        highCardEvaluator = new HighCardEvaluator(board, pairEvaluator, twoPairEvaluator, threeOfAKindEvaluator,
                straightEvaluator, flushEvaluator, fullHouseEvaluator, fourOfAKindEvaluator, straightFlushEvaluator);

        sortedCombosNew = getSortedCombosInitialize(board);

        highCardDrawEvaluator = new HighCardDrawEvaluator(board, straightEvaluator);
        straightDrawEvaluator = new StraightDrawEvaluator(board);
        flushDrawEvaluator = new FlushDrawEvaluator(board);
    }

    public Map<Integer, Set<Set<Card>>> getSortedCombosNew() {
        return sortedCombosNew;
    }

    public void setSortedCombosNew(Map<Integer, Set<Set<Card>>> sortedCombosNew) {
        this.sortedCombosNew = sortedCombosNew;
    }

    public boolean isBoardRainbow(List<Card> board) {
        if(getNumberOfSuitedCardsOnBoard(board) == 0) {
            return true;
        }
        return false;
    }

    public boolean hasBoardTwoOfOneSuit(List<Card> board) {
        if(getNumberOfSuitedCardsOnBoard(board) == 2) {
            return true;
        }
        return false;
    }

    public boolean hasBoardThreeOfOneSuit(List<Card> board) {
        if(getNumberOfSuitedCardsOnBoard(board) == 3 && (!isBoardSuited(board))) {
            return true;
        }
        return false;
    }

    public boolean hasBoardFourOfOneSuit(List<Card> board) {
        if(getNumberOfSuitedCardsOnBoard(board) == 4 && (!isBoardSuited(board))) {
            return true;
        }
        return false;
    }

    public boolean isBoardSuited(List<Card> board) {
        if(getNumberOfSuitedCardsOnBoard(board) == board.size()) {
            return true;
        }
        return false;
    }

    public boolean isBoardConnected(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        if(boardContainsAce(board)) {
            Integer aceLow = 1;
            Integer aceHigh = 14;
            boardRanks.add(aceLow);
            boardRanks.remove(aceHigh);
            Collections.sort(boardRanks);
            int counter = 0;
            for(int i = 0; i < (boardRanks.size()-1); i++) {
                if(boardRanks.get(i) + 1 == boardRanks.get(i+1)) {
                    counter++;
                    if(counter == boardRanks.size()-1) {
                        return true;
                    }
                    continue;
                }
                else {
                    boardRanks.remove(aceLow);
                    boardRanks.add(aceHigh);
                    break;
                }
            }
        }

        for(int i = 0; i < (boardRanks.size()-1); i++) {
            if(boardRanks.get(i) + 1 == boardRanks.get(i+1)) {
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }

    public boolean hasBoardTwoConnectingCards(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        int x = 0;
        for(int i = 0; i < (boardRanks.size()-1); i++) {
            if(boardRanks.get(i) + 1 == boardRanks.get(i+1)) {
                x++;
            }
        }
        if(x == 1) {
            return true;
        }
        return false;
    }

    public boolean isBoardPairedOnce(List<Card> board) {
        if(getNumberOfPairsOnBoard(board) == 1) {
            return true;
        }
        return false;
    }

    public boolean isBoardPairedTwice(List<Card> board) {
        if(getNumberOfPairsOnBoard(board) == 2) {
            return true;
        }
        return false;
    }

    //helper methods
    public int getNumberOfSuitedCardsOnBoard(List<Card> board) {
        StringBuilder s = new StringBuilder();
        int x = 0;
        for(Card c : board) {
            s.append(c.getSuit());
        }
        for(int i = 0; i <= (s.length()-1); i++) {
            int a = StringUtils.countMatches(s, "" + s.charAt(i));
            if(a > 1 && a > x) {
                x = a;
            }
        }
        return x;
    }

    public int getNumberOfPairsOnBoard(List<Card> board) {
        int numberOfPairsOnBoard = 0;

        if(!boardContainsTrips(board) && !boardContainsQuads(board)) {
            List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

            int y = 0;
            for(int i : boardRanks) {
                if(Collections.frequency(boardRanks, i) == 2 && i != y) {
                    numberOfPairsOnBoard++;
                    y = i;
                }
            }
        }

        return numberOfPairsOnBoard;
    }

    protected List<Integer> getRanksOfPairsOnBoard(List<Card> board) {
        List<Integer> ranksOfPairsOnBoad = new ArrayList<>();

        if(!boardContainsTrips(board) && !boardContainsQuads(board)) {
            List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
            int y = 0;
            for(int i : boardRanks) {
                if(Collections.frequency(boardRanks, i) == 2 && i != y) {
                    ranksOfPairsOnBoad.add(i);
                    y = i;
                }
            }
        }
        return ranksOfPairsOnBoad;
    }

    public List<Integer> getSortedCardRanksFromCardList(List<Card> board) {
        List<Integer> boardRanks = new ArrayList<Integer>();
        for(Card c : board) {
            boardRanks.add(c.getRank());
        }
        Collections.sort(boardRanks);
        return boardRanks;
    }

    protected boolean boardContainsAceAndWheelCard(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        Integer a = 2;
        Integer b = 3;
        Integer c = 4;
        Integer d = 5;
        Integer ace = 14;

        List<Integer> wheelCards = new ArrayList<Integer>();
        wheelCards.add(a);
        wheelCards.add(b);
        wheelCards.add(c);
        wheelCards.add(d);

        if(boardRanks.contains(ace)) {
            if (CollectionUtils.containsAny(boardRanks, wheelCards)) {
                return true;
            }
        }
        return false;
    }

    protected Map<Integer, List<Integer>> getAllPossibleCombos() {
        Map<Integer, List<Integer>> allPossibleCombos = new HashMap<>();
        int counter = 0;
        for(int i = 2; i < 15; i++) {
            List<Integer> combo = new ArrayList<>();
            combo.add(i);
            for (int j = 14; j >= i; j--) {
                combo.add(j);
                List<Integer> comboCopy = new ArrayList<>();
                comboCopy.addAll(combo);
                allPossibleCombos.put(counter, comboCopy);
                combo.remove((Integer) j);
                counter++;
            }
        }
        return allPossibleCombos;
    }

    protected <E> List<E> getDoubleEntriesFromList(List<E> list) {
        Set<E> hs1 = new HashSet<E>();
        Set<E> hs2 = new HashSet<E>();
        List<E> listToReturn = new ArrayList<>();

        for(E e : list) {
            if(!hs1.add(e)) {
                hs2.add(e);
            }
        }
        listToReturn.addAll(hs2);
        return listToReturn;
    }

    protected <E> List<E> removeDoubleEntriesInList(List<E> list) {
        Set<E> hs = new HashSet<E>();
        hs.addAll(list);
        list.clear();
        list.addAll(hs);
        return list;
    }

    protected <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    protected <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    protected List<Card> convertIntegerBoardToArtificialCardBoard(List<Integer> integerBoarList) {
        Map<Integer, Card> newCardObjects = new HashMap<Integer, Card>();
        for(int i = 0; i < integerBoarList.size(); i++) {
            newCardObjects.put(i, new Card(integerBoarList.get(i), 'd'));
        }
        List<Card> artificialCardBoard = new ArrayList<Card>();
        for(int i = 0; i < integerBoarList.size(); i++) {
            artificialCardBoard.add(newCardObjects.get(i));
        }
        return artificialCardBoard;
    }

    protected boolean boardContainsAce(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        Integer ace = 14;
        if(boardRanks.contains(ace)) {
            return true;
        }
        return false;
    }

    protected boolean comboContainsLowAce(List<Integer> combo) {
        for(Integer i : combo) {
            if(i == 1) {
                return true;
            }
        }
        return false;
    }

    protected List<Integer> addLowAceToSubBoardRanksAndRemoveHighAceIfPresent(List<Integer> subBoardRanks, int wantedSubBoardRanksSize) {
        Integer aceLow = 1;
        Integer aceHigh = 14;
        subBoardRanks.add(aceLow);
        if(subBoardRanks.contains(aceHigh)) {
            subBoardRanks.remove(aceHigh);
        }
        Collections.sort(subBoardRanks);
        if(subBoardRanks.size() == wantedSubBoardRanksSize + 1) {
            subBoardRanks.remove(wantedSubBoardRanksSize);
        }
        return subBoardRanks;
    }

    protected List<Integer> convertComboWithLowAceToHighAce(List<Integer> combo) {
        Integer aceLow = 1;
        Integer aceHigh = 14;
        combo.remove(aceLow);
        combo.add(aceHigh);
        Collections.sort(combo);
        return combo;
    }

    protected List<Integer> makeCopyOfComboToAddToReturnList(List<Integer> combo) {
        List<Integer> copiedCombo = new ArrayList<Integer>();
        copiedCombo.addAll(combo);
        return copiedCombo;
    }


    protected List<Integer> addSecondCardToCreateComboWhenSingleCardMakesStraight(List<Integer> combo, Integer i) {
        List<Integer> createdCombo = new ArrayList<Integer>();
        createdCombo.add(i);
        createdCombo.addAll(combo);
        Collections.sort(createdCombo);
        return createdCombo;
    }

    protected int getValueOfHighestCardOnBoard (List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        return boardRanks.get(boardRanks.size() - 1);
    }

    protected List<Card> getStartHandCardsThatAreHigherThanHighestBoardCard(List<Card> startHand, List<Card> board) {
        List<Card> startHandCardsThatAreHigherThanHighestBoardCard = new ArrayList<>();
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        int valueOfHighestBoardCard = boardRanks.get(boardRanks.size() - 1);

        for(Card c : startHand) {
            if(c.getRank() > valueOfHighestBoardCard) {
                startHandCardsThatAreHigherThanHighestBoardCard.add(c);
            }
        }
        return startHandCardsThatAreHigherThanHighestBoardCard;
    }

    protected Map<Integer, List<Card>> clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(Map<Integer, List<Card>> allStartHands, List<Card> board) {
        Map<Integer, List<Card>> allStartHandsClearedForBoardCards = new HashMap<>();

        int index = 0;
        for (Map.Entry<Integer, List<Card>> entry : allStartHands.entrySet()) {
            if(Collections.disjoint(entry.getValue(), board)) {
                allStartHandsClearedForBoardCards.put(index, entry.getValue());
                index++;
            }
        }
        return allStartHandsClearedForBoardCards;
    }

    protected Map<Integer, List<Card>> getAllStartHandsThatContainASpecificCard(Card card) {
        Map<Integer, List<Card>> allPossibleStartHands = ActionBuilderUtil.getAllPossibleStartHandsAsList();
        Map<Integer, List<Card>> allStartHandsThatContainASpecificCard = new HashMap<>();

        for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
            if(entry.getValue().contains(card)) {
                allStartHandsThatContainASpecificCard.put(allStartHandsThatContainASpecificCard.size(), entry.getValue());
            }
        }
        return allStartHandsThatContainASpecificCard;
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

    protected Map<Integer, List<Card>> clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank (Map<Integer, List<Card>> startHandMap, int rank) {
        for(Iterator<Map.Entry<Integer, List<Card>>> it = startHandMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, List<Card>> entry = it.next();
            if(entry.getValue().get(0).getRank() == rank || entry.getValue().get(1).getRank() == rank) {
                it.remove();
            }
        }
        return startHandMap;
    }

    protected Map<Integer, Integer> getFrequencyOfRanksOnBoard(List<Card> board) {
        Map<Integer, Integer> frequencyOfRanksOnBoard = new HashMap<>();
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        for(Integer i : boardRanks) {
            frequencyOfRanksOnBoard.put(i, Collections.frequency(boardRanks, i));
        }
        return frequencyOfRanksOnBoard;
    }

    protected boolean boardContainsTrips(List<Card> board) {
        Map<Integer, Integer> frequencyOfRanksOnBoard = getFrequencyOfRanksOnBoard(board);
        for (Map.Entry<Integer, Integer> entry : frequencyOfRanksOnBoard.entrySet()) {
            if(entry.getValue() == 3) {
                return true;
            }
        }
        return false;
    }

    protected boolean boardContainsBoat(List<Card> board) {
        Map<Integer, Integer> frequencyOfRanksOnBoard = getFrequencyOfRanksOnBoard(board);
        boolean boardContainsTrips = false;
        boolean boardContainsPair = false;

        for (Map.Entry<Integer, Integer> entry : frequencyOfRanksOnBoard.entrySet()) {
            if(entry.getValue() == 3) {
                boardContainsTrips = true;
            }
            if(entry.getValue() == 2) {
                boardContainsPair = true;
            }
        }

        if(boardContainsTrips && boardContainsPair) {
            return true;
        }
        return false;
    }

    protected boolean boardContainsQuads(List<Card> board) {
        Map<Integer, Integer> frequencyOfRanksOnBoard = getFrequencyOfRanksOnBoard(board);
        for (Map.Entry<Integer, Integer> entry : frequencyOfRanksOnBoard.entrySet()) {
            if(entry.getValue() == 4) {
                return true;
            }
        }
        return false;
    }

    protected Map<Integer, List<Integer>> getAllPossibleStartHandsRankOnly(Map<Integer, List<Card>> allPossibleStartHands) {
        Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = new HashMap<>();
        for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
            allPossibleStartHandsRankOnly.put(entry.getKey(), new ArrayList<>());
            allPossibleStartHandsRankOnly.get(entry.getKey()).add(entry.getValue().get(0).getRank());
            allPossibleStartHandsRankOnly.get(entry.getKey()).add(entry.getValue().get(1).getRank());
        }
        return allPossibleStartHandsRankOnly;
    }

    protected Map<Integer, List<Card>> getSortedComboMap(Map<Integer, List<Card>> comboMap) {
        List<List<Card>> comboList = new ArrayList<>(comboMap.values());
        Collections.sort(comboList, Card.sortCardCombosBasedOnRank());
        Map<Integer, List<Card>> sortedComboMap = new HashMap<>();

        for(List<Card> list : comboList) {
            sortedComboMap.put(sortedComboMap.size(), list);
        }
        return sortedComboMap;
    }

    protected Map<Integer, List<Integer>> getAllPossibleFiveConnectingCards() {
        Map<Integer, List<Integer>> allPossibleStraights = new HashMap<>();
        List<Integer> lowestStraight = new ArrayList<>();
        lowestStraight.add(14);
        lowestStraight.add(2);
        lowestStraight.add(3);
        lowestStraight.add(4);
        lowestStraight.add(5);
        allPossibleStraights.put(0, lowestStraight);

        for(int i = 1; i < 10; i++) {
            allPossibleStraights.put(i, new ArrayList<>());
        }

        int start = 2;
        for(int i = 1; i < allPossibleStraights.size(); i++) {
            for(int z = 0; z < 5; z++) {
                allPossibleStraights.get(i).add(start + z);
            }
            start++;
        }
        return allPossibleStraights;
    }

    protected Map<Integer, List<Card>> getAllPossibleFiveConnectingSuitedCards() {
        Map<Integer, List<Card>> allPossibleFiveConnectingSuitedCards = new HashMap<>();
        Map<Integer, List<Integer>> allFiveConnectingCards = getAllPossibleFiveConnectingCards();

        for (Map.Entry<Integer, List<Integer>> entry : allFiveConnectingCards.entrySet()) {
            List<Card> sCardList = new ArrayList<>();
            List<Card> cCardList = new ArrayList<>();
            List<Card> dCardList = new ArrayList<>();
            List<Card> hCardList = new ArrayList<>();

            for(Integer rank : entry.getValue()) {
                Card s = new Card(rank, 's');
                Card c = new Card(rank, 'c');
                Card d = new Card(rank, 'd');
                Card h = new Card(rank, 'h');

                sCardList.add(s);
                cCardList.add(c);
                dCardList.add(d);
                hCardList.add(h);
            }
            allPossibleFiveConnectingSuitedCards.put(allPossibleFiveConnectingSuitedCards.size(), sCardList);
            allPossibleFiveConnectingSuitedCards.put(allPossibleFiveConnectingSuitedCards.size(), cCardList);
            allPossibleFiveConnectingSuitedCards.put(allPossibleFiveConnectingSuitedCards.size(), dCardList);
            allPossibleFiveConnectingSuitedCards.put(allPossibleFiveConnectingSuitedCards.size(), hCardList);
        }
        return allPossibleFiveConnectingSuitedCards;
    }

    protected <T extends ComboComparatorRankOnly> Map<Integer, List<List<Integer>>> getSortedComboMapRankOnly
            (Map<Integer, List<Card>> comboMap, List<Card> board, T evaluatorClass) {
        Map<Integer, List<List<Integer>>> sortedComboMapRankOnly = new HashMap<>();
        Set<List<Integer>> comboSetRankOnlyUnsorted = new HashSet<>();
        Set<List<Integer>> comboSetRankOnlySorted = new TreeSet<>(evaluatorClass.getComboComparatorRankOnly(board));

        for (Map.Entry<Integer, List<Card>> entry : comboMap.entrySet()) {
            List<Integer> comboRanksOnly = getSortedCardRanksFromCardList(entry.getValue());
            comboSetRankOnlyUnsorted.add(comboRanksOnly);
        }

        comboSetRankOnlySorted.addAll(comboSetRankOnlyUnsorted);
        Map<List<Integer>, Set<List<Integer>>> allCombosUnsortedComboIsKey = new HashMap<>();

        for(List<Integer> l : comboSetRankOnlySorted) {
            allCombosUnsortedComboIsKey.put(l, new HashSet<>());
            allCombosUnsortedComboIsKey.get(l).add(l);
        }

        for(List<Integer> x : comboSetRankOnlyUnsorted) {
            for(List<Integer> y : comboSetRankOnlySorted) {
                Set<List<Integer>> test = new TreeSet<>(evaluatorClass.getComboComparatorRankOnly(board));
                test.add(x);
                test.add(y);

                if(test.size() == 1) {
                    allCombosUnsortedComboIsKey.get(y).add(x);
                }
            }
        }

        Map<Integer, List<List<Integer>>> sortedSetMap = new HashMap<>();
        List<List<Integer>> comboSetRankOnlySortedAsList = new ArrayList<>();
        comboSetRankOnlySortedAsList.addAll(comboSetRankOnlySorted);

        for(int i = 0; i < comboSetRankOnlySortedAsList.size(); i++) {
            sortedSetMap.put(i, new ArrayList<>());
            sortedSetMap.get(i).add(comboSetRankOnlySortedAsList.get(i));
        }

        for(int i = 0; i < comboSetRankOnlySortedAsList.size(); i++) {
            sortedComboMapRankOnly.put(i, new ArrayList<>());
        }

        for (Map.Entry<List<Integer>, Set<List<Integer>>> unsortedComboIsKeyEntry : allCombosUnsortedComboIsKey.entrySet()) {
            for (Map.Entry<Integer, List<List<Integer>>> sortedSetMapEntry : sortedSetMap.entrySet()) {
                int initialSize = unsortedComboIsKeyEntry.getValue().size();

                List<List<Integer>> unsortedComboIsKeyEntryCopy = new ArrayList<>();
                unsortedComboIsKeyEntryCopy.addAll(unsortedComboIsKeyEntry.getValue());

                List<List<Integer>> sortedSetMapEntryCopy = new ArrayList<>();
                sortedSetMapEntryCopy.addAll(sortedSetMapEntry.getValue());

                unsortedComboIsKeyEntryCopy.removeAll(sortedSetMapEntryCopy);

                if(initialSize != unsortedComboIsKeyEntryCopy.size()) {
                    sortedComboMapRankOnly.get(sortedSetMapEntry.getKey()).addAll(unsortedComboIsKeyEntry.getValue());
                    Collections.sort(sortedComboMapRankOnly.get(sortedSetMapEntry.getKey()), sortRankCombosBasedOnRank());
                    if(sortedComboMapRankOnly.get(sortedSetMapEntry.getKey()).size() == 1) {
                        Collections.sort(sortedComboMapRankOnly.get(sortedSetMapEntry.getKey()).get(0), Collections.reverseOrder());
                    }
                }
            }
        }
        return sortedComboMapRankOnly;
    }

    protected <T extends ComboComparator> Map<Integer, Set<Set<Card>>> getSortedCardComboMap
            (Map<Integer, List<Card>> comboMap, List<Card> board, T evaluatorClass) {
        Map<Integer, Set<Set<Card>>> sortedComboMap = new HashMap<>();
        Set<Set<Card>> comboSetUnsorted = new HashSet<>();
        Set<Set<Card>> comboSetSorted = new TreeSet<>(evaluatorClass.getComboComparator(board));

        for (Map.Entry<Integer, List<Card>> entry : comboMap.entrySet()) {
            Set<Card> entryListAsSet = new HashSet<>();
            entryListAsSet.addAll(entry.getValue());
            comboSetUnsorted.add(entryListAsSet);
        }

        comboSetSorted.addAll(comboSetUnsorted);
        Map<Set<Card>, Set<Set<Card>>> allCombosUnsortedComboIsKey = new HashMap<>();

        for(Set<Card> l : comboSetSorted) {
            allCombosUnsortedComboIsKey.put(l, new HashSet<>());
            allCombosUnsortedComboIsKey.get(l).add(l);
        }

        for(Set<Card> x : comboSetUnsorted) {
            for(Set<Card> y : comboSetSorted) {
                Set<Set<Card>> test = new TreeSet<>(evaluatorClass.getComboComparator(board));
                test.add(x);
                test.add(y);

                if(test.size() == 1) {
                    allCombosUnsortedComboIsKey.get(y).add(x);
                }
            }
        }

        Map<Integer, List<Set<Card>>> sortedSetMap = new HashMap<>();
        List<Set<Card>> comboSetRankOnlySortedAsList = new ArrayList<>();
        comboSetRankOnlySortedAsList.addAll(comboSetSorted);

        for(int i = 0; i < comboSetRankOnlySortedAsList.size(); i++) {
            sortedSetMap.put(i, new ArrayList<>());
            sortedSetMap.get(i).add(comboSetRankOnlySortedAsList.get(i));
        }

        for(int i = 0; i < comboSetRankOnlySortedAsList.size(); i++) {
            sortedComboMap.put(i, new HashSet<>());
        }

        for (Map.Entry<Set<Card>, Set<Set<Card>>> unsortedComboIsKeyEntry : allCombosUnsortedComboIsKey.entrySet()) {
            for (Map.Entry<Integer, List<Set<Card>>> sortedSetMapEntry : sortedSetMap.entrySet()) {
                int initialSize = unsortedComboIsKeyEntry.getValue().size();

                List<Set<Card>> unsortedComboIsKeyEntryCopy = new ArrayList<>();
                unsortedComboIsKeyEntryCopy.addAll(unsortedComboIsKeyEntry.getValue());

                List<Set<Card>> sortedSetMapEntryCopy = new ArrayList<>();
                sortedSetMapEntryCopy.addAll(sortedSetMapEntry.getValue());

                unsortedComboIsKeyEntryCopy.removeAll(sortedSetMapEntryCopy);

                if(initialSize != unsortedComboIsKeyEntryCopy.size()) {
                    sortedComboMap.get(sortedSetMapEntry.getKey()).addAll(unsortedComboIsKeyEntry.getValue());
                }
            }
        }
        return sortedComboMap;
    }

    public Map<Integer, Set<Set<Card>>> convertRankComboMapToCardComboMapCorrectedForBoard
            (Map<Integer, List<List<Integer>>> rankComboMap, List<Card> board) {
        Map<Integer, Set<Set<Card>>> cardComboMap = new HashMap<>();

        for (Map.Entry<Integer, List<List<Integer>>> entry : rankComboMap.entrySet()) {
            cardComboMap.put(cardComboMap.size(), new HashSet<>());
            for(List<Integer> l : entry.getValue()) {
                Set<Set<Card>> cardCombosCorrespondingToRankCombo = convertRankComboToSetOfCardCombos(l);
                Set<Set<Card>> cardCombosCorrespondingToRankComboCorrectedForBoard = new HashSet<>();

                for(Set<Card> s : cardCombosCorrespondingToRankCombo) {
                    if(Collections.disjoint(s, board)) {
                        cardCombosCorrespondingToRankComboCorrectedForBoard.add(s);
                    }
                }
                cardComboMap.get(entry.getKey()).addAll(cardCombosCorrespondingToRankComboCorrectedForBoard);
            }
        }
        return cardComboMap;
    }

    public Set<Set<Card>> convertRankComboToSetOfCardCombos(List<Integer> rankCombo) {
        List<Card> card1List = new ArrayList<>();
        card1List.add(new Card(rankCombo.get(0), 's'));
        card1List.add(new Card(rankCombo.get(0), 'c'));
        card1List.add(new Card(rankCombo.get(0), 'd'));
        card1List.add(new Card(rankCombo.get(0), 'h'));

        List<Card> card2List = new ArrayList<>();
        card2List.add(new Card(rankCombo.get(1), 's'));
        card2List.add(new Card(rankCombo.get(1), 'c'));
        card2List.add(new Card(rankCombo.get(1), 'd'));
        card2List.add(new Card(rankCombo.get(1), 'h'));

        Set<Set<Card>> setOfCardCombos = new HashSet<>();

        for(Card c1 : card1List) {
            for(Card c2: card2List) {
                Set<Card> combo = new HashSet<>();
                combo.add(c1);
                combo.add(c2);

                if(combo.size() == 2) {
                    setOfCardCombos.add(combo);
                }
            }
        }
        return setOfCardCombos;
    }

    public Comparator<List<Integer>> sortRankCombosBasedOnRank() {
        return new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> combo1, List<Integer> combo2) {
                Collections.sort(combo1, Collections.reverseOrder());
                Collections.sort(combo2, Collections.reverseOrder());

                if(combo2.get(0) > combo1.get(0)) {
                    return 1;
                } else if(combo2.get(0) == combo1.get(0)) {
                    if(combo2.get(1) > combo1.get(1)) {
                        return 1;
                    } else if(combo2.get(1) == combo1.get(1)) {
                        return 0;
                    }
                }
                return -1;
            }
        };
    }

    public  Map<Integer, Set<Set<Card>>> getSortedCombosInitialize(List<Card> board) {
        Map<Integer, Set<Set<Card>>> sortedCombos = new HashMap<>();

        Map<Integer, Set<Set<Card>>> highCardCombos = highCardEvaluator.getHighCardCombos();
        Map<Integer, Set<Set<Card>>> pairCombos = pairEvaluator.getCombosThatMakePair();
        Map<Integer, Set<Set<Card>>> twoPairCombos = twoPairEvaluator.getCombosThatMakeTwoPair();
        Map<Integer, Set<Set<Card>>> threeOfAKindCombos = threeOfAKindEvaluator.getThreeOfAKindCombos();
        Map<Integer, Set<Set<Card>>> straightCombos = straightEvaluator.getMapOfStraightCombos();
        Map<Integer, Set<Set<Card>>> flushCombos = flushEvaluator.getFlushCombos();
        Map<Integer, Set<Set<Card>>> fullHouseCombos = fullHouseEvaluator.getFullHouseCombos();
        Map<Integer, Set<Set<Card>>> fourOfAKindCombos = fourOfAKindEvaluator.getFourOfAKindCombos();
        Map<Integer, Set<Set<Card>>> straightFlushCombos = straightFlushEvaluator.getStraightFlushCombos();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : straightFlushCombos.entrySet()) {
            sortedCombos.put(sortedCombos.size(), entry.getValue());
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : fourOfAKindCombos.entrySet()) {
            sortedCombos.put(sortedCombos.size(), entry.getValue());
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : fullHouseCombos.entrySet()) {
            sortedCombos.put(sortedCombos.size(), entry.getValue());
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : flushCombos.entrySet()) {
            sortedCombos.put(sortedCombos.size(), entry.getValue());
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : straightCombos.entrySet()) {
            sortedCombos.put(sortedCombos.size(), entry.getValue());
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : threeOfAKindCombos.entrySet()) {
            sortedCombos.put(sortedCombos.size(), entry.getValue());
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : twoPairCombos.entrySet()) {
            sortedCombos.put(sortedCombos.size(), entry.getValue());
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : pairCombos.entrySet()) {
            sortedCombos.put(sortedCombos.size(), entry.getValue());
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : highCardCombos.entrySet()) {
            sortedCombos.put(sortedCombos.size(), entry.getValue());
        }

        return sortedCombos;
    }

    public Map<Integer, Set<Set<Card>>> removeDuplicateCombosPerCategory(Map<Integer, Set<Set<Card>>> categoryCombos,
                                                                         Map<Integer, Set<Set<Card>>> sortedCombos) {
        Map<Integer, Set<Set<Card>>> cleanedSortedCombos = new HashMap<>();
        Set<Set<Card>> straightFLushSetsToBeRemoved = new HashSet<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            loop: for(Set<Card> s : entry.getValue()) {
                for (Map.Entry<Integer, Set<Set<Card>>> entry2 : categoryCombos.entrySet()) {
                    for(Set<Card> s2 : entry2.getValue()) {
                        if (s.equals(s2)) {
                            straightFLushSetsToBeRemoved.add(s);
                            continue loop;
                        }
                    }
                }
            }
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            for(Iterator<Set<Card>> it = entry.getValue().iterator(); it.hasNext(); ) {
                Set<Card> itSet = it.next();
                for(Set<Card> s2 : straightFLushSetsToBeRemoved) {
                    if(itSet.equals(s2)) {
                        it.remove();
                    }
                }
            }
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            if(!entry.getValue().isEmpty()) {
                cleanedSortedCombos.put(cleanedSortedCombos.size(), entry.getValue());
            }
        }

        return cleanedSortedCombos;
    }

    public static int getBoardWetness(List<Set<Card>> top10percentPreviousStreetCombos, List<Set<Card>> top10percentCurrentStreetCombos) {
        int boardWetness = 200;

        if(top10percentPreviousStreetCombos != null && top10percentPreviousStreetCombos.size() > 1
                && top10percentCurrentStreetCombos != null && top10percentCurrentStreetCombos.size() > 1) {
            top10percentPreviousStreetCombos.retainAll(top10percentCurrentStreetCombos);
            boardWetness = top10percentPreviousStreetCombos.size();
        }

        return boardWetness;
    }

    public List<Set<Card>> getTop10percentCombos() {
        List<Set<Card>> top10percentCombos = new ArrayList<>();

        int counter = 1;

        loop: for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombosNew.entrySet()) {
            if(counter < 133) {
                List<Set<Card>> setSetAsList = new ArrayList<>();
                setSetAsList.addAll(entry.getValue());

                for(Set<Card> combo : setSetAsList) {
                    if(counter < 133) {
                        top10percentCombos.add(combo);
                        counter++;
                    } else {
                        break loop;
                    }
                }
            } else {
                break;
            }
        }

        return top10percentCombos;
    }

    public int getFlushStraightWetness() {
        int flushStraightWetness = 0;

        Map<Integer, Set<Set<Card>>> mapOfStraightCombos = straightEvaluator.getMapOfStraightCombos();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : mapOfStraightCombos.entrySet()) {
            Set<Set<Card>> combos = entry.getValue();
            flushStraightWetness = flushStraightWetness + combos.size();
        }

        Map<Integer, Set<Set<Card>>> mapOfFlushCombos = flushEvaluator.getFlushCombos();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : mapOfFlushCombos.entrySet()) {
            Set<Set<Card>> combos = entry.getValue();
            flushStraightWetness = flushStraightWetness + combos.size();
        }

        Map<Integer, Set<Card>> strongOosdCombos = straightDrawEvaluator.getStrongOosdCombos();
        flushStraightWetness = flushStraightWetness + strongOosdCombos.size();

        Map<Integer, Set<Card>> strongGutshotCombos = straightDrawEvaluator.getStrongGutshotCombos();
        flushStraightWetness = flushStraightWetness + strongGutshotCombos.size();

        Map<Integer, Set<Card>> strongFdCombos = flushDrawEvaluator.getStrongFlushDrawCombos();
        flushStraightWetness = flushStraightWetness + strongFdCombos.size();

        return flushStraightWetness;
    }

    public int getBoatWetness() {
        int boatWetness = 0;

        Map<Integer, Set<Set<Card>>> fullHouseCombos = fullHouseEvaluator.getFullHouseCombos();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : fullHouseCombos.entrySet()) {
            Set<Set<Card>> combos = entry.getValue();
            boatWetness = boatWetness + combos.size();
        }

        return boatWetness;
    }

    public StraightEvaluator getStraightEvaluator() {
        return straightEvaluator;
    }

    public FlushEvaluator getFlushEvaluator() {
        return flushEvaluator;
    }

    public HighCardDrawEvaluator getHighCardDrawEvaluator() {
        return highCardDrawEvaluator;
    }

    public StraightDrawEvaluator getStraightDrawEvaluator() {
        return straightDrawEvaluator;
    }

    public FlushDrawEvaluator getFlushDrawEvaluator() {
        return flushDrawEvaluator;
    }

    public List<Card> getBoard() {
        return board;
    }
}