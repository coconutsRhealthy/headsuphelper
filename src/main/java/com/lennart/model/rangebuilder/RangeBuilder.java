package com.lennart.model.rangebuilder;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.pokergame.Card;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LPO10346 on 9/2/2016.
 */
public class RangeBuilder {
    //Future class which will create estimated opponent ranges based on type of game (single raised, 3bet, 4bet,
    //ch-raised, etc. Then, to estimate the strength of your hand, you will evaluate your hand against this range.
    //For example if your hand beats 60% of the estimated range of your opponent, the hand is 'medium strong'

    //to evaluate your hand against a range, make a map of all possible starthands, sorted from strongest to weakest.
    //see how high your hand ranks in this map. To make this map, first add all combos that getCombosThatMakeRoyalFlush(),
    //then getCombosThatMakeStraightFlush, then getCombosThatMakeQuads(), etc. To correct this map for ranges, remove all
    //combos from this map that do not fall in the range. Of course, the combos that getCombosThatMakeRoyalFlush() and
    //the other methods return, should first be sorted from strongest to weakest, before added to the map.

    BoardEvaluator boardEvaluator = new BoardEvaluator();
    StraightDrawEvaluator straightDrawEvaluator = new StraightDrawEvaluator();
    FlushDrawEvaluator flushDrawEvaluator = new FlushDrawEvaluator();
    HighCardDrawEvaluator highCardDrawEvaluator = new HighCardDrawEvaluator();

    public Map<Integer, Set<Set<Card>>> getRange(String handPath, List<Card> board) {
//        if(handPath.equals("2bet2bet")) {
            //get alle combos, sorted
            Map<Integer, Set<Set<Card>>> allSortedCombos = boardEvaluator.getSortedCombos(board);

            //alle handen boven 50% op flop
            Map<Integer, Set<Set<Card>>> sortedCombosAboveLevel = boardEvaluator.getSortedCombosAboveDesignatedStrengthLevel(0.5, board);

            //alle strong en medium straight draws op flop
            Map<Integer, Set<Card>> strongOosdDraws = straightDrawEvaluator.getStrongOosdCombos(board);
            Map<Integer, Set<Card>> mediumOosdDraws = straightDrawEvaluator.getMediumOosdCombos(board);
            Map<Integer, Set<Card>> strongGutshotDraws = straightDrawEvaluator.getStrongGutshotCombos(board);
            Map<Integer, Set<Card>> mediumGutshotDraws = straightDrawEvaluator.getMediumGutshotCombos(board);

            //nog toevoegen, backdoor combos?

            //alle strong en medium flushdraws op flop
            Map<Integer, List<Card>> strongFlushDraws = flushDrawEvaluator.getStrongFlushDrawCombos(board);
            Map<Integer, List<Card>> mediumFlushDraws = flushDrawEvaluator.getMediumFlushDrawCombos(board);

            //nog toevoegen, strong backdoor combos?

            //alle strong en medium overcarddraws op flop
            Map<Integer, Set<Card>> strongHighCardDraws = highCardDrawEvaluator.getStrongTwoOvercards(board);
            Map<Integer, Set<Card>> mediumHighCardDraws = highCardDrawEvaluator.getMediumTwoOvercards(board);


            //nu gaan we removen:
            for (Map.Entry<Integer, Set<Set<Card>>> entry : allSortedCombos.entrySet()) {
                loop: for(Iterator<Set<Card>> it = entry.getValue().iterator(); it.hasNext(); ) {
                    Set<Card> setFromAllSortedCombos = it.next();
                    //alles wat niet daarin zit, moet uit de hoofdlijst

                    //we beginnen met strongOosdDraws:
                    for (Map.Entry<Integer, Set<Card>> entryStrongOosd : strongOosdDraws.entrySet()) {
                        if(setFromAllSortedCombos.equals(entryStrongOosd.getValue())) {
                            //continue met next 'entrySet'
                            continue loop;
                        }
                    }

                    //de anderen
                    for (Map.Entry<Integer, Set<Card>> entryMediumOosd : mediumOosdDraws.entrySet()) {
                        if(setFromAllSortedCombos.equals(entryMediumOosd.getValue())) {
                            //continue met next 'entrySet'
                            continue loop;
                        }
                    }

                    for (Map.Entry<Integer, Set<Card>> entryStrongGutshot : strongGutshotDraws.entrySet()) {
                        if(setFromAllSortedCombos.equals(entryStrongGutshot.getValue())) {
                            //continue met next 'entrySet'
                            continue loop;
                        }
                    }

                    for (Map.Entry<Integer, Set<Card>> entryMediumGutshot : mediumGutshotDraws.entrySet()) {
                        if(setFromAllSortedCombos.equals(entryMediumGutshot.getValue())) {
                            //continue met next 'entrySet'
                            continue loop;
                        }
                    }

                    for (Map.Entry<Integer, List<Card>> entryStrongFlushDraw : strongFlushDraws.entrySet()) {
                        if(setFromAllSortedCombos.containsAll(entryStrongFlushDraw.getValue())) {
                            //continue met next 'entrySet'
                            continue loop;
                        }
                    }

                    for (Map.Entry<Integer, List<Card>> entryMediumFlushDraw : mediumFlushDraws.entrySet()) {
                        if(setFromAllSortedCombos.containsAll(entryMediumFlushDraw.getValue())) {
                            //continue met next 'entrySet'
                            continue loop;
                        }
                    }

                    for (Map.Entry<Integer, Set<Card>> strongHighCardDraw : strongHighCardDraws.entrySet()) {
                        if(setFromAllSortedCombos.equals(strongHighCardDraw.getValue())) {
                            //continue met next 'entrySet'
                            continue loop;
                        }
                    }

                    for (Map.Entry<Integer, Set<Card>> mediumHighCardDraw : mediumHighCardDraws.entrySet()) {
                        if(setFromAllSortedCombos.equals(mediumHighCardDraw.getValue())) {
                            //continue met next 'entrySet'
                            continue loop;
                        }
                    }
                    it.remove();
                }
            }

//        }
        return allSortedCombos;
//        return null;
    }
}
