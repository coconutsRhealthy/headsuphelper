package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePersisterPreflop;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePreflopCall;

import java.sql.*;

public class MachineLearningPreflop {

    private Connection con;

    public String adjustActionToDbSaveData(ActionVariables actionVariables, GameVariables gameVariables) throws Exception {
        String actionToReturn = actionVariables.getAction();

        if(actionToReturn.equals("call")) {
            actionToReturn = adjustCallAction(actionVariables.getAction(), gameVariables);
        }

        return actionToReturn;
    }

    private String adjustCallAction(String action, GameVariables gameVariables) throws Exception {
        String actionToReturn;

        if(!gameVariables.isBotIsButton()) {
            String route = calculateCallRoute(gameVariables);

            initializeDbConnection();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM dbstats_pf_call_sng_compact WHERE route = '" + route + "';");

            rs.next();

            if(rs.getDouble("total") >= 15) {
                double success = rs.getDouble("success");
                double total = rs.getDouble("total");

                if(success / total < 0.5) {
                    actionToReturn = "fold";
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }

            rs.close();
            st.close();

            closeDbConnection();
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    private String calculateCallRoute(GameVariables gameVariables) throws Exception {
        double amountToCallBb = gameVariables.getOpponentBetSize() - gameVariables.getBotBetSize();

        if(amountToCallBb > gameVariables.getBotStack()) {
            amountToCallBb = gameVariables.getBotStack();
        }

        amountToCallBb = amountToCallBb / gameVariables.getBigBlind();

        DbSavePreflopCall dbSavePreflopCall = new DbSavePreflopCall();

        String handStrength = new DbSavePersisterPreflop().convertListCardToHandStrengthString(gameVariables.getBotHoleCards());
        String position = dbSavePreflopCall.getPositionLogic(gameVariables.isBotIsButton());
        String amountToCallGroup = dbSavePreflopCall.getAmountToCallViaLogic(amountToCallBb);
        String oppAggroGroup = dbSavePreflopCall.getOppAggroGroupViaLogic(gameVariables.getOpponentName());
        String effectiveStack = dbSavePreflopCall.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(), gameVariables.getOpponentStack() / gameVariables.getBigBlind());

        String route = handStrength + position + amountToCallGroup + oppAggroGroup + effectiveStack;

        return route;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
