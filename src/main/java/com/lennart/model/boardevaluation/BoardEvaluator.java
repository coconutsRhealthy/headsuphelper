package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by Lennart Popma on 21-6-2016.
 */
public class BoardEvaluator {
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
    protected int getNumberOfSuitedCardsOnBoard(List<Card> board) {
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

    protected int getNumberOfPairsOnBoard(List<Card> board) {
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

    protected List<Integer> getSortedCardRanksFromCardList(List<Card> board) {
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
        Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
        Map<Integer, List<Card>> allStartHandsThatContainASpecificCard = new HashMap<>();

        int index = 0;
        for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
            if(entry.getValue().contains(card)) {
                allStartHandsThatContainASpecificCard.put(index, entry.getValue());
            }
        }
        return allStartHandsThatContainASpecificCard;
    }

    protected Map<Integer, List<Card>> getAllPossibleStartHands() {
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
        return allPossibleStartHands;
    }

    protected List<Card> getCompleteCardDeck() {
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

    protected Map<Integer, List<Card>> getAllPocketPairStartHands() {
        Map<Integer, List<Card>> allPocketPairStartHands = new HashMap<>();
        Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();

        for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
            if(entry.getValue().get(0).getRank() == entry.getValue().get(1).getRank()) {
                allPocketPairStartHands.put(allPocketPairStartHands.size(), entry.getValue());
            }
        }
        return allPocketPairStartHands;
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

    protected boolean boardContainsQuads(List<Card> board) {
        Map<Integer, Integer> frequencyOfRanksOnBoard = getFrequencyOfRanksOnBoard(board);
        for (Map.Entry<Integer, Integer> entry : frequencyOfRanksOnBoard.entrySet()) {
            if(entry.getValue() == 4) {
                return true;
            }
        }
        return false;
    }

    protected Map<Integer, List<Integer>> getAllPossibleStartHandsRankOnlyCorrectedForBoard(Map<Integer, List<Card>> allPossibleStartHands, List<Card> board) {
        Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = new HashMap<>();
        for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
            allPossibleStartHandsRankOnly.put(entry.getKey(), new ArrayList<>());
            allPossibleStartHandsRankOnly.get(entry.getKey()).add(entry.getValue().get(0).getRank());
            allPossibleStartHandsRankOnly.get(entry.getKey()).add(entry.getValue().get(1).getRank());
        }
        return allPossibleStartHandsRankOnly;
    }



    public List<BooleanResult> allFunctions(List<Card> board) {
        BooleanResult result1 = new BooleanResult();
        BooleanResult result2 = new BooleanResult();
        BooleanResult result3 = new BooleanResult();
        BooleanResult result4 = new BooleanResult();
        BooleanResult result5 = new BooleanResult();
        BooleanResult result6 = new BooleanResult();
        BooleanResult result7 = new BooleanResult();

        result1.setFunctionDescription("Is board rainbow");
        result1.setResult(isBoardRainbow(board));
        result2.setFunctionDescription("Has board two of one suit");
        result2.setResult(hasBoardTwoOfOneSuit(board));
        result3.setFunctionDescription("Is board suited");
        result3.setResult(isBoardSuited(board));
        result4.setFunctionDescription("Is board connected");
        result4.setResult(isBoardConnected(board));
        result5.setFunctionDescription("Has board two connecting cards");
        result5.setResult(hasBoardTwoConnectingCards(board));
        result6.setFunctionDescription("Is board paired once");
        result6.setResult(isBoardPairedOnce(board));
        result7.setFunctionDescription("is er wheel activity?");
        result7.setResult(boardContainsAceAndWheelCard(board));

        List<BooleanResult> listOfFunctionResults = new ArrayList<BooleanResult>();

        listOfFunctionResults.add(result1);
        listOfFunctionResults.add(result2);
        listOfFunctionResults.add(result3);
        listOfFunctionResults.add(result4);
        listOfFunctionResults.add(result5);
        listOfFunctionResults.add(result6);
        listOfFunctionResults.add(result7);

        return listOfFunctionResults;
    }
}