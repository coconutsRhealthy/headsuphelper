package com.lennart.model.action.actionbuilders.ai;

import java.util.*;

/**
 * Created by LennartMac on 28/12/2017.
 */
public class ActionBuilder {

    //input

        //handstrength (equity)         X
        //handpath
        //position
        //potsize
        //opponent stack
        //opponent betsize
        //mystack
        //mybetsize
        //opponent type
        //board texture                 X


    //analyse

        //fold
        //check
        //call
        //bet
        //raise


    //action

        //one with highest expected payoff






    //small game
        //computer A vs computer B

        //100 rounds, each round A and B have 10 options: 1, 2, 3, 4, 5, 6, 7, 8, 9 and 10

        //A only knows it his 10 options (plays random), B remembers the payoff of each choice


    private double payoff1 = 0;
    private double payoff2 = 0;
    private double payoff3 = 0;
    private double payoff4 = 0;
    private double payoff5 = 0;
    private double payoff6 = 0;
    private double payoff7 = 0;
    private double payoff8 = 0;
    private double payoff9 = 0;
    private double payoff10 = 0;

//    public static void main(String[] args) {
//        new ActionBuilder().theMethod();
//    }

    private void theMethod() {

        int totalScoreA = 0;
        int totalScoreB = 0;

        Map<Integer, List<String>> payoffMapA = getInitialPayoffMap();
        Map<Integer, List<String>> payoffMapB = getInitialPayoffMap();

        for(int i = 0; i < 100; i++) {
            int actionA = chooseActionPlayerA(payoffMapA);
            int actionB = chooseActionPlayerB(payoffMapB);

            int payoffA = getPayoff(actionA);
            int payoffB = getPayoff(actionB);

            String winner = decideWinner(payoffA, payoffB);

            String resultForA;
            String resultForB;

            if(winner.equals("A")) {
                totalScoreA++;
                resultForA = "Win";
                resultForB = "Loss";
                //System.out.println("A wins");
            } else if(winner.equals("B")) {
                totalScoreB++;
                resultForA = "Loss";
                resultForB = "Win";
                //System.out.println("B wins");
            } else {
                resultForA = "Draw";
                resultForB = "Draw";
                //System.out.println("Draw");
            }

            System.out.println("A chooses: " + actionA);
            System.out.println("B chooses: " + actionB);
            payoffMapA = updatePayoffMap(payoffMapA, actionA, resultForA);
            payoffMapB = updatePayoffMap(payoffMapB, actionB, resultForB);
        }

        System.out.println("Total score player A: " + totalScoreA);
        System.out.println("Total score player B: " + totalScoreB);
    }

    private int chooseActionPlayerA(Map<Integer, List<String>> payoffMap) {
//        Random rn = new Random();
//        return rn.nextInt(10 - 1 + 1) + 1;

        return getBestActionFromPayoffMap(payoffMap);
    }

    private int chooseActionPlayerB(Map<Integer, List<String>> payoffMap) {
        return getBestActionFromPayoffMap(payoffMap);
    }

    private Map<Integer, List<String>> updatePayoffMap(Map<Integer, List<String>> payoffMap, int action, String result) {
        payoffMap.get(action).add(result);
        return payoffMap;
    }

    private int getBestActionFromPayoffMap(Map<Integer, List<String>> payoffMap) {
        Map<Double, Double> percentageOfWinsPerActionMap = new HashMap<>();

        for (Map.Entry<Integer, List<String>> entry : payoffMap.entrySet()) {
            double numberOfWins = Collections.frequency(entry.getValue(), "Win");
            double size = entry.getValue().size();

            percentageOfWinsPerActionMap.put((double) entry.getKey(), (numberOfWins / size));
        }

        percentageOfWinsPerActionMap = sortByValueHighToLow(percentageOfWinsPerActionMap);

        double valueFromMap = percentageOfWinsPerActionMap.entrySet().iterator().next().getValue();
        double actionFromMap = percentageOfWinsPerActionMap.entrySet().iterator().next().getKey();

        //return (int) actionFromMap;

        Double d = valueFromMap;
        if(d.isNaN()) {
            Random rn = new Random();
            return rn.nextInt(10 - 1 + 1) + 1;
        } else {
            return (int) actionFromMap;
        }
    }

    private int getPayoff(int choice) {
        return choice;
    }

    private String decideWinner(int payoffA, int payoffB) {
        if(payoffA > payoffB) {
            return "A";
        } else if(payoffA == payoffB) {
            return "draw";
        } else {
            return "B";
        }
    }

    private Map<Integer, List<String>> getInitialPayoffMap() {
        Map<Integer, List<String>> initialPayoffMap = new HashMap<>();

        initialPayoffMap.put(1, new ArrayList<>());
        initialPayoffMap.put(2, new ArrayList<>());
        initialPayoffMap.put(3, new ArrayList<>());
        initialPayoffMap.put(4, new ArrayList<>());
        initialPayoffMap.put(5, new ArrayList<>());
        initialPayoffMap.put(6, new ArrayList<>());
        initialPayoffMap.put(7, new ArrayList<>());
        initialPayoffMap.put(8, new ArrayList<>());
        initialPayoffMap.put(9, new ArrayList<>());
        initialPayoffMap.put(10, new ArrayList<>());

        return initialPayoffMap;
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
