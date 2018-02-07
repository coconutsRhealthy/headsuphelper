package com.lennart.model.action.actionbuilders.ai.opponenttypes;

import com.lennart.model.action.actionbuilders.ai.Poker;
import com.lennart.model.action.actionbuilders.ai.SimulatedHand;
import com.lennart.model.card.Card;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LPO21630 on 6-2-2018.
 */
public class DbAiActionBuilder {

    public String doAiDbAction(SimulatedHand simulatedHand) {
        try {
            String action;

            String route = getRoute(simulatedHand);
            String table = getTable(simulatedHand);

            Map<String, Double> routeData = getRouteData(route, table);

            if(!simulatedHand.isRandomContinuation()) {
                if(allOptionsHave100(simulatedHand, routeData)) {
                    action = doActionWithHighestPayoff(simulatedHand, route, table);
                } else {
                    action = pickOptionBelow100(simulatedHand, routeData);
                    simulatedHand.setRandomContinuation(true);
                }
            } else {
                action = doRandomAction(simulatedHand);
            }

            return action;
        } catch (Exception e) {
            System.out.println("Exception occurred in doAiDbAction()");
            return null;
        }
    }

    private String getRoute(SimulatedHand simulatedHand) {
        String street = simulatedHand.getStreet();
        boolean aiBotIsButton = simulatedHand.isAiBotIsButton();
        double potSizeBb = simulatedHand.getPotSizeInBb();
        String ruleBotAction = simulatedHand.getRuleBotAction();
        double facingOdds = simulatedHand.getFacingOdds();
        double effectiveStackBb = simulatedHand.getEffectiveStackInBb();
        boolean aiBotHasStrongDraw = simulatedHand.isAiBotHasStrongDraw();

        String route = new Poker().getRoute(street, aiBotIsButton, potSizeBb, ruleBotAction, facingOdds, effectiveStackBb, aiBotHasStrongDraw);

        return route;
    }

    private String getTable(SimulatedHand simulatedHand) {
        double handStrength = simulatedHand.getAiBotHandStrength();
        AbstractOpponent opponent = simulatedHand.getRuleBot();
        Poker poker = new Poker();
        String opponentTypeString = poker.getOpponentTypeString(opponent);
        return new Poker().getTableString(handStrength, opponentTypeString);
    }

    private Map<String, Double> getRouteData(String route, String table) throws Exception {
        return new Poker().retrieveRouteDataFromDb(route, table);
    }

    private boolean allOptionsHave100(SimulatedHand simulatedHand, Map<String, Double> routeData) {
        boolean allOptionsHave100 = true;

        List<String> eligibleActions = getEligibleActions(simulatedHand);
        eligibleActions = addPartToStringInList(eligibleActions, "_times");
        Map<String, Double> mapToUse = new HashMap<>();

        for (Map.Entry<String, Double> entry : routeData.entrySet()) {
            if(eligibleActions.contains(entry.getKey())) {
                mapToUse.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Double> entry : mapToUse.entrySet()) {
            if(entry.getValue() < 100) {
                allOptionsHave100 = false;
                break;
            }
        }

        return allOptionsHave100;
    }

    private String pickOptionBelow100(SimulatedHand simulatedHand, Map<String, Double> routeData) {
        String optionBelow100 = null;

        List<String> eligibleActions = getEligibleActions(simulatedHand);
        eligibleActions = addPartToStringInList(eligibleActions, "_times");
        Map<String, Double> mapToUse = new HashMap<>();

        for (Map.Entry<String, Double> entry : routeData.entrySet()) {
            if(eligibleActions.contains(entry.getKey())) {
                mapToUse.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Double> entry : mapToUse.entrySet()) {
            if(entry.getValue() < 100) {
                optionBelow100 = entry.getKey();
                break;
            }
        }

        optionBelow100 = optionBelow100.replace("_times", "");

        return optionBelow100;
    }

    private List<String> addPartToStringInList(List<String> list, String part) {
        for(int i = 0; i < list.size(); i++) {
            String appendedString = list.get(i) + part;
            list.set(i, appendedString);
        }
        return list;
    }

    private String doRandomAction(SimulatedHand simulatedHand) {
        String randomAction;

        String ruleBotAction = simulatedHand.getRuleBotAction();
        double ruleBotStack = simulatedHand.getRuleBotStack();
        double aiBotStack = simulatedHand.getAiBotStack();
        double aiBotBetSize = simulatedHand.getAiBotBetSize();
        double ruleBotBetSize = simulatedHand.getRuleBotBetSize();
        List<Card> board = simulatedHand.getBoard();
        boolean aiBotIsButton = simulatedHand.isAiBotIsButton();
        double bigBlind = simulatedHand.getBigBlind();

        if(ruleBotAction.contains("bet") || ruleBotAction.contains("raise")) {
            double random = Math.random();

            if(ruleBotStack == 0 || ((aiBotStack + aiBotBetSize) <= ruleBotBetSize)) {
                if(random < 0.5) {
                    randomAction = "fold";
                } else {
                    randomAction = "call";
                }
            } else {
                if(random < 0.333) {
                    randomAction = "fold";
                } else if(random < 0.666){
                    randomAction = "call";
                } else {
                    randomAction = "raise";
                }
            }
        } else {
            double random = Math.random();

            if(random < 0.5) {
                randomAction = "check";
            } else {
                if(board.isEmpty() && !aiBotIsButton && aiBotBetSize / bigBlind == 1 && ruleBotBetSize / bigBlind == 1) {
                    randomAction = "raise";
                } else {
                    randomAction = "bet75pct";
                }
            }
        }

        return randomAction;
    }

    private String doActionWithHighestPayoff(SimulatedHand simulatedHand, String route, String table) {
        List<String> eligibleActions = getEligibleActions(simulatedHand);
        return new Poker().getAction(route, table, eligibleActions);
    }

    private List<String> getEligibleActions(SimulatedHand simulatedHand) {
        double aiBotBetSize = simulatedHand.getAiBotBetSize();
        double ruleBotBetSize = simulatedHand.getRuleBotBetSize();
        List<Card> board = simulatedHand.getBoard();
        boolean aiBotIsButton = simulatedHand.isAiBotIsButton();
        double bigBlind = simulatedHand.getBigBlind();

        List<String> eligibleActions;

        if(simulatedHand.getRuleBotAction().contains("bet") || simulatedHand.getRuleBotAction().contains("raise")) {
            if(simulatedHand.getRuleBotStack() == 0 || ((simulatedHand.getAiBotStack() + simulatedHand.getAiBotBetSize()) <= simulatedHand.getRuleBotBetSize())) {
                eligibleActions = Arrays.asList("fold", "call");
            } else {
                eligibleActions = Arrays.asList("fold", "call", "raise");
            }
        } else {
            if(board.isEmpty() && !aiBotIsButton && aiBotBetSize / bigBlind == 1 && ruleBotBetSize / bigBlind == 1) {
                eligibleActions = Arrays.asList("check", "raise");
            } else {
                eligibleActions = Arrays.asList("check", "bet75pct");
            }
        }

        return eligibleActions;
    }
}
