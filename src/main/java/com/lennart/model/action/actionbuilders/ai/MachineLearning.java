package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveBluff;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveCall;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePersister;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveValue;
import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;
import com.lennart.model.card.Card;

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
            actionToReturn = adjustFoldAction(actionVariables, gameVariables, sizing, continuousTable.isPre3betOrPostRaisedPot());
        } else if(currentAction.equals("call")) {
            actionToReturn = adjustCallAction(actionVariables, gameVariables, sizing, continuousTable.isPre3betOrPostRaisedPot());
        } else if(currentAction.equals("check")) {
            actionToReturn = adjustCheckAction(actionVariables, gameVariables, continuousTable.isOpponentHasInitiative(), sizing);
        } else if(currentAction.equals("bet75pct")) {
            actionToReturn = adjustBetAction(actionVariables, gameVariables, sizing);
        } else if(currentAction.equals("raise")) {
            actionToReturn = adjustRaiseAction(actionVariables, gameVariables, continuousTable, sizing);
        }

        actionToReturn = doPilotBluffingOrRaising(actionToReturn, actionVariables, gameVariables,
                continuousTable.isOpponentHasInitiative(), sizing, continuousTable.isPre3betOrPostRaisedPot());

        actionToReturn = doPilotFloating(actionToReturn, actionVariables, gameVariables);

        return actionToReturn;
    }

    private String adjustCheckAction(ActionVariables actionVariables, GameVariables gameVariables,
                                     boolean opponentHasInitiative, double sizing) throws Exception {
        String actionToReturn;

        if(!opponentHasInitiative) {
            if(actionVariables.getBotHandStrength() < 0.7) {
                if(bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(),
                        gameVariables.getPot(), gameVariables.getBotStack(), gameVariables.getBoard())) {
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

    private String adjustFoldAction(ActionVariables actionVariables, GameVariables gameVariables, double sizing,
                                    boolean pre3BetOrPostRaisedPot) throws Exception {
        String actionToReturn = null;

        if(raiseIsEligible(gameVariables.getBoard(), pre3BetOrPostRaisedPot)) {
            if(bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(), gameVariables.getPot(),
                    gameVariables.getBotStack(), gameVariables.getBoard())) {
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

                    if(raiseSuccessRatio >= 0.57) {
                        actionToReturn = "raise";
                        System.out.println("Machinelearning I) Changed fold to raise");
                        System.out.println("Route: " + raiseRoute);
                    }
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
            } else {
                actionToReturn = doFreakyCallMachineLearning(actionVariables, gameVariables);

                if(actionToReturn != null && actionToReturn.equals("call")) {
                    System.out.println("Machinelearning K) (freaky) Changed fold to call");
                    System.out.println("Route: " + callRoute);
                }
            }
        }

        if(actionToReturn == null) {
            actionToReturn = "fold";
        }

        return actionToReturn;
    }

    private String doFreakyCallMachineLearning(ActionVariables actionVariables, GameVariables gameVariables) {
        String actionToReturn = null;

        List<Card> board = gameVariables.getBoard();

        if(board != null && (board.size() == 3 || board.size() == 4)) {
            double botBetSize = gameVariables.getBotBetSize();
            double opponentBetSize = gameVariables.getOpponentBetSize();
            double potSize = gameVariables.getPot();
            double botStack = gameVariables.getBotStack();
            double opponentStack = gameVariables.getOpponentStack();
            double amountToCall = opponentBetSize - botBetSize;


            double potAfterCall = potSize + (2 * opponentBetSize);
            double botStackAfterCall = botStack - amountToCall;
            double opponentStackAfterCall = opponentStack;
            double effectiveStackAfterCall;

            if(botStackAfterCall > opponentStackAfterCall) {
                effectiveStackAfterCall = opponentStackAfterCall;
            } else {
                effectiveStackAfterCall = botStackAfterCall;
            }

            if(effectiveStackAfterCall / potAfterCall >= 0.6) {
                double random = Math.random();

                if(gameVariables.isBotIsButton()) {
                    if(random >= 0.84) {
                        System.out.println("DO IP freaky call machine learning flop or turn");
                        actionToReturn = "call";
                    } else {
                        System.out.println("ZZZ IP freaky call machine learning flop or turn");
                    }
                } else {
                    if(random >= 0.9) {
                        System.out.println("DO OOP freaky call machine learning flop or turn");
                        actionToReturn = "call";
                    } else {
                        System.out.println("ZZZ OOP freaky call machine learning flop or turn");
                    }
                }
            }
        }

        if(board != null && board.size() == 5) {
            double handStrength = actionVariables.getBotHandStrength();
            double facingOdds = actionVariables.getFacingOdds(gameVariables);

            if(handStrength >= facingOdds) {
                double random = Math.random();

                if(random >= 0.9) {
                    System.out.println("DO freaky call machine learning river");
                    actionToReturn = "call";
                } else {
                    System.out.println("ZZZ freaky call machine learning river");
                }
            }
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
                    System.out.println("MachineLearning zzz");
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

    private String adjustCallAction(ActionVariables actionVariables, GameVariables gameVariables, double sizing,
                                    boolean pre3BetOrPostRaisedPot) throws Exception {
        String actionToReturn;

        String callRoute = calculateCallRoute(actionVariables, gameVariables);
        System.out.println("##Route: " + callRoute);
        List<Double> callData = getDataFromDb(callRoute, "call", actionVariables.getBotHandStrength());
        actionToReturn = changeToFoldOrRaiseOrKeepCallGivenData(callData, actionVariables, gameVariables, sizing, pre3BetOrPostRaisedPot);

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
                                                          GameVariables gameVariables, double sizing,
                                                          boolean pre3BetOrPostRaisedPot) throws Exception {
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
            if(raiseIsEligible(gameVariables.getBoard(), pre3BetOrPostRaisedPot)) {
                if(actionVariables.getBotHandStrength() >= 0.7 ||
                        bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(),
                                gameVariables.getPot(), gameVariables.getBotStack(), gameVariables.getBoard())) {

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

                        if (raiseSuccessRatio > 0.57) {
                            actionToReturn = "raise";
                            System.out.println("Machinelearning F) Changed call to raise");
                            System.out.println("Route: " + raiseRoute);
                        }
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

        String database = getTable(actionToConsider, handStrength, "sng");

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE route = '" + route + "';");

        rs.next();

        double successNumber;
        double totalNumber;

        if(rs.getDouble("total") < 20) {
            database = getTable(actionToConsider, handStrength, "play");

            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT * FROM " + database + " WHERE route = '" + route + "';");

            rs2.next();

            successNumber = rs2.getDouble("success");
            totalNumber = rs2.getDouble("total");

            if(totalNumber >= 20) {
                System.out.println("playmoney machine learning data used for route: " + route);
            }

            rs2.close();
            st2.close();
        } else {
            System.out.println("sng machine learning data used for route: " + route);

            successNumber = rs.getDouble("success");
            totalNumber = rs.getDouble("total");
        }

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

    private boolean bluffOddsAreOk(double sizing, double facingBetSize, double facingStackSize, double pot,
                                   double ownStackSize, List<Card> board) {
        boolean bluffOddsAreOk = false;
        double sizingInMethod;

        if(sizing > (facingBetSize + facingStackSize)) {
            sizingInMethod = facingBetSize + facingStackSize;
        } else {
            sizingInMethod = sizing;
        }

        double odds = (sizingInMethod - facingBetSize) / (facingBetSize + sizingInMethod + pot);

        if(board != null) {
            if(board.size() == 3 || board.size() == 4) {
                double ownStackAfterBluff = ownStackSize - sizing;
                double facingStackAfterBluff = facingStackSize - (sizing - facingBetSize);

                if(ownStackAfterBluff > 0 && facingStackAfterBluff > 0) {
                    bluffOddsAreOk = odds > 0.2;
                } else {
                    bluffOddsAreOk = odds > 0.36;
                }
            } else {
                bluffOddsAreOk = odds > 0.36;
            }
        } else {
            bluffOddsAreOk = odds > 0.36;
        }

        return bluffOddsAreOk;
    }

    private boolean floatOddsAreOk(GameVariables gameVariables) {
        double currentBotBetsize = gameVariables.getBotBetSize();
        double currentOppBetsize = gameVariables.getOpponentBetSize();
        double currentBotStack = gameVariables.getBotStack();
        double currentOppStack = gameVariables.getOpponentStack();
        double currentPot = gameVariables.getPot();

        double potAfterFloat = currentPot + (2 * currentOppBetsize);
        double botStackAfterFloat = currentBotStack - (currentOppBetsize - currentBotBetsize);
        double oppStackAfterFloat = currentOppStack;

        double effectiveStackAfterFloat;

        if(botStackAfterFloat > oppStackAfterFloat) {
            effectiveStackAfterFloat = oppStackAfterFloat;
        } else {
            effectiveStackAfterFloat = botStackAfterFloat;
        }

        return effectiveStackAfterFloat / potAfterFloat > 0.55;
    }

    private String getTable(String actionToConsider, double handStrength, String gameType) {
        String table;

        if(actionToConsider.equals("call")) {
            table = "dbstats_call_" + gameType + "_compact";
        } else {
            if(handStrength < 0.7) {
                table = "dbstats_bluff_" + gameType + "_compact";
            } else {
                table = "dbstats_value_" + gameType + "_compact";
            }
        }

        return table;
    }

    private String doPilotBluffingOrRaising(String currentAction, ActionVariables actionVariables, GameVariables gameVariables,
                                            boolean opponentHasInitiative, double sizing, boolean pre3BetOrPostRaisedPot) throws Exception {
        String actionToReturn;

        if(currentAction.equals("check")) {
            if(actionVariables.getBotHandStrength() < 0.7) {
                if(!opponentHasInitiative) {
                    if(bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(),
                            gameVariables.getPot(), gameVariables.getBotStack(), gameVariables.getBoard())) {
                        String route = calculateBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);

                        List<String> pilotRoutes = getPilotBluffRaiseRoutes();

                        if(pilotRoutes.contains(route)) {
                            double random = Math.random();

                            if(random < 0.75) {
                                actionToReturn = "bet75pct";
                                System.out.println("Pilot bluffbet done");
                                System.out.println("Route: " + route);
                            } else {
                                actionToReturn = currentAction;
                                System.out.println("Pilot zzz, route: " + route);
                            }
                        } else {
                            actionToReturn = currentAction;
                        }
                    } else {
                        actionToReturn = currentAction;
                    }
                } else {
                    actionToReturn = currentAction;
                }
            } else {
                actionToReturn = currentAction;
            }
        } else if(currentAction.equals("fold") || currentAction.equals("call")) {
            if(actionVariables.getBotHandStrength() < 0.7) {
                if(bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(),
                        gameVariables.getPot(), gameVariables.getBotStack(), gameVariables.getBoard())) {
                    String route = calculateBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);

                    List<String> pilotRoutes = getPilotBluffRaiseRoutes();

                    if(pilotRoutes.contains(route)) {
                        double random = Math.random();

                        if(random < 0.75) {
                            List<Card> board = gameVariables.getBoard();

                            if(raiseIsEligible(board, pre3BetOrPostRaisedPot)) {
                                actionToReturn = "raise";
                                System.out.println("Pilot bluffraise done");
                                System.out.println("Route: " + route);
                            } else {
                                actionToReturn = currentAction;
                            }
                        } else {
                            actionToReturn = currentAction;
                            System.out.println("Pilot zzz, route: " + route);
                        }
                    } else {
                        actionToReturn = currentAction;
                    }
                } else {
                    actionToReturn = currentAction;
                }
            } else {
                actionToReturn = currentAction;
            }
        } else {
            actionToReturn = currentAction;
        }

        return actionToReturn;
    }

    private String doPilotFloating(String currentAction, ActionVariables actionVariables, GameVariables gameVariables) throws Exception {
        String actionToReturn;

        if(currentAction.equals("fold")) {
            if(actionVariables.getBotHandStrength() < 0.7) {
                List<Card> board = gameVariables.getBoard();

                if(board != null && (board.size() == 3 || board.size() == 4)) {
                    double facingOdds = actionVariables.getFacingOdds(gameVariables);

                    if(facingOdds <= 0.5) {
                        if(floatOddsAreOk(gameVariables)) {
                            String route = calculateCallRoute(actionVariables, gameVariables);

                            List<String> floatPilotRoutes = getPilotFloatRoutes();

                            if(floatPilotRoutes.contains(route)) {
                                actionToReturn = "call";
                                System.out.println("Pilot float done");
                                System.out.println("Route: " + route);
                            } else {
                                actionToReturn = currentAction;
                            }
                        } else {
                            actionToReturn = currentAction;
                            System.out.println("float odds not ok");
                        }
                    } else {
                        actionToReturn = currentAction;
                    }
                } else {
                    actionToReturn = currentAction;
                }
            } else {
                actionToReturn = currentAction;
            }
        } else {
            actionToReturn = currentAction;
        }

        return actionToReturn;
    }

    private List<String> getPilotBluffRaiseRoutes() {
        List<String> pilotBluffRaiseRoutes = new ArrayList<>();

        pilotBluffRaiseRoutes.add("FlopBetOopSizing_20bb_upFoldstat_66_100_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("FlopRaiseIpSizing_0-10bbFoldstat_unknownStrongDrawFalse");
        pilotBluffRaiseRoutes.add("FlopRaiseIpSizing_10-20bbFoldstat_unknownStrongDrawFalse");
        pilotBluffRaiseRoutes.add("FlopRaiseIpSizing_20bb_upFoldstat_66_100_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("FlopRaiseOopSizing_0-10bbFoldstat_33_66_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("FlopRaiseOopSizing_10-20bbFoldstat_66_100_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("FlopRaiseOopSizing_20bb_upFoldstat_66_100_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("TurnBetIpSizing_0-10bbFoldstat_unknownStrongDrawTrue");
        pilotBluffRaiseRoutes.add("TurnRaiseOopSizing_10-20bbFoldstat_66_100_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("RiverBetIpSizing_10-20bbFoldstat_66_100_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("RiverBetOopSizing_10-20bbFoldstat_66_100_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("RiverRaiseIpSizing_0-10bbFoldstat_unknownStrongDrawFalse");
        pilotBluffRaiseRoutes.add("RiverRaiseIpSizing_20bb_upFoldstat_66_100_StrongDrawFalse");

        return pilotBluffRaiseRoutes;
    }

    private List<String> getPilotFloatRoutes() {
        List<String> pilotFloatRoutes = new ArrayList<>();

        pilotFloatRoutes.add("FlopFacingBetIpAtc_0-10bbAggro_66_100_HS_0_30_StrongDrawTrue");
        pilotFloatRoutes.add("FlopFacingBetIpAtc_0-10bbAggro_66_100_HS_0_30_StrongDrawFalse");
        pilotFloatRoutes.add("FlopFacingBetIpAtc_0-10bbAggro_66_100_HS_30_50_StrongDrawTrue");
        pilotFloatRoutes.add("FlopFacingBetIpAtc_0-10bbAggro_66_100_HS_60_70_StrongDrawFalse");
        pilotFloatRoutes.add("FlopFacingBetIpAtc_0-10bbAggro_unknownHS_30_50_StrongDrawTrue");
        pilotFloatRoutes.add("FlopFacingBetIpAtc_0-10bbAggro_unknownHS_60_70_StrongDrawFalse");
        pilotFloatRoutes.add("FlopFacingBetOopAtc_0-10bbAggro_33_66_HS_30_50_StrongDrawFalse");
        pilotFloatRoutes.add("FlopFacingBetOopAtc_0-10bbAggro_33_66_HS_60_70_StrongDrawFalse");
        pilotFloatRoutes.add("FlopFacingBetOopAtc_0-10bbAggro_66_100_HS_60_70_StrongDrawFalse");
        pilotFloatRoutes.add("TurnFacingBetIpAtc_0-10bbAggro_66_100_HS_0_30_StrongDrawTrue");
        pilotFloatRoutes.add("TurnFacingBetIpAtc_0-10bbAggro_66_100_HS_30_50_StrongDrawTrue");
        pilotFloatRoutes.add("TurnFacingBetIpAtc_0-10bbAggro_unknownHS_0_30_StrongDrawTrue");

        return pilotFloatRoutes;
    }

    public static void main(String[] args) throws Exception {
        List<String> pilotBluffRaiseRoutes = new ArrayList<>();

        pilotBluffRaiseRoutes.add("zzz");
        pilotBluffRaiseRoutes.add("zzz");
        pilotBluffRaiseRoutes.add("zzz");

        new MachineLearning().doPilotBluffRaiseRouteAnalysis(pilotBluffRaiseRoutes);
    }

    private void doPilotFloatRouteAnalysis(List<String> oldFloatPilotRoutes) throws Exception {
        List<String> newFloatPilotRoutes = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_call_sng_compact;");

        System.out.println("**** Routes ****");
        System.out.println();

        while(rs.next()) {
            String route = rs.getString("route");

            if(!route.contains("HS_70_80_") && !route.contains("HS_80_90_") && !route.contains("HS_90_100_")) {
                if(route.contains("Flop") || route.contains("Turn")) {
                    double success = rs.getDouble("success");
                    double total = rs.getDouble("total");
                    double ratio = success / total;

                    if(ratio >= 0.5 && total >= 3) {
                        newFloatPilotRoutes.add(route);
                        System.out.println(route + "      " + success + "       " + total);
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println();
        System.out.println();
        System.out.println("**** New Routes ****");
        System.out.println();

        for(String newRoute : newFloatPilotRoutes) {
            if(!oldFloatPilotRoutes.contains(newRoute)) {
                System.out.println(newRoute);
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("**** Routes removed ****");
        System.out.println();

        for(String oldRoute : oldFloatPilotRoutes) {
            if(!newFloatPilotRoutes.contains(oldRoute)) {
                System.out.println(oldRoute);
            }
        }
    }

    private void doPilotBluffRaiseRouteAnalysis(List<String> oldBluffPilotRoutes) throws Exception {
        List<String> newBluffPilotRoutes = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_bluff_sng_compact;");

        System.out.println("**** Routes ****");
        System.out.println();

        while(rs.next()) {
            String route = rs.getString("route");
            double success = rs.getDouble("success");
            double total = rs.getDouble("total");
            double ratio = success / total;

            if(route.contains("Bet")) {
                if(total >= 9 && total < 20 && ratio >= 0.57) {
                    newBluffPilotRoutes.add(route);
                    System.out.println(route + "    " + success + "   " + total + "   " + ratio);
                }
            } else if(route.contains("Raise")) {
                if(total >= 3 && total < 20 && ratio >= 0.66) {
                    newBluffPilotRoutes.add(route);
                    System.out.println(route + "    " + success + "   " + total + "   " + ratio);
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println();
        System.out.println();
        System.out.println("**** New Routes ****");
        System.out.println();

        for(String newRoute : newBluffPilotRoutes) {
            if(!oldBluffPilotRoutes.contains(newRoute)) {
                System.out.println(newRoute);
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("**** Routes removed ****");
        System.out.println();

        for(String oldRoute : oldBluffPilotRoutes) {
            if(!newBluffPilotRoutes.contains(oldRoute)) {
                System.out.println(oldRoute);
            }
        }
    }

    private boolean raiseIsEligible(List<Card> board, boolean pre3BetOrPostRaisedPot) {
        boolean raiseIsEligible = true;

        if(board != null) {
            if(board.size() == 3 || board.size() == 4) {
                if(pre3BetOrPostRaisedPot) {
                    raiseIsEligible = false;
                }
            }
        }

        return raiseIsEligible;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
