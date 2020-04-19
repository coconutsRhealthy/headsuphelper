package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;
import equitycalc.Example;
import equitycalc.ExampleOld;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 11/04/2020.
 */
public class RangeConstructor {

    public static void main(String[] args) {
        //for(int i = 0; i < 10; i++) {
            new RangeConstructor().testMethod();
        //}
    }

    private void testMethod() {
        List<Card> botHoleCards = Arrays.asList(new Card(3, 'c'), new Card(10, 'd'));
        List<Card> board = Arrays.asList(new Card(2, 'd'), new Card(2, 'h'), new Card(2, 's'));

        BoardEvaluator boardEvaluator = new BoardEvaluator(board);

        List<List<Card>> oppRange = getOpponentRange(null, boardEvaluator, botHoleCards);

        //List<List<Card>> oppRange = new ArrayList<>();

        //oppRange.add(Arrays.asList(new Card(7, 'c'), new Card(3, 'd')));
        //oppRange.add(Arrays.asList(new Card(10, 'h'), new Card(10, 's')));
        //oppRange.add(Arrays.asList(new Card(9, 'c'), new Card(5, 'd')));


        double averageEquity = getAverageEquityOfOppRangeViaLoop(oppRange, board);

        System.out.println(averageEquity);
    }


    private void testMethod2() {
        List<Card> combo = Arrays.asList(new Card(2, 'c'), new Card(10, 'd'));
        List<Card> board = Arrays.asList(new Card(2, 'd'), new Card(3, 'd'), new Card(5, 'c'));
        System.out.println(getEquity(combo, board));
    }



    private double getEquity(List<Card> combo, List<Card> board) {
        double equity;

        if(board.size() == 6 || board.size() == 8) {
            List<Double> hsAtRiver = new ArrayList<>();

            for(int i = 0; i < 10; i++) {
                System.out.println("A");
                List<Card> equityBoard = new ArrayList<>();
                equityBoard.addAll(board);
                equityBoard.add(drawRandomRemainingCardFromDeck(combo, board));

                if(board.size() == 4) {
                    equityBoard.add(drawRandomRemainingCardFromDeck(combo, board));
                }

                BoardEvaluator boardEvaluator = new BoardEvaluator(equityBoard);
                hsAtRiver.add(new HandEvaluator(boardEvaluator).getHandStrength(combo));
            }

            equity = hsAtRiver.stream()
                    .mapToDouble(hs -> hs)
                    .average()
                    .getAsDouble();
        } else {
            equity = new HandEvaluator(new BoardEvaluator(board)).getHandStrength(combo);
        }

        return equity;
    }

    private Card drawRandomRemainingCardFromDeck(List<Card> holeCards, List<Card> board) {
        List<Card> deck = BoardEvaluator.getCompleteCardDeck();

        List<Card> deckFiltered = deck.stream()
                .filter(card -> !holeCards.contains(card) && !board.contains(card))
                .collect(Collectors.toList());

        Random randomGenerator = new Random();
        int random = randomGenerator.nextInt(deckFiltered.size());
        Card cardToReturn = deckFiltered.get(random);

        return cardToReturn;
    }




    private double getAverageHsOfOppRange(List<List<Card>> oppRange, BoardEvaluator boardEvaluator) {
        HandEvaluator handEvaluator = new HandEvaluator(boardEvaluator);

        List<Double> allHandstrengths = oppRange.stream()
                .map(handEvaluator::getHandStrength)
                .collect(Collectors.toList());

        double average = allHandstrengths.stream()
                .mapToDouble(hs -> hs)
                .average()
                .getAsDouble();

        return average;
    }

    private double getAverageEquityOfOppRange(List<List<Card>> oppRange, List<Card> board) {
        ExampleOld exampleOld = new ExampleOld();

        double averageEquity = oppRange.stream()
                .map(oppRangeCombo -> exampleOld.calculateEquity(board, oppRangeCombo))
                .mapToDouble(average -> average)
                .average()
                .getAsDouble();

        return averageEquity;
    }



    private double getAverageEquityOfOppRangeViaLoop(List<List<Card>> oppRange, List<Card> board) {
        Example example = new Example();

        List<Double> equities = example.getAllEquities(oppRange, board);

        double average;
        try {
            average = equities.stream()
                    .mapToDouble(a -> a)
                    .average()
                    .getAsDouble();
        } catch (Exception e) {
            average = -1;
        }

        return average;
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








    private void getOppBetRangeYoOld() {

        //input:

        //opp betsize

        //facingodds

        //opp aggroness

        //river include ook misseddraws

        //basis:
            //bovenste 40% combos voor value
                //indien betsize > 100 && betsize < 200
                    //bovenste 30% combos voor value

                //indien betsize > 200 && betsize < 300
                    //bovenste 25% combos voor value

                //indien betsize > 300
                    //bovenste 20% combos voor value

                //-----

                //



            //alle strong draws -> oosd, fd, gutshot

            //alle medium draws -> oosd, fd, gutshot

            //18% air




    }

    private void getOppRaiseRangeYo() {

    }

    private void getOppBetRangeYo() {
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

            //betsize medium

            //betsize large


        //opp aggro medium
            //betsize small
                //value
                    //bovenste 60% combos
                    //18% van combos tussen 50 en 60

                //draw
                    //alle strong draws
                    //alle medium draws

                //missed strong draw river
                    //50% missed draws

                //air
                    //20% van onderste 50% combos

            //betsize medium
                //value
                    //bovenste 70% combos
                    //18% van combos tussen 60 en 70

                //draw
                    //alle strong draws
                    //50% medium draws

                //missed strong draw river
                    //45% missed draws

                //air
                    //17% van onderste 50% combos

            //betsize large
                //value
                    //bovenste 80% combos
                    //18% van combos tussen 70 en 80

                //draw
                    //alle strong draws
                    //20% medium draws

                //missed strong draw river
                    //40% missed draws

                //air
                    //15% van onderste 50% combos


        //opp aggro high
            //betsize small

            //betsize medium

            //betsize large

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



}
