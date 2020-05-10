package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSave;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveRaw;
import com.lennart.model.action.actionbuilders.ai.dbstatsraw.Analysis;
import com.lennart.model.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 10/05/2020.
 */
public class OpponentRangeSetter {

    private void setOpponentRange(ContinuousTable continuousTable, GameVariables gameVariables) {
        if(continuousTable.getDbSaveList().isEmpty()) {
            continuousTable.setOppRange(new RangeConstructor().createStartingOppRange(gameVariables.getBotHoleCards()));
        } else {
            setPostflopRange(continuousTable, gameVariables);
        }
    }

    private void setPostflopRange(ContinuousTable continuousTable, GameVariables gameVariables) {
        RangeConstructor rangeConstructor = new RangeConstructor();
        List<DbSave> dbSaveList = continuousTable.getDbSaveList();

        if(continuousTable.getOppRange() == null || continuousTable.getOppRange().isEmpty()) {
            System.out.println("Shouldn't come here, OpponentRangeSetter - A");
            continuousTable.setOppRange(rangeConstructor.createStartingOppRange(gameVariables.getBotHoleCards()));
        }

        if(gameVariables.getBoard() == null || gameVariables.getBoard().isEmpty()) {
            //preflop

        } else {
            List<DbSaveRaw> dbSaveRawList = new ArrayList<>();

            for(DbSave dbSave : dbSaveList) {
                if(dbSave instanceof DbSaveRaw) {
                    dbSaveRawList.add((DbSaveRaw) dbSave);
                }
            }

            DbSaveRaw previousRound = dbSaveRawList.get(dbSaveRawList.size() - 1);
            String previousBoardString = previousRound.getBoard();
            List<Card> previousBoard = new Analysis().convertCardStringToCardList(previousBoardString);
            List<Card> currentBoard = gameVariables.getBoard();

            EquityAction2 equityAction2 = new EquityAction2();

            if(previousBoard.equals(currentBoard)) {
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
            } else {
                //nieuwe straat
                if(previousBoard.isEmpty()) {
                    //vorige straat was preflop

                }



                //het kan zijn dat jij gecalled hebt en nieuwe straat
                //het kan zijn dat opp gecalled heeft en nieuwe straat
                //het kan zijn dat jij gecheckt hebt en nieuwe straat
                //het kan zijn dat opp gecheckt heeft en nieuwe straat
                if(gameVariables.getOpponentAction().equals("empty")) {
                    //je zit oop en er is nieuwe straat...
                    //als jouw laatste actie call was...
                    if(previousRound.getBotAction().equals("call")) {
                        //range staat al goed ingesteld..
                    } else if(previousRound.getBotAction().equals("check")) {
                        //if()

                    }




                    //laatste actie van opp is van vorige straat, ofwel check ofwel call...

                } else {
                    //je zit ip en er is nieuwe straat...
                    //je moet eerst de vorige actie van opp verwerken
                    //daarna de laatste actie..


                }


            }

            //nieuwe straat






        }


    }

    private void setPreflopRange() {

    }

}
