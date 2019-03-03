package com.lennart.model.action.actionbuilders.ai;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameFlow {

    private Connection con;

    public static double GROUP_C_MAX = 0.425;
    public static double GROUP_B_MAX = 0.55;
    public static double LESS_THAN_20_HANDS = -2;

    public double getNumberOfHandsWonAgainstOppInLast20Hands(String opponentName, int entry) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs;

        if(entry != -1) {
            rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "' AND entry < " + entry + " ORDER BY entry DESC;");
        } else {
            rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "' ORDER BY entry DESC;");
        }

        int counter = 0;
        double botWonHand = 0;
        double oppWonHand = 0;

        while(rs.next()) {
            counter++;

            if(counter <= 40) {
                if(rs.getString("bot_won_hand").equals("true")) {
                    botWonHand++;
                } else {
                    oppWonHand++;
                }
            } else {
                break;
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        double ratio;

        if(counter < 20) {
            ratio = -2;
        } else {
            ratio = botWonHand / (botWonHand + oppWonHand);
        }

        return ratio;
    }

    public String getOpponentGroup(String opponentName) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "' ORDER BY entry DESC;");

        double recentHandsWonRatio;

        if(rs.next()) {
            recentHandsWonRatio = rs.getDouble("recent_hands_won");
        } else {
            recentHandsWonRatio = -2;
        }

        rs.close();
        st.close();

        closeDbConnection();

        String oppGroup = getOpponentGroupInitialFromRatio(recentHandsWonRatio);
        oppGroup = adjustOppTypeForRecentBigPots(opponentName, oppGroup, -1);

        return oppGroup;
    }

    public String adjustOppTypeForRecentBigPots(String opponentName, String oppType, int entryFromAnalysis) throws Exception {
        String oppGroupToReturn = oppType;

        int counter = 0;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs;

        if(entryFromAnalysis != -1) {
            rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "' AND entry < " + entryFromAnalysis + " ORDER BY entry DESC;");
        } else {
            rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + opponentName + "' ORDER BY entry DESC;");
        }

        while(rs.next()) {
            counter++;

            if(counter <= 40) {
                String board = rs.getString("board");

                if(!board.equals("")) {
                    double botTotalBetSize = rs.getDouble("bot_total_betsize");
                    double opponentTotalBetSize = rs.getDouble("opponent_total_betsize");
                    String botAction = rs.getString("bot_action");

                    if(botTotalBetSize >= 300 || (opponentTotalBetSize >= 300 && !botAction.equals("fold"))) {
                        boolean botWonHand = rs.getString("bot_won_hand").equals("true");
                        int entry = rs.getInt("entry");

                        if(botWonHand) {
                            oppGroupToReturn = "OppTypeA";
                            System.out.println("Adjusted opptype to OppTypeA (from: " + oppType + ") entry: + " + entry);
                        } else {
                            oppGroupToReturn = "OppTypeC";
                            System.out.println("Adjusted opptype to OppTypeC (from: " + oppType + ") entry: + " + entry);
                        }

                        break;
                    }
                }
            } else {
                break;
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        return oppGroupToReturn;
    }

    public String getOpponentGroupInitialFromRatio(double recentHandsWon) {
        String oppGroup;

        if(recentHandsWon == LESS_THAN_20_HANDS) {
            oppGroup = "OppTypeB";
        } else if(recentHandsWon < GROUP_C_MAX) {
            oppGroup = "OppTypeC";
        } else if(recentHandsWon < GROUP_B_MAX) {
            oppGroup = "OppTypeB";
        } else {
            oppGroup = "OppTypeA";
        }

        return oppGroup;
    }

    private void printStatsBoundries() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw;");

        List<Double> allGameFlowRatios = new ArrayList<>();

        while(rs.next()) {
            if(rs.getDouble("recent_hands_won") != -2) {
                allGameFlowRatios.add(rs.getDouble("recent_hands_won"));
            }
        }

        Collections.sort(allGameFlowRatios);

        double oneThird = allGameFlowRatios.size() * 0.33333;
        int oneThirdInt = (int) oneThird;

        double twoThird = allGameFlowRatios.size() * 0.66666;
        int twoThirdInt = (int) twoThird;

        System.out.println(allGameFlowRatios.get(oneThirdInt));
        System.out.println(allGameFlowRatios.get(twoThirdInt));
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
