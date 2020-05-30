package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.action.actionbuilders.ai.Sizing;
import com.lennart.model.handtracker.ActionRequest;
import com.lennart.model.handtracker.PlayerActionRound;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 25/05/2020.
 */
public class BotActionBuilder {

    public String getAction(ContinuousTable continuousTable, GameVariables gameVariables) {
        String action = null;
        Rules rules = new Rules();

        setOpponentHasInitiative(gameVariables.getOpponentAction(), continuousTable, gameVariables);
        List<String> eligibleActions = getEligibleActions(gameVariables);
        double sizing = getSizing(gameVariables);

        action = rules.getInitialRuleAction(gameVariables, continuousTable.isOpponentHasInitiative(), eligibleActions);

        RangeConstructor rangeConstructor = new RangeConstructor();
        double botEquity = -3;

        if(action == null) {
            InputProvider inputProvider = new InputProvider();
            PreflopEquityHs preflopEquityHs = new PreflopEquityHs();
            EquityAction equityAction = new EquityAction(inputProvider, preflopEquityHs, rangeConstructor);

            action = equityAction.getValueAction(continuousTable, gameVariables, eligibleActions, sizing);

            action = rules.getValueTrapAction(action, gameVariables);

            if(action.equals("fold") || action.equals("check")) {
                if(!rules.isValueTrap()) {
                    action = new BluffAction(equityAction, inputProvider, rangeConstructor, preflopEquityHs).getBluffAction(
                            action,
                            eligibleActions,
                            continuousTable,
                            gameVariables,
                            sizing);
                }
            }

            action = rules.getAfterRuleAction(action, rangeConstructor, getFacingOdds(gameVariables), gameVariables.getBoard());
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
}
