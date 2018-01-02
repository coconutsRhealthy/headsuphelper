package com.lennart.model.action.actionbuilders.ai;

import java.util.*;

/**
 * Created by LennartMac on 29/12/2017.
 */
public class SimpleGame {

    //het spel:
        //we beginnen allebei met 20 fiches

        //ik krijg een nummer onder de 10 (1 tm 9), en computer krijgt ook een nummer onder de 10

        //we leggen allebei een fiche in de pot

        //computer mag wedden of hij een hoger nummer heeft dan ik of niet
            //concreet:

            //hij mag checken en meteen naar showdown gaan

            //hij mag 1 extra fiche inzetten

            //hij mag 2 extra fiches inzetten

            //hij mag 3 extra fiches inzetten

        //ik mag vervolgens callen of folden

        //vervolgens gaat de hele pot naar de winnaar


    private int humanScore = 0;
    private int computerScore = 0;

    private Map<Integer, Map<String, List<Double>>> payoffMap = initializePayoffMap();

    public static void main(String[] args) {
        SimpleGame simpleGame = new SimpleGame();

        for(int i = 0; i < 50000; i++) {
            simpleGame.playOneGame(i);
            System.out.println(i + " - " + simpleGame.computerScore);
        }
    }

    private Map<Integer, Map<String, List<Double>>> initializePayoffMap() {
        Map<Integer, Map<String, List<Double>>> payoffMap = new HashMap<>();

        payoffMap.put(1, new HashMap<>());
        payoffMap.put(2, new HashMap<>());
        payoffMap.put(3, new HashMap<>());
        payoffMap.put(4, new HashMap<>());
        payoffMap.put(5, new HashMap<>());
        payoffMap.put(6, new HashMap<>());
        payoffMap.put(7, new HashMap<>());
        payoffMap.put(8, new HashMap<>());
        payoffMap.put(9, new HashMap<>());

        payoffMap.get(1).put("fold 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("fold 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("fold 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("bet 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("call 4", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("call 6", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("call 9", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("raise all-in", Arrays.asList(0.0, 0.0));

        payoffMap.get(2).put("fold 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("fold 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("fold 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("bet 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("call 4", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("call 6", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("call 9", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("raise all-in", Arrays.asList(0.0, 0.0));

        payoffMap.get(3).put("fold 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("fold 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("fold 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("bet 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("call 4", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("call 6", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("call 9", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("raise all-in", Arrays.asList(0.0, 0.0));

        payoffMap.get(4).put("fold 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("fold 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("fold 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("bet 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("call 4", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("call 6", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("call 9", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("raise all-in", Arrays.asList(0.0, 0.0));

        payoffMap.get(5).put("fold 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("fold 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("fold 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("bet 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("call 4", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("call 6", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("call 9", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("raise all-in", Arrays.asList(0.0, 0.0));

        payoffMap.get(6).put("fold 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("fold 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("fold 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("bet 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("call 4", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("call 6", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("call 9", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("raise all-in", Arrays.asList(0.0, 0.0));

        payoffMap.get(7).put("fold 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("fold 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("fold 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("bet 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("call 4", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("call 6", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("call 9", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("raise all-in", Arrays.asList(0.0, 0.0));

        payoffMap.get(8).put("fold 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("fold 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("fold 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("bet 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("call 4", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("call 6", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("call 9", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("raise all-in", Arrays.asList(0.0, 0.0));

        payoffMap.get(9).put("fold 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("fold 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("fold 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("bet 3", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("call 4", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("call 6", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("call 9", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("raise all-in", Arrays.asList(0.0, 0.0));

        return payoffMap;
    }

    private void playOneGame(int gameNumber) {
        int humanNumber = getRandomNumber();
        int computerNumber = getRandomNumber();

        int humanStack = 19;
        int computerStack = 19;

        int pot = 2;

        String computerAction = doComputerAction(computerNumber, gameNumber, null, null);
        int[] stackAndPot = setStacksAndPot(computerAction, pot, computerStack);
        pot = stackAndPot[0];
        computerStack = stackAndPot[1];

        String humanAction = doHumanAction(humanNumber, computerAction);
        stackAndPot = setStacksAndPot(humanAction, pot, humanStack);
        pot = stackAndPot[0];
        humanStack = stackAndPot[1];

        if(humanAction.contains("raise")) {
            computerAction = doComputerAction(computerNumber, gameNumber, humanAction, computerAction);

            stackAndPot = setStacksAndPot(computerAction, pot, computerStack);
            pot = stackAndPot[0];
            computerStack = stackAndPot[1];

            if(computerAction.contains("raise")) {
                humanAction = doHumanAction(humanNumber, computerAction);
                stackAndPot = setStacksAndPot(humanAction, pot, humanStack);
                pot = stackAndPot[0];
                humanStack = stackAndPot[1];
            }
        }

        String winner = determineWinner(humanAction, computerAction, humanNumber, computerNumber);
        List<Integer> stacks = allocatePotToWinnerStack(winner, humanStack, computerStack, pot);

        humanStack = stacks.get(0);
        computerStack = stacks.get(1);

        updateScores(humanStack, computerStack);
        updatePayoffMap(computerNumber, computerAction, computerStack);
    }

    private int[] setStacksAndPot(String action, int currentPot, int currentStack) {
        int potToReturn = currentPot;
        int stackToReturn = currentStack;

        int[] arrayToReturn = new int[2];

        if(!action.contains("fold")) {
            if(action.contains("1")) {
                potToReturn++;
                stackToReturn = stackToReturn - 1;
            } else if(action.contains("2")) {
                potToReturn = potToReturn + 2;
                stackToReturn = stackToReturn - 2;
            } else if(action.contains("3")) {
                potToReturn = potToReturn + 3;
                stackToReturn = stackToReturn - 3;
            } else if(action.contains("4")) {
                potToReturn = potToReturn + 4;
                stackToReturn = stackToReturn - 4;
            } else if(action.contains("6")) {
                potToReturn = potToReturn + 6;
                stackToReturn = stackToReturn - 6;
            } else if(action.contains("9")) {
                potToReturn = potToReturn + 9;
                stackToReturn = stackToReturn - 9;
            } else if(action.contains("all-in")) {
                potToReturn = potToReturn + stackToReturn;
                stackToReturn = 0;
            }
        }

        arrayToReturn[0] = potToReturn;
        arrayToReturn[1] = stackToReturn;

        return arrayToReturn;
    }

    private int getRandomNumber() {
        Random rn = new Random();
        return rn.nextInt(9 - 1 + 1) + 1;
    }

    private void updateScores(int humanStack, int computerStack) {
        humanScore = humanScore + (humanStack - 20);
        computerScore = computerScore + (computerStack - 20);

//        System.out.println();
//        System.out.println("*******************");
//        System.out.println("Human total score: " + humanScore);
//        System.out.println("Computer total score " + computerScore);
//        System.out.println("*******************");
//        System.out.println();
    }

    private String doComputerAction(int computerNumber, int numberOfGames, String humanAction, String earlierComputerAction) {
        if(humanAction == null) {
            return doInitialComputerAction(computerNumber, numberOfGames);
        } else {
            return doSubsequentComputerAction(computerNumber, numberOfGames, humanAction, earlierComputerAction);
        }
    }

    private String doInitialComputerAction(int computerNumber, int numberOfGames) {
        if(numberOfGames > 5000) {
            Map<String, List<Double>> mapToUse = new HashMap<>();
            mapToUse.putAll(payoffMap.get(computerNumber));

            Map<String, Double> mapOfScores = new HashMap<>();
            mapOfScores.put("check", (mapToUse.get("check").get(0) / mapToUse.get("check").get(1)));
            mapOfScores.put("bet 1", (mapToUse.get("bet 1").get(0) / mapToUse.get("bet 1").get(1)));
            mapOfScores.put("bet 2", (mapToUse.get("bet 2").get(0) / mapToUse.get("bet 2").get(1)));
            mapOfScores.put("bet 3", (mapToUse.get("bet 3").get(0) / mapToUse.get("bet 3").get(1)));

            mapOfScores = sortByValueHighToLow(mapOfScores);

            return mapOfScores.entrySet().iterator().next().getKey();
        } else {
            Random rn = new Random();
            int i = rn.nextInt(4 - 1 + 1) + 1;

            if(i == 1) {
                return "check";
            } else if(i == 2) {
                return "bet 1";
            } else if(i == 3) {
                return "bet 2";
            } else if(i == 4) {
                return "bet 3";
            }

            return null;
        }
    }

    private String doSubsequentComputerAction(int computerNumber, int numberOfGames, String humanAction, String earlierComputerAction) {
        int earlierComputerActionNumber;
        int raiseNumber;

        if(earlierComputerAction.contains("1")) {
            earlierComputerActionNumber = 1;
        } else if(earlierComputerAction.contains("2")) {
            earlierComputerActionNumber = 2;
        } else {
            earlierComputerActionNumber = 3;
        }

        if(humanAction.contains("4")) {
            raiseNumber = 4;
        } else if(humanAction.contains("6")) {
            raiseNumber = 6;
        } else {
            raiseNumber = 9;
        }

        if(numberOfGames > 5000) {
            Map<String, List<Double>> mapToUse = new HashMap<>();
            mapToUse.putAll(payoffMap.get(computerNumber));

            Map<String, Double> mapOfScores = new HashMap<>();
            mapOfScores.put("fold " + earlierComputerActionNumber, (mapToUse.get("fold " + earlierComputerActionNumber).get(0) / mapToUse.get("fold " + earlierComputerActionNumber).get(1)));
            mapOfScores.put("call " + raiseNumber, (mapToUse.get("call " + raiseNumber).get(0) / mapToUse.get("call " + raiseNumber).get(1)));
            mapOfScores.put("raise all-in", (mapToUse.get("raise all-in").get(0) / mapToUse.get("raise all-in").get(1)));

            mapOfScores = sortByValueHighToLow(mapOfScores);

            return mapOfScores.entrySet().iterator().next().getKey();
        } else {
            Random rn = new Random();
            int i = rn.nextInt(3 - 1 + 1) + 1;

            if(i == 1) {
                return "fold " + earlierComputerActionNumber;
            } else if(i == 2) {
                return "call " + raiseNumber;
            } else if(i == 3) {
                return "raise all-in";
            }

            return null;
        }
    }

    private void updatePayoffMap(int computerNumber, String computerAction, int computerStack) {
        double payoffThisGame = computerStack - 20;
        double oldPayoffValue = payoffMap.get(computerNumber).get(computerAction).get(0);
        double gamesUntillNow = payoffMap.get(computerNumber).get(computerAction).get(1);
        gamesUntillNow++;

        payoffMap.get(computerNumber).get(computerAction).set(0, (oldPayoffValue + payoffThisGame));
        payoffMap.get(computerNumber).get(computerAction).set(1, gamesUntillNow);
    }

    private String determineWinner(String humanAction, String computerAction, int humanNumber, int computerNumber) {
        String winner = "";

        if(humanAction.contains("fold")) {
            winner = "computer";
        }

        if(computerAction.contains("fold")) {
            winner = "human";
        }

        if(computerAction.equals("check") || humanAction.contains("call") || computerAction.contains("call")) {
            if(humanNumber > computerNumber) {
                winner = "human";
            } else if(humanNumber == computerNumber) {
                winner = "draw";
            } else if(humanNumber < computerNumber) {
                winner = "computer";
            }

            //System.out.println("Your number: " + humanNumber);
            //System.out.println("Computer number: " + computerNumber);
        }

        //System.out.println("The winner is: " + winner);
        //System.out.println();

        return winner;
    }

    private List<Integer> allocatePotToWinnerStack(String winner, int humanStack, int computerStack, int pot) {
        List<Integer> stacks = new ArrayList<>();

        if(winner.equals("human")) {
            humanStack = humanStack + pot;
        } else if(winner.equals("computer")) {
            computerStack = computerStack + pot;
        } else if(winner.equals("draw")) {
            computerStack = computerStack + (pot / 2);
            humanStack = humanStack + (pot / 2);
        }

        stacks.add(humanStack);
        stacks.add(computerStack);

        return stacks;
    }

    private String doHumanAction(int humanNumber, String computerAction) {
        if(computerAction.equals("check")) {
            return "check";
        }

        if(computerAction.equals("bet 1")) {
            if(humanNumber >= 7) {
                return "raise 4";
            } else if (humanNumber == 4 || humanNumber == 5 || humanNumber == 6){
                return "call bet 1";
            } else {
                return "fold";
            }
        }

        if(computerAction.equals("bet 2")) {
            if(humanNumber >= 7) {
                return "raise 6";
            } else if (humanNumber == 5 || humanNumber == 6){
                return "call bet 2";
            } else {
                return "fold";
            }
        }

        if(computerAction.equals("bet 3")) {
            if(humanNumber >= 8) {
                return "raise 9";
            } else if (humanNumber == 6){
                return "call bet 3";
            } else {
                return "fold";
            }
        }

        if(computerAction.equals("raise all-in")) {
            if(humanNumber >= 7) {
                return "call all-in";
            } else {
                return "fold";
            }
        }

        return null;
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
