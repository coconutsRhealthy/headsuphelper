package com.lennart.model.action.actionbuilders.ai.foldstats;

import java.util.*;

/**
 * Created by LennartMac on 24/05/2018.
 */
public class FoldStatsKeeper {

    private static Map<String, Map<String, Double>> foldCountMap = new HashMap<>();
    private static Map<String, Map<Integer, String>> foldCountMapNew = new HashMap<>();


    public static void updateFoldCountMapNewNew(String playerName, String action) {
        if(foldCountMapNew.get(playerName) == null) {
            foldCountMapNew.put(playerName, new TreeMap<>(Collections.reverseOrder()));
        }

        Map<Integer, String> playerMap = foldCountMapNew.get(playerName);

        if(playerMap.size() >= 50) {
            playerMap.remove(getLowestIntKeyFromMap(playerMap));
        }

        int highestKey = getHighestKeyFromMap(playerMap);

        if(action != null) {
            if(action.equals("fold")) {
                playerMap.put(highestKey + 1, "fold");
            } else {
                playerMap.put(highestKey + 1, "nonFold");
            }
        }
    }

    public static double getFoldStatNewNew(String playerName) {
        double foldStat;

        Map<Integer, String> playerMap = foldCountMapNew.get(playerName);

        if(playerMap != null && playerMap.size() >= 20) {
            double foldCounter = 0;
            double total = playerMap.size();

            for (Map.Entry<Integer, String> entry : playerMap.entrySet()) {
                if(entry.getValue().equals("fold")) {
                    foldCounter++;
                }
            }

            System.out.println("playerMap size: " + playerMap.size());
            System.out.println("foldCounter: " + foldCounter);
            System.out.println("total: " + total);

            foldStat = foldCounter / total;

            System.out.println("folddddstat: " + foldStat);
        } else {
            foldStat = 0.43;
        }

        return foldStat;
    }






    public static void updateFoldCountMapNew(String playerName, String action) {
        if(foldCountMap.get(playerName) == null) {
            foldCountMap.put(playerName, new HashMap<>());
            foldCountMap.get(playerName).put("totalHandCount", 0.0);
            foldCountMap.get(playerName).put("foldCount", 0.0);
        }

        double totalUntilNow = foldCountMap.get(playerName).get("totalHandCount");
        System.out.println("totalUntilNow: " + totalUntilNow);
        foldCountMap.get(playerName).put("totalHandCount", totalUntilNow + 1);

        if(action != null && action.equals("fold")) {
            double foldTotalUntilNow = foldCountMap.get(playerName).get("foldCount");
            System.out.println("foldTotalUntilNow: " + foldTotalUntilNow);
            foldCountMap.get(playerName).put("foldCount", foldTotalUntilNow + 1);
        }
    }

    public static double getFoldStatNew(String playerName) {
        double foldStat;

        if(foldCountMap.get(playerName) != null) {
            double totalUntilNow = foldCountMap.get(playerName).get("totalHandCount");

            if(totalUntilNow < 20) {
                foldStat = 0.43;
            } else {
                double foldTotalUntilNow = foldCountMap.get(playerName).get("foldCount");
                foldStat = foldTotalUntilNow / totalUntilNow;
            }
        } else {
            foldStat = 0.43;
        }

        return foldStat;
    }

    private static int getLowestIntKeyFromMap(Map<Integer, String> map) {
        List<Integer> keysAsList = new ArrayList<>(map.keySet());
        Collections.sort(keysAsList);
        return keysAsList.get(0);
    }

    private static int getHighestKeyFromMap(Map<Integer, String> map) {
        if(map.isEmpty()) {
            return 0;
        } else {
            return map.entrySet().iterator().next().getKey();
        }
    }
}
