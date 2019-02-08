package com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePersisterPreflop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 26/01/2019.
 */
public class DbSavePersisterPreflop_2_0 {

    private Connection con_2_0;

    public static void main(String[] args) throws Exception {
        //new DbSavePersisterPreflop_2_0().getAllPfCallRoutesCompact();
        new DbSavePersisterPreflop_2_0().initializeDb("dbstats_pf_call_sng_compact_2_0");
    }

    private void initializeDb(String table) throws Exception {
        List<String> allRoutes = getAllPfCallRoutesCompact();

        initialize_2_0_DbConnection();

        for(String route : allRoutes) {
            Statement st = con_2_0.createStatement();

            st.executeUpdate("INSERT INTO " + table + " (route) VALUES ('" + route + "')");

            st.close();
        }

        close_2_0_DbConnection();
    }

    public List<String> getAllPfRaiseRoutesCompact() {
        List<String> holeCards;
        List<String> position = new ArrayList<>();
        List<String> sizing = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();
        List<String> oppType = new ArrayList<>();

        holeCards = new DbSavePersisterPreflop().getAllHoleCardCombos();

        position.add("Ip");
        position.add("Oop");

        sizing.add("Sizing_0-5bb");
        sizing.add("Sizing_5-13bb");
        sizing.add("Sizing_13-26bb");
        sizing.add("Sizing_26bb_up");

        effectiveStack.add("EffStack_0_35_");
        effectiveStack.add("EffStack_35_up_");

        oppType.add("OppTypeA");
        oppType.add("OppTypeB");
        oppType.add("OppTypeC");
        oppType.add("OppTypeD");

        List<String> allRoutes = new ArrayList<>();

        for(String a : holeCards) {
            for(String b : position) {
                for(String c : sizing) {
                    for(String d : effectiveStack) {
                        for(String e : oppType) {
                            allRoutes.add(a + b + c + d + e);
                        }
                    }
                }
            }
        }

        System.out.println(allRoutes.size());

        return allRoutes;
    }

    public List<String> getAllPfCallRoutesCompact() {
        List<String> handStrength = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> amountToCall = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();
        List<String> oppType = new ArrayList<>();

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

        effectiveStack.add("Effstack_0-10bb");
        effectiveStack.add("Effstack_10-30bb");
        effectiveStack.add("Effstack_30-50bb");
        effectiveStack.add("Effstack_50-75bb");
        effectiveStack.add("Effstack_75-110bb");
        effectiveStack.add("Effstack_110bb_up");

        oppType.add("OppTypeA");
        oppType.add("OppTypeB");
        oppType.add("OppTypeC");
        oppType.add("OppTypeD");

        List<String> allRoutes = new ArrayList<>();

        for(String a : handStrength) {
            for(String b : position) {
                for(String c : amountToCall) {
                    for(String d : effectiveStack) {
                        for(String e : oppType) {
                            allRoutes.add(a + b + c + d + e);
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