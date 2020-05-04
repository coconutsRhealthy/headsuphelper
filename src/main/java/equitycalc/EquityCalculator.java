package equitycalc;


import equitycalc.combination.Card;
import equitycalc.simulation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class EquityCalculator implements SimulationNotifiable {

    private Simulator simulator;
    private Map<List<com.lennart.model.card.Card>, Double> equities = new HashMap<>();

    public double getComboEquityPreflop(List<com.lennart.model.card.Card> combo) {
        Card[] comboCorrect = combo.stream()
                .map(hhCard -> new Card(hhCard.getRank(), hhCard.getSuit()))
                .toArray(Card[]::new);

        preparePreflopSimulater(comboCorrect);

        simulator.start();

        while(!simulator.getExecutor().isTerminated());

        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (Exception e) {

        }

        return equities.entrySet().iterator().next().getValue();
    }

    public double getComboEquityFlop(List<com.lennart.model.card.Card> combo, List<com.lennart.model.card.Card> flop) {
        Card[] flopCards = flop.stream()
                .map(hhCard -> new Card(hhCard.getRank(), hhCard.getSuit()))
                .toArray(Card[]::new);

        Card[] comboCorrect = combo.stream()
                .map(hhCard -> new Card(hhCard.getRank(), hhCard.getSuit()))
                .toArray(Card[]::new);

        prepareTheSimulator(flopCards, comboCorrect);
        simulator.start();

        while(!simulator.getExecutor().isTerminated());

        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (Exception e) {

        }

        return equities.entrySet().iterator().next().getValue();
    }

    public Map<List<com.lennart.model.card.Card>, Double> getRangeEquityFlop(List<List<com.lennart.model.card.Card>> range,
                                          List<com.lennart.model.card.Card> flop) {
        Card[] flopCards = flop.stream()
                .map(hhCard -> new Card(hhCard.getRank(), hhCard.getSuit()))
                .toArray(Card[]::new);

        for(List<com.lennart.model.card.Card> combo : range) {
            Card[] cards = combo.stream()
                    .map(hhCard -> new Card(hhCard.getRank(), hhCard.getSuit()))
                    .toArray(Card[]::new);

            prepareTheSimulator(flopCards, cards);
            simulator.start();
        }

        while(!simulator.getExecutor().isTerminated());

        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (Exception e) {

        }

        return equities;
    }

    private void prepareTheSimulator(Card[] flopCards, Card[] cards) {
        PlayerProfile player = new PlayerProfile(HandType.EXACTCARDS, null, cards);

        Simulator.SimulatorBuilder builder = new Simulator.SimulatorBuilder();

        builder.setGameType(PokerType.TEXAS_HOLDEM)
                .setNrRounds(50)    //simulate for 50 rounds
                .setNotifiable(this)        //call notification methods on "this" object
                .setUpdateInterval(10)      //call update method at progress intervals of at least 10 %
                .addPlayer(player)
                .addPlayer(new PlayerProfile(HandType.RANDOM, null, null))
                .setFlop(flopCards);

        this.simulator = builder.build();
    }

    private void preparePreflopSimulater(Card[] cards) {
        PlayerProfile player = new PlayerProfile(HandType.EXACTCARDS, null, cards);

        Simulator.SimulatorBuilder builder = new Simulator.SimulatorBuilder();

        builder.setGameType(PokerType.TEXAS_HOLDEM)
                .setNrRounds(50000)    //simulate for 50 rounds
                .setNotifiable(this)        //call notification methods on "this" object
                .setUpdateInterval(10)      //call update method at progress intervals of at least 10 %
                .addPlayer(player)
                .addPlayer(new PlayerProfile(HandType.RANDOM, null, null));

        this.simulator = builder.build();
    }
    
    @Override
    public void onSimulationStart(SimulationEvent event)
    {
        //made method empty
    }
    
    @Override
    public void onSimulationDone(SimulationEvent event)
    {
        SimulationFinalResult result = (SimulationFinalResult) event.getEventData();

        Card[] combo = result.getPlayer(0).getCards();

        List<com.lennart.model.card.Card> comboGoodCardObject = Arrays.asList(
                new com.lennart.model.card.Card(combo[0].getRank(), combo[0].getColor()),
                new com.lennart.model.card.Card(combo[1].getRank(), combo[1].getColor()));

        equities.put(comboGoodCardObject, result.getWinPercentage(0) / 100);
    }
    
    @Override
    public void onSimulationCancel(SimulationEvent event)
    {
        int progress = (Integer) event.getEventData();
        System.out.println("Simulation was stopped at " + progress + " percent");
    }
    
    @Override
    public void onSimulationProgress(SimulationEvent event)
    {
        //made method empty
    }
    
    @Override
    public void onSimulationError(SimulationEvent event)
    {
        Exception e = (Exception) event.getEventData();
        System.err.println("The simulation encountered an error: " + e.getMessage());
    }
}
