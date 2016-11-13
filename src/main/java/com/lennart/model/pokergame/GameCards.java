package com.lennart.model.pokergame;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lennart on 3-11-16.
 */
public class GameCards {

    private static List<Card> holeCards;
    private static List<Card> flopCards;
    private static Card turnCard;
    private static Card riverCard;
    private static Set<Card> knownGameCards = new HashSet<>();

    public static List<Card> getHoleCards() {
        return holeCards;
    }

    public static void setHoleCards(List<Card> holeCards) {
        GameCards.holeCards = holeCards;
    }

    public static List<Card> getFlopCards() {
        return flopCards;
    }

    public static void setFlopCards(List<Card> flopCards) {
        GameCards.flopCards = flopCards;
    }

    public static Card getTurnCard() {
        return turnCard;
    }

    public static void setTurnCard(Card turnCard) {
        GameCards.turnCard = turnCard;
    }

    public static Card getRiverCard() {
        return riverCard;
    }

    public static void setRiverCard(Card riverCard) {
        GameCards.riverCard = riverCard;
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
}
