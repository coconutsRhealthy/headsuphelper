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


    //gedachtespinsel:
    //airCombos is een mengsel van:
    //alle backdoor combos en alle air combos (< 35%)
    //voeg random combos toe aan je value / straight draw range totdat je gewenste %increase bereikt is

    public Map<Integer, Set<Card>> getAirRangeNew(Map<Integer, Map<Integer, Set<Card>>> rangeNoAir,
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
        Map<Integer, Set<Card>> mediumBackDoorFlushDraws = flushDrawEvaluator.getStrongBackDoorFlushCombos(board);
        Map<Integer, Set<Card>> weakBackDoorFlushDraws = flushDrawEvaluator.getStrongBackDoorFlushCombos(board);

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

        //TODO: Check getBackDoorFlush methods. On 6h 9d Kc board - strong, medium and weak all return the same amount

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
            Integer random = ThreadLocalRandom.current().nextInt(0, airCombosPool.size() + 1);
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

    public Map<Integer, Set<Card>> getStrongFlushDrawCombos(List<Card> board, double percentage) {
        Map<Integer, Set<Card>> strongFlushDrawCombos = flushDrawEvaluator.getStrongFlushDrawCombos(board);
        return getPercentageOfCombos(strongFlushDrawCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumFlushDrawCombos(List<Card> board, double percentage) {
        Map<Integer, Set<Card>> mediumFlushDrawCombos = flushDrawEvaluator.getMediumFlushDrawCombos(board);
        return getPercentageOfCombos(mediumFlushDrawCombos, percentage);
    }

    public Map<Integer, Set<Card>> getWeakFlushDrawCombos(List<Card> board, double percentage) {
        Map<Integer, Set<Card>> weakFlushDrawCombos = flushDrawEvaluator.getWeakFlushDrawCombos(board);
        return getPercentageOfCombos(weakFlushDrawCombos, percentage);
    }

    public Map<Integer, Set<Card>> getStrongOosdCombos(List<Card> board, double percentage) {
        Map<Integer, Set<Card>> strongOosdCombos = straightDrawEvaluator.getStrongOosdCombos(board);
        return getPercentageOfCombos(strongOosdCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumOosdCombos(List<Card> board, double percentage) {
        Map<Integer, Set<Card>> mediumOosdCombos = straightDrawEvaluator.getMediumOosdCombos(board);
        return getPercentageOfCombos(mediumOosdCombos, percentage);
    }

    public Map<Integer, Set<Card>> getWeakOosdCombos(List<Card> board, double percentage) {
        Map<Integer, Set<Card>> weakOosdCombos = straightDrawEvaluator.getWeakOosdCombos(board);
        return getPercentageOfCombos(weakOosdCombos, percentage);
    }

    public Map<Integer, Set<Card>> getStrongGutshotCombos(List<Card> board, double percentage) {
        Map<Integer, Set<Card>> strongGutshotCombos = straightDrawEvaluator.getStrongGutshotCombos(board);
        return getPercentageOfCombos(strongGutshotCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumGutshotCombos(List<Card> board, double percentage) {
        Map<Integer, Set<Card>> mediumGutshotCombos = straightDrawEvaluator.getMediumGutshotCombos(board);
        return getPercentageOfCombos(mediumGutshotCombos, percentage);
    }

    public Map<Integer, Set<Card>> getWeakGutshotCombos(List<Card> board, double percentage) {
        Map<Integer, Set<Card>> weakGutshotCombos = straightDrawEvaluator.getWeakGutshotCombos(board);
        return getPercentageOfCombos(weakGutshotCombos, percentage);
    }

    public Map<Integer, Set<Card>> getStrongTwoOvercardCombos(List<Card> board, double percentage) {
        Map<Integer, Set<Card>> strongTwoOvercardCombos = highCardDrawEvaluator.getStrongTwoOvercards(board);
        return getPercentageOfCombos(strongTwoOvercardCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumTwoOvercardCombos(List<Card> board, double percentage) {
        Map<Integer, Set<Card>> mediumTwoOvercardCombos = highCardDrawEvaluator.getMediumTwoOvercards(board);
        return getPercentageOfCombos(mediumTwoOvercardCombos, percentage);
    }
    public Map<Integer, Set<Card>> getWeakTwoOvercardCombos(List<Card> board, double percentage) {
        Map<Integer, Set<Card>> weakTwoOvercardCombos = highCardDrawEvaluator.getWeakTwoOvercards(board);
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

    public Map<Integer, Set<Card>> getBackDoorAndAirCombos(Map<Integer, Map<Integer, Set<Card>>> rangeNoAir,
                                                           Map<Integer, Set<Card>> rangeOfPreviousStreet,
                                                           double percentageOfAirCombos, List<Card> board,
                                                           List<Card> holeCards) {
        //TODO: correct for holeCards
        //TODO: check of je gevonden air en bd combos niet aanwezig zijn in hogere strength regionen


        Map<Integer, Set<Card>> backDoorAndAirCombos = new HashMap<>();
        Set<Set<Card>> backDoorAndAirCombosAsSet = new HashSet<>();

        Map<Integer, Set<Card>> strongBackDoorFlushDraws = new HashMap<>();
        Map<Integer, Set<Card>> strongBackDoorStraightDraws = new HashMap<>();
        Map<Integer, Set<Card>> mediumBackDoorFlushDraws = new HashMap<>();
        Map<Integer, Set<Card>> mediumBackDoorStraightDraws = new HashMap<>();
        Map<Integer, Set<Card>> weakBackDoorFlushDraws = new HashMap<>();
        Map<Integer, Set<Card>> weakBackDoorStraightDraws = new HashMap<>();
        Map<Integer, Set<Card>> weakBackDoorAndAirCombos = new  HashMap<>();

        int numberOfNoAirCombos = 0;
        double desiredNumberOfAirCombos;
        boolean mediumBackDoor = false;
        boolean weakBackDoorAndAir = false;

        //check eerst nog ff hoeveel combos in rangeNoAir ook present zijn in range uit eerdere straat.
        Map<Integer, Set<Card>> rangeNoAirSimpleMap = new HashMap<>();

        for (Map.Entry<Integer, Map<Integer, Set<Card>>> entry : rangeNoAir.entrySet()) {
            for(Map.Entry<Integer, Set<Card>> entry2 : entry.getValue().entrySet()) {
                rangeNoAirSimpleMap.put(rangeNoAirSimpleMap.size(), entry2.getValue());
            }
        }

        Map<Integer, Set<Card>> rangeNoAirCorrectedForPreviousStreetRange =
                getCombosThatArePresentInBothMaps(rangeNoAirSimpleMap, rangeOfPreviousStreet);

        desiredNumberOfAirCombos = rangeNoAirCorrectedForPreviousStreetRange.size() * percentageOfAirCombos;

        strongBackDoorFlushDraws = getCombosThatArePresentInBothMaps(
                new FlushDrawEvaluator().getStrongBackDoorFlushCombos(board), rangeOfPreviousStreet);
        //remove hier de holecard combos
        strongBackDoorFlushDraws = removeHoleCardCombosFromComboMap(strongBackDoorFlushDraws, holeCards);
        strongBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorFlushDraws, board);
        strongBackDoorFlushDraws = removeBackDoorCombosThatAreAlreadyInRangeNoAir(strongBackDoorFlushDraws, rangeNoAir);

        strongBackDoorStraightDraws = getCombosThatArePresentInBothMaps(
                new StraightDrawEvaluator().getStrongBackDoorCombos(board), rangeOfPreviousStreet);
        //remove hier de holecard combos
        strongBackDoorStraightDraws = removeHoleCardCombosFromComboMap(strongBackDoorStraightDraws, holeCards);
        strongBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorStraightDraws, board);
        strongBackDoorStraightDraws = removeBackDoorCombosThatAreAlreadyInRangeNoAir(strongBackDoorStraightDraws, rangeNoAir);

        Set<Set<Card>> strongBackDoorFlushDrawsAsSet = convertMapToSet(strongBackDoorFlushDraws);
        Set<Set<Card>> strongBackDoorStraightDrawsAsSet = convertMapToSet(strongBackDoorStraightDraws);
        Set<Set<Card>> strongBackDoorDrawsAsSet = new HashSet<>();
        strongBackDoorDrawsAsSet.addAll(strongBackDoorFlushDrawsAsSet);
        strongBackDoorDrawsAsSet.addAll(strongBackDoorStraightDrawsAsSet);

        double numberOfAirCombosPresentThusFar = strongBackDoorDrawsAsSet.size();

        if(numberOfAirCombosPresentThusFar < desiredNumberOfAirCombos) {
            mediumBackDoor = true;
            mediumBackDoorFlushDraws = getCombosThatArePresentInBothMaps(
                    new FlushDrawEvaluator().getMediumBackDoorFlushCombos(board), rangeOfPreviousStreet);
            //remove hier de holecard combos
            mediumBackDoorFlushDraws = removeHoleCardCombosFromComboMap(mediumBackDoorFlushDraws, holeCards);
            mediumBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(mediumBackDoorFlushDraws, holeCards);
            mediumBackDoorFlushDraws = removeBackDoorCombosThatAreAlreadyInRangeNoAir(mediumBackDoorFlushDraws, rangeNoAir);

            mediumBackDoorStraightDraws = getCombosThatArePresentInBothMaps(
                    new StraightDrawEvaluator().getMediumBackDoorCombos(board), rangeOfPreviousStreet);
            //remove hier de holecard combos
            mediumBackDoorStraightDraws = removeHoleCardCombosFromComboMap(mediumBackDoorStraightDraws, holeCards);
            mediumBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(mediumBackDoorStraightDraws, holeCards);
            mediumBackDoorStraightDraws = removeBackDoorCombosThatAreAlreadyInRangeNoAir(mediumBackDoorStraightDraws, rangeNoAir);

            Set<Set<Card>> mediumBackDoorFlushDrawsAsSet = convertMapToSet(mediumBackDoorFlushDraws);
            Set<Set<Card>> mediumBackDoorStraightDrawsAsSet = convertMapToSet(mediumBackDoorStraightDraws);
            Set<Set<Card>> mediumBackDoorDrawsAsSet = new HashSet<>();
            mediumBackDoorDrawsAsSet.addAll(mediumBackDoorFlushDrawsAsSet);
            mediumBackDoorDrawsAsSet.addAll(mediumBackDoorStraightDrawsAsSet);

            numberOfAirCombosPresentThusFar = numberOfAirCombosPresentThusFar + mediumBackDoorDrawsAsSet.size();
        }

        if(numberOfAirCombosPresentThusFar < desiredNumberOfAirCombos) {
            //vul de helft van remaining met weakBackdoors, en de rest met random Air.
            weakBackDoorAndAir = true;
            double numberOfCombosStillNeeded = desiredNumberOfAirCombos - numberOfAirCombosPresentThusFar;

            weakBackDoorFlushDraws = getCombosThatArePresentInBothMaps(
                    new FlushDrawEvaluator().getWeakBackDoorFlushCombos(board), rangeOfPreviousStreet);
            //remove hier de holecard combos
            weakBackDoorFlushDraws = removeHoleCardCombosFromComboMap(weakBackDoorFlushDraws, holeCards);
            weakBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(weakBackDoorFlushDraws, board);
            weakBackDoorFlushDraws = removeBackDoorCombosThatAreAlreadyInRangeNoAir(weakBackDoorFlushDraws, rangeNoAir);

            weakBackDoorStraightDraws = getCombosThatArePresentInBothMaps(
                    new StraightDrawEvaluator().getWeakBackDoorCombos(board), rangeOfPreviousStreet);
            //remove hier de holecard combos
            weakBackDoorStraightDraws = removeHoleCardCombosFromComboMap(weakBackDoorStraightDraws, holeCards);
            weakBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(weakBackDoorStraightDraws, board);
            weakBackDoorStraightDraws = removeBackDoorCombosThatAreAlreadyInRangeNoAir(weakBackDoorStraightDraws, rangeNoAir);

            Map<Integer, Set<Card>> designatedAirStrengthMap = getCombosOfDesignatedStrength(0, 0.4, 1);
            Map<Integer, Set<Card>> airCombosPresentInRange = getCombosThatArePresentInBothMaps(designatedAirStrengthMap,
                    rangeOfPreviousStreet);
            //remove hier de holecard combos
            airCombosPresentInRange = removeHoleCardCombosFromComboMap(airCombosPresentInRange, holeCards);
            airCombosPresentInRange = removeBackDoorCombosThatAreAlreadyInRangeNoAir(airCombosPresentInRange, rangeNoAir);

            //get 25% of combosStillNeeded from weakBackDoorFlush, or max number possible
            int counterWeakBdFlush = 0;
            for (Map.Entry<Integer, Set<Card>> entry : weakBackDoorFlushDraws.entrySet()) {
                if(counterWeakBdFlush <= (numberOfCombosStillNeeded * 0.25)) {
                    if(Math.random() < 0.25) {
                        weakBackDoorAndAirCombos.put(weakBackDoorAndAirCombos.size(), entry.getValue());
                        counterWeakBdFlush++;
                    }
                }
            }

            //get 25% of combosStillNeeded from weakBackDoorStraight, or max number possible
            int counterWeakBdStraight = 0;
            for (Map.Entry<Integer, Set<Card>> entry : weakBackDoorStraightDraws.entrySet()) {
                if(counterWeakBdStraight <= (numberOfCombosStillNeeded * 0.25)) {
                    if(Math.random() < 0.25) {
                        weakBackDoorAndAirCombos.put(weakBackDoorAndAirCombos.size(), entry.getValue());
                        counterWeakBdStraight++;
                    }
                }
            }

            //get remaining number of combosStillNeeded from Air
            numberOfCombosStillNeeded = numberOfCombosStillNeeded - weakBackDoorAndAirCombos.size();

            for (Map.Entry<Integer, Set<Card>> entry : airCombosPresentInRange.entrySet()) {
                if(numberOfCombosStillNeeded > 0) {
                    if(Math.random() < 0.25) {
                        weakBackDoorAndAirCombos.put(weakBackDoorAndAirCombos.size(), entry.getValue());
                    }
                }
            }
        }

        //dit doe je altijd: toevoegen van de strong combos
        Map<Integer, Set<Card>> combosThatAreBothStrongBdStraightAndFlushDraw =
                getCombosThatArePresentInBothMaps(strongBackDoorStraightDraws, strongBackDoorFlushDraws);

        //eerst put je alle combosThatAreBothStrongStraightAndFlushDraw
        for (Map.Entry<Integer, Set<Card>> entry : combosThatAreBothStrongBdStraightAndFlushDraw.entrySet()) {
            if(backDoorAndAirCombosAsSet.size() < desiredNumberOfAirCombos) {
                backDoorAndAirCombosAsSet.add(entry.getValue());
            }
        }

        //dan check je hoeveel combos je nog moet
        double numberOfCombosRemaining = desiredNumberOfAirCombos - backDoorAndAirCombosAsSet.size();
        double numberOfStrongFlushCombosToAdd = numberOfCombosRemaining / 2;

        //dan put je de helft van deze combos uit flushDraw
        int counterStrongFlush = 0;
        for (Map.Entry<Integer, Set<Card>> entry : strongBackDoorFlushDraws.entrySet()) {
            if(counterStrongFlush < numberOfStrongFlushCombosToAdd) {
                int backDoorAndAirCombosAsSetSizeInitial = backDoorAndAirCombosAsSet.size();
                backDoorAndAirCombosAsSet.add(entry.getValue());
                if(backDoorAndAirCombosAsSet.size() == backDoorAndAirCombosAsSetSizeInitial + 1){
                    counterStrongFlush++;
                }
            }
        }

        //dan put je de helft van deze combos uit straightDraw
        for (Map.Entry<Integer, Set<Card>> entry : strongBackDoorStraightDraws.entrySet()) {
            if(backDoorAndAirCombosAsSet.size() < desiredNumberOfAirCombos) {
                backDoorAndAirCombosAsSet.add(entry.getValue());
            }
        }

        //indien niet toereikend vul je aan uit andere comboMap
        for (Map.Entry<Integer, Set<Card>> entry : strongBackDoorFlushDraws.entrySet()) {
            if(backDoorAndAirCombosAsSet.size() < desiredNumberOfAirCombos) {
                backDoorAndAirCombosAsSet.add(entry.getValue());
            } else {
                break;
            }
        }


        if(mediumBackDoor) {
            //eerst voeg je de combos toe die both medium bd straight draw en medium bd flush draw zijn
            Map<Integer, Set<Card>> combosThatAreBothMediumBdStraightAndFlushDraw =
                    getCombosThatArePresentInBothMaps(mediumBackDoorStraightDraws, mediumBackDoorFlushDraws);

            for (Map.Entry<Integer, Set<Card>> entry : combosThatAreBothMediumBdStraightAndFlushDraw.entrySet()) {
                if(backDoorAndAirCombosAsSet.size() < desiredNumberOfAirCombos) {
                    backDoorAndAirCombosAsSet.add(entry.getValue());
                }
            }

            //dan check je hoeveel combos je nog moet
            numberOfCombosRemaining = desiredNumberOfAirCombos - backDoorAndAirCombosAsSet.size();
            double numberOfMediumFlushCombosToAdd = numberOfCombosRemaining / 2;

            //dan put je de helft van deze combos uit flushDraw
            int counterMediumFlush = 0;
            for (Map.Entry<Integer, Set<Card>> entry : mediumBackDoorFlushDraws.entrySet()) {
                if(counterMediumFlush < numberOfMediumFlushCombosToAdd) {
                    int backDoorAndAirCombosAsSetSizeInitial = backDoorAndAirCombosAsSet.size();
                    backDoorAndAirCombosAsSet.add(entry.getValue());
                    if(backDoorAndAirCombosAsSet.size() == backDoorAndAirCombosAsSetSizeInitial + 1){
                        counterMediumFlush++;
                    }
                }
            }

            //dan put je de helft van deze combos uit straightDraw
            for (Map.Entry<Integer, Set<Card>> entry : mediumBackDoorStraightDraws.entrySet()) {
                if(backDoorAndAirCombosAsSet.size() < desiredNumberOfAirCombos) {
                    backDoorAndAirCombosAsSet.add(entry.getValue());
                }
            }

            //indien niet toereikend vul je aan uit andere comboMap
            for (Map.Entry<Integer, Set<Card>> entry : mediumBackDoorFlushDraws.entrySet()) {
                if(backDoorAndAirCombosAsSet.size() < desiredNumberOfAirCombos) {
                    backDoorAndAirCombosAsSet.add(entry.getValue());
                } else {
                    break;
                }
            }


//            while (backDoorAndAirCombos.size() < desiredNumberOfAirCombos) {
//                if(!mediumBackDoorFlushDraws.isEmpty() && !mediumBackDoorStraightDraws.isEmpty()) {
//                    if(Math.random() < 0.5) {
//                        //flush
//                        for(Iterator<Map.Entry<Integer, Set<Card>>> it = mediumBackDoorFlushDraws.entrySet().iterator(); it.hasNext(); ) {
//                            Map.Entry<Integer, Set<Card>> entry = it.next();
//
//                        }
//
//
//                    } else {
//                        //straight
//                    }
//                } else {
//                    if(!mediumBackDoorFlushDraws.isEmpty()) {
//
//                    }
//                    if(!mediumBackDoorStraightDraws.isEmpty()) {
//
//                    }
//                }
//            }

        }

        if(weakBackDoorAndAir) {
            for (Map.Entry<Integer, Set<Card>> entry : weakBackDoorAndAirCombos.entrySet()) {
                backDoorAndAirCombosAsSet.add(entry.getValue());
            }
        }

        //nu maak je er map van om te returnen
        for(Set<Card> s : backDoorAndAirCombosAsSet) {
            backDoorAndAirCombos.put(backDoorAndAirCombos.size(), s);
        }
        return backDoorAndAirCombos;
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


//    public Map<Integer, Set<Card>> getStrongBackDoorFlushCombosMaxAmount(List<Card> board, int maxAmount) {
//        Map<Integer, Set<Card>> strongBackDoorFlushCombosMaxAmount = new StraightDrawEvaluator().getStrongBackDoorCombos(board);
//
//
//
//
//    }

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

    private Map<Integer, Set<Card>> removeHoleCardCombosFromComboMap(Map<Integer, Set<Card>> comboMap,
                                                                     List<Card> holeCards) {
        for(Iterator<Map.Entry<Integer, Set<Card>>> it = comboMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<Card>> entry = it.next();
            if(entry.getValue().contains(holeCards.get(0)) || entry.getValue().contains(holeCards.get(1))) {
                it.remove();
            }
        }
        return comboMap;
    }

    private Map<Integer, Set<Card>> removeDrawCombosThatAreAlsoHigherCombos(Map<Integer, Set<Card>> drawComboMap,
                                                                            List<Card> board) {
        HandEvaluator handEvaluator = new HandEvaluator();

        for(Iterator<Map.Entry<Integer, Set<Card>>> it = drawComboMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<Card>> entry = it.next();

            List<Card> comboAsList = new ArrayList<>();
            comboAsList.addAll(entry.getValue());

            //ff
            Set<Card> testje = new HashSet<>();
            testje.add(new Card(3, 'd'));
            testje.add(new Card(13, 'd'));

            if(entry.getValue().equals(testje)) {
                System.out.println("wacht hier nu");
            }

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