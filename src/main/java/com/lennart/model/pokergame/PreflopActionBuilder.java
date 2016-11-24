package com.lennart.model.pokergame;

import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;
import com.lennart.model.rangebuilder.preflop.ip._2betRangeBuilder;

import java.util.*;

/**
 * Created by lennart on 19-11-16.
 */
public class PreflopActionBuilder {

    private PreflopRangeBuilderUtil preflopRangeBuilderUtil = new PreflopRangeBuilderUtil();
    private static Map<Integer, Set<Card>> comboMap100Percent;
    private static Map<Integer, Set<Card>> comboMap5Percent;

    public String get05betF1bet(List<Card> holeCards) {
        Game.removeHoleCardsFromKnownGameCards();
        _2betRangeBuilder twoBetRangeBuilder = new _2betRangeBuilder();

        comboMap100Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (twoBetRangeBuilder.getComboMap100Percent());

        comboMap5Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (twoBetRangeBuilder.getComboMap5Percent());

        double percentageBet = 0;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(holeCards);

        for (Map.Entry<Integer, Set<Card>> entry : comboMap100Percent.entrySet()) {
            if(entry.getValue().equals(holeCardsAsSet)) {
                percentageBet = 1;
                break;
            }
        }

        if(percentageBet == 0) {
            for (Map.Entry<Integer, Set<Card>> entry : comboMap5Percent.entrySet()) {
                if(entry.getValue().equals(holeCardsAsSet)) {
                    percentageBet = 0.05;
                    break;
                }
            }
        }

        Game.addHoleCardsToKnownGameCards();

        if(Math.random() <= percentageBet) {
            return "2bet" + 2.44 * Game.getBigBlind();
        } else {
            return "fold";
        }
    }
}
