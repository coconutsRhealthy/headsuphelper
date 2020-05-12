package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.card.Card;
import equitycalc.EquityCalculator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by LennartMac on 08/05/2020.
 */
public class EquityAction {

    private String getPostflopCheckOrBetAction(ContinuousTable continuousTable, List<List<Card>> currentOppRange,
                                               String oppName, double sizing, List<Card> board, List<Card> botHoleCards) {
        String actionToReturn;

        String oppLooseness = getOppLooseness(oppName);
        String botSizingGroup = getBotSizingGroup(sizing);

        List<List<Card>> allCombosPostflopEquitySorted = getAllCombosPostflopEquitySorted(continuousTable, board, botHoleCards);

        List<List<Card>> oppCallingRange = new RangeConstructor().getOppPostflopCallRange(currentOppRange,
                allCombosPostflopEquitySorted, oppLooseness, botSizingGroup, board, botHoleCards);

        double botEquity = new EquityCalculator().getComboEquity(botHoleCards, board);
        double oppAverageCallingEquity = new EquityCalculator().getAverageRangeEquity(oppCallingRange, board);

        if(botEquity > oppAverageCallingEquity) {
            actionToReturn = "bet75pct";
        } else {
            actionToReturn = "check";
        }

        return actionToReturn;
    }

    private String getPostflopFoldCallOrRaiseAction(ContinuousTable continuousTable, List<List<Card>> currentOppRange,
                                                    String oppName, double oppTotalBetsize, List<Card> board,
                                                    List<Card> botHoleCards, List<String> eligibleActions,
                                                    double botHypotheticalRaiseSizing) {
        String actionToReturn;

        String oppAggroness = getOppAggroness(oppName);
        String oppSizingGroup = getOppSizingGroup(oppTotalBetsize);

        List<List<Card>> allCombosPostflopEquitySorted = getAllCombosPostflopEquitySorted(continuousTable, board, botHoleCards);

        List<List<Card>> oppBetRange = new RangeConstructor().getOppPostflopBetRange(currentOppRange,
                allCombosPostflopEquitySorted, oppAggroness, oppSizingGroup, board, botHoleCards);

        double botEquity = new EquityCalculator().getComboEquity(botHoleCards, board);
        double oppAverageBettingEquity = new EquityCalculator().getAverageRangeEquity(oppBetRange, board);

        if(botEquity > oppAverageBettingEquity) {
            if(eligibleActions.contains("raise")) {
                String oppLooseness = getOppLooseness(oppName);
                String botSizingGroup = getBotSizingGroup(botHypotheticalRaiseSizing);

                List<List<Card>> oppCallingRaiseRange = new RangeConstructor().getOppPostflopCallRange(oppBetRange,
                        allCombosPostflopEquitySorted, oppLooseness, botSizingGroup, board, botHoleCards);

                double oppAverageRaiseCallingEquity = new EquityCalculator().getAverageRangeEquity(oppCallingRaiseRange, board);

                if(botEquity > oppAverageRaiseCallingEquity) {
                    actionToReturn = "raise";
                } else {
                    actionToReturn = "call";
                }
            } else {
                actionToReturn = "call";
            }
        } else {
            actionToReturn = "fold";
        }

        return actionToReturn;
    }

    public List<List<Card>> getAllCombosPostflopEquitySorted(ContinuousTable continuousTable, List<Card> board,
                                                             List<Card> botHoleCards) {
        if(continuousTable.getAllCombosPostflopEquitySorted() != null &&
                continuousTable.getAllCombosPostflopEquitySorted().get(board) != null) {
            return continuousTable.getAllCombosPostflopEquitySorted().get(board);
        }

        if(continuousTable.getAllCombosPostflopEquitySorted() == null) {
            continuousTable.setAllCombosPostflopEquitySorted(new HashMap<>());
        }

        List<List<Card>> allCombos = ActionBuilderUtil.getAllPossibleStartHandsAsList().values().stream()
                .collect(Collectors.toList());

        List<List<Card>> allCombosKnownGameCardsRemoved = RangeConstructor.removeCombosWithKnownCards(
                allCombos, Stream.concat(board.stream(), botHoleCards.stream())
                        .collect(Collectors.toList()));

        List<List<Card>> allCombosPostflopEquitySorted = new EquityCalculator().getRangeEquities(
                allCombosKnownGameCardsRemoved, board).keySet().stream()
                .collect(Collectors.toList());

        Map<List<Card>, List<List<Card>>> currentAllCombosPostflopEquitySorted =
                continuousTable.getAllCombosPostflopEquitySorted();

        currentAllCombosPostflopEquitySorted.put(board, allCombosPostflopEquitySorted);
        continuousTable.setAllCombosPostflopEquitySorted(currentAllCombosPostflopEquitySorted);

        return allCombosPostflopEquitySorted;
    }

    public Map<List<Card>, Double> getHsNewStyleInput(List<Card> board) {
        List<List<Card>> allCombos = ActionBuilderUtil.getAllPossibleStartHandsAsList().values().stream().collect(Collectors.toList());
        List<Card> knownCards = board.stream().collect(Collectors.toList());
        allCombos = RangeConstructor.removeCombosWithKnownCards(allCombos, knownCards);
        return sortByValueHighToLow(new EquityCalculator().getRangeEquities(allCombos, board));
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

    public String getOppPreCall2betGroup(String oppName) {
        return null;
    }

    public String getOppPreCall3betGroup(String oppName) {
        return null;
    }

    public String getOppPreCall4betUpGroup(String oppName) {
        return null;
    }

    public String getOppPre2betGroup(String oppName) {
        return null;
    }

    public String getOppPre3betGroup(String oppName) {
        return null;
    }

    public String getOppPre4betUpGroup(String oppName) {
        return null;
    }

    public String getOppAggroness(String oppName) {
        return null;
    }

    public String getOppLooseness(String oppName) {
        return null;
    }

    public String getBotSizingGroup(double sizing) {
        return null;
    }

    public String getOppSizingGroup(double oppTotalBetsize) {
        return null;
    }

    public String getPotSizeGroup(double potSize) {
        return null;
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
