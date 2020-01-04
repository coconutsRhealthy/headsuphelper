package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.dbsave.DbSave;
import com.lennart.model.card.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nash {

    public boolean nashActionIsPossible(double effectiveStackBb, boolean position, double botBetSizeBb, List<Card> board,
                                        String opponentAction, double opponentStackBb, double amountToCallBb, double bigBlind) {
        double nashPositionBbLimit;

        if(bigBlind <= 30) {
            nashPositionBbLimit = 10;
        } else if(bigBlind == 40) {
            nashPositionBbLimit = 7;
        } else if(bigBlind == 50) {
            nashPositionBbLimit = 5;
        } else {
            nashPositionBbLimit = 3;
        }

        boolean nashActionIsPossible = false;

        if(board == null || board.isEmpty()) {
            if(effectiveStackBb <= 20) {
                if(position) {
                    if(botBetSizeBb == 0.5) {
                        if(effectiveStackBb <= nashPositionBbLimit) {
                            if(opponentAction.equals("bet")) {
                                nashActionIsPossible = true;
                                System.out.println("IP Nash action is possible!");
                            }
                        }
                    }
                } else {
                    if(botBetSizeBb == 1 && opponentAction.equals("raise") && opponentStackBb == 0 && amountToCallBb <= 20) {
                        nashActionIsPossible = true;
                        System.out.println("OOP Nash action is possible!");
                    }
                }
            }
        }

        return nashActionIsPossible;
    }

    public String doNashAction(List<Card> holeCards, boolean position, double effectiveStack, double amountToCallBb) {
        String holeCardsAsString = new DbSave().getComboLogic(holeCards);
        String nashAction;

        if(position) {
            Map<String, Double> pushMap = getPushMap();
            double stackLimitToPushBb = pushMap.get(holeCardsAsString);

            if(effectiveStack <= stackLimitToPushBb) {
                nashAction = "raise";
                System.out.println("Set IP Nash action to raise for: " + holeCardsAsString);
            } else {
                nashAction = "fold";
                System.out.println("Set IP Nash action to fold for: " + holeCardsAsString);
            }
        } else {
            Map<String, Double> callMap = getCallMap();
            double stackLimitToCallBb = callMap.get(holeCardsAsString);

            if(amountToCallBb <= stackLimitToCallBb) {
                nashAction = "call";
                System.out.println("Set OOP Nash action to call for: " + holeCardsAsString);
            } else {
                nashAction = "fold";
                System.out.println("Set OOP Nash action to fold for: " + holeCardsAsString);
            }
        }

        return nashAction;
    }

    private Map<String, Double> getPushMap() {
        Map<String, Double> pushMap = new HashMap<>();

        pushMap.put("AA", 20.0);
        pushMap.put("AKs", 20.0);
        pushMap.put("AQs", 20.0);
        pushMap.put("AJs", 20.0);
        pushMap.put("ATs", 20.0);
        pushMap.put("A9s", 20.0);
        pushMap.put("A8s", 20.0);
        pushMap.put("A7s", 20.0);
        pushMap.put("A6s", 20.0);
        pushMap.put("A5s", 20.0);
        pushMap.put("A4s", 20.0);
        pushMap.put("A3s", 20.0);
        pushMap.put("A2s", 20.0);

        pushMap.put("AKo", 20.0);
        pushMap.put("KK", 20.0);
        pushMap.put("KQs", 20.0);
        pushMap.put("KJs", 20.0);
        pushMap.put("KTs", 20.0);
        pushMap.put("K9s", 20.0);
        pushMap.put("K8s", 20.0);
        pushMap.put("K7s", 20.0);
        pushMap.put("K6s", 20.0);
        pushMap.put("K5s", 20.0);
        pushMap.put("K4s", 20.0);
        pushMap.put("K3s", 19.9);
        pushMap.put("K2s", 19.3);

        pushMap.put("AQo", 20.0);
        pushMap.put("KQo", 20.0);
        pushMap.put("QQ", 20.0);
        pushMap.put("QJs", 20.0);
        pushMap.put("QTs", 20.0);
        pushMap.put("Q9s", 20.0);
        pushMap.put("Q8s", 20.0);
        pushMap.put("Q7s", 20.0);
        pushMap.put("Q6s", 20.0);
        pushMap.put("Q5s", 20.0);
        pushMap.put("Q4s", 16.3);
        pushMap.put("Q3s", 13.5);
        pushMap.put("Q2s", 12.7);

        pushMap.put("AJo", 20.0);
        pushMap.put("KJo", 20.0);
        pushMap.put("QJo", 20.0);
        pushMap.put("JJ", 20.0);
        pushMap.put("JTs", 20.0);
        pushMap.put("J9s", 20.0);
        pushMap.put("J8s", 20.0);
        pushMap.put("J7s", 20.0);
        pushMap.put("J6s", 18.6);
        pushMap.put("J5s", 14.7);
        pushMap.put("J4s", 13.5);
        pushMap.put("J3s", 10.6);
        pushMap.put("J2s", 8.5);

        pushMap.put("ATo", 20.0);
        pushMap.put("KTo", 20.0);
        pushMap.put("QTo", 20.0);
        pushMap.put("JTo", 20.0);
        pushMap.put("TT", 20.0);
        pushMap.put("T9s", 20.0);
        pushMap.put("T8s", 20.0);
        pushMap.put("T7s", 20.0);
        pushMap.put("T6s", 20.0);
        pushMap.put("T5s", 11.9);
        pushMap.put("T4s", 10.5);
        pushMap.put("T3s", 7.7);
        pushMap.put("T2s", 6.5);

        pushMap.put("A9o", 20.0);
        pushMap.put("K9o", 20.0);
        pushMap.put("Q9o", 20.0);
        pushMap.put("J9o", 20.0);
        pushMap.put("T9o", 20.0);
        pushMap.put("99", 20.0);
        pushMap.put("98s", 20.0);
        pushMap.put("97s", 20.0);
        pushMap.put("96s", 20.0);
        pushMap.put("95s", 14.4);
        pushMap.put("94s", 6.9);
        pushMap.put("93s", 4.9);
        pushMap.put("92s", 3.7);

        pushMap.put("A8o", 20.0);
        pushMap.put("K8o", 18.0);
        pushMap.put("Q8o", 13.0);
        pushMap.put("J8o", 13.3);
        pushMap.put("T8o", 17.5);
        pushMap.put("98o", 20.0);
        pushMap.put("88", 20.0);
        pushMap.put("87s", 20.0);
        pushMap.put("86s", 20.0);
        pushMap.put("85s", 18.8);
        pushMap.put("84s", 10.1);
        pushMap.put("83s", 2.7);
        pushMap.put("82s", 2.5);

        pushMap.put("A7o", 20.0);
        pushMap.put("K7o", 16.1);
        pushMap.put("Q7o", 10.3);
        pushMap.put("J7o", 8.5);
        pushMap.put("T7o", 9.0);
        pushMap.put("97o", 10.8);
        pushMap.put("87o", 14.7);
        pushMap.put("77", 20.0);
        pushMap.put("76s", 20.0);
        pushMap.put("75s", 20.0);
        pushMap.put("74s", 13.9);
        pushMap.put("73s", 2.5);
        pushMap.put("72s", 2.1);

        pushMap.put("A6o", 20.0);
        pushMap.put("K6o", 15.1);
        pushMap.put("Q6o", 9.6);
        pushMap.put("J6o", 6.5);
        pushMap.put("T6o", 5.7);
        pushMap.put("96o", 5.2);
        pushMap.put("86o", 7.0);
        pushMap.put("76o", 10.7);
        pushMap.put("66", 20.0);
        pushMap.put("65s", 20.0);
        pushMap.put("64s", 16.3);
        pushMap.put("63s", 7.1);
        pushMap.put("62s", 2.0);

        pushMap.put("A5o", 20.0);
        pushMap.put("K5o", 14.2);
        pushMap.put("Q5o", 8.9);
        pushMap.put("J5o", 6.0);
        pushMap.put("T5o", 4.1);
        pushMap.put("95o", 3.5);
        pushMap.put("85o", 3.0);
        pushMap.put("75o", 2.6);
        pushMap.put("65o", 2.4);
        pushMap.put("55", 20.0);
        pushMap.put("54s", 16.3);
        pushMap.put("53s", 12.9);
        pushMap.put("52s", 2.0);

        pushMap.put("A4o", 20.0);
        pushMap.put("K4o", 13.1);
        pushMap.put("Q4o", 7.9);
        pushMap.put("J4o", 5.4);
        pushMap.put("T4o", 3.8);
        pushMap.put("94o", 2.7);
        pushMap.put("84o", 2.3);
        pushMap.put("74o", 2.1);
        pushMap.put("64o", 2.0);
        pushMap.put("54o", 2.1);
        pushMap.put("44", 20.0);
        pushMap.put("43s", 10.0);
        pushMap.put("42s", 1.8);

        pushMap.put("A3o", 20.0);
        pushMap.put("K3o", 12.2);
        pushMap.put("Q3o", 7.5);
        pushMap.put("J3o", 5.0);
        pushMap.put("T3o", 3.4);
        pushMap.put("93o", 2.5);
        pushMap.put("83o", 1.9);
        pushMap.put("73o", 1.8);
        pushMap.put("63o", 1.7);
        pushMap.put("53o", 1.8);
        pushMap.put("43o", 1.6);
        pushMap.put("33", 20.0);
        pushMap.put("32s", 1.7);

        pushMap.put("A2o", 20.0);
        pushMap.put("K2o", 11.6);
        pushMap.put("Q2o", 7.0);
        pushMap.put("J2o", 4.6);
        pushMap.put("T2o", 2.9);
        pushMap.put("92o", 2.2);
        pushMap.put("82o", 1.8);
        pushMap.put("72o", 1.6);
        pushMap.put("62o", 1.5);
        pushMap.put("52o", 1.5);
        pushMap.put("42o", 1.4);
        pushMap.put("32o", 1.4);
        pushMap.put("22", 20.0);

        return pushMap;
    }

    private Map<String, Double> getCallMap() {
        Map<String, Double> callMap = new HashMap<>();

        callMap.put("AA", 20.0);
        callMap.put("AKs", 20.0);
        callMap.put("AQs", 20.0);
        callMap.put("AJs", 20.0);
        callMap.put("ATs", 20.0);
        callMap.put("A9s", 20.0);
        callMap.put("A8s", 20.0);
        callMap.put("A7s", 20.0);
        callMap.put("A6s", 20.0);
        callMap.put("A5s", 20.0);
        callMap.put("A4s", 20.0);
        callMap.put("A3s", 20.0);
        callMap.put("A2s", 20.0);

        callMap.put("AKo", 20.0);
        callMap.put("KK", 20.0);
        callMap.put("KQs", 20.0);
        callMap.put("KJs", 20.0);
        callMap.put("KTs", 20.0);
        callMap.put("K9s", 20.0);
        callMap.put("K8s", 17.6);
        callMap.put("K7s", 15.2);
        callMap.put("K6s", 14.3);
        callMap.put("K5s", 13.2);
        callMap.put("K4s", 12.1);
        callMap.put("K3s", 11.4);
        callMap.put("K2s", 10.7);

        callMap.put("AQo", 20.0);
        callMap.put("KQo", 20.0);
        callMap.put("QQ", 20.0);
        callMap.put("QJs", 20.0);
        callMap.put("QTs", 20.0);
        callMap.put("Q9s", 16.1);
        callMap.put("Q8s", 13.0);
        callMap.put("Q7s", 10.5);
        callMap.put("Q6s", 9.9);
        callMap.put("Q5s", 8.9);
        callMap.put("Q4s", 8.4);
        callMap.put("Q3s", 7.8);
        callMap.put("Q2s", 7.2);

        callMap.put("AJo", 20.0);
        callMap.put("KJo", 20.0);
        callMap.put("QJo", 19.5);
        callMap.put("JJ", 20.0);
        callMap.put("JTs", 18.0);
        callMap.put("J9s", 13.4);
        callMap.put("J8s", 10.6);
        callMap.put("J7s", 8.8);
        callMap.put("J6s", 7.0);
        callMap.put("J5s", 6.9);
        callMap.put("J4s", 6.1);
        callMap.put("J3s", 5.8);
        callMap.put("J2s", 5.6);

        callMap.put("ATo", 20.0);
        callMap.put("KTo", 20.0);
        callMap.put("QTo", 15.3);
        callMap.put("JTo", 12.7);
        callMap.put("TT", 20.0);
        callMap.put("T9s", 11.5);
        callMap.put("T8s", 9.3);
        callMap.put("T7s", 7.4);
        callMap.put("T6s", 6.3);
        callMap.put("T5s", 5.2);
        callMap.put("T4s", 5.2);
        callMap.put("T3s", 4.8);
        callMap.put("T2s", 4.5);

        callMap.put("A9o", 20.0);
        callMap.put("K9o", 17.1);
        callMap.put("Q9o", 11.7);
        callMap.put("J9o", 9.5);
        callMap.put("T9o", 8.4);
        callMap.put("99", 20.0);
        callMap.put("98s", 8.2);
        callMap.put("97s", 7.0);
        callMap.put("96s", 5.8);
        callMap.put("95s", 5.0);
        callMap.put("94s", 4.3);
        callMap.put("93s", 4.1);
        callMap.put("92s", 3.9);

        callMap.put("A8o", 20.0);
        callMap.put("K8o", 13.8);
        callMap.put("Q8o", 9.7);
        callMap.put("J8o", 7.6);
        callMap.put("T8o", 6.6);
        callMap.put("98o", 6.0);
        callMap.put("88", 20.0);
        callMap.put("87s", 6.5);
        callMap.put("86s", 5.6);
        callMap.put("85s", 4.8);
        callMap.put("84s", 4.1);
        callMap.put("83s", 3.6);
        callMap.put("82s", 3.5);

        callMap.put("A7o", 20.0);
        callMap.put("K7o", 12.4);
        callMap.put("Q7o", 8.0);
        callMap.put("J7o", 6.4);
        callMap.put("T7o", 5.5);
        callMap.put("97o", 5.0);
        callMap.put("87o", 4.7);
        callMap.put("77", 20.0);
        callMap.put("76s", 5.4);
        callMap.put("75s", 4.8);
        callMap.put("74s", 4.1);
        callMap.put("73s", 3.6);
        callMap.put("72s", 3.3);

        callMap.put("A6o", 20.0);
        callMap.put("K6o", 11.0);
        callMap.put("Q6o", 7.3);
        callMap.put("J6o", 5.4);
        callMap.put("T6o", 4.6);
        callMap.put("96o", 4.2);
        callMap.put("86o", 4.1);
        callMap.put("76o", 4.0);
        callMap.put("66", 20.0);
        callMap.put("65s", 4.9);
        callMap.put("64s", 4.3);
        callMap.put("63s", 3.8);
        callMap.put("62s", 3.3);

        callMap.put("A5o", 20.0);
        callMap.put("K5o", 10.2);
        callMap.put("Q5o", 6.8);
        callMap.put("J5o", 5.1);
        callMap.put("T5o", 4.0);
        callMap.put("95o", 3.7);
        callMap.put("85o", 3.6);
        callMap.put("75o", 3.6);
        callMap.put("65o", 3.7);
        callMap.put("55", 20.0);
        callMap.put("54s", 4.6);
        callMap.put("53s", 4.0);
        callMap.put("52s", 3.6);

        callMap.put("A4o", 18.3);
        callMap.put("K4o", 9.1);
        callMap.put("Q4o", 6.2);
        callMap.put("J4o", 4.7);
        callMap.put("T4o", 3.8);
        callMap.put("94o", 3.3);
        callMap.put("84o", 3.2);
        callMap.put("74o", 3.2);
        callMap.put("64o", 3.3);
        callMap.put("54o", 3.5);
        callMap.put("44", 20.0);
        callMap.put("43s", 3.8);
        callMap.put("42s", 3.4);

        callMap.put("A3o", 16.6);
        callMap.put("K3o", 8.7);
        callMap.put("Q3o", 5.9);
        callMap.put("J3o", 4.5);
        callMap.put("T3o", 3.6);
        callMap.put("93o", 3.1);
        callMap.put("83o", 2.9);
        callMap.put("73o", 2.9);
        callMap.put("63o", 2.9);
        callMap.put("53o", 3.1);
        callMap.put("43o", 3.0);
        callMap.put("33", 20.0);
        callMap.put("32s", 3.3);

        callMap.put("A2o", 15.8);
        callMap.put("K2o", 8.1);
        callMap.put("Q2o", 5.6);
        callMap.put("J2o", 4.2);
        callMap.put("T2o", 3.5);
        callMap.put("92o", 3.0);
        callMap.put("82o", 2.8);
        callMap.put("72o", 2.6);
        callMap.put("62o", 2.7);
        callMap.put("52o", 2.8);
        callMap.put("42o", 2.7);
        callMap.put("32o", 2.6);
        callMap.put("22", 15.0);

        return callMap;
    }
}
