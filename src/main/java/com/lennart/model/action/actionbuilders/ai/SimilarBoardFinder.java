package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbstatsraw.Analysis;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.sun.jdi.IntegerValue;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LennartMac on 07/11/2020.
 */
public class SimilarBoardFinder {

    private Connection con;

    //zelfde hoeveelheid pairs
    //zelfde hoeveelheid suits
    //waarde per kaart mag 1 afwijken

    public static void main(String[] args) throws Exception {
        new SimilarBoardFinder().findBoards();
    }

    private void findBoards() throws Exception {
        List<Card> currentBoard = Arrays.asList(new Card(2, 'd'), new Card(2, 's'), new Card(2, 'h'), new Card(6, 's'), new Card(7, 'd'));

        BoardEvaluator boardEvaluator = new BoardEvaluator();

        int numberOfPairs = boardEvaluator.getNumberOfPairsOnBoard(currentBoard);
        int numberOfSuitedCards = boardEvaluator.getNumberOfSuitedCardsOnBoard(currentBoard);

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw WHERE board != \"\";");

        int counter = 0;

        while(rs.next()) {
            List<Card> boardFromDb = convertCardStringToCardList(rs.getString("board"));

            if(currentBoard.size() == boardFromDb.size()) {
                int numberOfPairsFromDb = boardEvaluator.getNumberOfPairsOnBoard(boardFromDb);
                int numberOfSuitedCardsFromDb = boardEvaluator.getNumberOfSuitedCardsOnBoard(boardFromDb);

                if(numberOfPairsFromDb == numberOfPairs && numberOfSuitedCardsFromDb == numberOfSuitedCards) {
                    if(boardHasSimilarRanks(currentBoard, boardFromDb, boardEvaluator)) {
                        counter++;
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println(counter);
    }

    private boolean boardHasSimilarRanks(List<Card> currenBoard, List<Card> boardFromDb, BoardEvaluator boardEvaluator) {
        boolean hasSimilarRanks = false;

        List<Integer> cardRanksCurrentBoard = boardEvaluator.getSortedCardRanksFromCardList(currenBoard);
        List<Integer> cardRanksBoardFromDb = boardEvaluator.getSortedCardRanksFromCardList(boardFromDb);

        for(Integer rank : cardRanksCurrentBoard) {
            if(cardRanksBoardFromDb.contains(rank) || cardRanksBoardFromDb.contains(rank + 1) || cardRanksBoardFromDb.contains(rank - 1)) {
                if(cardRanksBoardFromDb.contains(rank)) {
                    hasSimilarRanks = true;
                    cardRanksBoardFromDb.remove(Integer.valueOf(rank));
                }

                if(cardRanksBoardFromDb.contains(rank + 1)) {
                    hasSimilarRanks = true;
                    cardRanksBoardFromDb.remove(Integer.valueOf(rank + 1));
                }

                if(cardRanksBoardFromDb.contains(rank - 1)) {
                    hasSimilarRanks = true;
                    cardRanksBoardFromDb.remove(Integer.valueOf(rank - 1));
                }
            } else {
                hasSimilarRanks = false;
                break;
            }
        }

//        if(hasSimilarRanks) {
//            System.out.println("wacht");
//        }

        return hasSimilarRanks;
    }

    private List<Card> convertCardStringToCardList(String board) {
        if(board.contains("x")) {
            return new ArrayList<>();
        }

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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

}
