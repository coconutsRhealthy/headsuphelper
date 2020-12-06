package com.lennart.model.action.actionbuilders.ai;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by LennartMac on 12/10/2020.
 */
public class PreflopShoveAssistant {

    private Connection con;


    public double getExpectedChipProfitOfPfShove(double bigBlind, double oppFoldPercentage, boolean facingLimp, double effectiveStack, double botHandstrength) {
        double expectedChipProfit = -1;
        double chipsOnTable;

        if(facingLimp) {
            chipsOnTable = 2 * bigBlind;
        } else {
            chipsOnTable = 1.5 * bigBlind;
        }

        Map<Double, Double> expectedEquityMap = getExpectedEquity();

        for(Map.Entry<Double, Double> entry : expectedEquityMap.entrySet()) {
            if(botHandstrength < entry.getKey()) {
                double expectedNonSdProfit = oppFoldPercentage * chipsOnTable;
                double expectedSdProfit = (1 - oppFoldPercentage) * ((entry.getValue() * effectiveStack) - ((1 - entry.getValue()) * effectiveStack));
                expectedChipProfit = expectedNonSdProfit + expectedSdProfit;
                break;
            }
        }

        return expectedChipProfit;
    }

    public double getExpectedChipProfitOfPf3betShove(double oppFoldVs3betPercentage, double botTotalBetsize,
                                                     double oppTotalBetsize, double botHandstrength, double effectiveStack) {
        double expectedChipProfit = -1;
        double chipsOnTable = botTotalBetsize + oppTotalBetsize;

        Map<Double, Double> expectedEquityMap = getExpectedEquity();

        for(Map.Entry<Double, Double> entry : expectedEquityMap.entrySet()) {
            if(botHandstrength < entry.getKey()) {
                double expectedNonSdProfit = oppFoldVs3betPercentage * chipsOnTable;
                double expectedSdProfit = (1 - oppFoldVs3betPercentage) * ((entry.getValue() * effectiveStack) - ((1 - entry.getValue()) * effectiveStack));
                expectedChipProfit = expectedNonSdProfit + expectedSdProfit;
                break;
            }
        }

        return expectedChipProfit;
    }


    private LinkedHashMap<Double, Double> getExpectedEquity() {
        LinkedHashMap<Double, Double> expectedEquityMap = new LinkedHashMap<>();

        expectedEquityMap.put(0.25, 0.3);
        expectedEquityMap.put(0.35, 0.3);
        expectedEquityMap.put(0.45, 0.3);
        expectedEquityMap.put(0.55, 0.33);
        expectedEquityMap.put(0.65, 0.33);
        expectedEquityMap.put(0.75, 0.33);
        expectedEquityMap.put(0.85, 0.33);
        expectedEquityMap.put(0.95, 0.33);
        expectedEquityMap.put(1.2, 0.33);

        return expectedEquityMap;
    }

    public double getRecentFoldVsPfShoveRatioForPlayer(String playerName) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + playerName + "' ORDER BY entry DESC;");

        double ratio;
        double oppFoldCounter = 0;
        double totalCounter = 0;

        while(rs.next()) {
            if(rs.getString("board").equals("")) {
                if(rs.getString("opponent_action").equals("bet") || rs.getString("opponent_action").equals("call")) {
                    if(rs.getString("bot_action").equals("raise")) {
                        if(rs.getDouble("sizing") >= 500) {
                            if(rs.getString("bot_won_hand").equals("true")) {
                                if(rs.getString("showdown_occured").equals("false")) {
                                    oppFoldCounter++;
                                }
                            }

                            totalCounter++;

                            if(totalCounter >= 10) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        if(totalCounter < 2) {
            ratio = -1;
        } else {
            ratio = oppFoldCounter / totalCounter;
        }

        System.out.println("pf : " + playerName + " foldcounter: " + oppFoldCounter);
        System.out.println("pf : " + playerName + " totalcounter: " + totalCounter);
        System.out.println("pf : " + playerName + " ratio: " + ratio);

        return ratio;
    }

    public double getRecentFoldVsPf3betShoveRatioForPlayer(String playerName) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE opponent_name = '" + playerName + "' ORDER BY entry DESC;");

        double ratio;
        double oppFoldCounter = 0;
        double totalCounter = 0;

        while(rs.next()) {
            if(rs.getString("board").equals("")) {
                if(rs.getString("opponent_action").equals("raise")) {
                   if(rs.getString("bot_action").equals("raise")) {
                       if(rs.getDouble("sizing") >= 500) {
                           if(rs.getString("bot_won_hand").equals("true")) {
                               if(rs.getString("showdown_occured").equals("false")) {
                                   oppFoldCounter++;
                               }
                           }

                           totalCounter++;

                           if(totalCounter >= 10) {
                               break;
                           }
                       }
                   }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        if(totalCounter < 2) {
            ratio = -1;
        } else {
            ratio = oppFoldCounter / totalCounter;
        }

        System.out.println("pf : " + playerName + " 3bet-foldcounter: " + oppFoldCounter);
        System.out.println("pf : " + playerName + " 3bet-totalcounter: " + totalCounter);
        System.out.println("pf : " + playerName + " 3ratio: " + ratio);

        return ratio;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
