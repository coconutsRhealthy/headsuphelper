package com.lennart.model.action.actionbuilders;

import com.lennart.model.action.actionbuilders.ai.ActionVariables;
import com.lennart.model.action.actionbuilders.ai.ContinuousTable;
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

    public static void printAdjustedActions(long sessionStartTime) {
        Map<Long, Map<String, String>> actionAdjustments = ContinuousTable.getActionAdjustments();

        int checkToBetCounterTotal = 0;
        int checkToBetAgainstUnknownCounter = 0;
        int betToCheckCounterTotal = 0;
        int betToCheckAgainstUnknownCounter = 0;

        int foldToCallCounterTotal = 0;
        int foldToCallPreflopCounter = 0;
        int foldToCallPostflopCounter = 0;
        int foldToCallAgainstUnknownCounter = 0;

        int foldToRaiseCounterTotal = 0;
        int foldToRaisePreflopCounter = 0;
        int foldToRaisePostflopCounter = 0;
        int foldToRaiseAgainstUnknownCounter = 0;

        int callToFoldCounterTotal = 0;
        int callToFoldPreflopCounter = 0;
        int callToFoldPostflopCounter = 0;
        int callToFoldAgainstUnknownCounter = 0;

        int callToRaiseCounterTotal = 0;
        int callToRaisePreflopCounter = 0;
        int callToRaisePostflopCounter = 0;
        int callToRaiseAgainstUnknownCounter = 0;

        int raiseToFoldCounterTotal = 0;
        int raiseToFoldPreflopCounter = 0;
        int raiseToFoldPostflopCounter = 0;
        int raiseToFoldAgainstUnknownCounter = 0;

        int raiseToCallCounterTotal = 0;
        int raiseToCallPreflopCounter = 0;
        int raiseToCallPostflopCounter = 0;
        int raiseToCallAgainstUnknownCounter = 0;

        List<String> allAdjustmentTypes = new ArrayList<>();

        for(Map.Entry<Long, Map<String, String>> entry : actionAdjustments.entrySet()) {
           Map<String, String> actionRecordMap = entry.getValue();

           String oldAction = actionRecordMap.get("oldAction");
           String newAction = actionRecordMap.get("adjustedAction");
           String adjustmentType = actionRecordMap.get("adjustmentType");
           int boardSize = Integer.parseInt(actionRecordMap.get("boardSize"));
           boolean unknownOpp = Boolean.parseBoolean(actionRecordMap.get("unknownOpp"));

           allAdjustmentTypes.add(adjustmentType);

           if(oldAction.equals("check") && newAction.equals("bet75pct")) {
               checkToBetCounterTotal++;

               if(unknownOpp) {
                   checkToBetAgainstUnknownCounter++;
               }
           }

           if(oldAction.equals("bet75pct") && newAction.equals("check")) {
               betToCheckCounterTotal++;

               if(unknownOpp) {
                   betToCheckAgainstUnknownCounter++;
               }
           }

           if(oldAction.equals("fold")) {
               if(newAction.equals("call")) {
                   foldToCallCounterTotal++;

                   if(boardSize >= 3) {
                       foldToCallPostflopCounter++;
                   } else {
                       foldToCallPreflopCounter++;
                   }

                   if(unknownOpp) {
                       foldToCallAgainstUnknownCounter++;
                   }
               }

               if(newAction.equals("raise")) {
                   foldToRaiseCounterTotal++;

                   if(boardSize >= 3) {
                       foldToRaisePostflopCounter++;
                   } else {
                       foldToRaisePreflopCounter++;
                   }

                   if(unknownOpp) {
                       foldToRaiseAgainstUnknownCounter++;
                   }
               }
           }

           if(oldAction.equals("call")) {
               if(newAction.equals("fold")) {
                   callToFoldCounterTotal++;

                   if(boardSize >= 3) {
                       callToFoldPostflopCounter++;
                   } else {
                       callToFoldPreflopCounter++;
                   }

                   if(unknownOpp) {
                       callToFoldAgainstUnknownCounter++;
                   }
               }

               if(newAction.equals("raise")) {
                   callToRaiseCounterTotal++;

                   if(boardSize >= 3) {
                       callToRaisePostflopCounter++;
                   } else {
                       callToRaisePreflopCounter++;
                   }

                   if(unknownOpp) {
                       callToRaiseAgainstUnknownCounter++;
                   }
               }
           }

           if(oldAction.equals("raise")) {
               if (newAction.equals("fold")) {
                   raiseToFoldCounterTotal++;

                   if (boardSize >= 3) {
                       raiseToFoldPostflopCounter++;
                   } else {
                       raiseToFoldPreflopCounter++;
                   }

                   if (unknownOpp) {
                       raiseToFoldAgainstUnknownCounter++;
                   }
               }

               if (newAction.equals("call")) {
                   raiseToCallCounterTotal++;

                   if (boardSize >= 3) {
                       raiseToCallPostflopCounter++;
                   } else {
                       raiseToCallPreflopCounter++;
                   }

                   if (unknownOpp) {
                       raiseToCallAgainstUnknownCounter++;
                   }
               }
           }
        }

        Collections.sort(allAdjustmentTypes);

        try {
            PrintWriter writer = new PrintWriter("/Users/lennartmac/Documents/printedstats/actionadjustments/actionadjustments_" + sessionStartTime + ".txt", "UTF-8");

            writer.println("check to bet");
            writer.println("total: " + checkToBetCounterTotal);
            writer.println("against unknown: " + checkToBetAgainstUnknownCounter);
            writer.println();
            writer.println("bet to check");
            writer.println("total: " + betToCheckCounterTotal);
            writer.println("against unknown: " + betToCheckAgainstUnknownCounter);
            writer.println();
            writer.println("fold to call");
            writer.println("total: " + foldToCallCounterTotal);
            writer.println("preflop: " + foldToCallPreflopCounter);
            writer.println("postflop: " + foldToCallPostflopCounter);
            writer.println("against unknown: " + foldToCallAgainstUnknownCounter);
            writer.println();
            writer.println("fold to raise");
            writer.println("total: " + foldToRaiseCounterTotal);
            writer.println("preflop: " + foldToRaisePreflopCounter);
            writer.println("postflop: " + foldToRaisePostflopCounter);
            writer.println("against unknown: " + foldToRaiseAgainstUnknownCounter);
            writer.println();
            writer.println("call to fold");
            writer.println("total: " + callToFoldCounterTotal);
            writer.println("preflop: " + callToFoldPreflopCounter);
            writer.println("postflop: " + callToFoldPostflopCounter);
            writer.println("against unknown: " + callToFoldAgainstUnknownCounter);
            writer.println();
            writer.println("call to raise");
            writer.println("total: " + callToRaiseCounterTotal);
            writer.println("preflop: " + callToRaisePreflopCounter);
            writer.println("postflop: " + callToRaisePostflopCounter);
            writer.println("against unknown: " + callToRaiseAgainstUnknownCounter);
            writer.println();
            writer.println("raise to fold");
            writer.println("total: " + raiseToFoldCounterTotal);
            writer.println("preflop: " + raiseToFoldPreflopCounter);
            writer.println("postflop: " + raiseToFoldPostflopCounter);
            writer.println("against unknown: " + raiseToFoldAgainstUnknownCounter);
            writer.println();
            writer.println("raise to call");
            writer.println("total: " + raiseToCallCounterTotal);
            writer.println("preflop: " + raiseToCallPreflopCounter);
            writer.println("postflop: " + raiseToCallPostflopCounter);
            writer.println("against unknown: " + raiseToCallAgainstUnknownCounter);
            writer.println();
            writer.println();
            writer.println();
            writer.println("ADJUSTMENT TYPES:");
            writer.println();

            for(String adjustmentType : allAdjustmentTypes) {
                writer.println(adjustmentType);
            }

            writer.println();
            writer.close();
        } catch (Exception e) {
            System.out.println("Error in printAdjustedActions to file...");
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
