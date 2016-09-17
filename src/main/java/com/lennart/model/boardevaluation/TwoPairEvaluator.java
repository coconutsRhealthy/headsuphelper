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
//                if(entry.getValue().get(0).getRank() > rankOfLowestPairOnBoard) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), entry.getValue());
//                }
            }

            //voeg alle combos toe die niet pairen en geen boat maken
            boardRanks = getSortedCardRanksFromCardList(board);

            for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                if(Collections.disjoint(entry.getValue(), boardRanks) && entry.getValue().get(0) != entry.getValue().get(1)) {
                    combosThatMakeTwoPair.put(combosThatMakeTwoPair.size(), allPossibleStartHands.get(entry.getKey()));
                }
            }
            Map<Integer, List<Integer>> rankMap = getSortedComboMapRankOnly(combosThatMakeTwoPair, board, new TwoPairEvaluator());
            return combosThatMakeTwoPair;
        }
        return combosThatMakeTwoPair;
    }

    @Override
    public Comparator<List<Integer>> getComboComparatorRankOnly(List<Card> board) {
        return new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> combo1, List<Integer> combo2) {
                List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

                Collections.sort(combo1, Collections.reverseOrder());
                Collections.sort(combo2, Collections.reverseOrder());

                if(getNumberOfPairsOnBoard(board) == 0) {
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
                } else if(getNumberOfPairsOnBoard(board) == 1) {

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

                    int rankOfPairOnBoard = getRanksOfPairsOnBoard(board).get(0);

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
                            if(getNumberOfPairsOnBoard(board) == 0) {
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

                } else if(getNumberOfPairsOnBoard(board) == 2) {
                    //als het board 4 kaarten heeft
                    if(board.size() == 4) {
                        //als beide combos gepaired zijn
                        if(combo1.get(0) == combo1.get(1) && combo2.get(0) == combo2.get(1)) {
                            //als beide combos boven laagste boardpair
                            if(combo1.get(0) > Collections.min(boardRanks) && combo2.get(0) > Collections.min(boardRanks)) {
                                //als hoogste kaart combo2 > hoogste kaart combo1
                                if(Collections.max(combo2) > Collections.max(combo1)) {
                                    return 1;
                                }

                                //als hoogste kaart combo2 == hoogste kaart combo1
                                if(Collections.max(combo2) == Collections.max(combo1)) {
                                    return 0;
                                }

                                //als hoogste kaart combo2 < hoogste kaart combo1
                                if(Collections.max(combo2) < Collections.max(combo1)) {
                                    return -1;
                                }
                            }

                            //als combo1 boven laagste boardpair en combo2 niet
                            if(combo1.get(0) > Collections.min(boardRanks) && combo2.get(0) < Collections.min(boardRanks)) {
                                return -1;
                            }

                            //als combo2 boven laagste boardpair en combo1 niet
                            if(combo1.get(0) < Collections.min(boardRanks) && combo2.get(0) > Collections.min(boardRanks)) {
                                return 1;
                            }

                            //als beide combos onder laagste boardpair
                            if(combo1.get(0) < Collections.min(boardRanks) && combo2.get(0) < Collections.min(boardRanks)) {
                                //als hoogste kaart combo2 > hoogste kaart combo1
                                if(Collections.max(combo2) > Collections.max(combo1)) {
                                    return 1;
                                }

                                //als hoogste kaart combo2 == hoogste kaart combo1
                                if(Collections.max(combo2) == Collections.max(combo1)) {
                                    return 0;
                                }

                                //als hoogste kaart combo2 < hoogste kaart combo1
                                if(Collections.max(combo2) < Collections.max(combo1)) {
                                    return -1;
                                }
                            }
                        }

                        //als combo1 gepaired is en 2 niet
                        if(combo1.get(0) == combo1.get(1) && combo2.get(0) != combo2.get(1)) {
                            //als combo1 boven laagste boardpair
                            if(combo1.get(0) > Collections.min(boardRanks)) {
                                return -1;
                            }

                            //als combo1 onder laagste boardpair
                            if(combo1.get(0) < Collections.min(boardRanks)) {
                                //als hoogste kaart combo2 > hoogste kaart combo1
                                if(Collections.max(combo2) > Collections.max(combo1)) {
                                    return 1;
                                }

                                //als hoogste kaart combo2 == hoogste kaart combo1
                                if(Collections.max(combo2) == Collections.max(combo1)) {
                                    return 0;
                                }

                                //als hoogste kaart combo2 < hoogste kaart combo1
                                if(Collections.max(combo2) < Collections.max(combo1)) {
                                    return -1;
                                }
                            }
                        }

                        //als combo2 gepaired is en 1 niet
                        if(combo1.get(0) != combo1.get(1) && combo2.get(0) == combo2.get(1)) {
                            //als combo2 boven laagste boardpair
                            if(combo2.get(0) > Collections.min(boardRanks)) {
                                return 1;
                            }

                            //als combo2 onder laagste boardpair
                            if(combo2.get(0) < Collections.min(boardRanks)) {
                                //als hoogste kaart combo2 > hoogste kaart combo1
                                if(Collections.max(combo2) > Collections.max(combo1)) {
                                    return 1;
                                }

                                //als hoogste kaart combo2 == hoogste kaart combo1
                                if(Collections.max(combo2) == Collections.max(combo1)) {
                                    return 0;
                                }

                                //als hoogste kaart combo2 < hoogste kaart combo1
                                if(Collections.max(combo2) < Collections.max(combo1)) {
                                    return -1;
                                }
                            }
                        }

                        //als beide niet gepaired
                        if(combo1.get(0) != combo1.get(1) && combo2.get(0) != combo2.get(1)) {
                            //als hoogste kaart combo2 > hoogste kaart combo1
                            if(Collections.max(combo2) > Collections.max(combo1)) {
                                return 1;
                            }

                            //als hoogste kaart combo2 == hoogste kaart combo1
                            if(Collections.max(combo2) == Collections.max(combo1)) {
                                return 0;
                            }

                            //als hoogste kaart combo2 < hoogste kaart combo1
                            if(Collections.max(combo2) < Collections.max(combo1)) {
                                return -1;
                            }
                        }
                    }

                    //als het board 5 kaarten heeft
                    if(board.size() == 5) {
                        //als beide combos gepaired zijn
                        if(combo1.get(0) == combo1.get(1) && combo2.get(0) == combo2.get(1)) {
                            //als beide combos boven laagste boardpair

                            int rankOfLowestPairOnBoard = Collections.min(getRanksOfPairsOnBoard(board));
                            if(combo1.get(0) > rankOfLowestPairOnBoard && combo2.get(0) > rankOfLowestPairOnBoard) {
                                //als hoogste kaart combo2 > hoogste kaart combo1
                                if(Collections.max(combo2) > Collections.max(combo1)) {
                                    return 1;
                                }

                                //als hoogste kaart combo2 == hoogste kaart combo1
                                if(Collections.max(combo2) == Collections.max(combo1)) {
                                    return 0;
                                }

                                //als hoogste kaart combo2 < hoogste kaart combo1
                                if(Collections.max(combo2) < Collections.max(combo1)) {
                                    return -1;
                                }
                            }

                            //als combo1 boven laagste boardpair en combo2 niet
                            if(combo1.get(0) > rankOfLowestPairOnBoard && combo2.get(0) < rankOfLowestPairOnBoard) {
                                return -1;
                            }

                            //als combo2 boven laagste boardpair en combo1 niet
                            if(combo1.get(0) < rankOfLowestPairOnBoard && combo2.get(0) > rankOfLowestPairOnBoard) {
                                return 1;
                            }

                            //als beide combos onder laagste boardpair
                            if(combo1.get(0) < rankOfLowestPairOnBoard && combo2.get(0) < rankOfLowestPairOnBoard) {
                                //hier moet nog meer
                                boardRanks.removeAll(getRanksOfPairsOnBoard(board));
                                int boardKickerCard = boardRanks.get(0);

                                //als combo1 en combo2 boven boardKickerCard
                                if(combo1.get(0) > boardKickerCard && combo2.get(0) > boardKickerCard) {
                                    //als combo1 hoger dan combo2
                                    if(combo1.get(0) > combo2.get(0)) {
                                        return -1;
                                    }

                                    //als combo1 en combo2 gelijk
                                    if(combo1.get(0) == combo2.get(0)) {
                                        return 0;
                                    }

                                    //als combo2 boven combo1
                                    if(combo1.get(0) < combo2.get(0)) {
                                        return 1;
                                    }
                                }

                                //als combo1 boven boardKickerCard en combo2 niet
                                if(combo1.get(0) > boardKickerCard && combo2.get(0) < boardKickerCard) {
                                    return -1;
                                }

                                //als combo2 boven boardKickerCard en combo1 niet
                                if(combo1.get(0) < boardKickerCard && combo2.get(0) > boardKickerCard) {
                                    return 1;
                                }

                                //als beide combos onder boardKickerCard
                                if(combo1.get(0) < boardKickerCard && combo2.get(0) < boardKickerCard) {
                                    return 0;
                                }
                            }
                        }

                        //als combo1 gepaired is en 2 niet
                        if(combo1.get(0) == combo1.get(1) && combo2.get(0) != combo2.get(1)) {
                            //als combo1 hoger is dan laagste boardpair
                            if(combo1.get(0) > Collections.min(getRanksOfPairsOnBoard(board))) {
                                boardRanks.removeAll(getRanksOfPairsOnBoard(board));
                                int boardKickerCard = boardRanks.get(0);

                                if(combo2.contains(boardKickerCard) && boardKickerCard > combo1.get(0)) {
                                    return 1;
                                }

                                return -1;
                            }

                            //als combo1 lager is dan laagste boardpair
                            if(combo1.get(0) < Collections.min(getRanksOfPairsOnBoard(board))) {
                                boardRanks.removeAll(getRanksOfPairsOnBoard(board));
                                int boardKickerCard = boardRanks.get(0);

                                if(!combo1.contains(boardKickerCard) && !combo2.contains(boardKickerCard)) {
                                    //als combo1 en combo2 boven boardKickerCard
                                    if(combo1.get(0) > boardKickerCard && combo2.get(0) > boardKickerCard) {
                                        //als combo1 hoger dan combo2
                                        if(combo1.get(0) > combo2.get(0)) {
                                            return -1;
                                        }

                                        //als combo1 en combo2 gelijk
                                        if(combo1.get(0) == combo2.get(0)) {
                                            return 0;
                                        }

                                        //als combo2 boven combo1
                                        if(combo1.get(0) < combo2.get(0)) {
                                            return 1;
                                        }
                                    }

                                    //als combo1 boven boardKickerCard en combo2 niet
                                    if(combo1.get(0) > boardKickerCard && combo2.get(0) < boardKickerCard) {
                                        return -1;
                                    }

                                    //als combo2 boven boardKickerCard en combo1 niet
                                    if(combo1.get(0) < boardKickerCard && combo2.get(0) > boardKickerCard) {
                                        return 1;
                                    }

                                    //als beide combos onder boardKickerCard
                                    if(combo1.get(0) < boardKickerCard && combo2.get(0) < boardKickerCard) {
                                        return 0;
                                    }
                                }

                                //als combo2 pairt met boardKickerCard
                                if(combo2.contains(boardKickerCard)) {
                                    //als boardKickerCard boven laagste boardPairCard
                                    if(boardKickerCard > Collections.min(getRanksOfPairsOnBoard(board))) {
                                        return 1;
                                    }

                                    //als boardKickerCard onder laagste boardPairCard
                                    if(boardKickerCard < Collections.min(getRanksOfPairsOnBoard(board))) {
                                        //nu gaat het er om welke kicker het hoogst is:
                                        //kicker combo1, kicker combo2, of boardKickerCard
                                        combo2.remove(boardKickerCard);
                                        int kickerCombo1 = combo1.get(0);
                                        int kickerCombo2 = combo2.get(0);

                                        if(kickerCombo1 > kickerCombo2 ) {
                                            if(kickerCombo1 > boardKickerCard) {
                                                return -1;
                                            }

                                            if(kickerCombo1 < boardKickerCard) {
                                                return 0;
                                            }
                                        }

                                        if(kickerCombo1 == kickerCombo2) {
                                            return 0;
                                        }

                                        if(kickerCombo1 < kickerCombo2) {
                                            if(kickerCombo1 > boardKickerCard) {
                                                return 1;
                                            }

                                            if(kickerCombo2 < boardKickerCard) {
                                                return 0;
                                            }
                                        }
                                    }
                                }

                            }
                        }

                        //als combo2 gepaired is en 1 niet
                        if(combo1.get(0) != combo1.get(1) && combo2.get(0) == combo2.get(1)) {
                            //als combo2 hoger is dan laagste boardpair
                            if(combo2.get(0) > Collections.min(getRanksOfPairsOnBoard(board))) {
                                boardRanks.removeAll(getRanksOfPairsOnBoard(board));
                                int boardKickerCard = boardRanks.get(0);

                                if(combo1.contains(boardKickerCard) && boardKickerCard > combo2.get(0)) {
                                    return -1;
                                }

                                return 1;
                            }

                            //als combo2 lager is dan laagste boardpair
                            if(combo2.get(0) < Collections.min(getRanksOfPairsOnBoard(board))) {
                                boardRanks.removeAll(getRanksOfPairsOnBoard(board));
                                int boardKickerCard = boardRanks.get(0);

                                //als combo1 en combo2 boven boardKickerCard
                                if(!combo1.contains(boardKickerCard) && !combo2.contains(boardKickerCard)) {
                                    if(combo1.get(0) > boardKickerCard && combo2.get(0) > boardKickerCard) {
                                        //als combo1 hoger dan combo2
                                        if(combo1.get(0) > combo2.get(0)) {
                                            return -1;
                                        }

                                        //als combo1 en combo2 gelijk
                                        if(combo1.get(0) == combo2.get(0)) {
                                            return 0;
                                        }

                                        //als combo2 boven combo1
                                        if(combo1.get(0) < combo2.get(0)) {
                                            return 1;
                                        }
                                    }

                                    //als combo1 boven boardKickerCard en combo2 niet
                                    if(combo1.get(0) > boardKickerCard && combo2.get(0) < boardKickerCard) {
                                        return -1;
                                    }

                                    //als combo2 boven boardKickerCard en combo1 niet
                                    if(combo1.get(0) < boardKickerCard && combo2.get(0) > boardKickerCard) {
                                        return 1;
                                    }

                                    //als beide combos onder boardKickerCard
                                    if(combo1.get(0) < boardKickerCard && combo2.get(0) < boardKickerCard) {
                                        return 0;
                                    }
                                }

                                //als combo1 pairt met boardKickerCard
                                if(combo1.contains(boardKickerCard)) {
                                    //als boardKickerCard boven laagste boardPairCard
                                    if(boardKickerCard > Collections.min(getRanksOfPairsOnBoard(board))) {
                                        return 1;
                                    }

                                    //als boardKickerCard onder laagste boardPairCard
                                    if(boardKickerCard < Collections.min(getRanksOfPairsOnBoard(board))) {
                                        //nu gaat het er om welke kicker het hoogst is:
                                        //kicker combo1, kicker combo2, of boardKickerCard
                                        combo1.remove(boardKickerCard);
                                        int kickerCombo1 = combo1.get(0);
                                        int kickerCombo2 = combo2.get(0);

                                        if(kickerCombo1 > kickerCombo2 ) {
                                            if(kickerCombo1 > boardKickerCard) {
                                                return -1;
                                            }

                                            if(kickerCombo1 < boardKickerCard) {
                                                return 0;
                                            }
                                        }

                                        if(kickerCombo1 == kickerCombo2) {
                                            return 0;
                                        }

                                        if(kickerCombo1 < kickerCombo2) {
                                            if(kickerCombo1 > boardKickerCard) {
                                                return 1;
                                            }

                                            if(kickerCombo2 < boardKickerCard) {
                                                return 0;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        //als beide niet gepaired
                        if(combo1.get(0) != combo1.get(1) && combo2.get(0) != combo2.get(1)) {
                            boardRanks.removeAll(getRanksOfPairsOnBoard(board));
                            int boardKickerCard = boardRanks.get(0);

                            if(!combo1.contains(boardKickerCard) && !combo2.contains(boardKickerCard)) {
                                //als combo1 en combo2 boven boardKickerCard
                                if(combo1.get(0) > boardKickerCard && combo2.get(0) > boardKickerCard) {
                                    //als combo1 hoger dan combo2
                                    if(combo1.get(0) > combo2.get(0)) {
                                        return -1;
                                    }

                                    //als combo1 en combo2 gelijk
                                    if(combo1.get(0) == combo2.get(0)) {
                                        return 0;
                                    }

                                    //als combo2 boven combo1
                                    if(combo1.get(0) < combo2.get(0)) {
                                        return 1;
                                    }
                                }

                                //als combo1 boven boardKickerCard en combo2 niet
                                if(combo1.get(0) > boardKickerCard && combo2.get(0) < boardKickerCard) {
                                    return -1;
                                }

                                //als combo2 boven boardKickerCard en combo1 niet
                                if(combo1.get(0) < boardKickerCard && combo2.get(0) > boardKickerCard) {
                                    return 1;
                                }

                                //als beide combos onder boardKickerCard
                                if(combo1.get(0) < boardKickerCard && combo2.get(0) < boardKickerCard) {
                                    return 0;
                                }
                            }

                            //pair met kickercard check
                            //als kickercard is hoger dan laagste boardpair
                            if(boardKickerCard > Collections.min(getRanksOfPairsOnBoard(board))) {
                                //als combo1 pairt met kickercard en combo2 niet
                                if(combo1.contains(boardKickerCard) && !combo2.contains(boardKickerCard)) {
                                    return -1;
                                }

                                //als combo2 pairt met kickercard en combo1 niet
                                if(combo2.contains(boardKickerCard) && !combo1.contains(boardKickerCard)) {
                                    return 1;
                                }

                                //als beide combos niet pairen met kickercard
                                if(!combo1.contains(boardKickerCard) && !combo2.contains(boardKickerCard)) {
                                    //als combo1 en combo2 boven boardKickerCard
                                    if(combo1.get(0) > boardKickerCard && combo2.get(0) > boardKickerCard) {
                                        //als combo1 hoger dan combo2
                                        if(combo1.get(0) > combo2.get(0)) {
                                            return -1;
                                        }

                                        //als combo1 en combo2 gelijk
                                        if(combo1.get(0) == combo2.get(0)) {
                                            return 0;
                                        }

                                        //als combo2 boven combo1
                                        if(combo1.get(0) < combo2.get(0)) {
                                            return 1;
                                        }
                                    }

                                    //als combo1 boven boardKickerCard en combo2 niet
                                    if(combo1.get(0) > boardKickerCard && combo2.get(0) < boardKickerCard) {
                                        return -1;
                                    }

                                    //als combo2 boven boardKickerCard en combo1 niet
                                    if(combo1.get(0) < boardKickerCard && combo2.get(0) > boardKickerCard) {
                                        return 1;
                                    }

                                    //als beide combos onder boardKickerCard
                                    if(combo1.get(0) < boardKickerCard && combo2.get(0) < boardKickerCard) {
                                        return 0;
                                    }
                                }

                                //als beide combos pairen met kickercard
                                if(combo1.contains(boardKickerCard) && combo2.contains(boardKickerCard)) {
                                    return 0;
                                }
                            }
                        }
                    }
                }
                return 0;
            }
        };
    }
}
