package com.lennart.model.action.actionbuilders.ai.opponenttypes;

/**
 * Created by lpo21630 on 11-1-2018.
 */
public class LooseAggressive {

    private String doAction(String aiBotAction, double handStrength, boolean strongDraw, double aiBotBetsizeBb,
                            double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb, boolean position) {
        String action = "";
        if(aiBotAction.contains("bet") || aiBotAction.contains("raise")) {
            doFoldCallRaiseAction(handStrength, strongDraw, aiBotBetsizeBb, ruleBotBetsizeBb, aiBotStackBb, ruleBotStackBb, position);
        } else {
            doCheckBetAction(handStrength, strongDraw, position);
        }
        return action;
    }

    private String doFoldCallRaiseAction(double handStrength, boolean strongDraw, double aiBotBetsizeBb,
                                         double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb,
                                         boolean position) {
        String action = null;
        double callAmountBb = getCallAmountBb(aiBotBetsizeBb, ruleBotBetsizeBb, ruleBotStackBb);

        if(aiBotStackBb == 0 || ((aiBotStackBb + aiBotBetsizeBb) <= ruleBotStackBb)) {
            if(callAmountBb > 40) {
                if(handStrength > 0.75) {
                    action = "call";
                } else {
                    if(handStrength > 0.34 && strongDraw) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else if(callAmountBb > 20) {
                if(strongDraw) {
                    action = "call";
                } else {
                    if(handStrength > 0.69) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else if(callAmountBb > 10) {
                if(strongDraw) {
                    action = "call";
                } else {
                    if(handStrength > 0.57) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else {
                if(strongDraw) {
                    action = "call";
                } else {
                    if(handStrength > 0.44) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            }
        } else {
            if(callAmountBb > 40) {
                if(strongDraw) {
                    double random = Math.random();

                    if(random > 0.67) {
                        action = "raise";
                    }
                }

                if(action == null && handStrength >= 0.82) {
                    action = "raise";
                }

                if(action == null && handStrength < 0.45) {
                    double random = Math.random();

                    if(random > 0.8) {
                        action = "raise";
                    }
                }

                if(action == null) {
                    if(handStrength > 0.75) {
                        action = "call";
                    } else {
                        if(handStrength > 0.34 && strongDraw) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                }
            } else if(callAmountBb > 20) {
                if(strongDraw) {
                    double random = Math.random();

                    if(random > 0.65) {
                        action = "raise";
                    }
                }

                if(action == null && handStrength >= 0.81) {
                    action = "raise";
                }

                if(action == null && handStrength < 0.45) {
                    double random = Math.random();

                    if(random > 0.8) {
                        action = "raise";
                    }
                }

                if(action == null) {
                    if(strongDraw) {
                        action = "call";
                    } else {
                        if(handStrength > 0.69) {
                            action = "call";
                        } else {
                            if(position) {
                                double random = Math.random();

                                if(random > 0.7) {
                                    action = "call";
                                } else {
                                    action = "fold";
                                }
                            } else {
                                action = "fold";
                            }
                        }
                    }
                }
            } else if(callAmountBb > 10) {
                if(strongDraw) {
                    double random = Math.random();

                    if(random > 0.5) {
                        action = "raise";
                    }
                }

                if(action == null && handStrength >= 0.77) {
                    action = "raise";
                }

                if(action == null && handStrength < 0.45) {
                    double random = Math.random();

                    if(random > 0.8) {
                        action = "raise";
                    }
                }

                if(action == null) {
                    if(strongDraw) {
                        action = "call";
                    } else {
                        if(handStrength > 0.57) {
                            action = "call";
                        } else {
                            if(position) {
                                double random = Math.random();

                                if(random > 0.65) {
                                    action = "call";
                                } else {
                                    action = "fold";
                                }
                            } else {
                                action = "fold";
                            }
                        }
                    }
                }
            } else {
                if(strongDraw) {
                    double random = Math.random();

                    if(random > 0.47) {
                        action = "raise";
                    }
                }

                if(action == null && handStrength >= 0.72) {
                    action = "raise";
                }

                if(action == null && handStrength < 0.45) {
                    double random = Math.random();

                    if(random > 0.8) {
                        action = "raise";
                    }
                }

                if(action == null) {
                    if(strongDraw) {
                        action = "call";
                    } else {
                        if(handStrength > 0.44) {
                            action = "call";
                        } else {
                            if(position) {
                                double random = Math.random();

                                if(random > 0.50) {
                                    action = "call";
                                } else {
                                    action = "fold";
                                }
                            } else {
                                action = "fold";
                            }
                        }
                    }
                }
            }
        }
        return action;
    }

    private String doCheckBetAction(double handStrength, boolean strongDraw, boolean position) {
        String action = null;

        if(strongDraw) {
            action = "bet75pct";
        }

        if(action == null) {
            if(handStrength >= 0.5 && handStrength < 0.6) {
                double betSizeBb = getBetSizeBb();

                if(betSizeBb < 10) {
                    action = "bet75pct";
                } else if(betSizeBb < 20) {
                    double random = Math.random();

                    if(random > 0.8) {
                        action = "bet75pct";
                    }
                } else if(betSizeBb < 30) {
                    double random = Math.random();

                    if(random > 0.93) {
                        action = "bet75pct";
                    }
                }
            } else if(handStrength < 0.7) {
                double betSizeBb = getBetSizeBb();

                if(betSizeBb < 10) {
                    action = "bet75pct";
                } else if(betSizeBb < 20) {
                    double random = Math.random();

                    if(random > 0.5) {
                        action = "bet75pct";
                    }
                } else if(betSizeBb < 30) {
                    double random = Math.random();

                    if(random > 0.75) {
                        action = "bet75pct";
                    }
                } else {
                    double random = Math.random();

                    if(random > 0.9) {
                        action = "bet75pct";
                    }
                }
            } else if(handStrength < 0.8) {
                double betSizeBb = getBetSizeBb();

                if(betSizeBb < 20) {
                    action = "bet75pct";
                } else if(betSizeBb < 40) {
                    double random = Math.random();

                    if(random > 0.5) {
                        action = "bet75pct";
                    }
                } else {
                    double random = Math.random();

                    if(random > 0.8) {
                        action = "bet75pct";
                    }
                }
            } else {
                action = "bet75pct";
            }
        }

        if(action == null) {
            double random = Math.random();

            if(position) {
                if(random > 0.58) {
                    action = "bet75pct";
                }
            } else {
                if(random > 0.85) {
                    action = "bet75pct";
                }
            }
        }

        if(action == null) {
            action = "check";
        }

        return action;
    }

    private double getCallAmountBb(double aiBotBetSizeBb, double ruleBotBetSizeBb, double ruleBotStackBb) {
        double callAmount = aiBotBetSizeBb - ruleBotBetSizeBb;

        if(callAmount > ruleBotStackBb) {
            callAmount = ruleBotStackBb;
        }

        return callAmount;
    }

    private double getBetSizeBb() {
        return 0;
    }
}
