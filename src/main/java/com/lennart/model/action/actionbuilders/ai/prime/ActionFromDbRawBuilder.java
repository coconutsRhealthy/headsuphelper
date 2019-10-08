package com.lennart.model.action.actionbuilders.ai.prime;

import com.lennart.model.action.actionbuilders.ai.ActionVariables;
import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.action.actionbuilders.ai.Poker;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LennartMac on 07/10/2019.
 */
public class ActionFromDbRawBuilder {

    private Connection con;

    private String getAction(GameVariables gameVariables) throws Exception {
        String query = "";

        Map<String, Double> dbRawPayoffData = retrieveDbRawPayoffData(query);

        Poker poker = new Poker();
        Map<String, Double> sortedAveragePayoffMap = poker.getSortedAveragePayoffMapFromRouteData(dbRawPayoffData);
        sortedAveragePayoffMap = removeActionsWithTooLittleData(sortedAveragePayoffMap, dbRawPayoffData);
        List<String> eligibleActions = new ActionVariables().getEligibleActions(gameVariables);
        Map<String, Double> sortedEligibleActions = poker.retainOnlyEligibleActions(sortedAveragePayoffMap, eligibleActions);

        String action = sortedEligibleActions.entrySet().iterator().next().getKey();

        return action;
    }

    private Map<String, Double> retrieveDbRawPayoffData(String query) throws Exception {
        Map<String, Double> dbRawPayoffData = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);

        double totalFoldPayoff = 0;
        double totalFoldCounter = 0;
        double totalCheckPayoff = 0;
        double totalCheckCounter = 0;
        double totalCallPayoff = 0;
        double totalCallCounter = 0;
        double totalBetPayoff = 0;
        double totalBetCounter = 0;
        double totalRaisePayoff = 0;
        double totalRaiseCounter = 0;

        while(rs.next()) {
            String botAction = rs.getString("bot_action");
            double arWinnings = rs.getDouble("ar_winnings");

            if(botAction.equals("fold")) {
                totalFoldPayoff = totalFoldPayoff + arWinnings;
                totalFoldCounter++;
            } else if(botAction.equals("check")) {
                totalCheckPayoff = totalCheckPayoff + arWinnings;
                totalCheckCounter++;
            } else if(botAction.equals("call")) {
                totalCallPayoff = totalCallPayoff + arWinnings;
                totalCallCounter++;
            } else if(botAction.equals("bet75pct")) {
                totalBetPayoff = totalBetPayoff + arWinnings;
                totalBetCounter++;
            } else if(botAction.equals("raise")) {
                totalRaisePayoff = totalRaisePayoff + arWinnings;
                totalRaiseCounter++;
            }
        }

        rs.close();
        st.close();
        closeDbConnection();

        dbRawPayoffData.put("fold_payoff", totalFoldPayoff);
        dbRawPayoffData.put("fold_times", totalFoldCounter);
        dbRawPayoffData.put("check_payoff", totalCheckPayoff);
        dbRawPayoffData.put("check_times", totalCheckCounter);
        dbRawPayoffData.put("call_payoff", totalCallPayoff);
        dbRawPayoffData.put("call_times", totalCallCounter);
        dbRawPayoffData.put("bet75pct_payoff", totalBetPayoff);
        dbRawPayoffData.put("bet75pct_times", totalBetCounter);
        dbRawPayoffData.put("raise_payoff", totalRaisePayoff);
        dbRawPayoffData.put("raise_times", totalRaiseCounter);

        return dbRawPayoffData;
    }

    private Map<String, Double> removeActionsWithTooLittleData(Map<String, Double> sortedAveragePayoffMap,
                                                               Map<String, Double> dbRawPayoffData) {
        if(dbRawPayoffData.get("fold_times") < 10) {
            sortedAveragePayoffMap.remove("fold");
        }

        if(dbRawPayoffData.get("check_times") < 10) {
            sortedAveragePayoffMap.remove("check");
        }

        if(dbRawPayoffData.get("call_times") < 10) {
            sortedAveragePayoffMap.remove("call");
        }

        if(dbRawPayoffData.get("bet75pct_times") < 10) {
            sortedAveragePayoffMap.remove("bet75pct");
        }

        if(dbRawPayoffData.get("raise") < 10) {
            sortedAveragePayoffMap.remove("raise");
        }

        return sortedAveragePayoffMap;
    }

    private boolean actionShouldBeDefaultCheck() {
        //work with setOpponentHasInitiative() in ActionVariables

        return false;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

}
