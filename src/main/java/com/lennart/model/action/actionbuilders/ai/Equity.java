package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.*;
import java.util.List;

/**
 * Created by LennartMac on 16/06/2018.
 */
public class Equity {

    List<Card> deck = BoardEvaluator.getCompleteCardDeck();


    //deel jezelf een hand en een flop

    //als je een oosd, fd of stronggutshot hebt, doe dan equity analyse

    //bekijk welke range je hebt voor 80% vd oosd qua boven 90










    //FLOP
        //oosd                  74
        //fd
        //gutshot
        //solid bottom pair
        //solid middle pair



    public static void main(String[] args) {
        new Equity().theMethod();
    }

    private void theMethod2() {
        Map<String, List<Integer>> scoreMap = new HashMap<>();

        scoreMap.put("oosd", new ArrayList<>());
        scoreMap.put("fd", new ArrayList<>());
        scoreMap.put("gutshot", new ArrayList<>());

        int oosdCounter = 0;
        int fdCounter = 0;
        int gutshotCounter = 0;

        while(oosdCounter < 50 || fdCounter < 50 || gutshotCounter < 50) {
            deck = BoardEvaluator.getCompleteCardDeck();

            List<Card> holeCards = new ArrayList<>();
            List<Card> board = new ArrayList<>();

            holeCards.add(getAndRemoveRandomCardFromDeck());
            holeCards.add(getAndRemoveRandomCardFromDeck());

            board.add(getAndRemoveRandomCardFromDeck());
            board.add(getAndRemoveRandomCardFromDeck());
            board.add(getAndRemoveRandomCardFromDeck());
            board.add(getAndRemoveRandomCardFromDeck());

            BoardEvaluator boardEvaluator = new BoardEvaluator(board);
            HandEvaluator handEvaluator = new HandEvaluator(holeCards, boardEvaluator);

            double handstrength = handEvaluator.getHandStrength(holeCards);

            if(handstrength > 0.5) {
                continue;
            }

            boolean oosd = handEvaluator.hasDrawOfType("strongOosd");
            boolean fd = handEvaluator.hasDrawOfType("strongFlushDraw");
            boolean gutshot = handEvaluator.hasDrawOfType("strongGutshot");

            if((oosd && oosdCounter < 50 && !fd && !gutshot) || (fd && fdCounter < 50 && !oosd && !gutshot)
                    || (gutshot && gutshotCounter < 50 && !oosd && !fd)) {
                List<Double> hsList = getHandstrengthAtRiverList(board, holeCards, 25);

                if(oosd) {
                    oosdCounter++;
                    int numberOfScoresAbove95 = getNumberOfScoresAbove95(hsList);
                    scoreMap.get("oosd").add(numberOfScoresAbove95);

                    System.out.println("oosdcounter: " + oosdCounter);
                    moveMouseToLocation(270, 270);
                    click(270, 270);
                    moveMouseToLocation(20, 20);
                }

                if(fd) {
                    fdCounter++;
                    int numberOfScoresAbove95 = getNumberOfScoresAbove95(hsList);
                    scoreMap.get("fd").add(numberOfScoresAbove95);

                    System.out.println("fdcounter: " + fdCounter);
                    moveMouseToLocation(270, 270);
                    click(270, 270);
                    moveMouseToLocation(20, 20);
                }

                if(gutshot) {
                    gutshotCounter++;
                    int numberOfScoresAbove95 = getNumberOfScoresAbove95(hsList);
                    scoreMap.get("gutshot").add(numberOfScoresAbove95);

                    System.out.println("gutshotCounter: " + gutshotCounter);
                    moveMouseToLocation(270, 270);
                    click(270, 270);
                    moveMouseToLocation(20, 20);
                }
            }
        }

        List<Integer> oosdList = scoreMap.get("oosd");
        List<Integer> fdList = scoreMap.get("fd");
        List<Integer> gutshotList = scoreMap.get("gutshot");

        Collections.sort(oosdList, Collections.reverseOrder());
        Collections.sort(fdList, Collections.reverseOrder());
        Collections.sort(gutshotList, Collections.reverseOrder());

        System.out.println("OOSD:");
        System.out.println("average: " + getAverageFormList(oosdList));
//        for(int i = 0; i < oosdList.size(); i++) {
//            System.out.println(i + "         " + oosdList.get(i));
//        }

        System.out.println();
        System.out.println();
        System.out.println("FD:");
        System.out.println("average: " + getAverageFormList(fdList));
//        for(int i = 0; i < oosdList.size(); i++) {
//            System.out.println(i + "         " + fdList.get(i));
//        }

        System.out.println();
        System.out.println();
        System.out.println("gutshot:");
        System.out.println("average: " + getAverageFormList(gutshotList));
//        for(int i = 0; i < oosdList.size(); i++) {
//            System.out.println(i + "         " + gutshotList.get(i));
//        }
    }


    private void theMethod() {
        //for(int i = 0; i < 10; i++) {
            List<Card> board = new ArrayList<>();

            board.add(new Card(7, 'd'));
            board.add(new Card(8, 's'));
            board.add(new Card(3, 'c'));
            //board.add(new Card(2, 'd'));

            List<Card> holeCards = new ArrayList<>();

            holeCards.add(new Card(7, 'c'));
            holeCards.add(new Card(8, 'h'));

            List<Double> hsList = getHandstrengthAtRiverList(board, holeCards, 25);

            System.out.println("number of scores above 90: " + getNumberOfScoresAbove95(hsList));
        //}
    }


    public List<Double> getHandstrengthAtRiverList(List<Card> board, List<Card> holeCards, int iterations) {
        List<Double> hsAtRiverList = new ArrayList<>();
        List<Card> boardCopy = new ArrayList<>();
        boardCopy.addAll(board);

        for(int i = 0; i < iterations; i++) {
            //System.out.println(i);
            deck.removeAll(board);
            deck.removeAll(holeCards);
            getAndRemoveRandomCardFromDeck();
            getAndRemoveRandomCardFromDeck();

            while(board.size() < 5) {
                board.add(getAndRemoveRandomCardFromDeck());
            }

            BoardEvaluator boardEvaluator = new BoardEvaluator(board);
            HandEvaluator handEvaluator = new HandEvaluator(holeCards, boardEvaluator);
            double handStrength = handEvaluator.getHandStrength(holeCards);
            hsAtRiverList.add(handStrength);

            deck = BoardEvaluator.getCompleteCardDeck();
            board.clear();
            board.addAll(boardCopy);
        }

        Collections.sort(hsAtRiverList, Collections.reverseOrder());

        return hsAtRiverList;
    }

    private int getNumberOfScoresAbove95(List<Double> scoreList) {
        int counter = 0;

        for(Double d : scoreList) {
            if(d > 0.90) {
                counter++;
            }
        }

        return counter;
    }

    private Card getAndRemoveRandomCardFromDeck() {
        Random randomGenerator = new Random();
        int random = randomGenerator.nextInt(deck.size());
        Card cardToReturn = deck.get(random);
        deck.remove(random);

        return cardToReturn;
    }

    public static void moveMouseToLocation(int x, int y) {
        try {
            Robot bot = new Robot();
            bot.mouseMove(x, y);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void click(int x, int y) {
        try {
            Robot bot = new Robot();
            bot.mouseMove(x, y);
            bot.mousePress(InputEvent.BUTTON1_MASK);
            bot.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public double getAverageFormList(List<Integer> list) {
        double avarage;

        double total = 0;

        for(int i : list) {
            total = total + i;
        }

        avarage = total / list.size();
        return avarage;
    }
}
