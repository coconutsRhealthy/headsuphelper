package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LPO10346 on 8/10/2016.
 */
public class FlushEvaluator extends BoardEvaluator {

    public Map<Integer, List<Integer>> getFlushCombos (List<Card> board) {
        return null;
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
}
