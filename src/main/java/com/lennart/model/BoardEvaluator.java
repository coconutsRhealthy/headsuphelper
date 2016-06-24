package com.lennart.model;

//import org.apache.tomcat.util.codec.binary.StringUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by LPO10346 on 21-6-2016.
 */
public class BoardEvaluator {

    public static boolean isBoardSuited(List<Card> board) {
        StringBuilder s = new StringBuilder();
        for(Card c : board) {
            s.append(c.getSuit());
        }
        for(int i = 0; i <= (s.length()-2); i++) {
            if(s.charAt(i) == s.charAt(i+1)) {
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }

    public static boolean hasBoardTwoOfOneSuit(List<Card> board) {
        if(!isBoardSuited(board)) {
            StringBuilder s = new StringBuilder();
            for(Card c : board) {
                s.append(c.getSuit());
            }
            for(int i = 0; i <= (s.length()-1); i++) {
                if(StringUtils.countMatches(s, "" + s.charAt(i)) > 1) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static boolean isBoardConnected(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
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

    private static List<Integer> getSortedCardRanksFromCardList(List<Card> board) {
        List<Integer> boardRanks = new ArrayList<Integer>();
        for(Card c : board) {
            boardRanks.add(c.getRank());
        }
        Collections.sort(boardRanks);
        return boardRanks;
    }

    public static List<BooleanResult> allFunctions(List<Card> board) {
        BooleanResult result1 = new BooleanResult();
        BooleanResult result2 = new BooleanResult();
        BooleanResult result3 = new BooleanResult();
        BooleanResult result4 = new BooleanResult();

        result1.setFunctionDescription("Is board suited");
        result1.setResult(isBoardSuited(board));
        result2.setFunctionDescription("Has board two of one suit");
        result2.setResult(hasBoardTwoOfOneSuit(board));
        result3.setFunctionDescription("Is board connected");
        result3.setResult(isBoardConnected(board));
        result4.setFunctionDescription("Has board two connecting cards");
        result4.setResult(hasBoardTwoConnectingCards(board));

        List<BooleanResult> listOfFunctionResults = new ArrayList<BooleanResult>();

        listOfFunctionResults.add(result1);
        listOfFunctionResults.add(result2);
        listOfFunctionResults.add(result3);
        listOfFunctionResults.add(result4);

        return listOfFunctionResults;
    }
}
