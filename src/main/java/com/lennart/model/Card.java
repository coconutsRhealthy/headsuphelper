package com.lennart.model;

/**
 * Created by LPO10346 on 3-6-2016.
 */
public class Card {
    private int rank;
    private char suit;

    public Card() {
        //empty default constructor
    }

    public Card(int rank, char suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public char getSuit() {
        return suit;
    }

    public void setSuit(char suit) {
        this.suit = suit;
    }
}
