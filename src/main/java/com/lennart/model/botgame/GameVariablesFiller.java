package com.lennart.model.botgame;

import com.lennart.model.card.Card;
import com.lennart.model.botgame.imageprocessing.ImageProcessor;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class GameVariablesFiller {
    private double potSize;
    private double botStack;
    private double opponentStack;
    private double smallBlind;
    private double bigBlind;

    private Boolean botIsButton;
    private String opponentPlayerName;

    private Card botHoleCard1;
    private Card botHoleCard2;
    private Card flopCard1;
    private Card flopCard2;
    private Card flopCard3;
    private Card turnCard;
    private Card riverCard;

    private ImageProcessor imageProcessor;

    public GameVariablesFiller() {
        imageProcessor = new ImageProcessor();
        setPotSize();
        setBotStack();
        setOpponentStack();
        setBotHoleCard1();
        setBotHoleCard2();
        setSmallBlind();
        setBigBlind();
        setBotIsButton();
        setOpponentPlayerName();
    }

    public void initializeAndRefreshRelevantVariables(String street) {
        setPotSize();
        setBotStack();
        setOpponentStack();
        setBotIsButton();

        if(opponentPlayerName == null) {
            setOpponentPlayerName();
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

    private void setPotSize() {
        potSize = imageProcessor.getPotSizeFromImage();
    }

    private void setBotStack() {
        botStack = imageProcessor.getBotStackFromImage();
    }

    private void setOpponentStack() {
        opponentStack = imageProcessor.getOpponentStackFromImage();
    }

    private void setSmallBlind() {
        smallBlind = imageProcessor.getSmallBlindFromImage();
    }

    private void setBigBlind() {
        bigBlind = imageProcessor.getBigBlindFromImage();
    }

    private void setBotIsButton() {
        botIsButton = imageProcessor.isBotButtonFromImage();
    }

    private void setOpponentPlayerName() {
        opponentPlayerName = imageProcessor.getOpponentPlayerNameFromImage();
    }

    private void setBotHoleCard1() {
        botHoleCard1 = imageProcessor.getBotHoleCard1FromImage();
    }

    private void setBotHoleCard2() {
        botHoleCard2 = imageProcessor.getBotHoleCard2FromImage();
    }

    private void setFlopCard1() {
        flopCard1 = imageProcessor.getFlopCard1FromImage();
    }

    private void setFlopCard2() {
        flopCard2 = imageProcessor.getFlopCard2FromImage();
    }

    private void setFlopCard3() {
        flopCard3 = imageProcessor.getFlopCard3FromImage();
    }

    private void setTurnCard() {
        turnCard = imageProcessor.getTurnCardFromImage();
    }

    private void setRiverCard() {
        riverCard = imageProcessor.getRiverCardFromImage();
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
