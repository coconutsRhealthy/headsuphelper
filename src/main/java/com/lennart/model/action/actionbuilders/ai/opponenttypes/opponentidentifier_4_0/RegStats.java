package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_4_0;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class RegStats {

    private Connection con;

    public static void main(String[] args) throws Exception {
        //new RegStats().getAllIpRaiseRatios("ipRaiseCount", "numberOfHands");
        //new RegStats().getAllIpRaiseRatios("oopRaiseCount", "numberOfHands");
        //new RegStats().getAllIpRaiseRatios("callCount", "numberOfHands");
        //new RegStats().getStatForAllPlayers("opponentidentifier_2_0_postflop", "betCount", "numberOfHands");
        new RegStats().getStatForPlayer("Trickysleeps", "postflopRaise");
    }

    private double getStatForPlayer(String playerName, String stat) throws Exception {
        String playerType = "unknown";

        if(stat.equals("postflopRaise")) {
            Map<String, Double> allStatsMap = getStatForAllPlayers("opponentidentifier_2_0_preflop", "callCount", "numberOfHands");
            double oneThird = getValueAtPercentile(allStatsMap, 0.33);
            double twoThird = getValueAtPercentile(allStatsMap, 0.66);
            double statForPlayer = allStatsMap.get(playerName);

            if(statForPlayer < oneThird) {
                playerType = "low";
            } else if(statForPlayer < twoThird) {
                playerType = "medium";
            } else {
                playerType = "high";
            }

            System.out.println("one_third: " + oneThird);
            System.out.println("two_third: " + twoThird);
            System.out.println("playerstat: " + statForPlayer);
            System.out.println(playerType);
        }

        return 0.0;
    }

    private Map<String, Double> getStatForAllPlayers(String table, String topRatioStat, String bottomRatioStat) throws Exception {
        Map<String, Double> ipRaiseRatios = new LinkedHashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE numberOfHands > 300;");

        while(rs.next()) {
            double ipRaiseRatio = rs.getDouble(topRatioStat) / rs.getDouble(bottomRatioStat);
            ipRaiseRatios.put(rs.getString("playerName"), ipRaiseRatio);
        }

        rs.close();
        st.close();

        closeDbConnection();

        ipRaiseRatios = sortByValueLowToHigh(ipRaiseRatios);
        return ipRaiseRatios;
    }

    private double getValueAtPercentile(Map<String, Double> allStatsMap, double percentile) {
        double mapSize = allStatsMap.size();
        double correspondingPercentileIndex = mapSize * percentile;
        List<Double> valuesOfMap = new ArrayList<>(allStatsMap.values());
        return valuesOfMap.get((int) correspondingPercentileIndex);
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
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
