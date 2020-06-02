package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.action.actionbuilders.ai.Sizing;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSave;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveRaw;
import com.lennart.model.card.Card;
import com.lennart.model.handtracker.ActionRequest;
import com.lennart.model.handtracker.PlayerActionRound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LennartMac on 25/05/2020.
 */
public class BotActionBuilder {

    double sizing;

    public static void main(String[] args) {
        new BotActionBuilder().testMethod();
    }

    private void testMethod() {
        ContinuousTable continuousTable = new ContinuousTable();
        GameVariables gameVariables = new GameVariables();

        //////1
        gameVariables.setBotHoleCards(Arrays.asList(new Card(5, 's'), new Card(6, 'h')));
        gameVariables.setBoard(new ArrayList<>());
        gameVariables.setOpponentAction("bet");
        gameVariables.setBotIsButton(true);
        gameVariables.setBotStack(990);
        gameVariables.setOpponentStack(980);
        gameVariables.setBotBetSize(10);
        gameVariables.setOpponentBetSize(20);
        gameVariables.setBigBlind(20);
        gameVariables.setPot(0);
        gameVariables.setOpponentName("TestNewEquityStyle");

        PlayerActionRound playerActionRound1_1 = new PlayerActionRound("bot", new ArrayList<>(), gameVariables.getBigBlind() / 2, gameVariables.getBigBlind(), "preflop", "postSB");
        PlayerActionRound playerActionRound1_2 = new PlayerActionRound("opponent", new ArrayList<>(), gameVariables.getBigBlind() / 2, gameVariables.getBigBlind(), "preflop", "bet");
        PlayerActionRound playerActionRound1_3 = new PlayerActionRound("bot", new ArrayList<>(), 42, gameVariables.getBigBlind(), "preflop", "raise");
        PlayerActionRound playerActionRound1_4 = new PlayerActionRound("opponent", new ArrayList<>(), 42, gameVariables.getBigBlind(), "preflop", "call");

        ActionRequest actionRequest1 = new ActionRequest(gameVariables.getAllActionRequestsOfHand(), 10, gameVariables.getBoard(), gameVariables.isBotIsButton(), gameVariables.getBigBlind());

        actionRequest1.getActionsSinceLastRequest().add(playerActionRound1_1);
        actionRequest1.getActionsSinceLastRequest().add(playerActionRound1_2);
        actionRequest1.getActionsSinceLastRequest().add(playerActionRound1_3);
        actionRequest1.getActionsSinceLastRequest().add(playerActionRound1_4);

        gameVariables.getAllActionRequestsOfHand().add(actionRequest1);

        RangeConstructor rangeConstructor = new RangeConstructor();
        new OpponentRangeSetter(rangeConstructor, new InputProvider()).setOpponentRange(continuousTable, gameVariables);




        //////2
        gameVariables.setBoard(Arrays.asList(new Card(8, 'd'), new Card(12, 'd'), new Card(11, 'c')));
        gameVariables.setOpponentAction("check");
        gameVariables.setBotStack(958);
        gameVariables.setOpponentStack(958);
        gameVariables.setBotBetSize(0);
        gameVariables.setOpponentBetSize(0);
        gameVariables.setPot(84);

        PlayerActionRound playerActionRound2_1 = new PlayerActionRound("bot", gameVariables.getBoard(), 0, 0, "flop", "check");
        PlayerActionRound playerActionRound2_2 = new PlayerActionRound("opponent", gameVariables.getBoard(), 0, 0, "flop", "check");

        ActionRequest actionRequest2 = new ActionRequest(gameVariables.getAllActionRequestsOfHand(), 84, gameVariables.getBoard(), gameVariables.isBotIsButton(), gameVariables.getBigBlind());

        actionRequest2.getActionsSinceLastRequest().add(playerActionRound2_1);
        actionRequest2.getActionsSinceLastRequest().add(playerActionRound2_2);

        gameVariables.getAllActionRequestsOfHand().add(actionRequest2);

        List<DbSave> dbSaveList = new ArrayList<>();
        DbSaveRaw dbSaveRaw = new DbSaveRaw();
        dbSaveRaw.setBoard("");
        dbSaveRaw.setBotAction("raise");
        dbSaveRaw.setSizing(42);
        dbSaveList.add(dbSaveRaw);
        continuousTable.setDbSaveList(dbSaveList);

        RangeConstructor rangeConstructor2 = new RangeConstructor();
        new OpponentRangeSetter(rangeConstructor2, new InputProvider()).setOpponentRange(continuousTable, gameVariables);





        //////3
        gameVariables.setBoard(Arrays.asList(new Card(8, 'd'), new Card(12, 'd'), new Card(11, 'c'), new Card(8, 'c')));
        gameVariables.setOpponentAction("check");
        gameVariables.setBotStack(958);
        gameVariables.setOpponentStack(958);
        gameVariables.setBotBetSize(0);
        gameVariables.setOpponentBetSize(0);
        gameVariables.setPot(84);

        PlayerActionRound playerActionRound3_1 = new PlayerActionRound("bot", gameVariables.getBoard(), 0, 0, "turn", "check");
        PlayerActionRound playerActionRound3_2 = new PlayerActionRound("opponent", gameVariables.getBoard(), 0, 0, "turn", "check");

        ActionRequest actionRequest3 = new ActionRequest(gameVariables.getAllActionRequestsOfHand(), 84, gameVariables.getBoard(), gameVariables.isBotIsButton(), gameVariables.getBigBlind());

        actionRequest3.getActionsSinceLastRequest().add(playerActionRound3_1);
        actionRequest3.getActionsSinceLastRequest().add(playerActionRound3_2);

        gameVariables.getAllActionRequestsOfHand().add(actionRequest3);

        DbSaveRaw dbSaveRaw2 = new DbSaveRaw();
        dbSaveRaw2.setBoard("8d12d11c");
        dbSaveRaw2.setBotAction("check");
        dbSaveRaw2.setSizing(0);
        continuousTable.getDbSaveList().add(dbSaveRaw2);

        RangeConstructor rangeConstructor3 = new RangeConstructor();
        new OpponentRangeSetter(rangeConstructor3, new InputProvider()).setOpponentRange(continuousTable, gameVariables);




        ////////4
        gameVariables.setBoard(Arrays.asList(new Card(8, 'd'), new Card(12, 'd'), new Card(11, 'c'), new Card(8, 'c'), new Card(2, 's')));
        gameVariables.setOpponentAction("check");
        gameVariables.setBotStack(958);
        gameVariables.setOpponentStack(958);
        gameVariables.setBotBetSize(0);
        gameVariables.setOpponentBetSize(0);
        gameVariables.setPot(84);

        DbSaveRaw dbSaveRaw3 = new DbSaveRaw();
        dbSaveRaw3.setBoard("8d12d11c8c");
        dbSaveRaw3.setBotAction("check");
        dbSaveRaw3.setSizing(0);
        continuousTable.getDbSaveList().add(dbSaveRaw3);

        RangeConstructor rangeConstructor4 = new RangeConstructor();
        new OpponentRangeSetter(rangeConstructor4, new InputProvider()).setOpponentRange(continuousTable, gameVariables);

        String action = getAction(continuousTable, gameVariables, rangeConstructor4);

        System.out.println("ACTION: " + action);

//
//
//        PlayerActionRound playerActionRound3_1 = new PlayerActionRound("opponent", Arrays.asList(new Card(8, 'd'), new Card(12, 'd'), new Card(11, 'c'), new Card(8, 'c')), 0, 0, "turn", "check");
//        PlayerActionRound playerActionRound3_2 = new PlayerActionRound("bot", Arrays.asList(new Card(8, 'd'), new Card(12, 'd'), new Card(11, 'c'), new Card(8, 'c')), 0, 0, "turn", "check");
//
//        ActionRequest actionRequest3 = new ActionRequest(gameVariables.getAllActionRequestsOfHand(), 84, gameVariables.getBoard(), gameVariables.isBotIsButton(), gameVariables.getBigBlind());
//
//        actionRequest2.getActionsSinceLastRequest().add(playerActionRound3_1);
//        actionRequest2.getActionsSinceLastRequest().add(playerActionRound3_2);
//
//        gameVariables.getAllActionRequestsOfHand().add(actionRequest2);
//        new OpponentRangeSetter(rangeConstructor, new InputProvider()).setOpponentRange(continuousTable, gameVariables);
//
//        List<DbSave> dbSaveList = new ArrayList<>();
//        DbSaveRaw dbSaveRaw = new DbSaveRaw();
//        dbSaveRaw.setBoard("");
//        dbSaveRaw.setBotAction("raise");
//        dbSaveRaw.setSizing(160);
//        dbSaveList.add(dbSaveRaw);
//        continuousTable.setDbSaveList(dbSaveList);
//
//
//
//        new OpponentRangeSetter(rangeConstructor, new InputProvider()).setOpponentRange(continuousTable, gameVariables);
//
//        String action = getAction(continuousTable, gameVariables, rangeConstructor);
//
//        System.out.println(action);
    }

    public String getAction(ContinuousTable continuousTable, GameVariables gameVariables, RangeConstructor rangeConstructor) {
        String action = null;
        Rules rules = new Rules();

        setOpponentHasInitiative(gameVariables.getOpponentAction(), continuousTable, gameVariables);
        List<String> eligibleActions = getEligibleActions(gameVariables);
        sizing = getSizing(gameVariables);

        action = rules.getInitialRuleAction(gameVariables, continuousTable.isOpponentHasInitiative(), eligibleActions);

        System.out.println("initial: " + action);

        double botEquity = -3;

        if(action == null) {
            InputProvider inputProvider = new InputProvider();
            PreflopEquityHs preflopEquityHs = new PreflopEquityHs();
            EquityAction equityAction = new EquityAction(inputProvider, preflopEquityHs, rangeConstructor);

            action = equityAction.getValueAction(continuousTable, gameVariables, eligibleActions, sizing);

            System.out.println("value: " + action);

            action = rules.getValueTrapAction(action, gameVariables);

            System.out.println("valuetrap: " + action);

            if(action.equals("fold") || action.equals("check")) {
                if(!rules.isValueTrap()) {
                    action = new BluffAction(equityAction, inputProvider, rangeConstructor, preflopEquityHs).getBluffAction(
                            action,
                            eligibleActions,
                            continuousTable,
                            gameVariables,
                            sizing);
                    System.out.println("bluffaction: " + action);
                }
            }

            action = rules.getAfterRuleAction(action, rangeConstructor, getFacingOdds(gameVariables),
                    gameVariables.getBoard(), gameVariables, continuousTable);

            System.out.println("afterrules: " + action);

            botEquity = equityAction.getBotEquity();
        }

        Administration administration = new Administration();
        administration.doDbSaveStuff(action, continuousTable, gameVariables, sizing, rangeConstructor, botEquity);
        administration.doActionRoundStuff(action, gameVariables, sizing);

        return action;
    }

    private void setOpponentHasInitiative(String opponentAction, ContinuousTable continuousTable, GameVariables gameVariables) {
        if(continuousTable != null) {
            if(opponentAction != null) {
                if(opponentAction.equals("empty")) {
                    List<ActionRequest> allActionRequestsOfHand = gameVariables.getAllActionRequestsOfHand();
                    ActionRequest secondLastActionRequest = allActionRequestsOfHand.get(allActionRequestsOfHand.size() - 2);
                    PlayerActionRound botLastActionRound = secondLastActionRequest.getMostRecentActionRoundOfPLayer(secondLastActionRequest.getActionsSinceLastRequest(), "bot");
                    String botLastAction = botLastActionRound.getAction();

                    if(botLastAction.equals("call")) {
                        continuousTable.setOpponentHasInitiative(true);
                    } else {
                        continuousTable.setOpponentHasInitiative(false);
                    }
                } else {
                    if(opponentAction.equals("bet75pct") || opponentAction.equals("raise")) {
                        continuousTable.setOpponentHasInitiative(true);
                    } else {
                        continuousTable.setOpponentHasInitiative(false);
                    }
                }
            }
        }
    }

    private List<String> getEligibleActions(GameVariables gameVariables) {
        List<String> eligibleActions = new ArrayList<>();

        if(gameVariables.getOpponentAction().contains("bet") || gameVariables.getOpponentAction().contains("raise")) {
            if(gameVariables.getOpponentStack() == 0 ||
                    (gameVariables.getBotStack() + gameVariables.getBotBetSize()) <= gameVariables.getOpponentBetSize()) {
                eligibleActions.add("fold");
                eligibleActions.add("call");
            } else {
                eligibleActions.add("fold");
                eligibleActions.add("call");
                eligibleActions.add("raise");
            }
        } else {
            eligibleActions.add("check");

            if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
                eligibleActions.add("raise");
            } else {
                eligibleActions.add("bet75pct");
            }
        }

        return eligibleActions;
    }

    private double getSizing(GameVariables gameVariables) {
        return new Sizing().getAiBotSizing(gameVariables.getOpponentBetSize(), gameVariables.getBotBetSize(),
                gameVariables.getBotStack(), gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBigBlind(),
                gameVariables.getBoard(), -1.0, false, false);
    }

    private double getFacingOdds(GameVariables gameVariables) {
        double opponentBetSize = gameVariables.getOpponentBetSize();
        double botBetSize = gameVariables.getBotBetSize();
        double botStack = gameVariables.getBotStack();

        if((opponentBetSize - botBetSize) > botStack) {
            opponentBetSize = botStack;
        }

        double facingOdds = (opponentBetSize - botBetSize) / (gameVariables.getPot() + botBetSize + opponentBetSize);
        return facingOdds;
    }

    public double getSizing() {
        return sizing;
    }
}
