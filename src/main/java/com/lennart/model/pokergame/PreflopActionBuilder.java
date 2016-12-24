package com.lennart.model.pokergame;

import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilder;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;
import com.lennart.model.rangebuilder.preflop.ip.Call3betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.ip._2betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.ip._4betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.oop.Call2betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.oop.Call4betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.oop._3betRangeBuilder;

import java.util.*;

/**
 * Created by lennart on 19-11-16.
 */
public class PreflopActionBuilder {

    private PreflopRangeBuilderUtil preflopRangeBuilderUtil;

    public PreflopActionBuilder(PreflopRangeBuilder preflopRangeBuilder) {
        preflopRangeBuilderUtil = preflopRangeBuilder.getPreflopRangeBuilderUtil();
    }

    public String get05betF1bet(ComputerGame computerGame) {
        Map<Integer, Set<Card>> comboMap100Percent;
        Map<Integer, Set<Card>> comboMap5Percent;

        //Game.removeHoleCardsFromKnownGameCards();
        computerGame.removeHoleCardsFromKnownGameCards();

        _2betRangeBuilder x2BetRangeBuilder = new _2betRangeBuilder(preflopRangeBuilderUtil);

        comboMap100Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x2BetRangeBuilder.getComboMap100Percent());

        comboMap5Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x2BetRangeBuilder.getComboMap5Percent());

        double percentageBet = 0;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(computerGame.getComputerHoleCards());

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

        //Game.addHoleCardsToKnownGameCards();
        computerGame.addHoleCardsToKnownGameCards();

        if(Math.random() <= percentageBet) {
            return "2bet";
        } else {
            return "fold";
        }
    }

    public String get2betF3bet(ComputerGame computerGame) {
        //Game.removeHoleCardsFromKnownGameCards();
        computerGame.removeHoleCardsFromKnownGameCards();

        Call3betRangeBuilder call3betRangeBuilder = new Call3betRangeBuilder(preflopRangeBuilderUtil);
        _4betRangeBuilder x4BetRangeBuilder = new _4betRangeBuilder(preflopRangeBuilderUtil);

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

        double percentageCall3bet;
        double percentage4bet;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(computerGame.getComputerHoleCards());

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

        percentage4bet = setPercentage(x4bet_comboMap95Percent, holeCardsAsSet, 0.95);

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

        //Game.addHoleCardsToKnownGameCards();
        computerGame.addHoleCardsToKnownGameCards();

        double random = Math.random();
        if(random <= 1 - percentage4bet - percentageCall3bet) {
            return "fold";
        } else if ((random <= 1 - percentage4bet) && (random >= 1 - percentage4bet - percentageCall3bet)){
            return "call3bet";
        } else {
            return "4bet";
        }
    }

    public String get1betF2bet(ComputerGame computerGame) {
        //Game.removeHoleCardsFromKnownGameCards();
        computerGame.removeHoleCardsFromKnownGameCards();

        Call2betRangeBuilder call2betRangeBuilder = new Call2betRangeBuilder(preflopRangeBuilderUtil);
        _3betRangeBuilder x3BetRangeBuilder = new _3betRangeBuilder(preflopRangeBuilderUtil);

        Map<Integer, Set<Card>> call2bet_comboMap90Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2betRangeBuilder.getComboMap90Percent());
        Map<Integer, Set<Card>> call2bet_comboMap80Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2betRangeBuilder.getComboMap80Percent());
        Map<Integer, Set<Card>> call2bet_comboMap65Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2betRangeBuilder.getComboMap65Percent());
        Map<Integer, Set<Card>> call2bet_comboMap50Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2betRangeBuilder.getComboMap50Percent());
        Map<Integer, Set<Card>> call2bet_comboMap33Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2betRangeBuilder.getComboMap33Percent());
        Map<Integer, Set<Card>> call2bet_comboMap30Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2betRangeBuilder.getComboMap30Percent());
        Map<Integer, Set<Card>> call2bet_comboMap10Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2betRangeBuilder.getComboMap10Percent());
        Map<Integer, Set<Card>> call2bet_comboMap5Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2betRangeBuilder.getComboMap5Percent());

        Map<Integer, Set<Card>> x3bet_comboMap95Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3BetRangeBuilder.getComboMap95Percent());
        Map<Integer, Set<Card>> x3bet_comboMap70Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3BetRangeBuilder.getComboMap70Percent());
        Map<Integer, Set<Card>> x3bet_comboMap50Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3BetRangeBuilder.getComboMap50Percent());
        Map<Integer, Set<Card>> x3bet_comboMap35Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3BetRangeBuilder.getComboMap35Percent());
        Map<Integer, Set<Card>> x3bet_comboMap20Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3BetRangeBuilder.getComboMap20Percent());
        Map<Integer, Set<Card>> x3bet_comboMap10Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3BetRangeBuilder.getComboMap10Percent());
        Map<Integer, Set<Card>> x3bet_comboMap5Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3BetRangeBuilder.getComboMap5Percent());

        double percentageCall2bet;
        double percentage3bet;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(computerGame.getComputerHoleCards());

        percentageCall2bet = setPercentage(call2bet_comboMap90Percent, holeCardsAsSet, 0.90);

        if(percentageCall2bet == 0) {
            percentageCall2bet = setPercentage(call2bet_comboMap80Percent, holeCardsAsSet, 0.80);
        }
        if(percentageCall2bet == 0) {
            percentageCall2bet = setPercentage(call2bet_comboMap65Percent, holeCardsAsSet, 0.65);
        }
        if(percentageCall2bet == 0) {
            percentageCall2bet = setPercentage(call2bet_comboMap50Percent, holeCardsAsSet, 0.50);
        }
        if(percentageCall2bet == 0) {
            percentageCall2bet = setPercentage(call2bet_comboMap33Percent, holeCardsAsSet, 0.33);
        }
        if(percentageCall2bet == 0) {
            percentageCall2bet = setPercentage(call2bet_comboMap30Percent, holeCardsAsSet, 0.30);
        }
        if(percentageCall2bet == 0) {
            percentageCall2bet = setPercentage(call2bet_comboMap10Percent, holeCardsAsSet, 0.10);
        }
        if(percentageCall2bet == 0) {
            percentageCall2bet = setPercentage(call2bet_comboMap5Percent, holeCardsAsSet, 0.05);
        }

        percentage3bet = setPercentage(x3bet_comboMap95Percent, holeCardsAsSet, 0.95);

        if(percentage3bet == 0) {
            percentage3bet = setPercentage(x3bet_comboMap70Percent, holeCardsAsSet, 0.70);
        }
        if(percentage3bet == 0) {
            percentage3bet = setPercentage(x3bet_comboMap50Percent, holeCardsAsSet, 0.50);
        }
        if(percentage3bet == 0) {
            percentage3bet = setPercentage(x3bet_comboMap35Percent, holeCardsAsSet, 0.35);
        }
        if(percentage3bet == 0) {
            percentage3bet = setPercentage(x3bet_comboMap20Percent, holeCardsAsSet, 0.20);
        }
        if(percentage3bet == 0) {
            percentage3bet = setPercentage(x3bet_comboMap10Percent, holeCardsAsSet, 0.10);
        }
        if(percentage3bet == 0) {
            percentage3bet = setPercentage(x3bet_comboMap5Percent, holeCardsAsSet, 0.05);
        }

        //Game.addHoleCardsToKnownGameCards();
        computerGame.addHoleCardsToKnownGameCards();

        double random = Math.random();
        if(random <= 1 - percentage3bet - percentageCall2bet) {
            return "fold";
        } else if ((random <= 1 - percentage3bet) && (random >= 1 - percentage3bet - percentageCall2bet)){
            return "call2bet";
        } else {
            return "3bet";
        }
    }

    public String get3betF4bet(ComputerGame computerGame) {
        //Game.removeHoleCardsFromKnownGameCards();
        computerGame.removeHoleCardsFromKnownGameCards();

        Call4betRangeBuilder call4betRangeBuilder = new Call4betRangeBuilder(preflopRangeBuilderUtil);

        Map<Integer, Set<Card>> call4bet_comboMap100Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4betRangeBuilder.getComboMap100Percent());
        Map<Integer, Set<Card>> call4bet_comboMap80Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4betRangeBuilder.getComboMap80Percent());
        Map<Integer, Set<Card>> call4bet_comboMap70Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4betRangeBuilder.getComboMap70Percent());
        Map<Integer, Set<Card>> call4bet_comboMap62Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4betRangeBuilder.getComboMap62Percent());
        Map<Integer, Set<Card>> call4bet_comboMap60Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4betRangeBuilder.getComboMap60Percent());
        Map<Integer, Set<Card>> call4bet_comboMap50Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4betRangeBuilder.getComboMap50Percent());
        Map<Integer, Set<Card>> call4bet_comboMap5Percent = preflopRangeBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4betRangeBuilder.getComboMap5Percent());

        //nog 5bet hier//

        double percentageCall4bet;
        double percentage5bet = 0;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(computerGame.getComputerHoleCards());

        percentageCall4bet = setPercentage(call4bet_comboMap100Percent, holeCardsAsSet, 1.0);

        if(percentageCall4bet == 0) {
            percentageCall4bet = setPercentage(call4bet_comboMap80Percent, holeCardsAsSet, 0.80);
        }
        if(percentageCall4bet == 0) {
            percentageCall4bet = setPercentage(call4bet_comboMap70Percent, holeCardsAsSet, 0.70);
        }
        if(percentageCall4bet == 0) {
            percentageCall4bet = setPercentage(call4bet_comboMap62Percent, holeCardsAsSet, 0.62);
        }
        if(percentageCall4bet == 0) {
            percentageCall4bet = setPercentage(call4bet_comboMap60Percent, holeCardsAsSet, 0.60);
        }
        if(percentageCall4bet == 0) {
            percentageCall4bet = setPercentage(call4bet_comboMap50Percent, holeCardsAsSet, 0.50);
        }
        if(percentageCall4bet == 0) {
            percentageCall4bet = setPercentage(call4bet_comboMap5Percent, holeCardsAsSet, 0.05);
        }

        //nog 5bet hier//

        //Game.addHoleCardsToKnownGameCards();
        computerGame.addHoleCardsToKnownGameCards();

        double random = Math.random();
        if(random <= 1 - percentage5bet - percentageCall4bet) {
            return "fold";
        } else if ((random <= 1 - percentage5bet) && (random >= 1 - percentage5bet - percentageCall4bet)){
            return "call4bet";
        } else {
            return "5bet";
        }
    }

    public double getSize(ComputerGame computerGame) {
        switch(computerGame.getHandPath()) {
            case "2bet":
                return 2.5 * computerGame.getBigBlind();
        }
        return 0;
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
