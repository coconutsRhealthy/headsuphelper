package com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePersister;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 21/01/2019.
 */
public class DbSavePersisterPostflop_2_0 extends DbSavePersister {

    private Connection con_2_0;

    public static void main(String[] args) throws Exception {
        //new DbSavePersisterPostflop_2_0().getAllCallRoutesCompact();
        new DbSavePersisterPostflop_2_0().initializeDb("dbstats_call_sng_compact_2_0");
    }

    private void initializeDb(String table) throws Exception {
        List<String> allRoutes = getAllCallRoutesCompact();

        initialize_2_0_DbConnection();

        for(String route : allRoutes) {
            Statement st = con_2_0.createStatement();

            st.executeUpdate("INSERT INTO " + table + " (route) VALUES ('" + route + "')");

            st.close();
        }

        close_2_0_DbConnection();
    }

    public List<String> getAllBluffRoutesCompact() {
        List<String> street = new ArrayList<>();
        List<String> bluffAction = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> sizingGroup = new ArrayList<>();
        List<String> strongDraw = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();
        List<String> oppType = new ArrayList<>();

        street.add("Flop");
        street.add("Turn");
        street.add("River");

        bluffAction.add("Bet");
        bluffAction.add("Raise");

        position.add("Ip");
        position.add("Oop");

        sizingGroup.add("Sizing_0-5bb");
        sizingGroup.add("Sizing_5-10bb");
        sizingGroup.add("Sizing_10-20bb");
        sizingGroup.add("Sizing_20bb_up");

        strongDraw.add("StrongDrawTrue");
        strongDraw.add("StrongDrawFalse");

        effectiveStack.add("EffStack_0_35_");
        effectiveStack.add("EffStack_35_up_");

        oppType.add("OppTypeA");
        oppType.add("OppTypeB");
        oppType.add("OppTypeC");
        oppType.add("OppTypeD");

        List<String> allRoutes = new ArrayList<>();

        for(String a : street) {
            for(String b : bluffAction) {
                for(String c : position) {
                    for(String d : sizingGroup) {
                        for(String e : strongDraw) {
                            for(String f : effectiveStack) {
                                for(String g : oppType) {
                                    allRoutes.add(a + b + c + d + e + f + g);
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println(allRoutes.size());

        return allRoutes;
    }

    public List<String> getAllValueRoutesCompact() {
        List<String> street = new ArrayList<>();
        List<String> valueAction = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> sizing = new ArrayList<>();
        List<String> handStrength = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();
        List<String> oppType = new ArrayList<>();

        street.add("Flop");
        street.add("Turn");
        street.add("River");

        valueAction.add("Bet");
        valueAction.add("Raise");

        position.add("Ip");
        position.add("Oop");

        sizing.add("Sizing_0-5bb");
        sizing.add("Sizing_5-10bb");
        sizing.add("Sizing_10-20bb");
        sizing.add("Sizing_20bb_up");

        handStrength.add("HS_70_75_");
        handStrength.add("HS_75_80_");
        handStrength.add("HS_80_85_");
        handStrength.add("HS_85_90_");
        handStrength.add("HS_90_95_");
        handStrength.add("HS_95_100_");

        effectiveStack.add("EffStack_0_35_");
        effectiveStack.add("EffStack_35_up_");

        oppType.add("OppTypeA");
        oppType.add("OppTypeB");
        oppType.add("OppTypeC");
        oppType.add("OppTypeD");

        List<String> allRoutes = new ArrayList<>();

        for(String a : street) {
            for(String b : valueAction) {
                for(String c : position) {
                    for(String d : sizing) {
                        for(String e : handStrength) {
                            for(String f : effectiveStack) {
                                for(String g : oppType) {
                                    allRoutes.add(a + b + c + d + e + f + g);
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println(allRoutes.size());

        return allRoutes;
    }

    public List<String> getAllCallRoutesCompact() {
        List<String> street = new ArrayList<>();
        List<String> facingAction = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> amountToCallGroup = new ArrayList<>();
        List<String> handStrength = new ArrayList<>();
        List<String> strongDraw = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();
        List<String> oppType = new ArrayList<>();

        street.add("Flop");
        street.add("Turn");
        street.add("River");

        facingAction.add("FacingBet");
        facingAction.add("FacingRaise");

        position.add("Ip");
        position.add("Oop");

        amountToCallGroup.add("Atc_0-10bb");
        amountToCallGroup.add("Atc_10-20bb");
        amountToCallGroup.add("Atc_20bb_up");

        handStrength.add("HS_0_30_");
        handStrength.add("HS_30_50_");
        handStrength.add("HS_50_60_");
        handStrength.add("HS_60_70_");
        handStrength.add("HS_70_80_");
        handStrength.add("HS_80_90_");
        handStrength.add("HS_90_100_");

        strongDraw.add("StrongDrawTrue");
        strongDraw.add("StrongDrawFalse");

        effectiveStack.add("EffStack_0_35_");
        effectiveStack.add("EffStack_35_up_");

        oppType.add("OppTypeA");
        oppType.add("OppTypeB");
        oppType.add("OppTypeC");
        oppType.add("OppTypeD");

        List<String> allRoutes = new ArrayList<>();

        for(String a : street) {
            for(String b : facingAction) {
                for(String c : position) {
                    for(String d : amountToCallGroup) {
                        for(String e : handStrength) {
                            for(String f : strongDraw) {
                                for(String g : effectiveStack) {
                                    for(String h : oppType) {
                                        allRoutes.add(a + b + c + d + e + f + g + h);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println(allRoutes.size());

        return allRoutes;
    }

    private void initialize_2_0_DbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con_2_0 = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker_2_0?&serverTimezone=UTC", "root", "");
    }

    private void close_2_0_DbConnection() throws SQLException {
        con_2_0.close();
    }
}
