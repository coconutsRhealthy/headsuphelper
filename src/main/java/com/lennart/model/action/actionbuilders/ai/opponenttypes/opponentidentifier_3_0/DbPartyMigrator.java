package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 01/03/2022.
 */
public class DbPartyMigrator {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new DbPartyMigrator().clearTableFromNonPartyNames("opponentidentifier_2_0_preflop_party");
    }

    private void clearTableFromNonPartyNames(String table) throws Exception {
        Set<String> allOppNamesSet = new HashSet<>();


        initializeDbConnection();

        Statement st = con.createStatement();
        //ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflop");
        ResultSet rs = st.executeQuery("SELECT * FROM " + table);

        while(rs.next()) {
            allOppNamesSet.add(rs.getString("playerName"));
        }

        closeDbConnection();

        rs.close();
        st.close();

        List<String> allOppNames = allOppNamesSet.stream().collect(Collectors.toList());
        Collections.sort(allOppNames);

        List<String> allOppNamesParty = getPartyOpponentNames();

        allOppNames.removeAll(allOppNamesParty);

        int counter = 0;

        initializeDbConnection();

        for(String oppNameToRemove : allOppNames) {
            try {
                Statement st2 = con.createStatement();
                //st2.executeUpdate("DELETE FROM opponentidentifier_2_0_postflop_party WHERE playerName =  '" + oppNameToRemove + "';");
                st2.executeUpdate("DELETE FROM " + table + " WHERE playerName =  '" + oppNameToRemove + "';");
                System.out.println(counter++);
                st2.close();
            } catch (Exception e) {
                System.out.println("error: " + oppNameToRemove);
            }
        }

        closeDbConnection();
    }

    private List<String> getPartyOpponentNames() throws Exception {
        Set<String> partyOpponentNamesSet = new HashSet<>();

        initializeDbConnection();

        for(int i = 6; i <= 11; i++) {
            String query;

            if(i == 6) {
                query = "SELECT * FROM dbstats_raw_" + i + " WHERE entry > 8087;";
            } else {
                query = "SELECT * FROM dbstats_raw_" + i + ";";
            }

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()) {
                String oppName = rs.getString("opponent_name");

                if(!oppName.isEmpty()) {
                    partyOpponentNamesSet.add(rs.getString("opponent_name"));
                }
            }

            st.close();
            rs.close();
        }

        closeDbConnection();

        List<String> partyOpponentNames = partyOpponentNamesSet.stream().collect(Collectors.toList());
        Collections.sort(partyOpponentNames);
        return partyOpponentNames;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
