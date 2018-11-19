package com.lennart.model.action.actionbuilders.ai.foldstats;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 24/05/2018.
 */
public class FoldStatsKeeper {

    private static Map<String, Map<String, Double>> foldCountMap = new HashMap<>();
    private Connection con;

    public static void updateFoldCountMap(String playerName, String action) {
        if(foldCountMap.get(playerName) == null) {
            foldCountMap.put(playerName, new HashMap<>());
            foldCountMap.get(playerName).put("totalHandCount", 0.0);
            foldCountMap.get(playerName).put("foldCount", 0.0);
        }

        double totalUntilNow = foldCountMap.get(playerName).get("totalHandCount");
        System.out.println("totalUntilNow: " + totalUntilNow);
        foldCountMap.get(playerName).put("totalHandCount", totalUntilNow + 1);

        if(action != null && action.equals("fold")) {
            double foldTotalUntilNow = foldCountMap.get(playerName).get("foldCount");
            System.out.println("foldTotalUntilNow: " + foldTotalUntilNow);
            foldCountMap.get(playerName).put("foldCount", foldTotalUntilNow + 1);
        }
    }

    public static double getFoldStat(String playerName) {
        double foldStat;

        if(foldCountMap.get(playerName) != null) {
            double totalUntilNow = foldCountMap.get(playerName).get("totalHandCount");

            if(totalUntilNow < 20) {
                foldStat = 0.43;
            } else {
                double foldTotalUntilNow = foldCountMap.get(playerName).get("foldCount");
                foldStat = foldTotalUntilNow / totalUntilNow;
            }
        } else {
            foldStat = 0.43;
        }

        return foldStat;
    }

    public void updateFoldCountMapInDb(String playerName, String action) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM foldstats WHERE playerName = '" + playerName + "';");

        if(!rs.next()) {
            st.executeUpdate("INSERT INTO foldstats (playerName) VALUES ('" + playerName + "')");
        }

        st.executeUpdate("UPDATE foldstats SET totalHandCount = totalHandCount + 1 WHERE playerName = '" + playerName + "'");

        if(action != null && action.equals("fold")) {
            st.executeUpdate("UPDATE foldstats SET foldCount = foldCount + 1 WHERE playerName = '" + playerName + "'");
        }

        rs.close();
        st.close();
        closeDbConnection();
    }

    public double getFoldStatFromDb(String playerName) throws Exception {
        double foldStat;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM foldstats WHERE playerName = '" + playerName + "';");

        if(rs.next()) {
            double totalUntilNow = rs.getDouble("totalHandCount");

            if(totalUntilNow < 20) {
                foldStat = 0.43;
            } else {
                double foldTotalUntilNow = rs.getDouble("foldCount");
                foldStat = foldTotalUntilNow / totalUntilNow;
            }
        } else {
            foldStat = 0.43;
        }

        rs.close();
        st.close();
        closeDbConnection();

        return foldStat;
    }

    public double getTotalHandCountFromDb(String playerName) throws Exception {
        double totalHandCount = -1;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM foldstats WHERE playerName = '" + playerName + "';");

        if(rs.next()) {
            totalHandCount = rs.getDouble("totalHandCount");
        }

        return totalHandCount;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
