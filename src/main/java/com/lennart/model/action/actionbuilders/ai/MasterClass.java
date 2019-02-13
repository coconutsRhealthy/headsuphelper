package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.util.List;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;

import java.util.List;

/**
 * Created by LennartMac on 09/02/2019.
 */
public class MasterClass {

    public String adjustToOppPre3betStat(String action, double handStrength, String oppStatsString, GameVariables gameVariables) {
        String actionToReturn;

        if(oppStatsString.contains("OppPre3betHigh")) {
            if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
                if(gameVariables.isBotIsButton()) {
                    if(gameVariables.getOpponentAction().equals("raise")) {
                        if(gameVariables.getOpponentBetSize() / gameVariables.getBigBlind() <= 11 ||
                                (gameVariables.getOpponentStack() == 0 &&
                                        (gameVariables.getOpponentBetSize() / gameVariables.getBigBlind() < 30))) {
                            if(action.equals("fold")) {
                                if(handStrength > 0.9 && gameVariables.getOpponentStack() != 0) {
                                    double random = Math.random();

                                    if(random > 0.1) {
                                        actionToReturn  = "raise";
                                        System.out.println("MasterClass set action to raise in adjustToOppPre3betStat(). A");
                                    } else {
                                        actionToReturn = "call";
                                        System.out.println("MasterClass set action to call in adjustToOppPre3betStat(). B");
                                    }
                                } else if(handStrength >= 0.70) {
                                    actionToReturn = "call";
                                    System.out.println("MasterClass set action to call in adjustToOppPre3betStat(). C");
                                } else {
                                    actionToReturn = action;
                                }
                            } else if(action.equals("call")) {
                                if(handStrength > 0.9 && gameVariables.getOpponentStack() != 0) {
                                    double random = Math.random();

                                    if(random > 0.1) {
                                        actionToReturn = "raise";
                                        System.out.println("MasterClass set action to raise in adjustToOppPre3betStat(). D");
                                    } else {
                                        actionToReturn = "call";
                                        System.out.println("MasterClass set action to call in adjustToOppPre3betStat(). E");
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
        } else if(oppStatsString.contains("OppPre3betLow")) {
            if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
                if(gameVariables.isBotIsButton()) {
                    if(gameVariables.getOpponentAction().equals("raise")) {
                        if(action.equals("call")) {
                            if (handStrength < 0.87) {
                                actionToReturn = "fold";
                                System.out.println("MasterClass set action to fold in adjustToOppPre3betStat(). F");
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

    public String adjustToOppPreLooseness(String action, double handStrength, String oppStatsString,
                                           GameVariables gameVariables, boolean oppUnknown) {
        String actionToReturn;

        if(oppStatsString.contains("OppPreLoosenessTight") && !oppUnknown) {
            if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
                if(!gameVariables.isBotIsButton()) {
                    if(gameVariables.getOpponentAction().equals("raise")) {
                        if(gameVariables.getOpponentStack() != 0) {
                            if(gameVariables.getBotBetSize() == gameVariables.getBigBlind()) {
                                if(!action.equals("raise")) {
                                    if(handStrength >= 0.7) {
                                        actionToReturn = "raise";
                                        System.out.println("MasterClass set action to raise in adjustToOppPreLooseness(). G");
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

    public String adjustToOppPostRaise(String action, double handStrength, String oppStatsString,
                                        GameVariables gameVariables, double facingOdds) {
        String actionToReturn;

        if(oppStatsString.contains("OppPostRaiseHigh")) {
            if(gameVariables.getBoard() != null && !gameVariables.getBoard().isEmpty()) {
                if(gameVariables.getOpponentAction().equals("raise")) {
                    if(action.equals("fold")) {
                        if(handStrength >= 0.75) {
                            actionToReturn = "call";
                            System.out.println("MasterClass set action to call in adjustToOppPostRaise(). H");
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
        } else if(oppStatsString.contains("OppPostRaiseLow")) {
            if(gameVariables.getBoard() != null && !gameVariables.getBoard().isEmpty()) {
                if(gameVariables.getOpponentAction().equals("raise")) {
                    if(action.equals("call")) {
                        if(facingOdds > (1.0 / 6.0)) {
                            if(handStrength < 0.9) {
                                actionToReturn = "fold";
                                System.out.println("MasterClass set action to fold in adjustToOppPostRaise(). I");
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

    public String adjustToOppPostBet(String action, double handStrength, String oppStatsString,
                                      GameVariables gameVariables, double facingOdds, ContinuousTable continuousTable) throws Exception {
        String actionToReturn;

        if(oppStatsString.contains("OppPostBetHigh")) {
            if(gameVariables.getBoard() != null && !gameVariables.getBoard().isEmpty()) {
                if(gameVariables.getOpponentAction().equals("bet75pct")) {
                    if(action.equals("fold")) {
                        if(handStrength >= 0.72) {
                            actionToReturn = "call";
                            System.out.println("MasterClass set action to call in adjustToOppPostBet(). J");
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
        } else if(oppStatsString.contains("OppPostBetLow")) {
            if(gameVariables.getBoard() != null && !gameVariables.getBoard().isEmpty()) {
                if(gameVariables.getOpponentAction().equals("bet75pct")) {
                    if(action.equals("call")) {
                        if(facingOdds > (1.0 / 6.0)) {
                            if(handStrength < 0.83) {
                                actionToReturn = "fold";
                                System.out.println("MasterClass set action to fold in adjustToOppPostBet(). K");
                            } else {
                                actionToReturn = action;
                            }
                        } else {
                            actionToReturn = action;
                        }
                    } else if(action.equals("raise")) {
                        if(handStrength >= 0.95) {
                            actionToReturn = action;
                        } else {
                            actionToReturn = new ActionVariables().getDummyAction(continuousTable, gameVariables);
                            System.out.println("MasterClass set action from raise to call or fold in adjustToOppPostBet(). L");
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

    public String adjustToOppPostLooseness(String action, double handStrength, GameVariables gameVariables,
                                           ContinuousTable continuousTable, double postLooseness) throws Exception {
        String actionToReturn;

        if(postLooseness > OpponentIdentifier2_0.POST_LOOSENESS_TIGHT_69PCT) {
            if(gameVariables.getBoard() != null && !gameVariables.getBoard().isEmpty()) {
                if(action.equals("bet75pct")) {
                    if(handStrength < 0.7) {
                        actionToReturn = "check";
                        System.out.println("MasterClass set action to check in adjustToOppPostLooseness(). M");
                    } else {
                        actionToReturn = action;
                    }
                } else if(action.equals("raise")) {
                    if(handStrength < 0.8) {
                        actionToReturn = new ActionVariables().getDummyAction(continuousTable, gameVariables);
                        System.out.println("MasterClass set action from raise to call or fold in adjustToOppPostLooseness(). N");
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

    public String alwaysValueBetAgainstLoosePassivePostflop(String action, double handStrength, String opponentStatsString,
                                                            boolean opponentHasInitiative, List<Card> board) {
        String actionToReturn;

        if(opponentStatsString.contains("OppPostBetLowOppPostLoosenessLoose")) {
            if(board != null && !board.isEmpty()) {
                if(action.equals("check") && !opponentHasInitiative) {
                    if(handStrength >= 0.8) {
                        //maybe refine this
                        actionToReturn = "bet75pct";
                        System.out.println("MasterClass set action to bet in alwaysValueBetAgainstLoosePassivePostflop(). O");
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

    public String raiseWeapon(String action, GameVariables gameVariables, double sizing, double handStrength, HandEvaluator handEvaluator, ContinuousTable continuousTable, BoardEvaluator boardEvaluator) throws Exception {

        String actionToReturn;

        if(action.equals("fold") || action.equals("call")) {
            if(gameVariables.getBoard() != null && !gameVariables.getBoard().isEmpty()) {
                if(gameVariables.getOpponentAction().equals("bet75pct")) {
                    if(bluffOddsAreOk(sizing, gameVariables.getOpponentBetSize(), gameVariables.getOpponentStack(), gameVariables.getPot(),
                            gameVariables.getBotStack(), gameVariables.getBoard(), gameVariables.getBotBetSize())) {
                        if(gameVariables.getBoard().size() == 3 || gameVariables.getBoard().size() == 4) {
                            boolean strongFd = handEvaluator.hasDrawOfType("strongFlushDraw");
                            boolean strongOosd = handEvaluator.hasDrawOfType("strongOosd");
                            boolean strongGutshot = handEvaluator.hasDrawOfType("strongGutshot");
                            boolean strongThirdPair = handEvaluator.hasNonTopPairWithOverKicker(gameVariables.getBoard(), gameVariables.getBotHoleCards(), boardEvaluator);

                            if(handStrength > 0.91 || strongFd || strongOosd || strongGutshot || strongThirdPair) {
                                OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0(gameVariables.getOpponentName());

                                double postLooseness = opponentIdentifier2_0.getOppPostLooseness();
                                double postBet = opponentIdentifier2_0.getOppPostBet();

                                if(postLooseness <= OpponentIdentifier2_0.POST_LOOSENESS_TIGHT_69PCT && postBet >= OpponentIdentifier2_0.POST_BET_AGGRO_69_PCT) {
                                    actionToReturn = "raise";
                                    System.out.println("raiseWeapon changed action to raise flop or turn");
                                } else {
                                    actionToReturn = action;
                                }
                            } else {
                                actionToReturn = action;
                            }
                        } else {
                            int boardWetness = BoardEvaluator.getBoardWetness(continuousTable.getTop10percentTurnCombos(),
                                    continuousTable.getTop10percentRiverCombos());

                            if(handStrength > 0.91 || (boardWetness <= 66 && action.equals("fold"))) {
                                OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0(gameVariables.getOpponentName());

                                double postLooseness = opponentIdentifier2_0.getOppPostLooseness();
                                double postBet = opponentIdentifier2_0.getOppPostBet();

                                if(postLooseness <= OpponentIdentifier2_0.POST_LOOSENESS_TIGHT_69PCT && postBet >= OpponentIdentifier2_0.POST_BET_AGGRO_69_PCT) {
                                    actionToReturn = "raise";
                                    System.out.println("raiseWeapon changed action to raise river");
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
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public double adjustRaiseSizingToSng(double currentSizing, String action, GameVariables gameVariables,
                                          double effectiveStackBb) {
        double sngSizingToReturn;

        if(action.equals("raise")) {
            if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
                if(!gameVariables.isBotIsButton()) {
                    if(gameVariables.getOpponentAction().equals("raise")) {
                        if(effectiveStackBb <= 30) {
                            sngSizingToReturn = 5000 * gameVariables.getBigBlind();
                            System.out.println("MasterClass change pre3bet sizing to shove in adjustRaiseSizingToSng(). P");
                        } else {
                            sngSizingToReturn = currentSizing;
                        }
                    } else {
                        sngSizingToReturn = currentSizing;
                    }
                } else {
                    if(gameVariables.getOpponentAction().equals("raise")) {
                        //4bet
                        sngSizingToReturn = 5000 * gameVariables.getBigBlind();
                        System.out.println("MasterClass change pre4bet sizing to shove in adjustRaiseSizingToSng(). Q");
                    } else {
                        sngSizingToReturn = currentSizing;
                    }
                }
            } else {
                //postflop
                if(currentSizing > 300 || effectiveStackBb <= 30) {
                    sngSizingToReturn = 5000 * gameVariables.getBigBlind();
                    System.out.println("MasterClass change postRaise sizing to shove in adjustRaiseSizingToSng(). R");
                } else {
                    sngSizingToReturn = currentSizing;
                }
            }
        } else {
            sngSizingToReturn = currentSizing;
        }

        return sngSizingToReturn;
    }

    public String alterUnknownOpponentToLoosePassive(String oppStatsString) {
        String statsStringToReturn;

        if(oppStatsString.contains("OpponentUnknown")) {
            statsStringToReturn = "OppPre3betLowOppPreLoosenessLooseOppPostRaiseLowOppPostBetLowOppPostLoosenessLoose";
        } else {
            statsStringToReturn = oppStatsString;
        }

        return statsStringToReturn;
    }

    private boolean bluffOddsAreOk(double sizing, double facingBetSize, double facingStackSize, double pot,
                                   double ownStackSize, List<Card> board, double ownBetSize) {
        return new MachineLearning().bluffOddsAreOk(sizing, facingBetSize, facingStackSize, pot, ownStackSize, board, ownBetSize);
    }
}
