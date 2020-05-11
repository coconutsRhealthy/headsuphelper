package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSave;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveRaw;
import com.lennart.model.action.actionbuilders.ai.dbstatsraw.Analysis;
import com.lennart.model.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 10/05/2020.
 */
public class OpponentRangeSetter {

    private void setOpponentRange(ContinuousTable continuousTable, GameVariables gameVariables) {
        if(continuousTable.getDbSaveList().isEmpty()) {
            continuousTable.setOppRange(new RangeConstructor().createStartingOppRange(gameVariables.getBotHoleCards()));
        } else {
            if(continuousTable.getOppRange() == null || continuousTable.getOppRange().isEmpty()) {
                System.out.println("Shouldn't come here, OpponentRangeSetter - A");
                continuousTable.setOppRange(new RangeConstructor().createStartingOppRange(gameVariables.getBotHoleCards()));
            }

            setNonInitialRange(continuousTable, gameVariables);
        }
    }

    private void setNonInitialRange(ContinuousTable continuousTable, GameVariables gameVariables) {
        if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
            setPreflopRange();
        } else {
            setPostflopRange(continuousTable, gameVariables);
        }
    }

    private void setPreflopRange() {

    }

    private void setPostflopRange(ContinuousTable continuousTable, GameVariables gameVariables) {
        List<DbSaveRaw> dbSaveRawList = new ArrayList<>();

        for(DbSave dbSave : continuousTable.getDbSaveList()) {
            if(dbSave instanceof DbSaveRaw) {
                dbSaveRawList.add((DbSaveRaw) dbSave);
            }
        }

        DbSaveRaw previousRound = dbSaveRawList.get(dbSaveRawList.size() - 1);
        String previousBoardString = previousRound.getBoard();
        List<Card> previousBoard = new Analysis().convertCardStringToCardList(previousBoardString);
        List<Card> currentBoard = gameVariables.getBoard();

        if(previousBoard.equals(currentBoard)) {
            setPostflopSameStreetRange(continuousTable, gameVariables);
        } else {
            setPostflopNewStreetRange(gameVariables, previousBoard, previousRound);
        }
    }

    private void setPostflopSameStreetRange(ContinuousTable continuousTable, GameVariables gameVariables) {
        RangeConstructor rangeConstructor = new RangeConstructor();
        EquityAction2 equityAction2 = new EquityAction2();

        if(gameVariables.getOpponentAction().equals("bet75pct")) {
            List<List<Card>> oppBetRange = rangeConstructor.getOppPostflopBetRange(
                    continuousTable.getOppRange(),
                    equityAction2.getAllCombosPostflopEquitySorted(gameVariables.getBoard(), gameVariables.getBotHoleCards()),
                    equityAction2.getOppAggroness(gameVariables.getOpponentName()),
                    equityAction2.getOppSizingGroup(gameVariables.getOpponentBetSize()),
                    gameVariables.getBoard(),
                    gameVariables.getBotHoleCards());
            continuousTable.setOppRange(oppBetRange);
        } else if(gameVariables.getOpponentAction().equals("raise")) {
            List<List<Card>> oppRaiseRange = rangeConstructor.getOppPostflopRaiseRange(
                    continuousTable.getOppRange(),
                    equityAction2.getAllCombosPostflopEquitySorted(gameVariables.getBoard(), gameVariables.getBotHoleCards()),
                    equityAction2.getOppAggroness(gameVariables.getOpponentName()),
                    equityAction2.getOppSizingGroup(gameVariables.getOpponentBetSize()),
                    gameVariables.getBoard(),
                    gameVariables.getBotHoleCards());
            continuousTable.setOppRange(oppRaiseRange);
        }
    }

    private void setPostflopNewStreetRange(GameVariables gameVariables, List<Card> previousBoard, DbSaveRaw previousRound) {
        if(previousBoard.isEmpty()) {
            setPreflopToFlopRange(gameVariables);
        } else {
            setFlopToTurnOrTurnToRiverRange(null, gameVariables, previousRound, previousBoard);
        }
    }

    private void setPreflopToFlopRange(GameVariables gameVariables) {
        if(gameVariables.isBotIsButton()) {
            setInPositionPreflopToFlopRange();
        } else {
            setOopPreflopToFlopRange();
        }
    }

    private void setInPositionPreflopToFlopRange() {

    }

    private void setOopPreflopToFlopRange() {

    }

    private void setFlopToTurnOrTurnToRiverRange(ContinuousTable continuousTable, GameVariables gameVariables,
                                                 DbSaveRaw previousRound, List<Card> previousBoard) {
        RangeConstructor rangeConstructor = new RangeConstructor();
        EquityAction2 equityAction2 = new EquityAction2();

        if(gameVariables.isBotIsButton()) {
            setInPositionFlopToTurnOrTurnToRiverRange(continuousTable, gameVariables, previousRound, previousBoard,
                    equityAction2, rangeConstructor);
        } else {
            if(previousRound.getBotAction().equals("check")) {
                List<List<Card>> oppCheckRange = rangeConstructor.getOppPostflopCheckRange(
                        continuousTable.getOppRange(),
                        equityAction2.getAllCombosPostflopEquitySorted(previousBoard, gameVariables.getBotHoleCards()),
                        equityAction2.getOppAggroness(gameVariables.getOpponentName()),
                        equityAction2.getPotSizeGroup(previousRound.getPot()),
                        previousBoard,
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppCheckRange);
            } else if(previousRound.getBotAction().equals("bet75pct") || previousRound.getBotAction().equals("raise")) {
                List<List<Card>> oppCallRange = rangeConstructor.getOppPostflopCallRange(
                        continuousTable.getOppRange(),
                        equityAction2.getAllCombosPostflopEquitySorted(previousBoard, gameVariables.getBotHoleCards()),
                        equityAction2.getOppLooseness(gameVariables.getOpponentName()),
                        equityAction2.getBotSizingGroup(previousRound.getSizing()),
                        previousBoard,
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppCallRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - B");
            }
        }
    }

    private void setInPositionFlopToTurnOrTurnToRiverRange(ContinuousTable continuousTable, GameVariables gameVariables,
                                                           DbSaveRaw previousRound, List<Card> previousBoard,
                                                           EquityAction2 equityAction2, RangeConstructor rangeConstructor) {
        if(previousRound.getBotAction().equals("check")) {
            if(gameVariables.getOpponentAction().equals("check") || gameVariables.getOpponentAction().equals("call")) {
                List<List<Card>> oppCheckRange = rangeConstructor.getOppPostflopCheckRange(
                        continuousTable.getOppRange(),
                        equityAction2.getAllCombosPostflopEquitySorted(gameVariables.getBoard(), gameVariables.getBotHoleCards()),
                        equityAction2.getOppAggroness(gameVariables.getOpponentName()),
                        equityAction2.getPotSizeGroup(gameVariables.getPot()),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppCheckRange);
            } else if(gameVariables.getOpponentAction().equals("bet75pct")) {
                List<List<Card>> oppBetRange = rangeConstructor.getOppPostflopBetRange(
                        continuousTable.getOppRange(),
                        equityAction2.getAllCombosPostflopEquitySorted(gameVariables.getBoard(), gameVariables.getBotHoleCards()),
                        equityAction2.getOppAggroness(gameVariables.getOpponentName()),
                        equityAction2.getOppSizingGroup(gameVariables.getOpponentBetSize()),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppBetRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - C");
            }
        } else if(previousRound.getBotAction().equals("bet75pct") || previousRound.getBotAction().equals("raise")) {
            List<List<Card>> previousStreetOppCallRange = rangeConstructor.getOppPostflopCallRange(
                    continuousTable.getOppRange(),
                    equityAction2.getAllCombosPostflopEquitySorted(previousBoard, gameVariables.getBotHoleCards()),
                    equityAction2.getOppLooseness(gameVariables.getOpponentName()),
                    equityAction2.getBotSizingGroup(previousRound.getSizing()),
                    previousBoard,
                    gameVariables.getBotHoleCards());

            if(gameVariables.getOpponentAction().equals("check")) {
                continuousTable.setOppRange(previousStreetOppCallRange);
            } else if(gameVariables.getOpponentAction().equals("bet75pct")) {
                List<List<Card>> oppDonkBetRange = rangeConstructor.getOppPostflopBetRange(
                        previousStreetOppCallRange,
                        equityAction2.getAllCombosPostflopEquitySorted(gameVariables.getBoard(), gameVariables.getBotHoleCards()),
                        equityAction2.getOppAggroness(gameVariables.getOpponentName()),
                        equityAction2.getOppSizingGroup(gameVariables.getOpponentBetSize()),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppDonkBetRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - E");
            }
        } else {
            System.out.println("Shouldn't come here, OpponentRangeSetter - D");
        }
    }
}
