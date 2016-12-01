package com.lennart.model.pokergame;

import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;
import com.lennart.model.rangebuilder.preflop.ip.Call3betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.ip._2betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.ip._4betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.oop._3betRangeBuilder;

import java.util.*;

/**
 * Created by lennart on 19-11-16.
 */
public class PreflopActionBuilder {

    private PreflopRangeBuilderUtil preflopRangeBuilderUtil = new PreflopRangeBuilderUtil();

    public String get05betF1bet(List<Card> holeCards) {
        Map<Integer, Set<Card>> comboMap100Percent;
        Map<Integer, Set<Card>> comboMap5Percent;

        Game.removeHoleCardsFromKnownGameCards();
        _2betRangeBuilder x2BetRangeBuilder = new _2betRangeBuilder();

        comboMap100Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x2BetRangeBuilder.getComboMap100Percent());

        comboMap5Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x2BetRangeBuilder.getComboMap5Percent());

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
            return "2bet";
            //return "2bet" + 2.44 * Game.getBigBlind();
        } else {
            return "fold";
        }
    }

    public String get2betF3bet(List<Card> holeCards) {
        //je zit IP.. hebt ge2bet, facet nu 3bet. Wat te doen?

        //als je combo in een van de lijsten van call3bet zit dan call je.. bij juiste percentage
        //als je combo in een van de lijsten van 4bet zit dan 4bet je.. bij juiste percentage
        //anders fold je

        Game.removeHoleCardsFromKnownGameCards();

        Call3betRangeBuilder call3betRangeBuilder = new Call3betRangeBuilder();
        _4betRangeBuilder x4BetRangeBuilder = new _4betRangeBuilder();

        Map<Integer, Set<Card>> call3bet_comboMap100Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap100Percent());
        Map<Integer, Set<Card>> call3bet_comboMap94Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap94Percent());
        Map<Integer, Set<Card>> call3bet_comboMap89Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap89Percent());
        Map<Integer, Set<Card>> call3bet_comboMap80Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap80Percent());
        Map<Integer, Set<Card>> call3bet_comboMap73Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap73Percent());
        Map<Integer, Set<Card>> call3bet_comboMap50Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap50Percent());
        Map<Integer, Set<Card>> call3bet_comboMap34Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap34Percent());
        Map<Integer, Set<Card>> call3bet_comboMap29Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap29Percent());
        Map<Integer, Set<Card>> call3bet_comboMap27Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap27Percent());
        Map<Integer, Set<Card>> call3bet_comboMap19Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap19Percent());
        Map<Integer, Set<Card>> call3bet_comboMap8Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap8Percent());
        Map<Integer, Set<Card>> call3bet_comboMap7Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap7Percent());
        Map<Integer, Set<Card>> call3bet_comboMap5Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3betRangeBuilder.getComboMap5Percent());


        Map<Integer, Set<Card>> x4bet_comboMap95Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x4BetRangeBuilder.getComboMap95Percent());
        Map<Integer, Set<Card>> x4bet_comboMap50Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x4BetRangeBuilder.getComboMap50Percent());
        Map<Integer, Set<Card>> x4bet_comboMap20Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x4BetRangeBuilder.getComboMap20Percent());
        Map<Integer, Set<Card>> x4bet_comboMap11Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x4BetRangeBuilder.getComboMap11Percent());
        Map<Integer, Set<Card>> x4bet_comboMap6Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x4BetRangeBuilder.getComboMap6Percent());

        double percentageCall3bet = 0;
        double percentage4bet = 0;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(holeCards);

        percentageCall3bet = setPercentage(call3bet_comboMap100Percent, holeCardsAsSet, 1);

        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap94Percent, holeCardsAsSet, 0.94);
        }
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap89Percent, holeCardsAsSet, 0.89);
        }
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap80Percent, holeCardsAsSet, 0.80);
        }
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap73Percent, holeCardsAsSet, 0.73);
        }
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap50Percent, holeCardsAsSet, 0.50);
        }
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap34Percent, holeCardsAsSet, 0.34);
        }
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap29Percent, holeCardsAsSet, 0.29);
        }
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap27Percent, holeCardsAsSet, 0.27);
        }
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap19Percent, holeCardsAsSet, 0.19);
        }
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap8Percent, holeCardsAsSet, 0.08);
        }
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap7Percent, holeCardsAsSet, 0.07);
        }
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap5Percent, holeCardsAsSet, 0.05);
        }

        if(percentage4bet == 0) {
            percentage4bet = setPercentage(x4bet_comboMap95Percent, holeCardsAsSet, 0.95);
        }
        if(percentage4bet == 0) {
            percentage4bet = setPercentage(x4bet_comboMap50Percent, holeCardsAsSet, 0.50);
        }
        if(percentage4bet == 0) {
            percentage4bet = setPercentage(x4bet_comboMap20Percent, holeCardsAsSet, 0.20);
        }
        if(percentage4bet == 0) {
            percentage4bet = setPercentage(x4bet_comboMap11Percent, holeCardsAsSet, 0.11);
        }
        if(percentage4bet == 0) {
            percentage4bet = setPercentage(x4bet_comboMap6Percent, holeCardsAsSet, 0.06);
        }

        Game.addHoleCardsToKnownGameCards();

        double random = Math.random();
        if(random <= 1 - percentage4bet - percentageCall3bet) {
            return "fold";
        } else if ((random <= 1 - percentage4bet) && (random >= 1 - percentage4bet - percentageCall3bet)){
            return "call3bet";
        } else {
            return "4bet";
        }
    }

    public String get1betF2bet(List<Card> holeCards) {
        return "call2bet";
    }

    public String getSize(Action action) {
        switch(action.suggestedAction) {
            case "call":
                return String.valueOf(Game.getOpponentTotalBetSize() - Game.getMyTotalBetSize());
            case "2bet":
                return String.valueOf(2.5 * Game.getBigBlind());
            case "4bet":
                return String.valueOf(2.15 * Game.getOpponentTotalBetSize());
        }
        return null;
    }

    private double setPercentage(Map<Integer, Set<Card>> comboMap, Set<Card> combo, double percentage) {
        for (Map.Entry<Integer, Set<Card>> entry : comboMap.entrySet()) {
            if(entry.getValue().equals(combo)) {
                return percentage;
            }
        }
        return 0;
    }

}
