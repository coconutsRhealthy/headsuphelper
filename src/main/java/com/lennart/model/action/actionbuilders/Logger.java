package com.lennart.model.action.actionbuilders;

import com.lennart.model.action.actionbuilders.ai.ActionVariables;
import com.lennart.model.action.actionbuilders.ai.GameVariables;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.sites.party.PartyTableReader;

import java.io.PrintWriter;
import java.util.*;

public class Logger {

    public static void doLogging(GameVariables gameVariables, ActionVariables actionVariables, int numberOfActionRequests) throws Exception {
        PartyTableReader.saveScreenshotOfEntireScreen(numberOfActionRequests);

        String opponentStack = String.valueOf(gameVariables.getOpponentStack());
        String opponentBetSize = String.valueOf(gameVariables.getOpponentBetSize());
        String board = getCardListAsString(gameVariables.getBoard());
        String potSize = String.valueOf(gameVariables.getPot());
        String botBetSize = String.valueOf(gameVariables.getBotBetSize());
        String botStack = String.valueOf(gameVariables.getBotStack());
        String botHoleCards = getCardListAsString(gameVariables.getBotHoleCards());
        String opponentAction = gameVariables.getOpponentAction();
        String route = actionVariables.getRoute();
        String table = actionVariables.getTable();
        String suggestedAction = actionVariables.getAction();
        String sizing = String.valueOf(actionVariables.getSizing());

        PrintWriter writer = new PrintWriter("/Users/lennartmac/Documents/logging/" + numberOfActionRequests + ".txt", "UTF-8");

        writer.println("OpponentStack: " + opponentStack);
        writer.println("OpponentBetSize: " + opponentBetSize);
        writer.println("Board: " + board);
        writer.println("Potsize: " + potSize);
        writer.println("BotBetSize: " + botBetSize);
        writer.println("BotStack: " + botStack);
        writer.println("BotHoleCards: " + botHoleCards);
        writer.println("OpponentAction: " + opponentAction);
        writer.println();

        writer.println("------------------------");
        writer.println();

        writer.println("Route: " + route);
        writer.println("Table: " + table);
        writer.println("Action: " + suggestedAction);
        writer.println("Sizing: " + sizing);

        writer.close();
    }

    private void doRangeLogging(List<List<Card>> oppRange, int numberOfActionRequests) throws Exception {
        PrintWriter writer = new PrintWriter("/Users/lennartmac/Documents/logging/" + numberOfActionRequests + "-range.txt", "UTF-8");

        int counter = 1;

        if(oppRange != null) {
            for(List<Card> combo : oppRange) {
                Card card1 = combo.get(0);
                Card card2 = combo.get(1);

                writer.println("" + counter + ")  " + card1.getRank() + card1.getSuit() + " " + card2.getRank() + card2.getSuit() + "");
                counter++;
            }
        } else {
            System.out.println("oppRange is null!");
        }

        writer.close();
    }

    public static void printActionDurationsToTextFile(Map<String, List<Long>> botActionDurations, long sessionStartTime) throws Exception {
        List<Long> preflopDurations = botActionDurations.get("preflop");
        List<Long> flopDurations = botActionDurations.get("flop");
        List<Long> turnDurations = botActionDurations.get("turn");
        List<Long> riverDurations = botActionDurations.get("river");

        Collections.sort(preflopDurations);
        Collections.sort(flopDurations);
        Collections.sort(turnDurations);
        Collections.sort(riverDurations);

        PrintWriter writer = new PrintWriter("/Users/lennartmac/Documents/printedstats/actiondurations/durations_" + sessionStartTime + ".txt", "UTF-8");

        writer.println("PREFLOP");
        for(int i = 0; i < preflopDurations.size(); i++) {
            writer.println("" + i + ") " + preflopDurations.get(i));
        }
        writer.println();
        writer.println();

        writer.println("FLOP");
        for(int i = 0; i < flopDurations.size(); i++) {
            writer.println("" + i + ") " + flopDurations.get(i));
        }
        writer.println();
        writer.println();

        writer.println("TURN");
        for(int i = 0; i < turnDurations.size(); i++) {
            writer.println("" + i + ") " + turnDurations.get(i));
        }
        writer.println();
        writer.println();

        writer.println("RIVER");
        for(int i = 0; i < riverDurations.size(); i++) {
            writer.println("" + i + ") " + riverDurations.get(i));
        }
        writer.println();
        writer.println();

        OptionalDouble optPreflopAverage = preflopDurations.stream().mapToDouble(a -> a).average();
        OptionalDouble optFlopAverage = flopDurations.stream().mapToDouble(a -> a).average();
        OptionalDouble optTurnAverage = turnDurations.stream().mapToDouble(a -> a).average();
        OptionalDouble optRiverAverage = riverDurations.stream().mapToDouble(a -> a).average();

        double preflopAverage = optPreflopAverage.isPresent() ? optPreflopAverage.getAsDouble() : 0;
        double flopAverage = optFlopAverage.isPresent() ? optFlopAverage.getAsDouble() : 0;
        double turnAverage = optTurnAverage.isPresent() ? optTurnAverage.getAsDouble() : 0;
        double riverAverage = optRiverAverage.isPresent() ? optRiverAverage.getAsDouble() : 0;

        List<Long> allDurations = new ArrayList<>();
        allDurations.addAll(preflopDurations);
        allDurations.addAll(flopDurations);
        allDurations.addAll(turnDurations);
        allDurations.addAll(riverDurations);

        OptionalDouble optAllAverage = allDurations.stream().mapToDouble(a -> a).average();
        double allAverage = optAllAverage.isPresent() ? optAllAverage.getAsDouble() : 0;

        writer.println("AVERAGES");
        writer.println("Preflop: " + preflopAverage);
        writer.println("Flop: " + flopAverage);
        writer.println("Turn: " + turnAverage);
        writer.println("River: " + riverAverage);
        writer.println("Overall: " + allAverage);
        writer.println();
        writer.println();

        writer.close();
    }

    public static void printOppTypeData(List<Integer> allNumberOfHands, List<String> oppTypes, List<String> numberOfHandWithOppType,
                                        long sessionStartTime) throws Exception {
        double _0_25freq = 0;
        double _25_50freq = 0;
        double _50_75freq = 0;
        double _75_100freq = 0;
        double _100_150freq = 0;
        double _150_200freq = 0;
        double _200_upfreq = 0;
        double totalNumberFreq = allNumberOfHands.size();

        for(Integer numberOfHands : allNumberOfHands) {
            if(numberOfHands < 25) {
                _0_25freq++;
            } else if(numberOfHands < 50) {
                _25_50freq++;
            } else if(numberOfHands < 75) {
                _50_75freq++;
            } else if(numberOfHands < 100) {
                _75_100freq++;
            } else if(numberOfHands < 150) {
                _100_150freq++;
            } else if(numberOfHands < 200) {
                _150_200freq++;
            } else {
                _200_upfreq++;
            }
        }

        double tpFreq = Collections.frequency(oppTypes, "tp");
        double lpFreq = Collections.frequency(oppTypes, "lp");
        double taFreq = Collections.frequency(oppTypes, "ta");
        double laFreq = Collections.frequency(oppTypes, "la");
        double totalTypeFreq = oppTypes.size();

        PrintWriter writer = new PrintWriter("/Users/lennartmac/Documents/printedstats/opptypes/opptypes_" + sessionStartTime + ".txt", "UTF-8");

        writer.println("NUMBER OF HANDS");
        writer.println("0-25: " + _0_25freq + (" (" + _0_25freq / totalNumberFreq + ")"));
        writer.println("25-50: " + _25_50freq + (" (" + _25_50freq / totalNumberFreq + ")"));
        writer.println("50-75: " + _50_75freq + (" (" + _50_75freq / totalNumberFreq + ")"));
        writer.println("75-100: " + _75_100freq + (" (" + _75_100freq / totalNumberFreq + ")"));
        writer.println("100-150: " + _100_150freq + (" (" + _100_150freq / totalNumberFreq + ")"));
        writer.println("150-200: " + _150_200freq + (" (" + _150_200freq / totalNumberFreq + ")"));
        writer.println("200-up: " + _200_upfreq + (" (" + _200_upfreq / totalNumberFreq + ")"));
        writer.println();
        writer.println("OPPTYPES TOTAL: ");
        writer.println("tp: " + tpFreq + (" (" + tpFreq / totalTypeFreq + ")"));
        writer.println("lp: " + lpFreq + (" (" + lpFreq / totalTypeFreq + ")"));
        writer.println("ta: " + taFreq + (" (" + taFreq / totalTypeFreq + ")"));
        writer.println("la: " + laFreq + (" (" + laFreq / totalTypeFreq + ")"));
        writer.println();
        writer.println("OPPTYPES below 100: ");
        printOppTypesOfAmountOfHandsGroup("below100", writer, numberOfHandWithOppType);
        writer.println("OPPTYPES between 100_200: ");
        printOppTypesOfAmountOfHandsGroup("100_200", writer, numberOfHandWithOppType);
        writer.println("OPPTYPES above 200: ");
        printOppTypesOfAmountOfHandsGroup("above200", writer, numberOfHandWithOppType);

        writer.close();
    }

    private static void printOppTypesOfAmountOfHandsGroup(String handGroup, PrintWriter writer, List<String> numberOfHandWithOppType) {
        List<String> oppTypes = new ArrayList<>();

        for(String numberTypeCombo : numberOfHandWithOppType) {
            double number = Double.parseDouble(numberTypeCombo.split("_")[0]);
            String oppType = numberTypeCombo.split("_")[1];

            if(handGroup.equals("below100")) {
                if(number < 100) {
                    oppTypes.add(oppType);
                }
            } else if(handGroup.equals("100_200")) {
                if(number >= 100 && number < 200) {
                    oppTypes.add(oppType);
                }
            } else if(handGroup.equals("above200")) {
                if(number >= 200) {
                    oppTypes.add(oppType);
                }
            }
        }

        double tpFreq = Collections.frequency(oppTypes, "tp");
        double lpFreq = Collections.frequency(oppTypes, "lp");
        double taFreq = Collections.frequency(oppTypes, "ta");
        double laFreq = Collections.frequency(oppTypes, "la");
        double totalTypeFreq = oppTypes.size();

        writer.println("tp: " + tpFreq + (" (" + tpFreq / totalTypeFreq + ")"));
        writer.println("lp: " + lpFreq + (" (" + lpFreq / totalTypeFreq + ")"));
        writer.println("ta: " + taFreq + (" (" + taFreq / totalTypeFreq + ")"));
        writer.println("la: " + laFreq + (" (" + laFreq / totalTypeFreq + ")"));
        writer.println();
    }

    private static String getCardListAsString(List<Card> cardList) {
        String cardListAsString = "initial";

        if(cardList != null && !cardList.isEmpty()) {
            cardListAsString = "";

            for(Card card : cardList) {
                cardListAsString = cardListAsString + card.getRank() + card.getSuit() + " ";
            }
        }
        return cardListAsString;
    }

    public static void printOpponentNames(Map<Double, Map<String, Integer>> handsOfOpponentsPerStake, long sessionStartTime) {
        try {
            PrintWriter writer = new PrintWriter("/Users/lennartmac/Documents/printedstats/oppnames/oppnames_" + sessionStartTime + ".txt", "UTF-8");

            for(Map.Entry<Double, Map<String, Integer>> entryOuter : handsOfOpponentsPerStake.entrySet()) {
                writer.println("BUYIN: " + entryOuter.getKey());

                Map<String, Integer> handsPerOpponenent = entryOuter.getValue();
                handsPerOpponenent = sortByValueHighToLow(handsPerOpponenent);
                int oppCounter = 0;

                for(Map.Entry<String, Integer> entryInner : handsPerOpponenent.entrySet()) {
                    oppCounter++;
                    writer.println("" + oppCounter + ") " + entryInner.getKey() + "   " + entryInner.getValue());
                }

                writer.println();
            }

            writer.close();
        } catch (Exception e) {
            System.out.println("Error in printOppNames to file...");
            e.printStackTrace();
        }
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
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
}
