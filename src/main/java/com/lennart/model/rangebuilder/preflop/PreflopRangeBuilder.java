package com.lennart.model.rangebuilder.preflop;

import com.lennart.model.card.Card;
import com.lennart.model.rangebuilder.RangeBuildable;
import com.lennart.model.rangebuilder.RangeBuilder;
import com.lennart.model.rangebuilder.preflop.ip.Call3betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.ip._2betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.ip._4betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.oop.Call2betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.oop.Call4betRangeBuilder;
import com.lennart.model.rangebuilder.preflop.oop._3betRangeBuilder;

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

    private double potSize;
    private double bigBlind;
    private boolean botIsButton;
    private double handsHumanOopFacingPreflop2bet;
    private double opponentTotalBetSize;
    private RangeBuilder rangeBuilder;

    public PreflopRangeBuilder(RangeBuildable rangeBuildable, RangeBuilder rangeBuilder) {
        potSize = rangeBuildable.getPotSize();
        bigBlind = rangeBuildable.getBigBlind();
        opponentTotalBetSize = rangeBuildable.getOpponentTotalBetSize();
        botIsButton = rangeBuildable.isBotIsButton();
        handsHumanOopFacingPreflop2bet = rangeBuildable.getHandsOpponentOopFacingPreflop2bet();

        this.rangeBuilder = rangeBuilder;

        preflopRangeBuilderUtil = new PreflopRangeBuilderUtil(rangeBuildable.getKnownGameCards());
    }

    public Set<Set<Card>> getOpponentPreflopRange() {
        Set<Set<Card>> range;
        //double bbOpponentTotalBetSize = (potSize / 2) / bigBlind;

        //bovenstaande werkt niet als de pot nog 0 is...

        double bbOpponentTotalBetSize;
        if(potSize == 0) {
            bbOpponentTotalBetSize = opponentTotalBetSize / bigBlind;
        } else {
            bbOpponentTotalBetSize = (potSize / 2) / bigBlind;
        }

        if(bbOpponentTotalBetSize == 0) {
            range = null;
        } else if(bbOpponentTotalBetSize == 1) {
            if(botIsButton) {
                range = null;
            } else {
                range = RangeBuilder.convertMapToSet(PreflopRangeBuilderUtil.getAllStartHandsAsSet());
            }
        } else if(bbOpponentTotalBetSize > 1 && bbOpponentTotalBetSize <= 4) {
            if(botIsButton) {
                call2betRangeBuilder = new Call2betRangeBuilder(preflopRangeBuilderUtil);
                range = RangeBuilder.convertMapToSet(call2betRangeBuilder.getOpponentCall2betRange(rangeBuilder,
                        handsHumanOopFacingPreflop2bet));
            } else {
                _2betRangeBuilder = new _2betRangeBuilder(preflopRangeBuilderUtil);
                range = RangeBuilder.convertMapToSet(_2betRangeBuilder.getOpponent2betRange());
            }
        } else if(bbOpponentTotalBetSize > 4 && bbOpponentTotalBetSize <= 11) {
            if(botIsButton) {
                _3betRangeBuilder = new _3betRangeBuilder(preflopRangeBuilderUtil);
                range = RangeBuilder.convertMapToSet(_3betRangeBuilder.getOpponent3betRange(rangeBuilder,
                        handsHumanOopFacingPreflop2bet));
            } else {
                call3betRangeBuilder = new Call3betRangeBuilder(preflopRangeBuilderUtil);
                range = RangeBuilder.convertMapToSet(call3betRangeBuilder.getOpponentCall3betRange());
            }
        } else if(bbOpponentTotalBetSize > 11 && bbOpponentTotalBetSize <= 22) {
            if(botIsButton) {
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
}
