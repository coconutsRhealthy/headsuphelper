package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by lpo10346 on 9/23/2016.
 */
public class FourOfAKindEvaluator extends BoardEvaluator implements ComboComparator {

    private static Map<Integer, Set<Set<Card>>> combosThatMakeFourOfAKind;

    public Map<Integer, Set<Set<Card>>> getFourOfAKindCombos() {
        return combosThatMakeFourOfAKind;
    }

    public Map<Integer, Set<Set<Card>>> getFourOfAKindCombosInitialize(List<Card> board) {
        Map<Integer, List<Card>> comboMap = new HashMap<>();
        Map<Integer, Set<Set<Card>>> fourOfAKindCombos = new HashMap<>();

        if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            fourOfAKindCombos = removeDuplicateCombos(fourOfAKindCombos, board);

            this.combosThatMakeFourOfAKind = fourOfAKindCombos;

            return fourOfAKindCombos;
        }

        if(getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            int rankOfPairOnBoard = getRanksOfPairsOnBoard(board).get(0);

            Card pairCardS = new Card(rankOfPairOnBoard, 's');
            Card pairCardC = new Card(rankOfPairOnBoard, 'c');
            Card pairCardD = new Card(rankOfPairOnBoard, 'd');
            Card pairCardH = new Card(rankOfPairOnBoard, 'h');

            Set<Card> pairCards = new HashSet<>();
            pairCards.add(pairCardS);
            pairCards.add(pairCardC);
            pairCards.add(pairCardD);
            pairCards.add(pairCardH);

            pairCards.removeAll(board);

            List<Card> combo = new ArrayList<>();
            combo.addAll(pairCards);
            comboMap.put(comboMap.size(), combo);

            fourOfAKindCombos = getSortedCardComboMap(comboMap, board, new FourOfAKindEvaluator());
            fourOfAKindCombos = removeDuplicateCombos(fourOfAKindCombos, board);

            this.combosThatMakeFourOfAKind = fourOfAKindCombos;

            return fourOfAKindCombos;
        }

        if(getNumberOfPairsOnBoard(board) == 2 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            List<Integer> ranksOfPairOnBoard = getRanksOfPairsOnBoard(board);

            Card pairCard1S = new Card(ranksOfPairOnBoard.get(0), 's');
            Card pairCard1C = new Card(ranksOfPairOnBoard.get(0), 'c');
            Card pairCard1D = new Card(ranksOfPairOnBoard.get(0), 'd');
            Card pairCard1H = new Card(ranksOfPairOnBoard.get(0), 'h');
            Card pairCard2S = new Card(ranksOfPairOnBoard.get(1), 's');
            Card pairCard2C = new Card(ranksOfPairOnBoard.get(1), 'c');
            Card pairCard2D = new Card(ranksOfPairOnBoard.get(1), 'd');
            Card pairCard2H = new Card(ranksOfPairOnBoard.get(1), 'h');

            Set<Card> pair1Cards = new HashSet<>();
            pair1Cards.add(pairCard1S);
            pair1Cards.add(pairCard1C);
            pair1Cards.add(pairCard1D);
            pair1Cards.add(pairCard1H);

            pair1Cards.removeAll(board);

            Set<Card> pair2Cards = new HashSet<>();
            pair2Cards.add(pairCard2S);
            pair2Cards.add(pairCard2C);
            pair2Cards.add(pairCard2D);
            pair2Cards.add(pairCard2H);

            pair2Cards.removeAll(board);

            List<Card> combo1 = new ArrayList<>();
            List<Card> combo2 = new ArrayList<>();

            combo1.addAll(pair1Cards);
            combo2.addAll(pair2Cards);
            comboMap.put(comboMap.size(), combo1);
            comboMap.put(comboMap.size(), combo2);

            fourOfAKindCombos = getSortedCardComboMap(comboMap, board, new FourOfAKindEvaluator());
            fourOfAKindCombos = removeDuplicateCombos(fourOfAKindCombos, board);

            this.combosThatMakeFourOfAKind = fourOfAKindCombos;

            return fourOfAKindCombos;
        }

        if(boardContainsTrips(board) && !boardContainsQuads(board)) {
            Map<Integer, Integer> frequencyOfRanksOnBoard = getFrequencyOfRanksOnBoard(board);

            Integer rankOfTripsOnBoard = 0;

            for (Map.Entry<Integer, Integer> entry : frequencyOfRanksOnBoard.entrySet()) {
                if(entry.getValue() == 3) {
                    rankOfTripsOnBoard = entry.getKey();
                }
            }

            Card tripsCard1 = new Card(rankOfTripsOnBoard, 's');
            Card tripsCard2 = new Card(rankOfTripsOnBoard, 'c');
            Card tripsCard3 = new Card(rankOfTripsOnBoard, 'd');
            Card tripsCard4 = new Card(rankOfTripsOnBoard, 'h');

            List<Card> tripsCards = new ArrayList<>();
            tripsCards.add(tripsCard1);
            tripsCards.add(tripsCard2);
            tripsCards.add(tripsCard3);
            tripsCards.add(tripsCard4);

            tripsCards.removeAll(board);

            Card tripsCardNotOnBoard = tripsCards.get(0);

            Map<Integer, List<Card>> allStartHandsThatContainTripsCardNotOnBoard = getAllStartHandsThatContainASpecificCard(tripsCardNotOnBoard);

            allStartHandsThatContainTripsCardNotOnBoard =
                    clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allStartHandsThatContainTripsCardNotOnBoard, board);

            if(board.size() == 5) {
                List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
                boardRanks.removeAll(Collections.singleton(rankOfTripsOnBoard));
                if(boardRanks.get(0) == boardRanks.get(1)) {
                    Card pairCardBoatBoardS = new Card(boardRanks.get(0), 's');
                    Card pairCardBoatBoardC = new Card(boardRanks.get(0), 'c');
                    Card pairCardBoatBoardD = new Card(boardRanks.get(0), 'd');
                    Card pairCardBoatBoardH = new Card(boardRanks.get(0), 'h');

                    Set<Card> pairCards = new HashSet<>();
                    pairCards.add(pairCardBoatBoardS);
                    pairCards.add(pairCardBoatBoardC);
                    pairCards.add(pairCardBoatBoardD);
                    pairCards.add(pairCardBoatBoardH);

                    pairCards.removeAll(board);

                    List<Card> pairCardsConvertedToList = new ArrayList<>();
                    pairCardsConvertedToList.addAll(pairCards);

                    allStartHandsThatContainTripsCardNotOnBoard.put(allStartHandsThatContainTripsCardNotOnBoard.size(),
                            pairCardsConvertedToList);
                }
            }

            fourOfAKindCombos = getSortedCardComboMap(allStartHandsThatContainTripsCardNotOnBoard, board, new FourOfAKindEvaluator());
            fourOfAKindCombos = removeDuplicateCombos(fourOfAKindCombos, board);

            this.combosThatMakeFourOfAKind = fourOfAKindCombos;

            return fourOfAKindCombos;
        }

        if(boardContainsQuads(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHandsNew();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            fourOfAKindCombos = getSortedCardComboMap(allPossibleStartHands, board, new FourOfAKindEvaluator());
            fourOfAKindCombos = removeDuplicateCombos(fourOfAKindCombos, board);

            this.combosThatMakeFourOfAKind = fourOfAKindCombos;

            return fourOfAKindCombos;
        }
        fourOfAKindCombos = removeDuplicateCombos(fourOfAKindCombos, board);

        this.combosThatMakeFourOfAKind = fourOfAKindCombos;

        return fourOfAKindCombos;
    }

    //helper methods
    @Override
    public Comparator<Set<Card>> getComboComparator(List<Card> board) {
        return new Comparator<Set<Card>>() {
            @Override
            public int compare(Set<Card> xCombo1, Set<Card> xCombo2) {
                List<Card> combo1C = new ArrayList<>();
                List<Card> combo2C = new ArrayList<>();

                combo1C.addAll(xCombo1);
                combo2C.addAll(xCombo2);

                List<Integer> combo1 = getSortedCardRanksFromCardList(combo1C);
                List<Integer> combo2 = getSortedCardRanksFromCardList(combo2C);

                if(getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
                    return 1;
                }

                if(getNumberOfPairsOnBoard(board) == 2 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
                    if(Collections.max(combo2) > Collections.max(combo1)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }

                if(boardContainsTrips(board) && !boardContainsQuads(board)) {
                    Map<Integer, Integer> frequencyOfRanksOnBoard = getFrequencyOfRanksOnBoard(board);

                    Integer rankOfTripsOnBoard = 0;
                    for (Map.Entry<Integer, Integer> entry : frequencyOfRanksOnBoard.entrySet()) {
                        if(entry.getValue() == 3) {
                            rankOfTripsOnBoard = entry.getKey();
                        }
                    }

                    if(!combo2.contains(rankOfTripsOnBoard)) {
                        if(Collections.max(combo2) > rankOfTripsOnBoard) {
                            return 1;
                        }
                    }

                    if(!combo1.contains(rankOfTripsOnBoard)) {
                        if(Collections.max(combo1) > rankOfTripsOnBoard) {
                            return -1;
                        }
                    }

                    return 0;
                }

                if(boardContainsQuads(board)) {
                    if(board.size() == 4) {
                        if(Collections.max(combo2) > Collections.max(combo1)) {
                            return 1;
                        } else if(Collections.max(combo2) == Collections.max(combo1)) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }

                    if(board.size() == 5) {
                        Map<Integer, Integer> frequencyOfRanksOnBoard = getFrequencyOfRanksOnBoard(board);

                        Integer rankOfQuadsOnBoard = 0;
                        for (Map.Entry<Integer, Integer> entry : frequencyOfRanksOnBoard.entrySet()) {
                            if(entry.getValue() == 4) {
                                rankOfQuadsOnBoard = entry.getKey();
                            }
                        }

                        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
                        boardRanks.removeAll(Collections.singleton(rankOfQuadsOnBoard));
                        int boardKicker = boardRanks.get(0);

                        if(Collections.max(combo2) > Collections.max(combo1)) {
                            if(Collections.max(combo2) > boardKicker) {
                                return 1;
                            } else {
                                return 0;
                            }
                        } else if(Collections.max(combo2) == Collections.max(combo1)) {
                            return 0;
                        } else {
                            if(Collections.max(combo1) > boardKicker) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    }
                }
                System.out.println("should never come here, getComboComparator, FourOfAKindEvaluator");
                return 0;
            }
        };
    }

    private Map<Integer, Set<Set<Card>>> removeDuplicateCombos(Map<Integer, Set<Set<Card>>> sortedCombos, List<Card> board) {
        Map<Integer, Set<Set<Card>>> straightFlushCombos = new StraightFlushEvaluator().getStraightFlushCombosInitialize(board);
        sortedCombos = removeDuplicateCombosPerCategory(straightFlushCombos, sortedCombos);
        return sortedCombos;
    }
}
