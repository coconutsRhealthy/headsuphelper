package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0;

import com.lennart.model.action.actionbuilders.ai.HandHistoryReaderStars;
import com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0.DbSaveBluff_2_0;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 12/01/2019.
 */
public class OpponentIdentifier2_0 {

    private Connection con;

    private void testMethod() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop;");

        while(rs.next()) {
            String opponentName = rs.getString("playerName");

            DbSaveBluff_2_0 dbSaveBluff_2_0 = new DbSaveBluff_2_0();

            String preflopType = dbSaveBluff_2_0.getOpponentPreflopTypeLogic(opponentName, true);
            String postflopType = dbSaveBluff_2_0.getOpponentPostflopTypeLogic(opponentName, true);

            System.out.println(preflopType + postflopType);
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private void printLoosenessAndTightnessBoundries(boolean includingMedium, String table) throws Exception {
        List<Double> allLooseness = new ArrayList<>();
        List<Double> allAggro = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table + ";");

        while(rs.next()) {
            if(rs.getDouble("numberOfHands") >= 20) {
                double callTotal = rs.getDouble("callCount");
                double foldTotal = rs.getDouble("foldCount");
                double betTotal;

                if(table.contains("postflop")) {
                    betTotal = rs.getDouble("betCount");
                } else {
                    betTotal = 0;
                }

                double raiseTotal = rs.getDouble("raiseCount");
                double checkTotal = rs.getDouble("checkCount");

                double looseness = callTotal / (callTotal + foldTotal);
                double aggressiveness;

                if(table.contains("postflop")) {
                    aggressiveness = (betTotal + raiseTotal) / (betTotal + raiseTotal + checkTotal + callTotal);
                } else {
                    aggressiveness = (raiseTotal) / (raiseTotal + checkTotal + callTotal);
                }

                allLooseness.add(looseness);
                allAggro.add(aggressiveness);
            }
        }

        Collections.sort(allLooseness);
        Collections.sort(allAggro);

        if(!includingMedium) {
            int halfLooseness = allLooseness.size() / 2;
            int halfAggroness = allLooseness.size() / 2;

            System.out.println("Looseness tight below: " + allLooseness.get(halfLooseness));
            System.out.println("Aggro passive below: " + allAggro.get(halfAggroness));
        } else {
            int oneThirdLooseness = allLooseness.size() / 3;
            int twoThirdLooseness = (allLooseness.size() / 3) * 2;

            int oneThirdAggroness = allAggro.size() / 3;
            int twoThirdAggroness = (allAggro.size() / 3) * 2;

            System.out.println("Looseness tight below: " + allLooseness.get(oneThirdLooseness));
            System.out.println("Looseness medium below: " + allLooseness.get(twoThirdLooseness));

            System.out.println("Aggro passive below: " + allAggro.get(oneThirdAggroness));
            System.out.println("Aggro medium below: " + allAggro.get(twoThirdAggroness));
        }

    }

    public List<Double> getOpponentLoosenessAndAggroness(String opponentName, boolean preflop) throws Exception {
        List<Double> oppLoosenessAndAggroness = new ArrayList<>();

        String table;

        if(preflop) {
            table = "opponentidentifier_2_0_preflop";
        } else {
            table = "opponentidentifier_2_0_postflop";
        }

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE playerName = '" + opponentName + "'");

        if(rs.next()) {
            if(rs.getDouble("numberOfHands") >= 20) {
                double callTotal = rs.getDouble("callCount");
                double foldTotal = rs.getDouble("foldCount");
                double raiseTotal = rs.getDouble("raiseCount");
                double checkTotal = rs.getDouble("checkCount");

                double looseness;
                double aggressiveness;

                if(preflop) {
                    looseness = callTotal / (callTotal + foldTotal);
                    aggressiveness = raiseTotal / (raiseTotal + checkTotal + callTotal);
                } else {
                    double betTotal = rs.getDouble("betCount");

                    looseness = callTotal / (callTotal + foldTotal);
                    aggressiveness = (raiseTotal + betTotal) / (raiseTotal + betTotal + checkTotal + callTotal);
                }

                oppLoosenessAndAggroness.add(looseness);
                oppLoosenessAndAggroness.add(aggressiveness);
            }
        }

        return oppLoosenessAndAggroness;
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

        updateNumberOfHands(opponentPlayerNameOfLastHand);
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

    private void updateNumberOfHands(String opponentNick) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        st.executeUpdate("UPDATE opponentidentifier_2_0_preflop SET numberOfHands = numberOfHands + 1 WHERE playerName = '" + opponentNick + "'");
        st.executeUpdate("UPDATE opponentidentifier_2_0_postflop SET numberOfHands = numberOfHands + 1 WHERE playerName = '" + opponentNick + "'");

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
