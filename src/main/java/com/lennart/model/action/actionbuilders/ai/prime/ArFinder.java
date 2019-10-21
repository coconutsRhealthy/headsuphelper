package com.lennart.model.action.actionbuilders.ai.prime;

import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 02/10/2019.
 */
public class ArFinder {


//    private String buildQuery(double handstrength,
//                              String combo,
//                              boolean position,
//                              boolean strongDraw,
//                              String street,
//                              double botStack,
//                              double oppStack,
//                              double botBetSize,
//                              double oppbetsize,
//                              double pot,
//                              double sizing,
//                              String oppType) {
//        String query;
//
////        query = "SELECT * FROM dbstats_raw WHERE " + getHandStrengthQuery(handstrength) +
////                " AND " + getStreetQuery("Turn") +
////                " AND " + getPositionQuery(position) +
////                " AND " + getOppActionQuery("bet75pct") +
////                " AND " + getOppTypeQuery(oppType) +
////                ";";
//
////        query = "SELECT * FROM dbstats_raw WHERE " + getHandStrengthQuery(handstrength) +
////                " AND " + getPositionQuery(position) +
////                " AND " + getOppActionQuery("bet75pct") +
////                " AND " + getOppTypeQuery(oppType) +
////                ";";
//
//        return query;
//    }

    private static final int LIMIT = 100;

    public static void main(String[] args) throws Exception {
        String query = new ArFinder().buildQuery(0.69,
                "JTs",
                "bet75pct",
                false,
                false,
                "TA",
                false,
                "Flop",
                80,
                1400,
                60);

        System.out.println(query);
    }

    private Connection con;

    private String buildQuery(double handstrength,
                              String combo,
                              String oppAction,
                              boolean preflop,
                              boolean strongDraw,
                              String oppType,
                              boolean position,
                              String street,
                              double pot,
                              double oppStack,
                              double oppBetSize) throws Exception {
        String initialQuery;

        if(preflop) {
            initialQuery = "SELECT * FROM dbstats_raw WHERE combo = '" + combo +
                    "' AND opponent_action = '" + oppAction +
                    " AND board = '' ";
        } else {
            if(!strongDraw) {
                initialQuery = "SELECT * FROM dbstats_raw WHERE " + getHandStrengthQuery(handstrength, false)
                        + "' AND opponent_action = '" + oppAction +
                        "' AND board != '' ";
            } else {
                initialQuery = "SELECT * FROM dbstats_raw WHERE strongdraw = 'StrongDrawTrue' AND "
                        + getHandStrengthQuery(0, true) +
                        "' AND opponent_action = '" + oppAction
                        + " AND board != '' ";
            }
        }

        List<String> otherQueryLines = getAllQueryLines(street, position, oppType, pot, oppStack, oppBetSize);
        List<String> otherQueryLineCombinations = new Combination().getAllCombinationsOfList(otherQueryLines);

        List<String> allPossibleQueries = new ArrayList<>();
        allPossibleQueries.add(initialQuery);

        for(String queryCombination : otherQueryLineCombinations) {
            allPossibleQueries.add(initialQuery + queryCombination);
        }

        Map<String, Integer> initialQueryMap = new HashMap<>();

        for(String query : allPossibleQueries) {
            initialQueryMap.put(query, 0);
        }

        initialQueryMap = sortInitialQueryMapByNumberOfAnds(initialQueryMap);

        TreeMap<Integer, Map<String, Integer>> bigQueryMap = convertInitialQueryMapToBigMap(initialQueryMap);
        bigQueryMap = fillQueryMapWithNumberOfResultsNew(bigQueryMap);

        String query = selectQueryFromBigQueryMap(bigQueryMap);
        return query;
    }

    private List<String> getAllQueryLines(String street, boolean position, String oppType, double pot, double oppStack,
                                          double oppBetSize) {
        List<String> allQueryLines = new ArrayList<>();

        allQueryLines.add(getPositionQuery(position));
        allQueryLines.add(getStreetQuery(street));
        allQueryLines.add(getOppTypeQuery(oppType));
        allQueryLines.add(getPotSizeQuery(pot));
        allQueryLines.add(getOppStackQuery(oppStack));
        allQueryLines.add(getOppBetSizeQuery(oppBetSize));

        return allQueryLines;
    }

    private Map<String, Integer> sortInitialQueryMapByNumberOfAnds(Map<String, Integer> initialQueryMap) {
        Map<String, Integer> andCounterMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : initialQueryMap.entrySet()) {
            int andCounter = StringUtils.countMatches(entry.getKey(), "AND");
            andCounterMap.put(entry.getKey(), andCounter);
        }

        andCounterMap = sortByValueHighToLow(andCounterMap);

        return andCounterMap;
    }

    private TreeMap<Integer, Map<String, Integer>> convertInitialQueryMapToBigMap(Map<String, Integer> initialQueryMap) {
        TreeMap<Integer, Map<String, Integer>> bigQueryMap = new TreeMap<>();

        for(Map.Entry<String, Integer> entry : initialQueryMap.entrySet()) {
            bigQueryMap.putIfAbsent(entry.getValue(), new HashMap<>());
            bigQueryMap.get(entry.getValue()).put(entry.getKey(), 0);
        }

        return bigQueryMap;
    }

    private TreeMap<Integer, Map<String, Integer>> fillQueryMapWithNumberOfResultsNew(TreeMap<Integer, Map<String, Integer>> initialBigMap) throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();

        for(Map.Entry<Integer, Map<String, Integer>> entry : initialBigMap.entrySet()) {
            Map<String, Integer> numberQueryMap = entry.getValue();

            for(Map.Entry<String, Integer> innerEntry : numberQueryMap.entrySet()) {
                String query = innerEntry.getKey();

                ResultSet rs = st.executeQuery(query);
                rs.last();
                int size = rs.getRow();

                innerEntry.setValue(size);

                rs.close();
            }

            if(allValuesBelowLimit(numberQueryMap)) {
                break;
            }
        }

        st.close();
        closeDbConnection();

        return null;
    }

    private String selectQueryFromBigQueryMap(TreeMap<Integer, Map<String, Integer>> bigQueryMap) {
        int bigQueryMapIntegerToUse = -1;

        for(Map.Entry<Integer, Map<String, Integer>> entry : bigQueryMap.entrySet()) {
            Map<String, Integer> mapOfCurrentInt = entry.getValue();

            mapOfCurrentInt = sortByValueHighToLow(mapOfCurrentInt);

            int queryHighestHitNumber = mapOfCurrentInt.entrySet().iterator().next().getValue();

            if(queryHighestHitNumber > 0) {
                bigQueryMapIntegerToUse = entry.getKey();
            } else {
                break;
            }
        }

        Map<String, Integer> queryMapToConsider = bigQueryMap.get(bigQueryMapIntegerToUse);
        List<String> potentialQueries = getQueriesAboveLimitWithLowestHitsFromMapToConsider(queryMapToConsider);
        String selectedQuery = selectQueryFromPotentialQueries(potentialQueries);
        return selectedQuery;
    }

    private String selectQueryFromPotentialQueries(List<String> potentialQueries) {
        String selectedQuery = null;

        for(String query : potentialQueries) {
            if(query.contains("oppTypeBroad")) {
                selectedQuery = query;
                break;
            }
        }

        if(selectedQuery == null) {
            selectedQuery = potentialQueries.get(0);
        }

        return selectedQuery;
    }

    private List<String> getQueriesAboveLimitWithLowestHitsFromMapToConsider(Map<String, Integer> queryMapToConsider) {
        List<String> queries = new ArrayList<>();

        queryMapToConsider = sortByValueLowToHigh(queryMapToConsider);

        int inMethodLowestQueryNumber = -1;

        for(Map.Entry<String, Integer> entry : queryMapToConsider.entrySet()) {
            if(entry.getValue() > LIMIT) {


                if(queries.isEmpty()) {
                    queries.add(entry.getKey());
                    inMethodLowestQueryNumber = entry.getValue();
                } else {
                    if(entry.getValue() == inMethodLowestQueryNumber) {
                        queries.add(entry.getKey());
                    } else {
                        break;
                    }
                }
            }
        }

        return queries;
    }

    private boolean allValuesBelowLimit(Map<String, Integer> mapToCheck) {
        mapToCheck = sortByValueHighToLow(mapToCheck);
        int highestValueInMap = mapToCheck.entrySet().iterator().next().getValue();
        return highestValueInMap < LIMIT;
    }

    private String getHandStrengthQuery(double handstrength, boolean strongDraw) {
        String hsQuery;

        double bottomHsLimit;
        double topHsLmit;

        if(!strongDraw) {
            bottomHsLimit = handstrength - 0.02;
            topHsLmit = handstrength + 0.02;
        } else {
            bottomHsLimit = handstrength - 0.1;
            topHsLmit = handstrength + 0.1;
        }

        hsQuery = "handstrength > " + bottomHsLimit + " AND handstrength < " + topHsLmit;
        return hsQuery;
    }

    private String getStreetQuery(String street) {
        String streetQuery = "street = '" + street + "'";
        return streetQuery;
    }

    private String getPositionQuery(boolean position) {
        String positionQuery;

        if(position) {
            positionQuery = "position = 'Ip'";
        } else {
            positionQuery = "position = 'Oop'";
        }

        return positionQuery;
    }

    private String getOppActionQuery(String oppAction) {
        String oppActionQuery = null;

        if(oppAction.equals("check")) {
            oppActionQuery = "opponent_action = 'check'";
        } else if(oppAction.equals("call")) {
            oppActionQuery = "opponent_action = 'call'";
        } else if(oppAction.equals("empty")) {
            oppActionQuery = "opponent_action = 'empty'";
        } else if(oppAction.equals("bet75pct")) {
            oppActionQuery = "opponent_action = 'bet75pct'";
        } else if(oppAction.equals("raise")) {
            oppActionQuery = "opponent_action = 'raise'";
        }

        return oppActionQuery;
    }

    private String getOppTypeQuery(String oppType) {
        String oppTypeQuery = "oppTypeBroad = '" + oppType + "'";
        return oppTypeQuery;
    }

    private String getPotSizeQuery(double pot) {
        double potBottomLimit;
        double potTopLimit;
        String potQuery;

        if(pot == 0) {
            potQuery = "pot = 0";
        } else {
            if(pot <= 100) {
                potBottomLimit = pot - 50;

                if(potBottomLimit <= 0) {
                    potBottomLimit = 1;
                }

                potTopLimit = pot + 50;
            } else if(pot <= 300) {
                potBottomLimit = pot - 100;
                potTopLimit = pot + 100;
            } else {
                potBottomLimit = pot - 300;
                potTopLimit = pot + 300;
            }

            potQuery = "pot > " + potBottomLimit + " AND pot < " + potTopLimit;
        }

        return potQuery;
    }

    private String getOppStackQuery(double oppStack) {
        double oppStackBottomLimit = oppStack - 300;
        double oppStackTopLimit = oppStack + 300;

        String oppStackQuery = "opponentstack > " + oppStackBottomLimit + " AND opponentstack < " + oppStackTopLimit;

        return oppStackQuery;
    }

    private String getOppBetSizeQuery(double oppBetSize) {
        double oppBetSizeBottomLimit;
        double oppBetSizeTopLimit;

        String oppBetSizeQuery;

        if(oppBetSize == 0) {
            oppBetSizeQuery = "opponent_total_betsize = 0";
        } else {
            if(oppBetSize <= 100) {
                oppBetSizeBottomLimit = oppBetSize - 50;
                oppBetSizeTopLimit = oppBetSize + 50;
            } else if(oppBetSize <= 300) {
                oppBetSizeBottomLimit = oppBetSize - 100;
                oppBetSizeTopLimit = oppBetSize + 100;
            } else {
                oppBetSizeBottomLimit = oppBetSize - 300;
                oppBetSizeTopLimit = oppBetSize + 300;
            }

            oppBetSizeQuery = "opponent_total_betsize > " + oppBetSizeBottomLimit + " AND opponent_total_betsize < " + oppBetSizeTopLimit;
        }

        return oppBetSizeQuery;
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
