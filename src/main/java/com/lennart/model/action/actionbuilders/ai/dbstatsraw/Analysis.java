package com.lennart.model.action.actionbuilders.ai.dbstatsraw;

import com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0.DbSaveBluff_2_0;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 26/01/2019.
 */
public class Analysis {

    private Connection con;

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
                        List<Card> boardRiver = convertBoardStringToCardList(board);

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

    private List<Card> convertBoardStringToCardList(String board) {
        List<Card> boardCardList = new ArrayList<>();

        String boardCopy = board;
        String boardCopy2 = board;

        boardCopy = boardCopy.replaceAll("c", "d");
        boardCopy = boardCopy.replaceAll("s", "d");
        boardCopy = boardCopy.replaceAll("h", "d");

        String[] ranks = boardCopy.split("d");

        String onlySuits = boardCopy2.replaceAll("\\d","");

        char[] suits = onlySuits.toCharArray();

        for(int i = 0; i < 4; i++) {
            Card card = new Card(Integer.valueOf(ranks[i]), suits[i]);
            boardCardList.add(card);
        }

        return boardCardList;
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
