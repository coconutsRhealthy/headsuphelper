package com.lennart.model.botgame;

import com.lennart.model.action.Action;
import com.lennart.model.card.Card;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class Hand {

    private GameVariablesFiller gameVariablesFiller;

    private double potSize;
    private double botStack;
    private double opponentStack;
    private double botTotalBetSize;
    private double opponentTotalBetSize;
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

    private Action botAction;

    //extra variables
    private List<Card> botHoleCards;
    private List<Card> flopCards;
    private Set<Card> knownGameCards;
    private String opponentAction;
    private List<Card> board;
    private Set<Set<Card>> opponentRange;
    private List<String> actionHistory;
    private double handsOpponentOopFacingPreflop2bet;
    private double opponentPreCall2betStat;
    private double opponentPre3betStat;
    private boolean onlyCallRangeNeeded;
    private boolean opponentLastActionWasPreflop;
    private double opponentFormerTotalCallAmount;
    private String street;

    public Hand() {
        gameVariablesFiller = new GameVariablesFiller();

        setPotSize();
        setBotStack();
        setOpponentStack();
        setOpponentPlayerName();
        setBotHoleCard1();
        setBotHoleCard2();
        setSmallBlind();
        setBigBlind();

        knownGameCards = new HashSet<>();
        knownGameCards.add(botHoleCard1);
        knownGameCards.add(botHoleCard2);
    }

    public Hand getNewBotAction() {
        botAction = new Action(this);
        return this;
    }

    public void updateVariables() {
        gameVariablesFiller.initializeAndRefreshRelevantVariables(street);

        setPotSize();
        setBotStack();
        setOpponentStack();
        setBotIsButton();

        setFlopCard1IfNecessary();
        setFlopCard2IfNecessary();
        setFlopCard3IfNecessary();
        setTurnCardIfNecessary();
        setRiverCardIfNecessary();

        setDerivedVariables();
    }

    //main variables
    private void setPotSize() {
        potSize = gameVariablesFiller.getPotSize();
    }

    private void setBotStack() {
        botStack = gameVariablesFiller.getBotStack();
    }

    private void setOpponentStack() {
        opponentStack = gameVariablesFiller.getOpponentStack();
    }

    private void setOpponentPlayerName() {
        opponentPlayerName = gameVariablesFiller.getOpponentPlayerName();
    }

    private void setBotHoleCard1() {
        botHoleCard1 = gameVariablesFiller.getBotHoleCard1();
    }

    private void setBotHoleCard2() {
        botHoleCard2 = gameVariablesFiller.getBotHoleCard2();
    }

    private void setSmallBlind() {
        smallBlind = gameVariablesFiller.getSmallBlind();
    }

    private void setBigBlind() {
        bigBlind = gameVariablesFiller.getBigBlind();
    }

    private void setBotIsButton() {
        botIsButton = gameVariablesFiller.isBotIsButton();
    }

    private void setFlopCard1IfNecessary() {
        if(flopCard1 == null) {
            flopCard1 = gameVariablesFiller.getFlopCard1();
        }
    }

    private void setFlopCard2IfNecessary() {
        if(flopCard2 == null) {
            flopCard2 = gameVariablesFiller.getFlopCard2();
        }
    }

    private void setFlopCard3IfNecessary() {
        if(flopCard3 == null) {
            flopCard3 = gameVariablesFiller.getFlopCard3();
        }
    }

    private void setTurnCardIfNecessary() {
        if(turnCard == null) {
            turnCard = gameVariablesFiller.getTurnCard();
        }
    }

    private void setRiverCardIfNecessary() {
        if(riverCard == null) {
            riverCard = gameVariablesFiller.getRiverCard();
        }
    }

    //derived variables form main variables
    private void setDerivedVariables() {
        setBotHoleCards();
        setFlopCards();


    }


    private void setBotHoleCards() {
        if(botHoleCards == null && botHoleCard1 != null && botHoleCard2 != null) {
            botHoleCards = new ArrayList<>();
            botHoleCards.add(botHoleCard1);
            botHoleCards.add(botHoleCard2);
        }
    }

    private void setFlopCards() {
        if(flopCards == null && flopCard1 != null && flopCard2 != null && flopCard3 != null) {
            flopCards = new ArrayList<>();
            flopCards.add(flopCard1);
            flopCards.add(flopCard2);
            flopCards.add(flopCard3);
        }
    }

    private void setKnownGameCards() {

    }

    private void setBotTotalBetSize() {

    }

    private void setOpponentTotalBetSize() {

    }

}
