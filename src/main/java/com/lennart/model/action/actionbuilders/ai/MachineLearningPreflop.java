package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePreflopCall;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePreflopRaise;
import com.lennart.model.action.actionbuilders.ai.dbstatsraw.DbStatsRawBluffPostflopMigrator;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;

public class MachineLearningPreflop {

    private Connection con_2_0;

    public String adjustActionToDbSaveData(ActionVariables actionVariables, GameVariables gameVariables, double sizing) throws Exception {
        String actionToReturn = actionVariables.getAction();

        if(actionToReturn.equals("call")) {
            actionToReturn = adjustCallAction(actionVariables.getAction(), gameVariables, actionVariables);
        } else if(actionToReturn.equals("raise")) {
            actionToReturn = adjustRaiseAction(actionVariables.getAction(), gameVariables, actionVariables, sizing);
        }

        return actionToReturn;
    }

    private String adjustCallAction(String action, GameVariables gameVariables, ActionVariables actionVariables) throws Exception {
        String actionToReturn;

        String route = calculateCallRoute_2_0(gameVariables);

        actionToReturn = adjustCallActionDbLogic(action, gameVariables, actionVariables, route);

        if(actionToReturn == null) {
            actionToReturn = action;
            System.out.println("pf machinelearning 2_0 call data too small for route: " + route);
        } else {
            System.out.println("Use 2_0 pf call db. Route: " + route);
        }

        return actionToReturn;
    }

    private String adjustCallActionDbLogic(String action, GameVariables gameVariables, ActionVariables actionVariables, String route) throws Exception {
        String actionToReturn;

        Statement st;

        initialize_2_0_DbConnection();
        st = con_2_0.createStatement();

        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_pf_call_sng_compact_2_0 WHERE route = '" + route + "';");

        if(rs.next()) {
            if(rs.getDouble("total") >= 13) {
                System.out.println("a1a1");
                double success = rs.getDouble("success");
                double total = rs.getDouble("total");

                if(success / total < 0.5) {
                    System.out.println("b1b1");
                    double random = Math.random();

                    if(gameVariables.isBotIsButton()) {
                        System.out.println("c1c1");
                        if(random < 0.65) {
                            if(actionVariables.getBotHandStrength() > 0.95) {
                                actionToReturn = "call";
                                System.out.println("MachineLearning preflop IP ignored because hs > 0.95 : " + route);
                            } else {
                                actionToReturn = "call";
                                System.out.println("gvbgbv MachineLearning preflop IP fold. Route: " + route);
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
                                actionToReturn = "call";
                                System.out.println("gvbgbv MachineLearning preflop OOP fold. Route: " + route);
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
                actionToReturn = null;
            }
        } else {
            actionToReturn = null;
            System.out.println("EMPTY ROUTE! " + route);
        }

        rs.close();
        st.close();

        close_2_0_DbConnection();

        return actionToReturn;
    }

    private String adjustRaiseAction(String action, GameVariables gameVariables, ActionVariables actionVariables, double sizing) throws Exception {
        String actionToReturn;

        String route = calculateRaiseRoute_2_0(gameVariables, sizing);

        actionToReturn = adjustRaiseActionDbLogic(action, gameVariables, actionVariables, route);

        if(actionToReturn == null) {
            actionToReturn = action;
            System.out.println("pf machinelearning 2_0 raise data too small for route: " + route);
        } else {
            System.out.println("Use 2_0 pf raise db. Route: " + route);
        }

        return actionToReturn;
    }

    private String adjustRaiseActionDbLogic(String action, GameVariables gameVariables, ActionVariables actionVariables, String route) throws Exception {
        String actionToReturn;

        Statement st;

        initialize_2_0_DbConnection();
        st = con_2_0.createStatement();

        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_pf_raise_sng_compact_2_0 WHERE route = '" + route + "';");

        if(rs.next()) {
            if(rs.getDouble("total") >= 13) {
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
                                actionToReturn = "raise";
                                System.out.println("gvbgbv Changed to fold because was IP open from SB");
                            } else {
                                System.out.println("gvbgbv Call adjustCallAction() because it concerns call other than IP open from SB");
                                //actionToReturn = adjustCallAction("call", gameVariables, actionVariables);
                                actionToReturn = "raise";
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
                actionToReturn = null;

                System.out.println("Less than 10 hands preflop for route: " + route);
            }
        } else {
            System.out.println("wtf, exception error empty pf Machine Learning result set");
            actionToReturn = null;
        }

        rs.close();
        st.close();

        close_2_0_DbConnection();

        return actionToReturn;
    }

    private String calculateCallRoute_2_0(GameVariables gameVariables) throws Exception {
        double amountToCallBb = gameVariables.getOpponentBetSize() - gameVariables.getBotBetSize();

        if(amountToCallBb > gameVariables.getBotStack()) {
            amountToCallBb = gameVariables.getBotStack();
        }

        amountToCallBb = amountToCallBb / gameVariables.getBigBlind();

        DbSavePreflopCall dbSavePreflopCall = new DbSavePreflopCall();

        String combo = dbSavePreflopCall.getComboLogic(gameVariables.getBotHoleCards());
        String position = dbSavePreflopCall.getPositionLogic(gameVariables.isBotIsButton());
        String amountToCallGroup = dbSavePreflopCall.getAmountToCallViaLogic(amountToCallBb);
        String effectiveStack = new DbStatsRawBluffPostflopMigrator().getEffectiveStack(gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getBigBlind());

        OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0(gameVariables.getOpponentName());
        String opponentType = opponentIdentifier2_0.getOppType(opponentIdentifier2_0.getOppLooseness(), opponentIdentifier2_0.getOppAggressiveness());

        String route = combo + position + amountToCallGroup + effectiveStack + opponentType;

        while(StringUtils.countMatches(route, "OpponentUnknown") > 1) {
            route = route.substring(0, route.lastIndexOf("OpponentUnknown"));
        }

        return route;
    }

    private String calculateRaiseRoute_2_0(GameVariables gameVariables, double sizing) throws Exception {
        DbSavePreflopRaise dbSavePreflopRaise = new DbSavePreflopRaise();

        String combo = dbSavePreflopRaise.getComboLogic(gameVariables.getBotHoleCards());
        String position = dbSavePreflopRaise.getPositionLogic(gameVariables.isBotIsButton());
        String sizingString = dbSavePreflopRaise.getSizingLogic(sizing / gameVariables.getBigBlind());
        String effectiveStackString = new DbStatsRawBluffPostflopMigrator().getEffectiveStack(gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getBigBlind());

        OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0(gameVariables.getOpponentName());
        String opponentType = opponentIdentifier2_0.getOppType(opponentIdentifier2_0.getOppLooseness(), opponentIdentifier2_0.getOppAggressiveness());

        String route = combo + position + sizingString + effectiveStackString + opponentType;

        while(StringUtils.countMatches(route, "OpponentUnknown") > 1) {
            route = route.substring(0, route.lastIndexOf("OpponentUnknown"));
        }

        return route;
    }

    private void initialize_2_0_DbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con_2_0 = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker_2_0?&serverTimezone=UTC", "root", "");
    }

    private void close_2_0_DbConnection() throws SQLException {
        con_2_0.close();
    }
}
