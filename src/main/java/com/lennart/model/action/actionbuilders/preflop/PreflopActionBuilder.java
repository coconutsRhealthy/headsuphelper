package com.lennart.model.action.actionbuilders.preflop;

import com.lennart.model.action.Actionable;
import com.lennart.model.card.Card;
import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.ip.Call3bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.ip.Call5bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.ip._2bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.ip._4bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop.Call2bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop.Call4bet;
import com.lennart.model.action.actionbuilders.preflop.bettingrounds.oop._3bet;

import java.util.*;

/**
 * Created by lennart on 19-11-16.
 */
public class PreflopActionBuilder {

    private ActionBuilderUtil actionBuilderUtil;

    public PreflopActionBuilder(Set<Card> knownGameCards) {
        actionBuilderUtil = new ActionBuilderUtil(knownGameCards);
    }

    public String getAction(Actionable actionable) {
        String action = null;

        double bbOpponentTotalBetSize = actionable.getOpponentTotalBetSize() / actionable.getBigBlind();

        if(actionable.getOpponentStack() > -1 && actionable.getOpponentStack() < actionable.getBigBlind()) {
            //opponent is all in
            if(bbOpponentTotalBetSize <= 20) {
                action = getFallInShortStack(actionable);
            }
        }

        if(action == null) {
            if(bbOpponentTotalBetSize == 1) {
                if(actionable.isBotIsButton()) {
                    action = get05betF1bet(actionable);
                } else {
                    action = get1betFcheck(actionable);
                }
            } else if(bbOpponentTotalBetSize > 1 && bbOpponentTotalBetSize <= 4) {
                action = get1betF2bet(actionable);
            } else if(bbOpponentTotalBetSize > 4 && bbOpponentTotalBetSize <= 16) {
                action = get2betF3bet(actionable);
            } else if(bbOpponentTotalBetSize >= 16 && bbOpponentTotalBetSize <= 40) {
                action = get3betF4bet(actionable);
            } else {
                action = get4betF5bet(actionable);
            }

            if(actionable.getOpponentStack() > -1 && actionable.getOpponentStack() < actionable.getBigBlind()
                    && (action.equals("bet") || action.equals("raise"))) {
                action = "call";
            }
        }
        return action;
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

    private String get05betF1bet(Actionable actionable) {
        Map<Integer, Set<Card>> comboMap100Percent;
        Map<Integer, Set<Card>> comboMap5Percent;

        actionable.removeHoleCardsFromKnownGameCards();

        _2bet x2Bet = new _2bet(actionBuilderUtil);

        comboMap100Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x2Bet.getComboMap100Percent());

        comboMap5Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (x2Bet.getComboMap5Percent());

        double percentageBet = 0;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(actionable.getBotHoleCards());

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

        actionable.addHoleCardsToKnownGameCards();

        if(Math.random() <= percentageBet) {
            return "raise";
        } else {
            return "fold";
        }
    }

    private String get1betFcheck(Actionable actionable) {
        actionable.removeHoleCardsFromKnownGameCards();

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
        holeCardsAsSet.addAll(actionable.getBotHoleCards());

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

        actionable.addHoleCardsToKnownGameCards();

        double random = Math.random();
        if(random <= 1 - percentage2bet) {
            return "check";
        } else {
            return "raise";
        }
    }

    private String get2betF3bet(Actionable actionable) {
        actionable.removeHoleCardsFromKnownGameCards();

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
        holeCardsAsSet.addAll(actionable.getBotHoleCards());

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

        actionable.addHoleCardsToKnownGameCards();

        double random = Math.random();
        if(random <= 1 - percentage4bet - percentageCall3bet) {
            return "fold";
        } else if ((random <= 1 - percentage4bet) && (random >= 1 - percentage4bet - percentageCall3bet)){
            return "call";
        } else {
            return "raise";
        }
    }

    private String get1betF2bet(Actionable actionable) {
        actionable.removeHoleCardsFromKnownGameCards();

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
        holeCardsAsSet.addAll(actionable.getBotHoleCards());

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

        actionable.addHoleCardsToKnownGameCards();

        double random = Math.random();
        if(random <= 1 - percentage3bet - percentageCall2bet) {
            return "fold";
        } else if ((random <= 1 - percentage3bet) && (random >= 1 - percentage3bet - percentageCall2bet)){
            return "call";
        } else {
            return "raise";
        }
    }

    private String get3betF4bet(Actionable actionable) {
        actionable.removeHoleCardsFromKnownGameCards();

        Call4bet call4Bet = new Call4bet(actionBuilderUtil);

        Map<Integer, Set<Card>> call4bet_comboMap100Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4Bet.getComboMap100Percent());
        Map<Integer, Set<Card>> call4bet_comboMap80Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4Bet.getComboMap80Percent());
        Map<Integer, Set<Card>> call4bet_comboMap70Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4Bet.getComboMap70Percent());
        Map<Integer, Set<Card>> call4bet_comboMap62Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4Bet.getComboMap62Percent());
        Map<Integer, Set<Card>> call4bet_comboMap60Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4Bet.getComboMap60Percent());
        Map<Integer, Set<Card>> call4bet_comboMap50Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4Bet.getComboMap50Percent());
        Map<Integer, Set<Card>> call4bet_comboMap5Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call4Bet.getComboMap5Percent());

        //nog 5bet hier//

        double percentageCall4bet;
        double percentage5bet = 0;

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(actionable.getBotHoleCards());

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

        actionable.addHoleCardsToKnownGameCards();

        double random = Math.random();
        if(random <= 1 - percentage5bet - percentageCall4bet) {
            return "fold";
        } else if ((random <= 1 - percentage5bet) && (random >= 1 - percentage5bet - percentageCall4bet)){
            return "call";
        } else {
            return "raise";
        }
    }

    private String get4betF5bet(Actionable actionable) {
        actionable.removeHoleCardsFromKnownGameCards();

        Call5bet call5Bet = new Call5bet(actionBuilderUtil);

        Map<Integer, Set<Card>> call5bet_comboMap100Percent = actionBuilderUtil.convertPreflopComboMapToSimpleComboMap
                (call5Bet.getComboMap100Percent());

        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(actionable.getBotHoleCards());

        double percentageCall5bet = setPercentage(call5bet_comboMap100Percent, holeCardsAsSet, 1.0);

        actionable.addHoleCardsToKnownGameCards();

        double random = Math.random();
        if(random < percentageCall5bet) {
            return "call";
        } else {
            return "fold";
        }
    }

    private String getFallInShortStack(Actionable actionable) {
        Set<Set<Card>> combosToCallAllInWithVersusShortStack = new HashSet<>();
        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(actionable.getBotHoleCards());

        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRankIgnoreKnownGameCards(7).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRankIgnoreKnownGameCards(8).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRankIgnoreKnownGameCards(9).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRankIgnoreKnownGameCards(10).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRankIgnoreKnownGameCards(11).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRankIgnoreKnownGameCards(12).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRankIgnoreKnownGameCards(13).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getPocketPairCombosOfGivenRankIgnoreKnownGameCards(14).values());

        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getOffSuitCombosOfGivenRanksIgnoreKnownGameCards(14, 11).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getOffSuitCombosOfGivenRanksIgnoreKnownGameCards(14, 12).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getOffSuitCombosOfGivenRanksIgnoreKnownGameCards(14, 13).values());

        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanksIgnoreKnownGameCards(14, 8).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanksIgnoreKnownGameCards(14, 9).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanksIgnoreKnownGameCards(14, 10).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanksIgnoreKnownGameCards(14, 11).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanksIgnoreKnownGameCards(14, 12).values());
        combosToCallAllInWithVersusShortStack.addAll(actionBuilderUtil.getSuitedCombosOfGivenRanksIgnoreKnownGameCards(14, 13).values());

        if(!combosToCallAllInWithVersusShortStack.add(holeCardsAsSet)) {
            //holecards are a combo to call shortstack shove with
            return "call";
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
