package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_3_0;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LennartMac on 05/03/2022.
 */
public class AdjustPreflopPlayToOpp {

    //wat weet je allemaal?

        //hoe vaak opp pre2bet

        //hoe vaak opp preShoved

        //hoe vaak opp IP raist

        //hoe vaak opp OOP raist

        //hoe vaak opp een pre2bet callt

        //hoe vaak opp een shove callt


//    absolutStatsForOpp.put("_2betRatioForPlayer", _2betRatioForPlayer);
//    absolutStatsForOpp.put("shoveRatioForPlayer", shoveRatioForPlayer);
//    absolutStatsForOpp.put("call2betRatioForPlayer", call2betRatioForPlayer);
//    absolutStatsForOpp.put("ipRaiseRatioForPlayer", ipRaiseRatioForPlayer);
//    absolutStatsForOpp.put("oopRaiseRatioForPlayer", oopRaiseRatioForPlayer);
//    absolutStatsForOpp.put("overallRaiseRatioForPlayer", overallRaiseRatioForPlayer);
//    absolutStatsForOpp.put("overallCallRatioForPlayer", overallCallRatioForPlayer);


    private List<String> getPossibleAdjustments(Map<String, Double> oppRelativeStats) {
        Set<String> possibleAdjustments = new HashSet<>();

        double _2betDeviation = oppRelativeStats.get("_2betRatioForPlayer") - 0.5;
        double shoveDeviation = oppRelativeStats.get("shoveRatioForPlayer") - 0.5;
        double call2betDeviation = oppRelativeStats.get("call2betRatioForPlayer") - 0.5;
        double ipRaiseDeviation = oppRelativeStats.get("ipRaiseRatioForPlayer") - 0.5;
        double oopRaiseDeviation = oppRelativeStats.get("oopRaiseRatioForPlayer") - 0.5;
        double overallRaiseDeviation = oppRelativeStats.get("overallRaiseRatioForPlayer") - 0.5;
        double overallCallDeviation = oppRelativeStats.get("overallCallRatioForPlayer") - 0.5;

        if(_2betDeviation > 0) {
            //no changes needed
        } else if(_2betDeviation < 0) {
            double betDeviationAbsolute = _2betDeviation * -1;
            double random = Math.random();

            //weinig 3betten
            possibleAdjustments.add("nonWeak3bet");

        }

        if(shoveDeviation > 0) {
            //minder trash limpen
            //minder weak 2betten
            possibleAdjustments.add("nonTrashLimp");
            possibleAdjustments.add("nonWeak2bet");

        } else if(shoveDeviation < 0) {
            //no changes needed

        }

        if(call2betDeviation > 0) {
            //no changes needed

        } else if(call2betDeviation < 0) {
            //meer bluff 2betten

        }

        if(ipRaiseDeviation > 0) {
            //no changes needed

        } else if(ipRaiseDeviation < 0) {

            possibleAdjustments.add("nonWeak3bet");
        }

        if(oopRaiseDeviation > 0) {
            possibleAdjustments.add("nonTrashLimp");
        } else if(oopRaiseDeviation < 0) {
            //nothing


        }


        if(call2betDeviation < 0 && oopRaiseDeviation < 0) {
            possibleAdjustments.add("weak2bet");
        }










        return null;
    }


}
