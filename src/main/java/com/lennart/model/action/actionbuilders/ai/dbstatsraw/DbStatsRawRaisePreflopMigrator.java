package com.lennart.model.action.actionbuilders.ai.dbstatsraw;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSave;
import com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0.DbSavePersisterPreflop_2_0;
import com.lennart.model.card.Card;

import java.sql.*;
import java.util.List;

/**
 * Created by LennartMac on 26/01/2019.
 */
public class DbStatsRawRaisePreflopMigrator {

    private Connection con;
    private Connection con_2_0;

    public static void main(String[] args) throws Exception {
        new DbStatsRawRaisePreflopMigrator().migrateRawDataToBluffRouteCompact2_0();
    }

    private void quickAnalysis() throws Exception {
        initialize_2_0_DbConnection();

        Statement st = con_2_0.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_pf_raise_sng_compact_2_0;");

        while(rs.next()) {
            if(rs.getDouble("total") >= 10) {
                double ratio = rs.getDouble("success") / rs.getDouble("total");

                if(ratio < 0.5) {
                    System.out.println(rs.getString("route") + "    " + rs.getDouble("success") + "   " + rs.getDouble("total"));
                }
            }
        }

        rs.close();
        st.close();

        close_2_0_DbConnection();
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

            if(board.equals("")) {
                String botAction = rs.getString("bot_action");

                if(botAction.equals("raise")) {
                    List<Card> holeCards = new Analysis().convertCardStringToCardList(rs.getString("holecards"));
                    String combo = new DbSave().getComboLogic(holeCards);
                    String position = rs.getString("position");
                    String sizingGroup = getSizingGroup(rs.getDouble("sizing"), rs.getDouble("bigblind"));
                    String effectiveStack = new DbStatsRawBluffPostflopMigrator().getEffectiveStack(rs.getDouble("botstack"), rs.getDouble("opponentstack"), rs.getDouble("bigblind"));
                    String opponentType = new DbStatsRawBluffPostflopMigrator().getOpponentGroup(rs.getString("opponent_name"));

                    String route = combo + position + sizingGroup + effectiveStack + opponentType;

                    initialize_2_0_DbConnection();
                    Statement st2 = con_2_0.createStatement();

                    ResultSet rsTest = st2.executeQuery("SELECT * FROM dbstats_pf_raise_sng_compact_2_0 WHERE route = '" + route + "';");

                    if(!rsTest.next()) {
                        System.out.println("Route not found! " + route);
                    }

                    rsTest.close();

                    if(Boolean.valueOf(rs.getString("bot_won_hand"))) {
                        st2.executeUpdate("UPDATE dbstats_pf_raise_sng_compact_2_0 SET success = success + 1 WHERE route = '" + route + "'");
                    }

                    st2.executeUpdate("UPDATE dbstats_pf_raise_sng_compact_2_0 SET total = total + 1 WHERE route = '" + route + "'");

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

    public String getSizingGroup(double sizing, double bigBlind) {
        String sizingGroup;

        double sizingBb = sizing / bigBlind;

        if(sizingBb <= 5) {
            sizingGroup = "Sizing_0-5bb";
        } else if(sizingBb <= 13) {
            sizingGroup = "Sizing_5-13bb";
        } else if(sizingBb <= 26){
            sizingGroup = "Sizing_13-26bb";
        } else {
            sizingGroup = "Sizing_26bb_up";
        }

        return sizingGroup;
    }

    public String getEffectiveStack(double botStack, double opponentStack, double bigBlind) {
        String effectiveStack;

        double botStackBb = botStack / bigBlind;
        double opponentStackBb = opponentStack / bigBlind;

        double effectiveStackBb;

        if(botStackBb > opponentStackBb) {
            effectiveStackBb = opponentStackBb;
        } else {
            effectiveStackBb = botStackBb;
        }

        if(effectiveStackBb <= 10) {
            effectiveStack = "Effstack_0-10bb";
        } else if(effectiveStackBb <= 30){
            effectiveStack = "Effstack_10-30bb";
        } else if(effectiveStackBb <= 50) {
            effectiveStack = "Effstack_30-50bb";
        } else if(effectiveStackBb <= 75) {
            effectiveStack = "Effstack_50-75bb";
        } else if(effectiveStackBb <= 110) {
            effectiveStack = "Effstack_75-110bb";
        } else {
            effectiveStack = "Effstack_110bb_up";
        }

        return effectiveStack;
    }

    private void clearTable() throws Exception {
        initialize_2_0_DbConnection();

        Statement st = con_2_0.createStatement();
        st.executeUpdate("DELETE FROM dbstats_pf_raise_sng_compact_2_0;");

        st.close();

        close_2_0_DbConnection();
    }

    private void initializeDb() throws Exception {
        List<String> allRoutes = new DbSavePersisterPreflop_2_0().getAllPfRaiseRoutesCompact();

        initialize_2_0_DbConnection();

        for(String route : allRoutes) {
            Statement st = con_2_0.createStatement();

            st.executeUpdate("INSERT INTO dbstats_pf_raise_sng_compact_2_0 (route) VALUES ('" + route + "')");

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
