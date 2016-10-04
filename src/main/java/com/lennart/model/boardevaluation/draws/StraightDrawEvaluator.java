package com.lennart.model.boardevaluation.draws;

import com.lennart.model.boardevaluation.StraightEvaluator;
import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by LPO10346 on 10/4/2016.
 */
public class StraightDrawEvaluator extends StraightEvaluator {
    public Map<Integer, List<Integer>> getCombosThatGiveOosdOrDoubleGutter(List<Card> board) {
        if(board.size() == 5) {
            return new HashMap<>();
        }

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

    public Map<Integer, List<Integer>> getCombosThatGiveGutshot (List<Card> board) {
        if(board.size() == 5) {
            return new HashMap<>();
        }

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

    public Map<Integer, List<Integer>> getCombosThatGiveBackDoorStraightDraw(List<Card> board) {
        if(board.size() > 3) {
            return new HashMap<>();
        }

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

    public Map<Integer, List<Card>> getStrongStraightDrawCombos(List<Card> board) {
        return null;
    }

    public Map<Integer, List<Card>> getMediumStraightDrawCombos(List<Card> board) {
        return null;
    }

    public Map<Integer, List<Card>> getWeakStraightDrawCombos(List<Card> board) {
        return null;
    }




    //helper methods
    private Map<Integer, List<Integer>> addSpecificOosdCombosIfNecessary (Map<Integer, List<Integer>> oosdCombos, List<Card> board) {
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

    private Map<Integer, List<Integer>> getMapOfBoardTexturesThatWronglyRecognizeOosdAsGutshot() {
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

    private Map<Integer, List<Integer>> removeSpecificGutshotCombosIfNecessary (Map<Integer, List<Integer>> gutshotCombos, List<Card> board) {
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

    private Map<Integer, List<Integer>> addSpecificGutshotCombosIfBoardIsConnected (Map<Integer, List<Integer>> gutshotCombos, List<Card> board) {
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

    private Map<Integer, List<Integer>> addSpecificGutshotCombosIfNecessary (Map<Integer, List<Integer>> gutshotCombos, List<Card> board) {
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

    private Map<Integer, List<Integer>> addSpecificBackdoorCombosIfNecessary (Map<Integer, List<Integer>> backdoorCombos, List<Card> board) {
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
}
