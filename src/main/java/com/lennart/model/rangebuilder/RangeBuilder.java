package com.lennart.model.rangebuilder;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.pokergame.ComputerGame;
import com.lennart.model.rangebuilder.postflop.PostFlopRangeBuilder;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilder;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.*;

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


    private List<Card> holeCards;
    private List<Card> board;
    private Set<Card> knownGameCards;
    private PreflopRangeBuilder preflopRangeBuilder;
    private BoardEvaluator boardEvaluator;
    private Map<Integer, Set<Set<Card>>> sortedCombos;
    private PostFlopRangeBuilder postFlopRangeBuilder;
    private FlushDrawEvaluator flushDrawEvaluator;
    private StraightDrawEvaluator straightDrawEvaluator;
    private HighCardDrawEvaluator highCardDrawEvaluator;
    private HandEvaluator handEvaluator;
    private Set<Set<Card>> previousOpponentRange;

    public RangeBuilder(ComputerGame computerGame) {
        holeCards = computerGame.getComputerHoleCards();
        board = computerGame.getBoard();
        knownGameCards = computerGame.getKnownGameCards();

        preflopRangeBuilder = new PreflopRangeBuilder(computerGame);

        if(board != null) {
            previousOpponentRange = computerGame.getOpponentRange();
            boardEvaluator = new BoardEvaluator(board);
            sortedCombos = boardEvaluator.getSortedCombosNew();
            postFlopRangeBuilder = new PostFlopRangeBuilder(computerGame, boardEvaluator, this);
            flushDrawEvaluator = boardEvaluator.getFlushDrawEvaluator();
            straightDrawEvaluator = boardEvaluator.getStraightDrawEvaluator();
            highCardDrawEvaluator = boardEvaluator.getHighCardDrawEvaluator();
            handEvaluator = new HandEvaluator(holeCards, boardEvaluator, this);
        }
    }

    public Set<Set<Card>> createRangeNew(Map<Integer, Set<Card>> preflopRange,
                                                    Map<Integer, Map<Integer, Set<Card>>> flopRange) {
        Map<Integer, Set<Set<Card>>> allSortedCombosClearedForRange = getCopyOfSortedCombos();
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

        Set<Set<Card>> rangeToReturn = new HashSet<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombosClearedForRange.entrySet()) {
            for (Set<Card> combo : entry.getValue()) {
                rangeToReturn.add(combo);
            }
        }
        return rangeToReturn;
    }

    public Map<Integer, Set<Card>> getCombosOfDesignatedStrength(double lowLimit, double highLimit) {
        Map<Integer, Set<Set<Card>>> sortedCombosOfDesignatedStrengthLevel = getCopyOfSortedCombos();

        double numberUntillWhereYouNeedToRemoveStrongCombos = (1176 - (1176 * highLimit));
        double numberFromWhereYouNeedToStartRemovingAgain = (1176 - (1176 * lowLimit));
        int counter = 0;

        for(Iterator<Map.Entry<Integer, Set<Set<Card>>>> it = sortedCombosOfDesignatedStrengthLevel.entrySet().iterator(); it.hasNext(); ) {
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

        Map<Integer, Set<Card>> combosOfDesignatedStrengthLevel = new HashMap<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombosOfDesignatedStrengthLevel.entrySet()) {
            for(Set<Card> combo : entry.getValue()) {
                combosOfDesignatedStrengthLevel.put(combosOfDesignatedStrengthLevel.size(), combo);
            }
        }
        return combosOfDesignatedStrengthLevel;
    }

    public Map<Integer, Set<Card>> getCombosOfDesignatedStrength(double lowLimit, double highLimit,
                                                                 double percentageOfCombosToInclude) {
        Map<Integer, Set<Set<Card>>> sortedCombosInMethod = getCopyOfSortedCombos();
        Map<Integer, Set<Set<Card>>> sortedCombosAboveDesignatedStrengthLevel = new HashMap<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombosInMethod.entrySet()) {
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
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

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
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

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

    public Map<Integer, Set<Card>> getAir(Set<Set<Card>> previousRange, double upperLimit, double percentageToInclunde) {
        Map<Integer, Set<Card>> airCombos = new HashMap<>();
        Map<Integer, Set<Card>> airCombosNotCorrectedForDraws = getCombosOfDesignatedStrength(0, upperLimit);
        Set<Set<Card>> allNonBackDoorDraws = getAllNonBackDoorDraws();

        for (Map.Entry<Integer, Set<Card>> entry : airCombosNotCorrectedForDraws.entrySet()) {
            boolean noRegularDraw = false;
            boolean inPreviousRange = false;
            boolean noKnownGameCards = false;

            Set<Set<Card>> allRegularDrawsCopy = new HashSet<>();
            allRegularDrawsCopy.addAll(allNonBackDoorDraws);

            Set<Set<Card>> previousRangeCopy = new HashSet<>();
            previousRangeCopy.addAll(previousRange);

            if(allRegularDrawsCopy.add(entry.getValue())) {
                noRegularDraw = true;
            }
            if(!previousRangeCopy.add(entry.getValue())) {
                inPreviousRange = true;
            }
            if(Collections.disjoint(entry.getValue(), knownGameCards)) {
                noKnownGameCards = true;
            }

            if(noRegularDraw && inPreviousRange && noKnownGameCards) {
                if(Math.random() < percentageToInclunde) {
                    airCombos.put(airCombos.size(), entry.getValue());
                }
            }
        }
        return airCombos;
    }

    public Map<Integer, Set<Card>> getAirRange(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                               Map<Integer, Set<Card>> rangeOfPreviousStreet,
                                               List<Card> board, List<Card> holeCards,
                                               double numberOfAirCombosToBeAdded) {
        Set<Set<Card>> airCombosPool = new HashSet<>();

        if(board.size() == 3) {
            Map<Integer, Set<Card>> strongBackDoorFlushDraws = flushDrawEvaluator.getStrongBackDoorFlushCombos();
            Map<Integer, Set<Card>> mediumBackDoorFlushDraws = flushDrawEvaluator.getMediumBackDoorFlushCombos();
            Map<Integer, Set<Card>> weakBackDoorFlushDraws = flushDrawEvaluator.getWeakBackDoorFlushCombos();

            Map<Integer, Set<Card>> strongBackDoorStraightDraws = straightDrawEvaluator.getStrongBackDoorCombos();
            Map<Integer, Set<Card>> mediumBackDoorStraightDraws = straightDrawEvaluator.getMediumBackDoorCombos();
            Map<Integer, Set<Card>> weakBackDoorStraightDraws = straightDrawEvaluator.getWeakBackDoorCombos();

            strongBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorFlushDraws);
            mediumBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(mediumBackDoorFlushDraws);
            weakBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(weakBackDoorFlushDraws);

            strongBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorStraightDraws);
            mediumBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(mediumBackDoorStraightDraws);
            weakBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(weakBackDoorStraightDraws);

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
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

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
                                                                                      List<Card> holeCards, int numberNoAirCombos,
                                                                                      double percentageOfAirCombos) {
        double numberOfCombosToReturn = percentageOfAirCombos * numberNoAirCombos;

        Map<Integer, Set<Card>> strongBackDoorFlushDraws = flushDrawEvaluator.getStrongBackDoorFlushCombos();
        Map<Integer, Set<Card>> strongBackDoorStraightDraws = straightDrawEvaluator.getStrongBackDoorCombos();

        Map<Integer, Set<Card>> bothBdFlushDrawAndBdStraightDraw =
                getCombosThatArePresentInBothMaps(strongBackDoorFlushDraws, strongBackDoorStraightDraws);

        bothBdFlushDrawAndBdStraightDraw = removeHoleCardCombosFromComboMap(bothBdFlushDrawAndBdStraightDraw, holeCards);
        bothBdFlushDrawAndBdStraightDraw = getCombosThatArePresentInBothMaps(bothBdFlushDrawAndBdStraightDraw,
                rangeOfPreviousStreet);
        bothBdFlushDrawAndBdStraightDraw = removeDrawCombosThatAreAlsoHigherCombos(bothBdFlushDrawAndBdStraightDraw);

        Map<Integer, Set<Card>> combosToReturn = new HashMap<>();
        Set<Set<Card>> rangeThusFarAsSet = convertMapWithInnerMapToSet(rangeThusFar);

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

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
                                                                    List<Card> holeCards, int numberNoAirCombos,
                                                                    double percentageOfAirCombos) {
        double numberOfCombosToReturn = percentageOfAirCombos * numberNoAirCombos;

        Map<Integer, Set<Card>> combosToReturn = new HashMap<>();
        Map<Integer, Set<Card>> strongBackDoorFlushDraws = flushDrawEvaluator.getStrongBackDoorFlushCombos();
        Map<Integer, Set<Card>> strongBackDoorStraightDraws = straightDrawEvaluator.getStrongBackDoorCombos();

        strongBackDoorFlushDraws = getCombosThatArePresentInBothMaps(strongBackDoorFlushDraws, rangeOfPreviousStreet);

        strongBackDoorFlushDraws = removeHoleCardCombosFromComboMap(strongBackDoorFlushDraws, holeCards);
        strongBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorFlushDraws);
        strongBackDoorStraightDraws = removeHoleCardCombosFromComboMap(strongBackDoorStraightDraws, holeCards);
        strongBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorStraightDraws);

        Set<Set<Card>> strongBackDoorFlushDrawsAsSet = convertMapToSet(strongBackDoorFlushDraws);
        Set<Set<Card>> strongBackDoorStraightDrawsAsSet = convertMapToSet(strongBackDoorStraightDraws);

        strongBackDoorFlushDrawsAsSet.removeAll(strongBackDoorStraightDrawsAsSet);

        Set<Set<Card>> rangeThusFarAsSet = convertMapWithInnerMapToSet(rangeThusFar);

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

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
                                                                       List<Card> holeCards, int numberNoAirCombos,
                                                                       double percentageOfAirCombos) {
        double numberOfCombosToReturn = percentageOfAirCombos * numberNoAirCombos;

        Map<Integer, Set<Card>> combosToReturn = new HashMap<>();
        Map<Integer, Set<Card>> strongBackDoorFlushDraws = flushDrawEvaluator.getStrongBackDoorFlushCombos();
        Map<Integer, Set<Card>> strongBackDoorStraightDraws = straightDrawEvaluator.getStrongBackDoorCombos();

        strongBackDoorStraightDraws = getCombosThatArePresentInBothMaps(strongBackDoorStraightDraws, rangeOfPreviousStreet);

        strongBackDoorFlushDraws = removeHoleCardCombosFromComboMap(strongBackDoorFlushDraws, holeCards);
        strongBackDoorFlushDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorFlushDraws);
        strongBackDoorStraightDraws = removeHoleCardCombosFromComboMap(strongBackDoorStraightDraws, holeCards);
        strongBackDoorStraightDraws = removeDrawCombosThatAreAlsoHigherCombos(strongBackDoorStraightDraws);

        Set<Set<Card>> strongBackDoorFlushDrawsAsSet = convertMapToSet(strongBackDoorFlushDraws);
        Set<Set<Card>> strongBackDoorStraightDrawsAsSet = convertMapToSet(strongBackDoorStraightDraws);

        strongBackDoorStraightDrawsAsSet.removeAll(strongBackDoorFlushDrawsAsSet);

        Set<Set<Card>> rangeThusFarAsSet = convertMapWithInnerMapToSet(rangeThusFar);

        Set<Card> knownGameCards = new HashSet<>();
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

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
                                                            double percentage, List<Card> holeCards,
                                                            Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> strongFlushDrawCombos = flushDrawEvaluator.getStrongFlushDrawCombos();
        strongFlushDrawCombos = removeHoleCardCombosFromComboMap(strongFlushDrawCombos, holeCards);
        strongFlushDrawCombos = getCombosThatArePresentInBothMaps(strongFlushDrawCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, strongFlushDrawCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumFlushDrawCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                            double percentage, List<Card> holeCards,
                                                            Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> mediumFlushDrawCombos = flushDrawEvaluator.getMediumFlushDrawCombos();
        mediumFlushDrawCombos = removeHoleCardCombosFromComboMap(mediumFlushDrawCombos, holeCards);
        mediumFlushDrawCombos = getCombosThatArePresentInBothMaps(mediumFlushDrawCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, mediumFlushDrawCombos, percentage);
    }

    public Map<Integer, Set<Card>> getWeakFlushDrawCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                          double percentage, List<Card> holeCards,
                                                          Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> weakFlushDrawCombos = flushDrawEvaluator.getWeakFlushDrawCombos();
        weakFlushDrawCombos = removeHoleCardCombosFromComboMap(weakFlushDrawCombos, holeCards);
        weakFlushDrawCombos = getCombosThatArePresentInBothMaps(weakFlushDrawCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, weakFlushDrawCombos, percentage);
    }

    public Map<Integer, Set<Card>> getStrongOosdCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                       double percentage, List<Card> holeCards,
                                                       Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> strongOosdCombos = straightDrawEvaluator.getStrongOosdCombos();
        strongOosdCombos = removeHoleCardCombosFromComboMap(strongOosdCombos, holeCards);
        strongOosdCombos = getCombosThatArePresentInBothMaps(strongOosdCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, strongOosdCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumOosdCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                       double percentage, List<Card> holeCards,
                                                       Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> mediumOosdCombos = straightDrawEvaluator.getMediumOosdCombos();
        mediumOosdCombos = removeHoleCardCombosFromComboMap(mediumOosdCombos, holeCards);
        mediumOosdCombos = getCombosThatArePresentInBothMaps(mediumOosdCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, mediumOosdCombos, percentage);
    }

    public Map<Integer, Set<Card>> getWeakOosdCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                     double percentage, List<Card> holeCards,
                                                     Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> weakOosdCombos = straightDrawEvaluator.getWeakOosdCombos();
        weakOosdCombos = removeHoleCardCombosFromComboMap(weakOosdCombos, holeCards);
        weakOosdCombos = getCombosThatArePresentInBothMaps(weakOosdCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, weakOosdCombos, percentage);
    }

    public Map<Integer, Set<Card>> getStrongGutshotCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                          double percentage, List<Card> holeCards,
                                                          Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> strongGutshotCombos = straightDrawEvaluator.getStrongGutshotCombos();
        strongGutshotCombos = removeHoleCardCombosFromComboMap(strongGutshotCombos, holeCards);
        strongGutshotCombos = getCombosThatArePresentInBothMaps(strongGutshotCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, strongGutshotCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumGutshotCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                          double percentage, List<Card> holeCards,
                                                          Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> mediumGutshotCombos = straightDrawEvaluator.getMediumGutshotCombos();
        mediumGutshotCombos = removeHoleCardCombosFromComboMap(mediumGutshotCombos, holeCards);
        mediumGutshotCombos = getCombosThatArePresentInBothMaps(mediumGutshotCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, mediumGutshotCombos, percentage);
    }

    public Map<Integer, Set<Card>> getWeakGutshotCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                        double percentage, List<Card> holeCards,
                                                        Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> weakGutshotCombos = straightDrawEvaluator.getWeakGutshotCombos();
        weakGutshotCombos = removeHoleCardCombosFromComboMap(weakGutshotCombos, holeCards);
        weakGutshotCombos = getCombosThatArePresentInBothMaps(weakGutshotCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, weakGutshotCombos, percentage);
    }

    public Map<Integer, Set<Card>> getStrongTwoOvercardCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                              double percentage, List<Card> holeCards,
                                                              Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> strongTwoOvercardCombos = highCardDrawEvaluator.getStrongTwoOvercards();
        strongTwoOvercardCombos = removeHoleCardCombosFromComboMap(strongTwoOvercardCombos, holeCards);
        strongTwoOvercardCombos = getCombosThatArePresentInBothMaps(strongTwoOvercardCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, strongTwoOvercardCombos, percentage);
    }

    public Map<Integer, Set<Card>> getMediumTwoOvercardCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                              double percentage, List<Card> holeCards,
                                                              Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> mediumTwoOvercardCombos = highCardDrawEvaluator.getMediumTwoOvercards();
        mediumTwoOvercardCombos = removeHoleCardCombosFromComboMap(mediumTwoOvercardCombos, holeCards);
        mediumTwoOvercardCombos = getCombosThatArePresentInBothMaps(mediumTwoOvercardCombos, rangeOfPreviousStreet);

        return getPercentageOfCombos(rangeThusFar, mediumTwoOvercardCombos, percentage);
    }
    public Map<Integer, Set<Card>> getWeakTwoOvercardCombos(Map<Integer, Map<Integer, Set<Card>>> rangeThusFar,
                                                            double percentage, List<Card> holeCards,
                                                            Map<Integer, Set<Card>> rangeOfPreviousStreet) {
        Map<Integer, Set<Card>> weakTwoOvercardCombos = highCardDrawEvaluator.getWeakTwoOvercards();
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
        int numberOfNoAirCombos = countNumberOfCombosMapInnerMap(flopRange, holeCards, preflopRange);
        double desiredRangeSize = numberOfNoAirCombos * (1 + percentage);

        if(board.size() == 3) {
            flopRange.put(flopRange.size(), getCombosThatAreBothStrongBdFlushAndBdStraightDraw(preflopRange,
                    flopRange, holeCards, numberOfNoAirCombos, 0.05));
            flopRange.put(flopRange.size(), getStrongBackDoorFlushDrawCombos(preflopRange, flopRange, holeCards,
                    numberOfNoAirCombos, 0.03));
            flopRange.put(flopRange.size(), getStrongBackDoorStraightDrawCombos(preflopRange, flopRange, holeCards,
                    numberOfNoAirCombos, 0.03));
        }

        double numberOfCombosToBeAddedStill = desiredRangeSize -
                countNumberOfCombosMapInnerMap(flopRange, holeCards, preflopRange);

        if(numberOfCombosToBeAddedStill > 0) {
            flopRange.put(flopRange.size(), getAirRange(flopRange, preflopRange, board, holeCards,
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

    public static Set<Set<Card>> convertMapToSet(Map<Integer, Set<Card>> mapToConvertToSet) {
        Set<Set<Card>> set = new HashSet<>();

        for (Map.Entry<Integer, Set<Card>> entry : mapToConvertToSet.entrySet()) {
            set.add(entry.getValue());
        }
        return set;
    }

    public static Map<Integer, Set<Card>> convertSetToMap(Set<Set<Card>> set) {
        Map<Integer, Set<Card>> map = new HashMap<>();

        for(Set<Card> s : set) {
            map.put(map.size(), s);
        }
        return map;
    }

    //helper methods
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
        knownGameCards.addAll(this.knownGameCards);

        Set<Card> knownGameCardsCopy = new HashSet<>();
        knownGameCardsCopy.addAll(this.knownGameCards);

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

    private Map<Integer, Set<Card>> removeDrawCombosThatAreAlsoHigherCombos(Map<Integer, Set<Card>> drawComboMap) {
        for(Iterator<Map.Entry<Integer, Set<Card>>> it = drawComboMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<Card>> entry = it.next();

            List<Card> comboAsList = new ArrayList<>();
            comboAsList.addAll(entry.getValue());

            if(handEvaluator.getHandStrength(comboAsList) > 0.5) {
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

    private Set<Set<Card>> getAllNonBackDoorDraws() {
        Set<Set<Card>> allNonBackDoorDraws = new HashSet<>();

        allNonBackDoorDraws.addAll(convertMapToSet(flushDrawEvaluator.getStrongFlushDrawCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(flushDrawEvaluator.getMediumFlushDrawCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(flushDrawEvaluator.getWeakFlushDrawCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(straightDrawEvaluator.getStrongBackDoorCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(straightDrawEvaluator.getMediumOosdCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(straightDrawEvaluator.getWeakOosdCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(straightDrawEvaluator.getStrongGutshotCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(straightDrawEvaluator.getMediumGutshotCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(straightDrawEvaluator.getWeakGutshotCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(highCardDrawEvaluator.getStrongTwoOvercards()));
        allNonBackDoorDraws.addAll(convertMapToSet(highCardDrawEvaluator.getMediumTwoOvercards()));
        allNonBackDoorDraws.addAll(convertMapToSet(highCardDrawEvaluator.getWeakTwoOvercards()));

        return allNonBackDoorDraws;
    }

    private Set<Set<Card>> getAllStartHandsCorrectedForKnownGameCards() {
        Set<Set<Card>> allStartHandsToReturn = new HashSet<>();

        Map<Integer, Set<Card>> allPossibleStartHands = new HashMap<>();

        for (Map.Entry<Integer, Set<Card>> entry : PreflopRangeBuilderUtil.getAllStartHandsAsSet().entrySet()) {
            allPossibleStartHands.put(allPossibleStartHands.size(), new HashSet<>());
            allPossibleStartHands.get(allPossibleStartHands.size()).addAll(entry.getValue());
        }

        for (Map.Entry<Integer, Set<Card>> entry : allPossibleStartHands.entrySet()) {
            if(Collections.disjoint(entry.getValue(), knownGameCards)) {
                allStartHandsToReturn.add(entry.getValue());
            }
        }
        return allStartHandsToReturn;
    }

    public Set<Set<Card>> getOpponentRange(ComputerGame computerGame) {
        Set<Set<Card>> opponentRange;

        if(previousOpponentRange == null) {
            opponentRange = preflopRangeBuilder.getOpponentPreflopRange();
        } else {
            opponentRange = postFlopRangeBuilder.getOpponentPostFlopRange(previousOpponentRange);
        }
        computerGame.setOpponentRange(opponentRange);

        return opponentRange;
    }

    private Map<Integer, Set<Set<Card>>> getCopyOfSortedCombos() {
        Map<Integer, Set<Set<Card>>> copyOfSortedCombos = new HashMap<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            copyOfSortedCombos.put(copyOfSortedCombos.size(), new HashSet<>());

            for(Set<Card> combo : entry.getValue()) {
                Set<Card> comboCopy = new HashSet<>();
                comboCopy.addAll(combo);

                copyOfSortedCombos.get(copyOfSortedCombos.size() - 1).add(comboCopy);
            }
        }
        return copyOfSortedCombos;
    }

    public BoardEvaluator getBoardEvaluator() {
        return boardEvaluator;
    }

    public PreflopRangeBuilder getPreflopRangeBuilder() {
        return preflopRangeBuilder;
    }

    public HandEvaluator getHandEvaluator() {
        return handEvaluator;
    }
}