package com.lennart.model.action.actionbuilders.preflop;

import com.lennart.model.action.Actionable;
import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.ContinuousTableable;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop._5bet;
import com.lennart.model.card.Card;
import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.ip.Call3bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.ip.Call5bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.ip._2bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.ip._4bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop.Call2bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop.Call4bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop._3bet;
import com.lennart.model.handevaluation.PreflopHandStength;

import java.util.*;

/**
 * Created by lennart on 19-11-16.
 */
public class PreflopActionBuilder {

    private ActionBuilderUtil actionBuilderUtil;

    public PreflopActionBuilder() {
        actionBuilderUtil = new ActionBuilderUtil();
    }

    public String getAction(double opponentBetSize, double botBetSize, double opponentStack, double bigBlind, List<Card> botHoleCards, boolean botIsButton, ContinuousTableable continuousTableable, String opponentType, double amountToCallBb) {
        String action;
        double bbOpponentTotalBetSize = opponentBetSize / bigBlind;

        if(bbOpponentTotalBetSize == 1) {
            if(botIsButton) {
                action = get05betF1bet(botHoleCards);
            } else {
                action = get1betFcheck(botHoleCards);
            }
        } else if(bbOpponentTotalBetSize > 1 && bbOpponentTotalBetSize <= 4) {
            if(opponentStack == 0) {
                action = getActionFacingAllIn(botHoleCards, 0.5);
            } else {
                action = get1betF2bet(botHoleCards, continuousTableable);
            }
        } else if(bbOpponentTotalBetSize > 4 && bbOpponentTotalBetSize <= 16) {
            if(opponentStack == 0) {
                action = getActionFacingAllIn(botHoleCards, 0.75);
            } else {
                action = get2betF3bet(botHoleCards, continuousTableable);
            }
        } else if(bbOpponentTotalBetSize >= 16 && bbOpponentTotalBetSize <= 40) {
            if(opponentStack == 0) {
                action = getActionFacingAllIn(botHoleCards, 0.85);
            } else {
                action = get3betF4bet(botHoleCards, continuousTableable, opponentType);
            }
        } else {
            action = get4betF5bet(botHoleCards, amountToCallBb);
        }

        if(action.equals("fold") && ((opponentBetSize - botBetSize) / (opponentBetSize + botBetSize) < 0.24)) {
            action = "call";
        }

        return action;
    }

    private String getActionFacingAllIn(List<Card> botHoleCards, double handStrengthLowerLimit) {
        String actionToReturn;
        double handStrength = new PreflopHandStength().getPreflopHandStength(botHoleCards);

        if(handStrength >= handStrengthLowerLimit) {
            actionToReturn = "call";
        } else {
            actionToReturn = "fold";
        }
        return actionToReturn;
    }

    public double getSize(Actionable actionable) {
        double size;
        double bigBlind = actionable.getBigBlind();
        double potSizeInBb = actionable.getPotSize() / bigBlind;
        double computerTotalBetSizeInBb = actionable.getBotTotalBetSize() / bigBlind;
        double opponentTotalBetSizeInBb = actionable.getOpponentTotalBetSize() / bigBlind;

        double potSizePlusAllBetsInBb = potSizeInBb + computerTotalBetSizeInBb + opponentTotalBetSizeInBb;

        if(potSizePlusAllBetsInBb == 1.5) {
            size = 2.5 * actionable.getBigBlind();
        } else if(potSizePlusAllBetsInBb == 2) {
            size = 3.5 * actionable.getBigBlind();
        } else if(potSizePlusAllBetsInBb > 2 && potSizePlusAllBetsInBb <= 4) {
            size = 3.2 * actionable.getOpponentTotalBetSize();
        } else if(potSizePlusAllBetsInBb > 4 && potSizePlusAllBetsInBb <= 16) {
            size = 2.25 * actionable.getOpponentTotalBetSize();
        } else {
            size = actionable.getBotStack() - actionable.getBotTotalBetSize();
        }
        return size;
    }

    private String get05betF1bet(List<Card> botHoleCards) {
        Map<Integer, Set<Card>> comboMap100Percent;
        Map<Integer, Set<Card>> comboMap5Percent;

        _2bet x2Bet = new _2bet(actionBuilderUtil);

        comboMap100Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x2Bet.getComboMap100Percent());

        comboMap5Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x2Bet.getComboMap5Percent());

        double percentageBet = 0;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(botHoleCards);

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
            return "raise";
        } else {
            return "fold";
        }
    }

    private String get1betFcheck(List<Card> botHoleCards) {
        _3bet x3Bet = new _3bet(actionBuilderUtil);

        Map<Integer, Set<Card>> x3bet_comboMap95Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap95Percent());
        Map<Integer, Set<Card>> x3bet_comboMap70Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap70Percent());
        Map<Integer, Set<Card>> x3bet_comboMap50Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap50Percent());
        Map<Integer, Set<Card>> x3bet_comboMap35Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap35Percent());
        Map<Integer, Set<Card>> x3bet_comboMap20Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap20Percent());
        Map<Integer, Set<Card>> x3bet_comboMap10Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap10Percent());
        Map<Integer, Set<Card>> x3bet_comboMap5Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap5Percent());

        double percentage2bet;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(botHoleCards);

        percentage2bet = setPercentage(x3bet_comboMap95Percent, holeCardsAsSet, 0.95);

        if(percentage2bet == 0) {
            percentage2bet = setPercentage(x3bet_comboMap70Percent, holeCardsAsSet, 0.70);
        }
        if(percentage2bet == 0) {
            percentage2bet = setPercentage(x3bet_comboMap50Percent, holeCardsAsSet, 0.50);
        }
        if(percentage2bet == 0) {
            percentage2bet = setPercentage(x3bet_comboMap35Percent, holeCardsAsSet, 0.35);
        }
        if(percentage2bet == 0) {
            percentage2bet = setPercentage(x3bet_comboMap20Percent, holeCardsAsSet, 0.20);
        }
        if(percentage2bet == 0) {
            percentage2bet = setPercentage(x3bet_comboMap10Percent, holeCardsAsSet, 0.10);
        }
        if(percentage2bet == 0) {
            percentage2bet = setPercentage(x3bet_comboMap5Percent, holeCardsAsSet, 0.05);
        }

        double random = Math.random();
        if(random <= 1 - percentage2bet) {
            return "check";
        } else {
            return "raise";
        }
    }

    private String get2betF3bet(List<Card> botHoleCards, ContinuousTableable continuousTableable) {
        Call3bet call3Bet = new Call3bet(actionBuilderUtil);
        _4bet x4Bet = new _4bet(actionBuilderUtil);

        Map<Integer, Set<Card>> call3bet_comboMap100Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap100Percent());
        Map<Integer, Set<Card>> call3bet_comboMap94Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap94Percent());
        Map<Integer, Set<Card>> call3bet_comboMap89Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap89Percent());
        Map<Integer, Set<Card>> call3bet_comboMap80Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap80Percent());
        Map<Integer, Set<Card>> call3bet_comboMap73Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap73Percent());
        Map<Integer, Set<Card>> call3bet_comboMap50Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap50Percent());
        Map<Integer, Set<Card>> call3bet_comboMap34Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap34Percent());
        Map<Integer, Set<Card>> call3bet_comboMap29Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap29Percent());
        Map<Integer, Set<Card>> call3bet_comboMap27Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap27Percent());
        Map<Integer, Set<Card>> call3bet_comboMap19Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap19Percent());
        Map<Integer, Set<Card>> call3bet_comboMap8Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap8Percent());
        Map<Integer, Set<Card>> call3bet_comboMap7Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap7Percent());
        Map<Integer, Set<Card>> call3bet_comboMap5Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call3Bet.getComboMap5Percent());


        Map<Integer, Set<Card>> x4bet_comboMap95Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x4Bet.getComboMap95Percent());
        Map<Integer, Set<Card>> x4bet_comboMap50Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x4Bet.getComboMap50Percent());
        Map<Integer, Set<Card>> x4bet_comboMap20Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x4Bet.getComboMap20Percent());
        Map<Integer, Set<Card>> x4bet_comboMap11Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x4Bet.getComboMap11Percent());
        Map<Integer, Set<Card>> x4bet_comboMap6Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x4Bet.getComboMap6Percent());

        double percentageCall3bet;
        double percentage4bet;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(botHoleCards);

        percentageCall3bet = setPercentage(call3bet_comboMap100Percent, holeCardsAsSet, 1);

        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap94Percent, holeCardsAsSet, 0.94);
        }
        //changed this to 100
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap89Percent, holeCardsAsSet, 1.0);
        }
        //changed this to 100
        if(percentageCall3bet == 0) {
            percentageCall3bet = setPercentage(call3bet_comboMap80Percent, holeCardsAsSet, 1.0);
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

        double random = Math.random();
        if(random <= 1 - percentage4bet - percentageCall3bet) {
            return "fold";
        } else if ((random <= 1 - percentage4bet) && (random >= 1 - percentage4bet - percentageCall3bet)){
            continuousTableable.setPre3betOrPostRaisedPot(true);
            return "call";
        } else {
            continuousTableable.setPre3betOrPostRaisedPot(true);
            return "raise";
        }
    }

    private String get1betF2bet(List<Card> botHoleCards, ContinuousTableable continuousTableable) {
        Call2bet call2Bet = new Call2bet(actionBuilderUtil);
        _3bet x3Bet = new _3bet(actionBuilderUtil);

        Map<Integer, Set<Card>> call2bet_comboMap90Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2Bet.getComboMap90Percent());
        Map<Integer, Set<Card>> call2bet_comboMap80Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2Bet.getComboMap80Percent());
        Map<Integer, Set<Card>> call2bet_comboMap65Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2Bet.getComboMap65Percent());
        Map<Integer, Set<Card>> call2bet_comboMap50Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2Bet.getComboMap50Percent());
        Map<Integer, Set<Card>> call2bet_comboMap33Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2Bet.getComboMap33Percent());
        Map<Integer, Set<Card>> call2bet_comboMap30Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2Bet.getComboMap30Percent());
        Map<Integer, Set<Card>> call2bet_comboMap10Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2Bet.getComboMap10Percent());
        Map<Integer, Set<Card>> call2bet_comboMap5Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call2Bet.getComboMap5Percent());

        Map<Integer, Set<Card>> x3bet_comboMap95Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap95Percent());
        Map<Integer, Set<Card>> x3bet_comboMap70Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap70Percent());
        Map<Integer, Set<Card>> x3bet_comboMap50Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap50Percent());
        Map<Integer, Set<Card>> x3bet_comboMap35Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap35Percent());
        Map<Integer, Set<Card>> x3bet_comboMap20Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap20Percent());
        Map<Integer, Set<Card>> x3bet_comboMap10Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap10Percent());
        Map<Integer, Set<Card>> x3bet_comboMap5Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x3Bet.getComboMap5Percent());

        double percentageCall2bet;
        double percentage3bet;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(botHoleCards);

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

        double random = Math.random();
        if(random <= 1 - percentage3bet - percentageCall2bet) {
            return "fold";
        } else if ((random <= 1 - percentage3bet) && (random >= 1 - percentage3bet - percentageCall2bet)){
            return "call";
        } else {
            continuousTableable.setPre3betOrPostRaisedPot(true);
            return "raise";
        }
    }

    private String get3betF4bet(List<Card> botHoleCards, ContinuousTableable continuousTableable, String opponenType) {
        Call4bet call4Bet = new Call4bet(actionBuilderUtil, opponenType);

        Map<Integer, Set<Card>> call4bet_comboMap100Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4Bet.getComboMap100Percent());
        Map<Integer, Set<Card>> call4bet_comboMap5Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4Bet.getComboMap5Percent());

        _5bet x5bet = new _5bet(actionBuilderUtil, opponenType);

        Map<Integer, Set<Card>> x5bet_comboMap95Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x5bet.getComboMap95Percent());
        Map<Integer, Set<Card>> x5bet_comboMap50Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x5bet.getComboMap50Percent());

        double percentageCall4bet;
        double percentage5bet;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(botHoleCards);

        percentageCall4bet = setPercentage(call4bet_comboMap100Percent, holeCardsAsSet, 1.0);

        if(percentageCall4bet == 0) {
            percentageCall4bet = setPercentage(call4bet_comboMap5Percent, holeCardsAsSet, 0.05);
        }

        percentage5bet = setPercentage(x5bet_comboMap95Percent, holeCardsAsSet, 0.95);

        if(percentage5bet == 0) {
            percentage5bet = setPercentage(x5bet_comboMap50Percent, holeCardsAsSet, 0.50);
        }

        double random = Math.random();
        if(random <= 1 - percentage5bet - percentageCall4bet) {
            return "fold";
        } else if ((random <= 1 - percentage5bet) && (random >= 1 - percentage5bet - percentageCall4bet)){
            if(continuousTableable != null) {
                System.out.println("Opponent did preflop 4bet and bot called");
                continuousTableable.setOpponentDidPreflop4betPot(true);
            }

            return "call";
        } else {
            return "raise";
        }
    }

    private String get4betF5bet(List<Card> botHoleCards, double amountToCallBb) {
        Call5bet call5Bet = new Call5bet(actionBuilderUtil, amountToCallBb);

        Map<Integer, Set<Card>> call5bet_comboMap100Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call5Bet.getComboMap100Percent());

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(botHoleCards);

        double percentageCall5bet = setPercentage(call5bet_comboMap100Percent, holeCardsAsSet, 1.0);

        double random = Math.random();
        if(random < percentageCall5bet) {
            return "call";
        } else {
            return "fold";
        }
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
