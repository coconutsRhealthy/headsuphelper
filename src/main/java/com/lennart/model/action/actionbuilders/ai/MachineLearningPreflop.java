package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePersisterPreflop;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePreflopCall;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePreflopRaise;
import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;

import java.sql.*;

public class MachineLearningPreflop {

    private Connection con;
    private Connection con_2_0;

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

    private String adjustCallAction(String action, GameVariables gameVariables, ActionVariables actionVariables) throws Exception {
        String actionToReturn;

        String route = calculateCallRoute_2_0(gameVariables);

        actionToReturn = adjustCallActionDbLogic(action, gameVariables, actionVariables, "dbstats_pf_call_sng_compact_2_0", true, route);

        if(actionToReturn == null) {
            route = calculateCallRoute(gameVariables);
            actionToReturn = adjustCallActionDbLogic(action, gameVariables, actionVariables, "dbstats_pf_call_sng_compact", false, route);
        }

        return actionToReturn;
    }

    private String adjustCallActionDbLogic(String action, GameVariables gameVariables, ActionVariables actionVariables,
                                           String table, boolean db_2_0, String route) throws Exception {
        String actionToReturn;

        Statement st;

        if(db_2_0) {
            initialize_2_0_DbConnection();
            st = con_2_0.createStatement();
        } else {
            initializeDbConnection();
            st = con.createStatement();
        }

        ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE route = '" + route + "';");

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

            if(db_2_0) {
                actionToReturn = null;
            } else {
                actionToReturn = action;
            }
        }

        rs.close();
        st.close();

        if(db_2_0) {
            close_2_0_DbConnection();
        } else {
            closeDbConnection();
        }

        return actionToReturn;
    }

    private String adjustRaiseAction(String action, GameVariables gameVariables, ActionVariables actionVariables, double sizing) throws Exception {
        String actionToReturn;

        String route = calculateRaiseRoute_2_0(gameVariables, sizing);

        actionToReturn = adjustRaiseActionDbLogic(action, gameVariables, actionVariables, "dbstats_pf_raise_sng_compact_2_0", true, route);

        if(actionToReturn == null) {
            route = calculateRaiseRoute(gameVariables, sizing);
            actionToReturn = adjustRaiseActionDbLogic(action, gameVariables, actionVariables, "dbstats_pf_raise_sng_compact", false, route);
        }

        return actionToReturn;
    }

    private String adjustRaiseActionDbLogic(String action, GameVariables gameVariables, ActionVariables actionVariables,
                                           String table, boolean db_2_0, String route) throws Exception {
        String actionToReturn;

        Statement st;

        if(db_2_0) {
            initialize_2_0_DbConnection();
            st = con_2_0.createStatement();
        } else {
            initializeDbConnection();
            st = con.createStatement();
        }

        ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE route = '" + route + "';");

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
            if(db_2_0) {
                actionToReturn = null;
            } else {
                actionToReturn = action;
            }

            System.out.println("Less than 10 hands preflop for route: " + route);
        }

        rs.close();
        st.close();

        if(db_2_0) {
            close_2_0_DbConnection();
        } else {
            closeDbConnection();
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

    private String calculateCallRoute_2_0(GameVariables gameVariables) throws Exception {
        double amountToCallBb = gameVariables.getOpponentBetSize() - gameVariables.getBotBetSize();

        if(amountToCallBb > gameVariables.getBotStack()) {
            amountToCallBb = gameVariables.getBotStack();
        }

        amountToCallBb = amountToCallBb / gameVariables.getBigBlind();

        DbSavePreflopCall dbSavePreflopCall = new DbSavePreflopCall();

        String handStrength = new DbSavePersisterPreflop().convertListCardToHandStrengthString(gameVariables.getBotHoleCards());
        String position = dbSavePreflopCall.getPositionLogic(gameVariables.isBotIsButton());
        String amountToCallGroup = dbSavePreflopCall.getAmountToCallViaLogic(amountToCallBb);
        String effectiveStack = dbSavePreflopCall.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(), gameVariables.getOpponentStack() / gameVariables.getBigBlind());

        OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0(gameVariables.getOpponentName());

        String oppPre3bet = dbSavePreflopCall.getOppPre3betLogic(opponentIdentifier2_0);
        String oppPreLooseness = dbSavePreflopCall.getOppPreLoosenessLogic(opponentIdentifier2_0);
        String oppPostRaise = dbSavePreflopCall.getOppPostRaiseLogic(opponentIdentifier2_0);
        String oppPostBet = dbSavePreflopCall.getOppPostBetLogic(opponentIdentifier2_0);
        String oppPostLooseness = dbSavePreflopCall.getOppPostLoosenessLogic(opponentIdentifier2_0);

        String route = handStrength + position + amountToCallGroup + effectiveStack + oppPre3bet + oppPreLooseness +
                oppPostRaise + oppPostBet + oppPostLooseness;

        return route;
    }

    private String calculateRaiseRoute_2_0(GameVariables gameVariables, double sizing) throws Exception {
        DbSavePreflopRaise dbSavePreflopRaise = new DbSavePreflopRaise();

        String handStrength = new DbSavePersisterPreflop().convertListCardToHandStrengthString(gameVariables.getBotHoleCards());
        String position = dbSavePreflopRaise.getPositionLogic(gameVariables.isBotIsButton());
        String sizingString = dbSavePreflopRaise.getSizingLogic(sizing / gameVariables.getBigBlind());
        String effectiveStackString = dbSavePreflopRaise.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(), gameVariables.getOpponentStack() / gameVariables.getBigBlind());

        OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0(gameVariables.getOpponentName());

        String oppPre3bet = dbSavePreflopRaise.getOppPre3betLogic(opponentIdentifier2_0);
        String oppPreLooseness = dbSavePreflopRaise.getOppPreLoosenessLogic(opponentIdentifier2_0);
        String oppPostRaise = dbSavePreflopRaise.getOppPostRaiseLogic(opponentIdentifier2_0);
        String oppPostBet = dbSavePreflopRaise.getOppPostBetLogic(opponentIdentifier2_0);
        String oppPostLooseness = dbSavePreflopRaise.getOppPostLoosenessLogic(opponentIdentifier2_0);

        String route = handStrength + position + sizingString + effectiveStackString + oppPre3bet + oppPreLooseness +
                oppPostRaise + oppPostBet + oppPostLooseness;

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

    private void initialize_2_0_DbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con_2_0 = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker_2_0?&serverTimezone=UTC", "root", "");
    }

    private void close_2_0_DbConnection() throws SQLException {
        con_2_0.close();
    }
}
