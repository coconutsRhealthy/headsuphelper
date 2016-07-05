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
        List<Integer> combo = new ArrayList<Integer>();
        List<String> straightCombos1 = new ArrayList<String>();
        List<String> straightCombos2 = new ArrayList<String>();
        List<String> straightCombos3 = new ArrayList<String>();
        List<String> straightCombos4 = new ArrayList<String>();
        List<String> straightCombos5 = new ArrayList<String>();

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

        List<String> eije1 = getTwoCardsThatMakeStraight(boardje, subBoardRanks1, combo, straightCombos1);
        List<String> eije2 = getTwoCardsThatMakeStraight(boardje, subBoardRanks2, combo, straightCombos2);
        List<String> eije3 = getTwoCardsThatMakeStraight(boardje, subBoardRanks3, combo, straightCombos3);

        List<String> eije4 = getOneCardThatMakeStraight(boardje, subBoardRanks4, combo, straightCombos4);
        List<String> eije5 = getOneCardThatMakeStraight(boardje, subBoardRanks5, combo, straightCombos5);

        List<String> eije888 = new ArrayList<String>();
        eije888.addAll(eije1);
        eije888.addAll(eije2);
        eije888.addAll(eije3);
        eije888.addAll(eije4);
        eije888.addAll(eije5);

        Set<String> hs = new HashSet<String>();
        hs.addAll(eije888);
        eije888.clear();
        eije888.addAll(hs);

        return eije888;
    }

    private static List<String> getTwoCardsThatMakeStraight(List<Card> boardje, List<Integer> boardRanks, List<Integer> combo, List<String> straightCombos) {
        for(int i = 1; i < 15; i++) {
            combo.clear();
            combo.add(i);
            for(int j = 14; j > i; j--) {
                combo.add(j);
                boardRanks.addAll(combo);
                Collections.sort(boardRanks);
                int x = 0;
                for(int z = 0; z < (boardRanks.size()-1); z++) {
                    if(boardRanks.get(z) + 1 == boardRanks.get(z+1)) {
                        x++;
                    }
                }
                if(x == 4) {
                    if(!isBoardConnected(boardje)) {
                        if(comboContainsLowAce(combo)) {
                            Integer aceLow = 1;
                            Integer aceHigh = 14;
                            List<Integer> xyz = new ArrayList<Integer>();
                            xyz.addAll(combo);
                            xyz.remove(aceLow);
                            xyz.add(aceHigh);
                            Collections.sort(xyz);
                            straightCombos.add(xyz.toString());
                        }
                        else {
                            Collections.sort(combo);
                            straightCombos.add(combo.toString());
                        }
                    }
                    else {
                        if(combo.get(0) == getValueOfHighestCardOnBoard(boardje) + 1 || combo.get(1) == getValueOfHighestCardOnBoard(boardje) + 1) {
                            if(comboContainsLowAce(combo)) {
                                Integer aceLow = 1;
                                Integer aceHigh = 14;
                                List<Integer> xyz = new ArrayList<Integer>();
                                xyz.addAll(combo);
                                xyz.remove(aceLow);
                                xyz.add(aceHigh);
                                Collections.sort(xyz);
                                straightCombos.add(xyz.toString());
                            }
                            else {
                                Collections.sort(combo);
                                straightCombos.add(combo.toString());
                            }
                        }
                    }
                }
                boardRanks.remove(combo.get(0));
                boardRanks.remove(combo.get(1));
                List<Integer> boardRanksCopy = new ArrayList<Integer>();
                boardRanksCopy.addAll(boardRanks);

                if(boardContainsAce(boardje)) {
                    Integer aceLow = 1;
                    Integer aceHigh = 14;
                    boardRanks.add(aceLow);
                    boardRanks.remove(aceHigh);
                    Collections.sort(boardRanks);
                    if(boardRanks.size() == 4) {
                        boardRanks.remove(3);
                    }
                    boardRanks.addAll(combo);
                    Collections.sort(boardRanks);
                    int k = 0;
                    for(int v = 0; v < (boardRanks.size()-1); v++) {
                        if(boardRanks.get(v) + 1 == boardRanks.get(v+1)) {
                            k++;
                        }
                    }
                    if(k == 4) {
                        if(!isBoardConnected(boardje)) {
                            if(comboContainsLowAce(combo)) {
                                List<Integer> xyz = new ArrayList<Integer>();
                                xyz.addAll(combo);
                                xyz.remove(aceLow);
                                xyz.add(aceHigh);
                                Collections.sort(xyz);
                                straightCombos.add(xyz.toString());
                            }
                            else {
                                Collections.sort(combo);
                                straightCombos.add(combo.toString());
                            }
                        }
                        else {
                            if(combo.get(0) == getValueOfHighestCardOnBoard(boardje) + 1 || combo.get(1) == getValueOfHighestCardOnBoard(boardje) + 1) {
                                if(comboContainsLowAce(combo)) {
                                    List<Integer> xyz = new ArrayList<Integer>();
                                    xyz.addAll(combo);
                                    xyz.remove(aceLow);
                                    xyz.add(aceHigh);
                                    Collections.sort(xyz);
                                    straightCombos.add(xyz.toString());
                                }
                                else {
                                    Collections.sort(combo);
                                    straightCombos.add(combo.toString());
                                }
                            }
                        }
                    }
                    boardRanks.clear();
                    boardRanks.addAll(boardRanksCopy);
                }
                combo.remove(combo.size()-1);
            }
        }
        return straightCombos;
    }

    private static List<String> getOneCardThatMakeStraight(List<Card> boardje, List<Integer> boardRanks, List<Integer> combo, List<String> straightCombos) {
        for(int i = 1; i < 15; i++) {
            combo.clear();
            combo.add(i);

            boardRanks.addAll(combo);
            Collections.sort(boardRanks);
            int x = 0;
            for(int z = 0; z < (boardRanks.size()-1); z++) {
                if(boardRanks.get(z) + 1 == boardRanks.get(z+1)) {
                    x++;
                }
            }
            if(x == 4) {
                if(!isBoardConnected(boardje)) {
                    for(int b = 2; b < 15; b++) {
                        List<Integer> xxx = new ArrayList<Integer>();
                        xxx.add(b);
                        xxx.addAll(combo);
                        Collections.sort(xxx);
                        straightCombos.add(xxx.toString());
                    }
                }
                else {
                    if(combo.get(0) == getValueOfHighestCardOnBoard(boardje) + 1) {
                        for(int b = 2; b < 15; b++) {
                            List<Integer> xxx = new ArrayList<Integer>();
                            xxx.add(b);
                            xxx.addAll(combo);
                            Collections.sort(xxx);
                            straightCombos.add(xxx.toString());
                        }
                    }
                }
            }
            boardRanks.remove(combo.get(0));
            List<Integer> boardRanksCopy = new ArrayList<Integer>();
            boardRanksCopy.addAll(boardRanks);

            if(boardContainsAce(boardje)) {
                Integer aceLow = 1;
                Integer aceHigh = 14;
                boardRanks.add(aceLow);
                boardRanks.remove(aceHigh);
                Collections.sort(boardRanks);
                if(boardRanks.size() == 5) {
                    boardRanks.remove(4);
                }
                boardRanks.addAll(combo);
                Collections.sort(boardRanks);
                int k = 0;
                for(int v = 0; v < (boardRanks.size()-1); v++) {
                    if(boardRanks.get(v) + 1 == boardRanks.get(v+1)) {
                        k++;
                    }
                }
                if(k == 4) {
                    if (!isBoardConnected(boardje)) {
                        for (int b = 2; b < 15; b++) {
                            List<Integer> xxx = new ArrayList<Integer>();
                            xxx.add(b);
                            xxx.addAll(combo);
                            Collections.sort(xxx);
                            straightCombos.add(xxx.toString());
                        }
                    }
                }
                if(isBoardConnected(boardje) && !boardRanksCopy.contains(13)) {
                    if(combo.get(0) == 6) {
                        for(int b = 2; b < 15; b++) {
                            List<Integer> xxx = new ArrayList<Integer>();
                            xxx.add(b);
                            xxx.addAll(combo);
                            Collections.sort(xxx);
                            straightCombos.add(xxx.toString());
                        }
                    }
                }
                boardRanks.clear();
                boardRanks.addAll(boardRanksCopy);
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