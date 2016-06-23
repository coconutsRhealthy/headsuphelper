package com.lennart.model;

//import org.apache.tomcat.util.codec.binary.StringUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public static List<BooleanResult> allFunctions(List<Card> board) {
        BooleanResult result1 = new BooleanResult();
        BooleanResult result2 = new BooleanResult();

        result1.setFunctionName("isBoardSuited");
        result1.setResult(isBoardSuited(board));
        result2.setFunctionName("hasBoardTwoOfOneSuit");
        result2.setResult(hasBoardTwoOfOneSuit(board));

//        Map<String, Boolean> laterz = new HashMap<String, Boolean>();

        List<BooleanResult> hallo = new ArrayList<BooleanResult>();

        hallo.add(result1);
        hallo.add(result2);

//        laterz.put(result1.getFunctionName(), result1.isResult());
//        laterz.put(result2.getFunctionName(), result2.isResult());



//        laterz.put("isBoardSuited", isBoardSuited(board));
//        laterz.put("hasBoardTwoOfOneSuit", hasBoardTwoOfOneSuit(board));

        System.out.println(hallo);


        return hallo;
    }



}
