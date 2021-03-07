package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;

import java.sql.*;
import java.util.List;

public class DbSavePersister {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new DbSavePersister().comparisonMethodStackDepthNoStackDepthCompact();
    }

    private void comparisonMethodStackDepthNoStackDepthCompact() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_value_sng_compact;");

        while(rs.next()) {
            double total = rs.getDouble("total");
            String routeWithoutStackDepth = rs.getString("route");

            if(total >= 10) {
                Statement st2 = con.createStatement();
                ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_value_sng_compact_stackdepth;");

                while(rs2.next()) {
                    String routeWithStackDepth = rs2.getString("route");

                    if(routeWithStackDepth.contains(routeWithoutStackDepth)) {
                        double totalWithStackDepth = rs2.getDouble("total");
                        double totalWithoutStackDepth = rs.getDouble("total");

                        if(totalWithStackDepth >= 5) {
                            double ratioWithoutStackDepth = rs.getDouble("success") / rs.getDouble("total");
                            double ratioWithStackDepth = rs2.getDouble("success") / rs2.getDouble("total");

                            if(ratioWithoutStackDepth < 0.5 && ratioWithStackDepth > 0.5) {
                                System.out.println("without: " + ratioWithoutStackDepth);
                                System.out.println("total without: " + totalWithoutStackDepth);
                                System.out.println("with: " + ratioWithStackDepth);
                                System.out.println("total with: " + totalWithStackDepth);
                                System.out.println("route without: " + routeWithoutStackDepth);
                                System.out.println("route with: " + routeWithStackDepth);
                                System.out.println();
                            }
                        }
                    }
                }

                rs2.close();
                st2.close();
            }
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    public void doDbSaveUpdate(ContinuousTable continuousTable, double bigBlind) throws Exception {
        List<DbSave> dbSaveList = continuousTable.getDbSaveList();

        initializeDbConnection();

        Statement st = con.createStatement();

        String bluffTable;
        String bluffTableCompact;
        String callTable;
        String callTableCompact;
        String valueTable;
        String valueTableCompact;

        if(continuousTable.getGame().equals("playMoney")) {
            bluffTable = "dbstats_bluff_play";
            bluffTableCompact = "dbstats_bluff_play_compact";
            callTable = "dbstats_call_play";
            callTableCompact = "dbstats_call_play_compact";
            valueTable = "dbstats_value_play";
            valueTableCompact = "dbstats_value_play_compact";
        } else if (continuousTable.getGame().equals("sng")) {
            bluffTable = "dbstats_bluff_sng";
            bluffTableCompact = "dbstats_bluff_sng_compact_stackdepth";
            callTable = "dbstats_call_sng";
            callTableCompact = "dbstats_call_sng_compact_stackdepth";
            valueTable = "dbstats_value_sng";
            valueTableCompact = "dbstats_value_sng_compact_stackdepth";
        } else {
            bluffTable = "dbstats_bluff_50nl";
            bluffTableCompact = "dbstats_bluff_50nl_compact";
            callTable = "dbstats_call_50nl";
            callTableCompact = "dbstats_call_50nl_compact";
            valueTable = "dbstats_value_50nl";
            valueTableCompact = "dbstats_value_50nl_compact";
        }

        for(DbSave dbSave : dbSaveList) {
            if(dbSave instanceof DbSaveBluff) {
                DbSaveBluff dbSaveBluff = (DbSaveBluff) dbSave;

                String route = dbSaveBluff.getStreet() + dbSaveBluff.getBluffAction() + dbSaveBluff.getPosition() +
                        dbSaveBluff.getSizingGroup() + dbSaveBluff.getFoldStatGroup() + dbSaveBluff.getEffectiveStack()
                        + dbSaveBluff.getHandStrength() + dbSaveBluff.getDrawWetness() + dbSaveBluff.getBoatWetness()
                        + dbSaveBluff.getStrongDraw();

                String routeCompact = dbSaveBluff.getStreet() + dbSaveBluff.getBluffAction() + dbSaveBluff.getPosition() +
                        convertBluffOrValueSizingToCompact(dbSaveBluff.getSizingGroup()) +
                        dbSaveBluff.getFoldStatGroup() + dbSaveBluff.getStrongDraw() +
                        convertEffectiveStackToCompact(dbSaveBluff.getEffectiveStack());

                if(actionWasSuccessfull()) {
                    st.executeUpdate("UPDATE " + bluffTable + " SET success = success + 1 WHERE route = '" + route + "'");
                    st.executeUpdate("UPDATE " + bluffTableCompact + " SET success = success + 1 WHERE route = '" + routeCompact + "'");
                }

                st.executeUpdate("UPDATE " + bluffTable + " SET total = total + 1 WHERE route = '" + route + "'");
                st.executeUpdate("UPDATE " + bluffTableCompact + " SET total = total + 1 WHERE route = '" + routeCompact + "'");
            } else if(dbSave instanceof DbSaveCall) {
                DbSaveCall dbSaveCall = (DbSaveCall) dbSave;

                String route = dbSaveCall.getStreet() + dbSaveCall.getFacingAction() + dbSaveCall.getPosition() +
                        dbSaveCall.getAmountToCallGroup() + dbSaveCall.getOppAggroGroup() + dbSaveCall.getHandStrength() +
                        dbSaveCall.getStrongDraw() + dbSaveCall.getEffectiveStack() + dbSaveCall.getDrawWetness() +
                        dbSaveCall.getBoatWetness();

                String routeCompact = dbSaveCall.getStreet() + dbSaveCall.getFacingAction() + dbSaveCall.getPosition() +
                        convertCallAtcToCompact(dbSaveCall.getAmountToCallGroup()) + dbSaveCall.getOppAggroGroup() +
                        dbSaveCall.getHandStrength() + dbSaveCall.getStrongDraw() +
                        convertEffectiveStackToCompact(dbSaveCall.getEffectiveStack());

                if(actionWasSuccessfull()) {
                    st.executeUpdate("UPDATE " + callTable + " SET success = success + 1 WHERE route = '" + route + "'");
                    st.executeUpdate("UPDATE " + callTableCompact + " SET success = success + 1 WHERE route = '" + routeCompact + "'");
                }

                st.executeUpdate("UPDATE " + callTable + " SET total = total + 1 WHERE route = '" + route + "'");
                st.executeUpdate("UPDATE " + callTableCompact + " SET total = total + 1 WHERE route = '" + routeCompact + "'");
            } else if(dbSave instanceof DbSaveValue) {
                DbSaveValue dbSaveValue = (DbSaveValue) dbSave;

                String route = dbSaveValue.getStreet() + dbSaveValue.getValueAction() + dbSaveValue.getPosition() +
                        dbSaveValue.getSizingGroup() + dbSaveValue.getOppLoosenessGroup() + dbSaveValue.getHandStrength() +
                        dbSaveValue.getStrongDraw() + dbSaveValue.getEffectiveStack() + dbSaveValue.getDrawWetness() +
                        dbSaveValue.getBoatWetness();

                String routeCompact = dbSaveValue.getStreet() + dbSaveValue.getValueAction() + dbSaveValue.getPosition() +
                        convertBluffOrValueSizingToCompact(dbSaveValue.getSizingGroup()) + dbSaveValue.getOppLoosenessGroup() +
                        dbSaveValue.getHandStrength() + convertEffectiveStackToCompact(dbSaveValue.getEffectiveStack());

                if(actionWasSuccessfull()) {
                    st.executeUpdate("UPDATE " + valueTable + " SET success = success + 1 WHERE route = '" + route + "'");
                    st.executeUpdate("UPDATE " + valueTableCompact + " SET success = success + 1 WHERE route = '" + routeCompact + "'");
                }

                st.executeUpdate("UPDATE " + valueTable + " SET total = total + 1 WHERE route = '" + route + "'");
                st.executeUpdate("UPDATE " + valueTableCompact + " SET total = total + 1 WHERE route = '" + routeCompact + "'");
            }
        }

        st.close();
        closeDbConnection();
    }

    public String convertBluffOrValueSizingToCompact(String sizing) {
        String compactSizing;

        if(sizing.equals("Sizing_0-5bb") || sizing.equals("Sizing_5-10bb")) {
            compactSizing = "Sizing_0-10bb";
        } else if(sizing.equals("Sizing_10-15bb") || sizing.equals("Sizing_15-20bb")) {
            compactSizing = "Sizing_10-20bb";
        } else {
            compactSizing = "Sizing_20bb_up";
        }

        return compactSizing;
    }

    public String convertEffectiveStackToCompact(String effectiveStack) {
        String compactEffectiveStack;

        if(effectiveStack.contains("EffStack_0_35_")) {
            compactEffectiveStack = "EffStack_0_35_";
        } else {
            compactEffectiveStack = "EffStack_35_up_";
        }

        return compactEffectiveStack;
    }

    public String convertCallAtcToCompact(String sizing) {
        String compactSizing;

        if(sizing.equals("Atc_0-5bb") || sizing.equals("Atc_5-10bb")) {
            compactSizing = "Atc_0-10bb";
        } else if(sizing.equals("Atc_10-15bb") || sizing.equals("Atc_15-20bb")) {
            compactSizing = "Atc_10-20bb";
        } else {
            compactSizing = "Atc_20bb_up";
        }

        return compactSizing;
    }

    public boolean actionWasSuccessfull() throws Exception {
        //default false on Party
        return false;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
