package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;

import java.util.*;

public class BotRange {

    public Map<Integer, List<Card>> updateBotRange(Map<Integer, List<Card>> previousBotRange, String actionDoneByBot,
                                                   ContinuousTable continuousTable, GameVariables gameVariables,
                                                   String opponentType, BoardEvaluator boardEvaluator) throws Exception {

        //loop door al je combos van je previousRange heen en zie welke combos dezelfde actie doen als 'action';

        //dit is je range

        Map<Integer, List<Card>> rangeToReturn = new HashMap<>();

        for (Map.Entry<Integer, List<Card>> entry : previousBotRange.entrySet()) {
            Card newBotHoleCard1 = entry.getValue().get(0);
            Card newBotHoleCard2 = entry.getValue().get(1);
            List<Card> newBotHoleCards = new ArrayList<>();
            newBotHoleCards.add(newBotHoleCard1);
            newBotHoleCards.add(newBotHoleCard2);

            gameVariables.setBotHoleCard1(newBotHoleCard1);
            gameVariables.setBotHoleCard2(newBotHoleCard2);
            gameVariables.setBotHoleCards(newBotHoleCards);

            ActionVariables actionVariables = new ActionVariables(gameVariables, continuousTable, boardEvaluator, opponentType);
            String actionOfCombo = actionVariables.getAction();

            if(actionOfCombo.equals(actionDoneByBot)) {
                List<Card> comboCopy = new ArrayList<>();
                comboCopy.addAll(entry.getValue());
                rangeToReturn.put(rangeToReturn.size(), comboCopy);
            }
        }

        return rangeToReturn;
    }

    public Map<Integer, List<String>> getHsAndActionPerCombo(Map<Integer, List<Card>> botRange, ContinuousTable continuousTable,
                                                             GameVariables gameVariables, BoardEvaluator boardEvaluator,
                                                             String opponentType) throws Exception {
        Map<Integer, List<String>> hsAndActionPerCombo = new HashMap<>();

        for (Map.Entry<Integer, List<Card>> entry : botRange.entrySet()) {
            Card newBotHoleCard1 = entry.getValue().get(0);
            Card newBotHoleCard2 = entry.getValue().get(1);
            List<Card> newBotHoleCards = new ArrayList<>();
            newBotHoleCards.add(newBotHoleCard1);
            newBotHoleCards.add(newBotHoleCard2);

            gameVariables.setBotHoleCard1(newBotHoleCard1);
            gameVariables.setBotHoleCard2(newBotHoleCard2);
            gameVariables.setBotHoleCards(newBotHoleCards);

            ActionVariables actionVariables = new ActionVariables(gameVariables, continuousTable, boardEvaluator, opponentType);
            String actionOfCombo = actionVariables.getAction();
            double handStrength = actionVariables.getHandEvaluator().getHandStrength(newBotHoleCards);

            List<String> hsAndAction = new ArrayList<>();
            hsAndAction.add(actionOfCombo);
            hsAndAction.add(String.valueOf(handStrength));

            hsAndActionPerCombo.put(hsAndActionPerCombo.size(), hsAndAction);
        }

        return hsAndActionPerCombo;
    }

    public double getNumberOfValueBetRaiseCombos(Map<Integer, List<String>> hsAndActionPerCombo, String action) {
        double valueBetRaiseCombos = 0;

        for (Map.Entry<Integer, List<String>> entry : hsAndActionPerCombo.entrySet()) {
            String comboAction = entry.getValue().get(0);
            double hs = Double.parseDouble(entry.getValue().get(1));

            if(comboAction.equals(action) && hs >= 0.8) {
                valueBetRaiseCombos++;
            }
        }

        return valueBetRaiseCombos;
    }

    public double getNumberOfCombosBelowHsLimit(Map<Integer, List<String>> hsAndActionPerCombo, double limit) {
        double combosBelowHsLimit = 0;

        for (Map.Entry<Integer, List<String>> entry : hsAndActionPerCombo.entrySet()) {
            double hs = Double.parseDouble(entry.getValue().get(1));

            if(hs < limit) {
                combosBelowHsLimit++;
            }
        }

        return combosBelowHsLimit;
    }
}
