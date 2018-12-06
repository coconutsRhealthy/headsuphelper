package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.action.actionbuilders.ai.HandHistoryReaderStars;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbSavePersister {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new DbSavePersister().testMethod();
    }

//    total: 876.0
//    success: 555.0

//    total: 1471.0
//    success: 827.0

    private void testMethod() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_bluff_play_compact;");

        double totalTotal = 0;
        double successTotal = 0;

        int counter = 0;

        while(rs.next()) {
            String route = rs.getString("route");

            if(!route.contains("Sizing_0-10bb")) {
                double total = rs.getDouble("total");

                if(total >= 4) {
                    double success = rs.getDouble("success");

                    totalTotal = totalTotal + total;
                    successTotal = successTotal + success;

                    if(success / total > 0.5) {
                        System.out.println(route);
                    }

//                    double ratio = success / total;
//
//                    if(ratio < 0.55) {
//                        System.out.println(route);
//                    }
                }

            }
        }

        //System.out.println(counter);
        System.out.println("total: " + totalTotal);
        System.out.println("success: " + successTotal);

        rs.close();
        st.close();

        closeDbConnection();
    }

    private void initializeBluffDb() throws Exception {
        List<String> allRoutes = getAllBluffRoutesCopmact();

        initializeDbConnection();

        for(String route : allRoutes) {
            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO dbstats_bluff_play_compact (route) VALUES ('" + route + "')");

            st.close();
        }

        closeDbConnection();
    }

    private void initializeCallDb() throws Exception {
        List<String> allRoutes = getAllCallRoutes();

        initializeDbConnection();

        for(String route : allRoutes) {
            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO dbstats_call (route) VALUES ('" + route + "')");

            st.close();
        }

        closeDbConnection();
    }

    private void initializeValueDb() throws Exception {
        List<String> allRoutes = getAllValueRoutes();

        initializeDbConnection();

        for(String route : allRoutes) {
            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO dbstats_value (route) VALUES ('" + route + "')");

            st.close();
        }

        closeDbConnection();
    }

    private void migrateExtensiveBluffDbToCompact() throws Exception {
        String streetToUse = "";
        String bluffActionToUse = "";
        String positionToUse = "";
        String sizingGroupToUse = "";
        String foldStatGroupToUse = "";
        String strongDrawToUse = "";

        List<String> street = new ArrayList<>();
        List<String> bluffAction = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> foldStatGroup = new ArrayList<>();
        List<String> strongDraw = new ArrayList<>();

        street.add("Flop");
        street.add("Turn");
        street.add("River");

        bluffAction.add("Bet");
        bluffAction.add("Raise");

        position.add("Ip");
        position.add("Oop");

        foldStatGroup.add("Foldstat_0_33_");
        foldStatGroup.add("Foldstat_33_66_");
        foldStatGroup.add("Foldstat_66_100_");
        foldStatGroup.add("Foldstat_unknown");

        strongDraw.add("StrongDrawTrue");
        strongDraw.add("StrongDrawFalse");

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_bluff_play;");

        int counter = 0;

        while(rs.next()) {
            String route = rs.getString("route");
            double total = rs.getDouble("total");
            double success = rs.getDouble("success");

            for(String s : street) {
                if(route.contains(s)) {
                    streetToUse = s;
                    break;
                }
            }

            for(String s : bluffAction) {
                if(route.contains(s)) {
                    bluffActionToUse = s;
                    break;
                }
            }

            for(String s : position) {
                if(route.contains(s)) {
                    positionToUse = s;
                    break;
                }
            }

            if(route.contains("Sizing_0-5bb") || route.contains("Sizing_5-10bb")) {
                sizingGroupToUse = "Sizing_0-10bb";
            } else if(route.contains("Sizing_10-15bb") || route.contains("Sizing_15-20bb")) {
                sizingGroupToUse = "Sizing_10-20bb";
            } else {
                sizingGroupToUse = "Sizing_20bb_up";
            }

            for(String s : foldStatGroup) {
                if(route.contains(s)) {
                    foldStatGroupToUse = s;
                    break;
                }
            }

            for(String s : strongDraw) {
                if(route.contains(s)) {
                    strongDrawToUse = s;
                    break;
                }
            }

            String compactRoute = streetToUse + bluffActionToUse + positionToUse + sizingGroupToUse + foldStatGroupToUse +
                    strongDrawToUse;

            Statement st2 = con.createStatement();

            st2.executeUpdate("UPDATE dbstats_bluff_play_compact SET total = total + " + total + " WHERE route = '" + compactRoute + "'");
            st2.executeUpdate("UPDATE dbstats_bluff_play_compact SET success = success + " + success + " WHERE route = '" + compactRoute + "'");

            st2.close();

            counter++;

            if(counter == 100) {
                System.out.println();
                counter = 0;
            } else {
                System.out.print(".");
            }
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private List<String> getAllBluffRoutesCopmact() {
        List<String> street = new ArrayList<>();
        List<String> bluffAction = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> sizingGroup = new ArrayList<>();
        List<String> foldStatGroup = new ArrayList<>();
        List<String> strongDraw = new ArrayList<>();

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

        foldStatGroup.add("Foldstat_0_33_");
        foldStatGroup.add("Foldstat_33_66_");
        foldStatGroup.add("Foldstat_66_100_");
        foldStatGroup.add("Foldstat_unknown");

        strongDraw.add("StrongDrawTrue");
        strongDraw.add("StrongDrawFalse");

        List<String> allRoutes = new ArrayList<>();

        for(String a : street) {
            for(String b : bluffAction) {
                for(String c : position) {
                    for(String d : sizingGroup) {
                        for(String e : foldStatGroup) {
                            for(String f : strongDraw) {
                                allRoutes.add(a + b + c + d + e + f);
                            }
                        }
                    }
                }
            }
        }

        System.out.println(allRoutes.size());

        return allRoutes;
    }

    private List<String> getAllBluffRoutes() {
        List<String> street = new ArrayList<>();
        List<String> bluffAction = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> sizingGroup = new ArrayList<>();
        List<String> foldStatGroup = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();
        List<String> handStrength = new ArrayList<>();
        List<String> drawWetness = new ArrayList<>();
        List<String> boatWetness = new ArrayList<>();
        List<String> strongDraw = new ArrayList<>();

        street.add("Flop");
        street.add("Turn");
        street.add("River");

        bluffAction.add("Bet");
        bluffAction.add("Raise");

        position.add("Ip");
        position.add("Oop");

        sizingGroup.add("Sizing_0-5bb");
        sizingGroup.add("Sizing_5-10bb");
        sizingGroup.add("Sizing_10-15bb");
        sizingGroup.add("Sizing_15-20bb");
        sizingGroup.add("Sizing_20-30bb");
        sizingGroup.add("Sizing_30bb_up");

        foldStatGroup.add("Foldstat_0_33_");
        foldStatGroup.add("Foldstat_33_66_");
        foldStatGroup.add("Foldstat_66_100_");
        foldStatGroup.add("Foldstat_unknown");

        effectiveStack.add("EffStack_0_35_");
        effectiveStack.add("EffStack_35_70_");
        effectiveStack.add("EffStack_70_120_");
        effectiveStack.add("EffStack_120_up_");

        handStrength.add("HS_0_23_");
        handStrength.add("HS_23_46_");
        handStrength.add("HS_46_70_");

        drawWetness.add("DrawWetnessDry");
        drawWetness.add("DrawWetnessMedium");
        drawWetness.add("DrawWetnessWet");

        boatWetness.add("BoatWetnessDry");
        boatWetness.add("BoatWetnessMedium");
        boatWetness.add("BoatWetnessWet");

        strongDraw.add("StrongDrawTrue");
        strongDraw.add("StrongDrawFalse");

        List<String> allRoutes = new ArrayList<>();

        for(String a : street) {
            for(String b : bluffAction) {
                for(String c : position) {
                    for(String d : sizingGroup) {
                        for(String e : foldStatGroup) {
                            for(String f : effectiveStack) {
                                for(String g : handStrength) {
                                    for(String h : drawWetness) {
                                        for(String i : boatWetness) {
                                            for(String j : strongDraw) {
                                                allRoutes.add(a + b + c + d + e + f + g + h + i + j);
                                            }
                                        }
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

    private List<String> getAllCallRoutes() {
        List<String> street = new ArrayList<>();
        List<String> facingAction = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> amountToCallGroup = new ArrayList<>();
        List<String> oppAggroGroup = new ArrayList<>();
        List<String> handStrength = new ArrayList<>();
        List<String> strongDraw = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();
        List<String> drawWetness = new ArrayList<>();
        List<String> boatWetness = new ArrayList<>();

        street.add("Flop");
        street.add("Turn");
        street.add("River");

        facingAction.add("FacingBet");
        facingAction.add("FacingRaise");

        position.add("Ip");
        position.add("Oop");

        amountToCallGroup.add("Atc_0-5bb");
        amountToCallGroup.add("Atc_5-10bb");
        amountToCallGroup.add("Atc_10-15bb");
        amountToCallGroup.add("Atc_15-20bb");
        amountToCallGroup.add("Atc_20-30bb");
        amountToCallGroup.add("Atc_30bb_up");

        oppAggroGroup.add("Aggro_0_33_");
        oppAggroGroup.add("Aggro_33_66_");
        oppAggroGroup.add("Aggro_66_100_");
        oppAggroGroup.add("Aggro_unknown");

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
        effectiveStack.add("EffStack_35_70_");
        effectiveStack.add("EffStack_70_120_");
        effectiveStack.add("EffStack_120_up_");

        drawWetness.add("DrawWetnessDry");
        drawWetness.add("DrawWetnessMedium");
        drawWetness.add("DrawWetnessWet");

        boatWetness.add("BoatWetnessDry");
        boatWetness.add("BoatWetnessMedium");
        boatWetness.add("BoatWetnessWet");

        List<String> allRoutes = new ArrayList<>();

        for(String a : street) {
            for(String b : facingAction) {
                for(String c : position) {
                    for(String d : amountToCallGroup) {
                        for(String e : oppAggroGroup) {
                            for(String f : handStrength) {
                                for(String g : strongDraw) {
                                    for(String h : effectiveStack) {
                                        for(String i : drawWetness) {
                                            for(String j : boatWetness) {
                                                allRoutes.add(a + b + c + d + e + f + g + h + i + j);
                                            }
                                        }
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

    private List<String> getAllValueRoutes() {
        List<String> street = new ArrayList<>();
        List<String> valueAction = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> sizing = new ArrayList<>();
        List<String> oppLooseness = new ArrayList<>();
        List<String> handStrength = new ArrayList<>();
        List<String> strongDraw = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();
        List<String> drawWetness = new ArrayList<>();
        List<String> boatWetness = new ArrayList<>();

        street.add("Flop");
        street.add("Turn");
        street.add("River");

        valueAction.add("Bet");
        valueAction.add("Raise");

        position.add("Ip");
        position.add("Oop");

        sizing.add("Sizing_0-5bb");
        sizing.add("Sizing_5-10bb");
        sizing.add("Sizing_10-15bb");
        sizing.add("Sizing_15-20bb");
        sizing.add("Sizing_20-30bb");
        sizing.add("Sizing_30bb_up");

        oppLooseness.add("Looseness_0_33_");
        oppLooseness.add("Looseness_33_66_");
        oppLooseness.add("Looseness_66_100_");
        oppLooseness.add("Looseness_unknown");

        handStrength.add("HS_-1_");
        handStrength.add("HS_70_75_");
        handStrength.add("HS_75_80_");
        handStrength.add("HS_80_85_");
        handStrength.add("HS_85_90_");
        handStrength.add("HS_90_95_");
        handStrength.add("HS_95_100_");

        strongDraw.add("StrongDrawTrue");
        strongDraw.add("StrongDrawFalse");

        effectiveStack.add("EffStack_0_35_");
        effectiveStack.add("EffStack_35_70_");
        effectiveStack.add("EffStack_70_120_");
        effectiveStack.add("EffStack_120_up_");

        drawWetness.add("DrawWetnessDry");
        drawWetness.add("DrawWetnessMedium");
        drawWetness.add("DrawWetnessWet");

        boatWetness.add("BoatWetnessDry");
        boatWetness.add("BoatWetnessMedium");
        boatWetness.add("BoatWetnessWet");

        List<String> allRoutes = new ArrayList<>();

        for(String a : street) {
            for(String b : valueAction) {
                for(String c : position) {
                    for(String d : sizing) {
                        for(String e : oppLooseness) {
                            for(String f : handStrength) {
                                for(String g : strongDraw) {
                                    for(String h : effectiveStack) {
                                        for(String i : drawWetness) {
                                            for(String j : boatWetness) {
                                                allRoutes.add(a + b + c + d + e + f + g + h + i + j);
                                            }
                                        }
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

    public void doDbSaveUpdate(ContinuousTable continuousTable, double biglind) throws Exception {
        List<DbSave> dbSaveList = continuousTable.getDbSaveList();

        initializeDbConnection();

        Statement st = con.createStatement();

        String bluffTable;
        String callTable;
        String valueTable;

        if(continuousTable.getGame().equals("playMoney")) {
            bluffTable = "dbstats_bluff_play";
            callTable = "dbstats_call_play";
            valueTable = "dbstats_value_play";
        } else if (continuousTable.getGame().equals("sng")) {
            bluffTable = "dbstats_bluff_sng";
            callTable = "dbstats_call_sng";
            valueTable = "dbstats_value_sng";
        } else {
            bluffTable = "dbstats_bluff_50nl";
            callTable = "dbstats_call_50nl";
            valueTable = "dbstats_value_50nl";
        }

        for(DbSave dbSave : dbSaveList) {
            if(dbSave instanceof DbSaveBluff) {
                DbSaveBluff dbSaveBluff = (DbSaveBluff) dbSave;

                String route = dbSaveBluff.getStreet() + dbSaveBluff.getBluffAction() + dbSaveBluff.getPosition() +
                        dbSaveBluff.getSizingGroup() + dbSaveBluff.getFoldStatGroup() + dbSaveBluff.getEffectiveStack()
                        + dbSaveBluff.getHandStrength() + dbSaveBluff.getDrawWetness() + dbSaveBluff.getBoatWetness()
                        + dbSaveBluff.getStrongDraw();

                if(actionWasSuccessfull(biglind)) {
                    st.executeUpdate("UPDATE " + bluffTable + " SET success = success + 1 WHERE route = '" + route + "'");
                }

                st.executeUpdate("UPDATE " + bluffTable + " SET total = total + 1 WHERE route = '" + route + "'");
            } else if(dbSave instanceof DbSaveCall) {
                DbSaveCall dbSaveCall = (DbSaveCall) dbSave;

                String route = dbSaveCall.getStreet() + dbSaveCall.getFacingAction() + dbSaveCall.getPosition() +
                        dbSaveCall.getAmountToCallGroup() + dbSaveCall.getOppAggroGroup() + dbSaveCall.getHandStrength() +
                        dbSaveCall.getStrongDraw() + dbSaveCall.getEffectiveStack() + dbSaveCall.getDrawWetness() +
                        dbSaveCall.getBoatWetness();

                if(actionWasSuccessfull(biglind)) {
                    st.executeUpdate("UPDATE " + callTable + " SET success = success + 1 WHERE route = '" + route + "'");
                }

                st.executeUpdate("UPDATE " + callTable + " SET total = total + 1 WHERE route = '" + route + "'");
            } else if(dbSave instanceof DbSaveValue) {
                DbSaveValue dbSaveValue = (DbSaveValue) dbSave;

                String route = dbSaveValue.getStreet() + dbSaveValue.getValueAction() + dbSaveValue.getPosition() +
                        dbSaveValue.getSizingGroup() + dbSaveValue.getOppLoosenessGroup() + dbSaveValue.getHandStrength() +
                        dbSaveValue.getStrongDraw() + dbSaveValue.getEffectiveStack() + dbSaveValue.getDrawWetness() +
                        dbSaveValue.getBoatWetness();

                if(actionWasSuccessfull(biglind)) {
                    st.executeUpdate("UPDATE " + valueTable + " SET success = success + 1 WHERE route = '" + route + "'");
                }

                st.executeUpdate("UPDATE " + valueTable + " SET total = total + 1 WHERE route = '" + route + "'");
            }
        }

        st.close();
        closeDbConnection();
    }

    private boolean actionWasSuccessfull(double bigBlind) throws Exception {
        boolean botWonHand = false;

        HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();
        List<String> total = handHistoryReaderStars.readTextFile();
        List<String> lastHand = handHistoryReaderStars.getLinesOfLastGame(total, 1, bigBlind);
        Collections.reverse(lastHand);

        for(String line : lastHand) {
            if(line.contains("vegeta11223 collected")) {
                botWonHand = true;
                break;
            }
        }

        return botWonHand;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
