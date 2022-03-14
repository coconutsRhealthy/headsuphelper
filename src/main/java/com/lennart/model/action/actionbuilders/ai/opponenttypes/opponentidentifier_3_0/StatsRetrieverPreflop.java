package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 05/03/2022.
 */
public class StatsRetrieverPreflop {

    private Connection con;

    private final int NUMBER_OF_HANDS_UNKNOWN_BOUNDRY = 15;

    public static void main(String[] args) throws Exception {
        new StatsRetrieverPreflop().getPreflopStats("zdsdsdfds");
    }

    public Map<String, Double> getPreflopStats(String opponentName) throws Exception {
        Map<String, Double> absoluteStatsForOpp = getAbsoluteStatsForOpponent(opponentName);
        Map<String, Double> relativeStatsForOpp;
        double numberOfHands = absoluteStatsForOpp.get("numberOfHands");
        boolean unknownOpp = false;

        if(numberOfHands < NUMBER_OF_HANDS_UNKNOWN_BOUNDRY) {
            unknownOpp = true;
            Map<String, Double> absoluteStatsForTypicalUnknown = getAbsoluteStatsForTypicalUnknown(numberOfHands);
            relativeStatsForOpp = getRelativeStatsForOpponent(absoluteStatsForTypicalUnknown);
        } else {
            relativeStatsForOpp = getRelativeStatsForOpponent(absoluteStatsForOpp);
        }

        logRelativeStats(relativeStatsForOpp, opponentName, numberOfHands, unknownOpp);
        return relativeStatsForOpp;
    }

    private Map<String, Double> getRelativeStatsForOpponent(Map<String, Double> absoluteStats) throws Exception {
        List<Double> all2betRatios = getAll2betRatios();
        List<Double> allShoveRatios = getAllShoveRatios();
        List<Double> allCall2betRatios = getAllCall2betRatios();
        List<Double> allIpRaiseRatios = getAllIpRaiseRatios();
        List<Double> allOopRaiseRatios = getAllOopRaiseRatios();
        List<Double> allOverallRaiseRatios = getAllOverallRaiseRatios();
        List<Double> allOverallCallRatios = getAllOverallCallRatios();

        double absolute2betRatio = absoluteStats.get("_2betRatioForPlayer");
        double absoluteShoveRatio = absoluteStats.get("shoveRatioForPlayer");
        double absoluteCall2betRatio = absoluteStats.get("call2betRatioForPlayer");
        double absoluteIpRaiseRatio = absoluteStats.get("ipRaiseRatioForPlayer");
        double absoluteOopRaiseRatio = absoluteStats.get("oopRaiseRatioForPlayer");
        double absoluteOverallRaiseRatio = absoluteStats.get("overallRaiseRatioForPlayer");
        double absoluteOverallCallRatio = absoluteStats.get("overallCallRatioForPlayer");

        double relative2betRatio = countNumberOfElementsBelowValue(all2betRatios, absolute2betRatio) / (double) all2betRatios.size();
        double relativeShoveRatio = countNumberOfElementsBelowValue(allShoveRatios, absoluteShoveRatio) / (double) allShoveRatios.size();
        double relativeCall2betRatio = countNumberOfElementsBelowValue(allCall2betRatios, absoluteCall2betRatio) / (double) allCall2betRatios.size();
        double relativeIpRaiseRatio = countNumberOfElementsBelowValue(allIpRaiseRatios, absoluteIpRaiseRatio) / (double) allIpRaiseRatios.size();
        double relativeOopRaiseRatio = countNumberOfElementsBelowValue(allOopRaiseRatios, absoluteOopRaiseRatio) / (double) allOopRaiseRatios.size();
        double relativeOverallRaiseRatio = countNumberOfElementsBelowValue(allOverallRaiseRatios, absoluteOverallRaiseRatio) / (double) allOverallRaiseRatios.size();
        double relativeOverallCallRatio = countNumberOfElementsBelowValue(allOverallCallRatios, absoluteOverallCallRatio) / (double) allOverallCallRatios.size();

        Map<String, Double> relativeStatsForOpp = new HashMap<>();
        relativeStatsForOpp.put("relative2betRatio", relative2betRatio);
        relativeStatsForOpp.put("relativeShoveRatio", relativeShoveRatio);
        relativeStatsForOpp.put("relativeCall2betRatio", relativeCall2betRatio);
        relativeStatsForOpp.put("relativeIpRaiseRatio", relativeIpRaiseRatio);
        relativeStatsForOpp.put("relativeOopRaiseRatio", relativeOopRaiseRatio);
        relativeStatsForOpp.put("relativeOverallRaiseRatio", relativeOverallRaiseRatio);
        relativeStatsForOpp.put("relativeOverallCallRatio", relativeOverallCallRatio);

        return relativeStatsForOpp;
    }

    private Map<String, Double> getAbsoluteStatsForOpponent(String oppName) throws Exception {
        double numberOfHands = -1;
        double _2betRatioForPlayer = -1;
        double shoveRatioForPlayer = -1;
        double call2betRatioForPlayer = -1;
        double ipRaiseRatioForPlayer = -1;
        double oopRaiseRatioForPlayer = -1;
        double overallRaiseRatioForPlayer = -1;
        double overallCallRatioForPlayer = -1;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats_party WHERE playerName = '" + oppName + "';");

        while(rs.next()) {
            _2betRatioForPlayer = rs.getDouble("pre2bet") / rs.getDouble("preTotal");
            shoveRatioForPlayer = (rs.getDouble("pre3bet") + rs.getDouble("pre4bet_up")) / rs.getDouble("preTotal");
            call2betRatioForPlayer = rs.getDouble("pre_call2bet") / rs.getDouble("preTotal");
        }

        rs.close();
        st.close();

        Statement st2 = con.createStatement();
        ResultSet rs2 = st2.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop_party WHERE playerName = '" + oppName + "';");

        while(rs2.next()) {
            ipRaiseRatioForPlayer = rs2.getDouble("ipRaiseCount") / rs2.getDouble("numberOfHands");
            oopRaiseRatioForPlayer = rs2.getDouble("oopRaiseCount") / rs2.getDouble("numberOfHands");
            overallRaiseRatioForPlayer = rs2.getDouble("raiseCount") / rs2.getDouble("numberOfHands");
            overallCallRatioForPlayer = rs2.getDouble("callCount") / rs2.getDouble("numberOfHands");
            numberOfHands = rs2.getDouble("numberOfHands");
        }

        rs2.close();
        st2.close();

        closeDbConnection();

        Map<String, Double> absolutStatsForOpp = new HashMap<>();
        absolutStatsForOpp.put("_2betRatioForPlayer", _2betRatioForPlayer);
        absolutStatsForOpp.put("shoveRatioForPlayer", shoveRatioForPlayer);
        absolutStatsForOpp.put("call2betRatioForPlayer", call2betRatioForPlayer);
        absolutStatsForOpp.put("ipRaiseRatioForPlayer", ipRaiseRatioForPlayer);
        absolutStatsForOpp.put("oopRaiseRatioForPlayer", oopRaiseRatioForPlayer);
        absolutStatsForOpp.put("overallRaiseRatioForPlayer", overallRaiseRatioForPlayer);
        absolutStatsForOpp.put("overallCallRatioForPlayer", overallCallRatioForPlayer);
        absolutStatsForOpp.put("numberOfHands", numberOfHands);

        return absolutStatsForOpp;
    }

    private Map<String, Double> getAbsoluteStatsForTypicalUnknown(double handsPlayedAgainstUnknown) throws Exception {
        initializeDbConnection();

        List<Double> _2betRatios = new ArrayList<>();
        List<Double> shoveRatios = new ArrayList<>();
        List<Double> call2betRatios = new ArrayList<>();
        List<Double> ipRaiseRatios = new ArrayList<>();
        List<Double> oopRaiseRatios = new ArrayList<>();
        List<Double> overallRaiseRatios = new ArrayList<>();
        List<Double> overallCallRatios = new ArrayList<>();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats_party;");

        while(rs.next()) {
            if(rs.getDouble("preTotal") >= handsPlayedAgainstUnknown) {
                double _2betRatio = rs.getDouble("pre2bet") / rs.getDouble("preTotal");
                double shoveRatio = (rs.getDouble("pre3bet") + rs.getDouble("pre4bet_up")) / rs.getDouble("preTotal");
                double call2betRatio = rs.getDouble("pre_call2bet") / rs.getDouble("preTotal");

                if(!Double.isNaN(_2betRatio) && !Double.isInfinite(_2betRatio)) {
                    _2betRatios.add(_2betRatio);
                }

                if(!Double.isNaN(shoveRatio) && !Double.isInfinite(shoveRatio)) {
                    shoveRatios.add(shoveRatio);
                }

                if(!Double.isNaN(call2betRatio) && !Double.isInfinite(call2betRatio)) {
                    call2betRatios.add(call2betRatio);
                }
            }
        }

        rs.close();
        st.close();

        Statement st2 = con.createStatement();
        ResultSet rs2 = st2.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop_party;");

        while(rs2.next()) {
            if(rs2.getDouble("numberOfHands") >= handsPlayedAgainstUnknown) {
                double ipRaiseRatio = rs2.getDouble("ipRaiseCount") / rs2.getDouble("numberOfHands");
                double oopRaiseRatio = rs2.getDouble("oopRaiseCount") / rs2.getDouble("numberOfHands");
                double overallRaiseRatio = rs2.getDouble("raiseCount") / rs2.getDouble("numberOfHands");
                double overallCallRatio = rs2.getDouble("callCount") / rs2.getDouble("numberOfHands");

                if(!Double.isNaN(ipRaiseRatio) && !Double.isInfinite(ipRaiseRatio)) {
                    ipRaiseRatios.add(ipRaiseRatio);
                }

                if(!Double.isNaN(oopRaiseRatio) && !Double.isInfinite(oopRaiseRatio)) {
                    oopRaiseRatios.add(oopRaiseRatio);
                }

                if(!Double.isNaN(overallRaiseRatio) && !Double.isInfinite(overallRaiseRatio)) {
                    overallRaiseRatios.add(overallRaiseRatio);
                }

                if(!Double.isNaN(overallCallRatio) && !Double.isInfinite(overallCallRatio)) {
                    overallCallRatios.add(overallCallRatio);
                }
            }
        }

        rs2.close();
        st2.close();

        closeDbConnection();

        Collections.sort(_2betRatios);
        Collections.sort(shoveRatios);
        Collections.sort(call2betRatios);
        Collections.sort(ipRaiseRatios);
        Collections.sort(oopRaiseRatios);
        Collections.sort(overallRaiseRatios);
        Collections.sort(overallCallRatios);

        double average2betRatio = (_2betRatios.stream().mapToDouble(Double::doubleValue).sum()) / _2betRatios.size();
        double averageShoveRatio = (shoveRatios.stream().mapToDouble(Double::doubleValue).sum()) / shoveRatios.size();
        double averageCall2betRatio = (call2betRatios.stream().mapToDouble(Double::doubleValue).sum()) / call2betRatios.size();
        double averageIpRaiseRatio = (ipRaiseRatios.stream().mapToDouble(Double::doubleValue).sum()) / ipRaiseRatios.size();
        double averageOopRaiseRatio = (oopRaiseRatios.stream().mapToDouble(Double::doubleValue).sum()) / oopRaiseRatios.size();
        double averageOverallRaiseRatio = (overallRaiseRatios.stream().mapToDouble(Double::doubleValue).sum()) / overallRaiseRatios.size();
        double averageOverallCallRatio = (overallCallRatios.stream().mapToDouble(Double::doubleValue).sum()) / overallCallRatios.size();

        Map<String, Double> statsForOpp = new HashMap<>();
        statsForOpp.put("_2betRatioForPlayer", average2betRatio);
        statsForOpp.put("shoveRatioForPlayer", averageShoveRatio);
        statsForOpp.put("call2betRatioForPlayer", averageCall2betRatio);
        statsForOpp.put("ipRaiseRatioForPlayer", averageIpRaiseRatio);
        statsForOpp.put("oopRaiseRatioForPlayer", averageOopRaiseRatio);
        statsForOpp.put("overallRaiseRatioForPlayer", averageOverallRaiseRatio);
        statsForOpp.put("overallCallRatioForPlayer", averageOverallCallRatio);

        return statsForOpp;
    }

    private List<Double> getAll2betRatios() throws Exception {
        List<Double> all2betRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats_party;");

        while(rs.next()) {
            double betRatio = rs.getDouble("pre2bet") / rs.getDouble("preTotal");
            all2betRatios.add(betRatio);
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(all2betRatios);
        return all2betRatios;
    }

    private List<Double> getAllShoveRatios() throws Exception {
        List<Double> allShoveRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats_party;");

        while(rs.next()) {
            double shoveRatio = (rs.getDouble("pre3bet") + rs.getDouble("pre4bet_up")) / rs.getDouble("preTotal");
            allShoveRatios.add(shoveRatio);
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allShoveRatios);
        return allShoveRatios;
    }

    private List<Double> getAllCall2betRatios() throws Exception {
        List<Double> allCall2betRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats_party;");

        while(rs.next()) {
            double call2betRatio = rs.getDouble("pre_call2bet") / rs.getDouble("preTotal");
            allCall2betRatios.add(call2betRatio);
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allCall2betRatios);
        return allCall2betRatios;
    }

    private List<Double> getAllIpRaiseRatios() throws Exception {
        List<Double> allIpRaiseRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop_party;");

        while(rs.next()) {
            double ipRaiseRatio = rs.getDouble("ipRaiseCount") / rs.getDouble("numberOfHands");
            allIpRaiseRatios.add(ipRaiseRatio);
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allIpRaiseRatios);
        return allIpRaiseRatios;
    }

    private List<Double> getAllOopRaiseRatios() throws Exception {
        List<Double> allOopRaiseRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop_party;");

        while(rs.next()) {
            double oopRaiseRatio = rs.getDouble("oopRaiseCount") / rs.getDouble("numberOfHands");
            allOopRaiseRatios.add(oopRaiseRatio);
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allOopRaiseRatios);
        return allOopRaiseRatios;
    }

    private List<Double> getAllOverallRaiseRatios() throws Exception {
        List<Double> allOverallRaiseRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop_party;");

        while(rs.next()) {
            double overallRaiseRatio = rs.getDouble("raiseCount") / rs.getDouble("numberOfHands");
            allOverallRaiseRatios.add(overallRaiseRatio);
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allOverallRaiseRatios);
        return allOverallRaiseRatios;
    }

    private List<Double> getAllOverallCallRatios() throws Exception {
        List<Double> allOverallCallRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop_party;");

        while(rs.next()) {
            double overallCallRatio = rs.getDouble("callCount") / rs.getDouble("numberOfHands");
            allOverallCallRatios.add(overallCallRatio);
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allOverallCallRatios);
        return allOverallCallRatios;
    }

    private void logRelativeStats(Map<String, Double> relativeStats, String oppName, double numberOfHands, boolean unknown) {
        System.out.println();
        System.out.println("##Preflp relative stats##");
        System.out.println("opp: " + oppName + ", h: " + (int) numberOfHands);

        if(unknown) {
            System.out.println("stats for unknown");
        }

        System.out.print("2bet: " + convertToLogFormat(relativeStats.get("relative2betRatio")) + " ");
        System.out.print("shv: " + convertToLogFormat(relativeStats.get("relativeShoveRatio")) + " ");
        System.out.print("c2b: " + convertToLogFormat(relativeStats.get("relativeCall2betRatio")) + " ");
        System.out.print("IPr: " + convertToLogFormat(relativeStats.get("relativeIpRaiseRatio")) + " ");
        System.out.print("OOPr: " + convertToLogFormat(relativeStats.get("relativeOopRaiseRatio")) + " ");
        System.out.print("OR: " + convertToLogFormat(relativeStats.get("relativeOverallRaiseRatio")) + " ");
        System.out.println("OC: " + convertToLogFormat(relativeStats.get("relativeOverallCallRatio")));
        System.out.println("####");
        System.out.println();
    }

    private int convertToLogFormat(double value) {
        value = value * 100;
        value = ((double)Math.round(value * 1d) / 1d);
        return (int) value;
     }

    private double countNumberOfElementsBelowValue(List<Double> allValues, double limitValue) {
        double counter = 0;

        for(Double value : allValues) {
            if(value < limitValue) {
                counter++;
            } else {
                break;
            }
        }

        return counter;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
