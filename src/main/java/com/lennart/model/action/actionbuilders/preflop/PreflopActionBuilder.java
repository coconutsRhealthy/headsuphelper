package com.lennart.model.action.actionbuilders.preflop;

import com.lennart.model.action.Actionable;
import com.lennart.model.action.actionbuilders.ai.ContinuousTableable;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OppIdentifierPreflopStats;
import com.lennart.model.card.Card;
import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.ip._2bet;
import com.lennart.model.handevaluation.PreflopHandStength;

import java.util.*;

/**
 * Created by lennart on 19-11-16.
 */
public class PreflopActionBuilder {

    private ActionBuilderUtil actionBuilderUtil;
    Map<String, String> oppPreGroupMapClassVariable = null;

    public PreflopActionBuilder() {
        actionBuilderUtil = new ActionBuilderUtil();
    }

    public String getAction(double opponentBetSize, double botBetSize, double opponentStack, double bigBlind,
                            List<Card> botHoleCards, boolean botIsButton, ContinuousTableable continuousTableable,
                            double amountToCallBb, String opponentName, boolean noOfHandIsBluffable, double effectiveStackBb) throws Exception {
        String action;
        double bbOpponentTotalBetSize = opponentBetSize / bigBlind;

        boolean effectiveAllIn = opponentBetSize >= ((botBetSize + (amountToCallBb * bigBlind)) * 1.1);

        if(effectiveAllIn) {
            System.out.println("Effective preflop allin is true!");
        }

        Map<String, String> oppPreGroupMap = new OppIdentifierPreflopStats().getOppPreGroupMap(opponentName);
        oppPreGroupMapClassVariable = oppPreGroupMap;

        String oppPre2betGroup = oppPreGroupMap.get("pre2betGroup");
        String oppPre3betGroup = oppPreGroupMap.get("pre3betGroup");
        String oppPre4bet_up_Group = oppPreGroupMap.get("pre4bet_up_group");

        System.out.println(opponentName + " pre2betGroup: " + oppPre2betGroup);
        System.out.println(opponentName + " pre3betGroup: " + oppPre3betGroup);
        System.out.println(opponentName + " pre4betGroup: " + oppPre4bet_up_Group);

        if(opponentStack <= 0 || effectiveAllIn) {
            action = getCallOrFoldActionFacingAllIn(amountToCallBb, botHoleCards, oppPre3betGroup, oppPre4bet_up_Group);
        } else {
            if(bbOpponentTotalBetSize == 1) {
                if(botIsButton) {
                    action = get05betF1bet(botHoleCards, bigBlind);
                } else {
                    action = get1betFcheck(botHoleCards);
                }
            } else if(bbOpponentTotalBetSize > 1 && bbOpponentTotalBetSize <= 3) {
                action = get1betF2bet(botHoleCards, continuousTableable, oppPre2betGroup, noOfHandIsBluffable, botIsButton, bbOpponentTotalBetSize, effectiveStackBb);
            } else if(bbOpponentTotalBetSize > 3 && bbOpponentTotalBetSize <= 10) {
                action = get2betF3bet(botHoleCards, continuousTableable, oppPre3betGroup);
            } else if(bbOpponentTotalBetSize > 10 && bbOpponentTotalBetSize <= 25) {
                action = get3betF4bet(botHoleCards, continuousTableable, oppPre4bet_up_Group);
            } else {
                action = get4betF5bet(botHoleCards, oppPre4bet_up_Group);
            }
        }

        if(action.equals("fold") && ((opponentBetSize - botBetSize) / (opponentBetSize + botBetSize) < 0.24)) {
            action = "call";
        }

        return action;
    }

    private List<List<Card>> getPre3betPoule(String oppPre2betGroup, boolean noOfHandIsBluffable, double effectiveStackBb) {
        List<List<Card>> pre3betPoule = new ArrayList<>();

        Map<Double, List<Set<Card>>> allHands = new PreflopHandStength().getMapWithAllPreflopHandstrengthGroups();

        double limit;

        //if(effectiveStackBb > 12) {
        if(effectiveStackBb > -1) {
            if(oppPre2betGroup.equals("low")) {
                limit = 0.9;
            } else {
                double random = Math.random();

                //if(random < 0.10) {
                if(random < 0.41) {
                    limit = 0.5;
                } else if(random < 0.67) {
                    limit = 0.75;
                } else {
                    limit = 0.9;
                }
            }
        } else {
            if(oppPre2betGroup.equals("low") || oppPre2betGroup.equals("mediumUnknown")) {
                limit = 0.9;
            } else {
                if(noOfHandIsBluffable) {
                    limit = 0.75;
                } else {
                    limit = 0.9;
                }
            }
        }

        for (Map.Entry<Double, List<Set<Card>>> entry : allHands.entrySet()) {
            if(entry.getKey() > 0.95) {
                for(Set<Card> combo : entry.getValue()) {
                    List<Card> comboToAdd = new ArrayList<>();
                    comboToAdd.addAll(combo);
                    pre3betPoule.add(comboToAdd);
                }
            } else if(entry.getKey() > limit) {
                for(Set<Card> combo : entry.getValue()) {
                    double random = Math.random();

                    if(random <= 0.92) {
                        List<Card> comboToAdd = new ArrayList<>();
                        comboToAdd.addAll(combo);
                        pre3betPoule.add(comboToAdd);
                    }
                }
            }
        }

        return pre3betPoule;
    }

    private List<List<Card>> getPre4or5betPoule(String oppPre3or4bet_up_Group) {
        List<Set<Card>> pre4or5betPouleAsSets = new ArrayList<>();

        pre4or5betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(14).values());
        pre4or5betPouleAsSets.addAll(actionBuilderUtil.getOffSuitCombosOfGivenRanks(14, 13).values());
        pre4or5betPouleAsSets.addAll(actionBuilderUtil.getOffSuitCombosOfGivenRanks(14, 12).values());
        pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 13).values());
        pre4or5betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(13).values());
        pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 12).values());
        pre4or5betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(12).values());
        pre4or5betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(11).values());

        if(oppPre3or4bet_up_Group.equals("mediumUnknown")) {
            pre4or5betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(10).values());
        }

        if(oppPre3or4bet_up_Group.equals("medium") || oppPre3or4bet_up_Group.equals("high")) {
            pre4or5betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(10).values());
            pre4or5betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(9).values());
            pre4or5betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(8).values());
            pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 11).values());
            pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 10).values());
            pre4or5betPouleAsSets.addAll(actionBuilderUtil.getOffSuitCombosOfGivenRanks(14, 11).values());

            if(oppPre3or4bet_up_Group.equals("high")) {
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(7).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(6).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(5).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRank(4).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 9).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 8).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 7).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 6).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 5).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 4).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 3).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(14, 2).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getOffSuitCombosOfGivenRanks(14, 10).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getOffSuitCombosOfGivenRanks(14, 9).values());
                pre4or5betPouleAsSets.addAll(actionBuilderUtil.getOffSuitCombosOfGivenRanks(14, 8).values());

                if(Math.random() < 0.5) {
                    pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(10, 9).values());
                    pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(9, 8).values());
                    pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(8, 7).values());
                    pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(11, 9).values());
                    pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(10, 8).values());
                    pre4or5betPouleAsSets.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanks(9, 7).values());
                }
            }
        }

        List<List<Card>> pre4or5betpoule = new ArrayList<>();

        for(Set<Card> set : pre4or5betPouleAsSets) {
            List<Card> setAsList = new ArrayList<>();
            setAsList.addAll(set);
            pre4or5betpoule.add(setAsList);
        }

        return pre4or5betpoule;
    }

    private List<List<Card>> getPreCall2betPoule(List<List<Card>> pre3betPoule, String oppPre2betGroup, double effectiveStackBb) {
        List<List<Card>> preCall2betPoule = new ArrayList<>();

        Map<Double, List<Set<Card>>> allHands = new PreflopHandStength().getMapWithAllPreflopHandstrengthGroups();

        double limit;

        //if(effectiveStackBb > 12) {
        if(effectiveStackBb > -1) {
            if(oppPre2betGroup.equals("low")) {
                limit = 0.4;
            } else {
                limit = 0.25;
            }
        } else {
            if(oppPre2betGroup.equals("low") || oppPre2betGroup.equals("mediumUnknown")) {
                limit = 0.65;
            } else if(oppPre2betGroup.equals("facingLimp")) {
                limit = 0.5;
            } else {
                limit = 0.5;
            }
        }

        for (Map.Entry<Double, List<Set<Card>>> entry : allHands.entrySet()) {
            if(entry.getKey() > limit) {
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

    private List<List<Card>> getPreCall3betPoule(List<List<Card>> pre4betPoule, String oppPre3betGroup) {
        List<List<Card>> preCall3betPoule = new ArrayList<>();

        Map<Double, List<Set<Card>>> allHands = new PreflopHandStength().getMapWithAllPreflopHandstrengthGroups();

        double limit;

        if(oppPre3betGroup.equals("low") || oppPre3betGroup.equals("mediumUnknown")) {
            limit = 0.75;
        } else if(oppPre3betGroup.equals("medium")){
            limit = 0.6;
        } else {
            limit = 0.5;
        }

        for (Map.Entry<Double, List<Set<Card>>> entry : allHands.entrySet()) {
            if(entry.getKey() > limit) {
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

    private List<List<Card>> getPreCall4betPoule(List<List<Card>> pre5betPoule, String oppPre4bet_up_Group) {
        List<List<Card>> preCall4betPoule = new ArrayList<>();

        Map<Double, List<Set<Card>>> allHands = new PreflopHandStength().getMapWithAllPreflopHandstrengthGroups();

        double limit;

        if(oppPre4bet_up_Group.equals("low") || oppPre4bet_up_Group.equals("mediumUnknown")) {
            limit = 0.95;
        } else if(oppPre4bet_up_Group.equals("medium")){
            limit = 0.80;
        } else {
            limit = 0.65;
        }

        for (Map.Entry<Double, List<Set<Card>>> entry : allHands.entrySet()) {
            if(entry.getKey() > limit) {
                for(Set<Card> combo : entry.getValue()) {
                    List<Card> comboAsList = new ArrayList<>();
                    comboAsList.addAll(combo);

                    if(!pre5betPoule.contains(comboAsList)) {
                        preCall4betPoule.add(comboAsList);
                    }
                }
            }
        }

        return preCall4betPoule;
    }

    private List<List<Card>> getPreCall5betPoule(String oppPre4bet_up_Group) {
        List<List<Card>> preCall5betPoule = new ArrayList<>();

        Map<Double, List<Set<Card>>> allHands = new PreflopHandStength().getMapWithAllPreflopHandstrengthGroups();

        double limit;

        if(oppPre4bet_up_Group.equals("low") || oppPre4bet_up_Group.equals("mediumUnknown")) {
            limit = 0.95;
        } else if(oppPre4bet_up_Group.equals("medium")){
            limit = 0.85;
        } else {
            limit = 0.7;
        }

        for (Map.Entry<Double, List<Set<Card>>> entry : allHands.entrySet()) {
            if(entry.getKey() > limit) {
                for(Set<Card> combo : entry.getValue()) {
                    List<Card> comboAsList = new ArrayList<>();
                    comboAsList.addAll(combo);
                    preCall5betPoule.add(comboAsList);
                }
            }
        }

        return preCall5betPoule;
    }

    private List<List<Card>> addMoreCombosToPre3betPoule(List<List<Card>> currentPre3betPoule) {
        Set<List<Card>> checkSet = new HashSet<>();

        for(List<Card> combo : currentPre3betPoule) {
            checkSet.add(combo);
        }

        ActionBuilderUtil actionBuilderUtil = new ActionBuilderUtil();
        Map<Integer, Set<Card>> mapSuited = actionBuilderUtil.getSuitedHoleCards(2, 2, 100);
        Map<Integer, Set<Card>> mapPocketPairs = actionBuilderUtil.getPocketPairs(2, 100);

        for (Map.Entry<Integer, Set<Card>> entry : mapSuited.entrySet()) {
            double random = Math.random();

            if(random < 0.33) {
                List<Card> combo = new ArrayList<>();
                combo.addAll(entry.getValue());
                checkSet.add(combo);
            }
        }

        for (Map.Entry<Integer, Set<Card>> entry : mapPocketPairs.entrySet()) {
            double random = Math.random();

            if(random < 0.75) {
                List<Card> combo = new ArrayList<>();
                combo.addAll(entry.getValue());
                checkSet.add(combo);
            }
        }

        List<List<Card>> pre3betPouleToReturn = new ArrayList<>();
        pre3betPouleToReturn.addAll(checkSet);

        return pre3betPouleToReturn;
    }

    private List<List<Card>> addMoreCombosToPreCall2betPoule(List<List<Card>> currentPreCall2betPoule) {
        Set<List<Card>> checkSet = new HashSet<>();

        for(List<Card> combo : currentPreCall2betPoule) {
            checkSet.add(combo);
        }

        ActionBuilderUtil actionBuilderUtil = new ActionBuilderUtil();
        Map<Integer, Set<Card>> mapSuited = actionBuilderUtil.getSuitedHoleCards(2, 2, 100);

        for (Map.Entry<Integer, Set<Card>> entry : mapSuited.entrySet()) {
            List<Card> combo = new ArrayList<>();
            combo.addAll(entry.getValue());
            checkSet.add(combo);
        }

        List<List<Card>> preCall2betPouleToReturn = new ArrayList<>();
        preCall2betPouleToReturn.addAll(checkSet);

        return preCall2betPouleToReturn;
    }

    private String getCallOrFoldActionFacingAllIn(double amountToCallBb, List<Card> botHoleCards,
                                                  String oppPre3betGroup, String oppPre4bet_up_Group) {
        String actionToReturn;

        //for Hypers
        if(amountToCallBb <= 25 && (oppPre3betGroup.contains("high") || oppPre4bet_up_Group.contains("high"))) {
            double limit = -1;

            if(amountToCallBb > 10) {
                if(oppPre4bet_up_Group.contains("high")) {
                    if(amountToCallBb >= 20) {
                        limit = 0.8;
                    } else if(amountToCallBb >= 15) {
                        limit = 0.75;
                    } else if(amountToCallBb >= 10) {
                        limit = 0.7;
                    }
                }
            } else {
                if(amountToCallBb >= 7) {
                    limit = 0.6;
                } else if(amountToCallBb >= 5) {
                    limit = 0.5;
                } else if(amountToCallBb >= 3) {
                    limit = 0.3;
                } else if(amountToCallBb >= 2) {
                    limit = 0.15;
                } else {
                    limit = 0.0;
                }
            }

            if(limit != -1) {
                System.out.println("Call loose against opp pf shove. oppPre3betGroup: " + oppPre3betGroup + " oppPre4betGroup: " + oppPre4bet_up_Group + " limit: " + limit);
                return getActionFacingAllIn(botHoleCards, limit);
            }
        }

        if(amountToCallBb <= 4) {
            actionToReturn = getActionFacingAllIn(botHoleCards, 0.5);
        } else if(amountToCallBb <= 16) {
            double limit;

            if(oppPre3betGroup.equals("low")) {
                limit = 0.85;
            } else  {
                limit = 0.75;
            }

            actionToReturn = getActionFacingAllIn(botHoleCards, limit);
        } else if(amountToCallBb <= 25) {
            double limit;

            if(oppPre4bet_up_Group.equals("low")) {
                limit = 0.95;
            } else if(oppPre4bet_up_Group.equals("mediumUnknown")) {
                limit = 0.90;
            } else if(oppPre4bet_up_Group.equals("medium"))  {
                limit = 0.80;
            } else {
                limit = 0.70;
            }

            actionToReturn = getActionFacingAllIn(botHoleCards, limit);
        } else if(amountToCallBb <= 40) {
            double limit;

            if(oppPre4bet_up_Group.equals("low")) {
                limit = 0.95;
            } else if(oppPre4bet_up_Group.equals("mediumUnknown")) {
                limit = 0.90;
            } else if(oppPre4bet_up_Group.equals("medium"))  {
                limit = 0.85;
            } else {
                limit = 0.70;
            }

            actionToReturn = getActionFacingAllIn(botHoleCards, limit);
        } else {
            actionToReturn = get4betF5bet(botHoleCards, oppPre4bet_up_Group);
        }

        return actionToReturn;
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

    private String get05betF1bet(List<Card> botHoleCards, double bigBlind) {
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
            if(bigBlind >= 50) {
                System.out.println("Preflop limp!");
                return "call";
            } else {
                return "raise";
            }
        } else {
            return "fold";
        }
    }

    private String get2betF3bet(List<Card> botHoleCards, ContinuousTableable continuousTableable, String oppPre3betGroup) {
        List<Card> botHoleCardsReverseOrder = new ArrayList<>();
        botHoleCardsReverseOrder.add(botHoleCards.get(1));
        botHoleCardsReverseOrder.add(botHoleCards.get(0));

        List<List<Card>> pre4betPoule = getPre4or5betPoule(oppPre3betGroup);
        List<List<Card>> preCall3betPoule = getPreCall3betPoule(pre4betPoule, oppPre3betGroup);

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

    private String get1betF2bet(List<Card> botHoleCards, ContinuousTableable continuousTableable, String oppPre2betGroup, boolean noOfHandIsBluffable,
                                boolean position, double oppBetsizeBb, double effectiveStackBb) {
        String actionToReturn;

        List<Card> botHoleCardsReverseOrder = new ArrayList<>();
        botHoleCardsReverseOrder.add(botHoleCards.get(1));
        botHoleCardsReverseOrder.add(botHoleCards.get(0));

        List<List<Card>> pre3betPoule = getPre3betPoule(oppPre2betGroup, noOfHandIsBluffable, effectiveStackBb);
        List<List<Card>> preCall2betPoule = getPreCall2betPoule(pre3betPoule, oppPre2betGroup, effectiveStackBb);

        if(pre3betPoule.contains(botHoleCards) || pre3betPoule.contains(botHoleCardsReverseOrder)) {
            continuousTableable.setPre3betOrPostRaisedPot(true);
            actionToReturn = "raise";
        } else if(preCall2betPoule.contains(botHoleCards) || preCall2betPoule.contains(botHoleCardsReverseOrder)) {
            actionToReturn = "call";
        } else {
            actionToReturn = "fold";
        }

        if(position && actionToReturn.equals("fold")) {
            if(oppBetsizeBb < 2.5) {
                actionToReturn = "call";
                System.out.println("call after preflop limp");
            }
        }

        return actionToReturn;
    }

    private String get1betFcheck(List<Card> botHoleCards) {
        List<Card> botHoleCardsReverseOrder = new ArrayList<>();
        botHoleCardsReverseOrder.add(botHoleCards.get(1));
        botHoleCardsReverseOrder.add(botHoleCards.get(0));

        List<List<Card>> pre3betPoule = getPre3betPoule("high", Math.random() < 0.2, -3);
        List<List<Card>> preCall2betPoule = getPreCall2betPoule(pre3betPoule, "facingLimp", -3);

        if(pre3betPoule.contains(botHoleCards) || pre3betPoule.contains(botHoleCardsReverseOrder)
                || preCall2betPoule.contains(botHoleCards) || preCall2betPoule.contains(botHoleCardsReverseOrder)) {
            return "raise";
        } else {
            return "check";
        }
    }

    private String get3betF4bet(List<Card> botHoleCards, ContinuousTableable continuousTableable, String oppPre4betGroup) {
        List<Card> botHoleCardsReverseOrder = new ArrayList<>();
        botHoleCardsReverseOrder.add(botHoleCards.get(1));
        botHoleCardsReverseOrder.add(botHoleCards.get(0));

        List<List<Card>> pre5betPoule = getPre4or5betPoule(oppPre4betGroup);
        List<List<Card>> preCall4betPoule = getPreCall4betPoule(pre5betPoule, oppPre4betGroup);

        if(pre5betPoule.contains(botHoleCards) || pre5betPoule.contains(botHoleCardsReverseOrder)) {
            continuousTableable.setPre3betOrPostRaisedPot(true);
            return "raise";
        } else if(preCall4betPoule.contains(botHoleCards) || preCall4betPoule.contains(botHoleCardsReverseOrder)) {
            continuousTableable.setPre3betOrPostRaisedPot(true);
            return "call";
        } else {
            return "fold";
        }
    }

    private String get4betF5bet(List<Card> botHoleCards, String oppPre4bet_up_Group) {
        List<Card> botHoleCardsReverseOrder = new ArrayList<>();
        botHoleCardsReverseOrder.add(botHoleCards.get(1));
        botHoleCardsReverseOrder.add(botHoleCards.get(0));

        List<List<Card>> preCall5betPoule = getPreCall5betPoule(oppPre4bet_up_Group);

        if(preCall5betPoule.contains(botHoleCards) || preCall5betPoule.contains(botHoleCardsReverseOrder)) {
            return "call";
        } else {
            return "fold";
        }
    }

    public Map<String, String> getOppPreGroupMapClassVariable() {
        return oppPreGroupMapClassVariable;
    }
}
