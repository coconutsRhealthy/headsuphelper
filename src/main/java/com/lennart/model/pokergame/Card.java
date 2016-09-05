package com.lennart.model.pokergame;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Lennart Popma on 3-6-2016.
 */
public class Card implements Comparable<Card> {
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

    @Override
    public int compareTo(Card c) {
            if(c.getRank() > this.getRank()) {
                return 1;
            } else if(c.getRank() == this.getRank()) {
                return 0;
            } else {
                return -1;
            }
    }

    static Comparator<List<Card>> getComboComparator() {
        return new Comparator<List<Card>>() {
            @Override
            public int compare(List<Card> combo1, List<Card> combo2) {
                Collections.sort(combo1);
                Collections.sort(combo2);

                if(combo2.get(0).getRank() > combo1.get(0).getRank()) {
                    return 1;
                } else if(combo2.get(0).getRank() == combo1.get(0).getRank()) {
                    if(combo2.get(1).getRank() > combo1.get(1).getRank()) {
                        return 1;
                    } else if(combo2.get(1).getRank() == combo1.get(1).getRank()) {
                        return 0;
                    }
                }
                return -1;
            }
        };
    }
}
