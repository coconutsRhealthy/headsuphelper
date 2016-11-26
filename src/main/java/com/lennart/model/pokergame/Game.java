package com.lennart.model.pokergame;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lennart on 3-11-16.
 */
public class Game {

    private static String stakes;
    private static double myStack;
    private static double opponentStack;
    private static double smallBlind;
    private static double bigBlind;
    private static double myTotalBetSize;
    private static double opponentTotalBetSize;
    private static double myIncrementalBetSize;
    private static double opponentIncrementalBetsize;
    private static double potSize = 0;
    private static String street;
    private static String position;
    private static List<Card> holeCards;
    private static List<Card> flopCards;
    private static Card turnCard;
    private static Card riverCard;
    private static List<Card> boardCards = new ArrayList<>();
    private static Set<Card> knownGameCards = new HashSet<>();

    public static String getStakes() {
        return stakes;
    }

    public static void setStakes(String stakes) {
        Game.stakes = stakes;
    }

    public static double getMyStack() {
        return myStack;
    }

    public static void setMyStack(double myStack) {
        myStack = roundDoubleToTwoDigits(myStack);
        Game.myStack = myStack;
    }

    public static double getOpponentStack() {
        return opponentStack;
    }

    public static void setOpponentStack(double opponentStack) {
        opponentStack = roundDoubleToTwoDigits(opponentStack);
        Game.opponentStack = opponentStack;
    }

    public static double getSmallBlind() {
        return smallBlind;
    }

    public static void setSmallBlind(double smallBlind) {
        smallBlind = roundDoubleToTwoDigits(smallBlind);
        Game.smallBlind = smallBlind;
    }

    public static double getBigBlind() {
        return bigBlind;
    }

    public static void setBigBlind(double bigBlind) {
        bigBlind = roundDoubleToTwoDigits(bigBlind);
        Game.bigBlind = bigBlind;
    }

    public static double getMyTotalBetSize() {
        return myTotalBetSize;
    }

    public static void setMyTotalBetSize(double myTotalBetSize) {
        myTotalBetSize = roundDoubleToTwoDigits(myTotalBetSize);
        Game.myTotalBetSize = myTotalBetSize;
    }

    public static double getOpponentTotalBetSize() {
        return opponentTotalBetSize;
    }

    public static void setOpponentTotalBetSize(double opponentTotalBetSize) {
        opponentTotalBetSize = roundDoubleToTwoDigits(opponentTotalBetSize);
        Game.opponentTotalBetSize = opponentTotalBetSize;
    }

    public static double getMyIncrementalBetSize() {
        return myIncrementalBetSize;
    }

    public static void setMyIncrementalBetSize(double myIncrementalBetSize) {
        Game.myIncrementalBetSize = myIncrementalBetSize;
    }

    public static double getOpponentIncrementalBetsize() {
        return opponentIncrementalBetsize;
    }

    public static void setOpponentIncrementalBetsize(double opponentIncrementalBetsize) {
        Game.opponentIncrementalBetsize = opponentIncrementalBetsize;
    }

    public static double getPotSize() {
        return potSize;
    }

    public static void setPotSize(double potSize) {
        potSize = roundDoubleToTwoDigits(potSize);
        Game.potSize = potSize;
    }

    public static String getStreet() {
        return street;
    }

    public static void setStreet(String street) {
        Game.street = street;
    }

    public static String getPosition() {
        return position;
    }

    public static void setPosition(String position) {
        Game.position = position;
    }

    public static List<Card> getHoleCards() {
        return holeCards;
    }

    public static void setHoleCards(List<Card> holeCards) {
        Game.holeCards = holeCards;
    }

    public static List<Card> getFlopCards() {
        return flopCards;
    }

    public static void setFlopCards(List<Card> flopCards) {
        Game.flopCards = flopCards;
    }

    public static Card getTurnCard() {
        return turnCard;
    }

    public static void setTurnCard(Card turnCard) {
        Game.turnCard = turnCard;
    }

    public static Card getRiverCard() {
        return riverCard;
    }

    public static void setRiverCard(Card riverCard) {
        Game.riverCard = riverCard;
    }

    public static List<Card> getBoardCards() {
        return boardCards;
    }

    public static <T> void setBoardCards(T t) {
        if(boardCards.size() >= 5) {
            boardCards.clear();
        }

        if(t instanceof Card) {
            Card c = (Card) t;
            boardCards.add(c);
        }

        if(t instanceof List) {
            List l = (List) t;
            boardCards.addAll(l);
        }
    }

    public static Set<Card> getKnownGameCards() {
        return knownGameCards;
    }

    public static <T> void setKnownGameCards(T t) {
        if(knownGameCards.size() >= 7) {
            knownGameCards.clear();
        }

        if(t instanceof Card) {
            Card c = (Card) t;
            knownGameCards.add(c);
        }

        if(t instanceof List) {
            List l = (List) t;
            knownGameCards.addAll(l);
        }
    }

    public static void reset() {
        Game.holeCards = new ArrayList<>();
        Game.flopCards = new ArrayList<>();
        Game.turnCard = new Card();
        Game.riverCard = new Card();
        Game.boardCards = new ArrayList<>();
        Game.knownGameCards = new HashSet<>();
    }

    public static void setBlindsBasedOnStake(String stakes) {
        switch(stakes) {
            case "2NL":
                Game.setSmallBlind(0.01);
                Game.setBigBlind(0.02);
                break;
            case "4NL":
                Game.setSmallBlind(0.02);
                Game.setBigBlind(0.04);
                break;
            case "5NL":
                Game.setSmallBlind(0.02);
                Game.setBigBlind(0.05);
                break;
            case "10NL":
                Game.setSmallBlind(0.05);
                Game.setBigBlind(0.10);
                break;
            case "20NL":
                Game.setSmallBlind(0.10);
                Game.setBigBlind(0.20);
                break;
            case "50NL":
                Game.setSmallBlind(0.25);
                Game.setBigBlind(0.50);
                break;
            case "100NL":
                Game.setSmallBlind(0.50);
                Game.setBigBlind(1.0);
                break;
            case "200NL":
                Game.setSmallBlind(1.0);
                Game.setBigBlind(2.0);
                break;
            case "400NL":
                Game.setSmallBlind(2.0);
                Game.setBigBlind(4.0);
                break;
            case "500NL":
                Game.setSmallBlind(2.0);
                Game.setBigBlind(5.0);
                break;
            case "1000NL":
                Game.setSmallBlind(5.0);
                Game.setBigBlind(10.0);
                break;
        }
    }

    public static void setStacksAndPotBasedOnAction(double myIncrementalBetSize, double opponentIncrementalBetSize) {
        Game.setPotSize(Game.getPotSize() + myIncrementalBetSize + opponentIncrementalBetSize);
        Game.setMyStack(Game.getMyStack() - myIncrementalBetSize);
        Game.setOpponentStack(Game.getOpponentStack() - opponentIncrementalBetSize);
    }

    public static void resetPot() {
        Game.potSize = 0;
    }

    private static double roundDoubleToTwoDigits(double d) {
        BigDecimal bd = new BigDecimal(d).setScale(2, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

    public static void removeHoleCardsFromKnownGameCards() {
        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(Game.holeCards);

        Game.knownGameCards.removeAll(holeCardsAsSet);
    }

    public static void addHoleCardsToKnownGameCards() {
        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(Game.holeCards);

        Game.knownGameCards.addAll(holeCardsAsSet);
    }
}
