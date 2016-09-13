package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lpo10346 on 9/13/2016.
 */
public class HighCardEvaluator implements ComboComparator {

    @Override
    public Comparator<List<Integer>> getComboComparatorRankOnly(List<Card> board) {
        return new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> combo1, List<Integer> combo2) {
                Collections.sort(combo1, Collections.reverseOrder());
                Collections.sort(combo2, Collections.reverseOrder());

                if(combo2.get(0) > combo1.get(0)) {
                    return 1;
                } else if(combo2.get(0) == combo1.get(0)) {
                    if(combo2.get(1) > combo1.get(1)) {
                        return 1;
                    } else if(combo2.get(1) == combo1.get(1)) {
                        return 0;
                    }
                }
                return -1;
            }
        };
    }
}
