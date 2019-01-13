package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.HandHistoryReaderStars;

import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by LennartMac on 13/01/2019.
 */
public class DbSavePersisterRawData {

    private Connection con;

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
                    "position, " +
                    "stake, " +
                    "opponent_name, " +
                    "opponent_data, " +
                    "showdown_occured, " +
                    "bot_won_hand, " +
                    "bigblind, " +
                    "strongdraw) " +
                    "VALUES ('" +
                    getHighestIntEntry("dbstats_raw") + "', '" +
                    getCurrentDate() + "', '" +
                    dbSaveRaw.getBotAction() + "', '" +
                    dbSaveRaw.getOppAction() + "', '" +
                    dbSaveRaw.getBoard() + "', '" +
                    dbSaveRaw.getHoleCards() + "', '" +
                    dbSaveRaw.getHandStrength() + "', '" +
                    dbSaveRaw.getBotStack() + "', '" +
                    dbSaveRaw.getOpponentStack() + "', '" +
                    dbSaveRaw.getBotTotalBetSize() + "', '" +
                    dbSaveRaw.getOpponentTotalBetSize() + "', '" + dbSaveRaw.getPosition() + "', '" +
                    dbSaveRaw.getPosition() + "', '" +
                    dbSaveRaw.getStake() + "', '" +
                    dbSaveRaw.getOpponentName() + "', '" +
                    dbSaveRaw.getOpponentData() + "', '" +
                    showdownOccurred() + "', '" +
                    botWonHand(biglind) + "', '" +
                    dbSaveRaw.getBigBlind() + "', '" +
                    dbSaveRaw.getStrongDraw() + "', '" +
                    ")");
            }
        }

        st.close();
        closeDbConnection();
    }

    private boolean botWonHand(double bigBlind) throws Exception {
        boolean botWonHand = false;

        HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();
        List<String> total = handHistoryReaderStars.readTextFile();
        List<String> lastHand = handHistoryReaderStars.getLinesOfLastGame(total, 1, bigBlind);
        Collections.reverse(lastHand);

        for(String line : lastHand) {
            if(line.contains("vegeta11223 collected")) {
                botWonHand = true;
                break;
            }
        }

        return botWonHand;
    }

    private boolean showdownOccurred() {
        return false;
    }

    private int getHighestIntEntry(String database) throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM " + database + " ORDER BY entry DESC;");
        ResultSet rs = st.executeQuery(sql);

        if(rs.next()) {
            int highestIntEntry = rs.getInt("entry");
            st.close();
            rs.close();
            return highestIntEntry;
        }
        st.close();
        rs.close();
        return 0;
    }

    private String getCurrentDate() {
        return null;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
