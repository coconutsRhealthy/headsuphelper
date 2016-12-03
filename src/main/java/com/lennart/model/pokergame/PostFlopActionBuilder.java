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
    private final String _1BET = "1bet";
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
            return getIpFCheck(handStrengthAgainstRange);
        }
        //facing 1bet
        if(HandPath.getHandPath().contains("F1bet")) {
            return getIpF1bet(handStrengthAgainstRange);
        }
        //facing 2bet
        if(HandPath.getHandPath().contains("F2bet")) {
            return getIpF2bet(handStrengthAgainstRange);
        }
        //facing 3bet
        if(HandPath.getHandPath().contains("F3bet")) {
            return getIpF3bet(handStrengthAgainstRange);
        }
        return null;
    }

    private String getOopAction(double handStrengthAgainstRange) {
        //first to act
        if(!HandPath.getHandPath().contains("F")) {
            return getOopFirstToAct(handStrengthAgainstRange);
        }

        //facing 1bet
        if(HandPath.getHandPath().contains("F1bet")) {
            return getOopF1bet(handStrengthAgainstRange);
        }

        //facing 2bet

        //facing 3bet
        return null;
    }

    private String getIpFCheck(double handStrengthAgainstRange) {
        if(handStrengthAgainstRange > 0.6) {
            return getValueAction(_1BET, CHECK);
        }

        String drawAction = getDrawAction(_1BET, CHECK);

        if(drawAction != null) {
            return drawAction;
        } else {
            return getBluffAction(_1BET, CHECK);
        }
    }

    private String getIpF1bet(double handStrengthAgainstRange) {
        if(handStrengthAgainstRange > 0.7) {
            return getValueAction(_2BET, CALL_1_BET);
        } else {
            if(handStrengthAgainstRange > handEvaluator.getHandStrengthNeededToCall()) {
                //nog toevoegen, de calls met draws

                return CALL_1_BET;
            } else {
                return getBluffAction(_2BET, FOLD);
            }
        }
    }

    private String getIpF2bet(double handStrengthAgainstRange) {
        if(handStrengthAgainstRange > handEvaluator.getHandStrengthNeededToCall()) {
            return CALL_2_BET;
        } else {
            //hier nog toevoegen: de calls met draws

            return FOLD;
        }
    }

    private String getIpF3bet(double handStrengthAgainstRange) {
        if(handStrengthAgainstRange > handEvaluator.getHandStrengthNeededToCall()) {
            return CALL_2_BET;
        } else {
            //hier nog toevoegen: de calls met draws

            return FOLD;
        }
    }

    private String getOopFirstToAct(double handStrengthAgainstRange) {
        if(myLastActionWasCall()) {
            return CHECK;
        } else {
            if (handStrengthAgainstRange > 0.65) {
                getValueAction(_1BET, CHECK);
            }

            String drawAction = getDrawAction(_1BET, CHECK);

            if(drawAction != null) {
                return drawAction;
            } else {
                return getBluffAction(_1BET, CHECK);
            }
        }
    }

    private String getOopF1bet(double handStrengthAgainstRange) {
        return null;
    }

    private String getIpFCheckValue() {
        if(!Game.getStreet().equals("river")) {
            if(Math.random() < 0.8) {
                return _1BET;
            } else {
                return CHECK;
            }
        } else {
            //hier nog onderscheid maken tussen hoeveel bets al gedaan zijn? Tighter bij grotere pot..
            return _1BET;
        }
    }

    private String getValueAction(String bettingAction, String passiveAction) {
        if(!Game.getStreet().equals("river")) {
            if(Math.random() < 0.8) {
                return bettingAction;
            } else {
                return passiveAction;
            }
        } else {
            //hier nog onderscheid maken tussen hoeveel bets al gedaan zijn? Tighter bij grotere pot..
            return bettingAction;
        }
    }

    private String getDrawAction(String bettingAction, String passiveAction) {
        if (youHaveStrongFdOrSd) {
            if(Math.random() < 0.75) {
                return bettingAction;
            } else {
                return passiveAction;
            }
        } else if (youHaveStrongGutshot) {
            if(Math.random() < 0.68) {
                return bettingAction;
            } else {
                return passiveAction;
            }
        } else if (youHaveMediumFdOrSd) {
            if(Math.random() < 0.5) {
                return bettingAction;
            } else {
                return passiveAction;
            }
        }
        return null;
    }

    private String getBluffAction(String bettingAction, String passiveAction) {
        int numberOfArrivedDraws = boardEvaluator.getNumberOfArrivedDraws();
        int numberOfArrivedDrawsInYourPerceivedRange =
                handEvaluator.getNumberOfArrivedDrawsInYourPerceivedRange();
        double percentageOfYourPerceivedRangeThatHitsFlopRanks =
                handEvaluator.getPercentageOfYourPerceivedRangeThatHitsFlopRanks();
        double percentageOfYourPerceivedRangeThatHitsNewCard =
                handEvaluator.getPercentageOfYourPerceivedRangeThatHitsNewCard();

        if(numberOfArrivedDraws > 10 && numberOfArrivedDrawsInYourPerceivedRange > (numberOfArrivedDraws / 3)) {
            if(Math.random() < 0.8) {
                return bettingAction;
            }
        }

        if(Game.getStreet().equals("flop")) {
            if(percentageOfYourPerceivedRangeThatHitsFlopRanks > 0.5) {
                if(Math.random() < 0.8) {
                    return bettingAction;
                }
            }
        } else {
            if(percentageOfYourPerceivedRangeThatHitsNewCard > 0.5) {
                if(Math.random() < 0.8) {
                    return bettingAction;
                }
            }

        }
        return passiveAction;
    }



    private boolean myLastActionWasCall() {
        //TODO: implement this method
        return false;
    }

}
