package com.lennart.model.botgame;

import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class GameVariablesFiller {
    private double potSize;
    private double botStack;
    private double opponentStack;
    private double botTotalBetSize;
    private double opponentTotalBetSize;
    private double smallBlind;
    private double bigBlind;

    private Boolean botIsButton;
    private String opponentPlayerName;
    private Map<String, String> actionsFromLastThreeChatLines;

    private Card botHoleCard1;
    private Card botHoleCard2;
    private Card flopCard1;
    private Card flopCard2;
    private Card flopCard3;
    private Card turnCard;
    private Card riverCard;

    private NetBetTableReader netBetTableReader;

    private List<Integer> opponentStats;

    public GameVariablesFiller(BotHand botHand) {
        setSmallBlind();
        setBigBlind();
        netBetTableReader = new NetBetTableReader(bigBlind);
        setBotStack();
        setOpponentStack();
        setBotTotalBetSize();
        setOpponentTotalBetSize();
        setCorrectedPotSize();
        setBotHoleCard1(botHand.getBotHoleCard1());
        setBotHoleCard2(botHand.getBotHoleCard2());
        setBotIsButton();
        setOpponentPlayerName();
        setActionsFromLastThreeChatLines();

        setFlopCard1(botHand.getFlopCard1());
        if(flopCard1 != null) {
            setFlopCard2(botHand.getFlopCard2());
            setFlopCard3(botHand.getFlopCard3());
            setTurnCard(botHand.getTurnCard());
            if(turnCard != null) {
                setRiverCard(botHand.getRiverCard());
            }
        }
    }

    private void setCorrectedPotSize() {
        double potSizeIncludingAllBets = netBetTableReader.getPotSizeFromImage();
        potSize = potSizeIncludingAllBets - botTotalBetSize - opponentTotalBetSize;

        if(potSize < 0.01) {
            potSize = 0;
        }
    }

    private void setBotStack() {
        botStack = netBetTableReader.getBotStackFromImage();
    }

    private void setOpponentStack() {
        opponentStack = netBetTableReader.getOpponentStackFromImage();
    }

    public void setBotTotalBetSize() {
        botTotalBetSize = netBetTableReader.getBotTotalBetSizeFromImage();
    }

    public void setOpponentTotalBetSize() {
        opponentTotalBetSize = netBetTableReader.getOpponentTotalBetSizeFromImage();
    }

    private void setSmallBlind() {
        smallBlind = 0.01;
    }

    private void setBigBlind() {
        bigBlind = 0.02;
    }

    private void setBotIsButton() {
        botIsButton = netBetTableReader.isBotButtonFromImage();
    }

    private void setOpponentPlayerName() {
        opponentPlayerName = netBetTableReader.getOpponentPlayerNameFromImage();
    }

    public void setActionsFromLastThreeChatLines() {
        actionsFromLastThreeChatLines = netBetTableReader.getActionsFromLastThreeChatLines();
    }

    private void setBotHoleCard1(Card holeCard1) {
        if(holeCard1 == null) {
            botHoleCard1 = netBetTableReader.getBotHoleCard1FromImage();
        } else {
            botHoleCard1 = holeCard1;
        }
    }

    private void setBotHoleCard2(Card holeCard2) {
        if(holeCard2 == null) {
            botHoleCard2 = netBetTableReader.getBotHoleCard2FromImage();
        } else {
            botHoleCard2 = holeCard2;
        }
    }

    private void setFlopCard1(Card flopCard1) {
        if(flopCard1 == null) {
            this.flopCard1 = netBetTableReader.getFlopCard1FromImage();
        } else {
            this.flopCard1 = flopCard1;
        }
    }

    private void setFlopCard2(Card flopCard2) {
        if(flopCard2 == null) {
            this.flopCard2 = netBetTableReader.getFlopCard2FromImage();
        } else {
            this.flopCard2 = flopCard2;
        }
    }

    private void setFlopCard3(Card flopCard3) {
        if(flopCard3 == null) {
            this.flopCard3 = netBetTableReader.getFlopCard3FromImage();
        } else {
            this.flopCard3 = flopCard3;
        }
    }

    private void setTurnCard(Card turnCard) {
        if(turnCard == null) {
            this.turnCard = netBetTableReader.getTurnCardFromImage();
        } else {
            this.turnCard = turnCard;
        }
    }

    private void setRiverCard(Card riverCard) {
        if(riverCard == null) {
            this.riverCard = netBetTableReader.getRiverCardFromImage();
        } else {
            this.riverCard = riverCard;
        }
    }

    public double getPotSize() {
        return potSize;
    }

    public double getBotStack() {
        return botStack;
    }

    public double getOpponentStack() {
        return opponentStack;
    }

    public double getBotTotalBetSize() {
        return botTotalBetSize;
    }

    public double getOpponentTotalBetSize() {
        return opponentTotalBetSize;
    }

    public double getSmallBlind() {
        return smallBlind;
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public Boolean isBotIsButton() {
        return botIsButton;
    }

    public String getOpponentPlayerName() {
        return opponentPlayerName;
    }

    public Map<String, String> getActionsFromLastThreeChatLines() {
        return actionsFromLastThreeChatLines;
    }

    public Card getBotHoleCard1() {
        return botHoleCard1;
    }

    public Card getBotHoleCard2() {
        return botHoleCard2;
    }

    public Card getFlopCard1() {
        return flopCard1;
    }

    public Card getFlopCard2() {
        return flopCard2;
    }

    public Card getFlopCard3() {
        return flopCard3;
    }

    public Card getTurnCard() {
        return turnCard;
    }

    public Card getRiverCard() {
        return riverCard;
    }

    public NetBetTableReader getNetBetTableReader() {
        return netBetTableReader;
    }
}
