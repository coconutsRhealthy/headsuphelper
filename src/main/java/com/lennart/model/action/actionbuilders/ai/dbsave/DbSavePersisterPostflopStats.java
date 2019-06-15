package com.lennart.model.action.actionbuilders.ai.dbsave;

import java.sql.*;

public class DbSavePersisterPostflopStats {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new DbSavePersisterPostflopStats().doDbSaveUpdate("zhassakon");
    }

    public void doDbSaveUpdate(String opponentName) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "' AND board != '' AND opponent_action != 'empty';");

        double post4_6bbCounter = 0;
        double post6_13bbCounter = 0;
        double post13bb_upCounter = 0;
        double postTotalCounter = 0;

        while(rs.next()) {
            postTotalCounter++;

            double oppBetSize = rs.getDouble("opponent_total_betsize");
            double bigBlind = rs.getDouble("bigblind");
            double oppBetSizeBb = oppBetSize / bigBlind;

            if(oppBetSizeBb >= 4) {
                if(oppBetSizeBb <= 6.25) {
                    post4_6bbCounter++;
                } else if(oppBetSizeBb <= 13.27) {
                    post6_13bbCounter++;
                } else {
                    post13bb_upCounter++;
                }
            }
        }

        rs.close();
        st.close();

        addOpponentToDbIfNecessary(opponentName);

        Statement st2 = con.createStatement();
        st2.executeUpdate(
                "UPDATE opponentidentifier_2_0_postflopstats SET post_4_6bb = " + post4_6bbCounter +
                        ", post_6_13bb = " + post6_13bbCounter +
                        ", post_13bb_up = " + post13bb_upCounter +
                        ", postTotal = " + postTotalCounter +
                        " WHERE playerName = '" + opponentName + "'");
        st2.close();

        closeDbConnection();
    }

    private void addOpponentToDbIfNecessary(String opponentName) throws Exception {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflopstats WHERE playerName = '" + opponentName + "';");

        if(!rs.next()) {
            Statement st2 = con.createStatement();
            st2.executeUpdate("INSERT INTO opponentidentifier_2_0_postflopstats (playerName) VALUES ('" + opponentName + "')");
            st2.close();

            System.out.println("Opponent: " + opponentName + " was not present yet in postflopstats db. Added the opponent");
        }

        rs.close();
        st.close();
    }


    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
