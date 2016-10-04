package com.lennart.model.boardevaluation.draws;

import com.lennart.model.boardevaluation.FlushEvaluator;
import com.lennart.model.pokergame.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LPO10346 on 10/4/2016.
 */
public class FlushDrawEvaluator extends FlushEvaluator {
    public Map<Integer, List<Card>> getFlushDrawCombos (List<Card> board) {
        Map<Integer, List<Card>> flushDrawCombos = new HashMap<>();
        Map<Character, List<Card>> suitsOfBoard = getSuitsOfBoard(board);

        if(board.size() == 5) {
            return flushDrawCombos;
        }

        char flushSuit = 'x';
        char flushSuit2 = 'x';
        for (Map.Entry<Character, List<Card>> entry : suitsOfBoard.entrySet()) {
            if(entry.getValue().size() == 2) {
                if(flushSuit == 'x') {
                    flushSuit = entry.getValue().get(0).getSuit();
                } else {
                    flushSuit2 = entry.getValue().get(0).getSuit();
                }
            }
        }

        if(flushSuit != 'x' && flushSuit2 == 'x') {
            flushDrawCombos = getAllPossibleSuitedStartHands(flushSuit);
            flushDrawCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushDrawCombos, board);
            return flushDrawCombos;
        }

        if(flushSuit != 'x' && flushSuit2 != 'x') {
            flushDrawCombos = getAllPossibleSuitedStartHands(flushSuit);
            flushDrawCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushDrawCombos, board);

            Map<Integer, List<Card>> flushDrawCombos2;
            flushDrawCombos2 = getAllPossibleSuitedStartHands(flushSuit2);
            flushDrawCombos2 = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushDrawCombos2, board);

            for (Map.Entry<Integer, List<Card>> entry : flushDrawCombos2.entrySet()) {
                flushDrawCombos.put(flushDrawCombos.size(), entry.getValue());
            }

            return flushDrawCombos;
        }

        boolean threeToFlushOnBoard = false;
        for (Map.Entry<Character, List<Card>> entry : suitsOfBoard.entrySet()) {
            if(entry.getValue().size() == 3) {
                flushSuit = entry.getValue().get(0).getSuit();
                threeToFlushOnBoard = true;
            }
        }

        if(threeToFlushOnBoard) {
            Map<Integer, List<Card>> allStartHands = getAllPossibleStartHands();
            for (Map.Entry<Integer, List<Card>> entry : allStartHands.entrySet()) {
                if(entry.getValue().get(0).getSuit() == flushSuit && entry.getValue().get(1).getSuit() != flushSuit) {
                    flushDrawCombos.put(flushDrawCombos.size(), entry.getValue());
                } else if (entry.getValue().get(0).getSuit() != flushSuit && entry.getValue().get(1).getSuit() == flushSuit) {
                    flushDrawCombos.put(flushDrawCombos.size(), entry.getValue());
                }
            }
            flushDrawCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushDrawCombos, board);
        }
        return flushDrawCombos;
    }

    public Map<Integer, List<Card>> getBackDoorFlushDrawCombos(List<Card> board) {
        Map<Integer, List<Card>> backDoorFlushDrawCombos = new HashMap<>();

        if(board.size() > 3) {
            return backDoorFlushDrawCombos;
        }

        Map<Character, List<Card>> suitsOfBoard = getSuitsOfBoard(board);

        char flushSuitTwoOfSameSuitOnBoard;
        char flushSuitRainbow1 = 'x';
        char flushSuitRainbow2 = 'x';
        char flushSuitRainbow3;

        Map<Integer, List<Card>> allPossibleSuitedStartHands = new HashMap<>();
        Map<Integer, List<Card>> allStartHandsThatContainFlushSuitRainbow1 = new HashMap<>();
        Map<Integer, List<Card>> allStartHandsThatContainFlushSuitRainbow2 = new HashMap<>();
        Map<Integer, List<Card>> allStartHandsThatContainFlushSuitRainbow3 = new HashMap<>();

        for (Map.Entry<Character, List<Card>> entry : suitsOfBoard.entrySet()) {
            if(entry.getValue().size() == 1) {
                if(flushSuitRainbow1 == 'x') {
                    flushSuitRainbow1 = entry.getValue().get(0).getSuit();
                    allStartHandsThatContainFlushSuitRainbow1 = getAllPossibleSuitedStartHands(flushSuitRainbow1);
                } else if (flushSuitRainbow2 == 'x') {
                    flushSuitRainbow2 = entry.getValue().get(0).getSuit();
                    allStartHandsThatContainFlushSuitRainbow2 = getAllPossibleSuitedStartHands(flushSuitRainbow2);
                } else {
                    flushSuitRainbow3 = entry.getValue().get(0).getSuit();
                    allStartHandsThatContainFlushSuitRainbow3 = getAllPossibleSuitedStartHands(flushSuitRainbow3);
                }
            } else if (entry.getValue().size() == 2) {
                flushSuitTwoOfSameSuitOnBoard = entry.getValue().get(0).getSuit();
                allPossibleSuitedStartHands = getAllNonSuitedStartHandsThatContainASpecificSuit(flushSuitTwoOfSameSuitOnBoard);
            }
        }

        for (Map.Entry<Integer, List<Card>> entry : allPossibleSuitedStartHands.entrySet()) {
            backDoorFlushDrawCombos.put(backDoorFlushDrawCombos.size(), entry.getValue());
        }
        for (Map.Entry<Integer, List<Card>> entry : allStartHandsThatContainFlushSuitRainbow1.entrySet()) {
            backDoorFlushDrawCombos.put(backDoorFlushDrawCombos.size(), entry.getValue());
        }
        for (Map.Entry<Integer, List<Card>> entry : allStartHandsThatContainFlushSuitRainbow2.entrySet()) {
            backDoorFlushDrawCombos.put(backDoorFlushDrawCombos.size(), entry.getValue());
        }
        for (Map.Entry<Integer, List<Card>> entry : allStartHandsThatContainFlushSuitRainbow3.entrySet()) {
            backDoorFlushDrawCombos.put(backDoorFlushDrawCombos.size(), entry.getValue());
        }

        backDoorFlushDrawCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(backDoorFlushDrawCombos, board);
        return backDoorFlushDrawCombos;
    }

    public Map<Integer, List<Card>> getStrongFlushDrawCombos(List<Card> board) {
        Map<Integer, List<Card>> strongFlushDrawCombos = new HashMap<>();

        if(getNumberOfPairsOnBoard(board) < 2 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            if(getNumberOfSuitedCardsOnBoard(board) == 2) {
                return getFlushDrawCombos(board);
            }

            if(getNumberOfSuitedCardsOnBoard(board) > 2) {
                Map<Integer, List<Card>> allFlushDrawCombos = getFlushDrawCombos(board);

                char flushSuit = 'x';
                for (Map.Entry<Character, List<Card>> entry : getSuitsOfBoard(board).entrySet()) {
                    if(entry.getValue().size() > 2) {
                        flushSuit = entry.getValue().get(0).getSuit();
                    }
                }

                for (Map.Entry<Integer, List<Card>> entry : allFlushDrawCombos.entrySet()) {
                    Card c = getHighestFlushCardFromCombo(entry.getValue(), flushSuit);

                    if(c.getRank() >= 13) {
                        strongFlushDrawCombos.put(strongFlushDrawCombos.size(), entry.getValue());
                    }
                }
                return strongFlushDrawCombos;
            }
        }
        return new HashMap<>();
    }

    public Map<Integer, List<Card>> getMediumFlushDrawCombos(List<Card> board) {
        return null;
    }

    public Map<Integer, List<Card>> getWeakFlushDrawCombos(List<Card> board) {
        return null;
    }
}
