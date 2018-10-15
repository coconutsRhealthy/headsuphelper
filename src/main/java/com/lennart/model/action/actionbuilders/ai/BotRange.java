package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.computergame.ComputerGameNew;
import com.lennart.model.handevaluation.HandEvaluator;

import java.util.*;

public class BotRange {

    public Map<Integer, List<Card>> updateBotRange(Map<Integer, List<Card>> previousBotRange, String actionDoneByBot,
                                                   ContinuousTable continuousTable, GameVariables gameVariables,
                                                   String opponentType, BoardEvaluator boardEvaluator) throws Exception {
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

    public Map<Integer, List<Card>> updateBotRangeComputerGame(Map<Integer, List<Card>> previousBotRange, String actionDoneByBot,
                                                   ComputerGameNew computerGameNew, BoardEvaluator boardEvaluator) {
        Map<Integer, List<Card>> rangeToReturn = new HashMap<>();

        for (Map.Entry<Integer, List<Card>> entry : previousBotRange.entrySet()) {
            Card newBotHoleCard1 = entry.getValue().get(0);
            Card newBotHoleCard2 = entry.getValue().get(1);
            List<Card> newBotHoleCards = new ArrayList<>();
            newBotHoleCards.add(newBotHoleCard1);
            newBotHoleCards.add(newBotHoleCard2);

            computerGameNew.setComputerHoleCards(newBotHoleCards);
            computerGameNew.calculateHandStrengthsAndDraws(boardEvaluator);

            String action = computerGameNew.monkeyHetComputerActionFromAiBotForBotRange();

            if(action.equals(actionDoneByBot)) {
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

    public Map<Integer, List<String>> getHsAndActionPerComboComputerGame(Map<Integer, List<Card>> botRange,
                                                                         ComputerGameNew computerGameNew,
                                                                         BoardEvaluator boardEvaluator) {
        Map<Integer, List<String>> hsAndActionPerCombo = new HashMap<>();

        for (Map.Entry<Integer, List<Card>> entry : botRange.entrySet()) {
            Card newBotHoleCard1 = entry.getValue().get(0);
            Card newBotHoleCard2 = entry.getValue().get(1);
            List<Card> newBotHoleCards = new ArrayList<>();
            newBotHoleCards.add(newBotHoleCard1);
            newBotHoleCards.add(newBotHoleCard2);

            computerGameNew.setComputerHoleCards(newBotHoleCards);
            computerGameNew.calculateHandStrengthsAndDraws(boardEvaluator);

            String action = computerGameNew.monkeyHetComputerActionFromAiBotForBotRange();
            double handStrength = computerGameNew.getComputerHandStrength();

            List<String> hsAndAction = new ArrayList<>();
            hsAndAction.add(action);
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

    public List<Double> getStrongDrawCombosCountList(Map<Integer, List<Card>> botRange, BoardEvaluator boardEvaluator) {
        List<Double> strongDrawCombos = new ArrayList<>();

        strongDrawCombos.add(0.0);
        strongDrawCombos.add(0.0);

        for (Map.Entry<Integer, List<Card>> entry : botRange.entrySet()) {
            HandEvaluator handEvaluator = new HandEvaluator(entry.getValue(), boardEvaluator);

            boolean strongFd = handEvaluator.hasDrawOfType("strongFlushDraw");
            boolean strongOosd = handEvaluator.hasDrawOfType("strongOosd");
            boolean strongGutshot = handEvaluator.hasDrawOfType("strongGutshot");
            boolean strongBackDoorFlush = handEvaluator.hasDrawOfType("strongBackDoorFlush");
            boolean strongBackDoorStraight = handEvaluator.hasDrawOfType("strongBackDoorStraight");

            if(strongFd || strongOosd || strongGutshot) {
                double oldValue = strongDrawCombos.get(0);
                double newValue = oldValue + 1;
                strongDrawCombos.set(0, newValue);
            }

            if(strongBackDoorFlush || strongBackDoorStraight) {
                double oldValue = strongDrawCombos.get(1);
                double newValue = oldValue + 1;
                strongDrawCombos.set(1, newValue);
            }
        }

        return strongDrawCombos;
    }

    public double getNumberOfStrongDrawsNonBackdoor(List<Double> strongDrawCombosCountList) {
        return strongDrawCombosCountList.get(0);
    }

    public double getNumberOfBackdoorStrongDraws(List<Double> strongDrawCombosCountList) {
        return strongDrawCombosCountList.get(1);
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

    //alleen aanroepen als je actie bet of raise is
    public Map<Integer, List<Card>> addAirToRange(Map<Integer, List<Card>> currentRange, Map<Integer, List<Card>> previousRange,
                                                  BoardEvaluator boardEvaluator) {
        Map<Integer, List<Card>> rangeToReturn = new HashMap<>();
        rangeToReturn.putAll(currentRange);

        Map<String, Integer> valueAndDraws = getValuesAndDraws(currentRange, boardEvaluator);

        int numberOfValueCombosInRange = valueAndDraws.get("value");
        int numberOfNonValueDrawsInRange = valueAndDraws.get("draw");

        int numberOfCombosToFill = numberOfValueCombosInRange - numberOfNonValueDrawsInRange;

        if(numberOfCombosToFill > 0) {
            List<List<Card>> currentRangeAsList = new ArrayList<>(currentRange.values());

            for (Map.Entry<Integer, List<Card>> entry : previousRange.entrySet()) {
                if(numberOfCombosToFill > 0) {
                    if(!currentRangeAsList.contains(entry.getValue())) {
                        HandEvaluator handEvaluator = new HandEvaluator(entry.getValue(), boardEvaluator);

                        if(handEvaluator.getHandStrength(entry.getValue()) < 0.55) {
                            boolean strongFd = handEvaluator.hasDrawOfType("strongFlushDraw");
                            boolean strongOosd = handEvaluator.hasDrawOfType("strongOosd");
                            boolean strongGutshot = handEvaluator.hasDrawOfType("strongGutshot");
                            boolean strongBackDoorFlush = handEvaluator.hasDrawOfType("strongBackDoorFlush");
                            boolean strongBackDoorStraight = handEvaluator.hasDrawOfType("strongBackDoorStraight");

                            if(!strongFd && !strongOosd && !strongGutshot && !strongBackDoorFlush && !strongBackDoorStraight) {
                                List<Card> comboToAdd = new ArrayList<>();
                                comboToAdd.addAll(entry.getValue());

                                rangeToReturn.put(rangeToReturn.size(), comboToAdd);
                                numberOfCombosToFill--;
                            }
                        }
                    }
                }
            }
        }

        return rangeToReturn;
    }



    private Map<String, Integer> getValuesAndDraws(Map<Integer, List<Card>> range, BoardEvaluator boardEvaluator) {
        Map<String, Integer> valuesAndDraws = new HashMap<>();

        valuesAndDraws.put("value", 0);
        valuesAndDraws.put("draw", 0);

        for (Map.Entry<Integer, List<Card>> entry : range.entrySet()) {
            HandEvaluator handEvaluator = new HandEvaluator(entry.getValue(), boardEvaluator);

            double handstrength = handEvaluator.getHandStrength(entry.getValue());

            if(handstrength < 0.8) {
                boolean strongFd = handEvaluator.hasDrawOfType("strongFlushDraw");
                boolean strongOosd = handEvaluator.hasDrawOfType("strongOosd");
                boolean strongGutshot = handEvaluator.hasDrawOfType("strongGutshot");
                boolean strongBackDoorFlush = handEvaluator.hasDrawOfType("strongBackDoorFlush");
                boolean strongBackDoorStraight = handEvaluator.hasDrawOfType("strongBackDoorStraight");

                if(strongFd || strongOosd || strongGutshot || strongBackDoorFlush || strongBackDoorStraight) {
                    int oldDrawValue = valuesAndDraws.get("draw");
                    valuesAndDraws.put("draw", oldDrawValue + 1);
                }
            } else {
                int oldValueValue = valuesAndDraws.get("value");
                valuesAndDraws.put("value", oldValueValue + 1);
            }
        }

        return valuesAndDraws;
    }
}
