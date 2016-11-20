package com.lennart.model.pokergame;

import com.lennart.model.pokergame.Card;
import com.lennart.model.pokergame.Action;
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

    public Action getFacing1bet(List<Card> holeCards) {
        if(comboMap100Percent == null) {
            setComboMap100Percent();
        }
        if(comboMap5Percent == null) {
            setComboMap5Percent();
        }

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

        if(Math.random() <= percentageBet) {
            return new Action("2bet");
        } else {
            return new Action("fold");
        }
    }

    public void setComboMap100Percent() {
        comboMap100Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (_2betRangeBuilder.getComboMap100Percent());
    }

    public void setComboMap5Percent() {
        comboMap5Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (_2betRangeBuilder.getComboMap5Percent());
    }
}
