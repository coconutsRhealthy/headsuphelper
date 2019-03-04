package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.action.actionbuilders.ai.GameFlow;
import com.lennart.model.card.Card;

import java.sql.*;
import java.util.List;

/**
 * Created by LennartMac on 13/01/2019.
 */
public class DbSaveRaw extends DbSave {

    private String botAction;
    private String oppAction;
    private String board;
    private String holeCards;
    private double handStrength;
    private double botStack;
    private double opponentStack;
    private double botTotalBetSize;
    private double opponentTotalBetSize;
    private double sizing;
    private String position;
    private String stake;
    private String opponentName;
    private String opponentData;
    private double bigBlind;
    private String strongDraw;
    private double recentHandsWon;
    private String adjustedOppType;

    private Connection con;

    //logic getters
    public String getBoardLogic(List<Card> board) {
        String boardString = "";

        for(Card c : board) {
            boardString = boardString + c.getRank();
            boardString = boardString + c.getSuit();
        }

        return boardString;
    }

    public String getHoleCardsLogic(List<Card> holeCards) {
        String holeCardsString = "";

        for(Card c : holeCards) {
            holeCardsString = holeCardsString + c.getRank();
            holeCardsString = holeCardsString + c.getSuit();
        }

        return holeCardsString;
    }

    public String getOpponentDataLogic(String opponentName) throws Exception {
        String oppDataLogicString;

        double numberOfHands = -1;
        double preFoldCount = -1;
        double preCheckCount = -1;
        double preCallCount = -1;
        double preRaiseCount = -1;

        double postFoldCount = -1;
        double postCheckCount = -1;
        double postCallCount = -1;
        double postBetCount = -1;
        double postRaiseCount = -1;

        initializeDbConnection();

        Statement st1 = con.createStatement();
        ResultSet rs1 = st1.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop WHERE playerName = '" + opponentName + "'");

        if(rs1.next()) {
            numberOfHands = rs1.getDouble("numberOfHands");
            preFoldCount = rs1.getDouble("foldCount");
            preCheckCount = rs1.getDouble("checkCount");
            preCallCount = rs1.getDouble("callCount");
            preRaiseCount = rs1.getDouble("raiseCount");
        } else {
            System.out.println("Couldnt find player in opponentidentifier_2_0_preflop: " + opponentName);
        }

        st1.close();
        rs1.close();

        Statement st2 = con.createStatement();
        ResultSet rs2 = st2.executeQuery("SELECT * FROM opponentidentifier_2_0_postflop WHERE playerName = '" + opponentName + "'");

        if(rs2.next()) {
            postFoldCount = rs2.getDouble("foldCount");
            postCheckCount = rs2.getDouble("checkCount");
            postCallCount = rs2.getDouble("callCount");
            postBetCount = rs2.getDouble("betCount");
            postRaiseCount = rs2.getDouble("raiseCount");
        } else {
            System.out.println("Couldnt find player in opponentidentifier_2_0_pstflop: " + opponentName);
        }

        closeDbConnection();

        oppDataLogicString = "numberOfHands: " + numberOfHands + "preFoldCount: " + preFoldCount +
                "preCheckCount: " + preCheckCount + "preCallCount: " + preCallCount +
                "preRaiseCount: " + preRaiseCount + "postFoldCount: " + postFoldCount +
                "postCheckCount: " + postCheckCount + "postCallCount: " + postCallCount +
                "postBetCount: " + postBetCount + "postRaiseCount: " + postRaiseCount;

        return oppDataLogicString;
    }

    public double getRecentHandsWonLogic(String opponentName) throws Exception {
        return new GameFlow().getNumberOfHandsWonAgainstOppInLast20Hands(opponentName, -1);
    }

    public String getAdjustedOppTypeLogic(String opponentName) throws Exception {
        return new GameFlow().getAdjustedOppTypeForRecentBigPots(opponentName, -1);
    }

    //regular getters and setters
    public String getBotAction() {
        return botAction;
    }

    public void setBotAction(String botAction) {
        this.botAction = botAction;
    }

    public String getOppAction() {
        return oppAction;
    }

    public void setOppAction(String oppAction) {
        this.oppAction = oppAction;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getHoleCards() {
        return holeCards;
    }

    public void setHoleCards(String holeCards) {
        this.holeCards = holeCards;
    }

    public double getHandStrength() {
        return handStrength;
    }

    public void setHandStrength(double handStrength) {
        this.handStrength = handStrength;
    }

    public double getBotStack() {
        return botStack;
    }

    public void setBotStack(double botStack) {
        this.botStack = botStack;
    }

    public double getOpponentStack() {
        return opponentStack;
    }

    public void setOpponentStack(double opponentStack) {
        this.opponentStack = opponentStack;
    }

    public double getBotTotalBetSize() {
        return botTotalBetSize;
    }

    public void setBotTotalBetSize(double botTotalBetSize) {
        this.botTotalBetSize = botTotalBetSize;
    }

    public double getOpponentTotalBetSize() {
        return opponentTotalBetSize;
    }

    public void setOpponentTotalBetSize(double opponentTotalBetSize) {
        this.opponentTotalBetSize = opponentTotalBetSize;
    }

    public double getSizing() {
        return sizing;
    }

    public void setSizing(double sizing) {
        this.sizing = sizing;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStake() {
        return stake;
    }

    public void setStake(String stake) {
        this.stake = stake;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getOpponentData() {
        return opponentData;
    }

    public void setOpponentData(String opponentData) {
        this.opponentData = opponentData;
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(double bigBlind) {
        this.bigBlind = bigBlind;
    }

    public String getStrongDraw() {
        return strongDraw;
    }

    public void setStrongDraw(String strongDraw) {
        this.strongDraw = strongDraw;
    }

    public double getRecentHandsWon() {
        return recentHandsWon;
    }

    public void setRecentHandsWon(double recentHandsWon) {
        this.recentHandsWon = recentHandsWon;
    }

    public String getAdjustedOppType() {
        return adjustedOppType;
    }

    public void setAdjustedOppType(String adjustedOppType) {
        this.adjustedOppType = adjustedOppType;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
