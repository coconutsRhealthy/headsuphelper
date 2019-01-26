package com.lennart.model.action.actionbuilders.ai.dbstatsraw;

import com.lennart.model.action.actionbuilders.ai.dbsave.dbsave2_0.DbSaveBluff_2_0;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 26/01/2019.
 */
public class Analysis {

    private Connection con;

    private void boardWetnessBluffTestMethod() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw;");

        int total = 0;
        int success = 0;
        int counter = 0;

        DbStatsRawMigrator dbStatsRawMigrator = new DbStatsRawMigrator();

        while(rs.next()) {
            String board = rs.getString("board");

            if(!board.equals("") && dbStatsRawMigrator.getStreetString(board).equals("Turn")) {
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }



}
