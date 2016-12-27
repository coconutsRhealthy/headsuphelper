package com.lennart.model.boardevaluation.draws;

import com.lennart.model.boardevaluation.HighCardEvaluator;
import com.lennart.model.boardevaluation.StraightEvaluator;
import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by LPO10346 on 10/11/2016.
 */
public class HighCardDrawEvaluator extends HighCardEvaluator {

    //Todo: hier moet nog wat mee.. ook constructor
    StraightEvaluator straightEvaluator = new StraightEvaluator();

    public Map<Integer, Set<Card>> getStrongTwoOvercards(List<Card> board) {
        if(getNumberOfPairsOnBoard(board) == 0 && straightEvaluator.getMapOfStraightCombos().isEmpty()
                && getNumberOfSuitedCardsOnBoard(board) < 3) {
            return getAllOvercardCombos(board);
        }
        return new HashMap<>();
    }

    public Map<Integer, Set<Card>> getMediumTwoOvercards(List<Card> board) {
        List<List<Integer>> straightCombos = straightEvaluator.getCombosThatMakeStraight(board);

        if(getNumberOfPairsOnBoard(board) == 1 || getNumberOfSuitedCardsOnBoard(board) == 3 || straightCombos.size() < 10) {
            return getAllOvercardCombos(board);
        }
        return new HashMap<>();
    }

    public Map<Integer, Set<Card>> getWeakTwoOvercards(List<Card> board) {
        List<List<Integer>> straightCombos = straightEvaluator.getCombosThatMakeStraight(board);

        if(getNumberOfPairsOnBoard(board) > 1 || getNumberOfSuitedCardsOnBoard(board) > 3 || straightCombos.size() >= 10) {
            return getAllOvercardCombos(board);
        }
        return new HashMap<>();
    }

    public Map<Integer, Set<Card>> getAllOvercardCombos(List<Card> board) {
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
}
