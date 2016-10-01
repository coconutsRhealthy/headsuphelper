package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by LPO10346 on 8/10/2016.
 */
public class FlushEvaluator extends BoardEvaluator implements ComboComparator {

    public Map<Integer, Set<Set<Card>>> getFlushCombos(List<Card> board) {
        Map<Integer, Set<Set<Card>>> sortedFlushCombos;

        Map<Integer, Set<Set<Card>>> flushCombos = getFlushCombosOud(board);
        sortedFlushCombos = removeDuplicateCombos(flushCombos, board);
        return sortedFlushCombos;
    }


    public Map<Integer, Set<Set<Card>>> getMapOfFlushCombosForStraightFLushEvaluator(List<Card> board) {
        return getFlushCombosOud(board);
    }

    public Map<Integer, Set<Set<Card>>> getFlushCombosOud (List<Card> board) {
        Map<Integer, List<Card>> flushCombos = new HashMap<>();
        Map<Character, List<Card>> suitsOfBoard = getSuitsOfBoard(board);

        char flushSuit = 'x';
        int numberOfSuitedCards = 0;
        for (Map.Entry<Character, List<Card>> entry : suitsOfBoard.entrySet()) {
            if(entry.getValue().size() > numberOfSuitedCards && entry.getValue().size() != 1) {
                numberOfSuitedCards = entry.getValue().size();
            }
            if(entry.getValue().size() > 2) {
                flushSuit = entry.getValue().get(0).getSuit();
            }
        }

        if(numberOfSuitedCards < 3) {
            return new HashMap<>();
        }

        if(numberOfSuitedCards == 3) {
            flushCombos = getAllPossibleSuitedStartHands(flushSuit);
            flushCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushCombos, board);
            return getSortedCardComboMap(flushCombos, board, new FlushEvaluator());
        }

        if(numberOfSuitedCards == 4) {
            flushCombos = getAllPossibleSuitedStartHands(flushSuit);
            Map<Integer, List<Card>> allStartHands = getAllPossibleStartHands();
            for (Map.Entry<Integer, List<Card>> entry : allStartHands.entrySet()) {
                if (entry.getValue().get(0).getSuit() == flushSuit && entry.getValue().get(1).getSuit() != flushSuit) {
                    flushCombos.put(flushCombos.size(), entry.getValue());
                } else if (entry.getValue().get(0).getSuit() != flushSuit && entry.getValue().get(1).getSuit() == flushSuit) {
                    flushCombos.put(flushCombos.size(), entry.getValue());
                }
            }
            flushCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushCombos, board);
            return getSortedCardComboMap(flushCombos, board, new FlushEvaluator());
        }

        if(numberOfSuitedCards == 5) {
            Map<Integer, List<Card>> allStartHands = getAllPossibleStartHands();
            flushCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allStartHands, board);
            return getSortedCardComboMap(flushCombos, board, new FlushEvaluator());
        }
        return new HashMap<>();
    }

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

    //helper methods
    private Map<Integer, List<Card>> getAllNonSuitedStartHandsThatContainASpecificSuit(char suit) {
        Map<Integer, List<Card>> allNonSuitedStartHandsThatContainASpecificSuit = new HashMap<>();
        Map<Integer, List<Card>> allStartHands = getAllPossibleStartHands();
        for (Map.Entry<Integer, List<Card>> entry : allStartHands.entrySet()) {
            if(entry.getValue().get(0).getSuit() == suit && entry.getValue().get(1).getSuit() != suit) {
                allNonSuitedStartHandsThatContainASpecificSuit.put(allNonSuitedStartHandsThatContainASpecificSuit.size(), entry.getValue());
            } else if (entry.getValue().get(0).getSuit() != suit && entry.getValue().get(1).getSuit() == suit) {
                allNonSuitedStartHandsThatContainASpecificSuit.put(allNonSuitedStartHandsThatContainASpecificSuit.size(), entry.getValue());
            }
        }
        return allNonSuitedStartHandsThatContainASpecificSuit;
    }

    public Map<Character, List<Card>> getSuitsOfBoard (List<Card> board) {
        Map<Character, List<Card>> suitMap = new HashMap<>();
        suitMap.put('s', new ArrayList<>());
        suitMap.put('c', new ArrayList<>());
        suitMap.put('d', new ArrayList<>());
        suitMap.put('h', new ArrayList<>());

        for(int i = 0; i < board.size(); i++) {
            if(board.get(i).getSuit() == 's'){
                suitMap.get('s').add(board.get(i));
            } else if(board.get(i).getSuit() == 'c'){
                suitMap.get('c').add(board.get(i));
            } else if(board.get(i).getSuit() == 'd'){
                suitMap.get('d').add(board.get(i));
            } else if(board.get(i).getSuit() == 'h'){
                suitMap.get('h').add(board.get(i));
            }
        }
        return suitMap;
    }

    public Map<Integer, List<Card>> getAllPossibleSuitedStartHands (char suit) {
        Map<Integer, List<Card>> allPossibleCombosOfOneSuit = new HashMap<>();

        for(int i = 0; i < 78; i++) {
            allPossibleCombosOfOneSuit.put(i, new ArrayList<>());
        }

        int counter = 13;
        int counter2 = 0;
        for(int x = 14; x > 2; x--) {
            for(int y = counter; y > 1; y--) {
                Card cardOne = new Card(x, suit);
                Card cardTwo = new Card(y, suit);
                allPossibleCombosOfOneSuit.get(counter2).add(cardOne);
                allPossibleCombosOfOneSuit.get(counter2).add(cardTwo);
                counter2++;
            }
            counter--;
        }
        return allPossibleCombosOfOneSuit;
    }

    private int getNumberOfFlushCardsInCombo(List<Card> combo, char flushSuit) {
        int counter = 0;
        for(Card c : combo) {
            if(c.getSuit() == flushSuit) {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public Comparator<Set<Card>> getComboComparator(List<Card> board) {
        return new Comparator<Set<Card>>() {
            @Override
            public int compare(Set<Card> xCombo1, Set<Card> xCombo2) {
                List<Card> combo1 = new ArrayList<>();
                List<Card> combo2 = new ArrayList<>();

                combo1.addAll(xCombo1);
                combo2.addAll(xCombo2);

                Collections.sort(combo1);
                Collections.sort(combo2);

                Map<Character, List<Card>> suitsOnBoard = getSuitsOfBoard(board);
                char flushSuit = 'x';
                int numberOfSuitedCardsOnBoard = 0;

                for (Map.Entry<Character, List<Card>> entry : suitsOnBoard.entrySet()) {
                    if(entry.getValue().size() >= 3) {
                        flushSuit = entry.getKey();
                        numberOfSuitedCardsOnBoard = entry.getValue().size();
                    }
                }

                //als er 3 van een suit op het board liggen
                if(numberOfSuitedCardsOnBoard == 3) {
                    List<Integer> combo1RankOnly = getSortedCardRanksFromCardList(combo1);
                    List<Integer> combo2RankOnly = getSortedCardRanksFromCardList(combo2);

                    if(Collections.max(combo2RankOnly) > Collections.max(combo1RankOnly)) {
                        return 1;
                    } else if(Collections.max(combo2RankOnly) == Collections.max(combo1RankOnly)) {
                        if(Collections.min(combo2RankOnly) > Collections.min(combo1RankOnly)) {
                            return 1;
                        } else if(Collections.min(combo2RankOnly) == Collections.min(combo1RankOnly)) {
                            return 0;
                        }
                    }
                    return -1;
                }

                //als er 4 van een suit op het board liggen
                else if(numberOfSuitedCardsOnBoard > 3) {
                    //als beide combos 1 kaart hebben van flushsuit

                    List<Card> flushCardsOnBoard = suitsOnBoard.get(flushSuit);
                    Collections.sort(flushCardsOnBoard);
                    int rankOfLowestFlushCardOnBoard = flushCardsOnBoard.get(flushCardsOnBoard.size() -1).getRank();

                    if(getNumberOfFlushCardsInCombo(combo1, flushSuit) == 1 &&
                            getNumberOfFlushCardsInCombo(combo2, flushSuit) == 1) {
                        Card flushCardCombo1 = new Card();
                        Card flushCardCombo2 = new Card();

                        for(Card c : combo1) {
                            if(c.getSuit() == flushSuit) {
                                flushCardCombo1 = c;
                            }
                        }

                        for(Card c : combo2) {
                            if(c.getSuit() == flushSuit) {
                                flushCardCombo2 = c;
                            }
                        }

                        if(flushCardCombo2.getRank() > flushCardCombo1.getRank()) {
                            if(numberOfSuitedCardsOnBoard == 4) {
                                return 1;
                            } else {
                                if(flushCardCombo2.getRank() > rankOfLowestFlushCardOnBoard) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        } else if(flushCardCombo2.getRank() == flushCardCombo1.getRank()) {
                            return 0;
                        } else {
                            if(numberOfSuitedCardsOnBoard == 4) {
                                return -1;
                            } else {
                                if(flushCardCombo1.getRank() > rankOfLowestFlushCardOnBoard) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        }
                    }

                    //als combo2 twee kaarten van flushsuit heeft en combo1 één kaart
                    if(getNumberOfFlushCardsInCombo(combo1, flushSuit) == 1 &&
                            getNumberOfFlushCardsInCombo(combo2, flushSuit) == 2) {
                        Collections.sort(combo2);
                        Card flushCardCombo1 = new Card();
                        Card flushCardCombo2High = combo2.get(0);
                        Card flushCardCombo2Low = combo2.get(1);

                        for(Card c : combo1) {
                            if(c.getSuit() == flushSuit) {
                                flushCardCombo1 = c;
                            }
                        }

                        if(flushCardCombo2High.getRank() > flushCardCombo1.getRank()) {
                            if(numberOfSuitedCardsOnBoard == 4) {
                                return 1;
                            } else {
                                if(flushCardCombo2High.getRank() > rankOfLowestFlushCardOnBoard) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        } else if(flushCardCombo2High.getRank() == flushCardCombo1.getRank()) {
                            //als flushCardCombo2Low hoger is dan laagste flushKaart op board, dan wint combo2
                            if(flushCardCombo2Low.getRank() > rankOfLowestFlushCardOnBoard) {
                                return 1;
                            } else {
                                return 0;
                            }
                        } else {
                            if(numberOfSuitedCardsOnBoard == 4) {
                                return -1;
                            } else {
                                if(flushCardCombo1.getRank() > rankOfLowestFlushCardOnBoard) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        }
                    }

                    //als combo1 twee kaarten van flushsuit heeft en combo2 één kaart
                    if(getNumberOfFlushCardsInCombo(combo1, flushSuit) == 2 &&
                            getNumberOfFlushCardsInCombo(combo2, flushSuit) == 1) {
                        Collections.sort(combo1);
                        Card flushCardCombo1High = combo1.get(0);
                        Card flushCardCombo1Low = combo1.get(1);
                        Card flushCardCombo2 = new Card();

                        for(Card c : combo2) {
                            if(c.getSuit() == flushSuit) {
                                flushCardCombo2 = c;
                            }
                        }

                        if(flushCardCombo1High.getRank() > flushCardCombo2.getRank()) {
                            if(numberOfSuitedCardsOnBoard == 4) {
                                return -1;
                            } else {
                                if(flushCardCombo1High.getRank() > rankOfLowestFlushCardOnBoard) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        } else if(flushCardCombo1High.getRank() == flushCardCombo2.getRank()) {
                            //als flushCardCombo1Low hoger is dan laagste flushKaart op board, dan wint combo2
                            if(flushCardCombo1Low.getRank() > rankOfLowestFlushCardOnBoard) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else {
                            if(numberOfSuitedCardsOnBoard == 4) {
                                return 1;
                            } else {
                                if(flushCardCombo2.getRank() > rankOfLowestFlushCardOnBoard) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        }
                    }

                    //als beide combos twee kaarten van flushsuit hebben
                    if(getNumberOfFlushCardsInCombo(combo1, flushSuit) == 2 &&
                            getNumberOfFlushCardsInCombo(combo2, flushSuit) == 2) {
                        Collections.sort(combo1);
                        Collections.sort(combo2);
                        Card flushCardCombo1High = combo1.get(0);
                        Card flushCardCombo1Low = combo1.get(1);
                        Card flushCardCombo2High = combo2.get(0);
                        Card flushCardCombo2Low = combo2.get(1);

                        if(flushCardCombo2High.getRank() > flushCardCombo1High.getRank()) {
                            if(numberOfSuitedCardsOnBoard == 4) {
                                return 1;
                            } else {
                                if(flushCardCombo2High.getRank() > rankOfLowestFlushCardOnBoard) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        } else if(flushCardCombo2High.getRank() == flushCardCombo1High.getRank()) {
                            if(flushCardCombo2Low.getRank() > flushCardCombo1Low.getRank()) {
                                if(flushCardCombo2Low.getRank() > rankOfLowestFlushCardOnBoard) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            } else if(flushCardCombo2Low.getRank() == flushCardCombo1Low.getRank()) {
                                return 0;
                            } else {
                                if(flushCardCombo1Low.getRank() > rankOfLowestFlushCardOnBoard) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        } else {
                            if(numberOfSuitedCardsOnBoard == 4) {
                                return -1;
                            } else {
                                if(flushCardCombo1High.getRank() > rankOfLowestFlushCardOnBoard) {
                                    return -1;
                                } else {
                                    return 0;
                                }
                            }
                        }
                    }

                    //als combo1 geen flushkaarten heeft
                    if(getNumberOfFlushCardsInCombo(combo1, flushSuit) == 0) {
                        //als combo2 ook geen flushkaarten heeft
                        if(getNumberOfFlushCardsInCombo(combo2, flushSuit) == 0) {
                            return 0;
                        } else {
                            Collections.sort(combo2);
                            if(combo2.get(0).getRank() > rankOfLowestFlushCardOnBoard) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    }

                    //als combo2 geen flushkaarten heeft
                    if(getNumberOfFlushCardsInCombo(combo2, flushSuit) == 0) {
                        //als combo1 ook geen flushkaarten heeft
                        if(getNumberOfFlushCardsInCombo(combo1, flushSuit) == 0) {
                            return 0;
                        } else {
                            Collections.sort(combo1);
                            if(combo1.get(0).getRank() > rankOfLowestFlushCardOnBoard) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    }
                }
                System.out.println("Should never come here");
                return 0;
            }
        };
    }

    private Map<Integer, Set<Set<Card>>> removeDuplicateCombos(Map<Integer, Set<Set<Card>>> sortedCombos, List<Card> board) {
        Map<Integer, Set<Set<Card>>> fullHouseCombos = new FullHouseEvaluator().getFullHouseCombos(board);
        Map<Integer, Set<Set<Card>>> fourOfAKindCombos = new FourOfAKindEvaluator().getFourOfAKindCombos(board);
        Map<Integer, Set<Set<Card>>> straightFlushCombos = new StraightFlushEvaluator().getStraightFlushCombos(board);

        sortedCombos = removeDuplicateCombosPerCategory(straightFlushCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(fourOfAKindCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(fullHouseCombos, sortedCombos);

        return sortedCombos;
    }
}
