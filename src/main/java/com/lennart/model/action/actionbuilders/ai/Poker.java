package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.AbstractOpponent;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.LooseAggressive;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.LoosePassive;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.TightAggressive;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.TightPassive;
import com.lennart.model.card.Card;

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

    public String getAction(List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                            double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                            double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board) {
        try {
            String route = getRoute(street, position, potSizeBb, opponentAction, facingOdds, effectiveStackBb, strongDraw);

            //System.out.println(route);

            String table = getTableString(handStrength, opponentType);

            //System.out.println(table);

            Map<String, Double> routeData = retrieveRouteDataFromDb(route, table);

            if(!routeDataIsBigEnough(routeData, eligibleActions)) {
                //System.out.println("xxxx");

                if(opponentType.equals("tp") || opponentType.equals("lp")) {
                    return new TightPassive().doAction(opponentAction, handStrength, strongDraw, opponentBetSizeBb, ownBetSizeBb, opponentStackBb, ownStackBb, position, preflop, board, facingOdds);
                } else if(opponentType.equals("ta") || opponentType.equals("la")) {
                    return new LooseAggressive(potSizeBb, ownStackBb).doAction(opponentAction, handStrength, strongDraw, opponentBetSizeBb, ownBetSizeBb, opponentStackBb, ownStackBb, position, preflop, board, facingOdds);
                }

                //return getRuleActionWhenDataIsLimited(eligibleActions, handStrength);
            } else {
                Map<String, Double> sortedPayoffMap = getSortedAveragePayoffMapFromRouteData(routeData);
                Map<String, Double> sortedEligibleActions = retainOnlyEligibleActions(sortedPayoffMap, eligibleActions);

                return sortedEligibleActions.entrySet().iterator().next().getKey();
            }
        } catch (Exception e) {
            System.out.println("error occurred in getAction()");
            return null;
        }
        return null;
    }

    private boolean routeDataIsBigEnough(Map<String, Double> routeData, List<String> eligibleActions) {
        boolean routeDataIsBigEnough = false;

        if(eligibleActions.contains("check")) {
            double checkTimes = routeData.get("check_times");
            double betTimes = routeData.get("bet75pct_times");

            if(checkTimes >= 9.0 && betTimes >= 9.0) {
                routeDataIsBigEnough = true;
            }
        } else {
            double foldTimes = routeData.get("fold_times");
            double callTimes = routeData.get("call_times");
            double raiseTimes = routeData.get("raise_times");

            if(foldTimes >= 9.0 && callTimes >= 9.0 && raiseTimes >= 9.0) {
                routeDataIsBigEnough = true;
            }
        }

        return routeDataIsBigEnough;
    }

    private String getRuleActionWhenDataIsLimited(List<String> eligibleActions, double handStrength) {
        String ruleAction;

        if(eligibleActions.contains("check")) {
            if(handStrength > 0.9) {
                ruleAction = "bet75pct";
            } else {
                ruleAction = "check";
            }
        } else {

            if(handStrength > 0.9) {
                ruleAction = "call";
            } else {
                ruleAction = "fold";
            }
        }

        return ruleAction;
    }

    public String getAction(Map<String, Double> routeData, List<String> eligibleActions) {
        Map<String, Double> sortedPayoffMap = getSortedAveragePayoffMapFromRouteData(routeData);
        Map<String, Double> sortedEligibleActions = retainOnlyEligibleActions(sortedPayoffMap, eligibleActions);
        return sortedEligibleActions.entrySet().iterator().next().getKey();
    }

    public String getRoute(String street, boolean position, double potSizeBb, String opponentAction, double facingOdds,
                           double effectiveStackBb, boolean strongDraw) {

        String streetString = getStreetString(street);
        String positionString = getPositionString(position);
        String potSizeString = getPotsizeString(potSizeBb);
        String opponentActionString = getOpponentActionString(opponentAction);
        String facingOddsString = getFacingOddsString(facingOdds);
        String effectiveStackString = getEffectiveStackBbString(effectiveStackBb);
        String strongDrawString = getStrongDrawString(strongDraw);

        String route = streetString + positionString + potSizeString + opponentActionString + facingOddsString + effectiveStackString + strongDrawString;

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

    private String getOpponentActionString(String action) {
        String opponentActionString;

        if(action == null || action.contains("empty")) {
            opponentActionString = "OpponentActionEmpty";
        } else if(action.contains("check")) {
            opponentActionString = "OpponentActionCheck";
        } else if(action.contains("call")) {
            opponentActionString = "OpponentActionCall";
        } else if(action.contains("bet")) {
            opponentActionString = "OpponentActionBet";
        } else {
            opponentActionString = "OpponentActionRaise";
        }

        return opponentActionString;
    }

    private String getFacingOddsString(double facingOdds) {
        String facingOddsString;

        if(facingOdds == 0.0) {
            facingOddsString = "FacingOdds0";
        } else if(facingOdds <= 0.17) {
            facingOddsString = "FacingOdds0-21";
        } else if(facingOdds <= 0.34) {
            facingOddsString = "FacingOdds21-51";
        } else if(facingOdds <= 0.45) {
            facingOddsString = "FacingOdds51-81";
        } else if(facingOdds <= 0.53) {
            facingOddsString = "FacingOdds81-101";
        } else if(facingOdds <= 0.61) {
            facingOddsString = "FacingOdds101-151";
        } else {
            facingOddsString = "FacingOdds>151";
        }

        return facingOddsString;
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

    private String getStreetString(String street) {
        String streetString;

        if(street.equals("preflop")) {
            streetString = "StreetPreflop";
        } else if(street.equals("flopOrTurn")) {
            streetString = "StreetFlopOrTurn";
        } else {
            streetString = "StreetRiver";
        }

        return streetString;
    }

    public String getTableString(double handStrength, String opponentType) {
        String table = "";

        if(handStrength >= 0 && handStrength < 0.05) {
            table = opponentType + "_hs_0_5";
        } else if(handStrength < 0.10) {
            table = opponentType + "_hs_5_10";
        } else if(handStrength < 0.15) {
            table = opponentType + "_hs_10_15";
        } else if(handStrength < 0.20) {
            table = opponentType + "_hs_15_20";
        } else if(handStrength < 0.25) {
            table = opponentType + "_hs_20_25";
        } else if(handStrength < 0.30) {
            table = opponentType + "_hs_25_30";
        } else if(handStrength < 0.35) {
            table = opponentType + "_hs_30_35";
        } else if(handStrength < 0.40) {
            table = opponentType + "_hs_35_40";
        } else if(handStrength < 0.45) {
            table = opponentType + "_hs_40_45";
        } else if(handStrength < 0.50) {
            table = opponentType + "_hs_45_50";
        } else if(handStrength < 0.55) {
            table = opponentType + "_hs_50_55";
        } else if(handStrength < 0.60) {
            table = opponentType + "_hs_55_60";
        } else if(handStrength < 0.65) {
            table = opponentType + "_hs_60_65";
        } else if(handStrength < 0.70) {
            table = opponentType + "_hs_65_70";
        } else if(handStrength < 0.75) {
            table = opponentType + "_hs_70_75";
        } else if(handStrength < 0.80) {
            table = opponentType + "_hs_75_80";
        } else if(handStrength < 0.85) {
            table = opponentType + "_hs_80_85";
        } else if(handStrength < 0.90) {
            table = opponentType + "_hs_85_90";
        } else if(handStrength < 0.95) {
            table = opponentType + "_hs_90_95";
        } else if(handStrength <= 1) {
            table = opponentType + "_hs_95_100";
        } else {
            System.out.println("Handstrengt is not within 0-1 range: " + handStrength);
        }

        return table;
    }

    private String getTableString(String handStrengthAsString, String opponentType) {
        return getTableString(Double.parseDouble(handStrengthAsString), opponentType);
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

    public List<String> getAllRoutes() {
        List<String> street = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> potSize = new ArrayList<>();
        List<String> opponentAction = new ArrayList<>();
        List<String> facingOdds = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();
        List<String> strongDraw = new ArrayList<>();

        street.add("StreetPreflop");
        street.add("StreetFlopOrTurn");
        street.add("StreetRiver");

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

        opponentAction.add("OpponentActionEmpty");
        opponentAction.add("OpponentActionCheck");
        opponentAction.add("OpponentActionCall");
        opponentAction.add("OpponentActionBet");
        opponentAction.add("OpponentActionRaise");

        facingOdds.add("FacingOdds0");
        facingOdds.add("FacingOdds0-21");
        facingOdds.add("FacingOdds21-51");
        facingOdds.add("FacingOdds51-81");
        facingOdds.add("FacingOdds81-101");
        facingOdds.add("FacingOdds101-151");
        facingOdds.add("FacingOdds>151");

        effectiveStack.add("EffectiveStack0-20bb");
        effectiveStack.add("EffectiveStack20-50bb");
        effectiveStack.add("EffectiveStack50-75bb");
        effectiveStack.add("EffectiveStack75-110bb");
        effectiveStack.add("EffectiveStack110-150bb");
        effectiveStack.add("EffectiveStack>150bb");

        strongDraw.add("StrongDrawYes");
        strongDraw.add("StrongDrawNo");

        List<String> allRoutes = new ArrayList<>();

        for(String a : street) {
            for(String b : position) {
                for(String c : potSize) {
                    for(String d : opponentAction) {
                        for(String e : facingOdds) {
                            for(String f : effectiveStack) {
                                for(String g : strongDraw) {
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

    public void storeRoutesInDb(List<String> routes) throws Exception {
        initializeDbConnection();

        List<String> databases = new ArrayList<>();

        databases.add("ta_hs_0_5");
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

        databases.add("la_hs_0_5");
        databases.add("la_hs_5_10");
        databases.add("la_hs_10_15");
        databases.add("la_hs_15_20");
        databases.add("la_hs_20_25");
        databases.add("la_hs_25_30");
        databases.add("la_hs_30_35");
        databases.add("la_hs_35_40");
        databases.add("la_hs_40_45");
        databases.add("la_hs_45_50");
        databases.add("la_hs_50_55");
        databases.add("la_hs_55_60");
        databases.add("la_hs_60_65");
        databases.add("la_hs_65_70");
        databases.add("la_hs_70_75");
        databases.add("la_hs_75_80");
        databases.add("la_hs_80_85");
        databases.add("la_hs_85_90");
        databases.add("la_hs_90_95");
        databases.add("la_hs_95_100");

        databases.add("tp_hs_0_5");
        databases.add("tp_hs_5_10");
        databases.add("tp_hs_10_15");
        databases.add("tp_hs_15_20");
        databases.add("tp_hs_20_25");
        databases.add("tp_hs_25_30");
        databases.add("tp_hs_30_35");
        databases.add("tp_hs_35_40");
        databases.add("tp_hs_40_45");
        databases.add("tp_hs_45_50");
        databases.add("tp_hs_50_55");
        databases.add("tp_hs_55_60");
        databases.add("tp_hs_60_65");
        databases.add("tp_hs_65_70");
        databases.add("tp_hs_70_75");
        databases.add("tp_hs_75_80");
        databases.add("tp_hs_80_85");
        databases.add("tp_hs_85_90");
        databases.add("tp_hs_90_95");
        databases.add("tp_hs_95_100");

        databases.add("lp_hs_0_5");
        databases.add("lp_hs_5_10");
        databases.add("lp_hs_10_15");
        databases.add("lp_hs_15_20");
        databases.add("lp_hs_20_25");
        databases.add("lp_hs_25_30");
        databases.add("lp_hs_30_35");
        databases.add("lp_hs_35_40");
        databases.add("lp_hs_40_45");
        databases.add("lp_hs_45_50");
        databases.add("lp_hs_50_55");
        databases.add("lp_hs_55_60");
        databases.add("lp_hs_60_65");
        databases.add("lp_hs_65_70");
        databases.add("lp_hs_70_75");
        databases.add("lp_hs_75_80");
        databases.add("lp_hs_80_85");
        databases.add("lp_hs_85_90");
        databases.add("lp_hs_90_95");
        databases.add("lp_hs_95_100");

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
                        System.out.println("wacht444");
                    }
                }
                st.close();
            }
        }

        closeDbConnection();
    }

    public Map<String, Double> retrieveRouteDataFromDb(String route, String table) throws Exception {
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


    public void updatePayoff(Map<Integer, List<String>> actionHistory, double totalPayoff, String opponentType) {
        double payoffPerAction = totalPayoff / actionHistory.size();

        for (Map.Entry<Integer, List<String>> entry : actionHistory.entrySet()) {

            String table = getTableString(entry.getValue().get(0), opponentType);
            String route = entry.getValue().get(1);
            String action = entry.getValue().get(2);
            doDbPayoffUpdate(table, route, action, payoffPerAction);
            //doMemoryPayoffUpdate(route, action, payoffPerAction);
        }

        updateDbNumerOfHands(opponentType);
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
            st.executeUpdate("UPDATE " + table + " SET " + actionTimes + " = " + actionTimes + " + 1, " + actionPayoff + " = " + actionPayoff + " + " + payoffPerAction + " WHERE route = '" + route + "' AND " + actionTimes + " < 100");
            st.close();

            closeDbConnection();
        } catch (Exception e) {
            System.out.println("Exception occured in doDbPayoffUpdate()");
        }
    }

    private void updateDbNumerOfHands(String opponentType) {
        try {
            initializeDbConnection();

            Statement st = con.createStatement();
            st.executeUpdate("UPDATE number_of_hands SET amount = amount + 1 WHERE type = '" + opponentType + "'");
            st.executeUpdate("UPDATE number_of_hands SET amount = amount + 1 WHERE type = 'total'");
            st.close();

            closeDbConnection();
        } catch (Exception e) {
            System.out.println("Exception occured in updateDbNumerOfHands()");
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
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/poker", "root", "Vuurwerk00");
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
