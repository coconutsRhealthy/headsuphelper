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

    public static List<String> getCombosThatMakeStraight(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        Set<Integer> boardRankClearedDoubleCards = new HashSet<Integer>();
        boardRankClearedDoubleCards.addAll(boardRanks);
        boardRanks.clear();
        boardRanks.addAll(boardRankClearedDoubleCards);

        List<Integer> subBoardRanks1 = new ArrayList<Integer>();
        List<Integer> subBoardRanks2 = new ArrayList<Integer>();
        List<Integer> subBoardRanks3 = new ArrayList<Integer>();
        List<Integer> subBoardRanks4 = new ArrayList<Integer>();
        List<Integer> subBoardRanks5 = new ArrayList<Integer>();

        List<String> twoCardsThatMakeStraightList1;
        List<String> twoCardsThatMakeStraightList2;
        List<String> twoCardsThatMakeStraightList3;
        List<String> oneCardThatMakesStraightList1;
        List<String> oneCardThatMakesStraightList2;

        List<String> allCombosThatMakeStraight = new ArrayList<String>();

        if(boardRanks.size() == 1) {
            subBoardRanks1.add(boardRanks.get(0));
        }

        if(boardRanks.size() == 2) {
            subBoardRanks1.add(boardRanks.get(0));
            subBoardRanks1.add(boardRanks.get(1));
        }

        if(boardRanks.size() == 3) {
            subBoardRanks1.add(boardRanks.get(0));
            subBoardRanks1.add(boardRanks.get(1));
            subBoardRanks1.add(boardRanks.get(2));
        }

        if(boardRanks.size() == 4 || boardRanks.size() == 5) {
            subBoardRanks1.add(boardRanks.get(0));
            subBoardRanks1.add(boardRanks.get(1));
            subBoardRanks1.add(boardRanks.get(2));

            subBoardRanks2.add(boardRanks.get(1));
            subBoardRanks2.add(boardRanks.get(2));
            subBoardRanks2.add(boardRanks.get(3));

            subBoardRanks4.add(boardRanks.get(0));
            subBoardRanks4.add(boardRanks.get(1));
            subBoardRanks4.add(boardRanks.get(2));
            subBoardRanks4.add(boardRanks.get(3));
        }

        if(boardRanks.size() == 5) {
            subBoardRanks3.add(boardRanks.get(2));
            subBoardRanks3.add(boardRanks.get(3));
            subBoardRanks3.add(boardRanks.get(4));

            subBoardRanks5.add(boardRanks.get(1));
            subBoardRanks5.add(boardRanks.get(2));
            subBoardRanks5.add(boardRanks.get(3));
            subBoardRanks5.add(boardRanks.get(4));
        }

        if(boardRanks.size() < 4) {
            twoCardsThatMakeStraightList1 = getTwoCardsThatMakeStraight(board, boardRanks);
            allCombosThatMakeStraight.addAll(twoCardsThatMakeStraightList1);
            return allCombosThatMakeStraight;
        }

        if(boardRanks.size() == 4) {
            twoCardsThatMakeStraightList1 = getTwoCardsThatMakeStraight(board, subBoardRanks1);
            twoCardsThatMakeStraightList2 = getTwoCardsThatMakeStraight(board, subBoardRanks2);
            oneCardThatMakesStraightList1 = getOneCardThatMakeStraight(board, subBoardRanks4);

            allCombosThatMakeStraight.addAll(twoCardsThatMakeStraightList1);
            allCombosThatMakeStraight.addAll(twoCardsThatMakeStraightList2);
            allCombosThatMakeStraight.addAll(oneCardThatMakesStraightList1);

            Set<String> allCombosThatMakeStraightClearedDoubleEntries = new HashSet<String>();
            allCombosThatMakeStraightClearedDoubleEntries.addAll(allCombosThatMakeStraight);
            allCombosThatMakeStraight.clear();
            allCombosThatMakeStraight.addAll(allCombosThatMakeStraightClearedDoubleEntries);

            return allCombosThatMakeStraight;
        }

        if(boardRanks.size() == 5) {
            twoCardsThatMakeStraightList1 = getTwoCardsThatMakeStraight(board, subBoardRanks1);
            twoCardsThatMakeStraightList2 = getTwoCardsThatMakeStraight(board, subBoardRanks2);
            twoCardsThatMakeStraightList3 = getTwoCardsThatMakeStraight(board, subBoardRanks3);

            oneCardThatMakesStraightList1 = getOneCardThatMakeStraight(board, subBoardRanks4);
            oneCardThatMakesStraightList2 = getOneCardThatMakeStraight(board, subBoardRanks5);

            allCombosThatMakeStraight.addAll(twoCardsThatMakeStraightList1);
            allCombosThatMakeStraight.addAll(twoCardsThatMakeStraightList2);
            allCombosThatMakeStraight.addAll(twoCardsThatMakeStraightList3);
            allCombosThatMakeStraight.addAll(oneCardThatMakesStraightList1);
            allCombosThatMakeStraight.addAll(oneCardThatMakesStraightList2);

            Set<String> allCombosThatMakeStraightClearedDoubleEntries = new HashSet<String>();
            allCombosThatMakeStraightClearedDoubleEntries.addAll(allCombosThatMakeStraight);
            allCombosThatMakeStraight.clear();
            allCombosThatMakeStraight.addAll(allCombosThatMakeStraightClearedDoubleEntries);

            return allCombosThatMakeStraight;
        }
        return null;
    }

    private static List<String> getTwoCardsThatMakeStraight(List<Card> board, List<Integer> subBoardRanks) {
        List<Integer> combo = new ArrayList<Integer>();
        List<String> straightCombos = new ArrayList<String>();
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
                if(connectingCardCounter == 5) {
                    if(board.size() == 5 && isBoardConnected(board)) {
                        if(combo.get(0) == getValueOfHighestCardOnBoard(board) + 1 || combo.get(1) == getValueOfHighestCardOnBoard(board) + 1) {
                            if(comboContainsLowAce(combo)) {
                                List<Integer> convertedCombo = convertComboWithLowAceToHighAce(combo);
                                straightCombos.add(convertedCombo.toString());
                            }
                            else {
                                Collections.sort(combo);
                                straightCombos.add(combo.toString());
                            }
                        }
                    }
                    else {
                        if(comboContainsLowAce(combo)) {
                            List<Integer> convertedCombo = convertComboWithLowAceToHighAce(combo);
                            straightCombos.add(convertedCombo.toString());
                        }
                        else {
                            Collections.sort(combo);
                            straightCombos.add(combo.toString());
                        }
                    }
                }
                subBoardRanks.clear();
                subBoardRanks.addAll(subBoardRanksCopy);

                if(boardContainsAce(board)) {
                    subBoardRanks = addLowAceToSubBoardRanksAndRemoveHighAceIfPresent(subBoardRanks, 3);
                    subBoardRanks.addAll(combo);
                    Collections.sort(subBoardRanks);
                    int connectingCardCounterAceBoard = 1;
                    for(int z = 0; z < (subBoardRanks.size()-1); z++) {
                        if(subBoardRanks.get(z) + 1 == subBoardRanks.get(z+1)) {
                            connectingCardCounterAceBoard++;
                        }
                    }
                    if(connectingCardCounterAceBoard == 5) {
                        Collections.sort(combo);
                        straightCombos.add(combo.toString());
                    }
                    subBoardRanks.clear();
                    subBoardRanks.addAll(subBoardRanksCopy);
                }
                combo.remove(combo.size()-1);
            }
        }
        return straightCombos;
    }

    private static List<String> getOneCardThatMakeStraight(List<Card> board, List<Integer> subBoardRanks) {
        final List<Integer> combo = new ArrayList<Integer>();
        final List<String> straightCombos = new ArrayList<String>();
        List<Integer> subBoardRanksCopy = new ArrayList<Integer>();
        subBoardRanksCopy.addAll(subBoardRanks);

        class HelperClassForInnerMethod {
            private void addCombos() {
                for(int z = 2; z < 15; z++) {
                    List<Integer> createdCombo = addSecondCardToCreateComboWhenSingleCardMakesStraight(combo, z);
                    if(comboContainsLowAce(createdCombo)) {
                        List<Integer> convertedCombo = convertComboWithLowAceToHighAce(createdCombo);
                        straightCombos.add(convertedCombo.toString());
                    }
                    else {
                        straightCombos.add(createdCombo.toString());
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
            if(connectingCardCounter == 5) {
                if(!isBoardConnected(board) || board.size() == 4) {
                    HelperClassForInnerMethod h = new HelperClassForInnerMethod();
                    h.addCombos();
                }
                else {
                    if(board.size() == 5) {
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
                subBoardRanks = addLowAceToSubBoardRanksAndRemoveHighAceIfPresent(subBoardRanks, 4);
                subBoardRanks.addAll(combo);
                Collections.sort(subBoardRanks);
                int connectingCardCounterAceBoard = 1;
                for(int z = 0; z < (subBoardRanks.size()-1); z++) {
                    if(subBoardRanks.get(z) + 1 == subBoardRanks.get(z+1)) {
                        connectingCardCounterAceBoard++;
                    }
                }
                if(connectingCardCounterAceBoard == 5) {
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