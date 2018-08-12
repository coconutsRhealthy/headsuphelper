package com.lennart.model.action.actionbuilders.ai.opponenttypes;

import com.lennart.model.action.actionbuilders.ai.HandHistoryReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by lpo21630 on 1-2-2018.
 */
public class OpponentIdentifier {

    private static Map<String, Map<Integer, Map<String, List<Double>>>> countMapForAllOpponents = new HashMap<>();
    private static Map<String, Integer> numberOfHandsPerOpponentMap = new HashMap<>();

    private static final double LP_LOOSENESS_POSTFLOP = 0.71;
    private static final double LP_AGGRO_POSTFLOP = 0.1;
    private static final double LA_LOOSENESS_POSTFLOP = 0.71;
    private static final double LA_AGGRO_POSTFLOP = 0.61;
    private static final double TP_LOOSENESS_POSTFLOP = 0.47;
    private static final double TP_AGGRO_POSTFLOP = 0.11;
    private static final double TA_LOOSENESS_POSTFLOP = 0.53;
    private static final double TA_AGGRO_POSTFLOP = 0.43;

    private Connection con;

    public String getOpponentType(String opponentNick, int numberOfHands) {
        String opponentType;

        System.out.println("opponentNick: " + opponentNick);
        System.out.println("numberOfHands: " + numberOfHands);

        if(numberOfHands < 14) {
            opponentType = "tp";
        } else {
            if(countMapForAllOpponents.get(opponentNick) == null) {
                countMapForAllOpponents.put(opponentNick, initializeOpponentMap());
            }

            Map<Integer, Map<String, List<Double>>> opponentAllHandsMap = countMapForAllOpponents.get(opponentNick);

            List<Double> callRaiseCountList = new ArrayList<>();
            List<Double> foldCountList = new ArrayList<>();
            List<Double> betRaiseCountList = new ArrayList<>();
            List<Double> checkCallCountList = new ArrayList<>();

            for (Map.Entry<Integer, Map<String, List<Double>>> entry : opponentAllHandsMap.entrySet()) {
                callRaiseCountList.addAll(entry.getValue().get("callRaiseCount"));
                foldCountList.addAll(entry.getValue().get("foldCount"));
                betRaiseCountList.addAll(entry.getValue().get("betRaiseCount"));
                checkCallCountList.addAll(entry.getValue().get("checkCallCount"));
            }

            double callRaiseCount = getTotalOfList(callRaiseCountList);
            double foldCount = getTotalOfList(foldCountList);
            double betRaiseCount = getTotalOfList(betRaiseCountList);
            double checkCallCount = getTotalOfList(checkCallCountList);

            double looseness = callRaiseCount / (foldCount + callRaiseCount);
            double aggressiveness = betRaiseCount / (checkCallCount + betRaiseCount);

            System.out.println("Looseness: " + looseness);
            System.out.println("Aggressiveness: " + aggressiveness);

            Map<String, Double> loosenessMatchMap = getLoosenessMatchMap(looseness);
            Map<String, Double> aggroMatchMap = getAggroMatchMap(aggressiveness);

            opponentType = getMatch(loosenessMatchMap, aggroMatchMap);
        }

        return opponentType;
    }

    public String getOpponentTypeFromDb(String opponentNick, int numberOfHands) throws Exception {
        String opponentType;

        System.out.println("opponentNick: " + opponentNick);
        System.out.println("numberOfHands: " + numberOfHands);

        if(numberOfHands < 14) {
            opponentType = "tp";
        } else {
            initializeDbConnection();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier WHERE playerName = '" + opponentNick + "';");

            if(!rs.next()) {
                st.executeUpdate("INSERT INTO opponentidentifier (playerName) VALUES ('" + opponentNick + "')");
                opponentType = "tp";
            } else {
                double callRaiseCount = rs.getDouble("callRaiseCount");
                double foldCount = rs.getDouble("foldCount");
                double betRaiseCount = rs.getDouble("betRaiseCount");
                double checkCallCount = rs.getDouble("checkCallCount");

                double looseness = callRaiseCount / (foldCount + callRaiseCount);
                double aggressiveness = betRaiseCount / (checkCallCount + betRaiseCount);

                System.out.println("Looseness: " + looseness);
                System.out.println("Aggressiveness: " + aggressiveness);

                Map<String, Double> loosenessMatchMap = getLoosenessMatchMap(looseness);
                Map<String, Double> aggroMatchMap = getAggroMatchMap(aggressiveness);

                opponentType = getMatch(loosenessMatchMap, aggroMatchMap);
            }
        }

        return opponentType;
    }

    public void updateCounts(String opponentNick, String action, int numberOfHands) {
        if(countMapForAllOpponents.get(opponentNick) == null) {
            countMapForAllOpponents.put(opponentNick, initializeOpponentMap());
        }

        Map<Integer, Map<String, List<Double>>> opponentTotalMap = countMapForAllOpponents.get(opponentNick);

        if(opponentTotalMap.get(numberOfHands) == null) {
            addNewHandToMapAndRemoveOldIfNecessary(opponentNick, numberOfHands);
        }

        List<Double> callRaiseCount = opponentTotalMap.get(numberOfHands).get("callRaiseCount");
        List<Double> foldCount = opponentTotalMap.get(numberOfHands).get("foldCount");
        List<Double> betRaiseCount = opponentTotalMap.get(numberOfHands).get("betRaiseCount");
        List<Double> checkCallCount = opponentTotalMap.get(numberOfHands).get("checkCallCount");

        if(action.equals("fold")) {
            callRaiseCount.add(0.0);
            foldCount.add(1.0);
            betRaiseCount.add(0.0);
            checkCallCount.add(0.0);
        } else if(action.equals("check")) {
            callRaiseCount.add(0.0);
            foldCount.add(0.0);
            betRaiseCount.add(0.0);
            checkCallCount.add(1.0);
        } else if(action.equals("call")) {
            callRaiseCount.add(1.0);
            foldCount.add(0.0);
            betRaiseCount.add(0.0);
            checkCallCount.add(1.0);
        } else if(action.equals("bet75pct")) {
            callRaiseCount.add(0.0);
            foldCount.add(0.0);
            betRaiseCount.add(1.0);
            checkCallCount.add(0.0);
        } else if(action.equals("raise")) {
            callRaiseCount.add(1.0);
            foldCount.add(0.0);
            betRaiseCount.add(1.0);
            checkCallCount.add(0.0);
        }

        opponentTotalMap.get(numberOfHands).put("callRaiseCount", callRaiseCount);
        opponentTotalMap.get(numberOfHands).put("foldCount", foldCount);
        opponentTotalMap.get(numberOfHands).put("betRaiseCount", betRaiseCount);
        opponentTotalMap.get(numberOfHands).put("checkCallCount", checkCallCount);
    }

    public void updateCountsInDb(String opponentNick, String action) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier WHERE playerName = '" + opponentNick + "';");

        if(!rs.next()) {
            st.executeUpdate("INSERT INTO opponentidentifier (playerName) VALUES ('" + opponentNick + "')");
        }

        if(action.equals("fold")) {
            st.executeUpdate("UPDATE opponentidentifier SET foldCount = foldCount + 1 WHERE playerName = '" + opponentNick + "'");
        } else if(action.equals("check")) {
            st.executeUpdate("UPDATE opponentidentifier SET checkCallCount = checkCallCount + 1 WHERE playerName = '" + opponentNick + "'");
        } else if(action.equals("call")) {
            st.executeUpdate("UPDATE opponentidentifier SET callRaiseCount = callRaiseCount + 1 WHERE playerName = '" + opponentNick + "'");
            st.executeUpdate("UPDATE opponentidentifier SET checkCallCount = checkCallCount + 1 WHERE playerName = '" + opponentNick + "'");
        } else if(action.equals("bet75pct")) {
            st.executeUpdate("UPDATE opponentidentifier SET betRaiseCount = betRaiseCount + 1 WHERE playerName = '" + opponentNick + "'");
        } else if(action.equals("raise")) {
            st.executeUpdate("UPDATE opponentidentifier SET callRaiseCount = callRaiseCount + 1 WHERE playerName = '" + opponentNick + "'");
            st.executeUpdate("UPDATE opponentidentifier SET betRaiseCount = betRaiseCount + 1 WHERE playerName = '" + opponentNick + "'");
        }

        rs.close();
        st.close();
        closeDbConnection();
    }

    //dit haalt alle acties van de afgelopen hand uit de hand history file, en doet vervolgens in de in memory countmap de update.
    //ook haalt het de opponent player name uit de hand history
    public String updateCountsFromHandhistoryAndGetOpponentPlayerName() throws Exception {
        HandHistoryReader handHistoryReader = new HandHistoryReader();

        Map<String, List<String>> actionsOfLastHandMap = handHistoryReader.getOpponentActionsOfLastHand2();

        String opponentName = actionsOfLastHandMap.entrySet().iterator().next().getKey();
        updateNumberOfHandsPerOpponentMap(opponentName);

        List<String> opponentActions = actionsOfLastHandMap.entrySet().iterator().next().getValue();
        int numberOfHands = OpponentIdentifier.getNumberOfHandsPerOpponentMap().get(opponentName);

        for(String action : opponentActions) {
            updateCounts(opponentName, action, numberOfHands);
        }

        return opponentName;
    }

    public void updateCountsFromHandhistoryAndGetOpponentPlayerNameDbLogic() throws Exception {
        HandHistoryReader handHistoryReader = new HandHistoryReader();

        Map<String, List<String>> actionsOfLastHandMap = handHistoryReader.getOpponentActionsOfLastHand2();

        String opponentName = actionsOfLastHandMap.entrySet().iterator().next().getKey();
        updateNumberOfHandsPerOpponentMapInDb(opponentName);

        List<String> opponentActions = actionsOfLastHandMap.entrySet().iterator().next().getValue();

        for(String action : opponentActions) {
            updateCountsInDb(opponentName, action);
        }
    }

    public static void updateNumberOfHandsPerOpponentMap(String opponentPlayerName) {
        if(numberOfHandsPerOpponentMap.get(opponentPlayerName) == null) {
            numberOfHandsPerOpponentMap.put(opponentPlayerName, 0);
        } else {
            numberOfHandsPerOpponentMap.put(opponentPlayerName, numberOfHandsPerOpponentMap.get(opponentPlayerName) + 1);
        }
    }

    public void updateNumberOfHandsPerOpponentMapInDb(String opponentPlayerName) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier WHERE playerName = '" + opponentPlayerName + "';");

        if(!rs.next()) {
            st.executeUpdate("INSERT INTO opponentidentifier (playerName) VALUES ('" + opponentPlayerName + "')");
        } else {
            st.executeUpdate("UPDATE opponentidentifier SET numberOfHands = numberOfHands + 1 WHERE playerName = '" + opponentPlayerName + "'");
        }

        rs.close();
        st.close();
        closeDbConnection();
    }

    public static Map<String, Integer> getNumberOfHandsPerOpponentMap() {
        return numberOfHandsPerOpponentMap;
    }

    public int getOpponentNumberOfHandsFromDb(String opponentPlayerName) throws Exception {
        double numberOfHands;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier WHERE playerName = '" + opponentPlayerName + "';");

        if(rs.next()) {
            numberOfHands = rs.getDouble("numberOfHands");
        } else {
            numberOfHands = 0.0;
        }

        return (int) numberOfHands;
    }

    private Map<Integer, Map<String, List<Double>>> initializeOpponentMap() {
        Map<Integer, Map<String, List<Double>>> opponentTotalMap = new HashMap<>();
        opponentTotalMap.put(1, initializeHandMap());
        return opponentTotalMap;
    }

    private Map<String, List<Double>> initializeHandMap() {
        Map<String, List<Double>> opponentCountMap = new HashMap<>();

        opponentCountMap.put("callRaiseCount", new ArrayList<>());
        opponentCountMap.put("foldCount", new ArrayList<>());
        opponentCountMap.put("betRaiseCount", new ArrayList<>());
        opponentCountMap.put("checkCallCount", new ArrayList<>());

        return opponentCountMap;
    }

    private Double getTotalOfList(List<Double> list) {
        double total = 0;

        for(Double d : list) {
            total = total + d;
        }
        return total;
    }

    private void addNewHandToMapAndRemoveOldIfNecessary(String opponentNick, int numberOfHands) {
        Map<Integer, Map<String, List<Double>>> opponentTotalMap = countMapForAllOpponents.get(opponentNick);

        if(opponentTotalMap.size() < 100) {
            opponentTotalMap.put(numberOfHands, initializeHandMap());
        } else {
            opponentTotalMap.remove(getLowestIntKeyFromMap(opponentTotalMap));
            opponentTotalMap.put(numberOfHands, initializeHandMap());
        }
    }

    private int getLowestIntKeyFromMap(Map<Integer, Map<String, List<Double>>> opponentTotalMap) {
        List<Integer> keysAsList = new ArrayList<>(opponentTotalMap.keySet());
        Collections.sort(keysAsList);
        return keysAsList.get(0);
    }

    private Map<String, Double> getLoosenessMatchMap(double looseness) {
        double lpDifference = Math.abs(looseness - LP_LOOSENESS_POSTFLOP);
        double laDifference = Math.abs(looseness - LA_LOOSENESS_POSTFLOP);
        double tpDifference = Math.abs(looseness - TP_LOOSENESS_POSTFLOP);
        double taDifference = Math.abs(looseness - TA_LOOSENESS_POSTFLOP);

        Map<String, Double> loosenessMatchMap = new HashMap<>();

        loosenessMatchMap.put("lp", lpDifference);
        loosenessMatchMap.put("la", laDifference);
        loosenessMatchMap.put("tp", tpDifference);
        loosenessMatchMap.put("ta", taDifference);

        return loosenessMatchMap;
    }

    private Map<String, Double> getAggroMatchMap(double aggressiveness) {
        double lpDifference = Math.abs(aggressiveness - LP_AGGRO_POSTFLOP);
        double laDifference = Math.abs(aggressiveness - LA_AGGRO_POSTFLOP);
        double tpDifference = Math.abs(aggressiveness - TP_AGGRO_POSTFLOP);
        double taDifference = Math.abs(aggressiveness - TA_AGGRO_POSTFLOP);

        Map<String, Double> loosenessMatchMap = new HashMap<>();

        loosenessMatchMap.put("lp", lpDifference);
        loosenessMatchMap.put("la", laDifference);
        loosenessMatchMap.put("tp", tpDifference);
        loosenessMatchMap.put("ta", taDifference);

        return loosenessMatchMap;
    }

    private String getMatch(Map<String, Double> loosenessMatchMap,Map<String, Double> aggroMatchMap) {
        Map<String, Double> matchMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : loosenessMatchMap.entrySet()) {
            String type = entry.getKey();
            Double loosenessScore = entry.getValue();
            Double aggroScore = aggroMatchMap.get(type);

            matchMap.put(type, loosenessScore + aggroScore);
        }

        matchMap = sortByValueLowToHigh(matchMap);

        String match = matchMap.entrySet().iterator().next().getKey();
        return match;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueLowToHigh(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue() ).compareTo( o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
