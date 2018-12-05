package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MachineLearning {

    private Connection con;

    private String adjustActionToDbSaveData(String action) {
        String actionToReturn = "";

        if(action.equals("fold")) {
            actionToReturn = adjustFoldAction();
        } else if(action.equals("call")) {
            actionToReturn = adjustCallAction();
        } else if(action.equals("check")) {
            actionToReturn = adjustCheckAction();
        } else if(action.equals("bet75pct")) {
            actionToReturn = adjustBetAction();
        } else if(action.equals("raise")) {
            actionToReturn = adjustRaiseAction();
        }

        return actionToReturn;
    }

    private String adjustCheckAction(String action, double handStrength, String route, boolean opponentHasInitiative,
                                     List<Card> board, boolean position, String dbTable) throws Exception {
        String actionToReturn;

        if(!opponentHasInitiative) {
            if(handStrength < 0.7) {
                if(bluffOddsAreOk(0, 0, 0, 0)) {
                    List<Double> bluffBetData = getDataFromDb(dbTable, route);
                    actionToReturn = getActionFromData(bluffBetData, action, "bet75pct");
                } else {
                    actionToReturn = action;
                }
            } else {
                if(board != null && board.size() == 5) {
                    if(position) {
                        List<Double> valueBetData = getDataFromDb(dbTable, route);
                        actionToReturn = getActionFromData(valueBetData, action, "bet75pct");
                    } else {
                        actionToReturn = action;
                    }
                } else {
                    actionToReturn = action;
                }
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    private String adjustFoldAction(String route) {
        String actionToReturn;

        //check hier het succes van call en het succes van raise in deze spots...

        //change naar degene met hoogste success ratio...






    }

    private String adjustBetAction(String route, String dbTable) throws Exception {
        String actionToReturn;

        List<Double> betData = getDataFromDb(dbTable, route);
        double ratio = betData.get(0) / betData.get(1);

        if(ratio < 0.55) {
            double random = Math.random();

            if(random < 0.75) {
                actionToReturn = "check";
            } else {
                actionToReturn = "bet75pct";
            }
        } else {
            actionToReturn = "bet75pct";
        }

        return actionToReturn;
    }

    private String adjustRaiseAction() {
        //verander naar ofwel call of fold...
    }

    private String adjustCallAction() {
        //verander naar ofwel fold of raise...
    }

    private String getActionFromData(List<Double> data, String currentAction, String actionInConsideration) {
        String actionToReturn;

        double successNumber = data.get(0);
        double totalNumber = data.get(1);

        if(totalNumber >= 20) {
            double successRatio = successNumber / totalNumber;

            if(actionInConsideration.equals("bet75pct") || actionInConsideration.equals("raise")) {
                if(successRatio >= 0.55) {
                    //if(currentAction.equals("call") || currentAction.equals("bet75pct") || currentAction.equals("raise"))

                    actionToReturn = actionInConsideration;
                } else {
                    actionToReturn = currentAction;
                }
            } else if(currentAction.equals("bet75pct") || currentAction.equals("raise")) {
                if(successRatio < 0.5)

            }

        } else {
            actionToReturn = currentAction;
        }

        return actionToReturn;
    }

    private List<Double> getDataFromDb(String database, String route) throws Exception {
        List<Double> valuesToReturn = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE route = '" + route + "';");

        rs.next();

        double successNumber = rs.getDouble("success");
        double totalNumber = rs.getDouble("total");

        valuesToReturn.add(successNumber);
        valuesToReturn.add(totalNumber);

        rs.close();
        st.close();

        closeDbConnection();

        return valuesToReturn;
    }

    private boolean bluffOddsAreOk(double sizing, double facingBetSize, double facingStackSize, double pot) {
        double sizingInMethod;

        if(sizing > (facingBetSize + facingStackSize)) {
            sizingInMethod = facingBetSize + facingStackSize;
        } else {
            sizingInMethod = sizing;
        }

        double odds = (sizingInMethod - facingBetSize) / (facingBetSize + sizingInMethod + pot);
        return odds > 0.36;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
