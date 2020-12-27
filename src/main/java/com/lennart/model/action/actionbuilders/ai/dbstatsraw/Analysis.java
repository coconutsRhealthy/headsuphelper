package com.lennart.model.action.actionbuilders.ai.dbstatsraw;

import com.lennart.model.action.actionbuilders.ai.GameFlow;
import com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0.DbSaveBluff_2_0;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 26/01/2019.
 */
public class Analysis {

    private Connection con;

//    public static void main(String[] args) throws Exception {
//        new Analysis().potsizeGroupAnalysis2();
//    }

    private void potsizeGroupAnalysis2() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE pot > 0;");

        List<Double> allPots = new ArrayList<>();

        while(rs.next()) {
//            double botStack = rs.getDouble("botstack");
//            double oppStack = rs.getDouble("opponentstack");
//            double botTotalBetsize = rs.getDouble("bot_total_betsize");
//            double oppTotalBetsize = rs.getDouble("opponent_total_betsize");
//
//            double pot = 2000 - botStack - oppStack - botTotalBetsize - oppTotalBetsize;
//
//            if(pot >= 40 && pot < 2000)
//
//                allPots.add(pot);
            allPots.add(rs.getDouble("pot"));
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allPots);

        System.out.println(allPots.get(allPots.size() / 3) * 2);
    }

    private void potsizeGroupAnalysis() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE board != \"\" AND entry > 300000;");

        List<Double> allPots = new ArrayList<>();

        while(rs.next()) {
            double botStack = rs.getDouble("botstack");
            double oppStack = rs.getDouble("opponentstack");
            double botTotalBetsize = rs.getDouble("bot_total_betsize");
            double oppTotalBetsize = rs.getDouble("opponent_total_betsize");

            double pot = 2000 - botStack - oppStack - botTotalBetsize - oppTotalBetsize;

            if(pot >= 40 && pot < 2000)

            allPots.add(pot);
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allPots);

        System.out.println(allPots.get(allPots.size() / 3) * 2);
    }

    private void theCheck() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflop;");

        List<Double> allPre2betOdds = new ArrayList<>();
        List<Double> allPre3betOdds = new ArrayList<>();

        int counter = 0;

        while(rs.next()) {
            String opponentName = rs.getString("playerName");
            double average2bet = getAveragePre2betOdds(opponentName);
            double average3bet = getAveragePre3betOdds(opponentName);
            allPre2betOdds.add(average2bet);
            allPre3betOdds.add(average3bet);
            System.out.println(counter++);
        }

        Collections.sort(allPre2betOdds);
        Collections.sort(allPre3betOdds);

        for(int i = 0; i < allPre2betOdds.size(); i++) {
            System.out.println(i + "        " + allPre2betOdds.get(i));
        }

        System.out.println("******************");

        for(int i = 0; i < allPre3betOdds.size(); i++) {
            System.out.println(i + "        " + allPre3betOdds.get(i));
        }

        //System.out.println("wacht");
    }

    private void getOppPostflopAvBetOdds(String opponentName) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry > 280000 AND opponent_name = '" + opponentName + "';");

        double totalPostflopFacingOdds = 0;
        double counter = 0;

        while(rs.next()) {
            if(!rs.getString("board").equals("")) {
                if(rs.getString("opponent_action").equals("bet75pct")) {
                    double oppTotalBetsize = rs.getDouble("opponent_total_betsize");
                    double botStack = rs.getDouble("botstack");
                    double oppStack = rs.getDouble("opponentstack");
                    double pot = 2000 - botStack - oppStack;

                    if(oppTotalBetsize > 0 && pot > 0) {
                        double facingOdds = oppTotalBetsize / (pot + oppTotalBetsize);
                        totalPostflopFacingOdds = totalPostflopFacingOdds + facingOdds;
                        counter++;
                    } else {
                        System.out.println("trp");
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println("average: " + totalPostflopFacingOdds / counter);
        System.out.println("counter: " + counter);
    }

    public double getAveragePre3betOdds(String opponentName) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry > 280000 AND opponent_name = '" + opponentName + "';");

        double totalPreflopFacing3betOdds = 0;
        double counter = 0;

        while(rs.next()) {
            if(rs.getString("board").equals("")) {
                if(rs.getString("position").equals("Ip")) {
                    if(rs.getString("opponent_action").equals("raise")) {
                        double botTotalBetsize = rs.getDouble("bot_total_betsize");
                        double oppTotalBetsize = rs.getDouble("opponent_total_betsize");

                        if(botTotalBetsize > 0 && oppTotalBetsize > 0 && rs.getDouble("opponentstack") > 0) {
                            double preflopFacingOdds = (oppTotalBetsize - botTotalBetsize) / (oppTotalBetsize + botTotalBetsize);

                            totalPreflopFacing3betOdds = totalPreflopFacing3betOdds + preflopFacingOdds;
                            counter++;
                        }
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        double average = totalPreflopFacing3betOdds / counter;
        return average;
    }

    public double getAveragePre2betOdds(String opponentName) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry > 280000 AND opponent_name = '" + opponentName + "';");

        double totalPreflopFacingOdds = 0;
        double counter = 0;

        while(rs.next()) {
            if(rs.getString("board").equals("")) {
                if(rs.getDouble("bot_total_betsize") == rs.getDouble("bigblind")) {
                    double botTotalBetsize = rs.getDouble("bot_total_betsize");
                    double oppTotalBetsize = rs.getDouble("opponent_total_betsize");

                    if(botTotalBetsize > 0 && oppTotalBetsize > 0 && botTotalBetsize <= oppTotalBetsize) {
                        double preflopFacingOdds = (oppTotalBetsize - botTotalBetsize) / (oppTotalBetsize + botTotalBetsize);

                        totalPreflopFacingOdds = totalPreflopFacingOdds + preflopFacingOdds;
                        counter++;
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        double average = totalPreflopFacingOdds / counter;

        return average;
    }

    private void interesting2() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry > 314900;");

        int counter = 0;
        int trueCounter = 0;

        while(rs.next()) {
            counter++;

            if(counter > 1) {
                if(!rs.getString("board").equals("")) {
                    List<Card> board = convertCardStringToCardList(rs.getString("board"));

                    if(board.size() == 5) {
                        if(rs.getString("bot_action").equals("check")) {
                            if(rs.getDouble("handstrength") < 0.25) {
                                rs.previous();

                                if(!rs.getString("bot_action").equals("call")) {
                                    trueCounter++;
                                }

                                rs.next();
                            }
                        }
                    }
                }
            }

            //System.out.println(rs.getInt("entry"));
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println();
        System.out.println(trueCounter);
    }

    private void interesting() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry > 314910;");

        double betCounter = 0;
        double checkCounter = 0;

        while(rs.next()) {
            if(!rs.getString("board").equals("")) {
                List<Card> board = convertCardStringToCardList(rs.getString("board"));

                if(board.size() == 3) {
                    if(rs.getString("bot_action").equals("bet75pct")) {
                        betCounter++;
                    }

                    if(rs.getString("bot_action").equals("check")) {
                        checkCounter++;
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        double total = betCounter + checkCounter;

        System.out.println("bet: " + betCounter);
        System.out.println("check: " + checkCounter);
        System.out.println("total: " + total);
        System.out.println("ratio: " + (betCounter / total));
    }

    private void countNumberOfStrongTurnGutshots() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry > 317000;");

        int turnCounter = 0;
        int gutshotCounter = 0;

        List<Integer> numberOfGutshotCombos = new ArrayList<>();
        List<Integer> numberOfStraightCombos = new ArrayList<>();

        while(rs.next()) {
            if(!rs.getString("board").equals("")) {
                System.out.println(".");
                List<Card> board = convertCardStringToCardList(rs.getString("board"));

                if(board.size() == 4) {
                    turnCounter++;

                    List<Card> holeCards = convertCardStringToCardList(rs.getString("holecards"));

                    BoardEvaluator boardEvaluator = new BoardEvaluator(board);
                    HandEvaluator handEvaluator = new HandEvaluator(holeCards, boardEvaluator);

                    if(handEvaluator.hasDrawOfType("strongGutshot")) {
                        StraightDrawEvaluator straightDrawEvaluator = boardEvaluator.getStraightDrawEvaluator();

                        int numberOfGutshotDraws = straightDrawEvaluator.getWeakGutshotCombos().size() +
                                straightDrawEvaluator.getMediumGutshotCombos().size() + straightDrawEvaluator.getStrongGutshotCombos().size();
                        numberOfGutshotCombos.add(numberOfGutshotDraws);

                        int numberOfStraights = boardEvaluator.getStraightEvaluator().getMapOfStraightCombos().size();
                        numberOfStraightCombos.add(numberOfStraights);

                        if(numberOfStraights == 0) {
                            //System.out.println("wacheffiez");
                        }

                        if(numberOfGutshotDraws > 182 && numberOfStraights == 0) {
                            System.out.println("wtf");
                        }

                        gutshotCounter++;
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(numberOfGutshotCombos);
        Collections.sort(numberOfStraightCombos);

        System.out.println("Turncounter: " + turnCounter);
        System.out.println("Gutshotcounter: " + gutshotCounter);
    }

    private void countNumberOfStrongBdFds() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry > 317000;");

        int flopCounter = 0;
        int backdoorFdCounter = 0;
        int tenUpBackdoorFdCounter = 0;
        int jackUpBackdoorFdCounter = 0;
        int queenUpBackdoorFdCounter = 0;
        int kingUpBackdoorFdCounter = 0;
        int aceUpBackdoorFdCounter = 0;

        while(rs.next()) {
            if(!rs.getString("board").equals("")) {
                System.out.println(".");
                List<Card> board = convertCardStringToCardList(rs.getString("board"));

                if(board.size() == 3) {
                    flopCounter++;

                    List<Card> holeCards = convertCardStringToCardList(rs.getString("holecards"));

                    BoardEvaluator boardEvaluator = new BoardEvaluator(board);
                    HandEvaluator handEvaluator = new HandEvaluator(holeCards, boardEvaluator);

                    if(handEvaluator.hasDrawOfType("strongBackDoorFlush") && holeCards.get(0).getSuit() == holeCards.get(1).getSuit()) {
                        if(handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("mediumFlushDraw") || handEvaluator.hasDrawOfType("weakFlushDraw")) {
                            System.out.println("JEP!");
                        } else {
                            System.out.println("NOPE!");
                        }

                        if(holeCards.get(0).getRank() >= 10 || holeCards.get(1).getRank() >= 10) {
                            tenUpBackdoorFdCounter++;
                        }

                        if(holeCards.get(0).getRank() >= 11 || holeCards.get(1).getRank() >= 11) {
                            jackUpBackdoorFdCounter++;
                        }

                        if(holeCards.get(0).getRank() >= 12 || holeCards.get(1).getRank() >= 12) {
                            queenUpBackdoorFdCounter++;
                        }

                        if(holeCards.get(0).getRank() >= 13 || holeCards.get(1).getRank() >= 13) {
                            kingUpBackdoorFdCounter++;
                        }

                        if(holeCards.get(0).getRank() >= 14 || holeCards.get(1).getRank() >= 14) {
                            aceUpBackdoorFdCounter++;
                        }

                        backdoorFdCounter++;
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println("Flopcounter: " + flopCounter);
        System.out.println("BackdoorFDcounter: " + backdoorFdCounter);
        System.out.println("tenBackdoorFdCounter: " + tenUpBackdoorFdCounter);
        System.out.println("jackBackdoorFdCounter: " + jackUpBackdoorFdCounter);
        System.out.println("queenBackdoorFdCounter: " + queenUpBackdoorFdCounter);
        System.out.println("kingBackdoorFdCounter: " + kingUpBackdoorFdCounter);
        System.out.println("aceBackdoorFdCounter: " + aceUpBackdoorFdCounter);
    }

    private void newAlaysis() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry > 317632;");

        DbStatsRawBluffPostflopMigrator dbStatsRawBluffPostflopMigrator = new DbStatsRawBluffPostflopMigrator();

        int flopCounter = 0;
        int turnCounter = 0;
        int riverCounter = 0;

        while(rs.next()) {
            System.out.println(".");
            if(!rs.getString("board").equals("")) {
                String street = dbStatsRawBluffPostflopMigrator.getStreetString(rs.getString("board"));

                if(street.equals("Flop")) {
                    flopCounter++;
                } else if(street.equals("Turn")) {
                    turnCounter++;
                } else if(street.equals("River")) {
                    riverCounter++;
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println("Flop: " + flopCounter);
        System.out.println("Turn: " + turnCounter);
        System.out.println("River: " + riverCounter);
    }

    private void addOppTypeToDbStatsRaw() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw;");

        int counter = 0;
        int counter2 = 0;

        while(rs.next()) {
            double recentHandsWonRatio = rs.getDouble("recent_hands_won");

            GameFlow gameFlow = new GameFlow();

            int entry = rs.getInt("entry");

            String oppType = gameFlow.getOpponentGroupInitialFromRatio(recentHandsWonRatio);
            String oppTypeAfterAdjustment = gameFlow.getAdjustedOppTypeForRecentBigPots(rs.getString("opponent_name"), entry, oppType);

            if(!oppTypeAfterAdjustment.equals("") && !oppTypeAfterAdjustment.equals(oppType)) {
                Statement st2 = con.createStatement();

                st2.executeUpdate("UPDATE dbstats_raw SET adjusted_opp_type = '" + oppTypeAfterAdjustment + "' WHERE entry = '" + entry + "'");

                st2.close();
            }

            counter++;

            if(counter < 100) {
                System.out.print(".");
            } else {
                System.out.println();
                counter = 0;

                counter2++;

                if(counter2 == 15) {
                    MouseKeyboard.click(30, 30);
                    MouseKeyboard.moveMouseToLocation(90, 90);
                    counter2 = 0;
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private void addRecentHandsWonToDbStatsRaw() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw;");

        GameFlow gameFlow = new GameFlow();

        int counter = 0;
        int counter2 = 0;

        while(rs.next()) {
            int entry = rs.getInt("entry");
            double recentWinRatio = gameFlow.getNumberOfHandsWonAgainstOppInLast20Hands(rs.getString("opponent_name"), entry);

            Statement st2 = con.createStatement();

            st2.executeUpdate("UPDATE dbstats_raw SET recent_hands_won = " + recentWinRatio + " WHERE entry = '" + entry + "'");

            st2.close();

            counter++;

            if(counter < 100) {
                System.out.print(".");
            } else {
                System.out.println();
                counter = 0;

                counter2++;

                if(counter2 == 15) {
                    MouseKeyboard.click(30, 30);
                    MouseKeyboard.moveMouseToLocation(90, 90);
                    counter2 = 0;
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private TreeMap<String, Integer> getOpponentTypesForDate(String date) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw;");

        Set<String> opponents = new HashSet<>();

        while(rs.next()) {
            if(rs.getString("date").contains(date)) {
                opponents.add(rs.getString("opponent_name"));
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        List<String> opponentsAsList = new ArrayList<>();
        opponentsAsList.addAll(opponents);

        List<String> opponentTypes = new ArrayList<>();

        TreeMap<String, Integer> frequencyMap = new TreeMap<>();

        for(String opponent : opponentsAsList) {
            try {
                String opponentType = new DbStatsRawBluffPostflopMigrator().getOpponentGroup(opponent);
                opponentTypes.add(opponentType);
            } catch (Exception e) {
                System.out.println("opponent not found: " + opponent);
            }
        }

        Collections.sort(opponentTypes);

        for(String type : opponentTypes) {
            frequencyMap.put(type, Collections.frequency(opponentTypes, type));
        }

        return frequencyMap;
    }

    private void winAgainstOpponentTypeAnalysis() throws Exception {
        Map<String, List<Double>> oppTypeMap = new HashMap<>();

        List<String> allOpponentTypes = new ArrayList<>();

        List<String> oppPre3bet = new ArrayList<>();
        List<String> oppPreLooseness = new ArrayList<>();
        List<String> oppPostRaise = new ArrayList<>();
        List<String> oppPostBet = new ArrayList<>();
        List<String> oppPostLooseness = new ArrayList<>();

        oppPre3bet.add("OppPre3betLow");
        oppPre3bet.add("OppPre3betHigh");

        oppPreLooseness.add("OppPreLoosenessTight");
        oppPreLooseness.add("OppPreLoosenessLoose");

        oppPostRaise.add("OppPostRaiseLow");
        oppPostRaise.add("OppPostRaiseHigh");

        oppPostBet.add("OppPostBetLow");
        oppPostBet.add("OppPostBetHigh");

        oppPostLooseness.add("OppPostLoosenessTight");
        oppPostLooseness.add("OppPostLoosenessLoose");

        for(String a : oppPre3bet) {
            for(String b : oppPreLooseness) {
                for(String c : oppPostRaise) {
                    for(String d : oppPostBet) {
                        for(String e : oppPostLooseness) {
                            allOpponentTypes.add(a + b + c + d + e);
                        }
                    }
                }
            }
        }

        allOpponentTypes.add("OpponentUnknown");

        for(String type : allOpponentTypes) {
            oppTypeMap.put(type, Arrays.asList(0.0, 0.0));
        }

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw;");

        while(rs.next()) {
            String opponentName = rs.getString("opponent_name");
            String opponentType = new DbStatsRawBluffPostflopMigrator().getOpponentStatsString(opponentName);
            boolean botWonHand = rs.getString("bot_won_hand").equals("true");

            double oldSuccessValue = oppTypeMap.get(opponentType).get(0);
            double oldTotalValue = oppTypeMap.get(opponentType).get(1);

            double newSuccessValue;

            if(botWonHand) {
                newSuccessValue = oldSuccessValue + 1.0;
            } else {
                newSuccessValue = oldSuccessValue;
            }

            double newTotalValue = oldTotalValue + 1.0;

            List<Double> newListForType = new ArrayList<>();
            newListForType.add(newSuccessValue);
            newListForType.add(newTotalValue);

            oppTypeMap.put(opponentType, newListForType);
        }

        rs.close();
        st.close();

        closeDbConnection();

        Map<String, Double> finalMap = new HashMap<>();

        for (Map.Entry<String, List<Double>> entry : oppTypeMap.entrySet()) {
            finalMap.put(entry.getKey(), entry.getValue().get(0) / entry.getValue().get(1));
        }

        finalMap = sortByValueHighToLow(finalMap);

        for (Map.Entry<String, Double> entry : finalMap.entrySet()) {
            System.out.println(entry.getKey() + "        " + entry.getValue());
        }

        System.out.println();
        System.out.println();

        int counter = 0;

        for (Map.Entry<String, Double> entry : finalMap.entrySet()) {
            counter++;

            if(counter <= 8) {
                System.out.println("groupAstats.add(\"" + entry.getKey() + "\");");
            } else if(counter <= 16) {
                if(counter == 9) {
                    System.out.println();
                }

                System.out.println("groupBstats.add(\"" + entry.getKey() + "\");");
            } else if(counter <= 24) {
                if(counter == 17) {
                    System.out.println();
                }

                System.out.println("groupCstats.add(\"" + entry.getKey() + "\");");
            } else {
                if(counter == 25) {
                    System.out.println();
                }

                System.out.println("groupDstats.add(\"" + entry.getKey() + "\");");
            }
        }
    }

    private void boardWetnessBluffTestMethod() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw;");

        int total = 0;
        int success = 0;
        int counter = 0;

        DbStatsRawBluffPostflopMigrator dbStatsRawBluffPostflopMigrator = new DbStatsRawBluffPostflopMigrator();

        while(rs.next()) {
            String board = rs.getString("board");

            if(!board.equals("") && dbStatsRawBluffPostflopMigrator.getStreetString(board).equals("Turn")) {
                String botAction = rs.getString("bot_action");

                if(botAction.equals("bet75pct") || botAction.equals("raise")) {
                    double handStrength = rs.getDouble("handstrength");

                    if(handStrength < 0.7) {
                        List<Card> boardRiver = convertCardStringToCardList(board);

                        List<Card> boardTurn = new ArrayList<>();
                        boardTurn.addAll(boardRiver);
                        boardTurn.remove(boardTurn.size() - 1);

                        BoardEvaluator turnBoardEvaluator = new BoardEvaluator(boardTurn);
                        BoardEvaluator riverBoardEvaluator = new BoardEvaluator(boardRiver);

                        int boardWetness = BoardEvaluator.getBoardWetness(turnBoardEvaluator.getTop10percentCombos(),
                                riverBoardEvaluator.getTop10percentCombos());

                        String boardWetnessGroup = new DbSaveBluff_2_0().getBoardWetnessGroupLogic(boardRiver, boardWetness);

                        if(boardWetnessGroup.equals("wet")) {
                            total++;

                            if(rs.getString("bot_won_hand").equals("true")) {
                                success++;
                            }
                        }
                    }
                }
            }

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

        System.out.println("success: " + success);
        System.out.println("total: " + total);
    }

    public List<Card> convertCardStringToCardList(String board) {
        List<Card> boardCardList = new ArrayList<>();

        if(board.equals("")) {
            return boardCardList;
        }

        String boardCopy = board;
        String boardCopy2 = board;

        boardCopy = boardCopy.replaceAll("c", "d");
        boardCopy = boardCopy.replaceAll("s", "d");
        boardCopy = boardCopy.replaceAll("h", "d");

        String[] ranks = boardCopy.split("d");

        String onlySuits = boardCopy2.replaceAll("\\d","");

        char[] suits = onlySuits.toCharArray();

        for(int i = 0; i < ranks.length; i++) {
            Card card = new Card(Integer.valueOf(ranks[i]), suits[i]);
            boardCardList.add(card);
        }

        return boardCardList;
    }

    private void migrateRawDbToIncludeOppPre3betPostRaiseStats() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw;");

        Statement st2 = con.createStatement();

        int counter = 0;

        while(rs.next()) {
            List<Integer> entriesToUpdate = getEntriesToSetToOppDidPre3betOrPostRaiseTrue(rs.getInt("entry"));

            for(Integer i : entriesToUpdate) {
                st2.executeUpdate("UPDATE dbstats_raw SET opp_pre3bet_postraise = 'true' WHERE entry = " + i);
            }

            System.out.print(".");
            counter++;

            if(counter == 150) {
                System.out.println();
                counter = 0;
                MouseKeyboard.click(20, 20);
                MouseKeyboard.moveMouseToLocation(70, 70);
            }
        }

        rs.close();
        st.close();
        st2.close();

        closeDbConnection();
    }

    private List<Integer> getEntriesToSetToOppDidPre3betOrPostRaiseTrue(int handEntry) throws Exception {
        List<Integer> entriesToUpdate = new ArrayList<>();
        List<Integer> allEntriesOfHand = getAllEntryNumbersOfSameHand(handEntry);
        List<Double> allEntriesOfHandAsDouble = new ArrayList<>();

        for(Integer z : allEntriesOfHand) {
            allEntriesOfHandAsDouble.add((double) z);
        }

        initializeDbConnection();

        int limit = -1;

        for(int i = 0; i < allEntriesOfHandAsDouble.size(); i++) {
            int entry = allEntriesOfHandAsDouble.get(i).intValue();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry = " + entry + ";");

            rs.next();

            if(rs.getString("board").equals("") && rs.getString("position").equals("Ip") && rs.getString("opponent_action").equals("raise")) {
                limit = i;
                break;
            }

            if(!rs.getString("board").equals("") && rs.getString("opponent_action").equals("raise")) {
                limit = i;
                break;
            }
        }

        if(limit != -1) {
            for(int y = limit; y < allEntriesOfHandAsDouble.size(); y++) {
                entriesToUpdate.add(allEntriesOfHandAsDouble.get(y).intValue());
            }
        }

        closeDbConnection();

        return entriesToUpdate;
    }

    private boolean opponentDidPre3betOrPostRaiseInHand(int handEntry) throws Exception {
        boolean opponentDidPre3betOrPostRaise = false;

        List<Integer> allEntriesOfHand = getAllEntryNumbersOfSameHand(handEntry);

        initializeDbConnection();

        for(Integer i : allEntriesOfHand) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry = " + i + ";");

            rs.next();

            if(rs.getString("board").equals("") && rs.getString("position").equals("Ip") && rs.getString("opponent_action").equals("raise")) {
                opponentDidPre3betOrPostRaise = true;
                break;
            }

            if(!rs.getString("board").equals("") && rs.getString("opponent_action").equals("raise")) {
                opponentDidPre3betOrPostRaise = true;
                break;
            }
        }

        closeDbConnection();

        return opponentDidPre3betOrPostRaise;
    }

    private List<Integer> getAllEntryNumbersOfSameHand(int handEntry) throws Exception {
        List<Integer> allEntriesOfHand = new ArrayList<>();
        allEntriesOfHand.add(handEntry);

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry = " + handEntry + ";");

        rs.next();

        String holeCards = rs.getString("holecards");

        for(int i = 1; i < 20; i++) {
            int newEntry = handEntry + i;
            rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry = " + newEntry + ";");

            if(rs.next()) {
                if(rs.getString("holecards").equals(holeCards)) {
                    allEntriesOfHand.add(newEntry);
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        for(int i = 1; i < 20; i++) {
            int newEntry = handEntry - i;
            rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry = " + newEntry + ";");

            if(rs.next()) {
                if(rs.getString("holecards").equals(holeCards)) {
                    allEntriesOfHand.add(newEntry);
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allEntriesOfHand);

        return allEntriesOfHand;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }


//    public static void main(String[] args) throws Exception {
//        new Analysis().checkSitOut();
//    }

    private void checkSitOut() throws Exception {
        List<File> allHhFiles = getAllFilesFromDir("/Users/LennartMac/Desktop/tochecksitout");

        for(File file : allHhFiles) {
            List<String> linesOfFile = readTheFile(file);

            for(String line : linesOfFile) {
                if(line.contains("is sitting out") && line.contains("vegeta")) {
                    //System.out.println(line);
                    System.out.println(file.lastModified());
                }
            }
        }
    }




    public static void main(String[] args) throws Exception {
        //new Analysis().doAnalysis(new Analysis().getAllFilesFromDir("/Users/LennartMac/Documents/tourney_hist_analysis"));
        //new Analysis().doAnalysis(new Analysis().getAllFilesFromDir("/Users/LennartMac/Documents/historyanalysis/new_aggr_sizing_play_2"));
        new Analysis().doAnalysis(new Analysis().getAllFilesFromDir("/Users/LennartMac/Documents/historyanalysis/aurora_real26_perf"));
    }



    ////
    private List<File> getAllFilesFromDir(String dirPath) {
        //"/Users/LennartMac/Documents/tourney_hist_analysis"

        File dir = new File(dirPath);
        File[] files = dir.listFiles();

        List<File> allFiles = Arrays.asList(files);

        return allFiles;
    }

    private void doAnalysis(List<File> allFiles) throws Exception {
        double totalCounter = 0;
        double winCounter = 0;

        int diff = 0;

        for(File file : allFiles) {
            List<String> linesOfFile = readTheFile(file);

            if(linesOfFile != null && linesOfFile.size() > 1) {
                for(String line : linesOfFile) {
                    if(line.contains("You finished in 1st place")) {
                        winCounter++;
                        diff++;
                        break;
                    }

                    if(line.contains("You finished in 2nd place")) {
                        diff--;
                        break;
                    }
                }

                //System.out.println(diff);

                totalCounter++;

                double ratio = winCounter / totalCounter;

                String ratioString = String.valueOf(ratio);
                ratioString = ratioString.replace(".", ",");

                //System.out.println(ratioString);
            }

        }

        System.out.println(winCounter);
        System.out.println(totalCounter);
        System.out.println(winCounter / totalCounter);
    }

    private List<String> readTheFile(File file) throws Exception {
        List<String> textLines;
        try (Reader fileReader = new FileReader(file)) {
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = bufReader.readLine();

            textLines = new ArrayList<>();

            while (line != null) {
                textLines.add(line);
                line = bufReader.readLine();
            }

            bufReader.close();
            fileReader.close();
        }

        return textLines;
    }

//    public static void main(String[] args) throws Exception {
//        new Analysis().printBigPotShit();
//    }

    private void printBigPotShit() throws Exception {
        File file = new File("/Users/LennartMac/Documents/loglines/logs.txt");

        List<String> lines = readTheFile(file);

        String[] counterToRemember = new String[1];
        int[] lineCounter = new int[1];

        lines.forEach(line -> {
            if(line.contains("Counter:")) {
                counterToRemember[0] = line;
            }

            if(line.contains("really big pot")) {
                lineCounter[0]++;
                System.out.print(lineCounter[0] + ") ");
                System.out.print("big pot!");
                System.out.println(" " + counterToRemember[0]);
            }
        });
    }

//    public static void main(String[] args) throws Exception {
//        new Analysis().checkRiverBluffAmount();
//    }


    private void checkRiverBluffAmount() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry > 400000;");

        double bluffCounter = 0;
        double totalCounter = 0;

        while(rs.next()) {
            if(!rs.getString("board").equals("")) {
                if(rs.getString("bot_action").equals("bet75pct")) {
                    List<Card> board = convertCardStringToCardList(rs.getString("board"));

                    if(board.size() == 3) {
                        if(rs.getDouble("handstrength") < 0.5) {
                            bluffCounter++;
                        }

                        totalCounter++;
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println(bluffCounter);
        System.out.println(totalCounter);
        System.out.println(bluffCounter / totalCounter);
    }



    ///////

//    public static void main(String[] args) throws Exception {
//        new Analysis().ratioOverallMethod();
//    }

    private void ratioOverallMethod() throws Exception {
        List<Double> flopBluffBetRatios = getBluffBetRatios("flop");
        List<Double> turnBluffBetRatios = getBluffBetRatios("turn");
        List<Double> riverBluffBetRatios = getBluffBetRatios("river");
        //List<Double> allRatios = getAllBluffBetAndRaiseRatios();

        //allRatios.removeAll(flopBluffBetRatios);
        //allRatios.removeAll(turnBluffBetRatios);
        //allRatios.removeAll(riverBluffBetRatios);

        System.out.println("wacht");

        //uitkomst, je kunt gaan raisen vanaf ratio 0.5 om te balancen...
    }

    private List<Double> getBluffBetRatios(String street) throws Exception {
        File file = new File("/Users/LennartMac/Documents/historyanalysis/loggingForAnalysisNew.txt");

        List<String> lines = readTheFile(file);

        String lineToUse = "-";

        List<Double> allRatios = new ArrayList<>();

        for(String line : lines) {
            if(line.contains(street + " bluffbet opportunity")) {
                lineToUse = line;
            }

            if(line.contains("RATIO:") && lineToUse.contains(street)) {
                lineToUse = "--";
                //System.out.println(line);

                String realLine = line.substring(line.indexOf(" ") + 1, line.length());
                allRatios.add(Double.valueOf(realLine));
            }
        }

        Collections.sort(allRatios);

        return allRatios;
    }

    private List<Double> getAllBluffBetAndRaiseRatios() throws Exception {
        File file = new File("/Users/LennartMac/Documents/historyanalysis/efkeslogging.txt");

        List<String> lines = readTheFile(file);

        List<Double> allRatios = new ArrayList<>();

        for(String line : lines) {
            if(line.contains("RATIO:")) {
                String realLine = line.substring(line.indexOf(" ") + 1, line.length());
                allRatios.add(Double.valueOf(realLine));
            }
        }

        Collections.sort(allRatios);

        return allRatios;
    }


//    public static void main(String[] args) throws Exception {
//        new Analysis().analyseBetsAndBluffsPerPostStreet();
//    }

    private void analyseBetsAndBluffsPerPostStreet() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry > 479377;");

        double checkCounter = 0;
        double betCounter = 0;

        while(rs.next()) {
            if(!rs.getString("board").equals("")) {
                List<Card> board = convertCardStringToCardList(rs.getString("board"));

                if(board.size() == 3) {
                    if(rs.getString("bot_action").equals("check")) {
                        checkCounter++;
                    }

                    if(rs.getString("bot_action").equals("bet75pct")) {
                        betCounter++;
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println("checks: " + checkCounter);
        System.out.println("bets: " + betCounter);
        System.out.println("bet to total ratio: " + betCounter / (checkCounter + betCounter));
    }

//    public static void main(String[] args) throws Exception {
//        new Analysis().postflopRaiseAnalysis();
//
//        //515765
//
//
//        //real sept
//            //vanaf 502154
//            //tm 510154
//    }

    //raise shit
    private void postflopRaiseAnalysis() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw_2 WHERE entry >= 28000;");
        //ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry >= 502154 AND entry <= 510154;");
        //ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry >= 490873;");

        List<Double> allBotPostflopRaiseHs = new ArrayList<>();

        int showDownCounter = 0;
        int noShowdownCounter = 0;

        int botWonHandCounter = 0;
        int botLostHandCounter = 0;

        int botWonHandSdCounter = 0;
        int botLostHandSdCounter = 0;

        int botWonHandNoSdCounter = 0;
        int botLostHandNoSdCounter = 0;

        int hsBelow50 = 0;
        int hs50_80 = 0;
        int hs80plus = 0;
        int totalCounter = 0;

        while(rs.next()) {
            if(!rs.getString("board").equals("")) {
                if(rs.getString("bot_action").equals("raise")) {
                    //if(rs.getDouble("sizing") > 300) {
                        totalCounter++;
                        double botHs = rs.getDouble("handstrength");

                        if(botHs < 0.5) {
                            hsBelow50++;
                        } else if(botHs < 0.8) {
                            hs50_80++;
                            //System.out.println(botHs);
                        } else if(botHs >= 0.8) {
                            hs80plus++;
                            //System.out.println(botHs);
                        } else {
                            System.out.println("wtf");
                        }

                        if(rs.getString("showdown_occured").equals("true")) {
                            showDownCounter++;
                        }

                        if(rs.getString("showdown_occured").equals("false")) {
                            noShowdownCounter++;
                        }

                        if(rs.getString("bot_won_hand").equals("true")) {
                            botWonHandCounter++;

                            if(rs.getString("showdown_occured").equals("true")) {
                                botWonHandSdCounter++;
                            } else {
                                botWonHandNoSdCounter++;
                            }
                        }

                        if(rs.getString("bot_won_hand").equals("false")) {
                            botLostHandCounter++;

                            if(rs.getString("showdown_occured").equals("true")) {
                                botLostHandSdCounter++;
                            } else {
                                botLostHandNoSdCounter++;
                            }
                        }

                        allBotPostflopRaiseHs.add(rs.getDouble("handstrength"));
                    //}
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(allBotPostflopRaiseHs);

        //System.out.println("wacht");

        System.out.println("showdown: " + showDownCounter);
        System.out.println("no showdown: " + noShowdownCounter);

        System.out.println("bot wond hand: " + botWonHandCounter);
        System.out.println("bot lost hand: " + botLostHandCounter);

        System.out.println("bot won hand SD: " + botWonHandSdCounter);
        System.out.println("bot lost hand SD: " + botLostHandSdCounter);

        System.out.println("bot won hand no SD: " + botWonHandNoSdCounter);
        System.out.println("bot lost hand no SD: " + botLostHandNoSdCounter);

        System.out.println("hs below 50: " + hsBelow50);
        System.out.println("hs 50 - 80: " + hs50_80);
        System.out.println("hs 80+: " + hs80plus);
        System.out.println("total: " + totalCounter);
    }


    ///check is ff showdown big sizing bets postflop

//    public static void main(String[] args) throws Exception {
//        new Analysis().analyseHowOftenYouFoldAgainstBetsOnFlopAndTurnInPosition();
//    }

    private void checkShowdownBigBets() throws Exception {
        initializeDbConnection();

        //502151

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry >= 502151;");

        double totalCounter = 0;
        double showDownCounter = 0;
        double botWonHandCounter = 0;

        while(rs.next()) {
            if(!rs.getString("board").equals("")) {
                if(rs.getString("bot_action").equals("bet75pct")) {
                    //if(rs.getDouble("handstrength") >= 0.9) {
                    //if(rs.getDouble("bigblind") < 40) {
                        double sizing = rs.getDouble("sizing");
                        double pot = rs.getDouble("pot");

                        if(sizing / pot > 0.9) {
                            //totalCounter++;

                            if (rs.getString("showdown_occured").equals("true")) {
                                showDownCounter++;

                                if(rs.getString("bot_won_hand").equals("true")) {
                                    botWonHandCounter++;
                                }
                            }


                        }
                    //}
                    //}
                }
            }
        }

        rs.close();
        st.close();
        closeDbConnection();

        System.out.println("total: " + showDownCounter);
        System.out.println("showdown: " + botWonHandCounter);
        System.out.println("ratio: " + (botWonHandCounter / showDownCounter));

//        System.out.println();
//
//        System.out.println("total: " + totalCounter);
//        System.out.println("bot_won_hand: " + botWonHandCounter);
//        System.out.println("ratio: " + (botWonHandCounter / totalCounter));
    }

    private void analyseHowOftenYouFoldAgainstBetsOnFlopAndTurnInPosition() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry >= 502151;");

        double foldCounter = 0;
        double callCounter = 0;
        double raiseCounter = 0;
        double totalCounter = 0;


        while(rs.next()) {
            if(rs.getString("opponent_action").equals("bet75pct")) {
                if(rs.getDouble("handstrength") < 0.5) {
                    if(rs.getString("position").equals("Ip")) {
                        if(!rs.getString("board").equals("")) {
                            List<Card> board = convertCardStringToCardList(rs.getString("board"));

                            if(board.size() == 3) {
                                String botAction = rs.getString("bot_action");

                                if(botAction.equals("fold")) {
                                    foldCounter++;
                                }

                                if(botAction.equals("call")) {
                                    callCounter++;
                                }

                                if(botAction.equals("raise")) {
                                    raiseCounter++;
                                }

                                totalCounter++;
                            }
                        }
                    }
                }
            }
        }

        rs.close();
        st.close();
        closeDbConnection();

        System.out.println("fold: " + foldCounter);
        System.out.println("call: " + callCounter);
        System.out.println("raise: " + raiseCounter);
        System.out.println("total: " + totalCounter);
    }

//    public static void main(String[] args) throws Exception {
//        new Analysis().checkSizingVersusOppLimpPreflop();
//
//    }

    private void checkSizingVersusOppLimpPreflop() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry >= 502151;");

        while(rs.next()) {
            if(rs.getString("board").equals("")) {
                if(rs.getString("opponent_action").equals("call")) {
                    if(rs.getString("bot_action").equals("raise")) {
                        if(rs.getDouble("bigblind") == 50) {
                            System.out.println(rs.getDouble("sizing"));
                        }
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

//    public static void main(String[] args) throws Exception {
//        new Analysis().analysePreflopShoves();
//    }


    private void analysePreflopShoves() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw_2 WHERE entry >= 0;");

        int showDownCounter = 0;
        int noShowdownCounter = 0;

        int botWonHandCounter = 0;
        int botLostHandCounter = 0;

        int botWonHandSdCounter = 0;
        int botLostHandSdCounter = 0;

        int botWonHandNoSdCounter = 0;
        int botLostHandNoSdCounter = 0;

        while(rs.next()) {
            if(rs.getString("board").equals("")) {
                if(rs.getString("opponent_action").equals("bet") || rs.getString("opponent_action").equals("call")) {
                    //if(rs.getDouble("handstrength") < 0.5 && rs.getDouble("handstrength") < 0.5) {
                        if(rs.getString("bot_action").equals("raise")) {
                            if(rs.getDouble("sizing") >= 500) {
                                //System.out.println(rs.getInt("entry") - 560185);

                                if(rs.getString("showdown_occured").equals("true")) {
                                    showDownCounter++;
                                }

                                if(rs.getString("showdown_occured").equals("false")) {
                                    noShowdownCounter++;
                                }

                                if(rs.getString("bot_won_hand").equals("true")) {
                                    botWonHandCounter++;

                                    if(rs.getString("showdown_occured").equals("true")) {
                                        botWonHandSdCounter++;
                                    } else {
                                        botWonHandNoSdCounter++;
                                    }
                                }

                                if(rs.getString("bot_won_hand").equals("false")) {
                                    botLostHandCounter++;

                                    if(rs.getString("showdown_occured").equals("true")) {
                                        botLostHandSdCounter++;
                                    } else {
                                        botLostHandNoSdCounter++;
                                    }
                                }
                            }
                        }
                    //}
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println("showdown: " + showDownCounter);
        System.out.println("no showdown: " + noShowdownCounter);

        System.out.println("bot wond hand: " + botWonHandCounter);
        System.out.println("bot lost hand: " + botLostHandCounter);

        System.out.println("bot won hand SD: " + botWonHandSdCounter);
        System.out.println("bot lost hand SD: " + botLostHandSdCounter);

        System.out.println("bot won hand no SD: " + botWonHandNoSdCounter);
        System.out.println("bot lost hand no SD: " + botLostHandNoSdCounter);
    }

//    public static void main(String[] args) throws Exception {
//        new Analysis().getEntriesWithBoard(null);
//    }

    private void getEntriesWithBoard(String board) throws Exception {
        board = "14c13s8h6h";

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE board != \"\";");

        int counter = 0;

        while(rs.next()) {
            String boardFromDb = rs.getString("board");
            if(boardFromDb.contains("14") && boardFromDb.contains("13") && boardFromDb.contains("8") && boardFromDb.contains("6") && boardFromDb.contains("2")) {
                //boardFromDb = boardFromDb.replace("14c", "");
                //boardFromDb = boardFromDb.replace("13s", "");
                //boardFromDb = boardFromDb.replace("8h", "");
                //boardFromDb = boardFromDb.replace("6h", "");

                //if(boardFromDb.equals("")) {
                    //System.out.println(rs.getInt("entry"));
                counter++;
                //}
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println(counter);
    }

//    public static void main(String[] args) throws Exception {
//        new Analysis().postBetAnalysis();
//
//        //ratioOverallMethod
//    }

    private void postBetAnalysis() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE entry > 560000");

        double botWinCounter = 0;
        double totalCounter = 0;

        double showdownCounter = 0;

        double bluffCounter = 0;
        double valueCounter = 0;

        double betCounter = 0;
        double checkCounter = 0;

        while(rs.next()) {


            if(!rs.getString("board").equals("")) {
                if(rs.getString("bot_action").equals("check")) {
                    if(rs.getDouble("handstrength") < 0.5) {
                        checkCounter++;
                    }

                    //betCounter++;

//                    if(rs.getDouble("handstrength") < 0.5) {
//                        bluffCounter++;
//                    } else {
//                        valueCounter++;
//                    }


                    //if(rs.getDouble("handstrength") < 0.5) {
//                        if(rs.getString("showdown_occured").equals("false")) {
//                            showdownCounter++;
//
//                            if(rs.getString("bot_won_hand").equals("true")) {
//                                botWinCounter++;
//                            }
//
//                            totalCounter++;
//                        }


                    //}
                } else if(rs.getString("bot_action").equals("bet75pct")) {
                    if(rs.getDouble("handstrength") < 0.5) {
                        betCounter++;
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println("bet: " + betCounter);
        System.out.println("check: " + checkCounter);
        //System.out.println(botWinCounter / totalCounter);

        //System.out.println(showdownCounter);
    }



    //////
//    public static void main(String[] args) throws Exception {
//        new Analysis().analyseBotActionDuration();
//    }

    private void analyseBotActionDuration() throws Exception {
        File file = new File("/Users/LennartMac/Documents/loglines/bot_action_duration/today+prrt.txt");

        List<String> lines = readTheFile(file);
        List<Double> durations = new ArrayList<>();

        for(String line : lines) {
            if(line.contains("BOT ACTION DURATION")) {
                String strippedLine = line.substring(line.indexOf(":"), line.length());
                strippedLine = strippedLine.substring(strippedLine.indexOf(" "), strippedLine.indexOf("*"));
                strippedLine = strippedLine.replace(" ", "");
                durations.add(Double.valueOf(strippedLine));
            }
        }

        double total = 0;

        for(double duration : durations) {
            total = total + duration;
        }

        System.out.println(total / durations.size());
    }
}
