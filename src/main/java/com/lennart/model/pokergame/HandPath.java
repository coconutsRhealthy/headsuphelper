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

    private String instanceHandPathPreflop;
    private String instanceHandPathFlop;
    private String instanceHandPathTurn;
    private String instanceHandPathRiver;
    private String instanceHandPath;

    public HandPath() {
        this.instanceHandPathPreflop = getHandPathPreflop();
        this.instanceHandPathFlop = getHandPathFlop();
        this.instanceHandPathTurn = getHandPathTurn();
        this.instanceHandPathRiver = getHandPathRiver();
        this.instanceHandPath = getHandPath();
    }


    public static String getHandPathPreflop() {
        return handPathPreflop;
    }

    public static void setHandPathPreflop(String handPath) {
        HandPath.handPathPreflop = handPath;
    }

    public static String getHandPathFlop() {
        return handPathFlop;
    }

    public static void setHandPathFlop(String handPath) {
        String handPathThusFar = HandPath.getHandPath();
        HandPath.handPathFlop = handPath.replaceAll(handPathThusFar, "");
    }

    public static String getHandPathTurn() {
        return handPathTurn;
    }

    public static void setHandPathTurn(String handPath) {
        String handPathThusFar = HandPath.getHandPath();
        HandPath.handPathTurn = handPath.replaceAll(handPathThusFar, "");
    }

    public static String getHandPathRiver() {
        return handPathRiver;
    }

    public static void setHandPathRiver(String handPath) {
        String handPathThusFar = HandPath.getHandPath();
        HandPath.handPathRiver = handPath.replaceAll(handPathThusFar, "");
    }

    public static String getHandPath() {
        return HandPath.getHandPathPreflop() + HandPath.getHandPathFlop() + HandPath.getHandPathTurn() + HandPath.getHandPathRiver();
    }

    public String getInstanceHandPathPreflop() {
        return instanceHandPathPreflop;
    }

    public void setInstanceHandPathPreflop(String instanceHandPathPreflop) {
        this.instanceHandPathPreflop = instanceHandPathPreflop;
    }

    public String getInstanceHandPathFlop() {
        return instanceHandPathFlop;
    }

    public void setInstanceHandPathFlop(String instanceHandPathFlop) {
        this.instanceHandPathFlop = instanceHandPathFlop;
    }

    public String getInstanceHandPathTurn() {
        return instanceHandPathTurn;
    }

    public void setInstanceHandPathTurn(String instanceHandPathTurn) {
        this.instanceHandPathTurn = instanceHandPathTurn;
    }

    public String getInstanceHandPathRiver() {
        return instanceHandPathRiver;
    }

    public void setInstanceHandPathRiver(String instanceHandPathRiver) {
        this.instanceHandPathRiver = instanceHandPathRiver;
    }

    public String getInstanceHandPath() {
        return instanceHandPath;
    }

    public void setInstanceHandPath(String instanceHandPath) {
        this.instanceHandPath = instanceHandPath;
    }
}
