package com.lennart.model.boardevaluation.draws;

import com.lennart.model.boardevaluation.HighCardEvaluator;
import com.lennart.model.boardevaluation.StraightEvaluator;
import com.lennart.model.pokergame.Card;

import java.util.*;

/**
 * Created by LPO10346 on 10/11/2016.
 */
public class HighCardDrawEvaluator extends HighCardEvaluator {

    StraightEvaluator straightEvaluator = new StraightEvaluator();

    public Map<Integer, Set<Card>> getStrongTwoOvercards(List<Card> board) {
        //geen pair, max 2toStraight, max2toFlush, geen trips, geen quads
        if(getNumberOfPairsOnBoard(board) == 0 && straightEvaluator.getMapOfStraightCombos().isEmpty()
                && getNumberOfSuitedCardsOnBoard(board) < 3) {

            //get de max rank van board
            List<Integer> boardRanks = getSortedCardRanksFromCardList(board);
            int highestRankOnBoard = Collections.max(boardRanks);

            //get alle ranks daarboven en lager dan 15
            List<Integer> allRanksAboveHighestBoardRank = new ArrayList<>();

            for(int i = highestRankOnBoard; i < 15; i++) {
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

            List<List<Integer>> asList = new ArrayList<>();
            asList.addAll(rankCombos);

            for(Set<Integer> s : rankCombos) {

            }



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

    }

    public Map<Integer, Set<Card>> getMediumTwoOvercards(List<Card> board) {
        //max 1 pair, max 3 to straight, max 3toFlush

    }

    public Map<Integer, Set<Card>> getWeakTwoOvercards(List<Card> board) {
        //max 1 pair, more than 3 to straight, more than 3toFlush

    }

}
