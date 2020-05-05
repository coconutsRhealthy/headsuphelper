package equitycalc;


import equitycalc.combination.Card;
import equitycalc.simulation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class EquityCalculator implements SimulationNotifiable {

    private Simulator simulator;
    private Map<List<com.lennart.model.card.Card>, Double> equities = new HashMap<>();

    public double getComboEquity(List<com.lennart.model.card.Card> combo, List<com.lennart.model.card.Card> flop,
                                 com.lennart.model.card.Card turn, com.lennart.model.card.Card river) {
        Card[] _combo = combo.stream()
                .map(izoCard -> new Card(izoCard.getRank(), izoCard.getSuit()))
                .toArray(Card[]::new);

        Card[] _flopCards;
        Card _turn;
        Card _river;

        if(flop != null) {
            _flopCards = flop.stream()
                    .map(izoCard -> new Card(izoCard.getRank(), izoCard.getSuit()))
                    .toArray(Card[]::new);
        } else {
            _flopCards = null;
        }

        if(turn != null) {
            _turn = new Card(turn.getRank(), turn.getSuit());
        } else {
            _turn = null;
        }

        if(river != null) {
            _river = new Card(river.getRank(), river.getSuit());
        } else {
            _river = null;
        }

        prepareTheSimulator(_combo, _flopCards, _turn, _river, false);
        simulator.start();

        while(!simulator.getExecutor().isTerminated());

        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (Exception e) {

        }

        return equities.entrySet().iterator().next().getValue();
    }

    public Map<List<com.lennart.model.card.Card>, Double> getRangeEquities(List<List<com.lennart.model.card.Card>> range,
                                                                           List<com.lennart.model.card.Card> flop,
                                                                           com.lennart.model.card.Card turn,
                                                                           com.lennart.model.card.Card river) {
        Card[] _flopCards;
        Card _turn;
        Card _river;

        if(flop != null) {
            _flopCards = flop.stream()
                    .map(izoCard -> new Card(izoCard.getRank(), izoCard.getSuit()))
                    .toArray(Card[]::new);
        } else {
            _flopCards = null;
        }

        if(turn != null) {
            _turn = new Card(turn.getRank(), turn.getSuit());
        } else {
            _turn = null;
        }

        if(river != null) {
            _river = new Card(river.getRank(), river.getSuit());
        } else {
            _river = null;
        }

        for(List<com.lennart.model.card.Card> combo : range) {
            Card[] _combo = combo.stream()
                    .map(izoCard -> new Card(izoCard.getRank(), izoCard.getSuit()))
                    .toArray(Card[]::new);

            prepareTheSimulator(_combo, _flopCards, _turn, _river, true);
            simulator.start();
        }

        while(!simulator.getExecutor().isTerminated());

        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (Exception e) {

        }

        return equities;
    }

    private void prepareTheSimulator(Card[] _combo, Card[] _flopCards, Card _turn, Card _river, boolean partOfRange) {
        PlayerProfile player = new PlayerProfile(HandType.EXACTCARDS, null, _combo);

        Simulator.SimulatorBuilder builder = new Simulator.SimulatorBuilder();

        builder.setGameType(PokerType.TEXAS_HOLDEM)
                .setNotifiable(this)
                .setUpdateInterval(10)
                .addPlayer(player)
                .addPlayer(new PlayerProfile(HandType.RANDOM, null, null));

        if(_flopCards == null) {
            builder.setNrRounds(50000);
        } else {
            if(partOfRange) {
                builder.setNrRounds(50);
            } else {
                //todo: check welk nummer
                builder.setNrRounds(7500);
            }

            builder.setFlop(_flopCards);

            if(_turn != null) {
                builder.setTurn(_turn);

                if(_river != null) {
                    builder.setRiver(_river);
                }
            }
        }

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
