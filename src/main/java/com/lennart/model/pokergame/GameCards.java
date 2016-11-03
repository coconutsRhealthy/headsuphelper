package com.lennart.model.pokergame;

import java.util.List;

/**
 * Created by lennart on 3-11-16.
 */
public class GameCards {

    private static List<Card> holeCards;
    private static List<Card> flopCards;
    private static Card turnCard;
    private static Card riverCard;

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
}
