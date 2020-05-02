package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.card.Card;
import equitycalc.EquityCalculator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by LennartMac on 11/04/2020.
 */
public class RangeConstructor {

    private double getAverageEquityOfOppRange(List<List<Card>> oppRange, List<Card> board) {
        EquityCalculator equityCalculator = new EquityCalculator();

        Map<List<Card>, Double> equities = equityCalculator.getRangeEquityFlop(oppRange, board);

        double average;
        try {
            average = equities.values().stream()
                    .mapToDouble(a -> a)
                    .average()
                    .getAsDouble();
        } catch (Exception e) {
            e.printStackTrace();
            return getAverageEquityOfOppRange(oppRange, board);
        }

        return average;
    }

    private Map<List<Card>, Double> getAllCombosEquitySortedFlop(List<Card> board) {
        List<List<Card>> allCombos = ActionBuilderUtil.getAllPossibleStartHandsAsList().values().stream().collect(Collectors.toList());
        allCombos = removeCombosWithKnownCards(allCombos, board);
        return sortByValueHighToLow(new EquityCalculator().getRangeEquityFlop(allCombos, board));
    }

    private List<List<Card>> removeCombosWithKnownCards(List<List<Card>> listToRemoveCombosFrom, List<Card> knownCards) {
        return listToRemoveCombosFrom.stream().filter(combo -> Collections.disjoint(combo, knownCards)).collect(Collectors.toList());
    }

    private List<List<Card>> createStartingOppRange(List<Card> botHoleCards) {
        List<List<Card>> startingOppRange = ActionBuilderUtil.getAllPossibleStartHandsAsList()
                .values()
                .stream()
                .collect(Collectors.toList());

        startingOppRange = startingOppRange
                .stream()
                .filter(combo -> Collections.disjoint(combo, botHoleCards))
                .collect(Collectors.toList());

        return startingOppRange;
    }





    /////

    private void getOppPre2betRange(List<List<Card>> sortedPreflopNashCombos) {
        //input:
            //pre2betGroup
                //low
                    //57% plus combo

                //medium
                    //25% plus combo
                    //any suited

                //high
                    //any combo
    }

    private void getOppPreCall2betRange() {
        //input:
            //preCall2betGroup
                //low
                    //60% plus combo
                    //all suited connectors and suited onegappers
                //medium
                    //40% plus combo
                    //any suited
                //high
                    //any combo
    }

    private List<List<Card>> getOppPre3betRange(List<List<Card>> allSortedPfEquityCombos, String pre3betGroup) {
        Set<List<Card>> oppPre3betRange = new LinkedHashSet<>();

        double limit;

        if(pre3betGroup.equals("mediumUnknown") || pre3betGroup.equals("medium")) {
            limit = 0.30;
        } else if(pre3betGroup.equals("low")) {
            limit = 0.16;
        } else if(pre3betGroup.equals("high")) {
            limit = 0.37;
        } else {
            limit = 1;
            System.out.println("pre3betGroup is unknown, should not come here");
        }

        for(int i = 0; i < allSortedPfEquityCombos.size() * limit; i++) {
            oppPre3betRange.add(allSortedPfEquityCombos.get(i));
        }

        if(pre3betGroup.equals("mediumUnknown") || pre3betGroup.equals("medium")) {
            List<List<Card>> extraCombos = allSortedPfEquityCombos.stream().filter(combo -> {
                boolean suitedConnector = comboIsSuitedConnector(combo, 1) || comboIsSuitedConnector(combo, 2)
                        || comboIsSuitedConnector(combo, 3);

                return suitedConnector && combo.get(0).getRank() >= 4 && combo.get(1).getRank() >= 4;
            }).collect(Collectors.toList());

            oppPre3betRange.addAll(extraCombos);
        }

        if(pre3betGroup.equals("high")) {
            List<List<Card>> extraCombos = allSortedPfEquityCombos.stream()
                    .filter(combo -> {
                        boolean suitedConnector = comboIsSuitedConnector(combo, 1) || comboIsSuitedConnector(combo, 2)
                                || comboIsSuitedConnector(combo, 3);

                        boolean suitedHigh = false;

                        if(!suitedConnector) {
                            if(combo.get(0).getSuit() == combo.get(1).getSuit()) {
                                if(combo.get(0).getRank() >= 12 || combo.get(1).getRank() >= 12) {
                                    suitedHigh = true;
                                }
                            }
                        }

                        return suitedConnector || suitedHigh;
                    }).collect(Collectors.toList());

            oppPre3betRange.addAll(extraCombos);
        }

        return oppPre3betRange.stream().collect(Collectors.toList());


        //input:
            //pre3betGroup
                //low
                    //84% plus
                //medium
                    //70% plus
                    //all suited connectors and suited onegappers and two-gappers
                //high
                    //50% plus
                    //any suited

    }

    private void getOppPreCall3betRange() {
        //input:
            //preCall3betGroup
                //low
                    //70% plus
                    //suited connectors
                //medium
                    //50% plus
                    //all suited connectors and suited onegappers and two-gappers
                //high
                    //35% plus
                    //any suited
    }
    
    private void getOppRaiseRangeYo() {
        //input

        //opp aggroness
            //low
            //medium
            //high

        //betsize
            //small
            //medium
            //large

        //------------

        //opp aggro low
            //raisesize small
                //value
                    //vanaf 80%

                //draw
                    //none

                //air
                    //none

            //raisesize medium
                //value
                    //vanaf 87%

                //draw
                    //none

                //air
                    //none

            //raisesize large
                //value
                    //vanaf 92

                //draw
                    //none

                //air
                    //none

        //opp aggro medium
            //raisesize small
                //value
                    //vanaf 75%

                //draw
                    //strong draws

                //air
                    //12% onder 50%

            //raisesize medium
                //value
                    //vanaf 79%

                //draw
                    //strong draws

                //air
                    //10% onder 50%

            //raisesize large
                //value
                    //vanaf 83%

                //draw
                    //strong draws

                //air
                    //8% onder 50%


        //opp aggro high
            //raisesize small
                //value
                    //vanaf 60%

                //draw
                    //alle strong en medium draws

                //air
                    //24% van onderste 50% combos

            //raisesize medium
                //value
                    //vanaf 70%

                //draw
                    //alle strong en medium draws

                //air
                    //21% van onderste 50% combos (non draw)

            //raisesize large
                //value
                    //vanaf 78%

                //draw
                    //alle strong draws
                    //50% van de medium draws

                //air
                    //18% van onderste 50% combos

    }

    public static void main(String[] args) {
//        //System.out.println((int) (1326 * 0.6));
//
//        List<Card> combo1 = Arrays.asList(new Card(8, 'd'), new Card(11, 'c'));
//        List<Card> combo2 = Arrays.asList(new Card(7, 's'), new Card(3, 'h'));
//        List<Card> combo3 = Arrays.asList(new Card(11, 'c'), new Card(8, 'd'));
//        List<Card> combo4 = Arrays.asList(new Card(11, 'c'), new Card(8, 'd'));
//
//        List<List<Card>> input = Arrays.asList(combo1, combo2, combo3, combo4);

//        List<List<Card>> output = new RangeConstructor().testMethod();
//
//        System.out.println("wacht");

        new RangeConstructor().testMethod();

        System.out.println(new EquityCalculator().getComboEquityFlop(Arrays.asList(new Card(7, 's'), new Card(2, 'd')),
                Arrays.asList(new Card(3, 'c'), new Card(7, 'd'), new Card(13, 's'))));
        //jouw eigen single combo equity calculation moet vaker gebeuren dan 50 keer...
    }

    private void testMethod() {
        List<Card> board = Arrays.asList(new Card(3, 'c'), new Card(7, 'd'), new Card(13, 's'));
        List<Card> botHoleCards = Arrays.asList(new Card(2, 's'), new Card(4, 's'));

        List<List<Card>> oppStartingRange = ActionBuilderUtil.getAllPossibleStartHandsAsList().values().stream().collect(Collectors.toList());


        Map<List<Card>, Double> sortedEquities = getAllCombosEquitySortedFlop(board);




        List<List<Card>> allCombosEquitySorted = sortedEquities.keySet().stream().collect(Collectors.toList());


        //System.out.println("wacht");



        List<Card> knownGameCards = new ArrayList<>();
        knownGameCards.addAll(board);
        knownGameCards.addAll(botHoleCards);

        List<List<Card>> oppBetRange = getOppBetRangeYo(oppStartingRange, allCombosEquitySorted, "low", "large", board, botHoleCards);

        double averageEquity = getAverageEquityOfRangeNew(sortedEquities, oppBetRange);

        System.out.println(averageEquity);
//
//        //getDraws(Arrays.asList(new Card(6, 'c'), new Card(9, 'd'), new Card(12, 's')));
//
//
//        Set<String> a = new HashSet<>();
//        a.add("Z");
//        a.add("T");
//
//        Set<String> b = new HashSet<>();
//        b.add("T");
//        b.add("Z");
//
//        System.out.println(a.equals(b));
//
//
//        //moet je gaan werken met Sets??

    }

    private double getAverageEquityOfRangeNew(Map<List<Card>, Double> sortedEquities, List<List<Card>> range) {
        Map<List<Card>, Double> sortedEquitiesRangeFiltered = sortedEquities.entrySet().stream().filter(entry -> {
            List<Card> combo = Arrays.asList(entry.getKey().get(0), entry.getKey().get(1));
            List<Card> comboReverseOrder = Arrays.asList(entry.getKey().get(1), entry.getKey().get(0));
            return range.contains(combo) || range.contains(comboReverseOrder);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        double average = -1;

        try {
            average = sortedEquitiesRangeFiltered.values().stream()
                    .mapToDouble(a -> a)
                    .average()
                    .getAsDouble();
        } catch (Exception e) {
            System.out.println("wtf!");
            e.printStackTrace();
        }

        return average;
    }

    private List<List<Card>> getDraws(List<String> drawsToInclude, List<Card> board) {
        StraightDrawEvaluator straightDrawEvaluator = new StraightDrawEvaluator(board);
        FlushDrawEvaluator flushDrawEvaluator = new FlushDrawEvaluator(board);

        List<List<Card>> draws = new ArrayList<>();

        if(drawsToInclude.contains("strongOosd")) {
            draws.addAll(convertDrawMapToList(straightDrawEvaluator.getStrongOosdCombos()));
        }

        if(drawsToInclude.contains("mediumOosd")) {
            draws.addAll(convertDrawMapToList(straightDrawEvaluator.getMediumOosdCombos()));
        }

        if(drawsToInclude.contains("weakOosd")) {
            draws.addAll(convertDrawMapToList(straightDrawEvaluator.getWeakOosdCombos()));
        }

        if(drawsToInclude.contains("strongGutshot")) {
            draws.addAll(convertDrawMapToList(straightDrawEvaluator.getStrongGutshotCombos()));
        }

        if(drawsToInclude.contains("mediumGutshot")) {
            draws.addAll(convertDrawMapToList(straightDrawEvaluator.getMediumOosdCombos()));
        }

        if(drawsToInclude.contains("weakGutshot")) {
            draws.addAll(convertDrawMapToList(straightDrawEvaluator.getWeakGutshotCombos()));
        }

        if(drawsToInclude.contains("strongFd")) {
            draws.addAll(convertDrawMapToList(flushDrawEvaluator.getStrongFlushDrawCombos()));
        }

        if(drawsToInclude.contains("mediumFd")) {
            draws.addAll(convertDrawMapToList(flushDrawEvaluator.getMediumFlushDrawCombos()));
        }

        if(drawsToInclude.contains("weakFd")) {
            draws.addAll(convertDrawMapToList(flushDrawEvaluator.getWeakFlushDrawCombos()));
        }

        return draws;
    }

    private List<List<Card>> getAirCombos(List<List<Card>> allCombosEquitySorted, List<List<Card>> valueAndDrawRange,
                                          double airPercentageToAdd, List<Card> knownGameCards) {
        List<List<Card>> airCombosToAddToRange = new ArrayList<>();

        List<List<Card>> airCombosTotal = allCombosEquitySorted.subList((int) (allCombosEquitySorted.size() * 0.55), allCombosEquitySorted.size());
        List<List<Card>> eligibleAirCombos = removeCombosThatAreInRange(valueAndDrawRange, airCombosTotal);
        eligibleAirCombos = removeCombosWithKnownCards(eligibleAirCombos, knownGameCards);

        int numberOfCombosToAdd = (int) (valueAndDrawRange.size() * ((airPercentageToAdd / 100) + 1)) - valueAndDrawRange.size();
        int numberOfAirCombosAdded = 0;

        while(numberOfAirCombosAdded < numberOfCombosToAdd && !eligibleAirCombos.isEmpty()) {
            List<Card> randomAirCombo = getRandomComboFromList(eligibleAirCombos);
            airCombosToAddToRange.add(randomAirCombo);
            eligibleAirCombos.remove(randomAirCombo);
            numberOfAirCombosAdded++;
        }

        return airCombosToAddToRange;
    }

    private List<List<Card>> convertDrawMapToList(Map<Integer, Set<Card>> drawMap) {
        return drawMap.values().stream()
                .map(combo -> combo
                        .stream()
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private List<Card> getRandomComboFromList(List<List<Card>> input) {
        int min = 0;
        int max = input.size() - 1;
        int random = (int)(Math.random() * ((max - min) + 1)) + min;
        return input.get(random);
    }

    private List<List<Card>> retainCombosThatAreInRange(List<List<Card>> range, List<List<Card>> widerList) {
        List<Set<Card>> widerListWithSets = widerList.stream().map(combo -> combo.stream().collect(Collectors.toSet())).collect(Collectors.toList());
        List<Set<Card>> rangeWithSets = range.stream().map(combo -> combo.stream().collect(Collectors.toSet())).collect(Collectors.toList());
        widerListWithSets.retainAll(rangeWithSets);
        return widerListWithSets.stream().map(comboAsSet -> comboAsSet.stream().collect(Collectors.toList())).collect(Collectors.toList());
    }

    private List<List<Card>> removeCombosThatAreInRange(List<List<Card>> range, List<List<Card>> widerList) {
        List<Set<Card>> widerListWithSets = widerList.stream().map(combo -> combo.stream().collect(Collectors.toSet())).collect(Collectors.toList());
        List<Set<Card>> rangeWithSets = range.stream().map(combo -> combo.stream().collect(Collectors.toSet())).collect(Collectors.toList());
        widerListWithSets.removeAll(rangeWithSets);
        return widerListWithSets.stream().map(comboAsSet -> comboAsSet.stream().collect(Collectors.toList())).collect(Collectors.toList());
    }

    private List<List<Card>> filterOutDoubleCombos(List<List<Card>> input) {
        Set<Set<Card>> asSet = input.stream().map(combo -> combo.stream().collect(Collectors.toSet())).collect(Collectors.toSet());
        return asSet.stream().map(comboAsSet -> comboAsSet.stream().collect(Collectors.toList())).collect(Collectors.toList());
    }

    private List<List<Card>> getOppBetRangeYo(List<List<Card>> oppStartingRange, List<List<Card>> allCombosEquitySorted,
                                  String oppAggroness, String oppBetsize, List<Card> board, List<Card> botHoleCards) {
        List<List<Card>> oppBetRange = new ArrayList<>();
        List<Card> knownGameCards = Stream.concat(board.stream(), botHoleCards.stream()).collect(Collectors.toList());

        if(oppAggroness.equals("low")) {
            if(oppBetsize.equals("small")) {
                double valuePercentage = 60;
                List<String> drawsToInclude = Arrays.asList("strongFd", "strongOosd", "strongGutshot");
                double drawPercentageToInclude = 100;
                double airPercentage = 5;
                oppBetRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppBetsize.equals("medium")) {
                double valuePercentage = 70;
                List<String> drawsToInclude = Arrays.asList("strongFd", "strongOosd");
                double drawPercentageToInclude = 100;
                double airPercentage = 4;
                oppBetRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppBetsize.equals("large")) {
                double valuePercentage = 83;
                List<String> drawsToInclude = Arrays.asList("strongFd", "strongOosd");
                double drawPercentageToInclude = 50;
                double airPercentage = 2;
                oppBetRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            }
        }



        //input

        //opp aggroness
            //low
            //medium
            //high

        //betsize
            //small
            //medium
            //large


        //------------

        //opp aggro low
            //betsize small
                //value
                    //vanaf 60%

                //draw
                    //alle strong draws

                //air
                    //5% van onderste 50% combos

            //betsize medium
                //value
                    //bovenste 70%

                //draw
                    //strong fd en strong oosd

                //air
                    //4% van onderste 50% combos

            //betsize large
                //value
                    //vanaf 83%

                //draw
                    //50% strong fd en strong oosd

                //air
                    //2% van onderste 50% combos


        //opp aggro medium
            //betsize small
                //value
                    //vanaf 53%

                //draw
                    //alle strong draws
                    //alle medium draws

                //air
                    //20% van onderste 50% combos

            //betsize medium
                //value
                    //vanaf 63%

                //draw
                    //alle strong draws
                    //50% medium draws

                //air
                    //17% van onderste 50% combos

            //betsize large
                //value
                    //bovenste 77% combos

                //draw
                    //alle strong draws
                    //20% medium draws

                //air
                    //15% van onderste 50% combos


        //opp aggro high
            //betsize small
                //value
                    //vanaf 45%

                //draw
                    //alle strong draws
                    //alle medium draws
                    //alle weak draws

                //air
                    //33% van onderste 50% combos

            //betsize medium
                //value
                    //vanaf 55%

                //draw
                    //alle strong draws
                    //alle medium draws

                //air
                    //28% van onderste 50% combos

            //betsize large
                //value
                    //vanaf 68%

                //draw
                    //alle strong draws
                    //70% medium draws

                //air
                    //25% van onderste 50% combos

        return oppBetRange;
    }

    private List<List<Card>> fillRange(List<List<Card>> oppStartingRange, List<List<Card>> allCombosEquitySorted,
                                       List<Card> board, List<Card> knownGameCards, double valuePercentage,
                                       double airPercentage, List<String> drawsToInclude, double drawPercentageToInclude) {
        List<List<Card>> range = new ArrayList<>();

        //value
        List<List<Card>> valueCombos = allCombosEquitySorted.subList(0, (int) (allCombosEquitySorted.size() * (1 - (valuePercentage / 100))));
        List<List<Card>> eligibleValueCombos = retainCombosThatAreInRange(oppStartingRange, valueCombos);
        eligibleValueCombos = removeCombosWithKnownCards(eligibleValueCombos, knownGameCards);
        range.addAll(eligibleValueCombos);
        range = filterOutDoubleCombos(range);

        //draw
        List<List<Card>> draws = getDraws(drawsToInclude, board);
        List<List<Card>> eligibleDraws = retainCombosThatAreInRange(oppStartingRange, draws);
        eligibleDraws = removeCombosWithKnownCards(eligibleDraws, knownGameCards);

        if((drawPercentageToInclude / 100) < 1) {
            for(List<Card> draw : eligibleDraws) {
                if(Math.random() < drawPercentageToInclude) {
                    range.add(draw);
                }
            }
        } else {
            range.addAll(eligibleDraws);
        }

        range = filterOutDoubleCombos(range);

        //air
        List<List<Card>> airCombos = getAirCombos(allCombosEquitySorted, range, airPercentage, knownGameCards);
        range.addAll(airCombos);
        range = filterOutDoubleCombos(range);

        return range;
    }

    private void getOppCheckRangeYo() {

        //input

            //opp aggroness
                //low
                //medium
                //high

            //potsize
                //small
                //medium
                //large


        //------------

        //opp aggro low
            //pot small
                //value
                    //37% van de 40% hoogste combos

                //draw
                    //70% van alle strong en medium draws

                //air
                    //100% van de 60% laagste combos

            //pot medium
                //value
                    //50% van de 30% hoogste combos

                //draw
                    //80% van alle strong draws, 100% van alle medium draws

                //air
                    //100% van de 70% laagste combos

            //pot large
                //value
                    //50% van de 20% hoogste combos

                //draw
                    //90% van alle strong draws, 100% van alle medium draws

                //air
                    //100% van de 80% laagste combos



        //opp aggro medium
            //pot small
                //value
                    //25% van de 40% hoogste combos

                //draw
                    //35% van alle strong en medium draws

                //air
                    //100% van de 60% laagste combos

            //pot medium
                //value
                    //25% van de 30% hoogste combos

                //draw
                    //40% van alle strong draws
                    //75% van alle medium draws

                //air
                    //100% van de 70% laagste combos

            //pot large
                //value
                    //25% van de 20% hoogste combos

                //draw
                    //50% van alle strong draws
                    //100% van alle medium draws

                //air
                    //100% van de 80% laagste combos

        //opp aggro high
            //pot small
                //value
                    //15% van de 40% hoogste combos

                //draw
                    //10% van alle strong draws
                    //20% van alle medium draws

                //air
                    //100% van de 60% laagste combos

            //pot medium
                //value
                    //15% van de 30% hoogste combos

                //draw
                    //15% van alle strong draws
                    //25% van alle medium draws

                //air
                    //100% van de 70% laagste combos

            //pot large
                //value
                    //15% van de 75% hoogste combos

                //draw
                    //25% van alle strong draws
                    //45% van alle medium draws

                //air
                    //100% van de 75% laagste combos




        //-----------

        //basis:

            //onderste 60% combos
                //if potsize > 200 && potsize < 300
                    //onderste 70% combos

            //helft van deze draws:
                //strong fd, strong oosd, strong gutshot, medium fd, medium oosd, medium gutshot

            //33% van de bovenste 40% combos
                //if aggression low:

                //if aggression medium:

                //if aggresion high:


    }

    private void getOppCallRangeYo() {

        //input

            //opp looseness
                //low
                //medium
                //high

            //facing bet / raise
                //small
                //medium
                //large


        //------------

        //opp looseness low
            //facing bet small
                //value
                    //alle hoogste 70% combos
                    //25% van combos tussen 60 en 70%

                //draw
                    //alle strong draws
                    //geen medium draws

                //air
                    //niks

            //facing bet medium
                //value
                    //alle hoogste 80% combos
                    //25% van combos tussen 70 en 80%

                //draw
                    //alle strong oosd en fd
                    //50% van strong gutshot
                    //geen medium draws

                //air
                    //niks

            //facing bet large
                //value
                    //alle hoogste 90% combos
                    //25% van combos tussen 80 en 90%

                //draw
                    //alle strong oosd en fd

                //air
                    //niks

        //opp looseness medium
            //facing bet small
                //value
                    //alle hoogste 60% combos
                    //50% van combos tussen 50 en 60%

                //draw
                    //alle strong draws
                    //55% van alle medium draws

                //air
                    //flop en turn
                        //15% van combos tussen 0 en 50%

                    //river
                        //10% van combos tussen 40 en 50%
                        //0% van combos tussen 0 en 40%

            //facing bet medium
                //value
                    //alle hoogste 70% combos
                    //30% van combos tussen 60 en 70%

                //draw
                    //alle strong draws
                    //40% van alle medium draws

                //air
                    //flop en turn
                        //10% van combos tussen 0 en 50%

                    //river
                        //7% van combos tussen 40 en 50%
                        //0% van combos tussen 0 en 40%

            //facing bet large
                //value
                    //alle hoogste 80% combos
                    //30% van combos tussen 70 en 80%

                //draw
                    //alle strong draws
                    //20% van alle medium draws

                //air
                    //flop en turn
                        //5% van combos tussen 40 en 50%

                    //river
                        //5% van combos tussen 40 en 50%
                        //0% van combos tussen 0 en 40%

        //opp looseness high
            //facing bet small
                //value
                    //alle hoogste 50% combos

                //draw
                    //alle strong draws
                    //alle medium draws

                //air
                    //62% van combos tussen 40 en 50%
                    //50% van alle laagste 50% combos

            //facing bet medium
                //value
                    //alle hoogste 60% combos
                    //50% van combos tussen 50 en 60%

                //draw
                    //alle strong draws
                    //60% van alle medium draws

                //air
                    //43% van combos tussen 40 en 50%
                    //30% van de laagste 40% combos

            //facing bet large
                //value
                    //alle hoogste 70% combos
                    //50% van combos tussen 60 en 70%
                    //25% van combos tussen 50 en 60%

                //draw
                    //alle strong draws
                    //20% van alle medium draws

                //air
                    //35% van combos tussen 40 en 50%
                    //20% van de laagste 40% combos
    }

    private void getOppFoldRangeYo() {

        //input

            //opp looseness
                //low
                //medium
                //high

            //potsize
                //small
                //medium
                //large

        //------------

        //everything that is not in call en raise range...

    }



    private boolean comboIsSuitedConnector(List<Card> combo, int gapWith) {
        boolean comboIsSuitedConnector = false;

        if(combo.get(0).getSuit() == combo.get(1).getSuit()) {
            int rankCard1ToUse = combo.get(0).getRank();
            int rankCard2ToUse = combo.get(1).getRank();

            if(rankCard1ToUse > rankCard2ToUse) {
                if(rankCard1ToUse - rankCard2ToUse == gapWith) {
                    comboIsSuitedConnector = true;
                }
            } else {
                if(rankCard2ToUse - rankCard1ToUse == gapWith) {
                    comboIsSuitedConnector = true;
                }
            }


            if(!comboIsSuitedConnector &&
                    (combo.get(0).getRank() == 14 || combo.get(1).getRank() == 14)) {
                if(combo.get(0).getRank() == 14) {
                    rankCard1ToUse = 1;
                } else {
                    rankCard2ToUse = 1;
                }

                if(rankCard1ToUse > rankCard2ToUse) {
                    if(rankCard1ToUse - rankCard2ToUse == gapWith) {
                        comboIsSuitedConnector = true;
                    }
                } else {
                    if(rankCard2ToUse - rankCard1ToUse == gapWith) {
                        comboIsSuitedConnector = true;
                    }
                }
            }
        }

        return comboIsSuitedConnector;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
