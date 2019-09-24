package com.lennart.model.action.actionbuilders.ai.prime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 24/09/2019.
 */
public class DbRawLogic {

    private Connection con;

    private void fillHandNumbersInDbStatsRaw(List<Integer> entriesWhereNewHandsStart) throws Exception {
        int handCounter = 0;

        initializeDbConnection();
        Statement st = con.createStatement();

        for(int i = 0; i <= 100; i++) {
            if(entriesWhereNewHandsStart.contains(i)) {
                handCounter++;
            }

            st.executeUpdate("UPDATE dbstats_raw SET handnumber = " + handCounter + " WHERE entry = '" + i + "'");
        }

        st.close();

        closeDbConnection();
    }

    private List<Integer> getEntriesWhereNewHandStarts() throws Exception {
        List<Integer> entriesWhereNewHandStarts = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry < 100 ORDER BY entry ASC;");

        int startEntry;

        while(rs.next()) {
            startEntry = rs.getInt("entry");

            int counter = 0;

            String holeCardsBaseAr = rs.getString("holecards");
            String positionBaseAr = rs.getString("position");
            boolean isSameHand = true;

            while(isSameHand) {
                if(rs.next()) {
                    counter++;

                    String holeCardsResearchAr = rs.getString("holecards");
                    String positionResearchAr = rs.getString("position");

                    if(holeCardsBaseAr.equals(holeCardsResearchAr) && positionBaseAr.equals(positionResearchAr)) {
                        //nothing
                    } else {
                        counter = 0;
                        isSameHand = false;

                        int entryToAdd = startEntry + counter;
                        entriesWhereNewHandStarts.add(entryToAdd);

                        rs.previous();
                    }
                } else {
                    isSameHand = false;
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        return entriesWhereNewHandStarts;
    }

    private void analyseTotalChips() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw_test;");

        int counter = 0;

        while(rs.next()) {
            double botStack = rs.getDouble("botstack");
            double oppStack = rs.getDouble("opponentstack");
            double botTotalBetsize = rs.getDouble("bot_total_betsize");
            double oppTotalBetsize = rs.getDouble("opponent_total_betsize");

            double pot = 0;

            if(botTotalBetsize + oppTotalBetsize + botStack + oppStack != 3000) {
                pot = 3000 - (botStack + oppStack + botTotalBetsize + oppTotalBetsize);
            }

            int entry = rs.getInt("entry");

            if(pot > 0 && pot < 3000) {
                Statement st2 = con.createStatement();
                st2.executeUpdate("UPDATE dbstats_raw_test SET pot = " + pot + " WHERE entry = '" + entry + "'");
                st2.close();
                System.out.println(counter++);
            }
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
