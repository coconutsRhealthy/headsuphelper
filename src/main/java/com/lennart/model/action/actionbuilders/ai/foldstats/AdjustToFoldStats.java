package com.lennart.model.action.actionbuilders.ai.foldstats;

import com.lennart.model.action.actionbuilders.ai.ActionVariables;
import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.action.actionbuilders.ai.Poker;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.computergame.ComputerGameNew;

import java.util.List;
import java.util.Map;

/**
 * Created by LennartMac on 24/05/2018.
 */
public class AdjustToFoldStats {

    public String adjustPlayToBotFoldStat(String action, double handStrength, double requiredHandStrength,
                                          List<Card> holeCards, List<Card> board, boolean position, String opponentPlayerName,
                                          double botBetsizeBb, double opponentBetsizeBb, boolean computerGame) throws Exception {
        String actionToReturn;

        double botFoldStat;

        if(computerGame) {
            botFoldStat = FoldStatsKeeper.getFoldStat("bot-V-" + opponentPlayerName);
        } else {
            botFoldStat = new FoldStatsKeeper().getFoldStatFromDb("bot-V-" + opponentPlayerName);
        }

        System.out.println("botFoldStat: " + botFoldStat);

        double differenceBotFoldStatAndDefault = botFoldStat - 0.43;

        if(differenceBotFoldStatAndDefault > 0) {
            if(board == null || board.isEmpty()) {
                if(opponentBetsizeBb <= 16) {
                    if(holeCardsAreBluffable(holeCards) && position) {
                        //bij 63% alles...
                        System.out.println("differenceBotFoldStatAndDefault: " + differenceBotFoldStatAndDefault);
                        if(differenceBotFoldStatAndDefault >= 0.2) {
                            actionToReturn = "call";
                        } else {
                            double percentageToUseBluffablePreflop = differenceBotFoldStatAndDefault / 0.2;
                            System.out.println("percentageToUseBluffablePreflop: " + percentageToUseBluffablePreflop);
                            double random = Math.random();

                            if(random <= percentageToUseBluffablePreflop) {
                                actionToReturn = "call";
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
                if(opponentBetsizeBb - botBetsizeBb <= 100) {
                    if(differenceBotFoldStatAndDefault <= 0.13) {
                        differenceBotFoldStatAndDefault = differenceBotFoldStatAndDefault * 1.15;
                    }

                    double acceptableHandStrengthToCall = requiredHandStrength - (differenceBotFoldStatAndDefault);

                    if(handStrength >= acceptableHandStrengthToCall) {
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

        return actionToReturn;
    }

    public double getHandStrengthRequiredToCall(ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                                double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                                double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                                boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                                boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                                int boardWetness, boolean opponentHasInitiative, Map<Integer, List<Card>> botRange, ContinuousTable continuousTable,
                                                GameVariables gameVariables, BoardEvaluator boardEvaluator, ComputerGameNew computerGameNew) {
        double downLimit = 0;
        double upLimit = 1;
        int counter = 0;

        for(int i = 0; i < 11; i++) {
            double numberInTheMiddle = ((downLimit + upLimit) / 2);

            eligibleActions.clear();
            eligibleActions.add("fold");
            eligibleActions.add("call");

            String action = new Poker().getAction(actionVariables, eligibleActions, street, position, potSizeBb,
                    opponentAction, facingOdds, effectiveStackBb, strongDraw, numberInTheMiddle, opponentType, opponentBetSizeBb,
                    ownBetSizeBb, opponentStackBb, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                    bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                    strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, continuousTable, gameVariables, boardEvaluator, computerGameNew);

            if(action.equals("fold")) {
                downLimit = numberInTheMiddle;
                counter++;

                if(counter == 6) {
                    break;
                }
            } else {
                if(!strongDraw) {
                    upLimit = numberInTheMiddle;
                    counter++;

                    if(counter == 6) {
                        break;
                    }
                }
            }
        }

        System.out.println();
        System.out.println("downLimit: " + downLimit);
        System.out.println("upLimit: " + upLimit);
        System.out.println();

        double valueToReturn = (downLimit + upLimit) / 2;
        return valueToReturn;
    }

    private boolean holeCardsAreBluffable(List<Card> holeCards) {
        boolean holeCardsAreBluffable = false;

        if(holeCards != null && holeCards.size() == 2) {
            //ace
            if(holeCards.get(0).getRank() == 14 || holeCards.get(1).getRank() == 14) {
                holeCardsAreBluffable = true;
            }

            //suited
            if(!holeCardsAreBluffable && (holeCards.get(0).getSuit() == holeCards.get(1).getSuit())) {
                holeCardsAreBluffable = true;
            }

            //one gapper
            if(!holeCardsAreBluffable &&
                    (holeCards.get(0).getRank() - holeCards.get(1).getRank() == 1 || holeCards.get(0).getRank() - holeCards.get(1).getRank() == -1)) {
                holeCardsAreBluffable = true;
            }

            //pocket pairs
            if(!holeCardsAreBluffable && (holeCards.get(0).getRank() == holeCards.get(1).getRank())) {
                holeCardsAreBluffable = true;
            }
        }

        return holeCardsAreBluffable;
    }
}
