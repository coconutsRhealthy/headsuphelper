package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.action.actionbuilders.ai.MachineLearning;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.card.Card;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by LennartMac on 25/05/2020.
 */
public class Rules {

    private boolean valueTrap = false;

    public String getInitialRuleAction(GameVariables gameVariables, boolean opponentHasInitiative) {
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

    public String getAfterRuleAction(String action, double facingOdds, boolean oppHasInitiative, GameVariables gameVariables,
                                     RangeConstructor rangeConstructor, double botSizing) {
        String actionToReturn;

        if(action.equals("fold")) {
            actionToReturn = callWithFavorableOdds(action, facingOdds);
        } else {
            actionToReturn = betWithStrongDraws(action, oppHasInitiative, gameVariables, rangeConstructor, botSizing);
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
            if(bigBlind < 40) {
                //todo: add handstrength

                if(botStack > bigBlind) {
                    System.out.println("pre open 2bet rule");
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

    private String callWithFavorableOdds(String action, double facingOdds) {
        String actionToReturn;

        if(action.equals("fold")) {
            System.out.println("facingodds: " + facingOdds);

            if(facingOdds < 0) {
                System.out.println("negative facing odds...");
                actionToReturn = action;
            } else if(facingOdds <= 0.2) {
                actionToReturn = "call";
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    private String betWithStrongDraws(String action, boolean oppHasInitiative, GameVariables gameVariables,
                                      RangeConstructor rangeConstructor, double botSizing) {
        String actionToReturn = action;

        if(action.equals("check")) {
            boolean bluffOddsAreOk = new MachineLearning().bluffOddsAreOk(botSizing, gameVariables.getOpponentBetSize(),
                    gameVariables.getOpponentStack(), gameVariables.getPot(), gameVariables.getBotStack(),
                    gameVariables.getBoard(), gameVariables.getBotBetSize());

            if(bluffOddsAreOk && !oppHasInitiative) {
                FlushDrawEvaluator flushDrawEvaluator = rangeConstructor.getFlushDrawEvaluatorMap().get(gameVariables.getBoard());
                StraightDrawEvaluator straightDrawEvaluator = rangeConstructor.getStraightDrawEvaluatorMap().get(gameVariables.getBoard());

                if(flushDrawEvaluator != null) {
                    if(!flushDrawEvaluator.getStrongFlushDrawCombos().isEmpty()) {
                        Set<Card> botHoleCardsAsSet = new HashSet<>();
                        botHoleCardsAsSet.addAll(gameVariables.getBotHoleCards());

                        if(flushDrawEvaluator.getStrongFlushDrawCombos().values().contains(botHoleCardsAsSet)) {
                            if(Math.random() < 0.65) {
                                actionToReturn = "bet75pct";
                                System.out.println("Bet with strong fd");
                            }
                        }
                    }
                }

                if(straightDrawEvaluator != null) {
                    if(!straightDrawEvaluator.getStrongOosdCombos().isEmpty()) {
                        Set<Card> botHoleCardsAsSet = new HashSet<>();
                        botHoleCardsAsSet.addAll(gameVariables.getBotHoleCards());

                        if(straightDrawEvaluator.getStrongOosdCombos().values().contains(botHoleCardsAsSet)) {
                            if(Math.random() < 0.65) {
                                actionToReturn = "bet75pct";
                                System.out.println("Bet with strong oosd");
                            }
                        }
                    }
                }
            }
        }

        return actionToReturn;
    }
}
