package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.util.List;

/**
 * Created by lpo21630 on 25-1-2018.
 */
public class Sizing {

    public double getAiBotSizing(double facingBetSize, double myBetSize, double myStack, double facingStack,
                                 double pot, double bigBlind, List<Card> board) {
        double sizing;

        if(board == null || board.isEmpty()) {
            sizing = getAiBotPreflopSizing(facingBetSize, myBetSize, facingStack, myStack, bigBlind);
        } else {
            sizing = getAiBotPostFlopSizing(board, facingBetSize, pot, myStack, facingStack, bigBlind);
        }

        if(sizing < 0) {
            sizing = 0;
        }

        return sizing;
    }

    public double getRuleBotSizing(double handStrength, double facingBetSize, double myBetSize, double facingStack,
                                   double myStack, double pot, List<Card> board) {
        double oddsToOffer = getOddsToOffer(handStrength, board);
        double sizing = getSizingGivenOdds(oddsToOffer, facingBetSize, myBetSize, facingStack, myStack, pot);
        return sizing;
    }

    private double getSizingGivenOdds(double odds, double facingBetSize, double myBetSize, double facingStack,
                                      double myStack, double pot) {
        double size = (facingBetSize + (odds * pot) + (odds * facingBetSize)) / (1 - odds);

        if(myStack <= 1.2 * pot) {
            size = myStack + myBetSize;
        }

        if(myStack < facingStack) {
            if(size > (myStack - myBetSize)) {
                size = myStack + myBetSize;
            }
        } else {
            if(size > (facingStack - facingBetSize)) {
                size = facingStack + facingBetSize;
            }
        }

        return size;
    }

    private double getOddsToOffer(double handStrength, List<Card> board) {
        double oddsToOffer;

        if(handStrength < 0.4 || handStrength > 0.9) {
            if(board == null || board.isEmpty()) {
                double random = Math.random();

                if(random < 0.20) {
                    //20%
                    oddsToOffer = 0.34;
                } else if(random < 0.40) {
                    //20%
                    oddsToOffer = 0.43;
                } else if(random < 0.60) {
                    //20%
                    oddsToOffer = 0.51;
                } else if(random < 0.80) {
                    //20%
                    oddsToOffer = 0.59;
                } else {
                    //20%
                    oddsToOffer = 0.67;
                }
            } else {
                double random = Math.random();

                if(random < 0.07) {
                    //7%
                    oddsToOffer = 0.17;
                } else if(random < 0.21) {
                    //14%
                    oddsToOffer = 0.33;
                } else if(random < 0.44) {
                    //23%
                    oddsToOffer = 0.43;
                } else if(random < 0.77) {
                    //33%
                    oddsToOffer = 0.51;
                } else if(random < 0.93) {
                    //16%
                    oddsToOffer = 0.59;
                } else {
                    //7%
                    oddsToOffer = 0.67;
                }
            }
        } else {
            if(board == null || board.isEmpty()) {
                double random = Math.random();

                if(random < 0.20) {
                    //20%
                    oddsToOffer = 0.34;
                } else if(random < 0.40) {
                    //20%
                    oddsToOffer = 0.43;
                } else if(random < 0.60) {
                    //20%
                    oddsToOffer = 0.51;
                } else if(random < 0.80) {
                    //20%
                    oddsToOffer = 0.59;
                } else {
                    //20%
                    oddsToOffer = 0.67;
                }
            } else {
                double random = Math.random();

                if(random < 0.05) {
                    //5%
                    oddsToOffer = 0.17;
                } else if(random < 0.52) {
                    //47%
                    oddsToOffer = 0.33;
                } else {
                    //48%
                    oddsToOffer = 0.43;
                }
            }
        }

        return oddsToOffer;
    }

    private double getAiBotPreflopSizing(double facingBetSize, double myBetSize, double facingStack, double myStack, double bigBlind) {
        double size;
        double computerTotalBetSizeInBb = myBetSize / bigBlind;
        double opponentTotalBetSizeInBb = facingBetSize / bigBlind;

        double potSizePlusAllBetsInBb =  computerTotalBetSizeInBb + opponentTotalBetSizeInBb;

        if(potSizePlusAllBetsInBb == 1.5) {
            size = 2.1 * bigBlind;
        } else if(potSizePlusAllBetsInBb == 2) {
            size = 3.5 * bigBlind;
        } else if(opponentTotalBetSizeInBb >= 2 && opponentTotalBetSizeInBb <= 4) {
            size = 3.4 * facingBetSize;
        } else if(opponentTotalBetSizeInBb > 4 && opponentTotalBetSizeInBb <= 16) {
            size = 2.25 * facingBetSize;
        } else {
            if((myStack / bigBlind) + computerTotalBetSizeInBb > (5.5 * (opponentTotalBetSizeInBb + computerTotalBetSizeInBb))) {
                size = 2.25 * facingBetSize;
            } else {
                size = myStack + myBetSize;
            }
        }

        size = adjustSizingToPotCommit(size, myStack, facingStack, facingBetSize, bigBlind);

        return size;
    }

    private double adjustSizingToPotCommit(double currentSizing, double myStack, double facingStack, double facingBetSize,
                                           double bigBlind) {
        double sizingToReturn = currentSizing;

        double potAfterCall = currentSizing * 2;
        double myStackAfterCall = myStack - currentSizing;
        double facingStackAfterCall = facingStack - (currentSizing - facingBetSize);
        double myStackToPotAfterCall = myStackAfterCall / potAfterCall;
        double facingStackToPotAfterCall = facingStackAfterCall / potAfterCall;

        if(myStackToPotAfterCall < 0.75 || facingStackToPotAfterCall < 0.75) {
            sizingToReturn = 5000 * bigBlind;
            System.out.println("Adjusted preflop sizing because of pot commitance");
            System.out.println("myStackToPotAfterCall: " + myStackToPotAfterCall);
            System.out.println("facingStackToPotAfterCall: " + facingStackToPotAfterCall);
        }

        return sizingToReturn;
    }

    private double getAiBotPostFlopSizing(List<Card> board, double facingBetSize, double pot, double myStack, double facingStack, double bigBlind) {
        double sizing = 0;

        if(board.size() == 3) {
            sizing = getFlopSizing(facingBetSize, pot, myStack, facingStack, bigBlind);
        } else if(board.size() == 4) {
            sizing = getTurnSizing(facingBetSize, pot, myStack, facingStack, bigBlind);
        } else if(board.size() == 5) {
            sizing = getRiverSizing(facingBetSize, pot, myStack, facingStack, bigBlind);
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
            flopSizing = botStack + (0.05 * botStack);
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
                flopSizing = calculateRaiseAmount(opponentBetSize, potSize, effectiveStack, 2.33, bigBlind);
            }
        }

        if(flopSizing > botStack) {
            flopSizing = 5000 * bigBlind;
        }

        return flopSizing;
    }

    private double getTurnSizing(double facingBetSize, double pot, double myStack, double facingStack, double bigBlind) {
        double turnSizing;

        double opponentBetSize = facingBetSize;
        double potSize = pot;
        double botStack = myStack;
        double opponentStack = facingStack;
        double effectiveStack = getEffectiveStack(botStack, opponentStack);

        if(botStack <= 1.2 * potSize) {
            turnSizing = botStack + (0.05 * botStack);
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
                turnSizing = calculateRaiseAmount(opponentBetSize, potSize, effectiveStack, 2.33, bigBlind);
            }
        }

        if(turnSizing > botStack) {
            turnSizing = 5000 * bigBlind;
        }

        return turnSizing;
    }

    private double getRiverSizing(double facingBetSize, double pot, double myStack, double facingStack, double bigBlind) {
        double riverSizing;

        double opponentBetSize = facingBetSize;
        double potSize = pot;
        double botStack = myStack;
        double opponentStack = facingStack;
        double effectiveStack = getEffectiveStack(botStack, opponentStack);

        if(opponentBetSize == 0) {
            if(botStack <= 1.2 * potSize) {
                riverSizing = botStack + (0.05 * botStack);
            } else {
                riverSizing = 0.75 * potSize;
            }
        } else {
            riverSizing = calculateRaiseAmount(opponentBetSize, potSize, effectiveStack, 2.33, bigBlind);
        }

        if(riverSizing > botStack) {
            riverSizing = 5000 * bigBlind;
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

    private double calculateRaiseAmount(double facingBetSize, double potSize, double effectiveStack, double odds, double bigBlind) {
        double raiseAmount = (potSize / (odds - 1)) + (((odds + 1) * facingBetSize) / (odds - 1));
        double potSizeAfterRaiseAndCall = potSize + raiseAmount + raiseAmount;
        double effectiveStackRemainingAfterRaise = effectiveStack - raiseAmount;

        if(effectiveStackRemainingAfterRaise / potSizeAfterRaiseAndCall < 0.51) {
            raiseAmount = 5000 * bigBlind;
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
