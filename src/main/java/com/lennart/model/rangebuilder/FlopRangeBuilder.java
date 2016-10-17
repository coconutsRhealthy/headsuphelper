package com.lennart.model.rangebuilder;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LPO10346 on 10/17/2016.
 */
public class FlopRangeBuilder {

    PreflopRangeBuilder preflopRangeBuilder = new PreflopRangeBuilder();
    BoardEvaluator boardEvaluator = new BoardEvaluator();
    StraightDrawEvaluator straightDrawEvaluator = new StraightDrawEvaluator();
    FlushDrawEvaluator flushDrawEvaluator = new FlushDrawEvaluator();
    HighCardDrawEvaluator highCardDrawEvaluator = new HighCardDrawEvaluator();

    //IP
    Map<Integer, Map<Integer, Set<Card>>> get2betFCheck() {
        //preflop
        Map<Integer, Map<Integer, Set<Card>>> preflopRange = preflopRangeBuilder.get2bet();

        //postflop
        //alles van preflop range

        return null;
    }

    Map<Integer, Map<Integer, Set<Card>>> get2betF2bet(List<Card> board) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //preflop
        Map<Integer, Map<Integer, Set<Card>>> preflopRange = preflopRangeBuilder.get2bet();

        //postflop

        //de value range
        flopRange.put(flopRange.size(), boardEvaluator.getCombosAboveDesignatedStrengthLevel(0.87, board));

        //de tricky range
        //iets van getCombosBetweenTwoStrengthLevels, en dan uit die verzameling bijv random 20% van de combos pakken

        //de draws
        flopRange.put(flopRange.size(), straightDrawEvaluator.getStrongOosdCombos(board));
        flopRange.put(flopRange.size(), straightDrawEvaluator.getMediumGutshotCombos(board));

        flopRange.put(flopRange.size(), straightDrawEvaluator.getStrongGutshotCombos(board));

        flopRange.put(flopRange.size(), flushDrawEvaluator.getStrongFlushDrawCombos(board));
        flopRange.put(flopRange.size(), flushDrawEvaluator.getMediumFlushDrawCombos(board));
        flopRange.put(flopRange.size(), highCardDrawEvaluator.getStrongTwoOvercards(board));


        return null;
    }

}
