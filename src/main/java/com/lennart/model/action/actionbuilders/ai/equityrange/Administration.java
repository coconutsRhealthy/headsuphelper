package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePreflopStats;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveRaw;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handtracker.ActionRequest;
import com.lennart.model.handtracker.PlayerActionRound;
import equitycalc.EquityCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 26/05/2020.
 */
public class Administration {

    public void doDbSaveStuff(String action, ContinuousTable continuousTable, GameVariables gameVariables,
                                     double sizing, RangeConstructor rangeConstructor, double botEquity) {
        try {
            //DbSaveRaw
            DbSaveRaw dbSaveRaw = new DbSaveRaw();

            String boardString = dbSaveRaw.getBoardLogic(gameVariables.getBoard());
            String holeCardsString = dbSaveRaw.getHoleCardsLogic(gameVariables.getBotHoleCards());
            String positionString = dbSaveRaw.getPositionLogic(gameVariables.isBotIsButton());
            String opponentData = dbSaveRaw.getOpponentDataLogic(gameVariables.getOpponentName());
            double recentHandsWon = dbSaveRaw.getRecentHandsWonLogic(gameVariables.getOpponentName());
            String adjustedOppType = dbSaveRaw.getAdjustedOppTypeLogic(gameVariables.getOpponentName());
            String strongDrawString = getStrongDrawString(rangeConstructor, gameVariables.getBoard());

            dbSaveRaw.setBotAction(action);
            dbSaveRaw.setOppAction(gameVariables.getOpponentAction());
            dbSaveRaw.setBoard(boardString);
            dbSaveRaw.setHoleCards(holeCardsString);
            dbSaveRaw.setHandStrength(-2);
            dbSaveRaw.setBotStack(gameVariables.getBotStack());
            dbSaveRaw.setOpponentStack(gameVariables.getOpponentStack());
            dbSaveRaw.setBotTotalBetSize(gameVariables.getBotBetSize());
            dbSaveRaw.setOpponentTotalBetSize(gameVariables.getOpponentBetSize());
            dbSaveRaw.setSizing(sizing);
            dbSaveRaw.setPosition(positionString);
            dbSaveRaw.setStake("1.50sng_hyper");
            dbSaveRaw.setOpponentName(gameVariables.getOpponentName());
            dbSaveRaw.setOpponentData(opponentData);
            dbSaveRaw.setBigBlind(gameVariables.getBigBlind());
            dbSaveRaw.setStrongDraw(strongDrawString);
            dbSaveRaw.setRecentHandsWon(recentHandsWon);
            dbSaveRaw.setAdjustedOppType(adjustedOppType);
            dbSaveRaw.setPot(gameVariables.getPot());
            dbSaveRaw.setEquity(calculateBotEquityIfNecessary(botEquity, gameVariables.getBotHoleCards()));

            continuousTable.getDbSaveList().add(dbSaveRaw);
            //DbSaveRaw

            //DbSavePreflopStats
            if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
                DbSavePreflopStats dbSavePreflopStats = new DbSavePreflopStats();

                double pre2betCount = dbSavePreflopStats.getPreXbetCountLogic(
                        gameVariables.getOpponentAction(), gameVariables.getBoard(), gameVariables.getOpponentBetSize(),
                        gameVariables.getBigBlind(), "pre2bet");
                double pre3betCount = dbSavePreflopStats.getPreXbetCountLogic(
                        gameVariables.getOpponentAction(), gameVariables.getBoard(), gameVariables.getOpponentBetSize(),
                        gameVariables.getBigBlind(), "pre3bet");
                double pre4bet_up_count = dbSavePreflopStats.getPreXbetCountLogic(
                        gameVariables.getOpponentAction(), gameVariables.getBoard(), gameVariables.getOpponentBetSize(),
                        gameVariables.getBigBlind(), "pre4bet_up");
                double preTotalCount = dbSavePreflopStats.getPreTotalCountLogic(gameVariables.getOpponentAction(), gameVariables.getBoard());

                dbSavePreflopStats.setOpponentName(gameVariables.getOpponentName());
                dbSavePreflopStats.setOppPre2betCount(pre2betCount);
                dbSavePreflopStats.setOppPre3betCount(pre3betCount);
                dbSavePreflopStats.setOppPre4bet_up_count(pre4bet_up_count);
                dbSavePreflopStats.setOppPreTotalCount(preTotalCount);

                if(action.equals("raise")) {
                    dbSavePreflopStats.setOppPreCallTotalCount(1);

                    if(sizing / gameVariables.getBigBlind() > 10) {
                        continuousTable.setBotDidPre4bet(true);
                    }
                }

                continuousTable.getDbSaveList().add(dbSavePreflopStats);
            } else if(gameVariables.getBoard().size() == 3) {
                List<ActionRequest> allActionRequestsOfHand = gameVariables.getAllActionRequestsOfHand();
                ActionRequest secondLastActionRequest = allActionRequestsOfHand.get(allActionRequestsOfHand.size() - 2);
                PlayerActionRound botLastActionRound = secondLastActionRequest.getMostRecentActionRoundOfPLayer(secondLastActionRequest.getActionsSinceLastRequest(), "bot");
                List<Card> previousBoard = botLastActionRound.getBoard();

                if(previousBoard.isEmpty() && botLastActionRound.getAction().equals("raise")) {
                    DbSavePreflopStats dbSavePreflopStats = new DbSavePreflopStats();

                    double preCall2betCount = dbSavePreflopStats.getPreXbetCallCountLogic(
                            botLastActionRound.getTotalBotBetSize(), gameVariables.getBigBlind(), "preCall2bet");
                    double preCall3betCount = dbSavePreflopStats.getPreXbetCallCountLogic(
                            botLastActionRound.getTotalBotBetSize(), gameVariables.getBigBlind(), "preCall3bet");
                    double preCall4betUpCount = dbSavePreflopStats.getPreXbetCallCountLogic(
                            botLastActionRound.getTotalBotBetSize(), gameVariables.getBigBlind(), "preCall4bet_up");

                    dbSavePreflopStats.setOpponentName(gameVariables.getOpponentName());
                    dbSavePreflopStats.setOppPreCall2betCount(preCall2betCount);
                    dbSavePreflopStats.setOppPreCall3betCount(preCall3betCount);
                    dbSavePreflopStats.setOppPreCall4bet_up_count(preCall4betUpCount);

                    continuousTable.getDbSaveList().add(dbSavePreflopStats);
                }
            }
            //DbSavePreflopStats
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void doActionRoundStuff(String action, GameVariables gameVariables, double sizing) {
        double totalBotBetSizeForPlayerActionRound;

        if(sizing == 0) {
            totalBotBetSizeForPlayerActionRound = gameVariables.getBotBetSize();
        } else {
            totalBotBetSizeForPlayerActionRound = sizing;
        }

        List<Card> currentBoardCopy = new ArrayList<>();
        currentBoardCopy.addAll(gameVariables.getBoard());
        double opponentBetSizeCopy = gameVariables.getOpponentBetSize();
        String actionCopy = action;

        PlayerActionRound botPlayerActionRound = new PlayerActionRound("bot", currentBoardCopy, totalBotBetSizeForPlayerActionRound, opponentBetSizeCopy, "theCorrectStreet", actionCopy);
        List<ActionRequest> allActionRequestsOfHand = gameVariables.getAllActionRequestsOfHand();
        ActionRequest lastActionRequest = allActionRequestsOfHand.get(allActionRequestsOfHand.size() - 1);
        lastActionRequest.getActionsSinceLastRequest().add(botPlayerActionRound);

        double updatedBotStack = getUpdatedBotStack(actionCopy, gameVariables, totalBotBetSizeForPlayerActionRound);
        gameVariables.setBotStack(updatedBotStack);
        gameVariables.setBotBetSize(totalBotBetSizeForPlayerActionRound);
    }

    private double getUpdatedBotStack(String action, GameVariables gameVariables, double newBotBetSize) {
        double updatedBotStack;
        double botStackBeforeUpdate = gameVariables.getBotStack();

        double totalOpponentBetSize = gameVariables.getOpponentBetSize();
        double previousBotBetSize = gameVariables.getBotBetSize();

        if(action.equals("call")) {
            //botstack = botstack - (totalopponentbetsize - totalbotbetsize)
            updatedBotStack = botStackBeforeUpdate - (totalOpponentBetSize - newBotBetSize);
        } else if(action.equals("bet75pct")) {
            //botstack = botstack - totalbotbetsize
            updatedBotStack = botStackBeforeUpdate - newBotBetSize;
        } else if(action.equals("raise")) {
            //botstack = botstack - (totalbotbetsize - previoustotalbotbetsize)
            updatedBotStack = botStackBeforeUpdate - (newBotBetSize - previousBotBetSize);
        } else {
            updatedBotStack = botStackBeforeUpdate;
        }

        return updatedBotStack;
    }

    private String getStrongDrawString(RangeConstructor rangeConstructor, List<Card> board) {
        String strongDrawString = "StrongDrawFalse";

        StraightDrawEvaluator straightDrawEvaluator = rangeConstructor.getStraightDrawEvaluatorMap().get(board);

        if(straightDrawEvaluator != null) {
            if(straightDrawEvaluator.getStrongOosdCombos() != null) {
                if(!straightDrawEvaluator.getStrongOosdCombos().isEmpty()) {
                    strongDrawString = "StrongDrawTrue";
                }
            }
        }

        if(strongDrawString.equals("StrongDrawFalse")) {
            FlushDrawEvaluator flushDrawEvaluator = rangeConstructor.getFlushDrawEvaluatorMap().get(board);

            if(flushDrawEvaluator != null) {
                if(flushDrawEvaluator.getStrongFlushDrawCombos() != null) {
                    if(!flushDrawEvaluator.getStrongFlushDrawCombos().isEmpty()) {
                        strongDrawString = "StrongDrawTrue";
                    }
                }
            }
        }

        return strongDrawString;
    }

    private double calculateBotEquityIfNecessary(double botEquity, List<Card> botHoleCards) {
        double botEquityToReturn;

        if(botEquity == -3) {
            botEquityToReturn = new EquityCalculator().getComboEquity(botHoleCards, null);
        } else {
            botEquityToReturn = botEquity;
        }

        return botEquityToReturn;
    }
}
