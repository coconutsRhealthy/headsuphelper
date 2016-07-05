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
        List<Card> boardje = new ArrayList<Card>();
        Card card1 = new Card();
        Card card2 = new Card();
        Card card3 = new Card();
        Card card4 = new Card();
        Card card5 = new Card();
        card1.setRank(14);
        card1.setSuit('d');
        card2.setRank(2);
        card2.setSuit('c');
        card3.setRank(4);
        card3.setSuit('h');
        card4.setRank(5);
        card4.setSuit('d');
        card5.setRank(10);
        card5.setSuit('s');
        boardje.add(card1);
        boardje.add(card2);
        boardje.add(card3);
        boardje.add(card4);
        boardje.add(card5);

        List<Integer> boardRanks = getSortedCardRanksFromCardList(boardje);

        List<Integer> subBoardRanks1 = new ArrayList<Integer>();
        List<Integer> subBoardRanks2 = new ArrayList<Integer>();
        List<Integer> subBoardRanks3 = new ArrayList<Integer>();
        List<Integer> subBoardRanks4 = new ArrayList<Integer>();
        List<Integer> subBoardRanks5 = new ArrayList<Integer>();

        subBoardRanks1.add(boardRanks.get(0));
        subBoardRanks1.add(boardRanks.get(1));
        subBoardRanks1.add(boardRanks.get(2));

        subBoardRanks2.add(boardRanks.get(1));
        subBoardRanks2.add(boardRanks.get(2));
        subBoardRanks2.add(boardRanks.get(3));

        subBoardRanks3.add(boardRanks.get(2));
        subBoardRanks3.add(boardRanks.get(3));
        subBoardRanks3.add(boardRanks.get(4));

        subBoardRanks4.add(boardRanks.get(0));
        subBoardRanks4.add(boardRanks.get(1));
        subBoardRanks4.add(boardRanks.get(2));
        subBoardRanks4.add(boardRanks.get(3));

        subBoardRanks5.add(boardRanks.get(1));
        subBoardRanks5.add(boardRanks.get(2));
        subBoardRanks5.add(boardRanks.get(3));
        subBoardRanks5.add(boardRanks.get(4));

        List<String> twoCardsThatMakeStraightList1 = getTwoCardsThatMakeStraight(boardje, subBoardRanks1);
        List<String> twoCardsThatMakeStraightList2 = getTwoCardsThatMakeStraight(boardje, subBoardRanks2);
        List<String> twoCardsThatMakeStraightList3 = getTwoCardsThatMakeStraight(boardje, subBoardRanks3);

        List<String> oneCardThatMakesStraightList1 = getOneCardThatMakeStraight(boardje, subBoardRanks4);
        List<String> oneCardThatMakesStraightList2 = getOneCardThatMakeStraight(boardje, subBoardRanks5);

        List<String> allCombosThatMakeStraight = new ArrayList<String>();
        allCombosThatMakeStraight.addAll(twoCardsThatMakeStraightList1);
        allCombosThatMakeStraight.addAll(twoCardsThatMakeStraightList2);
        allCombosThatMakeStraight.addAll(twoCardsThatMakeStraightList3);
        allCombosThatMakeStraight.addAll(oneCardThatMakesStraightList1);
        allCombosThatMakeStraight.addAll(oneCardThatMakesStraightList2);

        Set<String> hs = new HashSet<String>();
        hs.addAll(allCombosThatMakeStraight);
        allCombosThatMakeStraight.clear();
        allCombosThatMakeStraight.addAll(hs);

        return allCombosThatMakeStraight;
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
                    if(!isBoardConnected(board)) {
                        if(comboContainsLowAce(combo)) {
                            List<Integer> convertedCombo = convertComboWithLowAceToHighAce(combo);
                            straightCombos.add(convertedCombo.toString());
                        }
                        else {
                            Collections.sort(combo);
                            straightCombos.add(combo.toString());
                        }
                    }
                    else {
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
                        if(!isBoardConnected(board)) {
                            if(comboContainsLowAce(combo)) {
                                List<Integer> convertedCombo = convertComboWithLowAceToHighAce(combo);
                                straightCombos.add(convertedCombo.toString());
                            }
                            else {
                                Collections.sort(combo);
                                straightCombos.add(combo.toString());
                            }
                        }
                        else {
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
                    }
                    subBoardRanks.clear();
                    subBoardRanks.addAll(subBoardRanksCopy);
                }
                combo.remove(combo.size()-1);
            }
        }
        return straightCombos;
    }

    private static List<String> getOneCardThatMakeStraight(List<Card> boardje, List<Integer> subBoardRanks) {
        List<Integer> combo = new ArrayList<Integer>();
        List<String> straightCombos = new ArrayList<String>();
        List<Integer> subBoardRanksCopy = new ArrayList<Integer>();
        subBoardRanksCopy.addAll(subBoardRanks);
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
                if(!isBoardConnected(boardje)) {
                    for(int z = 2; z < 15; z++) {
                        List<Integer> createdCombo = addSecondCardToCreateComboWhenSingleCardMakesStraight(combo, z);
                        straightCombos.add(createdCombo.toString());
                    }
                }
                else {
                    if(combo.get(0) == getValueOfHighestCardOnBoard(boardje) + 1) {
                        for(int z = 2; z < 15; z++) {
                            List<Integer> createdCombo = addSecondCardToCreateComboWhenSingleCardMakesStraight(combo, z);
                            straightCombos.add(createdCombo.toString());
                        }
                    }
                }
            }
            subBoardRanks.clear();
            subBoardRanks.addAll(subBoardRanksCopy);

            if(boardContainsAce(boardje)) {
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
                    if (!isBoardConnected(boardje)) {
                        for(int z = 2; z < 15; z++) {
                            List<Integer> createdCombo = addSecondCardToCreateComboWhenSingleCardMakesStraight(combo, z);
                            straightCombos.add(createdCombo.toString());
                        }
                    }
                }
                if(isBoardConnected(boardje) && !subBoardRanksCopy.contains(13)) {
                    if(combo.get(0) == 6) {
                        for(int z = 2; z < 15; z++) {
                            List<Integer> createdCombo = addSecondCardToCreateComboWhenSingleCardMakesStraight(combo, z);
                            straightCombos.add(createdCombo.toString());
                        }
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