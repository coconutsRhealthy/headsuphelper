package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by LennartMac on 13/01/2019.
 */
public class DbSavePersisterRawData {

    private Connection con;

    public void doBigDbSaveUpdate(ContinuousTable continuousTable) throws Exception {
        List<DbSave> dbSaveList = continuousTable.getDbSaveList();

        initializeDbConnection();

        Statement st = con.createStatement();

        int highestIntEntry = getHighestIntEntry("dbstats_raw_19");

        for(DbSave dbSave : dbSaveList) {
            if(dbSave instanceof DbSaveRaw) {
                highestIntEntry++;

                DbSaveRaw dbSaveRaw = (DbSaveRaw) dbSave;

                String showdownOccured = showdownOccurred();

                if(continuousTable.isBotDidPre4bet() && showdownOccured.equals("true")) {
                    updateOppDidPreCall4betInDb(dbSaveRaw.getOpponentName());
                    continuousTable.setBotDidPre4bet(false);
                }

                st.executeUpdate("INSERT INTO dbstats_raw_19 (" +
                    "entry, " +
                    "date, " +
                    "bot_action, " +
                    "opponent_action, " +
                    "board, " +
                    "holecards, " +
                    "handstrength, " +
                    "botstack, " +
                    "opponentstack, " +
                    "bot_total_betsize, " +
                    "opponent_total_betsize, " +
                    "sizing, " +
                    "position, " +
                    "stake, " +
                    "opponent_name, " +
                    "opponent_data, " +
                    "showdown_occured, " +
                    "bot_won_hand, " +
                    "bigblind, " +
                    "strongdraw, " +
                    "recent_hands_won, " +
                    "adjusted_opp_type, " +
                    "opp_holecards, " +
                    "pot, " +
                    "equity) " +
                    "VALUES ('" +
                    highestIntEntry + "', '" +
                    getCurrentDate() + "', '" +
                    dbSaveRaw.getBotAction() + "', '" +
                    dbSaveRaw.getOppAction() + "', '" +
                    dbSaveRaw.getBoard() + "', '" +
                    dbSaveRaw.getHoleCards() + "', '" +
                    dbSaveRaw.getHandStrength() + "', '" +
                    dbSaveRaw.getBotStack() + "', '" +
                    dbSaveRaw.getOpponentStack() + "', '" +
                    dbSaveRaw.getBotTotalBetSize() + "', '" +
                    dbSaveRaw.getOpponentTotalBetSize() + "', '" +
                    dbSaveRaw.getSizing() + "', '" +
                    dbSaveRaw.getPosition() + "', '" +
                    dbSaveRaw.getStake() + "', '" +
                    dbSaveRaw.getOpponentName() + "', '" +
                    dbSaveRaw.getOpponentData() + "', '" +
                    showdownOccured + "', '" +
                    botWonHand() + "', '" +
                    dbSaveRaw.getBigBlind() + "', '" +
                    dbSaveRaw.getStrongDraw() + "', '" +
                    dbSaveRaw.getRecentHandsWon() + "', '" +
                    dbSaveRaw.getAdjustedOppType() + "', '" +
                    getOpponentHolecards() + "', '" +
                    dbSaveRaw.getPot() + "', '" +
                    dbSaveRaw.getEquity() + "'" +
                    ")");
            }
        }

        st.close();
        closeDbConnection();
    }

    private String botWonHand() throws Exception {
        //Default false on Party
        return "false";
    }

    private String getOpponentHolecards() throws Exception {
        //Default 2d3d on Party
        return "2d3d";
    }

    private String showdownOccurred() throws Exception {
        //Default false on Party
        return "false";
    }

    private int getHighestIntEntry(String table) throws Exception {
        int highestIntEntry = 0;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table + " ORDER BY entry DESC;");

        if(rs.next()) {
            highestIntEntry = rs.getInt("entry");
        }

        st.close();
        rs.close();

        closeDbConnection();

        return highestIntEntry;
    }

    private String getCurrentDate() {
        java.util.Date date = new java.util.Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    private void updateOppDidPreCall4betInDb(String opponentName) throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();

        st.executeUpdate(
                "UPDATE opponentidentifier_2_0_preflopstats SET pre_call4bet_up = pre_call4bet_up + 1" +
                        " WHERE playerName = '" + opponentName + "'");

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
