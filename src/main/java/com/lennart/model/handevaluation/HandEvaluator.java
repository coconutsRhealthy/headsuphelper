package com.lennart.model.handevaluation;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.*;

/**
 * Created by lennart on 1-10-16.
 */
public class HandEvaluator {
    private BoardEvaluator boardEvaluator;
    private RangeBuilder rangeBuilder;

    public HandEvaluator(BoardEvaluator boardEvaluator, RangeBuilder rangeBuilder) {
        this.boardEvaluator = boardEvaluator;
        this.rangeBuilder = rangeBuilder;
    }

    public double getHandStrength(List<Card> hand) {
        Map<Integer, Set<Set<Card>>> sortedCombos = boardEvaluator.getSortedCombosNew();

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
        //TODO: dit werkt nu niet goed, want je geeft een range mee waar jouw hand niet in kan zitten. Range moet
        //een ander soort map zijn, nog even over nadenken hoe..

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

    public double getHandStrengthAgainstRangeNew(List<Card> yourHoleCardsAsList, Set<Set<Card>> opponentRange, Map<Integer,
            Set<Set<Card>>> sortedCombos) {
        Set<Card> yourHoleCards = convertListToSet(yourHoleCardsAsList);

        double combosInOpponentRangeBelowYourHand = 0;
        double index;
        boolean myHandHasBeenPassedInSortedCombos = false;

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            for(Set<Card> combo : entry.getValue()) {
                if(combo.equals(yourHoleCards)) {
                    myHandHasBeenPassedInSortedCombos = true;
                }

                Set<Set<Card>> rangeCopy = new HashSet<>();
                rangeCopy.addAll(opponentRange);

                if(!rangeCopy.add(combo)) {
                    if(myHandHasBeenPassedInSortedCombos) {
                        combosInOpponentRangeBelowYourHand++;
                    }
                }
            }
        }

        index = combosInOpponentRangeBelowYourHand / (opponentRange.size());
        return index;
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

    public int getNumberOfArrivedDrawsInRange(String opponentRangeOrMyPerceivedRange) {
        Map<Integer, Set<Card>> arrivedStraightDraws = boardEvaluator.getArrivedStraightDraws();
        Map<Integer, Set<Card>> arrivedFlushDraws = boardEvaluator.getArrivedFlushDraws();
        Map<Integer, Set<Set<Card>>> myPerceivedRange = rangeBuilder.getRange(opponentRangeOrMyPerceivedRange);

        int counter = 0;

        //de straights
        for (Map.Entry<Integer, Set<Card>> entry : arrivedStraightDraws.entrySet()) {
            for (Map.Entry<Integer, Set<Set<Card>>> entry2 : myPerceivedRange.entrySet()) {
                for(Set<Card> myPerceivedRangeCombo : entry2.getValue()) {
                    if(entry.getValue().equals(myPerceivedRangeCombo)) {
                        counter ++;
                    }
                }
            }
        }

        //de flushes
        for (Map.Entry<Integer, Set<Card>> entry : arrivedFlushDraws.entrySet()) {
            for (Map.Entry<Integer, Set<Set<Card>>> entry2 : myPerceivedRange.entrySet()) {
                for(Set<Card> myPerceivedRangeCombo : entry2.getValue()) {
                    if(entry.getValue().equals(myPerceivedRangeCombo)) {
                        counter ++;
                    }
                }
            }
        }

        return counter;
    }

    private Set<Card> convertListToSet(List<Card> list) {
        Set<Card> set = new HashSet<>();
        set.addAll(list);
        return set;
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
