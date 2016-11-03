package com.lennart.model.rangebuilder;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.pokergame.Card;

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

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombosAboveDesignatedStrengthLevel.entrySet()) {
            for(Set<Card> combo : entry.getValue()) {
                if(Math.random() <= percentageOfCombosToInclude) {
                    combosAboveDesignatedStrengthLevel.put(combosAboveDesignatedStrengthLevel.size(), combo);
                }
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

        for (Map.Entry<Integer, Set<Card>> entry : allCombosOfDesignatedStrength.entrySet()) {
            if(Math.random() < percentageOfCombosToInclude) {
                combosToReturn.put(combosToReturn.size(), entry.getValue());
            }
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

    public Map<Integer, Set<Card>> getAirRange(Map<Integer, Map<Integer, Set<Card>>> rangeNoAir,
                                               Map<Integer, Set<Card>> rangeOfPreviousStreet,
                                               double percentageOfAirCombos, List<Card> board, List<Card> holeCards) {

        Map<Integer, Set<Card>> rangeNoAirSimpleMap = new HashMap<>();

        for (Map.Entry<Integer, Map<Integer, Set<Card>>> entry : rangeNoAir.entrySet()) {
            for(Map.Entry<Integer, Set<Card>> entry2 : entry.getValue().entrySet()) {
                rangeNoAirSimpleMap.put(rangeNoAirSimpleMap.size(), entry2.getValue());
            }
        }

        Map<Integer, Set<Card>> rangeNoAirCorrectedForPreviousStreetRange =
                getCombosThatArePresentInBothMaps(rangeNoAirSimpleMap, rangeOfPreviousStreet);

        Set<Set<Card>> totalRange = new HashSet<>();

        for (Map.Entry<Integer, Set<Card>> entry : rangeNoAirCorrectedForPreviousStreetRange.entrySet()) {
            totalRange.add(entry.getValue());
        }

        Set<Set<Card>> airCombosPool = new HashSet<>();

        FlushDrawEvaluator flushDrawEvaluator = new FlushDrawEvaluator();
        StraightDrawEvaluator straightDrawEvaluator = new StraightDrawEvaluator();

        Map<Integer, Set<Card>> strongBackDoorFlushDraws = flushDrawEvaluator.getStrongBackDoorFlushCombos(board);
        Map<Integer, Set<Card>> mediumBackDoorFlushDraws = flushDrawEvaluator.getMediumBackDoorFlushCombos(board);
        Map<Integer, Set<Card>> weakBackDoorFlushDraws = flushDrawEvaluator.getWeakBackDoorFlushCombos(board);

        Map<Integer, Set<Card>> strongBackDoorStraightDraws = straightDrawEvaluator.getStrongBackDoorCombos(board);
        Map<Integer, Set<Card>> mediumBackDoorStraightDraws = straightDrawEvaluator.getMediumBackDoorCombos(board);
        Map<Integer, Set<Card>> weakBackDoorStraightDraws = straightDrawEvaluator.getWeakBackDoorCombos(board);

        Map<Integer, Set<Card>> airCombos = getCombosOfDesignatedStrength(0, 0.35, 1);

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

        airCombos = removeHoleCardCombosFromComboMap(airCombos, holeCards);
        airCombos = getCombosThatArePresentInBothMaps(airCombos, rangeOfPreviousStreet);

        airCombosPool = addMapCombosToSet(airCombosPool, strongBackDoorFlushDraws);
        airCombosPool = addMapCombosToSet(airCombosPool, mediumBackDoorFlushDraws);
        airCombosPool = addMapCombosToSet(airCombosPool, weakBackDoorFlushDraws);
        airCombosPool = addMapCombosToSet(airCombosPool, strongBackDoorStraightDraws);
        airCombosPool = addMapCombosToSet(airCombosPool, mediumBackDoorStraightDraws);
        airCombosPool = addMapCombosToSet(airCombosPool, weakBackDoorStraightDraws);
        airCombosPool = addMapCombosToSet(airCombosPool, airCombos);

        Map<Integer, Set<Card>> airCombosPoolAsMap = new HashMap<>();

        for(Set s : airCombosPool) {
            airCombosPoolAsMap.put(airCombosPoolAsMap.size(), s);
        }

        double desiredSizeOfTotalRange = rangeNoAirCorrectedForPreviousStreetRange.size() * (1 + percentageOfAirCombos);

        Map<Integer, Set<Card>> airCombosAddedToTotalRange = new HashMap<>();

        while(totalRange.size() < desiredSizeOfTotalRange) {
            //get random combos from airCombosPool en add deze aan totalRange
            int totalRangeSizeInitial = totalRange.size();
            Integer random = ThreadLocalRandom.current().nextInt(0, airCombosPool.size());
            totalRange.add(airCombosPoolAsMap.get(random));

            if(totalRange.size() == totalRangeSizeInitial + 1) {
                airCombosAddedToTotalRange.put(airCombosAddedToTotalRange.size(), airCombosPoolAsMap.get(random));
            }
        }

        return airCombosAddedToTotalRange;
    }

    public Map<Integer, Set<Card>> getCombosThatAreBothStrongBdFlushAndBdStraightDraw(Map<Integer, Map<Integer, Set<Card>>> rangeNoAir,
                                                                                      Map<Integer, Set<Card>> rangeOfPreviousStreet,
                                                                                      double percentageOfAirCombos, List<Card> board,
                                                                                      List<Card> holeCards) {
        double numberOfCombosToReturn = percentageOfAirCombos * getNumberOfNoAirCombos(rangeNoAir, rangeOfPreviousStreet);

        Map<Integer, Set<Card>> strongBackDoorFlushDraws = new FlushDrawEvaluator().getStrongBackDoorFlushCombos(board);
        Map<Integer, Set<Card>> strongBackDoorStraightDraws = new StraightDrawEvaluator().getStrongBackDoorCombos(board);

        Map<Integer, Set<Card>> bothBdFlushDrawAndBdStraightDraw =
                getCombosThatArePresentInBothMaps(strongBackDoorFlushDraws, strongBackDoorStraightDraws);

        bothBdFlushDrawAndBdStraightDraw = removeHoleCardCombosFromComboMap(bothBdFlushDrawAndBdStraightDraw, holeCards);
        bothBdFlushDrawAndBdStraightDraw = getCombosThatArePresentInBothMaps(bothBdFlushDrawAndBdStraightDraw,
                rangeOfPreviousStreet);

        Map<Integer, Set<Card>> combosToReturn = new HashMap<>();

        for (Map.Entry<Integer, Set<Card>> entry : bothBdFlushDrawAndBdStraightDraw.entrySet()) {
            if(Math.random() < numberOfCombosToReturn) {
                combosToReturn.put(combosToReturn.size(), entry.getValue());
            }
        }
        return combosToReturn;
    }

    public Map<Integer, Set<Card>> getStrongBackDoorFlushDrawCombos(Map<Integer, Map<Integer, Set<Card>>> rangeNoAir,
                                                                    Map<Integer, Set<Card>> rangeOfPreviousStreet,
                                                                    double percentageOfAirCombos, List<Card> board,
                                                                    List<Card> holeCards) {
        double numberOfCombosToReturn = percentageOfAirCombos * getNumberOfNoAirCombos(rangeNoAir, rangeOfPreviousStreet);

        Map<Integer, Set<Card>> combosToReturn = new HashMap<>();
        Map<Integer, Set<Card>> strongBackDoorFlushDraws = new FlushDrawEvaluator().getStrongBackDoorFlushCombos(board);
        Map<Integer, Set<Card>> strongBackDoorStraightDraws = new StraightDrawEvaluator().getStrongBackDoorCombos(board);

        strongBackDoorFlushDraws = getCombosThatArePresentInBothMaps(strongBackDoorFlushDraws, rangeOfPreviousStreet);

        strongBackDoorFlushDraws = removeHoleCardCombosFromComboMap(strongBackDoorFlushDraws, holeCards);
        strongBackDoorStraightDraws = removeHoleCardCombosFromComboMap(strongBackDoorStraightDraws, holeCards);

        Set<Set<Card>> strongBackDoorFlushDrawsAsSet = convertMapToSet(strongBackDoorFlushDraws);
        Set<Set<Card>> strongBackDoorStraightDrawsAsSet = convertMapToSet(strongBackDoorStraightDraws);

        strongBackDoorFlushDrawsAsSet.removeAll(strongBackDoorStraightDrawsAsSet);

        for(Set<Card> s : strongBackDoorFlushDrawsAsSet) {
            if(combosToReturn.size() < numberOfCombosToReturn) {
                combosToReturn.put(combosToReturn.size(), s);
            }
        }
        return combosToReturn;
    }

    public Map<Integer, Set<Card>> getStrongBackDoorStraightDrawCombos(Map<Integer, Map<Integer, Set<Card>>> rangeNoAir,
                                                                       Map<Integer, Set<Card>> rangeOfPreviousStreet,
                                                                       double percentageOfAirCombos, List<Card> board,
                                                                       List<Card> holeCards) {
        double numberOfCombosToReturn = percentageOfAirCombos * getNumberOfNoAirCombos(rangeNoAir, rangeOfPreviousStreet);

        Map<Integer, Set<Card>> combosToReturn = new HashMap<>();
        Map<Integer, Set<Card>> strongBackDoorFlushDraws = new FlushDrawEvaluator().getStrongBackDoorFlushCombos(board);
        Map<Integer, Set<Card>> strongBackDoorStraightDraws = new StraightDrawEvaluator().getStrongBackDoorCombos(board);

        strongBackDoorStraightDraws = getCombosThatArePresentInBothMaps(strongBackDoorStraightDraws, rangeOfPreviousStreet);

        strongBackDoorFlushDraws = removeHoleCardCombosFromComboMap(strongBackDoorFlushDraws, holeCards);
        strongBackDoorStraightDraws = removeHoleCardCombosFromComboMap(strongBackDoorStraightDraws, holeCards);

        Set<Set<Card>> strongBackDoorFlushDrawsAsSet = convertMapToSet(strongBackDoorFlushDraws);
        Set<Set<Card>> strongBackDoorStraightDrawsAsSet = convertMapToSet(strongBackDoorStraightDraws);

        strongBackDoorStraightDrawsAsSet.removeAll(strongBackDoorFlushDrawsAsSet);

        for(Set<Card> s : strongBackDoorStraightDrawsAsSet) {
            if(combosToReturn.size() < numberOfCombosToReturn) {
                combosToReturn.put(combosToReturn.size(), s);
            }
        }
        return combosToReturn;
    }

    public Map<Integer, Set<Card>> getStrongFlushDrawCombos(List<Card> board, double percentage, List<Card> holeCards,
                                                            Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> strongFlushDrawCombos = flushDrawEvaluator.getStrongFlushDrawCombos(board);
        strongFlushDrawCombos = removeHoleCardCombosFromComboMap(strongFlushDrawCombos, holeCards);
        strongFlushDrawCombos = getCombosThatArePresentInBothMaps(strongFlushDrawCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(strongFlushDrawCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumFlushDrawCombos(List<Card> board, double percentage, List<Card> holeCards,
                                                            Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> mediumFlushDrawCombos = flushDrawEvaluator.getMediumFlushDrawCombos(board);
        mediumFlushDrawCombos = removeHoleCardCombosFromComboMap(mediumFlushDrawCombos, holeCards);
        mediumFlushDrawCombos = getCombosThatArePresentInBothMaps(mediumFlushDrawCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(mediumFlushDrawCombos, percentage);
    }

    public Map<Integer, Set<Card>> getWeakFlushDrawCombos(List<Card> board, double percentage, List<Card> holeCards,
                                                          Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> weakFlushDrawCombos = flushDrawEvaluator.getWeakFlushDrawCombos(board);
        weakFlushDrawCombos = removeHoleCardCombosFromComboMap(weakFlushDrawCombos, holeCards);
        weakFlushDrawCombos = getCombosThatArePresentInBothMaps(weakFlushDrawCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(weakFlushDrawCombos, percentage);
    }

    public Map<Integer, Set<Card>> getStrongOosdCombos(List<Card> board, double percentage, List<Card> holeCards,
                                                       Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> strongOosdCombos = straightDrawEvaluator.getStrongOosdCombos(board);
        strongOosdCombos = removeHoleCardCombosFromComboMap(strongOosdCombos, holeCards);
        strongOosdCombos = getCombosThatArePresentInBothMaps(strongOosdCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(strongOosdCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumOosdCombos(List<Card> board, double percentage, List<Card> holeCards,
                                                       Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> mediumOosdCombos = straightDrawEvaluator.getMediumOosdCombos(board);
        mediumOosdCombos = removeHoleCardCombosFromComboMap(mediumOosdCombos, holeCards);
        mediumOosdCombos = getCombosThatArePresentInBothMaps(mediumOosdCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(mediumOosdCombos, percentage);
    }

    public Map<Integer, Set<Card>> getWeakOosdCombos(List<Card> board, double percentage, List<Card> holeCards,
                                                     Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> weakOosdCombos = straightDrawEvaluator.getWeakOosdCombos(board);
        weakOosdCombos = removeHoleCardCombosFromComboMap(weakOosdCombos, holeCards);
        weakOosdCombos = getCombosThatArePresentInBothMaps(weakOosdCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(weakOosdCombos, percentage);
    }

    public Map<Integer, Set<Card>> getStrongGutshotCombos(List<Card> board, double percentage, List<Card> holeCards,
                                                          Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> strongGutshotCombos = straightDrawEvaluator.getStrongGutshotCombos(board);
        strongGutshotCombos = removeHoleCardCombosFromComboMap(strongGutshotCombos, holeCards);
        strongGutshotCombos = getCombosThatArePresentInBothMaps(strongGutshotCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(strongGutshotCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumGutshotCombos(List<Card> board, double percentage, List<Card> holeCards,
                                                          Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> mediumGutshotCombos = straightDrawEvaluator.getMediumGutshotCombos(board);
        mediumGutshotCombos = removeHoleCardCombosFromComboMap(mediumGutshotCombos, holeCards);
        mediumGutshotCombos = getCombosThatArePresentInBothMaps(mediumGutshotCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(mediumGutshotCombos, percentage);
    }

    public Map<Integer, Set<Card>> getWeakGutshotCombos(List<Card> board, double percentage, List<Card> holeCards,
                                                        Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> weakGutshotCombos = straightDrawEvaluator.getWeakGutshotCombos(board);
        weakGutshotCombos = removeHoleCardCombosFromComboMap(weakGutshotCombos, holeCards);
        weakGutshotCombos = getCombosThatArePresentInBothMaps(weakGutshotCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(weakGutshotCombos, percentage);
    }

    public Map<Integer, Set<Card>> getStrongTwoOvercardCombos(List<Card> board, double percentage, List<Card> holeCards,
                                                              Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> strongTwoOvercardCombos = highCardDrawEvaluator.getStrongTwoOvercards(board);
        strongTwoOvercardCombos = removeHoleCardCombosFromComboMap(strongTwoOvercardCombos, holeCards);
        strongTwoOvercardCombos = getCombosThatArePresentInBothMaps(strongTwoOvercardCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(strongTwoOvercardCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumTwoOvercardCombos(List<Card> board, double percentage, List<Card> holeCards,
                                                              Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> mediumTwoOvercardCombos = highCardDrawEvaluator.getMediumTwoOvercards(board);
        mediumTwoOvercardCombos = removeHoleCardCombosFromComboMap(mediumTwoOvercardCombos, holeCards);
        mediumTwoOvercardCombos = getCombosThatArePresentInBothMaps(mediumTwoOvercardCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(mediumTwoOvercardCombos, percentage);
    }
    public Map<Integer, Set<Card>> getWeakTwoOvercardCombos(List<Card> board, double percentage, List<Card> holeCards,
                                                            Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> weakTwoOvercardCombos = highCardDrawEvaluator.getWeakTwoOvercards(board);
        weakTwoOvercardCombos = removeHoleCardCombosFromComboMap(weakTwoOvercardCombos, holeCards);
        weakTwoOvercardCombos = getCombosThatArePresentInBothMaps(weakTwoOvercardCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(weakTwoOvercardCombos, percentage);
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

    //helper methods
    private Set<Set<Card>> convertMapToSet(Map<Integer, Set<Card>> mapToConvertToSet) {
        Set<Set<Card>> set = new HashSet<>();

        for (Map.Entry<Integer, Set<Card>> entry : mapToConvertToSet.entrySet()) {
            set.add(entry.getValue());
        }
        return set;
    }

    private Map<Integer, Set<Card>> getPercentageOfCombos(Map<Integer, Set<Card>> allCombos, double percentage) {
        Map<Integer, Set<Card>> combosToReturn = new HashMap<>();

        for (Map.Entry<Integer, Set<Card>> entry : allCombos.entrySet()) {
            if(Math.random() < percentage) {
                combosToReturn.put(combosToReturn.size(), entry.getValue());
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
}