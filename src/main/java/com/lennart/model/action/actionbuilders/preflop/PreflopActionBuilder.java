package com.lennart.model.action.actionbuilders.preflop;

import com.lennart.model.action.Actionable;
import com.lennart.model.action.actionbuilders.ai.ContinuousTableable;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop._5bet;
import com.lennart.model.card.Card;
import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.ip.Call5bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.ip._2bet;
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

    public String getAction(double opponentBetSize, double botBetSize, double opponentStack, double bigBlind,
                            List<Card> botHoleCards, boolean botIsButton, ContinuousTableable continuousTableable,
                            String opponentType, double amountToCallBb) {
        String action;
        double bbOpponentTotalBetSize = opponentBetSize / bigBlind;

        boolean effectiveAllIn = opponentBetSize >= ((botBetSize + (amountToCallBb * bigBlind)) * 1.1);

        if(effectiveAllIn) {
            System.out.println("Effective preflop allin is true!");
        }

        if(opponentStack <= 0 || effectiveAllIn) {
            if(amountToCallBb <= 4) {
                action = getActionFacingAllIn(botHoleCards, 0.5);
            } else if(amountToCallBb <= 16) {
                action = getActionFacingAllIn(botHoleCards, 0.75);
            } else if(amountToCallBb <= 25) {
                action = getActionFacingAllIn(botHoleCards, 0.80);
            } else if(amountToCallBb <= 40) {
                action = getActionFacingAllIn(botHoleCards, 0.90);
            } else {
                action = get4betF5bet(botHoleCards, amountToCallBb);
            }
        } else {
            if(bbOpponentTotalBetSize == 1) {
                if(botIsButton) {
                    action = get05betF1bet(botHoleCards);
                } else {
                    action = get1betFcheck(botHoleCards);
                }
            } else if(bbOpponentTotalBetSize > 1 && bbOpponentTotalBetSize <= 3) {
                action = get1betF2bet(botHoleCards, continuousTableable);
            } else if(bbOpponentTotalBetSize > 3 && bbOpponentTotalBetSize <= 16) {
                action = get2betF3bet(botHoleCards, continuousTableable);
            } else if(bbOpponentTotalBetSize >= 16 && bbOpponentTotalBetSize <= 40) {
                action = get3betF4bet(botHoleCards, continuousTableable, opponentType);
            } else {
                action = get4betF5bet(botHoleCards, amountToCallBb);
            }
        }

        if(action.equals("fold") && ((opponentBetSize - botBetSize) / (opponentBetSize + botBetSize) < 0.24)) {
            action = "call";
        }

        return action;
    }

    private List<List<Card>> getPre3betPoule() {
        List<List<Card>> pre3betPoule = new ArrayList<>();

        Map<Double, List<Set<Card>>> allHands = new PreflopHandStength().getMapWithAllPreflopHandstrengthGroups();

        for (Map.Entry<Double, List<Set<Card>>> entry : allHands.entrySet()) {
            if(entry.getKey() > 0.95) {
                for(Set<Card> combo : entry.getValue()) {
                    List<Card> comboToAdd = new ArrayList<>();
                    comboToAdd.addAll(combo);
                    pre3betPoule.add(comboToAdd);
                }
            } else if(entry.getKey() > 0.75) {
                for(Set<Card> combo : entry.getValue()) {
                    double random = Math.random();

                    if(random <= 0.65) {
                        List<Card> comboToAdd = new ArrayList<>();
                        comboToAdd.addAll(combo);
                        pre3betPoule.add(comboToAdd);
                    }
                }
            }
        }

        return pre3betPoule;
    }

    private List<List<Card>> getPreCall2betPoule(List<List<Card>> pre3betPoule) {
        List<List<Card>> preCall2betPoule = new ArrayList<>();

        Map<Double, List<Set<Card>>> allHands = new PreflopHandStength().getMapWithAllPreflopHandstrengthGroups();

        for (Map.Entry<Double, List<Set<Card>>> entry : allHands.entrySet()) {
            if(entry.getKey() > 0.5) {
                for(Set<Card> combo : entry.getValue()) {
                    List<Card> comboAsList = new ArrayList<>();
                    comboAsList.addAll(combo);

                    if(!pre3betPoule.contains(comboAsList)) {
                        preCall2betPoule.add(comboAsList);
                    }
                }
            }
        }

        return preCall2betPoule;
    }

    private List<List<Card>> getPre4betPoule() {
        List<Set<Card>> pre4betPouleAsSets = new ArrayList<>();

        pre4betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(14).values());
        pre4betPouleAsSets.addAll(actionBuilderUtil.getOffSuitCombosOfGivenRanks(14, 13).values());
        pre4betPouleAsSets.addAll(actionBuilderUtil.getOffSuitCombosOfGivenRanks(14, 12).values());
        pre4betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 13).values());
        pre4betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(13).values());
        pre4betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 12).values());
        pre4betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(12).values());
        pre4betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(11).values());

        if(Math.random() < 0.5) {
            pre4betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(10).values());
        }

        if(Math.random() < 0.5) {
            pre4betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(9).values());
        }

        if(Math.random() < 0.5) {
            pre4betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(8).values());
        }

        List<List<Card>> pre4betpoule = new ArrayList<>();

        for(Set<Card> set : pre4betPouleAsSets) {
            List<Card> setAsList = new ArrayList<>();
            setAsList.addAll(set);
            pre4betpoule.add(setAsList);
        }

        return pre4betpoule;
    }

    private List<List<Card>> getPreCall3betPoule(List<List<Card>> pre4betPoule) {
        List<List<Card>> preCall3betPoule = new ArrayList<>();

        Map<Double, List<Set<Card>>> allHands = new PreflopHandStength().getMapWithAllPreflopHandstrengthGroups();

        for (Map.Entry<Double, List<Set<Card>>> entry : allHands.entrySet()) {
            if(entry.getKey() > 0.60) {
                for(Set<Card> combo : entry.getValue()) {
                    List<Card> comboAsList = new ArrayList<>();
                    comboAsList.addAll(combo);

                    if(!pre4betPoule.contains(comboAsList)) {
                        preCall3betPoule.add(comboAsList);
                    }
                }
            }
        }

        return preCall3betPoule;
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

    private String get2betF3bet(List<Card> botHoleCards, ContinuousTableable continuousTableable) {
        List<Card> botHoleCardsReverseOrder = new ArrayList<>();
        botHoleCardsReverseOrder.add(botHoleCards.get(1));
        botHoleCardsReverseOrder.add(botHoleCards.get(0));

        List<List<Card>> pre4betPoule = getPre4betPoule();
        List<List<Card>> preCall3betPoule = getPreCall3betPoule(pre4betPoule);

        if(pre4betPoule.contains(botHoleCards) || pre4betPoule.contains(botHoleCardsReverseOrder)) {
            continuousTableable.setPre3betOrPostRaisedPot(true);
            return "raise";
        } else if(preCall3betPoule.contains(botHoleCards) || preCall3betPoule.contains(botHoleCardsReverseOrder)) {
            continuousTableable.setPre3betOrPostRaisedPot(true);
            return "call";
        } else {
            return "fold";
        }
    }

    private String get1betF2bet(List<Card> botHoleCards, ContinuousTableable continuousTableable) {
        List<Card> botHoleCardsReverseOrder = new ArrayList<>();
        botHoleCardsReverseOrder.add(botHoleCards.get(1));
        botHoleCardsReverseOrder.add(botHoleCards.get(0));

        List<List<Card>> pre3betPoule = getPre3betPoule();
        List<List<Card>> preCall2betPoule = getPreCall2betPoule(pre3betPoule);

        if(pre3betPoule.contains(botHoleCards) || pre3betPoule.contains(botHoleCardsReverseOrder)) {
            continuousTableable.setPre3betOrPostRaisedPot(true);
            return "raise";
        } else if(preCall2betPoule.contains(botHoleCards) || preCall2betPoule.contains(botHoleCardsReverseOrder)) {
            return "call";
        } else {
            return "fold";
        }
    }

    private String get1betFcheck(List<Card> botHoleCards) {
        List<Card> botHoleCardsReverseOrder = new ArrayList<>();
        botHoleCardsReverseOrder.add(botHoleCards.get(1));
        botHoleCardsReverseOrder.add(botHoleCards.get(0));

        List<List<Card>> pre3betPoule = getPre3betPoule();

        if(pre3betPoule.contains(botHoleCards) || pre3betPoule.contains(botHoleCardsReverseOrder)) {
            return "raise";
        } else {
            return "check";
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

    public static void main(String[] args) {
        new PreflopActionBuilder().getPreCall3betSngDeepPoule();
    }

    public List<List<Card>> getPreCall3betSngDeepPoule() {
        List<List<Card>> call3betCombos = new ArrayList<>();
        List<Map<Integer, Set<Card>>> allCombosInMapsInList = new ArrayList<>();

        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(14));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(13));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(12));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(11));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(10));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(9));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(8));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(7));

        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(14));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(13));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(12));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(11));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(10));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(9));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(8));
        allCombosInMapsInList.add(actionBuilderUtil.getPocketPairCombosOfGivenRank(7));

        allCombosInMapsInList.add(actionBuilderUtil.getOffSuitCombosOfGivenRanks(14, 13));
        allCombosInMapsInList.add(actionBuilderUtil.getOffSuitCombosOfGivenRanks(14, 12));
        allCombosInMapsInList.add(actionBuilderUtil.getOffSuitCombosOfGivenRanks(14, 11));

        allCombosInMapsInList.add(actionBuilderUtil.getOffSuitCombosOfGivenRanks(13, 12));

        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 13));
        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 12));
        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 11));
        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 10));

        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(13, 12));
        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(12, 11));
        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(11, 10));
        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(10, 9));
        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(9, 8));
        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(8, 7));

        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(13, 11));
        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(12, 10));
        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(11, 9));
        allCombosInMapsInList.add(actionBuilderUtil.getSuitedCombosOfGivenRanks(10, 8));

        for(Map<Integer, Set<Card>> map : allCombosInMapsInList) {
            for(Map.Entry<Integer, Set<Card>> entry : map.entrySet()) {
                List<Card> combo = new ArrayList<>();
                combo.addAll(entry.getValue());
                call3betCombos.add(combo);
            }
        }

        return call3betCombos;
    }
}
