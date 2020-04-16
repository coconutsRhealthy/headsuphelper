package equitycalc;


import equitycalc.combination.Card;
import equitycalc.simulation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ExampleOld implements SimulationNotifiable {

    private Simulator simulator;
    private boolean processFinished = false;

    public static void main(String[] args) throws Exception {
        //System.out.println(new ExampleOld().calculateEquity(null, null));

        List<com.lennart.model.card.Card> hc = Arrays.asList
                (new com.lennart.model.card.Card(8, 'd'), new com.lennart.model.card.Card(2, 'h'));
        List<com.lennart.model.card.Card> hc2 = Arrays.asList
                (new com.lennart.model.card.Card(9, 'd'), new com.lennart.model.card.Card(3, 'h'));

        List<List<com.lennart.model.card.Card>> eije = new ArrayList<>();
        eije.add(hc);
        eije.add(hc2);

        List<com.lennart.model.card.Card> flop = Arrays.asList
                (new com.lennart.model.card.Card(4, 'd'), new com.lennart.model.card.Card(12, 'c'),
                        new com.lennart.model.card.Card(7, 's'));

        for(List<com.lennart.model.card.Card> combo : eije) {
            new ExampleOld().calculateEquity(flop, combo);
        }

        System.out.println("zzz");
    }


    public double calculateEquity(List<com.lennart.model.card.Card> board,
                                  List<com.lennart.model.card.Card> botHoleCards) {
        Card[] cards = botHoleCards.stream()
                .map(hhCard -> new Card(hhCard.getRank(), hhCard.getSuit()))
                .toArray(Card[]::new);

        Card[] flopCards = board.stream()
                .map(hhCard -> new Card(hhCard.getRank(), hhCard.getSuit()))
                .toArray(Card[]::new);

        PlayerProfile player = new PlayerProfile(HandType.EXACTCARDS, null, cards);

        Simulator.SimulatorBuilder builder = new Simulator.SimulatorBuilder();

        builder.setGameType(PokerType.TEXAS_HOLDEM)
                .setNrRounds(5000)
                .setNotifiable(this)
                .setUpdateInterval(10)
                .addPlayer(player)
                .addPlayer(new PlayerProfile(HandType.RANDOM, null, null))
                .setFlop(flopCards);

        this.simulator = builder.build();
        this.simulator.start();

//        while(!processFinished) {
//            try {
//                TimeUnit.MILLISECONDS.sleep(7);
//            } catch (Exception e) {
//
//            }
//        }


        try {
            while(simulator.getExecutor().awaitTermination(7, TimeUnit.SECONDS));
        } catch (Exception e) {
            System.out.println("trrr");
        }

        return simulator.getResult().getWinPercentage(0);
    }
    
    @Override
    public void onSimulationStart(SimulationEvent event)
    {
        int workers = (Integer) event.getEventData();
        System.out.println("Simulator started on " + workers + " threads");
    }
    
    @Override
    public void onSimulationDone(SimulationEvent event)
    {
        processFinished = true;
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
//        int progress = (Integer) event.getEventData();
//        System.out.println("Progress: " + progress + " %");
    }
    
    @Override
    public void onSimulationError(SimulationEvent event)
    {
        Exception e = (Exception) event.getEventData();
        System.err.println("The simulation encountered an error: " + e.getMessage());
    }
    
//    public static void main(String[] args)
//    {
//        ExampleOld instance = new ExampleOld();
//
//        instance.start();
//    }
}
