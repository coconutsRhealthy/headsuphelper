package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by LPO10346 on 9/1/2016.
 */
public class TwoPairEvaluator extends BoardEvaluator implements ComboComparator{

    public Map<Integer, List<Card>> getCombosThatMakeTwoPair (List<Card> board) {
        Map<Integer, List<Card>> combosThatMakeTwoPair = new HashMap<>();

        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board) && !boardContainsQuads(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnlyCorrectedForBoard(allPossibleStartHands, board);

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                if (boardRanks.containsAll(entry.getValue())) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                }
            }

            //verwijder pocket pairs
            for(Iterator<Map.Entry<Integer, List<Card>>> it = combosThatMakeTwoPair.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Integer, List<Card>> entry = it.next();
                if(entry.getValue().get(0).getRank() == entry.getValue().get(1).getRank()) {
                    it.remove();
                }
            }
            Map<Integer, List<Integer>> rankMap = getSortedComboMapRankOnly(combosThatMakeTwoPair, board, new TwoPairEvaluator());
            return combosThatMakeTwoPair;
        } else if (getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board)) {
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnlyCorrectedForBoard(allPossibleStartHands, board);

            //4 4 5 8 K
            //4 7 7 9 J
            //alle combos die pairen met één andere kaart op het board
            //alle combos die pairen met twee kaarten van het board, allebei boven het pair dat er al ligt

            Integer rankOfPairOnBoard = getRanksOfPairsOnBoard(board).get(0);
            boardRanks.removeAll(Collections.singleton(rankOfPairOnBoard));
            int initialSizeBoardRanks = boardRanks.size();

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                List<Integer> copyCombo = new ArrayList<>();
                copyCombo.addAll(entry.getValue());

                List<Integer> copyBoardRanks = new ArrayList<>();
                copyBoardRanks.addAll(boardRanks);

                copyBoardRanks.removeAll(copyCombo);

                if(copyBoardRanks.size() == initialSizeBoardRanks - 1 && !entry.getValue().contains(rankOfPairOnBoard)
                        && entry.getValue().get(0) != entry.getValue().get(1)) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                }
            }

            //voeg pocket pairs toe
            Map<Integer, List<Card>> allPocketPairs = getAllPocketPairStartHands();
            allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(0).getRank());
            allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(1).getRank());
            allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(2).getRank());

            if(board.size() == 4) {
                allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(3).getRank());
            }

            if(board.size() == 5) {
                allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(3).getRank());
                allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(4).getRank());
            }

            boardRanks = getSortedCardRanksFromCardList(board);

            for (Map.Entry<Integer, List<Card>> pocketPairEntry : allPocketPairs.entrySet()) {
                if(!boardRanks.contains(pocketPairEntry.getValue().get(0).getRank())) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), pocketPairEntry.getValue());
                }
            }
            Map<Integer, List<Integer>> rankMap = getSortedComboMapRankOnly(combosThatMakeTwoPair, board, new TwoPairEvaluator());
            return combosThatMakeTwoPair;
        } else if (getNumberOfPairsOnBoard(board) == 2) {
            //alle combos die niet een boat maken. Dus op 4499J ook J combos.
            Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
            allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
            Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnlyCorrectedForBoard(allPossibleStartHands, board);

            //get card die niet met andere boardCards pairt, indien aanwezig
            Integer rankOfUnpairedBoardCard = 0;
            List<Integer> ranksOfPairsOnBoard = getRanksOfPairsOnBoard(board);
            boardRanks.removeAll(ranksOfPairsOnBoard);
            if(boardRanks.size() == 1) {
                rankOfUnpairedBoardCard = boardRanks.get(0);
            }

            if(rankOfUnpairedBoardCard > ranksOfPairsOnBoard.get(0) || rankOfUnpairedBoardCard > ranksOfPairsOnBoard.get(1)) {
                for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                    List<Integer> comboCopy = new ArrayList<>();
                    comboCopy.addAll(entry.getValue());
                    comboCopy.removeAll(Collections.singleton(rankOfUnpairedBoardCard));

                    if(comboCopy.size() == 1 && !ranksOfPairsOnBoard.contains(comboCopy.get(0))) {
                        combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                    }
                }
            }

            //voeg alle pocket pairs toe hoger dan het laagste pair on board
            //get laagste boardpair kaart
            int rankOfLowestPairOnBoard = Collections.min(ranksOfPairsOnBoard);

            Map<Integer, List<Card>> allPocketPairs = getAllPocketPairStartHands();
            allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(0).getRank());
            allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(1).getRank());
            allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(2).getRank());

            if(board.size() == 4) {
                allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(3).getRank());
            }

            if(board.size() == 5) {
                allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(3).getRank());
                allPocketPairs = clearStartHandsMapOfStartHandsThatContainCardOfSpecificRank(allPocketPairs, board.get(4).getRank());
            }


            for(Iterator<Map.Entry<Integer, List<Card>>> it = allPocketPairs.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<Integer, List<Card>> entry = it.next();
                if(entry.getValue().get(0).getRank() > rankOfLowestPairOnBoard) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), entry.getValue());
                }
            }

            //voeg alle combos toe die niet pairen en geen boat maken
            boardRanks = getSortedCardRanksFromCardList(board);

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                if(Collections.disjoint(entry.getValue(), boardRanks) && entry.getValue().get(0) != entry.getValue().get(1)) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                }
            }
            return combosThatMakeTwoPair;
        }
        return combosThatMakeTwoPair;
    }

    @Override
    public Comparator<List<Integer>> getComboComparatorRankOnly(List<Card> board) {
        return new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> combo1, List<Integer> combo2) {
                BoardEvaluator boardEvaluator = new BoardEvaluator();
                List<Integer> boardRanks = boardEvaluator.getSortedCardRanksFromCardList(board);

                Collections.sort(combo1, Collections.reverseOrder());
                Collections.sort(combo2, Collections.reverseOrder());

                if(boardEvaluator.getNumberOfPairsOnBoard(board) == 0) {
                    if(Collections.max(combo2) > Collections.max(combo1)) {
                        return 1;
                    } else if(Collections.max(combo2) == Collections.max(combo1)) {
                        if(Collections.min(combo2) > Collections.min(combo1)) {
                            return 1;
                        } else if(Collections.min(combo2) == Collections.min(combo1)) {
                            return 0;
                        } else {
                            return -1;
                        }
                    } else {
                        return -1;
                    }
                } else if(boardEvaluator.getNumberOfPairsOnBoard(board) == 1) {
                    //7 8 9 9 J

                    int combo1HighCard = combo1.get(0);
                    int combo1LowCard = combo1.get(1);

                    int combo2HighCard = combo2.get(0);
                    int combo2LowCard = combo2.get(1);

                    int combo1HighPairCard = 0;
                    int combo1LowPairCard = 0;

                    int combo2HighPairCard = 0;
                    int combo2LowPairCard = 0;

                    int combo1KickerCard = 0;
                    int combo2KickerCard = 0;

                    int rankOfPairOnBoard = boardEvaluator.getRanksOfPairsOnBoard(board).get(0);

                    if(boardRanks.contains(combo1HighCard) && boardRanks.contains(combo1LowCard)) {
                        //als beide kaarten boven rank of pair on board zijn
                        if(combo1HighCard > rankOfPairOnBoard && combo1LowCard > rankOfPairOnBoard) {
                            combo1HighPairCard = combo1HighCard;
                            combo1LowPairCard = combo1LowCard;
                        }

                        //als een van beide kaarten boven rank of pair on board zijn
                        if(combo1HighCard > rankOfPairOnBoard && combo1LowCard < rankOfPairOnBoard) {
                            combo1HighPairCard = combo1HighCard;
                            combo1LowPairCard = rankOfPairOnBoard;
                        }

                        //als beide kaarten onder rank of pair on board zijn
                        if(combo1HighCard < rankOfPairOnBoard && combo1LowCard < rankOfPairOnBoard) {
                            combo1HighPairCard = rankOfPairOnBoard;
                            combo1LowPairCard = combo1HighCard;
                        }
                    }

                    //als een combo kaart pairt met het board
                    if(boardRanks.contains(combo1HighCard) && !boardRanks.contains(combo1LowCard)) {
                        if(combo1HighCard > rankOfPairOnBoard) {
                            combo1HighPairCard = combo1HighCard;
                            combo1LowPairCard = rankOfPairOnBoard;
                            combo1KickerCard = combo1LowCard;
                        } else if(combo1HighCard < rankOfPairOnBoard) {
                            combo1HighPairCard = rankOfPairOnBoard;
                            combo1LowPairCard = combo1HighCard;
                            combo1KickerCard = combo1LowCard;
                        }
                    } else if (!boardRanks.contains(combo1HighCard) && boardRanks.contains(combo1LowCard)) {
                        if(combo1LowCard > rankOfPairOnBoard) {
                            combo1HighPairCard = combo1LowCard;
                            combo1LowPairCard = rankOfPairOnBoard;
                            combo1KickerCard = combo1HighCard;
                        } else if(combo1LowCard < rankOfPairOnBoard) {
                            combo1HighPairCard = rankOfPairOnBoard;
                            combo1LowPairCard = combo1LowCard;
                            combo1KickerCard = combo1HighCard;
                        }
                    }

                    //als geen kaart pairt met het board
                    if(!boardRanks.contains(combo1HighCard) && !boardRanks.contains(combo1LowCard)) {
                        if(combo1HighCard > rankOfPairOnBoard) {
                            combo1HighPairCard = combo1HighCard;
                            combo1LowPairCard = rankOfPairOnBoard;
                        } else if(combo1HighCard < rankOfPairOnBoard) {
                            combo1HighPairCard = rankOfPairOnBoard;
                            combo1LowPairCard = combo1HighCard;
                        }
                    }


                    //voor combo2
                    //als beide combo kaarten pairen met het board
                    if(boardRanks.contains(combo2HighCard) && boardRanks.contains(combo2LowCard)) {
                        //als beide kaarten boven rank of pair on board zijn
                        if(combo2HighCard > rankOfPairOnBoard && combo2LowCard > rankOfPairOnBoard) {
                            combo2HighPairCard = combo2HighCard;
                            combo2LowPairCard = combo2LowCard;
                        }

                        //als een van beide kaarten boven rank of pair on board zijn
                        if(combo2HighCard > rankOfPairOnBoard && combo2LowCard < rankOfPairOnBoard) {
                            combo2HighPairCard = combo2HighCard;
                            combo2LowPairCard = rankOfPairOnBoard;
                        }

                        //als beide kaarten onder rank of pair on board zijn
                        if(combo2HighCard < rankOfPairOnBoard && combo2LowCard < rankOfPairOnBoard) {
                            combo2HighPairCard = rankOfPairOnBoard;
                            combo2LowPairCard = combo2HighCard;
                        }
                    }

                    //als een combo kaart pairt met het board
                    if(boardRanks.contains(combo2HighCard) && !boardRanks.contains(combo2LowCard)) {
                        if(combo2HighCard > rankOfPairOnBoard) {
                            combo2HighPairCard = combo2HighCard;
                            combo2LowPairCard = rankOfPairOnBoard;
                            combo2KickerCard = combo2LowCard;
                        } else if(combo2HighCard < rankOfPairOnBoard) {
                            combo2HighPairCard = rankOfPairOnBoard;
                            combo2LowPairCard = combo2HighCard;
                            combo2KickerCard = combo2LowCard;
                        }
                    } else if (!boardRanks.contains(combo2HighCard) && boardRanks.contains(combo2LowCard)) {
                        if(combo2LowCard > rankOfPairOnBoard) {
                            combo2HighPairCard = combo2LowCard;
                            combo2LowPairCard = rankOfPairOnBoard;
                            combo2KickerCard = combo2HighCard;
                        } else if(combo2LowCard < rankOfPairOnBoard) {
                            combo2HighPairCard = rankOfPairOnBoard;
                            combo2LowPairCard = combo2LowCard;
                            combo2KickerCard = combo2HighCard;
                        }
                    }

                    //als geen kaart pairt met het board
                    if(!boardRanks.contains(combo2HighCard) && !boardRanks.contains(combo2LowCard)) {
                        if(combo2HighCard > rankOfPairOnBoard) {
                            combo2HighPairCard = combo2HighCard;
                            combo2LowPairCard = rankOfPairOnBoard;
                        } else if(combo2HighCard < rankOfPairOnBoard) {
                            combo2HighPairCard = rankOfPairOnBoard;
                            combo2LowPairCard = combo2HighCard;
                        }
                    }

                    //5 5 J K A

                    // combo 1: J 4

                    // combo 2: J 8


                    if(combo2HighPairCard > combo1HighPairCard) {
                        return 1;
                    } else if (combo2HighPairCard == combo1HighPairCard) {
                        if(combo2LowPairCard > combo1LowPairCard) {
                            return 1;
                        } else if(combo2LowPairCard == combo1LowPairCard) {
                            if(boardEvaluator.getNumberOfPairsOnBoard(board) == 0) {
                                return 0;
                            } else {
                                if(boardRanks.size() == 3) {
                                    if(combo2KickerCard > combo1KickerCard) {
                                        return 1;
                                    } else if(combo2KickerCard == combo1KickerCard) {
                                        return 0;
                                    } else {
                                        return -1;
                                    }
                                } else if(boardRanks.size() > 3) {
                                    boardRanks.removeAll(Collections.singleton(combo2HighPairCard));
                                    boardRanks.removeAll(Collections.singleton(combo2LowPairCard));

                                    int highestUnpairedBoardCard = Collections.max(boardRanks);

                                    if(combo2KickerCard > combo1KickerCard) {
                                        if(combo2KickerCard > highestUnpairedBoardCard) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    } else if(combo2KickerCard == combo1KickerCard) {
                                        return 0;
                                    } else {
                                        // voor lage combo kicker
                                        if(combo1KickerCard > highestUnpairedBoardCard) {
                                            return -1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                }
                            }
                        } else {
                            return -1;
                        }
                    } else {
                        return -1;
                    }

                } else if(boardEvaluator.getNumberOfPairsOnBoard(board) == 2) {
                    //hier gewoon de standaard

                    Collections.sort(combo1, Collections.reverseOrder());
                    Collections.sort(combo2, Collections.reverseOrder());

                    if(combo2.get(0) > combo1.get(0)) {
                        return 1;
                    } else if(combo2.get(0) == combo1.get(0)) {
                        if(combo2.get(1) > combo1.get(1)) {
                            return 1;
                        } else if(combo2.get(1) == combo1.get(1)) {
                            return 0;
                        }
                    }
                    return -1;

                }
                return 0;
            }
        };
    }
}
