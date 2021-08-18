package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.botgame.MouseKeyboard;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.StatUtils;

import java.util.*;

/**
 * Created by LennartMac on 11/08/2021.
 */
public class BankrollSimulator {

    private double bankrollLimit_1;
    private double bankrollLimit_2;
    private double bankrollLimit_5;
    private double bankrollLimit_10;
    private double bankrollLimit_20;

    public static void main(String[] args) {
        new BankrollSimulator().compareBankrollStrategiesBasic();
    }

    //3, 5, 10, 20, 30, 40

    private void machineLearning() {
        Map<String, Double> results = new HashMap<>();

//        List<Double> allBankrollLimits_1 = Arrays.asList(3.0, 5.0, 10.0, 20.0, 30.0, 40.0);
//        List<Double> allBankrollLimits_2 = Arrays.asList(6.0, 10.0, 20.0, 40.0, 60.0, 80.0);
//        List<Double> allBankrollLimits_5 = Arrays.asList(15.0, 25.0, 50.0, 100.0, 150.0, 200.0);
//        List<Double> allBankrollLimits_10 = Arrays.asList(30.0, 50.0, 100.0, 200.0, 300.0, 400.0);
//        List<Double> allBankrollLimits_20 = Arrays.asList(60.0, 100.0, 200.0, 400.0, 600.0, 800.0);

        List<Double> allBankrollLimits_1 = Arrays.asList(30.0);
        List<Double> allBankrollLimits_2 = Arrays.asList(60.0);
        List<Double> allBankrollLimits_5 = Arrays.asList(100.0);
        List<Double> allBankrollLimits_10 = Arrays.asList(300.0, 400.0);
        List<Double> allBankrollLimits_20 = Arrays.asList(500.0, 800.0);

        int counter = 0;

        for(Double limit1 : allBankrollLimits_1) {
            for(Double limit2 : allBankrollLimits_2) {
                for(Double limit5 : allBankrollLimits_5) {
                    for(Double limit10 : allBankrollLimits_10) {
                        for(Double limit20 : allBankrollLimits_20) {
                            if(limit1 < limit2 && limit2 < limit5 && limit5 < limit10 && limit10 < limit20) {
                                bankrollLimit_1 = limit1;
                                bankrollLimit_2 = limit2;
                                bankrollLimit_5 = limit5;
                                bankrollLimit_10 = limit10;
                                bankrollLimit_20 = limit20;

                                double averageBankroll = getAverageBankroll(10000);

                                results.put("" + bankrollLimit_1 + "_" + bankrollLimit_2 + "_" + bankrollLimit_5 + "_"
                                        + bankrollLimit_10 + "_" + bankrollLimit_20, averageBankroll);

                                System.out.println(counter++);

//                                if(counter % 500 == 0) {
//                                    MouseKeyboard.moveMouseToLocation(20, 60);
//                                    MouseKeyboard.click(20, 60);
//                                    MouseKeyboard.moveMouseToLocation(500, 500);
//                                }


                            }
                        }
                    }
                }
            }
        }

        results = sortByValueHighToLow(results);

        for (Map.Entry<String, Double> entry : results.entrySet()) {
            System.out.println(entry.getKey() + "   " + entry.getValue());
        }
    }

    private double getAverageBankroll(int amountOfGames) {
        List<Double> bankrollResults = new ArrayList<>();

        for(int i = 0; i < amountOfGames; i++) {
            bankrollResults.add(testMachineLearningBankrollStyle(5000));
        }

        double average = getAverage(bankrollResults);
        return average;
    }

    private double getAverageSharpeRatio(int amountOfGames) {
        List<Double> bankrollResults = new ArrayList<>();

        for(int i = 0; i < amountOfGames; i++) {
            bankrollResults.add(testMachineLearningBankrollStyle(5000));
        }

        double average = getAverage(bankrollResults);
        double stdDev = getStandardDeviation(bankrollResults);
        double sharpe = average / stdDev;

        return sharpe;
    }

    private void compareBankrollStrategiesBasic() {
        List<Double> newStyleBankrolls = new ArrayList<>();
        List<Double> oldStyleBankrolls = new ArrayList<>();

        int counter = 0;

        for(int i = 0; i < 100_000; i++) {
            newStyleBankrolls.add(testNewBankrollStyle(5000));
            //oldStyleBankrolls.add(testOldBankrollStyle(5000));
            System.out.println(counter++);
        }

        Collections.sort(newStyleBankrolls);
        Collections.sort(oldStyleBankrolls);

        double stdDevNew = getStandardDeviation(newStyleBankrolls);
        double stdDevOld = getStandardDeviation(oldStyleBankrolls);

        double averageNew = getAverage(newStyleBankrolls);
        double averageOld = getAverage(oldStyleBankrolls);

        System.out.println("exp br: " + averageNew);
        System.out.println("std dev: " + stdDevNew);
        System.out.println("sharpe: " + (averageNew / stdDevNew));
    }

    private double testMachineLearningBankrollStyle(int gamesToPlay) {
        double bankroll = 100;

        for(int i = 0; i < gamesToPlay; i++) {
            double stakeToPlay = getStakeToPlayMachineLearning(bankroll);
            double profit = getProfitForGame(stakeToPlay);
            bankroll = bankroll + profit;

            if(bankroll < 1) {
                break;
            }
        }

        return bankroll;
    }

    private double testNewBankrollStyle(int gamesToPlay) {
        double bankroll = 100;

        for(int i = 0; i < gamesToPlay; i++) {
            double stakeToPlay = getStakeToPlayNew(bankroll);
            double profit = getProfitForGame(stakeToPlay);
            bankroll = bankroll + profit;

            if(bankroll < 1) {
                break;
            }

//            String bankrollString = "" + bankroll;
//            bankrollString = bankrollString.replace(".", ",");
//
//            System.out.println(bankrollString);
        }

        return bankroll;
    }

    private double testOldBankrollStyle(int gamesToPlay) {
        double bankroll = 100;
        double previousStakePlayed = 5;

        for(int i = 0; i < gamesToPlay; i++) {
            double stakeToPlay = getStakeToPlayOld(i, bankroll, previousStakePlayed);
            previousStakePlayed = stakeToPlay;
            double profit = getProfitForGame(stakeToPlay);
            bankroll = bankroll + profit;

            if(bankroll < 1) {
                break;
            }
        }

        return bankroll;
    }

    private double getStakeToPlayNew(double bankroll) {
        double stakeToPlay;

        //first version
//        if(bankroll <= 20) {
//            stakeToPlay = 1;
//        } else if(bankroll <= 40) {
//            stakeToPlay = 2;
//        } else if(bankroll <= 100) {
//            stakeToPlay = 5;
//        } else if(bankroll <= 150) {
//            stakeToPlay = 10;
//        } else if(bankroll <= 250) {
//            stakeToPlay = 20;
//        } else {
//            stakeToPlay = 50;
//        }

        //THIS IS GOING TO BE IT!!//
        if(bankroll <= 30) {
            stakeToPlay = 1;
        } else if(bankroll <= 60) {
            stakeToPlay = 2;
        } else if(bankroll <= 200) {
            stakeToPlay = 5;
        } else if(bankroll <= 300) {
            stakeToPlay = 10;
        } else if(bankroll <= 800) {
            stakeToPlay = 20;
        } else {
            stakeToPlay = 50;
        }
        //THIS IS GOING TO BE IT!!//

//        if(bankroll <= 50) {
//            stakeToPlay = 1;
//        } else if(bankroll <= 100) {
//            stakeToPlay = 1;
//        } else if(bankroll <= 120) {
//            stakeToPlay = 1;
//        } else if(bankroll <= 140) {
//            stakeToPlay = 1;
//        } else if(bankroll <= 200) {
//            stakeToPlay = 1;
//        } else {
//            stakeToPlay = 50;
//        }

        return stakeToPlay;
    }

    private double getStakeToPlayOld(int totalNumberOfGamesPlayed, double bankroll, double previousStakePlayed) {
        double stakeToPlay;

        if(totalNumberOfGamesPlayed % 40 == 0) {
//            if(bankroll <= 20) {
//                stakeToPlay = 1;
//            } else if(bankroll <= 40) {
//                stakeToPlay = 2;
//            } else if(bankroll <= 100) {
//                stakeToPlay = 5;
//            } else if(bankroll <= 200) {
//                stakeToPlay = 10;
//            } else if(bankroll <= 400) {
//                stakeToPlay = 20;
//            } else {
//                stakeToPlay = 50;
//            }

            //dit is goed
//            if(bankroll <= 40) {
//                stakeToPlay = 1;
//            } else if(bankroll <= 80) {
//                stakeToPlay = 2;
//            } else if(bankroll <= 200) {
//                stakeToPlay = 5;
//            } else if(bankroll <= 400) {
//                stakeToPlay = 10;
//            } else if(bankroll <= 1000) {
//                stakeToPlay = 20;
//            } else {
//                stakeToPlay = 50;
//            }

            if(bankroll <= 50) {
                stakeToPlay = 1;
            } else if(bankroll <= 100) {
                stakeToPlay = 2;
            } else if(bankroll <= 300) {
                stakeToPlay = 5;
            } else if(bankroll <= 600) {
                stakeToPlay = 10;
            } else if(bankroll <= 1500) {
                stakeToPlay = 20;
            } else {
                stakeToPlay = 50;
            }
        } else {
            stakeToPlay = previousStakePlayed;
        }

        return stakeToPlay;
    }

    private double getStakeToPlayMachineLearning(double bankroll) {
        double stakeToPlay;

        if(bankroll <= bankrollLimit_1) {
            stakeToPlay = 1;
        } else if(bankroll <= bankrollLimit_2) {
            stakeToPlay = 2;
        } else if(bankroll <= bankrollLimit_5) {
            stakeToPlay = 5;
        } else if(bankroll <= bankrollLimit_10) {
            stakeToPlay = 10;
        } else if(bankroll <= bankrollLimit_20) {
            stakeToPlay = 20;
        } else {
            stakeToPlay = 50;
        }

        return stakeToPlay;
    }

    private double getProfitForGame(double stake) {
        double profit = 0;

        if(stake == 1) {
            if(Math.random() < 0.56) {
                profit = 0.92;
            } else {
                profit = -1;
            }
        } else if(stake == 2) {
            if(Math.random() < 0.55) {
                profit = 1.9;
            } else {
                profit = -2;
            }
        } else if(stake == 5) {
            if(Math.random() < 0.535) {
                profit = 4.78;
            } else {
                profit = -5;
            }
        } else if(stake == 10) {
            if(Math.random() < 0.532) {
                profit = 9.58;
            } else {
                profit = -10;
            }
        } else if(stake == 20) {
            if(Math.random() < 0.5282) {
                profit = 19.16;
            } else {
                profit = -20;
            }
        } else if(stake == 50) {
            if(Math.random() < 0.525) {
                profit = 47.92;
            } else {
                profit = -50;
            }
        } else {
            System.out.println("Shouldnt come here - getProfitForGame()");
        }

        return profit;
    }

    private double getStandardDeviation(List<Double> values) {
        Double[] valuesAsArray = values.stream().toArray(Double[]::new);
        double[] valuesAsArrayDoublePrimitive = ArrayUtils.toPrimitive(valuesAsArray);
        double variance = StatUtils.variance(valuesAsArrayDoublePrimitive);
        double standardDeviation = Math.sqrt(variance);
        return standardDeviation;
    }

    private double getAverage(List<Double> values) {
        double total = 0;

        for(Double value : values) {
            total = total + value;
        }

        double average = total / values.size();
        return average;
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
