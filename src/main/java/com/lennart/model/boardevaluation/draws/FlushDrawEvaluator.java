package com.lennart.model.boardevaluation.draws;

import com.lennart.model.boardevaluation.FlushEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.pokergame.Game;

import java.util.*;

/**
 * Created by LPO10346 on 10/4/2016.
 */
public class FlushDrawEvaluator extends FlushEvaluator {

    private static Map<Integer, List<Card>> allFlushDraws;
    private static Map<Integer, Set<Card>> allFlushDrawsFlop;
    private static Map<Integer, Set<Card>> allFlushDrawsTurn;

    public Map<Integer, Set<Card>> getStrongFlushDrawCombos (List<Card> board) {
        Map<Integer, Set<Card>> strongFlushDrawCombos = new HashMap<>();
        Map<Integer, List<Card>> strongFlushDrawCombosAsMapList = getStrongFlushDrawCombosAsMapList(board);

        for (Map.Entry<Integer, List<Card>> entry : strongFlushDrawCombosAsMapList.entrySet()) {
            Set<Card> comboAsSet = new HashSet<>();
            comboAsSet.addAll(entry.getValue());
            strongFlushDrawCombos.put(strongFlushDrawCombos.size(), comboAsSet);
        }
        return strongFlushDrawCombos;
    }

    public Map<Integer, Set<Card>> getMediumFlushDrawCombos (List<Card> board) {
        Map<Integer, Set<Card>> mediumFlushDrawCombos = new HashMap<>();
        Map<Integer, List<Card>> mediumFlushDrawCombosAsMapList = getMediumFlushDrawCombosAsMapList(board);

        for (Map.Entry<Integer, List<Card>> entry : mediumFlushDrawCombosAsMapList.entrySet()) {
            Set<Card> comboAsSet = new HashSet<>();
            comboAsSet.addAll(entry.getValue());
            mediumFlushDrawCombos.put(mediumFlushDrawCombos.size(), comboAsSet);
        }
        return mediumFlushDrawCombos;
    }

    public Map<Integer, Set<Card>> getWeakFlushDrawCombos(List<Card> board) {
        Map<Integer, List<Card>> allFlushDraws = getFlushDrawCombos(board);
        Map<Integer, List<Card>> strongFlushDraws = getStrongFlushDrawCombosAsMapList(board);
        Map<Integer, List<Card>> mediumFlushDraws = getMediumFlushDrawCombosAsMapList(board);

        return getWeakFlushOrBackDoorFlushDrawCombos(allFlushDraws, strongFlushDraws, mediumFlushDraws);
    }

    public Map<Integer, Set<Card>> getStrongBackDoorFlushCombos (List<Card> board) {
        Map<Integer, Set<Card>> strongBackDoorFlushDrawCombos = new HashMap<>();
        Map<Integer, List<Card>> strongBackDoorFlushCombosAsMapList = getStrongBackDoorFlushCombosAsMapList(board);

        for (Map.Entry<Integer, List<Card>> entry : strongBackDoorFlushCombosAsMapList.entrySet()) {
            Set<Card> comboAsSet = new HashSet<>();
            comboAsSet.addAll(entry.getValue());
            strongBackDoorFlushDrawCombos.put(strongBackDoorFlushDrawCombos.size(), comboAsSet);
        }
        return strongBackDoorFlushDrawCombos;
    }

    public Map<Integer, Set<Card>> getMediumBackDoorFlushCombos (List<Card> board) {
        Map<Integer, Set<Card>> mediumBackDoorFlushDrawCombos = new HashMap<>();
        Map<Integer, List<Card>> mediumBackDoorFlushCombosAsMapList = getMediumBackDoorFlushCombosAsMapList(board);

        for (Map.Entry<Integer, List<Card>> entry : mediumBackDoorFlushCombosAsMapList.entrySet()) {
            Set<Card> comboAsSet = new HashSet<>();
            comboAsSet.addAll(entry.getValue());
            mediumBackDoorFlushDrawCombos.put(mediumBackDoorFlushDrawCombos.size(), comboAsSet);
        }
        return mediumBackDoorFlushDrawCombos;
    }

    public Map<Integer, Set<Card>> getWeakBackDoorFlushCombos(List<Card> board) {
        Map<Integer, List<Card>> allBackDoorFlushDraws = getBackDoorFlushDrawCombos(board);
        Map<Integer, List<Card>> strongBackDoorDraws = getStrongBackDoorFlushCombosAsMapList(board);
        Map<Integer, List<Card>> mediumBackDoorDraws = getMediumBackDoorFlushCombosAsMapList(board);

        return getWeakFlushOrBackDoorFlushDrawCombos(allBackDoorFlushDraws, strongBackDoorDraws, mediumBackDoorDraws);
    }

    public Map<Integer, Set<Card>> getAllFlushDrawCombos() {
        Map<Integer, Set<Card>> allFlushDrawCombos = new HashMap<>();
        Map<Integer, List<Card>> flushDrawCombosAsList = getFlushDrawCombos(Game.getBoardCards());

        for (Map.Entry<Integer, List<Card>> entry : flushDrawCombosAsList.entrySet()) {
            Set<Card> combo = new HashSet<>();
            combo.addAll(entry.getValue());
            allFlushDrawCombos.put(allFlushDrawCombos.size(), combo);
        }

        setFlushDrawCombosPerStreet(allFlushDrawCombos);
        return allFlushDrawCombos;
    }

    //helper methods
    private Map<Integer, List<Card>> getFlushDrawCombos (List<Card> board) {
        if(FlushDrawEvaluator.allFlushDraws != null) {
            return FlushDrawEvaluator.allFlushDraws;
        }

        Map<Integer, List<Card>> flushDrawCombos = new HashMap<>();
        Map<Character, List<Card>> suitsOfBoard = getSuitsOfBoard(board);

        if(board.size() == 5) {
            FlushDrawEvaluator.allFlushDraws = flushDrawCombos;
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
            FlushDrawEvaluator.allFlushDraws = flushDrawCombos;
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

            FlushDrawEvaluator.allFlushDraws = flushDrawCombos;
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
            Map<Integer, List<Card>> allStartHands = getAllPossibleStartHandsNew();
            for (Map.Entry<Integer, List<Card>> entry : allStartHands.entrySet()) {
                if(entry.getValue().get(0).getSuit() == flushSuit && entry.getValue().get(1).getSuit() != flushSuit) {
                    flushDrawCombos.put(flushDrawCombos.size(), entry.getValue());
                } else if (entry.getValue().get(0).getSuit() != flushSuit && entry.getValue().get(1).getSuit() == flushSuit) {
                    flushDrawCombos.put(flushDrawCombos.size(), entry.getValue());
                }
            }
            flushDrawCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushDrawCombos, board);
        }
        FlushDrawEvaluator.allFlushDraws = flushDrawCombos;
        return flushDrawCombos;
    }

    private Map<Integer, List<Card>> getBackDoorFlushDrawCombos(List<Card> board) {
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

    private Map<Integer, List<Card>> getStrongFlushDrawCombosAsMapList(List<Card> board) {
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

    private Map<Integer, List<Card>> getMediumFlushDrawCombosAsMapList(List<Card> board) {
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

    private Map<Integer, List<Card>> getStrongBackDoorFlushCombosAsMapList(List<Card> board) {
        return getStrongOrMediumBackDoorFlushCombos(board, "strong");
    }

    private Map<Integer, List<Card>> getMediumBackDoorFlushCombosAsMapList(List<Card> board) {
        return getStrongOrMediumBackDoorFlushCombos(board, "medium");
    }

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

    public static void setAllFlushDraws(Map<Integer, List<Card>> allFlushDraws) {
        FlushDrawEvaluator.allFlushDraws = allFlushDraws;
    }

    private void setFlushDrawCombosPerStreet(Map<Integer, Set<Card>> flushDrawCombos) {
        if(Game.getStreet().equals("Flop") && FlushDrawEvaluator.allFlushDrawsFlop == null) {
            FlushDrawEvaluator.allFlushDrawsFlop = flushDrawCombos;
        } else if(Game.getStreet().equals("Turn") && FlushDrawEvaluator.allFlushDrawsTurn == null) {
            FlushDrawEvaluator.allFlushDrawsTurn = flushDrawCombos;
        }
    }

    public static Map<Integer, Set<Card>> getAllFlushDrawsFlop() {
        return FlushDrawEvaluator.allFlushDrawsFlop;
    }

    public static Map<Integer, Set<Card>> getAllFlushDrawsTurn() {
        return FlushDrawEvaluator.allFlushDrawsTurn;
    }
}
