package com.lennart.model.action.actionbuilders.ai.opponenttypes;

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

    private static final double LP_LOOSENESS = 0.777;
    private static final double LP_AGGRO = 0.164;
    private static final double LA_LOOSENESS = 0.794;
    private static final double LA_AGGRO = 0.643;
    private static final double TP_LOOSENESS = 0.650;
    private static final double TP_AGGRO = 0.272;
    private static final double TA_LOOSENESS = 0.675;
    private static final double TA_AGGRO = 0.526;

    public String getOpponentType(String opponentNick, int numberOfHands) {
        String opponentType;

        if(numberOfHands < 19) {
            opponentType = "lp";
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

            Map<String, Double> loosenessMatchMap = getLoosenessMatchMap(looseness);
            Map<String, Double> aggroMatchMap = getAggroMatchMap(aggressiveness);

            opponentType = getMatch(loosenessMatchMap, aggroMatchMap);
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

    public static void updateNumberOfHandsPerOpponentMap(String opponentPlayerName) {
        if(numberOfHandsPerOpponentMap.get(opponentPlayerName) == null) {
            numberOfHandsPerOpponentMap.put(opponentPlayerName, 0);
        } else {
            numberOfHandsPerOpponentMap.put(opponentPlayerName, numberOfHandsPerOpponentMap.get(opponentPlayerName) + 1);
        }
    }

    public static Map<String, Integer> getNumberOfHandsPerOpponentMap() {
        return numberOfHandsPerOpponentMap;
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
        double lpDifference = Math.abs(looseness - LP_LOOSENESS);
        double laDifference = Math.abs(looseness - LA_LOOSENESS);
        double tpDifference = Math.abs(looseness - TP_LOOSENESS);
        double taDifference = Math.abs(looseness - TA_LOOSENESS);

        Map<String, Double> loosenessMatchMap = new HashMap<>();

        loosenessMatchMap.put("lp", lpDifference);
        loosenessMatchMap.put("la", laDifference);
        loosenessMatchMap.put("tp", tpDifference);
        loosenessMatchMap.put("ta", taDifference);

        return loosenessMatchMap;
    }

    private Map<String, Double> getAggroMatchMap(double aggressiveness) {
        double lpDifference = Math.abs(aggressiveness - LP_AGGRO);
        double laDifference = Math.abs(aggressiveness - LA_AGGRO);
        double tpDifference = Math.abs(aggressiveness - TP_AGGRO);
        double taDifference = Math.abs(aggressiveness - TA_AGGRO);

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
}
