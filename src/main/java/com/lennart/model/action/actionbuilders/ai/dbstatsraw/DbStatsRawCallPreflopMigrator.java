package com.lennart.model.action.actionbuilders.ai.dbstatsraw;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePersisterPreflop;

import java.sql.*;

/**
 * Created by LennartMac on 27/01/2019.
 */
public class DbStatsRawCallPreflopMigrator {

    private Connection con;
    private Connection con_2_0;

    public static void main(String[] args) throws Exception {
        new DbStatsRawCallPreflopMigrator().migrateRawDataToPreflopCallRouteCompact2_0();
    }

    private void migrateRawDataToPreflopCallRouteCompact2_0() throws Exception {
        int counter = 0;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw;");

        while(rs.next()) {
            String board = rs.getString("board");

            if(board.equals("")) {
                String botAction = rs.getString("bot_action");

                if(botAction.equals("call")) {
                    DbStatsRawRaisePreflopMigrator dbStatsRawRaisePreflopMigrator = new DbStatsRawRaisePreflopMigrator();

                    String handStrength = new DbSavePersisterPreflop().convertNumericHandstrengthToString(rs.getDouble("handstrength"));
                    String position = rs.getString("position");
                    String amountToCallGroup = getAmountToCallString(rs.getDouble("opponent_total_betsize"),
                            rs.getDouble("bot_total_betsize"), rs.getDouble("botstack"), rs.getDouble("bigblind"));
                    String effectiveStack = dbStatsRawRaisePreflopMigrator.getEffectiveStack(rs.getDouble("botstack"), rs.getDouble("opponentstack"), rs.getDouble("bigblind"));
                    String opponentStatsString = new DbStatsRawBluffPostflopMigrator().getOpponentStatsString(rs.getString("opponent_name"));

                    String route = handStrength + position + amountToCallGroup + effectiveStack + opponentStatsString;

                    initialize_2_0_DbConnection();
                    Statement st2 = con_2_0.createStatement();

                    ResultSet rsTest = st2.executeQuery("SELECT * FROM dbstats_pf_call_sng_compact_2_0 WHERE route = '" + route + "';");

                    if(!rsTest.next()) {
                        System.out.println("Route not found! " + route);
                    }

                    rsTest.close();

                    if(Boolean.valueOf(rs.getString("bot_won_hand"))) {
                        st2.executeUpdate("UPDATE dbstats_pf_call_sng_compact_2_0 SET success = success + 1 WHERE route = '" + route + "'");
                    }

                    st2.executeUpdate("UPDATE dbstats_pf_call_sng_compact_2_0 SET total = total + 1 WHERE route = '" + route + "'");

                    st2.close();
                    close_2_0_DbConnection();

                    counter++;

                    System.out.println(counter);
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private String getAmountToCallString(double opponentBetsize, double botBetsize, double botStack,
                               double bigBlind) {
        String amountToCallString;

        double amounToCall = opponentBetsize - botBetsize;

        if(amounToCall > botStack) {
            amounToCall = botStack;
        }

        double amountToCallBb = amounToCall / bigBlind;

        String sizingGroup = new DbStatsRawRaisePreflopMigrator().getSizingGroup(amountToCallBb, bigBlind);

        amountToCallString = sizingGroup.replace("Sizing_", "Atc_");

        return amountToCallString;
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
