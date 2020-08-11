package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.action.actionbuilders.ai.MachineLearning;
import com.lennart.model.action.actionbuilders.ai.Sizing;
import com.lennart.model.card.Card;
import equitycalc.EquityCalculator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by LennartMac on 25/05/2020.
 */
public class BluffAction {

    private EquityAction equityAction;
    private InputProvider inputProvider;
    private RangeConstructor rangeConstructor;
    private PreflopEquityHs preflopEquityHs;


    public BluffAction(EquityAction equityAction, InputProvider inputProvider, RangeConstructor rangeConstructor,
                       PreflopEquityHs preflopEquityHs) {
        this.equityAction = equityAction;
        this.inputProvider = inputProvider;
        this.rangeConstructor = rangeConstructor;
        this.preflopEquityHs = preflopEquityHs;
    }

    public String getBluffAction(String currentAction, List<String> eligibleActions, ContinuousTable continuousTable,
                                  GameVariables gameVariables, double botSizing, double botEquity) {
        String actionToReturn;

        if(currentAction.equals("check") || currentAction.equals("fold")) {
            String bluffActionToUse;

            if(currentAction.equals("check")) {
                if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
                    bluffActionToUse = "raise";
                } else {
                    bluffActionToUse = "bet75pct";
                }
            } else {
                bluffActionToUse = "raise";
            }

            if(bluffActionToUse.equals("bet75pct") || eligibleActions.contains("raise")) {
                boolean bluffOddsAreOk = new MachineLearning().bluffOddsAreOk(botSizing, gameVariables.getOpponentBetSize(),
                        gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBotStack(),
                        gameVariables.getBoard(), gameVariables.getBotBetSize());

                if(bluffOddsAreOk) {
                    List<List<Card>> oppCallRangeWhenYouBluff = getOppCallRangeWhenYouBluff(continuousTable,
                            gameVariables, botSizing);
                    List<List<Card>> oppRaiseRangeWhenYouBluff = getOppRaiseRangeWhenYouBluff(continuousTable,
                            gameVariables, botSizing);
                    List<List<Card>> oppCallRaiseRangeCombined = Stream.concat(oppCallRangeWhenYouBluff.stream(), oppRaiseRangeWhenYouBluff.stream())
                            .collect(Collectors.toList());
                    oppCallRaiseRangeCombined = filterOutDoubleCombos(oppCallRaiseRangeCombined);

                    double oppFoldRangeToTotalRangeRatio =
                            (continuousTable.getOppRange().size() - oppCallRaiseRangeCombined.size() + 0.0)
                                    / (continuousTable.getOppRange().size() + 0.0);

                    if(gameVariables.getBoard() != null && bluffActionToUse.equals("bet75pct")) {
                        int boardSize = gameVariables.getBoard().size();

                        if(boardSize == 3) {
                            System.out.println("flop bluffbet opportunity");
                        } else if(boardSize == 4) {
                            System.out.println("turn bluffbet opportunity");
                        } else if(boardSize == 5) {
                            System.out.println("river bluffbet opportunity");
                        }
                    }

                    System.out.println("CURR: " + continuousTable.getOppRange().size());
                    System.out.println("COMBINED: " + oppCallRaiseRangeCombined.size());
                    System.out.println("RATIO: " + oppFoldRangeToTotalRangeRatio);

                    double limit;

                    if(bluffActionToUse.equals("bet75pct")) {
                        if(gameVariables.getBoard().size() == 5) {
                            limit = 0.25;
                        } else {
                            limit = 0.4;
                        }
                    } else {
                        limit = 0.6;
                    }

                    if(oppFoldRangeToTotalRangeRatio > limit) {
                        if(currentAction.equals("check")) {
                            if(botEquity > 0.565) {
                                System.out.println("No bluff cause showdown value");
                                return currentAction;
                            }
                        }

                        if(gameVariables.getBoard().size() == 5) {
                            actionToReturn = bluffActionToUse;
                            System.out.println("river bluffje: " + actionToReturn);
                        } else {
                            if(Math.random() < 0.99) {
                                actionToReturn = bluffActionToUse;
                                System.out.println("flop/turn bluffje: " + actionToReturn);
                            } else {
                                actionToReturn = currentAction;
                            }
                        }
                    } else {
                        actionToReturn = currentAction;
                    }
                } else {
                    actionToReturn = currentAction;
                }
            } else {
                actionToReturn = currentAction;
            }
        } else {
            actionToReturn = currentAction;
        }

        return actionToReturn;
    }

    private List<List<Card>> getOppCallRangeWhenYouBluff(ContinuousTable continuousTable, GameVariables gameVariables, double botSizing) {
        List<List<Card>> oppCallRangeWhenYouBluff;

        if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
            String botPfRaiseType = inputProvider.determinBotPreflopRaiseType(botSizing, gameVariables.getBigBlind());

            if(botPfRaiseType.equals("2bet")) {
                oppCallRangeWhenYouBluff = rangeConstructor.getOppPreCall2betRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPreCall2betGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
            } else if(botPfRaiseType.equals("3bet")) {
                oppCallRangeWhenYouBluff = rangeConstructor.getOppPreCall3betRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPreCall3betGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
            } else if(botPfRaiseType.equals("4bet_up")) {
                oppCallRangeWhenYouBluff = rangeConstructor.getOppPreCall4betUpRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPreCall4betUpGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
            } else {
                System.out.println("Shouldn't come here, BluffAction - 1");
                oppCallRangeWhenYouBluff = null;
            }
        } else {
            oppCallRangeWhenYouBluff = rangeConstructor.getOppPostflopCallRange(
                    continuousTable.getOppRange(),
                    equityAction.getAllCombosPostflopEquitySorted(continuousTable, gameVariables.getBoard(), gameVariables.getBotHoleCards()),
                    inputProvider.getOppPostLooseness(gameVariables.getOpponentName()),
                    inputProvider.getBotSizingGroup(botSizing, gameVariables.getOpponentStack(), gameVariables.getOpponentBetSize()),
                    gameVariables.getBoard(),
                    gameVariables.getBotHoleCards());
        }

        return oppCallRangeWhenYouBluff;
    }

    private List<List<Card>> getOppRaiseRangeWhenYouBluff(ContinuousTable continuousTable, GameVariables gameVariables, double botSizing) {
        List<List<Card>> oppRaiseRangeWhenYouBluff;

        double fictionalOppRaiseSizing = new Sizing().getAiBotSizing(botSizing, gameVariables.getOpponentBetSize(),
                gameVariables.getOpponentStack(), gameVariables.getBotStack(), gameVariables.getPot(), gameVariables.getBigBlind(),
                gameVariables.getBoard(), 0.5, false, false);

        if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
            String oppPfRaiseType = inputProvider.determineOppPreflopRaiseType(fictionalOppRaiseSizing, gameVariables.getBigBlind());

            if(oppPfRaiseType.equals("2bet")) {
                String oppPre2betGroup = inputProvider.getOppPre2betGroup(gameVariables.getOpponentName());

                if(gameVariables.isBotIsButton()) {
                    oppRaiseRangeWhenYouBluff = rangeConstructor.getOppPreRaiseAgainstLimpRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                            oppPre2betGroup, gameVariables.getBotHoleCards());
                } else {
                    oppRaiseRangeWhenYouBluff = rangeConstructor.getOppPre2betRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                            oppPre2betGroup, gameVariables.getBotHoleCards());
                }
            } else if(oppPfRaiseType.equals("3bet")) {
                oppRaiseRangeWhenYouBluff = rangeConstructor.getOppPre3betRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPre3betGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
            } else if(oppPfRaiseType.equals("4bet_up")) {
                oppRaiseRangeWhenYouBluff = rangeConstructor.getOppPre4betUpRange(preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPre4betUpGroup(gameVariables.getOpponentName()), gameVariables.getBotHoleCards());
            } else {
                System.out.println("Shouldn't come here, BluffAction - 1");
                oppRaiseRangeWhenYouBluff = null;
            }
        } else {
            oppRaiseRangeWhenYouBluff = rangeConstructor.getOppPostflopRaiseRange(
                    continuousTable.getOppRange(),
                    equityAction.getAllCombosPostflopEquitySorted(continuousTable, gameVariables.getBoard(),
                            gameVariables.getBotHoleCards()),
                    inputProvider.getOppPostAggroness(gameVariables.getOpponentName()),
                    inputProvider.getOppSizingGroup(fictionalOppRaiseSizing, gameVariables.getBotStack(), botSizing),
                    gameVariables.getBoard(),
                    gameVariables.getBotHoleCards());
        }

        return oppRaiseRangeWhenYouBluff;
    }

    private List<List<Card>> filterOutDoubleCombos(List<List<Card>> input) {
        Set<Set<Card>> asSet = input.stream().map(combo -> combo.stream().collect(Collectors.toSet())).collect(Collectors.toSet());
        return asSet.stream().map(comboAsSet -> comboAsSet.stream().collect(Collectors.toList())).collect(Collectors.toList());
    }
}
