package equitycalc;


import equitycalc.combination.Card;
import equitycalc.simulation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class EquityCalculator implements SimulationNotifiable {

    private Simulator simulator;
    private Map<List<com.lennart.model.card.Card>, Double> equities = new HashMap<>();

    public double getComboEquity(List<com.lennart.model.card.Card> combo, List<com.lennart.model.card.Card> board) {
        Card[] _combo = combo.stream()
                .map(izoCard -> new Card(izoCard.getRank(), izoCard.getSuit()))
                .toArray(Card[]::new);

        Map<String, List<com.lennart.model.card.Card>> izoFlopTurnRiver = convertIzoBoardToFlopTurnRiver(board);

        Card[] _flopCards = null;
        Card _turn = null;
        Card _river= null;

        if(izoFlopTurnRiver.get("flop") != null) {
            _flopCards = izoFlopTurnRiver.get("flop").stream()
                    .map(izoCard -> new Card(izoCard.getRank(), izoCard.getSuit()))
                    .toArray(Card[]::new);
        }

        if(izoFlopTurnRiver.get("turn") != null) {
            _turn = new Card(izoFlopTurnRiver.get("turn").get(0).getRank(), izoFlopTurnRiver.get("turn").get(0).getSuit());
        }

        if(izoFlopTurnRiver.get("river") != null) {
            _river = new Card(izoFlopTurnRiver.get("river").get(0).getRank(), izoFlopTurnRiver.get("river").get(0).getSuit());
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

    public double getAverageRangeEquity(List<List<com.lennart.model.card.Card>> range,
                                        List<com.lennart.model.card.Card> board) {
        fillRangeMap(range, board);
        List<Double> values = equities.values().stream().collect(Collectors.toList());

        double total = 0;
        double counter = 0;

        for(double d : values) {
            total = total + d;
            counter++;
        }

        return total / counter;
    }

    public Map<List<com.lennart.model.card.Card>, Double> getRangeEquities(List<List<com.lennart.model.card.Card>> range,
                                                                           List<com.lennart.model.card.Card> board) {
        fillRangeMap(range, board);
        return sortByValueHighToLow(equities);
    }

    private void fillRangeMap(List<List<com.lennart.model.card.Card>> range,
                              List<com.lennart.model.card.Card> board) {
        Map<String, List<com.lennart.model.card.Card>> izoFlopTurnRiver = convertIzoBoardToFlopTurnRiver(board);

        Card[] _flopCards = null;
        Card _turn = null;
        Card _river = null;

        if(izoFlopTurnRiver.get("flop") != null) {
            _flopCards = izoFlopTurnRiver.get("flop").stream()
                    .map(izoCard -> new Card(izoCard.getRank(), izoCard.getSuit()))
                    .toArray(Card[]::new);
        }

        if(izoFlopTurnRiver.get("turn") != null) {
            _turn = new Card(izoFlopTurnRiver.get("turn").get(0).getRank(), izoFlopTurnRiver.get("turn").get(0).getSuit());
        }

        if(izoFlopTurnRiver.get("river") != null) {
            _river = new Card(izoFlopTurnRiver.get("river").get(0).getRank(), izoFlopTurnRiver.get("river").get(0).getSuit());
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
    }

    private Map<String, List<com.lennart.model.card.Card>> convertIzoBoardToFlopTurnRiver(List<com.lennart.model.card.Card> board) {
        Map<String, List<com.lennart.model.card.Card>> flopTurnRiver = new HashMap<>();

        if(board != null && !board.isEmpty()) {
            flopTurnRiver.put("flop", board.subList(0, 3));

            if(board.size() >= 4) {
                flopTurnRiver.put("turn", Arrays.asList(board.get(3)));

                if(board.size() == 5) {
                    flopTurnRiver.put("river", Arrays.asList(board.get(4)));
                }
            }
        }

        return flopTurnRiver;
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
            if(partOfRange) {
                builder.setNrRounds(50);
            } else {
                builder.setNrRounds(50000);
                //builder.setNrRounds(200_000);
            }
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

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
