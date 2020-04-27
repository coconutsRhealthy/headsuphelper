package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.card.Card;
import equitycalc.EquityCalculator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 11/04/2020.
 */
public class RangeConstructor {



    private void testMethodSingleCombo() {
        List<Card> botHoleCards = Arrays.asList(new Card(14, 'c'), new Card(14, 'd'));
        List<Card> board = Arrays.asList(new Card(2, 'd'), new Card(2, 'h'), new Card(2, 's'));

        EquityCalculator equityCalculator = new EquityCalculator();

        System.out.println(equityCalculator.getComboEquityFlop(botHoleCards, board));
    }

    private void testMethodRange() {
        List<Card> botHoleCards = Arrays.asList(new Card(3, 'c'), new Card(10, 'd'));
        List<Card> board = Arrays.asList(new Card(7, 'h'), new Card(8, 'h'), new Card(9, 'h'));

        BoardEvaluator boardEvaluator = new BoardEvaluator(board);

        //List<List<Card>> oppRange = getOpponentRange(null, boardEvaluator, botHoleCards);
        List<List<Card>> oppRange = ActionBuilderUtil.getAllPossibleStartHandsAsList().values().stream().collect(Collectors.toList());

        oppRange = removeCombosWithKnownCards(oppRange, board);

        double averageEquity = getAverageEquityOfOppRange(oppRange, board);
        System.out.println(averageEquity);
    }

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

    public static void main(String[] args) {
//        List<List<Card>> allCombos = new PreflopEuityHs().getAllPreflopCombosEquitySorted();
//        List<List<Card>> range = new RangeConstructor().getOppPre3betRange(allCombos, "high");
//        System.out.println("wacht");
//
//        //System.out.println(new RangeConstructor().comboIsSuitedConnector(Arrays.asList(new Card(2, 'd'), new Card(14, 'd')), 1));

        //new EquityCalculator().getComboEquityPreflop(Arrays.asList(new Card(8, 'd'), new Card(6, 'c')));

        new RangeConstructor().ffEenTestAllEquitiesFlop();

        //new EquityCalculator().getRangeEquityFlop()

        //new RangeConstructor().testMethodRange();
    }

    private void ffEenTestAllEquitiesFlop() {
        List<Card> board = Arrays.asList(new Card(8, 'c'), new Card(6, 's'), new Card(14, 'c'));
        Map<List<Card>, Double> eije = getAllCombosEquitySortedFlop(board);
        eije = sortByValueHighToLow(eije);
        System.out.println("tjo");
    }


    private Map<List<Card>, Double> getAllCombosEquitySortedFlop(List<Card> board) {
        List<List<Card>> allCombos = ActionBuilderUtil.getAllPossibleStartHandsAsList().values().stream().collect(Collectors.toList());
        allCombos = removeCombosWithKnownCards(allCombos, board);
        return new EquityCalculator().getRangeEquityFlop(allCombos, board);
    }

    private List<List<Card>> getOpponentRange(List<List<Card>> startingOppRange, BoardEvaluator boardEvaluator, List<Card> botHoleCards) {
        if(startingOppRange == null) {
            startingOppRange = createStartingOppRange(botHoleCards);
        }

        List<List<Card>> opponentRange = new ArrayList<>();

        List<List<Card>> valueRangeTotal = getValueRange(boardEvaluator);
        List<List<Card>> valueOppRange = new ArrayList<>();
        valueOppRange.addAll(startingOppRange);
        valueOppRange.retainAll(valueRangeTotal);

        List<List<Card>> drawOppRange = new ArrayList<>();
        if(boardEvaluator.getBoard().size() != 5) {
            List<List<Card>> drawRangeTotal = getDrawRange(boardEvaluator);
            drawOppRange.addAll(startingOppRange);
            drawOppRange.retainAll(drawRangeTotal);
        }

        List<List<Card>> airRangeTotal = getAirRange(boardEvaluator);
        List<List<Card>> airOppRange = new ArrayList<>();
        airOppRange.addAll(startingOppRange);
        airOppRange.retainAll(airRangeTotal);

        opponentRange.addAll(valueOppRange);
        opponentRange.addAll(drawOppRange);
        opponentRange.addAll(airOppRange);

        Set<List<Card>> rangeAsSet = new HashSet<>();
        rangeAsSet.addAll(opponentRange);

        opponentRange.clear();
        opponentRange.addAll(rangeAsSet);

        return opponentRange;
    }

    private List<List<Card>> removeCombosWithKnownCards(List<List<Card>> range, List<Card> knownCards) {
        return range.stream().filter(combo -> Collections.disjoint(combo, knownCards)).collect(Collectors.toList());
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

    private List<List<Card>> getValueRange(BoardEvaluator boardEvaluator) {
        int[] counter = {0};

        List<List<Card>> value = boardEvaluator.getSortedCombosNew()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(set -> {
                    counter[0]++;

                    if(counter[0] <= 530) {
                        List<Card> valueComboList = new ArrayList<>();
                        valueComboList.addAll(set);
                        return valueComboList;
                    } else {
                        return null;
                    }
                })
                .collect(Collectors.toList());

        value.removeIf(Objects::isNull);
        return value;
    }

    private List<List<Card>> getDrawRange(BoardEvaluator boardEvaluator) {
        StraightDrawEvaluator straightDrawEvaluator = boardEvaluator.getStraightDrawEvaluator();
        FlushDrawEvaluator flushDrawEvaluator = boardEvaluator.getFlushDrawEvaluator();

        List<List<Card>> draws = new ArrayList<>();

        draws.addAll(convertDrawEvaluatorMapToList(flushDrawEvaluator.getStrongFlushDrawCombos()));
        draws.addAll(convertDrawEvaluatorMapToList(flushDrawEvaluator.getMediumFlushDrawCombos()));
        draws.addAll(convertDrawEvaluatorMapToList(straightDrawEvaluator.getStrongOosdCombos()));
        draws.addAll(convertDrawEvaluatorMapToList(straightDrawEvaluator.getMediumOosdCombos()));
        draws.addAll(convertDrawEvaluatorMapToList(straightDrawEvaluator.getStrongGutshotCombos()));
        draws.addAll(convertDrawEvaluatorMapToList(straightDrawEvaluator.getMediumGutshotCombos()));

        return draws;
    }

    private List<List<Card>> getAirRange(BoardEvaluator boardEvaluator) {
        int[] counter2 = {0};

        List<List<Card>> air = boardEvaluator.getSortedCombosNew()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(set -> {
                    counter2[0]++;

                    if(counter2[0] > 530) {
                        if(Math.random() < 0.17) {
                            List<Card> airComboList = new ArrayList<>();
                            airComboList.addAll(set);
                            return airComboList;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                })
                .collect(Collectors.toList());

        air.removeIf(Objects::isNull);
        return air;
    }

    private List<List<Card>> convertDrawEvaluatorMapToList(Map<Integer, Set<Card>> drawEvaluatorMap) {
        return drawEvaluatorMap.values().stream()
                .map(combo -> {
                    List<Card> comboList = new ArrayList<>();
                    comboList.addAll(combo);
                    return comboList;
                })
                .collect(Collectors.toList());
    }




    //////

    //more test for ranges



    private void printStarthandsInOrder() {
        Map<Integer, List<Card>> allStarthands = ActionBuilderUtil.getAllPossibleStartHandsAsList();

        //Map<List<Card>, Double> eije = allStarthands.values().stream().map(combo -> new EquityCalculator().getComboEquityPreflop(combo)).collect(Collectors.toMap())

        Map<List<Card>, Double> eije = allStarthands.values().stream().collect(Collectors.toMap(combo -> combo, combo -> new EquityCalculator().getComboEquityPreflop(combo)));


        eije = sortByValueHighToLow(eije);

        //eije.entrySet().stream().sorted().co

        //eije = eije.entrySet().stream().sorted().collect(Collectors.toMap(a -> a, b -> b));

        //List<Card> preflopComboToTest = Arrays.asList(new Card(9, 'd'), new Card(14, 'c'));

        //System.out.println(new EquityCalculator().getComboEquityPreflop(preflopComboToTest));

        System.out.println("wacht");

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

    private void getOppBetRangeYo(List<List<Card>> oppStartingRange, List<List<Card>> allCombosEquitySorted,
                                  List<Card> knownGameCards, String oppAggroness, String oppBetsize) {
        List<List<Card>> oppBetRange = new ArrayList<>();

        if(oppAggroness.equals("low")) {
            if(oppBetsize.equals("small")) {
                //value
                    //wat je hier kunt doen... alle 1326 combos sorteren
                    //en dan... de bovenste 60% handhaven
                    //en dan... van deze combos de combos houden die al in je range zaten...

                oppBetRange = allCombosEquitySorted.subList(0, 530);
                oppBetRange.retainAll(oppStartingRange);
                oppBetRange = removeCombosWithKnownCards(oppBetRange, knownGameCards);
            } else if(oppBetsize.equals("medium")) {



            } else if(oppBetsize.equals("larg")) {

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


}
