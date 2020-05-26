package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.action.actionbuilders.ai.MachineLearning;
import com.lennart.model.action.actionbuilders.ai.Sizing;
import com.lennart.model.card.Card;

import java.util.List;

/**
 * Created by LennartMac on 25/05/2020.
 */
public class BluffAction {

    private EquityAction equityAction;

    public BluffAction(EquityAction equityAction) {
        this.equityAction = equityAction;
    }

    public String getBluffAction(String currentAction, boolean valueTrap,
                                  List<String> eligibleActions, ContinuousTable continuousTable,
                                  GameVariables gameVariables, double botSizing) {
        String actionToReturn;

        if(currentAction.equals("check") || currentAction.equals("fold")) {
            if(!valueTrap) {
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

                        double oppFoldRangeToTotalRangeRatio =
                                (continuousTable.getOppRange().size() -
                                        oppCallRangeWhenYouBluff.size() - oppRaiseRangeWhenYouBluff.size())
                                        / continuousTable.getOppRange().size();

                        if(oppFoldRangeToTotalRangeRatio > 0.5) {
                            if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
                                if(equityAction.getBotEquity() > 0.5) {
                                    actionToReturn = bluffActionToUse;
                                } else {
                                    actionToReturn = currentAction;
                                }
                            } else if(gameVariables.getBoard().size() < 5) {
                                if(equityAction.getBotEquity() > 0.4) {
                                    actionToReturn = bluffActionToUse;
                                } else {
                                    actionToReturn = currentAction;
                                }
                            } else {
                                if(oppFoldRangeToTotalRangeRatio > 0.6) {
                                    actionToReturn = bluffActionToUse;
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
        } else {
            actionToReturn = currentAction;
        }

        return actionToReturn;
    }

    private List<List<Card>> getOppCallRangeWhenYouBluff(ContinuousTable continuousTable, GameVariables gameVariables, double botSizing) {
        List<List<Card>> oppCallRangeWhenYouBluff;

        InputProvider inputProvider = new InputProvider();
        RangeConstructor rangeConstructor = new RangeConstructor();
        PreflopEquityHs preflopEquityHs = new PreflopEquityHs();

        if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
            String botPfRaiseType = inputProvider.determinBotPreflopRaiseType(botSizing);

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
                    inputProvider.getBotSizingGroup(botSizing),
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

        InputProvider inputProvider = new InputProvider();
        RangeConstructor rangeConstructor = new RangeConstructor();
        PreflopEquityHs preflopEquityHs = new PreflopEquityHs();

        if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
            String oppPfRaiseType = inputProvider.determineOppPreflopRaiseType(fictionalOppRaiseSizing);

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
                    inputProvider.getOppSizingGroup(fictionalOppRaiseSizing),
                    gameVariables.getBoard(),
                    gameVariables.getBotHoleCards());
        }

        return oppRaiseRangeWhenYouBluff;
    }
}
