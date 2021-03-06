package com.lennart.model.boardevaluation.draws;

import com.lennart.model.boardevaluation.ComboComparatorRankOnly;
import com.lennart.model.boardevaluation.StraightEvaluator;
import com.lennart.model.card.Card;

import java.util.*;

/**
 * Created by LPO10346 on 10/4/2016.
 */
public class StraightDrawEvaluator extends StraightEvaluator implements ComboComparatorRankOnly {

    private List<Card> board;

    private Map<Integer, Set<Card>> strongOosdCombos;
    private Map<Integer, Set<Card>> mediumOosdCombos;
    private Map<Integer, Set<Card>> weakOosdCombos;
    private Map<Integer, Set<Card>> strongGutshotCombos;
    private Map<Integer, Set<Card>> mediumGutshotCombos;
    private Map<Integer, Set<Card>> weakGutshotCombos;
    private Map<Integer, Set<Card>> strongBackDoorCombos;
    private Map<Integer, Set<Card>> mediumBackDoorCombos;
    private Map<Integer, Set<Card>> weakBackDoorCombos;

    private Map<Integer, Set<Card>> combosThatGiveOosdOrGutshotFlop;
    private Map<Integer, Set<Card>> combosThatGiveOosdOrGutshotTurn;

    public StraightDrawEvaluator(List<Card> board) {
        this.board = board;

        final Map<List<Integer>, List<List<Integer>>> allStraightDrawCombos = getCombosThatGiveAnyStraightDraw(board);
        final Map<Integer, List<Integer>> combosThatGiveOosdOrDoubleGutter = getCombosThatGiveOosdOrDoubleGutter(board, allStraightDrawCombos);
        final Map<Integer, List<Integer>> allGutshotCombos = getCombosThatGiveGutshot(board, allStraightDrawCombos);
        Map<Integer, List<Integer>> allGutshotCombosCopy = getCopyOfMap(allGutshotCombos);
        final Map<Integer, List<Integer>> gutShotCombosLowCard = getWeakGutshotCombosFromAllGutshotCombos(board, allGutshotCombosCopy);
        final Map<Integer, List<Integer>> gutShotCombosCorrectedForLowCard = removeWeakStraightDrawCombos(allGutshotCombosCopy, gutShotCombosLowCard);
        final Map<Integer, List<Integer>> allBackDoorCombos = getCombosThatGiveBackDoorStraightDraw(board, allStraightDrawCombos);
        Map<Integer, List<Integer>> allBackDoorCombosCopy = getCopyOfMap(allBackDoorCombos);
        final Map<Integer, List<Integer>> backDoorCombosLowCard = getWeakBackdoorCombosFromAllBackdoorCombos(board, allBackDoorCombosCopy);
        final Map<Integer, List<Integer>> backDoorCombosCorrectedForLowCard = removeWeakStraightDrawCombos(allBackDoorCombosCopy, backDoorCombosLowCard);

        strongOosdCombos = getStrongOosdCombos(board, combosThatGiveOosdOrDoubleGutter);
        mediumOosdCombos = getMediumOosdCombos(board, combosThatGiveOosdOrDoubleGutter);
        weakOosdCombos = getWeakOosdCombos(board, combosThatGiveOosdOrDoubleGutter);

        strongGutshotCombos = getStrongGutshotCombos(board, gutShotCombosCorrectedForLowCard);
        mediumGutshotCombos = getMediumGutshotCombos(board, gutShotCombosCorrectedForLowCard);
        weakGutshotCombos = getWeakGutshotCombos(board, gutShotCombosCorrectedForLowCard);

        strongBackDoorCombos = getStrongBackDoorCombos(board, backDoorCombosCorrectedForLowCard);
        mediumBackDoorCombos = getMediumBackDoorCombos(board, backDoorCombosCorrectedForLowCard);
        weakBackDoorCombos = getWeakBackDoorCombos(board, backDoorCombosCorrectedForLowCard);

        setCombosThatGiveOosdOrGutshotPerStreet(combosThatGiveOosdOrDoubleGutter, allGutshotCombos);
    }

    public Map<Integer, Set<Card>> getStrongOosdCombos() {
        return strongOosdCombos;
    }

    public Map<Integer, Set<Card>> getMediumOosdCombos() {
        return mediumOosdCombos;
    }

    public Map<Integer, Set<Card>> getWeakOosdCombos() {
        return weakOosdCombos;
    }

    public Map<Integer, Set<Card>> getStrongGutshotCombos() {
        return strongGutshotCombos;
    }

    public Map<Integer, Set<Card>> getMediumGutshotCombos() {
        return mediumGutshotCombos;
    }

    public Map<Integer, Set<Card>> getWeakGutshotCombos() {
        return weakGutshotCombos;
    }

    public Map<Integer, Set<Card>> getStrongBackDoorCombos() {
        return strongBackDoorCombos;
    }

    public Map<Integer, Set<Card>> getMediumBackDoorCombos() {
        return mediumBackDoorCombos;
    }

    public Map<Integer, Set<Card>> getWeakBackDoorCombos() {
        return weakBackDoorCombos;
    }

    public Map<Integer, Set<Card>> getCombosThatGiveOosdOrGutshotFlop() {
        return combosThatGiveOosdOrGutshotFlop;
    }

    public Map<Integer, Set<Card>> getCombosThatGiveOosdOrGutshotTurn() {
        return combosThatGiveOosdOrGutshotTurn;
    }

    //helper methods
    private Map<Integer, List<Integer>> getCombosThatGiveOosdOrDoubleGutter(List<Card> board,
                                                                           Map<List<Integer>, List<List<Integer>>>
                                                                                   allStraightDrawCombos) {
        if(board.size() == 5) {
            return new HashMap<>();
        }

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

    private Map<Integer, List<Integer>> getCombosThatGiveGutshot (List<Card> board,
                                                                 Map<List<Integer>, List<List<Integer>>>
                                                                         allStraightDrawCombos) {
        if(board.size() == 5) {
            return new HashMap<>();
        }

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

    private Map<Integer, List<Integer>> getCombosThatGiveBackDoorStraightDraw(List<Card> board,
                                                                             Map<List<Integer>, List<List<Integer>>>
                                                                                     allStraightDrawCombos) {
        if(board.size() > 3) {
            return new HashMap<>();
        }

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

    private Map<Integer, Set<Card>> getStrongOosdCombos(List<Card> board, Map<Integer, List<Integer>> allCombosThatGiveOosd) {
        //max 1pair on board and max 2 of same suit
        if(getNumberOfPairsOnBoard(board) < 2 && !boardContainsTrips(board) && getNumberOfSuitedCardsOnBoard(board) < 3) {
            return convertRankDrawCombosToCardDrawCombos(allCombosThatGiveOosd, board);
        }
        return new HashMap<>();
    }

    private Map<Integer, Set<Card>> getMediumOosdCombos(List<Card> board, Map<Integer, List<Integer>> allCombosThatGiveOosd) {
        //two pair op het board of 3toFlush
        if((getNumberOfPairsOnBoard(board) == 2 && !boardContainsTrips(board)) || getNumberOfSuitedCardsOnBoard(board) == 3) {
            return convertRankDrawCombosToCardDrawCombos(allCombosThatGiveOosd, board);
        }
        return new HashMap<>();
    }

    private Map<Integer, Set<Card>> getWeakOosdCombos(List<Card> board, Map<Integer, List<Integer>> allCombosThatGiveOosd) {
        if(boardContainsTrips(board) || getNumberOfSuitedCardsOnBoard(board) > 3) {
            return convertRankDrawCombosToCardDrawCombos(allCombosThatGiveOosd, board);
        }
        return new HashMap<>();
    }

    private Map<Integer, Set<Card>> getStrongGutshotCombos(List<Card> board, Map<Integer, List<Integer>> gutShotCombosCorrectedForLowCard) {
        if(getNumberOfPairsOnBoard(board) < 2 && !boardContainsTrips(board) && getNumberOfSuitedCardsOnBoard(board) < 3) {
            return convertRankDrawCombosToCardDrawCombos(gutShotCombosCorrectedForLowCard, board);
        }
        return new HashMap<>();
    }

    private Map<Integer, Set<Card>> getMediumGutshotCombos(List<Card> board, Map<Integer, List<Integer>> gutShotCombosCorrectedForLowCard) {
        if((getNumberOfPairsOnBoard(board) == 2 && !boardContainsTrips(board)) || getNumberOfSuitedCardsOnBoard(board) == 3) {
            return convertRankDrawCombosToCardDrawCombos(gutShotCombosCorrectedForLowCard, board);
        }
        return new HashMap<>();
    }

    private Map<Integer, Set<Card>> getWeakGutshotCombos(List<Card> board, Map<Integer, List<Integer>> gutShotCombosCorrectedForLowCard) {
        if(boardContainsTrips(board) || getNumberOfSuitedCardsOnBoard(board) > 3) {
            return convertRankDrawCombosToCardDrawCombos(gutShotCombosCorrectedForLowCard, board);
        }
        return new HashMap<>();
    }

    private Map<Integer, Set<Card>> getStrongBackDoorCombos(List<Card> board, Map<Integer, List<Integer>> backDoorCombosCorrectedForLowCard) {
        //max 2toFlush, no pair, no trips
        if(getNumberOfPairsOnBoard(board) == 0 && !boardContainsTrips(board) && getNumberOfSuitedCardsOnBoard(board) < 3) {
            return convertRankDrawCombosToCardDrawCombos(backDoorCombosCorrectedForLowCard, board);
        }
        return new HashMap<>();
    }

    private Map<Integer, Set<Card>> getMediumBackDoorCombos(List<Card> board, Map<Integer, List<Integer>> backDoorCombosCorrectedForLowCard) {
        //max 2toFlush, max 1 pair, no trips
        if(getNumberOfPairsOnBoard(board) == 1 && !boardContainsTrips(board) && getNumberOfSuitedCardsOnBoard(board) < 3) {
            return convertRankDrawCombosToCardDrawCombos(backDoorCombosCorrectedForLowCard, board);
        }
        return new HashMap<>();
    }

    private Map<Integer, Set<Card>> getWeakBackDoorCombos(List<Card> board, Map<Integer, List<Integer>> backDoorCombosCorrectedForLowCard) {
        //3toFlush, max 1 pair, trips
        if(getNumberOfSuitedCardsOnBoard(board) == 3 || boardContainsTrips(board)) {
            return convertRankDrawCombosToCardDrawCombos(backDoorCombosCorrectedForLowCard, board);
        }
        return new HashMap<>();
    }

    private void setCombosThatGiveOosdOrGutshotPerStreet(Map<Integer, List<Integer>> allOosdCombos,
                                                         Map<Integer, List<Integer>> allGutshotCombos) {
        Map<Integer, Set<Card>> allStraightDrawCombos =
                convertRankDrawCombosToCardDrawCombos(allOosdCombos, board);
        Map<Integer, Set<Card>> allGutshotCardCombos =
                convertRankDrawCombosToCardDrawCombos(allGutshotCombos, board);

        for (Map.Entry<Integer, Set<Card>> entry : allGutshotCardCombos.entrySet()) {
            allStraightDrawCombos.put(allStraightDrawCombos.size(), entry.getValue());
        }

        if(board.size() == 3 && combosThatGiveOosdOrGutshotFlop == null) {
            combosThatGiveOosdOrGutshotFlop = allStraightDrawCombos;
        } else if(board.size() == 4 && combosThatGiveOosdOrGutshotTurn == null) {
            combosThatGiveOosdOrGutshotTurn = allStraightDrawCombos;
        }
    }

    private Map<Integer, List<Integer>> getWeakGutshotCombosFromAllGutshotCombos(List<Card> board, Map<Integer, List<Integer>> combosThatGiveGutshot) {
        Map<Integer, List<Integer>> allFiveConnectingCards = getAllPossibleFiveConnectingCards();
        Map<List<Integer>, List<List<Integer>>> fiveConnectingCardsPerCombo = new HashMap<>();
        Map<List<Integer>, List<List<Integer>>> fiveConnectingCardsPerComboForCombosThatContributeOnlyOneCard = new HashMap<>();
        Map<Integer, List<Integer>> weakGutshotCombos = new HashMap<>();
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        for (Map.Entry<Integer, List<Integer>> entry : combosThatGiveGutshot.entrySet()) {
            List<Integer> comboPlusBoardRanks = new ArrayList<>();
            comboPlusBoardRanks.addAll(boardRanks);
            comboPlusBoardRanks.addAll(entry.getValue());

            for (Map.Entry<Integer, List<Integer>> entry2 : allFiveConnectingCards.entrySet()) {
                List<Integer> fiveConnectingCardsEntryCopy = new ArrayList<>();
                fiveConnectingCardsEntryCopy.addAll(entry2.getValue());

                fiveConnectingCardsEntryCopy.removeAll(comboPlusBoardRanks);

                if(fiveConnectingCardsEntryCopy.size() == 1) {
                    fiveConnectingCardsPerCombo.put(entry.getValue(), new ArrayList<>());
                    fiveConnectingCardsPerCombo.get(entry.getValue()).add(entry2.getValue());
                }
            }
        }

        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : fiveConnectingCardsPerCombo.entrySet()) {
            for(List<Integer> fiveConnectingCards : entry.getValue()) {
                List<Integer> comboCopy = new ArrayList<>();
                comboCopy.addAll(entry.getKey());
                boolean oneComboCardIsPairedWithBoardAndPresentInStraightAndOtherComboCardIsLowerThanLowestBoardCard = false;

                if(boardRanks.contains(comboCopy.get(0)) && fiveConnectingCards.contains(comboCopy.get(0))) {
                    if(comboCopy.get(1) < Collections.min(boardRanks)) {
                        oneComboCardIsPairedWithBoardAndPresentInStraightAndOtherComboCardIsLowerThanLowestBoardCard = true;
                    }
                }
                if(boardRanks.contains(comboCopy.get(1)) && fiveConnectingCards.contains(comboCopy.get(1))) {
                    if(comboCopy.get(0) < Collections.min(boardRanks)) {
                        oneComboCardIsPairedWithBoardAndPresentInStraightAndOtherComboCardIsLowerThanLowestBoardCard = true;
                    }
                }

                comboCopy.removeAll(fiveConnectingCards);

                if(comboCopy.size() == 1 || (comboCopy.size() == 0 && entry.getKey().get(0) == entry.getKey().get(1)) ||
                        (comboCopy.size() == 0 && oneComboCardIsPairedWithBoardAndPresentInStraightAndOtherComboCardIsLowerThanLowestBoardCard)) {
                    fiveConnectingCardsPerComboForCombosThatContributeOnlyOneCard.put(entry.getKey(), new ArrayList<>());
                    fiveConnectingCardsPerComboForCombosThatContributeOnlyOneCard.get(entry.getKey()).add(fiveConnectingCards);
                }
            }
        }

        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : fiveConnectingCardsPerComboForCombosThatContributeOnlyOneCard.entrySet()) {
            boolean allCombosWeak = true;
            for(List<Integer> fiveConnectingCards : entry.getValue()) {
                List<Integer> comboCopy = new ArrayList<>();
                comboCopy.addAll(entry.getKey());
                comboCopy.retainAll(fiveConnectingCards);
                if(comboCopy.get(0) != Collections.min(fiveConnectingCards)) {
                    allCombosWeak = false;
                }
            }
            if(allCombosWeak) {
                weakGutshotCombos.put(weakGutshotCombos.size(), entry.getKey());
            }
        }

        return weakGutshotCombos;
    }

    private Map<Integer, List<Integer>> getWeakBackdoorCombosFromAllBackdoorCombos(List<Card> board, Map<Integer, List<Integer>> combosThatGiveBackDoor) {
        Map<Integer, List<Integer>> weakBackDoorCombos = new HashMap<>();

        //1 get all backdoor combos
        Map<Integer, List<Integer>> allFiveConnectingCards = getAllPossibleFiveConnectingCards();
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);

        //2 per backdoor combo, get all straights that it could make
        Map<List<Integer>, List<List<Integer>>> backDoorComboWithAllStraightsItCanMake = new HashMap<>();

        for (Map.Entry<Integer, List<Integer>> entry : combosThatGiveBackDoor.entrySet()) {
            backDoorComboWithAllStraightsItCanMake.put(entry.getValue(), new ArrayList<>());
        }

        for (Map.Entry<Integer, List<Integer>> entry : combosThatGiveBackDoor.entrySet()) {
            List<Integer> comboCopyPlusBoardRanks = new ArrayList<>();
            comboCopyPlusBoardRanks.addAll(entry.getValue());
            comboCopyPlusBoardRanks.addAll(boardRanks);
            for (Map.Entry<Integer, List<Integer>> entry2 : allFiveConnectingCards.entrySet()) {
                List<Integer> fiveConnectingCardsCopy = new ArrayList<>();
                fiveConnectingCardsCopy.addAll(entry2.getValue());

                fiveConnectingCardsCopy.removeAll(comboCopyPlusBoardRanks);

                if(fiveConnectingCardsCopy.size() == 2) {
                    if(!Collections.disjoint(entry.getValue(), entry2.getValue())) {
                        backDoorComboWithAllStraightsItCanMake.get(entry.getValue()).add(entry2.getValue());
                    }
                }
            }
        }

        //3 keep only the backdoor comobs where only one card in the combo contributes to the all the straight
        Map<List<Integer>, List<List<Integer>>> backDoorCombosThatOnlyContributeOneCardToStraightWithAllStraightsItCanMake = new HashMap<>();
        for (Map.Entry<List<Integer>, List<List<Integer>>> entry : backDoorComboWithAllStraightsItCanMake.entrySet()) {
            boolean onlyOneCardContributes = true;

            for(List<Integer> l : entry.getValue()) {
                List<Integer> copyCombo = new ArrayList<>();
                List<Integer> copyStraight = new ArrayList<>();
                copyCombo.addAll(entry.getKey());
                copyStraight.addAll(l);

                copyCombo.removeAll(copyStraight);

                if(!(copyCombo.size() == 1 || (copyCombo.size() == 0 && entry.getKey().get(0) == entry.getKey().get(1)))) {
                    if(!boardRanks.contains(Integer.valueOf(Collections.max(copyStraight) + 1))) {
                        onlyOneCardContributes = false;
                    }
                }
            }

            if(onlyOneCardContributes) {
                backDoorCombosThatOnlyContributeOneCardToStraightWithAllStraightsItCanMake.put(entry.getKey(), entry.getValue());
            }
        }

        //4 check if this card is the lowest card in all the staight. If so, then it is a weak combo
        for (Map.Entry<List<Integer>, List<List<Integer>>> entry :
                backDoorCombosThatOnlyContributeOneCardToStraightWithAllStraightsItCanMake.entrySet()) {
            boolean isLowestCardInAllStraights = true;

            for(List<Integer> l : entry.getValue()) {
                if(!entry.getKey().contains(Collections.min(l))) {
                    isLowestCardInAllStraights = false;
                }
            }

            if(isLowestCardInAllStraights) {
                weakBackDoorCombos.put(weakBackDoorCombos.size(), entry.getKey());
            }
        }

        return weakBackDoorCombos;
    }

    private Map<List<Integer>, List<List<Integer>>> getCombosThatGiveAnyStraightDraw(List<Card> board) {
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

    private Map<Integer, List<Integer>> removeWeakStraightDrawCombos (Map<Integer, List<Integer>> allSraightDrawCombosOfType,
                                                                      Map<Integer, List<Integer>> weakStraightDrawCombosOfType) {
        for(Iterator<Map.Entry<Integer, List<Integer>>> it = allSraightDrawCombosOfType.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, List<Integer>> entry = it.next();
            for (Map.Entry<Integer, List<Integer>> entry2 : weakStraightDrawCombosOfType.entrySet()) {
                if(entry.getValue().containsAll(entry2.getValue())) {
                    if(entry.getValue().get(0) != entry.getValue().get(1) &&
                            entry2.getValue().get(0) != entry2.getValue().get(1)) {
                        it.remove();
                    } else if(entry.getValue().get(0) == entry.getValue().get(1) &&
                            entry2.getValue().get(0) == entry2.getValue().get(1)) {
                        it.remove();
                    }
                }
            }
        }
        return allSraightDrawCombosOfType;
    }

    private Map<Integer, Set<Card>> convertRankDrawCombosToCardDrawCombos(Map<Integer, List<Integer>> drawCombosRankOnly,
                                                                          List<Card> board) {
        Map<Integer, Set<Card>> drawCardCombos = new HashMap<>();
        Set<Set<Card>> cardCombosCorrespondingToRankComboCorrectedForBoard = new HashSet<>();

        for (Map.Entry<Integer, List<Integer>> entry : drawCombosRankOnly.entrySet()) {
            Set<Set<Card>> cardCombosCorrespondingToRankCombo = convertRankComboToSetOfCardCombos(entry.getValue());

            for(Set<Card> s : cardCombosCorrespondingToRankCombo) {
                if(Collections.disjoint(s, board)) {
                    cardCombosCorrespondingToRankComboCorrectedForBoard.add(s);
                }
            }
        }

        for(Set<Card> s : cardCombosCorrespondingToRankComboCorrectedForBoard) {
            drawCardCombos.put(drawCardCombos.size(), s);
        }
        return drawCardCombos;
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

                int highestStraightDrawThatIsPresentInCombo1PlusBoardRanks = 0;
                int highestStraightDrawThatIsPresentInCombo2PlusBoardRanks = 0;

                Map<Integer, List<Integer>> allPossibleStraights = getAllPossibleFiveConnectingCards();


                for (Map.Entry<Integer, List<Integer>> entry : allPossibleStraights.entrySet()) {
                    List<Integer> copyCombo1PlusBoardRanks = new ArrayList<>();
                    List<Integer> copyCombo2PlusBoardRanks = new ArrayList<>();
                    List<Integer> copyEntry1 = new ArrayList<>();
                    List<Integer> copyEntry2 = new ArrayList<>();

                    copyCombo1PlusBoardRanks.addAll(combo1PlusBoardRanks);
                    copyCombo2PlusBoardRanks.addAll(combo2PlusBoardRanks);
                    copyEntry1.addAll(entry.getValue());
                    copyEntry2.addAll(entry.getValue());

                    copyEntry1.removeAll(copyCombo1PlusBoardRanks);
                    copyEntry2.removeAll(copyCombo2PlusBoardRanks);
                    if(copyEntry1.size() == 1) {
                        highestStraightDrawThatIsPresentInCombo1PlusBoardRanks = entry.getKey();
                    }

                    if(copyEntry2.size() == 1) {
                        highestStraightDrawThatIsPresentInCombo2PlusBoardRanks = entry.getKey();
                    }
                }

                if(highestStraightDrawThatIsPresentInCombo2PlusBoardRanks > highestStraightDrawThatIsPresentInCombo1PlusBoardRanks) {
                    return 1;
                } else if (highestStraightDrawThatIsPresentInCombo2PlusBoardRanks == highestStraightDrawThatIsPresentInCombo1PlusBoardRanks) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };
    }

    private Map<Integer, List<Integer>> getCopyOfMap(Map<Integer, List<Integer>> map) {
        Map<Integer, List<Integer>> mapCopy = new HashMap<>();

        for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
            Integer i = entry.getKey();
            List<Integer> list = new ArrayList<>();
            list.addAll(entry.getValue());
            mapCopy.put(i, list);
        }
        return mapCopy;
    }
}