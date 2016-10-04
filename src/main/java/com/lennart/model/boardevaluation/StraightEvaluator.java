package com.lennart.model.boardevaluation;

import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by Lennart Popma on 8/9/2016.
 */
public class StraightEvaluator extends BoardEvaluator implements ComboComparatorRankOnly {

    private static Map<Integer, Set<Set<Card>>> combosThatMakeStraight;

    public Map<Integer, Set<Set<Card>>> getMapOfStraightCombos() {
        return combosThatMakeStraight;
    }

    public Map<Integer, Set<Set<Card>>> getMapOfStraightCombosInitialize(List<Card> board) {
        Map<Integer, Set<Set<Card>>> sortedCombos;
        List<List<Integer>> straightCombosList = getCombosThatMakeStraight(board);
        Map<Integer, List<Card>> straightCombosArtificialSuits = convertListOfRankOnlyCombosToCardComboMap(straightCombosList);
        Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(straightCombosArtificialSuits, board, new StraightEvaluator());
        sortedCombos = convertRankComboMapToCardComboMapCorrectedForBoard(rankMap, board);
        sortedCombos = removeDuplicateCombos(sortedCombos, board);

        this.combosThatMakeStraight = sortedCombos;

        return sortedCombos;
    }

    public Map<Integer, Set<Set<Card>>> getMapOfStraightCombosForStraightFLushEvaluator(List<Card> board) {
        List<List<Integer>> straightCombosList = getCombosThatMakeStraight(board);
        Map<Integer, List<Card>> straightCombosArtificialSuits = convertListOfRankOnlyCombosToCardComboMap(straightCombosList);
        Map<Integer, List<List<Integer>>> rankMap = getSortedComboMapRankOnly(straightCombosArtificialSuits, board, new StraightEvaluator());
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
                Map<Integer, List<Card>> allPossibleStartHands = getAllPossibleStartHands();
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


    public Map<List<Integer>, List<List<Integer>>> getCombosThatGiveAnyStraightDraw(List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        Map<Integer, List<Integer>> allCardCombos = getAllPossibleCombos();
        Map<Integer, List<Integer>> fictionalBoardRanks = new HashMap<>();
        List<List<Integer>> allStraightCombos = getCombosThatMakeStraight(board);
        List<List<Integer>> allCardCombosCorrectedForStraightCombos = new ArrayList<>();
        Map<List<Integer>, List<List<Integer>>> mapOfCombosThatGiveAnyStraightDraw = new HashMap<>();

        for(int i = 0; i < allCardCombos.size(); i++) {
            Collections.sort(allCardCombos.get(i));
        }

        if(allStraightCombos != null) {
            for(List<Integer> l : allStraightCombos) {
                Collections.sort(l);
            }
        }

        for (int i = 0; i < allCardCombosCorrectedForStraightCombos.size(); i++) {
            allCardCombos.put(i, allCardCombosCorrectedForStraightCombos.get(i));
        }

        for(int i = 0; i < allCardCombos.size(); i++) {
            List<Integer> copyOfBoardRanks = new ArrayList<>();
            copyOfBoardRanks.addAll(boardRanks);
            fictionalBoardRanks.put(i, copyOfBoardRanks);
            fictionalBoardRanks.get(i).addAll(allCardCombos.get(i));
        }

        for(int i = 0; i < fictionalBoardRanks.size(); i++) {
            List<Card> x = convertIntegerBoardToArtificialCardBoard(fictionalBoardRanks.get(i));
            mapOfCombosThatGiveAnyStraightDraw.put(allCardCombos.get(i), getCombosThatMakeStraight(x));
        }

        Set<List<Integer>> combosThatNeedToBeRemovedFromMap = new HashSet<>();
        for(List<List<Integer>> list : mapOfCombosThatGiveAnyStraightDraw.values()) {
            if(list.isEmpty()) {
                Set<List<Integer>> keysOfCombosThatNeedToBeRemoved = getKeysByValue(mapOfCombosThatGiveAnyStraightDraw, list);
                combosThatNeedToBeRemovedFromMap.addAll(keysOfCombosThatNeedToBeRemoved);
            }
        }

        for(List<Integer> list1 : combosThatNeedToBeRemovedFromMap) {
            mapOfCombosThatGiveAnyStraightDraw.remove(list1);
        }

        mapOfCombosThatGiveAnyStraightDraw = removeIncorrectStraightCombosFromMap(mapOfCombosThatGiveAnyStraightDraw, board);
        return mapOfCombosThatGiveAnyStraightDraw;
    }

    private Map<List<Integer>, List<List<Integer>>> removeIncorrectStraightCombosFromMap(Map<List<Integer>, List<List<Integer>>> mapOfCombosThatGiveAnyStraightDraw, List<Card> board) {
        Map<List<Integer>, List<List<Integer>>> baseMap = mapOfCombosThatGiveAnyStraightDraw;
        Map<Integer, List<Integer>> allCombined = new HashMap<>();
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        int counter = 0;
        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : baseMap.entrySet()) {
            entry.getValue().removeAll(getCombosThatMakeStraight(board));
        }

        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : baseMap.entrySet()) {
            for(int i = 0; i < entry.getValue().size(); i++) {
                List<Integer> newBoardRanks = new ArrayList<>();
                newBoardRanks.addAll(boardRanks);
                allCombined.put(counter, newBoardRanks);
                allCombined.get(counter).addAll(entry.getKey());
                allCombined.get(counter).addAll(entry.getValue().get(i));
                counter++;
            }
        }

        Map<Integer, List<Integer>> allPossibleStraights = getAllPossibleFiveConnectingCards();
        Map<Integer, List<Integer>> allCombinedCopy = new HashMap<>();
        Map<Integer, List<Integer>> highCardsMapFive = new HashMap<>();

        for (int i = 0; i < allCombined.size(); i++) {
            allCombinedCopy.put(i, allCombined.get(i));
        }

        for (int i = 0; i < allCombinedCopy.size(); i++) {
            for (int z = 0; z < allPossibleStraights.size(); z++) {
                Set<Integer> s = new HashSet<>();
                s.addAll(allCombinedCopy.get(i));
                int sizeInitial = s.size();
                s.removeAll(allPossibleStraights.get(z));
                int sizeAfter = s.size();
                if(sizeInitial - sizeAfter == 5) {
                    if(!s.iterator().hasNext()) {
                        highCardsMapFive.put(i, allPossibleStraights.get(z));
                        break;
                    }
                    if(s.iterator().hasNext() && s.iterator().next() != allPossibleStraights.get(z).get(4) + 1) {
                        highCardsMapFive.put(i, allPossibleStraights.get(z));
                        break;
                    }
                }
            }
        }

        int counter2 = 0;
        Map<Integer, List<Integer>> theCorrectKeyMap = new HashMap<>();
        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : baseMap.entrySet()) {
            for(int i = 0; i < entry.getValue().size(); i++) {
                List<Integer> theKeyYouNeed = new ArrayList<>();
                theKeyYouNeed.addAll(entry.getKey());
                theCorrectKeyMap.put(counter2, theKeyYouNeed);
                counter2++;
            }
        }

        Map<Integer, List<Integer>> combosThatHaveToBeRemoved = new HashMap<>();
        int counter3 = 0;

        Map<Integer, List<Integer>> copyHighCardsMapFive = highCardsMapFive;
        Map<Integer, List<Integer>> copyTheCorrectKeyMap = theCorrectKeyMap;
        for(int i = 0; i < theCorrectKeyMap.size(); i++) {
            List<Integer> x = new ArrayList<>();
            x.addAll(copyHighCardsMapFive.get(i));
            x.removeAll(copyTheCorrectKeyMap.get(i));
            if(x.size() > 4) {
                List<Integer> removeCombo = new ArrayList<>();
                removeCombo.addAll(theCorrectKeyMap.get(i));
                combosThatHaveToBeRemoved.put(counter3, removeCombo);
                counter3++;
            }
        }

        for(int i = 0; i < theCorrectKeyMap.size(); i++) {
            if(!(highCardsMapFive.get(i).contains(theCorrectKeyMap.get(i).get(0)))) {
                if(highCardsMapFive.get(i).contains(theCorrectKeyMap.get(i).get(1))) {
                    if(boardRanks.contains(theCorrectKeyMap.get(i).get(1))) {
                        List<Integer> removeCombo = new ArrayList<>();
                        removeCombo.addAll(theCorrectKeyMap.get(i));
                        combosThatHaveToBeRemoved.put(counter3, removeCombo);
                        counter3++;
                    }
                }
            }
            if(!(highCardsMapFive.get(i).contains(theCorrectKeyMap.get(i).get(1)))) {
                if(highCardsMapFive.get(i).contains(theCorrectKeyMap.get(i).get(0))) {
                    if(boardRanks.contains(theCorrectKeyMap.get(i).get(0))) {
                        List<Integer> removeCombo = new ArrayList<>();
                        removeCombo.addAll(theCorrectKeyMap.get(i));
                        combosThatHaveToBeRemoved.put(counter3, removeCombo);
                        counter3++;
                    }
                }
            }
        }

        for(int i = 0; i < theCorrectKeyMap.size(); i++) {
            if(boardRanks.containsAll(theCorrectKeyMap.get(i))) {
                List<Integer> removeCombo = new ArrayList<>();
                removeCombo.addAll(theCorrectKeyMap.get(i));
                combosThatHaveToBeRemoved.put(counter3, removeCombo);
                counter3++;
            }
        }

        List<List<Integer>> cleanedForDoubleEntriesCombosToBeRemoved = new ArrayList<>();
        for(int i = 0; i < combosThatHaveToBeRemoved.size(); i++) {
            cleanedForDoubleEntriesCombosToBeRemoved.add(combosThatHaveToBeRemoved.get(i));
        }

        Set<List<Integer>> hs = new HashSet<>();
        hs.addAll(cleanedForDoubleEntriesCombosToBeRemoved);
        cleanedForDoubleEntriesCombosToBeRemoved.clear();
        cleanedForDoubleEntriesCombosToBeRemoved.addAll(hs);

        combosThatHaveToBeRemoved.clear();

        for(int i = 0; i < cleanedForDoubleEntriesCombosToBeRemoved.size(); i++) {
            combosThatHaveToBeRemoved.put(i, cleanedForDoubleEntriesCombosToBeRemoved.get(i));
        }

        for(int i = 0; i < combosThatHaveToBeRemoved.size(); i++) {
            baseMap.remove(combosThatHaveToBeRemoved.get(i));
        }

        List<List<Integer>> hs2 = new ArrayList<>();
        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : baseMap.entrySet()) {
            if(entry.getValue().isEmpty()) {
                hs2.add(entry.getKey());
            }
        }

        for(int i = 0; i < hs2.size(); i++) {
            baseMap.remove(hs2.get(i));
        }

        Map<List<Integer>, List<List<Integer>>> keysOfWhichSomeValuesNeedToBeRemovedBecauseKeyMakesStraightOnBoard = new HashMap<>();

        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : baseMap.entrySet()) {
            if(comboIsAStraightComboOnTheBoard(entry.getKey(), board)) {
                List<Integer> highestFiveConnectingCards = getHighestFiveConnectingCardsOnBoard(boardRanks, entry.getKey());
                int highestCard = highestFiveConnectingCards.get(4);
                keysOfWhichSomeValuesNeedToBeRemovedBecauseKeyMakesStraightOnBoard.put(entry.getKey(), new ArrayList<>());
                for(List<Integer> list : baseMap.get(entry.getKey())) {
                    if(list.get(0) != highestCard + 1 && list.get(1) != highestCard + 1) {
                        List<Integer> copyOfListToBeRemoved = new ArrayList<>();
                        copyOfListToBeRemoved.addAll(list);
                        keysOfWhichSomeValuesNeedToBeRemovedBecauseKeyMakesStraightOnBoard.get(entry.getKey()).add(copyOfListToBeRemoved);
                    }
                }
            }
        }

        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : baseMap.entrySet()) {
            for (Map.Entry<List<Integer>, List<List<Integer>>> entry2 : keysOfWhichSomeValuesNeedToBeRemovedBecauseKeyMakesStraightOnBoard.entrySet()) {
                if(entry.getKey().equals(entry2.getKey())) {
                    List<List<Integer>> cleanList = new ArrayList<>();
                    cleanList.addAll(entry.getValue());
                    cleanList.removeAll(entry2.getValue());

                    baseMap.get(entry.getKey()).clear();
                    baseMap.get(entry.getKey()).addAll(cleanList);
                }
            }
        }
        return baseMap;
    }

    private boolean comboIsAStraightComboOnTheBoard (List<Integer> combo, List<Card> board) {
        List<List<Integer>> allCombosThatMakeStraight = getCombosThatMakeStraight(board);
        for(List<Integer> straightCombo : allCombosThatMakeStraight) {
            if(combo.equals(straightCombo)) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> getHighestFiveConnectingCardsOnBoard(List<Integer> board, List<Integer> combo) {
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

    private Map<Integer, Set<Set<Card>>> removeDuplicateCombos(Map<Integer, Set<Set<Card>>> sortedCombos, List<Card> board) {
        Map<Integer, Set<Set<Card>>> flushCombos = new FlushEvaluator().getFlushCombosInitialize(board);
        Map<Integer, Set<Set<Card>>> fullHouseCombos = new FullHouseEvaluator().getFullHouseCombos();
        Map<Integer, Set<Set<Card>>> fourOfAKindCombos = new FourOfAKindEvaluator().getFourOfAKindCombos();
        Map<Integer, Set<Set<Card>>> straightFlushCombos = new StraightFlushEvaluator().getStraightFlushCombos();

        sortedCombos = removeDuplicateCombosPerCategory(straightFlushCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(fourOfAKindCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(fullHouseCombos, sortedCombos);
        sortedCombos = removeDuplicateCombosPerCategory(flushCombos, sortedCombos);

        return sortedCombos;
    }
}
