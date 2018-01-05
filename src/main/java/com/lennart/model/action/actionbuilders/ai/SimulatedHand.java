package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by lpo21630 on 4-1-2018.
 */
public class SimulatedHand {

    private double aiBotStack = 50;
    private double ruleBotStack = 50;
    private double pot = 0;
    boolean aiBotIsButton;
    List<Card> deck = BoardEvaluator.getCompleteCardDeck();
    List<Card> aiBotHolecards = new ArrayList<>();
    List<Card> ruleBotHolecards = new ArrayList<>();
    List<Card> board = new ArrayList<>();

    private double aiBotBetSize = 0;
    private double ruleBotBetSize = 0;

    private boolean continueHand = true;
    private boolean nextStreetNeedsToBeDealt = false;

    private double aiBotHandStrength;
    private double ruleBotHandStrength;

    private boolean playerIsAllIn = false;

    public SimulatedHand(int numberOfHandsPlayed) {
        aiBotHolecards.add(getAndRemoveRandomCardFromDeck());
        aiBotHolecards.add(getAndRemoveRandomCardFromDeck());
        ruleBotHolecards.add(getAndRemoveRandomCardFromDeck());
        ruleBotHolecards.add(getAndRemoveRandomCardFromDeck());

        if(numberOfHandsPlayed % 2 == 0) {
            aiBotStack = 49.50;
            ruleBotStack = 49.75;
            aiBotIsButton = false;
        } else {
            aiBotStack = 49.75;
            ruleBotStack = 49.50;
            aiBotIsButton = true;
        }

        board.add(getAndRemoveRandomCardFromDeck());
        board.add(getAndRemoveRandomCardFromDeck());
        board.add(getAndRemoveRandomCardFromDeck());
    }

    public Map<String, Double> playHand() {
        while(continueHand) {
            while(!nextStreetNeedsToBeDealt && !playerIsAllIn) {
                if(!aiBotIsButton) {
                    doAiBotAction();
                    doRuleBotAction();
                } else {
                    doRuleBotAction();
                    doAiBotAction();
                }
            }

            dealNextStreet();

            if(!playerIsAllIn) {
                if(!aiBotIsButton) {
                    doAiBotAction();
                    doRuleBotAction();
                } else {
                    doRuleBotAction();
                    doAiBotAction();
                }
            }
        }

        Map<String, Double> scoreMap = new HashMap<>();
        scoreMap.put("aiBot", aiBotStack - 50);
        scoreMap.put("ruleBot", ruleBotStack - 50);

        return scoreMap;
    }

    private void doAiBotAction() {
        String action = "";

        if(action.equals("fold")) {
            continueHand = false;
            allocatePotAndBetsToWinner("ruleBot");
        } else if(action.equals("call")) {
            double callAmount = ruleBotBetSize - aiBotBetSize;

            if(callAmount < aiBotStack && ruleBotStack > 0) {
                if(board.size() < 5) {
                    pot = pot + ruleBotBetSize + (ruleBotBetSize - aiBotBetSize);
                    aiBotStack = aiBotStack - (ruleBotBetSize - aiBotBetSize);
                    nextStreetNeedsToBeDealt = true;
                } else {
                    continueHand = false;
                    String winner = determineWinnerAtShowdown();
                    allocatePotAndBetsToWinner(winner);
                }
            } else {
                if(callAmount >= aiBotStack) {
                    if(board.size() < 5) {
                        pot = pot + (2 * aiBotStack);
                        aiBotStack = 0;
                        nextStreetNeedsToBeDealt = true;
                        playerIsAllIn = true;
                    } else {
                        continueHand = false;
                        String winner = determineWinnerAtShowdown();
                        allocatePotAndBetsToWinner(winner);
                    }
                } else if(ruleBotStack == 0) {
                    if(board.size() < 5) {
                        pot = pot + (2 * ruleBotStack);
                        aiBotStack = aiBotStack - (ruleBotBetSize - aiBotBetSize);
                        nextStreetNeedsToBeDealt = true;
                        playerIsAllIn = true;
                    } else {
                        continueHand = false;
                        String winner = determineWinnerAtShowdown();
                        allocatePotAndBetsToWinner(winner);
                    }
                }
            }
        } else if(action.equals("check")) {
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
        } else if(action.equals("bet25%")) {
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
        } else if(action.equals("bet50%")) {
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
        } else if(action.equals("bet75%")) {
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
        } else if(action.equals("bet100%")) {
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
        } else if(action.equals("bet150%")) {
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
        } else if(action.equals("bet200%")) {
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
        } else if(action.equals("raise")) {
            double effectiveStack;

            if(aiBotStack > ruleBotStack) {
                effectiveStack = ruleBotStack;
            } else {
                effectiveStack = aiBotStack;
            }

            double sizeToBet = calculateRaiseAmount((ruleBotBetSize - aiBotBetSize), pot, effectiveStack, aiBotStack, 2.33);

            if((sizeToBet - aiBotBetSize) >= aiBotStack) {
                aiBotBetSize = aiBotStack;
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

    }

    private void dealNextStreet() {

    }

    private void allocatePotAndBetsToWinner(String winner) {
        if(winner.equals("aiBot")) {
            aiBotStack = aiBotStack + aiBotBetSize + ruleBotBetSize + pot;
        } else if(winner.equals("ruleBot")) {
            ruleBotStack = ruleBotStack + ruleBotBetSize + aiBotBetSize + pot;
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
        } else if (aiBotHandStrength == ruleBotHandStrength) {
            return "draw";
        } else {
            return "ruleBot";
        }
    }

    private double calculateRaiseAmount(double facingBetSize, double potSize, double effectiveStack,
                                        double raisingPlayerStack, double odds) {
        double raiseAmount = (potSize / (odds - 1)) + (((odds + 1) * facingBetSize) / (odds - 1));
        double potSizeAfterRaiseAndCall = potSize + raiseAmount + raiseAmount;
        double effectiveStackRemainingAfterRaise = effectiveStack - raiseAmount;

        if(effectiveStackRemainingAfterRaise / potSizeAfterRaiseAndCall < 0.51) {
            raiseAmount = raisingPlayerStack;
        }
        return raiseAmount;
    }
}
