package com.lennart.model.boardevaluation;

import com.lennart.model.card.Card;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by lennart on 25-9-16.
 */
public interface ComboComparator {

    Comparator<Set<Card>> getComboComparator(List<Card> board);

}
