package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.card.Card;
import equitycalc.EquityCalculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by LennartMac on 08/05/2020.
 */
public class EquityAction2 {

    private String getPostflopCheckOrBetAction(List<List<Card>> currentOppRange, String oppName, double sizing,
                                               List<Card> board, List<Card> botHoleCards) {
        String actionToReturn;

        String oppLooseness = getOppLooseness(oppName);
        String botSizingGroup = getBotSizingGroup(sizing);

        List<List<Card>> allCombosPostflopEquitySorted = getAllCombosPostflopEquitySorted(board, botHoleCards);

        List<List<Card>> oppCallingRange = new RangeConstructor().getOppPostflopCallRange(currentOppRange,
                allCombosPostflopEquitySorted, oppLooseness, botSizingGroup, board, botHoleCards);

        double botEquity = new EquityCalculator().getComboEquity(botHoleCards, board);
        double oppAverageCallingEquity = new EquityCalculator().getAverageRangeEquity(oppCallingRange, board);

        if(botEquity > oppAverageCallingEquity) {
            actionToReturn = "bet75pct";
        } else {
            actionToReturn = "check";
        }

        return actionToReturn;
    }

    private String getPostflopFoldCallOrRaiseAction(List<List<Card>> currentOppRange, String oppName, double oppTotalBetsize,
                                                    List<Card> board, List<Card> botHoleCards, List<String> eligibleActions,
                                                    double botHypotheticalRaiseSizing) {
        String actionToReturn;

        String oppAggroness = getOppAggroness(oppName);
        String oppSizingGroup = getOppSizingGroup(oppTotalBetsize);

        List<List<Card>> allCombosPostflopEquitySorted = getAllCombosPostflopEquitySorted(board, botHoleCards);

        List<List<Card>> oppBetRange = new RangeConstructor().getOppPostflopBetRange(currentOppRange,
                allCombosPostflopEquitySorted, oppAggroness, oppSizingGroup, board, botHoleCards);

        double botEquity = new EquityCalculator().getComboEquity(botHoleCards, board);
        double oppAverageBettingEquity = new EquityCalculator().getAverageRangeEquity(oppBetRange, board);

        if(botEquity > oppAverageBettingEquity) {
            if(eligibleActions.contains("raise")) {
                String oppLooseness = getOppLooseness(oppName);
                String botSizingGroup = getBotSizingGroup(botHypotheticalRaiseSizing);

                List<List<Card>> oppCallingRaiseRange = new RangeConstructor().getOppPostflopCallRange(oppBetRange,
                        allCombosPostflopEquitySorted, oppLooseness, botSizingGroup, board, botHoleCards);

                double oppAverageRaiseCallingEquity = new EquityCalculator().getAverageRangeEquity(oppCallingRaiseRange, board);

                if(botEquity > oppAverageRaiseCallingEquity) {
                    actionToReturn = "raise";
                } else {
                    actionToReturn = "call";
                }
            } else {
                actionToReturn = "call";
            }
        } else {
            actionToReturn = "fold";
        }

        return actionToReturn;
    }

    private List<List<Card>> getAllCombosPostflopEquitySorted(List<Card> board, List<Card> botHoleCards) {
        List<List<Card>> allCombos = ActionBuilderUtil.getAllPossibleStartHandsAsList().values().stream()
                .collect(Collectors.toList());

        List<List<Card>> allCombosKnownGameCardsRemoved = RangeConstructor.removeCombosWithKnownCards(
                allCombos, Stream.concat(board.stream(), botHoleCards.stream())
                        .collect(Collectors.toList()));

        return new EquityCalculator().getRangeEquities(allCombosKnownGameCardsRemoved, board).keySet().stream()
                .collect(Collectors.toList());
    }

    private String getOppAggroness(String oppName) {
        return null;
    }

    private String getOppLooseness(String oppName) {
        return null;
    }

    private String getBotSizingGroup(double sizing) {
        return null;
    }

    private String getOppSizingGroup(double oppTotalBetsize) {
        return null;
    }
}
