package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveBluff;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveCall;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePersister;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveValue;
import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;
import com.lennart.model.card.Card;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MachineLearning {

    private Connection con;
    private Connection con_2_0;

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

        doMachineLearningLogging(actionToReturn, actionVariables, gameVariables, sizing);

        return actionToReturn;
    }

    private void doMachineLearningLogging(String action, ActionVariables actionVariables, GameVariables gameVariables, double sizing) throws Exception {
        double handStrength = actionVariables.getBotHandStrength();

        if(action.equals("fold")) {
            String compactCallRoute = calculateCompactCallRoute(actionVariables, gameVariables);
            String extensiveCallRoute = calculateExtensiveCallRoute(actionVariables, gameVariables);
            String compactCallRoute_2_0 = calculateCompactCallRoute_2_0(actionVariables, gameVariables);

            List<Double> callRouteData = getDataFromDb(compactCallRoute, extensiveCallRoute, "call", handStrength, compactCallRoute_2_0);

            String compactRaiseRoute;
            String extensiveRaiseRoute;
            String compactRoute_2_0;

            if(handStrength < 0.7) {
                compactRaiseRoute = calculateCompactBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                extensiveRaiseRoute = calculateExtensiveBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                compactRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
            } else {
                compactRaiseRoute = calculateCompactValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                extensiveRaiseRoute = calculateExtensiveValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                compactRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
            }

            List<Double> raiseRouteData = getDataFromDb(compactRaiseRoute, extensiveRaiseRoute, "raise", handStrength, compactRoute_2_0);

            double callRatio = callRouteData.get(0) / callRouteData.get(1);
            double raiseRatio = raiseRouteData.get(0) / raiseRouteData.get(1);

            System.out.println();
            System.out.println();
            System.out.println("***** Postflop Machine Learning logging *****");
            System.out.println("Action is: FOLD");
            System.out.println("Callroute success: " + callRouteData.get(0));
            System.out.println("Callroute total: " + callRouteData.get(1));
            System.out.println("Callroute ratio: " + callRatio);
            System.out.println("Compact callroute: " + compactCallRoute);
            System.out.println("Extensive callroute: " + extensiveCallRoute);
            System.out.println();
            System.out.println("Raiseroute success: " + raiseRouteData.get(0));
            System.out.println("Raiseroute total: " + raiseRouteData.get(1));
            System.out.println("Raiseroute ratio: " + raiseRatio);
            System.out.println("Compact raiseRoute: " + compactRaiseRoute);
            System.out.println("Extensive raiseRoute: " + extensiveRaiseRoute);
            System.out.println("Compact_2_0 route: " + compactRoute_2_0);

            if((callRatio >= 0.5 && callRouteData.get(1) >= 10) || (raiseRatio >= 0.5 && raiseRouteData.get(1) >= 10)) {
                System.out.println("May be of interest Postflop Machine Learning logging!");
            }

            System.out.println("*****************");
            System.out.println();
            System.out.println();
        } else if(action.equals("check")) {
            String compactBetRoute;
            String extensiveBetRoute;
            String compactRoute_2_0;

            if(handStrength < 0.7) {
                compactBetRoute = calculateCompactBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                extensiveBetRoute = calculateExtensiveBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                compactRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "bet75pct", sizing);
            } else {
                compactBetRoute = calculateCompactValueBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                extensiveBetRoute = calculateExtensiveValueBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                compactRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "bet75pct", sizing);
            }

            List<Double> betRouteData = getDataFromDb(compactBetRoute, extensiveBetRoute, "bet75pct", handStrength, compactRoute_2_0);

            double betRouteRatio = betRouteData.get(0) / betRouteData.get(1);

            System.out.println();
            System.out.println();
            System.out.println("***** Postflop Machine Learning logging *****");
            System.out.println("Action is: CHECK");
            System.out.println("Betroute success: " + betRouteData.get(0));
            System.out.println("Betroute total: " + betRouteData.get(1));
            System.out.println("Betroute ratio: " + betRouteRatio);
            System.out.println("Compact betroute: " + compactBetRoute);
            System.out.println("Extensive betroute: " + extensiveBetRoute);
            System.out.println("Compact_2_0 route: " + compactRoute_2_0);
            System.out.println();

            if(betRouteRatio >= 0.5 && betRouteData.get(1) >= 10) {
                System.out.println("May be of interest Postflop Machine Learning logging!");
            }

            System.out.println("*****************");
            System.out.println();
            System.out.println();
        } else if(action.equals("call")) {
            String compactCallRoute = calculateCompactCallRoute(actionVariables, gameVariables);
            String extensiveCallRoute = calculateExtensiveCallRoute(actionVariables, gameVariables);
            String compactCallRoute_2_0 = calculateCompactCallRoute_2_0(actionVariables, gameVariables);

            List<Double> callRouteData = getDataFromDb(compactCallRoute, extensiveCallRoute, "call", handStrength, compactCallRoute_2_0);

            String compactRaiseRoute;
            String extensiveRaiseRoute;
            String compactRoute_2_0;

            if(handStrength < 0.7) {
                compactRaiseRoute = calculateCompactBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                extensiveRaiseRoute = calculateExtensiveBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                compactRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
            } else {
                compactRaiseRoute = calculateCompactValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                extensiveRaiseRoute = calculateExtensiveValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                compactRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
            }

            List<Double> raiseRouteData = getDataFromDb(compactRaiseRoute, extensiveRaiseRoute, "raise", handStrength, compactRoute_2_0);

            double callRatio = callRouteData.get(0) / callRouteData.get(1);
            double raiseRatio = raiseRouteData.get(0) / raiseRouteData.get(1);

            System.out.println();
            System.out.println();
            System.out.println("***** Postflop Machine Learning logging *****");
            System.out.println("Action is: CALL");
            System.out.println("Callroute success: " + callRouteData.get(0));
            System.out.println("Callroute total: " + callRouteData.get(1));
            System.out.println("Callroute ratio: " + callRatio);
            System.out.println("Compact callroute: " + compactCallRoute);
            System.out.println("Extensive callroute: " + extensiveCallRoute);
            System.out.println();
            System.out.println("Raiseroute success: " + raiseRouteData.get(0));
            System.out.println("Raiseroute total: " + raiseRouteData.get(1));
            System.out.println("Raiseroute ratio: " + raiseRatio);
            System.out.println("Compact raiseRoute: " + compactRaiseRoute);
            System.out.println("Extensive raiseRoute: " + extensiveRaiseRoute);
            System.out.println("Compact_2_0 route: " + compactRoute_2_0);

            if(callRatio < 0.5 && callRouteData.get(1) >= 10) {
                System.out.println("May be of interest Postflop Machine Learning logging!");
            }

            System.out.println("*****************");
            System.out.println();
            System.out.println();
        } else if(action.equals("bet75pct")) {
            String compactBetRoute;
            String extensiveBetRoute;
            String compactRoute_2_0;

            if(handStrength < 0.7) {
                compactBetRoute = calculateCompactBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                extensiveBetRoute = calculateExtensiveBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                compactRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "bet75pct", sizing);
            } else {
                compactBetRoute = calculateCompactValueBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                extensiveBetRoute = calculateExtensiveValueBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                compactRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "bet75pct", sizing);
            }

            List<Double> betRouteData = getDataFromDb(compactBetRoute, extensiveBetRoute, "bet75pct", handStrength, compactRoute_2_0);

            double betRouteRatio = betRouteData.get(0) / betRouteData.get(1);

            System.out.println();
            System.out.println();
            System.out.println("***** Postflop Machine Learning logging *****");
            System.out.println("Action is: BET75PCT");
            System.out.println("Betroute success: " + betRouteData.get(0));
            System.out.println("Betroute total: " + betRouteData.get(1));
            System.out.println("Betroute ratio: " + betRouteRatio);
            System.out.println("Compact betroute: " + compactBetRoute);
            System.out.println("Extensive betroute: " + extensiveBetRoute);
            System.out.println("Compact_2_0 route: " + compactRoute_2_0);
            System.out.println();

            if(betRouteRatio < 0.5 && betRouteData.get(1) >= 10) {
                System.out.println("May be of interest Postflop Machine Learning logging!");
            }

            System.out.println("*****************");
            System.out.println();
            System.out.println();
        } else if(action.equals("raise")) {
            String compactCallRoute = calculateCompactCallRoute(actionVariables, gameVariables);
            String extensiveCallRoute = calculateExtensiveCallRoute(actionVariables, gameVariables);
            String compactCallRoute_2_0 = calculateCompactCallRoute_2_0(actionVariables, gameVariables);

            List<Double> callRouteData = getDataFromDb(compactCallRoute, extensiveCallRoute, "call", handStrength, compactCallRoute_2_0);

            String compactRaiseRoute;
            String extensiveRaiseRoute;
            String compactRoute_2_0;

            if(handStrength < 0.7) {
                compactRaiseRoute = calculateCompactBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                extensiveRaiseRoute = calculateExtensiveBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                compactRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
            } else {
                compactRaiseRoute = calculateCompactValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                extensiveRaiseRoute = calculateExtensiveValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                compactRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
            }

            List<Double> raiseRouteData = getDataFromDb(compactRaiseRoute, extensiveRaiseRoute, "raise", handStrength, compactRoute_2_0);

            double callRatio = callRouteData.get(0) / callRouteData.get(1);
            double raiseRatio = raiseRouteData.get(0) / raiseRouteData.get(1);

            System.out.println();
            System.out.println();
            System.out.println("***** Postflop Machine Learning logging *****");
            System.out.println("Action is: RAISE");
            System.out.println("Raiseroute success: " + raiseRouteData.get(0));
            System.out.println("Raiseroute total: " + raiseRouteData.get(1));
            System.out.println("Raiseroute ratio: " + raiseRatio);
            System.out.println("Compact raiseRoute: " + compactRaiseRoute);
            System.out.println("Extensive raiseRoute: " + extensiveRaiseRoute);
            System.out.println("Compact_2_0 Route: " + compactRoute_2_0);
            System.out.println();
            System.out.println("Callroute success: " + callRouteData.get(0));
            System.out.println("Callroute total: " + callRouteData.get(1));
            System.out.println("Callroute ratio: " + callRatio);
            System.out.println("Compact callroute: " + compactCallRoute);
            System.out.println("Extensive callroute: " + extensiveCallRoute);
            System.out.println("CompactCallRoute_2_0: " + compactCallRoute_2_0);

            if(raiseRatio < 0.5 && raiseRouteData.get(1) >= 10) {
                System.out.println("May be of interest Postflop Machine Learning logging!");
            }

            System.out.println("*****************");
            System.out.println();
            System.out.println();
        }
    }

    private String adjustCheckAction(ActionVariables actionVariables, GameVariables gameVariables,
                                     boolean opponentHasInitiative, double sizing) throws Exception {
        String actionToReturn;

        if(!opponentHasInitiative) {
            if(actionVariables.getBotHandStrength() < 0.7) {
                if(bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(),
                        gameVariables.getPot(), gameVariables.getBotStack(), gameVariables.getBoard(), gameVariables.getBotBetSize())) {
                    String extensiveRoute = calculateExtensiveBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                    String compactRoute = calculateCompactBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                    String compactBluffRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "bet75pct", sizing);
                    System.out.println("##ExtensiveRoute: " + extensiveRoute);
                    System.out.println("##CompactRoute: " + compactRoute);
                    System.out.println("##ComapctRoute_2_0: " + compactBluffRoute_2_0);
                    List<Double> bluffBetData = getDataFromDb(compactRoute, extensiveRoute, "bet75pct", actionVariables.getBotHandStrength(), compactBluffRoute_2_0);
                    actionToReturn = changeToBetOrKeepCheckGivenData(bluffBetData, compactRoute, extensiveRoute);
                } else {
                    actionToReturn = actionVariables.getAction();
                }
            } else {
                if(gameVariables.getBoard() != null && gameVariables.getBoard().size() == 5) {
                    if(gameVariables.isBotIsButton()) {
                        String extensiveRoute = calculateExtensiveValueBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                        String compactRoute = calculateCompactValueBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
                        String compactValueRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "bet75pct", sizing);
                        System.out.println("##ExtensiveRoute: " + extensiveRoute);
                        System.out.println("##CompactRoute: " + compactRoute);
                        System.out.println("##CompactValueRoute_2_0: " + compactValueRoute_2_0);
                        List<Double> valueBetData = getDataFromDb(compactRoute, extensiveRoute, "bet75pct", actionVariables.getBotHandStrength(), compactValueRoute_2_0);
                        actionToReturn = changeToBetOrKeepCheckGivenData(valueBetData, compactRoute, extensiveRoute);
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
                    gameVariables.getBotStack(), gameVariables.getBoard(), gameVariables.getBotBetSize())) {
                String extensiveRaiseRoute;
                String compactRaiseRoute;
                String compactRoute_2_0;

                if(actionVariables.getBotHandStrength() < 0.7) {
                    extensiveRaiseRoute = calculateExtensiveBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                    compactRaiseRoute = calculateCompactBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                    compactRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
                } else {
                    extensiveRaiseRoute = calculateExtensiveValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                    compactRaiseRoute = calculateCompactValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                    compactRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
                }

                System.out.println("##ExtensiveRoute: " + extensiveRaiseRoute);
                System.out.println("##CompactRoute: " + compactRaiseRoute);
                System.out.println("##CompactRoute_2_0: " + compactRoute_2_0);
                List<Double> raiseData = getDataFromDb(compactRaiseRoute, extensiveRaiseRoute, "raise",
                        actionVariables.getBotHandStrength(), compactRoute_2_0);

                if(raiseData.get(1) >= 20) {
                    double raiseSuccessRatio = raiseData.get(0) / raiseData.get(1);

                    if(raiseSuccessRatio >= 0.57) {
                        actionToReturn = "raise";
                        System.out.println("Machinelearning I) Changed fold to raise");
                        System.out.println("ExtensiveRoute: " + extensiveRaiseRoute);
                        System.out.println("CompactRoute: " + compactRaiseRoute);
                    }
                }
            }
        }

        if(actionToReturn == null) {
            String extensiveCallRoute = calculateExtensiveCallRoute(actionVariables, gameVariables);
            String compactCallRoute = calculateCompactCallRoute(actionVariables, gameVariables);
            String compactCallRoute_2_0 = calculateCompactCallRoute_2_0(actionVariables, gameVariables);
            System.out.println("##ExtensiveCallRoute: " + extensiveCallRoute);
            System.out.println("##CompactCallRoute: " + compactCallRoute);
            System.out.println("##CompactCallRoute_2_0: " + compactCallRoute_2_0);
            List<Double> callData = getDataFromDb(compactCallRoute, extensiveCallRoute, "call", actionVariables.getBotHandStrength(), compactCallRoute_2_0);

            if(callData.get(1) >= 20) {
                double callSuccessRatio = callData.get(0) / callData.get(1);
                double facingOdds = actionVariables.getFacingOdds(gameVariables);

                if(callSuccessRatio > facingOdds) {
                    actionToReturn = "call";
                    System.out.println("Machinelearning J) Changed fold to call");
                    System.out.println("ExtensiveRoute: " + extensiveCallRoute);
                    System.out.println("CompactRoute: " + compactCallRoute);
                }
            } else {
                //actionToReturn = doFreakyCallMachineLearning(actionVariables, gameVariables);

                if(actionToReturn != null && actionToReturn.equals("call")) {
                    System.out.println("Machinelearning K) (freaky) Changed fold to call");
                    System.out.println("ExtensiveRoute: " + extensiveCallRoute);
                    System.out.println("CompactRoute: " + compactCallRoute);
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

        String extensiveRoute;
        String compactRoute;
        String compactRoute_2_0;

        if(actionVariables.getBotHandStrength() < 0.7) {
            extensiveRoute = calculateExtensiveBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
            compactRoute = calculateCompactBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
            compactRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "bet75pct", sizing);
            System.out.println("##ExtensiveRoute: " + extensiveRoute);
            System.out.println("##CompactRoute: " + compactRoute);
            System.out.println("##ComactRoute_2_0: " + compactRoute_2_0);
        } else {
            extensiveRoute = calculateExtensiveValueBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
            compactRoute = calculateCompactValueBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);
            compactRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "bet75pct", sizing);
            System.out.println("##ExtensiveRoute: " + extensiveRoute);
            System.out.println("##CompactRoute: " + compactRoute);
        }

        List<Double> betData = getDataFromDb(compactRoute, extensiveRoute, "bet75pct", actionVariables.getBotHandStrength(), compactRoute_2_0);

        if(betData.get(1) >= 20) {
            double ratio = betData.get(0) / betData.get(1);

            if(ratio < 0.51) {
                double random = Math.random();

                if(random < 0.75) {
                    System.out.println("Machinelearning A) Changed bet to check");
                    System.out.println("CompactRoute: " + compactRoute);
                    System.out.println("ExtensiveRoute: " + extensiveRoute);
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
            String extensiveBluffRaiseRoute = calculateExtensiveBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
            String compactBluffRaiseRoute = calculateCompactBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
            String compactBluffRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
            System.out.println("##ExtensiveRoute: " + extensiveBluffRaiseRoute);
            System.out.println("##CompactRoute: " + compactBluffRaiseRoute);
            System.out.println("##CompactRoute_2_0: " + compactBluffRoute_2_0);
            List<Double> bluffRaiseData = getDataFromDb(compactBluffRaiseRoute, extensiveBluffRaiseRoute, "raise", actionVariables.getBotHandStrength(), compactBluffRoute_2_0);
            actionToReturn = changeToFoldOrCallOrKeepRaiseGivenData(bluffRaiseData, actionVariables, gameVariables,
                    continuousTable);
        } else {
            String extensiveValueRaiseRoute = calculateExtensiveValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
            String compactValueRaiseRoute = calculateCompactValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
            String compactValueRaiseRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
            System.out.println("##ExtensiveRoute: " + extensiveValueRaiseRoute);
            System.out.println("##CompactRoute: " + compactValueRaiseRoute);
            System.out.println("##CompactValueRaiseRoute_2_0: " + compactValueRaiseRoute_2_0);
            List<Double> valueRaiseData = getDataFromDb(compactValueRaiseRoute, extensiveValueRaiseRoute, "raise", actionVariables.getBotHandStrength(), compactValueRaiseRoute_2_0);
            actionToReturn = changeToFoldOrCallOrKeepRaiseGivenData(valueRaiseData, actionVariables, gameVariables,
                    continuousTable);
        }

        return actionToReturn;
    }

    private String adjustCallAction(ActionVariables actionVariables, GameVariables gameVariables, double sizing,
                                    boolean pre3BetOrPostRaisedPot) throws Exception {
        String actionToReturn;

        String extensiveCallRoute = calculateExtensiveCallRoute(actionVariables, gameVariables);
        String compactCallRoute = calculateCompactCallRoute(actionVariables, gameVariables);
        String compactCallRoute_2_0 = calculateCompactCallRoute_2_0(actionVariables, gameVariables);
        System.out.println("##ExtensiveCallRoute: " + extensiveCallRoute);
        System.out.println("##CompactCallRoute: " + compactCallRoute);
        System.out.println("##CompactCallRoute_2_0: " + compactCallRoute_2_0);
        List<Double> callData = getDataFromDb(compactCallRoute, extensiveCallRoute, "call", actionVariables.getBotHandStrength(), compactCallRoute_2_0);
        actionToReturn = changeToFoldOrRaiseOrKeepCallGivenData(callData, actionVariables, gameVariables, sizing, pre3BetOrPostRaisedPot, compactCallRoute, extensiveCallRoute);

        return actionToReturn;
    }

    private String changeToBetOrKeepCheckGivenData(List<Double> data, String compactRoute, String extensiveRoute) {
        String actionToReturn;

        double successNumber = data.get(0);
        double totalNumber = data.get(1);

        if(totalNumber >= 20) {
            double successRatio = successNumber / totalNumber;

            if(successRatio > 0.51) {
                if(compactRoute.contains("Sizing_0-10bb") && extensiveRoute.contains("Sizing_0-5bb")) {
                    if(Math.random() < 0.7) {
                        actionToReturn = "bet75pct";
                        System.out.println("Machinelearning H1) Changed check to bet");
                        System.out.println("CompactRoute: " + compactRoute);
                        System.out.println("ExtensiveRoute: " + extensiveRoute);
                    } else {
                        actionToReturn = "check";
                        System.out.println("MachineLearning zzz");
                    }
                } else {
                    actionToReturn = "bet75pct";
                    System.out.println("Machinelearning H2) Changed check to bet");
                    System.out.println("CompactRoute: " + compactRoute);
                    System.out.println("ExtensiveRoute: " + extensiveRoute);
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
            String extensiveCallRoute = calculateExtensiveCallRoute(actionVariables, gameVariables);
            String compactCallRoute = calculateCompactCallRoute(actionVariables, gameVariables);
            String compactCallRoute_2_0 = calculateCompactCallRoute_2_0(actionVariables, gameVariables);
            System.out.println("##ExtensiveRoute: " + extensiveCallRoute);
            System.out.println("##CompactRoute: " + compactCallRoute);
            System.out.println("##CompactCallRoute_2_0: " + compactCallRoute_2_0);
            List<Double> callData = getDataFromDb(compactCallRoute, extensiveCallRoute, "call", actionVariables.getBotHandStrength(), compactCallRoute_2_0);

            if(callData.get(1) >= 20) {
                double callSuccessRatio = callData.get(0) / callData.get(1);
                double facingOdds = actionVariables.getFacingOdds(gameVariables);

                if (callSuccessRatio > facingOdds) {
                    actionToReturn = "call";
                    System.out.println("Machinelearning B) Changed raise to call");
                    System.out.println("ExtensiveRoute: " + compactCallRoute);
                    System.out.println("CompactRoute: " + compactCallRoute);
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
                                                          boolean pre3BetOrPostRaisedPot, String compactCallRoute,
                                                          String extensiveCallRoute) throws Exception {
        String actionToReturn = null;

        if(callData.get(1) >= 10) {
            double callRatio = callData.get(0) / callData.get(1);
            double facingOdds = actionVariables.getFacingOdds(gameVariables);
            double callLimit = getCallRatioLimit(facingOdds);

            System.out.println("callLimit: " + callLimit);

            if(callRatio >= callLimit) {
                System.out.println("callRatio above callLimit, callratio: " + callRatio + "   callLimit: " + callLimit);
                actionToReturn = "call";
            } else {
                double random = Math.random();

                if(gameVariables.isBotIsButton()) {
                    if(random < 0.54) {
                        System.out.println("IP call should be changed. actionToReturn kept null. CompactRoute: "
                                + compactCallRoute + " -------------- extensive route: " + extensiveCallRoute);
                        System.out.println("callLimit: " + callLimit);
                    } else {
                        actionToReturn = "call";
                        System.out.println("call zzz1. Route: " + compactCallRoute);
                    }
                } else {
                    if(random < 0.78) {
                        System.out.println("OOP call should be changed. actionToReturn kept null. CompactRoute: "
                                + compactCallRoute + " -------------- extensive route: " + extensiveCallRoute);
                        System.out.println("callLimit: " + callLimit);
                    } else {
                        actionToReturn = "call";
                        System.out.println("call zzz2. Route: " + compactCallRoute);
                    }
                }
            }
        } else {
            actionToReturn = "call";
            System.out.println("calldata below 20. Route: " + compactCallRoute);
        }

        if(actionToReturn == null) {
            if(raiseIsEligible(gameVariables.getBoard(), pre3BetOrPostRaisedPot)) {
                if(actionVariables.getBotHandStrength() >= 0.7 ||
                        bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(),
                                gameVariables.getPot(), gameVariables.getBotStack(), gameVariables.getBoard(), gameVariables.getBotBetSize())) {

                    String extensiveRaiseRoute;
                    String compactRaiseRoute;
                    String compactRaiseRoute_2_0;

                    if(actionVariables.getBotHandStrength() >= 0.7) {
                        extensiveRaiseRoute = calculateExtensiveValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                        compactRaiseRoute = calculateCompactValueBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                        compactRaiseRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
                    } else {
                        extensiveRaiseRoute = calculateExtensiveBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                        compactRaiseRoute = calculateCompactBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);
                        compactRaiseRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
                    }

                    System.out.println("##ExtensiveRoute: " + extensiveRaiseRoute);
                    System.out.println("##CompactRoute: " + compactRaiseRoute);
                    System.out.println("##ComapctRoute_2_0: " + compactRaiseRoute_2_0);
                    List<Double> raiseData = getDataFromDb(compactRaiseRoute, extensiveRaiseRoute, "raise", actionVariables.getBotHandStrength(), compactRaiseRoute_2_0);

                    if (raiseData.get(1) >= 20) {
                        double raiseSuccessRatio = raiseData.get(0) / raiseData.get(1);

                        if (raiseSuccessRatio > 0.57) {
                            actionToReturn = "raise";
                            System.out.println("Machinelearning F) Changed call to raise");
                            System.out.println("ExtensiveRoute: " + extensiveRaiseRoute);
                            System.out.println("CompactRoute: " + compactRaiseRoute);
                            System.out.println("CompactRoute_2_0: " + compactRaiseRoute_2_0);
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

    private List<Double> getDataFromDb(String compactRoute, String extensiveRoute, String actionToConsider,
                                       double handStrength, String compactRoute_2_0) throws Exception {
        List<Double> valuesToReturn = new ArrayList<>();

        if(compactRoute_2_0 != null && !compactRoute_2_0.contains("OpponentUnknown")) {
            String table_2_0 = getTable_2_0(actionToConsider, handStrength);

            initialize_2_0_DbConnection();

            Statement st_2_0 = con_2_0.createStatement();
            ResultSet rs_2_0 = st_2_0.executeQuery("SELECT * FROM " + table_2_0 + " WHERE route = '" + compactRoute_2_0 + "';");

            rs_2_0.next();

            if(rs_2_0.getDouble("total") >= 20) {
                valuesToReturn.add(rs_2_0.getDouble("success"));
                valuesToReturn.add(rs_2_0.getDouble("total"));
                System.out.println("Use compact_2_0 data! " + compactRoute_2_0);
            } else {
                System.out.println("Comapct_2_0 data too small: " + compactRoute_2_0);
            }

            rs_2_0.close();
            st_2_0.close();

            close_2_0_DbConnection();
        }

        if(valuesToReturn.isEmpty()) {
            initializeDbConnection();

            String table = getTable(actionToConsider, handStrength, "sng", false);

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE route = '" + extensiveRoute + "';");

            rs.next();

            double successNumber;
            double totalNumber;

            if(rs.getDouble("total") < 20) {
                table = getTable(actionToConsider, handStrength, "sng", true);

                Statement st2 = con.createStatement();
                ResultSet rs2 = st2.executeQuery("SELECT * FROM " + table + " WHERE route = '" + compactRoute + "';");

                rs2.next();

                if(rs2.getDouble("total") < 20) {
                    //System.out.println("insufficient data for MachineLearning. Route: " + compactRoute + " Total: " + rs2.getDouble("total"));

                    successNumber = rs2.getDouble("success");
                    totalNumber = rs2.getDouble("total");
                } else {
                    System.out.println("compact sng machine learning data used for route: " + compactRoute);

                    successNumber = rs2.getDouble("success");
                    totalNumber = rs2.getDouble("total");
                }

                rs2.close();
                st2.close();
            } else {
                System.out.println("extensive sng machine learning data used for route: " + extensiveRoute);

                successNumber = rs.getDouble("success");
                totalNumber = rs.getDouble("total");
            }

            valuesToReturn.add(successNumber);
            valuesToReturn.add(totalNumber);

            rs.close();
            st.close();

            closeDbConnection();
        }

        return valuesToReturn;
    }

    private String calculateCompactBluffBetOrRaiseRoute_2_0(ActionVariables actionVariables, GameVariables gameVariables,
                                                            String actionToConsider, double sizing) throws Exception {
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
        String strongDraw = dbSaveBluff.getStrongDrawLogic(actionVariables.getHandEvaluator().hasDrawOfType("strongFlushDraw"), actionVariables.getHandEvaluator().hasDrawOfType("strongOosd"));
        String effectiveStack = new DbSavePersister().convertEffectiveStackToCompact(
                dbSaveBluff.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(),
                        gameVariables.getOpponentStack() / gameVariables.getBigBlind()));

        OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0(gameVariables.getOpponentName());

        String oppPre3bet = dbSaveBluff.getOppPre3betLogic(opponentIdentifier2_0);
        String oppPreLooseness = dbSaveBluff.getOppPreLoosenessLogic(opponentIdentifier2_0);
        String oppPostRaise = dbSaveBluff.getOppPostRaiseLogic(opponentIdentifier2_0);
        String oppPostBet = dbSaveBluff.getOppPostBetLogic(opponentIdentifier2_0);
        String oppPostLooseness = dbSaveBluff.getOppPostLoosenessLogic(opponentIdentifier2_0);

        String route = street + bluffAction + position + sizingGroup + strongDraw + effectiveStack +
                oppPre3bet + oppPreLooseness + oppPostRaise + oppPostBet + oppPostLooseness;

        while(StringUtils.countMatches(route, "OpponentUnknown") > 1) {
            route = route.substring(0, route.lastIndexOf("OpponentUnknown"));
        }

        return route;
    }

    private String calculateCompactValueBetOrRaiseRoute_2_0(ActionVariables actionVariables, GameVariables gameVariables,
                                                            String actionToConsider, double sizing) throws Exception {
        if(actionToConsider.equals("bet75pct")) {
            actionToConsider = "Bet";
        } else {
            actionToConsider = "Raise";
        }

        DbSaveValue dbSaveValue = new DbSaveValue();

        String street = dbSaveValue.getStreetViaLogic(gameVariables.getBoard());
        String valueAction = actionToConsider;
        String position = dbSaveValue.getPositionLogic(gameVariables.isBotIsButton());
        String sizingGroup = new DbSavePersister().convertBluffOrValueSizingToCompact(dbSaveValue.getSizingGroupViaLogic(sizing / gameVariables.getBigBlind()));
        String handStrength = dbSaveValue.getHandStrengthLogic(actionVariables.getBotHandStrength());
        String effectiveStack = new DbSavePersister().convertEffectiveStackToCompact(
                dbSaveValue.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(),
                        gameVariables.getOpponentStack() / gameVariables.getBigBlind()));

        OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0(gameVariables.getOpponentName());

        String oppPre3bet = dbSaveValue.getOppPre3betLogic(opponentIdentifier2_0);
        String oppPreLooseness = dbSaveValue.getOppPreLoosenessLogic(opponentIdentifier2_0);
        String oppPostRaise = dbSaveValue.getOppPostRaiseLogic(opponentIdentifier2_0);
        String oppPostBet = dbSaveValue.getOppPostBetLogic(opponentIdentifier2_0);
        String oppPostLooseness = dbSaveValue.getOppPostLoosenessLogic(opponentIdentifier2_0);

        String route = street + valueAction + position + sizingGroup + handStrength + effectiveStack +
                oppPre3bet + oppPreLooseness + oppPostRaise + oppPostBet + oppPostLooseness;

        while(StringUtils.countMatches(route, "OpponentUnknown") > 1) {
            route = route.substring(0, route.lastIndexOf("OpponentUnknown"));
        }

        return route;
    }

    private String calculateCompactCallRoute_2_0(ActionVariables actionVariables, GameVariables gameVariables) throws Exception {
        double amountToCallBb = gameVariables.getOpponentBetSize() - gameVariables.getBotBetSize();

        if(amountToCallBb > gameVariables.getBotStack()) {
            amountToCallBb = gameVariables.getBotStack();
        }

        amountToCallBb = amountToCallBb / gameVariables.getBigBlind();

        DbSaveCall dbSaveCall = new DbSaveCall();

        String street = dbSaveCall.getStreetViaLogic(gameVariables.getBoard());
        String facingAction = dbSaveCall.getFacingActionViaLogic(gameVariables.getOpponentAction());
        String position = dbSaveCall.getPositionLogic(gameVariables.isBotIsButton());
        String amountToCallGroup = new DbSavePersister().convertCallAtcToCompact(dbSaveCall.getAmountToCallViaLogic(amountToCallBb));
        String handStrength = dbSaveCall.getHandStrengthLogic(actionVariables.getBotHandStrength());
        String strongDraw = dbSaveCall.getStrongDrawLogic(actionVariables.getHandEvaluator().hasDrawOfType("strongFlushDraw"), actionVariables.getHandEvaluator().hasDrawOfType("strongOosd"));
        String effectiveStack = new DbSavePersister().convertEffectiveStackToCompact(
                dbSaveCall.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(),
                        gameVariables.getOpponentStack() / gameVariables.getBigBlind()));

        OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0(gameVariables.getOpponentName());

        String oppPre3bet = dbSaveCall.getOppPre3betLogic(opponentIdentifier2_0);
        String oppPreLooseness = dbSaveCall.getOppPreLoosenessLogic(opponentIdentifier2_0);
        String oppPostRaise = dbSaveCall.getOppPostRaiseLogic(opponentIdentifier2_0);
        String oppPostBet = dbSaveCall.getOppPostBetLogic(opponentIdentifier2_0);
        String oppPostLooseness = dbSaveCall.getOppPostLoosenessLogic(opponentIdentifier2_0);

        String route = street + facingAction + position + amountToCallGroup + handStrength + strongDraw +
                effectiveStack + oppPre3bet + oppPreLooseness + oppPostRaise + oppPostBet + oppPostLooseness;

        while(StringUtils.countMatches(route, "OpponentUnknown") > 1) {
            route = route.substring(0, route.lastIndexOf("OpponentUnknown"));
        }

        return route;
    }

    private String calculateCompactBluffBetOrRaiseRoute(ActionVariables actionVariables, GameVariables gameVariables, String actionToConsider, double sizing) throws Exception {
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
        String effectiveStack = new DbSavePersister().convertEffectiveStackToCompact(
                dbSaveBluff.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(),
                        gameVariables.getOpponentStack() / gameVariables.getBigBlind()));

        String route = street + bluffAction + position + sizingGroup + foldStatGroup + strongDraw + effectiveStack;

        return route;
    }

    private String calculateExtensiveBluffBetOrRaiseRoute(ActionVariables actionVariables, GameVariables gameVariables, String actionToConsider, double sizing) throws Exception {
        if(actionToConsider.equals("bet75pct")) {
            actionToConsider = "Bet";
        } else {
            actionToConsider = "Raise";
        }

        DbSaveBluff dbSaveBluff = new DbSaveBluff();

        String street = dbSaveBluff.getStreetViaLogic(gameVariables.getBoard());
        String bluffAction = actionToConsider;
        String position = dbSaveBluff.getPositionLogic(gameVariables.isBotIsButton());
        String sizingGroup = dbSaveBluff.getSizingGroupViaLogic(sizing / gameVariables.getBigBlind());
        String foldStatGroup = dbSaveBluff.getFoldStatGroupLogic(new FoldStatsKeeper().getFoldStatFromDb(gameVariables.getOpponentName()));
        String effectiveStackString = dbSaveBluff.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(), gameVariables.getOpponentStack() / gameVariables.getBigBlind());
        String handStrength = dbSaveBluff.getHandStrengthLogic(actionVariables.getBotHandStrength());
        String drawWetnessString = dbSaveBluff.getDrawWetnessLogic(gameVariables.getBoard(), actionVariables.getBoardEvaluator().getFlushStraightWetness());
        String boatWetnessString = dbSaveBluff.getBoatWetnessLogic(gameVariables.getBoard(), actionVariables.getBoardEvaluator().getBoatWetness());
        String strongDraw = dbSaveBluff.getStrongDrawLogic(actionVariables.getHandEvaluator().hasDrawOfType("strongFlushDraw"), actionVariables.getHandEvaluator().hasDrawOfType("strongOosd"));

        String route = street + bluffAction + position + sizingGroup + foldStatGroup + effectiveStackString + handStrength + drawWetnessString + boatWetnessString + strongDraw;

        return route;
    }

    private String calculateCompactValueBetOrRaiseRoute(ActionVariables actionVariables, GameVariables gameVariables, String actionToConsider, double sizing) throws Exception {
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
        String effectiveStack = new DbSavePersister().convertEffectiveStackToCompact(
                dbSaveValue.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(),
                        gameVariables.getOpponentStack() / gameVariables.getBigBlind()));

        String route = street + valueAction + postion + sizingGroup + oppLoosenessGroup + handStrength + effectiveStack;

        return route;
    }

    private String calculateExtensiveValueBetOrRaiseRoute(ActionVariables actionVariables, GameVariables gameVariables, String actionToConsider, double sizing) throws Exception {
        if(actionToConsider.equals("bet75pct")) {
            actionToConsider = "Bet";
        } else {
            actionToConsider = "Raise";
        }

        DbSaveValue dbSaveValue = new DbSaveValue();

        String street = dbSaveValue.getStreetViaLogic(gameVariables.getBoard());
        String valueAction = actionToConsider;
        String position = dbSaveValue.getPositionLogic(gameVariables.isBotIsButton());
        String sizingGroup = dbSaveValue.getSizingGroupViaLogic(sizing / gameVariables.getBigBlind());
        String oppLoosenessGroup = dbSaveValue.getOppLoosenessGroupViaLogic(gameVariables.getOpponentName());
        String handStrength = dbSaveValue.getHandStrengthLogic(actionVariables.getBotHandStrength());
        String strongDraw = dbSaveValue.getStrongDrawLogic(actionVariables.getHandEvaluator().hasDrawOfType("strongFlushDraw"), actionVariables.getHandEvaluator().hasDrawOfType("strongOosd"));
        String effectiveStackString = dbSaveValue.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(), gameVariables.getOpponentStack() / gameVariables.getBigBlind());
        String drawWetnessString = dbSaveValue.getDrawWetnessLogic(gameVariables.getBoard(), actionVariables.getBoardEvaluator().getFlushStraightWetness());
        String boatWetnessString = dbSaveValue.getBoatWetnessLogic(gameVariables.getBoard(), actionVariables.getBoardEvaluator().getBoatWetness());

        String route = street + valueAction + position + sizingGroup + oppLoosenessGroup + handStrength + strongDraw + effectiveStackString + drawWetnessString + boatWetnessString;

        return route;
    }

    private String calculateCompactCallRoute(ActionVariables actionVariables, GameVariables gameVariables) throws Exception {
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
        String effectiveStack = new DbSavePersister().convertEffectiveStackToCompact(
                dbSaveCall.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(),
                        gameVariables.getOpponentStack() / gameVariables.getBigBlind()));

        String route = street + facingAction + postion + amountToCallGroup + oppAggroGroup + handStrength + strongDraw + effectiveStack;

        return route;
    }

    private String calculateExtensiveCallRoute(ActionVariables actionVariables, GameVariables gameVariables) throws Exception {
        double amountToCallBb = gameVariables.getOpponentBetSize() - gameVariables.getBotBetSize();

        if(amountToCallBb > gameVariables.getBotStack()) {
            amountToCallBb = gameVariables.getBotStack();
        }

        amountToCallBb = amountToCallBb / gameVariables.getBigBlind();

        DbSaveCall dbSaveCall = new DbSaveCall();

        String street = dbSaveCall.getStreetViaLogic(gameVariables.getBoard());
        String facingAction = dbSaveCall.getFacingActionViaLogic(gameVariables.getOpponentAction());
        String postion = dbSaveCall.getPositionLogic(gameVariables.isBotIsButton());
        String amountToCallGroup = dbSaveCall.getAmountToCallViaLogic(amountToCallBb);
        String oppAggroGroup = dbSaveCall.getOppAggroGroupViaLogic(gameVariables.getOpponentName());
        String handStrength = dbSaveCall.getHandStrengthLogic(actionVariables.getBotHandStrength());
        String strongDraw = dbSaveCall.getStrongDrawLogic(actionVariables.getHandEvaluator().hasDrawOfType("strongFlushDraw"), actionVariables.getHandEvaluator().hasDrawOfType("strongOosd"));
        String effectiveStackString = dbSaveCall.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(), gameVariables.getOpponentStack() / gameVariables.getBigBlind());
        String drawWetnessString = dbSaveCall.getDrawWetnessLogic(gameVariables.getBoard(), actionVariables.getBoardEvaluator().getFlushStraightWetness());
        String boatWetnessString = dbSaveCall.getBoatWetnessLogic(gameVariables.getBoard(), actionVariables.getBoardEvaluator().getBoatWetness());

        String route = street + facingAction + postion + amountToCallGroup + oppAggroGroup + handStrength + strongDraw + effectiveStackString + drawWetnessString + boatWetnessString;

        return route;
    }

    public boolean bluffOddsAreOk(double sizing, double facingBetSize, double facingStackSize, double pot,
                                   double ownStackSize, List<Card> board, double ownBetSize) {
        boolean bluffOddsAreOk = false;
        double sizingInMethod;
        double sizingCopy = sizing;

        if(sizingCopy > ownStackSize + ownBetSize) {
            sizingCopy = ownStackSize + ownBetSize;
        }

        if(sizingCopy > (facingBetSize + facingStackSize)) {
            sizingInMethod = facingBetSize + facingStackSize;
        } else {
            sizingInMethod = sizingCopy;
        }

        double odds = (sizingInMethod - facingBetSize) / (facingBetSize + sizingInMethod + pot);

        if(board != null) {
            if(board.size() == 3 || board.size() == 4) {
                double ownStackAfterBluff = ownStackSize - sizingCopy;
                double facingStackAfterBluff = facingStackSize - (sizingCopy - facingBetSize);

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

    private String getTable(String actionToConsider, double handStrength, String gameType, boolean compact) {
        String table;

        if(compact) {
            if(actionToConsider.equals("call")) {
                table = "dbstats_call_" + gameType + "_compact_stackdepth";
            } else {
                if(handStrength < 0.7) {
                    table = "dbstats_bluff_" + gameType + "_compact_stackdepth";
                } else {
                    table = "dbstats_value_" + gameType + "_compact_stackdepth";
                }
            }
        } else {
            if(actionToConsider.equals("call")) {
                table = "dbstats_call_" + gameType;
            } else {
                if(handStrength < 0.7) {
                    table = "dbstats_bluff_" + gameType;
                } else {
                    table = "dbstats_value_" + gameType;
                }
            }
        }

        return table;
    }

    private String getTable_2_0(String actionToConsider, double handStrength) {
        String table_2_0;

        if(actionToConsider.equals("call")) {
            table_2_0 = "dbstats_call_sng_compact_2_0";
        } else {
            if(handStrength < 0.7) {
                table_2_0 = "dbstats_bluff_sng_compact_2_0";
            } else {
                table_2_0 = "dbstats_value_sng_compact_2_0";
            }
        }

        return table_2_0;
    }

    private String doPilotBluffingOrRaising(String currentAction, ActionVariables actionVariables, GameVariables gameVariables,
                                            boolean opponentHasInitiative, double sizing, boolean pre3BetOrPostRaisedPot) throws Exception {
        String actionToReturn;

        if(currentAction.equals("check")) {
            if(actionVariables.getBotHandStrength() < 0.7) {
                if(!opponentHasInitiative) {
                    if(bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(),
                            gameVariables.getPot(), gameVariables.getBotStack(), gameVariables.getBoard(), gameVariables.getBotBetSize())) {
                        String route = calculateCompactBluffBetOrRaiseRoute(actionVariables, gameVariables, "bet75pct", sizing);

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
                        gameVariables.getPot(), gameVariables.getBotStack(), gameVariables.getBoard(), gameVariables.getBotBetSize())) {
                    String route = calculateCompactBluffBetOrRaiseRoute(actionVariables, gameVariables, "raise", sizing);

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
                            String route = calculateCompactCallRoute(actionVariables, gameVariables);

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

        pilotBluffRaiseRoutes.add("FlopBetIpSizing_20bb_upFoldstat_66_100_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("FlopRaiseIpSizing_0-10bbFoldstat_33_66_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("FlopRaiseIpSizing_10-20bbFoldstat_33_66_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("FlopRaiseOopSizing_0-10bbFoldstat_33_66_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("FlopRaiseOopSizing_10-20bbFoldstat_66_100_StrongDrawTrue");
        pilotBluffRaiseRoutes.add("FlopRaiseOopSizing_10-20bbFoldstat_66_100_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("FlopRaiseOopSizing_10-20bbFoldstat_unknownStrongDrawTrue");
        pilotBluffRaiseRoutes.add("FlopRaiseOopSizing_20bb_upFoldstat_66_100_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("TurnBetIpSizing_10-20bbFoldstat_unknownStrongDrawFalse");
        pilotBluffRaiseRoutes.add("RiverBetIpSizing_10-20bbFoldstat_66_100_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("RiverBetOopSizing_0-10bbFoldstat_0_33_StrongDrawFalse");
        pilotBluffRaiseRoutes.add("RiverRaiseIpSizing_0-10bbFoldstat_unknownStrongDrawFalse");

        return pilotBluffRaiseRoutes;
    }

    private List<String> getPilotFloatRoutes() {
        List<String> pilotFloatRoutes = new ArrayList<>();

        pilotFloatRoutes.add("FlopFacingBetIpAtc_0-10bbAggro_33_66_HS_0_30_StrongDrawTrue");
        pilotFloatRoutes.add("FlopFacingBetIpAtc_0-10bbAggro_66_100_HS_30_50_StrongDrawTrue");
        pilotFloatRoutes.add("FlopFacingBetIpAtc_0-10bbAggro_unknownHS_30_50_StrongDrawTrue");
        pilotFloatRoutes.add("FlopFacingBetOopAtc_0-10bbAggro_0_33_HS_30_50_StrongDrawFalse");
        pilotFloatRoutes.add("FlopFacingBetOopAtc_0-10bbAggro_33_66_HS_0_30_StrongDrawTrue");
        pilotFloatRoutes.add("FlopFacingBetOopAtc_0-10bbAggro_33_66_HS_60_70_StrongDrawFalse");
        pilotFloatRoutes.add("TurnFacingBetIpAtc_0-10bbAggro_33_66_HS_60_70_StrongDrawFalse");
        pilotFloatRoutes.add("TurnFacingBetIpAtc_0-10bbAggro_66_100_HS_30_50_StrongDrawTrue");
        pilotFloatRoutes.add("TurnFacingBetIpAtc_0-10bbAggro_66_100_HS_60_70_StrongDrawFalse");
        pilotFloatRoutes.add("TurnFacingBetIpAtc_0-10bbAggro_unknownHS_0_30_StrongDrawTrue");
        pilotFloatRoutes.add("TurnFacingBetOopAtc_0-10bbAggro_33_66_HS_60_70_StrongDrawFalse");
        pilotFloatRoutes.add("TurnFacingBetOopAtc_0-10bbAggro_66_100_HS_50_60_StrongDrawFalse");
        pilotFloatRoutes.add("TurnFacingRaiseOopAtc_0-10bbAggro_66_100_HS_0_30_StrongDrawTrue");

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
                Statement st2 = con.createStatement();
                ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_call_sng_compact WHERE route = '" + oldRoute + "';");

                rs2.next();

                double success = rs2.getDouble("success");
                double total = rs2.getDouble("total");

                System.out.println(oldRoute + "      " + success + "     " + total);

                rs2.close();
                st2.close();
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("**** Copy paste routes ****");
        System.out.println();

        for(String route : newFloatPilotRoutes) {
            System.out.println("pilotFloatRoutes.add(\"" + route + "\");");
        }

        closeDbConnection();
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
                Statement st2 = con.createStatement();
                ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_bluff_sng_compact WHERE route = '" + oldRoute + "';");

                rs2.next();

                double success = rs2.getDouble("success");
                double total = rs2.getDouble("total");

                System.out.println(oldRoute + "      " + success + "     " + total);

                rs2.close();
                st2.close();
            }
        }

        System.out.println();
        System.out.println();
        System.out.println("**** Copy paste routes ****");
        System.out.println();

        for(String route : newBluffPilotRoutes) {
            System.out.println("pilotBluffRaiseRoutes.add(\"" + route + "\");");
        }

        closeDbConnection();
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

    private double getCallRatioLimit(double facingOdds) {
        double ratioLimit;

        if(facingOdds <= 0.2) {
            ratioLimit = facingOdds * 1.05;
        } else if(facingOdds <= 0.35) {
            ratioLimit = 0.44;
        } else {
            ratioLimit = 0.49;
        }

        return ratioLimit;
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
