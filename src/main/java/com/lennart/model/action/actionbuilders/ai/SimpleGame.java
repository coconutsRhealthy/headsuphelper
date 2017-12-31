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

        for(int i = 0; i < 15000; i++) {
            if(i == 14777) {
                System.out.println("wacht");
            }
            simpleGame.playOneGame(i);
        }
        System.out.println("wacht");
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

        payoffMap.get(1).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(1).put("bet 3", Arrays.asList(0.0, 0.0));

        payoffMap.get(2).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(2).put("bet 3", Arrays.asList(0.0, 0.0));

        payoffMap.get(3).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(3).put("bet 3", Arrays.asList(0.0, 0.0));

        payoffMap.get(4).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(4).put("bet 3", Arrays.asList(0.0, 0.0));

        payoffMap.get(5).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(5).put("bet 3", Arrays.asList(0.0, 0.0));

        payoffMap.get(6).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(6).put("bet 3", Arrays.asList(0.0, 0.0));

        payoffMap.get(7).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(7).put("bet 3", Arrays.asList(0.0, 0.0));

        payoffMap.get(8).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(8).put("bet 3", Arrays.asList(0.0, 0.0));

        payoffMap.get(9).put("check", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("bet 1", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("bet 2", Arrays.asList(0.0, 0.0));
        payoffMap.get(9).put("bet 3", Arrays.asList(0.0, 0.0));

        return payoffMap;
    }

    private void playOneGame(int gameNumber) {
        //System.out.println();
        //System.out.println("STARTING A NEW GAME");
        //System.out.println();

        int humanNumber = getRandomNumber();
        int computerNumber = getRandomNumber();

        int humanStack = 3;
        int computerStack = 3;

        int pot = 2;

        String computerAction = doComputerAction(computerNumber, gameNumber);
        String humanAction = doHumanAction(humanNumber, computerAction);

        if(humanAction.equals("call")) {
            if(computerAction.equals("bet 1")) {
                humanStack = 2;
                computerStack = 2;
                pot = 4;
            } else if(computerAction.equals("bet 2")) {
                humanStack = 1;
                computerStack = 1;
                pot = 6;
            } else if(computerAction.equals("bet 3")) {
                humanStack = 0;
                computerStack = 0;
                pot = 8;
            }
        }

        String winner = determineWinner(humanAction, computerAction, humanNumber, computerNumber);
        List<Integer> stacks = allocatePotToWinnerStack(winner, humanStack, computerStack, pot);

        humanStack = stacks.get(0);
        computerStack = stacks.get(1);

        //System.out.println("Human stack at end of game is: " + humanStack);
        //System.out.println("Computer stack at end of game is: " + computerStack);

        updateScores(humanStack, computerStack);
        updatePayoffMap(computerNumber, computerAction, computerStack);
    }

    private int getRandomNumber() {
        Random rn = new Random();
        return rn.nextInt(9 - 1 + 1) + 1;
    }

    private void updateScores(int humanStack, int computerStack) {
        humanScore = humanScore + (humanStack - 4);
        computerScore = computerScore + (computerStack - 4);

//        System.out.println();
//        System.out.println("*******************");
//        System.out.println("Human total score: " + humanScore);
//        System.out.println("Computer total score " + computerScore);
//        System.out.println("*******************");
//        System.out.println();
    }

    private String doComputerAction(int computerNumber, int numberOfGames) {
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

    private void updatePayoffMap(int computerNumber, String computerAction, int computerStack) {
        double payoffThisGame = computerStack - 4.0;
        double oldPayoffValue = payoffMap.get(computerNumber).get(computerAction).get(0);
        double gamesUntillNow = payoffMap.get(computerNumber).get(computerAction).get(1);
        gamesUntillNow++;

        payoffMap.get(computerNumber).get(computerAction).set(0, (oldPayoffValue + payoffThisGame));
        payoffMap.get(computerNumber).get(computerAction).set(1, gamesUntillNow);
    }

    private String determineWinner(String humanAction, String computerAction, int humanNumber, int computerNumber) {
        String winner = "";

        if(humanAction.equals("fold")) {
            winner = "computer";
        }

        if(computerAction.equals("check") || humanAction.equals("call")) {
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

//        if(humanNumber <= 3) {
//            return "fold";
//        } else if(humanNumber == 4) {
//            if(computerAction.equals("bet 1")) {
//                return "call";
//            } else {
//                return "fold";
//            }
//        } else if(humanNumber == 5 || humanNumber == 6) {
//            if(computerAction.equals("bet 1") || computerAction.equals("bet 2")) {
//                return "call";
//            } else {
//                return "fold";
//            }
//        } else {
//            return "call";
//        }

        if(humanNumber < 6) {
            return "fold";
        } else {
            return "call";
        }




//        System.out.println("You have: " + humanNumber);
//        System.out.println("Computer does: " + computerAction);
//        System.out.println("What would you like to do?");
//        System.out.println();
//
//        if(computerAction.equals("check")) {
//            System.out.println("a) check");
//        } else {
//            System.out.println("a) fold");
//            System.out.println("b) call");
//        }
//
//        Scanner scan = new Scanner(System.in);
//        String input = scan.nextLine();
//        String humanAction = null;
//
//        while(humanAction == null) {
//            if(computerAction.equals("check")) {
//                if(input.equals("a")) {
//                    humanAction = "check";
//                } else {
//                    System.out.println("You did a not allowed action, try again");
//                    input = scan.nextLine();
//                }
//            } else {
//                if(input.equals("a")) {
//                    humanAction = "fold";
//                } else if(input.equals("b")) {
//                    humanAction = "call";
//                } else {
//                    System.out.println("You did a not allowed action, try again");
//                    input = scan.nextLine();
//                }
//            }
//        }
//
//        System.out.println();
//        System.out.println("you did: " + humanAction);
//
//        return humanAction;
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
