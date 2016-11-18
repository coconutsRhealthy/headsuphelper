package com.lennart.model.pokergame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lennart on 3-11-16.
 */
public class Game {

    private static String position;
    private static List<Card> holeCards;
    private static List<Card> flopCards;
    private static Card turnCard;
    private static Card riverCard;
    private static List<Card> boardCards = new ArrayList<>();
    private static Set<Card> knownGameCards = new HashSet<>();

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
}
