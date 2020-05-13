package com.lennart.model.action.actionbuilders.ai.equityrange;

import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.boardevaluation.draws.FlushDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.card.Card;
import equitycalc.EquityCalculator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by LennartMac on 11/04/2020.
 */
public class RangeConstructor {

    private static final String STRONG_FD = "strongFd";
    private static final String MEDIUM_FD = "mediumFd";
    private static final String WEAK_FD = "weakFd";
    private static final String STRONG_OOSD = "strongOosd";
    private static final String MEDIUM_OOSD = "mediumOosd";
    private static final String WEAK_OOSD = "weakOosd";
    private static final String STRONG_GUTSHOT = "strongGutshot";
    private static final String MEDIUM_GUTSHOT = "mediumGutshot";
    private static final String WEAK_GUTSHOT = "weakGutshot";

    private static final String LOW = "low";
    private static final String MEDIUM = "medium";
    private static final String HIGH = "high";
    private static final String SMALL = "small";
    private static final String LARGE = "large";

    public static void main(String[] args) {
        new RangeConstructor().testMethod();
    }

    private void testMethod() {
        Map<List<Card>, Double> allSortedPfEquityCombos = new PreflopEquityHs().getAllPreflopCombosEquitySortedMap();
        List<List<Card>> startingRange = allSortedPfEquityCombos.keySet().stream().collect(Collectors.toList());

        List<Card> botHoleCards = Arrays.asList(new Card(3, 's'), new Card(3, 'c'));
        List<Card> board = Arrays.asList(new Card(2, 's'), new Card(8, 'd'), new Card(13, 'h'));

        startingRange = removeCombosWithKnownCards(startingRange, botHoleCards);
        startingRange = removeCombosWithKnownCards(startingRange, board);

        Map<List<Card>, Double> postflopEquityMap = new EquityCalculator().getRangeEquities(startingRange, board);
        List<List<Card>> allPostflopCombosEquitySorted = postflopEquityMap.keySet().stream().collect(Collectors.toList());

        List<List<Card>> range = getOppPostflopRaiseRange(startingRange, allPostflopCombosEquitySorted, "high", "medium", board, botHoleCards);
        System.out.println(new EquityAction(new RangeConstructor(), new PreflopEquityHs()).getAverageEquityOfRangeWithEquityMap(postflopEquityMap, range));
    }

    public List<List<Card>> getOppPreLimpRange(List<List<Card>> allSortedPfEquityCombos, String pre2betGroup, List<Card> botHoleCards) {
        List<List<Card>> oppPre2betRange = new ArrayList<>();

        if(pre2betGroup.equals("mediumUnknown") || pre2betGroup.equals("medium")) {
            for(int i = 0; i < allSortedPfEquityCombos.size(); i++) {
                if((i + 0.0) / (allSortedPfEquityCombos.size() + 0.0) <= 0.3) {
                    if(Math.random() < 0.35) {
                        oppPre2betRange.add(allSortedPfEquityCombos.get(i));
                    }
                } else {
                    oppPre2betRange.add(allSortedPfEquityCombos.get(i));
                }
            }
        } else if(pre2betGroup.equals("low")) {
            oppPre2betRange.addAll(allSortedPfEquityCombos);
        } else if(pre2betGroup.equals("high")) {
            for(int i = 0; i < allSortedPfEquityCombos.size(); i++) {
                if((i + 0.0) / (allSortedPfEquityCombos.size() + 0.0) <= 0.5) {
                    if(Math.random() < 0.3) {
                        oppPre2betRange.add(allSortedPfEquityCombos.get(i));
                    }
                } else {
                    oppPre2betRange.add(allSortedPfEquityCombos.get(i));
                }
            }
        }

        oppPre2betRange = removeCombosWithKnownCards(oppPre2betRange, botHoleCards);
        return oppPre2betRange;
    }

    public List<List<Card>> getOppPreCheckAgainstLimpRange(List<List<Card>> allSortedPfEquityCombos, String pre2betGroup, List<Card> botHoleCards) {
        List<List<Card>> oppPreCheckRange = new ArrayList<>();

        if(pre2betGroup.equals("mediumUnknown") || pre2betGroup.equals("medium")) {
            for(int i = 0; i < allSortedPfEquityCombos.size(); i++) {
                if((i + 0.0) / (allSortedPfEquityCombos.size() + 0.0) <= 0.3) {
                    if(Math.random() < 0.35) {
                        oppPreCheckRange.add(allSortedPfEquityCombos.get(i));
                    }
                } else {
                    oppPreCheckRange.add(allSortedPfEquityCombos.get(i));
                }
            }
        } else if(pre2betGroup.equals("low")) {
            for(int i = 0; i < allSortedPfEquityCombos.size(); i++) {
                if((i + 0.0) / (allSortedPfEquityCombos.size() + 0.0) <= 0.1) {
                    if(Math.random() < 0.3) {
                        oppPreCheckRange.add(allSortedPfEquityCombos.get(i));
                    }
                } else {
                    oppPreCheckRange.add(allSortedPfEquityCombos.get(i));
                }
            }
        } else if(pre2betGroup.equals("high")) {
            for(int i = 0; i < allSortedPfEquityCombos.size(); i++) {
                if((i + 0.0) / (allSortedPfEquityCombos.size() + 0.0) <= 0.5) {
                    if(Math.random() < 0.3) {
                        oppPreCheckRange.add(allSortedPfEquityCombos.get(i));
                    }
                } else {
                    oppPreCheckRange.add(allSortedPfEquityCombos.get(i));
                }
            }
        }

        oppPreCheckRange = removeCombosWithKnownCards(oppPreCheckRange, botHoleCards);
        return oppPreCheckRange;
    }

    public List<List<Card>> getOppPreRaiseAgainstLimpRange(List<List<Card>> allSortedPfEquityCombos, String pre2betGroup, List<Card> botHoleCards) {
        List<List<Card>> oppPreRaiseAgainstLimpRange = new ArrayList<>();

        if(pre2betGroup.equals("mediumUnknown") || pre2betGroup.equals("medium")) {
            for(int i = 0; i < allSortedPfEquityCombos.size(); i++) {
                if((i + 0.0) / (allSortedPfEquityCombos.size() + 0.0) <= 0.41) {
                    oppPreRaiseAgainstLimpRange.add(allSortedPfEquityCombos.get(i));
                } else {
                    List<List<Card>> extraCombos = allSortedPfEquityCombos.stream().filter(combo -> {
                        boolean suitedConnector = comboIsSuitedConnector(combo, 1) || comboIsSuitedConnector(combo, 2)
                                || comboIsSuitedConnector(combo, 3);

                        return suitedConnector;
                    }).collect(Collectors.toList());

                    oppPreRaiseAgainstLimpRange.addAll(extraCombos);
                    oppPreRaiseAgainstLimpRange = filterOutDoubleCombos(oppPreRaiseAgainstLimpRange);
                }
            }
        } else if(pre2betGroup.equals("low")) {
            for(int i = 0; i < allSortedPfEquityCombos.size(); i++) {
                if((i + 0.0) / (allSortedPfEquityCombos.size() + 0.0) <= 0.27) {
                    oppPreRaiseAgainstLimpRange.add(allSortedPfEquityCombos.get(i));
                } else {
                    List<List<Card>> extraCombos = allSortedPfEquityCombos.stream().filter(combo -> {
                        boolean suitedConnector = comboIsSuitedConnector(combo, 1);
                        return suitedConnector && combo.get(0).getRank() >= 4 && combo.get(1).getRank() >= 4;
                    }).collect(Collectors.toList());

                    oppPreRaiseAgainstLimpRange.addAll(extraCombos);
                    oppPreRaiseAgainstLimpRange = filterOutDoubleCombos(oppPreRaiseAgainstLimpRange);
                }
            }
        } else if(pre2betGroup.equals("high")) {
            for(int i = 0; i < allSortedPfEquityCombos.size(); i++) {
                if((i + 0.0) / (allSortedPfEquityCombos.size() + 0.0) <= 0.56) {
                    oppPreRaiseAgainstLimpRange.add(allSortedPfEquityCombos.get(i));
                } else {
                    List<List<Card>> suitedCombos = allSortedPfEquityCombos.stream()
                            .filter(combo -> combo.get(0).getSuit() == combo.get(1).getSuit())
                            .collect(Collectors.toList());

                    oppPreRaiseAgainstLimpRange.addAll(suitedCombos);
                    oppPreRaiseAgainstLimpRange = filterOutDoubleCombos(oppPreRaiseAgainstLimpRange);
                }
            }
        }

        oppPreRaiseAgainstLimpRange = removeCombosWithKnownCards(oppPreRaiseAgainstLimpRange, botHoleCards);
        return oppPreRaiseAgainstLimpRange;
    }

    public List<List<Card>> getOppPre2betRange(List<List<Card>> allSortedPfEquityCombos, String pre2betGroup, List<Card> botHoleCards) {
        List<List<Card>> oppPre2betRange = new ArrayList<>();

        double limit;

        if(pre2betGroup.equals("mediumUnknown") || pre2betGroup.equals("medium")) {
            limit = 25;
        } else if(pre2betGroup.equals("low")) {
            limit = 57;
        } else if(pre2betGroup.equals("high")) {
            limit = 0;
        } else {
            limit = 100;
            System.out.println("pre2betGroup is unknown, should not come here");
        }

        for(int i = 0; i < allSortedPfEquityCombos.size() * (1.0 - (limit / 100.0)); i++) {
            oppPre2betRange.add(allSortedPfEquityCombos.get(i));
        }

        if(pre2betGroup.equals("mediumUnknown") || pre2betGroup.equals("medium")) {
            List<List<Card>> suitedCombos = allSortedPfEquityCombos.stream()
                    .filter(combo -> combo.get(0).getSuit() == combo.get(1).getSuit())
                    .collect(Collectors.toList());

            oppPre2betRange.addAll(suitedCombos);
            oppPre2betRange = filterOutDoubleCombos(oppPre2betRange);
        }

        oppPre2betRange = removeCombosWithKnownCards(oppPre2betRange, botHoleCards);
        return oppPre2betRange;
    }

    public List<List<Card>> getOppPreCall2betRange(List<List<Card>> allSortedPfEquityCombos, String preCall2betGroup, List<Card> botHoleCards) {
        List<List<Card>> oppPreCall2betRange = new ArrayList<>();

        double limit;

        if(preCall2betGroup.equals("mediumUnknown") || preCall2betGroup.equals("medium")) {
            limit = 40;
        } else if(preCall2betGroup.equals("low")) {
            limit = 60;
        } else if(preCall2betGroup.equals("high")) {
            limit = 0;
        } else {
            limit = 0;
            System.out.println("preCall2betGroup is unknown, should not come here");
        }

        for(int i = 0; i < allSortedPfEquityCombos.size() * (1.0 - (limit / 100.0)); i++) {
            oppPreCall2betRange.add(allSortedPfEquityCombos.get(i));
        }

        if(preCall2betGroup.equals("low")) {
            List<List<Card>> extraCombos = allSortedPfEquityCombos.stream()
                    .filter(combo -> comboIsSuitedConnector(combo, 1) || comboIsSuitedConnector(combo, 2))
                    .collect(Collectors.toList());
            oppPreCall2betRange.addAll(extraCombos);
            oppPreCall2betRange = filterOutDoubleCombos(oppPreCall2betRange);
        }

        if(preCall2betGroup.equals("mediumUnknown") || preCall2betGroup.equals("medium")) {
            List<List<Card>> suitedCombos = allSortedPfEquityCombos.stream()
                    .filter(combo -> combo.get(0).getSuit() == combo.get(1).getSuit())
                    .collect(Collectors.toList());

            oppPreCall2betRange.addAll(suitedCombos);
            oppPreCall2betRange = filterOutDoubleCombos(oppPreCall2betRange);
        }

        oppPreCall2betRange = removeCombosWithKnownCards(oppPreCall2betRange, botHoleCards);
        return oppPreCall2betRange;
    }

    public List<List<Card>> getOppPre3betRange(List<List<Card>> allSortedPfEquityCombos, String pre3betGroup, List<Card> botHoleCards) {
        List<List<Card>> oppPre3betRange = new ArrayList<>();

        double limit;

        if(pre3betGroup.equals("mediumUnknown") || pre3betGroup.equals("medium")) {
            limit = 70;
        } else if(pre3betGroup.equals("low")) {
            limit = 84;
        } else if(pre3betGroup.equals("high")) {
            limit = 63;
        } else {
            limit = 100;
            System.out.println("pre3betGroup is unknown, should not come here");
        }

        for(int i = 0; i < allSortedPfEquityCombos.size() * (1.0 - (limit / 100.0)); i++) {
            oppPre3betRange.add(allSortedPfEquityCombos.get(i));
        }

        if(pre3betGroup.equals("mediumUnknown") || pre3betGroup.equals("medium")) {
            List<List<Card>> extraCombos = allSortedPfEquityCombos.stream().filter(combo -> {
                boolean suitedConnector = comboIsSuitedConnector(combo, 1) || comboIsSuitedConnector(combo, 2)
                        || comboIsSuitedConnector(combo, 3);

                return suitedConnector && combo.get(0).getRank() >= 4 && combo.get(1).getRank() >= 4;
            }).collect(Collectors.toList());

            oppPre3betRange.addAll(extraCombos);
            oppPre3betRange = filterOutDoubleCombos(oppPre3betRange);
        }

        if(pre3betGroup.equals("high")) {
            List<List<Card>> extraCombos = allSortedPfEquityCombos.stream()
                    .filter(combo -> {
                        boolean suitedConnector = comboIsSuitedConnector(combo, 1) || comboIsSuitedConnector(combo, 2)
                                || comboIsSuitedConnector(combo, 3);

                        boolean suitedHigh = false;

                        if(!suitedConnector) {
                            if(combo.get(0).getSuit() == combo.get(1).getSuit()) {
                                if(combo.get(0).getRank() >= 12 || combo.get(1).getRank() >= 12) {
                                    suitedHigh = true;
                                }
                            }
                        }

                        return suitedConnector || suitedHigh;
                    }).collect(Collectors.toList());

            oppPre3betRange.addAll(extraCombos);
            oppPre3betRange = filterOutDoubleCombos(oppPre3betRange);
        }

        oppPre3betRange = removeCombosWithKnownCards(oppPre3betRange, botHoleCards);
        return oppPre3betRange;
    }

    public List<List<Card>> getOppPreCall3betRange(List<List<Card>> allSortedPfEquityCombos, String preCall3betGroup, List<Card> botHoleCards) {
        List<List<Card>> preCall3betRange = new ArrayList<>();

        double limit;

        if(preCall3betGroup.equals("mediumUnknown") || preCall3betGroup.equals("medium")) {
            limit = 50;
        } else if(preCall3betGroup.equals("low")) {
            limit = 70;
        } else if(preCall3betGroup.equals("high")) {
            limit = 35;
        } else {
            limit = 100;
            System.out.println("preCall3betGroup is unknown, should not come here");
        }

        for(int i = 0; i < allSortedPfEquityCombos.size() * (1.0 - (limit / 100.0)); i++) {
            preCall3betRange.add(allSortedPfEquityCombos.get(i));
        }

        if(preCall3betGroup.equals("mediumUnknown") || preCall3betGroup.equals("medium")) {
            List<List<Card>> extraCombos = allSortedPfEquityCombos.stream()
                    .filter(combo -> comboIsSuitedConnector(combo, 1) || comboIsSuitedConnector(combo, 2) ||
                            comboIsSuitedConnector(combo, 3))
                    .collect(Collectors.toList());

            preCall3betRange.addAll(extraCombos);
        }

        if(preCall3betGroup.equals("low")) {
            List<List<Card>> extraCombos = allSortedPfEquityCombos.stream()
                    .filter(combo -> comboIsSuitedConnector(combo, 1)
                            && combo.get(0).getRank() >= 5 && combo.get(1).getRank() >= 5)
                    .collect(Collectors.toList());

            preCall3betRange.addAll(extraCombos);
        }

        if(preCall3betGroup.equals("high")) {
            List<List<Card>> suitedCombos = allSortedPfEquityCombos.stream()
                    .filter(combo -> combo.get(0).getSuit() == combo.get(1).getSuit())
                    .collect(Collectors.toList());

            preCall3betRange.addAll(suitedCombos);
        }

        preCall3betRange = filterOutDoubleCombos(preCall3betRange);
        preCall3betRange = removeCombosWithKnownCards(preCall3betRange, botHoleCards);
        return preCall3betRange;
    }

    public List<List<Card>> getOppPre4betUpRange(List<List<Card>> allSortedPfEquityCombos, String pre4betUpGroup, List<Card> botHoleCards) {
        List<List<Card>> pre4betUpRange = new ArrayList<>();

        double limit;

        if(pre4betUpGroup.equals("mediumUnknown") || pre4betUpGroup.equals("medium")) {
            limit = 87;
        } else if(pre4betUpGroup.equals("low")) {
            limit = 94;
        } else if(pre4betUpGroup.equals("high")) {
            limit = 70;
        } else {
            limit = 100;
            System.out.println("pre4betUpGroup is unknown, should not come here");
        }

        for(int i = 0; i < allSortedPfEquityCombos.size() * (1.0 - (limit / 100.0)); i++) {
            pre4betUpRange.add(allSortedPfEquityCombos.get(i));
        }

        if(pre4betUpGroup.equals("mediumUnknown") || pre4betUpGroup.equals("medium")) {
            List<List<Card>> suitedConnectors = allSortedPfEquityCombos.stream()
                    .filter(combo -> comboIsSuitedConnector(combo, 1)
                            && combo.get(0).getRank() >= 8 && combo.get(1).getRank() >= 8)
                    .collect(Collectors.toList());

            pre4betUpRange.addAll(suitedConnectors);
            pre4betUpRange = filterOutDoubleCombos(pre4betUpRange);
        }

        if(pre4betUpGroup.equals("high")) {
            List<List<Card>> suitedConnectors = allSortedPfEquityCombos.stream()
                    .filter(combo -> comboIsSuitedConnector(combo, 1)
                            && combo.get(0).getRank() >= 5 && combo.get(1).getRank() >= 5)
                    .collect(Collectors.toList());

            pre4betUpRange.addAll(suitedConnectors);
            pre4betUpRange = filterOutDoubleCombos(pre4betUpRange);
        }

        pre4betUpRange = removeCombosWithKnownCards(pre4betUpRange, botHoleCards);
        return pre4betUpRange;
    }

    public List<List<Card>> getOppPreCall4betUpRange(List<List<Card>> allSortedPfEquityCombos, String preCall4betUpGroup, List<Card> botHoleCards) {
        List<List<Card>> preCall4betUpRange = new ArrayList<>();

        double limit;

        if(preCall4betUpGroup.equals("mediumUnknown") || preCall4betUpGroup.equals("medium")) {
            limit = 85;
        } else if(preCall4betUpGroup.equals("low")) {
            limit = 93;
        } else if(preCall4betUpGroup.equals("high")) {
            limit = 73;
        } else {
            limit = 100;
            System.out.println("preCall4betUpGroup is unknown, should not come here");
        }

        for(int i = 0; i < allSortedPfEquityCombos.size() * (1.0 - (limit / 100.0)); i++) {
            preCall4betUpRange.add(allSortedPfEquityCombos.get(i));
        }

        preCall4betUpRange = removeCombosWithKnownCards(preCall4betUpRange, botHoleCards);
        return preCall4betUpRange;
    }

    public List<List<Card>> getOppPostflopCheckRange(List<List<Card>> oppStartingRange, List<List<Card>> allCombosEquitySorted,
                                                String oppAggroness, String potSize, List<Card> board, List<Card> botHoleCards) {
        List<List<Card>> oppCheckRange;
        List<Card> knownGameCards = Stream.concat(board.stream(), botHoleCards.stream()).collect(Collectors.toList());

        if(oppAggroness.equals(LOW)) {
            if(potSize.equals(SMALL)) {
                double valueVsLowComboBoundry = 60;
                double valueInclusionPercentage = 37;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT,
                        MEDIUM_FD, MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 70;
                oppCheckRange = fillRangeOppActionIsCheck(oppStartingRange, allCombosEquitySorted, board, knownGameCards,
                        valueVsLowComboBoundry, valueInclusionPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(potSize.equals(MEDIUM)) {
                double valueVsLowComboBoundry = 70;
                double valueInclusionPercentage = 50;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT,
                        MEDIUM_FD, MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 85;

                oppCheckRange = fillRangeOppActionIsCheck(oppStartingRange, allCombosEquitySorted, board, knownGameCards,
                        valueVsLowComboBoundry, valueInclusionPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(potSize.equals(LARGE)) {
                double valueVsLowComboBoundry = 80;
                double valueInclusionPercentage = 50;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT,
                        MEDIUM_FD, MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 95;

                oppCheckRange = fillRangeOppActionIsCheck(oppStartingRange, allCombosEquitySorted, board, knownGameCards,
                        valueVsLowComboBoundry, valueInclusionPercentage, drawsToInclude, drawPercentageToInclude);
            } else {
                System.out.println("Should not come here Rangeconstructor - checkRange - A");
                oppCheckRange = new ArrayList<>();
            }
        } else if(oppAggroness.equals(MEDIUM)) {
            if(potSize.equals(SMALL)) {
                double valueVsLowComboBoundry = 60;
                double valueInclusionPercentage = 25;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT,
                        MEDIUM_FD, MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 35;

                oppCheckRange = fillRangeOppActionIsCheck(oppStartingRange, allCombosEquitySorted, board, knownGameCards,
                        valueVsLowComboBoundry, valueInclusionPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(potSize.equals(MEDIUM)) {
                double valueVsLowComboBoundry = 70;
                double valueInclusionPercentage = 25;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT,
                        MEDIUM_FD, MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 50;

                oppCheckRange = fillRangeOppActionIsCheck(oppStartingRange, allCombosEquitySorted, board, knownGameCards,
                        valueVsLowComboBoundry, valueInclusionPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(potSize.equals(LARGE)) {
                double valueVsLowComboBoundry = 80;
                double valueInclusionPercentage = 25;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT,
                        MEDIUM_FD, MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 75;

                oppCheckRange = fillRangeOppActionIsCheck(oppStartingRange, allCombosEquitySorted, board, knownGameCards,
                        valueVsLowComboBoundry, valueInclusionPercentage, drawsToInclude, drawPercentageToInclude);
            } else {
                System.out.println("Should not come here Rangeconstructor - checkRange - B");
                oppCheckRange = new ArrayList<>();
            }
        } else if(oppAggroness.equals(HIGH)) {
            if(potSize.equals(SMALL)) {
                double valueVsLowComboBoundry = 60;
                double valueInclusionPercentage = 15;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT,
                        MEDIUM_FD, MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 15;

                oppCheckRange = fillRangeOppActionIsCheck(oppStartingRange, allCombosEquitySorted, board, knownGameCards,
                        valueVsLowComboBoundry, valueInclusionPercentage, drawsToInclude, drawPercentageToInclude);


            } else if(potSize.equals(MEDIUM)) {
                double valueVsLowComboBoundry = 70;
                double valueInclusionPercentage = 15;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT,
                        MEDIUM_FD, MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 20;

                oppCheckRange = fillRangeOppActionIsCheck(oppStartingRange, allCombosEquitySorted, board, knownGameCards,
                        valueVsLowComboBoundry, valueInclusionPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(potSize.equals(LARGE)) {
                double valueVsLowComboBoundry = 75;
                double valueInclusionPercentage = 15;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT,
                        MEDIUM_FD, MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 35;

                oppCheckRange = fillRangeOppActionIsCheck(oppStartingRange, allCombosEquitySorted, board, knownGameCards,
                        valueVsLowComboBoundry, valueInclusionPercentage, drawsToInclude, drawPercentageToInclude);
            } else {
                System.out.println("Should not come here Rangeconstructor - checkRange - C");
                oppCheckRange = new ArrayList<>();
            }
        } else {
            System.out.println("Should not come here Rangeconstructor - checkRange - D");
            oppCheckRange = new ArrayList<>();
        }

        return oppCheckRange;
    }

    public List<List<Card>> getOppPostflopBetRange(List<List<Card>> oppStartingRange, List<List<Card>> allCombosEquitySorted,
                                                    String oppAggroness, String oppBetsize, List<Card> board, List<Card> botHoleCards) {
        List<List<Card>> oppBetRange;
        List<Card> knownGameCards = Stream.concat(board.stream(), botHoleCards.stream()).collect(Collectors.toList());

        if(oppAggroness.equals(LOW)) {
            if(oppBetsize.equals(SMALL)) {
                double valuePercentage = 60;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage = 5;
                oppBetRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppBetsize.equals(MEDIUM)) {
                double valuePercentage = 70;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD);
                double drawPercentageToInclude = 100;
                double airPercentage = 4;
                oppBetRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppBetsize.equals(LARGE)) {
                double valuePercentage = 83;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD);
                double drawPercentageToInclude = 50;
                double airPercentage = 2;
                oppBetRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else {
                System.out.println("Should not come here Rangeconstructor - betRange - A");
                oppBetRange = new ArrayList<>();
            }
        } else if(oppAggroness.equals(MEDIUM)) {
            if(oppBetsize.equals(SMALL)) {
                double valuePercentage = 53;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD, MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage = 20;
                oppBetRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppBetsize.equals(MEDIUM)) {
                double valuePercentage = 63;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD, MEDIUM_OOSD);
                double drawPercentageToInclude = 100;
                double airPercentage = 17;
                oppBetRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppBetsize.equals(LARGE)) {
                double valuePercentage = 77;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD);
                double drawPercentageToInclude = 100;
                double airPercentage = 15;
                oppBetRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else {
                System.out.println("Should not come here Rangeconstructor - betRange - B");
                oppBetRange = new ArrayList<>();
            }
        } else if(oppAggroness.equals(HIGH)) {
            if(oppBetsize.equals(SMALL)) {
                double valuePercentage = 45;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD,
                        MEDIUM_OOSD, MEDIUM_GUTSHOT, WEAK_FD, WEAK_OOSD, WEAK_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage = 33;
                oppBetRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppBetsize.equals(MEDIUM)) {
                double valuePercentage = 55;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD,
                        MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage = 28;
                oppBetRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppBetsize.equals(LARGE)) {
                double valuePercentage = 68;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD,
                        MEDIUM_OOSD);
                double drawPercentageToInclude = 100;
                double airPercentage = 25;
                oppBetRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else {
                System.out.println("Should not come here Rangeconstructor - betRange - C");
                oppBetRange = new ArrayList<>();
            }
        } else {
            System.out.println("Should not come here Rangeconstructor - betRange - D");
            oppBetRange = new ArrayList<>();
        }

        return oppBetRange;
    }

    public List<List<Card>> getOppPostflopCallRange(List<List<Card>> oppStartingRange, List<List<Card>> allCombosEquitySorted,
                                               String oppLooseness, String botSizing, List<Card> board, List<Card> botHoleCards) {
        List<List<Card>> oppCallRange;
        List<Card> knownGameCards = Stream.concat(board.stream(), botHoleCards.stream()).collect(Collectors.toList());

        if(oppLooseness.equals(LOW)) {
            if(botSizing.equals(SMALL)) {
                double valuePercentage = 67;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage = 0;
                oppCallRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(botSizing.equals(MEDIUM)) {
                double valuePercentage = 77;
                List<String> drawsToInclude;

                if(board.size() == 3) {
                    drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT);
                } else if(board.size() == 4){
                    drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD);
                } else {
                    drawsToInclude = new ArrayList<>();
                }

                double drawPercentageToInclude = 100;

                double airPercentage = 0;
                oppCallRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(botSizing.equals(LARGE)) {
                double valuePercentage = 85;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD);
                double drawPercentageToInclude;

                if(board.size() == 3) {
                    drawPercentageToInclude = 60;
                } else if(board.size() == 4) {
                    drawPercentageToInclude = 30;
                } else {
                    drawPercentageToInclude = 0;
                }

                double airPercentage = 0;
                oppCallRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else {
                System.out.println("Should not come here Rangeconstructor - callRange - A");
                oppCallRange = new ArrayList<>();
            }
        } else if(oppLooseness.equals(MEDIUM)) {
            if(botSizing.equals(SMALL)) {
                double valuePercentage = 55;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD,
                        MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage;

                if(board.size() == 3 || board.size() == 4) {
                    airPercentage = 15;
                } else {
                    airPercentage = 0;
                }

                oppCallRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(botSizing.equals(MEDIUM)) {
                double valuePercentage = 67;
                List<String> drawsToInclude;

                if(board.size() == 3) {
                    drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD, MEDIUM_OOSD);
                } else if(board.size() == 4) {
                    drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT);
                } else {
                    drawsToInclude = new ArrayList<>();
                }

                double drawPercentageToInclude = 100;
                double airPercentage;

                if(board.size() == 3 || board.size() == 4) {
                    airPercentage = 10;
                } else {
                    airPercentage = 0;
                }

                oppCallRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(botSizing.equals(LARGE)) {
                double valuePercentage = 77;
                List<String> drawsToInclude;

                if(board.size() == 3) {
                    drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD);
                } else if(board.size() == 4) {
                    drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD);
                } else {
                    drawsToInclude = new ArrayList<>();
                }

                double drawPercentageToInclude = 100;
                double airPercentage;

                if(board.size() == 3 || board.size() == 4) {
                    airPercentage = 5;
                } else {
                    airPercentage = 0;
                }

                oppCallRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else {
                System.out.println("Should not come here Rangeconstructor - callRange - B");
                oppCallRange = new ArrayList<>();
            }
        } else if(oppLooseness.equals(HIGH)) {
            if(botSizing.equals(SMALL)) {
                double valuePercentage = 48;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD,
                        MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage;

                if(board.size() == 3 || board.size() == 4) {
                    airPercentage = 45;
                } else {
                    airPercentage = 30;
                }

                oppCallRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(botSizing.equals(MEDIUM)) {
                double valuePercentage = 55;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD,
                        MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage;

                if(board.size() == 3 || board.size() == 4) {
                    airPercentage = 35;
                } else {
                    airPercentage = 20;
                }

                oppCallRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(botSizing.equals(LARGE)) {
                double valuePercentage = 65;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD,
                        MEDIUM_OOSD);
                double drawPercentageToInclude = 100;
                double airPercentage;

                if(board.size() == 3 || board.size() == 4) {
                    airPercentage = 25;
                } else {
                    airPercentage = 15;
                }

                oppCallRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else {
                System.out.println("Should not come here Rangeconstructor - callRange - C");
                oppCallRange = new ArrayList<>();
            }
        } else {
            System.out.println("Should not come here Rangeconstructor - callRange - D");
            oppCallRange = new ArrayList<>();
        }

        return oppCallRange;
    }

    public List<List<Card>> getOppPostflopRaiseRange(List<List<Card>> oppStartingRange, List<List<Card>> allCombosEquitySorted,
                                                      String oppAggroness, String oppRaiseSize, List<Card> board, List<Card> botHoleCards) {
        List<List<Card>> oppRaiseRange;
        List<Card> knownGameCards = Stream.concat(board.stream(), botHoleCards.stream()).collect(Collectors.toList());

        if(oppAggroness.equals(LOW)) {
            if(oppRaiseSize.equals(SMALL)) {
                double valuePercentage = 80;
                List<String> drawsToInclude = new ArrayList<>();
                double drawPercentageToInclude = 100;
                double airPercentage = 0;
                oppRaiseRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppRaiseSize.equals(MEDIUM)) {
                double valuePercentage = 87;
                List<String> drawsToInclude = new ArrayList<>();
                double drawPercentageToInclude = 100;
                double airPercentage = 0;
                oppRaiseRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppRaiseSize.equals(LARGE)) {
                double valuePercentage = 92;
                List<String> drawsToInclude = new ArrayList<>();
                double drawPercentageToInclude = 100;
                double airPercentage = 0;
                oppRaiseRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else {
                System.out.println("Should not come here Rangeconstructor - raiseRange - A");
                oppRaiseRange = new ArrayList<>();
            }
        } else if(oppAggroness.equals(MEDIUM)) {
            if(oppRaiseSize.equals(SMALL)) {
                double valuePercentage = 75;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage = 12;
                oppRaiseRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppRaiseSize.equals(MEDIUM)) {
                double valuePercentage = 79;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage = 10;
                oppRaiseRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppRaiseSize.equals(LARGE)) {
                double valuePercentage = 83;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage = 8;
                oppRaiseRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else {
                System.out.println("Should not come here Rangeconstructor - raiseRange - B");
                oppRaiseRange = new ArrayList<>();
            }
        } else if(oppAggroness.equals(HIGH)) {
            if(oppRaiseSize.equals(SMALL)) {
                double valuePercentage = 60;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD,
                        MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage = 24;
                oppRaiseRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppRaiseSize.equals(MEDIUM)) {
                double valuePercentage = 70;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT, MEDIUM_FD,
                        MEDIUM_OOSD, MEDIUM_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage = 21;
                oppRaiseRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else if(oppRaiseSize.equals(LARGE)) {
                double valuePercentage = 78;
                List<String> drawsToInclude = Arrays.asList(STRONG_FD, STRONG_OOSD, STRONG_GUTSHOT);
                double drawPercentageToInclude = 100;
                double airPercentage = 18;
                oppRaiseRange = fillRange(oppStartingRange, allCombosEquitySorted, board, knownGameCards, valuePercentage,
                        airPercentage, drawsToInclude, drawPercentageToInclude);
            } else {
                System.out.println("Should not come here Rangeconstructor - raiseRange - C");
                oppRaiseRange = new ArrayList<>();
            }
        } else {
            System.out.println("Should not come here Rangeconstructor - raiseRange - D");
            oppRaiseRange = new ArrayList<>();
        }

        return oppRaiseRange;
    }

    public static List<List<Card>> removeCombosWithKnownCards(List<List<Card>> listToRemoveCombosFrom, List<Card> knownCards) {
        return listToRemoveCombosFrom.stream().filter(combo -> Collections.disjoint(combo, knownCards)).collect(Collectors.toList());
    }

    private List<List<Card>> fillRange(List<List<Card>> oppStartingRange, List<List<Card>> allCombosEquitySorted,
                                       List<Card> board, List<Card> knownGameCards, double valuePercentage,
                                       double airPercentage, List<String> drawsToInclude, double drawPercentageToInclude) {
        List<List<Card>> range = new ArrayList<>();

        //value
        List<List<Card>> valueCombos = allCombosEquitySorted.subList(0, (int) (allCombosEquitySorted.size() * (1 - (valuePercentage / 100))));
        List<List<Card>> eligibleValueCombos = retainCombosThatAreInRange(oppStartingRange, valueCombos);
        eligibleValueCombos = removeCombosWithKnownCards(eligibleValueCombos, knownGameCards);
        range.addAll(eligibleValueCombos);
        range = filterOutDoubleCombos(range);

        //draw
        List<List<Card>> draws = getDraws(drawsToInclude, board);
        List<List<Card>> eligibleDraws = retainCombosThatAreInRange(oppStartingRange, draws);
        eligibleDraws = removeCombosWithKnownCards(eligibleDraws, knownGameCards);

        if((drawPercentageToInclude / 100) < 1) {
            for(List<Card> draw : eligibleDraws) {
                if(Math.random() < (drawPercentageToInclude / 100)) {
                    range.add(draw);
                }
            }
        } else {
            range.addAll(eligibleDraws);
        }

        range = filterOutDoubleCombos(range);

        //air
        List<List<Card>> airCombos = getAirCombos(allCombosEquitySorted, range, airPercentage, knownGameCards);
        range.addAll(airCombos);
        range = filterOutDoubleCombos(range);

        return range;
    }

    private List<List<Card>> fillRangeOppActionIsCheck(List<List<Card>> oppStartingRange, List<List<Card>> allCombosEquitySorted,
                                                       List<Card> board, List<Card> knownGameCards, double valueVsLowComboBoundry,
                                                       double valueInclusionPercentage, List<String> drawsToInclude, double drawPercentageToInclude) {
        List<List<Card>> range = new ArrayList<>();

        //value
        List<List<Card>> valueCombos = allCombosEquitySorted.subList(0,
                (int) (allCombosEquitySorted.size() * (1 - (valueVsLowComboBoundry / 100))));
        List<List<Card>> eligibleValueCombos = retainCombosThatAreInRange(oppStartingRange, valueCombos);
        eligibleValueCombos = removeCombosWithKnownCards(eligibleValueCombos, knownGameCards);
        List<List<Card>> eligibleValueCombosFilteredForCheck = new ArrayList<>();

        for(List<Card> combo : eligibleValueCombos) {
            if(Math.random() < (valueInclusionPercentage / 100)) {
                eligibleValueCombosFilteredForCheck.add(combo);
            }
        }

        range.addAll(eligibleValueCombosFilteredForCheck);
        range = filterOutDoubleCombos(range);

        //draw
        List<List<Card>> draws = getDraws(drawsToInclude, board);
        List<List<Card>> eligibleDraws = retainCombosThatAreInRange(oppStartingRange, draws);
        eligibleDraws = removeCombosWithKnownCards(eligibleDraws, knownGameCards);

        if((drawPercentageToInclude / 100) < 1) {
            for(List<Card> draw : eligibleDraws) {
                if(Math.random() < drawPercentageToInclude) {
                    range.add(draw);
                }
            }
        } else {
            range.addAll(eligibleDraws);
        }

        range = filterOutDoubleCombos(range);

        //low combos
        List<List<Card>> lowCombos = allCombosEquitySorted.subList(
                (int) (allCombosEquitySorted.size() * (1 - (valueVsLowComboBoundry / 100))), allCombosEquitySorted.size());
        range.addAll(lowCombos);
        range = filterOutDoubleCombos(range);

        return range;
    }

    private List<List<Card>> getDraws(List<String> drawsToInclude, List<Card> board) {
        StraightDrawEvaluator straightDrawEvaluator = new StraightDrawEvaluator(board);
        FlushDrawEvaluator flushDrawEvaluator = new FlushDrawEvaluator(board);

        List<List<Card>> draws = new ArrayList<>();

        if(drawsToInclude.contains(STRONG_OOSD)) {
            draws.addAll(convertDrawMapToList(straightDrawEvaluator.getStrongOosdCombos()));
        }

        if(drawsToInclude.contains(MEDIUM_OOSD)) {
            draws.addAll(convertDrawMapToList(straightDrawEvaluator.getMediumOosdCombos()));
        }

        if(drawsToInclude.contains(WEAK_OOSD)) {
            draws.addAll(convertDrawMapToList(straightDrawEvaluator.getWeakOosdCombos()));
        }

        if(drawsToInclude.contains(STRONG_GUTSHOT)) {
            draws.addAll(convertDrawMapToList(straightDrawEvaluator.getStrongGutshotCombos()));
        }

        if(drawsToInclude.contains(MEDIUM_GUTSHOT)) {
            draws.addAll(convertDrawMapToList(straightDrawEvaluator.getMediumGutshotCombos()));
        }

        if(drawsToInclude.contains(WEAK_GUTSHOT)) {
            draws.addAll(convertDrawMapToList(straightDrawEvaluator.getWeakGutshotCombos()));
        }

        if(drawsToInclude.contains(STRONG_FD)) {
            draws.addAll(convertDrawMapToList(flushDrawEvaluator.getStrongFlushDrawCombos()));
        }

        if(drawsToInclude.contains(MEDIUM_FD)) {
            draws.addAll(convertDrawMapToList(flushDrawEvaluator.getMediumFlushDrawCombos()));
        }

        if(drawsToInclude.contains(WEAK_FD)) {
            draws.addAll(convertDrawMapToList(flushDrawEvaluator.getWeakFlushDrawCombos()));
        }

        return draws;
    }

    private List<List<Card>> getAirCombos(List<List<Card>> allCombosEquitySorted, List<List<Card>> valueAndDrawRange,
                                          double airPercentageToAdd, List<Card> knownGameCards) {
        List<List<Card>> airCombosToAddToRange = new ArrayList<>();

        List<List<Card>> airCombosTotal = allCombosEquitySorted.subList((int) (allCombosEquitySorted.size() * 0.55), allCombosEquitySorted.size());
        List<List<Card>> eligibleAirCombos = removeCombosThatAreInRange(valueAndDrawRange, airCombosTotal);
        eligibleAirCombos = removeCombosWithKnownCards(eligibleAirCombos, knownGameCards);

        int numberOfCombosToAdd = (int) (valueAndDrawRange.size() * ((airPercentageToAdd / 100) + 1)) - valueAndDrawRange.size();
        int numberOfAirCombosAdded = 0;

        while(numberOfAirCombosAdded < numberOfCombosToAdd && !eligibleAirCombos.isEmpty()) {
            List<Card> randomAirCombo = getRandomComboFromList(eligibleAirCombos);
            airCombosToAddToRange.add(randomAirCombo);
            eligibleAirCombos.remove(randomAirCombo);
            numberOfAirCombosAdded++;
        }

        return airCombosToAddToRange;
    }

    private List<List<Card>> convertDrawMapToList(Map<Integer, Set<Card>> drawMap) {
        return drawMap.values().stream()
                .map(combo -> combo
                        .stream()
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private List<Card> getRandomComboFromList(List<List<Card>> input) {
        int min = 0;
        int max = input.size() - 1;
        int random = (int)(Math.random() * ((max - min) + 1)) + min;
        return input.get(random);
    }

    private List<List<Card>> retainCombosThatAreInRange(List<List<Card>> range, List<List<Card>> widerList) {
        List<Set<Card>> widerListWithSets = widerList.stream().map(combo -> combo.stream().collect(Collectors.toSet())).collect(Collectors.toList());
        List<Set<Card>> rangeWithSets = range.stream().map(combo -> combo.stream().collect(Collectors.toSet())).collect(Collectors.toList());
        widerListWithSets.retainAll(rangeWithSets);
        return widerListWithSets.stream().map(comboAsSet -> comboAsSet.stream().collect(Collectors.toList())).collect(Collectors.toList());
    }

    private List<List<Card>> removeCombosThatAreInRange(List<List<Card>> range, List<List<Card>> widerList) {
        List<Set<Card>> widerListWithSets = widerList.stream().map(combo -> combo.stream().collect(Collectors.toSet())).collect(Collectors.toList());
        List<Set<Card>> rangeWithSets = range.stream().map(combo -> combo.stream().collect(Collectors.toSet())).collect(Collectors.toList());
        widerListWithSets.removeAll(rangeWithSets);
        return widerListWithSets.stream().map(comboAsSet -> comboAsSet.stream().collect(Collectors.toList())).collect(Collectors.toList());
    }

    private List<List<Card>> filterOutDoubleCombos(List<List<Card>> input) {
        Set<Set<Card>> asSet = input.stream().map(combo -> combo.stream().collect(Collectors.toSet())).collect(Collectors.toSet());
        return asSet.stream().map(comboAsSet -> comboAsSet.stream().collect(Collectors.toList())).collect(Collectors.toList());
    }

    private String determineOppSizingGroup(double oppTotalBetsize) {
        String oppSizingGroup;

        if(oppTotalBetsize <= 60) {
            oppSizingGroup = SMALL;
        } else if(oppTotalBetsize <= 160) {
            oppSizingGroup = MEDIUM;
        } else {
            oppSizingGroup = LARGE;
        }

        return oppSizingGroup;
    }

    private boolean comboIsSuitedConnector(List<Card> combo, int gapWith) {
        boolean comboIsSuitedConnector = false;

        if(combo.get(0).getSuit() == combo.get(1).getSuit()) {
            int rankCard1ToUse = combo.get(0).getRank();
            int rankCard2ToUse = combo.get(1).getRank();

            if(rankCard1ToUse > rankCard2ToUse) {
                if(rankCard1ToUse - rankCard2ToUse == gapWith) {
                    comboIsSuitedConnector = true;
                }
            } else {
                if(rankCard2ToUse - rankCard1ToUse == gapWith) {
                    comboIsSuitedConnector = true;
                }
            }


            if(!comboIsSuitedConnector &&
                    (combo.get(0).getRank() == 14 || combo.get(1).getRank() == 14)) {
                if(combo.get(0).getRank() == 14) {
                    rankCard1ToUse = 1;
                } else {
                    rankCard2ToUse = 1;
                }

                if(rankCard1ToUse > rankCard2ToUse) {
                    if(rankCard1ToUse - rankCard2ToUse == gapWith) {
                        comboIsSuitedConnector = true;
                    }
                } else {
                    if(rankCard2ToUse - rankCard1ToUse == gapWith) {
                        comboIsSuitedConnector = true;
                    }
                }
            }
        }

        return comboIsSuitedConnector;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public List<List<Card>> createStartingOppRange(List<Card> botHoleCards) {
        List<List<Card>> startingOppRange = ActionBuilderUtil.getAllPossibleStartHandsAsList()
                .values()
                .stream()
                .collect(Collectors.toList());

        startingOppRange = startingOppRange
                .stream()
                .filter(combo -> Collections.disjoint(combo, botHoleCards))
                .collect(Collectors.toList());

        return startingOppRange;
    }
}
