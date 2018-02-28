package com.lennart.model.action.actionbuilders.ai.opponenttypes;

import com.lennart.model.card.Card;

import java.util.List;

/**
 * Created by lpo21630 on 12-1-2018.
 */
public class TightAggressive extends AbstractOpponent {

    private double potSizeBb;
    private double ruleBotStackBb;

    public TightAggressive(double potSizeBb, double ruleBotStackBb) {
        this.potSizeBb = potSizeBb;
        this.ruleBotStackBb = ruleBotStackBb;
    }

    @Override
    public String doAction(String aiBotAction, double handStrength, boolean strongDraw, double aiBotBetsizeBb,
                           double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb, boolean position,
                           boolean preflop, List<Card> board, double facingOdds) {
        String action;

        if(preflop) {
            action = doPreflopAction(handStrength, aiBotBetsizeBb, ruleBotBetsizeBb, aiBotStackBb,
                    ruleBotStackBb, position);
        } else {
            action = doPostflopAction(aiBotAction, handStrength, strongDraw, aiBotBetsizeBb, ruleBotBetsizeBb,
                    aiBotStackBb, ruleBotStackBb, position);
        }

        return action;
    }

    private String doPreflopAction(double handStrength, double aiBotBetsizeBb, double ruleBotBetsizeBb,
                                   double aiBotStackBb, double ruleBotStackBb, boolean position) {
        String action;

        if(position) {
            if(aiBotBetsizeBb == 1) {
                if(handStrength > 0.10) {
                    double random = Math.random();

                    if(random > 0.05) {
                        action = "raise";
                    } else {
                        action = "call";
                    }
                } else {
                    action = "fold";
                }
            } else if(aiBotBetsizeBb < 12) {
                if(handStrength >= 0.95) {
                    double random = Math.random();

                    if(random > 0.05) {
                        action = "raise";
                    } else {
                        action = "call";
                    }
                } else if(handStrength > 0.8) {
                    double random = Math.random();

                    if(random > 0.9) {
                        action = "raise";
                    } else {
                        action = "call";
                    }
                } else if(handStrength > 0.54) {
                    double random = Math.random();

                    if(random > 0.3) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                } else {
                    double random = Math.random();

                    if(random > 0.95) {
                        action = "raise";
                    } else if(random > 0.8) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else {
                if(aiBotStackBb == 0 || ((ruleBotStackBb + ruleBotBetsizeBb) <= aiBotBetsizeBb)) {
                    double callAmountBb = getCallAmountBb(aiBotBetsizeBb, ruleBotBetsizeBb, ruleBotStackBb);

                    if(callAmountBb < 20) {
                        if(ruleBotBetsizeBb >= 10) {
                            action = "call";
                        } else {
                            if(handStrength > 0.59) {
                                action = "call";
                            } else {
                                action = "fold";
                            }
                        }
                    } else if(callAmountBb < 50) {
                        if(ruleBotBetsizeBb >= 20) {
                            if(handStrength >= 0.65) {
                                action = "call";
                            } else {
                                action = "fold";
                            }
                        } else {
                            if(handStrength >= 0.83) {
                                action = "call";
                            } else {
                                action = "fold";
                            }
                        }
                    } else {
                        if(handStrength >= 0.87) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                } else {
                    double callAmountBb = getCallAmountBb(aiBotBetsizeBb, ruleBotBetsizeBb, ruleBotStackBb);

                    if(callAmountBb < 20) {
                        if(ruleBotBetsizeBb >= 10) {
                            action = "call";
                        } else {
                            if(handStrength > 0.95) {
                                double random = Math.random();

                                if(random > 0.05) {
                                    action = "raise";
                                } else {
                                    action = "call";
                                }
                            } else if(handStrength > 0.82) {
                                action = "call";
                            } else {
                                double random = Math.random();

                                if(random < 0.05) {
                                    action = "raise";
                                } else {
                                    action = "fold";
                                }
                            }
                        }
                    } else if(callAmountBb < 50) {
                        if(ruleBotBetsizeBb >= 20) {
                            if (handStrength >= 0.95) {
                                action = "raise";
                            } else {
                                if (handStrength > 0.80) {
                                    action = "call";
                                } else {
                                    action = "fold";
                                }
                            }
                        } else {
                            if (handStrength >= 0.95) {
                                action = "raise";
                            } else if(handStrength > 0.85) {
                                action = "call";
                            } else {
                                action = "fold";
                            }
                        }
                    } else {
                        if (handStrength >= 0.95) {
                            action = "raise";
                        } else if(handStrength > 0.88) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                }
            }
        } else {
            if(aiBotBetsizeBb == 1) {
                if(handStrength > 0.80) {
                    action = "raise";
                } else {
                    action = "check";
                }
            } else if(aiBotBetsizeBb <= 5) {
                if(handStrength >= 0.8) {
                    double random = Math.random();

                    if(random < 0.13) {
                        action = "call";
                    } else {
                        action = "raise";
                    }
                } else if(handStrength >= 0.5) {
                    double random = Math.random();

                    if(random < 0.15) {
                        action = "raise";
                    } else {
                        action = "call";
                    }
                } else {
                    double random = Math.random();

                    if(random < 0.05) {
                        action = "raise";
                    } else {
                        action = "fold";
                    }
                }
            } else if(aiBotBetsizeBb <= 25) {
                double callAmount = getCallAmountBb(aiBotBetsizeBb, ruleBotBetsizeBb, ruleBotStackBb);

                if(callAmount < ruleBotStackBb) {
                    if(handStrength > 0.95) {
                        double random = Math.random();

                        if(random < 0.05) {
                            action = "call";
                        } else {
                            action = "raise";
                        }
                    } else if(handStrength > 0.8) {
                        double random = Math.random();

                        if(random < 0.05) {
                            action = "raise";
                        } else {
                            action = "call";
                        }
                    } else if(handStrength > 0.7) {
                        double random = Math.random();

                        if(random > 0.8) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    } else {
                        double random = Math.random();

                        if(random < 0.05) {
                            action = "raise";
                        } else {
                            action = "fold";
                        }
                    }
                } else {
                    if(handStrength > 0.65) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else {
                if(aiBotStackBb == 0 || ((ruleBotStackBb + ruleBotBetsizeBb) <= aiBotBetsizeBb)) {
                    if(ruleBotStackBb <= 20) {
                        if(handStrength > 0.6) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    } else if(ruleBotStackBb <= 40){
                        if(handStrength > 0.7) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    } else {
                        if(handStrength > 0.85) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                } else {
                    if(ruleBotStackBb <= 20) {
                        if(handStrength > 0.6) {
                            action = "raise";
                        } else {
                            action = "fold";
                        }
                    } else if(ruleBotStackBb <= 40){
                        if(handStrength > 0.8) {
                            action = "raise";
                        } else if(handStrength > 0.7) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    } else {
                        if(handStrength > 0.91) {
                            action = "raise";
                        } else if(handStrength > 0.85) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                }
            }
        }

        return action;
    }

    private String doPostflopAction(String aiBotAction, double handStrength, boolean strongDraw, double aiBotBetsizeBb,
                                    double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb, boolean position) {
        String action;

        if(aiBotAction != null && (aiBotAction.contains("bet") || aiBotAction.contains("raise"))) {
            action = doPostflopFoldCallRaiseAction(handStrength, strongDraw, aiBotBetsizeBb, ruleBotBetsizeBb, aiBotStackBb, ruleBotStackBb, position);
        } else {
            action = doPostflopCheckBetAction(handStrength, strongDraw, position);
        }

        return action;
    }

    private String doPostflopFoldCallRaiseAction(double handStrength, boolean strongDraw, double aiBotBetsizeBb,
                                                 double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb,
                                                 boolean position) {
        String action = null;
        double callAmountBb = getCallAmountBb(aiBotBetsizeBb, ruleBotBetsizeBb, ruleBotStackBb);

        if(aiBotStackBb == 0 || ((ruleBotStackBb + ruleBotBetsizeBb) <= aiBotBetsizeBb)) {
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

    private String doPostflopCheckBetAction(double handStrength, boolean strongDraw, boolean position) {
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
            } else if(handStrength >= 0.7 && handStrength < 0.8) {
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
            } else if(handStrength >= 0.8 && handStrength < 0.9) {
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

        if(betAmount > ruleBotStackBb) {
            betAmount = ruleBotStackBb;
        }

        return betAmount;
    }
}
