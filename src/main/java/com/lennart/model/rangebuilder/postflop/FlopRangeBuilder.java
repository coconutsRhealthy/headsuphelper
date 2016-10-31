package com.lennart.model.rangebuilder.postflop;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.rangebuilder.RangeBuilder;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by LPO10346 on 10/17/2016.
 */
public class FlopRangeBuilder {

    PreflopRangeBuilder preflopRangeBuilder = new PreflopRangeBuilder();
    RangeBuilder rangeBuilder = new RangeBuilder();
    BoardEvaluator boardEvaluator = new BoardEvaluator();
    StraightDrawEvaluator straightDrawEvaluator = new StraightDrawEvaluator();
    FlushDrawEvaluator flushDrawEvaluator = new FlushDrawEvaluator();
    HighCardDrawEvaluator highCardDrawEvaluator = new HighCardDrawEvaluator();

    //IP
    public Map<Integer, Set<Set<Card>>> get2betFCheck(List<Card> holeCards) {
        //preflop
        Map<Integer, Set<Card>> preflopRange = preflopRangeBuilder.getOpponentCall2betRange();

        //postflop
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        return rangeBuilder.createRange(preflopRange, flopRange, holeCards);
    }

    public Map<Integer, Set<Set<Card>>> get2betF2bet(List<Card> board, List<Card> holeCards) {
        Map<Integer, Map<Integer, Set<Card>>> flopRange = new HashMap<>();

        //preflop
        Map<Integer, Set<Card>> preflopRange = preflopRangeBuilder.getOpponentCall2betRange();

        //postflop

        //de value range
        flopRange.put(flopRange.size(), rangeBuilder.getCombosOfDesignatedStrength(0.87, 1, board, 0.9));

        //de tricky range
        //iets van getCombosBetweenTwoStrengthLevels, en dan uit die verzameling bijv random 20% van de combos pakken
        Map<Integer, Set<Card>> hmm = rangeBuilder.getCombosOfDesignatedStrength(0.65, 0.87, board, 0.2);

        //de draws
        flopRange.put(flopRange.size(), straightDrawEvaluator.getStrongOosdCombos(board));
        flopRange.put(flopRange.size(), straightDrawEvaluator.getMediumGutshotCombos(board));

        flopRange.put(flopRange.size(), straightDrawEvaluator.getStrongGutshotCombos(board));

        flopRange.put(flopRange.size(), flushDrawEvaluator.getStrongFlushDrawCombos(board));
        flopRange.put(flopRange.size(), flushDrawEvaluator.getMediumFlushDrawCombos(board));
        flopRange.put(flopRange.size(), highCardDrawEvaluator.getStrongTwoOvercards(board));

        //de air combos
        //flopRange.put(flopRange.size(), rangeBuilder.getBackDoorAndAirCombos(flopRange, preflopRange, 0.2, board, holeCards));
        flopRange.put(flopRange.size(), rangeBuilder.getAirRangeNew(flopRange, preflopRange, 0.2, board, holeCards));


//        flopRange.put(flopRange.size(), flushDrawEvaluator.getStrongBackDoorFlushCombos(board)); //max 60 combos
//        flopRange.put(flopRange.size(), straightDrawEvaluator.getStrongBackDoorCombos(board)); //max 40 combos

        Map<Integer, Set<Set<Card>>> eije = rangeBuilder.createRange(preflopRange, flopRange, holeCards);

        //eigenlijk moet je na de value + straight range pas de air gaan toevoegen... Of je geeft als parameter mee
        //aan de nieuwe functie getBackDoorAndAirCombos(rangeOfPreviousStreet)

        return eije;
    }
}
