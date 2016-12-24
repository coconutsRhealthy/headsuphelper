package com.lennart.model.pokergame;

import com.lennart.model.boardevaluation.BoardEvaluator;

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
    private Set<Card> knownGameCards = new HashSet<>();
    private String handPath;
    private boolean computerIsToAct;
    private String myAction;
    private String mySize;
    private List<Card> board;

    private BoardEvaluator boardEvaluator;

    public ComputerGame() {
        //default constructor
    }

    public ComputerGame(String initialize) {
        getNewCardDeck();
        dealHoleCards();
        knownGameCards.addAll(computerHoleCards);
        decideWhoIsButton();
        myStack = 50;
        computerStack = 50;
        setBlinds();
        postBlinds();
        calculatePotSize();

        if(isComputerIsButton()) {
            handPath = "05betF1bet";
            doComputerAction();
        } else {
            handPath = "1bet";
            computerIsToAct = false;
        }
    }

    private void getNewCardDeck() {
        deck = (BoardEvaluator.getCompleteCardDeck());
    }

    private void dealHoleCards() {
        myHoleCards = new ArrayList<>();
        computerHoleCards = new ArrayList<>();

        myHoleCards.add(getAndRemoveRandomCardFromDeck());
        myHoleCards.add(getAndRemoveRandomCardFromDeck());
        computerHoleCards.add(getAndRemoveRandomCardFromDeck());
        computerHoleCards.add(getAndRemoveRandomCardFromDeck());
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
            computerIncrementalBetSize = smallBlind;
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

        String writtenComputerAction = computerAction.getWrittenAction();
        if(!writtenComputerAction.contains("fold") && !writtenComputerAction.contains("check")) {
            computerIncrementalBetSize = computerAction.getSizing() - computerTotalBetSize;
            computerStack = computerStack - computerIncrementalBetSize;
            computerTotalBetSize = computerAction.getSizing();
            potSize = potSize + computerIncrementalBetSize;
            computerIsToAct = false;
        } else if(writtenComputerAction.contains("fold")) {
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
        //TODO: implement
    }

    public ComputerGame submitHumanActionAndDoComputerAction() {

        //set correct bet and stacksizes
        processMyStackAndBetsize();

        //set correct handPath
        updateHandPath();

        //proceed to next street/finish if necessary
        if(myAction.equals("call") || (myAction.equals("check") && !computerIsButton)) {
            resetComputerBetsize();
            proceedToNextStreetOrFinishHand();
        }

        //do computer action
        doComputerAction();

        //return computerGame

        return this;
    }

    private void processMyStackAndBetsize() {
        if(myAction.equals("fold")) {
            finishHand();
        } else if(myAction.equals("check")) {
            computerIsToAct = true;
        } else if(myAction.equals("call")) {
            myStack = myStack - (computerTotalBetSize - myIncrementalBetSize);
            potSize = potSize + (computerTotalBetSize - myIncrementalBetSize);
            myIncrementalBetSize = 0;
            myTotalBetSize = 0;
        } else {
            myIncrementalBetSize = myTotalBetSize - myIncrementalBetSize;
            myStack = myStack - myIncrementalBetSize;
            potSize = potSize + myIncrementalBetSize;
        }
    }

    private void proceedToNextStreetOrFinishHand() {
        if(flopCards == null) {
            dealFlopCards();
        } else if (turnCard == null) {
            dealTurnCard();
        } else if (riverCard == null) {
            dealRiverCard();
        }
    }

    private void dealFlopCards() {
        flopCards = new ArrayList<>();
        flopCards.add(getAndRemoveRandomCardFromDeck());
        flopCards.add(getAndRemoveRandomCardFromDeck());
        flopCards.add(getAndRemoveRandomCardFromDeck());
    }

    private void dealTurnCard() {
        turnCard = getAndRemoveRandomCardFromDeck();
    }

    private void dealRiverCard() {
        riverCard = getAndRemoveRandomCardFromDeck();
    }

    private void updateHandPath() {
        if(myAction.equals("check")) {
            if(computerIsButton) {
                handPath = handPath + "Fcheck";
            } else {
                //handPath stays the same
            }
        } else if(myAction.equals("call")) {
            //handPath stays the same
        } else if(myAction.equals("bet")) {
            handPath = handPath + "F1bet";
        } else if(myAction.equals("raise")) {
            String computerLastAction = handPath.substring(handPath.length() - 4);

            if(computerLastAction.contains("1")) {
                handPath = handPath + "F2bet";
            } else if(computerLastAction.contains("2")) {
                handPath = handPath + "F3bet";
            }
        }
    }

    private void resetComputerBetsize() {
        computerIncrementalBetSize = 0;
        computerTotalBetSize = 0;
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

    public String getHandPath() {
        return handPath;
    }

    public void setHandPath(String handPath) {
        this.handPath = handPath;
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
}
