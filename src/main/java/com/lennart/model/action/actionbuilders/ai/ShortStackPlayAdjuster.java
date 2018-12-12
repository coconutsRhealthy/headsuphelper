package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;

import java.util.List;

public class ShortStackPlayAdjuster {

    public String adjustAction(String action, GameVariables gameVariables, ActionVariables actionVariables) {
        String actionToReturn;

        if(isPreflop(gameVariables.getBoard())) {
            actionToReturn = adjustPreflopAction(action, gameVariables, actionVariables);
        } else {
            actionToReturn = adjustPostflopAction(action, gameVariables, actionVariables);
        }

        return actionToReturn;
    }

    public double adjustSizing(String action, double currentSizing, double effectiveStack, double botTotalBetsize, double opponentTotalBetsize) {
        double sizingToReturn = currentSizing;

        if(action.equals("bet75pct") || action.equals("raise")) {
            sizingToReturn = effectiveStack + botTotalBetsize + opponentTotalBetsize;
        }

        return sizingToReturn;
    }

    private String adjustPreflopAction(String action, GameVariables gameVariables, ActionVariables actionVariables) {
        String actionToReturn;

        if(gameVariables.getOpponentStack() > 0) {
            if(action.equals("fold") || action.equals("call") || action.equals("check")) {
                if(actionVariables.getBotHandStrength() >= 0.57) {
                    actionToReturn = "raise";
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        } else {
            if(action.equals("fold")) {
                if(actionVariables.getBotHandStrength() >= 0.57) {
                    actionToReturn = "call";
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        }

        return actionToReturn;
    }

    private String adjustPostflopAction(String action, GameVariables gameVariables, ActionVariables actionVariables) {
        String actionToReturn;

        if(gameVariables.getOpponentStack() > 0) {
            if(action.equals("check") || action.equals("fold") || action.equals("call")) {
                String actionToUse;

                if(action.equals("check")) {
                    actionToUse = "bet75pct";
                } else {
                    actionToUse = "raise";
                }

                if(actionVariables.getBotHandStrength() >= 0.57) {
                    actionToReturn = actionToUse;
                } else {
                    HandEvaluator handEvaluator = actionVariables.getHandEvaluator();

                    boolean strongFd = handEvaluator.hasDrawOfType("strongFlushDraw");
                    boolean strongOosd = handEvaluator.hasDrawOfType("strongOosd");
                    boolean strongGutshot = handEvaluator.hasDrawOfType("strongGutshot");

                    if(strongFd || strongOosd || strongGutshot) {
                        actionToReturn = actionToUse;
                    } else {
                        actionToReturn = action;
                    }
                }
            } else {
                actionToReturn = action;
            }
        } else {
            if(action.equals("fold")) {
                if(actionVariables.getBotHandStrength() >= 0.57) {
                    actionToReturn = "call";
                } else {
                    HandEvaluator handEvaluator = actionVariables.getHandEvaluator();

                    boolean strongFd = handEvaluator.hasDrawOfType("strongFlushDraw");
                    boolean strongOosd = handEvaluator.hasDrawOfType("strongOosd");

                    if(strongFd || strongOosd) {
                        actionToReturn = "call";
                    } else {
                        actionToReturn = action;
                    }
                }
            } else {
                actionToReturn = action;
            }
        }

        return actionToReturn;
    }

    private boolean isPreflop(List<Card> board) {
        boolean preflop;

        if(board == null || board.isEmpty()) {
            preflop = true;
        } else {
            preflop = false;
        }

        return preflop;
    }
}
