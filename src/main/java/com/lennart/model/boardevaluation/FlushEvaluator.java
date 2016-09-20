package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by LPO10346 on 8/10/2016.
 */
public class FlushEvaluator extends BoardEvaluator {

    public Map<Integer, List<Card>> getFlushCombos (List<Card> board) {
        Map<Integer, List<Card>> flushCombos = new HashMap<>();
        Map<Character, List<Card>> suitsOfBoard = getSuitsOfBoard(board);

        char flushSuit = 'x';
        int numberOfSuitedCards = 0;
        for (Map.Entry<Character, List<Card>> entry : suitsOfBoard.entrySet()) {
            if(entry.getValue().size() > numberOfSuitedCards && entry.getValue().size() != 1) {
                numberOfSuitedCards = entry.getValue().size();
            }
            if(entry.getValue().size() > 2) {
                flushSuit = entry.getValue().get(0).getSuit();
            }
        }

        if(numberOfSuitedCards < 3) {
            return flushCombos;
        }

        if(numberOfSuitedCards == 3) {
            flushCombos = getAllPossibleSuitedStartHands(flushSuit);
            flushCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushCombos, board);
            Map<Integer, List<List<Card>>> sortedFlushCombos = getSortedFlushComboMap(flushCombos, board);
            return flushCombos;
        }

        if(numberOfSuitedCards == 4 || numberOfSuitedCards == 5) {
            flushCombos = getAllPossibleSuitedStartHands(flushSuit);
            Map<Integer, List<Card>> allStartHands = getAllPossibleStartHands();
            for (Map.Entry<Integer, List<Card>> entry : allStartHands.entrySet()) {
                if(entry.getValue().get(0).getSuit() == flushSuit && entry.getValue().get(1).getSuit() != flushSuit) {
                    flushCombos.put(flushCombos.size(), entry.getValue());
                } else if (entry.getValue().get(0).getSuit() != flushSuit && entry.getValue().get(1).getSuit() == flushSuit) {
                    flushCombos.put(flushCombos.size(), entry.getValue());
                }
            }
            flushCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushCombos, board);

            if(numberOfSuitedCards == 5) {
                for(Iterator<Map.Entry<Integer, List<Card>>> it = flushCombos.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<Integer, List<Card>> entry = it.next();
                    List<Card> startHandCardsThatAreHigherThanHighestBoardCard = getStartHandCardsThatAreHigherThanHighestBoardCard(entry.getValue(), board);
                    if(startHandCardsThatAreHigherThanHighestBoardCard.isEmpty()) {
                        it.remove();
                    } else if (startHandCardsThatAreHigherThanHighestBoardCard.size() == 1) {
                        if(startHandCardsThatAreHigherThanHighestBoardCard.get(0).getSuit() != flushSuit) {
                            it.remove();
                        }
                    }
                }
            }
            Map<Integer, List<List<Card>>> sortedFlushCombos = getSortedFlushComboMap(flushCombos, board);
            return flushCombos;
        }
        return null;
    }

    public Map<Integer, List<Card>> getFlushDrawCombos (List<Card> board) {
        Map<Integer, List<Card>> flushDrawCombos = new HashMap<>();
        Map<Character, List<Card>> suitsOfBoard = getSuitsOfBoard(board);

        if(board.size() == 5) {
            return flushDrawCombos;
        }

        char flushSuit = 'x';
        char flushSuit2 = 'x';
        for (Map.Entry<Character, List<Card>> entry : suitsOfBoard.entrySet()) {
            if(entry.getValue().size() == 2) {
                if(flushSuit == 'x') {
                    flushSuit = entry.getValue().get(0).getSuit();
                } else {
                    flushSuit2 = entry.getValue().get(0).getSuit();
                }
            }
        }

        if(flushSuit != 'x' && flushSuit2 == 'x') {
            flushDrawCombos = getAllPossibleSuitedStartHands(flushSuit);
            flushDrawCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushDrawCombos, board);
            return flushDrawCombos;
        }

        if(flushSuit != 'x' && flushSuit2 != 'x') {
            flushDrawCombos = getAllPossibleSuitedStartHands(flushSuit);
            flushDrawCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushDrawCombos, board);

            Map<Integer, List<Card>> flushDrawCombos2;
            flushDrawCombos2 = getAllPossibleSuitedStartHands(flushSuit2);
            flushDrawCombos2 = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushDrawCombos2, board);

            for (Map.Entry<Integer, List<Card>> entry : flushDrawCombos2.entrySet()) {
                flushDrawCombos.put(flushDrawCombos.size(), entry.getValue());
            }

            return flushDrawCombos;
        }

        boolean threeToFlushOnBoard = false;
        for (Map.Entry<Character, List<Card>> entry : suitsOfBoard.entrySet()) {
            if(entry.getValue().size() == 3) {
                flushSuit = entry.getValue().get(0).getSuit();
                threeToFlushOnBoard = true;
            }
        }

        if(threeToFlushOnBoard) {
            Map<Integer, List<Card>> allStartHands = getAllPossibleStartHands();
            for (Map.Entry<Integer, List<Card>> entry : allStartHands.entrySet()) {
                if(entry.getValue().get(0).getSuit() == flushSuit && entry.getValue().get(1).getSuit() != flushSuit) {
                    flushDrawCombos.put(flushDrawCombos.size(), entry.getValue());
                } else if (entry.getValue().get(0).getSuit() != flushSuit && entry.getValue().get(1).getSuit() == flushSuit) {
                    flushDrawCombos.put(flushDrawCombos.size(), entry.getValue());
                }
            }
            flushDrawCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushDrawCombos, board);
        }
        return flushDrawCombos;
    }

    public Map<Integer, List<Card>> getBackDoorFlushDrawCombos(List<Card> board) {
        Map<Integer, List<Card>> backDoorFlushDrawCombos = new HashMap<>();

        if(board.size() > 3) {
            return backDoorFlushDrawCombos;
        }

        Map<Character, List<Card>> suitsOfBoard = getSuitsOfBoard(board);

        char flushSuitTwoOfSameSuitOnBoard;
        char flushSuitRainbow1 = 'x';
        char flushSuitRainbow2 = 'x';
        char flushSuitRainbow3;

        Map<Integer, List<Card>> allPossibleSuitedStartHands = new HashMap<>();
        Map<Integer, List<Card>> allStartHandsThatContainFlushSuitRainbow1 = new HashMap<>();
        Map<Integer, List<Card>> allStartHandsThatContainFlushSuitRainbow2 = new HashMap<>();
        Map<Integer, List<Card>> allStartHandsThatContainFlushSuitRainbow3 = new HashMap<>();

        for (Map.Entry<Character, List<Card>> entry : suitsOfBoard.entrySet()) {
            if(entry.getValue().size() == 1) {
                if(flushSuitRainbow1 == 'x') {
                    flushSuitRainbow1 = entry.getValue().get(0).getSuit();
                    allStartHandsThatContainFlushSuitRainbow1 = getAllPossibleSuitedStartHands(flushSuitRainbow1);
                } else if (flushSuitRainbow2 == 'x') {
                    flushSuitRainbow2 = entry.getValue().get(0).getSuit();
                    allStartHandsThatContainFlushSuitRainbow2 = getAllPossibleSuitedStartHands(flushSuitRainbow2);
                } else {
                    flushSuitRainbow3 = entry.getValue().get(0).getSuit();
                    allStartHandsThatContainFlushSuitRainbow3 = getAllPossibleSuitedStartHands(flushSuitRainbow3);
                }
            } else if (entry.getValue().size() == 2) {
                flushSuitTwoOfSameSuitOnBoard = entry.getValue().get(0).getSuit();
                allPossibleSuitedStartHands = getAllNonSuitedStartHandsThatContainASpecificSuit(flushSuitTwoOfSameSuitOnBoard);
            }
        }

        for (Map.Entry<Integer, List<Card>> entry : allPossibleSuitedStartHands.entrySet()) {
            backDoorFlushDrawCombos.put(backDoorFlushDrawCombos.size(), entry.getValue());
        }
        for (Map.Entry<Integer, List<Card>> entry : allStartHandsThatContainFlushSuitRainbow1.entrySet()) {
            backDoorFlushDrawCombos.put(backDoorFlushDrawCombos.size(), entry.getValue());
        }
        for (Map.Entry<Integer, List<Card>> entry : allStartHandsThatContainFlushSuitRainbow2.entrySet()) {
            backDoorFlushDrawCombos.put(backDoorFlushDrawCombos.size(), entry.getValue());
        }
        for (Map.Entry<Integer, List<Card>> entry : allStartHandsThatContainFlushSuitRainbow3.entrySet()) {
            backDoorFlushDrawCombos.put(backDoorFlushDrawCombos.size(), entry.getValue());
        }

        backDoorFlushDrawCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(backDoorFlushDrawCombos, board);
        return backDoorFlushDrawCombos;
    }

    //helper methods
    private Map<Integer, List<List<Card>>> getSortedFlushComboMap(Map<Integer, List<Card>> comboMap, List<Card> board) {
        Map<Integer, List<List<Card>>> sortedComboMapRankOnly = new HashMap<>();
        Set<List<Card>> comboSetRankOnlyUnsorted = new HashSet<>();
        Set<List<Card>> comboSetRankOnlySorted = new TreeSet<>(sortFlushCombos(board));

        for (Map.Entry<Integer, List<Card>> entry : comboMap.entrySet()) {
            comboSetRankOnlyUnsorted.add(entry.getValue());
        }

        comboSetRankOnlySorted.addAll(comboSetRankOnlyUnsorted);
        Map<List<Card>, Set<List<Card>>> allCombosUnsortedComboIsKey = new HashMap<>();

        for(List<Card> l : comboSetRankOnlySorted) {
            allCombosUnsortedComboIsKey.put(l, new HashSet<>());
            allCombosUnsortedComboIsKey.get(l).add(l);
        }

        for(List<Card> x : comboSetRankOnlyUnsorted) {
            for(List<Card> y : comboSetRankOnlySorted) {
                Set<List<Card>> test = new TreeSet<>(sortFlushCombos(board));
                test.add(x);
                test.add(y);

                if(test.size() == 1) {
                    allCombosUnsortedComboIsKey.get(y).add(x);
                }
            }
        }

        Map<Integer, List<List<Card>>> sortedSetMap = new HashMap<>();
        List<List<Card>> comboSetRankOnlySortedAsList = new ArrayList<>();
        comboSetRankOnlySortedAsList.addAll(comboSetRankOnlySorted);

        for(int i = 0; i < comboSetRankOnlySortedAsList.size(); i++) {
            sortedSetMap.put(i, new ArrayList<>());
            sortedSetMap.get(i).add(comboSetRankOnlySortedAsList.get(i));
        }

        for(int i = 0; i < comboSetRankOnlySortedAsList.size(); i++) {
            sortedComboMapRankOnly.put(i, new ArrayList<>());
        }

        for (Map.Entry<List<Card>, Set<List<Card>>> unsortedComboIsKeyEntry : allCombosUnsortedComboIsKey.entrySet()) {
            for (Map.Entry<Integer, List<List<Card>>> sortedSetMapEntry : sortedSetMap.entrySet()) {
                int initialSize = unsortedComboIsKeyEntry.getValue().size();

                List<List<Card>> unsortedComboIsKeyEntryCopy = new ArrayList<>();
                unsortedComboIsKeyEntryCopy.addAll(unsortedComboIsKeyEntry.getValue());

                List<List<Card>> sortedSetMapEntryCopy = new ArrayList<>();
                sortedSetMapEntryCopy.addAll(sortedSetMapEntry.getValue());

                unsortedComboIsKeyEntryCopy.removeAll(sortedSetMapEntryCopy);

                if(initialSize != unsortedComboIsKeyEntryCopy.size()) {
                    sortedComboMapRankOnly.get(sortedSetMapEntry.getKey()).addAll(unsortedComboIsKeyEntry.getValue());
                    Collections.sort(sortedComboMapRankOnly.get(sortedSetMapEntry.getKey()), Card.sortCardCombosBasedOnRank());
                    if(sortedComboMapRankOnly.get(sortedSetMapEntry.getKey()).size() == 1) {
                        Collections.sort(sortedComboMapRankOnly.get(sortedSetMapEntry.getKey()).get(0), Collections.reverseOrder());
                    }
                }
            }
        }
        return sortedComboMapRankOnly;
    }


    private Map<Integer, List<Card>> getAllNonSuitedStartHandsThatContainASpecificSuit(char suit) {
        Map<Integer, List<Card>> allNonSuitedStartHandsThatContainASpecificSuit = new HashMap<>();
        Map<Integer, List<Card>> allStartHands = getAllPossibleStartHands();
        for (Map.Entry<Integer, List<Card>> entry : allStartHands.entrySet()) {
            if(entry.getValue().get(0).getSuit() == suit && entry.getValue().get(1).getSuit() != suit) {
                allNonSuitedStartHandsThatContainASpecificSuit.put(allNonSuitedStartHandsThatContainASpecificSuit.size(), entry.getValue());
            } else if (entry.getValue().get(0).getSuit() != suit && entry.getValue().get(1).getSuit() == suit) {
                allNonSuitedStartHandsThatContainASpecificSuit.put(allNonSuitedStartHandsThatContainASpecificSuit.size(), entry.getValue());
            }
        }
        return allNonSuitedStartHandsThatContainASpecificSuit;
    }

    public Map<Character, List<Card>> getSuitsOfBoard (List<Card> board) {
        Map<Character, List<Card>> suitMap = new HashMap<>();
        suitMap.put('s', new ArrayList<>());
        suitMap.put('c', new ArrayList<>());
        suitMap.put('d', new ArrayList<>());
        suitMap.put('h', new ArrayList<>());

        for(int i = 0; i < board.size(); i++) {
            if(board.get(i).getSuit() == 's'){
                suitMap.get('s').add(board.get(i));
            } else if(board.get(i).getSuit() == 'c'){
                suitMap.get('c').add(board.get(i));
            } else if(board.get(i).getSuit() == 'd'){
                suitMap.get('d').add(board.get(i));
            } else if(board.get(i).getSuit() == 'h'){
                suitMap.get('h').add(board.get(i));
            }
        }
        return suitMap;
    }

    public Map<Integer, List<Card>> getAllPossibleSuitedStartHands (char suit) {
        Map<Integer, List<Card>> allPossibleCombosOfOneSuit = new HashMap<>();

        for(int i = 0; i < 78; i++) {
            allPossibleCombosOfOneSuit.put(i, new ArrayList<>());
        }

        int counter = 13;
        int counter2 = 0;
        for(int x = 14; x > 2; x--) {
            for(int y = counter; y > 1; y--) {
                Card cardOne = new Card(x, suit);
                Card cardTwo = new Card(y, suit);
                allPossibleCombosOfOneSuit.get(counter2).add(cardOne);
                allPossibleCombosOfOneSuit.get(counter2).add(cardTwo);
                counter2++;
            }
            counter--;
        }
        return allPossibleCombosOfOneSuit;
    }

    public Comparator<List<Card>> sortFlushCombos(List<Card> board) {
        return new Comparator<List<Card>>() {
            @Override
            public int compare(List<Card> combo1, List<Card> combo2) {
                return 0;
            }
        };
    }
}
