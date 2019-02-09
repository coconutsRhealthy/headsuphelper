package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0.DbSaveBluff_2_0;
import com.lennart.model.action.actionbuilders.ai.dbstatsraw.DbStatsRawBluffPostflopMigrator;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;

import java.util.List;

/**
 * Created by LennartMac on 03/02/2019.
 */
public class RuleApplier_2_0 {

    public String raisePostflopAgainstAggroOpps(String action, String opponentName, ContinuousTable continuousTable, List<Card> board,
                                               double sizing, double facingBetSize, double facingStackSize, double pot,
                                               double ownStackSize, double ownBetSize, String opponentAction, BoardEvaluator boardEvaluator) throws Exception {
        String actionToReturn;

        if(action.equals("fold") || action.equals("call")) {
            if(board != null && !board.isEmpty()) {
                if(bluffOddsAreOk(sizing, facingBetSize, facingStackSize, pot, ownStackSize, board, ownBetSize)) {
                    int boardWetness;

                    if(board.size() == 3) {
                        boardWetness = boardEvaluator.getFlushStraightWetness();
                    } else if(board.size() == 4) {
                        boardWetness = BoardEvaluator.getBoardWetness(continuousTable.getTop10percentFlopCombos(), continuousTable.getTop10percentTurnCombos());
                    } else {
                        boardWetness = BoardEvaluator.getBoardWetness(continuousTable.getTop10percentTurnCombos(), continuousTable.getTop10percentRiverCombos());
                    }

                    String boardWetnessGroup = new DbSaveBluff_2_0().getBoardWetnessGroupLogic(board, boardWetness);

                    if(boardWetnessGroup.equals("wet")) {
                        String opponentStatsString = new DbStatsRawBluffPostflopMigrator().getOpponentStatsString(opponentName);

                        if(opponentAction.equals("bet75pct")) {
                            if(opponentStatsString.contains("OppPostBetHigh")) {
                                double random = Math.random();

                                if(random < 0.8) {
                                    actionToReturn = "raise";
                                    System.out.println("Changed action to raise, opp bet, in raiseAggroAgainstNonGroupAopps()");
                                } else {
                                    actionToReturn = action;
                                    System.out.println("zzz, opp bet, in raiseAggroAgainstNonGroupAopps()");
                                }
                            } else {
                                actionToReturn = action;
                            }
                        } else {
                            if(opponentStatsString.contains("OppPostRaiseHigh")) {
                                double random = Math.random();

                                if(random < 0.8) {
                                    actionToReturn = "raise";
                                    System.out.println("Changed action to raise, opp raise, in raiseAggroAgainstNonGroupAopps()");
                                } else {
                                    actionToReturn = action;
                                    System.out.println("zzz, opp raise, in raiseAggroAgainstNonGroupAopps()");
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
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String betWithInitiativeOnWetBoardTurnAndRiver(String action, ContinuousTable continuousTable, List<Card> board,
                                                          double sizing, double facingBetSize, double facingStackSize, double pot,
                                                          double ownStackSize, double ownBetSize, boolean botHasInitiative) {
        String actionToReturn;

        if(action.equals("check")) {
            if(botHasInitiative) {
                if(board != null && (board.size() == 4 || board.size() == 5)) {
                    if(bluffOddsAreOk(sizing, facingBetSize, facingStackSize, pot, ownStackSize, board, ownBetSize)) {
                        int boardWetness;

                        if(board.size() == 4) {
                            boardWetness = BoardEvaluator.getBoardWetness(continuousTable.getTop10percentFlopCombos(), continuousTable.getTop10percentTurnCombos());
                        } else {
                            boardWetness = BoardEvaluator.getBoardWetness(continuousTable.getTop10percentTurnCombos(), continuousTable.getTop10percentRiverCombos());
                        }

                        String boardWetnessGroup = new DbSaveBluff_2_0().getBoardWetnessGroupLogic(board, boardWetness);

                        if(boardWetnessGroup.equals("wet")) {
                            double random = Math.random();

                            if(random < 0.8) {
                                actionToReturn = "bet";
                                System.out.println("Changed action to bet in betWithInitiativeOnWetBoardTurnAndRiver()");
                            } else {
                                actionToReturn = action;
                                System.out.println("zzz in betWithInitiativeOnWetBoardTurnAndRiver()");
                            }
                        } else {
                            actionToReturn = action;
                        }
                    } else {
                        actionToReturn = action;
                    }
                } else{
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

    public String aggro3betPreOopShortStacked(String action, boolean position, double effectiveStackBb, String opponentAction,
                                               double handStrength, List<Card> board, String opponentName, double botTotalBetSize,
                                               double bigBlind, double opponentStack) throws Exception {
        String actionToReturn;

        if(!action.equals("raise")) {
            if(board == null || board.isEmpty()) {
                if(effectiveStackBb <= 30) {
                    if(!position) {
                        if(opponentAction.equals("raise")) {
                            if(opponentStack > 0) {
                                if(botTotalBetSize == bigBlind) {
                                    if(handStrength >= 0.7) {
                                        String opponentStatsString = new DbStatsRawBluffPostflopMigrator().getOpponentStatsString(opponentName);

                                        if(opponentStatsString.contains("OppPreLoosenessTight")) {
                                            actionToReturn = "raise";
                                            System.out.println("aggro3betPreOopShortStacked() changed to raise");
                                        } else {
                                            actionToReturn = action;
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
                        } else {
                            actionToReturn = action;
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
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String callLightPre3betsAgainstAggroShoves(String action, boolean position, String opponentAction,
                                                      double handStrength, List<Card> board, String opponentName, double botTotalBetSize,
                                                      double bigBlind, double opponentStack, double opponentTotalBetsize) throws Exception {
        String actionToReturn;

        if(action.equals("fold")) {
            if(board == null || board.isEmpty()) {
                if(position) {
                    if(opponentAction.equals("raise")) {
                        if(opponentStack == 0) {
                            if(botTotalBetSize < 2.7 * bigBlind) {
                                if(opponentTotalBetsize / bigBlind <= 30) {
                                    if(handStrength >= 0.7) {
                                        String opponentStatsString = new DbStatsRawBluffPostflopMigrator().getOpponentStatsString(opponentName);

                                        if(opponentStatsString.contains("OppPre3betHigh")) {
                                            actionToReturn = "call";
                                            System.out.println("callLightPre3betsAgainstAggroShoves() changed to call");
                                        } else {
                                            actionToReturn = action;
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
                        } else {
                            actionToReturn = action;
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
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    private boolean bluffOddsAreOk(double sizing, double facingBetSize, double facingStackSize, double pot,
                                   double ownStackSize, List<Card> board, double ownBetSize) {
        return new MachineLearning().bluffOddsAreOk(sizing, facingBetSize, facingStackSize, pot, ownStackSize, board, ownBetSize);
    }
}
