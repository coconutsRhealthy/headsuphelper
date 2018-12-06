//package com.lennart.model.action.actionbuilders.ai;
//
//import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveBluff;
//import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveCall;
//import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveValue;
//import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class MachineLearning {
//
//    private Connection con;
//
//    public String adjustActionToDbSaveData(ActionVariables actionVariables, GameVariables gameVariables) {
//        String actionToReturn = "";
//
//        String currentAction = actionVariables.getAction();
//
//        if(currentAction.equals("fold")) {
//            actionToReturn = adjustFoldAction();
//        } else if(currentAction.equals("call")) {
//            actionToReturn = adjustCallAction();
//        } else if(currentAction.equals("check")) {
//            actionToReturn = adjustCheckAction();
//        } else if(currentAction.equals("bet75pct")) {
//            actionToReturn = adjustBetAction();
//        } else if(currentAction.equals("raise")) {
//            actionToReturn = adjustRaiseAction();
//        }
//
//        return actionToReturn;
//    }
//
//    private String adjustCheckAction(ActionVariables actionVariables, GameVariables gameVariables,
//                                     boolean opponentHasInitiative, String dbTable) throws Exception {
//        String actionToReturn;
//
//        if(!opponentHasInitiative) {
//            if(actionVariables.getBotHandStrength() < 0.7) {
//                if(bluffOddsAreOk(actionVariables.getSizing(), gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(), gameVariables.getPot())) {
//                    String route = calculateBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct");
//                    List<Double> bluffBetData = getDataFromDb(dbTable, route);
//                    actionToReturn = changeToBetOrKeepCheckGivenData(bluffBetData);
//                } else {
//                    actionToReturn = actionVariables.getAction();
//                }
//            } else {
//                if(gameVariables.getBoard() != null && gameVariables.getBoard().size() == 5) {
//                    if(gameVariables.isBotIsButton()) {
//                        String route = calculateValueBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct");
//                        List<Double> valueBetData = getDataFromDb(dbTable, route);
//                        actionToReturn = changeToBetOrKeepCheckGivenData(valueBetData);
//                    } else {
//                        actionToReturn = actionVariables.getAction();
//                    }
//                } else {
//                    actionToReturn = actionVariables.getAction();
//                }
//            }
//        } else {
//            actionToReturn = actionVariables.getAction();
//        }
//
//        return actionToReturn;
//    }
//
//    private String adjustFoldAction(ActionVariables actionVariables, GameVariables gameVariables, String dbTable) {
//        String actionToReturn;
//
//
//
//
//
//
//        //check hier het succes van call en het succes van raise in deze spots...
//
//        //change naar degene met hoogste success ratio...
//
//
//
//
//
//
//    }
//
//    private String adjustBetAction(ActionVariables actionVariables, GameVariables gameVariables, String dbTable) throws Exception {
//        String actionToReturn;
//
//        String route;
//
//        if(actionVariables.getBotHandStrength() < 0.75) {
//            route = calculateBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct");
//        } else {
//            route = calculateValueBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct");
//        }
//
//        List<Double> betData = getDataFromDb(dbTable, route);
//        double ratio = betData.get(0) / betData.get(1);
//
//        if(ratio < 0.55) {
//            double random = Math.random();
//
//            if(random < 0.75) {
//                actionToReturn = "check";
//            } else {
//                actionToReturn = "bet75pct";
//            }
//        } else {
//            actionToReturn = "bet75pct";
//        }
//
//        return actionToReturn;
//    }
//
//    private String adjustRaiseAction() {
//        //verander naar ofwel call of fold...
//    }
//
//    private String adjustCallAction() {
//        //verander naar ofwel fold of raise...
//    }
//
//    private String changeToBetOrKeepCheckGivenData(List<Double> data) {
//        String actionToReturn;
//
//        double successNumber = data.get(0);
//        double totalNumber = data.get(1);
//
//        if(totalNumber >= 20) {
//            double successRatio = successNumber / totalNumber;
//
//            if(successRatio >= 0.55) {
//                double random = Math.random();
//
//                if(random < 0.75) {
//                    actionToReturn = "bet75pct";
//                } else {
//                    actionToReturn = "check";
//                }
//            } else {
//                actionToReturn = "check";
//            }
//        } else {
//            actionToReturn = "check";
//        }
//
//        return actionToReturn;
//    }
//
//    private List<Double> getDataFromDb(String database, String route) throws Exception {
//        List<Double> valuesToReturn = new ArrayList<>();
//
//        initializeDbConnection();
//
//        Statement st = con.createStatement();
//        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE route = '" + route + "';");
//
//        rs.next();
//
//        double successNumber = rs.getDouble("success");
//        double totalNumber = rs.getDouble("total");
//
//        valuesToReturn.add(successNumber);
//        valuesToReturn.add(totalNumber);
//
//        rs.close();
//        st.close();
//
//        closeDbConnection();
//
//        return valuesToReturn;
//    }
//
//    private String calculateBluffBetOrRaiseRoute(ActionVariables actionVariables, GameVariables gameVariables, String actionToConsider) throws Exception {
//        if(actionToConsider.equals("bet75pct")) {
//            actionToConsider = "Bet";
//        } else {
//            actionToConsider = "Raise";
//        }
//
//        DbSaveBluff dbSaveBluff = new DbSaveBluff();
//
//        String street = dbSaveBluff.getStreetViaLogic(gameVariables.getBoard());
//        String bluffAction = actionToConsider;
//        String position = dbSaveBluff.getPositionLogic(gameVariables.isBotIsButton());
//        String sizingGroup = dbSaveBluff.getSizingGroupViaLogic(actionVariables.getSizing() / gameVariables.getBigBlind());
//        String foldStatGroup = dbSaveBluff.getFoldStatGroupLogic(new FoldStatsKeeper().getFoldStatFromDb(gameVariables.getOpponentName()));
//        String effectiveStackString = dbSaveBluff.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(), gameVariables.getOpponentStack() / gameVariables.getBigBlind());
//        String handStrength = dbSaveBluff.getHandStrengthLogic(actionVariables.getBotHandStrength());
//        String drawWetnessString = dbSaveBluff.getDrawWetnessLogic(gameVariables.getBoard(), actionVariables.getBoardEvaluator().getFlushStraightWetness());
//        String boatWetnessString = dbSaveBluff.getBoatWetnessLogic(gameVariables.getBoard(), actionVariables.getBoardEvaluator().getBoatWetness());
//        String strongDraw = dbSaveBluff.getStrongDrawLogic(actionVariables.getHandEvaluator().hasDrawOfType("strongFlushDraw"), actionVariables.getHandEvaluator().hasDrawOfType("strongOosd"));
//
//        String route = street + bluffAction + position + sizingGroup + foldStatGroup + effectiveStackString +
//                handStrength + drawWetnessString + boatWetnessString + strongDraw;
//
//        return route;
//    }
//
//    private String calculateValueBetOrRaiseRoute(ActionVariables actionVariables, GameVariables gameVariables, String actionToConsider) throws Exception {
//        if(actionToConsider.equals("bet75pct")) {
//            actionToConsider = "Bet";
//        } else {
//            actionToConsider = "Raise";
//        }
//
//        DbSaveValue dbSaveValue = new DbSaveValue();
//
//        String street = dbSaveValue.getStreetViaLogic(gameVariables.getBoard());
//        String valueAction = actionToConsider;
//        String postion = dbSaveValue.getPositionLogic(gameVariables.isBotIsButton());
//        String sizingGroup = dbSaveValue.getSizingGroupViaLogic(actionVariables.getSizing() / gameVariables.getBigBlind());
//        String oppLoosenessGroup = dbSaveValue.getOppLoosenessGroupViaLogic(gameVariables.getOpponentName());
//        String handStrength = dbSaveValue.getHandStrengthLogic(actionVariables.getBotHandStrength());
//        String strongDraw = dbSaveValue.getStrongDrawLogic(actionVariables.getHandEvaluator().hasDrawOfType("strongFlushDraw"), actionVariables.getHandEvaluator().hasDrawOfType("strongOosd"));
//        String effectiveStackString = dbSaveValue.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(), gameVariables.getOpponentStack() / gameVariables.getBigBlind());
//        String drawWetnessString = dbSaveValue.getDrawWetnessLogic(gameVariables.getBoard(), actionVariables.getBoardEvaluator().getFlushStraightWetness());
//        String boatWetnessString = dbSaveValue.getBoatWetnessLogic(gameVariables.getBoard(), actionVariables.getBoardEvaluator().getBoatWetness());
//
//        String route = street + valueAction + postion + sizingGroup + oppLoosenessGroup + handStrength + strongDraw +
//                effectiveStackString + drawWetnessString + boatWetnessString;
//
//        return route;
//    }
//
//    private String calculateCallRoute(ActionVariables actionVariables, GameVariables gameVariables) throws Exception {
//        double amountToCallBb = gameVariables.getOpponentBetSize() - gameVariables.getBotBetSize();
//
//        if(amountToCallBb > gameVariables.getBotStack()) {
//            amountToCallBb = gameVariables.getBotStack();
//        }
//
//        amountToCallBb = amountToCallBb / gameVariables.getBigBlind();
//
//        DbSaveCall dbSaveCall = new DbSaveCall();
//
//        String street = dbSaveCall.getStreetViaLogic(gameVariables.getBoard());
//        String facingAction = dbSaveCall.getFacingActionViaLogic(gameVariables.getOpponentAction());
//        String postion = dbSaveCall.getPositionLogic(gameVariables.isBotIsButton());
//        String amountToCallGroup = dbSaveCall.getAmountToCallViaLogic(amountToCallBb);
//        String oppAggroGroup = dbSaveCall.getOppAggroGroupViaLogic(gameVariables.getOpponentName());
//        String handStrength = dbSaveCall.getHandStrengthLogic(actionVariables.getBotHandStrength());
//        String strongDraw = dbSaveCall.getStrongDrawLogic(actionVariables.getHandEvaluator().hasDrawOfType("strongFlushDraw"), actionVariables.getHandEvaluator().hasDrawOfType("strongOosd"));
//        String effectiveStackString = dbSaveCall.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(), gameVariables.getOpponentStack() / gameVariables.getBigBlind());
//        String drawWetnessString = dbSaveCall.getDrawWetnessLogic(gameVariables.getBoard(), actionVariables.getBoardEvaluator().getFlushStraightWetness());
//        String boatWetnessString = dbSaveCall.getBoatWetnessLogic(gameVariables.getBoard(), actionVariables.getBoardEvaluator().getBoatWetness());
//
//        String route = street + facingAction + postion + amountToCallGroup + oppAggroGroup + handStrength + strongDraw +
//                effectiveStackString + drawWetnessString + boatWetnessString;
//
//        return route;
//    }
//
//    private boolean bluffOddsAreOk(double sizing, double facingBetSize, double facingStackSize, double pot) {
//        double sizingInMethod;
//
//        if(sizing > (facingBetSize + facingStackSize)) {
//            sizingInMethod = facingBetSize + facingStackSize;
//        } else {
//            sizingInMethod = sizing;
//        }
//
//        double odds = (sizingInMethod - facingBetSize) / (facingBetSize + sizingInMethod + pot);
//        return odds > 0.36;
//    }
//
//    private void initializeDbConnection() throws Exception {
//        Class.forName("com.mysql.jdbc.Driver").newInstance();
//        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
//    }
//
//    private void closeDbConnection() throws SQLException {
//        con.close();
//    }
//}
