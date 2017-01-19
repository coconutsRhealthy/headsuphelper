package com.lennart.model.rangebuilder.preflop;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.pokergame.ComputerGame;
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
        _2betRangeBuilder = new _2betRangeBuilder(preflopRangeBuilderUtil);
        call3betRangeBuilder = new Call3betRangeBuilder(preflopRangeBuilderUtil);
        _4betRangeBuilder = new _4betRangeBuilder(preflopRangeBuilderUtil);
        call2betRangeBuilder = new Call2betRangeBuilder(preflopRangeBuilderUtil);
        _3betRangeBuilder = new _3betRangeBuilder(preflopRangeBuilderUtil);
        call4betRangeBuilder = new Call4betRangeBuilder(preflopRangeBuilderUtil);
    }

    public Set<Set<Card>> getOpponentPreflopRange() {
        Set<Set<Card>> range;
        double bbOpponentTotalBetSize = opponentTotalBetSize / bigBlind;

        if(bbOpponentTotalBetSize == 1) {

        } else if(bbOpponentTotalBetSize > 1 && bbOpponentTotalBetSize <= 4) {
            if(computerIsButton) {
                //opponent callt mijn 2bet
            } else {
                //opponent heeft ge2bet
            }
        } else if(bbOpponentTotalBetSize > 4 && bbOpponentTotalBetSize <= 11) {
            if(computerIsButton) {
                //opponent heeft ge3bet
            } else {
                //opponent callt mijn 3bet
            }
        } else if(bbOpponentTotalBetSize > 11 && bbOpponentTotalBetSize <= 22) {
            if(computerIsButton) {
                //opponent callt mijn 4bet
            } else {
                //opponent 4bet
            }
        } else {
            //5bet
        }
        return null;
    }

    public Set<Set<Card>> getOpponentPreflopRangeFirstTimePostFlop() {
        if(computerIsButton) {
            computerIsButton = false;
        } else {
            computerIsButton = true;
        }

        opponentTotalBetSize = potSize / 2;

        return getOpponentPreflopRange();
    }



    public PreflopRangeBuilderUtil getPreflopRangeBuilderUtil() {
        return preflopRangeBuilderUtil;
    }

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
