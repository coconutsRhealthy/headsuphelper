package com.lennart.model.action.actionbuilders.ai;

import java.sql.*;
import java.text.SimpleDateFormat;
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

    public static void main(String[] args) throws Exception {
        new Poker().theMethod();
    }

    private void theMethod() throws Exception {
//        Map<String, Double> eije = retrieveRouteDataFromDb("Handstrength50-55StrongDrawNoPositionBTNPotsize60-100bbComputerBetsize10-15bbOpponentBetsize10-15bbEffectiveStack75-110bbBoardTextureDry");
//
//        System.out.println("jaja");
//        List<String> routesFromDb = retrieveAllRoutesFromDb();
//
//        List<String> routesNormal = generateTable();
//
//        System.out.println("wacht");
//
//        routesFromDb.removeAll(routesNormal);
//
//        System.out.println("wacht2");
        List<String> routes = generateTable();
        storeRoutesInDb(routes);
    }

    private String getAction(List<String> eligibleActions, double handStrength, boolean strongDraw, boolean position,
                             double potSize, double computerBetSize, double opponentBetSize, double effectiveStack,
                             String boardTexture) throws Exception {
        String route = createRoute(handStrength, strongDraw, position, potSize, computerBetSize, opponentBetSize, effectiveStack, boardTexture);

        Map<String, Double> routeData = retrieveRouteDataFromDb(route);
        Map<String, Double> sortedPayoffMap = getSortedAveragePayoffMapFromRouteData(routeData);
        Map<String, Double> sortedEligibleActions = retainOnlyEligibleActions(sortedPayoffMap, eligibleActions);

        return sortedEligibleActions.entrySet().iterator().next().getKey();
    }

    private String createRoute(double handStrength, boolean strongDraw, boolean position, double potSizeBb,
                               double computerBetSizeBb, double opponentBetSizeBb, double effectiveStackBb,
                               String boardTexture) {
        String handStrengthString = getHandStrengthString(handStrength);
        String strongDrawString = getStrongDrawString(strongDraw);
        String positionString = getPositionString(position);
        String potSizeString = getPotsizeString(potSizeBb);
        String computerBetSizeString = getComputerBetSizeBbString(computerBetSizeBb);
        String opponentBetSizeString = getOpponentBetSizeBbString(opponentBetSizeBb);
        String effectiveStackString = getEffectiveStackBbString(effectiveStackBb);

        String route = handStrengthString + strongDrawString + positionString + potSizeString + computerBetSizeString + opponentBetSizeString + effectiveStackString + boardTexture;

        return route;
    }

    private String getHandStrengthString(double handStength) {
        String handStrengthString = null;

        if(handStength >= 0 && handStength < 0.05) {
            handStrengthString = "Handstrength0-5";
        } else if(handStength < 0.10) {
            handStrengthString = "Handstrength5-10";
        } else if(handStength < 0.15) {
            handStrengthString = "Handstrength10-15";
        } else if(handStength < 0.20) {
            handStrengthString = "Handstrength15-20";
        } else if(handStength < 0.25) {
            handStrengthString = "Handstrength20-25";
        } else if(handStength < 0.30) {
            handStrengthString = "Handstrength25-30";
        } else if(handStength < 0.35) {
            handStrengthString = "Handstrength30-35";
        } else if(handStength < 0.40) {
            handStrengthString = "Handstrength35-40";
        } else if(handStength < 0.45) {
            handStrengthString = "Handstrength40-45";
        } else if(handStength < 0.50) {
            handStrengthString = "Handstrength45-50";
        } else if(handStength < 0.55) {
            handStrengthString = "Handstrength50-55";
        } else if(handStength < 0.60) {
            handStrengthString = "Handstrength55-60";
        } else if(handStength < 0.65) {
            handStrengthString = "Handstrength60-65";
        } else if(handStength < 0.70) {
            handStrengthString = "Handstrength65-70";
        } else if(handStength < 0.75) {
            handStrengthString = "Handstrength70-75";
        } else if(handStength < 0.80) {
            handStrengthString = "Handstrength75-80";
        } else if(handStength < 0.85) {
            handStrengthString = "Handstrength80-85";
        } else if(handStength < 0.90) {
            handStrengthString = "Handstrength85-90";
        } else if(handStength < 0.95) {
            handStrengthString = "Handstrength90-95";
        } else if(handStength < 1.1){
            handStrengthString = "Handstrength95-100";
        }

        return handStrengthString;
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

    private Map<String, Double> getSortedAveragePayoffMapFromRouteData(Map<String, Double> routeData) {
        Map<String, Double> sortedAveragePayoffMap = new HashMap<>();

        sortedAveragePayoffMap.put("fold", (routeData.get("fold_payoff") / routeData.get("fold_times")));
        sortedAveragePayoffMap.put("check", (routeData.get("check_payoff") / routeData.get("check_times")));
        sortedAveragePayoffMap.put("call", (routeData.get("call_payoff") / routeData.get("call_times")));
        sortedAveragePayoffMap.put("bet25%", (routeData.get("bet25%_payoff") / routeData.get("bet25%_times")));
        sortedAveragePayoffMap.put("bet50%", (routeData.get("bet50%_payoff") / routeData.get("bet50%_times")));
        sortedAveragePayoffMap.put("bet75%", (routeData.get("bet75%_payoff") / routeData.get("bet75%_times")));
        sortedAveragePayoffMap.put("bet100%", (routeData.get("bet100%_payoff") / routeData.get("bet100%_times")));
        sortedAveragePayoffMap.put("bet150%", (routeData.get("bet150%_payoff") / routeData.get("bet150%_times")));
        sortedAveragePayoffMap.put("bet200%", (routeData.get("bet200%_payoff") / routeData.get("bet200%_times")));
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

    private List<String> generateTable() {
        List<String> handStrength = new ArrayList<>();
        List<String> strongDraw = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> potSize = new ArrayList<>();
        List<String> computerBetSize = new ArrayList<>();
        List<String> opponentBetSize = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();
        List<String> boardTexture = new ArrayList<>();

        handStrength.add("Handstrength0-5");
        handStrength.add("Handstrength5-10");
        handStrength.add("Handstrength10-15");
        handStrength.add("Handstrength15-20");
        handStrength.add("Handstrength20-25");
        handStrength.add("Handstrength25-30");
        handStrength.add("Handstrength30-35");
        handStrength.add("Handstrength35-40");
        handStrength.add("Handstrength40-45");
        handStrength.add("Handstrength45-50");
        handStrength.add("Handstrength50-55");
        handStrength.add("Handstrength55-60");
        handStrength.add("Handstrength60-65");
        handStrength.add("Handstrength65-70");
        handStrength.add("Handstrength70-75");
        handStrength.add("Handstrength75-80");
        handStrength.add("Handstrength80-85");
        handStrength.add("Handstrength85-90");
        handStrength.add("Handstrength90-95");
        handStrength.add("Handstrength95-100");

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

        for(String a : handStrength) {
            for(String b : strongDraw) {
                for(String c : position) {
                    for(String d : potSize) {
                        for(String e : computerBetSize) {
                            for(String f : opponentBetSize) {
                                for(String g : effectiveStack) {
                                    for(String h : boardTexture) {
                                        allRoutes.add(a + b + c + d + e + f + g + h);
                                    }
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

        for(String route : routes) {
            Statement st = con.createStatement();
            try {
                st.executeUpdate("INSERT INTO standard (route) VALUES ('" + route + "')");
            } catch (Exception e) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    st.executeUpdate("INSERT INTO standard (route) VALUES ('" + route + "')");
                } catch (Exception f) {
                    System.out.println("wacht");
                }
            }
            st.close();
        }
        closeDbConnection();
    }

    private Map<String, Double> retrieveRouteDataFromDb(String route) throws Exception {
        Map<String, Double> routeData = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM standard WHERE route = '" + route + "';");

        while(rs.next()) {
            routeData.put("fold_times", rs.getDouble("fold_times"));
            routeData.put("fold_payoff", rs.getDouble("fold_payoff"));
            routeData.put("check_times", rs.getDouble("check_times"));
            routeData.put("check_payoff", rs.getDouble("check_payoff"));
            routeData.put("call_times", rs.getDouble("call_times"));
            routeData.put("call_payoff", rs.getDouble("call_payoff"));
            routeData.put("bet25%_times", rs.getDouble("bet25%_times"));
            routeData.put("bet25%_payoff", rs.getDouble("bet25%_payoff"));
            routeData.put("bet50%_times", rs.getDouble("bet50%_times"));
            routeData.put("bet50%_payoff", rs.getDouble("bet50%_payoff"));
            routeData.put("bet75%_times", rs.getDouble("bet75%_times"));
            routeData.put("bet75%_payoff", rs.getDouble("bet75%_payoff"));
            routeData.put("bet100%_times", rs.getDouble("bet100%_times"));
            routeData.put("bet100%_payoff", rs.getDouble("bet100%_payoff"));
            routeData.put("bet150%_times", rs.getDouble("bet150%_times"));
            routeData.put("bet150%_payoff", rs.getDouble("bet150%_payoff"));
            routeData.put("bet200%_times", rs.getDouble("bet200%_times"));
            routeData.put("bet200%_payoff", rs.getDouble("bet200%_payoff"));
            routeData.put("raise_times", rs.getDouble("raise_times"));
            routeData.put("raise_payoff", rs.getDouble("raise_payoff"));
        }
        return routeData;
    }

    private List<String> retrieveAllRoutesFromDb() throws Exception {
        List<String> routesFromDb = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM standard");

        while(rs.next()) {
            routesFromDb.add(rs.getString("route"));
        }

        rs.close();
        st.close();
        closeDbConnection();

        return routesFromDb;
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
