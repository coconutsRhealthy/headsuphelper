package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LennartMac on 27/10/2018.
 */
public class RangeTracker {

    private Connection con;



    private void ffTest() {

        //dry board
        //medium board
        //wet board

        //dry boat
        //medium boat
        //wet boat

        //flop
        //turn
        //river

        //bet
        //raise

        //size 0-5bb
        //size 5-10bb
        //size 10-15bb
        //size 15-20bb
        //size 20-30bb
        //size 30-40bb
        //size 40-50bb
        //size 50-100bb
        //size 100-150bb
        //size >150bb

        //btn
        //bb


        //1080 routes

    }




    public Map<String, List<Double>> updateRangeMap(Map<String, List<Double>> rangeMap,
                                                    String action, double sizing, double bigBlind,
                                                    boolean position, double handStrength) {
        String actionString = getActionString(action);
        String positionString = getPositionString(position);
        String sizingString = getSizingString(sizing, bigBlind);

        String route = actionString + positionString + sizingString;

        List<Double> currentListFromRoute = rangeMap.get(route);

        Double newValue;

        if(handStrength < 0.7) {
            newValue = currentListFromRoute.get(0);
            newValue = newValue + 1;
            currentListFromRoute.set(0, newValue);
        } else {
            newValue = currentListFromRoute.get(1);
            newValue = newValue + 1;
            currentListFromRoute.set(1, newValue);
        }

        return rangeMap;
    }

    public static void main(String[] args) throws Exception {
        new RangeTracker().fillDbInitial();
    }

    private void fillDbInitial() throws Exception {
        List<String> allRoutes = getAllRangeRoutes();

        initializeDbConnection();

        for(String route : allRoutes) {
            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO rangetracker (route) VALUES ('" + route + "')");

            st.close();
        }

        closeDbConnection();
    }

    public void updateRangeMapInDb(String action, double sizing, double bigBlind, boolean position, double handStrength, List<Card> board) throws Exception {
        String streetString = getStreetString(board);
        String actionString = getActionString(action);
        String positionString = getPositionString(position);
        String sizingString = getSizingString(sizing, bigBlind);

        String route = streetString + actionString + positionString + sizingString;

        initializeDbConnection();

        Statement st = con.createStatement();

        if(handStrength < 0.7) {
            st.executeUpdate("UPDATE rangetracker SET bluff_amount = bluff_amount + 1 WHERE route = '" + route + "'");
        } else {
            st.executeUpdate("UPDATE rangetracker SET value_amount = value_amount + 1 WHERE route = '" + route + "'");
        }

        st.close();
        closeDbConnection();
    }

    public static Map<String, List<Double>> initializeRangeMap() {
        Map<String, List<Double>> rangeMap = new HashMap<>();

        List<String> allRangeRoutes = getAllRangeRoutes();

        for(String route : allRangeRoutes) {
            List<Double> newList = new ArrayList<>();
            Double firstZero = 0.0;
            Double secondZero = 0.0;

            newList.add(firstZero);
            newList.add(secondZero);

            rangeMap.put(route, newList);
        }

        return rangeMap;
    }

    public String getRangeRoute(String action, boolean position, double sizing, double bigBlind) {
        String actionString = getActionString(action);
        String positionString = getPositionString(position);
        String sizingString = getSizingString(sizing, bigBlind);

        return actionString + positionString + sizingString;
    }

    private String getActionString(String action) {
        String actionString;

        if(action.equals("bet75pct")) {
            actionString = "MyActionBet";
        } else {
            actionString = "MyActionRaise";
        }

        return actionString;
    }

    private String getPositionString(boolean position) {
        String positionString;

        if(position) {
            positionString = "PositionBTN";
        } else {
            positionString = "PositionBB";
        }

        return positionString;
    }

    private String getSizingString(double sizing, double bigBlind) {
        String sizingString;

        if(sizing / bigBlind <= 10) {
            sizingString = "Sizing0-10bb";
        } else if(sizing / bigBlind <= 20) {
            sizingString = "Sizing10-20bb";
        } else if(sizing / bigBlind <= 30) {
            sizingString = "Sizing20-30bb";
        } else {
            sizingString = "Sizing>30bb";
        }

        return sizingString;
    }

    private String getStreetString(List<Card> board)  {
        String streetString;

        if(board.size() == 3) {
            streetString = "Flop";
        } else if(board.size() == 4) {
            streetString = "Turn";
        } else {
            streetString = "River";
        }

        return streetString;
    }

    private static List<String> getAllRangeRoutes() {
        List<String> street = new ArrayList<>();
        List<String> action = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> sizing = new ArrayList<>();

        street.add("Flop");
        street.add("Turn");
        street.add("River");

        action.add("MyActionBet");
        action.add("MyActionRaise");

        position.add("PositionBTN");
        position.add("PositionBB");

        sizing.add("Sizing0-10bb");
        sizing.add("Sizing10-20bb");
        sizing.add("Sizing20-30bb");
        sizing.add("Sizing>30bb");

        List<String> allRangeRoutes = new ArrayList<>();

        for(String a : street) {
            for(String b : action) {
                for(String c : position) {
                    for(String d : sizing) {
                        allRangeRoutes.add(a + b + c + d);
                    }
                }
            }
        }

        return allRangeRoutes;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
