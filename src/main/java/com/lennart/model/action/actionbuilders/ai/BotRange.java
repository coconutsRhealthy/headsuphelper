package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.util.*;

public class BotRange {

    public Map<Integer, List<Card>> updateBotRange(Map<Integer, List<Card>> previousBotRange, String actionDoneByBot, ContinuousTable continuousTable, GameVariables gameVariables, String opponentType) throws Exception {

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

            ContinuousTable continuousTableCopy = continuousTable;

            ActionVariables actionVariables = new ActionVariables(gameVariables, continuousTableCopy, false);
            String actionOfCombo = actionVariables.getAction();

            if(actionOfCombo.equals(actionDoneByBot)) {
                List<Card> comboCopy = new ArrayList<>();
                comboCopy.addAll(entry.getValue());
                rangeToReturn.put(rangeToReturn.size(), comboCopy);
            }
        }

        return rangeToReturn;
    }



}
