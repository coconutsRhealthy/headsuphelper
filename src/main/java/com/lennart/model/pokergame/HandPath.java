package com.lennart.model.pokergame;

/**
 * Created by LPO10346 on 9/8/2016.
 */
public class HandPath {
    //Future class to keep track of the path (line) in a hand. Should be send back and forth between server and client to
    //keep track.

    private static String handPathPreflop = "";
    private static String handPathFlop = "";
    private static String handPathTurn = "";
    private static String handPathRiver = "";
    private static String handPath = "";

//    public HandPath(String handPathPreflop, String handPathFlop, String handPathTurn, String handPathRiver) {
//        HandPath.handPathPreflop = handPathPreflop;
//        HandPath.handPathFlop = handPathFlop;
//        HandPath.handPathTurn = handPathTurn;
//        HandPath.handPathRiver = handPathRiver;
//        HandPath.handPath = handPathPreflop + handPathFlop + handPathTurn + handPathRiver;
//    }
//
//    public String getHandPath(Action action) {
//        return "2bet";
//    }


    public static String getHandPathPreflop() {
        return handPathPreflop;
    }

    public static void setHandPathPreflop(String handPathPreflop) {
        HandPath.handPathPreflop = handPathPreflop;
    }

    public static String getHandPathFlop() {
        return handPathFlop;
    }

    public static void setHandPathFlop(String handPathFlop) {
        HandPath.handPathFlop = handPathFlop;
    }

    public static String getHandPathTurn() {
        return handPathTurn;
    }

    public static void setHandPathTurn(String handPathTurn) {
        HandPath.handPathTurn = handPathTurn;
    }

    public static String getHandPathRiver() {
        return handPathRiver;
    }

    public static void setHandPathRiver(String handPathRiver) {
        HandPath.handPathRiver = handPathRiver;
    }

    public static String getHandPath() {
        return HandPath.getHandPathPreflop() + HandPath.getHandPathFlop() + HandPath.getHandPathTurn() + HandPath.getHandPathRiver();
    }

//    public static void setHandPath() {
//        HandPath.handPath = HandPath.getHandPathPreflop() + HandPath.
//    }
}
