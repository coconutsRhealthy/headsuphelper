package com.lennart.model.action.actionbuilders.ai.prime;

/**
 * Created by LennartMac on 02/10/2019.
 */
public class ArFinder {

    //handstrength
        //1) 3%+ / 3%-
        //2) 5%+ / 5%-                                      16

    //position
        //1) same position
        //2) oop                                            29

    //strongdraw
        //1) strongdraw true
        //2) strongdraw false                               27

    //street
        //1) samestreet
        //2) anystreet                                      28

    //botstack
        //1) +100 / - 100
        //2) +200 / - 200                                   2
        //3) +300 / - 300                                   4
        //4) +400 / - 400                                   12
        //5) +500 / - 500                                   19
        //6) +750 / - 750

    //oppstack
        //1) +100 / - 100
        //2) +200 / - 200                                   1
        //3) +300 / - 300                                   3
        //4) +400 / - 400                                   11
        //5) +500 / - 500                                   18
        //6) +750 / - 750

    //botbetsize
        //1) +50 / - 50
        //2) +100 / - 100                                   7
        //3) +200 / - 200                                   14
        //4) +350 / - 350                                   21
        //5) +500 / - 500                                   24

    //oppbetsize
        //1) +50 / - 50
        //2) +100 / - 100                                   6
        //3) +200 / - 200                                   13
        //4) +350 / - 350                                   20
        //5) +500 / - 500                                   23

    //pot
        //1) +50 / - 50
        //2) +100 / - 100                                   5
        //3) +200 / - 200                                   15
        //4) +350 / - 350                                   17
        //5) +500 / - 500                                   25

    //sizing
        //1) +50 / - 50
        //2) +100 / - 100                                   8
        //3) +200 / - 200                                   17
        //4) +350 / - 350                                   22
        //5) +500 / - 500                                   26

    //opptype
        //1) narrow opptype match (incl medium)
        //2) broader opptype match (only T / A)             10
        //3) all opptypes                                   30

    //oppAction
        //


    public static void main(String[] args) {
        ArFinder arFinder = new ArFinder();

        String query = arFinder.buildQuery(0.72, true, false, null, 0, 0, 0, 0, 0, 0, null);

        System.out.println(query);
    }

    private String buildQuery(double handstrength,
                              boolean position,
                              boolean strongDraw,
                              String street,
                              double botStack,
                              double oppStack,
                              double botBetSize,
                              double oppbetsize,
                              double pot,
                              double sizing,
                              String oppType) {
        String query;

        query = "SELECT * FROM dbstats_raw WHERE " + getHandStrengthQuery(handstrength) +
                " AND " + getStreetQuery("aa") +
                " AND " + getPositionQuery(position) +
                " AND " + getOppActionQuery("bet75pct") +
                ";";

        return query;
    }

    private String getHandStrengthQuery(double handstrength) {
        double bottomHsLimit = handstrength * 0.97;
        double topHsLimit = handstrength * 1.03;

        String hsQuery = "handstrength > " + bottomHsLimit + " AND handstrength < " + topHsLimit;

        return hsQuery;
    }

    private String getStreetQuery(String street) {
        String streetQuery = "z";

        if(street != null && !street.isEmpty()) {
            streetQuery = "board != ''";
        }

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


    //ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry < 100 ORDER BY entry ASC;");




    //8984
    //12708
}
