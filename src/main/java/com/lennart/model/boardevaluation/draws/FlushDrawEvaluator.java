package com.lennart.model.boardevaluation.draws;

import com.lennart.model.boardevaluation.FlushEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.*;

/**
 * Created by LPO10346 on 10/4/2016.
 */
public class FlushDrawEvaluator extends FlushEvaluator {

    private List<Card> board;

    private Map<Integer, Set<Card>> strongFlushDrawCombos;
    private Map<Integer, Set<Card>> mediumFlushDrawCombos;
    private Map<Integer, Set<Card>> weakFlushDrawCombos;
    private Map<Integer, Set<Card>> strongBackDoorFlushCombos;
    private Map<Integer, Set<Card>> mediumBackDoorFlushCombos;
    private Map<Integer, Set<Card>> weakBackDoorFlushCombos;

    private Map<Integer, Set<Card>> allFlushDrawsFlop;
    private Map<Integer, Set<Card>> allFlushDrawsTurn;

    public FlushDrawEvaluator(List<Card> board) {
        this.board = board;

        final Map<Integer, List<Card>> allFlushDraws = getFlushDrawCombos(board);
        final Map<Integer, List<Card>> strongFlushDrawCombosAsMapList = getStrongFlushDrawCombosAsMapList(allFlushDraws);
        final Map<Integer, List<Card>> mediumFlushDrawCombosAsMapList = getMediumFlushDrawCombosAsMapList(allFlushDraws);
        final Map<Integer, List<Card>> allBackDoorFlushDrawCombos = getBackDoorFlushDrawCombos();
        final Map<Integer, List<Card>> strongBackDoorFlushDrawCombosAsMapList = getStrongBackDoorFlushCombosAsMapList(allBackDoorFlushDrawCombos);
        final Map<Integer, List<Card>> mediumBackDoorFlushDrawCombosAsMapList = getMediumBackDoorFlushCombosAsMapList(allBackDoorFlushDrawCombos);

        strongFlushDrawCombos = getStrongFlushDrawCombosInitialize(strongFlushDrawCombosAsMapList);
        mediumFlushDrawCombos = getMediumFlushDrawCombosInitialize(mediumFlushDrawCombosAsMapList);
        weakFlushDrawCombos = getWeakFlushDrawCombosInitialize(allFlushDraws, strongFlushDrawCombosAsMapList, mediumFlushDrawCombosAsMapList);
        strongBackDoorFlushCombos = getStrongBackDoorFlushCombosInitialize(strongBackDoorFlushDrawCombosAsMapList);
        mediumBackDoorFlushCombos = getMediumBackDoorFlushCombosInitialize(mediumBackDoorFlushDrawCombosAsMapList);
        weakBackDoorFlushCombos = getWeakBackDoorFlushCombosInitialize(allBackDoorFlushDrawCombos,
                strongBackDoorFlushDrawCombosAsMapList, mediumBackDoorFlushDrawCombosAsMapList);

        setFlushDrawCombosPerStreet(allFlushDraws);
    }

    public Map<Integer, Set<Card>> getStrongFlushDrawCombos() {
        return strongFlushDrawCombos;
    }

    public Map<Integer, Set<Card>> getMediumFlushDrawCombos() {
        return mediumFlushDrawCombos;
    }

    public Map<Integer, Set<Card>> getWeakFlushDrawCombos() {
        return weakFlushDrawCombos;
    }

    public Map<Integer, Set<Card>> getStrongBackDoorFlushCombos() {
        return strongBackDoorFlushCombos;
    }

    public Map<Integer, Set<Card>> getMediumBackDoorFlushCombos() {
        return mediumBackDoorFlushCombos;
    }

    public Map<Integer, Set<Card>> getWeakBackDoorFlushCombos() {
        return weakBackDoorFlushCombos;
    }

    public Map<Integer, Set<Card>> getAllFlushDrawsFlop() {
        return allFlushDrawsFlop;
    }

    public Map<Integer, Set<Card>> getAllFlushDrawsTurn() {
        return allFlushDrawsTurn;
    }

    //helper methods
    private Map<Integer, Set<Card>> getStrongFlushDrawCombosInitialize(Map<Integer, List<Card>> strongFlushDrawCombosAsMapList) {
        Map<Integer, Set<Card>> strongFlushDrawCombos = new HashMap<>();

        for (Map.Entry<Integer, List<Card>> entry : strongFlushDrawCombosAsMapList.entrySet()) {
            Set<Card> comboAsSet = new HashSet<>();
            comboAsSet.addAll(entry.getValue());
            strongFlushDrawCombos.put(strongFlushDrawCombos.size(), comboAsSet);
        }
        return strongFlushDrawCombos;
    }

    private Map<Integer, Set<Card>> getMediumFlushDrawCombosInitialize(Map<Integer, List<Card>> mediumFlushDrawCombosAsMapList) {
        Map<Integer, Set<Card>> mediumFlushDrawCombos = new HashMap<>();

        for (Map.Entry<Integer, List<Card>> entry : mediumFlushDrawCombosAsMapList.entrySet()) {
            Set<Card> comboAsSet = new HashSet<>();
            comboAsSet.addAll(entry.getValue());
            mediumFlushDrawCombos.put(mediumFlushDrawCombos.size(), comboAsSet);
        }
        return mediumFlushDrawCombos;
    }

    private Map<Integer, Set<Card>> getWeakFlushDrawCombosInitialize(Map<Integer, List<Card>> allFlushDraws,
                                                                     Map<Integer, List<Card>> strongFlushDrawCombosAsMapList,
                                                                     Map<Integer, List<Card>> mediumFlushDrawCombosAsMapList) {
        return getWeakFlushOrBackDoorFlushDrawCombos(allFlushDraws, strongFlushDrawCombosAsMapList, mediumFlushDrawCombosAsMapList);
    }

    private Map<Integer, Set<Card>> getStrongBackDoorFlushCombosInitialize(Map<Integer, List<Card>>
                                                                                   strongBackDoorFlushCombosAsMapList) {
        Map<Integer, Set<Card>> strongBackDoorFlushDrawCombos = new HashMap<>();

        for (Map.Entry<Integer, List<Card>> entry : strongBackDoorFlushCombosAsMapList.entrySet()) {
            Set<Card> comboAsSet = new HashSet<>();
            comboAsSet.addAll(entry.getValue());
            strongBackDoorFlushDrawCombos.put(strongBackDoorFlushDrawCombos.size(), comboAsSet);
        }
        return strongBackDoorFlushDrawCombos;
    }

    private Map<Integer, Set<Card>> getMediumBackDoorFlushCombosInitialize(Map<Integer, List<Card>>
                                                                                   mediumBackDoorFlushCombosAsMapList) {
        Map<Integer, Set<Card>> mediumBackDoorFlushDrawCombos = new HashMap<>();

        for (Map.Entry<Integer, List<Card>> entry : mediumBackDoorFlushCombosAsMapList.entrySet()) {
            Set<Card> comboAsSet = new HashSet<>();
            comboAsSet.addAll(entry.getValue());
            mediumBackDoorFlushDrawCombos.put(mediumBackDoorFlushDrawCombos.size(), comboAsSet);
        }
        return mediumBackDoorFlushDrawCombos;
    }

    private Map<Integer, Set<Card>> getWeakBackDoorFlushCombosInitialize(Map<Integer, List<Card>> allBackDoorFlushDraws,
                                                                         Map<Integer, List<Card>> strongBackDoorFlushDrawCombosAsMapList,
                                                                         Map<Integer, List<Card>> mediumBackDoorFlushDrawCombosAsMapList) {

        return getWeakFlushOrBackDoorFlushDrawCombos(allBackDoorFlushDraws, strongBackDoorFlushDrawCombosAsMapList,
                mediumBackDoorFlushDrawCombosAsMapList);
    }

    private Map<Integer, List<Card>> getFlushDrawCombos (List<Card> board) {
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
            Map<Integer, List<Card>> allStartHands = PreflopRangeBuilderUtil.getAllPossibleStartHandsAsList();
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

    private Map<Integer, List<Card>> getBackDoorFlushDrawCombos() {
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

    private Map<Integer, List<Card>> getStrongFlushDrawCombosAsMapList(Map<Integer, List<Card>> allFlushDraws) {
        Map<Integer, List<Card>> strongFlushDrawCombos = new HashMap<>();

        if(getNumberOfPairsOnBoard(board) < 2 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            if(getNumberOfSuitedCardsOnBoard(board) == 2) {
                return allFlushDraws;
            }

            if(getNumberOfSuitedCardsOnBoard(board) == 3) {
                Map<Integer, List<Card>> allFlushDrawCombos = allFlushDraws;
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

    private Map<Integer, List<Card>> getMediumFlushDrawCombosAsMapList(Map<Integer, List<Card>> allFlushDraws) {
        Map<Integer, List<Card>> mediumFlushDrawCombos = new HashMap<>();

        //max 1 pair
        if(getNumberOfPairsOnBoard(board) < 2 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            if (getNumberOfSuitedCardsOnBoard(board) == 3) {
                char flushSuit = getFlushSuitWhen3ToFlushOnBoard(board);
                List<Card> flushCardsOnBoard = getFlushCardsOnBoard(board, flushSuit);
                int numberOfRanksNeeded = 4;
                List<Integer> mediumRanksNotPresentOnBoard = getNeededRanksNotPresentOnFlushBoard(flushCardsOnBoard,
                        numberOfRanksNeeded, "medium");

                for (Map.Entry<Integer, List<Card>> entry : allFlushDraws.entrySet()) {
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
            return allFlushDraws;
        }

        return new HashMap<>();
    }

    private Map<Integer, List<Card>> getStrongBackDoorFlushCombosAsMapList(Map<Integer, List<Card>> allBackDoorCombos) {
        return getStrongOrMediumBackDoorFlushCombos(allBackDoorCombos, "strong");
    }

    private Map<Integer, List<Card>> getMediumBackDoorFlushCombosAsMapList(Map<Integer, List<Card>> allBackDoorCombos) {
        return getStrongOrMediumBackDoorFlushCombos(allBackDoorCombos, "medium");
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

    private Map<Integer, List<Card>> getStrongOrMediumBackDoorFlushCombos(Map<Integer, List<Card>> allBackDoorCombos,
                                                                          String strongOrMedium) {
        if(strongOrMedium.equals("strong")) {
            if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board)) {
                if(getNumberOfSuitedCardsOnBoard(board) < 2) {
                    return allBackDoorCombos;
                }
            }
        }

        if(getNumberOfPairsOnBoard(board) < 2 && !boardContainsTrips(board)) {
            if(getNumberOfSuitedCardsOnBoard(board) == 2) {
                Map<Integer, List<Card>> strongOrMediumBackDoorCombos = new HashMap<>();

                Map<Character, List<Card>> suitsOfBoard = getSuitsOfBoard(board);
                List<Card> backDoorFlushCardsOnBoard = new ArrayList<>();
                char singleFlushSuit = 'x';

                for (Map.Entry<Character, List<Card>> entry : suitsOfBoard.entrySet()) {
                    if(entry.getValue().size() == 2) {
                        backDoorFlushCardsOnBoard = entry.getValue();
                    } else if(entry.getValue().size() == 1) {
                        singleFlushSuit = entry.getKey();
                    }
                }

                char doubleFlushSuit = backDoorFlushCardsOnBoard.get(0).getSuit();

                int numberOfRanksNeeded = 0;
                List<Integer> neededRanksNotPresentOnBoard = new ArrayList<>();
                if(strongOrMedium.equals("strong")) {
                    numberOfRanksNeeded = 2;
                    neededRanksNotPresentOnBoard = getNeededRanksNotPresentOnFlushBoard(backDoorFlushCardsOnBoard,
                            numberOfRanksNeeded, "high");
                }
                if(strongOrMedium.equals("medium")) {
                    //voeg ook strong combos toe aan medium als er pair op board ligt
                    if(getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board)) {
                        neededRanksNotPresentOnBoard.addAll(getNeededRanksNotPresentOnFlushBoard(backDoorFlushCardsOnBoard,
                                2, "high"));
                    }

                    numberOfRanksNeeded = 4;
                    neededRanksNotPresentOnBoard.addAll(getNeededRanksNotPresentOnFlushBoard(backDoorFlushCardsOnBoard,
                            numberOfRanksNeeded, "medium"));
                }

                for (Map.Entry<Integer, List<Card>> entry : allBackDoorCombos.entrySet()) {
                    if(strongOrMedium.equals("strong")) {
                        if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board)) {
                            if(entry.getValue().get(0).getSuit() == singleFlushSuit &&
                                    entry.getValue().get(1).getSuit() == singleFlushSuit) {
                                strongOrMediumBackDoorCombos.put(strongOrMediumBackDoorCombos.size(), entry.getValue());
                            }
                        }
                    }

                    if(strongOrMedium.equals("medium")) {
                        if(getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board)) {
                            if(entry.getValue().get(0).getSuit() == singleFlushSuit &&
                                    entry.getValue().get(1).getSuit() == singleFlushSuit) {
                                strongOrMediumBackDoorCombos.put(strongOrMediumBackDoorCombos.size(), entry.getValue());
                            }
                        }
                    }

                    Card c = getHighestFlushCardFromCombo(entry.getValue(), doubleFlushSuit);
                    if(neededRanksNotPresentOnBoard.contains(Integer.valueOf(c.getRank()))) {
                        if(entry.getValue().get(0).getSuit() != singleFlushSuit ||
                                entry.getValue().get(1).getSuit() != singleFlushSuit) {
                            if(strongOrMedium.equals("strong")) {
                                if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board)) {
                                    strongOrMediumBackDoorCombos.put(strongOrMediumBackDoorCombos.size(), entry.getValue());
                                }
                            }
                            if(strongOrMedium.equals("medium")) {
                                if(getNumberOfPairsOnBoard(board) < 2 && !boardContainsTrips(board)) {
                                    strongOrMediumBackDoorCombos.put(strongOrMediumBackDoorCombos.size(), entry.getValue());
                                }
                            }
                        }
                    }
                }
                return strongOrMediumBackDoorCombos;
            } else if(getNumberOfPairsOnBoard(board) == 1) {
                if(strongOrMedium.equals("medium")) {
                    return allBackDoorCombos;
                }
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

    private void setFlushDrawCombosPerStreet(Map<Integer, List<Card>> flushDrawCombos) {
        Map<Integer, Set<Card>> flushDrawCombosAsSet = convertListMapToSetMap(flushDrawCombos);

        if(board.size() == 3) {
            allFlushDrawsFlop = flushDrawCombosAsSet;
        } else if(board.size() == 4) {
            allFlushDrawsTurn = flushDrawCombosAsSet;
        }
    }

    private Map<Integer, Set<Card>> convertListMapToSetMap(Map<Integer, List<Card>> listMap) {
        Map<Integer, Set<Card>> setMap = new HashMap<>();

        for (Map.Entry<Integer, List<Card>> entry : listMap.entrySet()) {
            Set<Card> set = new HashSet<>();
            set.addAll(entry.getValue());
            setMap.put(setMap.size(), set);
        }
        return setMap;
    }
}
