package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.PreflopHandStength;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbSavePersisterPreflop {

    private Connection con;

    public void doDbSaveUpdate(ContinuousTable continuousTable, double bigBlind) throws Exception {
        List<DbSave> dbSaveList = continuousTable.getDbSaveList();

        initializeDbConnection();

        Statement st = con.createStatement();

        String raiseTable;
        String raiseTableCompact;
        String callTable;
        String callTableCompact;

        if(continuousTable.getGame().equals("playMoney")) {
            raiseTable = "toImplement";
            raiseTableCompact = "toImplement";
            callTable = "toImplement";
            callTableCompact = "toImplement";
        } else if (continuousTable.getGame().equals("sng")) {
            raiseTable = "dbstats_pf_raise_sng";
            raiseTableCompact = "dbstats_pf_raise_sng_compact";
            callTable = "dbstats_pf_call_sng";
            callTableCompact = "dbstats_pf_call_sng_compact";
        } else {
            raiseTable = "toImplement";
            raiseTableCompact = "toImplement";
            callTable = "toImplement";
            callTableCompact = "toImplement";
        }

        for(DbSave dbSave : dbSaveList) {
            if(dbSave instanceof DbSavePreflopRaise) {
                DbSavePreflopRaise dbSavePreflopRaise = (DbSavePreflopRaise) dbSave;

                String route = dbSavePreflopRaise.getCombo() + dbSavePreflopRaise.getPosition() +
                        dbSavePreflopRaise.getSizing() + dbSavePreflopRaise.getFoldStatGroup() +
                        dbSavePreflopRaise.getEffectiveStack();

                String routeCompact = convertComboStringToCompactHandStrengthString(dbSavePreflopRaise.getCombo()) +
                        dbSavePreflopRaise.getPosition() + dbSavePreflopRaise.getSizing() +
                        dbSavePreflopRaise.getFoldStatGroup() + dbSavePreflopRaise.getEffectiveStack();

                verifyRouteExists(route, raiseTable);
                verifyRouteExists(routeCompact, raiseTableCompact);

                if(new DbSavePersister().actionWasSuccessfull(bigBlind)) {
                    st.executeUpdate("UPDATE " + raiseTable + " SET success = success + 1 WHERE route = '" + route + "'");
                    st.executeUpdate("UPDATE " + raiseTableCompact + " SET success = success + 1 WHERE route = '" + routeCompact + "'");
                }

                st.executeUpdate("UPDATE " + raiseTable + " SET total = total + 1 WHERE route = '" + route + "'");
                st.executeUpdate("UPDATE " + raiseTableCompact + " SET total = total + 1 WHERE route = '" + routeCompact + "'");
            } else if(dbSave instanceof DbSavePreflopCall) {
                DbSavePreflopCall dbSavePreflopCall = (DbSavePreflopCall) dbSave;

                String route = dbSavePreflopCall.getCombo() + dbSavePreflopCall.getPosition() +
                        dbSavePreflopCall.getAmountToCallBb() + dbSavePreflopCall.getOppAggroGroup() +
                        dbSavePreflopCall.getEffectiveStack();

                String routeCompact = convertComboStringToCompactHandStrengthString(dbSavePreflopCall.getCombo()) +
                        dbSavePreflopCall.getPosition() + dbSavePreflopCall.getAmountToCallBb() +
                        dbSavePreflopCall.getOppAggroGroup() + dbSavePreflopCall.getEffectiveStack();

                verifyRouteExists(route, callTable);
                verifyRouteExists(routeCompact, callTableCompact);

                if(new DbSavePersister().actionWasSuccessfull(bigBlind)) {
                    st.executeUpdate("UPDATE " + callTable + " SET success = success + 1 WHERE route = '" + route + "'");
                    st.executeUpdate("UPDATE " + callTableCompact + " SET success = success + 1 WHERE route = '" + routeCompact + "'");
                }

                st.executeUpdate("UPDATE " + callTable + " SET total = total + 1 WHERE route = '" + route + "'");
                st.executeUpdate("UPDATE " + callTableCompact + " SET total = total + 1 WHERE route = '" + routeCompact + "'");
            }
        }

        st.close();
        closeDbConnection();
    }

    private void verifyRouteExists(String route, String table) throws Exception {
        Statement st2 = con.createStatement();

        ResultSet rs2 = st2.executeQuery("SELECT * FROM " + table + " WHERE route = '" + route + "';");

        if(!rs2.next()) {
           System.out.println("Preflop Route does not exist! " + route + "    table: " + table);
        } else {
            System.out.println("Preflop Route exists: " + route + "     table: " + table);
        }

        rs2.close();
        st2.close();
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

    private void migrateExtensivePfDbToCompact(String table) throws Exception {
        String extensiveTable = table.replace("_compact", "");

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + extensiveTable + ";");

        int counter = 0;

        while(rs.next()) {
            String route = rs.getString("route");
            double total = rs.getDouble("total");
            double success = rs.getDouble("success");

            String handAsString;

            if(route.contains("Ip")) {
                handAsString = route.substring(0, route.indexOf("Ip"));
            } else {
                handAsString = route.substring(0, route.indexOf("Oop"));
            }

            List<Card> holeCards = convertStringToCardCombo(handAsString);
            String handStrengthForCompactRoute = convertListCardToHandStrengthString(holeCards);

            String usabelPartOfExtensiveRoute;

            if(route.contains("Ip")) {
                usabelPartOfExtensiveRoute = route.substring(route.indexOf("Ip"));
            } else {
                usabelPartOfExtensiveRoute = route.substring(route.indexOf("Oop"));
            }

            String compactRoute = handStrengthForCompactRoute + usabelPartOfExtensiveRoute;

            Statement st2 = con.createStatement();

            st2.executeUpdate("UPDATE " + table + " SET total = total + " + total + " WHERE route = '" + compactRoute + "'");
            st2.executeUpdate("UPDATE " + table + " SET success = success + " + success + " WHERE route = '" + compactRoute + "'");

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

    private List<String> getAllPfRaiseRoutesCompact() {
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

    private List<String> getAllPfRaiseRoutes() {
        List<String> holeCards;
        List<String> position = new ArrayList<>();
        List<String> sizing = new ArrayList<>();
        List<String> foldStatGroup = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();

        holeCards = getAllHoleCardCombos();

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

        for(String a : holeCards) {
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

    private List<String> getAllPfCallRoutesCompact() {
        List<String> handStrength = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> amountToCall = new ArrayList<>();
        List<String> oppAggroGroup = new ArrayList<>();
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

        oppAggroGroup.add("Aggro_0_33_");
        oppAggroGroup.add("Aggro_33_66_");
        oppAggroGroup.add("Aggro_66_100_");
        oppAggroGroup.add("Aggro_unknown");

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
                    for(String d : oppAggroGroup) {
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
        List<String> holeCards;
        List<String> position = new ArrayList<>();
        List<String> amountToCall = new ArrayList<>();
        List<String> oppAggroGroup = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();

        holeCards = getAllHoleCardCombos();

        position.add("Ip");
        position.add("Oop");

        amountToCall.add("Atc_0-5bb");
        amountToCall.add("Atc_5-13bb");
        amountToCall.add("Atc_13-26bb");
        amountToCall.add("Atc_26bb_up");

        oppAggroGroup.add("Aggro_0_33_");
        oppAggroGroup.add("Aggro_33_66_");
        oppAggroGroup.add("Aggro_66_100_");
        oppAggroGroup.add("Aggro_unknown");

        effectiveStack.add("Effstack_0-10bb");
        effectiveStack.add("Effstack_10-30bb");
        effectiveStack.add("Effstack_30-50bb");
        effectiveStack.add("Effstack_50-75bb");
        effectiveStack.add("Effstack_75-110bb");
        effectiveStack.add("Effstack_110bb_up");

        List<String> allRoutes = new ArrayList<>();

        for(String a : holeCards) {
            for(String b : position) {
                for(String c : amountToCall) {
                    for(String d : oppAggroGroup) {
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

    private List<String> getAllHoleCardCombos() {
        List<String> allHoleCardCombos = new ArrayList<>();

        List<String> values = new ArrayList<>();

        values.add("A");
        values.add("K");
        values.add("Q");
        values.add("J");
        values.add("T");
        values.add("9");
        values.add("8");
        values.add("7");
        values.add("6");
        values.add("5");
        values.add("4");
        values.add("3");
        values.add("2");

        for(int i = 0; i < values.size(); i++) {
            List<String> subList = values.subList(i, values.size());

            for(String s : subList) {
                String value = values.get(i);

                if(!value.equals(s)) {
                    allHoleCardCombos.add(value + s + "o");
                    allHoleCardCombos.add(value + s + "s");
                } else {
                    allHoleCardCombos.add(value + s);
                }
            }
        }

        return allHoleCardCombos;
    }

    private List<Card> convertStringToCardCombo(String handAsString) {
        List<Card> comboToReturn = new ArrayList<>();

        String firstCard = handAsString.substring(0, 1);
        String seconCard = handAsString.substring(1, 2);

        boolean suited;

        if(handAsString.length() == 2) {
            suited = false;
        } else {
            suited = handAsString.substring(2, 3).equals("s");
        }

        List<String> cardsAsString = new ArrayList<>();
        cardsAsString.add(firstCard);
        cardsAsString.add(seconCard);

        List<Integer> ranks = new ArrayList<>();

        for(String card : cardsAsString) {
            if(card.equals("A")) {
                ranks.add(14);
            } else if(card.equals("K")) {
                ranks.add(13);
            } else if(card.equals("Q")) {
                ranks.add(12);
            } else if(card.equals("J")) {
                ranks.add(11);
            } else if(card.equals("T")) {
                ranks.add(10);
            } else if(card.equals("9")) {
                ranks.add(9);
            } else if(card.equals("8")) {
                ranks.add(8);
            } else if(card.equals("7")) {
                ranks.add(7);
            } else if(card.equals("6")) {
                ranks.add(6);
            } else if(card.equals("5")) {
                ranks.add(5);
            } else if(card.equals("4")) {
                ranks.add(4);
            } else if(card.equals("3")) {
                ranks.add(3);
            } else if(card.equals("2")) {
                ranks.add(2);
            }
        }

        boolean first = true;

        for(Integer i : ranks) {
            if(first) {
                comboToReturn.add(new Card(i, 's'));
            } else {
                if(suited) {
                    comboToReturn.add(new Card(i, 's'));
                } else {
                    comboToReturn.add(new Card(i, 'h'));
                }
            }

            first = false;
        }

        return comboToReturn;
    }

    public String convertComboStringToCompactHandStrengthString(String combo) {
        List<Card> comboAsCardList = convertStringToCardCombo(combo);
        return convertListCardToHandStrengthString(comboAsCardList);
    }

    public String convertListCardToHandStrengthString(List<Card> combo) {
        double handStrength = new PreflopHandStength().getPreflopHandStength(combo);
        return convertNumericHandstrengthToString(handStrength);
    }

    public String convertNumericHandstrengthToString(double handStrength) {
        String handStrengthString;

        if(handStrength <= 0.2) {
            handStrengthString = "HS_0_20_";
        } else if(handStrength <= 0.35) {
            handStrengthString = "HS_20_35_";
        } else if(handStrength <= 0.50) {
            handStrengthString = "HS_35_50_";
        } else if(handStrength <= 0.60) {
            handStrengthString = "HS_50_60_";
        } else if(handStrength <= 0.70) {
            handStrengthString = "HS_60_70_";
        } else if(handStrength <= 0.75) {
            handStrengthString = "HS_70_75_";
        } else if(handStrength <= 0.80) {
            handStrengthString = "HS_75_80_";
        } else if(handStrength <= 0.85) {
            handStrengthString = "HS_80_85_";
        } else if(handStrength <= 0.90) {
            handStrengthString = "HS_85_90_";
        } else if(handStrength <= 0.95) {
            handStrengthString = "HS_90_95_";
        } else {
            handStrengthString = "HS_95_100_";
        }

        return handStrengthString;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
