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

    private boolean aiBotHasStrongFlushDraw;
    private boolean ruleBotHasStrongFlushDraw;
    private boolean aiBotHasStrongOosd;
    private boolean ruleBotHasStrongOosd;
    private boolean aiBotHasStrongGutshot;
    private boolean ruleBotHasStrongGutshot;

    private Map<Integer, List<String>> aiBotActionHistory = new TreeMap<>(Collections.reverseOrder());
    private AbstractOpponent ruleBot;

    private static int numberOfHandsPlayed = 0;

    private boolean randomContinuation = false;

//    public static void main(String[] args) {
//        double aiBotTotalScore = 0;
//        double ruleBotTotalScore = 0;
//
//        for(int i = 0; i < 100_000; i++) {
//            Random rn = new Random();
//            int y = rn.nextInt(2 - 1 + 1) + 1;
//
//            SimulatedHand simulatedHand = new SimulatedHand(y);
//            Map<String, Double> scores = simulatedHand.playHand();
//
//            //simulatedHand.updatePayoff(scores.get("aiBot"));
//
//            aiBotTotalScore = aiBotTotalScore + scores.get("aiBot");
//            ruleBotTotalScore = ruleBotTotalScore + scores.get("ruleBot");
//
//            System.out.println(getOpponentTypeString2(simulatedHand.ruleBot));
//            System.out.println(getOpponentTypeString(simulatedHand.ruleBot));
//            System.out.println(i + "       " + aiBotTotalScore + "          " + ruleBotTotalScore);
//            //System.out.println();
//        }
//    }

    public SimulatedHand(int numberOfHandsPlayed) {
        SimulatedHand.numberOfHandsPlayed++;

        aiBotHolecards.add(getAndRemoveRandomCardFromDeck());
        aiBotHolecards.add(getAndRemoveRandomCardFromDeck());
        ruleBotHolecards.add(getAndRemoveRandomCardFromDeck());
        ruleBotHolecards.add(getAndRemoveRandomCardFromDeck());

        ruleBot = initializeRuleBot();

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

        return scoreMap;
    }

    public void updatePayoff(double totalPayoff) {
        new Poker().updatePayoff(aiBotActionHistory, totalPayoff, getOpponentTypeString(ruleBot));
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

            if(SimulatedHand.numberOfHandsPlayed > 4_000_000) {
                if(ruleBotAction.contains("bet") || ruleBotAction.contains("raise")) {
                    if(ruleBotStack == 0 || ((aiBotStack + aiBotBetSize) <= ruleBotBetSize)) {
                        List<String> eligibleActions = Arrays.asList("fold", "call");

                        //System.out.println(getOpponentTypeString2(ruleBot));
                        //System.out.println(getOpponentTypeString(ruleBot));

                        aiBotAction = poker.getAction(null, eligibleActions, getStreet(), aiBotIsButton, getPotSizeInBb(), ruleBotAction, getAiBotFacingOdds(), getEffectiveStackInBb(), aiBotHasStrongDraw, aiBotHandStrength, getOpponentTypeString(ruleBot), getRuleBotBetSizeInBb(), getAiBotBetSizeInBb(), ruleBotStack / bigBlind, aiBotStack / bigBlind, board.isEmpty(), board, aiBotHasStrongFlushDraw, aiBotHasStrongOosd, aiBotHasStrongGutshot, bigBlind, false, false);
                    } else {
                        List<String> eligibleActions = Arrays.asList("fold", "call", "raise");

                        //System.out.println(getOpponentTypeString2(ruleBot));
                        //System.out.println(getOpponentTypeString(ruleBot));

                        aiBotAction = poker.getAction(null, eligibleActions, getStreet(), aiBotIsButton, getPotSizeInBb(), ruleBotAction, getAiBotFacingOdds(), getEffectiveStackInBb(), aiBotHasStrongDraw, aiBotHandStrength, getOpponentTypeString(ruleBot), getRuleBotBetSizeInBb(), getAiBotBetSizeInBb(), ruleBotStack / bigBlind, aiBotStack / bigBlind, board.isEmpty(), board, aiBotHasStrongFlushDraw, aiBotHasStrongOosd, aiBotHasStrongGutshot, bigBlind, false, false);
                    }
                } else {
                    if(board.isEmpty() && !aiBotIsButton && aiBotBetSize / bigBlind == 1 && ruleBotBetSize / bigBlind == 1) {
                        List<String> eligibleActions = Arrays.asList("check", "raise");

                        //System.out.println(getOpponentTypeString2(ruleBot));
                        //System.out.println(getOpponentTypeString(ruleBot));

                        aiBotAction = poker.getAction(null, eligibleActions, getStreet(), aiBotIsButton, getPotSizeInBb(), ruleBotAction, getAiBotFacingOdds(), getEffectiveStackInBb(), aiBotHasStrongDraw, aiBotHandStrength, getOpponentTypeString(ruleBot), getRuleBotBetSizeInBb(), getAiBotBetSizeInBb(), ruleBotStack / bigBlind, aiBotStack / bigBlind, board.isEmpty(), board, aiBotHasStrongFlushDraw, aiBotHasStrongOosd, aiBotHasStrongGutshot, bigBlind, false, false);
                    } else {
                        List<String> eligibleActions = Arrays.asList("check", "bet75pct");

                        //System.out.println(getOpponentTypeString2(ruleBot));
                        //System.out.println(getOpponentTypeString(ruleBot));

                        aiBotAction = poker.getAction(null, eligibleActions, getStreet(), aiBotIsButton, getPotSizeInBb(), ruleBotAction, getAiBotFacingOdds(), getEffectiveStackInBb(), aiBotHasStrongDraw, aiBotHandStrength, getOpponentTypeString(ruleBot), getRuleBotBetSizeInBb(), getAiBotBetSizeInBb(), ruleBotStack / bigBlind, aiBotStack / bigBlind, board.isEmpty(), board, aiBotHasStrongFlushDraw, aiBotHasStrongOosd, aiBotHasStrongGutshot, bigBlind, false, false);
                    }
                }
            } else {
                aiBotAction = new DbAiActionBuilder().doAiDbAction(this);
                String actionCopy = aiBotAction;
                String route = poker.getRoute(getStreet(), aiBotIsButton, getPotSizeInBb(), ruleBotAction, getAiBotFacingOdds(), getEffectiveStackInBb(), aiBotHasStrongDraw);
                aiBotActionHistory.put(getHighestKeyFromMap() + 1, Arrays.asList(String.valueOf(aiBotHandStrength), route, actionCopy));
            }
        } else if(bot.equals("ruleBot")) {
            ruleBotAction = ruleBot.doAction(aiBotAction, ruleBotHandStrength, ruleBotHasStrongDraw, getAiBotBetSizeInBb(),
                    getRuleBotBetSizeInBb(), (aiBotStack / bigBlind), (ruleBotStack / bigBlind), !aiBotIsButton, board.isEmpty(), board, getRuleBotFacingOdds());


//            if(ruleBot instanceof TightPassive) {
//                new OpponentIdentifier().updateCounts("tightPassive", ruleBotAction, SimulatedHand.numberOfHandsPlayed);
//            } else if(ruleBot instanceof LoosePassive) {
//                new OpponentIdentifier().updateCounts("loosePassive", ruleBotAction, SimulatedHand.numberOfHandsPlayed);
//            } else if(ruleBot instanceof TightAggressive) {
//                new OpponentIdentifier().updateCounts("tightAggressive", ruleBotAction, SimulatedHand.numberOfHandsPlayed);
//            } else if(ruleBot instanceof LooseAggressive) {
//                new OpponentIdentifier().updateCounts("looseAggressive", ruleBotAction, SimulatedHand.numberOfHandsPlayed);
//            }
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

            aiBotHasStrongDraw = hasStrongDraw(handEvaluatorForAiBot, "aiBot");
            ruleBotHasStrongDraw = hasStrongDraw(handEvaluatorForRuleBot, "ruleBot");
        }
    }

    private boolean hasStrongDraw(HandEvaluator handEvaluator, String aiBotOrRuleBot) {
        if(aiBotOrRuleBot.equals("aiBot")) {
            aiBotHasStrongFlushDraw = handEvaluator.hasDrawOfType("strongFlushDraw");
            aiBotHasStrongOosd = handEvaluator.hasDrawOfType("strongOosd");
            aiBotHasStrongGutshot = handEvaluator.hasDrawOfType("strongGutshot");

            return aiBotHasStrongFlushDraw || aiBotHasStrongOosd || aiBotHasStrongGutshot;
        } else {
            ruleBotHasStrongFlushDraw = handEvaluator.hasDrawOfType("strongFlushDraw");
            ruleBotHasStrongOosd = handEvaluator.hasDrawOfType("strongOosd");
            ruleBotHasStrongGutshot = handEvaluator.hasDrawOfType("strongGutshot");

            return ruleBotHasStrongFlushDraw || ruleBotHasStrongOosd || ruleBotHasStrongGutshot;
        }
    }

    public double getEffectiveStackInBb() {
        if(aiBotStack > ruleBotStack) {
            return ruleBotStack / bigBlind;
        }
        return aiBotStack / bigBlind;
    }

    public double getPotSizeInBb() {
        if(board.isEmpty()) {
            return (aiBotBetSize + ruleBotBetSize) / bigBlind;
        } else {
            return pot / bigBlind;
        }
    }

    private double getAiBotBetSizeInBb() {
        return aiBotBetSize / bigBlind;
    }

    private double getRuleBotBetSizeInBb() {
        return ruleBotBetSize / bigBlind;
    }

    public double getAiBotFacingOdds() {
        double facingOdds = (ruleBotBetSize - aiBotBetSize) / (pot + aiBotBetSize + ruleBotBetSize);
        return facingOdds;
    }

    private double getRuleBotFacingOdds() {
        double facingOdds = (aiBotBetSize - ruleBotBetSize) / (pot + aiBotBetSize + ruleBotBetSize);
        return facingOdds;
    }

    public String getStreet() {
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

    public static String getOpponentTypeString(AbstractOpponent opponent) {
        String opponentTypeString = null;

        if(opponent instanceof TightPassive) {
            opponentTypeString = "tp";
        } else if(opponent instanceof TightAggressive) {
            opponentTypeString = "ta";
        } else if(opponent instanceof LoosePassive) {
            opponentTypeString = "lp";
        } else if(opponent instanceof LooseAggressive) {
            opponentTypeString = "la";
        } else {
            opponentTypeString = null;
            System.out.println("No valid opponentType");
        }



//        if(opponent instanceof TightPassive) {
//            opponentTypeString = new OpponentIdentifier().getOpponentType("tightPassive", numberOfHandsPlayed);
//        } else if(opponent instanceof LoosePassive) {
//            opponentTypeString = new OpponentIdentifier().getOpponentType("loosePassive", numberOfHandsPlayed);
//        } else if(opponent instanceof TightAggressive) {
//            opponentTypeString = new OpponentIdentifier().getOpponentType("tightAggressive", numberOfHandsPlayed);
//        } else if(opponent instanceof LooseAggressive) {
//            opponentTypeString = new OpponentIdentifier().getOpponentType("looseAggressive", numberOfHandsPlayed);
//        }

        return opponentTypeString;
    }

    public static String getOpponentTypeString2(AbstractOpponent opponent) {
        String opponentTypeString = null;

        if(opponent instanceof TightPassive) {
            opponentTypeString = "tp";
        } else if(opponent instanceof TightAggressive) {
            opponentTypeString = "ta";
        } else if(opponent instanceof LoosePassive) {
            opponentTypeString = "lp";
        } else if(opponent instanceof LooseAggressive) {
            opponentTypeString = "la";
        } else {
            opponentTypeString = null;
            System.out.println("No valid opponentType");
        }

        return opponentTypeString;
    }

    public boolean isRandomContinuation() {
        return randomContinuation;
    }

    public void setRandomContinuation(boolean randomContinuation) {
        this.randomContinuation = randomContinuation;
    }

    public boolean isAiBotIsButton() {
        return aiBotIsButton;
    }

    public String getRuleBotAction() {
        return ruleBotAction;
    }

    public boolean isAiBotHasStrongDraw() {
        return aiBotHasStrongDraw;
    }

    public double getAiBotHandStrength() {
        return aiBotHandStrength;
    }

    public double getRuleBotStack() {
        return ruleBotStack;
    }

    public double getAiBotStack() {
        return aiBotStack;
    }

    public double getAiBotBetSize() {
        return aiBotBetSize;
    }

    public double getRuleBotBetSize() {
        return ruleBotBetSize;
    }

    public List<Card> getBoard() {
        return board;
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public AbstractOpponent getRuleBot() {
        return ruleBot;
    }
}
