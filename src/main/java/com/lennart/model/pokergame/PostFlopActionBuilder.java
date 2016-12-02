package com.lennart.model.pokergame;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;

import java.util.Map;
import java.util.Set;

/**
 * Created by LPO21630 on 2-12-2016.
 */
public class PostFlopActionBuilder {

    private final String FOLD = "fold";
    private final String CHECK = "check";
    private final String BET = "1bet";
    private final String _2BET = "2bet";
    private final String CALL_1_BET = "call1bet";
    private final String CALL_2_BET = "call2bet";

    private boolean youHaveStrongFdOrSd;
    private boolean youHaveStrongGutshot;
    private boolean youHaveMediumFdOrSd;

    private BoardEvaluator boardEvaluator = new BoardEvaluator();
    private HandEvaluator handEvaluator = new HandEvaluator();

    public String getAction(Map<Integer, Set<Set<Card>>> opponentRange) {
        double handStrengthAgainstRange = handEvaluator.getHandStrengthAgainstRange(Game.getHoleCards(), opponentRange);

        if(Game.getPosition().equals("IP")) {
            return getIpAction(handStrengthAgainstRange);
        }

        if(Game.getPosition().equals("OOP")) {
            return getOopAction(handStrengthAgainstRange);
        }
        return null;
    }

    private String getIpAction(double handStrengthAgainstRange) {
        //facing check
        if(HandPath.getHandPath().contains("Fcheck")) {
            if(handStrengthAgainstRange > 0.6) {
                if(!Game.getStreet().equals("river")) {
                    if(Math.random() < 0.8) {
                        return BET;
                    } else {
                        return CHECK;
                    }
                } else {
                    //hier nog onderscheid maken tussen hoeveel bets al gedaan zijn? Tighter bij grotere pot..
                    return BET;
                }
            } else if (youHaveStrongFdOrSd) {
                if(Math.random() < 0.75) {
                    return BET;
                } else {
                    return CHECK;
                }
            } else if (youHaveStrongGutshot) {
                if(Math.random() < 0.68) {
                    return BET;
                } else {
                    return CHECK;
                }
            } else if (youHaveMediumFdOrSd) {
                if(Math.random() < 0.5) {
                    return BET;
                } else {
                    return CHECK;
                }
            } else {
                //de bluffs
                int numberOfArrivedDraws = boardEvaluator.getNumberOfArrivedDraws();
                int numberOfArrivedDrawsInYourPerceivedRange =
                        handEvaluator.getNumberOfArrivedDrawsInYourPerceivedRange();
                double percentageOfYourPerceivedRangeThatHitsFlopRanks =
                        handEvaluator.getPercentageOfYourPerceivedRangeThatHitsFlopRanks();
                double percentageOfYourPerceivedRangeThatHitsNewCard =
                        handEvaluator.getPercentageOfYourPerceivedRangeThatHitsNewCard();



                if(numberOfArrivedDraws > 10 && numberOfArrivedDrawsInYourPerceivedRange > (numberOfArrivedDraws / 3)) {
                    if(Math.random() < 0.8) {
                        return BET;
                    }
                }

                if(Game.getStreet().equals("flop")) {
                    if(percentageOfYourPerceivedRangeThatHitsFlopRanks > 0.5) {
                        if(Math.random() < 0.8) {
                            return BET;
                        }
                    }
                } else {
                    if(percentageOfYourPerceivedRangeThatHitsNewCard > 0.5) {
                        if(Math.random() < 0.8) {
                            return BET;
                        }
                    }

                }
                return CHECK;
            }
        }
        //facing 1bet
        if(HandPath.getHandPath().contains("F1bet")) {
            if(handStrengthAgainstRange > 0.7) {
                if(!Game.getStreet().equals("river")) {
                    if(Math.random() < 0.8) {
                        return _2BET;
                    } else {
                        return CALL_1_BET;
                    }
                } else {
                    //hier nog onderscheid maken tussen hoeveel bets al gedaan zijn? Tighter bij grotere pot..
                    return _2BET;
                }
            } else {
                if(handStrengthAgainstRange > handEvaluator.getHandStrengthNeededToCall()) {
                    return CALL_1_BET;
                } else {
                    //de bluff 2bets...
                    int numberOfArrivedDraws = boardEvaluator.getNumberOfArrivedDraws();
                    int numberOfArrivedDrawsInYourPerceivedRange =
                            handEvaluator.getNumberOfArrivedDrawsInYourPerceivedRange();
                    double percentageOfYourPerceivedRangeThatHitsFlopRanks =
                            handEvaluator.getPercentageOfYourPerceivedRangeThatHitsFlopRanks();
                    double percentageOfYourPerceivedRangeThatHitsNewCard =
                            handEvaluator.getPercentageOfYourPerceivedRangeThatHitsNewCard();


                    if(numberOfArrivedDraws > 10 && numberOfArrivedDrawsInYourPerceivedRange > (numberOfArrivedDraws / 3)) {
                        if(Math.random() < 0.8) {
                            return _2BET;
                        }
                    }

                    if(Game.getStreet().equals("flop")) {
                        if(percentageOfYourPerceivedRangeThatHitsFlopRanks > 0.5) {
                            if(Math.random() < 0.8) {
                                return _2BET;
                            }
                        }
                    }

                    if(!Game.getStreet().equals("flop")) {
                        if(percentageOfYourPerceivedRangeThatHitsNewCard > 0.5) {
                            if(Math.random() < 0.8) {
                                return _2BET;
                            }
                        }
                    }
                    return FOLD;
                }
            }


        }
        //facing 2bet
        if(HandPath.getHandPath().contains("F2bet")) {
            if(handStrengthAgainstRange > handEvaluator.getHandStrengthNeededToCall()) {
                return CALL_2_BET;
            } else {
                //hier nog toevoegen: de calls met draws

                return FOLD;
            }
        }
        //facing 3bet
        if(HandPath.getHandPath().contains("F3bet")) {
            if(handStrengthAgainstRange > handEvaluator.getHandStrengthNeededToCall()) {
                return CALL_2_BET;
            } else {
                //hier nog toevoegen: de calls met draws

                return FOLD;
            }
        }
        return null;
    }

    private String getOopAction(double handStrengthAgainstRange) {
        //first to act
        if(!HandPath.getHandPath().contains("F")) {
            if(myLastActionWasCall()) {
                return CHECK;
            } else {
                if (handStrengthAgainstRange > 0.65) {
                    if(!Game.getStreet().equals("river")) {
                        if(Math.random() < 0.8) {
                            return BET;
                        } else {
                            return CHECK;
                        }
                    } else {
                        //hier nog onderscheid maken tussen hoeveel bets al gedaan zijn? Tigher bij grotere pot..
                        return BET;
                    }
                } else if (youHaveStrongFdOrSd) {
                    if(Math.random() < 0.75) {
                        return BET;
                    } else {
                        return CHECK;
                    }
                } else if (youHaveStrongGutshot) {
                    if(Math.random() < 0.68) {
                        return BET;
                    } else {
                        return CHECK;
                    }
                } else if (youHaveMediumFdOrSd) {
                    if (Math.random() < 0.5) {
                        return BET;
                    } else {
                        return CHECK;
                    }
                } else {
                    //de bluffs
                    int numberOfArrivedDraws = boardEvaluator.getNumberOfArrivedDraws();
                    int numberOfArrivedDrawsInYourPerceivedRange =
                            handEvaluator.getNumberOfArrivedDrawsInYourPerceivedRange();
                    double percentageOfYourPerceivedRangeThatHitsFlopRanks =
                            handEvaluator.getPercentageOfYourPerceivedRangeThatHitsFlopRanks();
                    double percentageOfYourPerceivedRangeThatHitsNewCard =
                            handEvaluator.getPercentageOfYourPerceivedRangeThatHitsNewCard();


                    if(numberOfArrivedDraws > 10 && numberOfArrivedDrawsInYourPerceivedRange > (numberOfArrivedDraws / 3)) {
                        if(Math.random() < 0.8) {
                            return BET;
                        }
                    }

                    if(Game.getStreet().equals("flop")) {
                        if(percentageOfYourPerceivedRangeThatHitsFlopRanks > 0.5) {
                            if(Math.random() < 0.8) {
                                return BET;
                            }
                        }
                    }

                    if(!Game.getStreet().equals("flop")) {
                        if(percentageOfYourPerceivedRangeThatHitsNewCard > 0.5) {
                            if(Math.random() < 0.8) {
                                return BET;
                            }
                        }
                    }

                    return CHECK;
                }
            }
        }

        //facing 1bet

        //facing 2bet

        //facing 3bet
        return null;
    }

    private boolean myLastActionWasCall() {
        //TODO: implement this method
        return false;
    }

}
