package com.lennart.model;

import java.util.*;

/**
 * Created by LPO10346 on 8/9/2016.
 */
public class StraightEvaluator extends BoardEvaluator {

    public static List<List<Integer>> getCombosThatMakeStraight(List<Card> board) {
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
        return null;
    }

    public static Map<Integer, List<Integer>> getCombosThatGiveOosdOrDoubleGutter(List<Card> board) {
        Map<List<Integer>, List<List<Integer>>> allStraightDrawCombos = getCombosThatGiveAnyStraightDraw(board);
        Map<Integer, List<Integer>> oosdCombos = new HashMap<>();
        int counter = 0;

        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : allStraightDrawCombos.entrySet()) {
            if (entry.getValue().size() > 20) {
                oosdCombos.put(counter, entry.getKey());
                counter++;
            }
        }

        if(board.size() == 4) {
            oosdCombos = addSpecificOosdCombosIfNecessary(oosdCombos, board);
        }

        return oosdCombos;
    }

    public static Map<Integer, List<Integer>> getCombosThatGiveGutshot (List<Card> board) {
        Map<List<Integer>, List<List<Integer>>> allStraightDrawCombos = getCombosThatGiveAnyStraightDraw(board);
        Map<Integer, List<Integer>> gutshotCombos = new HashMap<>();
        int counter = 0;

        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : allStraightDrawCombos.entrySet()) {
            if (entry.getValue().size() > 9 && entry.getValue().size() < 15) {
                gutshotCombos.put(counter, entry.getKey());
                counter++;
            }
        }

        if(board.size() == 4) {
            gutshotCombos = removeSpecificGutshotCombosIfNecessary(gutshotCombos, board);
            gutshotCombos = addSpecificGutshotCombosIfNecessary(gutshotCombos, board);
            if(isBoardConnected(board)) {
                gutshotCombos = addSpecificGutshotCombosIfBoardIsConnected(gutshotCombos, board);
            }
        }
        return gutshotCombos;
    }

    public static Map<Integer, List<Integer>> getCombosThatGiveBackDoorStraightDraw(List<Card> board) {
        Map<List<Integer>, List<List<Integer>>> allStraightDrawCombos = getCombosThatGiveAnyStraightDraw(board);
        Map<Integer, List<Integer>> backdoorCombos = new HashMap<>();
        int counter = 0;

        if(board.size() < 4) {
            for (Map.Entry<List<Integer>, List<List<Integer>>> entry : allStraightDrawCombos.entrySet()) {
                if (entry.getValue().size() < 8) {
                    backdoorCombos.put(counter, entry.getKey());
                    counter++;
                }
            }
            if(isBoardConnected(board)) {
                backdoorCombos = addSpecificBackdoorCombosIfNecessary(backdoorCombos, board);
            }
        }
        return backdoorCombos;
    }

    //helper methods
    private static Map<Integer, List<Integer>> addSpecificOosdCombosIfNecessary (Map<Integer, List<Integer>> oosdCombos, List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        boolean boardMatchesWithTextureThatWronglyRecognizesOosdAsGutshot = false;

        Map<Integer, List<Integer>> mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot = getMapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot();

        for(int i = 0; i < mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot.size(); i++) {
            if (mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot.get(i).equals(boardRanks)) {
                boardMatchesWithTextureThatWronglyRecognizesOosdAsGutshot = true;
                break;
            }
        }

        if(boardMatchesWithTextureThatWronglyRecognizesOosdAsGutshot) {
            int integerValueOfCombosThatNeedToBeAdded;
            if(boardContainsAce(board)) {
                integerValueOfCombosThatNeedToBeAdded = boardRanks.get(boardRanks.size()-2) + 1;
            } else {
                integerValueOfCombosThatNeedToBeAdded = boardRanks.get(boardRanks.size()-1) + 1;
            }

            Map<Integer, List<Integer>> combosToBeAdded = new HashMap<>();
            for(int i = 0; i < 13; i++) {
                combosToBeAdded.put(i, new ArrayList<>());
                combosToBeAdded.get(i).add(integerValueOfCombosThatNeedToBeAdded);
                combosToBeAdded.get(i).add(i + 2);
            }

            List<Integer> exceptionComboBecauseItMakesStraight = new ArrayList<>();
            if(boardContainsAce(board)) {
                exceptionComboBecauseItMakesStraight.add(boardRanks.get(boardRanks.size()-2) + 1);
                exceptionComboBecauseItMakesStraight.add(boardRanks.get(boardRanks.size()-2) + 2);
                combosToBeAdded.remove(getKeyByValue(combosToBeAdded, exceptionComboBecauseItMakesStraight));
            } else {
                exceptionComboBecauseItMakesStraight.add(boardRanks.get(boardRanks.size()-1) + 1);
                exceptionComboBecauseItMakesStraight.add(boardRanks.get(boardRanks.size()-1) + 2);
                combosToBeAdded.remove(getKeyByValue(combosToBeAdded, exceptionComboBecauseItMakesStraight));
            }

            int keyForOosdCombos = oosdCombos.size();

            for (Map.Entry<Integer, List<Integer>> entry : combosToBeAdded.entrySet()) {
                oosdCombos.put(keyForOosdCombos, combosToBeAdded.get(entry.getKey()));
                keyForOosdCombos++;
            }
        }
        return oosdCombos;
    }

    private static Map<Integer, List<Integer>> getMapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot() {
        int a = 2;
        int b = 4;
        int c = 5;
        int d = 6;

        Map<Integer, List<Integer>> mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot = new HashMap<>();
        List<Integer> firstList = new ArrayList<>();
        firstList.add(3);
        firstList.add(4);
        firstList.add(5);
        firstList.add(14);
        mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot.put(0, firstList);


        for(int i = 1; i < 8; i++) {
            mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot.put(i, new ArrayList<>());
            mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot.get(i).add(a);
            mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot.get(i).add(b);
            mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot.get(i).add(c);
            mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot.get(i).add(d);

            a++;
            b++;
            c++;
            d++;
        }
        return mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot;
    }

    private static Map<Integer, List<Integer>> removeSpecificGutshotCombosIfNecessary (Map<Integer, List<Integer>> gutshotCombos, List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        boolean boardMatchesWithTextureThatWronglyRecognizesOosdAsGutshot = false;

        Map<Integer, List<Integer>> mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot = getMapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot();

        for(int i = 0; i < mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot.size(); i++) {
            if (mapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot.get(i).equals(boardRanks)) {
                boardMatchesWithTextureThatWronglyRecognizesOosdAsGutshot = true;
                break;
            }
        }

        if(boardMatchesWithTextureThatWronglyRecognizesOosdAsGutshot) {
            int integerValueOfCombosThatNeedToBeRemoved;
            if(boardContainsAce(board)) {
                integerValueOfCombosThatNeedToBeRemoved = boardRanks.get(boardRanks.size()-2) + 1;
            } else {
                integerValueOfCombosThatNeedToBeRemoved = boardRanks.get(boardRanks.size()-1) + 1;
            }

            Map<Integer, List<Integer>> combosToBeRemoved = new HashMap<>();
            for(int i = 0; i < 13; i++) {
                combosToBeRemoved.put(i, new ArrayList<>());
                combosToBeRemoved.get(i).add(integerValueOfCombosThatNeedToBeRemoved);
                combosToBeRemoved.get(i).add(i + 2);
            }

            for(int i = 0; i < combosToBeRemoved.size(); i++) {
                gutshotCombos.remove(getKeyByValue(gutshotCombos, combosToBeRemoved.get(i)));
            }

            for(int i = 0; i < combosToBeRemoved.size(); i++) {
                Collections.sort(combosToBeRemoved.get(i));
            }

            for(int i = 0; i < combosToBeRemoved.size(); i++) {
                gutshotCombos.remove(getKeyByValue(gutshotCombos, combosToBeRemoved.get(i)));
            }
        }
        return gutshotCombos;
    }

    private static Map<Integer, List<Integer>> addSpecificGutshotCombosIfBoardIsConnected (Map<Integer, List<Integer>> gutshotCombos, List<Card> board) {
        int highestBoardCard;
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        if(!boardContainsAce(board)) {
            highestBoardCard = getValueOfHighestCardOnBoard(board);
        } else {
            highestBoardCard = boardRanks.get(boardRanks.size() - 2);
        }

        if(highestBoardCard < 13) {
            List<Integer> comboToBeAdded = new ArrayList<>();
            for(int i = 2; i < 15; i++) {
                comboToBeAdded.add(highestBoardCard + 2);
                comboToBeAdded.add(i);
                List<Integer> copyOfComboToBeAdded = new ArrayList<>();
                copyOfComboToBeAdded.addAll(comboToBeAdded);
                Collections.sort(copyOfComboToBeAdded);
                gutshotCombos.put(gutshotCombos.size(), copyOfComboToBeAdded);
                comboToBeAdded.clear();
            }
        }

        if(highestBoardCard < 13) {
            if(!boardContainsAce(board)) {
                List<Integer> comboToBeAdded = new ArrayList<>();
                comboToBeAdded.add(boardRanks.get(boardRanks.size() - 1) + 1);
                if(boardRanks.get(0) != 2) {
                    comboToBeAdded.add(boardRanks.get(0) - 1);
                } else {
                    comboToBeAdded.add(14);
                }
                gutshotCombos.put(gutshotCombos.size(), comboToBeAdded);
            }
        }
        return gutshotCombos;
    }

    private static Map<Integer, List<Integer>> addSpecificGutshotCombosIfNecessary (Map<Integer, List<Integer>> gutshotCombos, List<Card> board) {
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        boolean boardMatchesWithTextureThatWronglyRecognizesOosdAsGutshot = false;

        int a = 2;
        int b = 3;
        int c = 4;
        int d = 6;

        Map<Integer, List<Integer>> mapOfBoardTexturesThatWronglyNotRecognizeGutshot = new HashMap<>();
        List<Integer> firstList = new ArrayList<>();
        firstList.add(2);
        firstList.add(3);
        firstList.add(5);
        firstList.add(14);
        mapOfBoardTexturesThatWronglyNotRecognizeGutshot.put(0, firstList);


        for(int i = 1; i < 9; i++) {
            mapOfBoardTexturesThatWronglyNotRecognizeGutshot.put(i, new ArrayList<>());
            mapOfBoardTexturesThatWronglyNotRecognizeGutshot.get(i).add(a);
            mapOfBoardTexturesThatWronglyNotRecognizeGutshot.get(i).add(b);
            mapOfBoardTexturesThatWronglyNotRecognizeGutshot.get(i).add(c);
            mapOfBoardTexturesThatWronglyNotRecognizeGutshot.get(i).add(d);

            a++;
            b++;
            c++;
            d++;
        }

        for(int i = 0; i <mapOfBoardTexturesThatWronglyNotRecognizeGutshot.size(); i++) {
            if (mapOfBoardTexturesThatWronglyNotRecognizeGutshot.get(i).equals(boardRanks)) {
                boardMatchesWithTextureThatWronglyRecognizesOosdAsGutshot = true;
                break;
            }
        }

        int valueOfCardInStraightComboThatHasToBeIncluded;
        if(boardMatchesWithTextureThatWronglyRecognizesOosdAsGutshot) {
            if(boardContainsAce(board)) {
                valueOfCardInStraightComboThatHasToBeIncluded = boardRanks.get(boardRanks.size() -2) - 1;
            } else {
                valueOfCardInStraightComboThatHasToBeIncluded = boardRanks.get(boardRanks.size() -1) - 1;
            }

            List<List<Integer>> straightCombos = getCombosThatMakeStraight(board);
            List<List<Integer>> straightCombosThatMakeGutshot = new ArrayList<>();
            for(int i = 0; i < straightCombos.size(); i++) {
                if(valueOfCardInStraightComboThatHasToBeIncluded == 4) {
                    return gutshotCombos;
                }
                if(valueOfCardInStraightComboThatHasToBeIncluded == 12) {
                    List<Integer> comboToBeAdded = new ArrayList<>();
                    comboToBeAdded.add(8);
                    comboToBeAdded.add(12);
                    gutshotCombos.put(gutshotCombos.size(), comboToBeAdded);
                    return gutshotCombos;
                }

                if(straightCombos.get(i).contains(valueOfCardInStraightComboThatHasToBeIncluded + 2) || straightCombos.get(i).contains(valueOfCardInStraightComboThatHasToBeIncluded - 4)) {
                    straightCombosThatMakeGutshot.add(straightCombos.get(i));
                }
            }

            Integer x = gutshotCombos.size() - 1;
            for(int i = 0; i < straightCombosThatMakeGutshot.size(); i++) {
                gutshotCombos.put(x, straightCombosThatMakeGutshot.get(i));
                x++;
            }
        }
        return gutshotCombos;
    }

    private static Map<Integer, List<Integer>> addSpecificBackdoorCombosIfNecessary (Map<Integer, List<Integer>> backdoorCombos, List<Card> board) {
        int highestBoardCard;
        if(!boardContainsAce(board)) {
            highestBoardCard = getValueOfHighestCardOnBoard(board);
        } else {
            List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
            highestBoardCard = boardRanks.get(boardRanks.size() - 2);
        }

        if(highestBoardCard < 11) {
            List<Integer> comboToBeAdded = new ArrayList<>();
            for(int i = 2; i < 15; i++) {
                comboToBeAdded.add(highestBoardCard + 3);
                comboToBeAdded.add(i);
                List<Integer> copyOfComboToBeAdded = new ArrayList<>();
                copyOfComboToBeAdded.addAll(comboToBeAdded);
                Collections.sort(copyOfComboToBeAdded);
                backdoorCombos.put(backdoorCombos.size(), copyOfComboToBeAdded);
                comboToBeAdded.clear();
            }
        }
        return backdoorCombos;
    }

    public static Map<List<Integer>, List<List<Integer>>> getCombosThatGiveAnyStraightDraw(List<Card> board) {
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

    private static Map<List<Integer>, List<List<Integer>>> removeIncorrectStraightCombosFromMap(Map<List<Integer>, List<List<Integer>>> mapOfCombosThatGiveAnyStraightDraw, List<Card> board) {
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

    private static boolean comboIsAStraightComboOnTheBoard (List<Integer> combo, List<Card> board) {
        List<List<Integer>> allCombosThatMakeStraight = getCombosThatMakeStraight(board);
        for(List<Integer> straightCombo : allCombosThatMakeStraight) {
            if(combo.equals(straightCombo)) {
                return true;
            }
        }
        return false;
    }

    private static List<Integer> getHighestFiveConnectingCardsOnBoard(List<Integer> board, List<Integer> combo) {
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


    private static Map<Integer, List<Integer>> getAllPossibleFiveConnectingCards() {
        Map<Integer, List<Integer>> allPossibleStraights = new HashMap<>();
        List<Integer> lowestStraight = new ArrayList<>();
        lowestStraight.add(14);
        lowestStraight.add(2);
        lowestStraight.add(3);
        lowestStraight.add(4);
        lowestStraight.add(5);
        allPossibleStraights.put(0, lowestStraight);

        for(int i = 1; i < 10; i++) {
            allPossibleStraights.put(i, new ArrayList<>());
        }

        int start = 2;
        for(int i = 1; i < allPossibleStraights.size(); i++) {
            for(int z = 0; z < 5; z++) {
                allPossibleStraights.get(i).add(start + z);
            }
            start++;
        }
        return allPossibleStraights;
    }

    private static Map <Integer, List<Integer>> getSubBoardRankLists(int numberOValuesInSublist, List<Integer> boardRanks) {
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

    private static List<List<Integer>> getTwoCardsThatMakeStraight(List<Card> board, List<Integer> subBoardRanks, int number) {
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

    private static List<List<Integer>> getOneCardThatMakeStraight(List<Card> board, List<Integer> subBoardRanks, int number) {
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
}
