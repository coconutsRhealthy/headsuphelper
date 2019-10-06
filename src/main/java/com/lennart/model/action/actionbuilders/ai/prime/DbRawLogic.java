package com.lennart.model.action.actionbuilders.ai.prime;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSave;
import com.lennart.model.action.actionbuilders.ai.dbstatsraw.DbStatsRawBluffPostflopMigrator;
import com.lennart.model.card.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 24/09/2019.
 */
public class DbRawLogic {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new DbRawLogic().fillArWinnings();
    }

    private void testmethod() throws Exception {
        setPotSize(false);

        List<Integer> entriesWhereNewHandsStart = getEntriesWhereNewHandStarts();
        fillHandNumbersInDbStatsRaw(entriesWhereNewHandsStart);

        for(int i = 1; i < 38; i++) {
            fillPotAtEndOfHand(i);
        }
    }

    private void fillHandNumbersInDbStatsRaw(List<Integer> entriesWhereNewHandsStart) throws Exception {
        int handCounter = 0;

        initializeDbConnection();
        Statement st = con.createStatement();

        for(int i = 0; i <= 100; i++) {
            if(entriesWhereNewHandsStart.contains(i)) {
                handCounter++;
            }

            st.executeUpdate("UPDATE dbstats_raw SET handnumber = " + handCounter + " WHERE entry = '" + i + "'");
        }

        st.close();

        closeDbConnection();
    }

    private List<Integer> getEntriesWhereNewHandStarts() throws Exception {
        List<Integer> entriesWhereNewHandStarts = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry < 100 ORDER BY entry ASC;");

        int startEntry;

        while(rs.next()) {
            startEntry = rs.getInt("entry");

            int counter = 0;

            String holeCardsBaseAr = rs.getString("holecards");
            String positionBaseAr = rs.getString("position");
            boolean isSameHand = true;

            while(isSameHand) {
                if(rs.next()) {
                    counter++;

                    String holeCardsResearchAr = rs.getString("holecards");
                    String positionResearchAr = rs.getString("position");

                    if(holeCardsBaseAr.equals(holeCardsResearchAr) && positionBaseAr.equals(positionResearchAr)) {
                        //nothing
                    } else {
                        counter = 0;
                        isSameHand = false;

                        int entryToAdd = startEntry + counter;
                        entriesWhereNewHandStarts.add(entryToAdd);

                        rs.previous();
                    }
                } else {
                    isSameHand = false;
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        return entriesWhereNewHandStarts;
    }

    private void setPotSize(boolean includingBets) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry < 100 ORDER BY entry ASC;");

        int counter = 0;

        while(rs.next()) {
            double botStack = rs.getDouble("botstack");
            double oppStack = rs.getDouble("opponentstack");
            double botTotalBetsize = rs.getDouble("bot_total_betsize");
            double oppTotalBetsize = rs.getDouble("opponent_total_betsize");

            double pot = 0;

            if(botTotalBetsize + oppTotalBetsize + botStack + oppStack != 3000) {
                pot = 3000 - (botStack + oppStack + botTotalBetsize + oppTotalBetsize);
            }

            if(includingBets) {
                pot = pot + botTotalBetsize + oppTotalBetsize;
            }

            int entry = rs.getInt("entry");

            if(pot > 0 && pot < 3000) {
                String potColumn;

                if(includingBets) {
                    potColumn = "pot_incl_bets";
                } else {
                    potColumn = "pot";
                }

                Statement st2 = con.createStatement();

                st2.executeUpdate("UPDATE dbstats_raw SET " + potColumn + " = " + pot + " WHERE entry = '" + entry + "'");

                st2.close();
                System.out.println(counter++);
            }
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private void fillPotAtEndOfHand(int handNumber) throws Exception {
        double potAtEndOfHand = -1;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE handnumber = " + handNumber + " ORDER BY entry DESC;");

        if(rs.next()) {
            String botAction = rs.getString("bot_action");
            String oppAction = rs.getString("opponent_action");
            double currentPot = rs.getDouble("pot");
            double botStack = rs.getDouble("botstack");
            double opponentStack = rs.getDouble("opponentstack");
            double botTotalBetSize = rs.getDouble("bot_total_betsize");
            double oppTotalBetSize = rs.getDouble("opponent_total_betsize");
            double botSizing = rs.getDouble("sizing");
            boolean showdown = rs.getString("showdown_occured").equals("true");

            if(botAction.equals("fold")) {
                potAtEndOfHand = getPotAtEndOfHandBotActionFold(currentPot, botTotalBetSize);
            } else if(botAction.equals("check")) {
                potAtEndOfHand = getPotAtEndOfHandBotActionCheck(currentPot);
            } else if(botAction.equals("call")) {
                potAtEndOfHand = getPotAtEndOfHandBotActionCall(currentPot, botTotalBetSize, oppTotalBetSize, botStack, oppAction);
            } else if(botAction.equals("bet75pct")) {
                potAtEndOfHand = getPotAtEndOfHandBotActionBet(currentPot, botSizing, opponentStack, showdown);
            } else if(botAction.equals("raise")) {
                potAtEndOfHand = getPotAtEndOfHandBotActionRaise(currentPot, botSizing, oppTotalBetSize, opponentStack, showdown);
            }
        }

        rs.close();
        st.close();

        Statement st2 = con.createStatement();
        st2.executeUpdate("UPDATE dbstats_raw SET pot_end_of_hand = " + potAtEndOfHand + " WHERE handnumber = '" + handNumber + "'");
        st2.close();

        closeDbConnection();
    }

    private void fillArWinnings() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();

        for(int i = 1; i < 38; i++) {
            ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE handnumber = " + i + ";");

            double arInHandCounter = 0;
            double potAtEndOfHand = -1;
            boolean botWonHand = false;

            while(rs.next()) {
                arInHandCounter++;

                if(potAtEndOfHand == -1) {
                    potAtEndOfHand = rs.getDouble("pot_end_of_hand");
                }

                botWonHand = rs.getString("bot_won_hand").equals("true");
            }

            double arWinnings = potAtEndOfHand / arInHandCounter;

            if(!botWonHand) {
                arWinnings = arWinnings * -1;
            }

            if(potAtEndOfHand == 0) {
                arWinnings = 6666.66;
            }

            System.out.println(arWinnings);

            st.executeUpdate("UPDATE dbstats_raw SET ar_winnings = " + arWinnings + " WHERE handnumber = '" + i + "'");

            rs.close();
        }

        st.close();
        closeDbConnection();
    }

    private double getPotAtEndOfHandBotActionCheck(double currentPot) {
        return currentPot;
    }

    private double getPotAtEndOfHandBotActionBet(double currentPot, double botBetSizing, double opponentStack, boolean showdown) {
        double potAtEndOfHand;

        if(showdown) {
            double effectiveBetSize;

            if(botBetSizing > opponentStack) {
                effectiveBetSize = opponentStack;
            } else {
                effectiveBetSize = botBetSizing;
            }

            potAtEndOfHand = currentPot + (2 * effectiveBetSize);
        } else {
            potAtEndOfHand = currentPot;
        }

        return potAtEndOfHand;
    }

    private double getPotAtEndOfHandBotActionCall(double currentPot, double botBetSize, double oppBetSize, double botStack, String oppAction) {
        double potAtEndOfHand;

        if(oppAction.equals("bet75pct")) {
            double oppEffectiveBetSize;

            if(oppBetSize > botStack) {
                oppEffectiveBetSize = botStack;
            } else {
                oppEffectiveBetSize = oppBetSize;
            }

            potAtEndOfHand = currentPot + (2 * oppEffectiveBetSize);
        } else if(oppAction.equals("raise")) {
            double oppEffectiveRaiseSize;

            if(oppBetSize > (botStack - botBetSize)) {
                oppEffectiveRaiseSize = botStack - botBetSize;
            } else {
                oppEffectiveRaiseSize = oppBetSize - botBetSize;
            }

            potAtEndOfHand = currentPot + botBetSize + botBetSize + (2 * oppEffectiveRaiseSize);
        } else {
            System.out.println("Should not come here! X");
            potAtEndOfHand = -50;
        }

        return potAtEndOfHand;
    }

    private double getPotAtEndOfHandBotActionFold(double currentPot, double botBetSize) {
        double potAtEndOfHand = currentPot + botBetSize;
        return potAtEndOfHand;
    }

    private double getPotAtEndOfHandBotActionRaise(double currentPot, double botRaiseSizing, double oppBetSize, double opponentStack, boolean showdown) {
        double potAtEndOfHand;

        if(showdown) {
            double effectiveCallAmountForOpp;

            if((botRaiseSizing - oppBetSize) > (opponentStack - oppBetSize)) {
                effectiveCallAmountForOpp = opponentStack - oppBetSize;
            } else {
                effectiveCallAmountForOpp = botRaiseSizing - oppBetSize;
            }

            potAtEndOfHand = currentPot + (2 * oppBetSize) + (2 * effectiveCallAmountForOpp);
        } else {
            potAtEndOfHand = currentPot + oppBetSize;
        }

        return potAtEndOfHand;
    }

    private void fillStreet() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry < 100 ORDER BY entry ASC;");

        DbStatsRawBluffPostflopMigrator dbStatsEtc = new DbStatsRawBluffPostflopMigrator();

        int counter = 0;

        while(rs.next()) {
            int entry = rs.getInt("entry");
            String board = rs.getString("board");

            String streetString;

            if(board.equals("")) {
                streetString = "Preflop";
            } else {
                streetString = dbStatsEtc.getStreetString(board);
            }

            Statement st2 = con.createStatement();
            st2.executeUpdate("UPDATE dbstats_raw SET street = '" + streetString + "' WHERE entry = '" + entry + "'");
            st2.close();
            System.out.println(counter++);
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private void fillCombo() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry < 100 ORDER BY entry ASC;");

        int counter = 0;

        while(rs.next()) {
            int entry = rs.getInt("entry");
            String holeCards = rs.getString("holecards");
            String combo = getWrittenCombo(holeCards);

            Statement st2 = con.createStatement();
            st2.executeUpdate("UPDATE dbstats_raw SET combo = '" + combo + "' WHERE entry = '" + entry + "'");
            st2.close();
            System.out.println(counter++);
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private String getWrittenCombo(String holeCards) {
        String holeCardsOneSuit = holeCards.replaceAll("s", "x");
        holeCardsOneSuit = holeCardsOneSuit.replaceAll("c", "x");
        holeCardsOneSuit = holeCardsOneSuit.replaceAll("d", "x");
        holeCardsOneSuit = holeCardsOneSuit.replaceAll("h", "x");

        String[] hcOneSuitSplitted = holeCardsOneSuit.split("x");

        int rankCard1 = Integer.valueOf(hcOneSuitSplitted[0]);
        int rankCard2 = Integer.valueOf(hcOneSuitSplitted[1]);

        String holeCardsOnlySuits = holeCards.replaceAll("[\\d.]", "");

        char suitCard1 = holeCardsOnlySuits.charAt(0);
        char suitCard2 = holeCardsOnlySuits.charAt(1);

        Card holeCard1 = new Card(rankCard1, suitCard1);
        Card holeCard2 = new Card(rankCard2, suitCard2);

        List<Card> holeCardsAsCardObjects = new ArrayList<>();
        holeCardsAsCardObjects.add(holeCard1);
        holeCardsAsCardObjects.add(holeCard2);

        String holeCardsCombo = new DbSave().getComboLogic(holeCardsAsCardObjects);

        return holeCardsCombo;
    }

    private void fillOppType(boolean narrow) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponent_types;");

        int counter = 0;

        while(rs.next()) {
            String oppName = rs.getString("playerName");
            String oppType;
            String column;

            if(narrow) {
                oppType = rs.getString("oppTypeNarrow");
                column = "oppTypeNarrow";
            } else {
                oppType = rs.getString("oppTypeBroad");
                column = "oppTypeBroad";
            }

            Statement st2 = con.createStatement();
            st2.executeUpdate("UPDATE dbstats_raw SET " + column + " = '" + oppType + "' WHERE opponent_name = '" + oppName + "';");
            st2.close();

            System.out.println(counter++);
        }

        rs.close();
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
}
