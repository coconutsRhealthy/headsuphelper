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
    private double smallBlind;
    private double bigBlind;
    private double myStack;
    private double computerStack;
    private double myBetSize;
    private double computerBetSize;
    private double potSize;
    private boolean computerIsButton;
    private Action computerAction;
    private Set<Card> knownGameCards = new HashSet<>();
    private String handPath;
    private boolean computerIsToAct;
    private String myAction;
    private String mySize;

    public ComputerGame() {
        getNewCardDeck();
        dealHoleCards();
        knownGameCards.addAll(computerHoleCards);
        decideWhoIsButton();
        myStack = 50;
        computerStack = 50;
        setBlinds();
        postBlinds();
        setPotSize();

        if(getComputerIsButton()) {
            handPath = "05betF1bet";
            doComputerAction();
        } else {
            handPath = "1bet";
            computerIsToAct = false;
        }
    }

    private void getNewCardDeck() {
        deck = (new BoardEvaluator().getCompleteCardDeck());
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
            myBetSize = bigBlind;
            computerStack = computerStack - smallBlind;
            computerBetSize = smallBlind;
        } else {
            myStack = myStack - smallBlind;
            myBetSize = smallBlind;
            computerStack = computerStack - bigBlind;
            computerBetSize = bigBlind;
        }
    }

    public void removeHoleCardsFromKnownGameCards() {
        knownGameCards.removeAll(computerHoleCards);
    }

    private void doComputerAction() {
        computerAction = new Action(this);

        String writtenComputerAction = computerAction.getWrittenAction();
        if(!writtenComputerAction.contains("fold") && !writtenComputerAction.contains("check")) {
            computerStack = computerStack - computerAction.getSizing();
            potSize = potSize + computerAction.getSizing();
            computerIsToAct = false;
        } else if(writtenComputerAction.contains("fold")) {
            finishHand();
        } else {
            //here the computer checks
            computerIsToAct = false;
        }
    }

    private void finishHand() {
        //TODO: implement
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

    public boolean getComputerIsButton() {
        return computerIsButton;
    }

    public void setButton(boolean computerIsButton) {
        this.computerIsButton = computerIsButton;
    }

    public double getMyBetSize() {
        return myBetSize;
    }

    public void setMyBetSize(double myBetSize) {
        this.myBetSize = myBetSize;
    }

    public double getComputerBetSize() {
        return computerBetSize;
    }

    public void setComputerBetSize(double computerBetSize) {
        this.computerBetSize = computerBetSize;
    }

    public double getPotSize() {
        return potSize;
    }

    public void setPotSize() {
        potSize = myBetSize + computerBetSize;
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

    public void setPotSize(double potSize) {
        this.potSize = potSize;
    }

    public boolean isComputerIsButton() {
        return computerIsButton;
    }

    public void setComputerIsButton(boolean computerIsButton) {
        this.computerIsButton = computerIsButton;
    }
}
