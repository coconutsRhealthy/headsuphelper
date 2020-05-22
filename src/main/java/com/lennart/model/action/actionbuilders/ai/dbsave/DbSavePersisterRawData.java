package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.HandHistoryReaderStars;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        int highestIntEntry = getHighestIntEntry("dbstats_raw");

        for(DbSave dbSave : dbSaveList) {
            if(dbSave instanceof DbSaveRaw) {
                highestIntEntry++;

                DbSaveRaw dbSaveRaw = (DbSaveRaw) dbSave;

                String showdownOccured = showdownOccurred(biglind);

                if(continuousTable.isBotDidPre4bet() && showdownOccured.equals("true")) {
                    updateOppDidPreCall4betInDb(dbSaveRaw.getOpponentName());
                    continuousTable.setBotDidPre4bet(false);
                }

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
                    "recent_hands_won, " +
                    "adjusted_opp_type, " +
                    "opp_holecards, " +
                    "pot) " +
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
                    botWonHand(biglind) + "', '" +
                    dbSaveRaw.getBigBlind() + "', '" +
                    dbSaveRaw.getStrongDraw() + "', '" +
                    dbSaveRaw.getRecentHandsWon() + "', '" +
                    dbSaveRaw.getAdjustedOppType() + "', '" +
                    getOpponentHolecards(showdownOccured, biglind) + "', '" +
                    dbSaveRaw.getPot() + "'" +
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

    private String getOpponentHolecards(String showDownOccured, double bigBlind) throws Exception {
        String opponentHolecards = "";

        if(showDownOccured.equals("true")) {
            System.out.println("showDownOccured true, we are logging opp holecards :)");

            if(lastHand == null) {
                HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();
                List<String> total = handHistoryReaderStars.readTextFile();
                lastHand = handHistoryReaderStars.getLinesOfLastGame(total, 1, bigBlind);
            }

            Collections.reverse(lastHand);

            List<String> relevantLines = lastHand.stream()
                    .filter(line -> ((line.contains("showed") || line.contains("mucked")) && !line.contains("vegeta11223")))
                    .collect(Collectors.toList());

            if(relevantLines.size() == 1) {
                String workingString = relevantLines.get(0);

                if(workingString.contains("showed")) {
                    workingString = workingString.substring(workingString.indexOf("showed"));
                    workingString = workingString.replace("showed", "");
                } else {
                    workingString = workingString.substring(workingString.indexOf("mucked"));
                    workingString = workingString.replace("mucked", "");
                }

                workingString = workingString.substring(0, workingString.indexOf("]"));
                workingString = workingString.replace("[", "");
                workingString = workingString.replace("]", "");
                workingString = workingString.replace(" ", "");

                opponentHolecards = workingString;
            } else {
                System.out.println("wtf, relevant opp showdownlines bigger than 1? " + relevantLines.size());
                System.out.println("line 1: " + relevantLines.get(0));
                System.out.println("line 2: " + relevantLines.get(1));
            }
        }

        return opponentHolecards;
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
