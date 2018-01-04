package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;

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

    private boolean continueHand = true;
    private boolean nextStreetHasBeenDealt = false;

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
            while(!nextStreetHasBeenDealt) {
                if(!aiBotIsButton) {
                    doAiBotAction();
                    doRuleBotAction();
                } else {
                    doRuleBotAction();
                    doAiBotAction();
                }
            }

            if(!aiBotIsButton) {
                doAiBotAction();
                doRuleBotAction();
            } else {
                doRuleBotAction();
                doAiBotAction();
            }
        }

        Map<String, Double> scoreMap = new HashMap<>();
        scoreMap.put("aiBot", aiBotStack - 50);
        scoreMap.put("ruleBot", ruleBotStack - 50);

        return scoreMap;
    }

    private void doAiBotAction() {

    }

    private void doRuleBotAction() {

    }

    private Card getAndRemoveRandomCardFromDeck() {
        Random randomGenerator = new Random();
        int random = randomGenerator.nextInt(deck.size());
        Card cardToReturn = deck.get(random);
        deck.remove(random);

        return cardToReturn;
    }
}
