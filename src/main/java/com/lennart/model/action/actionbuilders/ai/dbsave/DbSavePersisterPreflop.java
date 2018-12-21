package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbSavePersisterPreflop {

    private Connection con;

    public void doDbSaveUpdate(ContinuousTable continuousTable, double bigBlind) throws Exception {
        List<DbSave> dbSaveList = continuousTable.getDbSaveList();

        initializeDbConnection();

        Statement st = con.createStatement();

        String callTable;
        String raiseTable;

        if(continuousTable.getGame().equals("playMoney")) {
            raiseTable = "toImplement";
            callTable = "toImplement";
        } else if (continuousTable.getGame().equals("sng")) {
            raiseTable = "dbstats_pf_raise_sng";
            callTable = "dbstats_pf_call_sng";
        } else {
            raiseTable = "toImplement";
            callTable = "toImplement";
        }

        for(DbSave dbSave : dbSaveList) {
            if(dbSave instanceof DbSavePreflopRaise) {
                DbSavePreflopRaise dbSavePreflopRaise = (DbSavePreflopRaise) dbSave;

                String route = dbSavePreflopRaise.getHandStrength() + dbSavePreflopRaise.getPosition() +
                        dbSavePreflopRaise.getSizing() + dbSavePreflopRaise.getFoldStatGroup() +
                        dbSavePreflopRaise.getEffectiveStack();

                if(new DbSavePersister().actionWasSuccessfull(bigBlind)) {
                    st.executeUpdate("UPDATE " + raiseTable + " SET success = success + 1 WHERE route = '" + route + "'");
                }

                st.executeUpdate("UPDATE " + raiseTable + " SET total = total + 1 WHERE route = '" + route + "'");
            } else if(dbSave instanceof DbSavePreflopCall) {
                DbSavePreflopCall dbSavePreflopCall = (DbSavePreflopCall) dbSave;

                String route = dbSavePreflopCall.getHandStrenght() + dbSavePreflopCall.getPosition() +
                        dbSavePreflopCall.getAmountToCallBb() + dbSavePreflopCall.getFoldStatGroup() +
                        dbSavePreflopCall.getEffectiveStack();

                if(new DbSavePersister().actionWasSuccessfull(bigBlind)) {
                    st.executeUpdate("UPDATE " + callTable + " SET success = success + 1 WHERE route = '" + route + "'");
                }

                st.executeUpdate("UPDATE " + callTable + " SET total = total + 1 WHERE route = '" + route + "'");
            }
        }

        st.close();
        closeDbConnection();
    }

    public static void main(String[] args) throws Exception {
        new DbSavePersisterPreflop().initializePreflopRaiseDb();
    }

    private void initializePreflopCallDb() throws Exception {
        List<String> allRoutes = getAllPfCallRoutes();

        initializeDbConnection();

        for(String route : allRoutes) {
            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO dbstats_pf_call_sng (route) VALUES ('" + route + "')");

            st.close();
        }

        closeDbConnection();
    }

    private void initializePreflopRaiseDb() throws Exception {
        List<String> allRoutes = getAllPfRaiseRoutes();

        initializeDbConnection();

        for(String route : allRoutes) {
            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO dbstats_pf_raise_sng (route) VALUES ('" + route + "')");

            st.close();
        }

        closeDbConnection();
    }

    private List<String> getAllPfRaiseRoutes() {
        List<String> handStrength = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> sizing = new ArrayList<>();
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

        sizing.add("Sizing_0-5bb");
        sizing.add("Sizing_5-13bb");
        sizing.add("Sizing_13-26bb");
        sizing.add("Sizing_26bb_up");

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
                for(String c : sizing) {
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

    private List<String> getAllPfCallRoutes() {
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
