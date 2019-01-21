package com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSave;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSaveBluff;
import com.lennart.model.action.actionbuilders.ai.dbsave.DbSavePersister;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 21/01/2019.
 */
public class DbSavePersisterPostflop_2_0 extends DbSavePersister {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new DbSavePersisterPostflop_2_0().initializeDb("dbstats_bluff_sng_compact_2_0");
        //new DbSavePersisterPostflop_2_0().testMethod();
    }

    private void testMethod() {
        List<String> hmm = new ArrayList<>();
        List<String> hmm2 = new ArrayList<>();

        hmm.add("Tp");
        hmm.add("Ta");
        hmm.add("Lp");
        hmm.add("La");

        hmm2.add("tp");
        hmm2.add("ta");
        hmm2.add("lp");
        hmm2.add("la");


        for(String s : hmm) {
            for(String g : hmm2) {
                System.out.println("opponentTypeGroup.add(\"" + s + g + "\");");
            }
        }
    }

    @Override
    public void doDbSaveUpdate(ContinuousTable continuousTable, double bigBlind) throws Exception {
        List<DbSave> dbSaveList = continuousTable.getDbSaveList();

        initializeDbConnection();

        Statement st = con.createStatement();

        for(DbSave dbSave : dbSaveList) {
            if(dbSave instanceof DbSaveBluff) {
                DbSaveBluff dbSaveBluff = (DbSaveBluff) dbSave;

                String routeCompact_2_0 = dbSaveBluff.getStreet() + dbSaveBluff.getBluffAction() + dbSaveBluff.getPosition() +
                        convertBluffOrValueSizingToCompact(dbSaveBluff.getSizingGroup()) +
                        dbSaveBluff.getOpponentType() + dbSaveBluff.getStrongDraw() +
                        convertEffectiveStackToCompact(dbSaveBluff.getEffectiveStack());

                verifyRouteExists(routeCompact_2_0, "dbstats_bluff_sng_compact_2_0");

                if(actionWasSuccessfull(bigBlind)) {
                    st.executeUpdate("UPDATE dbstats_bluff_sng_compact_2_0 SET success = success + 1 WHERE route = '" + routeCompact_2_0 + "'");
                }

                st.executeUpdate("UPDATE dbstats_bluff_sng_compact_2_0 SET total = total + 1 WHERE route = '" + routeCompact_2_0 + "'");
            }
        }

        st.close();
        closeDbConnection();
    }

    private void initializeDb(String table) throws Exception {
        List<String> allRoutes = getAllBluffRoutesCompact();

        initializeDbConnection();

        for(String route : allRoutes) {
            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO " + table + " (route) VALUES ('" + route + "')");

            st.close();
        }

        closeDbConnection();
    }

    private List<String> getAllBluffRoutesCompact() {
        List<String> street = new ArrayList<>();
        List<String> bluffAction = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> sizingGroup = new ArrayList<>();
        List<String> opponentTypeGroup = new ArrayList<>();
        List<String> strongDraw = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();

        street.add("Flop");
        street.add("Turn");
        street.add("River");

        bluffAction.add("Bet");
        bluffAction.add("Raise");

        position.add("Ip");
        position.add("Oop");

        sizingGroup.add("Sizing_0-10bb");
        sizingGroup.add("Sizing_10-20bb");
        sizingGroup.add("Sizing_20bb_up");

        opponentTypeGroup.add("Uuuu");
        opponentTypeGroup.add("Tptp");
        opponentTypeGroup.add("Tpta");
        opponentTypeGroup.add("Tplp");
        opponentTypeGroup.add("Tpla");
        opponentTypeGroup.add("Tatp");
        opponentTypeGroup.add("Tata");
        opponentTypeGroup.add("Talp");
        opponentTypeGroup.add("Tala");
        opponentTypeGroup.add("Lptp");
        opponentTypeGroup.add("Lpta");
        opponentTypeGroup.add("Lplp");
        opponentTypeGroup.add("Lpla");
        opponentTypeGroup.add("Latp");
        opponentTypeGroup.add("Lata");
        opponentTypeGroup.add("Lalp");
        opponentTypeGroup.add("Lala");

        strongDraw.add("StrongDrawTrue");
        strongDraw.add("StrongDrawFalse");

        effectiveStack.add("EffStack_0_35_");
        effectiveStack.add("EffStack_35_up_");

        List<String> allRoutes = new ArrayList<>();

        for(String a : street) {
            for(String b : bluffAction) {
                for(String c : position) {
                    for(String d : sizingGroup) {
                        for(String e : opponentTypeGroup) {
                            for(String f : strongDraw) {
                                for(String g : effectiveStack) {
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

    private void verifyRouteExists(String route, String table) throws Exception {
        Statement st2 = con.createStatement();

        ResultSet rs2 = st2.executeQuery("SELECT * FROM " + table + " WHERE route = '" + route + "';");

        if(!rs2.next()) {
            System.out.println("Postflop Route does not exist! " + route + "    table: " + table);
        } else {
            System.out.println("Postflop Route exists: " + route + "     table: " + table);
        }

        rs2.close();
        st2.close();
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker_2_0?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

}
