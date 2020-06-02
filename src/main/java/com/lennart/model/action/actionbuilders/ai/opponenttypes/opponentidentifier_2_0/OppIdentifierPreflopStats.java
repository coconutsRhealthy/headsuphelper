package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 24/05/2019.
 */
public class OppIdentifierPreflopStats {

    private Connection con;

    private static final double PRE_2_BET_33PCT_VALUE = 0.175;
    private static final double PRE_2_BET_66PCT_VALUE = 0.4166666666666667;
    private static final double PRE_3_BET_33PCT_VALUE = 0.07692307692307693;
    private static final double PRE_3_BET_66PCT_VALUE = 0.17307692307692307;
    private static final double PRE_4_BET_33PCT_VALUE = 0.031496062992125984;
    private static final double PRE_4_BET_66PCT_VALUE = 0.14814814814814814;

    private static final double PRE_CALL_2_BET_33PCT_VALUE = 0.3333333333333333;
    private static final double PRE_CALL_2_BET_66PCT_VALUE = 0.5;
    private static final double PRE_CALL_3_BET_33PCT_VALUE = 0.0;
    private static final double PRE_CALL_3_BET_66PCT_VALUE = 0.0625;
    private static final double PRE_CALL_4_BET_33PCT_VALUE = 0.0;
    private static final double PRE_CALL_4_BET_66PCT_VALUE = 0.041666666666666664;


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

            double preCall2betNumber = oppPreStatsMap.get("pre_call2bet");
            double preCall3betNumber = oppPreStatsMap.get("pre_call3bet");
            double preCall4bet_up_Number = oppPreStatsMap.get("pre_call4bet_up");
            double preCallTotal = oppPreStatsMap.get("preCallTotal");

            double pre2betRatio = pre2betNumber / preTotal;
            double pre3betRatio = pre3betNumber / preTotal;
            double pre4bet_up_ratio = pre4bet_up_Number / preTotal;

            double preCall2betRatio = preCall2betNumber / preCallTotal;
            double preCall3betRatio = preCall3betNumber / preCallTotal;
            double preCall4betRatio = preCall4bet_up_Number / preCallTotal;

            if(preTotal >= 9) {
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

                if(preCall2betRatio < PRE_CALL_2_BET_33PCT_VALUE) {
                    oppPreGroupMap.put("preCall2betGroup", "low");
                } else if(preCall2betRatio < PRE_CALL_2_BET_66PCT_VALUE) {
                    oppPreGroupMap.put("preCall2betGroup", "medium");
                } else {
                    oppPreGroupMap.put("preCall2betGroup", "high");
                }

                if(preCall3betRatio < PRE_CALL_3_BET_33PCT_VALUE) {
                    oppPreGroupMap.put("preCall3betGroup", "low");
                } else if(preCall3betRatio < PRE_CALL_3_BET_66PCT_VALUE) {
                    oppPreGroupMap.put("preCall3betGroup", "medium");
                } else {
                    oppPreGroupMap.put("preCall3betGroup", "high");
                }

                if(preCall4betRatio < PRE_CALL_4_BET_33PCT_VALUE) {
                    oppPreGroupMap.put("preCall4bet_up_group", "low");
                } else if(preCall4betRatio < PRE_CALL_4_BET_66PCT_VALUE) {
                    oppPreGroupMap.put("preCall4bet_up_group", "medium");
                } else {
                    oppPreGroupMap.put("preCall4bet_up_group", "high");
                }
            } else {
                oppPreGroupMap.put("pre2betGroup", "medium");
                oppPreGroupMap.put("pre3betGroup", "mediumUnknown");
                oppPreGroupMap.put("pre4bet_up_group", "mediumUnknown");

                oppPreGroupMap.put("preCall2betGroup", "medium");
                oppPreGroupMap.put("preCall3betGroup", "mediumUnknown");
                oppPreGroupMap.put("preCall4bet_up_group", "mediumUnknown");
            }

        } else {
            System.out.println("oppPreStatsMap empty for opponent: " + opponentName);
            System.out.println("put everyting as 'medium' as default");

            oppPreGroupMap.put("pre2betGroup", "medium");
            oppPreGroupMap.put("pre3betGroup", "mediumUnknown");
            oppPreGroupMap.put("pre4bet_up_group", "mediumUnknown");

            oppPreGroupMap.put("preCall2betGroup", "medium");
            oppPreGroupMap.put("preCall3betGroup", "mediumUnknown");
            oppPreGroupMap.put("preCall4bet_up_group", "mediumUnknown");
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

            double preCall2betNumber = rs.getDouble("pre2bet");
            double preCall3betNumber = rs.getDouble("pre3bet");
            double preCall4bet_up_Number = rs.getDouble("pre4bet_up");
            double preCallTotal = rs.getDouble("preTotal");

            oppPreStatsMap.put("pre_call2bet", preCall2betNumber);
            oppPreStatsMap.put("pre_call3bet", preCall3betNumber);
            oppPreStatsMap.put("pre_call4bet_up", preCall4bet_up_Number);
            oppPreStatsMap.put("preCallTotal", preCallTotal);
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

    private void migrateRawDataToPreflopStatsCall() throws Exception {
        int counter = 0;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop;");

        while(rs.next()) {
            String opponentName = rs.getString("playerName");

            Statement st2 = con.createStatement();
            ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "' ORDER BY entry ASC;");

            while(rs2.next()) {
                if(rs2.getString("board").equals("") && rs2.getString("bot_action").equals("raise")) {
                    Statement st3 = con.createStatement();

                    st3.executeUpdate("UPDATE opponentidentifier_2_0_preflopstats SET preCallTotal = preCallTotal + 1 WHERE playerName = '" + opponentName + "'");

                    double botPfRaiseSize = rs2.getDouble("sizing");

                    if(rs2.next()) {
                        if(!rs2.getString("board").equals("")) {
                            double bigBlind = rs2.getDouble("bigblind");
                            double botPfRaiseSizeBb = botPfRaiseSize / bigBlind;

                            if(botPfRaiseSizeBb > 1 && botPfRaiseSizeBb <= 3) {
                                st3.executeUpdate("UPDATE opponentidentifier_2_0_preflopstats SET pre_call2bet = pre_call2bet + 1 WHERE playerName = '" + opponentName + "'");
                            } else if(botPfRaiseSizeBb > 3 && botPfRaiseSizeBb <= 10) {
                                st3.executeUpdate("UPDATE opponentidentifier_2_0_preflopstats SET pre_call3bet = pre_call3bet + 1 WHERE playerName = '" + opponentName + "'");
                            } else if(botPfRaiseSizeBb > 10) {
                                st3.executeUpdate("UPDATE opponentidentifier_2_0_preflopstats SET pre_call4bet_up = pre_call4bet_up + 1 WHERE playerName = '" + opponentName + "'");
                            }

                            System.out.println(counter++);
                        }

                        rs2.previous();
                    }

                    st3.close();
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

    private void migrateRawDataToPreflopStatsCall4betUp() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw ORDER BY entry ASC;");

        int counter = 0;

        while(rs.next()) {
            counter++;

            if(counter == 1000) {
                System.out.println(rs.getRow());
                counter = 0;
            }

            if(rs.getString("board").equals("") && rs.getString("bot_action").equals("raise")) {
                double botPfRaiseSize = rs.getDouble("sizing");
                double bigBlind = rs.getDouble("bigblind");
                double botStack = rs.getDouble("botstack");
                double oppStack = rs.getDouble("opponentstack");

                double botPfRaiseSizeBb = botPfRaiseSize / bigBlind;
                double botStackBb = botStack / bigBlind;
                double oppStackBb = oppStack / bigBlind;

                if(botPfRaiseSizeBb > 10 && botStackBb > 10 && oppStackBb > 10) {
                    if(rs.getString("showdown_occured").equals("true")) {
                        String opponentName = rs.getString("opponent_name");

                        Statement st2 = con.createStatement();

                        st2.executeUpdate("UPDATE opponentidentifier_2_0_preflopstats SET pre_call4bet_up = pre_call4bet_up + 1 WHERE playerName = '" + opponentName + "'");

                        st2.close();

                        System.out.println("done: " + rs.getRow());
                    }
                }
            }
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

        List<Double> allPreCall2betRatios = new ArrayList<>();
        List<Double> allPreCall3betRatios = new ArrayList<>();
        List<Double> allPreCall4betRatios = new ArrayList<>();

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

            double preCallTotal = rs.getDouble("preCallTotal");

            if(preCallTotal >= 11) {
                double preCall2betNumber = rs.getDouble("pre_call2bet");
                double preCall3betNumber = rs.getDouble("pre_call3bet");
                double preCall4bet_up_Number = rs.getDouble("pre_call4bet_up");

                allPreCall2betRatios.add(preCall2betNumber / preCallTotal);
                allPreCall3betRatios.add(preCall3betNumber / preCallTotal);
                allPreCall4betRatios.add(preCall4bet_up_Number / preCallTotal);
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

        System.out.println();

        Collections.sort(allPreCall2betRatios);
        Collections.sort(allPreCall3betRatios);
        Collections.sort(allPreCall4betRatios);

        double oneThirdCall = allPreCall2betRatios.size() * 0.33333;
        int oneThirdIntCall = (int) oneThirdCall;

        double twoThirdCall = allPreCall2betRatios.size() * 0.66666;
        int twoThirdIntCall = (int) twoThirdCall;

        if(allPreCall4betRatios.get(twoThirdIntCall) == 0.0) {


        }

        System.out.println("33pct limit call_2bet: " + allPreCall2betRatios.get(oneThirdIntCall));
        System.out.println("66pct limit call_2bet: " + allPreCall2betRatios.get(twoThirdIntCall));
        System.out.println("33pct limit call_3bet: " + allPreCall3betRatios.get(oneThirdIntCall));
        System.out.println("66pct limit call_3bet: " + allPreCall3betRatios.get(twoThirdIntCall));
        System.out.println("33pct limit call_4bet: " + allPreCall4betRatios.get(oneThirdIntCall));
        System.out.println("66pct limit call_4bet: " + getCall4betTwoThirdLimit(allPreCall4betRatios, twoThirdIntCall));
    }

    private double getCall4betTwoThirdLimit(List<Double> allPreCall4betRatios, int twoThirdInt) {
        double valueToReturn;

        double normalTwoThird = allPreCall4betRatios.get(twoThirdInt);

        if(normalTwoThird == 0) {
            List<Double> preCall4betRatiosAboveZero = allPreCall4betRatios.stream().filter(ratio -> ratio > 0).collect(Collectors.toList());
            valueToReturn = preCall4betRatiosAboveZero.get((int) (0.4 * preCall4betRatiosAboveZero.size()));
        } else {
            valueToReturn = normalTwoThird;
        }

        return valueToReturn;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
