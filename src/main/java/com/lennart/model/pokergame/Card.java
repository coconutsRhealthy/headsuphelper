package com.lennart.model.pokergame;

import com.lennart.model.boardevaluation.BoardEvaluator;

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

    public static Comparator<List<Card>> sortCardCombosBasedOnRank() {
        return new Comparator<List<Card>>() {
            @Override
            public int compare(List<Card> xCombo1, List<Card> xCombo2) {
                BoardEvaluator boardEvaluator = new BoardEvaluator();

                List<Integer> combo1 = boardEvaluator.getSortedCardRanksFromCardList(xCombo1);
                List<Integer> combo2 = boardEvaluator.getSortedCardRanksFromCardList(xCombo2);

                if(Collections.max(combo2) > Collections.max(combo1)) {
                    return 1;
                } else if(Collections.max(combo2) == Collections.max(combo1)) {
                    if(Collections.min(combo2) > Collections.min(combo1)) {
                        return 1;
                    } else if(Collections.min(combo2) == Collections.min(combo1)) {
                        return 0;
                    }
                }
                return -1;
            }
        };
    }
}
