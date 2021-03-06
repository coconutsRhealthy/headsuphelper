package com.lennart.model.boardevaluation;

import com.lennart.model.card.Card;
import com.lennart.model.action.actionbuilders.ActionBuilderUtil;

import java.util.*;

/**
 * Created by Lennart Popma on 8/9/2016.
 */
public class StraightEvaluator extends BoardEvaluator implements ComboComparatorRankOnly {

    private Map<Integer, Set<Set<Card>>> combosThatMakeStraight;
    private int numberOfStraightsOnFlop;
    private int numberOfStraightsOnTurn;
    private int numberOfStraightsOnRiver;
    private List<Card> board;
    private FlushEvaluator flushEvaluator;
    private FullHouseEvaluator fullHouseEvaluator;
    private FourOfAKindEvaluator fourOfAKindEvaluator;
    private StraightFlushEvaluator straightFlushEvaluator;

    public StraightEvaluator(List<Card> board, FlushEvaluator flushEvaluator, FullHouseEvaluator fullHouseEvaluator,
                             FourOfAKindEvaluator fourOfAKindEvaluator, StraightFlushEvaluator straightFlushEvaluator) {
        this.board = board;
        this.flushEvaluator = flushEvaluator;
        this.fullHouseEvaluator = fullHouseEvaluator;
        this.fourOfAKindEvaluator = fourOfAKindEvaluator;
        this.straightFlushEvaluator = straightFlushEvaluator;
        getMapOfStraightCombosInitialize(board);
    }

    public StraightEvaluator() {
        //This default constructor can only be used in StraightFlushEvaluator. In addition, it is needed for
        //classes that extend StraightEvaluator.
    }

    public Map<Integer, Set<Set<Card>>> getMapOfStraightCombos() {
        return combosThatMakeStraight;
    }

    private void getMapOfStraightCombosInitialize(List<Card> board) {
        Map<Integer, Set<Set<Card>>> sortedCombos;
        List<List<Integer>> straightCombosList = getCombosThatMakeStraight(board);
        Map<Integer, List<Card>> straightCombosArtificialSuits = convertListOfRankOnlyCombosToCardComboMap(straightCombosList);
        Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(straightCombosArtificialSuits, board, this);
        sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
        sortedCombos = removeDuplicateCombos(sortedCombos);

        combosThatMakeStraight = sortedCombos;
    }

    public Map<Integer, Set<Set<Card>>> getMapOfStraightCombosForStraightFLushEvaluator(List<Card> board) {
        List<List<Integer>> straightCombosList = getCombosThatMakeStraight(board);
        Map<Integer, List<Card>> straightCombosArtificialSuits = convertListOfRankOnlyCombosToCardComboMap(straightCombosList);
        Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(straightCombosArtificialSuits, board, this);
        return convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
    }

    public List<List<Integer>> getCombosThatMakeStraight(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        boardRanks = removeDoubleEntriesInList(boardRanks);

        Map<Integer, List<List<Integer>>> listsOfFoundCombos = new TreeMap();
        for(int i = 1; i <= 7; i++) {
            listsOfFoundCombos.put(i, new ArrayList<>());
        }

        List<List<Integer>> allCombosThatMakeStraight = new ArrayList<List<Integer>>();

        Map <Integer, List<Integer>> threeCardSubBoardRankLists = getSubBoardRankLists(3, boardRanks);
        Map <Integer, List<Integer>> fourCardSubBoardRankLists = getSubBoardRankLists(4, boardRanks);

        if(boardRanks.size() < 4) {
            listsOfFoundCombos.get(1).addAll(getTwoCardsThatMakeStraight(board, boardRanks, 5));
            allCombosThatMakeStraight.addAll(listsOfFoundCombos.get(1));
            return allCombosThatMakeStraight;
        }

        if(boardRanks.size() == 4) {
            listsOfFoundCombos.get(1).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(1), 5));
            listsOfFoundCombos.get(2).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(2), 5));
            listsOfFoundCombos.get(4).addAll(getOneCardThatMakeStraight(board, fourCardSubBoardRankLists.get(1), 5));

            for(int i = 1; i <= listsOfFoundCombos.size(); i++) {
                allCombosThatMakeStraight.addAll(listsOfFoundCombos.get(i));
            }

            allCombosThatMakeStraight = removeDoubleEntriesInList(allCombosThatMakeStraight);

            return allCombosThatMakeStraight;
        }

        if(boardRanks.size() == 5) {
            listsOfFoundCombos.get(1).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(1), 5));
            listsOfFoundCombos.get(2).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(2), 5));
            listsOfFoundCombos.get(3).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(3), 5));
            listsOfFoundCombos.get(4).addAll(getOneCardThatMakeStraight(board, fourCardSubBoardRankLists.get(1), 5));
            listsOfFoundCombos.get(5).addAll(getOneCardThatMakeStraight(board, fourCardSubBoardRankLists.get(2), 5));

            for(int i = 1; i <= listsOfFoundCombos.size(); i++) {
                allCombosThatMakeStraight.addAll(listsOfFoundCombos.get(i));
            }

            if(isBoardConnected(board)) {
                Map<Integer, List<Card>> allPossibleStartHands = ActionBuilderUtil.getAllPossibleStartHandsAsList();
                allPossibleStartHands = clearStartHandsMapOfStartHandsThatContainCardsOnTheBoard(allPossibleStartHands, board);
                Map<Integer, List<Integer>> allPossibleStartHandsRankOnly = getAllPossibleStartHandsRankOnly(allPossibleStartHands);

                for (Map.Entry<Integer, List<Integer>> entry : allPossibleStartHandsRankOnly.entrySet()) {
                    allCombosThatMakeStraight.add(entry.getValue());
                }
            }

            allCombosThatMakeStraight = removeDoubleEntriesInList(allCombosThatMakeStraight);
            return allCombosThatMakeStraight;
        }

        if(boardRanks.size() == 6) {
            listsOfFoundCombos.get(1).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(1), 5));
            listsOfFoundCombos.get(2).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(2), 5));
            listsOfFoundCombos.get(3).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(3), 5));
            listsOfFoundCombos.get(4).addAll(getTwoCardsThatMakeStraight(board, threeCardSubBoardRankLists.get(4), 5));

            listsOfFoundCombos.get(5).addAll(getOneCardThatMakeStraight(board, fourCardSubBoardRankLists.get(1), 5));
            listsOfFoundCombos.get(6).addAll(getOneCardThatMakeStraight(board, fourCardSubBoardRankLists.get(2), 5));
            listsOfFoundCombos.get(7).addAll(getOneCardThatMakeStraight(board, fourCardSubBoardRankLists.get(3), 5));

            for(int i = 1; i <= listsOfFoundCombos.size(); i++) {
                allCombosThatMakeStraight.addAll(listsOfFoundCombos.get(i));
            }

            allCombosThatMakeStraight = removeDoubleEntriesInList(allCombosThatMakeStraight);

            return allCombosThatMakeStraight;
        }
        return new ArrayList<>();
    }

    @Override
    public Comparator<List<Integer>> getComboComparatorRankOnly(List<Card> board) {
        return new Comparator<List<Integer>>() {
            @Override
            public int compare(List<Integer> combo1, List<Integer> combo2) {
                List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

                List<Integer> combo1PlusBoardRanks = new ArrayList<>();
                List<Integer> combo2PlusBoardRanks = new ArrayList<>();

                combo1PlusBoardRanks.addAll(combo1);
                combo1PlusBoardRanks.addAll(boardRanks);
                combo2PlusBoardRanks.addAll(combo2);
                combo2PlusBoardRanks.addAll(boardRanks);

                int highestStraightThatIsPresentInCombo1PlusBoardRanks = 0;
                int highestStraightThatIsPresentInCombo2PlusBoardRanks = 0;

                Map<Integer, List<Integer>> allPossibleStraights = getAllPossibleFiveConnectingCards();


                for (Map.Entry<Integer, List<Integer>> entry : allPossibleStraights.entrySet()) {
                    if(combo1PlusBoardRanks.containsAll(entry.getValue())) {
                        highestStraightThatIsPresentInCombo1PlusBoardRanks = entry.getKey();
                    }

                    if(combo2PlusBoardRanks.containsAll(entry.getValue())) {
                        highestStraightThatIsPresentInCombo2PlusBoardRanks = entry.getKey();
                    }
                }

                if(highestStraightThatIsPresentInCombo2PlusBoardRanks > highestStraightThatIsPresentInCombo1PlusBoardRanks) {
                    return 1;
                } else if (highestStraightThatIsPresentInCombo2PlusBoardRanks == highestStraightThatIsPresentInCombo1PlusBoardRanks) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };
    }

    //helper methods
    protected boolean comboIsAStraightComboOnTheBoard (List<Integer> combo, List<Card> board) {
        List<List<Integer>> allCombosThatMakeStraight = getCombosThatMakeStraight(board);
        for(List<Integer> straightCombo : allCombosThatMakeStraight) {
            if(combo.equals(straightCombo)) {
                return true;
            }
        }
        return false;
    }

    protected List<Integer> getHighestFiveConnectingCardsOnBoard(List<Integer> board, List<Integer> combo) {
        List<Integer> boardPlusCombo = new ArrayList<>();
        boardPlusCombo.addAll(board);
        boardPlusCombo.addAll(combo);
        Map<Integer, List<Integer>> allPossibleFiveConnectingCards = getAllPossibleFiveConnectingCards();
        Map<Integer, List<Integer>> fiveConnectingCardsThatArePresentOnBoard = new HashMap<>();

        int counter = 0;
        for(int i = 0; i < allPossibleFiveConnectingCards.size(); i++) {
            if(boardPlusCombo.containsAll(allPossibleFiveConnectingCards.get(i))) {
                fiveConnectingCardsThatArePresentOnBoard.put(counter, allPossibleFiveConnectingCards.get(i));
                counter++;
            }
        }

        List<Integer> highestFiveConnectingCards = fiveConnectingCardsThatArePresentOnBoard.get(counter-1);
        return highestFiveConnectingCards;
    }

    private Map <Integer, List<Integer>> getSubBoardRankLists(int numberOValuesInSublist, List<Integer> boardRanks) {
        int numberOfLists;
        if(boardRanks.size() > numberOValuesInSublist) {
            numberOfLists = 1 + (boardRanks.size() - numberOValuesInSublist);
        } else {
            numberOfLists = 1;
        }

        Map <Integer, List<Integer>> subBoardRankLists = new TreeMap();
        int counter = 0;
        for(int i = 1; i <= numberOfLists; i++) {
            subBoardRankLists.put(i, new ArrayList<Integer>());
            if(boardRanks.size() < numberOValuesInSublist) {
                for(int z = counter; z < counter + boardRanks.size(); z++) {
                    subBoardRankLists.get(i).add(boardRanks.get(z));
                }
                counter++;
            } else {
                for(int z = counter; z < counter + numberOValuesInSublist; z++) {
                    subBoardRankLists.get(i).add(boardRanks.get(z));
                }
                counter++;
            }
        }
        return subBoardRankLists;
    }

    private List<List<Integer>> getTwoCardsThatMakeStraight(List<Card> board, List<Integer> subBoardRanks, int number) {
        List<Integer> combo = new ArrayList<Integer>();
        List<List<Integer>> straightCombos = new ArrayList<List<Integer>>();
        List<Integer> subBoardRanksCopy = new ArrayList<Integer>();
        subBoardRanksCopy.addAll(subBoardRanks);
        for(int i = 1; i < 15; i++) {
            combo.clear();
            combo.add(i);
            for(int j = 14; j > i; j--) {
                combo.add(j);
                subBoardRanks.addAll(combo);
                Collections.sort(subBoardRanks);
                int connectingCardCounter = 1;
                for(int z = 0; z < (subBoardRanks.size()-1); z++) {
                    if(subBoardRanks.get(z) + 1 == subBoardRanks.get(z+1)) {
                        connectingCardCounter++;
                    }
                }
                if(connectingCardCounter == number) {
                    if(board.size() == 5 && isBoardConnected(board)) {
                        if(combo.get(0) == getValueOfHighestCardOnBoard(board) + 1 || combo.get(1) == getValueOfHighestCardOnBoard(board) + 1) {
                            if(comboContainsLowAce(combo)) {
                                List<Integer> convertedCombo = convertComboWithLowAceToHighAce(combo);
                                List<Integer> copiedCombo = makeCopyOfComboToAddToReturnList(convertedCombo);
                                straightCombos.add(copiedCombo);
                            }
                            else {
                                Collections.sort(combo);
                                List<Integer> copiedCombo = makeCopyOfComboToAddToReturnList(combo);
                                straightCombos.add(copiedCombo);
                            }
                        }
                    }
                    else {
                        if(comboContainsLowAce(combo)) {
                            List<Integer> convertedCombo = convertComboWithLowAceToHighAce(combo);
                            List<Integer> copiedCombo = makeCopyOfComboToAddToReturnList(convertedCombo);
                            straightCombos.add(copiedCombo);
                        }
                        else {
                            Collections.sort(combo);
                            List<Integer> copiedCombo = makeCopyOfComboToAddToReturnList(combo);
                            straightCombos.add(copiedCombo);
                        }
                    }
                }
                subBoardRanks.clear();
                subBoardRanks.addAll(subBoardRanksCopy);

                if(boardContainsAce(board)) {
                    subBoardRanks = addLowAceToSubBoardRanksAndRemoveHighAceIfPresent(subBoardRanks, subBoardRanks.size());
                    subBoardRanks.addAll(combo);
                    Collections.sort(subBoardRanks);
                    int connectingCardCounterAceBoard = 1;
                    for(int z = 0; z < (subBoardRanks.size()-1); z++) {
                        if(subBoardRanks.get(z) + 1 == subBoardRanks.get(z+1)) {
                            connectingCardCounterAceBoard++;
                        }
                    }
                    if(connectingCardCounterAceBoard == number) {
                        if (!isBoardConnected(board)) {
                            Collections.sort(combo);
                            List<Integer> copiedCombo = makeCopyOfComboToAddToReturnList(combo);
                            straightCombos.add(copiedCombo);
                        }
                        if(isBoardConnected(board) && !board.contains(13)) {
                            if(combo.get(0) == board.size() + 1 || combo.get(1) == board.size() + 1) {
                                Collections.sort(combo);
                                List<Integer> copiedCombo = makeCopyOfComboToAddToReturnList(combo);
                                straightCombos.add(copiedCombo);
                            }
                        }
                    }
                    subBoardRanks.clear();
                    subBoardRanks.addAll(subBoardRanksCopy);
                }
                combo.remove(combo.size()-1);
            }
        }
        return straightCombos;
    }

    private List<List<Integer>> getOneCardThatMakeStraight(List<Card> board, List<Integer> subBoardRanks, int number) {
        final List<Integer> combo = new ArrayList<Integer>();
        final List<List<Integer>> straightCombos = new ArrayList<List<Integer>>();
        List<Integer> subBoardRanksCopy = new ArrayList<Integer>();
        subBoardRanksCopy.addAll(subBoardRanks);

        class HelperClassForInnerMethod {
            private void addCombos() {
                for(int z = 2; z < 15; z++) {
                    List<Integer> createdCombo = addSecondCardToCreateComboWhenSingleCardMakesStraight(combo, z);
                    if(comboContainsLowAce(createdCombo)) {
                        List<Integer> convertedCombo = convertComboWithLowAceToHighAce(createdCombo);
                        straightCombos.add(convertedCombo);
                    }
                    else {
                        straightCombos.add(createdCombo);
                    }
                }
            }
        }

        for(int i = 1; i < 15; i++) {
            combo.clear();
            combo.add(i);
            subBoardRanks.addAll(combo);
            Collections.sort(subBoardRanks);
            int connectingCardCounter = 1;
            for(int z = 0; z < (subBoardRanks.size()-1); z++) {
                if(subBoardRanks.get(z) + 1 == subBoardRanks.get(z+1)) {
                    connectingCardCounter++;
                }
            }
            if(connectingCardCounter == number) {
                if(!isBoardConnected(board) || board.size() == number - 1) {
                    HelperClassForInnerMethod h = new HelperClassForInnerMethod();
                    h.addCombos();
                }
                else {
                    if(board.size() == number) {
                        if(combo.get(0) == getValueOfHighestCardOnBoard(board) + 1) {
                            HelperClassForInnerMethod h = new HelperClassForInnerMethod();
                            h.addCombos();
                        }
                    }
                }
            }
            subBoardRanks.clear();
            subBoardRanks.addAll(subBoardRanksCopy);

            if(boardContainsAce(board)) {
                subBoardRanks = addLowAceToSubBoardRanksAndRemoveHighAceIfPresent(subBoardRanks, subBoardRanks.size());
                subBoardRanks.addAll(combo);
                Collections.sort(subBoardRanks);
                int connectingCardCounterAceBoard = 1;
                for(int z = 0; z < (subBoardRanks.size()-1); z++) {
                    if(subBoardRanks.get(z) + 1 == subBoardRanks.get(z+1)) {
                        connectingCardCounterAceBoard++;
                    }
                }
                if(connectingCardCounterAceBoard == number) {
                    if (!isBoardConnected(board)) {
                        HelperClassForInnerMethod h = new HelperClassForInnerMethod();
                        h.addCombos();
                    }
                }
                if(isBoardConnected(board) && !subBoardRanksCopy.contains(13)) {
                    if(combo.get(0) == board.size() + 1) {
                        HelperClassForInnerMethod h = new HelperClassForInnerMethod();
                        h.addCombos();
                    }
                }
                subBoardRanks.clear();
                subBoardRanks.addAll(subBoardRanksCopy);
            }
        }
        return straightCombos;
    }

    private Map<Integer, List<Card>> convertListOfRankOnlyCombosToCardComboMap (List<List<Integer>> rankOnlyComboList) {
        Map<Integer, List<Card>> comboMap = new HashMap<>();
        for(List<Integer> l : rankOnlyComboList) {
            List<Card> cardListToAddToMap = convertIntegerBoardToArtificialCardBoard(l);
            comboMap.put(comboMap.size(), cardListToAddToMap);
        }
        return comboMap;
    }

    private Map<Integer, Set<Set<Card>>> removeDuplicateCombos(Map<Integer, Set<Set<Card>>> sortedCombos) {
        Map<Integer, Set<Set<Card>>> flushCombos = flushEvaluator.getFlushCombos();
        Map<Integer, Set<Set<Card>>> fullHouseCombos = fullHouseEvaluator.getFullHouseCombos();
        Map<Integer, Set<Set<Card>>> fourOfAKindCombos = fourOfAKindEvaluator.getFourOfAKindCombos();
        Map<Integer, Set<Set<Card>>> straightFlushCombos = straightFlushEvaluator.getStraightFlushCombos();

        sortedCombos = removeDuplicateCombosPerCategory(straightFlushCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(fourOfAKindCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(fullHouseCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(flushCombos, sortedCombos);

        return sortedCombos;
    }

    public int getNumberOfStraightsOnFlop() {
        return numberOfStraightsOnFlop;
    }

    public void setNumberOfStraightsOnFlop(int numberOfStraightsOnFlop) {
        this.numberOfStraightsOnFlop = numberOfStraightsOnFlop;
    }

    public int getNumberOfStraightsOnTurn() {
        return numberOfStraightsOnTurn;
    }

    public void setNumberOfStraightsOnTurn(int numberOfStraightsOnTurn) {
        this.numberOfStraightsOnTurn = numberOfStraightsOnTurn;
    }

    public int getNumberOfStraightsOnRiver() {
        return numberOfStraightsOnRiver;
    }

    public void setNumberOfStraightsOnRiver(int numberOfStraightsOnRiver) {
        this.numberOfStraightsOnRiver = numberOfStraightsOnRiver;
    }

    private void setNumberOfStraights(int numberOfStraights) {
        if(board.size() == 3) {
            setNumberOfStraightsOnFlop(numberOfStraights);
        } else if(board.size() == 4) {
            setNumberOfStraightsOnTurn(numberOfStraights);
        } else if(board.size() == 5) {
            setNumberOfStraightsOnRiver(numberOfStraights);
        }
    }
}
