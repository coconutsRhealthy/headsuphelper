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
            setPreflopRange(continuousTable, gameVariables);
        } else {
            setPostflopRange(continuousTable, gameVariables);
        }
    }

    private void setPreflopRange(ContinuousTable continuousTable, GameVariables gameVariables) {
        if(gameVariables.isBotIsButton()) {
            setInPositionPreflopRange(continuousTable, gameVariables);
        } else {
            setOopPreflopRange(continuousTable, gameVariables);
        }
    }

    private void setInPositionPreflopRange(ContinuousTable continuousTable, GameVariables gameVariables) {
        if(gameVariables.getOpponentAction().equals("bet")) {
            continuousTable.setOppRange(new RangeConstructor().createStartingOppRange(gameVariables.getBotHoleCards()));
        } else if(gameVariables.getOpponentAction().equals("raise")) {
            String oppRaiseType = determineOppPreflopRaiseType();

            if(oppRaiseType.equals("2bet")) {
                //dit betekent dat jij als bot gelimped hebt
                //todo: limp

            } else if(oppRaiseType.equals("3bet")) {
                List<List<Card>> oppPre3betRange = new RangeConstructor().getOppPre3betRange(
                        new PreflopEuityHs().getAllSortedPfEquityCombos(),
                        new EquityAction2().getOppPre3betGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppPre3betRange);
            } else if(oppRaiseType.equals("4bet_up")) {
                List<List<Card>> oppPre4betUpRange = new RangeConstructor().getOppPre4betUpRange(
                        new PreflopEuityHs().getAllSortedPfEquityCombos(),
                        new EquityAction2().getOppPre4betUpGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppPre4betUpRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - X");
            }
        } else {
            System.out.println("Shouldn't come here, OpponentRangeSetter - Y");
        }
    }

    private void setOopPreflopRange(ContinuousTable continuousTable, GameVariables gameVariables) {
        if(gameVariables.getOpponentAction().equals("call")) {
            List<List<Card>> oppPreLimpRange = new RangeConstructor().getOppPreLimpRange(
                    new PreflopEuityHs().getAllSortedPfEquityCombos(),
                    new EquityAction2().getOppPre2betGroup(gameVariables.getOpponentName()),
                    gameVariables.getBotHoleCards());
            continuousTable.setOppRange(oppPreLimpRange);
        } else if(gameVariables.getOpponentAction().equals("raise")) {
            String oppRaiseType = determineOppPreflopRaiseType();

            if(oppRaiseType.equals("2bet")) {
                List<List<Card>> oppPre2betRange = new RangeConstructor().getOppPre2betRange(
                        new PreflopEuityHs().getAllSortedPfEquityCombos(),
                        new EquityAction2().getOppPre2betGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppPre2betRange);
            } else if(oppRaiseType.equals("3bet")) {
                List<List<Card>> oppPre3betRange = new RangeConstructor().getOppPre3betRange(
                        new PreflopEuityHs().getAllSortedPfEquityCombos(),
                        new EquityAction2().getOppPre3betGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppPre3betRange);
            } else if(oppRaiseType.equals("4bet_up")) {
                List<List<Card>> oppPre4betUpRange = new RangeConstructor().getOppPre4betUpRange(
                        new PreflopEuityHs().getAllSortedPfEquityCombos(),
                        new EquityAction2().getOppPre4betUpGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppPre4betUpRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - W");
            }
        } else {
            System.out.println("Shouldn't come here, OpponentRangeSetter - T");
        }
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
            setPostflopNewStreetRange(continuousTable, gameVariables, previousBoard, previousRound);
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

    private void setPostflopNewStreetRange(ContinuousTable continuousTable, GameVariables gameVariables, List<Card> previousBoard, DbSaveRaw previousRound) {
        if(previousBoard.isEmpty()) {
            setPreflopToFlopRange(continuousTable, gameVariables, previousRound);
        } else {
            setFlopToTurnOrTurnToRiverRange(continuousTable, gameVariables, previousRound, previousBoard);
        }
    }

    private void setPreflopToFlopRange(ContinuousTable continuousTable, GameVariables gameVariables, DbSaveRaw previousRound) {
        if(gameVariables.isBotIsButton()) {
            setInPositionPreflopToFlopRange(continuousTable, gameVariables, previousRound);
        } else {
            setOopPreflopToFlopRange(continuousTable, gameVariables, previousRound);
        }
    }

    private void setInPositionPreflopToFlopRange(ContinuousTable continuousTable, GameVariables gameVariables, DbSaveRaw previousRound) {
        RangeConstructor rangeConstructor = new RangeConstructor();
        EquityAction2 equityAction2 = new EquityAction2();

        if(previousRound.getBotAction().equals("raise")) {
            String botRaiseType = determinBotPreflopRaiseType();
            PreflopEuityHs preflopEuityHs = new PreflopEuityHs();

            List<List<Card>> oppPreCallRange = null;

            if(botRaiseType.equals("2bet")) {
                oppPreCallRange = rangeConstructor.getOppPreCall2betRange(
                    preflopEuityHs.getAllSortedPfEquityCombos(),
                    equityAction2.getOppPreCall2betGroup(gameVariables.getOpponentName()),
                    gameVariables.getBotHoleCards());
            } else if(botRaiseType.equals("3bet")) {
                oppPreCallRange = rangeConstructor.getOppPreCall3betRange(
                    preflopEuityHs.getAllSortedPfEquityCombos(),
                    equityAction2.getOppPreCall3betGroup(gameVariables.getOpponentName()),
                    gameVariables.getBotHoleCards());
            } else if(botRaiseType.equals("4bet")) {
                oppPreCallRange = rangeConstructor.getOppPreCall4betUpRange(
                    preflopEuityHs.getAllSortedPfEquityCombos(),
                    equityAction2.getOppPreCall4betUpGroup(gameVariables.getOpponentName()),
                    gameVariables.getBotHoleCards());
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - G");
            }

            if(gameVariables.getOpponentAction().equals("check")) {
                continuousTable.setOppRange(oppPreCallRange);
            } else if(gameVariables.getOpponentAction().equals("bet75pct")) {
                List<List<Card>> oppFlopBetRange = rangeConstructor.getOppPostflopBetRange(
                        oppPreCallRange,
                        equityAction2.getAllCombosPostflopEquitySorted(gameVariables.getBoard(), gameVariables.getBotHoleCards()),
                        equityAction2.getOppAggroness(gameVariables.getOpponentName()),
                        equityAction2.getOppSizingGroup(gameVariables.getOpponentBetSize()),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppFlopBetRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - G");
            }
        } else if(previousRound.getBotAction().equals("call")) {
            //Todo: bot limp

            if(gameVariables.getOpponentAction().equals("check")) {
                List<List<Card>> oppFlopCheckRange = rangeConstructor.getOppPostflopCheckRange(
                        continuousTable.getOppRange(),
                        new EquityAction2().getAllCombosPostflopEquitySorted(gameVariables.getBoard(), gameVariables.getBotHoleCards()),
                        new EquityAction2().getOppAggroness(gameVariables.getOpponentName()),
                        new EquityAction2().getPotSizeGroup(gameVariables.getPot()),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppFlopCheckRange);
            } else if(gameVariables.getOpponentAction().equals("bet75pct")) {
                List<List<Card>> oppBetRange = rangeConstructor.getOppPostflopBetRange(
                        continuousTable.getOppRange(),
                        new EquityAction2().getAllCombosPostflopEquitySorted(gameVariables.getBoard(), gameVariables.getBotHoleCards()),
                        new EquityAction2().getOppAggroness(gameVariables.getOpponentName()),
                        new EquityAction2().getOppSizingGroup(gameVariables.getOpponentBetSize()),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppBetRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - M");
            }
        } else {
            System.out.println("Shouldn't come here, OpponentRangeSetter - G");
        }
    }

    private void setOopPreflopToFlopRange(ContinuousTable continuousTable, GameVariables gameVariables, DbSaveRaw previousRound) {
        if(previousRound.getBotAction().equals("check")) {
            continuousTable.setOppRange(continuousTable.getOppRange());
        } else if(previousRound.getBotAction().equals("raise")) {
            RangeConstructor rangeConstructor = new RangeConstructor();
            EquityAction2 equityAction2 = new EquityAction2();
            PreflopEuityHs preflopEuityHs = new PreflopEuityHs();

            String botRaiseType = determinBotPreflopRaiseType();

            List<List<Card>> oppPreCallRange = null;

            if(botRaiseType.equals("2bet")) {
                oppPreCallRange = rangeConstructor.getOppPreCall2betRange(
                        preflopEuityHs.getAllSortedPfEquityCombos(),
                        equityAction2.getOppPreCall2betGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
            } else if(botRaiseType.equals("3bet")) {
                oppPreCallRange = rangeConstructor.getOppPreCall3betRange(
                        preflopEuityHs.getAllSortedPfEquityCombos(),
                        equityAction2.getOppPreCall3betGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
            } else if(botRaiseType.equals("4bet")) {
                oppPreCallRange = rangeConstructor.getOppPreCall4betUpRange(
                        preflopEuityHs.getAllSortedPfEquityCombos(),
                        equityAction2.getOppPreCall4betUpGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - I");
            }

            continuousTable.setOppRange(oppPreCallRange);
        } else {
            System.out.println("Shouldn't come here, OpponentRangeSetter - K");
        }
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

    private String determinBotPreflopRaiseType() {
        return null;
    }

    private String determineOppPreflopRaiseType() {
        return null;
    }
}
