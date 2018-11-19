package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.util.List;

/**
 * Created by LennartMac on 18/11/2018.
 */
public class DbSave {

    private String action;
    private List<Card> board;
    private double sizing;
    private double oppFoldStat;
    private String oppType;
    private int bluffSuccessNumber;
    private String stake;
    private double numberOfHands;
    private double oppLooseness;
    private double oppAggressiveness;
    private double handStrength;
    private String opponentName;
    private String date;

    public String getBoardAsString(List<Card> board) {
        String boardString = "";

        for(Card c : board) {
            boardString = boardString + c.getRank();
            boardString = boardString + c.getSuit();
        }

        return boardString;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<Card> getBoard() {
        return board;
    }

    public void setBoard(List<Card> board) {
        this.board = board;
    }

    public double getSizing() {
        return sizing;
    }

    public void setSizing(double sizing) {
        this.sizing = sizing;
    }

    public double getOppFoldStat() {
        return oppFoldStat;
    }

    public void setOppFoldStat(double oppFoldStat) {
        this.oppFoldStat = oppFoldStat;
    }

    public String getOppType() {
        return oppType;
    }

    public void setOppType(String oppType) {
        this.oppType = oppType;
    }

    public int getBluffSuccessNumber() {
        return bluffSuccessNumber;
    }

    public void setBluffSuccessNumber(int bluffSuccessNumber) {
        this.bluffSuccessNumber = bluffSuccessNumber;
    }

    public String getStake() {
        return stake;
    }

    public void setStake(String stake) {
        this.stake = stake;
    }

    public double getNumberOfHands() {
        return numberOfHands;
    }

    public void setNumberOfHands(double numberOfHands) {
        this.numberOfHands = numberOfHands;
    }

    public double getOppLooseness() {
        return oppLooseness;
    }

    public void setOppLooseness(double oppLooseness) {
        this.oppLooseness = oppLooseness;
    }

    public double getOppAggressiveness() {
        return oppAggressiveness;
    }

    public void setOppAggressiveness(double oppAggressiveness) {
        this.oppAggressiveness = oppAggressiveness;
    }

    public double getHandStrength() {
        return handStrength;
    }

    public void setHandStrength(double handStrength) {
        this.handStrength = handStrength;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
