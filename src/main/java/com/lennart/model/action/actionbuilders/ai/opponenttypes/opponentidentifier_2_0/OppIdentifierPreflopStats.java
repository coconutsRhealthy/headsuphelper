package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 24/05/2019.
 */
public class OppIdentifierPreflopStats {

    private Connection con;

    private static final double PRE_2_BET_33PCT_VALUE = 0.2980769230769231;
    private static final double PRE_2_BET_66PCT_VALUE = 0.56;
    private static final double PRE_3_BET_33PCT_VALUE = 0.07692307692307693;
    private static final double PRE_3_BET_66PCT_VALUE = 0.15789473684210525;
    private static final double PRE_4_BET_33PCT_VALUE = 0.037037037037037035;
    private static final double PRE_4_BET_66PCT_VALUE = 0.12903225806451613;

    public static void main(String[] args) throws Exception {
        //new OppIdentifierPreflopStats().getOppPreGroupMap("Hentasy");
        new OppIdentifierPreflopStats().printGroupBorders();
    }

    public Map<String, String> getOppPreGroupMap(String opponentName) throws Exception {
        Map<String, String> oppPreGroupMap = new HashMap<>();

        Map<String, Double> oppPreStatsMap = getOppPreStatsMap(opponentName);

        if(!oppPreStatsMap.isEmpty()) {
            double pre2betNumber = oppPreStatsMap.get("pre2bet");
            double pre3betNumber = oppPreStatsMap.get("pre3bet");
            double pre4bet_up_Number = oppPreStatsMap.get("pre4bet_up");
            double preTotal = oppPreStatsMap.get("preTotal");

            double pre2betRatio = pre2betNumber / preTotal;
            double pre3betRatio = pre3betNumber / preTotal;
            double pre4bet_up_ratio = pre4bet_up_Number / preTotal;

            if(preTotal >= 11) {
                if(pre2betRatio < PRE_2_BET_33PCT_VALUE) {
                    oppPreGroupMap.put("pre2betGroup", "low");
                } else if(pre2betRatio < PRE_2_BET_66PCT_VALUE) {
                    oppPreGroupMap.put("pre2betGroup", "medium");
                } else {
                    oppPreGroupMap.put("pre2betGroup", "high");
                }

                if(pre3betRatio < PRE_3_BET_33PCT_VALUE) {
                    oppPreGroupMap.put("pre3betGroup", "low");
                } else if(pre3betRatio < PRE_3_BET_66PCT_VALUE) {
                    oppPreGroupMap.put("pre3betGroup", "medium");
                } else {
                    oppPreGroupMap.put("pre3betGroup", "high");
                }

                if(pre4bet_up_ratio < PRE_4_BET_33PCT_VALUE) {
                    oppPreGroupMap.put("pre4bet_up_group", "low");
                } else if(pre4bet_up_ratio < PRE_4_BET_66PCT_VALUE) {
                    oppPreGroupMap.put("pre4bet_up_group", "medium");
                } else {
                    oppPreGroupMap.put("pre4bet_up_group", "high");
                }
            } else {
                System.out.println("too few hands preflop for oppPreStatsMap: " + preTotal);
                System.out.println("put everything as 'medium' because too few hands");

                oppPreGroupMap.put("pre2betGroup", "medium");
                oppPreGroupMap.put("pre3betGroup", "mediumUnknown");
                oppPreGroupMap.put("pre4bet_up_group", "mediumUnknown");
            }

        } else {
            System.out.println("oppPreStatsMap empty for opponent: " + opponentName);
            System.out.println("put everyting as 'medium' as default");

            oppPreGroupMap.put("pre2betGroup", "medium");
            oppPreGroupMap.put("pre3betGroup", "mediumUnknown");
            oppPreGroupMap.put("pre4bet_up_group", "mediumUnknown");
        }

        return oppPreGroupMap;
    }

    private Map<String, Double> getOppPreStatsMap(String opponentName) throws Exception {
        Map<String, Double> oppPreStatsMap = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats WHERE playerName = '" + opponentName + "';");

        if(rs.next()) {
            double pre2betNumber = rs.getDouble("pre2bet");
            double pre3betNumber = rs.getDouble("pre3bet");
            double pre4bet_up_Number = rs.getDouble("pre4bet_up");
            double preTotal = rs.getDouble("preTotal");

            oppPreStatsMap.put("pre2bet", pre2betNumber);
            oppPreStatsMap.put("pre3bet", pre3betNumber);
            oppPreStatsMap.put("pre4bet_up", pre4bet_up_Number);
            oppPreStatsMap.put("preTotal", preTotal);
        } else {
            System.out.println("Can't find opponent in opponentidentifier_2_0_preflopstats! Will add to db");

            Statement st2 = con.createStatement();
            st2.executeUpdate("INSERT INTO opponentidentifier_2_0_preflopstats (playerName) VALUES ('" + opponentName + "')");
            st2.close();
        }

        rs.close();
        st.close();

        closeDbConnection();

        return oppPreStatsMap;
    }

    private void migrateRawDataToPreflopStats() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop;");

        while(rs.next()) {
            String opponentName = rs.getString("playerName");

            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "';");

            while(rs2.next()) {
                if(rs2.getString("board").equals("") && !rs2.getString("opponent_action").equals("bet")) {
                    Statement st3 = con.createStatement();
                    st3.executeUpdate("UPDATE opponentidentifier_2_0_preflopstats SET preTotal = preTotal + 1 WHERE playerName = '" + opponentName + "'");
                    st3.close();

                    if(rs2.getString("opponent_action").equals("raise")) {
                        double bigBlind = rs2.getDouble("bigblind");
                        double oppBetSize = rs2.getDouble("opponent_total_betsize");
                        double oppBetSizeBb = oppBetSize / bigBlind;

                        Statement st4 = con.createStatement();

                        if(oppBetSizeBb > 1 && oppBetSizeBb <= 3) {
                            st4.executeUpdate("UPDATE opponentidentifier_2_0_preflopstats SET pre2bet = pre2bet + 1 WHERE playerName = '" + opponentName + "'");
                        } else if(oppBetSizeBb > 3 && oppBetSizeBb <= 10) {
                            st4.executeUpdate("UPDATE opponentidentifier_2_0_preflopstats SET pre3bet = pre3bet + 1 WHERE playerName = '" + opponentName + "'");
                        } else if(oppBetSizeBb > 10) {
                            st4.executeUpdate("UPDATE opponentidentifier_2_0_preflopstats SET pre4bet_up = pre4bet_up + 1 WHERE playerName = '" + opponentName + "'");
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

    private void printCountsFromDbStatsRaw(String opponentName) throws Exception {
        double oppTotalCount = 0;
        double opp2betCount = 0;
        double opp3betCount = 0;
        double opp4betCount = 0;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "';");

        while(rs.next()) {
            if(rs.getString("board").equals("") && !rs.getString("opponent_action").equals("bet")) {
                oppTotalCount++;

                if(rs.getString("opponent_action").equals("raise")) {
                    double bigBlind = rs.getDouble("bigblind");
                    double oppBetSize = rs.getDouble("opponent_total_betsize");
                    double oppBetSizeBb = oppBetSize / bigBlind;

                    if(oppBetSizeBb > 1 && oppBetSizeBb <= 3) {
                        opp2betCount++;
                    } else if(oppBetSizeBb > 3 && oppBetSizeBb <= 10) {
                        opp3betCount++;
                    } else if(oppBetSizeBb > 10) {
                        opp4betCount++;
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println("oppTotalCount: " + oppTotalCount);
        System.out.println("opp2betCount: " + opp2betCount);
        System.out.println("opp3betCount: " + opp3betCount);
        System.out.println("opp4betCount: " + opp4betCount);
    }

    private void printGroupBorders() throws Exception {
        List<Double> allPre2betRatios = new ArrayList<>();
        List<Double> allPre3betRatios = new ArrayList<>();
        List<Double> allPre4betRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats;");

        while(rs.next()) {
            double preTotal = rs.getDouble("preTotal");

            if(preTotal >= 11) {
                double pre2betNumber = rs.getDouble("pre2bet");
                double pre3betNumber = rs.getDouble("pre3bet");
                double pre4bet_up_Number = rs.getDouble("pre4bet_up");

                allPre2betRatios.add(pre2betNumber / preTotal);
                allPre3betRatios.add(pre3betNumber / preTotal);
                allPre4betRatios.add(pre4bet_up_Number / preTotal);
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allPre2betRatios);
        Collections.sort(allPre3betRatios);
        Collections.sort(allPre4betRatios);

        double oneThird = allPre2betRatios.size() * 0.33333;
        int oneThirdInt = (int) oneThird;

        double twoThird = allPre2betRatios.size() * 0.66666;
        int twoThirdInt = (int) twoThird;

        System.out.println("33pct limit 2bet: " + allPre2betRatios.get(oneThirdInt));
        System.out.println("66pct limit 2bet: " + allPre2betRatios.get(twoThirdInt));
        System.out.println("33pct limit 3bet: " + allPre3betRatios.get(oneThirdInt));
        System.out.println("66pct limit 3bet: " + allPre3betRatios.get(twoThirdInt));
        System.out.println("33pct limit 4bet: " + allPre4betRatios.get(oneThirdInt));
        System.out.println("66pct limit 4bet: " + allPre4betRatios.get(twoThirdInt));
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}