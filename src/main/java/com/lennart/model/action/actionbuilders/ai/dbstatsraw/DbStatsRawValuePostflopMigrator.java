package com.lennart.model.action.actionbuilders.ai.dbstatsraw;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveValue;
import com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0.DbSavePersisterPostflop_2_0;

import java.sql.*;
import java.util.List;

/**
 * Created by LennartMac on 26/01/2019.
 */
public class DbStatsRawValuePostflopMigrator {

    private Connection con;
    private Connection con_2_0;

    public static void main(String[] args) throws Exception {
        new DbStatsRawValuePostflopMigrator().migrateRawDataToBluffRouteCompact2_0();
    }

    private void migrateRawDataToBluffRouteCompact2_0() throws Exception {
        clearTable();
        initializeDb();

        int counter = 0;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw;");

        while(rs.next()) {
            String board = rs.getString("board");

            if(!board.equals("")) {
                String botAction = rs.getString("bot_action");

                if(botAction.equals("bet75pct") || botAction.equals("raise")) {
                    double handStrength = rs.getDouble("handstrength");

                    if(handStrength >= 0.7) {
                        DbStatsRawBluffPostflopMigrator dbStatsRawBluffPostflopMigrator = new DbStatsRawBluffPostflopMigrator();

                        String street = dbStatsRawBluffPostflopMigrator.getStreetString(rs.getString("board"));
                        String valueAction = dbStatsRawBluffPostflopMigrator.getAction(rs.getString("bot_action"));
                        String position = rs.getString("position");
                        String sizingGroup = dbStatsRawBluffPostflopMigrator.getSizingGroup(rs.getDouble("sizing"), rs.getDouble("bigblind"));
                        String handStrengthString = new DbSaveValue().getHandStrengthLogic(handStrength);
                        String effectiveStack = dbStatsRawBluffPostflopMigrator.getEffectiveStack(rs.getDouble("botstack"), rs.getDouble("opponentstack"), rs.getDouble("bigblind"));
                        String opponentType = dbStatsRawBluffPostflopMigrator.getOpponentGroup(rs.getString("opponent_name"));

                        String route = street + valueAction + position + sizingGroup + handStrengthString + effectiveStack + opponentType;

                        initialize_2_0_DbConnection();
                        Statement st2 = con_2_0.createStatement();

                        ResultSet rsTest = st2.executeQuery("SELECT * FROM dbstats_value_sng_compact_2_0 WHERE route = '" + route + "';");

                        if(!rsTest.next()) {
                            System.out.println("Route not found! " + route);
                        }

                        rsTest.close();

                        if(Boolean.valueOf(rs.getString("bot_won_hand"))) {
                            st2.executeUpdate("UPDATE dbstats_value_sng_compact_2_0 SET success = success + 1 WHERE route = '" + route + "'");
                        }

                        st2.executeUpdate("UPDATE dbstats_value_sng_compact_2_0 SET total = total + 1 WHERE route = '" + route + "'");

                        st2.close();
                        close_2_0_DbConnection();

                        counter++;

                        System.out.println(counter);
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private void clearTable() throws Exception {
        initialize_2_0_DbConnection();

        Statement st = con_2_0.createStatement();
        st.executeUpdate("DELETE FROM dbstats_value_sng_compact_2_0;");

        st.close();

        close_2_0_DbConnection();
    }

    private void initializeDb() throws Exception {
        List<String> allRoutes = new DbSavePersisterPostflop_2_0().getAllValueRoutesCompact();

        initialize_2_0_DbConnection();

        for(String route : allRoutes) {
            Statement st = con_2_0.createStatement();

            st.executeUpdate("INSERT INTO dbstats_value_sng_compact_2_0 (route) VALUES ('" + route + "')");

            st.close();
        }

        close_2_0_DbConnection();
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void initialize_2_0_DbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con_2_0 = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker_2_0?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

    private void close_2_0_DbConnection() throws SQLException {
        con_2_0.close();
    }
}
