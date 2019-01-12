package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0;

import com.lennart.model.action.actionbuilders.ai.HandHistoryReaderStars;

import java.sql.*;
import java.util.List;

/**
 * Created by LennartMac on 12/01/2019.
 */
public class OpponentIdentifier2_0 {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new OpponentIdentifier2_0().updateOpponentIdentifier2_0_db("sjaakie", 20);
    }

    public void updateOpponentIdentifier2_0_db(String opponentPlayerNameOfLastHand, double bigBlind) throws Exception {
        HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();

        List<String> opponentPreflopActions = handHistoryReaderStars.getOpponentActionsOfLastHand(false, bigBlind);

        for(String action : opponentPreflopActions) {
            updateCountsInDb(opponentPlayerNameOfLastHand, action, "opponentidentifier_2_0_preflop");
        }

        List<String> opponentPostflopActions = handHistoryReaderStars.getOpponentActionsOfLastHand(true, bigBlind);

        for(String action : opponentPostflopActions) {
            updateCountsInDb(opponentPlayerNameOfLastHand, action, "opponentidentifier_2_0_postflop");
        }
    }

    private void updateCountsInDb(String opponentNick, String action, String table) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
              ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE playerName = '" + opponentNick + "';");

        if(!rs.next()) {
            st.executeUpdate("INSERT INTO " + table + " (playerName) VALUES ('" + opponentNick + "')");
        }

        if(action.equals("fold")) {
            st.executeUpdate("UPDATE " + table + " SET foldCount = foldCount + 1 WHERE playerName = '" + opponentNick + "'");
        } else if(action.equals("check")) {
            st.executeUpdate("UPDATE " + table + " SET checkCount = checkCount + 1 WHERE playerName = '" + opponentNick + "'");
        } else if(action.equals("call")) {
            st.executeUpdate("UPDATE " + table + " SET callCount = callCount + 1 WHERE playerName = '" + opponentNick + "'");
        } else if(action.equals("bet75pct")) {
            st.executeUpdate("UPDATE " + table + " SET betCount = betCount + 1 WHERE playerName = '" + opponentNick + "'");
        } else if(action.equals("raise")) {
            st.executeUpdate("UPDATE " + table + " SET raiseCount = raiseCount + 1 WHERE playerName = '" + opponentNick + "'");
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
