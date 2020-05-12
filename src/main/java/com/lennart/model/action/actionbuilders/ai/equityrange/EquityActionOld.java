package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.card.Card;
import equitycalc.EquityCalculator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 20/04/2020.
 */
public class EquityActionOld {

    //ook nog preflop

    private String checkOrBet(double botEquity, double avEquityOppCallingRange) {

        //je wil betten als jouw hs sterker is dan de gemiddelde hs van zijn calling range

        //misschien wil je hier ook nog iets met raise range opp doen (omvang vergeleken met callingrange?)

        String actionToReturn;

        if(botEquity > avEquityOppCallingRange) {
            actionToReturn = "bet75pct";
        } else {
            actionToReturn = "check";
        }

        return actionToReturn;
    }

    private String foldCallOrRaiseFacingBet(double botEquity, double avEquityOppBettingRange, double avEquityOppCallingVsRaiseRange) {
        String actionToReturn;

        if(botEquity > avEquityOppBettingRange) {
            if(botEquity > avEquityOppCallingVsRaiseRange) {
                //misschien wil je hier ook nog iets met raise range opp doen (omvang vergeleken met callingrange?)
                actionToReturn = "raise";
            } else {
                actionToReturn = "call";
            }
        } else {
            actionToReturn = "fold";
        }

        return actionToReturn;
    }

    private String foldCallOrRaiseFacingRaise(double botEquity, double avEquityOppRaiseRange, double avEquityOppCallingVsRaiseRange) {
        String actionToReturn;

        if(botEquity > avEquityOppRaiseRange) {
            if(botEquity > avEquityOppCallingVsRaiseRange) {
                //misschien wil je hier ook nog iets met raise range opp doen (omvang vergeleken met callingrange?)
                actionToReturn = "raise";
            } else {
                actionToReturn = "call";
            }
        } else {
            actionToReturn = "fold";
        }

        return actionToReturn;
    }

    public double getAverageEquityOfOppRangeWithoutEquityMap(List<List<Card>> oppRange, List<Card> board) {
        EquityCalculator equityCalculator = new EquityCalculator();

        Map<List<Card>, Double> equities = equityCalculator.getRangeEquities(oppRange, board);

        double average;
        try {
            average = equities.values().stream()
                    .mapToDouble(a -> a)
                    .average()
                    .getAsDouble();
        } catch (Exception e) {
            e.printStackTrace();
            return getAverageEquityOfOppRangeWithoutEquityMap(oppRange, board);
        }

        return average;
    }

    public double getAverageEquityOfRangeWithEquityMap(Map<List<Card>, Double> sortedEquities, List<List<Card>> range) {
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

    public Map<List<Card>, Double> getAllStarthandsEquitySorted(List<Card> board) {
        List<List<Card>> allCombos = ActionBuilderUtil.getAllPossibleStartHandsAsList().values().stream().collect(Collectors.toList());
        List<Card> knownCards = board.stream().collect(Collectors.toList());
        allCombos = RangeConstructor.removeCombosWithKnownCards(allCombos, knownCards);
        return sortByValueHighToLow(new EquityCalculator().getRangeEquities(allCombos, board));
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
