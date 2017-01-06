package com.lennart.model.boardevaluation.draws;

import com.lennart.model.boardevaluation.HighCardEvaluator;
import com.lennart.model.boardevaluation.StraightEvaluator;
import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by LPO10346 on 10/11/2016.
 */
public class HighCardDrawEvaluator extends HighCardEvaluator {

    private List<Card> board;

    private Map<Integer, Set<Card>> strongTwoOvercards;
    private Map<Integer, Set<Card>> mediumTwoOvercards;
    private Map<Integer, Set<Card>> weakTwoOvercards;

    private Map<Integer, Set<Card>> allOvercardCombosFlop;
    private Map<Integer, Set<Card>> allOvercardCombosTurn;

    public HighCardDrawEvaluator(List<Card> board, StraightEvaluator straightEvaluator) {
        this.board = board;

        final Map<Integer, Set<Card>> allOvercardCombos = getAllOvercardCombos(board);

        strongTwoOvercards = getStrongTwoOvercards(allOvercardCombos, straightEvaluator);
        mediumTwoOvercards = getMediumTwoOvercards(allOvercardCombos, straightEvaluator);
        weakTwoOvercards = getWeakTwoOvercards(allOvercardCombos, straightEvaluator);

        setOvercardCombosPerStreet(allOvercardCombos);
    }

    public Map<Integer, Set<Card>> getStrongTwoOvercards() {
        return strongTwoOvercards;
    }

    public Map<Integer, Set<Card>> getMediumTwoOvercards() {
        return mediumTwoOvercards;
    }

    public Map<Integer, Set<Card>> getWeakTwoOvercards() {
        return weakTwoOvercards;
    }

    public Map<Integer, Set<Card>> getAllOvercardCombosFlop() {
        return allOvercardCombosFlop;
    }

    public Map<Integer, Set<Card>> getAllOvercardCombosTurn() {
        return allOvercardCombosTurn;
    }

    //helper methods
    private Map<Integer, Set<Card>> getStrongTwoOvercards(Map<Integer, Set<Card>> allOvercardCombos, StraightEvaluator straightEvaluator) {
        if(getNumberOfPairsOnBoard(board) == 0 && straightEvaluator.getMapOfStraightCombos().isEmpty()
                && getNumberOfSuitedCardsOnBoard(board) < 3) {
            return allOvercardCombos;
        }
        return new HashMap<>();
    }

    private Map<Integer, Set<Card>> getMediumTwoOvercards(Map<Integer, Set<Card>> allOvercardCombos, StraightEvaluator straightEvaluator) {
        List<List<Integer>> straightCombos = straightEvaluator.getCombosThatMakeStraight(board);

        if(getNumberOfPairsOnBoard(board) == 1 || getNumberOfSuitedCardsOnBoard(board) == 3 || straightCombos.size() < 10) {
            return allOvercardCombos;
        }
        return new HashMap<>();
    }

    private Map<Integer, Set<Card>> getWeakTwoOvercards(Map<Integer, Set<Card>> allOvercardCombos, StraightEvaluator straightEvaluator) {
        List<List<Integer>> straightCombos = straightEvaluator.getCombosThatMakeStraight(board);

        if(getNumberOfPairsOnBoard(board) > 1 || getNumberOfSuitedCardsOnBoard(board) > 3 || straightCombos.size() >= 10) {
            return allOvercardCombos;
        }
        return new HashMap<>();
    }

    private Map<Integer, Set<Card>> getAllOvercardCombos(List<Card> board) {
        //get de max rank van board
        List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
        int highestRankOnBoard = Collections.max(boardRanks);

        //get alle ranks daarboven en lager dan 15
        List<Integer> allRanksAboveHighestBoardRank = new ArrayList<>();

        for(int i = highestRankOnBoard + 1; i < 15; i++) {
            allRanksAboveHighestBoardRank.add(i);
        }

        //maak van deze ranks alle 2rank combos
        Set<Set<Integer>> rankCombos = new HashSet<>();

        for(Integer i : allRanksAboveHighestBoardRank) {
            for(Integer z : allRanksAboveHighestBoardRank) {
                if(i != z) {
                    Set<Integer> rankCombo = new HashSet<>();
                    rankCombo.add(i);
                    rankCombo.add(z);
                    rankCombos.add(rankCombo);
                }
            }
        }

        //convert deze combos naar 2card combos, gecorrigeerd voor board
        Map<Integer, Set<Card>> overcardCombos = new HashMap<>();
        Set<Set<Card>> cardCombosCorrespondingToRankComboCorrectedForBoard = new HashSet<>();

        List<List<Integer>> rankCombosAsList = new ArrayList<>();

        for(Set<Integer> s : rankCombos) {
            List<Integer> l = new ArrayList<>();
            l.addAll(s);
            rankCombosAsList.add(l);
        }


        for (List<Integer> l : rankCombosAsList) {
            Set<Set<Card>> cardCombosCorrespondingToRankCombo = convertRankComboToSetOfCardCombos(l);

            for(Set<Card> s : cardCombosCorrespondingToRankCombo) {
                cardCombosCorrespondingToRankComboCorrectedForBoard.add(s);
            }
        }

        for(Set<Card> s : cardCombosCorrespondingToRankComboCorrectedForBoard) {
            overcardCombos.put(overcardCombos.size(), s);
        }
        return overcardCombos;
    }

    private void setOvercardCombosPerStreet(Map<Integer, Set<Card>> allOvercardCombos) {
        if(board.size() == 3 && allOvercardCombosFlop == null) {
            allOvercardCombosFlop = allOvercardCombos;
        } else if(board.size() == 4 && allOvercardCombosTurn == null) {
            allOvercardCombosTurn = allOvercardCombos;
        }
    }
}
