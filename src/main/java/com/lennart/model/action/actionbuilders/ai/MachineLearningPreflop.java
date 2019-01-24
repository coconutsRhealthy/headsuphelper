package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePersisterPreflop;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePreflopCall;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePreflopRaise;
import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;

import java.sql.*;

public class MachineLearningPreflop {

    private Connection con;

    public String adjustActionToDbSaveData(ActionVariables actionVariables, GameVariables gameVariables, double sizing) throws Exception {
        String actionToReturn = actionVariables.getAction();

        if(actionToReturn.equals("call")) {
            actionToReturn = adjustCallAction(actionVariables.getAction(), gameVariables, actionVariables);
        } else if(actionToReturn.equals("raise")) {
            actionToReturn = adjustRaiseAction(actionVariables.getAction(), gameVariables, actionVariables, sizing);
        }

        doMachineLearningLogging(actionToReturn, gameVariables, sizing);

        return actionToReturn;
    }

    private void doMachineLearningLogging(String action, GameVariables gameVariables, double sizing) throws Exception {
        initializeDbConnection();

        if(action.equals("fold")) {
            String callRoute = calculateCallRoute(gameVariables);
            String raiseRoute = calculateRaiseRoute(gameVariables, sizing);

            Statement st1 = con.createStatement();
            ResultSet rs1 = st1.executeQuery("SELECT * FROM dbstats_pf_call_sng_compact WHERE route = '" + callRoute + "';");

            rs1.next();

            double callSuccess = rs1.getDouble("success");
            double callTotal = rs1.getDouble("total");
            double callRatio = callSuccess / callTotal;

            rs1.close();
            st1.close();

            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_pf_raise_sng_compact WHERE route = '" + raiseRoute + "';");

            rs2.next();

            double raiseSuccess = rs2.getDouble("success");
            double raiseTotal = rs2.getDouble("total");
            double raiseRatio = raiseSuccess / raiseTotal;

            rs2.close();
            st2.close();

            System.out.println();
            System.out.println();
            System.out.println("***** Preflop Machine Learning logging *****");
            System.out.println("Action is: FOLD");
            System.out.println("Callroute success: " + callSuccess);
            System.out.println("Callroute total: " + callTotal);
            System.out.println("Callroute ratio: " + callRatio);
            System.out.println("Callroute: " + callRoute);
            System.out.println();
            System.out.println("Raiseroute success: " + raiseSuccess);
            System.out.println("Raiseroute total: " + raiseTotal);
            System.out.println("Raiseroute ratio: " + raiseRatio);
            System.out.println("Raiseroute: " + raiseRoute);

            if((callRatio >= 0.5 && callTotal >= 10) || (raiseRatio >= 0.5 && raiseTotal >= 10)) {
                System.out.println("May be of interest Preflop Machine Learning logging!");
            }

            System.out.println("*****************");
            System.out.println();
            System.out.println();
        } else if(action.equals("call")) {
            String callRoute = calculateCallRoute(gameVariables);
            String raiseRoute = calculateRaiseRoute(gameVariables, sizing);

            Statement st1 = con.createStatement();
            ResultSet rs1 = st1.executeQuery("SELECT * FROM dbstats_pf_call_sng_compact WHERE route = '" + callRoute + "';");

            rs1.next();

            double callSuccess = rs1.getDouble("success");
            double callTotal = rs1.getDouble("total");
            double callRatio = callSuccess / callTotal;

            rs1.close();
            st1.close();

            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_pf_raise_sng_compact WHERE route = '" + raiseRoute + "';");

            rs2.next();

            double raiseSuccess = rs2.getDouble("success");
            double raiseTotal = rs2.getDouble("total");
            double raiseRatio = raiseSuccess / raiseTotal;

            rs2.close();
            st2.close();

            System.out.println();
            System.out.println();
            System.out.println("***** Preflop Machine Learning logging *****");
            System.out.println("Action is: CALL");
            System.out.println("Callroute success: " + callSuccess);
            System.out.println("Callroute total: " + callTotal);
            System.out.println("Callroute ratio: " + callRatio);
            System.out.println("Callroute: " + callRoute);
            System.out.println();
            System.out.println("Raiseroute success: " + raiseSuccess);
            System.out.println("Raiseroute total: " + raiseTotal);
            System.out.println("Raiseroute ratio: " + raiseRatio);
            System.out.println("Raiseroute: " + raiseRoute);

            if(callRatio < 0.5 && callTotal >= 10) {
                System.out.println("May be of interest Preflop Machine Learning logging!");
            }

            System.out.println("*****************");
            System.out.println();
            System.out.println();
        } else if(action.equals("check")) {
            String raiseRoute = calculateRaiseRoute(gameVariables, sizing);

            Statement st1 = con.createStatement();
            ResultSet rs1 = st1.executeQuery("SELECT * FROM dbstats_pf_raise_sng_compact WHERE route = '" + raiseRoute + "';");

            rs1.next();

            double raiseSuccess = rs1.getDouble("success");
            double raiseTotal = rs1.getDouble("total");
            double raiseRatio = raiseSuccess / raiseTotal;

            rs1.close();
            st1.close();

            System.out.println();
            System.out.println();
            System.out.println("***** Preflop Machine Learning logging *****");
            System.out.println("Action is: CHECK");
            System.out.println("Raiseroute success: " + raiseSuccess);
            System.out.println("Raiseroute total: " + raiseTotal);
            System.out.println("Raiseroute ratio: " + raiseRatio);
            System.out.println("Raiseroute: " + raiseRoute);
            System.out.println();

            if(raiseRatio >= 0.5 && raiseTotal >= 10) {
                System.out.println("May be of interest Preflop Machine Learning logging!");
            }

            System.out.println("*****************");
            System.out.println();
            System.out.println();
        } else if(action.equals("raise")) {
            String callRoute = calculateCallRoute(gameVariables);
            String raiseRoute = calculateRaiseRoute(gameVariables, sizing);

            Statement st1 = con.createStatement();
            ResultSet rs1 = st1.executeQuery("SELECT * FROM dbstats_pf_call_sng_compact WHERE route = '" + callRoute + "';");

            rs1.next();

            double callSuccess = rs1.getDouble("success");
            double callTotal = rs1.getDouble("total");
            double callRatio = callSuccess / callTotal;

            rs1.close();
            st1.close();

            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_pf_raise_sng_compact WHERE route = '" + raiseRoute + "';");

            rs2.next();

            double raiseSuccess = rs2.getDouble("success");
            double raiseTotal = rs2.getDouble("total");
            double raiseRatio = raiseSuccess / raiseTotal;

            rs2.close();
            st2.close();

            System.out.println();
            System.out.println();
            System.out.println("***** Preflop Machine Learning logging *****");
            System.out.println("Action is: RAISE");
            System.out.println("Raiseroute success: " + raiseSuccess);
            System.out.println("Raiseroute total: " + raiseTotal);
            System.out.println("Raiseroute ratio: " + raiseRatio);
            System.out.println("Raiseroute: " + raiseRoute);
            System.out.println();
            System.out.println("Callroute success: " + callSuccess);
            System.out.println("Callroute total: " + callTotal);
            System.out.println("Callroute ratio: " + callRatio);
            System.out.println("Callroute: " + callRoute);

            if(raiseRatio < 0.5 && raiseTotal >= 10) {
                System.out.println("May be of interest Preflop Machine Learning logging!");
            }

            System.out.println("*****************");
            System.out.println();
            System.out.println();
        }
    }

    public static void main(String[] args) throws Exception {
        new MachineLearningPreflop().testMethodNew();
    }

    private void testMethodNew() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_call_sng_compact;");

        double successTotal = 0;
        double totalTotal = 0;

        int counter = 0;

        while(rs.next()) {
            String route = rs.getString("route");

            if(rs.getDouble("total") >= 20) {
                counter++;
                double success = rs.getDouble("success");
                double total = rs.getDouble("total");

                if(success / total >= 0.47) {
                    System.out.println(route + "     " + success + "      " + total);
                } else {
                    //System.out.println("x");
                }

                successTotal = successTotal + success;
                totalTotal = totalTotal + total;
            }
        }

        rs.close();
        st.close();
        closeDbConnection();

        System.out.println(counter);
//        System.out.println(totalTotal);
//        System.out.println(successTotal / totalTotal);
    }

    private String adjustCallAction(String action, GameVariables gameVariables, ActionVariables actionVariables) throws Exception {
        String actionToReturn;

        String route = calculateCallRoute(gameVariables);

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_pf_call_sng_compact WHERE route = '" + route + "';");

        rs.next();

        if(rs.getDouble("total") >= 10) {
            System.out.println("a1a1");
            double success = rs.getDouble("success");
            double total = rs.getDouble("total");

            if(success / total < 0.5) {
                System.out.println("b1b1");
                double random = Math.random();

                if(gameVariables.isBotIsButton()) {
                    System.out.println("c1c1");
                    if(random < 0.43) {
                        if(actionVariables.getBotHandStrength() > 0.95) {
                            actionToReturn = "call";
                            System.out.println("MachineLearning preflop IP ignored because hs > 0.95 : " + route);
                        } else {
                            actionToReturn = "fold";
                            System.out.println("MachineLearning preflop IP fold. Route: " + route);
                        }
                    } else {
                        System.out.println("d1d1");
                        actionToReturn = action;
                    }
                } else {
                    System.out.println("e1e1");
                    if(random < 0.75) {
                        System.out.println("f1f1");
                        if(actionVariables.getBotHandStrength() > 0.95) {
                            actionToReturn = "call";
                            System.out.println("MachineLearning preflop OOP ignored because hs > 0.95 : " + route);
                        } else {
                            actionToReturn = "fold";
                            System.out.println("MachineLearning preflop OOP fold. Route: " + route);
                        }
                    } else {
                        System.out.println("g1g1");
                        actionToReturn = action;
                    }
                }
            } else {
                System.out.println("h1h1");
                actionToReturn = action;
            }
        } else {
            System.out.println("z1z1");
            actionToReturn = action;
        }

        rs.close();
        st.close();

        closeDbConnection();

        return actionToReturn;
    }

    private String adjustRaiseAction(String action, GameVariables gameVariables, ActionVariables actionVariables, double sizing) throws Exception {
        String actionToReturn;

        String route = calculateRaiseRoute(gameVariables, sizing);

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_pf_raise_sng_compact WHERE route = '" + route + "';");

        rs.next();

        if(rs.getDouble("total") >= 10) {
            double success = rs.getDouble("success");
            double total = rs.getDouble("total");

            if(success / total < 0.5) {
                double random = Math.random();

                System.out.println("Preflop raise action success below 0,5. Route: " + route);
                System.out.println("Opponentaction equals: " + gameVariables.getOpponentAction());

                if(random < 0.75) {
                    if(gameVariables.getOpponentAction().equals("call")) {
                        actionToReturn = "check";
                        System.out.println("MachineLearning preflop Raise change to check. Route: " + route);
                    } else {
                        System.out.println("MachineLearning preflop Raise change to fold or call. Route: " + route);

                        if(gameVariables.getBotBetSize() < gameVariables.getBigBlind()) {
                            actionToReturn = "fold";
                            System.out.println("Changed to fold because was IP open from SB");
                        } else {
                            System.out.println("Call adjustCallAction() because it concerns call other than IP open from SB");
                            actionToReturn = adjustCallAction("call", gameVariables, actionVariables);
                        }
                    }
                } else {
                    actionToReturn = action;
                    System.out.println("zzz preflop raise adjuster");
                }
            } else {
                actionToReturn = action;
                System.out.println("Preflop raise success above 0,5. Route: " + route);
            }
        } else {
            actionToReturn = action;
            System.out.println("Less than 10 hands preflop for route: " + route);
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

    private String calculateRaiseRoute(GameVariables gameVariables, double sizing) throws Exception {
        DbSavePreflopRaise dbSavePreflopRaise = new DbSavePreflopRaise();

        String handStrength = new DbSavePersisterPreflop().convertListCardToHandStrengthString(gameVariables.getBotHoleCards());
        String position = dbSavePreflopRaise.getPositionLogic(gameVariables.isBotIsButton());
        String sizingString = dbSavePreflopRaise.getSizingLogic(sizing / gameVariables.getBigBlind());
        String foldStatGroup = dbSavePreflopRaise.getFoldStatGroupLogic(new FoldStatsKeeper().getFoldStatFromDb(gameVariables.getOpponentName()));
        String effectiveStackString = dbSavePreflopRaise.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(), gameVariables.getOpponentStack() / gameVariables.getBigBlind());

        String route = handStrength + position + sizingString + foldStatGroup + effectiveStackString;

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