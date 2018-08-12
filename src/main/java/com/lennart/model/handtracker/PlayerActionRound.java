package com.lennart.model.handtracker;

import com.lennart.model.card.Card;

import java.util.List;

public class PlayerActionRound {

    private String playerName;
    private List<Card> board;
    private double totalBotBetSize;
    private double totalOpponentBetSize;
    private String action;
    private String street;

    public PlayerActionRound(String playerName, List<Card> board, double totalBotBetSize, double totalOpponentBetSize, String street, String action) {
        this.playerName = playerName;
        this.board = board;
        this.totalBotBetSize = totalBotBetSize;
        this.totalOpponentBetSize = totalOpponentBetSize;
        this.street = street;
        this.action = action;
    }

    public String getPlayerName() {
        return playerName;
    }

    public List<Card> getBoard() {
        return board;
    }

    public double getTotalBotBetSize() {
        return totalBotBetSize;
    }

    public double getTotalOpponentBetSize() {
        return totalOpponentBetSize;
    }

    public String getAction() {
        return action;
    }

    public String getStreet() {
        return street;
    }
}
