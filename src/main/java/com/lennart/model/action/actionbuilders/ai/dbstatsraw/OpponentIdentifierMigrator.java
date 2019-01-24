package com.lennart.model.action.actionbuilders.ai.dbstatsraw;

import java.sql.*;

/**
 * Created by LennartMac on 24/01/2019.
 */
public class OpponentIdentifierMigrator {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new OpponentIdentifierMigrator().migrateOpponentIdentifier();
    }

    private void migrateOpponentIdentifier() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop;");

        while(rs.next()) {
            String opponentName = rs.getString("playerName");

            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_raw;");

            while(rs2.next()) {
                if(opponentName.equals(rs2.getString("opponent_name"))) {
                    if(rs2.getString("board").equals("")) {
                        if(rs2.getString("opponent_action").equals("raise")) {
                            if(rs2.getString("position").equals("Oop")) {
                                Statement st3 = con.createStatement();
                                st3.executeUpdate("UPDATE opponentidentifier_2_0_preflop SET ipRaiseCount = ipRaiseCount + 1 WHERE playerName = '" + opponentName + "'");
                                st3.close();
                            }

                            if(rs2.getString("position").equals("Ip")) {
                                Statement st3 = con.createStatement();
                                st3.executeUpdate("UPDATE opponentidentifier_2_0_preflop SET oopRaiseCount = oopRaiseCount + 1 WHERE playerName = '" + opponentName + "'");
                                st3.close();
                            }
                        }
                    }
                }
            }

            rs2.close();
            st2.close();

            System.out.println(".");
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
