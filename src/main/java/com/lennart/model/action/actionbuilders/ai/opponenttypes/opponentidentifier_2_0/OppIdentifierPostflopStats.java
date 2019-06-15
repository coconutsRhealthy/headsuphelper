package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0;

import java.sql.*;
import java.util.*;

public class OppIdentifierPostflopStats {

    private Connection con;

    private static final double POST_0_3BB_33PCT_VALUE = 0.3333333333333333;
    private static final double POST_0_3BB_66PCT_VALUE = 0.5263157894736842;
    private static final double POST_3_7BB_33PCT_VALUE = 0.17391304347826086;
    private static final double PRE_3_7BB_66PCT_VALUE = 0.2857142857142857;
    private static final double PRE_7BB_UP_33PCT_VALUE = 0.23529411764705882;
    private static final double PRE_7BB_UP_66PCT_VALUE = 0.375;



    private static final double POST_4_6BB_33PCT_VALUE = 0.28;
    private static final double POST_4_6BB_66PCT_VALUE = 0.4;
    private static final double POST_6_13BB_33PCT_VALUE = 0.25;
    private static final double PRE_6_13BB_66PCT_VALUE = 0.37755102040816324;
    private static final double PRE_13BB_UP_33PCT_VALUE = 0.2564102564102564;
    private static final double PRE_13BB_UP_66PCT_VALUE = 0.391304347826087;



    public static void main(String[] args) throws Exception {
        new OppIdentifierPostflopStats().printGroupBordersNewVersion();
    }

    public Map<String, String> getOppPostGroupMap(String opponentName) throws Exception {
        Map<String, String> oppPostGroupMap = new HashMap<>();

        Map<String, Double> oppPostStatsMap = getOppPostStatsMap(opponentName);

        if(!oppPostStatsMap.isEmpty()) {
            double post_0_3bb_Number = oppPostStatsMap.get("post_0_3bb");
            double post_3_7bb_Number = oppPostStatsMap.get("post_3_7bb");
            double post_7bb_Up_Number = oppPostStatsMap.get("post_7bb_up");
            double postTotal = oppPostStatsMap.get("postTotal");

            double post_0_3bb_ratio = post_0_3bb_Number / postTotal;
            double post_3_7bb_ratio = post_3_7bb_Number / postTotal;
            double post_7bb_up_ratio = post_7bb_Up_Number / postTotal;

            if(postTotal >= 11) {
                if(post_0_3bb_ratio < POST_0_3BB_33PCT_VALUE) {
                    oppPostGroupMap.put("post_0_3bb_group", "low");
                } else if(post_0_3bb_ratio < POST_0_3BB_66PCT_VALUE) {
                    oppPostGroupMap.put("post_0_3bb_group", "medium");
                } else {
                    oppPostGroupMap.put("post_0_3bb_group", "high");
                }

                if(post_3_7bb_ratio < POST_3_7BB_33PCT_VALUE) {
                    oppPostGroupMap.put("post_3_7bb_group", "low");
                } else if(post_3_7bb_ratio < PRE_3_7BB_66PCT_VALUE) {
                    oppPostGroupMap.put("post_3_7bb_group", "medium");
                } else {
                    oppPostGroupMap.put("post_3_7bb_group", "high");
                }

                if(post_7bb_up_ratio < PRE_7BB_UP_33PCT_VALUE) {
                    oppPostGroupMap.put("post_7bb_up_group", "low");
                } else if(post_7bb_up_ratio < PRE_7BB_UP_66PCT_VALUE) {
                    oppPostGroupMap.put("post_7bb_up_group", "medium");
                } else {
                    oppPostGroupMap.put("post_7bb_up_group", "high");
                }
            } else {
                System.out.println("too few hands postflop for oppPostStatsMap: " + postTotal);
                System.out.println("put everything as 'medium' because too few hands");

                oppPostGroupMap.put("post_0_3bb_group", "medium");
                oppPostGroupMap.put("post_3_7bb_group", "medium");
                oppPostGroupMap.put("post_7bb_up_group", "medium");
            }

        } else {
            System.out.println("oppPostStatsMap empty for opponent: " + opponentName);
            System.out.println("put everyting as 'medium' as default");

            oppPostGroupMap.put("post_0_3bb_group", "medium");
            oppPostGroupMap.put("post_3_7bb_group", "medium");
            oppPostGroupMap.put("post_7bb_up_group", "medium");
        }

        return oppPostGroupMap;
    }

    private Map<String, Double> getOppPostStatsMap(String opponentName) throws Exception {
        Map<String, Double> oppPreStatsMap = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflopstats WHERE playerName = '" + opponentName + "';");

        if(rs.next()) {
            double post_0_3bb_Number = rs.getDouble("post_0_3bb");
            double post_3_7bb_Number = rs.getDouble("post_3_7bb");
            double post_7bb_Up_Number = rs.getDouble("post_7bb_up");
            double postTotal = rs.getDouble("postTotal");

            oppPreStatsMap.put("post_0_3bb", post_0_3bb_Number);
            oppPreStatsMap.put("post_3_7bb", post_3_7bb_Number);
            oppPreStatsMap.put("post_7bb_up", post_7bb_Up_Number);
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




    private void migrateRawDataToPostflopStatsNewVersion() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats;");

        while(rs.next()) {
            String opponentName = rs.getString("playerName");

            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "';");

            while(rs2.next()) {
                if(!rs2.getString("board").equals("") && (rs2.getString("opponent_action").equals("bet75pct") || rs2.getString("opponent_action").equals("raise"))) {
                    double bigBlind = rs2.getDouble("bigblind");
                    double oppBetSize = rs2.getDouble("opponent_total_betsize");
                    double oppBetSizeBb = oppBetSize / bigBlind;

                    if(oppBetSizeBb >= 4) {
                        Statement st3 = con.createStatement();
                        st3.executeUpdate("UPDATE opponentidentifier_2_0_postflopstats_new SET postTotal = postTotal + 1 WHERE playerName = '" + opponentName + "'");
                        st3.close();

                        Statement st4 = con.createStatement();

                        if(oppBetSizeBb <= 6.25) {
                            st4.executeUpdate("UPDATE opponentidentifier_2_0_postflopstats_new SET post_4_6bb = post_4_6bb + 1 WHERE playerName = '" + opponentName + "'");
                        } else if(oppBetSizeBb <= 13.27) {
                            st4.executeUpdate("UPDATE opponentidentifier_2_0_postflopstats_new SET post_6_13bb = post_6_13bb + 1 WHERE playerName = '" + opponentName + "'");
                        } else {
                            st4.executeUpdate("UPDATE opponentidentifier_2_0_postflopstats_new SET post_13bb_up = post_13bb_up + 1 WHERE playerName = '" + opponentName + "'");
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







    private void migrateRawDataToPostflopStats() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats;");

        while(rs.next()) {
            String opponentName = rs.getString("playerName");

            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "';");

            while(rs2.next()) {
                if(!rs2.getString("board").equals("") && (rs2.getString("opponent_action").equals("bet75pct") || rs2.getString("opponent_action").equals("raise"))) {
                    Statement st3 = con.createStatement();
                    st3.executeUpdate("UPDATE opponentidentifier_2_0_postflopstats SET postTotal = postTotal + 1 WHERE playerName = '" + opponentName + "'");
                    st3.close();

                    double bigBlind = rs2.getDouble("bigblind");
                    double oppBetSize = rs2.getDouble("opponent_total_betsize");
                    double oppBetSizeBb = oppBetSize / bigBlind;

                    Statement st4 = con.createStatement();

                    if(oppBetSizeBb < 1) {
                        System.out.println("below 1bb: " + rs2.getInt("entry"));
                    } else if(oppBetSizeBb <= 3) {
                        st4.executeUpdate("UPDATE opponentidentifier_2_0_postflopstats SET post_0_3bb = post_0_3bb + 1 WHERE playerName = '" + opponentName + "'");
                    } else if(oppBetSizeBb > 3 && oppBetSizeBb <= 7) {
                        st4.executeUpdate("UPDATE opponentidentifier_2_0_postflopstats SET post_3_7bb = post_3_7bb + 1 WHERE playerName = '" + opponentName + "'");
                    } else if(oppBetSizeBb > 7) {
                        st4.executeUpdate("UPDATE opponentidentifier_2_0_postflopstats SET post_7bb_up = post_7bb_up + 1 WHERE playerName = '" + opponentName + "'");
                    }

                    st4.close();
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
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats;");

        while(rs.next()) {
            String opponentName = rs.getString("playerName");

            Statement st2 = con.createStatement();
            st2.executeUpdate("INSERT INTO opponentidentifier_2_0_postflopstats_new (playerName) VALUES ('" + opponentName + "')");
            st2.close();
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private void printGroupBorders() throws Exception {
        List<Double> allPost_0_3bbRatios = new ArrayList<>();
        List<Double> allPost_3_7bbRatios = new ArrayList<>();
        List<Double> allPost_7bb_UpRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflopstats;");

        while(rs.next()) {
            double postTotal = rs.getDouble("postTotal");

            if(postTotal >= 11) {
                double post_0_3bb_Number = rs.getDouble("post_0_3bb");
                double post_3_7bb_Number = rs.getDouble("post_3_7bb");
                double post_7bb_Up_Number = rs.getDouble("post_7bb_up");

                allPost_0_3bbRatios.add(post_0_3bb_Number / postTotal);
                allPost_3_7bbRatios.add(post_3_7bb_Number / postTotal);
                allPost_7bb_UpRatios.add(post_7bb_Up_Number / postTotal);
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allPost_0_3bbRatios);
        Collections.sort(allPost_3_7bbRatios);
        Collections.sort(allPost_7bb_UpRatios);

        double oneThird = allPost_0_3bbRatios.size() * 0.33333;
        int oneThirdInt = (int) oneThird;

        double twoThird = allPost_0_3bbRatios.size() * 0.66666;
        int twoThirdInt = (int) twoThird;

        System.out.println("33pct limit post 0_3bb: " + allPost_0_3bbRatios.get(oneThirdInt));
        System.out.println("66pct limit post 0_3bb: " + allPost_0_3bbRatios.get(twoThirdInt));
        System.out.println("33pct limit post 3_7bb: " + allPost_3_7bbRatios.get(oneThirdInt));
        System.out.println("66pct limit post 3_7bb: " + allPost_3_7bbRatios.get(twoThirdInt));
        System.out.println("33pct limit post 7bb_up: " + allPost_7bb_UpRatios.get(oneThirdInt));
        System.out.println("66pct limit post 7bb_up: " + allPost_7bb_UpRatios.get(twoThirdInt));
    }




    private void printGroupBordersNewVersion() throws Exception {
        List<Double> allPost_4_6bbRatios = new ArrayList<>();
        List<Double> allPost_6_13bbRatios = new ArrayList<>();
        List<Double> allPost_13bb_UpRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflopstats_new;");

        while(rs.next()) {
            double postTotal = rs.getDouble("postTotal");

            if(postTotal >= 10) {
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
