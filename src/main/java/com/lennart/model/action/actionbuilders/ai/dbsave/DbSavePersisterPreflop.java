package com.lennart.model.action.actionbuilders.ai.dbsave;

import java.util.ArrayList;
import java.util.List;

public class DbSavePersisterPreflop {

//        } else if(handStrength < 0.55) {
//        table = opponentType + "_hs_50_55";
//        } else if(handStrength < 0.60) {
//        table = opponentType + "_hs_55_60";
//        } else if(handStrength < 0.65) {
//        table = opponentType + "_hs_60_65";
//        } else if(handStrength < 0.70) {
//        table = opponentType + "_hs_65_70";
//        } else if(handStrength < 0.75) {
//        table = opponentType + "_hs_70_75";


//            if(foldStat < 0.26) {
//        foldStatGroup = "Foldstat_0_33_";
//    } else if(foldStat <= 0.4) {
//        foldStatGroup = "Foldstat_33_66_";
//    } else if(foldStat == 0.43) {
//        foldStatGroup = "Foldstat_unknown";
//    } else {
//        foldStatGroup = "Foldstat_66_100_";
//    }





//    private List<String> getAllRaiseRoutes() {
//        List<String> handStrength = new ArrayList<>();
//        List<String> position = new ArrayList<>();
//        List<String> sizing = new ArrayList<>();
//        List<String> foldStatGroup = new ArrayList<>();
//        List<String> effectiveStack = new ArrayList<>();
//
//        handStrength.add("HS_0_20_");
//        handStrength.add("HS_20_35_");
//        handStrength.add("HS_35_50_");
//        handStrength.add("HS_50_60_");
//        handStrength.add("HS_60_70_");
//        handStrength.add("HS_70_75_");
//        handStrength.add("HS_75_80_");
//        handStrength.add("HS_80_85_");
//        handStrength.add("HS_85_90_");
//        handStrength.add("HS_90_95_");
//        handStrength.add("HS_95_100_");
//
//        position.add("Ip");
//        position.add("Oop");
//
//        sizing.add("Sizing_0-5bb");
//        sizing.add("Sizing_5-13bb");
//        sizing.add("Sizing_13-26bb");
//        sizing.add("Sizing_26bb_up");
//
//        foldStatGroup.add("Foldstat_unknown");
//        foldStatGroup.add("Foldstat_0_33_");
//        foldStatGroup.add("Foldstat_33_66_");
//        foldStatGroup.add("Foldstat_66_100_");
//
//        effectiveStack.add("Effstack_0-10bb");
//        effectiveStack.add("Effstack_10-30bb");
//        effectiveStack.add("Effstack_30-50bb");
//        effectiveStack.add("Effstack_50-75bb");
//        effectiveStack.add("Effstack_75-110bb");
//        effectiveStack.add("Effstack_110bb_up");
//
//        List<String> allRoutes = new ArrayList<>();
//
//        for(String a : handStrength) {
//            for(String b : position) {
//                for(String c : sizing) {
//                    for(String d : foldStatGroup) {
//                        for(String e : effectiveStack) {
//                            allRoutes.add(a + b + c + d + e);
//                        }
//                    }
//                }
//            }
//        }
//
//        System.out.println(allRoutes.size());
//
//        return allRoutes;
//    }

    private List<String> getAllCallRoutes() {
        List<String> handStrength = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> amountToCall = new ArrayList<>();
        List<String> foldStatGroup = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();

        handStrength.add("HS_0_20_");
        handStrength.add("HS_20_35_");
        handStrength.add("HS_35_50_");
        handStrength.add("HS_50_60_");
        handStrength.add("HS_60_70_");
        handStrength.add("HS_70_75_");
        handStrength.add("HS_75_80_");
        handStrength.add("HS_80_85_");
        handStrength.add("HS_85_90_");
        handStrength.add("HS_90_95_");
        handStrength.add("HS_95_100_");

        position.add("Ip");
        position.add("Oop");

        amountToCall.add("Atc_0-5bb");
        amountToCall.add("Atc_5-13bb");
        amountToCall.add("Atc_13-26bb");
        amountToCall.add("Atc_26bb_up");

        foldStatGroup.add("Foldstat_unknown");
        foldStatGroup.add("Foldstat_0_33_");
        foldStatGroup.add("Foldstat_33_66_");
        foldStatGroup.add("Foldstat_66_100_");

        effectiveStack.add("Effstack_0-10bb");
        effectiveStack.add("Effstack_10-30bb");
        effectiveStack.add("Effstack_30-50bb");
        effectiveStack.add("Effstack_50-75bb");
        effectiveStack.add("Effstack_75-110bb");
        effectiveStack.add("Effstack_110bb_up");

        List<String> allRoutes = new ArrayList<>();

        for(String a : handStrength) {
            for(String b : position) {
                for(String c : amountToCall) {
                    for(String d : foldStatGroup) {
                        for(String e : effectiveStack) {
                            allRoutes.add(a + b + c + d + e);
                        }
                    }
                }
            }
        }

        System.out.println(allRoutes.size());

        return allRoutes;
    }
}
