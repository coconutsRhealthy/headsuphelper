package com.lennart.model.rangebuilder.preflop;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.pokergame.ComputerGame;
import com.lennart.model.rangebuilder.RangeBuilder;
import com.lennart.model.rangebuilder.preflop.ip.Call3betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.ip._2betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.ip._4betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.oop.Call2betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.oop.Call4betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.oop._3betRangeBuilder;

import java.util.Map;
import java.util.Set;


/**
 * Created by LPO21630 on 27-10-2016.
 */
public class PreflopRangeBuilder {

    private PreflopRangeBuilderUtil preflopRangeBuilderUtil;
    private _2betRangeBuilder _2betRangeBuilder;
    private Call3betRangeBuilder call3betRangeBuilder;
    private _4betRangeBuilder _4betRangeBuilder;
    private Call2betRangeBuilder call2betRangeBuilder;
    private _3betRangeBuilder _3betRangeBuilder;
    private Call4betRangeBuilder call4betRangeBuilder;

    private double opponentTotalBetSize;
    private double potSize;
    private double bigBlind;
    private boolean computerIsButton;

    public PreflopRangeBuilder(ComputerGame computerGame) {
        opponentTotalBetSize = computerGame.getMyTotalBetSize();
        potSize = computerGame.getPotSize();
        bigBlind = computerGame.getBigBlind();
        computerIsButton = computerGame.isComputerIsButton();

        preflopRangeBuilderUtil = new PreflopRangeBuilderUtil(computerGame.getKnownGameCards());
    }

    public Set<Set<Card>> getOpponentPreflopRange() {
        Set<Set<Card>> range;
        double bbOpponentTotalBetSize = (potSize / 2) / bigBlind;

        if(bbOpponentTotalBetSize == 1) {
            range = RangeBuilder.convertMapToSet(PreflopRangeBuilderUtil.getAllStartHandsAsSet());
        } else if(bbOpponentTotalBetSize > 1 && bbOpponentTotalBetSize <= 4) {
            if(computerIsButton) {
                call2betRangeBuilder = new Call2betRangeBuilder(preflopRangeBuilderUtil);
                range = RangeBuilder.convertMapToSet(call2betRangeBuilder.getOpponentCall2betRange());
            } else {
                _2betRangeBuilder = new _2betRangeBuilder(preflopRangeBuilderUtil);
                range = RangeBuilder.convertMapToSet(_2betRangeBuilder.getOpponent2betRange());
            }
        } else if(bbOpponentTotalBetSize > 4 && bbOpponentTotalBetSize <= 11) {
            if(computerIsButton) {
                _3betRangeBuilder = new _3betRangeBuilder(preflopRangeBuilderUtil);
                range = RangeBuilder.convertMapToSet(_3betRangeBuilder.getOpponent3betRange());
            } else {
                call3betRangeBuilder = new Call3betRangeBuilder(preflopRangeBuilderUtil);
                range = RangeBuilder.convertMapToSet(call3betRangeBuilder.getOpponentCall3betRange());
            }
        } else if(bbOpponentTotalBetSize > 11 && bbOpponentTotalBetSize <= 22) {
            if(computerIsButton) {
                call4betRangeBuilder = new Call4betRangeBuilder(preflopRangeBuilderUtil);
                range = RangeBuilder.convertMapToSet(call4betRangeBuilder.getOpponentCall4betRange());
            } else {
                _4betRangeBuilder = new _4betRangeBuilder(preflopRangeBuilderUtil);
                range = RangeBuilder.convertMapToSet(_4betRangeBuilder.getOpponent4betRange());
            }
        } else {
            //5bet
            range = RangeBuilder.convertMapToSet(PreflopRangeBuilderUtil.getAllStartHandsAsSet());
        }
        return range;
    }

    public PreflopRangeBuilderUtil getPreflopRangeBuilderUtil() {
        return preflopRangeBuilderUtil;
    }

    public Map<Integer, Set<Card>> getOpponent2betRange() {
        if(_2betRangeBuilder == null) {
            _2betRangeBuilder = new _2betRangeBuilder(preflopRangeBuilderUtil);
        }
        return _2betRangeBuilder.getOpponent2betRange();
    }

    public Map<Integer, Set<Card>> getOpponentCall3betRange() {
        if(call3betRangeBuilder == null) {
            call3betRangeBuilder = new Call3betRangeBuilder(preflopRangeBuilderUtil);
        }

        return call3betRangeBuilder.getOpponentCall3betRange();
    }

    public Map<Integer, Set<Card>> getOpponent4betRange() {
        if(_4betRangeBuilder == null) {
            _4betRangeBuilder = new _4betRangeBuilder(preflopRangeBuilderUtil);
        }

        return _4betRangeBuilder.getOpponent4betRange();
    }

    public Map<Integer, Set<Card>> getOpponentCall2betRange() {
        if(call2betRangeBuilder == null) {
            call2betRangeBuilder = new Call2betRangeBuilder(preflopRangeBuilderUtil);
        }
        return call2betRangeBuilder.getOpponentCall2betRange();
    }

    public Map<Integer, Set<Card>> getOpponent3betRange() {
        if(_3betRangeBuilder == null) {
            _3betRangeBuilder = new _3betRangeBuilder(preflopRangeBuilderUtil);
        }

        return _3betRangeBuilder.getOpponent3betRange();
    }

    public Map<Integer, Set<Card>> getOpponentCall4betRange() {
        if(call4betRangeBuilder == null) {
            call4betRangeBuilder = new Call4betRangeBuilder(preflopRangeBuilderUtil);
        }
        return call4betRangeBuilder.getOpponentCall4betRange();
    }
}
