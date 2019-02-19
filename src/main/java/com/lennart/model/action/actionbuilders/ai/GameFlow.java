package com.lennart.model.action.actionbuilders.ai;

import java.sql.*;

public class GameFlow {

    private Connection con;

    public static double GROUP_D_MAX = 0.4;
    public static double GROUP_C_MAX = 0.5128;
    public static double GROUP_B_MAX = 0.65;
    public static double LESS_THAN_20_HANDS = -2;

    public double getNumberOfHandsWonAgainstOppInLast20Hands(String opponentName, int entry) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "' AND entry < " + entry + " ORDER BY entry DESC;");

        int counter = 0;
        double botWonHand = 0;
        double oppWonHand = 0;

        while(rs.next()) {
            counter++;

            if(counter <= 40) {
                if(rs.getString("bot_won_hand").equals("true")) {
                    botWonHand++;
                } else {
                    oppWonHand++;
                }
            } else {
                break;
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        double ratio;

        if(counter < 20) {
            ratio = -2;
        } else {
            ratio = botWonHand / (botWonHand + oppWonHand);
        }

        return ratio;
    }

    public String getOpponentGroup(double recentHandsWon) {
        String oppGroup;

        if(recentHandsWon == LESS_THAN_20_HANDS) {
            oppGroup = "OppTypeB";
        } else if(recentHandsWon < GROUP_D_MAX) {
            oppGroup = "OppTypeD";
        } else if(recentHandsWon < GROUP_C_MAX) {
            oppGroup = "OppTypeC";
        } else if(recentHandsWon < GROUP_B_MAX) {
            oppGroup = "OppTypeB";
        } else {
            oppGroup = "OppTypeA";
        }

        return oppGroup;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
