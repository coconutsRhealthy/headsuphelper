package com.lennart.model.rangebuilder.preflop;

import com.lennart.model.pokergame.Card;
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

    private _2betRangeBuilder _2betRangeBuilder = new _2betRangeBuilder();
    private Call3betRangeBuilder call3betRangeBuilder = new Call3betRangeBuilder();
    private _4betRangeBuilder _4betRangeBuilder = new _4betRangeBuilder();
    private Call2betRangeBuilder call2betRangeBuilder = new Call2betRangeBuilder();
    private _3betRangeBuilder _3betRangeBuilder = new _3betRangeBuilder();
    private Call4betRangeBuilder call4betRangeBuilder = new Call4betRangeBuilder();

    public Map<Integer, Set<Card>> getOpponent2betRange() {
        return _2betRangeBuilder.getOpponent2betRange();
    }

    public Map<Integer, Set<Card>> getOpponentCall3betRange() {
        return call3betRangeBuilder.getOpponentCall3betRange();
    }

    public Map<Integer, Set<Card>> getOpponent4betRange() {
        return _4betRangeBuilder.getOpponent4betRange();
    }

    public Map<Integer, Set<Card>> getOpponentCall2betRange() {
        return call2betRangeBuilder.getOpponentCall2betRange();
    }

    public Map<Integer, Set<Card>> getOpponent3betRange() {
        return _3betRangeBuilder.getOpponent3betRange();
    }

    public Map<Integer, Set<Card>> getOpponentCall4betRange() {
        return call4betRangeBuilder.getOpponentCall4betRange();
    }
}
