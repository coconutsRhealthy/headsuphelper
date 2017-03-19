package com.lennart.model.botgame;

import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;

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

    public GameVariablesFiller() {
        netBetTableReader = new NetBetTableReader();
        setBotStack();
        setOpponentStack();
        setBotTotalBetSize();
        setOpponentTotalBetSize();
        setCorrectedPotSize();
        setBotHoleCard1();
        setBotHoleCard2();
        setSmallBlind();
        setBigBlind();
        setBotIsButton();
        //setOpponentPlayerName();
        setActionsFromLastThreeChatLines();
    }

    public void initializeAndRefreshRelevantVariables(String street) {
        setBotStack();
        setOpponentStack();
        setBotTotalBetSize();
        setOpponentTotalBetSize();
        setCorrectedPotSize();
        setActionsFromLastThreeChatLines();

        if(opponentPlayerName == null) {
            //setOpponentPlayerName();
        }
        if(street.equals("flop")) {
            if (flopCard1 == null) {
                setFlopCard1();
            }
            if (flopCard2 == null) {
                setFlopCard2();
            }
            if (flopCard3 == null) {
                setFlopCard3();
            }
        }
        if(street.equals("turn")) {
            if(turnCard == null) {
                setTurnCard();
            }
        }
        if(street.equals("river")) {
            if(riverCard == null) {
                setRiverCard();
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

//    private void setOpponentPlayerName() {
//        opponentPlayerName = netBetTableReader.getOpponentPlayerNameFromImage();
//    }

    private void setActionsFromLastThreeChatLines() {
        actionsFromLastThreeChatLines = netBetTableReader.getActionsFromLastThreeChatLines();
    }

    private void setBotHoleCard1() {
        botHoleCard1 = netBetTableReader.getBotHoleCard1FromImage();
    }

    private void setBotHoleCard2() {
        botHoleCard2 = netBetTableReader.getBotHoleCard2FromImage();
    }

    private void setFlopCard1() {
        flopCard1 = netBetTableReader.getFlopCard1FromImage();
    }

    private void setFlopCard2() {
        flopCard2 = netBetTableReader.getFlopCard2FromImage();
    }

    private void setFlopCard3() {
        flopCard3 = netBetTableReader.getFlopCard3FromImage();
    }

    private void setTurnCard() {
        turnCard = netBetTableReader.getTurnCardFromImage();
    }

    private void setRiverCard() {
        riverCard = netBetTableReader.getRiverCardFromImage();
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
}
