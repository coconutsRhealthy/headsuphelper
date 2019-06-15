package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0;

import java.sql.*;
import java.util.*;

public class OppIdentifierPostflopStats {

    private Connection con;

    private static final double POST_4_6BB_33PCT_VALUE = 0.05063291139240506;
    private static final double POST_4_6BB_66PCT_VALUE = 0.1;
    private static final double POST_6_13BB_33PCT_VALUE = 0.05;
    private static final double PRE_6_13BB_66PCT_VALUE = 0.1;
    private static final double PRE_13BB_UP_33PCT_VALUE = 0.046511627906976744;
    private static final double PRE_13BB_UP_66PCT_VALUE = 0.1;

    public static void main(String[] args) throws Exception {
        new OppIdentifierPostflopStats().getOppPostGroupMap("Hentasy");
    }

    public Map<String, String> getOppPostGroupMap(String opponentName) throws Exception {
        Map<String, String> oppPostGroupMap = new HashMap<>();

        Map<String, Double> oppPostStatsMap = getOppPostStatsMap(opponentName);

        if(!oppPostStatsMap.isEmpty()) {
            double post_4_6bb_Number = oppPostStatsMap.get("post_4_6bb");
            double post_6_13bb_Number = oppPostStatsMap.get("post_6_13bb");
            double post_13bb_Up_Number = oppPostStatsMap.get("post_13bb_up");
            double postTotal = oppPostStatsMap.get("postTotal");

            if(postTotal >= 20) {
                double post_4_6bb_ratio = post_4_6bb_Number / postTotal;
                double post_6_13bb_ratio = post_6_13bb_Number / postTotal;
                double post_13bb_up_ratio = post_13bb_Up_Number / postTotal;

                if(post_4_6bb_ratio < POST_4_6BB_33PCT_VALUE) {
                    oppPostGroupMap.put("post_4_6bb_group", "low");
                } else if(post_4_6bb_ratio < POST_4_6BB_66PCT_VALUE) {
                    oppPostGroupMap.put("post_4_6bb_group", "medium");
                } else {
                    oppPostGroupMap.put("post_4_6bb_group", "high");
                }

                if(post_6_13bb_ratio < POST_6_13BB_33PCT_VALUE) {
                    oppPostGroupMap.put("post_6_13bb_group", "low");
                } else if(post_6_13bb_ratio < PRE_6_13BB_66PCT_VALUE) {
                    oppPostGroupMap.put("post_6_13bb_group", "medium");
                } else {
                    oppPostGroupMap.put("post_6_13bb_group", "high");
                }

                if(post_13bb_up_ratio < PRE_13BB_UP_33PCT_VALUE) {
                    oppPostGroupMap.put("post_13bb_up_group", "low");
                } else if(post_13bb_up_ratio < PRE_13BB_UP_66PCT_VALUE) {
                    oppPostGroupMap.put("post_13bb_up_group", "medium");
                } else {
                    oppPostGroupMap.put("post_13bb_up_group", "high");
                }
            } else {
                System.out.println("too few hands postflop for oppPostStatsMap: " + postTotal);
                System.out.println("put everything as 'medium' because too few hands");

                oppPostGroupMap.put("post_4_6bb_group", "medium");
                oppPostGroupMap.put("post_6_13bb_group", "medium");
                oppPostGroupMap.put("post_13bb_up_group", "medium");
            }

        } else {
            System.out.println("oppPostStatsMap empty for opponent: " + opponentName);
            System.out.println("put everything as 'medium' as default");

            oppPostGroupMap.put("post_4_6bb_group", "medium");
            oppPostGroupMap.put("post_6_13bb_group", "medium");
            oppPostGroupMap.put("post_13bb_up_group", "medium");
        }

        return oppPostGroupMap;
    }

    private Map<String, Double> getOppPostStatsMap(String opponentName) throws Exception {
        Map<String, Double> oppPreStatsMap = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflopstats WHERE playerName = '" + opponentName + "';");

        if(rs.next()) {
            double post_4_6bb_Number = rs.getDouble("post_4_6bb");
            double post_6_13bb_Number = rs.getDouble("post_6_13bb");
            double post_13bb_Up_Number = rs.getDouble("post_13bb_up");
            double postTotal = rs.getDouble("postTotal");

            oppPreStatsMap.put("post_4_6bb", post_4_6bb_Number);
            oppPreStatsMap.put("post_6_13bb", post_6_13bb_Number);
            oppPreStatsMap.put("post_13bb_up", post_13bb_Up_Number);
            oppPreStatsMap.put("postTotal", postTotal);
        } else {
            System.out.println("Can't find opponent in opponentidentifier_2_0_postflopstats! Will add to db");

            Statement st2 = con.createStatement();
            st2.executeUpdate("INSERT INTO opponentidentifier_2_0_postflopstats (playerName) VALUES ('" + opponentName + "')");
            st2.close();
        }

        rs.close();
        st.close();

        closeDbConnection();

        return oppPreStatsMap;
    }

    private void migrateRawDataToPostflopStats() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats;");

        while(rs.next()) {
            String opponentName = rs.getString("playerName");

            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "';");

            while(rs2.next()) {
                String opponentAction = rs2.getString("opponent_action");

                if(!rs2.getString("board").equals("") &&
                        (opponentAction.equals("check") || opponentAction.equals("bet75pct") || opponentAction.equals("fold") || opponentAction.equals("call")
                        || opponentAction.equals("raise"))) {
                    Statement st3 = con.createStatement();
                    st3.executeUpdate("UPDATE opponentidentifier_2_0_postflopstats SET postTotal = postTotal + 1 WHERE playerName = '" + opponentName + "'");
                    st3.close();

                    double bigBlind = rs2.getDouble("bigblind");
                    double oppBetSize = rs2.getDouble("opponent_total_betsize");
                    double oppBetSizeBb = oppBetSize / bigBlind;

                    if(oppBetSizeBb >= 4) {
                        Statement st4 = con.createStatement();

                        if(oppBetSizeBb <= 6.25) {
                            st4.executeUpdate("UPDATE opponentidentifier_2_0_postflopstats SET post_4_6bb = post_4_6bb + 1 WHERE playerName = '" + opponentName + "'");
                        } else if(oppBetSizeBb <= 13.27) {
                            st4.executeUpdate("UPDATE opponentidentifier_2_0_postflopstats SET post_6_13bb = post_6_13bb + 1 WHERE playerName = '" + opponentName + "'");
                        } else {
                            st4.executeUpdate("UPDATE opponentidentifier_2_0_postflopstats SET post_13bb_up = post_13bb_up + 1 WHERE playerName = '" + opponentName + "'");
                        }

                        st4.close();
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

    private void insertAllPlayersToEmptyTable() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflopstats;");

        while(rs.next()) {
            String opponentName = rs.getString("playerName");

            Statement st2 = con.createStatement();
            st2.executeUpdate("INSERT INTO opponentidentifier_2_0_postflopstats (playerName) VALUES ('" + opponentName + "')");
            st2.close();
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private void printGroupBorders() throws Exception {
        List<Double> allPost_4_6bbRatios = new ArrayList<>();
        List<Double> allPost_6_13bbRatios = new ArrayList<>();
        List<Double> allPost_13bb_UpRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflopstats;");

        while(rs.next()) {
            double postTotal = rs.getDouble("postTotal");

            if(postTotal >= 20) {
                double post_4_6bb_Number = rs.getDouble("post_4_6bb");
                double post_6_13bb_Number = rs.getDouble("post_6_13bb");
                double post_13bb_Up_Number = rs.getDouble("post_13bb_up");

                allPost_4_6bbRatios.add(post_4_6bb_Number / postTotal);
                allPost_6_13bbRatios.add(post_6_13bb_Number / postTotal);
                allPost_13bb_UpRatios.add(post_13bb_Up_Number / postTotal);
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allPost_4_6bbRatios);
        Collections.sort(allPost_6_13bbRatios);
        Collections.sort(allPost_13bb_UpRatios);

        double oneThird = allPost_4_6bbRatios.size() * 0.33333;
        int oneThirdInt = (int) oneThird;

        double twoThird = allPost_4_6bbRatios.size() * 0.66666;
        int twoThirdInt = (int) twoThird;

        System.out.println("33pct limit post 4_6bb: " + allPost_4_6bbRatios.get(oneThirdInt));
        System.out.println("66pct limit post 4_6bb: " + allPost_4_6bbRatios.get(twoThirdInt));
        System.out.println("33pct limit post 6_13bb: " + allPost_6_13bbRatios.get(oneThirdInt));
        System.out.println("66pct limit post 6_13bb: " + allPost_6_13bbRatios.get(twoThirdInt));
        System.out.println("33pct limit post 13bb_up: " + allPost_13bb_UpRatios.get(oneThirdInt));
        System.out.println("66pct limit post 13bb_up: " + allPost_13bb_UpRatios.get(twoThirdInt));
    }

    private List<Double> getAllPostflopOppBbBetsizes() throws Exception {
        List<Double> allOppBbBetsizes = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw;");

        while(rs.next()) {
            if(!rs.getString("board").equals("")) {
                double oppBetSize = rs.getDouble("opponent_total_betsize");
                if(oppBetSize > 0) {
                    double oppBetSizeBb = oppBetSize / rs.getDouble("bigblind");

                    if(oppBetSizeBb >= 4) {
                        allOppBbBetsizes.add(oppBetSize / rs.getDouble("bigblind"));
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allOppBbBetsizes);

        return allOppBbBetsizes;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
