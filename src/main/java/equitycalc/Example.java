package equitycalc;


import equitycalc.combination.Card;
import equitycalc.simulation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Example implements SimulationNotifiable {

    private Simulator simulator;
    private boolean processFinished = false;

    public static void main(String[] args) throws Exception {
        System.out.println(new Example().calculateEquity(null, null));
    }


    public double calculateEquity(List<com.lennart.model.card.Card> board,
                                  List<com.lennart.model.card.Card> botHoleCards) throws Exception {


        Card[] flopCards = {new Card(5, 's'), new Card(6, 'h'), new Card(2, 'c')};

        Card[] cards = {new Card(5, 'd'), new Card(14, 'c')};
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

        while(!processFinished) {
            TimeUnit.MILLISECONDS.sleep(7);
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
        int progress = (Integer) event.getEventData();
        System.out.println("Progress: " + progress + " %");
    }
    
    @Override
    public void onSimulationError(SimulationEvent event)
    {
        Exception e = (Exception) event.getEventData();
        System.err.println("The simulation encountered an error: " + e.getMessage());
    }
    
//    public static void main(String[] args)
//    {
//        Example instance = new Example();
//
//        instance.start();
//    }
}
