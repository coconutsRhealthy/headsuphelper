package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by lpo21630 on 25-1-2018.
 */
public class Sizing {

    public double getAiBotSizing(double facingBetSize, double myBetSize, double myStack, double facingStack,
                                 double pot, double bigBlind, List<Card> board) {
        if(board == null || board.isEmpty()) {
            return getAiBotPreflopSizing(facingBetSize, myBetSize, facingStack, myStack, bigBlind);
        } else {
            return getAiBotPostFlopSizing(board, facingBetSize, pot, myStack, facingStack, bigBlind);
        }
    }


//    public static void main(String[] args) {
//        for(int i = 0; i < 30; i++) {
//            System.out.println(new Sizing().getRuleBotSizing(0, 50, 0.5));
//        }
//    }

    public double getRuleBotSizing(double ruleBotSizingThusFar, double effectiveStack, double bigBlind) {
        List<Double> ruleBotOptions = getRuleBotOptions(effectiveStack, bigBlind);
        double chosenOption = pickOption(ruleBotOptions);
        double bbSizing = getSizingFromChosenOption(chosenOption);
        return ruleBotSizingThusFar + (bbSizing * bigBlind);
    }

    private List<Double> getRuleBotOptions(double effectiveStack, double bigBlind) {
        double effectiveStackBb = effectiveStack / bigBlind;

        List<Double> options = new ArrayList<>();

        if(effectiveStackBb <= 5) {
            options.add(5.0);
        } else if(effectiveStackBb <= 10) {
            options.add(5.0);
            options.add(10.0);
        } else if(effectiveStackBb <= 15) {
            options.add(5.0);
            options.add(10.0);
            options.add(15.0);
        } else if(effectiveStackBb <= 20) {
            options.add(5.0);
            options.add(10.0);
            options.add(15.0);
            options.add(20.0);
        } else if(effectiveStackBb <= 25) {
            options.add(5.0);
            options.add(10.0);
            options.add(15.0);
            options.add(20.0);
            options.add(25.0);
        } else if(effectiveStackBb <= 40) {
            options.add(5.0);
            options.add(10.0);
            options.add(15.0);
            options.add(20.0);
            options.add(25.0);
            options.add(40.0);
        } else if(effectiveStackBb <= 60) {
            options.add(5.0);
            options.add(10.0);
            options.add(15.0);
            options.add(20.0);
            options.add(25.0);
            options.add(40.0);
            options.add(60.0);
        } else if(effectiveStackBb <= 100) {
            options.add(5.0);
            options.add(10.0);
            options.add(15.0);
            options.add(20.0);
            options.add(25.0);
            options.add(40.0);
            options.add(60.0);
            options.add(100.0);
        } else if(effectiveStackBb <= 150) {
            options.add(5.0);
            options.add(10.0);
            options.add(15.0);
            options.add(20.0);
            options.add(25.0);
            options.add(40.0);
            options.add(60.0);
            options.add(100.0);
            options.add(150.0);
        } else {
            options.add(5.0);
            options.add(10.0);
            options.add(15.0);
            options.add(20.0);
            options.add(25.0);
            options.add(40.0);
            options.add(60.0);
            options.add(100.0);
            options.add(150.0);
            options.add(effectiveStackBb);
        }

        return options;
    }

    private double pickOption(List<Double> options) {
        int size = options.size();

        Random rn = new Random();
        int maximum = size - 1;
        int minimum = 0;
        int range = maximum - minimum + 1;
        int randomNum = rn.nextInt(range) + minimum;

        return options.get(randomNum);
    }

    private double getSizingFromChosenOption(double chosenOption) {
        double bbsToBet;

        if(chosenOption == 5.0) {
            bbsToBet = pickNumberBetween(0, 5);
        } else if(chosenOption == 10.0) {
            bbsToBet = pickNumberBetween(5, 10);
        } else if(chosenOption == 15.0) {
            bbsToBet = pickNumberBetween(10, 15);
        } else if(chosenOption == 20.0) {
            bbsToBet = pickNumberBetween(15, 20);
        } else if(chosenOption == 25.0) {
            bbsToBet = pickNumberBetween(20, 25);
        } else if(chosenOption == 40.0) {
            bbsToBet = pickNumberBetween(25, 40);
        } else if(chosenOption == 60.0) {
            bbsToBet = pickNumberBetween(40, 60);
        } else if(chosenOption == 100.0) {
            bbsToBet = pickNumberBetween(60, 100);
        } else if(chosenOption == 150.0) {
            bbsToBet = pickNumberBetween(100, 150);
        } else {
            bbsToBet = pickNumberBetween(150, (int) chosenOption);
        }

        return bbsToBet;
    }

    private double pickNumberBetween(int downRange, int upRange) {
        if(downRange == 0.0) {
            downRange++;
        }

        return ThreadLocalRandom.current().nextDouble(downRange, upRange);
    }





    //helper methods

    //preflop

    private double getRuleBotPreflopSizing() {




        return 0;
    }

    private double getAiBotPreflopSizing(double facingBetSize, double myBetSize, double facingStack, double myStack, double bigBlind) {
        double size;
        double computerTotalBetSizeInBb = myBetSize / bigBlind;
        double opponentTotalBetSizeInBb = facingBetSize / bigBlind;

        double potSizePlusAllBetsInBb =  computerTotalBetSizeInBb + opponentTotalBetSizeInBb;

        if(potSizePlusAllBetsInBb == 1.5) {
            size = 2.5 * bigBlind;
        } else if(potSizePlusAllBetsInBb == 2) {
            size = 3.5 * bigBlind;
        } else if(potSizePlusAllBetsInBb > 2 && potSizePlusAllBetsInBb <= 4) {
            size = 3.2 * facingBetSize;
        } else if(potSizePlusAllBetsInBb > 4 && potSizePlusAllBetsInBb <= 16) {
            size = 2.25 * facingBetSize;
        } else {
            size = myStack + myBetSize;
        }

        if(size > (facingStack - facingBetSize)) {
            size = facingStack + facingBetSize;
        }

        return size;
    }








    //postflop

    private double getRuleBotPostFlopSizing() {


        return 0;
    }

    private double getAiBotPostFlopSizing(List<Card> board, double facingBetSize, double pot, double myStack, double facingStack, double bigBlind) {
        double sizing = 0;

        if(board.size() == 3) {
            sizing = getFlopSizing(facingBetSize, pot, myStack, facingStack, bigBlind);
        } else if(board.size() == 4) {
            sizing = getTurnSizing(facingBetSize, pot, myStack, facingStack);
        } else if(board.size() == 5) {
            sizing = getRiverSizing(facingBetSize, pot, myStack, facingStack);
        }

        if(sizing > (facingStack - facingBetSize)) {
            sizing = facingStack + facingBetSize;
        }

        return sizing;
    }

    private double getFlopSizing(double facingBetSize, double pot, double myStack, double facingStack, double bigBlind) {
        double flopSizing;

        double opponentBetSize = facingBetSize;
        double potSize = pot;
        double potSizeBb = potSize / bigBlind;
        double botStack = myStack;
        double opponentStack = facingStack;
        double effectiveStack = getEffectiveStack(botStack, opponentStack);

        if(botStack <= 1.2 * potSize) {
            flopSizing = botStack;
        } else {
            if(opponentBetSize == 0) {
                if(potSizeBb <= 8) {
                    flopSizing = 0.75 * potSize;
                } else if(potSizeBb > 8 && potSizeBb <= 24) {
                    double flopBetPercentage = getFlopBetPercentage(effectiveStack, potSize, 0.7, 0.75);

                    if(flopBetPercentage < 0.37) {
                        flopBetPercentage = 0.5;
                    }
                    if(flopBetPercentage > 0.75) {
                        flopBetPercentage = 0.75;
                    }

                    flopSizing = flopBetPercentage * potSize;
                } else {
                    double flopBetPercentage = getFlopBetPercentage(effectiveStack, potSize, 0.33, 0.51);

                    if(flopBetPercentage < 0.2) {
                        flopBetPercentage = 0.2;
                    }
                    if(flopBetPercentage > 0.75) {
                        flopBetPercentage = 0.75;
                    }

                    flopSizing = flopBetPercentage * potSize;
                }
            } else {
                flopSizing = calculateRaiseAmount(opponentBetSize, potSize, effectiveStack, botStack, 2.33);
            }
        }
        if(flopSizing > botStack) {
            flopSizing = botStack;
        }

        return flopSizing;
    }

    private double getTurnSizing(double facingBetSize, double pot, double myStack, double facingStack) {
        double turnSizing;

        double opponentBetSize = facingBetSize;
        double potSize = pot;
        double botStack = myStack;
        double opponentStack = facingStack;
        double effectiveStack = getEffectiveStack(botStack, opponentStack);

        if(botStack <= 1.2 * potSize) {
            turnSizing = botStack;
        } else {
            if(opponentBetSize == 0) {
                double turnBetPercentage3bet = getTurnBetPercentage(effectiveStack, potSize, 0.75);
                double turnBetPercentage4bet = getTurnBetPercentage(effectiveStack, potSize, 0.51);

                if(turnBetPercentage3bet > 0.75) {
                    turnSizing = 0.75 * potSize;
                } else if(turnBetPercentage3bet > 0.5) {
                    turnSizing = turnBetPercentage3bet * potSize;
                } else if(turnBetPercentage3bet > 0.4) {
                    turnSizing = getTurnBetPercentage(effectiveStack, potSize, 0.67) * potSize;
                } else if(turnBetPercentage4bet > 0.2) {
                    turnSizing = turnBetPercentage4bet * potSize;
                } else {
                    turnSizing = 0.2 * potSize;
                }
            } else {
                turnSizing = calculateRaiseAmount(opponentBetSize, potSize, effectiveStack, botStack, 2.33);
            }
        }
        if(turnSizing > botStack) {
            turnSizing = botStack;
        }

        return turnSizing;
    }

    private double getRiverSizing(double facingBetSize, double pot, double myStack, double facingStack) {
        double riverSizing;

        double opponentBetSize = facingBetSize;
        double potSize = pot;
        double botStack = myStack;
        double opponentStack = facingStack;
        double effectiveStack = getEffectiveStack(botStack, opponentStack);

        if(opponentBetSize == 0) {
            if(botStack <= 1.2 * potSize) {
                riverSizing = botStack;
            } else {
                riverSizing = 0.75 * potSize;
            }
        } else {
            riverSizing = calculateRaiseAmount(opponentBetSize, potSize, effectiveStack, botStack, 2.33);
        }
        if(riverSizing > botStack) {
            riverSizing = botStack;
        }

        return riverSizing;
    }

    private double getEffectiveStack(double botStack, double opponentStack) {
        if(botStack > opponentStack) {
            return opponentStack;
        } else {
            return botStack;
        }
    }

    private double calculateRaiseAmount(double facingBetSize, double potSize, double effectiveStack, double botStack, double odds) {
        double raiseAmount = (potSize / (odds - 1)) + (((odds + 1) * facingBetSize) / (odds - 1));
        double potSizeAfterRaiseAndCall = potSize + raiseAmount + raiseAmount;
        double effectiveStackRemainingAfterRaise = effectiveStack - raiseAmount;

        if(effectiveStackRemainingAfterRaise / potSizeAfterRaiseAndCall < 0.51) {
            raiseAmount = botStack;
        }
        return raiseAmount;
    }

    private double getFlopBetPercentage(double effectiveStackSize, double potSize, double turnBetPercentage, double riverBetPercentage) {
        double flopBetPercentage;

        double s = effectiveStackSize;
        double p = potSize;
        double t = turnBetPercentage;
        double r = riverBetPercentage;

        flopBetPercentage = (s - (2 * t * p * r) - (p * r) - (t * p)) / ((4 * t * p * r) + (2 * p * r) + (2 * t * p) + p);

        if(flopBetPercentage <= 0) {
            return 0;
        } else {
            return flopBetPercentage;
        }
    }

    private double getTurnBetPercentage(double effectiveStackSize, double potSize, double riverBetPercentage) {
        double turnBetPercentage;

        double s = effectiveStackSize;
        double p = potSize;
        double r = riverBetPercentage;

        turnBetPercentage = (s - (r * p)) / ((2 * r * p) + p);

        if (turnBetPercentage <= 0) {
            return 0;
        } else {
            return turnBetPercentage;
        }
    }
}
