package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.Comparator;
import java.util.List;

/**
 * Created by lennart on 11-9-16.
 */
public interface ComboComparatorRankOnly {

    Comparator<List<Integer>> getComboComparatorRankOnly(List<Card> board);

}
