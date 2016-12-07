package com.lennart.model.rangebuilder;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.pokergame.Game;
import com.lennart.model.pokergame.HandPath;
import com.lennart.model.rangebuilder.postflop.FlopRangeBuilder;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilder;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LPO10346 on 9/2/2016.
 */
public class RangeBuilder {
    //Future class which will create estimated opponent ranges based on type of game (single raised, 3bet, 4bet,
    //ch-raised, etc. Then, to estimate the strength of your hand, you will evaluate your hand against this range.
    //For example if your hand beats 60% of the estimated range of your opponent, the hand is 'medium strong'

    //to evaluate your hand against a range, make a map of all possible starthands, sorted from strongest to weakest.
    //see how high your hand ranks in this map. To make this map, first add all combos that getCombosThatMakeRoyalFlush(),
    //then getCombosThatMakeStraightFlush, then getCombosThatMakeQuads(), etc. To correct this map for ranges, remove all
    //combos from this map that do not fall in the range. Of course, the combos that getCombosThatMakeRoyalFlush() and
    //the other methods return, should first be sorted from strongest to weakest, before added to the map.

    BoardEvaluator boardEvaluator = new BoardEvaluator();
    FlushDrawEvaluator flushDrawEvaluator = new FlushDrawEvaluator();
    StraightDrawEvaluator straightDrawEvaluator = new StraightDrawEvaluator();
    HighCardDrawEvaluator highCardDrawEvaluator = new HighCardDrawEvaluator();
    private static List<Card> completeCardDeck = getCompleteCardDeckForTest();

    public Map<Integer, Set<Set<Card>>> createRange(Map<Integer, Set<Card>> preflopRange,
                                                    Map<Integer, Map<Integer, Set<Card>>> flopRange, List<Card> holeCards) {
        Map<Integer, Set<Set<Card>>> allSortedCombosClearedForRange = boardEvaluator.getCopyOfSortedCombos();
        allSortedCombosClearedForRange = removeHoleCardCombosFromAllSortedCombos(allSortedCombosClearedForRange, holeCards);

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombosClearedForRange.entrySet()) {
            loop: for (Iterator<Set<Card>> it = entry.getValue().iterator(); it.hasNext(); ) {
                Set<Card> comboFromAllSortedCombos = it.next();
                for (Map.Entry<Integer, Set<Card>> preflopRangeMapEntry : preflopRange.entrySet()) {
                    if(comboFromAllSortedCombos.equals(preflopRangeMapEntry.getValue())) {
                        continue loop;
                    }
                }
                it.remove();
            }
        }

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombosClearedForRange.entrySet()) {
            loop: for (Iterator<Set<Card>> it = entry.getValue().iterator(); it.hasNext(); ) {
                Set<Card> comboFromAllSortedCombos = it.next();
                for (Map.Entry<Integer, Map<Integer, Set<Card>>> flopRangeMapEntry : flopRange.entrySet()) {
                    if(!flopRangeMapEntry.getValue().isEmpty()) {
                        for (Map.Entry<Integer, Set<Card>> comboToRetainInRange : flopRangeMapEntry.getValue().entrySet()) {
                            if (comboFromAllSortedCombos.equals(comboToRetainInRange.getValue())) {
                                continue loop;
                            }
                        }
                    }
                }
                it.remove();
            }
        }

        //clean up of empty Map entries
        for(Iterator<Map.Entry<Integer, Set<Set<Card>>>> it = allSortedCombosClearedForRange.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<Set<Card>>> entry = it.next();
            if(entry.getValue().isEmpty()) {
                it.remove();
            }
        }

        return allSortedCombosClearedForRange;
    }

    public Map<Integer, Set<Card>> getCombosOfDesignatedStrength(double lowLimit, double highLimit,
                                                                 double percentageOfCombosToInclude) {
        Map<Integer, Set<Set<Card>>> sortedCombos = boardEvaluator.getCopyOfSortedCombos();
        Map<Integer, Set<Set<Card>>> sortedCombosAboveDesignatedStrengthLevel = new HashMap<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            sortedCombosAboveDesignatedStrengthLevel.put(entry.getKey(), entry.getValue());
        }

        double numberUntillWhereYouNeedToRemoveStrongCombos = (1176 - (1176 * highLimit));
        double numberFromWhereYouNeedToStartRemovingAgain = (1176 - (1176 * lowLimit));
        int counter = 0;

        for(Iterator<Map.Entry<Integer, Set<Set<Card>>>> it = sortedCombosAboveDesignatedStrengthLevel.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<Set<Card>>> entry = it.next();

            for(Iterator<Set<Card>> it2 = entry.getValue().iterator(); it2.hasNext(); ) {
                it2.next();
                counter++;

                if(counter < numberUntillWhereYouNeedToRemoveStrongCombos || counter > numberFromWhereYouNeedToStartRemovingAgain) {
                    it2.remove();
                }
            }

            if(entry.getValue().isEmpty()) {
                it.remove();
            }
        }

        Map<Integer, Set<Card>> combosAboveDesignatedStrengthLevel = new HashMap<>();

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(Game.getKnownGameCards());

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(Game.getKnownGameCards());

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombosAboveDesignatedStrengthLevel.entrySet()) {
            for(Set<Card> combo : entry.getValue()) {
                List<Card> asList = new ArrayList<>(combo);

                if(knownGameCardsCopy.add(asList.get(0)) && knownGameCardsCopy.add(asList.get(1))) {
                    if(Math.random() <= percentageOfCombosToInclude) {
                        combosAboveDesignatedStrengthLevel.put(combosAboveDesignatedStrengthLevel.size(), combo);
                    }
                }
                knownGameCardsCopy.clear();
                knownGameCardsCopy.addAll(knownGameCards);
            }
        }
        return combosAboveDesignatedStrengthLevel;
    }

    public Map<Integer, Set<Card>> getCombosOfDesignatedStrength(double lowLimit, double highLimit,
                                                                 double percentageOfCombosToInclude,
                                                                 List<Card> holeCards,
                                                                 Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> combosToReturn = new HashMap<>();
        Map<Integer, Set<Card>> allCombosOfDesignatedStrength = getCombosOfDesignatedStrength(lowLimit, highLimit, 1);
        allCombosOfDesignatedStrength = removeHoleCardCombosFromComboMap(allCombosOfDesignatedStrength, holeCards);
        allCombosOfDesignatedStrength = getCombosThatArePresentInBothMaps(allCombosOfDesignatedStrength, rangeOfPreviousStreet);

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(Game.getKnownGameCards());

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(Game.getKnownGameCards());

        for (Map.Entry<Integer, Set<Card>> entry : allCombosOfDesignatedStrength.entrySet()) {
            List<Card> asList = new ArrayList<>(entry.getValue());

            if(knownGameCardsCopy.add(asList.get(0)) && knownGameCardsCopy.add(asList.get(1))) {
                if (Math.random() < percentageOfCombosToInclude) {
                    combosToReturn.put(combosToReturn.size(), entry.getValue());
                }
            }
            knownGameCardsCopy.clear();
            knownGameCardsCopy.addAll(knownGameCards);
        }
        return combosToReturn;
    }

    //deprecated
    public Map<Integer, Set<Card>> getCombosAboveDesignatedStrengthLevel(double strengthLevel, List<Card> board) {
        Map<Integer, Set<Set<Card>>> sortedCombos = boardEvaluator.getSortedCombos(board);
        Map<Integer, Set<Set<Card>>> sortedCombosAboveDesignatedStrengthLevel = new HashMap<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            sortedCombosAboveDesignatedStrengthLevel.put(entry.getKey(), entry.getValue());
        }

        double doubleFromWhereCombosShouldBeRemoved = (1176 * (1 - strengthLevel));
        int intFromWhereCombosShouldBeRemoved = (int) doubleFromWhereCombosShouldBeRemoved;
        int counter = 0;

        for(Iterator<Map.Entry<Integer, Set<Set<Card>>>> it = sortedCombosAboveDesignatedStrengthLevel.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<Set<Card>>> entry = it.next();

            if(counter > intFromWhereCombosShouldBeRemoved) {
                it.remove();
                continue;
            }

            for(Iterator<Set<Card>> it2 = entry.getValue().iterator(); it2.hasNext(); ) {
                Set<Card> entry2 = it2.next();
                counter++;
                if(counter > intFromWhereCombosShouldBeRemoved) {
                    it2.remove();
                }
            }
        }

        Map<Integer, Set<Card>> combosAboveDesignatedStrengthLevel = new HashMap<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombosAboveDesignatedStrengthLevel.entrySet()) {
            for(Set<Card> combo : entry.getValue()) {
                combosAboveDesignatedStrengthLevel.put(combosAboveDesignatedStrengthLevel.size(), combo);
            }
        }
        return combosAboveDesignatedStrengthLevel;
    }

    public Map<Integer, Set<Card>> getAirRange(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                               Map<Integer, Set<Card>> rangeOfPreviousStreet,
                                               List<Card> board, List<Card> holeCards,
                                               double numberOfAirCombosToBeAdded) {
        Set<Set<Card>> airCombosPool = new HashSet<>();

        if(board.size() == 3) {
            FlushDrawEvaluator flushDrawEvaluator = new FlushDrawEvaluator();
            StraightDrawEvaluator straightDrawEvaluator = new StraightDrawEvaluator();

            Map<Integer, Set<Card>> strongBackDoorFlushDraws = flushDrawEvaluator.getStrongBackDoorFlushCombos(board);
            Map<Integer, Set<Card>> mediumBackDoorFlushDraws = flushDrawEvaluator.getMediumBackDoorFlushCombos(board);
            Map<Integer, Set<Card>> weakBackDoorFlushDraws = flushDrawEvaluator.getWeakBackDoorFlushCombos(board);

            Map<Integer, Set<Card>> strongBackDoorStraightDraws = straightDrawEvaluator.getStrongBackDoorCombos(board);
            Map<Integer, Set<Card>> mediumBackDoorStraightDraws = straightDrawEvaluator.getMediumBackDoorCombos(board);
            Map<Integer, Set<Card>> weakBackDoorStraightDraws = straightDrawEvaluator.getWeakBackDoorCombos(board);

            strongBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorFlushDraws, board);
            mediumBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(mediumBackDoorFlushDraws, board);
            weakBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(weakBackDoorFlushDraws, board);

            strongBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorStraightDraws, board);
            mediumBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(mediumBackDoorStraightDraws, board);
            weakBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(weakBackDoorStraightDraws, board);

            strongBackDoorFlushDraws = removeHoleCardCombosFromComboMap(strongBackDoorFlushDraws, holeCards);
            mediumBackDoorFlushDraws = removeHoleCardCombosFromComboMap(mediumBackDoorFlushDraws, holeCards);
            weakBackDoorFlushDraws = removeHoleCardCombosFromComboMap(weakBackDoorFlushDraws, holeCards);

            strongBackDoorStraightDraws = removeHoleCardCombosFromComboMap(strongBackDoorStraightDraws, holeCards);
            mediumBackDoorStraightDraws = removeHoleCardCombosFromComboMap(mediumBackDoorStraightDraws, holeCards);
            weakBackDoorStraightDraws = removeHoleCardCombosFromComboMap(weakBackDoorStraightDraws, holeCards);

            strongBackDoorFlushDraws = getCombosThatArePresentInBothMaps(strongBackDoorFlushDraws, rangeOfPreviousStreet);
            mediumBackDoorFlushDraws = getCombosThatArePresentInBothMaps(mediumBackDoorFlushDraws, rangeOfPreviousStreet);
            weakBackDoorFlushDraws = getCombosThatArePresentInBothMaps(weakBackDoorFlushDraws, rangeOfPreviousStreet);

            strongBackDoorStraightDraws = getCombosThatArePresentInBothMaps(strongBackDoorStraightDraws, rangeOfPreviousStreet);
            mediumBackDoorStraightDraws = getCombosThatArePresentInBothMaps(mediumBackDoorStraightDraws, rangeOfPreviousStreet);
            weakBackDoorStraightDraws = getCombosThatArePresentInBothMaps(weakBackDoorStraightDraws, rangeOfPreviousStreet);

            airCombosPool = addMapCombosToSet(airCombosPool, strongBackDoorFlushDraws);
            airCombosPool = addMapCombosToSet(airCombosPool, mediumBackDoorFlushDraws);
            airCombosPool = addMapCombosToSet(airCombosPool, weakBackDoorFlushDraws);
            airCombosPool = addMapCombosToSet(airCombosPool, strongBackDoorStraightDraws);
            airCombosPool = addMapCombosToSet(airCombosPool, mediumBackDoorStraightDraws);
            airCombosPool = addMapCombosToSet(airCombosPool, weakBackDoorStraightDraws);
        }

        Map<Integer, Set<Card>> airCombos;

        if(board.size() == 3) {
            airCombos = getCombosOfDesignatedStrength(0, 0.35, 1);
        } else {
            airCombos = getCombosOfDesignatedStrength(0, 0.45, 1);
        }

        airCombos = removeHoleCardCombosFromComboMap(airCombos, holeCards);
        airCombos = getCombosThatArePresentInBothMaps(airCombos, rangeOfPreviousStreet);

        airCombosPool = addMapCombosToSet(airCombosPool, airCombos);

        Map<Integer, Set<Card>> airCombosPoolAsMap = new HashMap<>();

        for(Set s : airCombosPool) {
            airCombosPoolAsMap.put(airCombosPoolAsMap.size(), s);
        }

        Map<Integer, Set<Card>> airCombosAddedToTotalRange = new HashMap<>();
        Set<Set<Card>> setToTestIfComboIsUnique = new HashSet<>();
        Set<Set<Card>> rangeThusFarAsSet = convertMapWithInnerMapToSet(rangeThusFar);

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(Game.getKnownGameCards());

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(Game.getKnownGameCards());

        List<Integer> keysOfAirCombosPoolsAsMap = new ArrayList<>(airCombosPoolAsMap.keySet());
        Collections.shuffle(keysOfAirCombosPoolsAsMap);
        int counter = 0;

        for(int i : keysOfAirCombosPoolsAsMap) {
            if(counter <= numberOfAirCombosToBeAdded) {
                if(setToTestIfComboIsUnique.add(airCombosPoolAsMap.get(i)) &&
                        rangeThusFarAsSet.add(airCombosPoolAsMap.get(i))) {
                    List<Card> asList = new ArrayList<>(airCombosPoolAsMap.get(i));

                    if(knownGameCardsCopy.add(asList.get(0)) && knownGameCardsCopy.add(asList.get(1))) {
                        airCombosAddedToTotalRange.put(airCombosAddedToTotalRange.size(), airCombosPoolAsMap.get(i));
                        counter++;
                    }
                    knownGameCardsCopy.clear();
                    knownGameCardsCopy.addAll(knownGameCards);
                }
            }
        }
        return airCombosAddedToTotalRange;
    }

    public Map<Integer, Set<Card>> getCombosThatAreBothStrongBdFlushAndBdStraightDraw(Map<Integer, Set<Card>> rangeOfPreviousStreet,
                                                                                      Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                                                      List<Card> board, List<Card> holeCards,
                                                                                      int numberNoAirCombos, double percentageOfAirCombos) {
        double numberOfCombosToReturn = percentageOfAirCombos * numberNoAirCombos;

        Map<Integer, Set<Card>> strongBackDoorFlushDraws = new FlushDrawEvaluator().getStrongBackDoorFlushCombos(board);
        Map<Integer, Set<Card>> strongBackDoorStraightDraws = new StraightDrawEvaluator().getStrongBackDoorCombos(board);

        Map<Integer, Set<Card>> bothBdFlushDrawAndBdStraightDraw =
                getCombosThatArePresentInBothMaps(strongBackDoorFlushDraws, strongBackDoorStraightDraws);

        bothBdFlushDrawAndBdStraightDraw = removeHoleCardCombosFromComboMap(bothBdFlushDrawAndBdStraightDraw, holeCards);
        bothBdFlushDrawAndBdStraightDraw = getCombosThatArePresentInBothMaps(bothBdFlushDrawAndBdStraightDraw,
                rangeOfPreviousStreet);
        bothBdFlushDrawAndBdStraightDraw = removeDrawCombosThatAreAlsoHigherCombos(bothBdFlushDrawAndBdStraightDraw, board);

        Map<Integer, Set<Card>> combosToReturn = new HashMap<>();
        Set<Set<Card>> rangeThusFarAsSet = convertMapWithInnerMapToSet(rangeThusFar);

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(Game.getKnownGameCards());

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(Game.getKnownGameCards());

        for (Map.Entry<Integer, Set<Card>> entry : bothBdFlushDrawAndBdStraightDraw.entrySet()) {
            List<Card> asList = new ArrayList<>(entry.getValue());

            if(knownGameCardsCopy.add(asList.get(0)) && knownGameCardsCopy.add(asList.get(1))) {
                if (combosToReturn.size() < numberOfCombosToReturn && rangeThusFarAsSet.add(entry.getValue())) {
                    combosToReturn.put(combosToReturn.size(), entry.getValue());
                }
            }
            knownGameCardsCopy.clear();
            knownGameCardsCopy.addAll(knownGameCards);
        }
        return combosToReturn;
    }

    public Map<Integer, Set<Card>> getStrongBackDoorFlushDrawCombos(Map<Integer, Set<Card>> rangeOfPreviousStreet,
                                                                    Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                                    List<Card> board, List<Card> holeCards,
                                                                    int numberNoAirCombos, double percentageOfAirCombos) {
        double numberOfCombosToReturn = percentageOfAirCombos * numberNoAirCombos;

        Map<Integer, Set<Card>> combosToReturn = new HashMap<>();
        Map<Integer, Set<Card>> strongBackDoorFlushDraws = new FlushDrawEvaluator().getStrongBackDoorFlushCombos(board);
        Map<Integer, Set<Card>> strongBackDoorStraightDraws = new StraightDrawEvaluator().getStrongBackDoorCombos(board);

        strongBackDoorFlushDraws = getCombosThatArePresentInBothMaps(strongBackDoorFlushDraws, rangeOfPreviousStreet);

        strongBackDoorFlushDraws = removeHoleCardCombosFromComboMap(strongBackDoorFlushDraws, holeCards);
        strongBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorFlushDraws, board);
        strongBackDoorStraightDraws = removeHoleCardCombosFromComboMap(strongBackDoorStraightDraws, holeCards);
        strongBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorStraightDraws, board);

        Set<Set<Card>> strongBackDoorFlushDrawsAsSet = convertMapToSet(strongBackDoorFlushDraws);
        Set<Set<Card>> strongBackDoorStraightDrawsAsSet = convertMapToSet(strongBackDoorStraightDraws);

        strongBackDoorFlushDrawsAsSet.removeAll(strongBackDoorStraightDrawsAsSet);

        Set<Set<Card>> rangeThusFarAsSet = convertMapWithInnerMapToSet(rangeThusFar);

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(Game.getKnownGameCards());

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(Game.getKnownGameCards());

        for(Set<Card> s : strongBackDoorFlushDrawsAsSet) {
            List<Card> asList = new ArrayList<>(s);

            if(knownGameCardsCopy.add(asList.get(0)) && knownGameCardsCopy.add(asList.get(1))) {
                if (combosToReturn.size() < numberOfCombosToReturn && rangeThusFarAsSet.add(s)) {
                    combosToReturn.put(combosToReturn.size(), s);
                }
            }
            knownGameCardsCopy.clear();
            knownGameCardsCopy.addAll(knownGameCards);
        }
        return combosToReturn;
    }

    public Map<Integer, Set<Card>> getStrongBackDoorStraightDrawCombos(Map<Integer, Set<Card>> rangeOfPreviousStreet,
                                                                       Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                                       List<Card> board, List<Card> holeCards,
                                                                       int numberNoAirCombos, double percentageOfAirCombos) {
        double numberOfCombosToReturn = percentageOfAirCombos * numberNoAirCombos;

        Map<Integer, Set<Card>> combosToReturn = new HashMap<>();
        Map<Integer, Set<Card>> strongBackDoorFlushDraws = new FlushDrawEvaluator().getStrongBackDoorFlushCombos(board);
        Map<Integer, Set<Card>> strongBackDoorStraightDraws = new StraightDrawEvaluator().getStrongBackDoorCombos(board);

        strongBackDoorStraightDraws = getCombosThatArePresentInBothMaps(strongBackDoorStraightDraws, rangeOfPreviousStreet);

        strongBackDoorFlushDraws = removeHoleCardCombosFromComboMap(strongBackDoorFlushDraws, holeCards);
        strongBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorFlushDraws, board);
        strongBackDoorStraightDraws = removeHoleCardCombosFromComboMap(strongBackDoorStraightDraws, holeCards);
        strongBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorStraightDraws, board);

        Set<Set<Card>> strongBackDoorFlushDrawsAsSet = convertMapToSet(strongBackDoorFlushDraws);
        Set<Set<Card>> strongBackDoorStraightDrawsAsSet = convertMapToSet(strongBackDoorStraightDraws);

        strongBackDoorStraightDrawsAsSet.removeAll(strongBackDoorFlushDrawsAsSet);

        Set<Set<Card>> rangeThusFarAsSet = convertMapWithInnerMapToSet(rangeThusFar);

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(Game.getKnownGameCards());

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(Game.getKnownGameCards());

        for(Set<Card> s : strongBackDoorStraightDrawsAsSet) {
            List<Card> asList = new ArrayList<>(s);

            if(knownGameCardsCopy.add(asList.get(0)) && knownGameCardsCopy.add(asList.get(1))) {
                if (combosToReturn.size() < numberOfCombosToReturn && rangeThusFarAsSet.add(s)) {
                    combosToReturn.put(combosToReturn.size(), s);
                }
            }
            knownGameCardsCopy.clear();
            knownGameCardsCopy.addAll(knownGameCards);
        }
        return combosToReturn;
    }

    public Map<Integer, Set<Card>> getStrongFlushDrawCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                            List<Card> board, double percentage, List<Card> holeCards,
                                                            Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> strongFlushDrawCombos = flushDrawEvaluator.getStrongFlushDrawCombos(board);
        strongFlushDrawCombos = removeHoleCardCombosFromComboMap(strongFlushDrawCombos, holeCards);
        strongFlushDrawCombos = getCombosThatArePresentInBothMaps(strongFlushDrawCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, strongFlushDrawCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumFlushDrawCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                            List<Card> board, double percentage, List<Card> holeCards,
                                                            Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> mediumFlushDrawCombos = flushDrawEvaluator.getMediumFlushDrawCombos(board);
        mediumFlushDrawCombos = removeHoleCardCombosFromComboMap(mediumFlushDrawCombos, holeCards);
        mediumFlushDrawCombos = getCombosThatArePresentInBothMaps(mediumFlushDrawCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, mediumFlushDrawCombos, percentage);
    }

    public Map<Integer, Set<Card>> getWeakFlushDrawCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                          List<Card> board, double percentage, List<Card> holeCards,
                                                          Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> weakFlushDrawCombos = flushDrawEvaluator.getWeakFlushDrawCombos(board);
        weakFlushDrawCombos = removeHoleCardCombosFromComboMap(weakFlushDrawCombos, holeCards);
        weakFlushDrawCombos = getCombosThatArePresentInBothMaps(weakFlushDrawCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, weakFlushDrawCombos, percentage);
    }

    public Map<Integer, Set<Card>> getStrongOosdCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                       List<Card> board, double percentage, List<Card> holeCards,
                                                       Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> strongOosdCombos = straightDrawEvaluator.getStrongOosdCombos(board);
        strongOosdCombos = removeHoleCardCombosFromComboMap(strongOosdCombos, holeCards);
        strongOosdCombos = getCombosThatArePresentInBothMaps(strongOosdCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, strongOosdCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumOosdCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                       List<Card> board, double percentage, List<Card> holeCards,
                                                       Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> mediumOosdCombos = straightDrawEvaluator.getMediumOosdCombos(board);
        mediumOosdCombos = removeHoleCardCombosFromComboMap(mediumOosdCombos, holeCards);
        mediumOosdCombos = getCombosThatArePresentInBothMaps(mediumOosdCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, mediumOosdCombos, percentage);
    }

    public Map<Integer, Set<Card>> getWeakOosdCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                     List<Card> board, double percentage, List<Card> holeCards,
                                                     Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> weakOosdCombos = straightDrawEvaluator.getWeakOosdCombos(board);
        weakOosdCombos = removeHoleCardCombosFromComboMap(weakOosdCombos, holeCards);
        weakOosdCombos = getCombosThatArePresentInBothMaps(weakOosdCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, weakOosdCombos, percentage);
    }

    public Map<Integer, Set<Card>> getStrongGutshotCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                          List<Card> board, double percentage, List<Card> holeCards,
                                                          Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> strongGutshotCombos = straightDrawEvaluator.getStrongGutshotCombos(board);
        strongGutshotCombos = removeHoleCardCombosFromComboMap(strongGutshotCombos, holeCards);
        strongGutshotCombos = getCombosThatArePresentInBothMaps(strongGutshotCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, strongGutshotCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumGutshotCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                          List<Card> board, double percentage, List<Card> holeCards,
                                                          Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> mediumGutshotCombos = straightDrawEvaluator.getMediumGutshotCombos(board);
        mediumGutshotCombos = removeHoleCardCombosFromComboMap(mediumGutshotCombos, holeCards);
        mediumGutshotCombos = getCombosThatArePresentInBothMaps(mediumGutshotCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, mediumGutshotCombos, percentage);
    }

    public Map<Integer, Set<Card>> getWeakGutshotCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                        List<Card> board, double percentage, List<Card> holeCards,
                                                        Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> weakGutshotCombos = straightDrawEvaluator.getWeakGutshotCombos(board);
        weakGutshotCombos = removeHoleCardCombosFromComboMap(weakGutshotCombos, holeCards);
        weakGutshotCombos = getCombosThatArePresentInBothMaps(weakGutshotCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, weakGutshotCombos, percentage);
    }

    public Map<Integer, Set<Card>> getStrongTwoOvercardCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                              List<Card> board, double percentage, List<Card> holeCards,
                                                              Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> strongTwoOvercardCombos = highCardDrawEvaluator.getStrongTwoOvercards(board);
        strongTwoOvercardCombos = removeHoleCardCombosFromComboMap(strongTwoOvercardCombos, holeCards);
        strongTwoOvercardCombos = getCombosThatArePresentInBothMaps(strongTwoOvercardCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, strongTwoOvercardCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumTwoOvercardCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                              List<Card> board, double percentage, List<Card> holeCards,
                                                              Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> mediumTwoOvercardCombos = highCardDrawEvaluator.getMediumTwoOvercards(board);
        mediumTwoOvercardCombos = removeHoleCardCombosFromComboMap(mediumTwoOvercardCombos, holeCards);
        mediumTwoOvercardCombos = getCombosThatArePresentInBothMaps(mediumTwoOvercardCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, mediumTwoOvercardCombos, percentage);
    }
    public Map<Integer, Set<Card>> getWeakTwoOvercardCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                            List<Card> board, double percentage, List<Card> holeCards,
                                                            Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> weakTwoOvercardCombos = highCardDrawEvaluator.getWeakTwoOvercards(board);
        weakTwoOvercardCombos = removeHoleCardCombosFromComboMap(weakTwoOvercardCombos, holeCards);
        weakTwoOvercardCombos = getCombosThatArePresentInBothMaps(weakTwoOvercardCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, weakTwoOvercardCombos, percentage);
    }

    public int getNumberOfNoAirCombos(Map<Integer, Map<Integer, Set<Card>>> rangeNoAir,
                                      Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> rangeNoAirSimpleMap = new HashMap<>();

        for (Map.Entry<Integer, Map<Integer, Set<Card>>> entry : rangeNoAir.entrySet()) {
            for(Map.Entry<Integer, Set<Card>> entry2 : entry.getValue().entrySet()) {
                rangeNoAirSimpleMap.put(rangeNoAirSimpleMap.size(), entry2.getValue());
            }
        }

        Map<Integer, Set<Card>> rangeNoAirCorrectedForPreviousStreetRange =
                getCombosThatArePresentInBothMaps(rangeNoAirSimpleMap, rangeOfPreviousStreet);

        return rangeNoAirCorrectedForPreviousStreetRange.size();
    }

    public Map<Integer, Set<Card>> getCombosThatArePresentInBothMaps(Map<Integer, Set<Card>> map1, Map<Integer, Set<Card>>
            map2) {
        Map<Integer, Set<Card>> combosThatArePresentInBothMaps = new HashMap<>();
        Set<Set<Card>> map1asSet = new HashSet<>();
        Set<Set<Card>> map2asSet = new HashSet<>();

        for (Map.Entry<Integer, Set<Card>> entry : map1.entrySet()) {
            map1asSet.add(entry.getValue());
        }

        for (Map.Entry<Integer, Set<Card>> entry : map2.entrySet()) {
            map2asSet.add(entry.getValue());
        }

        map1asSet.retainAll(map2asSet);

        for(Set<Card> s : map1asSet) {
            combosThatArePresentInBothMaps.put(combosThatArePresentInBothMaps.size(), s);
        }
        return combosThatArePresentInBothMaps;
    }

    public Map<Integer, Set<Card>> removeHoleCardCombosFromComboMap(Map<Integer, Set<Card>> comboMap,
                                                                    List<Card> holeCards) {
        for(Iterator<Map.Entry<Integer, Set<Card>>> it = comboMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<Card>> entry = it.next();
            if(entry.getValue().contains(holeCards.get(0)) || entry.getValue().contains(holeCards.get(1))) {
                it.remove();
            }
        }
        return comboMap;
    }

    public Map<Integer, Set<Card>> convertPreviousActionOrStreetRangeToCorrectFormat(Map<Integer, Set<Set<Card>>>
                                                                                     rangePreviousStreet) {
        Map<Integer, Set<Card>> rangePreviousStreetCorrectFormat = new HashMap<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : rangePreviousStreet.entrySet()) {
            for(Set<Card> combo : entry.getValue()) {
                rangePreviousStreetCorrectFormat.put(rangePreviousStreetCorrectFormat.size(), combo);
            }
        }
        return rangePreviousStreetCorrectFormat;
    }

    public Map<Integer, Set<Set<Card>>> convertPreflopRangeToMapSetSetFormat(Map<Integer, Set<Card>> preflopRange) {
        Map<Integer, Set<Set<Card>>> range = new HashMap<>();
        Set<Set<Card>> outerSetToAdd = new HashSet<>();
        range.put(range.size(), outerSetToAdd);

        for (Map.Entry<Integer, Set<Card>> entry : preflopRange.entrySet()) {
            range.get(0).add(entry.getValue());
        }
        return range;
    }

    public Map<Integer, Map<Integer, Set<Card>>> addXPercentAirCombos(List<Card> holeCards, List<Card> board,
                                                                      Map<Integer, Map<Integer, Set<Card>>> flopRange,
                                                                      Map<Integer, Set<Card>> preflopRange,
                                                                      double percentage) {
        RangeBuilder rangeBuilder = new RangeBuilder();
        int numberOfNoAirCombos = rangeBuilder.countNumberOfCombosMapInnerMap(flopRange, holeCards, preflopRange);
        double desiredRangeSize = numberOfNoAirCombos * (1 + percentage);

        if(board.size() == 3) {
            flopRange.put(flopRange.size(), rangeBuilder.getCombosThatAreBothStrongBdFlushAndBdStraightDraw(preflopRange,
                    flopRange, board, holeCards, numberOfNoAirCombos, 0.05));
            flopRange.put(flopRange.size(), rangeBuilder.getStrongBackDoorFlushDrawCombos(preflopRange, flopRange, board,
                    holeCards, numberOfNoAirCombos, 0.03));
            flopRange.put(flopRange.size(), rangeBuilder.getStrongBackDoorStraightDrawCombos(preflopRange, flopRange, board,
                    holeCards, numberOfNoAirCombos, 0.03));
        }

        double numberOfCombosToBeAddedStill = desiredRangeSize -
                rangeBuilder.countNumberOfCombosMapInnerMap(flopRange, holeCards, preflopRange);

        if(numberOfCombosToBeAddedStill > 0) {
            flopRange.put(flopRange.size(), rangeBuilder.getAirRange(flopRange, preflopRange, board, holeCards,
                    numberOfCombosToBeAddedStill));
        }
        return flopRange;
    }

    public Map<Integer, Set<Card>> getOppositeRangeAtFlop(Map<Integer, Set<Set<Card>>> baseRange,
                                                               Map<Integer, Set<Card>> preflopRange) {
        Map<Integer, Set<Card>> preflopRangeCopy = new HashMap<>();
        for (Map.Entry<Integer, Set<Card>> entry : preflopRange.entrySet()) {
            Set<Card> combo = new HashSet<>();
            combo.addAll(entry.getValue());
            preflopRangeCopy.put(preflopRangeCopy.size(), combo);
        }

        loop: for(Iterator<Map.Entry<Integer, Set<Card>>> it = preflopRangeCopy.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<Card>> entry = it.next();
            for (Map.Entry<Integer, Set<Set<Card>>> entry2 : baseRange.entrySet()) {
                for(Set<Card> combo : entry2.getValue()) {
                    if(entry.getValue().equals(combo)) {
                        it.remove();
                        continue loop;
                    }
                }
            }
        }
        return preflopRangeCopy;
    }

    //helper methods
    private Set<Set<Card>> convertMapToSet(Map<Integer, Set<Card>> mapToConvertToSet) {
        Set<Set<Card>> set = new HashSet<>();

        for (Map.Entry<Integer, Set<Card>> entry : mapToConvertToSet.entrySet()) {
            set.add(entry.getValue());
        }
        return set;
    }

    private Set<Set<Card>> convertMapWithInnerMapToSet(Map<Integer, Map<Integer, Set<Card>>> mapToConvertToSet) {
        Set<Set<Card>> mapAsSet = new HashSet<>();

        for (Map.Entry<Integer, Map<Integer, Set<Card>>> entry : mapToConvertToSet.entrySet()) {
            for (Map.Entry<Integer, Set<Card>> entry2 : entry.getValue().entrySet()) {
                mapAsSet.add(entry2.getValue());
            }
        }
        return mapAsSet;
    }

    private Map<Integer, Set<Card>> getPercentageOfCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                          Map<Integer, Set<Card>> allCombos, double percentage) {
        Map<Integer, Set<Card>> combosToReturn = new HashMap<>();
        Set<Set<Card>> rangeThusFarAsSet = convertMapWithInnerMapToSet(rangeThusFar);

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(Game.getKnownGameCards());

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(Game.getKnownGameCards());

        for (Map.Entry<Integer, Set<Card>> entry : allCombos.entrySet()) {
            if(rangeThusFarAsSet.add(entry.getValue())) {
                List<Card> asList = new ArrayList<>(entry.getValue());
                if(knownGameCardsCopy.add(asList.get(0)) && knownGameCardsCopy.add(asList.get(1))) {
                    if(Math.random() < percentage) {
                        combosToReturn.put(combosToReturn.size(), entry.getValue());
                    }
                }
                knownGameCardsCopy.clear();
                knownGameCardsCopy.addAll(knownGameCards);
            }
        }
        return combosToReturn;
    }

    private Map<Integer, Set<Set<Card>>> removeHoleCardCombosFromAllSortedCombos(Map<Integer, Set<Set<Card>>> allSortedCombos,
                                                                                 List<Card> holeCards) {
        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombos.entrySet()) {
            for(Iterator<Set<Card>> it = entry.getValue().iterator(); it.hasNext(); ) {
                Set<Card> comboToCheck = it.next();
                if(comboToCheck.contains(holeCards.get(0)) || comboToCheck.contains(holeCards.get(1))) {
                    it.remove();
                }
            }
        }
        return allSortedCombos;
    }

    private Map<Integer, Set<Card>> removeDrawCombosThatAreAlsoHigherCombos(Map<Integer, Set<Card>> drawComboMap,
                                                                            List<Card> board) {
        HandEvaluator handEvaluator = new HandEvaluator();

        for(Iterator<Map.Entry<Integer, Set<Card>>> it = drawComboMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<Card>> entry = it.next();

            List<Card> comboAsList = new ArrayList<>();
            comboAsList.addAll(entry.getValue());

            if(handEvaluator.getHandStrength(comboAsList, board) > 0.5) {
                it.remove();
            }
        }
        return drawComboMap;
    }

    private Map<Integer, Set<Card>> removeBackDoorCombosThatAreAlreadyInRangeNoAir(Map<Integer, Set<Card>> backDoorCombos,
                                                                                   Map<Integer, Map<Integer, Set<Card>>>
                                                                                           rangeNoAir) {
        for(Iterator<Map.Entry<Integer, Set<Card>>> it = backDoorCombos.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<Card>> entry = it.next();

            for (Map.Entry<Integer, Map<Integer, Set<Card>>> entry2 : rangeNoAir.entrySet()) {
                for (Map.Entry<Integer, Set<Card>> entry3 : entry2.getValue().entrySet()) {
                    if(entry.getValue().equals(entry3.getValue())) {
                        it.remove();
                    }
                }
            }
        }
        return backDoorCombos;
    }

    private Set<Set<Card>> addMapCombosToSet(Set<Set<Card>> set, Map<Integer, Set<Card>> map) {
        for (Map.Entry<Integer, Set<Card>> entry : map.entrySet()) {
            set.add(entry.getValue());
        }
        return set;
    }

    public int countNumberOfCombos(Map<Integer, Set<Set<Card>>> combos) {
        int counter = 0;

        for (Map.Entry<Integer, Set<Set<Card>>> entry : combos.entrySet()) {
            for(Set<Card> combo : entry.getValue()) {
                counter++;
            }
        }
        return counter;
    }

    public int countNumberOfCombosMapInnerMap(Map<Integer, Map<Integer, Set<Card>>> combos, List<Card> holeCards,
                                              Map<Integer, Set<Card>> rangePreviousStreet) {
        Set<Set<Card>> mapAsSet = new HashSet<>();
        Map<Integer, Set<Card>> mapSimple = new HashMap<>();

        for (Map.Entry<Integer, Map<Integer, Set<Card>>> entry : combos.entrySet()) {
            for (Map.Entry<Integer, Set<Card>> entry2 : entry.getValue().entrySet()) {
                mapAsSet.add(entry2.getValue());
            }
        }

        for(Set<Card> s : mapAsSet) {
            mapSimple.put(mapSimple.size(), s);
        }

        mapSimple = removeHoleCardCombosFromComboMap(mapSimple, holeCards);
        mapSimple = getCombosThatArePresentInBothMaps(mapSimple, rangePreviousStreet);

        return mapSimple.size();
    }

    public Map<Integer, Set<Set<Card>>> getRange(String opponentRangeOrMyPerceivedRange) {
        //TODO: hier nog goed onderscheid maken tussen of je opponent Range wil of jouw perceived range

        Map<Integer, Set<Set<Card>>> rangeToReturn;
        PreflopRangeBuilder preflopRangeBuilder = new PreflopRangeBuilder();
        FlopRangeBuilder flopRangeBuilder = new FlopRangeBuilder();
        String handPath = HandPath.getHandPath();

        if(opponentRangeOrMyPerceivedRange.equals("myPerceivedRange")) {
            Game.removeHoleCardsFromKnownGameCards();
        }

        switch(handPath) {
            case "2bet":
                rangeToReturn = convertPreflopRangeToMapSetSetFormat(preflopRangeBuilder.getOpponentCall2betRange());
                break;
            case "call2bet":
                rangeToReturn = convertPreflopRangeToMapSetSetFormat(preflopRangeBuilder.getOpponent2betRange());
                break;
            case "3bet":
                rangeToReturn = convertPreflopRangeToMapSetSetFormat(preflopRangeBuilder.getOpponentCall3betRange());
                break;
            default:
                rangeToReturn = boardEvaluator.getSortedCombos(Game.getBoardCards());
        }

        if(opponentRangeOrMyPerceivedRange.equals("myPerceivedRange")) {
            Game.addHoleCardsToKnownGameCards();
        }

        return rangeToReturn;
    }

    private static List<Card> getCompleteCardDeckForTest() {
        List<Card> completeCardDeck = new ArrayList<>();

        for(int i = 2; i <= 14; i++) {
            for(int z = 1; z <= 4; z++) {
                if(z == 1) {
                    completeCardDeck.add(new Card(i, 's'));
                }
                if(z == 2) {
                    completeCardDeck.add(new Card(i, 'c'));
                }
                if(z == 3) {
                    completeCardDeck.add(new Card(i, 'd'));
                }
                if(z == 4) {
                    completeCardDeck.add(new Card(i, 'h'));
                }
            }
        }
        return completeCardDeck;
    }

    public static void setCompleteCardDeckForTest() {
        RangeBuilder.completeCardDeck.clear();
        RangeBuilder.completeCardDeck = getCompleteCardDeckForTest();
    }

    public Card getRandomCardForTest() {
        Integer random = ThreadLocalRandom.current().nextInt(0, completeCardDeck.size());

        Card c = completeCardDeck.get(random);

        completeCardDeck.remove(c);

        return c;
    }
}