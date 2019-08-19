package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.HandHistoryReaderStars;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * Created by LennartMac on 13/01/2019.
 */
public class DbSavePersisterRawData {

    private Connection con;
    List<String> lastHand = null;

    public void doBigDbSaveUpdate(ContinuousTable continuousTable, double biglind) throws Exception {
        List<DbSave> dbSaveList = continuousTable.getDbSaveList();

        initializeDbConnection();

        Statement st = con.createStatement();

        for(DbSave dbSave : dbSaveList) {
            if(dbSave instanceof DbSaveRaw) {
                DbSaveRaw dbSaveRaw = (DbSaveRaw) dbSave;

                st.executeUpdate("INSERT INTO dbstats_raw (" +
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
                    "VALUES ('" +
                    (getHighestIntEntry("dbstats_raw") + 1) + "', '" +
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
                    showdownOccurred(biglind) + "', '" +
                    botWonHand(biglind) + "', '" +
                    dbSaveRaw.getBigBlind() + "', '" +
                    dbSaveRaw.getStrongDraw() + "', '" +
                    ")");
            }
        }

        st.close();
        closeDbConnection();
    }

    private String botWonHand(double bigBlind) throws Exception {
        boolean botWonHand = false;

        if(lastHand == null) {
            HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();
            List<String> total = handHistoryReaderStars.readTextFile();
            lastHand = handHistoryReaderStars.getLinesOfLastGame(total, 1, bigBlind);
        }

        Collections.reverse(lastHand);

        for(String line : lastHand) {
            if(line.contains("vegeta11223 collected")) {
                botWonHand = true;
                break;
            }
        }

        return String.valueOf(botWonHand);
    }

    private String showdownOccurred(double bigBlind) throws Exception {
        boolean showdownOccurred = false;

        if(lastHand == null) {
            HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();
            List<String> total = handHistoryReaderStars.readTextFile();
            lastHand = handHistoryReaderStars.getLinesOfLastGame(total, 1, bigBlind);
        }

        Collections.reverse(lastHand);

        for(String line : lastHand) {
            if(line.contains("*** SHOW DOWN ***")) {
                showdownOccurred = true;
                break;
            }
        }

        return String.valueOf(showdownOccurred);
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
