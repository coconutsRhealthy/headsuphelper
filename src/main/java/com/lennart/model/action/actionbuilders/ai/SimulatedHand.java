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

    private Map<Integer, List<String>> aiBotActionHistory = new TreeMap<>(Collections.reverseOrder());

    private static int numberOfHandsPlayed = 0;

    public static void main(String[] args) {
        double aiBotTotalScore = 0;
        double ruleBotTotalScore = 0;

        SimulatedHand.initializePayoffMap();

        for(int i = 0; i < 30000; i++) {
            Random rn = new Random();
            int y = rn.nextInt(2 - 1 + 1) + 1;

            SimulatedHand simulatedHand = new SimulatedHand(y);
            Map<String, Double> scores = simulatedHand.playHand();

            simulatedHand.updatePayoffMap(scores.get("aiBot"));

            aiBotTotalScore = aiBotTotalScore + scores.get("aiBot");
            ruleBotTotalScore = ruleBotTotalScore + scores.get("ruleBot");

            System.out.println(i + "        " + aiBotTotalScore);
        }

        System.out.println("aiBot total score: " + aiBotTotalScore);
        System.out.println("ruleBot total score: " + ruleBotTotalScore);
    }

    private static void initializePayoffMap() {
        payoffMap.put("IP0-5", new HashMap<>());
        payoffMap.put("IP5-10", new HashMap<>());
        payoffMap.put("IP10-15", new HashMap<>());
        payoffMap.put("IP15-20", new HashMap<>());
        payoffMap.put("IP20-25", new HashMap<>());
        payoffMap.put("IP25-30", new HashMap<>());
        payoffMap.put("IP30-35", new HashMap<>());
        payoffMap.put("IP35-40", new HashMap<>());
        payoffMap.put("IP40-45", new HashMap<>());
        payoffMap.put("IP45-50", new HashMap<>());
        payoffMap.put("IP50-55", new HashMap<>());
        payoffMap.put("IP55-60", new HashMap<>());
        payoffMap.put("IP60-65", new HashMap<>());
        payoffMap.put("IP65-70", new HashMap<>());
        payoffMap.put("IP70-75", new HashMap<>());
        payoffMap.put("IP75-80", new HashMap<>());
        payoffMap.put("IP80-85", new HashMap<>());
        payoffMap.put("IP85-90", new HashMap<>());
        payoffMap.put("IP90-95", new HashMap<>());
        payoffMap.put("IP95-100", new HashMap<>());

        payoffMap.put("OOP0-5", new HashMap<>());
        payoffMap.put("OOP5-10", new HashMap<>());
        payoffMap.put("OOP10-15", new HashMap<>());
        payoffMap.put("OOP15-20", new HashMap<>());
        payoffMap.put("OOP20-25", new HashMap<>());
        payoffMap.put("OOP25-30", new HashMap<>());
        payoffMap.put("OOP30-35", new HashMap<>());
        payoffMap.put("OOP35-40", new HashMap<>());
        payoffMap.put("OOP40-45", new HashMap<>());
        payoffMap.put("OOP45-50", new HashMap<>());
        payoffMap.put("OOP50-55", new HashMap<>());
        payoffMap.put("OOP55-60", new HashMap<>());
        payoffMap.put("OOP60-65", new HashMap<>());
        payoffMap.put("OOP65-70", new HashMap<>());
        payoffMap.put("OOP70-75", new HashMap<>());
        payoffMap.put("OOP75-80", new HashMap<>());
        payoffMap.put("OOP80-85", new HashMap<>());
        payoffMap.put("OOP85-90", new HashMap<>());
        payoffMap.put("OOP90-95", new HashMap<>());
        payoffMap.put("OOP95-100", new HashMap<>());

        payoffMap.get("IP0-5").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP0-5").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP0-5").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP0-5").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP0-5").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP5-10").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP5-10").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP5-10").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP5-10").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP5-10").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP10-15").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP10-15").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP10-15").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP10-15").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP10-15").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP15-20").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP15-20").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP15-20").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP15-20").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP15-20").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP20-25").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP20-25").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP20-25").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP20-25").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP20-25").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP25-30").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP25-30").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP25-30").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP25-30").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP25-30").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP30-35").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP30-35").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP30-35").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP30-35").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP30-35").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP35-40").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP35-40").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP35-40").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP35-40").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP35-40").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP40-45").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP40-45").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP40-45").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP40-45").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP40-45").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP45-50").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP45-50").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP45-50").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP45-50").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP45-50").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP50-55").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP50-55").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP50-55").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP50-55").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP50-55").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP55-60").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP55-60").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP55-60").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP55-60").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP55-60").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP60-65").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP60-65").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP60-65").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP60-65").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP60-65").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP65-70").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP65-70").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP65-70").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP65-70").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP65-70").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP70-75").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP70-75").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP70-75").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP70-75").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP70-75").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP75-80").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP75-80").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP75-80").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP75-80").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP75-80").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP80-85").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP80-85").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP80-85").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP80-85").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP80-85").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP85-90").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP85-90").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP85-90").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP85-90").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP85-90").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP90-95").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP90-95").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP90-95").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP90-95").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP90-95").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("IP95-100").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP95-100").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP95-100").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP95-100").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("IP95-100").put("raise", Arrays.asList(0.0, 0.0));


        payoffMap.get("OOP0-5").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP0-5").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP0-5").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP0-5").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP0-5").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP5-10").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP5-10").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP5-10").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP5-10").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP5-10").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP10-15").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP10-15").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP10-15").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP10-15").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP10-15").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP15-20").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP15-20").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP15-20").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP15-20").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP15-20").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP20-25").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP20-25").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP20-25").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP20-25").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP20-25").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP25-30").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP25-30").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP25-30").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP25-30").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP25-30").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP30-35").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP30-35").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP30-35").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP30-35").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP30-35").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP35-40").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP35-40").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP35-40").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP35-40").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP35-40").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP40-45").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP40-45").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP40-45").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP40-45").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP40-45").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP45-50").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP45-50").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP45-50").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP45-50").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP45-50").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP50-55").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP50-55").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP50-55").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP50-55").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP50-55").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP55-60").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP55-60").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP55-60").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP55-60").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP55-60").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP60-65").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP60-65").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP60-65").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP60-65").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP60-65").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP65-70").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP65-70").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP65-70").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP65-70").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP65-70").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP70-75").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP70-75").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP70-75").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP70-75").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP70-75").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP75-80").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP75-80").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP75-80").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP75-80").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP75-80").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP80-85").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP80-85").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP80-85").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP80-85").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP80-85").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP85-90").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP85-90").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP85-90").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP85-90").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP85-90").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP90-95").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP90-95").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP90-95").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP90-95").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP90-95").put("raise", Arrays.asList(0.0, 0.0));

        payoffMap.get("OOP95-100").put("fold", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP95-100").put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP95-100").put("call", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP95-100").put("bet75%", Arrays.asList(0.0, 0.0));
        payoffMap.get("OOP95-100").put("raise", Arrays.asList(0.0, 0.0));
    }

    public SimulatedHand(int numberOfHandsPlayed) {
        SimulatedHand.numberOfHandsPlayed++;

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

    private void updatePayoffMap(double totalPayoff) {
        Map<Integer, List<String>> actionMap = new HashMap<>();
        actionMap.putAll(aiBotActionHistory);

        double payoffPerAction = totalPayoff / actionMap.size();

        for (Map.Entry<Integer, List<String>> entry : actionMap.entrySet()) {
            String handStrength = entry.getValue().get(0);
            String action = entry.getValue().get(1);

            if(handStrength.equals("IP0-5")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP0-5").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP0-5").get("fold").get(1);

                    payoffMap.get("IP0-5").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP0-5").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP0-5").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP0-5").get("check").get(1);

                    payoffMap.get("IP0-5").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP0-5").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP0-5").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP0-5").get("call").get(1);

                    payoffMap.get("IP0-5").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP0-5").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP0-5").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP0-5").get("bet75%").get(1);

                    payoffMap.get("IP0-5").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP0-5").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP0-5").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP0-5").get("raise").get(1);

                    payoffMap.get("IP0-5").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP0-5").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP5-10")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP5-10").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP5-10").get("fold").get(1);

                    payoffMap.get("IP5-10").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP5-10").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP5-10").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP5-10").get("check").get(1);

                    payoffMap.get("IP5-10").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP5-10").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP5-10").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP5-10").get("call").get(1);

                    payoffMap.get("IP5-10").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP5-10").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP5-10").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP5-10").get("bet75%").get(1);

                    payoffMap.get("IP5-10").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP5-10").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP5-10").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP5-10").get("raise").get(1);

                    payoffMap.get("IP5-10").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP5-10").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP10-15")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP10-15").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP10-15").get("fold").get(1);

                    payoffMap.get("IP10-15").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP10-15").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP10-15").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP10-15").get("check").get(1);

                    payoffMap.get("IP10-15").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP10-15").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP10-15").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP10-15").get("call").get(1);

                    payoffMap.get("IP10-15").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP10-15").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP10-15").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP10-15").get("bet75%").get(1);

                    payoffMap.get("IP10-15").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP10-15").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP10-15").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP10-15").get("raise").get(1);

                    payoffMap.get("IP10-15").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP10-15").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP15-20")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP15-20").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP15-20").get("fold").get(1);

                    payoffMap.get("IP15-20").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP15-20").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP15-20").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP15-20").get("check").get(1);

                    payoffMap.get("IP15-20").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP15-20").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP15-20").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP15-20").get("call").get(1);

                    payoffMap.get("IP15-20").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP15-20").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP15-20").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP15-20").get("bet75%").get(1);

                    payoffMap.get("IP15-20").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP15-20").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP15-20").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP15-20").get("raise").get(1);

                    payoffMap.get("IP15-20").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP15-20").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP20-25")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP20-25").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP20-25").get("fold").get(1);

                    payoffMap.get("IP20-25").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP20-25").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP20-25").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP20-25").get("check").get(1);

                    payoffMap.get("IP20-25").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP20-25").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP20-25").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP20-25").get("call").get(1);

                    payoffMap.get("IP20-25").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP20-25").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP20-25").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP20-25").get("bet75%").get(1);

                    payoffMap.get("IP20-25").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP20-25").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP20-25").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP20-25").get("raise").get(1);

                    payoffMap.get("IP20-25").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP20-25").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP25-30")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP25-30").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP25-30").get("fold").get(1);

                    payoffMap.get("IP25-30").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP25-30").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP25-30").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP25-30").get("check").get(1);

                    payoffMap.get("IP25-30").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP25-30").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP25-30").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP25-30").get("call").get(1);

                    payoffMap.get("IP25-30").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP25-30").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP25-30").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP25-30").get("bet75%").get(1);

                    payoffMap.get("IP25-30").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP25-30").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP25-30").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP25-30").get("raise").get(1);

                    payoffMap.get("IP25-30").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP25-30").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP30-35")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP30-35").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP30-35").get("fold").get(1);

                    payoffMap.get("IP30-35").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP30-35").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP30-35").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP30-35").get("check").get(1);

                    payoffMap.get("IP30-35").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP30-35").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP30-35").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP30-35").get("call").get(1);

                    payoffMap.get("IP30-35").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP30-35").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP30-35").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP30-35").get("bet75%").get(1);

                    payoffMap.get("IP30-35").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP30-35").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP30-35").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP30-35").get("raise").get(1);

                    payoffMap.get("IP30-35").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP30-35").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP35-40")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP35-40").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP35-40").get("fold").get(1);

                    payoffMap.get("IP35-40").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP35-40").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP35-40").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP35-40").get("check").get(1);

                    payoffMap.get("IP35-40").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP35-40").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP35-40").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP35-40").get("call").get(1);

                    payoffMap.get("IP35-40").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP35-40").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP35-40").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP35-40").get("bet75%").get(1);

                    payoffMap.get("IP35-40").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP35-40").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP35-40").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP35-40").get("raise").get(1);

                    payoffMap.get("IP35-40").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP35-40").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP40-45")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP40-45").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP40-45").get("fold").get(1);

                    payoffMap.get("IP40-45").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP40-45").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP40-45").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP40-45").get("check").get(1);

                    payoffMap.get("IP40-45").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP40-45").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP40-45").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP40-45").get("call").get(1);

                    payoffMap.get("IP40-45").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP40-45").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP40-45").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP40-45").get("bet75%").get(1);

                    payoffMap.get("IP40-45").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP40-45").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP40-45").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP40-45").get("raise").get(1);

                    payoffMap.get("IP40-45").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP40-45").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP45-50")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP45-50").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP45-50").get("fold").get(1);

                    payoffMap.get("IP45-50").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP45-50").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP45-50").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP45-50").get("check").get(1);

                    payoffMap.get("IP45-50").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP45-50").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP45-50").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP45-50").get("call").get(1);

                    payoffMap.get("IP45-50").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP45-50").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP45-50").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP45-50").get("bet75%").get(1);

                    payoffMap.get("IP45-50").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP45-50").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP45-50").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP45-50").get("raise").get(1);

                    payoffMap.get("IP45-50").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP45-50").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP50-55")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP50-55").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP50-55").get("fold").get(1);

                    payoffMap.get("IP50-55").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP50-55").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP50-55").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP50-55").get("check").get(1);

                    payoffMap.get("IP50-55").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP50-55").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP50-55").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP50-55").get("call").get(1);

                    payoffMap.get("IP50-55").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP50-55").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP50-55").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP50-55").get("bet75%").get(1);

                    payoffMap.get("IP50-55").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP50-55").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP50-55").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP50-55").get("raise").get(1);

                    payoffMap.get("IP50-55").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP50-55").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP55-60")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP55-60").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP55-60").get("fold").get(1);

                    payoffMap.get("IP55-60").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP55-60").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP55-60").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP55-60").get("check").get(1);

                    payoffMap.get("IP55-60").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP55-60").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP55-60").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP55-60").get("call").get(1);

                    payoffMap.get("IP55-60").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP55-60").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP55-60").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP55-60").get("bet75%").get(1);

                    payoffMap.get("IP55-60").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP55-60").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP55-60").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP55-60").get("raise").get(1);

                    payoffMap.get("IP55-60").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP55-60").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP60-65")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP60-65").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP60-65").get("fold").get(1);

                    payoffMap.get("IP60-65").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP60-65").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP60-65").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP60-65").get("check").get(1);

                    payoffMap.get("IP60-65").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP60-65").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP60-65").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP60-65").get("call").get(1);

                    payoffMap.get("IP60-65").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP60-65").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP60-65").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP60-65").get("bet75%").get(1);

                    payoffMap.get("IP60-65").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP60-65").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP60-65").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP60-65").get("raise").get(1);

                    payoffMap.get("IP60-65").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP60-65").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP65-70")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP65-70").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP65-70").get("fold").get(1);

                    payoffMap.get("IP65-70").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP65-70").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP65-70").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP65-70").get("check").get(1);

                    payoffMap.get("IP65-70").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP65-70").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP65-70").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP65-70").get("call").get(1);

                    payoffMap.get("IP65-70").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP65-70").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP65-70").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP65-70").get("bet75%").get(1);

                    payoffMap.get("IP65-70").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP65-70").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP65-70").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP65-70").get("raise").get(1);

                    payoffMap.get("IP65-70").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP65-70").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP70-75")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP70-75").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP70-75").get("fold").get(1);

                    payoffMap.get("IP70-75").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP70-75").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP70-75").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP70-75").get("check").get(1);

                    payoffMap.get("IP70-75").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP70-75").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP70-75").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP70-75").get("call").get(1);

                    payoffMap.get("IP70-75").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP70-75").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP70-75").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP70-75").get("bet75%").get(1);

                    payoffMap.get("IP70-75").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP70-75").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP70-75").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP70-75").get("raise").get(1);

                    payoffMap.get("IP70-75").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP70-75").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP75-80")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP75-80").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP75-80").get("fold").get(1);

                    payoffMap.get("IP75-80").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP75-80").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP75-80").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP75-80").get("check").get(1);

                    payoffMap.get("IP75-80").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP75-80").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP75-80").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP75-80").get("call").get(1);

                    payoffMap.get("IP75-80").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP75-80").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP75-80").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP75-80").get("bet75%").get(1);

                    payoffMap.get("IP75-80").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP75-80").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP75-80").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP75-80").get("raise").get(1);

                    payoffMap.get("IP75-80").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP75-80").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP80-85")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP80-85").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP80-85").get("fold").get(1);

                    payoffMap.get("IP80-85").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP80-85").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP80-85").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP80-85").get("check").get(1);

                    payoffMap.get("IP80-85").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP80-85").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP80-85").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP80-85").get("call").get(1);

                    payoffMap.get("IP80-85").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP80-85").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP80-85").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP80-85").get("bet75%").get(1);

                    payoffMap.get("IP80-85").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP80-85").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP80-85").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP80-85").get("raise").get(1);

                    payoffMap.get("IP80-85").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP80-85").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP85-90")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP85-90").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP85-90").get("fold").get(1);

                    payoffMap.get("IP85-90").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP85-90").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP85-90").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP85-90").get("check").get(1);

                    payoffMap.get("IP85-90").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP85-90").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP85-90").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP85-90").get("call").get(1);

                    payoffMap.get("IP85-90").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP85-90").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP85-90").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP85-90").get("bet75%").get(1);

                    payoffMap.get("IP85-90").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP85-90").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP85-90").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP85-90").get("raise").get(1);

                    payoffMap.get("IP85-90").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP85-90").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP90-95")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP90-95").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP90-95").get("fold").get(1);

                    payoffMap.get("IP90-95").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP90-95").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP90-95").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP90-95").get("check").get(1);

                    payoffMap.get("IP90-95").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP90-95").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP90-95").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP90-95").get("call").get(1);

                    payoffMap.get("IP90-95").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP90-95").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP90-95").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP90-95").get("bet75%").get(1);

                    payoffMap.get("IP90-95").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP90-95").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP90-95").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP90-95").get("raise").get(1);

                    payoffMap.get("IP90-95").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP90-95").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("IP95-100")){
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("IP95-100").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("IP95-100").get("fold").get(1);

                    payoffMap.get("IP95-100").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP95-100").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("IP95-100").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("IP95-100").get("check").get(1);

                    payoffMap.get("IP95-100").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP95-100").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("IP95-100").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("IP95-100").get("call").get(1);

                    payoffMap.get("IP95-100").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP95-100").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("IP95-100").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("IP95-100").get("bet75%").get(1);

                    payoffMap.get("IP95-100").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP95-100").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("IP95-100").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("IP95-100").get("raise").get(1);

                    payoffMap.get("IP95-100").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("IP95-100").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            }





            if(handStrength.equals("OOP0-5")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP0-5").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP0-5").get("fold").get(1);

                    payoffMap.get("OOP0-5").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP0-5").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP0-5").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP0-5").get("check").get(1);

                    payoffMap.get("OOP0-5").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP0-5").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP0-5").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP0-5").get("call").get(1);

                    payoffMap.get("OOP0-5").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP0-5").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP0-5").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP0-5").get("bet75%").get(1);

                    payoffMap.get("OOP0-5").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP0-5").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP0-5").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP0-5").get("raise").get(1);

                    payoffMap.get("OOP0-5").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP0-5").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP5-10")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP5-10").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP5-10").get("fold").get(1);

                    payoffMap.get("OOP5-10").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP5-10").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP5-10").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP5-10").get("check").get(1);

                    payoffMap.get("OOP5-10").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP5-10").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP5-10").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP5-10").get("call").get(1);

                    payoffMap.get("OOP5-10").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP5-10").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP5-10").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP5-10").get("bet75%").get(1);

                    payoffMap.get("OOP5-10").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP5-10").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP5-10").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP5-10").get("raise").get(1);

                    payoffMap.get("OOP5-10").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP5-10").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP10-15")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP10-15").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP10-15").get("fold").get(1);

                    payoffMap.get("OOP10-15").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP10-15").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP10-15").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP10-15").get("check").get(1);

                    payoffMap.get("OOP10-15").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP10-15").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP10-15").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP10-15").get("call").get(1);

                    payoffMap.get("OOP10-15").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP10-15").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP10-15").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP10-15").get("bet75%").get(1);

                    payoffMap.get("OOP10-15").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP10-15").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP10-15").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP10-15").get("raise").get(1);

                    payoffMap.get("OOP10-15").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP10-15").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP15-20")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP15-20").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP15-20").get("fold").get(1);

                    payoffMap.get("OOP15-20").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP15-20").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP15-20").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP15-20").get("check").get(1);

                    payoffMap.get("OOP15-20").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP15-20").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP15-20").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP15-20").get("call").get(1);

                    payoffMap.get("OOP15-20").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP15-20").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP15-20").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP15-20").get("bet75%").get(1);

                    payoffMap.get("OOP15-20").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP15-20").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP15-20").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP15-20").get("raise").get(1);

                    payoffMap.get("OOP15-20").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP15-20").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP20-25")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP20-25").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP20-25").get("fold").get(1);

                    payoffMap.get("OOP20-25").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP20-25").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP20-25").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP20-25").get("check").get(1);

                    payoffMap.get("OOP20-25").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP20-25").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP20-25").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP20-25").get("call").get(1);

                    payoffMap.get("OOP20-25").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP20-25").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP20-25").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP20-25").get("bet75%").get(1);

                    payoffMap.get("OOP20-25").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP20-25").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP20-25").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP20-25").get("raise").get(1);

                    payoffMap.get("OOP20-25").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP20-25").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP25-30")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP25-30").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP25-30").get("fold").get(1);

                    payoffMap.get("OOP25-30").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP25-30").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP25-30").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP25-30").get("check").get(1);

                    payoffMap.get("OOP25-30").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP25-30").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP25-30").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP25-30").get("call").get(1);

                    payoffMap.get("OOP25-30").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP25-30").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP25-30").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP25-30").get("bet75%").get(1);

                    payoffMap.get("OOP25-30").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP25-30").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP25-30").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP25-30").get("raise").get(1);

                    payoffMap.get("OOP25-30").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP25-30").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP30-35")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP30-35").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP30-35").get("fold").get(1);

                    payoffMap.get("OOP30-35").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP30-35").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP30-35").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP30-35").get("check").get(1);

                    payoffMap.get("OOP30-35").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP30-35").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP30-35").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP30-35").get("call").get(1);

                    payoffMap.get("OOP30-35").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP30-35").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP30-35").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP30-35").get("bet75%").get(1);

                    payoffMap.get("OOP30-35").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP30-35").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP30-35").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP30-35").get("raise").get(1);

                    payoffMap.get("OOP30-35").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP30-35").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP35-40")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP35-40").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP35-40").get("fold").get(1);

                    payoffMap.get("OOP35-40").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP35-40").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP35-40").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP35-40").get("check").get(1);

                    payoffMap.get("OOP35-40").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP35-40").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP35-40").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP35-40").get("call").get(1);

                    payoffMap.get("OOP35-40").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP35-40").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP35-40").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP35-40").get("bet75%").get(1);

                    payoffMap.get("OOP35-40").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP35-40").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP35-40").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP35-40").get("raise").get(1);

                    payoffMap.get("OOP35-40").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP35-40").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP40-45")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP40-45").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP40-45").get("fold").get(1);

                    payoffMap.get("OOP40-45").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP40-45").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP40-45").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP40-45").get("check").get(1);

                    payoffMap.get("OOP40-45").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP40-45").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP40-45").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP40-45").get("call").get(1);

                    payoffMap.get("OOP40-45").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP40-45").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP40-45").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP40-45").get("bet75%").get(1);

                    payoffMap.get("OOP40-45").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP40-45").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP40-45").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP40-45").get("raise").get(1);

                    payoffMap.get("OOP40-45").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP40-45").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP45-50")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP45-50").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP45-50").get("fold").get(1);

                    payoffMap.get("OOP45-50").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP45-50").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP45-50").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP45-50").get("check").get(1);

                    payoffMap.get("OOP45-50").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP45-50").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP45-50").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP45-50").get("call").get(1);

                    payoffMap.get("OOP45-50").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP45-50").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP45-50").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP45-50").get("bet75%").get(1);

                    payoffMap.get("OOP45-50").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP45-50").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP45-50").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP45-50").get("raise").get(1);

                    payoffMap.get("OOP45-50").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP45-50").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP50-55")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP50-55").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP50-55").get("fold").get(1);

                    payoffMap.get("OOP50-55").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP50-55").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP50-55").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP50-55").get("check").get(1);

                    payoffMap.get("OOP50-55").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP50-55").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP50-55").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP50-55").get("call").get(1);

                    payoffMap.get("OOP50-55").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP50-55").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP50-55").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP50-55").get("bet75%").get(1);

                    payoffMap.get("OOP50-55").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP50-55").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP50-55").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP50-55").get("raise").get(1);

                    payoffMap.get("OOP50-55").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP50-55").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP55-60")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP55-60").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP55-60").get("fold").get(1);

                    payoffMap.get("OOP55-60").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP55-60").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP55-60").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP55-60").get("check").get(1);

                    payoffMap.get("OOP55-60").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP55-60").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP55-60").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP55-60").get("call").get(1);

                    payoffMap.get("OOP55-60").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP55-60").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP55-60").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP55-60").get("bet75%").get(1);

                    payoffMap.get("OOP55-60").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP55-60").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP55-60").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP55-60").get("raise").get(1);

                    payoffMap.get("OOP55-60").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP55-60").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP60-65")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP60-65").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP60-65").get("fold").get(1);

                    payoffMap.get("OOP60-65").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP60-65").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP60-65").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP60-65").get("check").get(1);

                    payoffMap.get("OOP60-65").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP60-65").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP60-65").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP60-65").get("call").get(1);

                    payoffMap.get("OOP60-65").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP60-65").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP60-65").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP60-65").get("bet75%").get(1);

                    payoffMap.get("OOP60-65").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP60-65").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP60-65").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP60-65").get("raise").get(1);

                    payoffMap.get("OOP60-65").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP60-65").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP65-70")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP65-70").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP65-70").get("fold").get(1);

                    payoffMap.get("OOP65-70").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP65-70").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP65-70").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP65-70").get("check").get(1);

                    payoffMap.get("OOP65-70").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP65-70").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP65-70").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP65-70").get("call").get(1);

                    payoffMap.get("OOP65-70").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP65-70").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP65-70").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP65-70").get("bet75%").get(1);

                    payoffMap.get("OOP65-70").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP65-70").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP65-70").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP65-70").get("raise").get(1);

                    payoffMap.get("OOP65-70").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP65-70").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP70-75")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP70-75").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP70-75").get("fold").get(1);

                    payoffMap.get("OOP70-75").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP70-75").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP70-75").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP70-75").get("check").get(1);

                    payoffMap.get("OOP70-75").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP70-75").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP70-75").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP70-75").get("call").get(1);

                    payoffMap.get("OOP70-75").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP70-75").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP70-75").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP70-75").get("bet75%").get(1);

                    payoffMap.get("OOP70-75").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP70-75").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP70-75").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP70-75").get("raise").get(1);

                    payoffMap.get("OOP70-75").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP70-75").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP75-80")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP75-80").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP75-80").get("fold").get(1);

                    payoffMap.get("OOP75-80").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP75-80").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP75-80").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP75-80").get("check").get(1);

                    payoffMap.get("OOP75-80").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP75-80").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP75-80").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP75-80").get("call").get(1);

                    payoffMap.get("OOP75-80").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP75-80").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP75-80").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP75-80").get("bet75%").get(1);

                    payoffMap.get("OOP75-80").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP75-80").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP75-80").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP75-80").get("raise").get(1);

                    payoffMap.get("OOP75-80").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP75-80").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP80-85")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP80-85").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP80-85").get("fold").get(1);

                    payoffMap.get("OOP80-85").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP80-85").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP80-85").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP80-85").get("check").get(1);

                    payoffMap.get("OOP80-85").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP80-85").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP80-85").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP80-85").get("call").get(1);

                    payoffMap.get("OOP80-85").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP80-85").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP80-85").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP80-85").get("bet75%").get(1);

                    payoffMap.get("OOP80-85").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP80-85").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP80-85").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP80-85").get("raise").get(1);

                    payoffMap.get("OOP80-85").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP80-85").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP85-90")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP85-90").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP85-90").get("fold").get(1);

                    payoffMap.get("OOP85-90").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP85-90").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP85-90").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP85-90").get("check").get(1);

                    payoffMap.get("OOP85-90").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP85-90").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP85-90").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP85-90").get("call").get(1);

                    payoffMap.get("OOP85-90").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP85-90").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP85-90").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP85-90").get("bet75%").get(1);

                    payoffMap.get("OOP85-90").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP85-90").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP85-90").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP85-90").get("raise").get(1);

                    payoffMap.get("OOP85-90").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP85-90").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP90-95")) {
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP90-95").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP90-95").get("fold").get(1);

                    payoffMap.get("OOP90-95").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP90-95").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP90-95").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP90-95").get("check").get(1);

                    payoffMap.get("OOP90-95").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP90-95").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP90-95").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP90-95").get("call").get(1);

                    payoffMap.get("OOP90-95").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP90-95").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP90-95").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP90-95").get("bet75%").get(1);

                    payoffMap.get("OOP90-95").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP90-95").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP90-95").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP90-95").get("raise").get(1);

                    payoffMap.get("OOP90-95").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP90-95").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
                }
            } else if(handStrength.equals("OOP95-100")){
                if(action.equals("fold")) {
                    double oldAmountOfTimes = payoffMap.get("OOP95-100").get("fold").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP95-100").get("fold").get(1);

                    payoffMap.get("OOP95-100").get("fold").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP95-100").get("fold").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("check")) {
                    double oldAmountOfTimes = payoffMap.get("OOP95-100").get("check").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP95-100").get("check").get(1);

                    payoffMap.get("OOP95-100").get("check").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP95-100").get("check").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("call")) {
                    double oldAmountOfTimes = payoffMap.get("OOP95-100").get("call").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP95-100").get("call").get(1);

                    payoffMap.get("OOP95-100").get("call").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP95-100").get("call").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("bet75%")) {
                    double oldAmountOfTimes = payoffMap.get("OOP95-100").get("bet75%").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP95-100").get("bet75%").get(1);

                    payoffMap.get("OOP95-100").get("bet75%").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP95-100").get("bet75%").set(1, (oldTotalPayoff + payoffPerAction));
                } else if(action.equals("raise")) {
                    double oldAmountOfTimes = payoffMap.get("OOP95-100").get("raise").get(0);
                    double oldTotalPayoff = payoffMap.get("OOP95-100").get("raise").get(1);

                    payoffMap.get("OOP95-100").get("raise").set(0, (oldAmountOfTimes + 1));
                    payoffMap.get("OOP95-100").get("raise").set(1, (oldTotalPayoff + payoffPerAction));
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

    private int getHighestKeyFromMap() {
        if(aiBotActionHistory.isEmpty()) {
            return 0;
        } else {
            return aiBotActionHistory.entrySet().iterator().next().getKey();
        }
    }

    private String getHandStrengthString(double handStrength) {
        String handStrengthString;

        if(aiBotIsButton) {
            if(handStrength >= 0 && handStrength < 0.05) {
                handStrengthString = "IP0-5";
            } else if(handStrength >= 0.05 && handStrength < 0.10) {
                handStrengthString = "IP5-10";
            } else if(handStrength >= 0.10 && handStrength < 0.15) {
                handStrengthString = "IP10-15";
            } else if(handStrength >= 0.15 && handStrength < 0.20) {
                handStrengthString = "IP15-20";
            } else if(handStrength >= 0.20 && handStrength < 0.25) {
                handStrengthString = "IP20-25";
            } else if(handStrength >= 0.25 && handStrength < 0.30) {
                handStrengthString = "IP25-30";
            } else if(handStrength >= 0.30 && handStrength < 0.35) {
                handStrengthString = "IP30-35";
            } else if(handStrength >= 0.35 && handStrength < 0.40) {
                handStrengthString = "IP35-40";
            } else if(handStrength >= 0.40 && handStrength < 0.45) {
                handStrengthString = "IP40-45";
            } else if(handStrength >= 0.45 && handStrength < 0.50) {
                handStrengthString = "IP45-50";
            } else if(handStrength >= 0.50 && handStrength < 0.55) {
                handStrengthString = "IP50-55";
            } else if(handStrength >= 0.55 && handStrength < 0.60) {
                handStrengthString = "IP55-60";
            } else if(handStrength >= 0.60 && handStrength < 0.65) {
                handStrengthString = "IP60-65";
            } else if(handStrength >= 0.65 && handStrength < 0.70) {
                handStrengthString = "IP65-70";
            } else if(handStrength >= 0.70 && handStrength < 0.75) {
                handStrengthString = "IP70-75";
            } else if(handStrength >= 0.75 && handStrength < 0.80) {
                handStrengthString = "IP75-80";
            } else if(handStrength >= 0.80 && handStrength < 0.85) {
                handStrengthString = "IP80-85";
            } else if(handStrength >= 0.85 && handStrength < 0.90) {
                handStrengthString = "IP85-90";
            } else if(handStrength >= 0.90 && handStrength < 0.95) {
                handStrengthString = "IP90-95";
            } else {
                handStrengthString = "IP95-100";
            }
        } else {
            if(handStrength >= 0 && handStrength < 0.05) {
                handStrengthString = "OOP0-5";
            } else if(handStrength >= 0.05 && handStrength < 0.10) {
                handStrengthString = "OOP5-10";
            } else if(handStrength >= 0.10 && handStrength < 0.15) {
                handStrengthString = "OOP10-15";
            } else if(handStrength >= 0.15 && handStrength < 0.20) {
                handStrengthString = "OOP15-20";
            } else if(handStrength >= 0.20 && handStrength < 0.25) {
                handStrengthString = "OOP20-25";
            } else if(handStrength >= 0.25 && handStrength < 0.30) {
                handStrengthString = "OOP25-30";
            } else if(handStrength >= 0.30 && handStrength < 0.35) {
                handStrengthString = "OOP30-35";
            } else if(handStrength >= 0.35 && handStrength < 0.40) {
                handStrengthString = "OOP35-40";
            } else if(handStrength >= 0.40 && handStrength < 0.45) {
                handStrengthString = "OOP40-45";
            } else if(handStrength >= 0.45 && handStrength < 0.50) {
                handStrengthString = "OOP45-50";
            } else if(handStrength >= 0.50 && handStrength < 0.55) {
                handStrengthString = "OOP50-55";
            } else if(handStrength >= 0.55 && handStrength < 0.60) {
                handStrengthString = "OOP55-60";
            } else if(handStrength >= 0.60 && handStrength < 0.65) {
                handStrengthString = "OOP60-65";
            } else if(handStrength >= 0.65 && handStrength < 0.70) {
                handStrengthString = "OOP65-70";
            } else if(handStrength >= 0.70 && handStrength < 0.75) {
                handStrengthString = "OOP70-75";
            } else if(handStrength >= 0.75 && handStrength < 0.80) {
                handStrengthString = "OOP75-80";
            } else if(handStrength >= 0.80 && handStrength < 0.85) {
                handStrengthString = "OOP80-85";
            } else if(handStrength >= 0.85 && handStrength < 0.90) {
                handStrengthString = "OOP85-90";
            } else if(handStrength >= 0.90 && handStrength < 0.95) {
                handStrengthString = "OOP90-95";
            } else {
                handStrengthString = "OOP95-100";
            }
        }

        return handStrengthString;
    }

    private String getBestActionFromPayoffMap(String handStrength, List<String> eligibleActions) {
        Map<String, Double> payoffMapToUse = new HashMap<>();

        for(String action : eligibleActions) {
            double amountOfTimes = payoffMap.get(handStrength).get(action).get(0);
            double totalPayoff = payoffMap.get(handStrength).get(action).get(1);
            double averagePayoff = totalPayoff / amountOfTimes;

            payoffMapToUse.put(action, averagePayoff);
        }
        payoffMapToUse = sortByValueHighToLow(payoffMapToUse);
        return payoffMapToUse.entrySet().iterator().next().getKey();
    }

    private void setDummyAction(String bot) {
        if(bot.equals("aiBot")) {
            if(SimulatedHand.numberOfHandsPlayed > 15000) {
                String handStrengthString = getHandStrengthString(aiBotHandStrength);

                if(ruleBotAction.contains("bet") || ruleBotAction.contains("raise")) {
                    if(ruleBotStack == 0) {
                        List<String> eligibleActions = Arrays.asList("fold", "call");
                        aiBotAction = getBestActionFromPayoffMap(handStrengthString, eligibleActions);
                    } else {
                        List<String> eligibleActions = Arrays.asList("fold", "call", "raise");
                        aiBotAction = getBestActionFromPayoffMap(handStrengthString, eligibleActions);
                    }
                } else {
                    List<String> eligibleActions = Arrays.asList("check", "bet75%");
                    aiBotAction = getBestActionFromPayoffMap(handStrengthString, eligibleActions);
                }
            } else {
                if(ruleBotAction.contains("bet") || ruleBotAction.contains("raise")) {
                    double random = Math.random();

                    if(ruleBotStack == 0) {
                        if(random < 0.5) {
                            aiBotAction = "fold";
                            aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(getHandStrengthString(aiBotHandStrength), "fold"));
                        } else {
                            aiBotAction = "call";
                            aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(getHandStrengthString(aiBotHandStrength), "call"));
                        }
                    } else {
                        if(random < 0.333) {
                            aiBotAction = "fold";
                            aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(getHandStrengthString(aiBotHandStrength), "fold"));
                        } else if(random < 0.666){
                            aiBotAction = "call";
                            aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(getHandStrengthString(aiBotHandStrength), "call"));
                        } else {
                            aiBotAction = "raise";
                            aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(getHandStrengthString(aiBotHandStrength), "raise"));
                        }
                    }
                } else {
                    double random = Math.random();

                    if(random < 0.5) {
                        aiBotAction = "check";
                        aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(getHandStrengthString(aiBotHandStrength), "check"));
                    } else {
                        aiBotAction = "bet75%";
                        aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(getHandStrengthString(aiBotHandStrength), "bet75%"));
                    }
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
                } else {
                    ruleBotAction = "bet75%";
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
