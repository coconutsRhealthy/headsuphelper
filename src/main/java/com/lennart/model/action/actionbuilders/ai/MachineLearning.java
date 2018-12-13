package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveBluff;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveCall;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePersister;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveValue;
import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MachineLearning {

    private Connection con;

    public String adjustActionToDbSaveData(ActionVariables actionVariables, GameVariables gameVariables,
                                           ContinuousTable continuousTable, double sizing) throws Exception {
        String actionToReturn = "";

        String currentAction = actionVariables.getAction();

        if(currentAction.equals("fold")) {
            actionToReturn = adjustFoldAction(actionVariables, gameVariables, sizing);
        } else if(currentAction.equals("call")) {
            actionToReturn = adjustCallAction(actionVariables, gameVariables, sizing);
        } else if(currentAction.equals("check")) {
            actionToReturn = adjustCheckAction(actionVariables, gameVariables, continuousTable.isOpponentHasInitiative(), sizing);
        } else if(currentAction.equals("bet75pct")) {
            actionToReturn = adjustBetAction(actionVariables, gameVariables, sizing);
        } else if(currentAction.equals("raise")) {
            actionToReturn = adjustRaiseAction(actionVariables, gameVariables, continuousTable, sizing);
        }

        return actionToReturn;
    }

    private String adjustCheckAction(ActionVariables actionVariables, GameVariables gameVariables,
                                     boolean opponentHasInitiative, double sizing) throws Exception {
        String actionToReturn;

        if(!opponentHasInitiative) {
            if(actionVariables.getBotHandStrength() < 0.7) {
                if(bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(), gameVariables.getPot())) {
                    String route = calculateBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                    System.out.println("##Route: " + route);
                    List<Double> bluffBetData = getDataFromDb(route, "bet75pct", actionVariables.getBotHandStrength());
                    actionToReturn = changeToBetOrKeepCheckGivenData(bluffBetData, route);
                } else {
                    actionToReturn = actionVariables.getAction();
                }
            } else {
                if(gameVariables.getBoard() != null && gameVariables.getBoard().size() == 5) {
                    if(gameVariables.isBotIsButton()) {
                        String route = calculateValueBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                        System.out.println("##Route: " + route);
                        List<Double> valueBetData = getDataFromDb(route, "bet75pct", actionVariables.getBotHandStrength());
                        actionToReturn = changeToBetOrKeepCheckGivenData(valueBetData, route);
                    } else {
                        actionToReturn = actionVariables.getAction();
                    }
                } else {
                    actionToReturn = actionVariables.getAction();
                }
            }
        } else {
            actionToReturn = actionVariables.getAction();
        }

        return actionToReturn;
    }

    private String adjustFoldAction(ActionVariables actionVariables, GameVariables gameVariables, double sizing) throws Exception {
        String actionToReturn = null;

        if(bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(), gameVariables.getPot())) {
            String raiseRoute;

            if(actionVariables.getBotHandStrength() < 0.7) {
                raiseRoute = calculateBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
            } else {
                raiseRoute = calculateValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
            }

            System.out.println("##Route: " + raiseRoute);
            List<Double> raiseData = getDataFromDb(raiseRoute, "raise", actionVariables.getBotHandStrength());

            if(raiseData.get(1) >= 20) {
                double raiseSuccessRatio = raiseData.get(0) / raiseData.get(1);

                if(raiseSuccessRatio > 0.51) {
                    actionToReturn = "raise";
                    System.out.println("Machinelearning I) Changed fold to raise");
                    System.out.println("Route: " + raiseRoute);
                }
            }
        }

        if(actionToReturn == null) {
            String callRoute = calculateCallRoute(actionVariables, gameVariables);
            System.out.println("##Route: " + callRoute);
            List<Double> callData = getDataFromDb(callRoute, "call", actionVariables.getBotHandStrength());

            if(callData.get(1) >= 20) {
                double callSuccessRatio = callData.get(0) / callData.get(1);
                double facingOdds = actionVariables.getFacingOdds(gameVariables);

                if(callSuccessRatio > facingOdds) {
                    actionToReturn = "call";
                    System.out.println("Machinelearning J) Changed fold to call");
                    System.out.println("Route: " + callRoute);
                }
            }
        }

        if(actionToReturn == null) {
            actionToReturn = "fold";
        }

        return actionToReturn;
    }

    private String adjustBetAction(ActionVariables actionVariables, GameVariables gameVariables, double sizing) throws Exception {
        String actionToReturn;

        String route;

        if(actionVariables.getBotHandStrength() < 0.7) {
            route = calculateBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
            System.out.println("##Route: " + route);
        } else {
            route = calculateValueBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
            System.out.println("##Route: " + route);
        }

        List<Double> betData = getDataFromDb(route, "bet75pct", actionVariables.getBotHandStrength());

        if(betData.get(1) >= 20) {
            double ratio = betData.get(0) / betData.get(1);

            if(ratio < 0.51) {
                double random = Math.random();

                if(random < 0.75) {
                    System.out.println("Machinelearning A) Changed bet to check");
                    System.out.println("Route: " + route);
                    actionToReturn = "check";
                } else {
                    actionToReturn = "bet75pct";
                }
            } else {
                actionToReturn = "bet75pct";
            }
        } else {
            actionToReturn = "bet75pct";
        }

        return actionToReturn;
    }

    private String adjustRaiseAction(ActionVariables actionVariables, GameVariables gameVariables,
                                     ContinuousTable continuousTable, double sizing) throws Exception {
        String actionToReturn;

        if(actionVariables.getBotHandStrength() < 0.7) {
            String bluffRaiseRoute = calculateBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
            System.out.println("##Route: " + bluffRaiseRoute);
            List<Double> bluffRaiseData = getDataFromDb(bluffRaiseRoute, "raise", actionVariables.getBotHandStrength());
            actionToReturn = changeToFoldOrCallOrKeepRaiseGivenData(bluffRaiseData, actionVariables, gameVariables,
                    continuousTable);
        } else {
            String valueRaiseRoute = calculateValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
            System.out.println("##Route: " + valueRaiseRoute);
            List<Double> valueRaiseData = getDataFromDb(valueRaiseRoute, "raise", actionVariables.getBotHandStrength());
            actionToReturn = changeToFoldOrCallOrKeepRaiseGivenData(valueRaiseData, actionVariables, gameVariables,
                    continuousTable);
        }

        return actionToReturn;
    }

    private String adjustCallAction(ActionVariables actionVariables, GameVariables gameVariables, double sizing) throws Exception {
        String actionToReturn;

        String callRoute = calculateCallRoute(actionVariables, gameVariables);
        System.out.println("##Route: " + callRoute);
        List<Double> callData = getDataFromDb(callRoute, "call", actionVariables.getBotHandStrength());
        actionToReturn = changeToFoldOrRaiseOrKeepCallGivenData(callData, actionVariables, gameVariables, sizing);

        return actionToReturn;
    }

    private String changeToBetOrKeepCheckGivenData(List<Double> data, String route) {
        String actionToReturn;

        double successNumber = data.get(0);
        double totalNumber = data.get(1);

        if(totalNumber >= 20) {
            double successRatio = successNumber / totalNumber;

            if(successRatio > 0.51) {
                if(route.contains("Sizing_0-10bb")) {
                    if(Math.random() < 0.7) {
                        actionToReturn = "bet75pct";
                        System.out.println("Machinelearning H1) Changed check to bet");
                        System.out.println("Route: " + route);
                    } else {
                        actionToReturn = "check";
                        System.out.println("MachineLearning zzz");
                    }
                } else {
                    actionToReturn = "bet75pct";
                    System.out.println("Machinelearning H2) Changed check to bet");
                    System.out.println("Route: " + route);
                }
            } else {
                actionToReturn = "check";
            }
        } else {
            actionToReturn = "check";
        }

        return actionToReturn;
    }

    private String changeToFoldOrCallOrKeepRaiseGivenData(List<Double> raiseData, ActionVariables actionVariables,
                                                          GameVariables gameVariables, ContinuousTable continuousTable) throws Exception {
        String actionToReturn = null;

        if(raiseData.get(1) >= 20) {
            double raiseRatio = raiseData.get(0) / raiseData.get(1);

            if(raiseRatio > 0.51) {
                actionToReturn = "raise";
            } else {
                double random = Math.random();

                if(random < 0.75) {
                    //keep actionToReturn to null so that raise will be changed
                } else {
                    actionToReturn = "raise";
                }
            }
        } else {
            actionToReturn = "raise";
        }

        if(actionToReturn == null) {
            String callRoute = calculateCallRoute(actionVariables, gameVariables);
            System.out.println("##Route: " + callRoute);
            List<Double> callData = getDataFromDb(callRoute, "call", actionVariables.getBotHandStrength());

            if(callData.get(1) >= 20) {
                double callSuccessRatio = callData.get(0) / callData.get(1);
                double facingOdds = actionVariables.getFacingOdds(gameVariables);

                if (callSuccessRatio > facingOdds) {
                    actionToReturn = "call";
                    System.out.println("Machinelearning B) Changed raise to call");
                    System.out.println("Route: " + callRoute);
                }
            }
        }

        if(actionToReturn == null) {
            actionToReturn = getFoldCallActionFromPoker(actionVariables, gameVariables, continuousTable);

            if(actionToReturn.equals("call")) {
                System.out.println("Machinelearning C) Changed raise to call");
            } else if(actionToReturn.equals("fold")) {
                System.out.println("Machinelearning D) Changed raise to fold");
            } else {
                System.out.println("Machinelearning E) Should not come here");
            }
        }

        return actionToReturn;
    }

    private String changeToFoldOrRaiseOrKeepCallGivenData(List<Double> callData, ActionVariables actionVariables,
                                                          GameVariables gameVariables, double sizing) throws Exception {
        String actionToReturn = null;

        if(callData.get(1) >= 20) {
            double callRatio = callData.get(0) / callData.get(1);
            double facingOdds = actionVariables.getFacingOdds(gameVariables);

            if(callRatio > facingOdds) {
                actionToReturn = "call";
            } else {
                double random = Math.random();

                if(random < 0.75) {
                    //keep actionToReturn to null so that call will be changed
                } else {
                    actionToReturn = "call";
                }
            }
        } else {
            actionToReturn = "call";
        }

        if(actionToReturn == null) {
            if(actionVariables.getBotHandStrength() >= 0.7 ||
                bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(), gameVariables.getPot())) {

                String raiseRoute;

                if(actionVariables.getBotHandStrength() >= 0.7) {
                    raiseRoute = calculateValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                } else {
                    raiseRoute = calculateBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                }

                System.out.println("##Route: " + raiseRoute);
                List<Double> raiseData = getDataFromDb(raiseRoute, "raise", actionVariables.getBotHandStrength());

                if (raiseData.get(1) >= 20) {
                    double raiseSuccessRatio = raiseData.get(0) / raiseData.get(1);

                    if (raiseSuccessRatio > 0.51) {
                        actionToReturn = "raise";
                        System.out.println("Machinelearning F) Changed call to raise");
                        System.out.println("Route: " + raiseRoute);
                    }
                }
            }
        }

        if(actionToReturn == null) {
            actionToReturn = "fold";
            System.out.println("Machinelearning G) Changed call to fold");
        }

        return actionToReturn;
    }

    private String getFoldCallActionFromPoker(ActionVariables actionVariables, GameVariables gameVariables, ContinuousTable continuousTable) {
        List<String> eligibleActions = new ArrayList<>();
        eligibleActions.add("fold");
        eligibleActions.add("call");

        double bigBlind = gameVariables.getBigBlind();

        String actionFromPoker = new Poker().getAction(
                actionVariables,
                eligibleActions,
                actionVariables.getStreet(gameVariables),
                gameVariables.isBotIsButton(),
                gameVariables.getPot() / bigBlind,
                gameVariables.getOpponentAction(),
                actionVariables.getFacingOdds(gameVariables),
                0,
                actionVariables.isBotHasStrongDraw(),
                actionVariables.getBotHandStrength(),
                actionVariables.getOpponentType(),
                gameVariables.getOpponentBetSize() / bigBlind,
                gameVariables.getBotBetSize() / bigBlind,
                0,
                gameVariables.getBotStack(),
                gameVariables.getBoard().isEmpty(),
                gameVariables.getBoard(),
                actionVariables.isStrongFlushDraw(),
                actionVariables.isStrongOosd(),
                actionVariables.isStrongGutshot(),
                bigBlind,
                continuousTable.isOpponentDidPreflop4betPot(),
                continuousTable.isPre3betOrPostRaisedPot(),
                actionVariables.isStrongOvercards(),
                actionVariables.isStrongBackdoorFd(),
                actionVariables.isStrongBackdoorSd(),
                200,
                continuousTable.isOpponentHasInitiative());

        return actionFromPoker;
    }

    private List<Double> getDataFromDb(String route, String actionToConsider, double handStrength) throws Exception {
        List<Double> valuesToReturn = new ArrayList<>();

        initializeDbConnection();

        String database = getTable(actionToConsider, handStrength);

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

    private String calculateBluffBetOrRaiseRoute(ActionVariables actionVariables, GameVariables gameVariables, String actionToConsider, double sizing) throws Exception {
        if(actionToConsider.equals("bet75pct")) {
            actionToConsider = "Bet";
        } else {
            actionToConsider = "Raise";
        }

        DbSaveBluff dbSaveBluff = new DbSaveBluff();

        String street = dbSaveBluff.getStreetViaLogic(gameVariables.getBoard());
        String bluffAction = actionToConsider;
        String position = dbSaveBluff.getPositionLogic(gameVariables.isBotIsButton());
        String sizingGroup = new DbSavePersister().convertBluffOrValueSizingToCompact(dbSaveBluff.getSizingGroupViaLogic(sizing / gameVariables.getBigBlind()));
        String foldStatGroup = dbSaveBluff.getFoldStatGroupLogic(new FoldStatsKeeper().getFoldStatFromDb(gameVariables.getOpponentName()));
        String strongDraw = dbSaveBluff.getStrongDrawLogic(actionVariables.getHandEvaluator().hasDrawOfType("strongFlushDraw"), actionVariables.getHandEvaluator().hasDrawOfType("strongOosd"));

        String route = street + bluffAction + position + sizingGroup + foldStatGroup + strongDraw;

        return route;
    }

    private String calculateValueBetOrRaiseRoute(ActionVariables actionVariables, GameVariables gameVariables, String actionToConsider, double sizing) throws Exception {
        if(actionToConsider.equals("bet75pct")) {
            actionToConsider = "Bet";
        } else {
            actionToConsider = "Raise";
        }

        DbSaveValue dbSaveValue = new DbSaveValue();

        String street = dbSaveValue.getStreetViaLogic(gameVariables.getBoard());
        String valueAction = actionToConsider;
        String postion = dbSaveValue.getPositionLogic(gameVariables.isBotIsButton());
        String sizingGroup = new DbSavePersister().convertBluffOrValueSizingToCompact(dbSaveValue.getSizingGroupViaLogic(sizing / gameVariables.getBigBlind()));
        String oppLoosenessGroup = dbSaveValue.getOppLoosenessGroupViaLogic(gameVariables.getOpponentName());
        String handStrength = dbSaveValue.getHandStrengthLogic(actionVariables.getBotHandStrength());

        String route = street + valueAction + postion + sizingGroup + oppLoosenessGroup + handStrength;

        return route;
    }

    private String calculateCallRoute(ActionVariables actionVariables, GameVariables gameVariables) throws Exception {
        double amountToCallBb = gameVariables.getOpponentBetSize() - gameVariables.getBotBetSize();

        if(amountToCallBb > gameVariables.getBotStack()) {
            amountToCallBb = gameVariables.getBotStack();
        }

        amountToCallBb = amountToCallBb / gameVariables.getBigBlind();

        DbSaveCall dbSaveCall = new DbSaveCall();

        String street = dbSaveCall.getStreetViaLogic(gameVariables.getBoard());
        String facingAction = dbSaveCall.getFacingActionViaLogic(gameVariables.getOpponentAction());
        String postion = dbSaveCall.getPositionLogic(gameVariables.isBotIsButton());
        String amountToCallGroup = new DbSavePersister().convertCallAtcToCompact(dbSaveCall.getAmountToCallViaLogic(amountToCallBb));
        String oppAggroGroup = dbSaveCall.getOppAggroGroupViaLogic(gameVariables.getOpponentName());
        String handStrength = dbSaveCall.getHandStrengthLogic(actionVariables.getBotHandStrength());
        String strongDraw = dbSaveCall.getStrongDrawLogic(actionVariables.getHandEvaluator().hasDrawOfType("strongFlushDraw"), actionVariables.getHandEvaluator().hasDrawOfType("strongOosd"));

        String route = street + facingAction + postion + amountToCallGroup + oppAggroGroup + handStrength + strongDraw;

        return route;
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

    private String getTable(String actionToConsider, double handStrength) {
        String table;

        if(actionToConsider.equals("call")) {
            table = "dbstats_call_play_compact";
        } else {
            if(handStrength < 0.7) {
                table = "dbstats_bluff_play_compact";
            } else {
                table = "dbstats_value_play_compact";
            }
        }

        return table;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
