package com.lennart.model.computergame;

import com.lennart.model.card.Card;
import com.lennart.model.rangebuilder.RangeBuildable;
import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lpo21630 on 15-3-2017.
 */
public class OpponentRangeSetter {

    private Map<String, String> actionHistoryOfHand;
    private int actionCounter;
    private int botActionCounter;
    private int opponentActionCounter;
    private List<Card> board;

    private void setCorrectOpponentRange(RangeBuildable rangeBuildable) {
        String streetOfBotLastAction = getStreetOfBotLastAction();
        String currentStreet = getCurrentStreet();

        if(streetOfBotLastAction.equals(currentStreet)) {
            //calculate range with current board
        } else {
            //calculate range with previous street board
        }
    }

    private String getStreetOfBotLastAction() {
        String streetOfBotLastAction = null;
        String botLastAction = actionHistoryOfHand.get(botActionCounter + " computer");

        if(botLastAction.contains("preflop")) {
            streetOfBotLastAction = "preflop";
        } else if(botLastAction.contains("flop")) {
            streetOfBotLastAction = "flop";
        } else if(botLastAction.contains("turn")) {
            streetOfBotLastAction = "turn";
        } else if(botLastAction.contains("river")) {
            streetOfBotLastAction = "river";
        }
        return streetOfBotLastAction;
    }

    private void updateActionHistoryOfHand(String player, String action) {
        if(actionHistoryOfHand == null) {
            actionHistoryOfHand = new HashMap<>();
            actionCounter = 0;
            botActionCounter = 0;
            opponentActionCounter = 0;
        }

        actionCounter++;
        if(player.equals("computer")) {
            botActionCounter++;
        } else if(player.equals("opponent")) {
            opponentActionCounter++;
        }

        if(player.equals("computer")) {
            action = setActionToCorrectFormat(action);
        }

        actionHistoryOfHand.put(getActionHistoryOfHandKey(player), actionCounter + " " + getCurrentStreet() + " " + action);
    }

    private String getActionHistoryOfHandKey(String player) {
        String key = null;
        if(player.equals("computer")) {
            key = botActionCounter + " " + player;
        } else if(player.equals("opponent")) {
            key = opponentActionCounter + " " + player;
        }
        return key;
    }

    private String getCurrentStreet() {
        String street = null;
        if(board == null) {
            street = "preflop";
        } else if(board.size() == 3) {
            street = "flop";
        } else if(board.size() == 4) {
            street = "turn";
        } else if(board.size() == 5) {
            street = "river";
        }
        return street;
    }

    private String setActionToCorrectFormat(String action) {
        if(action.contains("fold")) {
            action = "fold";
        } else if(action.contains("check")) {
            action = "check";
        } else if(action.contains("call")) {
            action = "call";
        } else if(action.contains("bet")) {
            action = "bet";
        } else if(action.contains("raise")) {
            action = "raise";
        }
        return action;
    }

    private int getNumberOfActionsDoneByOpponentSinceBotLastAction() {
        String valueCorrespondingToLastBotActionKey = actionHistoryOfHand.get(botActionCounter + " computer");
        int actionCounterCorrespondingToLastBotAction = Integer.valueOf(valueCorrespondingToLastBotActionKey.substring(0, 1));
        return actionCounter - actionCounterCorrespondingToLastBotAction;
    }

    private String getStreetAtWhichOpponentLastActed() {
        String streetAtWhichOpponentLastActed = null;
        String lastActionHistoryValueOfOpponent = actionHistoryOfHand.get(opponentActionCounter + " opponent");

        if(lastActionHistoryValueOfOpponent.contains("preflop")) {
            streetAtWhichOpponentLastActed = "preflop";
        } else if(lastActionHistoryValueOfOpponent.contains("flop")) {
            streetAtWhichOpponentLastActed = "flop";
        } else if(lastActionHistoryValueOfOpponent.contains("turn")) {
            streetAtWhichOpponentLastActed = "turn";
        } else if(lastActionHistoryValueOfOpponent.contains("river")) {
            streetAtWhichOpponentLastActed = "river";
        }
        return streetAtWhichOpponentLastActed;
    }


}
