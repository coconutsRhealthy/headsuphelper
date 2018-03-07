package com.lennart.model.action.actionbuilders.ai;

/**
 * Created by LennartMac on 01/03/2018.
 */
public class RuleApplier {

    public String moderateBluffingAndRandomizeValue(String action, double handStrength, String street, boolean position, boolean strongDraw) {
        String actionToReturn;

        if(handStrength < 0.6) {
            if(action.equals("bet75pct") && !strongDraw) {
                double random = Math.random();

                if(random < 0.2) {
                    actionToReturn = action;
                } else {
                    actionToReturn = "check";
                }
            } else {
                actionToReturn = action;
            }
        } else if(handStrength > 0.8) {
            if(action.equals("bet75pct")) {
                double random = Math.random();

                if(random > 0.2) {
                    actionToReturn = action;
                } else {
                    if(street.equals("river") && position) {
                        //river ip value bet
                        actionToReturn = action;
                    } else {
                        actionToReturn = "check";
                    }
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }
        return actionToReturn;
    }

    public String moderateBluffRaises(String action, double handStrength, String street, boolean strongDraw, double opponentBetSizeBb) {
        String actionToReturn;

        if(action.equals("raise")) {
            if(street.equals("flopOrTurn")) {
                if(!strongDraw) {
                    if(handStrength < 0.5) {
                        if(opponentBetSizeBb > 4) {
                            actionToReturn = "fold";
                        } else {
                            actionToReturn = action;
                        }
                    } else {
                        actionToReturn = action;
                    }
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String randomizePre3betAction(String action, String route, double handStrength, double myBetSizeBb) {
        String actionToReturn;

        if(route.contains("StreetPreflop") && route.contains("PositionBB")) {
            if(myBetSizeBb == 1) {
                if(action.equals("raise")) {
                    double random = Math.random();

                    if(random > 0.20) {
                        if(handStrength < 0.35) {
                            actionToReturn = "fold";
                        } else {
                            if(handStrength >= 0.8) {
                                double random2 = Math.random();

                                if(random2 > 0.2) {
                                    actionToReturn = action;
                                } else {
                                    actionToReturn = "call";
                                }
                            } else {
                                actionToReturn = "call";
                            }
                        }
                    } else {
                        actionToReturn = action;
                    }
                } else {
                    actionToReturn = action;
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String moderateIpOpenPre(String action, String route, double handStrength, double myBetSizeBb) {
        String actionToReturn = action;

        if(route.contains("StreetPreflop") && route.contains("PositionBTN")) {
            if(myBetSizeBb == 0.5) {
                if(action.equals("fold")) {
                    if(handStrength > 0.15) {
                        actionToReturn = "raise";
                    }
                } else if(action.equals("call")) {
                    if(handStrength >= 0.9) {
                        actionToReturn = "raise";
                    }
                }
            }
        }

        return actionToReturn;
    }

}