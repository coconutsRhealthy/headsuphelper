package com.lennart.model.handevaluation;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.card.Card;

import java.util.*;

/**
 * Created by lennart on 1-10-16.
 */
public class HandEvaluator {
    private List<Card> holeCards;
    private BoardEvaluator boardEvaluator;
    private StraightDrawEvaluator straightDrawEvaluator;
    private FlushDrawEvaluator flushDrawEvaluator;
    private HighCardDrawEvaluator highCardDrawEvaluator;
    private Map<String, Boolean> handDrawEvaluation;

    public HandEvaluator(BoardEvaluator boardEvaluator) {
        //Constructor only to be used for end of hand evalution in ComputerGame
        this.boardEvaluator = boardEvaluator;
    }

    public HandEvaluator(List<Card> holeCards, BoardEvaluator boardEvaluator) {
        this.holeCards = holeCards;
        this.boardEvaluator = boardEvaluator;
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
        Map<Integer, Set<Set<Card>>> rangeSortedCombos = new HashMap<>();
        Set<Card> myHoleCards = new HashSet<>();
        myHoleCards.addAll(yourHoleCardsAsList);

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            rangeSortedCombos.put(entry.getKey(), new HashSet<>());

            for(Set<Card> combo : entry.getValue()) {
                if(combo.equals(myHoleCards)) {
                    rangeSortedCombos.get(entry.getKey()).add(combo);
                }

                Set<Set<Card>> rangeCopy = new HashSet<>();
                rangeCopy.addAll(opponentRange);

                if(!rangeCopy.add(combo)) {
                    rangeSortedCombos.get(entry.getKey()).add(combo);
                }
            }
        }

        double handStrength = -1;

        for (Map.Entry<Integer, Set<Set<Card>>> entry : rangeSortedCombos.entrySet()) {
            for(Set<Card> s : entry.getValue()) {
                if(s.equals(myHoleCards)) {
                    handStrength = getIndexOfHandInSortedCombos(entry.getKey(), rangeSortedCombos);
                }
            }
        }
        return handStrength;
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
