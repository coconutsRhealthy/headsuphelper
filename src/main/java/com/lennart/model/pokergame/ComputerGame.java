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
    private boolean iAmButton;

    public ComputerGame() {
        getNewCardDeck();
        dealHoleCards();
        decideWhoIsButton();
        setBlinds();
        postBlinds();
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
            iAmButton = true;
        } else {
            iAmButton = false;
        }
    }

    private void setBlinds() {
        smallBlind = 0.25;
        bigBlind = 0.50;
    }

    private void postBlinds() {
        if(iAmButton) {
            myStack = myStack - smallBlind;
            computerStack = computerStack - bigBlind;
        } else {
            myStack = myStack - bigBlind;
            computerStack = computerStack - smallBlind;
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

    public boolean isiAmButton() {
        return iAmButton;
    }

    public void setiAmButton(boolean iAmButton) {
        this.iAmButton = iAmButton;
    }
}
