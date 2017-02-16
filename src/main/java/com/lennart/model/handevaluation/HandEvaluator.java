package com.lennart.model.handevaluation;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.*;

/**
 * Created by lennart on 1-10-16.
 */
public class HandEvaluator {
    private List<Card> holeCards;
    private BoardEvaluator boardEvaluator;
    private RangeBuilder rangeBuilder;
    private StraightDrawEvaluator straightDrawEvaluator;
    private FlushDrawEvaluator flushDrawEvaluator;
    private HighCardDrawEvaluator highCardDrawEvaluator;
    private Map<String, Boolean> handDrawEvaluation;

    public HandEvaluator(BoardEvaluator boardEvaluator) {
        //Constructor only to be used for end of hand evalution in ComputerGame
        this.boardEvaluator = boardEvaluator;
    }

    public HandEvaluator(List<Card> holeCards, BoardEvaluator boardEvaluator, RangeBuilder rangeBuilder) {
        this.holeCards = holeCards;
        this.boardEvaluator = boardEvaluator;
        this.rangeBuilder = rangeBuilder;
        straightDrawEvaluator = boardEvaluator.getStraightDrawEvaluator();
        flushDrawEvaluator = boardEvaluator.getFlushDrawEvaluator();
        highCardDrawEvaluator = boardEvaluator.getHighCardDrawEvaluator();
        fillHandDrawEvaluation();
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

        loop: for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            for(Set<Card> combo : entry.getValue()) {
                if(combo.equals(yourHoleCards)) {
                    myHandHasBeenPassedInSortedCombos = true;
                    continue loop;
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

//    public int getNumberOfArrivedDrawsInRange(String opponentRangeOrMyPerceivedRange) {
//        Map<Integer, Set<Card>> arrivedStraightDraws = boardEvaluator.getArrivedStraightDraws();
//        Map<Integer, Set<Card>> arrivedFlushDraws = boardEvaluator.getArrivedFlushDraws();
//        Map<Integer, Set<Set<Card>>> myPerceivedRange = rangeBuilder.getRange(opponentRangeOrMyPerceivedRange);
//
//        int counter = 0;
//
//        //de straights
//        for (Map.Entry<Integer, Set<Card>> entry : arrivedStraightDraws.entrySet()) {
//            for (Map.Entry<Integer, Set<Set<Card>>> entry2 : myPerceivedRange.entrySet()) {
//                for(Set<Card> myPerceivedRangeCombo : entry2.getValue()) {
//                    if(entry.getValue().equals(myPerceivedRangeCombo)) {
//                        counter ++;
//                    }
//                }
//            }
//        }
//
//        //de flushes
//        for (Map.Entry<Integer, Set<Card>> entry : arrivedFlushDraws.entrySet()) {
//            for (Map.Entry<Integer, Set<Set<Card>>> entry2 : myPerceivedRange.entrySet()) {
//                for(Set<Card> myPerceivedRangeCombo : entry2.getValue()) {
//                    if(entry.getValue().equals(myPerceivedRangeCombo)) {
//                        counter ++;
//                    }
//                }
//            }
//        }
//
//        return counter;
//    }

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

    public boolean isSingleBetPot(List<String> actionHistory) {
        //TODO: implement this method;

        //single bet pot is pot singleRaised preflop, en geen raises postflop
        //thus, in actionHistory, 'raise' can only be present once, for the 2bet preflop

        //maybe you have to make this potsize dependent instead of actionHistory dependent

        int counter = 0;

        for(String action : actionHistory) {
            if(action.contains("raise")) {
                counter++;
            }
        }

        if(counter == 1) {
            return true;
        }
        return false;
    }

    public Map<String, Boolean> getHandDrawEvaluation() {
        return handDrawEvaluation;
    }

    public boolean hasAnyDrawNonBackDoor() {
        for (Map.Entry<String, Boolean> entry : handDrawEvaluation.entrySet()) {
            if(!entry.getKey().contains("BackDoor") && entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDrawOfType(String drawType) {
        for (Map.Entry<String, Boolean> entry : handDrawEvaluation.entrySet()) {
            if(entry.getKey().contains(drawType) && entry.getValue()) {
                return true;
            }
        }
        return false;
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

    private void fillHandDrawEvaluation() {
        handDrawEvaluation = new HashMap<>();

        handDrawEvaluation.put("strongFlushDraw", comboPresentInMap(flushDrawEvaluator.getStrongFlushDrawCombos(), holeCards));
        handDrawEvaluation.put("mediumFlushDraw", comboPresentInMap(flushDrawEvaluator.getMediumFlushDrawCombos(), holeCards));
        handDrawEvaluation.put("weakFlushDraw", comboPresentInMap(flushDrawEvaluator.getWeakFlushDrawCombos(), holeCards));

        handDrawEvaluation.put("strongOosd", comboPresentInMap(straightDrawEvaluator.getStrongOosdCombos(), holeCards));
        handDrawEvaluation.put("mediumOosd", comboPresentInMap(straightDrawEvaluator.getMediumOosdCombos(), holeCards));
        handDrawEvaluation.put("weakOosd", comboPresentInMap(straightDrawEvaluator.getWeakOosdCombos(), holeCards));

        handDrawEvaluation.put("strongGutshot", comboPresentInMap(straightDrawEvaluator.getStrongGutshotCombos(), holeCards));
        handDrawEvaluation.put("mediumGutshot", comboPresentInMap(straightDrawEvaluator.getMediumGutshotCombos(), holeCards));
        handDrawEvaluation.put("weakGutshot", comboPresentInMap(straightDrawEvaluator.getWeakGutshotCombos(), holeCards));

        handDrawEvaluation.put("strongOvercards", comboPresentInMap(highCardDrawEvaluator.getStrongTwoOvercards(), holeCards));
        handDrawEvaluation.put("mediumOvercards", comboPresentInMap(highCardDrawEvaluator.getMediumTwoOvercards(), holeCards));
        handDrawEvaluation.put("weakOvercards", comboPresentInMap(highCardDrawEvaluator.getWeakTwoOvercards(), holeCards));

        handDrawEvaluation.put("strongBackDoorFlush", comboPresentInMap(flushDrawEvaluator.getStrongBackDoorFlushCombos(), holeCards));
        handDrawEvaluation.put("mediumBackDoorFlush", comboPresentInMap(flushDrawEvaluator.getMediumBackDoorFlushCombos(), holeCards));
        handDrawEvaluation.put("weakBackDoorFlush", comboPresentInMap(flushDrawEvaluator.getWeakBackDoorFlushCombos(), holeCards));

        handDrawEvaluation.put("strongBackDoorStraight", comboPresentInMap(straightDrawEvaluator.getStrongBackDoorCombos(), holeCards));
        handDrawEvaluation.put("mediumBackDoorStraight", comboPresentInMap(straightDrawEvaluator.getMediumBackDoorCombos(), holeCards));
        handDrawEvaluation.put("weakBackDoorStraight", comboPresentInMap(straightDrawEvaluator.getWeakBackDoorCombos(), holeCards));
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
