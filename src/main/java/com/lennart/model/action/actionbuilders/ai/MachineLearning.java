package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveBluff;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveCall;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePersister;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveValue;
import com.lennart.model.action.actionbuilders.ai.dbstatsraw.DbStatsRawBluffPostflopMigrator;
import com.lennart.model.card.Card;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MachineLearning {

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

        return actionToReturn;
    }

    private String adjustCheckAction(ActionVariables actionVariables, GameVariables gameVariables,
                                     boolean opponentHasInitiative, double sizing) throws Exception {
        String actionToReturn;

        if(!opponentHasInitiative) {
            if(actionVariables.getBotHandStrength() < 0.7) {
                if(bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(),
                        gameVariables.getPot(), gameVariables.getBotStack(), gameVariables.getBoard(), gameVariables.getBotBetSize())) {
                    String compactBluffRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "bet75pct", sizing);
                    System.out.println("##ComapctRoute_2_0: " + compactBluffRoute_2_0);
                    List<Double> bluffBetData = getDataFromDb("bet75pct", actionVariables.getBotHandStrength(), compactBluffRoute_2_0);
                    actionToReturn = changeToBetOrKeepCheckGivenData(bluffBetData, compactBluffRoute_2_0);
                } else {
                    actionToReturn = actionVariables.getAction();
                }
            } else {
                if(gameVariables.getBoard() != null && gameVariables.getBoard().size() == 5) {
                    if(gameVariables.isBotIsButton()) {
                        String compactValueRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "bet75pct", sizing);
                        System.out.println("##CompactValueRoute_2_0: " + compactValueRoute_2_0);
                        List<Double> valueBetData = getDataFromDb("bet75pct", actionVariables.getBotHandStrength(), compactValueRoute_2_0);
                        actionToReturn = changeToBetOrKeepCheckGivenData(valueBetData, compactValueRoute_2_0);
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
                String compactRoute_2_0;

                if(actionVariables.getBotHandStrength() < 0.7) {
                    compactRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
                } else {
                    compactRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
                }

                System.out.println("##CompactRoute_2_0: " + compactRoute_2_0);
                List<Double> raiseData = getDataFromDb("raise", actionVariables.getBotHandStrength(), compactRoute_2_0);

                if(raiseData.get(1) >= 20) {
                    double raiseSuccessRatio = raiseData.get(0) / raiseData.get(1);

                    if(raiseSuccessRatio >= 0.57) {
                        actionToReturn = "raise";
                        System.out.println("Machinelearning I) Changed fold to raise");
                    }
                }
            }
        }

        if(actionToReturn == null) {
            String compactCallRoute_2_0 = calculateCompactCallRoute_2_0(actionVariables, gameVariables);
            System.out.println("##CompactCallRoute_2_0: " + compactCallRoute_2_0);
            List<Double> callData = getDataFromDb("call", actionVariables.getBotHandStrength(), compactCallRoute_2_0);

            if(callData.get(1) >= 20) {
                double callSuccessRatio = callData.get(0) / callData.get(1);
                double facingOdds = actionVariables.getFacingOdds(gameVariables);

                if(callSuccessRatio > facingOdds) {
                    actionToReturn = "call";
                    System.out.println("Machinelearning J) Changed fold to call");
                }
            } else {
                //actionToReturn = doFreakyCallMachineLearning(actionVariables, gameVariables);

                if(actionToReturn != null && actionToReturn.equals("call")) {
                    System.out.println("Machinelearning K) (freaky) Changed fold to call");
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

        String compactRoute_2_0;

        if(actionVariables.getBotHandStrength() < 0.7) {
            compactRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "bet75pct", sizing);
            System.out.println("##ComactRoute_2_0: " + compactRoute_2_0);
        } else {
            compactRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "bet75pct", sizing);
        }

        List<Double> betData = getDataFromDb("bet75pct", actionVariables.getBotHandStrength(), compactRoute_2_0);

        if(betData.get(1) >= 20) {
            double ratio = betData.get(0) / betData.get(1);

            if(ratio < 0.51) {
                double random = Math.random();

                if(random < 0.75) {
                    System.out.println("Machinelearning A) Changed bet to check");
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
            String compactBluffRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
            System.out.println("##CompactRoute_2_0: " + compactBluffRoute_2_0);
            List<Double> bluffRaiseData = getDataFromDb("raise", actionVariables.getBotHandStrength(), compactBluffRoute_2_0);
            actionToReturn = changeToFoldOrCallOrKeepRaiseGivenData(bluffRaiseData, actionVariables, gameVariables,
                    continuousTable);
        } else {
            String compactValueRaiseRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
            System.out.println("##CompactValueRaiseRoute_2_0: " + compactValueRaiseRoute_2_0);
            List<Double> valueRaiseData = getDataFromDb("raise", actionVariables.getBotHandStrength(), compactValueRaiseRoute_2_0);
            actionToReturn = changeToFoldOrCallOrKeepRaiseGivenData(valueRaiseData, actionVariables, gameVariables,
                    continuousTable);
        }

        return actionToReturn;
    }

    private String adjustCallAction(ActionVariables actionVariables, GameVariables gameVariables, double sizing,
                                    boolean pre3BetOrPostRaisedPot) throws Exception {
        String actionToReturn;

        String compactCallRoute_2_0 = calculateCompactCallRoute_2_0(actionVariables, gameVariables);
        System.out.println("##CompactCallRoute_2_0: " + compactCallRoute_2_0);
        List<Double> callData = getDataFromDb("call", actionVariables.getBotHandStrength(), compactCallRoute_2_0);
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
                if(route.contains("Sizing_0-5bb") || route.contains("Sizing_5-10bb")) {
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
            String compactCallRoute_2_0 = calculateCompactCallRoute_2_0(actionVariables, gameVariables);
            System.out.println("##CompactCallRoute_2_0: " + compactCallRoute_2_0);
            List<Double> callData = getDataFromDb("call", actionVariables.getBotHandStrength(), compactCallRoute_2_0);

            if(callData.get(1) >= 20) {
                double callSuccessRatio = callData.get(0) / callData.get(1);
                double facingOdds = actionVariables.getFacingOdds(gameVariables);

                if (callSuccessRatio > facingOdds) {
                    actionToReturn = "call";
                    System.out.println("Machinelearning B) Changed raise to call");
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
            double callLimit = getCallRatioLimit(facingOdds);

            System.out.println("callLimit: " + callLimit);

            if(callRatio >= callLimit) {
                System.out.println("callRatio above callLimit, callratio: " + callRatio + "   callLimit: " + callLimit);
                actionToReturn = "call";
            } else {
                double random = Math.random();

                if(gameVariables.isBotIsButton()) {
                    if(random < 0.8) {
                        System.out.println("MachineLearning change postflop call A");
                    } else {
                        actionToReturn = "call";
                    }
                } else {
                    if(random < 0.8) {
                        System.out.println("MachineLearning change postflop call A");
                    } else {
                        actionToReturn = "call";
                    }
                }
            }
        } else {
            actionToReturn = "call";
        }

        if(actionToReturn == null) {
            if(raiseIsEligible(gameVariables.getBoard(), pre3BetOrPostRaisedPot)) {
                if(actionVariables.getBotHandStrength() >= 0.7 ||
                        bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(),
                                gameVariables.getPot(), gameVariables.getBotStack(), gameVariables.getBoard(), gameVariables.getBotBetSize())) {

                    String compactRaiseRoute_2_0;

                    if(actionVariables.getBotHandStrength() >= 0.7) {
                        compactRaiseRoute_2_0 = calculateCompactValueBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
                    } else {
                        compactRaiseRoute_2_0 = calculateCompactBluffBetOrRaiseRoute_2_0(actionVariables, gameVariables, "raise", sizing);
                    }

                    System.out.println("##ComapctRoute_2_0: " + compactRaiseRoute_2_0);
                    List<Double> raiseData = getDataFromDb("raise", actionVariables.getBotHandStrength(), compactRaiseRoute_2_0);

                    if (raiseData.get(1) >= 20) {
                        double raiseSuccessRatio = raiseData.get(0) / raiseData.get(1);

                        if (raiseSuccessRatio > 0.57) {
                            actionToReturn = "raise";
                            System.out.println("Machinelearning F) Changed call to raise");
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

    private List<Double> getDataFromDb(String actionToConsider, double handStrength, String compactRoute_2_0) throws Exception {
        List<Double> valuesToReturn = new ArrayList<>();

        if(compactRoute_2_0 != null) {
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
            double successNumber = 0;
            double totalNumber = 0;

            System.out.println("postflop machinelearning 2_0 data too small for route: " + compactRoute_2_0);

            valuesToReturn.add(successNumber);
            valuesToReturn.add(totalNumber);
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
        String sizingGroup = new DbStatsRawBluffPostflopMigrator().getSizingGroup(sizing, gameVariables.getBigBlind());
        String strongDraw = dbSaveBluff.getStrongDrawLogic(actionVariables.getHandEvaluator().hasDrawOfType("strongFlushDraw"), actionVariables.getHandEvaluator().hasDrawOfType("strongOosd"));
        String effectiveStack = new DbSavePersister().convertEffectiveStackToCompact(
                dbSaveBluff.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(),
                        gameVariables.getOpponentStack() / gameVariables.getBigBlind()));

        String opponentType = new GameFlow().getOpponentGroup(gameVariables.getOpponentName());

        String route = street + bluffAction + position + sizingGroup + strongDraw + effectiveStack + opponentType;

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
        String sizingGroup = new DbStatsRawBluffPostflopMigrator().getSizingGroup(sizing, gameVariables.getBigBlind());
        String handStrength = dbSaveValue.getHandStrengthLogic(actionVariables.getBotHandStrength());
        String effectiveStack = new DbSavePersister().convertEffectiveStackToCompact(
                dbSaveValue.getEffectiveStackLogic(gameVariables.getBotStack() / gameVariables.getBigBlind(),
                        gameVariables.getOpponentStack() / gameVariables.getBigBlind()));

        String opponentType = new GameFlow().getOpponentGroup(gameVariables.getOpponentName());

        String route = street + valueAction + position + sizingGroup + handStrength + effectiveStack + opponentType;

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

        String opponentType = new GameFlow().getOpponentGroup(gameVariables.getOpponentName());

        String route = street + facingAction + position + amountToCallGroup + handStrength + strongDraw +
                effectiveStack + opponentType;

        while(StringUtils.countMatches(route, "OpponentUnknown") > 1) {
            route = route.substring(0, route.lastIndexOf("OpponentUnknown"));
        }

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

    private void initialize_2_0_DbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con_2_0 = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker_2_0?&serverTimezone=UTC", "root", "");
    }

    private void close_2_0_DbConnection() throws SQLException {
        con_2_0.close();
    }
}
