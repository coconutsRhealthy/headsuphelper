package com.lennart.model.action.actionbuilders.ai.dbstatsraw;

import com.lennart.model.action.actionbuilders.ai.GameFlow;
import com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0.DbSaveBluff_2_0;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 26/01/2019.
 */
public class Analysis {

    private Connection con;

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
}
