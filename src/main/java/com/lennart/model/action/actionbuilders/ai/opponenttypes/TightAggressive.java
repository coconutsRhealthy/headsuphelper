package com.lennart.model.action.actionbuilders.ai.opponenttypes;

/**
 * Created by lpo21630 on 12-1-2018.
 */
public class TightAggressive {

    private double potSizeBb;
    private double ruleBotStack;

    public TightAggressive(double potSizeBb, double ruleBotStack) {
        this.potSizeBb = potSizeBb;
        this.ruleBotStack = ruleBotStack;
    }

    public String doAction(String aiBotAction, double handStrength, boolean strongDraw, double aiBotBetsizeBb,
                            double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb, boolean position) {
        String action;
        if(aiBotAction.contains("bet") || aiBotAction.contains("raise")) {
            action = doFoldCallRaiseAction(handStrength, strongDraw, aiBotBetsizeBb, ruleBotBetsizeBb, aiBotStackBb, ruleBotStackBb, position);
        } else {
            action = doCheckBetAction(handStrength, strongDraw, position);
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
                if(handStrength > 0.83) {
                    action = "call";
                } else {
                    if(handStrength > 0.67 && strongDraw) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else if(callAmountBb > 20) {
                if(strongDraw && handStrength > 0.57) {
                    action = "call";
                } else {
                    if(handStrength > 0.77) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else if(callAmountBb > 10) {
                if(strongDraw) {
                    action = "call";
                } else {
                    if(handStrength > 0.72) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else {
                if(strongDraw) {
                    action = "call";
                } else {
                    if(handStrength > 0.58) {
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

                    if(random > 0.95) {
                        action = "raise";
                    }
                }

                if(action == null && handStrength >= 0.93) {
                    action = "raise";
                }

                if(action == null && handStrength < 0.45) {
                    double random = Math.random();

                    if(random > 0.98) {
                        action = "raise";
                    }
                }

                if(action == null) {
                    if(handStrength > 0.83) {
                        action = "call";
                    } else {
                        if(handStrength > 0.67 && strongDraw) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                }
            } else if(callAmountBb > 20) {
                if(strongDraw) {
                    double random = Math.random();

                    if(random > 0.93) {
                        action = "raise";
                    }
                }

                if(action == null && handStrength >= 0.91) {
                    action = "raise";
                }

                if(action == null && handStrength < 0.45) {
                    double random = Math.random();

                    if(random > 0.97) {
                        action = "raise";
                    }
                }

                if(action == null) {
                    if(strongDraw) {
                        action = "call";
                    } else {
                        if(handStrength > 0.77) {
                            action = "call";
                        } else {
                            if(position) {
                                double random = Math.random();

                                if(random > 0.92) {
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

                    if(random > 0.85) {
                        action = "raise";
                    }
                }

                if(action == null && handStrength >= 0.90) {
                    action = "raise";
                }

                if(action == null && handStrength < 0.45) {
                    double random = Math.random();

                    if(random > 0.95) {
                        action = "raise";
                    }
                }

                if(action == null) {
                    if(strongDraw) {
                        action = "call";
                    } else {
                        if(handStrength > 0.72) {
                            action = "call";
                        } else {
                            if(position) {
                                double random = Math.random();

                                if(random > 0.85) {
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

                    if(random > 0.75) {
                        action = "raise";
                    }
                }

                if(action == null && handStrength >= 0.85) {
                    action = "raise";
                }

                if(action == null && handStrength < 0.45) {
                    double random = Math.random();

                    if(random > 0.95) {
                        action = "raise";
                    }
                }

                if(action == null) {
                    if(strongDraw) {
                        action = "call";
                    } else {
                        if(handStrength > 0.58) {
                            action = "call";
                        } else {
                            if(position) {
                                double random = Math.random();

                                if(random > 0.80) {
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
            double betSizeBb = getBetSizeBb();

            if(betSizeBb < 28) {
                action = "bet75pct";
            }
        }

        if(action == null) {
            if(handStrength >= 0.57 && handStrength < 0.7) {
                double betSizeBb = getBetSizeBb();

                if(betSizeBb < 10) {
                    if(position) {
                        action = "bet75pct";
                    } else {
                        double random = Math.random();

                        if(random > 0.9) {
                            action = "bet75pct";
                        }
                    }
                } else if(betSizeBb < 20) {
                    if(position) {
                        double random = Math.random();

                        if(random > 0.8) {
                            action = "bet75pct";
                        }
                    }
                }
            } else if(handStrength < 0.8) {
                double betSizeBb = getBetSizeBb();

                if(betSizeBb < 10) {
                    action = "bet75pct";
                } else if(betSizeBb < 20) {
                    if(position) {
                        action = "bet75pct";
                    } else {
                        double random = Math.random();

                        if(random > 0.55) {
                            action = "bet75pct";
                        }
                    }
                } else if(betSizeBb < 30) {
                    if(position) {
                        double random = Math.random();

                        if(random > 0.5) {
                            action = "bet75pct";
                        }
                    }
                } else {
                    if(position) {
                        double random = Math.random();

                        if(random > 0.93) {
                            action = "bet75pct";
                        }
                    }
                }
            } else if(handStrength < 0.9) {
                double betSizeBb = getBetSizeBb();

                if(betSizeBb < 40) {
                    action = "bet75pct";
                }
            } else if(handStrength >= 0.9) {
                action = "bet75pct";
            }
        }

        if(action == null) {
            double betSizeBb = getBetSizeBb();
            double random = Math.random();

            if(betSizeBb < 25) {
                if(position) {
                    if(random > 0.88) {
                        action = "bet75pct";
                    }
                } else {
                    if(random > 0.99) {
                        action = "bet75pct";
                    }
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
        double betAmount = 0.75 * potSizeBb;

        if(betAmount > ruleBotStack) {
            betAmount = ruleBotStack;
        }

        return betAmount;
    }
}
