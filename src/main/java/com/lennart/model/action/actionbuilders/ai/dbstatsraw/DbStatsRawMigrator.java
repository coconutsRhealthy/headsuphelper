package com.lennart.model.action.actionbuilders.ai.dbstatsraw;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0.OpponentIdentifier2_0;

import java.sql.*;

/**
 * Created by LennartMac on 23/01/2019.
 */
public class DbStatsRawMigrator {

    private Connection con;
    private Connection con_2_0;

    private void migrateRawDataToBluffRouteCompact2_0() throws Exception {
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
                        String opponentStatsString = getOpponentStatsString(rs.getString("opponent_name"));

                        String route = street + bluffAction + position + sizingGroup + strongDraw + effectiveStack + opponentStatsString;

                        initialize_2_0_DbConnection();
                        Statement st2 = con_2_0.createStatement();

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

        if(sizingBb <= 10) {
            sizingGroup = "Sizing_0-10bb";
        } else if(sizingBb <= 20) {
            sizingGroup = "Sizing_10-20bb";
        } else {
            sizingGroup = "Sizing_20bb_up";
        }

        return sizingGroup;
    }

    public String getOpponentStatsString(String opponentName) throws Exception {
        String opponentStatsString;

        OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0(opponentName);

        if(opponentIdentifier2_0.getNumberOfHands() >= 20) {
            if(opponentIdentifier2_0.getOppPostRaise() < 0.12162162162162163) {
                opponentStatsString = "OppPostRaiseLow";
            } else {
                opponentStatsString = "OppPostRaiseHigh";
            }

            if(opponentIdentifier2_0.getOppPostLooseness() < 0.5) {
                opponentStatsString = opponentStatsString + "OppPostLoosenessTight";
            } else {
                opponentStatsString = opponentStatsString + "OppPostLoosenessLoose";
            }
        } else {
            opponentStatsString = "OpponentUnknown";
        }

        return opponentStatsString;
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
