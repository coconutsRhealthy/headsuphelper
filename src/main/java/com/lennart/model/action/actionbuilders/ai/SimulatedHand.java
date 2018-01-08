package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;

import java.util.*;

/**
 * Created by lpo21630 on 4-1-2018.
 */
public class SimulatedHand {

    private double aiBotStack = 50;
    private double ruleBotStack = 50;
    private double pot = 0;
    private boolean aiBotIsButton;
    private List<Card> deck = BoardEvaluator.getCompleteCardDeck();
    private List<Card> aiBotHolecards = new ArrayList<>();
    private List<Card> ruleBotHolecards = new ArrayList<>();
    private List<Card> board = new ArrayList<>();

    private double aiBotBetSize = 0;
    private double ruleBotBetSize = 0;

    private boolean continueHand = true;
    private boolean nextStreetNeedsToBeDealt = false;

    private boolean playerIsAllIn = false;

    private String aiBotAction = "empty";
    private String ruleBotAction = "empty";

    private boolean potAllocated = false;

    private double aiBotHandStrength = -1;
    private double ruleBotHandStrength = -1;

    private static Map<String, Map<String, List<Double>>> payoffMap = new HashMap<>();

    private List<String> aiBotActionHistory = new ArrayList<>();

    public static void main(String[] args) {
        double aiBotTotalScore = 0;
        double ruleBotTotalScore = 0;

        SimulatedHand.initializePayoffMap();

        for(int i = 0; i < 100; i++) {
            Random rn = new Random();
            int y = rn.nextInt(2 - 1 + 1) + 1;

            SimulatedHand simulatedHand = new SimulatedHand(y);
            Map<String, Double> scores = simulatedHand.playHand();

            aiBotTotalScore = aiBotTotalScore + scores.get("aiBot");
            ruleBotTotalScore = ruleBotTotalScore + scores.get("ruleBot");
        }

        System.out.println("aiBot total score: " + aiBotTotalScore);
        System.out.println("ruleBot total score: " + ruleBotTotalScore);
    }

    private static void initializePayoffMap() {
        payoffMap.put("0-5", new HashMap<>());
        payoffMap.put("5-10", new HashMap<>());
        payoffMap.put("10-15", new HashMap<>());
        payoffMap.put("15-20", new HashMap<>());
        payoffMap.put("20-25", new HashMap<>());
        payoffMap.put("25-30", new HashMap<>());
        payoffMap.put("30-35", new HashMap<>());
        payoffMap.put("35-40", new HashMap<>());
        payoffMap.put("40-45", new HashMap<>());
        payoffMap.put("45-50", new HashMap<>());
        payoffMap.put("50-55", new HashMap<>());
        payoffMap.put("55-60", new HashMap<>());
        payoffMap.put("60-65", new HashMap<>());
        payoffMap.put("65-70", new HashMap<>());
        payoffMap.put("70-75", new HashMap<>());
        payoffMap.put("75-80", new HashMap<>());
        payoffMap.put("80-85", new HashMap<>());
        payoffMap.put("85-90", new HashMap<>());
        payoffMap.put("90-95", new HashMap<>());
        payoffMap.put("95-100", new HashMap<>());

        payoffMap.get("0-5").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("0-5").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("0-5").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("0-5").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("0-5").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("0-5").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("0-5").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("0-5").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("0-5").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("0-5").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("5-10").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("5-10").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("5-10").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("5-10").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("5-10").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("5-10").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("5-10").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("5-10").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("5-10").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("5-10").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("10-15").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("10-15").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("10-15").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("10-15").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("10-15").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("10-15").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("10-15").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("10-15").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("10-15").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("10-15").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("15-20").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("15-20").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("15-20").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("15-20").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("15-20").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("15-20").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("15-20").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("15-20").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("15-20").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("15-20").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("20-25").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("20-25").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("20-25").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("20-25").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("20-25").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("20-25").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("20-25").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("20-25").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("20-25").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("20-25").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("25-30").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("25-30").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("25-30").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("25-30").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("25-30").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("25-30").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("25-30").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("25-30").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("25-30").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("25-30").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("30-35").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("30-35").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("30-35").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("30-35").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("30-35").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("30-35").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("30-35").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("30-35").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("30-35").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("30-35").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("35-40").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("35-40").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("35-40").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("35-40").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("35-40").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("35-40").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("35-40").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("35-40").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("35-40").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("35-40").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("40-45").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("40-45").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("40-45").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("40-45").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("40-45").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("40-45").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("40-45").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("40-45").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("40-45").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("40-45").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("45-50").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("45-50").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("45-50").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("45-50").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("45-50").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("45-50").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("45-50").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("45-50").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("45-50").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("45-50").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("50-55").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("50-55").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("50-55").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("50-55").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("50-55").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("50-55").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("50-55").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("50-55").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("50-55").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("50-55").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("55-60").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("55-60").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("55-60").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("55-60").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("55-60").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("55-60").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("55-60").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("55-60").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("55-60").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("55-60").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("60-65").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("60-65").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("60-65").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("60-65").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("60-65").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("60-65").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("60-65").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("60-65").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("60-65").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("60-65").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("65-70").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("65-70").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("65-70").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("65-70").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("65-70").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("65-70").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("65-70").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("65-70").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("65-70").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("65-70").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("70-75").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("70-75").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("70-75").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("70-75").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("70-75").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("70-75").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("70-75").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("70-75").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("70-75").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("70-75").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("75-80").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("75-80").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("75-80").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("75-80").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("75-80").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("75-80").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("75-80").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("75-80").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("75-80").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("75-80").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("80-85").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("80-85").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("80-85").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("80-85").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("80-85").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("80-85").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("80-85").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("80-85").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("80-85").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("80-85").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("85-90").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("85-90").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("85-90").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("85-90").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("85-90").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("85-90").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("85-90").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("85-90").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("85-90").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("85-90").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("90-95").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("90-95").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("90-95").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("90-95").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("90-95").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("90-95").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("90-95").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("90-95").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("90-95").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("90-95").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("95-100").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("95-100").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("95-100").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("95-100").put("bet25%", Arrays.asList(0.0, 0.0));
        payoffMap.get("95-100").put("bet50%", Arrays.asList(0.0, 0.0));
        payoffMap.get("95-100").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("95-100").put("bet100%", Arrays.asList(0.0, 0.0));
        payoffMap.get("95-100").put("bet150%", Arrays.asList(0.0, 0.0));
        payoffMap.get("95-100").put("bet200%", Arrays.asList(0.0, 0.0));
        payoffMap.get("95-100").put("raise", Arrays.asList(0.0, 0.0));
    }

    public SimulatedHand(int numberOfHandsPlayed) {
        aiBotHolecards.add(getAndRemoveRandomCardFromDeck());
        aiBotHolecards.add(getAndRemoveRandomCardFromDeck());
        ruleBotHolecards.add(getAndRemoveRandomCardFromDeck());
        ruleBotHolecards.add(getAndRemoveRandomCardFromDeck());

        if(numberOfHandsPlayed % 2 == 0) {
            aiBotStack = 49.50;
            ruleBotStack = 49.50;
            aiBotIsButton = false;
        } else {
            aiBotStack = 49.50;
            ruleBotStack = 49.50;
            aiBotIsButton = true;
        }

        pot = 1;

        board.add(getAndRemoveRandomCardFromDeck());
        board.add(getAndRemoveRandomCardFromDeck());
        board.add(getAndRemoveRandomCardFromDeck());

        calculateHandStrengths();
    }

    public Map<String, Double> playHand() {
        loop: while(continueHand) {
            while(!nextStreetNeedsToBeDealt && !playerIsAllIn) {
                if(!aiBotIsButton) {
                    doAiBotAction();
                    if(aiBotAction.equals("fold")) {
                        break loop;
                    }

                    if(!nextStreetNeedsToBeDealt) {
                        doRuleBotAction();
                        if(ruleBotAction.equals("fold")) {
                            break loop;
                        }
                    }
                } else {
                    doRuleBotAction();
                    if(ruleBotAction.equals("fold")) {
                        break loop;
                    }

                    if(!nextStreetNeedsToBeDealt) {
                        doAiBotAction();
                        if(aiBotAction.equals("fold")) {
                            break loop;
                        }
                    }
                }
            }

            dealNextStreet();

            if(!playerIsAllIn && !potAllocated) {
                if(!aiBotIsButton) {
                    doAiBotAction();
                    if(aiBotAction.equals("fold")) {
                        break loop;
                    }

                    doRuleBotAction();
                    if(ruleBotAction.equals("fold")) {
                        break loop;
                    }
                } else {
                    doRuleBotAction();
                    if(ruleBotAction.equals("fold")) {
                        break loop;
                    }

                    doAiBotAction();
                    if(aiBotAction.equals("fold")) {
                        break loop;
                    }
                }
            }
        }

        Map<String, Double> scoreMap = new HashMap<>();
        scoreMap.put("aiBot", aiBotStack - 50);
        scoreMap.put("ruleBot", ruleBotStack - 50);

        return scoreMap;
    }

    private void updatePayoffMap(double totalPayoff, double handStrength) {
        List<String> actionsOfHand = new ArrayList<>();
        actionsOfHand.addAll(aiBotActionHistory);

        double payoffPerAction = totalPayoff / actionsOfHand.size();

        for(String action : actionsOfHand) {
            if(handStrength >= 0 && handStrength < 0.05) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("0-5").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("0-5").get("fold").get(1);

                    payoffMap.get("0-5").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("0-5").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("0-5").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("0-5").get("check").get(1);

                    payoffMap.get("0-5").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("0-5").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("0-5").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("0-5").get("call").get(1);

                    payoffMap.get("0-5").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("0-5").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("0-5").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("0-5").get("bet25%").get(1);

                    payoffMap.get("0-5").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("0-5").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("0-5").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("0-5").get("bet50%").get(1);

                    payoffMap.get("0-5").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("0-5").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("0-5").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("0-5").get("bet75%").get(1);

                    payoffMap.get("0-5").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("0-5").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("0-5").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("0-5").get("bet100%").get(1);

                    payoffMap.get("0-5").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("0-5").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("0-5").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("0-5").get("bet150%").get(1);

                    payoffMap.get("0-5").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("0-5").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("0-5").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("0-5").get("bet200%").get(1);

                    payoffMap.get("0-5").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("0-5").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("0-5").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("0-5").get("raise").get(1);

                    payoffMap.get("0-5").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("0-5").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.05 && handStrength < 0.10) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("fold").get(1);

                    payoffMap.get("0.05-0.10").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("check").get(1);

                    payoffMap.get("5-10").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("call").get(1);

                    payoffMap.get("5-10").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet25%").get(1);

                    payoffMap.get("5-10").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet50%").get(1);

                    payoffMap.get("5-10").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet75%").get(1);

                    payoffMap.get("5-10").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet100%").get(1);

                    payoffMap.get("5-10").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet150%").get(1);

                    payoffMap.get("5-10").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet200%").get(1);

                    payoffMap.get("5-10").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("raise").get(1);

                    payoffMap.get("5-10").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.10 && handStrength < 0.15) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("10-15").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("10-15").get("fold").get(1);

                    payoffMap.get("10-15").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("10-15").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("10-15").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("10-15").get("check").get(1);

                    payoffMap.get("10-15").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("10-15").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("10-15").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("10-15").get("call").get(1);

                    payoffMap.get("10-15").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("10-15").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("10-15").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("10-15").get("bet25%").get(1);

                    payoffMap.get("10-15").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("10-15").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("10-15").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("10-15").get("bet50%").get(1);

                    payoffMap.get("10-15").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("10-15").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("10-15").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("10-15").get("bet75%").get(1);

                    payoffMap.get("10-15").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("10-15").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("10-15").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("10-15").get("bet100%").get(1);

                    payoffMap.get("10-15").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("10-15").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("10-15").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("10-15").get("bet150%").get(1);

                    payoffMap.get("10-15").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("10-15").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("10-15").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("10-15").get("bet200%").get(1);

                    payoffMap.get("10-15").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("10-15").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("10-15").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("10-15").get("raise").get(1);

                    payoffMap.get("10-15").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("10-15").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.15 && handStrength < 0.20) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("15-20").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("15-20").get("fold").get(1);

                    payoffMap.get("15-20").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("15-20").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("15-20").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("15-20").get("check").get(1);

                    payoffMap.get("15-20").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("15-20").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("15-20").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("15-20").get("call").get(1);

                    payoffMap.get("15-20").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("15-20").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("15-20").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("15-20").get("bet25%").get(1);

                    payoffMap.get("15-20").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("15-20").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("15-20").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("15-20").get("bet50%").get(1);

                    payoffMap.get("15-20").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("15-20").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("15-20").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("15-20").get("bet75%").get(1);

                    payoffMap.get("15-20").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("15-20").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("15-20").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("15-20").get("bet100%").get(1);

                    payoffMap.get("15-20").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("15-20").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("15-20").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("15-20").get("bet150%").get(1);

                    payoffMap.get("15-20").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("15-20").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("15-20").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("15-20").get("bet200%").get(1);

                    payoffMap.get("15-20").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("15-20").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("15-20").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("15-20").get("raise").get(1);

                    payoffMap.get("15-20").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("15-20").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.20 && handStrength < 0.25) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("20-25").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("20-25").get("fold").get(1);

                    payoffMap.get("20-25").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("20-25").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("20-25").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("20-25").get("check").get(1);

                    payoffMap.get("20-25").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("20-25").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("20-25").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("20-25").get("call").get(1);

                    payoffMap.get("20-25").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("20-25").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet25%").get(1);

                    payoffMap.get("5-10").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("20-25").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("20-25").get("bet50%").get(1);

                    payoffMap.get("20-25").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("20-25").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("20-25").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("20-25").get("bet75%").get(1);

                    payoffMap.get("20-25").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("20-25").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("20-25").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("20-25").get("bet100%").get(1);

                    payoffMap.get("20-25").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("20-25").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("20-25").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("20-25").get("bet150%").get(1);

                    payoffMap.get("20-25").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("20-25").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("20-25").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("20-25").get("bet200%").get(1);

                    payoffMap.get("20-25").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("20-25").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("20-25").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("20-25").get("raise").get(1);

                    payoffMap.get("20-25").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("20-25").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.25 && handStrength < 0.30) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("25-30").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("25-30").get("fold").get(1);

                    payoffMap.get("25-30").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("25-30").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("25-30").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("25-30").get("check").get(1);

                    payoffMap.get("25-30").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("25-30").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("25-30").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("25-30").get("call").get(1);

                    payoffMap.get("25-30").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("25-30").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("25-30").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("25-30").get("bet25%").get(1);

                    payoffMap.get("25-30").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("25-30").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("25-30").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("25-30").get("bet50%").get(1);

                    payoffMap.get("25-30").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("25-30").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("25-30").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("25-30").get("bet75%").get(1);

                    payoffMap.get("25-30").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("25-30").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("25-30").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("25-30").get("bet100%").get(1);

                    payoffMap.get("25-30").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("25-30").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("25-30").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("25-30").get("bet150%").get(1);

                    payoffMap.get("25-30").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("25-30").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("25-30").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("25-30").get("bet200%").get(1);

                    payoffMap.get("25-30").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("25-30").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("25-30").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("25-30").get("raise").get(1);

                    payoffMap.get("25-30").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("25-30").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.30 && handStrength < 0.35) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("30-35").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("30-35").get("fold").get(1);

                    payoffMap.get("30-35").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("30-35").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("30-35").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("30-35").get("check").get(1);

                    payoffMap.get("30-35").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("30-35").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("30-35").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("30-35").get("call").get(1);

                    payoffMap.get("30-35").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("30-35").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("30-35").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("30-35").get("bet25%").get(1);

                    payoffMap.get("30-35").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("30-35").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("30-35").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("30-35").get("bet50%").get(1);

                    payoffMap.get("30-35").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("30-35").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("30-35").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("30-35").get("bet75%").get(1);

                    payoffMap.get("30-35").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("30-35").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("30-35").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("30-35").get("bet100%").get(1);

                    payoffMap.get("30-35").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("30-35").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("30-35").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("30-35").get("bet150%").get(1);

                    payoffMap.get("30-35").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("30-35").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("30-35").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("30-35").get("bet200%").get(1);

                    payoffMap.get("30-35").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("30-35").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("30-35").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("30-35").get("raise").get(1);

                    payoffMap.get("30-35").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("30-35").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.35 && handStrength < 0.40) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("35-40").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("35-40").get("fold").get(1);

                    payoffMap.get("35-40").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("35-40").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("35-40").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("35-40").get("check").get(1);

                    payoffMap.get("35-40").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("35-40").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("35-40").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("35-40").get("call").get(1);

                    payoffMap.get("35-40").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("35-40").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("35-40").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("35-40").get("bet25%").get(1);

                    payoffMap.get("35-40").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("35-40").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("35-40").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("35-40").get("bet50%").get(1);

                    payoffMap.get("35-40").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("35-40").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("35-40").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("35-40").get("bet75%").get(1);

                    payoffMap.get("35-40").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("35-40").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("35-40").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("35-40").get("bet100%").get(1);

                    payoffMap.get("35-40").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("35-40").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("35-40").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("35-40").get("bet150%").get(1);

                    payoffMap.get("35-40").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("35-40").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("35-40").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("35-40").get("bet200%").get(1);

                    payoffMap.get("35-40").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("35-40").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("35-40").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("35-40").get("raise").get(1);

                    payoffMap.get("35-40").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("35-40").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.40 && handStrength < 0.45) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("40-45").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("40-45").get("fold").get(1);

                    payoffMap.get("40-45").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("40-45").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("40-45").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("40-45").get("check").get(1);

                    payoffMap.get("40-45").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("40-45").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("40-45").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("40-45").get("call").get(1);

                    payoffMap.get("40-45").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("40-45").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("40-45").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("40-45").get("bet25%").get(1);

                    payoffMap.get("40-45").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("40-45").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("40-45").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("40-45").get("bet50%").get(1);

                    payoffMap.get("40-45").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("40-45").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("40-45").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("40-45").get("bet75%").get(1);

                    payoffMap.get("40-45").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("40-45").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("40-45").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("40-45").get("bet100%").get(1);

                    payoffMap.get("40-45").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("40-45").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("40-45").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("40-45").get("bet150%").get(1);

                    payoffMap.get("40-45").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("40-45").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("40-45").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("40-45").get("bet200%").get(1);

                    payoffMap.get("40-45").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("40-45").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("40-45").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("40-45").get("raise").get(1);

                    payoffMap.get("40-45").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("40-45").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.45 && handStrength < 0.50) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("45-50").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("45-50").get("fold").get(1);

                    payoffMap.get("45-50").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("45-50").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("45-50").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("45-50").get("check").get(1);

                    payoffMap.get("45-50").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("45-50").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("45-50").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("45-50").get("call").get(1);

                    payoffMap.get("45-50").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("45-50").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("45-50").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("45-50").get("bet25%").get(1);

                    payoffMap.get("45-50").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("45-50").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("45-50").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("45-50").get("bet50%").get(1);

                    payoffMap.get("45-50").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("45-50").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("45-50").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("45-50").get("bet75%").get(1);

                    payoffMap.get("45-50").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("45-50").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("45-50").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("45-50").get("bet100%").get(1);

                    payoffMap.get("45-50").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("45-50").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("45-50").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("45-50").get("bet150%").get(1);

                    payoffMap.get("45-50").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("45-50").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("45-50").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("45-50").get("bet200%").get(1);

                    payoffMap.get("45-50").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("45-50").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("45-50").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("45-50").get("raise").get(1);

                    payoffMap.get("45-50").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("45-50").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.50 && handStrength < 0.55) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("50-55").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("50-55").get("fold").get(1);

                    payoffMap.get("50-55").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("50-55").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("50-55").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("50-55").get("check").get(1);

                    payoffMap.get("50-55").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("50-55").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("50-55").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("50-55").get("call").get(1);

                    payoffMap.get("50-55").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("50-55").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("50-55").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("50-55").get("bet25%").get(1);

                    payoffMap.get("50-55").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("50-55").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("50-55").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("50-55").get("bet50%").get(1);

                    payoffMap.get("50-55").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("50-55").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("50-55").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("50-55").get("bet75%").get(1);

                    payoffMap.get("50-55").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("50-55").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("50-55").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("50-55").get("bet100%").get(1);

                    payoffMap.get("50-55").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("50-55").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("50-55").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("50-55").get("bet150%").get(1);

                    payoffMap.get("50-55").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("50-55").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("50-55").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("50-55").get("bet200%").get(1);

                    payoffMap.get("50-55").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("50-55").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("50-55").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("50-55").get("raise").get(1);

                    payoffMap.get("50-55").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("50-55").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.55 && handStrength < 0.60) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("55-60").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("55-60").get("fold").get(1);

                    payoffMap.get("55-60").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("55-60").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("55-60").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("55-60").get("check").get(1);

                    payoffMap.get("55-60").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("55-60").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("55-60").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("55-60").get("call").get(1);

                    payoffMap.get("55-60").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("55-60").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("55-60").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("55-60").get("bet25%").get(1);

                    payoffMap.get("55-60").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("55-60").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("55-60").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("55-60").get("bet50%").get(1);

                    payoffMap.get("55-60").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("55-60").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("55-60").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("55-60").get("bet75%").get(1);

                    payoffMap.get("55-60").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("55-60").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("55-60").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("55-60").get("bet100%").get(1);

                    payoffMap.get("55-60").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("55-60").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("55-60").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("55-60").get("bet150%").get(1);

                    payoffMap.get("55-60").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("55-60").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("55-60").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("55-60").get("bet200%").get(1);

                    payoffMap.get("55-60").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("55-60").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("55-60").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("55-60").get("raise").get(1);

                    payoffMap.get("55-60").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("55-60").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.60 && handStrength < 0.65) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("60-65").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("60-65").get("fold").get(1);

                    payoffMap.get("60-65").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("60-65").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("60-65").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("60-65").get("check").get(1);

                    payoffMap.get("60-65").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("60-65").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("60-65").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("60-65").get("call").get(1);

                    payoffMap.get("60-65").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("60-65").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("60-65").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("60-65").get("bet25%").get(1);

                    payoffMap.get("60-65").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("60-65").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("60-65").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("60-65").get("bet50%").get(1);

                    payoffMap.get("60-65").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("60-65").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("60-65").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("60-65").get("bet75%").get(1);

                    payoffMap.get("60-65").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("60-65").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("60-65").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("60-65").get("bet100%").get(1);

                    payoffMap.get("60-65").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("60-65").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("60-65").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("60-65").get("bet150%").get(1);

                    payoffMap.get("60-65").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("60-65").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("60-65").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("60-65").get("bet200%").get(1);

                    payoffMap.get("60-65").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("60-65").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("60-65").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("60-65").get("raise").get(1);

                    payoffMap.get("60-65").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("60-65").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.65 && handStrength < 0.70) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("65-70").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("65-70").get("fold").get(1);

                    payoffMap.get("65-70").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("65-70").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("65-70").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("65-70").get("check").get(1);

                    payoffMap.get("65-70").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("65-70").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("65-70").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("65-70").get("call").get(1);

                    payoffMap.get("65-70").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("65-70").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("65-70").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("65-70").get("bet25%").get(1);

                    payoffMap.get("65-70").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("65-70").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("65-70").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("65-70").get("bet50%").get(1);

                    payoffMap.get("65-70").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("65-70").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("65-70").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("65-70").get("bet75%").get(1);

                    payoffMap.get("65-70").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("65-70").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("65-70").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("65-70").get("bet100%").get(1);

                    payoffMap.get("65-70").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("65-70").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("65-70").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("65-70").get("bet150%").get(1);

                    payoffMap.get("65-70").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("65-70").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("65-70").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("65-70").get("bet200%").get(1);

                    payoffMap.get("65-70").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("65-70").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("65-70").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("65-70").get("raise").get(1);

                    payoffMap.get("65-70").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("65-70").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.70 && handStrength < 0.75) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("70-75").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("70-75").get("fold").get(1);

                    payoffMap.get("70-75").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("70-75").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("70-75").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("70-75").get("check").get(1);

                    payoffMap.get("70-75").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("70-75").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("70-75").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("70-75").get("call").get(1);

                    payoffMap.get("70-75").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("70-75").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("70-75").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("70-75").get("bet25%").get(1);

                    payoffMap.get("70-75").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("70-75").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("70-75").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("70-75").get("bet50%").get(1);

                    payoffMap.get("70-75").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("70-75").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("70-75").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("70-75").get("bet75%").get(1);

                    payoffMap.get("70-75").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("70-75").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("70-75").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("70-75").get("bet100%").get(1);

                    payoffMap.get("70-75").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("70-75").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("70-75").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("70-75").get("bet150%").get(1);

                    payoffMap.get("70-75").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("70-75").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("70-75").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("70-75").get("bet200%").get(1);

                    payoffMap.get("70-75").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("70-75").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("70-75").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("70-75").get("raise").get(1);

                    payoffMap.get("70-75").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("70-75").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.75 && handStrength < 0.80) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("75-80").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("75-80").get("fold").get(1);

                    payoffMap.get("75-80").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("75-80").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("75-80").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("75-80").get("check").get(1);

                    payoffMap.get("75-80").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("75-80").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("75-80").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("75-80").get("call").get(1);

                    payoffMap.get("75-80").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("75-80").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("75-80").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("75-80").get("bet25%").get(1);

                    payoffMap.get("5-10").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet50%").get(1);

                    payoffMap.get("5-10").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet75%").get(1);

                    payoffMap.get("5-10").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet100%").get(1);

                    payoffMap.get("5-10").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet150%").get(1);

                    payoffMap.get("5-10").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet200%").get(1);

                    payoffMap.get("5-10").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("raise").get(1);

                    payoffMap.get("5-10").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.80 && handStrength < 0.85) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("fold").get(1);

                    payoffMap.get("5-10").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("check").get(1);

                    payoffMap.get("5-10").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("call").get(1);

                    payoffMap.get("5-10").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet25%").get(1);

                    payoffMap.get("5-10").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet50%").get(1);

                    payoffMap.get("5-10").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet75%").get(1);

                    payoffMap.get("5-10").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet100%").get(1);

                    payoffMap.get("5-10").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet150%").get(1);

                    payoffMap.get("5-10").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet200%").get(1);

                    payoffMap.get("5-10").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("raise").get(1);

                    payoffMap.get("5-10").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.85 && handStrength < 0.90) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("fold").get(1);

                    payoffMap.get("5-10").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("check").get(1);

                    payoffMap.get("5-10").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("call").get(1);

                    payoffMap.get("5-10").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet25%").get(1);

                    payoffMap.get("5-10").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet50%").get(1);

                    payoffMap.get("5-10").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet75%").get(1);

                    payoffMap.get("5-10").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet100%").get(1);

                    payoffMap.get("5-10").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet150%").get(1);

                    payoffMap.get("5-10").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet200%").get(1);

                    payoffMap.get("5-10").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("raise").get(1);

                    payoffMap.get("5-10").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength >= 0.90 && handStrength < 0.95) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("fold").get(1);

                    payoffMap.get("5-10").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("check").get(1);

                    payoffMap.get("5-10").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("call").get(1);

                    payoffMap.get("5-10").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet25%").get(1);

                    payoffMap.get("5-10").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet50%").get(1);

                    payoffMap.get("5-10").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet75%").get(1);

                    payoffMap.get("5-10").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet100%").get(1);

                    payoffMap.get("5-10").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet150%").get(1);

                    payoffMap.get("5-10").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet200%").get(1);

                    payoffMap.get("5-10").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("raise").get(1);

                    payoffMap.get("5-10").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("fold").get(1);

                    payoffMap.get("5-10").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("check").get(1);

                    payoffMap.get("5-10").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("call").get(1);

                    payoffMap.get("5-10").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet25%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet25%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet25%").get(1);

                    payoffMap.get("5-10").get("bet25%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet25%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet50%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet50%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet50%").get(1);

                    payoffMap.get("5-10").get("bet50%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet50%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet75%").get(1);

                    payoffMap.get("5-10").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet100%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet100%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet100%").get(1);

                    payoffMap.get("5-10").get("bet100%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet100%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet150%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet150%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet150%").get(1);

                    payoffMap.get("5-10").get("bet150%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet150%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet200%")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("bet200%").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("bet200%").get(1);

                    payoffMap.get("5-10").get("bet200%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("bet200%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("5-10").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("5-10").get("raise").get(1);

                    payoffMap.get("5-10").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("5-10").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            }
        }



    }

    private void doAiBotAction() {
        setDummyAction("aiBot");

        if(aiBotAction.equals("fold")) {
            continueHand = false;
            allocatePotAndBetsToWinner("ruleBot");
        } else if(aiBotAction.equals("call")) {
            double callAmount = ruleBotBetSize - aiBotBetSize;

            if(callAmount < aiBotStack && ruleBotStack > 0) {
                if(board.size() < 5) {
                    pot = pot + (2 * ruleBotBetSize);
                    aiBotStack = aiBotStack - (ruleBotBetSize - aiBotBetSize);
                    aiBotBetSize = 0;
                    ruleBotBetSize = 0;
                    nextStreetNeedsToBeDealt = true;
                } else {
                    pot = pot + (2 * ruleBotBetSize);
                    aiBotStack = aiBotStack - (ruleBotBetSize - aiBotBetSize);
                    continueHand = false;
                    aiBotBetSize = 0;
                    ruleBotBetSize = 0;
                    nextStreetNeedsToBeDealt = true;
                    String winner = determineWinnerAtShowdown();
                    allocatePotAndBetsToWinner(winner);
                    potAllocated = true;
                }
            } else {
                if((callAmount + 0.01) >= aiBotStack) {
                    if(board.size() < 5) {
                        pot = pot + (2 * aiBotStack) + (2 * aiBotBetSize);
                        aiBotStack = 0;
                        nextStreetNeedsToBeDealt = true;
                        playerIsAllIn = true;
                    } else {
                        pot = pot + (2 * aiBotStack) + (2 * aiBotBetSize);
                        aiBotStack = 0;
                        continueHand = false;
                        aiBotBetSize = 0;
                        ruleBotBetSize = 0;
                        playerIsAllIn = true;
                        nextStreetNeedsToBeDealt = true;
                        String winner = determineWinnerAtShowdown();
                        allocatePotAndBetsToWinner(winner);
                        potAllocated = true;
                    }
                } else if(ruleBotStack == 0) {
                    if(board.size() < 5) {
                        pot = pot + (2 * ruleBotStack) + (2 * aiBotBetSize);
                        aiBotStack = aiBotStack - (ruleBotBetSize - aiBotBetSize);
                        nextStreetNeedsToBeDealt = true;
                        playerIsAllIn = true;
                    } else {
                        pot = pot + (2 * ruleBotStack) + (2 * aiBotBetSize);
                        aiBotStack = aiBotStack - (ruleBotBetSize - aiBotBetSize);
                        continueHand = false;
                        aiBotBetSize = 0;
                        ruleBotBetSize = 0;
                        playerIsAllIn = true;
                        nextStreetNeedsToBeDealt = true;
                        String winner = determineWinnerAtShowdown();
                        allocatePotAndBetsToWinner(winner);
                        potAllocated = true;
                    }
                }
            }
        } else if(aiBotAction.equals("check")) {
            if(board.size() < 5) {
                if(aiBotIsButton) {
                    nextStreetNeedsToBeDealt = true;
                } else {
                    //check, do nothing
                }
            } else {
                if(aiBotIsButton) {
                    continueHand = false;
                    String winner = determineWinnerAtShowdown();
                    allocatePotAndBetsToWinner(winner);
                } else {
                    //check, do nothing
                }
            }
        } else if(aiBotAction.equals("bet25%")) {
            double sizeToBet = 0.25 * pot;

            if(sizeToBet >= aiBotStack) {
                aiBotBetSize = aiBotStack;
                aiBotStack = 0;
            } else if(sizeToBet >= ruleBotStack) {
                aiBotBetSize = ruleBotStack;
                aiBotStack = aiBotStack - ruleBotStack;
            } else {
                aiBotBetSize = sizeToBet;
                aiBotStack = aiBotStack - aiBotBetSize;
            }
        } else if(aiBotAction.equals("bet50%")) {
            double sizeToBet = 0.50 * pot;

            if(sizeToBet >= aiBotStack) {
                aiBotBetSize = aiBotStack;
                aiBotStack = 0;
            } else if(sizeToBet >= ruleBotStack) {
                aiBotBetSize = ruleBotStack;
                aiBotStack = aiBotStack - ruleBotStack;
            } else {
                aiBotBetSize = sizeToBet;
                aiBotStack = aiBotStack - aiBotBetSize;
            }
        } else if(aiBotAction.equals("bet75%")) {
            double sizeToBet = 0.75 * pot;

            if(sizeToBet >= aiBotStack) {
                aiBotBetSize = aiBotStack;
                aiBotStack = 0;
            } else if(sizeToBet >= ruleBotStack) {
                aiBotBetSize = ruleBotStack;
                aiBotStack = aiBotStack - ruleBotStack;
            } else {
                aiBotBetSize = sizeToBet;
                aiBotStack = aiBotStack - aiBotBetSize;
            }
        } else if(aiBotAction.equals("bet100%")) {
            double sizeToBet = 1 * pot;

            if(sizeToBet >= aiBotStack) {
                aiBotBetSize = aiBotStack;
                aiBotStack = 0;
            } else if(sizeToBet >= ruleBotStack) {
                aiBotBetSize = ruleBotStack;
                aiBotStack = aiBotStack - ruleBotStack;
            } else {
                aiBotBetSize = sizeToBet;
                aiBotStack = aiBotStack - aiBotBetSize;
            }
        } else if(aiBotAction.equals("bet150%")) {
            double sizeToBet = 1.5 * pot;

            if(sizeToBet >= aiBotStack) {
                aiBotBetSize = aiBotStack;
                aiBotStack = 0;
            } else if(sizeToBet >= ruleBotStack) {
                aiBotBetSize = ruleBotStack;
                aiBotStack = aiBotStack - ruleBotStack;
            } else {
                aiBotBetSize = sizeToBet;
                aiBotStack = aiBotStack - aiBotBetSize;
            }
        } else if(aiBotAction.equals("bet200%")) {
            double sizeToBet = 2 * pot;

            if(sizeToBet >= aiBotStack) {
                aiBotBetSize = aiBotStack;
                aiBotStack = 0;
            } else if(sizeToBet >= ruleBotStack) {
                aiBotBetSize = ruleBotStack;
                aiBotStack = aiBotStack - ruleBotStack;
            } else {
                aiBotBetSize = sizeToBet;
                aiBotStack = aiBotStack - aiBotBetSize;
            }
        } else if(aiBotAction.equals("raise")) {
            double sizeToBet = calculateRaiseAmount(aiBotBetSize, ruleBotBetSize, pot);

            if((sizeToBet - aiBotBetSize) >= aiBotStack) {
                aiBotBetSize = aiBotStack + aiBotBetSize;
                aiBotStack = 0;
            } else if(sizeToBet >= (ruleBotStack + ruleBotBetSize)) {
                double aiBotNewBetSize = ruleBotStack + ruleBotBetSize;
                aiBotStack = aiBotStack - (aiBotNewBetSize - aiBotBetSize);
                aiBotBetSize = aiBotNewBetSize;
            } else {
                double aiBotNewBetSize = sizeToBet;
                aiBotStack = aiBotStack - (aiBotNewBetSize - aiBotBetSize);
                aiBotBetSize = aiBotNewBetSize;
            }
        }
    }

    private void doRuleBotAction() {
        setDummyAction("ruleBot");

        if(ruleBotAction.equals("fold")) {
            continueHand = false;
            allocatePotAndBetsToWinner("aiBot");
        } else if(ruleBotAction.equals("call")) {
            double callAmount = aiBotBetSize - ruleBotBetSize;

            if(callAmount < ruleBotStack && aiBotStack > 0) {
                if(board.size() < 5) {
                    pot = pot + (2 * aiBotBetSize);
                    ruleBotStack = ruleBotStack - (aiBotBetSize - ruleBotBetSize);
                    nextStreetNeedsToBeDealt = true;
                } else {
                    pot = pot + (2 * aiBotBetSize);
                    ruleBotStack = ruleBotStack - (aiBotBetSize - ruleBotBetSize);
                    continueHand = false;
                    ruleBotBetSize = 0;
                    aiBotBetSize = 0;
                    nextStreetNeedsToBeDealt = true;
                    String winner = determineWinnerAtShowdown();
                    allocatePotAndBetsToWinner(winner);
                    potAllocated = true;
                }
            } else {
                if((callAmount + 0.01) >= ruleBotStack) {
                    if(board.size() < 5) {
                        pot = pot + (2 * ruleBotStack) + (2 * ruleBotBetSize);
                        ruleBotStack = 0;
                        nextStreetNeedsToBeDealt = true;
                        playerIsAllIn = true;
                    } else {
                        pot = pot + (2 * ruleBotStack) + (2 * ruleBotBetSize);
                        ruleBotStack = 0;
                        continueHand = false;
                        ruleBotBetSize = 0;
                        aiBotBetSize = 0;
                        playerIsAllIn = true;
                        nextStreetNeedsToBeDealt = true;
                        String winner = determineWinnerAtShowdown();
                        allocatePotAndBetsToWinner(winner);
                        potAllocated = true;
                    }
                } else if(aiBotStack == 0) {
                    if(board.size() < 5) {
                        pot = pot + (2 * aiBotStack) + (2 * aiBotBetSize);
                        ruleBotStack = ruleBotStack - (aiBotBetSize - ruleBotBetSize);
                        nextStreetNeedsToBeDealt = true;
                        playerIsAllIn = true;
                    } else {
                        pot = pot + (2 * aiBotStack) + (2 * aiBotBetSize);
                        ruleBotStack = ruleBotStack - (aiBotBetSize - ruleBotBetSize);
                        continueHand = false;
                        ruleBotBetSize = 0;
                        aiBotBetSize = 0;
                        playerIsAllIn = true;
                        nextStreetNeedsToBeDealt = true;
                        String winner = determineWinnerAtShowdown();
                        allocatePotAndBetsToWinner(winner);
                        potAllocated = true;
                    }
                }
            }
        } else if(ruleBotAction.equals("check")) {
            if(board.size() < 5) {
                if(!aiBotIsButton) {
                    nextStreetNeedsToBeDealt = true;
                } else {
                    //check, do nothing
                }
            } else {
                if(!aiBotIsButton) {
                    continueHand = false;
                    String winner = determineWinnerAtShowdown();
                    allocatePotAndBetsToWinner(winner);
                } else {
                    //check, do nothing
                }
            }
        } else if(ruleBotAction.equals("bet25%")) {
            double sizeToBet = 0.25 * pot;

            if(sizeToBet >= ruleBotStack) {
                ruleBotBetSize = ruleBotStack;
                ruleBotStack = 0;
            } else if(sizeToBet >= aiBotStack) {
                ruleBotBetSize = aiBotStack;
                ruleBotStack = ruleBotStack - aiBotStack;
            } else {
                ruleBotBetSize = sizeToBet;
                ruleBotStack = ruleBotStack - ruleBotBetSize;
            }
        } else if(ruleBotAction.equals("bet50%")) {
            double sizeToBet = 0.50 * pot;

            if(sizeToBet >= ruleBotStack) {
                ruleBotBetSize = ruleBotStack;
                ruleBotStack = 0;
            } else if(sizeToBet >= aiBotStack) {
                ruleBotBetSize = aiBotStack;
                ruleBotStack = ruleBotStack - aiBotStack;
            } else {
                ruleBotBetSize = sizeToBet;
                ruleBotStack = ruleBotStack - ruleBotBetSize;
            }
        } else if(ruleBotAction.equals("bet75%")) {
            double sizeToBet = 0.75 * pot;

            if(sizeToBet >= ruleBotStack) {
                ruleBotBetSize = ruleBotStack;
                ruleBotStack = 0;
            } else if(sizeToBet >= aiBotStack) {
                ruleBotBetSize = aiBotStack;
                ruleBotStack = ruleBotStack - aiBotStack;
            } else {
                ruleBotBetSize = sizeToBet;
                ruleBotStack = ruleBotStack - ruleBotBetSize;
            }
        } else if(ruleBotAction.equals("bet100%")) {
            double sizeToBet = 1 * pot;

            if(sizeToBet >= ruleBotStack) {
                ruleBotBetSize = ruleBotStack;
                ruleBotStack = 0;
            } else if(sizeToBet >= aiBotStack) {
                ruleBotBetSize = aiBotStack;
                ruleBotStack = ruleBotStack - aiBotStack;
            } else {
                ruleBotBetSize = sizeToBet;
                ruleBotStack = ruleBotStack - ruleBotBetSize;
            }
        } else if(ruleBotAction.equals("bet150%")) {
            double sizeToBet = 1.5 * pot;

            if(sizeToBet >= ruleBotStack) {
                ruleBotBetSize = ruleBotStack;
                ruleBotStack = 0;
            } else if(sizeToBet >= aiBotStack) {
                ruleBotBetSize = aiBotStack;
                ruleBotStack = ruleBotStack - aiBotStack;
            } else {
                ruleBotBetSize = sizeToBet;
                ruleBotStack = ruleBotStack - ruleBotBetSize;
            }
        } else if(ruleBotAction.equals("bet200%")) {
            double sizeToBet = 2 * pot;

            if(sizeToBet >= ruleBotStack) {
                ruleBotBetSize = ruleBotStack;
                ruleBotStack = 0;
            } else if(sizeToBet >= aiBotStack) {
                ruleBotBetSize = aiBotStack;
                ruleBotStack = ruleBotStack - aiBotStack;
            } else {
                ruleBotBetSize = sizeToBet;
                ruleBotStack = ruleBotStack - ruleBotBetSize;
            }
        } else if(ruleBotAction.equals("raise")) {
            double sizeToBet = calculateRaiseAmount(ruleBotBetSize, aiBotBetSize, pot);

            if((sizeToBet - ruleBotBetSize) >= ruleBotStack) {
                ruleBotBetSize = ruleBotStack + ruleBotBetSize;
                ruleBotStack = 0;
            } else if(sizeToBet >= (aiBotStack + aiBotBetSize)) {
                double ruleBotNewBetSize = aiBotStack + aiBotBetSize;
                ruleBotStack = ruleBotStack - (ruleBotNewBetSize - ruleBotBetSize);
                ruleBotBetSize = ruleBotNewBetSize;
            } else {
                double ruleBotNewBetSize = sizeToBet;
                ruleBotStack = ruleBotStack - (ruleBotNewBetSize - ruleBotBetSize);
                ruleBotBetSize = ruleBotNewBetSize;
            }
        }
    }

    private void setDummyAction(String bot) {
        if(bot.equals("aiBot")) {
            if(ruleBotAction.contains("bet") || ruleBotAction.contains("raise")) {
                double random = Math.random();

                if(ruleBotStack == 0) {
                    if(random < 0.6) {
                        aiBotAction = "fold";
                        aiBotActionHistory.add("fold");
                    } else {
                        aiBotAction = "call";
                        aiBotActionHistory.add("call");
                    }
                } else {
                    if(random < 0.45) {
                        aiBotAction = "fold";
                        aiBotActionHistory.add("fold");
                    } else if(random < 0.82){
                        aiBotAction = "call";
                        aiBotActionHistory.add("call");
                    } else {
                        aiBotAction = "raise";
                        aiBotActionHistory.add("raise");
                    }
                }
            } else {
                double random = Math.random();

                if(random < 0.5) {
                    aiBotAction = "check";
                    aiBotActionHistory.add("check");
                } else if(random < 0.6) {
                    aiBotAction = "bet25%";
                    aiBotActionHistory.add("bet25%");
                } else if(random < 0.7) {
                    aiBotAction = "bet50%";
                    aiBotActionHistory.add("bet50%");
                } else if(random < 0.8) {
                    aiBotAction = "bet75%";
                    aiBotActionHistory.add("bet75%");
                } else if(random < 0.87) {
                    aiBotAction = "bet100%";
                    aiBotActionHistory.add("bet100%");
                } else if(random < 0.93) {
                    aiBotAction = "bet150%";
                    aiBotActionHistory.add("bet150%");
                } else {
                    aiBotAction = "bet200%";
                    aiBotActionHistory.add("bet200%");
                }
            }
        } else if(bot.equals("ruleBot")) {
            if(aiBotAction.contains("bet") || aiBotAction.contains("raise")) {
                if(aiBotStack == 0) {
                    if(ruleBotHandStrength < 0.5) {
                        ruleBotAction = "fold";
                    } else {
                        ruleBotAction = "call";
                    }
                } else {
                    if(ruleBotHandStrength > 0.5 && ruleBotHandStrength < 0.85) {
                        ruleBotAction = "call";
                    } else if(ruleBotHandStrength >= 0.85) {
                        ruleBotAction = "raise";
                    } else {
                        ruleBotAction = "fold";
                    }
                }
            } else {
                if(ruleBotHandStrength < 0.5) {
                    ruleBotAction = "check";
                } else if(ruleBotHandStrength < 0.6) {
                    ruleBotAction = "bet25%";
                } else if(ruleBotHandStrength < 0.7) {
                    ruleBotAction = "bet50%";
                } else if(ruleBotHandStrength < 0.8) {
                    ruleBotAction = "bet75%";
                } else if(ruleBotHandStrength < 0.85) {
                    ruleBotAction = "bet100%";
                } else if(ruleBotHandStrength < 0.93) {
                    ruleBotAction = "bet150%";
                } else {
                    ruleBotAction = "bet200%";
                }
            }
        }
    }

    private void calculateHandStrengths() {
        BoardEvaluator boardEvaluator = new BoardEvaluator(board);
        HandEvaluator handEvaluator = new HandEvaluator(boardEvaluator);
        aiBotHandStrength = handEvaluator.getHandStrength(aiBotHolecards);
        ruleBotHandStrength = handEvaluator.getHandStrength(ruleBotHolecards);
    }

    private void dealNextStreet() {
        if(playerIsAllIn && board.size() == 5) {
            continueHand = false;

            if(!potAllocated) {
                String winner = determineWinnerAtShowdown();
                allocatePotAndBetsToWinner(winner);
            }

            return;
        } else {
            nextStreetNeedsToBeDealt = false;
            aiBotAction = "empty";
            ruleBotAction = "empty";
            aiBotBetSize = 0;
            ruleBotBetSize = 0;

            if(board.size() < 5) {
                board.add(getAndRemoveRandomCardFromDeck());
                calculateHandStrengths();
            }
        }
    }

    private void allocatePotAndBetsToWinner(String winner) {
        if(winner.equals("aiBot")) {
            aiBotStack = aiBotStack + aiBotBetSize + ruleBotBetSize + pot;
        } else if(winner.equals("ruleBot")) {
            ruleBotStack = ruleBotStack + ruleBotBetSize + aiBotBetSize + pot;
        } else if(winner.equals("draw")) {
            aiBotStack = 50;
            ruleBotStack = 50;
        }
    }

    private Card getAndRemoveRandomCardFromDeck() {
        Random randomGenerator = new Random();
        int random = randomGenerator.nextInt(deck.size());
        Card cardToReturn = deck.get(random);
        deck.remove(random);

        return cardToReturn;
    }

    private String determineWinnerAtShowdown() {
        if(aiBotHandStrength > ruleBotHandStrength) {
            return "aiBot";
        } else if(aiBotHandStrength == ruleBotHandStrength) {
            return "draw";
        } else {
            return "ruleBot";
        }
    }


    private double calculateRaiseAmount(double ownBetSize, double facingBetSize, double potSize) {
        double initial = ownBetSize + facingBetSize + potSize;
        return 1.3 * initial;
    }


//    private double calculateRaiseAmount(double facingBetSize, double potSize, double effectiveStack,
//                                        double raisingPlayerStack, double odds) {
//        double raiseAmount = (potSize / (odds - 1)) + (((odds + 1) * facingBetSize) / (odds - 1));
//        double potSizeAfterRaiseAndCall = potSize + raiseAmount + raiseAmount;
//        double effectiveStackRemainingAfterRaise = effectiveStack - raiseAmount;
//
//        if(effectiveStackRemainingAfterRaise / potSizeAfterRaiseAndCall < 0.51) {
//            raiseAmount = raisingPlayerStack;
//        }
//        return raiseAmount;
//    }
}
