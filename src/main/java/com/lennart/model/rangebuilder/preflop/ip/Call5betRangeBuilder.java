package com.lennart.model.rangebuilder.preflop.ip;

import com.lennart.model.card.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by LPO21630 on 7-12-2016.
 */
public class Call5betRangeBuilder {

    private Map<Integer, Map<Integer, Set<Card>>> comboMap100Percent = new HashMap<>();

    public Call5betRangeBuilder(PreflopRangeBuilderUtil p) {
        comboMap100Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
        comboMap100Percent.put(2, p.getPocketPairCombosOfGivenRank(13));
        comboMap100Percent.put(3, p.getPocketPairCombosOfGivenRank(12));
        comboMap100Percent.put(4, p.getSuitedCombosOfGivenRanks(14, 13));
        comboMap100Percent.put(5, p.getOffSuitCombosOfGivenRanks(14, 13));
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap100Percent() {
        return comboMap100Percent;
    }
}
