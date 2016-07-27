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

    public static List<List<Integer>> getCombosThatGiveOOSD(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        boardRanks = removeDoubleEntriesInList(boardRanks);

        Map <Integer, List<List<Integer>>> listsOfFoundCombos = new TreeMap();
        for(int i = 1; i <= 5; i++) {
            listsOfFoundCombos.put(i, new ArrayList<List<Integer>>());
        }

        List<List<Integer>> allCombosThatMakeStraight = new ArrayList<List<Integer>>();

        Map <Integer, List<Integer>> twoCardSubBoardRankLists = getSubBoardRankLists(2, boardRanks);
        Map <Integer, List<Integer>> threeCardSubBoardRankLists = getSubBoardRankLists(3, boardRanks);

        if(boardRanks.size() < 3) {
            listsOfFoundCombos.get(1).addAll(getTwoCardsThatMakeStraight(board, boardRanks, 4));
            allCombosThatMakeStraight.addAll(listsOfFoundCombos.get(1));
            return allCombosThatMakeStraight;
        }

        if(boardRanks.size() == 3) {
            listsOfFoundCombos.get(1).addAll(getTwoCardsThatMakeStraight(board, twoCardSubBoardRankLists.get(1), 4));
            listsOfFoundCombos.get(2).addAll(getTwoCardsThatMakeStraight(board, twoCardSubBoardRankLists.get(2), 4));
            listsOfFoundCombos.get(4).addAll(getOneCardThatMakeStraight(board, threeCardSubBoardRankLists.get(1), 4));

            for(int i = 1; i <= listsOfFoundCombos.size(); i++) {
                allCombosThatMakeStraight.addAll(listsOfFoundCombos.get(i));
            }

            allCombosThatMakeStraight = removeDoubleEntriesInList(allCombosThatMakeStraight);
            List<List<Integer>> straightCombos = getCombosThatMakeStraight(board);
            allCombosThatMakeStraight.removeAll(straightCombos);

            return allCombosThatMakeStraight;
        }

        if(boardRanks.size() == 4) {
            listsOfFoundCombos.get(1).addAll(getTwoCardsThatMakeStraight(board, twoCardSubBoardRankLists.get(1), 4));
            listsOfFoundCombos.get(2).addAll(getTwoCardsThatMakeStraight(board, twoCardSubBoardRankLists.get(2), 4));
            listsOfFoundCombos.get(3).addAll(getTwoCardsThatMakeStraight(board, twoCardSubBoardRankLists.get(3), 4));
            listsOfFoundCombos.get(4).addAll(getOneCardThatMakeStraight(board, threeCardSubBoardRankLists.get(1), 4));
            listsOfFoundCombos.get(5).addAll(getOneCardThatMakeStraight(board, threeCardSubBoardRankLists.get(2), 4));

            for(int i = 1; i <= listsOfFoundCombos.size(); i++) {
                allCombosThatMakeStraight.addAll(listsOfFoundCombos.get(i));
            }

            allCombosThatMakeStraight = removeDoubleEntriesInList(allCombosThatMakeStraight);
            List<List<Integer>> straightCombos = getCombosThatMakeStraight(board);
            allCombosThatMakeStraight.removeAll(straightCombos);

            //getCombosThatMakeWheelStraight(board);

            return allCombosThatMakeStraight;
        }
        return null;
    }


    public static List<List<Integer>> getCombosThatMakeStraight(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        boardRanks = removeDoubleEntriesInList(boardRanks);

        Map <Integer, List<List<Integer>>> listsOfFoundCombos = new TreeMap();
        for(int i = 1; i <= 5; i++) {
            listsOfFoundCombos.put(i, new ArrayList<List<Integer>>());
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
        return null;
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

    public static List<List<Integer>> getCombosThatGiveWheelStraightDraw(List<Card> board) {
        List<List<Integer>> combosThatMakeWheelStraight = new ArrayList<List<Integer>>();
        List<Integer> boardRanksInitial = getSortedCardRanksFromCardList(board);
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
//        List<Integer> numbersToRetain = new ArrayList<Integer>();
//        numbersToRetain.add(14);
//        numbersToRetain.add(2);
//        numbersToRetain.add(3);
//        numbersToRetain.add(4);
//        numbersToRetain.add(5);

        //get alle kaarten onder de 6 incl A
//        boardRanks.retainAll(numbersToRetain);

        //als dit 4 kaarten zijn, dan niets doen
//        if(boardRanks.size() == 4) {
//            return combosThatMakeWheelStraight;
//        }

        //als het 3 of 2 kaarten zijn, voeg hieraan de oosd combos toe
        List<List<Integer>> combosThatGiveOOSD = getCombosThatGiveOOSD(board);
        List<List<Integer>> fictionalBoards = new ArrayList<List<Integer>>();
        fictionalBoards.addAll(getCombosThatGiveOOSD(board));

        Map<Integer, List<Integer>> fictionalBoardsMap = new HashMap<Integer, List<Integer>>();

        Map <Integer, List<Integer>> sblists = getSubBoardRankLists(3, boardRanks);

        //hier gaat het nog mis, dit kan een board van 6 kaarten opleveren als boardRanks 4 is, vandaar getSubBoardRankLists zoals hierboven.
        for(int i = 0; i < combosThatGiveOOSD.size(); i++) {
            fictionalBoardsMap.put(i, fictionalBoards.get(i));
            fictionalBoardsMap.get(i).addAll(boardRanks);
        }

        //kijk bij het nieuw gegenereerde board hoeveel combos een straight geven.
        Map<List<Integer>, List<List<Integer>>> fictionalStraightCombos = new HashMap<List<Integer>, List<List<Integer>>>();
        for(int i = 0; i < fictionalBoardsMap.size(); i++) {
            fictionalStraightCombos.put(combosThatGiveOOSD.get(i), getCombosThatMakeStraight(convertIntegerBoardToArtificialCardBoard(fictionalBoardsMap.get(i))));
        }

        //Als dit er 13 zijn ipv 25, dan geeft de combo een wheel straight
        for(List<List<Integer>> list : fictionalStraightCombos.values()) {
            if (list.size() < 20) {
                combosThatMakeWheelStraight.add(getKeyByValue(fictionalStraightCombos, list));
                fictionalStraightCombos.get(getKeyByValue(fictionalStraightCombos, list)).clear();
            }
        }

        return combosThatMakeWheelStraight;

//        List<List<Integer>> newList = new ArrayList<List<Integer>>();
//
//        for(List<Integer> l : combosThatMakeWheelStraight) {
//            boardRanksInitial.addAll(l);
//            if(boardRanksInitial.equals(numbersToRetain)) {
//                newList.add(l);
//            }
//        }
//
//        return newList;
    }


    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
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
        //straightCombos.clear();
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