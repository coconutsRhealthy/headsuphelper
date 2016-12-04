package com.lennart.model.handevaluation;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by lennart on 1-10-16.
 */
public class HandEvaluator {
    BoardEvaluator boardEvaluator = new BoardEvaluator();

    public double getHandStrength(List<Card> hand, List<Card> board) {
        Map<Integer, Set<Set<Card>>> sortedCombos = boardEvaluator.getSortedCombos(board);

        Set<Card> handSet = new HashSet<>();
        handSet.addAll(hand);

        double handStrength = 0;

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
           for(Set<Card> s : entry.getValue()) {
               if(s.equals(handSet)) {
                   handStrength = getIndexOfHandInSortedCombos(entry.getKey(), sortedCombos);
               }
           }
        }
        return handStrength;
    }

    public double getHandStrengthAgainstRange(List<Card> hand, Map<Integer, Set<Set<Card>>> range) {
        Set<Card> handSet = new HashSet<>();
        handSet.addAll(hand);

        double handStrength = 0;

        for (Map.Entry<Integer, Set<Set<Card>>> entry : range.entrySet()) {
            for(Set<Card> s : entry.getValue()) {
                if(s.equals(handSet)) {
                    handStrength = getIndexOfHandInSortedCombos(entry.getKey(), range);
                }
            }
        }
        return handStrength;
    }

    public double getIndexOfHandInSortedCombos(Integer key, Map<Integer, Set<Set<Card>>> sortedCombos) {
        double index;

        Map<Integer, Set<Set<Card>>> combosStrongerThanYours = new HashMap<>();
        Map<Integer, Set<Set<Card>>> combosEquallyStrongAsYours = new HashMap<>();
        Map<Integer, Set<Set<Card>>> combosWeakerThanYours = new HashMap<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            if(entry.getKey() < key) {
                combosStrongerThanYours.put(combosStrongerThanYours.size(), entry.getValue());
            }
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            if(entry.getKey() == key) {
                combosEquallyStrongAsYours.put(combosEquallyStrongAsYours.size(), entry.getValue());
            }
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            if(entry.getKey() > key) {
                combosWeakerThanYours.put(combosWeakerThanYours.size(), entry.getValue());
            }
        }

        double numberOfCombosStrongerThanYours = 0;
        double numberOfCombosEquallyStrongAsYours = 0;
        double numberOfcombosWeakerThanYours = 0;

        for (Map.Entry<Integer, Set<Set<Card>>> entry : combosStrongerThanYours.entrySet()) {
            for(Set<Card> s : entry.getValue()) {
                numberOfCombosStrongerThanYours++;
            }
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : combosEquallyStrongAsYours.entrySet()) {
            for(Set<Card> s : entry.getValue()) {
                numberOfCombosEquallyStrongAsYours++;
            }
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : combosWeakerThanYours.entrySet()) {
            for(Set<Card> s : entry.getValue()) {
                numberOfcombosWeakerThanYours++;
            }
        }

        index = numberOfcombosWeakerThanYours / (numberOfcombosWeakerThanYours + numberOfCombosStrongerThanYours);

        if(numberOfcombosWeakerThanYours == 0 && numberOfCombosStrongerThanYours == 0) {
            return 1;
        } else {
            return index;
        }
    }

    public int getNumberOfArrivedDrawsInYourPerceivedRange() {
        //TODO: implement this method
        return 0;
    }

    public double getPercentageOfYourPerceivedRangeThatHitsFlopRanks() {
        //TODO: implement this method
        return 0;
    }

    public double getPercentageOfYourPerceivedRangeThatHitsNewCard() {
        //TODO: implement this method
        return 0;
    }

    public double getHandStrengthNeededToCall() {
        //TODO: implement this method
        return 0;
    }

    public int getDrawEquityOfYourHand() {
        //TODO: implement this method
        return 0;
    }

    public boolean isSingleBetPot() {
        //TODO: implement this method;
        return false;
    }

}
