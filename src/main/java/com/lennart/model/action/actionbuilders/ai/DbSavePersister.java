package com.lennart.model.action.actionbuilders.ai;

import java.sql.*;
import java.util.List;

public class DbSavePersister {

    private Connection con;

    public void doDbSaveUpdate(ContinuousTable continuousTable, double biglind) throws Exception {
        List<DbSave> dbSaveList = continuousTable.getDbSaveList();

        initializeDbConnection();

        for(DbSave dbSave : dbSaveList) {
            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO zzz (" +
                    "entry, " +
                    "action, " +
                    "board, " +
                    "sizing, " +
                    "opp_fold_stat, " +
                    "bluff_success, " +
                    "stake, " +
                    "number_of_hands, " +
                    "opp_looseness, " +
                    "opp_aggressiveness, " +
                    "handstrength, " +
                    "opp_name, " +
                    "date, " +
                    "opp_type, " +
                    "showdown, " +
                    "won_hand) " +
                    "VALUES ('" +
                    getHighestIntEntry("zzz") + "', '" +
                    dbSave.getAction() + "', '" +
                    dbSave.getBoardAsString(dbSave.getBoard()) + "', '" +
                    dbSave.getSizing() + "', '" +
                    dbSave.getOppFoldStat() + "', '" +
                    dbSave.getBluffSuccessNumber() + "', '" +
                    dbSave.getStake() + "', '" +
                    dbSave.getNumberOfHands() + "', '" +
                    dbSave.getOppLooseness() + "', '" +
                    dbSave.getOppAggressiveness() + "', '" +
                    dbSave.getHandStrength() + "', '" +
                    dbSave.getOpponentName() + "', '" +
                    dbSave.getDate() + "', '" +
                    dbSave.getOppType() + "', '" +
                    showdownOccured(biglind) + "', '" +
                    botWonHand(biglind) + "' " +
                    ")");

            st.close();
        }

        closeDbConnection();
    }

    private boolean showdownOccured(double bigBlind) throws Exception {
        boolean showdownOccured = false;

        HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();
        List<String> total = handHistoryReaderStars.readTextFile();
        List<String> lastHand = handHistoryReaderStars.getLinesOfLastGame(total, 1, bigBlind);

        for(String line : lastHand) {
            if(line.contains("*** SHOW DOWN ***")) {
                showdownOccured = true;
                break;
            }
        }

        return showdownOccured;
    }

    private boolean botWonHand(double bigBlind) throws Exception {
        boolean botWonHand = false;

        HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();
        List<String> total = handHistoryReaderStars.readTextFile();
        List<String> lastHand = handHistoryReaderStars.getLinesOfLastGame(total, 1, bigBlind);

        for(String line : lastHand) {
            if(line.contains("vegeta11223 collected")) {
                botWonHand = true;
                break;
            }
        }

        return botWonHand;
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
