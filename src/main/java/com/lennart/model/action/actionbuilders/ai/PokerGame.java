package com.lennart.model.action.actionbuilders.ai;


import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lpo21630 on 3-1-2018.
 */
public class PokerGame {

    private int aiBotScore = 0;
    private int ruleBotScore = 0;

    private void playHand(int numberOfHandsPlayed) {
        double aiBotStack = 50;
        double ruleBotStack = 50;
        boolean aiBotIsButton;

        if(numberOfHandsPlayed % 2 == 0) {
            aiBotStack = 49.50;
            ruleBotStack = 49.75;
            aiBotIsButton = false;
        } else {
            aiBotStack = 49.75;
            ruleBotStack = 49.50;
            aiBotIsButton = true;
        }

        List<Card> deck = BoardEvaluator.getCompleteCardDeck();
        List<Card> aiBotHolecards = new ArrayList<>();
        List<Card> ruleBotHolecards = new ArrayList<>();
        int random = new Random().nextInt(deck.size());
        aiBotHolecards.add(deck.get(random));
        deck.remove(random);

        random = new Random().nextInt(deck.size());
        aiBotHolecards.add(deck.get(random));
        deck.remove(random);

        random = new Random().nextInt(deck.size());
        ruleBotHolecards.add(deck.get(random));
        deck.remove(random);

        random = new Random().nextInt(deck.size());
        ruleBotHolecards.add(deck.get(random));
        deck.remove(random);

        boolean continueHand = true;

        while(continueHand) {


        }

        //do action

        //update stacks and pot

        //deal next round

        //finish hand







//    private void dealHoleCards() {
//        myHoleCards = new ArrayList<>();
//        computerHoleCards = new ArrayList<>();
//        knownGameCards = new HashSet<>();
//
//        myHoleCards.add(getAndRemoveRandomCardFromDeck());
//        myHoleCards.add(getAndRemoveRandomCardFromDeck());
//        computerHoleCards.add(getAndRemoveRandomCardFromDeck());
//        computerHoleCards.add(getAndRemoveRandomCardFromDeck());
//
//        System.out.println(computerHoleCards.get(0).getRank() + "" + computerHoleCards.get(0).getSuit() + "" +
//                computerHoleCards.get(1).getRank() + "" + computerHoleCards.get(1).getSuit());
//
//        knownGameCards.addAll(computerHoleCards);
//    }

    }


//    private Card getAndRemoveRandomCardFromDeck(List) {
//        Random randomGenerator = new Random();
//        int random = randomGenerator.nextInt(deck.size());
//        Card cardToReturn = deck.get(random);
//        deck.remove(random);
//
//        return cardToReturn;
//    }

    //2 bots

    //instantieer de bots
    //1 is jouw nieuwe ai bot

    //andere is rule based bot



    //bepaal wie button is

    //deel kaarten

    //zet stacks

    //post blinds

    //doe acties

    //indien nodig, deal flop of finish hand

    //





}


