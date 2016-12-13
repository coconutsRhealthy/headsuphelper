package com.lennart.model.pokergame;

import com.lennart.model.boardevaluation.BoardEvaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lennart on 11-12-16.
 */
public class ComputerGame {

    private List<Card> deck;
    private List<Card> myHand;
    private List<Card> computerHand;
    private double smallBlind;
    private double bigBlind;
    private double myStack;
    private double computerStack;
    private double myBetSize;
    private double computerBetSize;
    private double potSize;
    private boolean computerIsButton;
    private String handPath;
    private Action computerAction;

    public ComputerGame() {
        getNewCardDeck();
        dealHoleCards();
        decideWhoIsButton();
        myStack = 50;
        computerStack = 50;
        setBlinds();
        postBlinds();
        setPotSize();

        if(getComputerIsButton()) {
            handPath = "05betF1bet";
            computerAction = new Action(handPath);

            if(computerAction.getSuggestedSizing() != null) {
                //computerStack = computerStack - computerAction.getSuggestedSizing();
            }
        }
    }

    private void getNewCardDeck() {
        deck = (new BoardEvaluator().getCompleteCardDeck());
    }

    private void dealHoleCards() {
        myHand = new ArrayList<>();
        computerHand = new ArrayList<>();

        myHand.add(getAndRemoveRandomCardFromDeck());
        myHand.add(getAndRemoveRandomCardFromDeck());
        computerHand.add(getAndRemoveRandomCardFromDeck());
        computerHand.add(getAndRemoveRandomCardFromDeck());
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

    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    public List<Card> getMyHand() {
        return myHand;
    }

    public void setMyHand(List<Card> myHand) {
        this.myHand = myHand;
    }

    public List<Card> getComputerHand() {
        return computerHand;
    }

    public void setComputerHand(List<Card> computerHand) {
        this.computerHand = computerHand;
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
}
