package com.lennart.model.action.actionbuilders.ai.opponenttypes.opponentidentifier_2_0;

import com.lennart.model.action.actionbuilders.ai.HandHistoryReaderStars;
import com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0.DbSaveBluff_2_0;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 12/01/2019.
 */
public class OpponentIdentifier2_0 {

    private Connection con;

    private double numberOfHands;
    private double oppPre3bet;
    private double oppPreLooseness;
    private double oppPostRaise;
    private double oppPostBet;
    private double oppPostLooseness;

    public static double PRE_3_BET = 0.08;
    public static double PRE_LOOSENESS = 0.7105263157894737;
    public static double POST_RAISE = 0.13636363636363635;
    public static double POST_BET = 0.35714285714285715;
    public static double POST_LOOSENESS = 0.46153846153846156;

    public OpponentIdentifier2_0() {
        //default constructor
    }

    public OpponentIdentifier2_0(String opponentName) throws Exception {
        Map<String, Double> opponentData = getAllDataOfOpponent(opponentName);

        numberOfHands = opponentData.get("preNumberOfHands");
        oppPre3bet = getOpponentPre3bet(opponentData);
        oppPreLooseness = getOpponentPreLooseness(opponentData);
        oppPostRaise = getOpponentPostRaise(opponentData);
        oppPostBet = getOpponentPostBet(opponentData);
        oppPostLooseness = getOpponentPostLooseness(opponentData);
    }

    private void testMethod() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop;");

        while(rs.next()) {
            String opponentName = rs.getString("playerName");

            DbSaveBluff_2_0 dbSaveBluff_2_0 = new DbSaveBluff_2_0();

            String preflopType = dbSaveBluff_2_0.getOpponentPreflopTypeLogic(opponentName, true);
            String postflopType = dbSaveBluff_2_0.getOpponentPostflopTypeLogic(opponentName, true);

            System.out.println(preflopType + postflopType);
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private void printLoosenessAndTightnessBoundries(boolean includingMedium, String table) throws Exception {
        List<Double> allLooseness = new ArrayList<>();
        List<Double> allAggro = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table + ";");

        while(rs.next()) {
            if(rs.getDouble("numberOfHands") >= 20) {
                double callTotal = rs.getDouble("callCount");
                double foldTotal = rs.getDouble("foldCount");
                double betTotal;

                if(table.contains("postflop")) {
                    betTotal = rs.getDouble("betCount");
                } else {
                    betTotal = 0;
                }

                double raiseTotal = rs.getDouble("raiseCount");
                double checkTotal = rs.getDouble("checkCount");

                double looseness = callTotal / (callTotal + foldTotal);
                double aggressiveness;

                if(table.contains("postflop")) {
                    aggressiveness = (betTotal + raiseTotal) / (betTotal + raiseTotal + checkTotal + callTotal);
                } else {
                    aggressiveness = (raiseTotal) / (raiseTotal + checkTotal + callTotal);
                }

                allLooseness.add(looseness);
                allAggro.add(aggressiveness);
            }
        }

        Collections.sort(allLooseness);
        Collections.sort(allAggro);

        if(!includingMedium) {
            int halfLooseness = allLooseness.size() / 2;
            int halfAggroness = allLooseness.size() / 2;

            System.out.println("Looseness tight below: " + allLooseness.get(halfLooseness));
            System.out.println("Aggro passive below: " + allAggro.get(halfAggroness));
        } else {
            int oneThirdLooseness = allLooseness.size() / 3;
            int twoThirdLooseness = (allLooseness.size() / 3) * 2;

            int oneThirdAggroness = allAggro.size() / 3;
            int twoThirdAggroness = (allAggro.size() / 3) * 2;

            System.out.println("Looseness tight below: " + allLooseness.get(oneThirdLooseness));
            System.out.println("Looseness medium below: " + allLooseness.get(twoThirdLooseness));

            System.out.println("Aggro passive below: " + allAggro.get(oneThirdAggroness));
            System.out.println("Aggro medium below: " + allAggro.get(twoThirdAggroness));
        }
    }

    public static void main(String[] args) throws Exception {
        new OpponentIdentifier2_0().printStatsBoundries();

        OpponentIdentifier2_0 opponentIdentifier2_0 = new OpponentIdentifier2_0("dukeRH888");

        System.out.println();

        System.out.println("3bet: " + opponentIdentifier2_0.getOppPre3bet());
        System.out.println("preLooseness: " + opponentIdentifier2_0.getOppPreLooseness());
        System.out.println("oppPostRaise: " + opponentIdentifier2_0.getOppPostRaise());
        System.out.println("oppPostBet: " + opponentIdentifier2_0.getOppPostBet());
        System.out.println("oppPostLooseness: " + opponentIdentifier2_0.getOppPostLooseness());
    }

    private void printStatsBoundries() throws Exception {
        List<Double> allOppPre3betStats = new ArrayList<>();
        List<Double> allOppPreLoosenessStats = new ArrayList<>();
        List<Double> allOppPostRaiseStats = new ArrayList<>();
        List<Double> allOppPostBetStats = new ArrayList<>();
        List<Double> allOppPostLoosenessStats = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop;");

        while(rs.next()) {
            String opponentName = rs.getString("playerName");

            Map<String, Double> opponentData = getAllDataOfOpponent(opponentName);

            if(!opponentData.isEmpty()) {
                double numberOfHands = opponentData.get("preNumberOfHands");

                if(numberOfHands >= 14) {
                    allOppPre3betStats.add(getOpponentPre3bet(opponentData));
                    allOppPreLoosenessStats.add(getOpponentPreLooseness(opponentData));
                    allOppPostRaiseStats.add(getOpponentPostRaise(opponentData));
                    allOppPostBetStats.add(getOpponentPostBet(opponentData));
                    allOppPostLoosenessStats.add(getOpponentPostLooseness(opponentData));
                }
            } else {
                System.out.println("opponentData is empty for: " + opponentName);
            }
        }

        Collections.sort(allOppPre3betStats);
        Collections.sort(allOppPreLoosenessStats);
        Collections.sort(allOppPostRaiseStats);
        Collections.sort(allOppPostBetStats);
        Collections.sort(allOppPostLoosenessStats);

        System.out.println("pre3bet: " + allOppPre3betStats.get(allOppPre3betStats.size() / 2));
        System.out.println("preLooseness: " + allOppPreLoosenessStats.get(allOppPreLoosenessStats.size() / 2));
        System.out.println("postRaise: " + allOppPostRaiseStats.get(allOppPostRaiseStats.size() / 2));
        System.out.println("postBet: " + allOppPostBetStats.get(allOppPostBetStats.size() / 2));
        System.out.println("postLooseness: " + allOppPostLoosenessStats.get(allOppPostLoosenessStats.size() / 2));
    }

    public List<Double> getOpponentLoosenessAndAggroness(String opponentName, boolean preflop) throws Exception {
        List<Double> oppLoosenessAndAggroness = new ArrayList<>();

        String table;

        if(preflop) {
            table = "opponentidentifier_2_0_preflop";
        } else {
            table = "opponentidentifier_2_0_postflop";
        }

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE playerName = '" + opponentName + "'");

        if(rs.next()) {
            if(rs.getDouble("numberOfHands") >= 20) {
                double callTotal = rs.getDouble("callCount");
                double foldTotal = rs.getDouble("foldCount");
                double raiseTotal = rs.getDouble("raiseCount");
                double checkTotal = rs.getDouble("checkCount");

                double looseness;
                double aggressiveness;

                if(preflop) {
                    looseness = callTotal / (callTotal + foldTotal);
                    aggressiveness = raiseTotal / (raiseTotal + checkTotal + callTotal);
                } else {
                    double betTotal = rs.getDouble("betCount");

                    looseness = callTotal / (callTotal + foldTotal);
                    aggressiveness = (raiseTotal + betTotal) / (raiseTotal + betTotal + checkTotal + callTotal);
                }

                oppLoosenessAndAggroness.add(looseness);
                oppLoosenessAndAggroness.add(aggressiveness);
            }
        }

        return oppLoosenessAndAggroness;
    }

    public double getOpponentPre3bet(Map<String, Double> opponentData) {
        //Oop raise tov total hands

        double oppPre3bet;
        double numberOfHands = opponentData.get("preNumberOfHands");

        if(numberOfHands >= 14) {
            double oopRaiseNumber = opponentData.get("preOopRaiseCount");
            double totalNumberOfHands = opponentData.get("preNumberOfHands");
            oppPre3bet = oopRaiseNumber / totalNumberOfHands;
        } else {
            oppPre3bet = -1;
        }

        return oppPre3bet;
    }

    public double getOpponentPreLooseness(Map<String, Double> opponentData) {
        //call count tov call + fold count

        double oppPreLooseness;
        double numberOfHands = opponentData.get("preNumberOfHands");

        if(numberOfHands >= 14) {
            double callCount = opponentData.get("preCallCount");
            double foldCount = opponentData.get("preFoldCount");
            oppPreLooseness = callCount / (callCount + foldCount);
        } else {
            oppPreLooseness = -1;
        }

        return oppPreLooseness;
    }

    public double getOpponentPostRaise(Map<String, Double> opponentData) {
        //Post all raises tov fold + call + raise

        double oppPostRaise;
        double numberOfHands = opponentData.get("preNumberOfHands");

        if(numberOfHands >= 14) {
            double postRaiseCount = opponentData.get("postRaiseCount");
            double postFoldCount = opponentData.get("postFoldCount");
            double postCallCount = opponentData.get("postCallCount");
            oppPostRaise = postRaiseCount / (postFoldCount + postCallCount + postRaiseCount);
        } else {
            oppPostRaise = -1;
        }

        return oppPostRaise;
    }

    public double getOpponentPostBet(Map<String, Double> opponentData) {
        //post bet tov bet + check

        double oppPostBet;
        double numberOfHands = opponentData.get("preNumberOfHands");

        if(numberOfHands >= 14) {
            double postBetCount = opponentData.get("postBetCount");
            double postCheckCount = opponentData.get("postCheckCount");
            oppPostBet = postBetCount / (postBetCount + postCheckCount);
        } else {
            oppPostBet = -1;
        }

        return oppPostBet;
    }

    public double getOpponentPostLooseness(Map<String, Double> opponentData) {
        //call count tov call + fold count

        double oppPostLooseness;
        double numberOfHands = opponentData.get("preNumberOfHands");

        if(numberOfHands >= 14) {
            double postCallCount = opponentData.get("postCallCount");
            double postFoldCount = opponentData.get("postFoldCount");
            oppPostLooseness = postCallCount / (postCallCount + postFoldCount);
        } else {
            oppPostLooseness = -1;
        }

        return oppPostLooseness;
    }

    private Map<String, Double> getAllDataOfOpponent(String opponentName) throws Exception {
        Map<String, Double> opponentData = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop WHERE playerName = '" + opponentName + "';");
        Statement st2 = con.createStatement();
        ResultSet rs2 = st2.executeQuery("SELECT * FROM opponentidentifier_2_0_postflop WHERE playerName = '" + opponentName + "';");

        if(rs.next() && rs2.next()) {
            opponentData.put("preNumberOfHands", rs.getDouble("numberOfHands"));
            opponentData.put("preFoldCount", rs.getDouble("foldCount"));
            opponentData.put("preCheckCount", rs.getDouble("checkCount"));
            opponentData.put("preCallCount", rs.getDouble("callCount"));
            opponentData.put("preIpRaiseCount", rs.getDouble("ipRaiseCount"));
            opponentData.put("preOopRaiseCount", rs.getDouble("oopRaiseCount"));

            opponentData.put("postNumberOfHands", rs2.getDouble("numberOfHands"));
            opponentData.put("postFoldCount", rs2.getDouble("foldCount"));
            opponentData.put("postCheckCount", rs2.getDouble("checkCount"));
            opponentData.put("postCallCount", rs2.getDouble("callCount"));
            opponentData.put("postBetCount", rs2.getDouble("betCount"));
            opponentData.put("postRaiseCount", rs2.getDouble("raiseCount"));
        }

        rs.close();
        st.close();
        rs2.close();
        st2.close();

        closeDbConnection();

        if(opponentData.isEmpty()) {
            opponentData.put("preNumberOfHands", 0.0);
            opponentData.put("preFoldCount", 0.0);
            opponentData.put("preCheckCount", 0.0);
            opponentData.put("preCallCount", 0.0);
            opponentData.put("preIpRaiseCount", 0.0);
            opponentData.put("preOopRaiseCount", 0.0);
            opponentData.put("postNumberOfHands", 0.0);
            opponentData.put("postFoldCount", 0.0);
            opponentData.put("postCheckCount", 0.0);
            opponentData.put("postCallCount", 0.0);
            opponentData.put("postBetCount", 0.0);
            opponentData.put("postRaiseCount", 0.0);

            System.out.println("Opponentname: " + opponentName + " not found in opponentidentifier db !");
        }

        return opponentData;
    }

    public void updateOpponentIdentifier2_0_db(String opponentPlayerNameOfLastHand, double bigBlind, boolean botWasButton) throws Exception {
        HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();

        List<String> opponentPreflopActions = handHistoryReaderStars.getOpponentActionsOfLastHand(false, bigBlind);

        for(String action : opponentPreflopActions) {
            updateCountsInDb(opponentPlayerNameOfLastHand, action, "opponentidentifier_2_0_preflop", botWasButton);
        }

        List<String> opponentPostflopActions = handHistoryReaderStars.getOpponentActionsOfLastHand(true, bigBlind);

        for(String action : opponentPostflopActions) {
            updateCountsInDb(opponentPlayerNameOfLastHand, action, "opponentidentifier_2_0_postflop", botWasButton);
        }

        updateNumberOfHands(opponentPlayerNameOfLastHand);
    }

    private void updateCountsInDb(String opponentNick, String action, String table, boolean botWasButton) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table + " WHERE playerName = '" + opponentNick + "';");

        if(!rs.next()) {
            st.executeUpdate("INSERT INTO " + table + " (playerName) VALUES ('" + opponentNick + "')");
        }

        if(action.equals("fold")) {
            st.executeUpdate("UPDATE " + table + " SET foldCount = foldCount + 1 WHERE playerName = '" + opponentNick + "'");
        } else if(action.equals("check")) {
            st.executeUpdate("UPDATE " + table + " SET checkCount = checkCount + 1 WHERE playerName = '" + opponentNick + "'");
        } else if(action.equals("call")) {
            st.executeUpdate("UPDATE " + table + " SET callCount = callCount + 1 WHERE playerName = '" + opponentNick + "'");
        } else if(action.equals("bet75pct")) {
            st.executeUpdate("UPDATE " + table + " SET betCount = betCount + 1 WHERE playerName = '" + opponentNick + "'");
        } else if(action.equals("raise")) {
            st.executeUpdate("UPDATE " + table + " SET raiseCount = raiseCount + 1 WHERE playerName = '" + opponentNick + "'");

            if(table.contains("preflop")) {
                if(botWasButton) {
                    st.executeUpdate("UPDATE " + table + " SET oopRaiseCount = oopRaiseCount + 1 WHERE playerName = '" + opponentNick + "'");
                } else {
                    st.executeUpdate("UPDATE " + table + " SET ipRaiseCount = ipRaiseCount + 1 WHERE playerName = '" + opponentNick + "'");
                }
            }
        }

        rs.close();
        st.close();
        closeDbConnection();
    }

    private void updateNumberOfHands(String opponentNick) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        st.executeUpdate("UPDATE opponentidentifier_2_0_preflop SET numberOfHands = numberOfHands + 1 WHERE playerName = '" + opponentNick + "'");
        st.executeUpdate("UPDATE opponentidentifier_2_0_postflop SET numberOfHands = numberOfHands + 1 WHERE playerName = '" + opponentNick + "'");

        st.close();
        closeDbConnection();
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

    public double getNumberOfHands() {
        return numberOfHands;
    }

    public double getOppPre3bet() {
        return oppPre3bet;
    }

    public double getOppPreLooseness() {
        return oppPreLooseness;
    }

    public double getOppPostRaise() {
        return oppPostRaise;
    }

    public double getOppPostBet() {
        return oppPostBet;
    }

    public double getOppPostLooseness() {
        return oppPostLooseness;
    }
}
