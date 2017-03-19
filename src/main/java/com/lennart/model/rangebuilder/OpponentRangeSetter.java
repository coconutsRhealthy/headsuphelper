package com.lennart.model.rangebuilder;

import com.lennart.model.card.Card;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lpo21630 on 15-3-2017.
 */
public class OpponentRangeSetter {

    private List<Card> currentFlopCards;
    private Card currentTurnCard;
    private Card currentRiverCard;
    private List<Card> currentBoard;
    private Set<Card> currentKnownGameCards;
    private RangeBuilder rangeBuilder;

    public void setCorrectOpponentRange(RangeBuildable rangeBuildable) {
        String streetOfBotLastAction = getStreetOfBotLastAction(rangeBuildable);
        String currentStreet = getCurrentStreet(rangeBuildable);

        if(streetOfBotLastAction.equals(currentStreet)) {
            setRange(rangeBuildable);
        } else {
            String botLastAction = getBotLastAction(rangeBuildable);

            if(botLastAction.equals("bet") || botLastAction.equals("raise")) {
                setBoardToPreviousStreet(rangeBuildable);
                setRange(rangeBuildable);
                setBoardBackToCurrentStreet(rangeBuildable);
            } else {
                setRange(rangeBuildable);
            }
        }
    }

    private String getStreetOfBotLastAction(RangeBuildable rangeBuildable) {
        String streetOfBotLastAction = null;
        List<String> botActionHistory = rangeBuildable.getBotActionHistory();

        if(botActionHistory != null) {
            String botLastAction = botActionHistory.get(botActionHistory.size() - 1);

            if(botLastAction.contains("preflop")) {
                streetOfBotLastAction = "preflop";
            } else if(botLastAction.contains("flop")) {
                streetOfBotLastAction = "flop";
            } else if(botLastAction.contains("turn")) {
                streetOfBotLastAction = "turn";
            } else if(botLastAction.contains("river")) {
                streetOfBotLastAction = "river";
            }
        } else {
            streetOfBotLastAction = "preflop";
        }
        return streetOfBotLastAction;
    }

    private String getBotLastAction(RangeBuildable rangeBuildable) {
        String action = null;
        List<String> botActionHistory = rangeBuildable.getBotActionHistory();
        String botLastAction = botActionHistory.get(botActionHistory.size() - 1);

        if(botLastAction.contains("fold")) {
            action = "fold";
        } else if(botLastAction.contains("check")) {
            action = "check";
        } else if(botLastAction.contains("call")) {
            action = "call";
        } else if(botLastAction.contains("bet")) {
            action = "bet";
        } else if(botLastAction.contains("raise")) {
            action = "raise";
        }
        return action;
    }

    private String getCurrentStreet(RangeBuildable rangeBuildable) {
        List<Card> board = rangeBuildable.getBoard();

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

    private void setBoardToPreviousStreet(RangeBuildable rangeBuildable) {
        if(rangeBuildable.getFlopCards() != null) {
            currentFlopCards = new ArrayList<>();
            currentFlopCards.addAll(rangeBuildable.getFlopCards());
        }
        if(rangeBuildable.getTurnCard() != null) {
            currentTurnCard = new Card(rangeBuildable.getTurnCard().getRank(), rangeBuildable.getTurnCard().getSuit());
        }
        if(rangeBuildable.getRiverCard() != null) {
            currentRiverCard = new Card(rangeBuildable.getRiverCard().getRank(), rangeBuildable.getRiverCard().getSuit());
        }
        if(rangeBuildable.getBoard() != null) {
            currentBoard = new ArrayList<>();
            currentBoard.addAll(rangeBuildable.getBoard());
        }
        if(rangeBuildable.getKnownGameCards() != null) {
            currentKnownGameCards = new HashSet<>();
            currentKnownGameCards.addAll(rangeBuildable.getKnownGameCards());
        }

        if(currentBoard == null) {
            //Should never come here
        } else if(currentBoard.size() == 3) {
            Set<Card> modifiedKnownGameCards = new HashSet<>();
            modifiedKnownGameCards.addAll(currentKnownGameCards);
            modifiedKnownGameCards.removeAll(currentFlopCards);
            rangeBuildable.setKnownGameCards(modifiedKnownGameCards);

            rangeBuildable.setFlopCards(null);
            rangeBuildable.setBoard(null);
        } else if(currentBoard.size() == 4) {
            Set<Card> modifiedKnownGameCards = new HashSet<>();
            modifiedKnownGameCards.addAll(currentKnownGameCards);
            modifiedKnownGameCards.remove(currentTurnCard);
            rangeBuildable.setKnownGameCards(modifiedKnownGameCards);

            List<Card> modifiedBoard = new ArrayList<>();
            modifiedBoard.addAll(currentBoard);
            modifiedBoard.remove(currentTurnCard);
            rangeBuildable.setBoard(modifiedBoard);

            rangeBuildable.setTurnCard(null);
        } else if(currentBoard.size() == 5) {
            Set<Card> modifiedKnownGameCards = new HashSet<>();
            modifiedKnownGameCards.addAll(currentKnownGameCards);
            modifiedKnownGameCards.remove(currentRiverCard);
            rangeBuildable.setKnownGameCards(modifiedKnownGameCards);

            List<Card> modifiedBoard = new ArrayList<>();
            modifiedBoard.addAll(currentBoard);
            modifiedBoard.remove(currentRiverCard);
            rangeBuildable.setBoard(modifiedBoard);

            rangeBuildable.setRiverCard(null);
        }
    }

    private void setBoardBackToCurrentStreet(RangeBuildable rangeBuildable) {
        if(currentFlopCards != null) {
            rangeBuildable.setFlopCards(currentFlopCards);
        }
        if(currentTurnCard != null) {
            rangeBuildable.setTurnCard(currentTurnCard);
        }
        if(currentRiverCard != null) {
            rangeBuildable.setRiverCard(currentRiverCard);
        }
        if(currentBoard != null) {
            rangeBuildable.setBoard(currentBoard);
        }
        if(currentKnownGameCards != null) {
            rangeBuildable.setKnownGameCards(currentKnownGameCards);
        }
    }

    private void setRange(RangeBuildable rangeBuildable) {
        rangeBuilder = new RangeBuilder(rangeBuildable, true);
        rangeBuildable.setOpponentRange(rangeBuilder.getOpponentRange());
    }

    public RangeBuilder getRangeBuilder() {
        return rangeBuilder;
    }
}
