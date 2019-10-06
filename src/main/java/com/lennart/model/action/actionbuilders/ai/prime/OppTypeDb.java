package com.lennart.model.action.actionbuilders.ai.prime;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;

import java.sql.*;

/**
 * Created by LennartMac on 05/10/2019.
 */
public class OppTypeDb {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new OppTypeDb().fillOppType(false);
    }

    private void fillOppType(boolean narrow) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponent_types;");

        int counter = 0;

        while(rs.next()) {
            String opponentName = rs.getString("playerName");

            OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0(opponentName);

            String opponentType;

            if(narrow) {
                opponentType = opponentIdentifier2_0.getOppTypeExtensive(opponentIdentifier2_0.getOppLooseness(), opponentIdentifier2_0.getOppAggressiveness());
            } else {
                opponentType = opponentIdentifier2_0.getOppType(opponentIdentifier2_0.getOppLooseness(), opponentIdentifier2_0.getOppAggressiveness());
            }

            opponentType = opponentType.replace("OppType", "");
            opponentType = opponentType.toUpperCase();

            Statement st2 = con.createStatement();

            String oppTypeColumn;

            if(narrow) {
                oppTypeColumn = "oppTypeNarrow";
            } else {
                oppTypeColumn = "oppTypeBroad";
            }

            st2.executeUpdate("UPDATE opponent_types SET " + oppTypeColumn + " = '" + opponentType + "' WHERE playerName = '" + opponentName + "';");
            st2.close();

            System.out.println(counter++);
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private void fillTableWithPlayers() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats;");

        int counter = 0;

        while(rs.next()) {
            String playerName = rs.getString("playerName");

            Statement st2 = con.createStatement();
            st2.executeUpdate("INSERT INTO opponent_types (playerName) VALUES ('" + playerName + "')");
            st2.close();

            System.out.println(counter++);
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private void clearTable() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM opponent_types;");

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
