package com.lennart.model.action.actionbuilders.ai.foldstats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LennartMac on 24/05/2018.
 */
public class FoldStatsKeeper {

    private static Map<String, Map<String, Double>> foldCountMap = new HashMap<>();

    public static void updateFoldCountMap(String playerName, String action) {
        if(foldCountMap.get(playerName) == null) {
            foldCountMap.put(playerName, new HashMap<>());
            foldCountMap.get(playerName).put("totalHandCount", 0.0);
            foldCountMap.get(playerName).put("foldCount", 0.0);
        }

        double totalUntilNow = foldCountMap.get(playerName).get("totalHandCount");
        System.out.println("totalUntilNow: " + totalUntilNow);
        foldCountMap.get(playerName).put("totalHandCount", totalUntilNow + 1);

        if(action.equals("fold")) {
            double foldTotalUntilNow = foldCountMap.get(playerName).get("foldCount");
            System.out.println("foldTotalUntilNow: " + foldTotalUntilNow);
            foldCountMap.get(playerName).put("foldCount", foldTotalUntilNow + 1);
        }
    }

    public static double getFoldStat(String playerName) {
        double foldStat;

        if(foldCountMap.get(playerName) != null) {
            double totalUntilNow = foldCountMap.get(playerName).get("totalHandCount");

            if(totalUntilNow < 20) {
                foldStat = 0.45;
            } else {
                double foldTotalUntilNow = foldCountMap.get(playerName).get("foldCount");
                foldStat = foldTotalUntilNow / totalUntilNow;
            }
        } else {
            foldStat = 0.45;
        }

        return foldStat;
    }
}
