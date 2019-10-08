package com.lennart.model.action.actionbuilders.ai.prime;

/**
 * Created by LennartMac on 02/10/2019.
 */
public class ArFinder {

    //handstrength
        //1) 3%+ / 3%-                                  2
        //2) 5%+ / 5%-

    //position                                          4
        //1) same position
        //2) oop

    //strongdraw - ALLLEEN ALS JIJ SD HEBT
        //1) strongdraw true
        //2) strongdraw false

    //street                                            5
        //1) samestreet
        //2) anystreet

    //oppstack                                          6
        //1) +100 / - 100
        //2) +200 / - 200
        //3) +300 / - 300
        //4) +400 / - 400
        //5) +500 / - 500
        //6) +750 / - 750

    //oppbetsize - ALLEEN ALS OPPACTION = BET OF RAISE  6
        //1) +50 / - 50
        //2) +100 / - 100
        //3) +200 / - 200
        //4) +350 / - 350
        //5) +500 / - 500

    //pot                                               6
        //1) +50 / - 50
        //2) +100 / - 100
        //3) +200 / - 200
        //4) +350 / - 350
        //5) +500 / - 500

    //opptype                                           3
        //1) narrow opptype match (incl medium)
        //2) broader opptype match (only T / A)
        //3) all opptypes

    //oppAction                                         1
        //1) same oppAction
        //2) all oppActions



    ///*********

    //1
        //handstrength
        //oppAction

    //2
        //oppType
        //position
        //street

    //3
        //pot
        //oppstack
        //opbetsize








    ///////
        //match gewoon eerst hs, oppaction en pot, bet en stacksizes (range van 200)

        //vervolgens voeg je oppType toe
            //narrow, broad

        //dan position

        //dan street





//    private String buildQueryNew(double handstrength,
//                                 String combo,
//                                 String oppAction,
//                                 String oppType,
//                                 boolean position,
//                                 String street,
//                                 double pot,
//                                 double oppStack,
//                                 double oppBetSize,
//                                 boolean strongDraw) {
//        String firstQuery = "SELECT * FROM dbstats_raw WHERE " + getOppActionQuery(oppAction) +
//                            "AND " + getHandStrengthOrComboQuery(street, combo, handstrength);
//
//    }






//    public static void main(String[] args) {
//        ArFinder arFinder = new ArFinder();
//
//        String query = arFinder.buildQuery(0.72, true, false, "Flop", 0, 0, 0, 0, 0, 0, "LA");
//
//        System.out.println(query);
//    }

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

    private String getHandStrengthOrComboQuery(String street, String combo, double handstrength) {
        String hsOrComboQuery;

        if(street.equals("Preflop")) {
            hsOrComboQuery = "combo = '" + combo + "'";
        } else {
            double bottomHsLimit = handstrength * 0.97;
            double topHsLimit = handstrength * 1.03;

            hsOrComboQuery = "handstrength > " + bottomHsLimit + " AND handstrength < " + topHsLimit;
        }

        return hsOrComboQuery;
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

        if(pot <= 100) {
            potBottomLimit = pot - 50;
            potTopLimit = pot + 50;
        } else if(pot <= 300) {
            potBottomLimit = pot - 100;
            potTopLimit = pot + 100;
        } else {
            potBottomLimit = pot - 300;
            potTopLimit = pot + 300;
        }

        String potQuery = "pot > " + potBottomLimit + " AND pot < " + potTopLimit;

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

        String oppBetSizeQuery = "opponent_total_betsize > " + oppBetSizeBottomLimit + " AND opponent_total_betsize < " + oppBetSizeTopLimit;

        return oppBetSizeQuery;
    }
}
