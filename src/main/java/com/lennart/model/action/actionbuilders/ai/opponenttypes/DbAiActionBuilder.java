package com.lennart.model.action.actionbuilders.ai.opponenttypes;

import com.lennart.model.action.actionbuilders.ai.Poker;
import com.lennart.model.action.actionbuilders.ai.SimulatedHand;
import com.lennart.model.card.Card;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by LPO21630 on 6-2-2018.
 */
public class DbAiActionBuilder {

    private Connection con;

    //actie

    //vul alles tot 100 qua aantal



    //als alles 100 of hoger, kies degene met beste payoff

    //als een van de opties onder de 100, kies dan een optie onder de 100

    //rest van de hand moet random




    public String doAiDbAction(SimulatedHand simulatedHand) throws Exception {
        String action;

        String route = getRoute(simulatedHand);
        String table = getTable(simulatedHand);

        Map<String, Double> routeData = getRouteData(route, table);

        if(!simulatedHand.isRandomContinuation()) {
            if(allOptionsHave100(routeData)) {
                action = doActionWithHighestPayoff(simulatedHand, route, table);
            } else {
                action = pickOptionBelow100(routeData);
                simulatedHand.setRandomContinuation(true);
            }
        } else {
            action = doRandomAction(simulatedHand);
        }

        return action;
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
        String tableString = new Poker().getTableString(handStrength, "");
        return tableString;
    }

    private Map<String, Double> getRouteData(String route, String table) throws Exception {
        return new Poker().retrieveRouteDataFromDb(route, table);
    }

    private boolean allOptionsHave100(Map<String, Double> routeData) {
        return false;
    }

    private String pickOptionBelow100(Map<String, Double> routeData) {
        return null;
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
        String action;

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

        action = new Poker().getAction(route, table, eligibleActions);
        return action;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/poker", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

}
