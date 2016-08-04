package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by LPO10346 on 21-6-2016.
 */
public class BoardEvaluator {
    public static boolean isBoardRainbow(List<Card> board) {
        if(getNumberOfSuitedCardsOnBoard(board) == 0) {
            return true;
        }
        return false;
    }

    public static boolean hasBoardTwoOfOneSuit(List<Card> board) {
        if(getNumberOfSuitedCardsOnBoard(board) == 2) {
            return true;
        }
        return false;
    }

    public static boolean hasBoardThreeOfOneSuit(List<Card> board) {
        if(getNumberOfSuitedCardsOnBoard(board) == 3 && (!isBoardSuited(board))) {
            return true;
        }
        return false;
    }

    public static boolean hasBoardFourOfOneSuit(List<Card> board) {
        if(getNumberOfSuitedCardsOnBoard(board) == 4 && (!isBoardSuited(board))) {
            return true;
        }
        return false;
    }

    public static boolean isBoardSuited(List<Card> board) {
        if(getNumberOfSuitedCardsOnBoard(board) == board.size()) {
            return true;
        }
        return false;
    }

    public static boolean isBoardConnected(List<Card> board) {
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

    public static boolean hasBoardTwoConnectingCards(List<Card> board) {
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

    public static boolean isBoardPairedOnce(List<Card> board) {
        if(getNumberOfPairsOnBoard(board) == 1) {
            return true;
        }
        return false;
    }

    public static boolean isBoardPairedTwice(List<Card> board) {
        if(getNumberOfPairsOnBoard(board) == 2) {
            return true;
        }
        return false;
    }

    public static List<List<Integer>> getCombosThatMakeStraight(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        boardRanks = removeDoubleEntriesInList(boardRanks);

        Map <Integer, List<List<Integer>>> listsOfFoundCombos = new TreeMap();
        for(int i = 1; i <= 7; i++) {
            listsOfFoundCombos.put(i, new ArrayList<>());
        }

        List<List<Integer>> allCombosThatMakeStraight = new ArrayList<List<Integer>>();

        Map <Integer, List<Integer>> threeCardSubBoardRankLists = getSubBoardRankLists(3, boardRanks);
        Map <Integer, List<Integer>> fourCardSubBoardRankLists = getSubBoardRankLists(4, boardRanks);

        if(boardRanks.size() < 4) {
            listsOfFoundCombos.get(1).addAll(getTwoCardsThatMakeStraight(board, boardRanks, 5));
            allCombosThatMakeStraight.addAll(listsOfFoundCombos.get(1));
            return allCombosThatMakeStraight;
        }

        if(boardRanks.size() == 4) {
            listsOfFoundCombos.get(1).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(1), 5));
            listsOfFoundCombos.get(2).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(2), 5));
            listsOfFoundCombos.get(4).addAll(getOneCardThatMakeStraight(board, fourCardSubBoardRankLists.get(1), 5));

            for(int i = 1; i <= listsOfFoundCombos.size(); i++) {
                allCombosThatMakeStraight.addAll(listsOfFoundCombos.get(i));
            }

            allCombosThatMakeStraight = removeDoubleEntriesInList(allCombosThatMakeStraight);

            return allCombosThatMakeStraight;
        }

        if(boardRanks.size() == 5) {
            listsOfFoundCombos.get(1).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(1), 5));
            listsOfFoundCombos.get(2).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(2), 5));
            listsOfFoundCombos.get(3).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(3), 5));
            listsOfFoundCombos.get(4).addAll(getOneCardThatMakeStraight(board, fourCardSubBoardRankLists.get(1), 5));
            listsOfFoundCombos.get(5).addAll(getOneCardThatMakeStraight(board, fourCardSubBoardRankLists.get(2), 5));

            for(int i = 1; i <= listsOfFoundCombos.size(); i++) {
                allCombosThatMakeStraight.addAll(listsOfFoundCombos.get(i));
            }

            allCombosThatMakeStraight = removeDoubleEntriesInList(allCombosThatMakeStraight);

            return allCombosThatMakeStraight;
        }

        if(boardRanks.size() == 6) {
            listsOfFoundCombos.get(1).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(1), 5));
            listsOfFoundCombos.get(2).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(2), 5));
            listsOfFoundCombos.get(3).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(3), 5));
            listsOfFoundCombos.get(4).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(4), 5));

            listsOfFoundCombos.get(5).addAll(getOneCardThatMakeStraight(board, fourCardSubBoardRankLists.get(1), 5));
            listsOfFoundCombos.get(6).addAll(getOneCardThatMakeStraight(board, fourCardSubBoardRankLists.get(2), 5));
            listsOfFoundCombos.get(7).addAll(getOneCardThatMakeStraight(board, fourCardSubBoardRankLists.get(3), 5));

            for(int i = 1; i <= listsOfFoundCombos.size(); i++) {
                allCombosThatMakeStraight.addAll(listsOfFoundCombos.get(i));
            }

            allCombosThatMakeStraight = removeDoubleEntriesInList(allCombosThatMakeStraight);

            return allCombosThatMakeStraight;
        }
        return null;
    }

    public static Map<Integer, List<Integer>> getCombosThatGiveOosdOrDoubleGutter(List<Card> board) {
        Map<List<Integer>, List<List<Integer>>> allStraightDrawCombos = getCombosThatGiveAnyStraightDraw(board);
        Map<Integer, List<Integer>> oosdCombos = new HashMap<>();
        int counter = 0;

        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : allStraightDrawCombos.entrySet()) {
            if (entry.getValue().size() > 20) {
                oosdCombos.put(counter, entry.getKey());
                counter++;
            }
        }
        return oosdCombos;
    }

    public static Map<Integer, List<Integer>> getCombosThatGiveGutshot (List<Card> board) {
        Map<List<Integer>, List<List<Integer>>> allStraightDrawCombos = getCombosThatGiveAnyStraightDraw(board);
        Map<Integer, List<Integer>> gutshotCombos = new HashMap<>();
        int counter = 0;

        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : allStraightDrawCombos.entrySet()) {
            if (entry.getValue().size() > 9 && entry.getValue().size() < 15) {
                gutshotCombos.put(counter, entry.getKey());
                counter++;
            }
        }
        return gutshotCombos;
    }

    public static Map<Integer, List<Integer>> getCombosThatGiveBackDoorStraightDraw(List<Card> board) {
        Map<List<Integer>, List<List<Integer>>> allStraightDrawCombos = getCombosThatGiveAnyStraightDraw(board);
        Map<Integer, List<Integer>> backdoorCombos = new HashMap<>();
        int counter = 0;

        if(board.size() < 4) {
            for (Map.Entry<List<Integer>, List<List<Integer>>> entry : allStraightDrawCombos.entrySet()) {
                if (entry.getValue().size() < 8) {
                    backdoorCombos.put(counter, entry.getKey());
                    counter++;
                }
            }
        }
        return backdoorCombos;
    }

    //helper methods
    private static int getNumberOfSuitedCardsOnBoard(List<Card> board) {
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

    private static int getNumberOfPairsOnBoard(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        int x = 0;
        int y = 0;
        for(int i : boardRanks) {
            if(Collections.frequency(boardRanks, i) == 2 && i != y) {
                x++;
                y = i;
            }
        }
        return x;
    }

    private static List<Integer> getSortedCardRanksFromCardList(List<Card> board) {
        List<Integer> boardRanks = new ArrayList<Integer>();
        for(Card c : board) {
            boardRanks.add(c.getRank());
        }
        Collections.sort(boardRanks);
        return boardRanks;
    }

    private static boolean boardContainsAceAndWheelCard(List<Card> board) {
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

    public static Map<List<Integer>, List<List<Integer>>> getCombosThatGiveAnyStraightDraw(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        Map<Integer, List<Integer>> allCardCombos = getAllPossibleCombos();
        Map<Integer, List<Integer>> fictionalBoardRanks = new HashMap<>();
        List<List<Integer>> allStraightCombos = getCombosThatMakeStraight(board);
        List<List<Integer>> allCardCombosCorrectedForStraightCombos = new ArrayList<>();
        Map<List<Integer>, List<List<Integer>>> mapOfCombosThatGiveAnyStraightDraw = new HashMap<>();

        for(int i = 0; i < allCardCombos.size(); i++) {
            Collections.sort(allCardCombos.get(i));
        }

        if(allStraightCombos != null) {
            for(List<Integer> l : allStraightCombos) {
                Collections.sort(l);
            }
        }

        for (int i = 0; i < allCardCombosCorrectedForStraightCombos.size(); i++) {
            allCardCombos.put(i, allCardCombosCorrectedForStraightCombos.get(i));
        }

        for(int i = 0; i < allCardCombos.size(); i++) {
            List<Integer> copyOfBoardRanks = new ArrayList<>();
            copyOfBoardRanks.addAll(boardRanks);
            fictionalBoardRanks.put(i, copyOfBoardRanks);
            fictionalBoardRanks.get(i).addAll(allCardCombos.get(i));
        }

        for(int i = 0; i < fictionalBoardRanks.size(); i++) {
            List<Card> x = convertIntegerBoardToArtificialCardBoard(fictionalBoardRanks.get(i));
            mapOfCombosThatGiveAnyStraightDraw.put(allCardCombos.get(i), getCombosThatMakeStraight(x));
        }

        Set<List<Integer>> combosThatNeedToBeRemovedFromMap = new HashSet<>();
        for(List<List<Integer>> list : mapOfCombosThatGiveAnyStraightDraw.values()) {
            if(list.isEmpty()) {
                Set<List<Integer>> keysOfCombosThatNeedToBeRemoved = getKeysByValue(mapOfCombosThatGiveAnyStraightDraw, list);
                combosThatNeedToBeRemovedFromMap.addAll(keysOfCombosThatNeedToBeRemoved);
            }
        }

        for(List<Integer> list1 : combosThatNeedToBeRemovedFromMap) {
            mapOfCombosThatGiveAnyStraightDraw.remove(list1);
        }

        mapOfCombosThatGiveAnyStraightDraw = removeIncorrectStraightCombosFromMap(mapOfCombosThatGiveAnyStraightDraw, board);
        return mapOfCombosThatGiveAnyStraightDraw;
    }

    private static Map<List<Integer>, List<List<Integer>>> removeIncorrectStraightCombosFromMap(Map<List<Integer>, List<List<Integer>>> mapOfCombosThatGiveAnyStraightDraw, List<Card> board) {
        Map<List<Integer>, List<List<Integer>>> baseMap = mapOfCombosThatGiveAnyStraightDraw;
        Map<Integer, List<Integer>> allCombined = new HashMap<>();
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        int counter = 0;
        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : baseMap.entrySet()) {
            entry.getValue().removeAll(getCombosThatMakeStraight(board));
        }

        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : baseMap.entrySet()) {
            for(int i = 0; i < entry.getValue().size(); i++) {
                List<Integer> newBoardRanks = new ArrayList<>();
                newBoardRanks.addAll(boardRanks);
                allCombined.put(counter, newBoardRanks);
                allCombined.get(counter).addAll(entry.getKey());
                allCombined.get(counter).addAll(entry.getValue().get(i));
                counter++;
            }
        }

        Map<Integer, List<Integer>> allPossibleStraights = getAllPossibleFiveConnectingCards();
        Map<Integer, List<Integer>> allCombinedCopy = new HashMap<>();
        Map<Integer, List<Integer>> highCardsMapFive = new HashMap<>();

        for (int i = 0; i < allCombined.size(); i++) {
            allCombinedCopy.put(i, allCombined.get(i));
        }

        for (int i = 0; i < allCombinedCopy.size(); i++) {
            for (int z = 0; z < allPossibleStraights.size(); z++) {
                Set<Integer> s = new HashSet<>();
                s.addAll(allCombinedCopy.get(i));
                int sizeInitial = s.size();
                s.removeAll(allPossibleStraights.get(z));
                int sizeAfter = s.size();
                if(sizeInitial - sizeAfter == 5) {
                    if(!s.iterator().hasNext()) {
                        highCardsMapFive.put(i, allPossibleStraights.get(z));
                        break;
                    }
                    if(s.iterator().hasNext() && s.iterator().next() != allPossibleStraights.get(z).get(4) + 1) {
                        highCardsMapFive.put(i, allPossibleStraights.get(z));
                        break;
                    }
                }
            }
        }

        int counter2 = 0;
        Map<Integer, List<Integer>> theCorrectKeyMap = new HashMap<>();
        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : baseMap.entrySet()) {
            for(int i = 0; i < entry.getValue().size(); i++) {
                List<Integer> theKeyYouNeed = new ArrayList<>();
                theKeyYouNeed.addAll(entry.getKey());
                theCorrectKeyMap.put(counter2, theKeyYouNeed);
                counter2++;
            }
        }

        Map<Integer, List<Integer>> combosThatHaveToBeRemoved = new HashMap<>();
        int counter3 = 0;

        Map<Integer, List<Integer>> copyHighCardsMapFive = highCardsMapFive;
        Map<Integer, List<Integer>> copyTheCorrectKeyMap = theCorrectKeyMap;
        for(int i = 0; i < theCorrectKeyMap.size(); i++) {
            List<Integer> x = new ArrayList<>();
            x.addAll(copyHighCardsMapFive.get(i));
            x.removeAll(copyTheCorrectKeyMap.get(i));
            if(x.size() > 4) {
                List<Integer> removeCombo = new ArrayList<>();
                removeCombo.addAll(theCorrectKeyMap.get(i));
                combosThatHaveToBeRemoved.put(counter3, removeCombo);
                counter3++;
            }
        }

        for(int i = 0; i < theCorrectKeyMap.size(); i++) {
            if(!(highCardsMapFive.get(i).contains(theCorrectKeyMap.get(i).get(0)))) {
                if(highCardsMapFive.get(i).contains(theCorrectKeyMap.get(i).get(1))) {
                    if(boardRanks.contains(theCorrectKeyMap.get(i).get(1))) {
                        List<Integer> removeCombo = new ArrayList<>();
                        removeCombo.addAll(theCorrectKeyMap.get(i));
                        combosThatHaveToBeRemoved.put(counter3, removeCombo);
                        counter3++;
                    }
                }
            }
            if(!(highCardsMapFive.get(i).contains(theCorrectKeyMap.get(i).get(1)))) {
                if(highCardsMapFive.get(i).contains(theCorrectKeyMap.get(i).get(0))) {
                    if(boardRanks.contains(theCorrectKeyMap.get(i).get(0))) {
                        List<Integer> removeCombo = new ArrayList<>();
                        removeCombo.addAll(theCorrectKeyMap.get(i));
                        combosThatHaveToBeRemoved.put(counter3, removeCombo);
                        counter3++;
                    }
                }
            }
        }

        for(int i = 0; i < theCorrectKeyMap.size(); i++) {
            if(boardRanks.containsAll(theCorrectKeyMap.get(i))) {
                List<Integer> removeCombo = new ArrayList<>();
                removeCombo.addAll(theCorrectKeyMap.get(i));
                combosThatHaveToBeRemoved.put(counter3, removeCombo);
                counter3++;
            }
        }

        List<List<Integer>> cleanedForDoubleEntriesCombosToBeRemoved = new ArrayList<>();
        for(int i = 0; i < combosThatHaveToBeRemoved.size(); i++) {
            cleanedForDoubleEntriesCombosToBeRemoved.add(combosThatHaveToBeRemoved.get(i));
        }

        Set<List<Integer>> hs = new HashSet<>();
        hs.addAll(cleanedForDoubleEntriesCombosToBeRemoved);
        cleanedForDoubleEntriesCombosToBeRemoved.clear();
        cleanedForDoubleEntriesCombosToBeRemoved.addAll(hs);

        combosThatHaveToBeRemoved.clear();

        for(int i = 0; i < cleanedForDoubleEntriesCombosToBeRemoved.size(); i++) {
            combosThatHaveToBeRemoved.put(i, cleanedForDoubleEntriesCombosToBeRemoved.get(i));
        }

        for(int i = 0; i < combosThatHaveToBeRemoved.size(); i++) {
            baseMap.remove(combosThatHaveToBeRemoved.get(i));
        }

        List<List<Integer>> hs2 = new ArrayList<>();
        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : baseMap.entrySet()) {
            if(entry.getValue().isEmpty()) {
                hs2.add(entry.getKey());
            }
        }

        for(int i = 0; i < hs2.size(); i++) {
            baseMap.remove(hs2.get(i));
        }

        Map<List<Integer>, List<List<Integer>>> keysOfWhichSomeValuesNeedToBeRemovedBecauseKeyMakesStraightOnBoard = new HashMap<>();

        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : baseMap.entrySet()) {
            if(comboIsAStraightComboOnTheBoard(entry.getKey(), board)) {
                List<Integer> highestFiveConnectingCards = getHighestFiveConnectingCardsOnBoard(boardRanks, entry.getKey());
                int highestCard = highestFiveConnectingCards.get(4);
                keysOfWhichSomeValuesNeedToBeRemovedBecauseKeyMakesStraightOnBoard.put(entry.getKey(), new ArrayList<>());
                for(List<Integer> list : baseMap.get(entry.getKey())) {
                    if(list.get(0) != highestCard + 1 && list.get(1) != highestCard + 1) {
                        List<Integer> copyOfListToBeRemoved = new ArrayList<>();
                        copyOfListToBeRemoved.addAll(list);
                        keysOfWhichSomeValuesNeedToBeRemovedBecauseKeyMakesStraightOnBoard.get(entry.getKey()).add(copyOfListToBeRemoved);
                    }
                }
            }
        }

        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : baseMap.entrySet()) {
            for (Map.Entry<List<Integer>, List<List<Integer>>> entry2 : keysOfWhichSomeValuesNeedToBeRemovedBecauseKeyMakesStraightOnBoard.entrySet()) {
                if(entry.getKey().equals(entry2.getKey())) {
                    List<List<Integer>> cleanList = new ArrayList<>();
                    cleanList.addAll(entry.getValue());
                    cleanList.removeAll(entry2.getValue());

                    baseMap.get(entry.getKey()).clear();
                    baseMap.get(entry.getKey()).addAll(cleanList);
                }
            }
        }
        return baseMap;
    }

    private static boolean comboIsAStraightComboOnTheBoard (List<Integer> combo, List<Card> board) {
        List<List<Integer>> allCombosThatMakeStraight = getCombosThatMakeStraight(board);
        for(List<Integer> straightCombo : allCombosThatMakeStraight) {
            if(combo.equals(straightCombo)) {
                return true;
            }
        }
        return false;
    }

    private static List<Integer> getHighestFiveConnectingCardsOnBoard(List<Integer> board, List<Integer> combo) {
        List<Integer> boardPlusCombo = new ArrayList<>();
        boardPlusCombo.addAll(board);
        boardPlusCombo.addAll(combo);
        Map<Integer, List<Integer>> allPossibleFiveConnectingCards = getAllPossibleFiveConnectingCards();
        Map<Integer, List<Integer>> fiveConnectingCardsThatArePresentOnBoard = new HashMap<>();

        int counter = 0;
        for(int i = 0; i < allPossibleFiveConnectingCards.size(); i++) {
            if(boardPlusCombo.containsAll(allPossibleFiveConnectingCards.get(i))) {
                fiveConnectingCardsThatArePresentOnBoard.put(counter, allPossibleFiveConnectingCards.get(i));
                counter++;
            }
        }

        List<Integer> highestFiveConnectingCards = fiveConnectingCardsThatArePresentOnBoard.get(counter-1);
        return highestFiveConnectingCards;
    }


    private static Map<Integer, List<Integer>> getAllPossibleFiveConnectingCards() {
        Map<Integer, List<Integer>> allPossibleStraights = new HashMap<>();
        List<Integer> lowestStraight = new ArrayList<>();
        lowestStraight.add(14);
        lowestStraight.add(2);
        lowestStraight.add(3);
        lowestStraight.add(4);
        lowestStraight.add(5);
        allPossibleStraights.put(0, lowestStraight);

        //Map<Integer, List<Integer>> tussenMap = new HashMap<>();
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


    private static Map<Integer, List<Integer>> getAllPossibleCombos() {
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

    private static <E> List<E> getDoubleEntriesFromList(List<E> list) {
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

    private static <E> List<E> removeDoubleEntriesInList(List<E> list) {
        Set<E> hs = new HashSet<E>();
        hs.addAll(list);
        list.clear();
        list.addAll(hs);
        return list;
    }

    private static Map <Integer, List<Integer>> getSubBoardRankLists(int numberOValuesInSublist, List<Integer> boardRanks) {
        int numberOfLists;
        if(boardRanks.size() > numberOValuesInSublist) {
            numberOfLists = 1 + (boardRanks.size() - numberOValuesInSublist);
        } else {
            numberOfLists = 1;
        }

        Map <Integer, List<Integer>> subBoardRankLists = new TreeMap();
        int counter = 0;
        for(int i = 1; i <= numberOfLists; i++) {
            subBoardRankLists.put(i, new ArrayList<Integer>());
            if(boardRanks.size() < numberOValuesInSublist) {
                for(int z = counter; z < counter + boardRanks.size(); z++) {
                    subBoardRankLists.get(i).add(boardRanks.get(z));
                }
                counter++;
            } else {
                for(int z = counter; z < counter + numberOValuesInSublist; z++) {
                    subBoardRankLists.get(i).add(boardRanks.get(z));
                }
                counter++;
            }
        }
        return subBoardRankLists;
    }

    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    private static List<Card> convertIntegerBoardToArtificialCardBoard(List<Integer> integerBoarList) {
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

    private static List<List<Integer>> getTwoCardsThatMakeStraight(List<Card> board, List<Integer> subBoardRanks, int number) {
        List<Integer> combo = new ArrayList<Integer>();
        List<List<Integer>> straightCombos = new ArrayList<List<Integer>>();
        List<Integer> subBoardRanksCopy = new ArrayList<Integer>();
        subBoardRanksCopy.addAll(subBoardRanks);
        for(int i = 1; i < 15; i++) {
            combo.clear();
            combo.add(i);
            for(int j = 14; j > i; j--) {
                combo.add(j);
                subBoardRanks.addAll(combo);
                Collections.sort(subBoardRanks);
                int connectingCardCounter = 1;
                for(int z = 0; z < (subBoardRanks.size()-1); z++) {
                    if(subBoardRanks.get(z) + 1 == subBoardRanks.get(z+1)) {
                        connectingCardCounter++;
                    }
                }
                if(connectingCardCounter == number) {
                    if(board.size() == 5 && isBoardConnected(board)) {
                        if(combo.get(0) == getValueOfHighestCardOnBoard(board) + 1 || combo.get(1) == getValueOfHighestCardOnBoard(board) + 1) {
                            if(comboContainsLowAce(combo)) {
                                List<Integer> convertedCombo = convertComboWithLowAceToHighAce(combo);
                                List<Integer> copiedCombo = makeCopyOfComboToAddToReturnList(convertedCombo);
                                straightCombos.add(copiedCombo);
                            }
                            else {
                                Collections.sort(combo);
                                List<Integer> copiedCombo = makeCopyOfComboToAddToReturnList(combo);
                                straightCombos.add(copiedCombo);
                            }
                        }
                    }
                    else {
                        if(comboContainsLowAce(combo)) {
                            List<Integer> convertedCombo = convertComboWithLowAceToHighAce(combo);
                            List<Integer> copiedCombo = makeCopyOfComboToAddToReturnList(convertedCombo);
                            straightCombos.add(copiedCombo);
                        }
                        else {
                            Collections.sort(combo);
                            List<Integer> copiedCombo = makeCopyOfComboToAddToReturnList(combo);
                            straightCombos.add(copiedCombo);
                        }
                    }
                }
                subBoardRanks.clear();
                subBoardRanks.addAll(subBoardRanksCopy);

                if(boardContainsAce(board)) {
                    subBoardRanks = addLowAceToSubBoardRanksAndRemoveHighAceIfPresent(subBoardRanks, subBoardRanks.size());
                    subBoardRanks.addAll(combo);
                    Collections.sort(subBoardRanks);
                    int connectingCardCounterAceBoard = 1;
                    for(int z = 0; z < (subBoardRanks.size()-1); z++) {
                        if(subBoardRanks.get(z) + 1 == subBoardRanks.get(z+1)) {
                            connectingCardCounterAceBoard++;
                        }
                    }
                    if(connectingCardCounterAceBoard == number) {
                        if (!isBoardConnected(board)) {
                            Collections.sort(combo);
                            List<Integer> copiedCombo = makeCopyOfComboToAddToReturnList(combo);
                            straightCombos.add(copiedCombo);
                        }
                        if(isBoardConnected(board) && !board.contains(13)) {
                            if(combo.get(0) == board.size() + 1 || combo.get(1) == board.size() + 1) {
                                Collections.sort(combo);
                                List<Integer> copiedCombo = makeCopyOfComboToAddToReturnList(combo);
                                straightCombos.add(copiedCombo);
                            }
                        }
                    }
                    subBoardRanks.clear();
                    subBoardRanks.addAll(subBoardRanksCopy);
                }
                combo.remove(combo.size()-1);
            }
        }
        return straightCombos;
    }

    private static List<List<Integer>> getOneCardThatMakeStraight(List<Card> board, List<Integer> subBoardRanks, int number) {
        final List<Integer> combo = new ArrayList<Integer>();
        final List<List<Integer>> straightCombos = new ArrayList<List<Integer>>();
        List<Integer> subBoardRanksCopy = new ArrayList<Integer>();
        subBoardRanksCopy.addAll(subBoardRanks);

        class HelperClassForInnerMethod {
            private void addCombos() {
                for(int z = 2; z < 15; z++) {
                    List<Integer> createdCombo = addSecondCardToCreateComboWhenSingleCardMakesStraight(combo, z);
                    if(comboContainsLowAce(createdCombo)) {
                        List<Integer> convertedCombo = convertComboWithLowAceToHighAce(createdCombo);
                        straightCombos.add(convertedCombo);
                    }
                    else {
                        straightCombos.add(createdCombo);
                    }
                }
            }
        }

        for(int i = 1; i < 15; i++) {
            combo.clear();
            combo.add(i);
            subBoardRanks.addAll(combo);
            Collections.sort(subBoardRanks);
            int connectingCardCounter = 1;
            for(int z = 0; z < (subBoardRanks.size()-1); z++) {
                if(subBoardRanks.get(z) + 1 == subBoardRanks.get(z+1)) {
                    connectingCardCounter++;
                }
            }
            if(connectingCardCounter == number) {
                if(!isBoardConnected(board) || board.size() == number - 1) {
                    HelperClassForInnerMethod h = new HelperClassForInnerMethod();
                    h.addCombos();
                }
                else {
                    if(board.size() == number) {
                        if(combo.get(0) == getValueOfHighestCardOnBoard(board) + 1) {
                            HelperClassForInnerMethod h = new HelperClassForInnerMethod();
                            h.addCombos();
                        }
                    }
                }
            }
            subBoardRanks.clear();
            subBoardRanks.addAll(subBoardRanksCopy);

            if(boardContainsAce(board)) {
                subBoardRanks = addLowAceToSubBoardRanksAndRemoveHighAceIfPresent(subBoardRanks, subBoardRanks.size());
                subBoardRanks.addAll(combo);
                Collections.sort(subBoardRanks);
                int connectingCardCounterAceBoard = 1;
                for(int z = 0; z < (subBoardRanks.size()-1); z++) {
                    if(subBoardRanks.get(z) + 1 == subBoardRanks.get(z+1)) {
                        connectingCardCounterAceBoard++;
                    }
                }
                if(connectingCardCounterAceBoard == number) {
                    if (!isBoardConnected(board)) {
                        HelperClassForInnerMethod h = new HelperClassForInnerMethod();
                        h.addCombos();
                    }
                }
                if(isBoardConnected(board) && !subBoardRanksCopy.contains(13)) {
                    if(combo.get(0) == board.size() + 1) {
                        HelperClassForInnerMethod h = new HelperClassForInnerMethod();
                        h.addCombos();
                    }
                }
                subBoardRanks.clear();
                subBoardRanks.addAll(subBoardRanksCopy);
            }
        }
        return straightCombos;
    }

    private static boolean boardContainsAce(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        Integer ace = 14;
        if(boardRanks.contains(ace)) {
            return true;
        }
        return false;
    }

    private static boolean comboContainsLowAce(List<Integer> combo) {
        for(Integer i : combo) {
            if(i == 1) {
                return true;
            }
        }
        return false;
    }

    private static List<Integer> addLowAceToSubBoardRanksAndRemoveHighAceIfPresent(List<Integer> subBoardRanks, int wantedSubBoardRanksSize) {
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

    private static List<Integer> convertComboWithLowAceToHighAce(List<Integer> combo) {
        Integer aceLow = 1;
        Integer aceHigh = 14;
        combo.remove(aceLow);
        combo.add(aceHigh);
        Collections.sort(combo);
        return combo;
    }

    private static List<Integer> makeCopyOfComboToAddToReturnList(List<Integer> combo) {
        List<Integer> copiedCombo = new ArrayList<Integer>();
        copiedCombo.addAll(combo);
        return copiedCombo;
    }


    private static List<Integer> addSecondCardToCreateComboWhenSingleCardMakesStraight(List<Integer> combo, Integer i) {
        List<Integer> createdCombo = new ArrayList<Integer>();
        createdCombo.add(i);
        createdCombo.addAll(combo);
        Collections.sort(createdCombo);
        return createdCombo;
    }

    private static int getValueOfHighestCardOnBoard (List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        return boardRanks.get(boardRanks.size() - 1);
    }

    public static List<BooleanResult> allFunctions(List<Card> board) {
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