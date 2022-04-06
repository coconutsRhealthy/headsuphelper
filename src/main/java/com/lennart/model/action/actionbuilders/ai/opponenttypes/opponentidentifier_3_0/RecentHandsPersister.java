package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0;

import com.lennart.model.handtracker.ActionRequest;
import com.lennart.model.handtracker.PlayerActionRound;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by LennartMac on 12/03/2022.
 */
public class RecentHandsPersister {

    private static final int MAX_RECENT_HANDS = 50;

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

        updateRecentHandsPreflop(opponentPreflopActions, opponentPlayerNameOfLastHand, botWasButton);
    }

    //opponentidentifier_2_0_postflop_party

    //opponentidentifier_2_0_preflop_party

    //opponentidentifier_2_0_preflopstats_party
        //see: DbSavePersisterPreflopStats -> doDbSaveUpdate()

    private void updateRecentHandsPreflop(List<String> opponentPreflopActions, String oppName, boolean botWasButton) throws Exception {
        int callCount = Collections.frequency(opponentPreflopActions, "call");
        int raiseCount = Collections.frequency(opponentPreflopActions, "raise");

        insertNewOppInTable(oppName, "playerName", "opponentidentifier_3_0_preflop_party_rh");

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_3_0_preflop_party_rh WHERE playerName = '" + oppName + "';");

        rs.next();

        String currentCallString = rs.getString("callCount");
        String currentRaiseString = rs.getString("raiseCount");
        String currentPositionalRaiseString = botWasButton ? rs.getString("oopRaiseCount") : rs.getString("ipRaiseCount");

        rs.close();
        st.close();

        String newCallString = updateRecentHandsString(currentCallString, callCount);
        String newRaiseString = updateRecentHandsString(currentRaiseString, raiseCount);
        String newPositionalRaiseString = updateRecentHandsString(currentPositionalRaiseString, raiseCount);

        Statement st2 = con.createStatement();

        st2.executeUpdate("UPDATE opponentidentifier_3_0_preflop_party_rh SET callCount = '" + newCallString + "' WHERE playerName = '" + oppName + "'");
        st2.executeUpdate("UPDATE opponentidentifier_3_0_preflop_party_rh SET raiseCount = '" + newRaiseString + "' WHERE playerName = '" + oppName + "'");

        if(botWasButton) {
            st2.executeUpdate("UPDATE opponentidentifier_3_0_preflop_party_rh SET oopRaiseCount = '" + newPositionalRaiseString + "' WHERE playerName = '" + oppName + "'");
        } else {
            st2.executeUpdate("UPDATE opponentidentifier_3_0_preflop_party_rh SET ipRaiseCount = '" + newPositionalRaiseString + "' WHERE playerName = '" + oppName + "'");
        }

        st2.close();
        closeDbConnection();
    }

    private String updateRecentHandsString(String currentRecentHandsString, int newCount) {
        if(newCount > 9) {
            System.out.println("Very high action count in hand: " + newCount);
            newCount = 9;
        }

        String newRecentHandsString;

        if(currentRecentHandsString.length() > MAX_RECENT_HANDS) {
            newRecentHandsString = "_" + currentRecentHandsString.substring(2) + newCount;
        } else {
            newRecentHandsString = currentRecentHandsString + newCount;
        }

        return newRecentHandsString;
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
