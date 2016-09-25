package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.Comparator;
import java.util.List;

/**
 * Created by lpo10346 on 9/13/2016.
 */
public class HighCardEvaluator implements ComboComparatorRankOnly {

    @Override
    public Comparator<List<Integer>> getComboComparatorRankOnly(List<Card> board) {
        return new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> combo1, List<Integer> combo2) {
                //To implement

                return 0;
            }
        };
    }
}
