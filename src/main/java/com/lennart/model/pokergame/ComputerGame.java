package com.lennart.model.pokergame;

import com.lennart.model.boardevaluation.BoardEvaluator;
import org.apache.commons.math3.util.Precision;

import java.util.*;

/**
 * Created by lennart on 11-12-16.
 */
public class ComputerGame {

    private List<Card> deck;
    private List<Card> myHoleCards;
    private List<Card> computerHoleCards;
    private List<Card> flopCards;
    private Card turnCard;
    private Card riverCard;
    private double smallBlind;
    private double bigBlind;
    private double myStack;
    private double computerStack;
    private double myIncrementalBetSize;
    private double myTotalBetSize;
    private double computerIncrementalBetSize;
    private double computerTotalBetSize;
    private double potSize;
    private boolean computerIsButton;
    private Action computerAction;
    private Set<Card> knownGameCards;
    private boolean computerIsToAct;
    private String myAction;
    private String mySize;
    private List<Card> board;
    private String computerWrittenAction;
    private Set<Set<Card>> opponentRange;

    public ComputerGame() {
        //default constructor
    }

    public ComputerGame(String initialize) {
        getNewCardDeck();
        dealHoleCards();
        decideWhoIsButton();
        myStack = 50;
        computerStack = 50;
        setBlinds();
        postBlinds();
        calculatePotSize();

        if(isComputerIsButton()) {
            doComputerAction();
        } else {
            computerIsToAct = false;
        }
    }

    private void getNewCardDeck() {
        deck = BoardEvaluator.getCompleteCardDeck();
    }

    private void dealHoleCards() {
        myHoleCards = new ArrayList<>();
        computerHoleCards = new ArrayList<>();
        knownGameCards = new HashSet<>();

        myHoleCards.add(getAndRemoveRandomCardFromDeck());
        myHoleCards.add(getAndRemoveRandomCardFromDeck());
        computerHoleCards.add(getAndRemoveRandomCardFromDeck());
        computerHoleCards.add(getAndRemoveRandomCardFromDeck());
        knownGameCards.addAll(computerHoleCards);
    }

    private Card getAndRemoveRandomCardFromDeck() {
        Random randomGenerator = new Random();
        int random = randomGenerator.nextInt(deck.size());
        Card cardToReturn = deck.get(random);
        deck.remove(random);

        return cardToReturn;
    }

    private void decideWhoIsButton() {
        if(Math.random() < 0.5) {
            computerIsButton = true;
        } else {
            computerIsButton = false;
        }
    }

    private void setBlinds() {
        smallBlind = 0.25;
        bigBlind = 0.50;
    }

    private void postBlinds() {
        if(computerIsButton) {
            myStack = myStack - bigBlind;
            myIncrementalBetSize = bigBlind;
            myTotalBetSize = bigBlind;
            computerStack = computerStack - smallBlind;
            computerIncrementalBetSize = smallBlind;
            computerTotalBetSize = smallBlind;
        } else {
            myStack = myStack - smallBlind;
            myIncrementalBetSize = smallBlind;
            myTotalBetSize = smallBlind;
            computerStack = computerStack - bigBlind;
            computerIncrementalBetSize = bigBlind;
            computerTotalBetSize = bigBlind;
        }
    }

    public void removeHoleCardsFromKnownGameCards() {
        knownGameCards.removeAll(computerHoleCards);
    }

    public void addHoleCardsToKnownGameCards() {
        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(computerHoleCards);

        knownGameCards.addAll(holeCardsAsSet);
    }

    private void doComputerAction() {
        computerAction = new Action(this);

        computerWrittenAction = computerAction.getWrittenAction();
        if(computerWrittenAction.contains("bet")) {
            computerIncrementalBetSize = computerAction.getSizing();
            computerStack = computerStack - computerIncrementalBetSize;
            computerTotalBetSize = computerIncrementalBetSize;
            computerIsToAct = false;
        } else if (computerWrittenAction.contains("raise")) {
            computerIncrementalBetSize = computerAction.getSizing() - computerTotalBetSize;
            computerStack = computerStack - computerIncrementalBetSize;
            computerTotalBetSize = computerAction.getSizing();
            computerIsToAct = false;
        } else if (computerWrittenAction.contains("call")) {
            computerStack = computerStack - (myTotalBetSize - computerTotalBetSize);
            potSize = potSize + myTotalBetSize + computerTotalBetSize;
            computerIncrementalBetSize = 0;
            computerTotalBetSize = 0;
        } else if(computerWrittenAction.contains("fold")) {
            finishHand();
        } else {
            //here the computer checks
            computerIsToAct = false;
        }
    }

    public void calculatePotSize() {
        potSize = myTotalBetSize + computerTotalBetSize;
    }

    private void finishHand() {
        if(myAction.equals("fold")) {
            computerStack = computerStack + potSize;
            resetGameVariablesAfterFold();
        } else if(computerAction.getWrittenAction().contains("fold")) {
            myStack = myStack + potSize;
            resetGameVariablesAfterFold();
        } else {
            //showdown, determine who has strongest hand
        }
    }

    private void resetGameVariablesAfterFold() {
        if(myStack < 50) {
            myStack = 50;
        }

        if(computerStack < 50) {
            computerStack = 50;
        }

        potSize = 0;
        myIncrementalBetSize = 0;
        myTotalBetSize = 0;
        computerIncrementalBetSize = 0;
        computerTotalBetSize = 0;

        if(isComputerIsButton()) {
            setComputerIsButton(false);
        } else {
            setComputerIsButton(true);
        }

        flopCards = null;
        turnCard = null;
        riverCard = null;
        board = null;
        knownGameCards = null;
    }

    public ComputerGame submitHumanActionAndDoComputerAction() {

        if(myAction.equals("fold")) {
            finishHand();
            getNewCardDeck();
            dealHoleCards();
            postBlinds();
            calculatePotSize();

            if(isComputerIsButton()) {
                doComputerAction();
            }
            return this;
        }

        //set correct bet and stacksizes
        processMyStackAndBetsize();

        //proceed to next street/finish if necessary
        if(myAction.equals("call") || (myAction.equals("check") && !computerIsButton)) {
            resetComputerBetsize();
            proceedToNextStreetOrFinishHand();
            if(isComputerIsButton()) {
                //moved to next street and it is your turn again immediately
                return this;
            }
        }

        //do computer action
        doComputerAction();

        //als computeraction fold is...
        if(computerWrittenAction.contains("fold")) {
            finishHand();
            dealHoleCards();
            postBlinds();
            if(isComputerIsButton()) {
                doComputerAction();
            }
        }

        //als computeraction call is
        if(computerWrittenAction.contains("call")) {
            resetAllBets();
            proceedToNextStreetOrFinishHand();

            //als computer oop zit
            if(!isComputerIsButton()) {
                String formerComputerWrittenAction = computerWrittenAction;
                doComputerAction();
                computerWrittenAction = formerComputerWrittenAction + " and " + computerAction.getWrittenAction();
                //computerWrittenAction = computerWrittenAction + " and " + computerAction.getWrittenAction();
            }
        }

        roundToTwoDecimals();
        //return computerGame
        return this;
    }

    private void processMyStackAndBetsize() {
        if(myAction.equals("fold")) {
            //hier niets
        } else if(myAction.equals("check")) {
            computerIsToAct = true;
        } else if(myAction.equals("call")) {
            myStack = myStack - (computerTotalBetSize - myIncrementalBetSize);
            potSize = potSize + myTotalBetSize + computerTotalBetSize;
            myIncrementalBetSize = 0;
            myTotalBetSize = 0;
        } else {
            myIncrementalBetSize = myTotalBetSize - myIncrementalBetSize;
            myStack = myStack - myIncrementalBetSize;
        }
    }

    private void proceedToNextStreetOrFinishHand() {
        if(flopCards == null) {
            dealFlopCards();
        } else if (turnCard == null) {
            dealTurnCard();
        } else if (riverCard == null) {
            dealRiverCard();
        } else {
            //go to showdown
        }
    }

    private void dealFlopCards() {
        flopCards = new ArrayList<>();
        flopCards.add(getAndRemoveRandomCardFromDeck());
        flopCards.add(getAndRemoveRandomCardFromDeck());
        flopCards.add(getAndRemoveRandomCardFromDeck());

        board = new ArrayList<>();
        board.addAll(flopCards);
        knownGameCards.addAll(flopCards);
    }

    private void dealTurnCard() {
        turnCard = getAndRemoveRandomCardFromDeck();
        board.add(turnCard);
        knownGameCards.add(turnCard);
    }

    private void dealRiverCard() {
        riverCard = getAndRemoveRandomCardFromDeck();
        board.add(riverCard);
        knownGameCards.add(riverCard);
    }

    private void resetComputerBetsize() {
        computerIncrementalBetSize = 0;
        computerTotalBetSize = 0;
    }

    private void resetAllBets() {
        myIncrementalBetSize = 0;
        myTotalBetSize = 0;
        computerIncrementalBetSize = 0;
        computerTotalBetSize = 0;
    }

    private void roundToTwoDecimals() {
        potSize = Precision.round(potSize, 2);
        myStack = Precision.round(myStack, 2);
        computerStack = Precision.round(computerStack, 2);
        myTotalBetSize = Precision.round(myTotalBetSize, 2);
        computerTotalBetSize = Precision.round(computerTotalBetSize, 2);
    }


    //getters and setters
    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    public List<Card> getMyHoleCards() {
        return myHoleCards;
    }

    public void setMyHoleCards(List<Card> myHoleCards) {
        this.myHoleCards = myHoleCards;
    }

    public List<Card> getComputerHoleCards() {
        return computerHoleCards;
    }

    public void setComputerHoleCards(List<Card> computerHoleCards) {
        this.computerHoleCards = computerHoleCards;
    }

    public double getSmallBlind() {
        return smallBlind;
    }

    public void setSmallBlind(double smallBlind) {
        this.smallBlind = smallBlind;
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(double bigBlind) {
        this.bigBlind = bigBlind;
    }

    public double getMyStack() {
        return myStack;
    }

    public void setMyStack(double myStack) {
        this.myStack = myStack;
    }

    public double getComputerStack() {
        return computerStack;
    }

    public void setComputerStack(double computerStack) {
        this.computerStack = computerStack;
    }

    public double getMyTotalBetSize() {
        return myTotalBetSize;
    }

    public void setMyTotalBetSize(double myTotalBetSize) {
        this.myTotalBetSize = myTotalBetSize;
    }

    public double getComputerTotalBetSize() {
        return computerTotalBetSize;
    }

    public void setComputerTotalBetSize(double computerTotalBetSize) {
        this.computerTotalBetSize = computerTotalBetSize;
    }

    public double getPotSize() {
        return potSize;
    }

    public void setPotSize(double potSize) {
        this.potSize = potSize;
    }

    public Action getComputerAction() {
        return computerAction;
    }

    public void setComputerAction(Action computerAction) {
        this.computerAction = computerAction;
    }

    public Set<Card> getKnownGameCards() {
        return knownGameCards;
    }

    public void setKnownGameCards(Set<Card> knownGameCards) {
        this.knownGameCards = knownGameCards;
    }

    public boolean isComputerIsToAct() {
        return computerIsToAct;
    }

    public void setComputerIsToAct(boolean computerIsToAct) {
        this.computerIsToAct = computerIsToAct;
    }

    public String getMyAction() {
        return myAction;
    }

    public void setMyAction(String myAction) {
        this.myAction = myAction;
    }

    public String getMySize() {
        return mySize;
    }

    public void setMySize(String mySize) {
        this.mySize = mySize;
    }

    public boolean isComputerIsButton() {
        return computerIsButton;
    }

    public void setComputerIsButton(boolean computerIsButton) {
        this.computerIsButton = computerIsButton;
    }

    public double getMyIncrementalBetSize() {
        return myIncrementalBetSize;
    }

    public void setMyIncrementalBetSize(double myIncrementalBetSize) {
        this.myIncrementalBetSize = myIncrementalBetSize;
    }

    public double getComputerIncrementalBetSize() {
        return computerIncrementalBetSize;
    }

    public void setComputerIncrementalBetSize(double computerIncrementalBetSize) {
        this.computerIncrementalBetSize = computerIncrementalBetSize;
    }

    public List<Card> getFlopCards() {
        return flopCards;
    }

    public void setFlopCards(List<Card> flopCards) {
        this.flopCards = flopCards;
    }

    public Card getTurnCard() {
        return turnCard;
    }

    public void setTurnCard(Card turnCard) {
        this.turnCard = turnCard;
    }

    public Card getRiverCard() {
        return riverCard;
    }

    public void setRiverCard(Card riverCard) {
        this.riverCard = riverCard;
    }

    public List<Card> getBoard() {
        return board;
    }

    public void setBoard(List<Card> board) {
        this.board = board;
    }

    public String getComputerWrittenAction() {
        return computerWrittenAction;
    }

    public void setComputerWrittenAction(String computerWrittenAction) {
        this.computerWrittenAction = computerWrittenAction;
    }

    public Set<Set<Card>> getOpponentRange() {
        return opponentRange;
    }

    public void setOpponentRange(Set<Set<Card>> opponentRange) {
        this.opponentRange = opponentRange;
    }
}
