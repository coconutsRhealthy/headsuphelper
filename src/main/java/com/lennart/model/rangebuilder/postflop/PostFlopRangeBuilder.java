package com.lennart.model.rangebuilder.postflop;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.rangebuilder.OpponentRangeSetter;
import com.lennart.model.rangebuilder.RangeBuildable;
import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.*;

/**
 * Created by LennartMac on 11/01/17.
 */
public class PostFlopRangeBuilder {

    //Class will build postflop range based on preflop range and postflop potsize

    private RangeBuilder rangeBuilder;
    private FlushDrawEvaluator flushDrawEvaluator;
    private StraightDrawEvaluator straightDrawEvaluator;
    private HighCardDrawEvaluator highCardDrawEvaluator;
    private double botTotalBetSize;
    private double opponentTotalBetSize;
    private double potSize;
    private double bigBlind;
    private double handsHumanOopFacingPreflop2bet;
    private Set<Card> knownGameCards;

    private double potSizeAfterLastBotAction;
    private String opponentAction;

    private Map<Integer, Set<Card>> strongFlushDraws;
    private Map<Integer, Set<Card>> mediumFlushDraws;
    private Map<Integer, Set<Card>> weakFlushDraws;
    private Map<Integer, Set<Card>> strongOosdCombos;
    private Map<Integer, Set<Card>> mediumOosdCombos;
    private Map<Integer, Set<Card>> weakOosdCombos;
    private Map<Integer, Set<Card>> strongGutshots;
    private Map<Integer, Set<Card>> mediumGutshots;
    private Map<Integer, Set<Card>> weakGutshots;
    private Map<Integer, Set<Card>> strongOvercards;
    private Map<Integer, Set<Card>> mediumOvercards;
    private Map<Integer, Set<Card>> weakOvercards;
    private Map<Integer, Set<Card>> strongBackDoorFlushCombos;
    private Map<Integer, Set<Card>> mediumBackDoorFlushCombos;
    private Map<Integer, Set<Card>> weakBackDoorFlushCombos;
    private Map<Integer, Set<Card>> strongBackDoorStraightCombos;
    private Map<Integer, Set<Card>> mediumBackDoorStraightCombos;
    private Map<Integer, Set<Card>> weakBackDoorStraightCombos;


    public PostFlopRangeBuilder(RangeBuildable rangeBuildable, BoardEvaluator boardEvaluator, RangeBuilder rangeBuilder) {
        botTotalBetSize = rangeBuildable.getBotTotalBetSize();
        opponentTotalBetSize = rangeBuildable.getOpponentTotalBetSize();
        potSize = rangeBuildable.getPotSize();
        bigBlind = rangeBuildable.getBigBlind();
        knownGameCards = rangeBuildable.getKnownGameCards();
        handsHumanOopFacingPreflop2bet = rangeBuildable.getHandsOpponentOopFacingPreflop2bet();

        potSizeAfterLastBotAction = rangeBuildable.getPotSizeAfterLastBotAction();
        opponentAction = rangeBuildable.getOpponentAction();

        this.rangeBuilder = rangeBuilder;
        flushDrawEvaluator = boardEvaluator.getFlushDrawEvaluator();
        straightDrawEvaluator = boardEvaluator.getStraightDrawEvaluator();
        highCardDrawEvaluator = boardEvaluator.getHighCardDrawEvaluator();

        initializeDrawMap();
    }

    public Set<Set<Card>> getOpponentPostFlopRange(Set<Set<Card>> previousRange) {
        Set<Set<Card>> range;
        double bbPotSizePlusOpponentTotalBetSize = (potSize + opponentTotalBetSize) / bigBlind;

        if(bbPotSizePlusOpponentTotalBetSize <= 7) {
            range = previousRange;
        } else if(bbPotSizePlusOpponentTotalBetSize > 7 && bbPotSizePlusOpponentTotalBetSize <= 20) {
            range = get7to20bbRange(previousRange);
        } else if(bbPotSizePlusOpponentTotalBetSize > 20 && bbPotSizePlusOpponentTotalBetSize <= 40) {
            range = get20to40bbRange(previousRange);
        } else if(bbPotSizePlusOpponentTotalBetSize > 40 && bbPotSizePlusOpponentTotalBetSize < 90) {
            range = get40to90bbRange(previousRange);
        } else {
            range = getAbove90bbRange(previousRange);
        }
        return range;
    }

    //helper methods
    private Set<Set<Card>> get7to20bbRange(Set<Set<Card>> previousRange) {
        Set<Set<Card>> _7to20bbRange;

        double percentagePotIncrease = getPercentagePotIncrease();

        if(percentagePotIncrease <= 0.167) {
            _7to20bbRange = previousRange;
        } else if(percentagePotIncrease > 0.167 && percentagePotIncrease <= 0.33) {
            _7to20bbRange = get7to20bb20to50percent(previousRange);
        } else if(percentagePotIncrease > 0.33 && percentagePotIncrease <= 0.5) {
            _7to20bbRange = get7to20bb50to100percent(previousRange);
        } else {
            _7to20bbRange = get7to20bbAbove100percent(previousRange);
        }
        return _7to20bbRange;
    }

    private Set<Set<Card>> get20to40bbRange(Set<Set<Card>> previousRange) {
        Set<Set<Card>> _20to40bbRange;

        double percentagePotIncrease = getPercentagePotIncrease();

        if(percentagePotIncrease <= 0.167) {
            _20to40bbRange = previousRange;
        } else if(percentagePotIncrease > 0.167 && percentagePotIncrease <= 0.33) {
            _20to40bbRange = get20to40bb20to50percent(previousRange);
        } else if(percentagePotIncrease > 0.33 && percentagePotIncrease <= 0.5) {
            _20to40bbRange = get20to40bb50to100percent(previousRange);
        } else {
            _20to40bbRange = get20to40bbAbove100percent(previousRange);
        }
        return _20to40bbRange;
    }

    private Set<Set<Card>> get40to90bbRange(Set<Set<Card>> previousRange) {
        Set<Set<Card>> _40to90bbRange;

        double percentagePotIncrease = getPercentagePotIncrease();

        if(percentagePotIncrease <= 0.167) {
            _40to90bbRange = previousRange;
        } else if(percentagePotIncrease > 0.167 && percentagePotIncrease <= 0.33) {
            _40to90bbRange = get40to90bb20to50percent(previousRange);
        } else if(percentagePotIncrease > 0.33 && percentagePotIncrease <= 0.5) {
            _40to90bbRange = get40to90bb50to100percent(previousRange);
        } else {
            _40to90bbRange = get40to90bbAbove100percent(previousRange);
        }
        return _40to90bbRange;
    }

    private Set<Set<Card>> getAbove90bbRange(Set<Set<Card>> previousRange) {
        Set<Set<Card>> above90bbRange;

        double percentagePotIncrease = getPercentagePotIncrease();

        if(percentagePotIncrease <= 0.167) {
            above90bbRange = previousRange;
        } else if(percentagePotIncrease > 0.167 && percentagePotIncrease <= 0.33) {
            above90bbRange = getAbove90bb20to50percent(previousRange);
        } else {
            above90bbRange = getAbove90bbAbove50percent(previousRange);
        }
        return above90bbRange;
    }

    private double getPercentagePotIncrease() {
        double percentagePotIncrease;
        double currentPotSize = potSize + botTotalBetSize + opponentTotalBetSize;

        if(opponentAction != null && (opponentAction.equals("bet") || opponentAction.equals("raise"))) {
            double amountToCall = opponentTotalBetSize - botTotalBetSize;
            percentagePotIncrease = amountToCall / currentPotSize;
        } else {
            percentagePotIncrease = (currentPotSize / potSizeAfterLastBotAction) - 1;
        }
        return percentagePotIncrease;
    }

    private Set<Set<Card>> get7to20bb20to50percent(Set<Set<Card>> previousRange) {
        Set<Set<Card>> _7to20bb20to50percent = new HashSet<>();

        //value
        Map<Integer, Set<Card>> valueRangeAsMap = rangeBuilder.getCombosOfDesignatedStrength(0.4, 1);
        valueRangeAsMap = clearForKnownGameCards(valueRangeAsMap);
        Set<Set<Card>> valueRange = convertMapToSet(valueRangeAsMap);

        //regular draws
        Map<Integer, Map<Integer, Set<Card>>> drawMap = new HashMap<>();
        drawMap.put(drawMap.size(), strongFlushDraws);
        drawMap.put(drawMap.size(), mediumFlushDraws);
        drawMap.put(drawMap.size(), weakFlushDraws);
        drawMap.put(drawMap.size(), strongOosdCombos);
        drawMap.put(drawMap.size(), mediumOosdCombos);
        drawMap.put(drawMap.size(), weakOosdCombos);
        drawMap.put(drawMap.size(), strongGutshots);
        drawMap.put(drawMap.size(), mediumGutshots);
        drawMap.put(drawMap.size(), weakGutshots);
        drawMap.put(drawMap.size(), strongOvercards);
        drawMap.put(drawMap.size(), mediumOvercards);
        drawMap.put(drawMap.size(), weakOvercards);
        drawMap.put(drawMap.size(), strongBackDoorFlushCombos);
        drawMap.put(drawMap.size(), mediumBackDoorFlushCombos);
        drawMap.put(drawMap.size(), strongBackDoorStraightCombos);
        drawMap.put(drawMap.size(), mediumBackDoorStraightCombos);

        drawMap = clearForKnownGameCardsMultiple(drawMap);
        Set<Set<Card>> drawRange = convertMultipleMapsToSet(drawMap);

        //air
        Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.4, 0.7);
        Set<Set<Card>> airRange = convertMapToSet(airRangeAsMap);

        _7to20bb20to50percent.addAll(valueRange);
        _7to20bb20to50percent.addAll(drawRange);
        _7to20bb20to50percent.addAll(airRange);

        _7to20bb20to50percent.retainAll(previousRange);

        return _7to20bb20to50percent;
    }

    private Set<Set<Card>> get7to20bb50to100percent(Set<Set<Card>> previousRange) {
        Set<Set<Card>> _7to20bb50to100percent = new HashSet<>();

        //value
        Map<Integer, Set<Card>> valueRangeAsMap = rangeBuilder.getCombosOfDesignatedStrength(0.5, 1);
        valueRangeAsMap = clearForKnownGameCards(valueRangeAsMap);
        Set<Set<Card>> valueRange = convertMapToSet(valueRangeAsMap);

        //regular draws
        Map<Integer, Map<Integer, Set<Card>>> drawMap = new HashMap<>();
        drawMap.put(drawMap.size(), strongFlushDraws);
        drawMap.put(drawMap.size(), mediumFlushDraws);
        drawMap.put(drawMap.size(), weakFlushDraws);
        drawMap.put(drawMap.size(), strongOosdCombos);
        drawMap.put(drawMap.size(), mediumOosdCombos);
        drawMap.put(drawMap.size(), weakOosdCombos);
        drawMap.put(drawMap.size(), strongGutshots);
        drawMap.put(drawMap.size(), mediumGutshots);
        drawMap.put(drawMap.size(), weakGutshots);
        drawMap.put(drawMap.size(), strongOvercards);
        drawMap.put(drawMap.size(), mediumOvercards);
        drawMap.put(drawMap.size(), weakOvercards);
        drawMap.put(drawMap.size(), strongBackDoorFlushCombos);
        drawMap.put(drawMap.size(), strongBackDoorStraightCombos);

        drawMap = clearForKnownGameCardsMultiple(drawMap);
        Set<Set<Card>> drawRange = convertMultipleMapsToSet(drawMap);

        //air
        //Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.5, 0.4);
        Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.5,
                (0.5 * rangeBuilder.getOpponetPostflopLoosenessFactor(handsHumanOopFacingPreflop2bet)));

        Set<Set<Card>> airRange = convertMapToSet(airRangeAsMap);

        _7to20bb50to100percent.addAll(valueRange);
        _7to20bb50to100percent.addAll(drawRange);
        _7to20bb50to100percent.addAll(airRange);

        _7to20bb50to100percent.retainAll(previousRange);

        return _7to20bb50to100percent;
    }

    private Set<Set<Card>> get7to20bbAbove100percent(Set<Set<Card>> previousRange) {
        Set<Set<Card>> _7to20bbAbove100percent = new HashSet<>();

        //value
        Map<Integer, Set<Card>> valueRangeAsMap = rangeBuilder.getCombosOfDesignatedStrength(0.7, 1);
        valueRangeAsMap = clearForKnownGameCards(valueRangeAsMap);
        Set<Set<Card>> valueRange = convertMapToSet(valueRangeAsMap);

        //regular draws
        Map<Integer, Map<Integer, Set<Card>>> drawMap = new HashMap<>();
        drawMap.put(drawMap.size(), strongFlushDraws);
        drawMap.put(drawMap.size(), strongOosdCombos);
        drawMap.put(drawMap.size(), strongGutshots);
        drawMap.put(drawMap.size(), strongOvercards);

        drawMap = clearForKnownGameCardsMultiple(drawMap);
        Set<Set<Card>> drawRange = convertMultipleMapsToSet(drawMap);

        //air
        //Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.7, 0.25);
        Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.7,
                (0.25 * rangeBuilder.getOpponetPostflopLoosenessFactor(handsHumanOopFacingPreflop2bet)));

        Set<Set<Card>> airRange = convertMapToSet(airRangeAsMap);

        _7to20bbAbove100percent.addAll(valueRange);
        _7to20bbAbove100percent.addAll(drawRange);
        _7to20bbAbove100percent.addAll(airRange);

        _7to20bbAbove100percent.retainAll(previousRange);

        return _7to20bbAbove100percent;
    }

    private Set<Set<Card>> get20to40bb20to50percent(Set<Set<Card>> previousRange) {
        Set<Set<Card>> _20to40bb20to50percent = new HashSet<>();

        //value
        Map<Integer, Set<Card>> valueRangeAsMap = rangeBuilder.getCombosOfDesignatedStrength(0.4, 1);
        valueRangeAsMap = clearForKnownGameCards(valueRangeAsMap);
        Set<Set<Card>> valueRange = convertMapToSet(valueRangeAsMap);

        //regular draws
        Map<Integer, Map<Integer, Set<Card>>> drawMap = new HashMap<>();
        drawMap.put(drawMap.size(), strongFlushDraws);
        drawMap.put(drawMap.size(), mediumFlushDraws);
        drawMap.put(drawMap.size(), weakFlushDraws);
        drawMap.put(drawMap.size(), strongOosdCombos);
        drawMap.put(drawMap.size(), mediumOosdCombos);
        drawMap.put(drawMap.size(), weakOosdCombos);
        drawMap.put(drawMap.size(), strongGutshots);
        drawMap.put(drawMap.size(), mediumGutshots);
        drawMap.put(drawMap.size(), weakGutshots);
        drawMap.put(drawMap.size(), strongOvercards);
        drawMap.put(drawMap.size(), mediumOvercards);
        drawMap.put(drawMap.size(), weakOvercards);
        drawMap.put(drawMap.size(), strongBackDoorFlushCombos);
        drawMap.put(drawMap.size(), mediumBackDoorFlushCombos);
        drawMap.put(drawMap.size(), strongBackDoorStraightCombos);
        drawMap.put(drawMap.size(), mediumBackDoorStraightCombos);

        drawMap = clearForKnownGameCardsMultiple(drawMap);
        Set<Set<Card>> drawRange = convertMultipleMapsToSet(drawMap);

        //air
        //Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.4, 0.7);
        Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.4,
                (0.7 * rangeBuilder.getOpponetPostflopLoosenessFactor(handsHumanOopFacingPreflop2bet)));

        Set<Set<Card>> airRange = convertMapToSet(airRangeAsMap);

        _20to40bb20to50percent.addAll(valueRange);
        _20to40bb20to50percent.addAll(drawRange);
        _20to40bb20to50percent.addAll(airRange);

        _20to40bb20to50percent.retainAll(previousRange);

        return _20to40bb20to50percent;
    }

    private Set<Set<Card>> get20to40bb50to100percent(Set<Set<Card>> previousRange) {
        Set<Set<Card>> _20to40bb50to100percent = new HashSet<>();

        //value
        Map<Integer, Set<Card>> valueRangeAsMap = rangeBuilder.getCombosOfDesignatedStrength(0.6, 1);
        valueRangeAsMap = clearForKnownGameCards(valueRangeAsMap);
        Set<Set<Card>> valueRange = convertMapToSet(valueRangeAsMap);

        //regular draws
        Map<Integer, Map<Integer, Set<Card>>> drawMap = new HashMap<>();
        drawMap.put(drawMap.size(), strongFlushDraws);
        drawMap.put(drawMap.size(), mediumFlushDraws);
        drawMap.put(drawMap.size(), strongOosdCombos);
        drawMap.put(drawMap.size(), mediumOosdCombos);
        drawMap.put(drawMap.size(), strongGutshots);
        drawMap.put(drawMap.size(), mediumGutshots);
        drawMap.put(drawMap.size(), strongOvercards);
        drawMap.put(drawMap.size(), mediumOvercards);
        drawMap.put(drawMap.size(), strongBackDoorFlushCombos);
        drawMap.put(drawMap.size(), strongBackDoorStraightCombos);

        drawMap = clearForKnownGameCardsMultiple(drawMap);
        Set<Set<Card>> drawRange = convertMultipleMapsToSet(drawMap);

        //air
        //Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.6, 0.37);
        Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.6,
                (0.45 * rangeBuilder.getOpponetPostflopLoosenessFactor(handsHumanOopFacingPreflop2bet)));

        Set<Set<Card>> airRange = convertMapToSet(airRangeAsMap);

        _20to40bb50to100percent.addAll(valueRange);
        _20to40bb50to100percent.addAll(drawRange);
        _20to40bb50to100percent.addAll(airRange);

        _20to40bb50to100percent.retainAll(previousRange);

        return _20to40bb50to100percent;
    }

    private Set<Set<Card>> get20to40bbAbove100percent(Set<Set<Card>> previousRange) {
        Set<Set<Card>> _20to40bbAbove100percent = new HashSet<>();

        //value
        Map<Integer, Set<Card>> valueRangeAsMap = rangeBuilder.getCombosOfDesignatedStrength(0.84, 1);
        valueRangeAsMap = clearForKnownGameCards(valueRangeAsMap);
        Set<Set<Card>> valueRange = convertMapToSet(valueRangeAsMap);

        //regular draws
        Map<Integer, Map<Integer, Set<Card>>> drawMap = new HashMap<>();
        drawMap.put(drawMap.size(), strongFlushDraws);
        drawMap.put(drawMap.size(), strongOosdCombos);
        drawMap.put(drawMap.size(), strongGutshots);
        drawMap.put(drawMap.size(), strongOvercards);

        drawMap = clearForKnownGameCardsMultiple(drawMap);
        Set<Set<Card>> drawRange = convertMultipleMapsToSet(drawMap);

        //air
        //Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.84, 0.18);
        Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.84,
                (0.24 * rangeBuilder.getOpponetPostflopLoosenessFactor(handsHumanOopFacingPreflop2bet)));

        Set<Set<Card>> airRange = convertMapToSet(airRangeAsMap);

        _20to40bbAbove100percent.addAll(valueRange);
        _20to40bbAbove100percent.addAll(drawRange);
        _20to40bbAbove100percent.addAll(airRange);

        _20to40bbAbove100percent.retainAll(previousRange);

        return _20to40bbAbove100percent;
    }

    private Set<Set<Card>> get40to90bb20to50percent(Set<Set<Card>> previousRange) {
        Set<Set<Card>> _40to90bb20to50percent = new HashSet<>();

        //value
        Map<Integer, Set<Card>> valueRangeAsMap = rangeBuilder.getCombosOfDesignatedStrength(0.7, 1);
        valueRangeAsMap = clearForKnownGameCards(valueRangeAsMap);
        Set<Set<Card>> valueRange = convertMapToSet(valueRangeAsMap);

        //regular draws
        Map<Integer, Map<Integer, Set<Card>>> drawMap = new HashMap<>();
        drawMap.put(drawMap.size(), strongFlushDraws);
        drawMap.put(drawMap.size(), mediumFlushDraws);
        drawMap.put(drawMap.size(), strongOosdCombos);
        drawMap.put(drawMap.size(), mediumOosdCombos);
        drawMap.put(drawMap.size(), strongGutshots);
        drawMap.put(drawMap.size(), mediumGutshots);
        drawMap.put(drawMap.size(), strongOvercards);
        drawMap.put(drawMap.size(), mediumOvercards);

        drawMap = clearForKnownGameCardsMultiple(drawMap);
        Set<Set<Card>> drawRange = convertMultipleMapsToSet(drawMap);

        //air
        //Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.7, 0.5);
        Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.7,
                (0.6 * rangeBuilder.getOpponetPostflopLoosenessFactor(handsHumanOopFacingPreflop2bet)));

        Set<Set<Card>> airRange = convertMapToSet(airRangeAsMap);

        _40to90bb20to50percent.addAll(valueRange);
        _40to90bb20to50percent.addAll(drawRange);
        _40to90bb20to50percent.addAll(airRange);

        _40to90bb20to50percent.retainAll(previousRange);

        return _40to90bb20to50percent;
    }

    private Set<Set<Card>> get40to90bb50to100percent(Set<Set<Card>> previousRange) {
        Set<Set<Card>> _40to90bb50to100percent = new HashSet<>();

        //value
        Map<Integer, Set<Card>> valueRangeAsMap = rangeBuilder.getCombosOfDesignatedStrength(0.8, 1);
        valueRangeAsMap = clearForKnownGameCards(valueRangeAsMap);
        Set<Set<Card>> valueRange = convertMapToSet(valueRangeAsMap);

        //regular draws
        Map<Integer, Map<Integer, Set<Card>>> drawMap = new HashMap<>();
        drawMap.put(drawMap.size(), strongFlushDraws);
        drawMap.put(drawMap.size(), strongOosdCombos);
        drawMap.put(drawMap.size(), strongGutshots);
        drawMap.put(drawMap.size(), strongOvercards);

        drawMap = clearForKnownGameCardsMultiple(drawMap);
        Set<Set<Card>> drawRange = convertMultipleMapsToSet(drawMap);

        //air
        //Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.8, 0.3);
        Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.8,
                (0.5 * rangeBuilder.getOpponetPostflopLoosenessFactor(handsHumanOopFacingPreflop2bet)));

        Set<Set<Card>> airRange = convertMapToSet(airRangeAsMap);

        _40to90bb50to100percent.addAll(valueRange);
        _40to90bb50to100percent.addAll(drawRange);
        _40to90bb50to100percent.addAll(airRange);

        _40to90bb50to100percent.retainAll(previousRange);

        return _40to90bb50to100percent;
    }

    private Set<Set<Card>> get40to90bbAbove100percent(Set<Set<Card>> previousRange) {
        Set<Set<Card>> _40to90bbAbove100percent = new HashSet<>();

        //value
        Map<Integer, Set<Card>> valueRangeAsMap = rangeBuilder.getCombosOfDesignatedStrength(0.89, 1);
        valueRangeAsMap = clearForKnownGameCards(valueRangeAsMap);
        Set<Set<Card>> valueRange = convertMapToSet(valueRangeAsMap);

        //regular draws
        Map<Integer, Map<Integer, Set<Card>>> drawMap = new HashMap<>();
        drawMap.put(drawMap.size(), strongFlushDraws);
        drawMap.put(drawMap.size(), strongOosdCombos);
        drawMap.put(drawMap.size(), strongGutshots);
        drawMap.put(drawMap.size(), strongOvercards);

        drawMap = clearForKnownGameCardsMultiple(drawMap);
        Set<Set<Card>> drawRange = convertMultipleMapsToSet(drawMap);

        //air
        //Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.8, 0.18);
        Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.8,
                (0.24 * rangeBuilder.getOpponetPostflopLoosenessFactor(handsHumanOopFacingPreflop2bet)));

        Set<Set<Card>> airRange = convertMapToSet(airRangeAsMap);

        _40to90bbAbove100percent.addAll(valueRange);
        _40to90bbAbove100percent.addAll(drawRange);
        _40to90bbAbove100percent.addAll(airRange);

        _40to90bbAbove100percent.retainAll(previousRange);

        return _40to90bbAbove100percent;
    }

    private Set<Set<Card>> getAbove90bb20to50percent(Set<Set<Card>> previousRange) {
        Set<Set<Card>> above90bb20to50percent = new HashSet<>();

        //value
        Map<Integer, Set<Card>> valueRangeAsMap = rangeBuilder.getCombosOfDesignatedStrength(0.75, 1);
        valueRangeAsMap = clearForKnownGameCards(valueRangeAsMap);
        Set<Set<Card>> valueRange = convertMapToSet(valueRangeAsMap);

        //regular draws
        Map<Integer, Map<Integer, Set<Card>>> drawMap = new HashMap<>();
        drawMap.put(drawMap.size(), strongFlushDraws);
        drawMap.put(drawMap.size(), mediumFlushDraws);
        drawMap.put(drawMap.size(), strongOosdCombos);
        drawMap.put(drawMap.size(), mediumOosdCombos);
        drawMap.put(drawMap.size(), strongGutshots);
        drawMap.put(drawMap.size(), mediumGutshots);
        drawMap.put(drawMap.size(), strongOvercards);
        drawMap.put(drawMap.size(), mediumOvercards);

        drawMap = clearForKnownGameCardsMultiple(drawMap);
        Set<Set<Card>> drawRange = convertMultipleMapsToSet(drawMap);

        //air
        //Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.75, 0.5);
        Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.75,
                (0.6 * rangeBuilder.getOpponetPostflopLoosenessFactor(handsHumanOopFacingPreflop2bet)));

        Set<Set<Card>> airRange = convertMapToSet(airRangeAsMap);

        above90bb20to50percent.addAll(valueRange);
        above90bb20to50percent.addAll(drawRange);
        above90bb20to50percent.addAll(airRange);

        above90bb20to50percent.retainAll(previousRange);

        return above90bb20to50percent;
    }

    private Set<Set<Card>> getAbove90bbAbove50percent(Set<Set<Card>> previousRange) {
        Set<Set<Card>> above90bbAbove50percent = new HashSet<>();

        //value
        Map<Integer, Set<Card>> valueRangeAsMap = rangeBuilder.getCombosOfDesignatedStrength(0.82, 1);
        valueRangeAsMap = clearForKnownGameCards(valueRangeAsMap);
        Set<Set<Card>> valueRange = convertMapToSet(valueRangeAsMap);

        //regular draws
        Map<Integer, Map<Integer, Set<Card>>> drawMap = new HashMap<>();
        drawMap.put(drawMap.size(), strongFlushDraws);
        drawMap.put(drawMap.size(), strongOosdCombos);
        drawMap.put(drawMap.size(), strongGutshots);
        drawMap.put(drawMap.size(), strongOvercards);

        drawMap = clearForKnownGameCardsMultiple(drawMap);
        Set<Set<Card>> drawRange = convertMultipleMapsToSet(drawMap);

        //air
        Map<Integer, Set<Card>> airRangeAsMap = rangeBuilder.getAir(previousRange, 0.82,
                (0.35 * rangeBuilder.getOpponetPostflopLoosenessFactor(handsHumanOopFacingPreflop2bet)));
        Set<Set<Card>> airRange = convertMapToSet(airRangeAsMap);

        above90bbAbove50percent.addAll(valueRange);
        above90bbAbove50percent.addAll(drawRange);
        above90bbAbove50percent.addAll(airRange);

        above90bbAbove50percent.retainAll(previousRange);

        return above90bbAbove50percent;
    }

    private void initializeDrawMap() {
        strongFlushDraws = flushDrawEvaluator.getStrongFlushDrawCombos();
        mediumFlushDraws = flushDrawEvaluator.getMediumFlushDrawCombos();
        weakFlushDraws = flushDrawEvaluator.getWeakFlushDrawCombos();
        strongOosdCombos = straightDrawEvaluator.getStrongOosdCombos();
        mediumOosdCombos = straightDrawEvaluator.getMediumOosdCombos();
        weakOosdCombos = straightDrawEvaluator.getWeakOosdCombos();
        strongGutshots = straightDrawEvaluator.getStrongGutshotCombos();
        mediumGutshots = straightDrawEvaluator.getMediumGutshotCombos();
        weakGutshots = straightDrawEvaluator.getWeakGutshotCombos();
        strongOvercards = highCardDrawEvaluator.getStrongTwoOvercards();
        mediumOvercards = highCardDrawEvaluator.getMediumTwoOvercards();
        weakOvercards = highCardDrawEvaluator.getWeakTwoOvercards();
        strongBackDoorFlushCombos = flushDrawEvaluator.getStrongBackDoorFlushCombos();
        mediumBackDoorFlushCombos = flushDrawEvaluator.getMediumBackDoorFlushCombos();
        weakBackDoorFlushCombos = flushDrawEvaluator.getWeakBackDoorFlushCombos();
        strongBackDoorStraightCombos = straightDrawEvaluator.getStrongBackDoorCombos();
        mediumBackDoorStraightCombos = straightDrawEvaluator.getMediumBackDoorCombos();
        weakBackDoorStraightCombos = straightDrawEvaluator.getWeakBackDoorCombos();
    }

    private Map<Integer, Set<Card>> clearForKnownGameCards(Map<Integer, Set<Card>> comboMap) {
        Map<Integer, Set<Card>> clearedMap = new HashMap<>();

        for (Map.Entry<Integer, Set<Card>> entry : comboMap.entrySet()) {
            if(Collections.disjoint(entry.getValue(), knownGameCards)) {
                clearedMap.put(clearedMap.size(), entry.getValue());
            }
        }
        return clearedMap;
    }

    private Map<Integer, Map<Integer, Set<Card>>> clearForKnownGameCardsMultiple(Map<Integer, Map<Integer,
            Set<Card>>> multipleComboMap) {
        Map<Integer, Map<Integer, Set<Card>>> clearedMultiMap = new HashMap<>();

        for (Map.Entry<Integer, Map<Integer, Set<Card>>> outerEntry : multipleComboMap.entrySet()) {
            clearedMultiMap.put(clearedMultiMap.size(), new HashMap<>());

            for (Map.Entry<Integer, Set<Card>> innerEntry : outerEntry.getValue().entrySet()) {
                if(Collections.disjoint(innerEntry.getValue(), knownGameCards)) {
                    clearedMultiMap.get(clearedMultiMap.size() - 1).put(clearedMultiMap.get(clearedMultiMap.size() - 1).size(),
                            innerEntry.getValue());
                }
            }
        }
        return clearedMultiMap;
    }

    private Set<Set<Card>> convertMapToSet(Map<Integer, Set<Card>> mapToConvertToSet) {
        Set<Set<Card>> set = new HashSet<>();

        for (Map.Entry<Integer, Set<Card>> entry : mapToConvertToSet.entrySet()) {
            set.add(entry.getValue());
        }
        return set;
    }

    private Set<Set<Card>> convertMultipleMapsToSet(Map<Integer, Map<Integer, Set<Card>>> multipleMapToConvert) {
        Set<Set<Card>> set = new HashSet<>();

        for (Map.Entry<Integer, Map<Integer, Set<Card>>> outerEntry : multipleMapToConvert.entrySet()) {
            for (Map.Entry<Integer, Set<Card>> innerEntry : outerEntry.getValue().entrySet()) {
                set.add(innerEntry.getValue());
            }
        }
        return set;
    }
}
