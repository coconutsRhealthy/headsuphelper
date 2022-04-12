package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSave;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePreflopStats;
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
        updateRecentHandsPostflop(opponentPostflopActions, opponentPlayerNameOfLastHand);
    }

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

    public void updateRecentHandsPostflop(List<String> opponentPostflopActions, String oppName) throws Exception {
        int checkCount = Collections.frequency(opponentPostflopActions, "check");
        int callCount = Collections.frequency(opponentPostflopActions, "call");
        int betCount = Collections.frequency(opponentPostflopActions, "bet75pct");
        int raiseCount = Collections.frequency(opponentPostflopActions, "raise");

        insertNewOppInTable(oppName, "playerName", "opponentidentifier_3_0_postflop_party_rh");

        initializeDbConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_3_0_postflop_party_rh WHERE playerName = '" + oppName + "';");

        rs.next();

        String currentCheckString = rs.getString("checkCount");
        String currentCallString = rs.getString("callCount");
        String currentBetString = rs.getString("betCount");
        String currentRaiseString = rs.getString("raiseCount");

        rs.close();
        st.close();

        String newCheckString = updateRecentHandsString(currentCheckString, checkCount);
        String newCallString = updateRecentHandsString(currentCallString, callCount);
        String newBetString = updateRecentHandsString(currentBetString, betCount);
        String newRaiseString = updateRecentHandsString(currentRaiseString, raiseCount);

        Statement st2 = con.createStatement();

        st2.executeUpdate("UPDATE opponentidentifier_3_0_postflop_party_rh SET checkCount = '" + newCheckString + "' WHERE playerName = '" + oppName + "'");
        st2.executeUpdate("UPDATE opponentidentifier_3_0_postflop_party_rh SET callCount = '" + newCallString + "' WHERE playerName = '" + oppName + "'");
        st2.executeUpdate("UPDATE opponentidentifier_3_0_postflop_party_rh SET betCount = '" + newBetString + "' WHERE playerName = '" + oppName + "'");
        st2.executeUpdate("UPDATE opponentidentifier_3_0_postflop_party_rh SET raiseCount = '" + newRaiseString + "' WHERE playerName = '" + oppName + "'");

        st2.close();
        closeDbConnection();
    }

    public void updateRecentHandsPreflopStats(ContinuousTable continuousTable) throws Exception {
        List<DbSave> dbSaveList = continuousTable.getDbSaveList();

        double totalPre2betCountLastHand = 0;
        double totalPre3betCountLastHand = 0;
        double totalPre4bet_up_countLastHand = 0;
        double totalPreCall2betCountLastHand = 0;

        String opponentName = null;

        for(DbSave dbSave : dbSaveList) {
            if(dbSave instanceof DbSavePreflopStats) {
                DbSavePreflopStats dbSavePreflopStats = (DbSavePreflopStats) dbSave;

                totalPre2betCountLastHand = totalPre2betCountLastHand + dbSavePreflopStats.getOppPre2betCount();
                totalPre3betCountLastHand = totalPre3betCountLastHand + dbSavePreflopStats.getOppPre3betCount();
                totalPre4bet_up_countLastHand = totalPre4bet_up_countLastHand + dbSavePreflopStats.getOppPre4bet_up_count();
                totalPreCall2betCountLastHand = totalPreCall2betCountLastHand + dbSavePreflopStats.getOppPreCall2betCount();

                if(opponentName == null) {
                    opponentName = dbSavePreflopStats.getOpponentName();
                }
            }
        }

        insertNewOppInTable(opponentName, "playerName", "opponentidentifier_3_0_preflopstats_party_rh");

        initializeDbConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_3_0_preflopstats_party_rh WHERE playerName = '" + opponentName + "';");

        rs.next();

        String currentPre2betString = rs.getString("pre2bet");
        String currentPre3betString = rs.getString("pre3bet");
        String currentPre4betUpString = rs.getString("pre4bet_up");
        String currentPreCall2betString = rs.getString("pre_call2bet");

        rs.close();
        st.close();

        String newPre2betString = updateRecentHandsString(currentPre2betString, (int) totalPre2betCountLastHand);
        String newPre3betString = updateRecentHandsString(currentPre3betString, (int) totalPre3betCountLastHand);
        String newPre4betUpString = updateRecentHandsString(currentPre4betUpString, (int) totalPre4bet_up_countLastHand);
        String newPreCall2betString = updateRecentHandsString(currentPreCall2betString,(int) totalPreCall2betCountLastHand);

        Statement st2 = con.createStatement();

        st2.executeUpdate("UPDATE opponentidentifier_3_0_preflopstats_party_rh SET pre2bet = '" + newPre2betString + "' WHERE playerName = '" + opponentName + "'");
        st2.executeUpdate("UPDATE opponentidentifier_3_0_preflopstats_party_rh SET pre3bet = '" + newPre3betString + "' WHERE playerName = '" + opponentName + "'");
        st2.executeUpdate("UPDATE opponentidentifier_3_0_preflopstats_party_rh SET pre4bet_up = '" + newPre4betUpString + "' WHERE playerName = '" + opponentName + "'");
        st2.executeUpdate("UPDATE opponentidentifier_3_0_preflopstats_party_rh SET pre_call2bet = '" + newPreCall2betString + "' WHERE playerName = '" + opponentName + "'");

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
