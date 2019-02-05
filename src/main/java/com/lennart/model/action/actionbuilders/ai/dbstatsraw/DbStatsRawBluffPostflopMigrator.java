package com.lennart.model.action.actionbuilders.ai.dbstatsraw;

import com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0.DbSavePersisterPostflop_2_0;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 23/01/2019.
 */
public class DbStatsRawBluffPostflopMigrator {

    private Connection con;
    private Connection con_2_0;

    public static void main(String[] args) throws Exception {
        new DbStatsRawBluffPostflopMigrator().migrateRawDataToBluffRouteCompact2_0();
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

                    if(handStrength < 0.7) {
                        String street = getStreetString(rs.getString("board"));
                        String bluffAction = getAction(rs.getString("bot_action"));
                        String position = rs.getString("position");
                        String sizingGroup = getSizingGroup(rs.getDouble("sizing"), rs.getDouble("bigblind"));
                        String strongDraw = rs.getString("strongdraw");
                        String effectiveStack = getEffectiveStack(rs.getDouble("botstack"), rs.getDouble("opponentstack"), rs.getDouble("bigblind"));
                        String opponentType = getOpponentGroup(rs.getString("opponent_name"));

                        String route = street + bluffAction + position + sizingGroup + strongDraw + effectiveStack + opponentType;

                        initialize_2_0_DbConnection();
                        Statement st2 = con_2_0.createStatement();

                        ResultSet rsTest = st2.executeQuery("SELECT * FROM dbstats_bluff_sng_compact_2_0 WHERE route = '" + route + "';");

                        if(!rsTest.next()) {
                            System.out.println("Route not found! " + route);
                        }

                        rsTest.close();

                        if(Boolean.valueOf(rs.getString("bot_won_hand"))) {
                            st2.executeUpdate("UPDATE dbstats_bluff_sng_compact_2_0 SET success = success + 1 WHERE route = '" + route + "'");
                        }

                        st2.executeUpdate("UPDATE dbstats_bluff_sng_compact_2_0 SET total = total + 1 WHERE route = '" + route + "'");

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

    public String getAction(String actionFromDb) {
        String action = null;

        if(actionFromDb.equals("bet75pct")) {
            action = "Bet";
        } else if(actionFromDb.equals("raise")) {
            action = "Raise";
        }

        return action;
    }

    public String getStreetString(String board) {
        String street;

        String boardOnlyNumbers = board.replaceAll("[^\\d.]", "");

        char numbersOfBoard [] = boardOnlyNumbers.toCharArray();

        for(char c : numbersOfBoard) {
            board = board.replace(c, ' ');
        }

        board = board.replaceAll(" ", "");

        if(board.length() == 3) {
            street = "Flop";
        } else if(board.length() == 4) {
            street = "Turn";
        } else if(board.length() == 5) {
            street = "River";
        } else {
            System.out.println("wrong boardstring!");
            throw new RuntimeException();
        }

        return street;
    }

    public String getSizingGroup(double sizing, double bigBlind) {
        String sizingGroup;

        double sizingBb = sizing / bigBlind;

        if(sizingBb <= 5) {
            sizingGroup = "Sizing_0-5bb";
        } else if(sizingBb <= 10) {
            sizingGroup = "Sizing_5-10bb";
        } else if(sizingBb <= 20) {
            sizingGroup = "Sizing_10-20bb";
        } else {
            sizingGroup = "Sizing_20bb_up";
        }

        return sizingGroup;
    }

    public String getOpponentGroup(String opponentName) throws Exception {
        String opponentStatsString;

        OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0(opponentName);

        if(opponentIdentifier2_0.getNumberOfHands() >= 14) {
            if(opponentIdentifier2_0.getOppPre3bet() < OpponentIdentifier2_0.PRE_3_BET) {
                opponentStatsString = "OppPre3betLow";
            } else {
                opponentStatsString = "OppPre3betHigh";
            }

            if(opponentIdentifier2_0.getOppPreLooseness() < OpponentIdentifier2_0.PRE_LOOSENESS) {
                opponentStatsString = opponentStatsString + "OppPreLoosenessTight";
            } else {
                opponentStatsString = opponentStatsString + "OppPreLoosenessLoose";
            }

            if(opponentIdentifier2_0.getOppPostRaise() < OpponentIdentifier2_0.POST_RAISE) {
                opponentStatsString = opponentStatsString + "OppPostRaiseLow";
            } else {
                opponentStatsString = opponentStatsString + "OppPostRaiseHigh";
            }

            if(opponentIdentifier2_0.getOppPostBet() < OpponentIdentifier2_0.POST_BET) {
                opponentStatsString = opponentStatsString + "OppPostBetLow";
            } else {
                opponentStatsString = opponentStatsString + "OppPostBetHigh";
            }

            if(opponentIdentifier2_0.getOppPostLooseness() < OpponentIdentifier2_0.POST_LOOSENESS) {
                opponentStatsString = opponentStatsString + "OppPostLoosenessTight";
            } else {
                opponentStatsString = opponentStatsString + "OppPostLoosenessLoose";
            }
        } else {
            opponentStatsString = "OpponentUnknown";
        }

        String opponentGroup = getOppGroupFromOppStatString(opponentStatsString);

        return opponentGroup;
    }

    private String getOppGroupFromOppStatString(String oppStatString) {
        String oppGroup = null;

        List<String> groupAstats = new ArrayList<>();
        List<String> groupBstats = new ArrayList<>();
        List<String> groupCstats = new ArrayList<>();
        List<String> groupDstats = new ArrayList<>();

        groupAstats.add("OppPre3betLowOppPreLoosenessLooseOppPostRaiseLowOppPostBetLowOppPostLoosenessTight");
        groupAstats.add("OppPre3betLowOppPreLoosenessTightOppPostRaiseHighOppPostBetLowOppPostLoosenessTight");
        groupAstats.add("OppPre3betLowOppPreLoosenessTightOppPostRaiseLowOppPostBetHighOppPostLoosenessTight");
        groupAstats.add("OppPre3betLowOppPreLoosenessTightOppPostRaiseLowOppPostBetLowOppPostLoosenessTight");
        groupAstats.add("OppPre3betLowOppPreLoosenessLooseOppPostRaiseHighOppPostBetLowOppPostLoosenessTight");
        groupAstats.add("OpponentUnknown");
        groupAstats.add("OppPre3betLowOppPreLoosenessTightOppPostRaiseLowOppPostBetLowOppPostLoosenessLoose");
        groupAstats.add("OppPre3betLowOppPreLoosenessLooseOppPostRaiseHighOppPostBetHighOppPostLoosenessTight");

        groupBstats.add("OppPre3betLowOppPreLoosenessLooseOppPostRaiseLowOppPostBetHighOppPostLoosenessTight");
        groupBstats.add("OppPre3betHighOppPreLoosenessTightOppPostRaiseLowOppPostBetLowOppPostLoosenessTight");
        groupBstats.add("OppPre3betHighOppPreLoosenessTightOppPostRaiseLowOppPostBetLowOppPostLoosenessLoose");
        groupBstats.add("OppPre3betHighOppPreLoosenessLooseOppPostRaiseLowOppPostBetLowOppPostLoosenessLoose");
        groupBstats.add("OppPre3betLowOppPreLoosenessLooseOppPostRaiseLowOppPostBetLowOppPostLoosenessLoose");
        groupBstats.add("OppPre3betLowOppPreLoosenessTightOppPostRaiseHighOppPostBetHighOppPostLoosenessTight");
        groupBstats.add("OppPre3betHighOppPreLoosenessLooseOppPostRaiseHighOppPostBetLowOppPostLoosenessTight");
        groupBstats.add("OppPre3betHighOppPreLoosenessTightOppPostRaiseLowOppPostBetHighOppPostLoosenessTight");

        groupCstats.add("OppPre3betHighOppPreLoosenessTightOppPostRaiseHighOppPostBetHighOppPostLoosenessLoose");
        groupCstats.add("OppPre3betLowOppPreLoosenessLooseOppPostRaiseLowOppPostBetHighOppPostLoosenessLoose");
        groupCstats.add("OppPre3betLowOppPreLoosenessTightOppPostRaiseLowOppPostBetHighOppPostLoosenessLoose");
        groupCstats.add("OppPre3betHighOppPreLoosenessTightOppPostRaiseHighOppPostBetHighOppPostLoosenessTight");
        groupCstats.add("OppPre3betHighOppPreLoosenessTightOppPostRaiseHighOppPostBetLowOppPostLoosenessLoose");
        groupCstats.add("OppPre3betHighOppPreLoosenessLooseOppPostRaiseLowOppPostBetHighOppPostLoosenessTight");
        groupCstats.add("OppPre3betHighOppPreLoosenessTightOppPostRaiseHighOppPostBetLowOppPostLoosenessTight");
        groupCstats.add("OppPre3betHighOppPreLoosenessLooseOppPostRaiseLowOppPostBetLowOppPostLoosenessTight");

        groupDstats.add("OppPre3betLowOppPreLoosenessLooseOppPostRaiseHighOppPostBetLowOppPostLoosenessLoose");
        groupDstats.add("OppPre3betLowOppPreLoosenessTightOppPostRaiseHighOppPostBetHighOppPostLoosenessLoose");
        groupDstats.add("OppPre3betLowOppPreLoosenessLooseOppPostRaiseHighOppPostBetHighOppPostLoosenessLoose");
        groupDstats.add("OppPre3betHighOppPreLoosenessLooseOppPostRaiseHighOppPostBetHighOppPostLoosenessTight");
        groupDstats.add("OppPre3betHighOppPreLoosenessLooseOppPostRaiseLowOppPostBetHighOppPostLoosenessLoose");
        groupDstats.add("OppPre3betHighOppPreLoosenessTightOppPostRaiseLowOppPostBetHighOppPostLoosenessLoose");
        groupDstats.add("OppPre3betLowOppPreLoosenessTightOppPostRaiseHighOppPostBetLowOppPostLoosenessLoose");
        groupDstats.add("OppPre3betHighOppPreLoosenessLooseOppPostRaiseHighOppPostBetLowOppPostLoosenessLoose");
        groupDstats.add("OppPre3betHighOppPreLoosenessLooseOppPostRaiseHighOppPostBetHighOppPostLoosenessLoose");

        if(groupAstats.contains(oppStatString)) {
            oppGroup = "OppTypeA";
        } else if(groupBstats.contains(oppStatString)) {
            oppGroup = "OppTypeB";
        } else if(groupCstats.contains(oppStatString)) {
            oppGroup = "OppTypeC";
        } else if(groupDstats.contains(oppStatString)) {
            oppGroup = "OppTypeD";
        }

        return oppGroup;
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

        if(effectiveStackBb <= 35) {
            effectiveStack = "EffStack_0_35_";
        } else {
            effectiveStack = "EffStack_35_up_";
        }

        return effectiveStack;
    }

    private void clearTable() throws Exception {
        initialize_2_0_DbConnection();

        Statement st = con_2_0.createStatement();
        st.executeUpdate("DELETE FROM dbstats_bluff_sng_compact_2_0;");

        st.close();

        close_2_0_DbConnection();
    }

    private void initializeDb() throws Exception {
        List<String> allRoutes = new DbSavePersisterPostflop_2_0().getAllBluffRoutesCompact();

        initialize_2_0_DbConnection();

        for(String route : allRoutes) {
            Statement st = con_2_0.createStatement();

            st.executeUpdate("INSERT INTO dbstats_bluff_sng_compact_2_0 (route) VALUES ('" + route + "')");

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
