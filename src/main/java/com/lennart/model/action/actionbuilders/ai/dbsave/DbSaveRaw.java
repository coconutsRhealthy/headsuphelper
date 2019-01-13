package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.card.Card;

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
    private String position;
    private String stake;
    private String opponentName;
    private String opponentData;
    private double bigBlind;
    private String strongDraw;

    //logic getters
    public String getBotActionLogic(String botAction) {
        return null;
    }

    public String getOpponentActionLogic(String opponentAction) {
        return null;
    }

    public String getBoardLogic(List<Card> board) {
        return null;
    }

    public String getHoleCardsLogic(List<Card> holeCards) {
        return null;
    }

    public double getHandStrengthLogic(double handStrength) {
        return 0;
    }

    public double getBotStackLogic(double botStack) {
        return 0;
    }

    public double getOpponentSackLogic(double opponentStack) {
        return 0;
    }

    public double getBotTotalBetSizeLogic(double botTotalBetSize) {
        return 0;
    }

    public double getOpponentTotalBetSizeLogic(double opponentTotalBetSize) {
        return 0;
    }

    public String getPositionLogic(boolean position) {
        return null;
    }

    public String getStakeLogic(String stake) {
        return null;
    }

    public String getOpponentNameLogic(String opponentName) {
        return null;
    }

    public String getOpponentDataLogic(String opponentName) {
        return null;
    }

    public double getBigBlindLogic(double bigBlind) {
        return 0;
    }

    public String getStrongDrawLogic(boolean strongDraw) {
        return null;
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
}
