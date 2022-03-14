package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0;

import com.lennart.model.handtracker.ActionRequest;
import com.lennart.model.handtracker.PlayerActionRound;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 12/03/2022.
 */
public class RecentHandsPersister {

    private Connection con;

    public void updateRecentHands(String opponentPlayerNameOfLastHand, boolean botWasButton,
                                               List<ActionRequest> allActionRequestsOfHand) throws Exception {
        List<String> opponentPreflopActions = new ArrayList<>();
        List<String> opponentPostflopActions = new ArrayList<>();

        for(ActionRequest actionRequest : allActionRequestsOfHand) {
            List<PlayerActionRound> actionsSinceLastRequest = actionRequest.getActionsSinceLastRequest();

            for(PlayerActionRound playerActionRound : actionsSinceLastRequest) {
                if(playerActionRound.getPlayerName().equals("opponent")) {
                    if(playerActionRound.getBoard() == null || playerActionRound.getBoard().isEmpty()) {
                        opponentPreflopActions.add(playerActionRound.getAction());
                    } else {
                        opponentPostflopActions.add(playerActionRound.getAction());
                    }
                }
            }
        }

        for(String action : opponentPreflopActions) {
            updateCountsInDb(opponentPlayerNameOfLastHand, action, "opponentidentifier_2_0_preflop", botWasButton);
        }

        for(String action : opponentPostflopActions) {
            updateCountsInDb(opponentPlayerNameOfLastHand, action, "opponentidentifier_2_0_postflop", botWasButton);
        }

        updateNumberOfHands(opponentPlayerNameOfLastHand);
    }



    //opponentidentifier_2_0_postflop_party

    //opponentidentifier_2_0_preflop_party

    //opponentidentifier_2_0_preflopstats_party
        //see: DbSavePersisterPreflopStats -> doDbSaveUpdate()


    public void updateRecentHandsPreflop(String oppName, String oppPreflopAction, boolean botWasButton) throws Exception {
        insertNewOppInTable(oppName, "dummyNameField", "dummyTableName");

        initializeDbConnection();

        if(oppPreflopAction.equals("call")) {



        } else if(oppPreflopAction.equals("raise")) {

        }
    }

    public void updateRecentHandsPostflop() {

    }

    public void updateRecentHandsPreflopStats() {

    }

    private void insertNewOppInTable(String oppName, String oppNameFieldInTable, String table) throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE " + oppNameFieldInTable + " = '" + oppName + "';");

        if(!rs.next()) {
            st.executeUpdate("INSERT INTO " + table + " (" + oppNameFieldInTable + ") VALUES ('" + oppName + "')");
        }

        rs.close();
        st.close();
        closeDbConnection();
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
