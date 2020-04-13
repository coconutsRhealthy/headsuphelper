package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.card.Card;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 11/04/2020.
 */
public class RangeConstructor {



    public static void main(String[] args) {
        List<Card> board = Arrays.asList(new Card(2, 'd'), new Card(7, 's'), new Card(13, 'c'));

        BoardEvaluator boardEvaluator = new BoardEvaluator(board);

        new RangeConstructor().constructTypicalOppBettingRange(null, boardEvaluator);
    }


    private void constructTypicalOppBettingRange(List<Card> board, BoardEvaluator boardEvaluator) {

        //draws
            //strong fd
            //strong oosd
            //strong gutshot
            //medium fd
            //medium oosd

        //handstrength 60% up

            //count how many combos this is
                //and add 10% air combos (hs < 0.5)
        //HandEvaluator handEvaluator = new HandEvaluator()


        List<List<Card>> range = new ArrayList<>();

        List<List<Card>> value = getValueRange(boardEvaluator);
        List<List<Card>> draws = getDrawRange(boardEvaluator);
        List<List<Card>> air = getAirRange(boardEvaluator);

        System.out.println("wacht");






//
//        int counter = 0;
//
//        Map<Integer, Set<Set<Card>>> sortedCombos = boardEvaluator.getSortedCombosNew();
//
//        for(Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
//           for(Set<Card> combo : entry.getValue()) {
//               counter++;
//
//               if(counter <= 530) {
//                   List comboAsList = new ArrayList<>();
//                   comboAsList.addAll(combo);
//                   range.add(comboAsList);
//               }
//           }
//        }
//
//        //draws
//        StraightDrawEvaluator straightDrawEvaluator = boardEvaluator.getStraightDrawEvaluator();
//        FlushDrawEvaluator flushDrawEvaluator = boardEvaluator.getFlushDrawEvaluator();
//
//        range.addAll(convertDrawEvaluatorMapToList(flushDrawEvaluator.getStrongFlushDrawCombos()));
//        range.addAll(convertDrawEvaluatorMapToList(flushDrawEvaluator.getMediumFlushDrawCombos()));
//        range.addAll(convertDrawEvaluatorMapToList(straightDrawEvaluator.getStrongOosdCombos()));
//        range.addAll(convertDrawEvaluatorMapToList(straightDrawEvaluator.getMediumOosdCombos()));
//        range.addAll(convertDrawEvaluatorMapToList(straightDrawEvaluator.getStrongGutshotCombos()));
//        range.addAll(convertDrawEvaluatorMapToList(straightDrawEvaluator.getMediumGutshotCombos()));
//
//        //air
//        int[] counter2 = {0};
//
//        List<List<Card>> air = sortedCombos.values().stream().flatMap(Collection::stream).map(set -> {
//            counter2[0]++;
//
//            if(counter2[0] > 530) {
//                if(Math.random() < 0.17) {
//                    List<Card> airComboList = new ArrayList<>();
//                    airComboList.addAll(set);
//                    return airComboList;
//                } else {
//                    return null;
//                }
//            } else {
//                return null;
//            }
//        }).collect(Collectors.toList());
//
//        air.removeIf(Objects::isNull);
//
//        range.addAll(air);
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
}
