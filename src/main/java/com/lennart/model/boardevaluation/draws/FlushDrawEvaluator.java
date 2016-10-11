package com.lennart.model.boardevaluation.draws;

import com.lennart.model.boardevaluation.FlushEvaluator;
import com.lennart.model.pokergame.Card;

import java.util.*;

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

            if(getNumberOfSuitedCardsOnBoard(board) == 3) {
                Map<Integer, List<Card>> allFlushDrawCombos = getFlushDrawCombos(board);
                char flushSuit = getFlushSuitWhen3ToFlushOnBoard(board);
                List<Card> flushCardsOnBoard = getFlushCardsOnBoard(board, flushSuit);
                int numberOfRanksNeeded = 2;
                List<Integer> highestRanksNotPresentOnBoard = getNeededRanksNotPresentOnFlushBoard(flushCardsOnBoard,
                        numberOfRanksNeeded, "high");

                for (Map.Entry<Integer, List<Card>> entry : allFlushDrawCombos.entrySet()) {
                    Card c = getHighestFlushCardFromCombo(entry.getValue(), flushSuit);
                    if(highestRanksNotPresentOnBoard.contains(Integer.valueOf(c.getRank()))) {
                        strongFlushDrawCombos.put(strongFlushDrawCombos.size(), entry.getValue());
                    }
                }
                return strongFlushDrawCombos;
            }
        }
        return new HashMap<>();
    }

    public Map<Integer, List<Card>> getMediumFlushDrawCombos(List<Card> board) {
        Map<Integer, List<Card>> mediumFlushDrawCombos = new HashMap<>();

        //max 1 pair
        if(getNumberOfPairsOnBoard(board) < 2 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            if (getNumberOfSuitedCardsOnBoard(board) == 3) {
                Map<Integer, List<Card>> allFlushDrawCombos = getFlushDrawCombos(board);
                char flushSuit = getFlushSuitWhen3ToFlushOnBoard(board);
                List<Card> flushCardsOnBoard = getFlushCardsOnBoard(board, flushSuit);
                int numberOfRanksNeeded = 4;
                List<Integer> mediumRanksNotPresentOnBoard = getNeededRanksNotPresentOnFlushBoard(flushCardsOnBoard,
                        numberOfRanksNeeded, "medium");

                for (Map.Entry<Integer, List<Card>> entry : allFlushDrawCombos.entrySet()) {
                    Card c = getHighestFlushCardFromCombo(entry.getValue(), flushSuit);
                    if(mediumRanksNotPresentOnBoard.contains(Integer.valueOf(c.getRank()))) {
                        mediumFlushDrawCombos.put(mediumFlushDrawCombos.size(), entry.getValue());
                    }
                }
                return mediumFlushDrawCombos;
            }
        }

        //twee pair
        if(getNumberOfPairsOnBoard(board) == 2 && getNumberOfSuitedCardsOnBoard(board) == 2) {
            return getFlushDrawCombos(board);
        }

        return new HashMap<>();
    }

    public Map<Integer, Set<Card>> getWeakFlushDrawCombos(List<Card> board) {
        Map<Integer, List<Card>> allFlushDraws = getFlushDrawCombos(board);
        Map<Integer, List<Card>> strongFlushDraws = getStrongFlushDrawCombos(board);
        Map<Integer, List<Card>> mediumFlushDraws = getMediumFlushDrawCombos(board);

        return getWeakFlushOrBackDoorFlushDrawCombos(allFlushDraws, strongFlushDraws, mediumFlushDraws);
    }

    public Map<Integer, List<Card>> getStrongBackDoorFlushCombos(List<Card> board) {
        return getStrongOrMediumBackDoorFlushCombos(board, "strong");
    }

    public Map<Integer, List<Card>> getMediumBackDoorFlushCombos(List<Card> board) {
        return getStrongOrMediumBackDoorFlushCombos(board, "medium");
    }

    public Map<Integer, Set<Card>> getWeakBackDoorFlushCombos(List<Card> board) {
        Map<Integer, List<Card>> allBackDoorFlushDraws = getBackDoorFlushDrawCombos(board);
        Map<Integer, List<Card>> strongBackDoorDraws = getStrongBackDoorFlushCombos(board);
        Map<Integer, List<Card>> mediumBackDoorDraws = getMediumBackDoorFlushCombos(board);

        return getWeakFlushOrBackDoorFlushDrawCombos(allBackDoorFlushDraws, strongBackDoorDraws, mediumBackDoorDraws);
    }

    //helper methods
    private Map<Integer, Set<Card>> getWeakFlushOrBackDoorFlushDrawCombos(Map<Integer, List<Card>> allDraws,
                                                                         Map<Integer, List<Card>> strongDraws,
                                                                         Map<Integer, List<Card>> mediumDraws) {
        Map<Integer, Set<Card>> weakDraws = new HashMap<>();

        Set<Set<Card>> allDrawsSet = new HashSet<>();
        Set<Set<Card>> strongDrawsSet = new HashSet<>();
        Set<Set<Card>> mediumDrawsSet = new HashSet<>();

        for (Map.Entry<Integer, List<Card>> entry : allDraws.entrySet()) {
            Set<Card> flushCombo = new HashSet<>();
            flushCombo.addAll(entry.getValue());
            allDrawsSet.add(flushCombo);
        }

        for (Map.Entry<Integer, List<Card>> entry : strongDraws.entrySet()) {
            Set<Card> flushCombo = new HashSet<>();
            flushCombo.addAll(entry.getValue());
            strongDrawsSet.add(flushCombo);
        }

        for (Map.Entry<Integer, List<Card>> entry : mediumDraws.entrySet()) {
            Set<Card> flushCombo = new HashSet<>();
            flushCombo.addAll(entry.getValue());
            mediumDrawsSet.add(flushCombo);
        }

        allDrawsSet.removeAll(strongDrawsSet);
        allDrawsSet.removeAll(mediumDrawsSet);

        for(Set s : allDrawsSet) {
            weakDraws.put(weakDraws.size(), s);
        }

        return weakDraws;
    }

    private Map<Integer, List<Card>> getStrongOrMediumBackDoorFlushCombos(List<Card> board, String strongOrMedium) {
        if(strongOrMedium.equals("strong")) {
            if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board)) {
                if(getNumberOfSuitedCardsOnBoard(board) < 2) {
                    return getBackDoorFlushDrawCombos(board);
                }
            }
        }

        if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board)) {
            if(getNumberOfSuitedCardsOnBoard(board) == 2) {
                Map<Integer, List<Card>> strongOrMediumBackDoorCombos = new HashMap<>();
                Map<Integer, List<Card>> allBackDoorCombos = getBackDoorFlushDrawCombos(board);

                Map<Character, List<Card>> suitsOfBoard = getSuitsOfBoard(board);
                List<Card> backDoorFlushCardsOnBoard = new ArrayList<>();

                for (Map.Entry<Character, List<Card>> entry : suitsOfBoard.entrySet()) {
                    if(entry.getValue().size() == 2) {
                        backDoorFlushCardsOnBoard = entry.getValue();
                    }
                }

                char flushSuit = backDoorFlushCardsOnBoard.get(0).getSuit();

                int numberOfRanksNeeded = 0;
                List<Integer> neededRanksNotPresentOnBoard = new ArrayList<>();
                if(strongOrMedium.equals("strong")) {
                    numberOfRanksNeeded = 2;
                    neededRanksNotPresentOnBoard = getNeededRanksNotPresentOnFlushBoard(backDoorFlushCardsOnBoard,
                            numberOfRanksNeeded, "high");
                }
                if(strongOrMedium.equals("medium")) {
                    numberOfRanksNeeded = 4;
                    neededRanksNotPresentOnBoard = getNeededRanksNotPresentOnFlushBoard(backDoorFlushCardsOnBoard,
                            numberOfRanksNeeded, "medium");
                }

                for (Map.Entry<Integer, List<Card>> entry : allBackDoorCombos.entrySet()) {
                    Card c = getHighestFlushCardFromCombo(entry.getValue(), flushSuit);
                    if(neededRanksNotPresentOnBoard.contains(Integer.valueOf(c.getRank()))) {
                        strongOrMediumBackDoorCombos.put(strongOrMediumBackDoorCombos.size(), entry.getValue());
                    }
                }
                return strongOrMediumBackDoorCombos;
            }
        }
        return new HashMap<>();
    }

    private List<Integer> getNeededRanksNotPresentOnFlushBoard(List<Card> flushCardsOnBoard, int numberOfRanksNeeded,
                                                                String highMediumOrLow) {
        List<Integer> flushCardsOnBoardRankOnly = getSortedCardRanksFromCardList(flushCardsOnBoard);
        List<Integer> allPossibleCardRanksSorted = new ArrayList<>();
        for(int i = 14; i > 1; i--) {
            allPossibleCardRanksSorted.add(i);
        }

        allPossibleCardRanksSorted.removeAll(flushCardsOnBoardRankOnly);
        List<Integer> cardRanksToReturn = new ArrayList<>();

        if(highMediumOrLow.equals("high")) {
            for(int i = 0; i < numberOfRanksNeeded; i++) {
                cardRanksToReturn.add(allPossibleCardRanksSorted.get(i));
            }
        }
        if(highMediumOrLow.equals("medium")) {
            for(int i = 2; i < (numberOfRanksNeeded + 2); i++) {
                cardRanksToReturn.add(allPossibleCardRanksSorted.get(i));
            }
        }
        return cardRanksToReturn;
    }
}
