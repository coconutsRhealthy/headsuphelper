package com.lennart.model.handevaluation;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.*;

/**
 * Created by lennart on 1-10-16.
 */
public class HandEvaluator {
    private BoardEvaluator boardEvaluator;
    private RangeBuilder rangeBuilder;
    private StraightDrawEvaluator straightDrawEvaluator;
    private FlushDrawEvaluator flushDrawEvaluator;
    private HighCardDrawEvaluator highCardDrawEvaluator;

    public HandEvaluator(BoardEvaluator boardEvaluator, RangeBuilder rangeBuilder) {
        this.boardEvaluator = boardEvaluator;
        this.rangeBuilder = rangeBuilder;
        straightDrawEvaluator = boardEvaluator.getStraightDrawEvaluator();
        flushDrawEvaluator = boardEvaluator.getFlushDrawEvaluator();
        highCardDrawEvaluator = boardEvaluator.getHighCardDrawEvaluator();
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

    public double getHandStrengthAgainstRange(List<Card> yourHoleCardsAsList, Set<Set<Card>> opponentRange, Map<Integer,
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

    public double getPercentageOfYourPerceivedRangeThatHitsFlopRanks() {
        //TODO: implement this method
        return 0;
    }

    public double getPercentageOfYourPerceivedRangeThatHitsNewCard() {
        //TODO: implement this method
        return 0;
    }

//    public double getHandStrengthNeededToCall() {
//        //TODO: implement this method
//        return 0;
//    }

    public int getDrawEquityOfYourHand() {
        //TODO: implement this method
        return 0;
    }

    public boolean isSingleBetPot() {
        //TODO: implement this method;
        return false;
    }

    public boolean youHaveStrongFlushDraw(List<Card> holeCards) {
        return comboPresentInMap(flushDrawEvaluator.getStrongFlushDrawCombos(), holeCards);
    }

    public boolean youHaveMediumFlushDraw(List<Card> holeCards) {
        return comboPresentInMap(flushDrawEvaluator.getMediumFlushDrawCombos(), holeCards);
    }

    public boolean youHaveWeakFlushDraw(List<Card> holeCards) {
        return comboPresentInMap(flushDrawEvaluator.getWeakFlushDrawCombos(), holeCards);
    }

    public boolean youHaveStrongOosd(List<Card> holeCards) {
        return comboPresentInMap(straightDrawEvaluator.getStrongOosdCombos(), holeCards);
    }

    public boolean youHaveMediumOosd(List<Card> holeCards) {
        return comboPresentInMap(straightDrawEvaluator.getMediumOosdCombos(), holeCards);
    }

    public boolean youHaveWeakOosd(List<Card> holeCards) {
        return comboPresentInMap(straightDrawEvaluator.getWeakOosdCombos(), holeCards);
    }

//    public boolean youHaveStrongGutshot(List<Card> holeCards) {
//        return comboPresentInMap(straightDrawEvaluator.getStrongGutshotCombos(), holeCards);
//    }

    public boolean youHaveMediumGutshot(List<Card> holeCards) {
        return comboPresentInMap(straightDrawEvaluator.getMediumGutshotCombos(), holeCards);
    }

    public boolean youHaveWeakGutshot(List<Card> holeCards) {
        return comboPresentInMap(straightDrawEvaluator.getWeakGutshotCombos(), holeCards);
    }

//    public boolean youHaveStrongOvercards(List<Card> holeCards) {
//        return comboPresentInMap(highCardDrawEvaluator.getStrongTwoOvercards(), holeCards);
//    }

    public boolean youHaveMediumOvercards(List<Card> holeCards) {
        return comboPresentInMap(highCardDrawEvaluator.getMediumTwoOvercards(), holeCards);
    }

    public boolean youHaveWeakOvercards(List<Card> holeCards) {
        return comboPresentInMap(highCardDrawEvaluator.getWeakTwoOvercards(), holeCards);
    }

    public boolean youHaveStrongBackDoorFlush(List<Card> holeCards) {
        return comboPresentInMap(flushDrawEvaluator.getStrongBackDoorFlushCombos(), holeCards);
    }

    public boolean youHaveMediumBackDoorFlush(List<Card> holeCards) {
        return comboPresentInMap(flushDrawEvaluator.getMediumBackDoorFlushCombos(), holeCards);
    }

    public boolean youHaveWeakBackDoorFlush(List<Card> holeCards) {
        return comboPresentInMap(flushDrawEvaluator.getWeakBackDoorFlushCombos(), holeCards);
    }

    public boolean youHaveStrongBackDoorStraight(List<Card> holeCards) {
        return comboPresentInMap(straightDrawEvaluator.getStrongBackDoorCombos(), holeCards);
    }

    public boolean youHaveMediumBackDoorStraight(List<Card> holeCards) {
        return comboPresentInMap(straightDrawEvaluator.getMediumBackDoorCombos(), holeCards);
    }

    public boolean youHaveWeakBackDoorStraight(List<Card> holeCards) {
        return comboPresentInMap(straightDrawEvaluator.getWeakBackDoorCombos(), holeCards);
    }

    public boolean youHaveAnyDraw(List<Card> holeCards) {
        return false;
    }

    public boolean youHaveStrongFdOrSd(List<Card> holeCards) {
        boolean strongStraightDraw = comboPresentInMap(straightDrawEvaluator.getStrongOosdCombos(), holeCards);
        boolean strongFlushDraw = comboPresentInMap(flushDrawEvaluator.getStrongFlushDrawCombos(), holeCards);

        return(strongStraightDraw || strongFlushDraw);
    }

    public boolean youHaveStrongGutshot(List<Card> holeCards) {
        return comboPresentInMap(straightDrawEvaluator.getStrongGutshotCombos(), holeCards);
    }

    public boolean youHaveMediumFdOrSd(List<Card> holeCards) {
        boolean mediumStraightDraw = comboPresentInMap(straightDrawEvaluator.getMediumOosdCombos(), holeCards);
        boolean mediumFlushDraw = comboPresentInMap(flushDrawEvaluator.getMediumFlushDrawCombos(), holeCards);

        return(mediumStraightDraw || mediumFlushDraw);
    }

    public boolean youHaveStrongOvercards(List<Card> holeCards) {
        return comboPresentInMap(highCardDrawEvaluator.getStrongTwoOvercards(), holeCards);
    }

    public boolean youHaveStrongBackDoor(List<Card> holeCards) {
        boolean strongBackDoorStraight = comboPresentInMap(straightDrawEvaluator.getStrongBackDoorCombos(), holeCards);
        boolean strongBackDoorFlush = comboPresentInMap(flushDrawEvaluator.getStrongBackDoorFlushCombos(), holeCards);

        return(strongBackDoorStraight || strongBackDoorFlush);
    }

    //helper methods
    private double getIndexOfHandInSortedCombos(Integer key, Map<Integer, Set<Set<Card>>> sortedCombos) {
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

    private Set<Card> convertListToSet(List<Card> list) {
        Set<Card> set = new HashSet<>();
        set.addAll(list);
        return set;
    }

    private boolean comboPresentInMap(Map<Integer, Set<Card>> map, List<Card> holeCardsAsList) {
        Set<Card> holeCards = convertListToSet(holeCardsAsList);

        for (Map.Entry<Integer, Set<Card>> entry : map.entrySet()) {
            if(entry.getValue().equals(holeCards)) {
                return true;
            }
        }
        return false;
    }
}
