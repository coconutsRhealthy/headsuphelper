package com.lennart.model.botgame;

import com.lennart.model.card.Card;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class Hand {

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

    private GameVariablesFiller gameVariablesFiller;

    public Hand() {
        gameVariablesFiller = new GameVariablesFiller();

        potSize = gameVariablesFiller.getPotSize(false);
        botStack = gameVariablesFiller.getBotStack(false);
        opponentStack = gameVariablesFiller.getOpponentStack(false);
        opponentPlayerName = gameVariablesFiller.getOpponentPlayerName(false);
        botHoleCard1 = gameVariablesFiller.getBotHoleCard1(false);
        botHoleCard2 = gameVariablesFiller.getBotHoleCard2(false);

        smallBlind = gameVariablesFiller.getSmallBlind();
        bigBlind = gameVariablesFiller.getBigBlind();
    }
}
