package com.lennart.model;

//import org.apache.tomcat.util.codec.binary.StringUtils;

import org.apache.commons.lang3.StringUtils;

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


    public static Map<String, Boolean> allFunctions(List<Card> board) {
        Map<String, Boolean> laterz = new HashMap<String, Boolean>();
        laterz.put("isBoardSuited", isBoardSuited(board));
        laterz.put("hasBoardTwoOfOneSuit", hasBoardTwoOfOneSuit(board));
        return(laterz);
    }



}
