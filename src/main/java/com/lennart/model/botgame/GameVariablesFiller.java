package com.lennart.model.botgame;

import com.lennart.model.card.Card;
import com.lennart.model.botgame.imageprocessing.ImageProcessor;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class GameVariablesFiller {
    private double potSize;
    private double opponentStack;
    private double botStack;
    private double opponentTotalBetSize;
    private double botTotalBetSize;
    private double smallBlind;
    private double bigBlind;

    private boolean botIsButton;
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

        potSize = getPotSize(true);
        botStack = getBotStack(true);
        opponentStack = getOpponentStack(true);
        opponentPlayerName = getOpponentPlayerName(true);
        botHoleCard1 = getBotHoleCard1(true);
        botHoleCard2 = getBotHoleCard2(true);

        smallBlind = potSize * (1 /3);
        bigBlind = potSize * (2 / 3);
    }

    public double getPotSize(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            potSize = imageProcessor.getPotSizeFromImage();
            return potSize;
        }
        return potSize;
    }

    public double getOpponentStack(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            opponentStack = imageProcessor.getOpponentStackFromImage();
            return opponentStack;
        }
        return opponentStack;
    }

    public double getBotStack(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            botStack = imageProcessor.getBotStackFromImage();
            return botStack;
        }
        return botStack;
    }

    public double getOpponentTotalBetSize(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            opponentTotalBetSize = imageProcessor.getOpponentTotalBetSizeFromImage();
            return opponentTotalBetSize;
        }
        return opponentTotalBetSize;
    }

    public double getBotTotalBetSize(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            botTotalBetSize = imageProcessor.getBotTotalBetSizeFromImage();
            return botTotalBetSize;
        }
        return botTotalBetSize;
    }

    public double getSmallBlind() {
        return smallBlind;
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public boolean isBotIsButton(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            botIsButton = imageProcessor.isBotButtonFromImage();
            return botIsButton;
        }
        return botIsButton;
    }

    public String getOpponentPlayerName(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            opponentPlayerName = imageProcessor.getOpponentPlayerNameFromImage();
            return opponentPlayerName;
        }
        return opponentPlayerName;
    }

    public Card getBotHoleCard1(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            botHoleCard1 = imageProcessor.getBotHoleCard1FromImage();
            return botHoleCard1;
        }
        return botHoleCard1;
    }

    public Card getBotHoleCard2(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            botHoleCard2 = imageProcessor.getBotHoleCard2FromImage();
            return botHoleCard2;
        }
        return botHoleCard2;
    }

    public Card getFlopCard1(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            flopCard1 = imageProcessor.getFlopCard1FromImage();
            return flopCard1;
        }
        return flopCard1;
    }

    public Card getFlopCard2(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            flopCard2 = imageProcessor.getFlopCard2FromImage();
            return flopCard2;
        }
        return flopCard2;
    }

    public Card getFlopCard3(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            flopCard3 = imageProcessor.getFlopCard3FromImage();
            return flopCard3;
        }
        return flopCard3;
    }

    public Card getTurnCard(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            turnCard = imageProcessor.getTurnCardFromImage();
            return turnCard;
        }
        return turnCard;
    }

    public Card getRiverCard(boolean initializeOrRefreshNeeded) {
        if(initializeOrRefreshNeeded) {
            riverCard = imageProcessor.getRiverCardFromImage();
            return riverCard;
        }
        return riverCard;
    }
}
