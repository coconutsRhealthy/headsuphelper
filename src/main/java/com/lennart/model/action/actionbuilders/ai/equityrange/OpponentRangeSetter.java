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

    private RangeConstructor rangeConstructor;
    private EquityAction equityAction;
    private PreflopEquityHs preflopEquityHs;
    private InputProvider inputProvider;

    public OpponentRangeSetter(InputProvider inputProvider) {
        this.rangeConstructor = new RangeConstructor();
        this.preflopEquityHs = new PreflopEquityHs();
        this.inputProvider = inputProvider;
        this.equityAction = new EquityAction(inputProvider, preflopEquityHs, rangeConstructor);
    }

    public void setOpponentRange(ContinuousTable continuousTable, GameVariables gameVariables) {
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
            continuousTable.setOppRange(rangeConstructor.createStartingOppRange(gameVariables.getBotHoleCards()));
        } else if(gameVariables.getOpponentAction().equals("raise")) {
            String oppRaiseType = inputProvider.determineOppPreflopRaiseType(-1);

            if(oppRaiseType.equals("2bet")) {
                List<List<Card>> oppPreRaiseAgainstLimpRange = rangeConstructor.getOppPreRaiseAgainstLimpRange(
                        preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPre2betGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppPreRaiseAgainstLimpRange);
            } else if(oppRaiseType.equals("3bet")) {
                List<List<Card>> oppPre3betRange = rangeConstructor.getOppPre3betRange(
                        preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPre3betGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppPre3betRange);
            } else if(oppRaiseType.equals("4bet_up")) {
                List<List<Card>> oppPre4betUpRange = rangeConstructor.getOppPre4betUpRange(
                        preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPre4betUpGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppPre4betUpRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - 1");
            }
        } else {
            System.out.println("Shouldn't come here, OpponentRangeSetter - 2");
        }
    }

    private void setOopPreflopRange(ContinuousTable continuousTable, GameVariables gameVariables) {
        if(gameVariables.getOpponentAction().equals("call")) {
            List<List<Card>> oppPreLimpRange = rangeConstructor.getOppPreLimpRange(
                    preflopEquityHs.getAllSortedPfEquityCombos(),
                    inputProvider.getOppPre2betGroup(gameVariables.getOpponentName()),
                    gameVariables.getBotHoleCards());
            continuousTable.setOppRange(oppPreLimpRange);
        } else if(gameVariables.getOpponentAction().equals("raise")) {
            String oppRaiseType = inputProvider.determineOppPreflopRaiseType(-1);

            if(oppRaiseType.equals("2bet")) {
                List<List<Card>> oppPre2betRange = rangeConstructor.getOppPre2betRange(
                        preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPre2betGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppPre2betRange);
            } else if(oppRaiseType.equals("3bet")) {
                List<List<Card>> oppPre3betRange = rangeConstructor.getOppPre3betRange(
                        preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPre3betGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppPre3betRange);
            } else if(oppRaiseType.equals("4bet_up")) {
                List<List<Card>> oppPre4betUpRange = rangeConstructor.getOppPre4betUpRange(
                        preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPre4betUpGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppPre4betUpRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - 3");
            }
        } else {
            System.out.println("Shouldn't come here, OpponentRangeSetter - 4");
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
        if(gameVariables.getOpponentAction().equals("bet75pct")) {
            List<List<Card>> oppBetRange = rangeConstructor.getOppPostflopBetRange(
                    continuousTable.getOppRange(),
                    equityAction.getAllCombosPostflopEquitySorted(continuousTable, gameVariables.getBoard(),
                            gameVariables.getBotHoleCards()),
                    inputProvider.getOppPostAggroness(gameVariables.getOpponentName()),
                    inputProvider.getOppSizingGroup(gameVariables.getOpponentBetSize()),
                    gameVariables.getBoard(),
                    gameVariables.getBotHoleCards());
            continuousTable.setOppRange(oppBetRange);
        } else if(gameVariables.getOpponentAction().equals("raise")) {
            List<List<Card>> oppRaiseRange = rangeConstructor.getOppPostflopRaiseRange(
                    continuousTable.getOppRange(),
                    equityAction.getAllCombosPostflopEquitySorted(continuousTable, gameVariables.getBoard(),
                            gameVariables.getBotHoleCards()),
                    inputProvider.getOppPostAggroness(gameVariables.getOpponentName()),
                    inputProvider.getOppSizingGroup(gameVariables.getOpponentBetSize()),
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
        if(previousRound.getBotAction().equals("raise")) {
            String botRaiseType = inputProvider.determinBotPreflopRaiseType(-1);

            List<List<Card>> oppPreCallRange = null;

            if(botRaiseType.equals("2bet")) {
                oppPreCallRange = rangeConstructor.getOppPreCall2betRange(
                    preflopEquityHs.getAllSortedPfEquityCombos(),
                    inputProvider.getOppPreCall2betGroup(gameVariables.getOpponentName()),
                    gameVariables.getBotHoleCards());
            } else if(botRaiseType.equals("3bet")) {
                oppPreCallRange = rangeConstructor.getOppPreCall3betRange(
                    preflopEquityHs.getAllSortedPfEquityCombos(),
                    inputProvider.getOppPreCall3betGroup(gameVariables.getOpponentName()),
                    gameVariables.getBotHoleCards());
            } else if(botRaiseType.equals("4bet")) {
                oppPreCallRange = rangeConstructor.getOppPreCall4betUpRange(
                    preflopEquityHs.getAllSortedPfEquityCombos(),
                    inputProvider.getOppPreCall4betUpGroup(gameVariables.getOpponentName()),
                    gameVariables.getBotHoleCards());
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - 5");
            }

            if(gameVariables.getOpponentAction().equals("check")) {
                continuousTable.setOppRange(oppPreCallRange);
            } else if(gameVariables.getOpponentAction().equals("bet75pct")) {
                List<List<Card>> oppFlopBetRange = rangeConstructor.getOppPostflopBetRange(
                        oppPreCallRange,
                        equityAction.getAllCombosPostflopEquitySorted(continuousTable, gameVariables.getBoard(),
                                gameVariables.getBotHoleCards()),
                        inputProvider.getOppPostAggroness(gameVariables.getOpponentName()),
                        inputProvider.getOppSizingGroup(gameVariables.getOpponentBetSize()),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppFlopBetRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - 6");
            }
        } else if(previousRound.getBotAction().equals("call")) {
            List<List<Card>> rangeToUse;

            if(previousRound.getOppAction().equals("bet")) {
                List<List<Card>> oppPreCheckAgainstLimpRange = rangeConstructor.getOppPreCheckAgainstLimpRange(
                        preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPre2betGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());

                rangeToUse = oppPreCheckAgainstLimpRange;
            } else {
                rangeToUse = continuousTable.getOppRange();
            }

            if(gameVariables.getOpponentAction().equals("check")) {
                List<List<Card>> oppFlopCheckRange = rangeConstructor.getOppPostflopCheckRange(
                        rangeToUse,
                        equityAction.getAllCombosPostflopEquitySorted(continuousTable, gameVariables.getBoard(),
                                gameVariables.getBotHoleCards()),
                        inputProvider.getOppPostAggroness(gameVariables.getOpponentName()),
                        inputProvider.getPotSizeGroup(gameVariables.getPot()),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppFlopCheckRange);
            } else if(gameVariables.getOpponentAction().equals("bet75pct")) {
                List<List<Card>> oppBetRange = rangeConstructor.getOppPostflopBetRange(
                        rangeToUse,
                        equityAction.getAllCombosPostflopEquitySorted(continuousTable, gameVariables.getBoard(),
                                gameVariables.getBotHoleCards()),
                        inputProvider.getOppPostAggroness(gameVariables.getOpponentName()),
                        inputProvider.getOppSizingGroup(gameVariables.getOpponentBetSize()),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppBetRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - 7");
            }
        } else {
            System.out.println("Shouldn't come here, OpponentRangeSetter - 8");
        }
    }

    private void setOopPreflopToFlopRange(ContinuousTable continuousTable, GameVariables gameVariables, DbSaveRaw previousRound) {
        if(previousRound.getBotAction().equals("check")) {
            continuousTable.setOppRange(continuousTable.getOppRange());
        } else if(previousRound.getBotAction().equals("raise")) {
            String botRaiseType = inputProvider.determinBotPreflopRaiseType(-1);

            List<List<Card>> oppPreCallRange = null;

            if(botRaiseType.equals("2bet")) {
                oppPreCallRange = rangeConstructor.getOppPreCall2betRange(
                        preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPreCall2betGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
            } else if(botRaiseType.equals("3bet")) {
                oppPreCallRange = rangeConstructor.getOppPreCall3betRange(
                        preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPreCall3betGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
            } else if(botRaiseType.equals("4bet")) {
                oppPreCallRange = rangeConstructor.getOppPreCall4betUpRange(
                        preflopEquityHs.getAllSortedPfEquityCombos(),
                        inputProvider.getOppPreCall4betUpGroup(gameVariables.getOpponentName()),
                        gameVariables.getBotHoleCards());
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - 9");
            }

            continuousTable.setOppRange(oppPreCallRange);
        } else {
            System.out.println("Shouldn't come here, OpponentRangeSetter - 10");
        }
    }

    private void setFlopToTurnOrTurnToRiverRange(ContinuousTable continuousTable, GameVariables gameVariables,
                                                 DbSaveRaw previousRound, List<Card> previousBoard) {
        if(gameVariables.isBotIsButton()) {
            setInPositionFlopToTurnOrTurnToRiverRange(continuousTable, gameVariables, previousRound, previousBoard);
        } else {
            if(previousRound.getBotAction().equals("check")) {
                List<List<Card>> oppCheckRange = rangeConstructor.getOppPostflopCheckRange(
                        continuousTable.getOppRange(),
                        equityAction.getAllCombosPostflopEquitySorted(continuousTable, previousBoard,
                                gameVariables.getBotHoleCards()),
                        inputProvider.getOppPostAggroness(gameVariables.getOpponentName()),
                        inputProvider.getPotSizeGroup(previousRound.getPot()),
                        previousBoard,
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppCheckRange);
            } else if(previousRound.getBotAction().equals("bet75pct") || previousRound.getBotAction().equals("raise")) {
                List<List<Card>> oppCallRange = rangeConstructor.getOppPostflopCallRange(
                        continuousTable.getOppRange(),
                        equityAction.getAllCombosPostflopEquitySorted(continuousTable, previousBoard,
                                gameVariables.getBotHoleCards()),
                        inputProvider.getOppPostLooseness(gameVariables.getOpponentName()),
                        inputProvider.getBotSizingGroup(previousRound.getSizing()),
                        previousBoard,
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppCallRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - 11");
            }
        }
    }

    private void setInPositionFlopToTurnOrTurnToRiverRange(ContinuousTable continuousTable, GameVariables gameVariables,
                                                           DbSaveRaw previousRound, List<Card> previousBoard) {
        if(previousRound.getBotAction().equals("check")) {
            if(gameVariables.getOpponentAction().equals("check") || gameVariables.getOpponentAction().equals("call")) {
                List<List<Card>> oppCheckRange = rangeConstructor.getOppPostflopCheckRange(
                        continuousTable.getOppRange(),
                        equityAction.getAllCombosPostflopEquitySorted(continuousTable, gameVariables.getBoard(),
                                gameVariables.getBotHoleCards()),
                        inputProvider.getOppPostAggroness(gameVariables.getOpponentName()),
                        inputProvider.getPotSizeGroup(gameVariables.getPot()),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppCheckRange);
            } else if(gameVariables.getOpponentAction().equals("bet75pct")) {
                List<List<Card>> oppBetRange = rangeConstructor.getOppPostflopBetRange(
                        continuousTable.getOppRange(),
                        equityAction.getAllCombosPostflopEquitySorted(continuousTable, gameVariables.getBoard(),
                                gameVariables.getBotHoleCards()),
                        inputProvider.getOppPostAggroness(gameVariables.getOpponentName()),
                        inputProvider.getOppSizingGroup(gameVariables.getOpponentBetSize()),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppBetRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - 12");
            }
        } else if(previousRound.getBotAction().equals("bet75pct") || previousRound.getBotAction().equals("raise")) {
            List<List<Card>> previousStreetOppCallRange = rangeConstructor.getOppPostflopCallRange(
                    continuousTable.getOppRange(),
                    equityAction.getAllCombosPostflopEquitySorted(continuousTable, previousBoard,
                            gameVariables.getBotHoleCards()),
                    inputProvider.getOppPostLooseness(gameVariables.getOpponentName()),
                    inputProvider.getBotSizingGroup(previousRound.getSizing()),
                    previousBoard,
                    gameVariables.getBotHoleCards());

            if(gameVariables.getOpponentAction().equals("check")) {
                continuousTable.setOppRange(previousStreetOppCallRange);
            } else if(gameVariables.getOpponentAction().equals("bet75pct")) {
                List<List<Card>> oppDonkBetRange = rangeConstructor.getOppPostflopBetRange(
                        previousStreetOppCallRange,
                        equityAction.getAllCombosPostflopEquitySorted(continuousTable, gameVariables.getBoard(),
                                gameVariables.getBotHoleCards()),
                        inputProvider.getOppPostAggroness(gameVariables.getOpponentName()),
                        inputProvider.getOppSizingGroup(gameVariables.getOpponentBetSize()),
                        gameVariables.getBoard(),
                        gameVariables.getBotHoleCards());
                continuousTable.setOppRange(oppDonkBetRange);
            } else {
                System.out.println("Shouldn't come here, OpponentRangeSetter - 13");
            }
        } else {
            System.out.println("Shouldn't come here, OpponentRangeSetter - 14");
        }
    }
}
