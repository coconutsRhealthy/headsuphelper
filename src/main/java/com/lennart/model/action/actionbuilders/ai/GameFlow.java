package com.lennart.model.action.actionbuilders.ai;

import java.sql.*;

public class GameFlow {

    private Connection con;

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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
