package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.card.Card;
import equitycalc.simulation.Range;

import java.util.List;

/**
 * Created by LennartMac on 25/05/2020.
 */
public class Rules {


    //sizing blijft zoals ie is

    //altijd pre2betten bij blinds lager dan 50

    //altijd pre limpen bij blinds vanaf 50

    //callen met draws en goede odds

    //altijd callen met super goede odds

    private boolean valueTrap = false;


    public String getInitialRuleAction(GameVariables gameVariables, boolean opponentHasInitiative, List<String> eligibleActions) {
        String initialRuleAction = null;

        initialRuleAction = defaultCheckWhenOppHasInitiative(opponentHasInitiative, gameVariables.getOpponentAction());

        if(initialRuleAction == null) {
            initialRuleAction = preOpen(gameVariables.getOpponentAction(), gameVariables.getBigBlind(), gameVariables.getBotStack());
        }

        return initialRuleAction;
    }

    public String getValueTrapAction(String currentAction, GameVariables gameVariables) {
        String actionToReturn;

        if(currentAction.equals("bet75pct") || currentAction.equals("raise")) {
            double valueTrapDecimal = 0.065;

            if(Math.random() < valueTrapDecimal) {
                String valueTrapActionToUse;

                if(currentAction.equals("bet75pct")) {
                    valueTrapActionToUse = "check";
                } else {
                    valueTrapActionToUse = "call";
                }

                if(gameVariables.getBoard() != null && gameVariables.getBoard().size() == 5) {
                    if(gameVariables.isBotIsButton()) {
                        actionToReturn = currentAction;
                    } else {
                        actionToReturn = valueTrapActionToUse;
                        valueTrap = true;
                    }
                } else {
                    actionToReturn = valueTrapActionToUse;
                    valueTrap = true;
                }
            } else {
                actionToReturn = currentAction;
            }
        } else {
            actionToReturn = currentAction;
        }

        return actionToReturn;
    }

    public String getAfterRuleAction(String action, RangeConstructor rangeConstructor, double facingOdds, List<Card> board) {
        String actionToReturn;

        if(action.equals("fold")) {
            actionToReturn = callWithFavorableOdds(action, facingOdds);

            if(actionToReturn.equals("fold")) {
                actionToReturn = callWithStrongDrawAndGoodOdds(action, rangeConstructor, facingOdds, board);
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public boolean isValueTrap() {
        return valueTrap;
    }

    private String defaultCheckWhenOppHasInitiative(boolean oppHasInitiative, String oppAction) {
        String actionToReturn = null;

        if(oppHasInitiative && oppAction.equals("empty")) {
            actionToReturn = "check";
        }

        return actionToReturn;
    }

    private String preOpen(String oppAction, double bigBlind, double botStack) {
        String actionToReturn = null;

        if(oppAction.equals("bet")) {
            if(bigBlind < 50) {
                //todo: add handstrength

                if(botStack > bigBlind) {
                    actionToReturn = "raise";
                } else {
                    actionToReturn = "call";
                }
            } else {
                actionToReturn = "call";
            }
        }

        return actionToReturn;
    }

    private String callWithStrongDrawAndGoodOdds(String action, RangeConstructor rangeConstructor, double facingOdds, List<Card> board) {
        String actionToReturn;

        if(action.equals("fold")) {
            if(board != null && (board.size() == 3  || board.size() == 4)) {
                if(facingOdds < 0.43) {
                    FlushDrawEvaluator flushDrawEvaluator = rangeConstructor.getFlushDrawEvaluatorMap().get(board);
                    StraightDrawEvaluator straightDrawEvaluator = rangeConstructor.getStraightDrawEvaluatorMap().get(board);

                    if(board.size() == 3) {
                        if((flushDrawEvaluator.getStrongFlushDrawCombos() != null && !flushDrawEvaluator.getStrongFlushDrawCombos().isEmpty())
                                || (straightDrawEvaluator.getStrongOosdCombos() != null && !straightDrawEvaluator.getStrongOosdCombos().isEmpty())
                                || (straightDrawEvaluator.getStrongGutshotCombos() != null && !straightDrawEvaluator.getStrongGutshotCombos().isEmpty())) {
                            actionToReturn = "call";
                        } else {
                            actionToReturn = action;
                        }
                    } else {
                        if(facingOdds < 0.35) {
                            if((flushDrawEvaluator.getStrongFlushDrawCombos() != null && !flushDrawEvaluator.getStrongFlushDrawCombos().isEmpty())
                                    || (straightDrawEvaluator.getStrongOosdCombos() != null && !straightDrawEvaluator.getStrongOosdCombos().isEmpty())) {
                                actionToReturn = "call";
                            } else {
                                actionToReturn = action;
                            }
                        } else {
                            actionToReturn = action;
                        }
                    }
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    private String callWithFavorableOdds(String action, double facingOdds) {
        String actionToReturn;

        if(action.equals("fold")) {
            if(facingOdds < 0.2) {
                actionToReturn = "call";
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }
}
