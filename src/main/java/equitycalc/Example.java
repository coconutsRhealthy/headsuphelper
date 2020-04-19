package equitycalc;


import equitycalc.combination.Card;
import equitycalc.simulation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Example implements SimulationNotifiable
{
    public Simulator simulator;


    private List<Double> equities = new ArrayList<>();

    
    public Example()
    {
        //Card[] flopCards = {new Card(5, 's'), new Card('3', 's'), new Card(2, 'c')};

        //Card[] cards = {new Card(6, 's'), new Card(7, 's')};

//        PlayerProfile player = new PlayerProfile(HandType.EXACTCARDS, null, cards);
//
//        SimulatorBuilder builder = new Simulator.SimulatorBuilder();
//
//        builder.setGameType(PokerType.TEXAS_HOLDEM)
//                .setNrRounds(5000)    //simulate for 100 thousand rounds
//                .setNotifiable(this)        //call notification methods on "this" object
//                .setUpdateInterval(10)      //call update method at progress intervals of at least 10 %
//                .addPlayer(player)
//                .addPlayer(new PlayerProfile(HandType.RANDOM, null, null))
//                .setFlop(flopCards);
//
//        this.simulator = builder.build();
    }

    private void doShit(Card[] flopCards, Card[] cards) {
        //Card[] flopCards = {new Card(5, 's'), new Card('3', 's'), new Card(2, 'c')};

        //Card[] cards = {new Card(6, 's'), new Card(7, 's')};

        PlayerProfile player = new PlayerProfile(HandType.EXACTCARDS, null, cards);

        Simulator.SimulatorBuilder builder = new Simulator.SimulatorBuilder();

        builder.setGameType(PokerType.TEXAS_HOLDEM)
                .setNrRounds(50)    //simulate for 100 thousand rounds
                .setNotifiable(this)        //call notification methods on "this" object
                .setUpdateInterval(10)      //call update method at progress intervals of at least 10 %
                .addPlayer(player)
                .addPlayer(new PlayerProfile(HandType.RANDOM, null, null))
                .setFlop(flopCards);

        this.simulator = builder.build();
    }
    
    public void start()
    {
        this.simulator.start();
    }
    
    @Override
    public void onSimulationStart(SimulationEvent event)
    {
        int workers = (Integer) event.getEventData();
//        System.out.println("Simulator started on " + workers + " threads");
    }
    
    @Override
    public void onSimulationDone(SimulationEvent event)
    {
        SimulationFinalResult result = (SimulationFinalResult) event.getEventData();
        
        double w0 = result.getWinPercentage(0);
        double l0 = result.getLosePercentage(0);
        double t0 = result.getTiePercentage(0);
        
//        double w1 = result.getWinPercentage(1);
//        double l1 = result.getLosePercentage(1);
//        double t1 = result.getTiePercentage(1);
        
//        double w2 = result.getWinPercentage(2);
//        double l2 = result.getLosePercentage(2);
//        double t2 = result.getTiePercentage(2);
        
//        System.out.println("Win 1: " + w0);
//        System.out.println("Lose 1: " + l0);
//        System.out.println("Tie 1: " + t0);
        
//        System.out.println("Win 2: " + w1);
//        System.out.println("Lose 2: " + l1);
//        System.out.println("Tie 2: " + t1);
        
//        System.out.println("Win 3: " + w2);
//        System.out.println("Lose 3: " + l2);
//        System.out.println("Tie 3: " + t2);
        
        long duration = result.getDuration();
        //System.out.println("Duration: " + duration + " ms");

        //vul hier een lijst met alle equities....
        equities.add(w0 / 100);
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
        int progress = (Integer) event.getEventData();
        //System.out.println("Progress: " + progress + " %");
    }
    
    @Override
    public void onSimulationError(SimulationEvent event)
    {
        Exception e = (Exception) event.getEventData();
        System.err.println("The simulation encountered an error: " + e.getMessage());
    }
    
    public static void main(String[] args) throws Exception
    {
        for(int i = 0; i < 10; i++) {
            Card[] flopCards = {new Card(5, 's'), new Card('3', 's'), new Card(2, 'c')};

            List<Card[]> holeCardList = new ArrayList<>();

            Card[] hc1 = {new Card(6, 's'), new Card(10, 'd')};
            Card[] hc2 = {new Card(7, 's'), new Card(11, 'd')};
            Card[] hc3 = {new Card(8, 's'), new Card(12, 'd')};

            holeCardList.add(hc1);
            holeCardList.add(hc2);
            holeCardList.add(hc3);

            Example instance = new Example();

            for(Card[] hc : holeCardList) {
                instance.doShit(flopCards, hc);

                instance.start();
            }

            while(!instance.simulator.getExecutor().isTerminated());

            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (Exception e) {

            }

            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            System.out.println(instance.equities.size());
        }


    }

    public List<Double> getAllEquities(List<List<com.lennart.model.card.Card>> range,
                                       List<com.lennart.model.card.Card> flop) {
        Card[] flopCards = flop.stream()
                .map(hhCard -> new Card(hhCard.getRank(), hhCard.getSuit()))
                .toArray(Card[]::new);

        int counter = 0;

        for(List<com.lennart.model.card.Card> combo : range) {
            Card[] cards = combo.stream()
                    .map(hhCard -> new Card(hhCard.getRank(), hhCard.getSuit()))
                    .toArray(Card[]::new);

            System.out.println(counter++);

            doShit(flopCards, cards);
            simulator.start();
        }

        while(!simulator.getExecutor().isTerminated());

        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (Exception e) {

        }

        return equities;
    }
}
