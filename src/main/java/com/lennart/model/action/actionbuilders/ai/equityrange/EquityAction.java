package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.card.Card;
import equitycalc.EquityCalculator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by LennartMac on 08/05/2020.
 */
public class EquityAction {

    private RangeConstructor rangeConstructor;
    private PreflopEquityHs preflopEquityHs;
    private InputProvider inputProvider;

    private double botEquity;

    public EquityAction(InputProvider inputProvider) {
        this.rangeConstructor = new RangeConstructor();
        this.preflopEquityHs = new PreflopEquityHs();
        this.inputProvider = inputProvider;
    }

    public String getValueAction(ContinuousTable continuousTable, GameVariables gameVariables,
                                 List<String> eligibleActions, double sizing) {
        String action;

        if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
            if(gameVariables.getOpponentAction().equals("call")) {
                action = getPreflopCheckOrRaiseAction(gameVariables);
            } else {
                action = getPreflopFoldCallOrRaiseAction(gameVariables, eligibleActions);
            }
        } else {
            String oppAction = gameVariables.getOpponentAction();

            if(oppAction.equals("bet75pct") || oppAction.equals("raise")) {
                action = getPostflopFoldCallOrRaiseAction(
                        continuousTable,
                        continuousTable.getOppRange(),
                        gameVariables.getOpponentName(),
                        gameVariables.getOpponentBetSize(),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards(),
                        eligibleActions,
                        sizing);
            } else {
                action = getPostflopCheckOrBetAction(
                        continuousTable,
                        continuousTable.getOppRange(),
                        gameVariables.getOpponentName(),
                        sizing,
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
            }
        }

        return action;
    }

    private String getPreflopFoldCallOrRaiseAction(GameVariables gameVariables, List<String> eligibleActions) {
        String actionToReturn;

        botEquity = new EquityCalculator().getComboEquity(gameVariables.getBotHoleCards(), null);

        String oppPfRaiseType = inputProvider.determineOppPreflopRaiseType(-1);

        List<List<Card>> oppPfRaiseRange = null;

        if(oppPfRaiseType.equals("2bet")) {
            String oppPre2betGroup = inputProvider.getOppPre2betGroup(gameVariables.getOpponentName());

            if(gameVariables.isBotIsButton()) {
                oppPfRaiseRange = rangeConstructor.getOppPreRaiseAgainstLimpRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                        oppPre2betGroup, gameVariables.getBotHoleCards());
            } else {
                oppPfRaiseRange = rangeConstructor.getOppPre2betRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                        oppPre2betGroup, gameVariables.getBotHoleCards());
            }
        } else if(oppPfRaiseType.equals("3bet")) {
            oppPfRaiseRange = rangeConstructor.getOppPre3betRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                    inputProvider.getOppPre3betGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
        } else if(oppPfRaiseType.equals("4bet_up")) {
            oppPfRaiseRange = rangeConstructor.getOppPre4betUpRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                    inputProvider.getOppPre4betUpGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
        } else {
            System.out.println("Shouldn't come here, EquityAction - 1");
        }

        double oppAveragePfRaiseRangeEquity = new EquityCalculator().getAverageRangeEquity(oppPfRaiseRange, null);

        if(botEquity > oppAveragePfRaiseRangeEquity) {
            if(eligibleActions.contains("raise")) {
                String botPfRaiseType = inputProvider.determinBotPreflopRaiseType(-1);

                List<List<Card>> oppPfCallingRange = null;

                if(botPfRaiseType.equals("2bet")) {
                    oppPfCallingRange = rangeConstructor.getOppPreCall2betRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                            inputProvider.getOppPreCall2betGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
                } else if(botPfRaiseType.equals("3bet")) {
                    oppPfCallingRange = rangeConstructor.getOppPreCall3betRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                            inputProvider.getOppPreCall3betGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
                } else if(botPfRaiseType.equals("4bet_up")) {
                    oppPfCallingRange = rangeConstructor.getOppPreCall4betUpRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                            inputProvider.getOppPreCall4betUpGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
                } else {
                    System.out.println("Shouldn't come here, EquityAction - 2");
                }

                double oppAveragePfRaiseCallingEquity = new EquityCalculator().getAverageRangeEquity(oppPfCallingRange, null);

                if(botEquity > oppAveragePfRaiseCallingEquity) {
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

    private String getPreflopCheckOrRaiseAction(GameVariables gameVariables) {
        String actionToReturn;

        botEquity = new EquityCalculator().getComboEquity(gameVariables.getBotHoleCards(), null);

        String botPfRaiseType = inputProvider.determinBotPreflopRaiseType(-1);

        List<List<Card>> oppPfCallingRange = null;

        if(botPfRaiseType.equals("2bet")) {
            oppPfCallingRange = rangeConstructor.getOppPreCall2betRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                    inputProvider.getOppPreCall2betGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
        } else if(botPfRaiseType.equals("3bet")) {
            oppPfCallingRange = rangeConstructor.getOppPreCall3betRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                    inputProvider.getOppPreCall3betGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
        } else if(botPfRaiseType.equals("4bet_up")) {
            oppPfCallingRange = rangeConstructor.getOppPreCall4betUpRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                    inputProvider.getOppPreCall4betUpGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
        } else {
            System.out.println("Shouldn't come here, EquityAction - 3");
        }

        double oppAveragePfCallRangeEquity = new EquityCalculator().getAverageRangeEquity(oppPfCallingRange, null);

        if(botEquity > oppAveragePfCallRangeEquity) {
            actionToReturn = "raise";
        } else {
            actionToReturn = "check";
        }

        return actionToReturn;
    }

    private String getPostflopCheckOrBetAction(ContinuousTable continuousTable, List<List<Card>> currentOppRange,
                                               String oppName, double sizing, List<Card> board, List<Card> botHoleCards) {
        String actionToReturn;

        String oppLooseness = inputProvider.getOppPostLooseness(oppName);
        String botSizingGroup = inputProvider.getBotSizingGroup(sizing);

        List<List<Card>> allCombosPostflopEquitySorted = getAllCombosPostflopEquitySorted(continuousTable, board, botHoleCards);

        List<List<Card>> oppCallingRange = rangeConstructor.getOppPostflopCallRange(currentOppRange,
                allCombosPostflopEquitySorted, oppLooseness, botSizingGroup, board, botHoleCards);

        botEquity = new EquityCalculator().getComboEquity(botHoleCards, board);
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

        String oppAggroness = inputProvider.getOppPostAggroness(oppName);
        String oppSizingGroup = inputProvider.getOppSizingGroup(oppTotalBetsize);

        List<List<Card>> allCombosPostflopEquitySorted = getAllCombosPostflopEquitySorted(continuousTable, board, botHoleCards);

        List<List<Card>> oppBetRange = rangeConstructor.getOppPostflopBetRange(currentOppRange,
                allCombosPostflopEquitySorted, oppAggroness, oppSizingGroup, board, botHoleCards);

        botEquity = new EquityCalculator().getComboEquity(botHoleCards, board);
        double oppAverageBettingEquity = new EquityCalculator().getAverageRangeEquity(oppBetRange, board);

        if(botEquity > oppAverageBettingEquity) {
            if(eligibleActions.contains("raise")) {
                String oppLooseness = inputProvider.getOppPostLooseness(oppName);
                String botSizingGroup = inputProvider.getBotSizingGroup(botHypotheticalRaiseSizing);

                List<List<Card>> oppCallingRaiseRange = rangeConstructor.getOppPostflopCallRange(oppBetRange,
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

    public double getBotEquity() {
        return botEquity;
    }
}
