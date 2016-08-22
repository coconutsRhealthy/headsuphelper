package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by LPO10346 on 8/10/2016.
 */
public class FlushEvaluator extends BoardEvaluator {

    public Map<Integer, List<Card>> getFlushCombos (List<Card> board) {
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
            return flushCombos;
        }

        if(numberOfSuitedCards == 3) {
            flushCombos = getAllPossibleSuitedStartHands(flushSuit);
            flushCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushCombos, board);
            return flushCombos;
        }

        if(numberOfSuitedCards == 4 || numberOfSuitedCards == 5) {
            flushCombos = getAllPossibleSuitedStartHands(flushSuit);
            Map<Integer, List<Card>> allStartHands = getAllPossibleStartHands();
            for (Map.Entry<Integer, List<Card>> entry : allStartHands.entrySet()) {
                if(entry.getValue().get(0).getSuit() == flushSuit && entry.getValue().get(1).getSuit() != flushSuit) {
                    flushCombos.put(flushCombos.size(), entry.getValue());
                } else if (entry.getValue().get(0).getSuit() != flushSuit && entry.getValue().get(1).getSuit() == flushSuit) {
                    flushCombos.put(flushCombos.size(), entry.getValue());
                }
            }
            flushCombos = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(flushCombos, board);

            if(numberOfSuitedCards == 5) {
                for(Iterator<Map.Entry<Integer, List<Card>>> it = flushCombos.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<Integer, List<Card>> entry = it.next();
                    List<Card> startHandCardsThatAreHigherThanHighestBoardCard = getStartHandCardsThatAreHigherThanHighestBoardCard(entry.getValue(), board);
                    if(startHandCardsThatAreHigherThanHighestBoardCard.isEmpty()) {
                        it.remove();
                    } else if (startHandCardsThatAreHigherThanHighestBoardCard.size() == 1) {
                        if(startHandCardsThatAreHigherThanHighestBoardCard.get(0).getSuit() != flushSuit) {
                            it.remove();
                        }
                    }
                }
            }
            return flushCombos;
        }
        return null;
    }

    public Map<Integer, List<Card>> getFlushDrawCombos (List<Card> board) {
        return null;
    }

    public List<Card> getStartHandCardsThatAreHigherThanHighestBoardCard(List<Card> startHand, List<Card> board) {
        List<Card> startHandCardsThatAreHigherThanHighestBoardCard = new ArrayList<>();
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        int valueOfHighestBoardCard = boardRanks.get(boardRanks.size() - 1);

        for(Card c : startHand) {
            if(c.getRank() > valueOfHighestBoardCard) {
                startHandCardsThatAreHigherThanHighestBoardCard.add(c);
            }
        }
        return startHandCardsThatAreHigherThanHighestBoardCard;
    }


    public Map<Integer, List<Card>> clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(Map<Integer, List<Card>> allStartHands, List<Card> board) {
        Map<Integer, List<Card>> allStartHandsClearedForBoardCards = new HashMap<>();

        int index = 0;
        for (Map.Entry<Integer, List<Card>> entry : allStartHands.entrySet()) {
            if(Collections.disjoint(entry.getValue(), board)) {
                allStartHandsClearedForBoardCards.put(index, entry.getValue());
                index++;
            }
        }
        return allStartHandsClearedForBoardCards;
    }

    public Map<Integer, List<Card>> getAllStartHandsThatContainASpecificCard(Card card) {
        Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
        Map<Integer, List<Card>> allStartHandsThatContainASpecificCard = new HashMap<>();

        int index = 0;
        for (Map.Entry<Integer, List<Card>> entry : allPossibleStartHands.entrySet()) {
            if(entry.getValue().contains(card)) {
                allStartHandsThatContainASpecificCard.put(index, entry.getValue());
            }
        }
        return allStartHandsThatContainASpecificCard;
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

    public Map<Integer, List<Card>> getAllPossibleStartHands() {
        Map<Integer, List<Card>> allPossibleStartHands = new HashMap<>();
        List<Card> completeCardDeck = getCompleteCardDeck();

        int i = 1;
        for(int z = 0; z < 52; z++) {
            for(int q = 0; q < 52; q++) {
                if(!completeCardDeck.get(z).equals(completeCardDeck.get(q))) {
                    allPossibleStartHands.put(i, new ArrayList<>());
                    allPossibleStartHands.get(i).add(completeCardDeck.get(z));
                    allPossibleStartHands.get(i).add(completeCardDeck.get(q));
                    i++;
                }
            }
        }
        return allPossibleStartHands;
    }

    public List<Card> getCompleteCardDeck() {
        List<Card> completeCardDeck = new ArrayList<>();

        for(int i = 2; i <= 14; i++) {
            for(int z = 1; z <= 4; z++) {
                if(z == 1) {
                    completeCardDeck.add(new Card(i, 's'));
                }
                if(z == 2) {
                    completeCardDeck.add(new Card(i, 'c'));
                }
                if(z == 3) {
                    completeCardDeck.add(new Card(i, 'd'));
                }
                if(z == 4) {
                    completeCardDeck.add(new Card(i, 'h'));
                }
            }
        }
        return completeCardDeck;
    }
}
