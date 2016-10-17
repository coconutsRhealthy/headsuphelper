package com.lennart.model.rangebuilder;

import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by LPO10346 on 10/17/2016.
 */
public class PreflopRangeBuilder {

    PreflopRangeBuilderUtil preflopRangeBuilderUtil = new PreflopRangeBuilderUtil();

    //IP
    Map<Integer, Map<Integer, Set<Card>>> get2bet() {
        Map<Integer, Map<Integer, Set<Card>>> preflopRange = new HashMap<>();

        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getPocketPairs(2));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getSuitedHoleCards(2, 2));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getOffSuitConnectors(4));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getOffSuitOneGappers(6));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getOffSuitTwoGappers(8));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getOffSuitThreeGappers(8));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getOffSuitHoleCards(12, 2));

        return preflopRange;
    }

    Map<Integer, Map<Integer, Set<Card>>> getCall3bet() {
        Map<Integer, Map<Integer, Set<Card>>> preflopRange = new HashMap<>();

        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getPocketPairs(2));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getSuitedHoleCards(2, 2));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getOffSuitConnectors(4));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getOffSuitOneGappers(6));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getOffSuitTwoGappers(8));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getOffSuitThreeGappers(9));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getOffSuitHoleCards(14, 2));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getOffSuitHoleCards(13, 9));
        preflopRange.put(preflopRange.size(), preflopRangeBuilderUtil.getBroadWayHoleCards());

        return preflopRange;
    }

    Map<Integer, Map<Integer, Set<Card>>> get4bet() {





        return null;
    }


    //OOP
    Map<Integer, Map<Integer, Set<Card>>> getCall2bet() {
        return null;
    }

    Map<Integer, Map<Integer, Set<Card>>> get3bet() {
        return null;
    }

    Map<Integer, Map<Integer, Set<Card>>> getCall4bet() {
        return null;
    }
}
