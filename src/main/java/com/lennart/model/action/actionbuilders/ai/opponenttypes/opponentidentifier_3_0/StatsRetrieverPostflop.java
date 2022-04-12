package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 02/03/2022.
 */
public class StatsRetrieverPostflop {

    private Connection con;

    private final int NUMBER_OF_HANDS_UNKNOWN_BOUNDRY = 15;

    public static void main(String[] args) throws Exception {
//        //new StatsRetrieverPostflop().getRelativeStatsForOpponent("ingolf", 20);
//
//        for(int i = 0; i < 20; i++) {
//            Map<String, Double> absoluteStatsForUnknownNew = new StatsRetrieverPostflop().getAbsoluteStatsForTypicalUnknown2(i);
//            Map<String, Double> relativeStatsForUnknownNew = new StatsRetrieverPostflop().getRelativeStatsForOpponent(absoluteStatsForUnknownNew, 0);
//
//            //System.out.println(i);
//            //System.out.println("call: " + relativeStatsForUnknownNew.get("relativeCallRatio"));
//            System.out.println("bet: " + relativeStatsForUnknownNew.get("relativeBetRatio"));
//            //System.out.println("raise: " + relativeStatsForUnknownNew.get("relativeRaiseRatio"));
//
//            //Syst
//        }
//
//        //Map<String, Double> absoluteStatsForUnknownNew = new StatsRetrieverPostflop().getAbsoluteStatsForTypicalUnknown2();
//        //Map<String, Double> relativeStatsForUnknownNew = new StatsRetrieverPostflop().getRelativeStatsForOpponent(absoluteStatsForUnknownNew, 20);
//
//
//        //stats for unknown:
//            //x percentage voor below 10 hands stats
//            //x percentage voor between 10 and 20 hands stats
//            //x percentage voor 20 up stats...
//                //combine this shit...
//
//            //-> je moet gewoon voor alle players hun bet, call en raise stats berekenen, en dan daarvan de
//                //gemiddeldes nemen. Eventueel vanaf aantal handen dat unknown opp gespeeld heeft..
//
//
//        System.out.println("sdf");

        new StatsRetrieverPreflop().getPreflopStats("ovalis");
        new StatsRetrieverPostflop().getPostflopStats("ovalis");
    }


    public Map<String, Double> getPostflopStats(String opponentName) throws Exception {
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
        List<Double> allBetRatios = getAllBetRatios();
        List<Double> allCallRatios = getAllCallRatios();
        List<Double> allRaiseRatios = getAllRaiseRatios();

        double absoluteBetRatio = absoluteStats.get("betRatio");
        double absoluteCallRatio = absoluteStats.get("callRatio");
        double absoluteRaiseRatio = absoluteStats.get("raiseRatio");

        double relativeBetRatio = countNumberOfElementsBelowValue(allBetRatios, absoluteBetRatio) / (double) allBetRatios.size();
        double relativeCallRatio = countNumberOfElementsBelowValue(allCallRatios, absoluteCallRatio) / (double) allCallRatios.size();
        double relativeRaiseRatio = countNumberOfElementsBelowValue(allRaiseRatios, absoluteRaiseRatio) / (double) allRaiseRatios.size();

        Map<String, Double> relativeStatsForOpp = new HashMap<>();
        relativeStatsForOpp.put("relativeBetRatio", relativeBetRatio);
        relativeStatsForOpp.put("relativeCallRatio", relativeCallRatio);
        relativeStatsForOpp.put("relativeRaiseRatio", relativeRaiseRatio);

        return relativeStatsForOpp;
    }

    private Map<String, Double> getAbsoluteStatsForOpponent(String oppName) throws Exception {
        double numberOfHands = -1;
        double betRatioForPlayer = -1;
        double callRatioForPlayer = -1;
        double raiseRatioForPlayer = -1;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflop WHERE playerName = '" + oppName + "';");


        while(rs.next()) {
            betRatioForPlayer = rs.getDouble("betCount") / (rs.getDouble("checkCount") + rs.getDouble("betCount"));
            callRatioForPlayer = rs.getDouble("callCount") / rs.getDouble("numberOfHands");
            raiseRatioForPlayer = rs.getDouble("raiseCount") / rs.getDouble("numberOfHands");
            numberOfHands = rs.getDouble("numberOfHands");
        }

        rs.close();
        st.close();
        closeDbConnection();

        Map<String, Double> statsForOpp = new HashMap<>();
        statsForOpp.put("betRatio", betRatioForPlayer);
        statsForOpp.put("callRatio", callRatioForPlayer);
        statsForOpp.put("raiseRatio", raiseRatioForPlayer);
        statsForOpp.put("numberOfHands", numberOfHands);

        return statsForOpp;
    }

    private Map<String, Double> getAbsoluteStatsForTypicalUnknown(double handsPlayedAgainstUnknown) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflop;");

        List<Double> betRatios = new ArrayList<>();
        List<Double> callRatios = new ArrayList<>();
        List<Double> raiseRatios = new ArrayList<>();

        while(rs.next()) {
            if(rs.getDouble("numberOfHands") >= handsPlayedAgainstUnknown) {
                double betRatioForPlayer = rs.getDouble("betCount") / (rs.getDouble("checkCount") + rs.getDouble("betCount"));
                double callRatioForPlayer = rs.getDouble("callCount") / rs.getDouble("numberOfHands");
                double raiseRatioForPlayer = rs.getDouble("raiseCount") / rs.getDouble("numberOfHands");

                if(!Double.isNaN(betRatioForPlayer) && !Double.isInfinite(betRatioForPlayer)) {
                    betRatios.add(betRatioForPlayer);
                }

                if(!Double.isNaN(callRatioForPlayer) && !Double.isInfinite(callRatioForPlayer)) {
                    callRatios.add(callRatioForPlayer);
                }

                if(!Double.isNaN(raiseRatioForPlayer) && !Double.isInfinite(raiseRatioForPlayer)) {
                    raiseRatios.add(raiseRatioForPlayer);
                }
            }
        }

        rs.close();
        st.close();
        closeDbConnection();

        Collections.sort(betRatios);
        Collections.sort(callRatios);
        Collections.sort(raiseRatios);

        double averageBetRatio = (betRatios.stream().mapToDouble(Double::doubleValue).sum()) / betRatios.size();
        double averageCallRatio = (callRatios.stream().mapToDouble(Double::doubleValue).sum()) / callRatios.size();
        double averageRaiseRatio = (raiseRatios.stream().mapToDouble(Double::doubleValue).sum()) / raiseRatios.size();

        Map<String, Double> statsForOpp = new HashMap<>();
        statsForOpp.put("betRatio", averageBetRatio);
        statsForOpp.put("callRatio", averageCallRatio);
        statsForOpp.put("raiseRatio", averageRaiseRatio);

        return statsForOpp;
    }

    private List<Double> getAllBetRatios() throws Exception {
        List<Double> allBetRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflop;");

        while(rs.next()) {
            double betRatio = rs.getDouble("betCount") / (rs.getDouble("checkCount") + (rs.getDouble("betCount")));
            allBetRatios.add(betRatio);
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allBetRatios);
        return allBetRatios;
    }

    private List<Double> getAllCallRatios() throws Exception {
        List<Double> allCallRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflop;");

        while(rs.next()) {
            double callToTotalRatio = rs.getDouble("callCount") / rs.getDouble("numberOfHands");
            allCallRatios.add(callToTotalRatio);
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allCallRatios);
        return allCallRatios;
    }

    private List<Double> getAllRaiseRatios() throws Exception {
        List<Double> allRaiseRatios = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_postflop;");

        while(rs.next()) {
            double raiseToTotalRatio = rs.getDouble("raiseCount") / rs.getDouble("numberOfHands");
            allRaiseRatios.add(raiseToTotalRatio);
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allRaiseRatios);
        return allRaiseRatios;
    }

    private void logRelativeStats(Map<String, Double> relativeStats, String oppName, double numberOfHands, boolean unknown) {
        System.out.println();
        System.out.println("##Postflp relative stats##");
        System.out.println("opp: " + oppName + ", h: " + (int) numberOfHands);

        if(unknown) {
            System.out.println("stats for unknown");
        }

        System.out.print("Bet: " + convertToLogFormat(relativeStats.get("relativeBetRatio")) + " ");
        System.out.print("Cll: " + convertToLogFormat(relativeStats.get("relativeCallRatio")) + " ");
        System.out.println("Rai: " + convertToLogFormat(relativeStats.get("relativeRaiseRatio")));

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
