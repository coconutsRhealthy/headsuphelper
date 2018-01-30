package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.*;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.handevaluation.PreflopHandStength;
import org.apache.commons.math3.util.Precision;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by lpo21630 on 4-1-2018.
 */
public class SimulatedHand {

    private double aiBotStack;
    private double aiBotStackAtStartHand;
    private double ruleBotStack;
    private double ruleBotStackAtStartHand;
    private double pot = 0;
    private boolean aiBotIsButton;
    private List<Card> deck = BoardEvaluator.getCompleteCardDeck();
    private List<Card> aiBotHolecards = new ArrayList<>();
    private List<Card> ruleBotHolecards = new ArrayList<>();
    private List<Card> board = new ArrayList<>();

    private double bigBlind = 0.5;

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

    private boolean aiBotHasStrongDraw = false;
    private boolean ruleBotHasStrongDraw = false;

    private Map<Integer, List<String>> aiBotActionHistory = new TreeMap<>(Collections.reverseOrder());
    private AbstractOpponent ruleBot;

    private static int numberOfHandsPlayed = 0;

    private List<String> aiBotHandHistory;
    private List<String> ruleBotHandHistory;

    public static void main(String[] args) {
        double aiBotTotalScore = 0;
        double ruleBotTotalScore = 0;

        for(int i = 0; i < 1000; i++) {
            Random rn = new Random();
            int y = rn.nextInt(2 - 1 + 1) + 1;

            SimulatedHand simulatedHand = new SimulatedHand(y);
            Map<String, Double> scores = simulatedHand.playHand();

//            System.out.println(simulatedHand.ruleBotStack);
//            System.out.println(simulatedHand.aiBotStack);

            simulatedHand.updatePayoff(scores.get("aiBot"));

//            System.out.println(scores.get("ruleBot"));
//            System.out.println(scores.get("aiBot"));

            aiBotTotalScore = aiBotTotalScore + scores.get("aiBot");
            ruleBotTotalScore = ruleBotTotalScore + scores.get("ruleBot");

            if(scores.get("aiBot") + scores.get("ruleBot") > 1 || scores.get("aiBot") + scores.get("ruleBot") < -1) {
                System.out.println("wacht hier");
            }

            System.out.println(i + "        " + aiBotTotalScore + "        " + ruleBotTotalScore);
        }

        System.out.println("aiBot total score: " + aiBotTotalScore);
        System.out.println("ruleBot total score: " + ruleBotTotalScore);
    }

    public SimulatedHand(int numberOfHandsPlayed) {
        SimulatedHand.numberOfHandsPlayed++;

        aiBotHolecards.add(getAndRemoveRandomCardFromDeck());
        aiBotHolecards.add(getAndRemoveRandomCardFromDeck());
        ruleBotHolecards.add(getAndRemoveRandomCardFromDeck());
        ruleBotHolecards.add(getAndRemoveRandomCardFromDeck());

        ruleBot = initializeRuleBot();

        aiBotHandHistory = new ArrayList<>();
        ruleBotHandHistory = new ArrayList<>();

        if(numberOfHandsPlayed % 2 == 0) {
            aiBotStack = getRandomStackSizeOfAiBot();
            aiBotStackAtStartHand = aiBotStack;
            ruleBotStack = getRandomStackSizeOfRuleBot();
            ruleBotStackAtStartHand = ruleBotStack;
            aiBotAction = "bet";

            aiBotBetSize = 0.50;
            ruleBotBetSize = 0.25;
            aiBotStack = aiBotStack - 0.50;
            ruleBotStack = ruleBotStack - 0.25;

            aiBotIsButton = false;

            aiBotHandHistory.add("Preflop " + "posts Big Blind " + aiBotBetSize + " Stack:" + aiBotStack);
            ruleBotHandHistory.add("Preflop " + "posts Small Blind " + ruleBotBetSize + " Stack:" + ruleBotStack);
        } else {
            aiBotStack = getRandomStackSizeOfAiBot();
            aiBotStackAtStartHand = aiBotStack;
            ruleBotStack = getRandomStackSizeOfRuleBot();
            ruleBotStackAtStartHand = ruleBotStack;
            ruleBotAction = "bet";

            aiBotBetSize = 0.25;
            ruleBotBetSize = 0.50;
            aiBotStack = aiBotStack - 0.25;
            ruleBotStack = ruleBotStack - 0.50;

            aiBotIsButton = true;

            aiBotHandHistory.add("Preflop " + "posts Small Blind " + aiBotBetSize + " Stack:" + aiBotStack);
            ruleBotHandHistory.add("Preflop " + "posts Big Blind " + ruleBotBetSize + " Stack:" + ruleBotStack);
        }

        calculateHandStrengthsAndDraws();
    }

    public Map<String, Double> playHand() {
        loop: while(continueHand) {
            while(!nextStreetNeedsToBeDealt && !playerIsAllIn) {

                if(board.isEmpty()) {
                    if(aiBotIsButton) {
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
                } else {
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
        scoreMap.put("aiBot", aiBotStack - aiBotStackAtStartHand);
        scoreMap.put("ruleBot", ruleBotStack - ruleBotStackAtStartHand);

        double scoreBalance = (aiBotStack - aiBotStackAtStartHand) + (ruleBotStack - ruleBotStackAtStartHand);

        if(scoreBalance > 1 || scoreBalance < -1) {
            System.out.println("rrrrrr" + "   " + ruleBotStack + "   " + aiBotStack);
        }

        return scoreMap;
    }

    private void updatePayoff(double totalPayoff) {
        new Poker().updatePayoff(aiBotActionHistory, totalPayoff, ruleBot);
    }

    private void doAiBotAction() {
        setDummyAction("aiBot");

        if(aiBotAction.equals("fold")) {
            continueHand = false;
            allocatePotAndBetsToWinner("ruleBot");
        } else if(aiBotAction.equals("call")) {
            double callAmount = ruleBotBetSize - aiBotBetSize;

            if(callAmount == bigBlind / 2) {
                //preflop limp
                aiBotStack = aiBotStack - callAmount;
                aiBotBetSize = ruleBotBetSize;
            } else if(callAmount < aiBotStack && ruleBotStack > 0) {
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
                        pot = pot + (2 * ruleBotBetSize);
                        aiBotStack = aiBotStack - (ruleBotBetSize - aiBotBetSize);
                        nextStreetNeedsToBeDealt = true;
                        playerIsAllIn = true;
                    } else {
                        pot = pot + (2 * ruleBotStack) + (2 * ruleBotBetSize);
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
                    if(board.isEmpty() && aiBotBetSize / bigBlind == 1 && ruleBotBetSize/ bigBlind == 1) {
                        //preflop check after limp...
                        pot = aiBotBetSize + ruleBotBetSize;
                        aiBotBetSize = 0;
                        ruleBotBetSize = 0;
                        nextStreetNeedsToBeDealt = true;
                    }
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
        } else if(aiBotAction.equals("bet75pct")) {
            double sizeToBet = new Sizing().getAiBotSizing(ruleBotBetSize, aiBotBetSize, aiBotStack, ruleBotStack, pot, bigBlind, board);

            if(sizeToBet >= aiBotStack) {
                if(sizeToBet >= ruleBotStack) {
                    if(ruleBotStack > aiBotStack) {
                        aiBotBetSize = aiBotStack;
                        aiBotStack = 0;
                    } else {
                        aiBotBetSize = ruleBotStack;
                        aiBotStack = aiBotStack - ruleBotStack;
                    }
                } else {
                    aiBotBetSize = aiBotStack;
                    aiBotStack = 0;
                }
            } else if(sizeToBet >= ruleBotStack) {
                aiBotBetSize = ruleBotStack;
                aiBotStack = aiBotStack - ruleBotStack;
            } else {
                aiBotBetSize = sizeToBet;
                aiBotStack = aiBotStack - aiBotBetSize;
            }
        } else if(aiBotAction.equals("raise")) {
            double sizeToBet = new Sizing().getAiBotSizing(ruleBotBetSize, aiBotBetSize, aiBotStack, ruleBotStack, pot, bigBlind, board);

            if((sizeToBet - aiBotBetSize) >= aiBotStack) {
                if(sizeToBet >= (ruleBotStack + ruleBotBetSize)) {
                    if(aiBotStack > (ruleBotStack + ruleBotBetSize)) {
                        //take the rulebot amount
                        double aiBotNewBetSize = ruleBotStack + ruleBotBetSize;
                        aiBotStack = aiBotStack - (aiBotNewBetSize - aiBotBetSize);
                        aiBotBetSize = aiBotNewBetSize;
                    } else {
                        //take the aibot amount
                        aiBotBetSize = aiBotStack + aiBotBetSize;
                        aiBotStack = 0;
                    }
                } else {
                    aiBotBetSize = aiBotStack + aiBotBetSize;
                    aiBotStack = 0;
                }
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

        aiBotHandHistory.add(getStreetForHistory() + " Action: " + aiBotAction + " aiBotBetsize: " + aiBotBetSize + " aiBotStack: " + aiBotStack + " potsize: " + pot);
    }

    private void doRuleBotAction() {
        setDummyAction("ruleBot");

        if(ruleBotAction.equals("fold")) {
            continueHand = false;
            allocatePotAndBetsToWinner("aiBot");
        } else if(ruleBotAction.equals("call")) {
            double callAmount = aiBotBetSize - ruleBotBetSize;

            if(callAmount == bigBlind / 2) {
                //preflop limp
                ruleBotStack = ruleBotStack - callAmount;
                ruleBotBetSize = aiBotBetSize;
            } else if(callAmount < ruleBotStack && aiBotStack > 0) {
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
                        pot = pot + (2 * aiBotBetSize);
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
                    if(board.isEmpty() && aiBotBetSize / bigBlind == 1 && ruleBotBetSize / bigBlind == 1) {
                        //preflop check after limp...
                        pot = aiBotBetSize + ruleBotBetSize;
                        aiBotBetSize = 0;
                        ruleBotBetSize = 0;
                        nextStreetNeedsToBeDealt = true;
                    }
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
        } else if(ruleBotAction.equals("bet75pct")) {
            double sizeToBet = new Sizing().getRuleBotSizing(ruleBotHandStrength, aiBotBetSize, ruleBotBetSize, aiBotStack, ruleBotStack, pot, board);

            if(sizeToBet >= ruleBotStack) {
                if(sizeToBet >= aiBotStack) {
                    if(aiBotStack > ruleBotStack) {
                        ruleBotBetSize = ruleBotStack;
                        ruleBotStack = 0;
                    } else {
                        ruleBotBetSize = aiBotStack;
                        ruleBotStack = ruleBotStack - aiBotStack;
                    }
                } else {
                    ruleBotBetSize = ruleBotStack;
                    ruleBotStack = 0;
                }
            } else if(sizeToBet >= aiBotStack) {
                ruleBotBetSize = aiBotStack;
                ruleBotStack = ruleBotStack - aiBotStack;
            } else {
                ruleBotBetSize = sizeToBet;
                ruleBotStack = ruleBotStack - ruleBotBetSize;
            }
        } else if(ruleBotAction.equals("raise")) {
            double sizeToBet = new Sizing().getRuleBotSizing(ruleBotHandStrength, aiBotBetSize, ruleBotBetSize, aiBotStack, ruleBotStack, pot, board);

            if((sizeToBet - ruleBotBetSize) >= ruleBotStack) {
                if(sizeToBet >= (aiBotStack + aiBotBetSize)) {
                    if(ruleBotStack > (aiBotStack + aiBotBetSize)) {
                        double ruleBotNewBetSize = aiBotStack + aiBotBetSize;
                        ruleBotStack = ruleBotStack - (ruleBotNewBetSize - ruleBotBetSize);
                        ruleBotBetSize = ruleBotNewBetSize;
                    } else {
                        ruleBotBetSize = ruleBotStack + ruleBotBetSize;
                        ruleBotStack = 0;
                    }
                } else {
                    ruleBotBetSize = ruleBotStack + ruleBotBetSize;
                    ruleBotStack = 0;
                }
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

        ruleBotHandHistory.add(getStreetForHistory() + " Action: " + ruleBotAction + " ruleBotBetsize: " + ruleBotBetSize + " ruleBotStack: " + ruleBotStack + " potsize: " + pot);
    }

    private int getHighestKeyFromMap() {
        if(aiBotActionHistory.isEmpty()) {
            return 0;
        } else {
            return aiBotActionHistory.entrySet().iterator().next().getKey();
        }
    }

    private void setDummyAction(String bot) {
        if(bot.equals("aiBot")) {
            Poker poker = new Poker();

            if(SimulatedHand.numberOfHandsPlayed > 50_000) {
                if(ruleBotAction.contains("bet") || ruleBotAction.contains("raise")) {
                    if(ruleBotStack == 0 || ((aiBotStack + aiBotBetSize) <= ruleBotBetSize)) {
                        List<String> eligibleActions = Arrays.asList("fold", "call");
                        aiBotAction = poker.getAction(eligibleActions, getStreet(), aiBotIsButton, getPotSizeInBb(), ruleBotAction, getFacingOdds(), getEffectiveStackInBb(), aiBotHasStrongDraw, aiBotHandStrength, ruleBot);
                    } else {
                        List<String> eligibleActions = Arrays.asList("fold", "call", "raise");
                        aiBotAction = poker.getAction(eligibleActions, getStreet(), aiBotIsButton, getPotSizeInBb(), ruleBotAction, getFacingOdds(), getEffectiveStackInBb(), aiBotHasStrongDraw, aiBotHandStrength, ruleBot);
                    }
                } else {
                    if(board.isEmpty() && !aiBotIsButton && aiBotBetSize / bigBlind == 1 && ruleBotBetSize / bigBlind == 1) {
                        List<String> eligibleActions = Arrays.asList("check", "raise");
                        aiBotAction = poker.getAction(eligibleActions, getStreet(), aiBotIsButton, getPotSizeInBb(), ruleBotAction, getFacingOdds(), getEffectiveStackInBb(), aiBotHasStrongDraw, aiBotHandStrength, ruleBot);
                    } else {
                        List<String> eligibleActions = Arrays.asList("check", "bet75pct");
                        aiBotAction = poker.getAction(eligibleActions, getStreet(), aiBotIsButton, getPotSizeInBb(), ruleBotAction, getFacingOdds(), getEffectiveStackInBb(), aiBotHasStrongDraw, aiBotHandStrength, ruleBot);
                    }
                }
            } else {
                String route = poker.getRoute(getStreet(), aiBotIsButton, getPotSizeInBb(), ruleBotAction, getFacingOdds(), getEffectiveStackInBb(), aiBotHasStrongDraw);

                if(ruleBotAction.contains("bet") || ruleBotAction.contains("raise")) {
                    double random = Math.random();

                    if(ruleBotStack == 0 || ((aiBotStack + aiBotBetSize) <= ruleBotBetSize)) {
                        if(random < 0.5) {
                            aiBotAction = "fold";
                            aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(String.valueOf(aiBotHandStrength), route, "fold"));
                        } else {
                            aiBotAction = "call";
                            aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(String.valueOf(aiBotHandStrength), route, "call"));
                        }
                    } else {
                        if(random < 0.333) {
                            aiBotAction = "fold";
                            aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(String.valueOf(aiBotHandStrength), route, "fold"));
                        } else if(random < 0.666){
                            aiBotAction = "call";
                            aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(String.valueOf(aiBotHandStrength), route, "call"));
                        } else {
                            aiBotAction = "raise";
                            aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(String.valueOf(aiBotHandStrength), route, "raise"));
                        }
                    }
                } else {
                    double random = Math.random();

                    if(random < 0.5) {
                        aiBotAction = "check";
                        aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(String.valueOf(aiBotHandStrength), route, "check"));
                    } else {
                        //moet soms raise zijn
                        if(board.isEmpty() && !aiBotIsButton && aiBotBetSize / bigBlind == 1 && ruleBotBetSize / bigBlind == 1) {
                            aiBotAction = "raise";
                            aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(String.valueOf(aiBotHandStrength), route, "raise"));
                        } else {
                            aiBotAction = "bet75pct";
                            aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(String.valueOf(aiBotHandStrength), route, "bet75pct"));
                        }
                    }
                }
            }
        } else if(bot.equals("ruleBot")) {
            ruleBotAction = ruleBot.doAction(aiBotAction, ruleBotHandStrength, ruleBotHasStrongDraw, getAiBotBetSizeInBb(),
                    getRuleBotBetSizeInBb(), (aiBotStack / bigBlind), (ruleBotStack / bigBlind), !aiBotIsButton, board.isEmpty(), board);
        }
    }

    private void calculateHandStrengthsAndDraws() {
        if(board.isEmpty()) {
            PreflopHandStength preflopHandStength = new PreflopHandStength();
            aiBotHandStrength = preflopHandStength.getPreflopHandStength(aiBotHolecards);
            ruleBotHandStrength = preflopHandStength.getPreflopHandStength(ruleBotHolecards);

            aiBotHasStrongDraw = false;
            ruleBotHasStrongDraw = false;
        } else {
            BoardEvaluator boardEvaluator = new BoardEvaluator(board);
            HandEvaluator handEvaluatorForAiBot = new HandEvaluator(aiBotHolecards, boardEvaluator);
            HandEvaluator handEvaluatorForRuleBot = new HandEvaluator(ruleBotHolecards, boardEvaluator);

            aiBotHandStrength = handEvaluatorForAiBot.getHandStrength(aiBotHolecards);
            ruleBotHandStrength = handEvaluatorForRuleBot.getHandStrength(ruleBotHolecards);

            aiBotHasStrongDraw = hasStrongDraw(handEvaluatorForAiBot);
            ruleBotHasStrongDraw = hasStrongDraw(handEvaluatorForRuleBot);
        }
    }

    private boolean hasStrongDraw(HandEvaluator handEvaluator) {
        return handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")
                || handEvaluator.hasDrawOfType("strongGutshot");
    }

    private double getEffectiveStackInBb() {
        if(aiBotStack > ruleBotStack) {
            return ruleBotStack / bigBlind;
        }
        return aiBotStack / bigBlind;
    }

    private double getPotSizeInBb() {
        return pot / bigBlind;
    }

    private double getAiBotBetSizeInBb() {
        return aiBotBetSize / bigBlind;
    }

    private double getRuleBotBetSizeInBb() {
        return ruleBotBetSize / bigBlind;
    }

    private double getFacingOdds() {
        double facingOdds = (ruleBotBetSize - aiBotBetSize) / (pot + aiBotBetSize + ruleBotBetSize);
        return facingOdds;
    }

    private String getStreet() {
        String street;

        if(board.isEmpty()) {
            street = "preflop";
        } else if(board.size() == 3 || board.size() == 4) {
            street = "flopOrTurn";
        } else if(board.size() == 5) {
            street = "river";
        } else {
            street = "wrong";
        }

        return street;
    }

    private String getStreetForHistory() {
        String street;

        if(board.isEmpty()) {
            street = "Preflop";
        } else if(board.size() == 3) {
            street = "Flop";
        } else if(board.size() == 4) {
            street = "Turn";
        } else if(board.size() == 5) {
            street = "River";
        } else {
            street = "Wrong";
        }

        return street;
    }

    private double getRandomStackSizeOfRuleBot() {
        double stackSize = ThreadLocalRandom.current().nextDouble(1.0, 75.0);
        stackSize = Precision.round(stackSize, 2);
        return stackSize;
    }

    private double getRandomStackSizeOfAiBot() {
        double stackSize = ThreadLocalRandom.current().nextDouble(50.0, 75.0);
        stackSize = Precision.round(stackSize, 2);
        return stackSize;
    }

    private AbstractOpponent initializeRuleBot() {
        AbstractOpponent ruleBot;

        Random randomGenerator = new Random();
        int random = randomGenerator.nextInt(4);

        if(random == 0) {
            ruleBot = new TightPassive();
        } else if(random == 1) {
            ruleBot = new TightAggressive(pot / bigBlind, ruleBotStack / bigBlind);
        } else if(random == 2) {
            ruleBot = new LoosePassive();
        } else if(random == 3) {
            ruleBot = new LooseAggressive(pot / bigBlind, ruleBotStack / bigBlind);
        } else {
            ruleBot = null;
        }

        return ruleBot;
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
                if(board.isEmpty()) {
                    board.add(getAndRemoveRandomCardFromDeck());
                    board.add(getAndRemoveRandomCardFromDeck());
                    board.add(getAndRemoveRandomCardFromDeck());
                } else {
                    board.add(getAndRemoveRandomCardFromDeck());
                }

                calculateHandStrengthsAndDraws();
            }
        }
    }

    private void allocatePotAndBetsToWinner(String winner) {
        if(winner.equals("aiBot")) {
            aiBotStack = aiBotStack + aiBotBetSize + ruleBotBetSize + pot;
        } else if(winner.equals("ruleBot")) {
            ruleBotStack = ruleBotStack + ruleBotBetSize + aiBotBetSize + pot;
        } else if(winner.equals("draw")) {
            aiBotStack = aiBotStackAtStartHand;
            ruleBotStack = ruleBotStackAtStartHand;
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
}
