package com.lennart.model.rangebuilder;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.rangebuilder.postflop.PostFlopRangeBuilder;
import com.lennart.model.rangebuilder.preflop.PreflopRangeBuilder;

import java.util.*;

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


    private List<Card> holeCards;
    private List<Card> board;
    private Set<Card> knownGameCards;
    private PreflopRangeBuilder preflopRangeBuilder;
    private BoardEvaluator boardEvaluator;
    private Map<Integer, Set<Set<Card>>> sortedCombos;
    private PostFlopRangeBuilder postFlopRangeBuilder;
    private FlushDrawEvaluator flushDrawEvaluator;
    private StraightDrawEvaluator straightDrawEvaluator;
    private HighCardDrawEvaluator highCardDrawEvaluator;
    private HandEvaluator handEvaluator;
    private Set<Set<Card>> previousOpponentRange;

    private double opponentPreCall2betStat;
    private double opponentPre3betStat;

    private Set<Set<Card>> opponentRange;

    public RangeBuilder(RangeBuildable rangeBuildable, boolean initializeRange) {
        holeCards = rangeBuildable.getBotHoleCards();
        board = rangeBuildable.getBoard();
        knownGameCards = rangeBuildable.getKnownGameCards();
        opponentPreCall2betStat = rangeBuildable.getOpponentPreCall2betStat();
        opponentPre3betStat = rangeBuildable.getOpponentPre3betStat();

        preflopRangeBuilder = new PreflopRangeBuilder(rangeBuildable, this);

        if(board != null) {
            previousOpponentRange = rangeBuildable.getOpponentRange();
            boardEvaluator = new BoardEvaluator(board);
            sortedCombos = boardEvaluator.getSortedCombosNew();
            postFlopRangeBuilder = new PostFlopRangeBuilder(rangeBuildable, boardEvaluator, this);
            flushDrawEvaluator = boardEvaluator.getFlushDrawEvaluator();
            straightDrawEvaluator = boardEvaluator.getStraightDrawEvaluator();
            highCardDrawEvaluator = boardEvaluator.getHighCardDrawEvaluator();
            handEvaluator = new HandEvaluator(holeCards, boardEvaluator);
        }

        if(initializeRange) {
            opponentRange = getOpponentRangeInitialize();
        }
    }

    public Set<Set<Card>> getOpponentRange() {
        return opponentRange;
    }

    public Map<Integer, Set<Card>> getCombosOfDesignatedStrength(double lowLimit, double highLimit) {
        Map<Integer, Set<Set<Card>>> sortedCombosOfDesignatedStrengthLevel = getCopyOfSortedCombos();

        double numberUntillWhereYouNeedToRemoveStrongCombos = (1176 - (1176 * highLimit));
        double numberFromWhereYouNeedToStartRemovingAgain = (1176 - (1176 * lowLimit));
        int counter = 0;

        for(Iterator<Map.Entry<Integer, Set<Set<Card>>>> it = sortedCombosOfDesignatedStrengthLevel.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, Set<Set<Card>>> entry = it.next();

            for(Iterator<Set<Card>> it2 = entry.getValue().iterator(); it2.hasNext(); ) {
                it2.next();
                counter++;

                if(counter < numberUntillWhereYouNeedToRemoveStrongCombos || counter > numberFromWhereYouNeedToStartRemovingAgain) {
                    it2.remove();
                }
            }

            if(entry.getValue().isEmpty()) {
                it.remove();
            }
        }

        Map<Integer, Set<Card>> combosOfDesignatedStrengthLevel = new HashMap<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombosOfDesignatedStrengthLevel.entrySet()) {
            for(Set<Card> combo : entry.getValue()) {
                combosOfDesignatedStrengthLevel.put(combosOfDesignatedStrengthLevel.size(), combo);
            }
        }
        return combosOfDesignatedStrengthLevel;
    }

    public Map<Integer, Set<Card>> getAir(Set<Set<Card>> previousRange, double upperLimit, double percentageToInclunde) {
        Map<Integer, Set<Card>> airCombos = new HashMap<>();
        Map<Integer, Set<Card>> airCombosNotCorrectedForDraws = getCombosOfDesignatedStrength(0, upperLimit);
        Set<Set<Card>> allNonBackDoorDraws = getAllNonBackDoorDraws();

        for (Map.Entry<Integer, Set<Card>> entry : airCombosNotCorrectedForDraws.entrySet()) {
            boolean noRegularDraw = false;
            boolean inPreviousRange = false;
            boolean noKnownGameCards = false;

            Set<Set<Card>> allRegularDrawsCopy = new HashSet<>();
            allRegularDrawsCopy.addAll(allNonBackDoorDraws);

            Set<Set<Card>> previousRangeCopy = new HashSet<>();
            previousRangeCopy.addAll(previousRange);

            if(allRegularDrawsCopy.add(entry.getValue())) {
                noRegularDraw = true;
            }
            if(!previousRangeCopy.add(entry.getValue())) {
                inPreviousRange = true;
            }
            if(Collections.disjoint(entry.getValue(), knownGameCards)) {
                noKnownGameCards = true;
            }

            if(noRegularDraw && inPreviousRange && noKnownGameCards) {
                if(Math.random() < percentageToInclunde) {
                    airCombos.put(airCombos.size(), entry.getValue());
                }
            }
        }
        return airCombos;
    }

    public static Set<Set<Card>> convertMapToSet(Map<Integer, Set<Card>> mapToConvertToSet) {
        Set<Set<Card>> set = new HashSet<>();

        for (Map.Entry<Integer, Set<Card>> entry : mapToConvertToSet.entrySet()) {
            set.add(entry.getValue());
        }
        return set;
    }

    public double getOpponentPreflopCall2betFactor(double handsOpponentOopFacingPreflop2bet) {
        double opponentCall2betFactor = 0;
        double standardPreCall2betStat = 0.3;

        if(handsOpponentOopFacingPreflop2bet > 10) {
            opponentCall2betFactor = opponentPreCall2betStat - standardPreCall2betStat;
        }
        return opponentCall2betFactor;
    }

    public double getOpponentPreflop3betFactor(double handsOpponentOopFacingPreflop2bet) {
        double opponentPreflop3betFactor = 0;
        double standardPreflop3betStat = 0.2;

        if(handsOpponentOopFacingPreflop2bet > 10) {
            opponentPreflop3betFactor = opponentPre3betStat - standardPreflop3betStat;
        }
        return opponentPreflop3betFactor;
    }

    public double getOpponetPostflopLoosenessFactor(double handsOpponentOopFacingPreflop2bet) {
        double opponentPostflopLoosenessFactor;
        double opponentPreflopCall2betFactor = getOpponentPreflopCall2betFactor(handsOpponentOopFacingPreflop2bet);
        double opponentPreflop3betFactor = getOpponentPreflop3betFactor(handsOpponentOopFacingPreflop2bet);

        double average = (opponentPreflopCall2betFactor + opponentPreflop3betFactor) / 2;

        opponentPostflopLoosenessFactor = average + 1;

        if(opponentPostflopLoosenessFactor > 1) {
            return opponentPostflopLoosenessFactor;
        }
        return 1;
    }

    public BoardEvaluator getBoardEvaluator() {
        return boardEvaluator;
    }

    public PreflopRangeBuilder getPreflopRangeBuilder() {
        return preflopRangeBuilder;
    }

    public HandEvaluator getHandEvaluator() {
        return handEvaluator;
    }

    public List<Card> getBoard() {
        return board;
    }

    //helper methods
    private Set<Set<Card>> getOpponentRangeInitialize() {
        Set<Set<Card>> opponentRange;

        if(board == null) {
            opponentRange = preflopRangeBuilder.getOpponentPreflopRange();
        } else {
            opponentRange = postFlopRangeBuilder.getOpponentPostFlopRange(previousOpponentRange);
        }

        if(opponentRange != null) {
            System.out.println("Opponentrange size: " + opponentRange.size());
        }
        return opponentRange;
    }

    private Set<Set<Card>> getAllNonBackDoorDraws() {
        Set<Set<Card>> allNonBackDoorDraws = new HashSet<>();

        allNonBackDoorDraws.addAll(convertMapToSet(flushDrawEvaluator.getStrongFlushDrawCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(flushDrawEvaluator.getMediumFlushDrawCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(flushDrawEvaluator.getWeakFlushDrawCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(straightDrawEvaluator.getStrongBackDoorCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(straightDrawEvaluator.getMediumOosdCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(straightDrawEvaluator.getWeakOosdCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(straightDrawEvaluator.getStrongGutshotCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(straightDrawEvaluator.getMediumGutshotCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(straightDrawEvaluator.getWeakGutshotCombos()));
        allNonBackDoorDraws.addAll(convertMapToSet(highCardDrawEvaluator.getStrongTwoOvercards()));
        allNonBackDoorDraws.addAll(convertMapToSet(highCardDrawEvaluator.getMediumTwoOvercards()));
        allNonBackDoorDraws.addAll(convertMapToSet(highCardDrawEvaluator.getWeakTwoOvercards()));

        return allNonBackDoorDraws;
    }

    private Map<Integer, Set<Set<Card>>> getCopyOfSortedCombos() {
        Map<Integer, Set<Set<Card>>> copyOfSortedCombos = new HashMap<>();

        for (Map.Entry<Integer, Set<Set<Card>>> entry : sortedCombos.entrySet()) {
            copyOfSortedCombos.put(copyOfSortedCombos.size(), new HashSet<>());

            for(Set<Card> combo : entry.getValue()) {
                Set<Card> comboCopy = new HashSet<>();
                comboCopy.addAll(combo);

                copyOfSortedCombos.get(copyOfSortedCombos.size() - 1).add(comboCopy);
            }
        }
        return copyOfSortedCombos;
    }
}