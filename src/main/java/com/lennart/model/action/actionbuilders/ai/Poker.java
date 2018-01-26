package com.lennart.model.action.actionbuilders.ai;

import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 31/12/2017.
 */
public class Poker {

    //input
        //hand strength (5% groups)
        //strong draw yes / no
        //opponent type
        //board texture
        //handpath
        //opponent sizing ( < 50% pot, 50 - 100 / 100 - 150 / > 150 )
        //stacks
        //pot odds







    //input
        //opponent type
        //hand strength
        //strong draw
        //position
        //potsize - different sizes
        //your bet - different sizes
        //opponent bet - different sizes
        //effective stack
        //board texture

        //een TAG, jij hebt 82%, geen draw, IP, pot is 8, hij bet 4, eff. stack 40, droog board

    //process


    //output
        //-fold
        //-call
        //check
        //bet 0.25
        //bet 0.5
        //bet 0.75
        //bet 1
        //bet 1.5
        //bet 2
        //raise

    protected Connection con;

    private static Map<String, Map<String, Double>> payoffMap = new HashMap<>();

//    public static void main(String[] args) throws Exception {
//        new Poker().theMethod();
//    }

    private void theMethod() throws Exception {
//        Map<String, Double> eije = retrieveRouteDataFromDb("Handstrength50-55StrongDrawNoPositionBTNPotsize60-100bbComputerBetsize10-15bbOpponentBetsize10-15bbEffectiveStack75-110bbBoardTextureDry");
//
//        System.out.println("jaja");
//        List<String> routesFromDb = retrieveAllRoutesFromDb();
//
//        List<String> routesNormal = getAllRoutes();
//
//        System.out.println("wacht");
//
//        routesFromDb.removeAll(routesNormal);
//
//        System.out.println("wacht2");
        List<String> routes = getAllRoutes();
        storeRoutesInDb(routes);
    }

    public String getAction(List<String> eligibleActions, double handStrength, boolean strongDraw, boolean position,
                             double potSizeBb, double computerBetSizeBb, double opponentBetSizeBb, double effectiveStackBb,
                             String boardTexture) {
        try {
            String route = getRoute(strongDraw, position, potSizeBb, computerBetSizeBb, opponentBetSizeBb, effectiveStackBb, boardTexture);
            String table = getTableString(handStrength);

            Map<String, Double> routeData = retrieveRouteDataFromDb(route, table);
            Map<String, Double> sortedPayoffMap = getSortedAveragePayoffMapFromRouteData(routeData);
            Map<String, Double> sortedEligibleActions = retainOnlyEligibleActions(sortedPayoffMap, eligibleActions);

            return sortedEligibleActions.entrySet().iterator().next().getKey();
        } catch (Exception e) {
            System.out.println("error occurred in getAction()");
            return null;
        }
    }

    public String getRoute(boolean strongDraw, boolean position, double potSizeBb, double computerBetSizeBb,
                           double opponentBetSizeBb, double effectiveStackBb, String boardTexture) {
        String strongDrawString = getStrongDrawString(strongDraw);
        String positionString = getPositionString(position);
        String potSizeString = getPotsizeString(potSizeBb);
        String computerBetSizeString = getComputerBetSizeBbString(computerBetSizeBb);
        String opponentBetSizeString = getOpponentBetSizeBbString(opponentBetSizeBb);
        String effectiveStackString = getEffectiveStackBbString(effectiveStackBb);

        String route = strongDrawString + positionString + potSizeString + computerBetSizeString + opponentBetSizeString + effectiveStackString + boardTexture;

        return route;
    }

    private String getStrongDrawString(boolean strongDraw) {
        if(strongDraw) {
            return "StrongDrawYes";
        } else {
            return "StrongDrawNo";
        }
    }

    private String getPositionString(boolean position) {
        if(position) {
            return "PositionBTN";
        } else {
            return "PositionBB";
        }
    }

    private String getPotsizeString(double potSizeBb) {
        String potSizeString;

        if(potSizeBb >= 0 && potSizeBb < 5) {
            potSizeString = "Potsize0-5bb";
        } else if(potSizeBb < 10) {
            potSizeString = "Potsize5-10bb";
        } else if(potSizeBb < 15) {
            potSizeString = "Potsize10-15bb";
        } else if(potSizeBb < 20) {
            potSizeString = "Potsize15-20bb";
        } else if(potSizeBb < 25) {
            potSizeString = "Potsize20-25bb";
        } else if(potSizeBb < 40) {
            potSizeString = "Potsize25-40bb";
        } else if(potSizeBb < 60) {
            potSizeString = "Potsize40-60bb";
        } else if(potSizeBb < 100) {
            potSizeString = "Potsize60-100bb";
        } else if(potSizeBb < 150) {
            potSizeString = "Potsize100-150bb";
        } else {
            potSizeString = "Potsize>150bb";
        }
        return potSizeString;
    }

    private String getComputerBetSizeBbString(double computerBetSizeBb) {
        String computerBetSizeBbString;

        if(computerBetSizeBb >= 0 && computerBetSizeBb < 5) {
            computerBetSizeBbString = "ComputerBetsize0-5bb";
        } else if(computerBetSizeBb < 10) {
            computerBetSizeBbString = "ComputerBetsize5-10bb";
        } else if(computerBetSizeBb < 15) {
            computerBetSizeBbString = "ComputerBetsize10-15bb";
        } else if(computerBetSizeBb < 20) {
            computerBetSizeBbString = "ComputerBetsize15-20bb";
        } else if(computerBetSizeBb < 25) {
            computerBetSizeBbString = "ComputerBetsize20-25bb";
        } else if(computerBetSizeBb < 40) {
            computerBetSizeBbString = "ComputerBetsize25-40bb";
        } else if(computerBetSizeBb < 60) {
            computerBetSizeBbString = "ComputerBetsize40-60bb";
        } else if(computerBetSizeBb < 100) {
            computerBetSizeBbString = "ComputerBetsize60-100bb";
        } else if(computerBetSizeBb < 150) {
            computerBetSizeBbString = "ComputerBetsize100-150bb";
        } else {
            computerBetSizeBbString = "ComputerBetsize>150bb";
        }

        return computerBetSizeBbString;
    }

    private String getOpponentBetSizeBbString(double opponentBetSizeBb) {
        String computerBetSizeBbString;

        if(opponentBetSizeBb >= 0 && opponentBetSizeBb < 5) {
            computerBetSizeBbString = "OpponentBetsize0-5bb";
        } else if(opponentBetSizeBb < 10) {
            computerBetSizeBbString = "OpponentBetsize5-10bb";
        } else if(opponentBetSizeBb < 15) {
            computerBetSizeBbString = "OpponentBetsize10-15bb";
        } else if(opponentBetSizeBb < 20) {
            computerBetSizeBbString = "OpponentBetsize15-20bb";
        } else if(opponentBetSizeBb < 25) {
            computerBetSizeBbString = "OpponentBetsize20-25bb";
        } else if(opponentBetSizeBb < 40) {
            computerBetSizeBbString = "OpponentBetsize25-40bb";
        } else if(opponentBetSizeBb < 60) {
            computerBetSizeBbString = "OpponentBetsize40-60bb";
        } else if(opponentBetSizeBb < 100) {
            computerBetSizeBbString = "OpponentBetsize60-100bb";
        } else if(opponentBetSizeBb < 150) {
            computerBetSizeBbString = "OpponentBetsize100-150bb";
        } else {
            computerBetSizeBbString = "OpponentBetsize>150bb";
        }

        return computerBetSizeBbString;
    }

    private String getEffectiveStackBbString(double effectiveStackBb) {
        String effectiveStackBbString;

        if(effectiveStackBb >= 0 && effectiveStackBb < 20) {
            effectiveStackBbString = "EffectiveStack0-20bb";
        } else if(effectiveStackBb < 50) {
            effectiveStackBbString = "EffectiveStack20-50bb";
        } else if(effectiveStackBb < 75) {
            effectiveStackBbString = "EffectiveStack50-75bb";
        } else if(effectiveStackBb < 110) {
            effectiveStackBbString = "EffectiveStack75-110bb";
        } else if(effectiveStackBb < 150) {
            effectiveStackBbString = "EffectiveStack110-150bb";
        } else {
            effectiveStackBbString = "EffectiveStack>150bb";
        }

        return effectiveStackBbString;
    }

    private String getTableString(double handStrength) {
        String table = "";

        if(handStrength >= 0 && handStrength < 0.05) {
            table = "ta_hs_0_5";
        } else if(handStrength < 0.10) {
            table = "ta_hs_5_10";
        } else if(handStrength < 0.15) {
            table = "ta_hs_10_15";
        } else if(handStrength < 0.20) {
            table = "ta_hs_15_20";
        } else if(handStrength < 0.25) {
            table = "ta_hs_20_25";
        } else if(handStrength < 0.30) {
            table = "ta_hs_25_30";
        } else if(handStrength < 0.35) {
            table = "ta_hs_30_35";
        } else if(handStrength < 0.40) {
            table = "ta_hs_35_40";
        } else if(handStrength < 0.45) {
            table = "ta_hs_40_45";
        } else if(handStrength < 0.50) {
            table = "ta_hs_45_50";
        } else if(handStrength < 0.55) {
            table = "ta_hs_50_55";
        } else if(handStrength < 0.60) {
            table = "ta_hs_55_60";
        } else if(handStrength < 0.65) {
            table = "ta_hs_60_65";
        } else if(handStrength < 0.70) {
            table = "ta_hs_65_70";
        } else if(handStrength < 0.75) {
            table = "ta_hs_70_75";
        } else if(handStrength < 0.80) {
            table = "ta_hs_75_80";
        } else if(handStrength < 0.85) {
            table = "ta_hs_80_85";
        } else if(handStrength < 0.90) {
            table = "ta_hs_85_90";
        } else if(handStrength < 0.95) {
            table = "ta_hs_90_95";
        } else if(handStrength <= 1) {
            table = "ta_hs_95_100";
        } else {
            System.out.println("Handstrengt is not within 0-1 range: " + handStrength);
        }

        return table;
    }

    private String getTableString(String handStrengthAsString) {
        return getTableString(Double.parseDouble(handStrengthAsString));
    }

    private Map<String, Double> getSortedAveragePayoffMapFromRouteData(Map<String, Double> routeData) {
        Map<String, Double> sortedAveragePayoffMap = new HashMap<>();

        sortedAveragePayoffMap.put("fold", (routeData.get("fold_payoff") / routeData.get("fold_times")));
        sortedAveragePayoffMap.put("check", (routeData.get("check_payoff") / routeData.get("check_times")));
        sortedAveragePayoffMap.put("call", (routeData.get("call_payoff") / routeData.get("call_times")));
        sortedAveragePayoffMap.put("bet75pct", (routeData.get("bet75pct_payoff") / routeData.get("bet75pct_times")));
        sortedAveragePayoffMap.put("raise", (routeData.get("raise_payoff") / routeData.get("raise_times")));

        return sortByValueHighToLow(sortedAveragePayoffMap);
    }

    private Map<String, Double> retainOnlyEligibleActions(Map<String, Double> sortedActionMap, List<String> eligibleActions) {
        Map<String, Double> sortedEligibleActions = new HashMap<>();

        for(String eligibleAction : eligibleActions) {
            for (Map.Entry<String, Double> entry : sortedActionMap.entrySet()) {
                if(entry.getKey().contains(eligibleAction)) {
                    sortedEligibleActions.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return sortByValueHighToLow(sortedEligibleActions);
    }

    private List<String> getAllRoutes() {
        List<String> strongDraw = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> potSize = new ArrayList<>();
        List<String> computerBetSize = new ArrayList<>();
        List<String> opponentBetSize = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();
        List<String> boardTexture = new ArrayList<>();

        strongDraw.add("StrongDrawYes");
        strongDraw.add("StrongDrawNo");

        position.add("PositionBTN");
        position.add("PositionBB");

        potSize.add("Potsize0-5bb");
        potSize.add("Potsize5-10bb");
        potSize.add("Potsize10-15bb");
        potSize.add("Potsize15-20bb");
        potSize.add("Potsize20-25bb");
        potSize.add("Potsize25-40bb");
        potSize.add("Potsize40-60bb");
        potSize.add("Potsize60-100bb");
        potSize.add("Potsize100-150bb");
        potSize.add("Potsize>150bb");

        computerBetSize.add("ComputerBetsize0-5bb");
        computerBetSize.add("ComputerBetsize5-10bb");
        computerBetSize.add("ComputerBetsize10-15bb");
        computerBetSize.add("ComputerBetsize15-20bb");
        computerBetSize.add("ComputerBetsize20-25bb");
        computerBetSize.add("ComputerBetsize25-40bb");
        computerBetSize.add("ComputerBetsize40-60bb");
        computerBetSize.add("ComputerBetsize60-100bb");
        computerBetSize.add("ComputerBetsize100-150bb");
        computerBetSize.add("ComputerBetsize>150bb");

        opponentBetSize.add("OpponentBetsize0-5bb");
        opponentBetSize.add("OpponentBetsize5-10bb");
        opponentBetSize.add("OpponentBetsize10-15bb");
        opponentBetSize.add("OpponentBetsize15-20bb");
        opponentBetSize.add("OpponentBetsize20-25bb");
        opponentBetSize.add("OpponentBetsize25-40bb");
        opponentBetSize.add("OpponentBetsize40-60bb");
        opponentBetSize.add("OpponentBetsize60-100bb");
        opponentBetSize.add("OpponentBetsize100-150bb");
        opponentBetSize.add("OpponentBetsize>150bb");

        effectiveStack.add("EffectiveStack0-20bb");
        effectiveStack.add("EffectiveStack20-50bb");
        effectiveStack.add("EffectiveStack50-75bb");
        effectiveStack.add("EffectiveStack75-110bb");
        effectiveStack.add("EffectiveStack110-150bb");
        effectiveStack.add("EffectiveStack>150bb");

        boardTexture.add("BoardTextureDry");
        boardTexture.add("BoardTextureMedium");
        boardTexture.add("BoardTextureWet");

        List<String> allRoutes = new ArrayList<>();

        for(String a : strongDraw) {
            for(String b : position) {
                for(String c : potSize) {
                    for(String d : computerBetSize) {
                        for(String e : opponentBetSize) {
                            for(String f : effectiveStack) {
                                for(String g : boardTexture) {
                                    allRoutes.add(a + b + c + d + e + f + g);
                                }
                            }
                        }
                    }
                }
            }
        }


        return allRoutes;
    }

    private void storeRoutesInDb(List<String> routes) throws Exception {
        initializeDbConnection();

        List<String> databases = new ArrayList<>();
        databases.add("ta_hs_5_10");
        databases.add("ta_hs_10_15");
        databases.add("ta_hs_15_20");
        databases.add("ta_hs_20_25");
        databases.add("ta_hs_25_30");
        databases.add("ta_hs_30_35");
        databases.add("ta_hs_35_40");
        databases.add("ta_hs_40_45");
        databases.add("ta_hs_45_50");
        databases.add("ta_hs_50_55");
        databases.add("ta_hs_55_60");
        databases.add("ta_hs_60_65");
        databases.add("ta_hs_65_70");
        databases.add("ta_hs_70_75");
        databases.add("ta_hs_75_80");
        databases.add("ta_hs_80_85");
        databases.add("ta_hs_85_90");
        databases.add("ta_hs_90_95");
        databases.add("ta_hs_95_100");


        for(String database : databases) {
            for(String route : routes) {
                Statement st = con.createStatement();
                try {
                    st.executeUpdate("INSERT INTO " + database + " (route) VALUES ('" + route + "')");
                } catch (Exception e) {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                        st.executeUpdate("INSERT INTO " + database + " (route) VALUES ('" + route + "')");
                    } catch (Exception f) {
                        System.out.println("wacht");
                    }
                }
                st.close();
            }
        }

        closeDbConnection();
    }

    private Map<String, Double> retrieveRouteDataFromDb(String route, String table) throws Exception {
        Map<String, Double> routeData = new HashMap<>();

//        routeData.put("fold_times", 30.0);
//        routeData.put("fold_payoff", Math.random());
//        routeData.put("check_times", 30.0);
//        routeData.put("check_payoff", Math.random());
//        routeData.put("call_times", 30.0);
//        routeData.put("call_payoff", Math.random());
//        routeData.put("bet75pct_times", 30.0);
//        routeData.put("bet75pct_payoff", Math.random());
//        routeData.put("raise_times", 30.0);
//        routeData.put("raise_payoff", Math.random());


        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE route = '" + route + "';");

        while(rs.next()) {
            routeData.put("fold_times", rs.getDouble("fold_times"));
            routeData.put("fold_payoff", rs.getDouble("fold_payoff"));
            routeData.put("check_times", rs.getDouble("check_times"));
            routeData.put("check_payoff", rs.getDouble("check_payoff"));
            routeData.put("call_times", rs.getDouble("call_times"));
            routeData.put("call_payoff", rs.getDouble("call_payoff"));
            routeData.put("bet75pct_times", rs.getDouble("bet75pct_times"));
            routeData.put("bet75pct_payoff", rs.getDouble("bet75pct_payoff"));
            routeData.put("raise_times", rs.getDouble("raise_times"));
            routeData.put("raise_payoff", rs.getDouble("raise_payoff"));
        }

        rs.close();
        st.close();
        closeDbConnection();

        return routeData;
    }

    private Map<String, Double> retrieveRouteFromMemory(String route) {
        return payoffMap.get(route);
    }


    public void updatePayoff(Map<Integer, List<String>> actionHistory, double totalPayoff) {
        double payoffPerAction = totalPayoff / actionHistory.size();

        for (Map.Entry<Integer, List<String>> entry : actionHistory.entrySet()) {
            String table = getTableString(entry.getValue().get(0));
            String route = entry.getValue().get(1);
            String action = entry.getValue().get(2);
            doDbPayoffUpdate(table, route, action, payoffPerAction);
            //doMemoryPayoffUpdate(route, action, payoffPerAction);
        }
    }

    private void doDbPayoffUpdate(String table, String route, String action, double payoffPerAction) {
        try {
            if(action.contains("%")) {
                action = removePercentageFromString(action);
            }

            String actionTimes = action + "_times";
            String actionPayoff = action + "_payoff";

            initializeDbConnection();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE route = '" + route + "';");

            rs.next();

            int previousTimes = rs.getInt(actionTimes);
            double previousTotalPayoff = rs.getDouble(actionPayoff);

            rs.close();
            st.close();

            Statement st2 = con.createStatement();
            st2.executeUpdate("UPDATE " + table + " SET " + actionTimes + " = " + (previousTimes + 1) + ", " + actionPayoff + " = " + (previousTotalPayoff + payoffPerAction) + " WHERE route = '" + route + "'");
            st2.close();

            closeDbConnection();
        } catch (Exception e) {
            System.out.println("Exception occured in doDbPayoffUpdate()");
        }
    }

    private void doMemoryPayoffUpdate(String route, String action, double payoffPerAction) {
        if(action.contains("%")) {
            action = removePercentageFromString(action);
        }

        String actionTimes = action + "_times";
        String actionPayoff = action + "_payoff";

        double previousTimes = payoffMap.get(route).get(actionTimes);
        double previousPayoff = payoffMap.get(route).get(actionPayoff);

        payoffMap.get(route).put(actionTimes, (previousTimes + 1.0));
        payoffMap.get(route).put(actionPayoff, (previousPayoff + payoffPerAction));
    }

    private String removePercentageFromString(String string) {
        String toReturn = string.replaceAll("%", "pct");
        return toReturn;
    }

    public void initializePayoffMap() {
        List<String> allRoutes = getAllRoutes();

        for(String route : allRoutes) {
            payoffMap.put(route, new HashMap<>());

            payoffMap.get(route).put("fold_times", 0.0);
            payoffMap.get(route).put("fold_payoff", 0.0);
            payoffMap.get(route).put("check_times", 0.0);
            payoffMap.get(route).put("check_payoff", 0.0);
            payoffMap.get(route).put("call_times", 0.0);
            payoffMap.get(route).put("call_payoff", 0.0);
            payoffMap.get(route).put("bet75pct_times", 0.0);
            payoffMap.get(route).put("bet75pct_payoff", 0.0);
            payoffMap.get(route).put("raise_times", 0.0);
            payoffMap.get(route).put("raise_payoff", 0.0);
        }

        System.out.println("hoi");
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/poker", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
