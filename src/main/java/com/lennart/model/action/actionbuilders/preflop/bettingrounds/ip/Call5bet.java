package com.lennart.model.action.actionbuilders.preflop.bettingrounds.ip;

import com.lennart.model.action.Actionable;
import com.lennart.model.card.Card;
import com.lennart.model.action.actionbuilders.ActionBuilderUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by LPO21630 on 7-12-2016.
 */
public class Call5bet {

    private Map<Integer, Map<Integer, Set<Card>>> comboMap100Percent = new HashMap<>();

    public Call5bet(ActionBuilderUtil p, double amountToCallBb) {
        if(amountToCallBb <= 40) {
            comboMap100Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
            comboMap100Percent.put(2, p.getPocketPairCombosOfGivenRank(13));
            comboMap100Percent.put(3, p.getPocketPairCombosOfGivenRank(12));
            comboMap100Percent.put(4, p.getPocketPairCombosOfGivenRank(11));
            comboMap100Percent.put(5, p.getSuitedCombosOfGivenRanks(14, 13));
            comboMap100Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 13));

            comboMap100Percent.put(11, p.getSuitedCombosOfGivenRanks(14, 12));
            comboMap100Percent.put(12, p.getSuitedCombosOfGivenRanks(14, 11));
            comboMap100Percent.put(13, p.getSuitedCombosOfGivenRanks(14, 10));

            comboMap100Percent.put(17, p.getOffSuitCombosOfGivenRanks(14, 12));
            comboMap100Percent.put(18, p.getOffSuitCombosOfGivenRanks(14, 11));

            comboMap100Percent.put(7, p.getPocketPairCombosOfGivenRank(10));

            comboMap100Percent.put(8, p.getPocketPairCombosOfGivenRank(9));
            comboMap100Percent.put(9, p.getPocketPairCombosOfGivenRank(8));
            comboMap100Percent.put(10, p.getPocketPairCombosOfGivenRank(7));

            comboMap100Percent.put(14, p.getSuitedCombosOfGivenRanks(14, 9));
            comboMap100Percent.put(15, p.getSuitedCombosOfGivenRanks(14, 8));
            comboMap100Percent.put(16, p.getSuitedCombosOfGivenRanks(13, 12));

            comboMap100Percent.put(19, p.getOffSuitCombosOfGivenRanks(14, 10));
        } else if(amountToCallBb <= 160) {
            comboMap100Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
            comboMap100Percent.put(2, p.getPocketPairCombosOfGivenRank(13));
            comboMap100Percent.put(3, p.getPocketPairCombosOfGivenRank(12));
            comboMap100Percent.put(4, p.getPocketPairCombosOfGivenRank(11));
            comboMap100Percent.put(5, p.getSuitedCombosOfGivenRanks(14, 13));
            comboMap100Percent.put(6, p.getOffSuitCombosOfGivenRanks(14, 13));

            comboMap100Percent.put(7, p.getSuitedCombosOfGivenRanks(14, 12));
        } else if(amountToCallBb <= 250) {
            comboMap100Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
            comboMap100Percent.put(2, p.getPocketPairCombosOfGivenRank(13));
        } else {
            comboMap100Percent.put(1, p.getPocketPairCombosOfGivenRank(14));
        }
    }

    public Map<Integer, Map<Integer, Set<Card>>> getComboMap100Percent() {
        return comboMap100Percent;
    }
}
