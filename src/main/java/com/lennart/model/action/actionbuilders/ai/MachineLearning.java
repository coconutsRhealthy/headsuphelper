package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MachineLearning {

    private Connection con;

    private String adjustActionToDbSaveData(String action) {
        String actionToReturn = "";

//        if(action.equals("fold")) {
//            actionToReturn = adjustFoldAction();
//        } else if(action.equals("call")) {
//            actionToReturn = adjustCallAction();
//        } else if(action.equals("check")) {
//            actionToReturn = adjustCheckAction();
//        } else if(action.equals("bet75pct")) {
//            actionToReturn = adjustBetAction();
//        } else if(action.equals("raise")) {
//            actionToReturn = adjustRaiseAction();
//        }

        return actionToReturn;
    }

    private String adjustCheckAction(String action, double handStrength, String route, boolean opponentHasInitiative,
                                     List<Card> board, boolean position) throws Exception {
        String actionToReturn;

        if(!opponentHasInitiative) {
            if(handStrength < 0.7) {
                if(bluffOddsAreOk(0, 0, 0, 0)) {
                    List<Double> bluffBetData = getDataFromDb("dbstats_bluff", route);
                    actionToReturn = getActionFromData(bluffBetData, action, "bet75pct");
                } else {
                    actionToReturn = action;
                }
            } else {
                if(board != null && board.size() == 5) {
                    if(position) {
                        List<Double> valueBetData = getDataFromDb("dbstats_value", route);
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
//
//    private String adjustFoldAction() {
//
//    }
//
//    private String adjustBetAction() {
//
//    }
//
//    private String adjustBluffBetAction() {
//
//    }
//
//    private String adjustValueBetAction() {
//
//    }
//
//    private String adjustRaiseAction() {
//
//    }
//
//    private String adjustBluffRaiseAction() {
//
//    }
//
//    private String adjustValueRaiseAction() {
//
//    }
//
//    private String adjustCallAction() {
//
//    }

    private String getActionFromData(List<Double> data, String currentAction, String actionInConsideration) {
        String actionToReturn;

        double successNumber = data.get(0);
        double totalNumber = data.get(1);

        if(totalNumber >= 20) {
            double successRatio = successNumber / totalNumber;

            if(successRatio >= 0.55) {
                actionToReturn = actionInConsideration;
            } else {
                actionToReturn = currentAction;
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

        //valuesToReturn.add(successNumber, totalNumber);

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
