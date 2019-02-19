package com.lennart.model.action.actionbuilders.ai.dbstatsraw;

import com.lennart.model.action.actionbuilders.ai.GameFlow;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveCall;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePersister;
import com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0.DbSavePersisterPostflop_2_0;

import java.sql.*;
import java.util.List;

/**
 * Created by LennartMac on 27/01/2019.
 */
public class DbStatsRawCallPostflopMigrator {

    private Connection con;
    private Connection con_2_0;

    public static void main(String[] args) throws Exception {
        new DbStatsRawCallPostflopMigrator().migrateRawDataToCallRouteCompact2_0();
    }

    private void migrateRawDataToCallRouteCompact2_0() throws Exception {
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

                if(botAction.equals("call")) {
                    DbStatsRawBluffPostflopMigrator dbStatsRawBluffPostflopMigrator = new DbStatsRawBluffPostflopMigrator();
                    DbSaveCall dbSaveCall = new DbSaveCall();

                    String street = dbStatsRawBluffPostflopMigrator.getStreetString(rs.getString("board"));
                    String facingAction = dbSaveCall.getFacingActionViaLogic(rs.getString("opponent_action"));
                    String position = rs.getString("position");
                    String amountToCallGroup = getAmountToCallString(rs.getDouble("opponent_total_betsize"),
                            rs.getDouble("bot_total_betsize"), rs.getDouble("botstack"), rs.getDouble("bigblind"));
                    String handStrengthString = dbSaveCall.getHandStrengthLogic(rs.getDouble("handstrength"));
                    String strongDrawString = rs.getString("strongdraw");
                    String effectiveStack = dbStatsRawBluffPostflopMigrator.getEffectiveStack(rs.getDouble("botstack"), rs.getDouble("opponentstack"), rs.getDouble("bigblind"));
                    String opponentType = new GameFlow().getOpponentGroup(rs.getDouble("recent_hands_won"));

                    String route = street + facingAction + position + amountToCallGroup + handStrengthString +
                            strongDrawString + effectiveStack + opponentType;

                    initialize_2_0_DbConnection();
                    Statement st2 = con_2_0.createStatement();

                    ResultSet rsTest = st2.executeQuery("SELECT * FROM dbstats_call_sng_compact_2_0 WHERE route = '" + route + "';");

                    if(!rsTest.next()) {
                        System.out.println("Route not found! " + route);
                    }

                    rsTest.close();

                    if(Boolean.valueOf(rs.getString("bot_won_hand"))) {
                        st2.executeUpdate("UPDATE dbstats_call_sng_compact_2_0 SET success = success + 1 WHERE route = '" + route + "'");
                    }

                    st2.executeUpdate("UPDATE dbstats_call_sng_compact_2_0 SET total = total + 1 WHERE route = '" + route + "'");

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

        amountToCallString = new DbSaveCall().getAmountToCallViaLogic(amountToCallBb);
        amountToCallString = new DbSavePersister().convertCallAtcToCompact(amountToCallString);

        return amountToCallString;
    }

    private void clearTable() throws Exception {
        initialize_2_0_DbConnection();

        Statement st = con_2_0.createStatement();
        st.executeUpdate("DELETE FROM dbstats_call_sng_compact_2_0;");

        st.close();

        close_2_0_DbConnection();
    }

    private void initializeDb() throws Exception {
        List<String> allRoutes = new DbSavePersisterPostflop_2_0().getAllCallRoutesCompact();

        initialize_2_0_DbConnection();

        for(String route : allRoutes) {
            Statement st = con_2_0.createStatement();

            st.executeUpdate("INSERT INTO dbstats_call_sng_compact_2_0 (route) VALUES ('" + route + "')");

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
