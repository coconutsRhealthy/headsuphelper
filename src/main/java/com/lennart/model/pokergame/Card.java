package com.lennart.model.pokergame;

/**
 * Created by Lennart Popma on 3-6-2016.
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (rank != card.rank) return false;
        return suit == card.suit;
    }

    @Override
    public int hashCode() {
        int result = rank;
        result = 31 * result + (int) suit;
        return result;
    }
}
