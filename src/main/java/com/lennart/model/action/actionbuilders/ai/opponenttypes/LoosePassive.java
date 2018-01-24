package com.lennart.model.action.actionbuilders.ai.opponenttypes;

import com.lennart.model.card.Card;

import java.util.List;

/**
 * Created by lpo21630 on 12-1-2018.
 */
public class LoosePassive {

    public String doAction(String aiBotAction, double handStrength, boolean strongDraw, double aiBotBetsizeBb,
                           double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb, boolean position,
                           boolean preflop, List<Card> board) {
        String action;

        if(preflop) {
            action = doPreflopAction(handStrength, aiBotBetsizeBb, ruleBotBetsizeBb, aiBotStackBb, ruleBotStackBb, position);
        } else {
            action = doPostflopAction(aiBotAction, handStrength, strongDraw, aiBotBetsizeBb, ruleBotBetsizeBb,
                    aiBotStackBb, ruleBotStackBb, board, position);
        }

        return action;
    }

    private String doPreflopAction(double handStrength, double aiBotBetsizeBb, double ruleBotBetsizeBb,
                                   double aiBotStackBb, double ruleBotStackBb, boolean position) {
        String action;

        if(position) {
            if(aiBotBetsizeBb == 1) {
                if(handStrength > 0.8) {
                    action = "raise";
                } else {
                    double random = Math.random();

                    if(random > 0.65) {
                        action = "raise";
                    } else {
                        action = "call";
                    }
                }
            } else if(aiBotBetsizeBb < 12) {
                if(handStrength >= 0.95) {
                    double random = Math.random();

                    if(random > 0.30) {
                        action = "raise";
                    } else {
                        action = "call";
                    }
                } else if(handStrength > 0.8) {
                    double random = Math.random();

                    if(random > 0.97) {
                        action = "raise";
                    } else {
                        action = "call";
                    }
                } else if(handStrength > 0.50) {
                    double random = Math.random();

                    if(random > 0.97) {
                        action = "raise";
                    } else {
                        action = "call";
                    }
                } else {
                    double random = Math.random();

                    if(random > 0.99) {
                        action = "raise";
                    } else if(random > 0.64) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else {
                if(aiBotStackBb == 0 || ((aiBotStackBb + aiBotBetsizeBb) <= ruleBotStackBb)) {
                    double callAmountBb = getCallAmountBb(aiBotBetsizeBb, ruleBotBetsizeBb, ruleBotStackBb);

                    if(callAmountBb < 20) {
                        if(ruleBotBetsizeBb >= 10) {
                            action = "call";
                        } else {
                            if(handStrength > 0.51) {
                                action = "call";
                            } else {
                                action = "fold";
                            }
                        }
                    } else if(callAmountBb < 50) {
                        if(ruleBotBetsizeBb >= 20) {
                            if(handStrength >= 0.51) {
                                action = "call";
                            } else {
                                action = "fold";
                            }
                        } else {
                            if(handStrength >= 0.7) {
                                action = "call";
                            } else {
                                action = "fold";
                            }
                        }
                    } else {
                        if(handStrength >= 0.75) {
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

                                if(random > 0.30) {
                                    action = "raise";
                                } else {
                                    action = "call";
                                }
                            } else if(handStrength > 0.55) {
                                action = "call";
                            } else {
                                double random = Math.random();

                                if(random < 0.02) {
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
                                if (handStrength > 0.65) {
                                    action = "call";
                                } else {
                                    action = "fold";
                                }
                            }
                        } else {
                            if (handStrength >= 0.95) {
                                action = "raise";
                            } else if(handStrength > 0.75) {
                                action = "call";
                            } else {
                                action = "fold";
                            }
                        }
                    } else {
                        if (handStrength >= 0.95) {
                            action = "raise";
                        } else if(handStrength > 0.80) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                }
            }
        } else {
            if(aiBotBetsizeBb == 1) {
                if(handStrength > 0.85) {
                    action = "raise";
                } else {
                    action = "check";
                }
            } else if(aiBotBetsizeBb <= 5) {
                if(handStrength >= 0.8) {
                    double random = Math.random();

                    if(random < 0.60) {
                        action = "call";
                    } else {
                        action = "raise";
                    }
                } else if(handStrength >= 0.5) {
                    double random = Math.random();

                    if(random < 0.05) {
                        action = "raise";
                    } else {
                        action = "call";
                    }
                } else {
                    double random = Math.random();

                    if (random > 0.7) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else if(aiBotBetsizeBb <= 25) {
                double callAmount = getCallAmountBb(aiBotBetsizeBb, ruleBotBetsizeBb, ruleBotStackBb);

                if(callAmount < ruleBotStackBb) {
                    if(handStrength > 0.95) {
                        double random = Math.random();

                        if(random < 0.30) {
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

                        if(random > 0.37) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    } else {
                        double random = Math.random();

                        if(random < 0.01) {
                            action = "raise";
                        } else if(random < 0.32) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                } else {
                    if(handStrength > 0.50) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else {
                if(aiBotStackBb == 0 || ((aiBotStackBb + aiBotBetsizeBb) <= ruleBotStackBb)) {
                    if(ruleBotStackBb <= 20) {
                        if(handStrength > 0.5) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    } else if(ruleBotStackBb <= 40){
                        if(handStrength > 0.6) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    } else {
                        if(handStrength > 0.75) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                } else {
                    if(ruleBotStackBb <= 20) {
                        if(handStrength > 0.90) {
                            action = "raise";
                        } else if(handStrength > 0.5) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    } else if(ruleBotStackBb <= 40){
                        if(handStrength > 0.95) {
                            action = "raise";
                        } else if(handStrength > 0.56) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    } else {
                        if(handStrength > 0.95) {
                            action = "raise";
                        } else if(handStrength > 0.70) {
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
                                    double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb, List<Card> board,
                                    boolean position) {
        String action;

        if(aiBotAction != null && (aiBotAction.contains("bet") || aiBotAction.contains("raise"))) {
            action = doPostflopFoldCallRaiseAction(handStrength, strongDraw, aiBotBetsizeBb, ruleBotBetsizeBb, aiBotStackBb, ruleBotStackBb);
        } else {
            action = doPostflopCheckBetAction(handStrength, strongDraw, board, position);
        }

        return action;
    }

    private String doPostflopFoldCallRaiseAction(double handStrength, boolean strongDraw, double aiBotBetsizeBb,
                                                 double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb) {
        String action = null;
        double callAmountBb = getCallAmountBb(aiBotBetsizeBb, ruleBotBetsizeBb, ruleBotStackBb);

        if(aiBotStackBb == 0 || ((aiBotStackBb + aiBotBetsizeBb) <= ruleBotStackBb)) {
            if(strongDraw) {
                action = "call";
            }

            if(action == null) {
                if(callAmountBb > 40) {
                    if(handStrength > 0.71) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                } else if(callAmountBb > 20) {
                    if(handStrength > 0.60) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                } else if(callAmountBb > 10) {
                    if(handStrength > 0.50) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                } else {
                    if(handStrength > 0.40) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            }
        } else {
            if(callAmountBb > 40) {
                if(handStrength >= 0.97) {
                    action = "raise";
                } else {
                    if(strongDraw) {
                        action = "call";
                    } else {
                        if(handStrength > 0.71) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                }
            } else if(callAmountBb > 20) {
                if(handStrength >= 0.96) {
                    action = "raise";
                } else {
                    if(strongDraw) {
                        action = "call";
                    } else {
                        if(handStrength > 0.60) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                }
            } else if(callAmountBb > 10) {
                if(handStrength >= 0.95) {
                    action = "raise";
                } else {
                    if(strongDraw) {
                        action = "call";
                    } else {
                        if(handStrength > 0.50) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                }
            } else {
                if(handStrength >= 0.94) {
                    action = "raise";
                } else {
                    if(strongDraw) {
                        action = "call";
                    } else {
                        if(handStrength > 0.40) {
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

    private String doPostflopCheckBetAction(double handStrength, boolean strongDraw, List<Card> board, boolean position) {
        String action = null;

        if(strongDraw) {
            double random = Math.random();

            if(random < 0.05) {
                action = "bet75pct";
            }
        }

        if(action == null) {
            if(handStrength < 0.6) {
                action = "check";
            } else if(handStrength < 0.7) {
                double random = Math.random();

                if(random < 0.90) {
                    action = "check";
                } else {
                    action ="bet75pct";
                }
            } else if(handStrength < 0.8) {
                double random = Math.random();

                if(random < 0.85) {
                    action = "check";
                } else {
                    action ="bet75pct";
                }
            } else if(handStrength < 0.9) {
                double random = Math.random();

                if(random < 0.80) {
                    action = "check";
                } else {
                    action ="bet75pct";
                }
            } else {
                if(board.size() == 5 && position) {
                    action = "bet75pct";
                } else {
                    double random = Math.random();

                    if(random < 0.75) {
                        action = "check";
                    } else {
                        action ="bet75pct";
                    }
                }
            }
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
