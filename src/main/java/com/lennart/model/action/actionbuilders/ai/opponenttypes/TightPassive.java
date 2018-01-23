package com.lennart.model.action.actionbuilders.ai.opponenttypes;

/**
 * Created by lpo21630 on 11-1-2018.
 */
public class TightPassive {

    public String doAction(String aiBotAction, double handStrength, boolean strongDraw, double aiBotBetsizeBb,
                           double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb, boolean position, boolean preflop) {
        String action;

        if(preflop) {
            action = doPreflopAction(handStrength, aiBotBetsizeBb, ruleBotBetsizeBb, aiBotStackBb,
                    ruleBotStackBb, position);
        } else {
            action = doPostflopAction(aiBotAction, handStrength, strongDraw, aiBotBetsizeBb, ruleBotBetsizeBb,
                    aiBotStackBb, ruleBotStackBb);
        }

        return action;
    }

    private String doPreflopAction(double handStrength, double aiBotBetsizeBb,
                                   double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb, boolean position) {
        String action;

        if(position) {
            if(aiBotBetsizeBb == 1) {
                if(handStrength > 0.10) {
                    double random = Math.random();

                    if(random > 0.20) {
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

                    if(random > 0.20) {
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
                } else if(handStrength > 0.59) {
                    double random = Math.random();

                    if(random > 0.3) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                } else {
                    double random = Math.random();

                    if(random > 0.8) {
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

                                if(random > 0.20) {
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
                            } else {
                                action = "fold";
                            }
                        }
                    } else {
                        if (handStrength >= 0.95) {
                            action = "raise";
                        } else {
                            action = "fold";
                        }
                    }
                }
            }
        } else {
            if(aiBotBetsizeBb == 1) {
                if(handStrength > 0.95) {
                    action = "raise";
                } else {
                    action = "check";
                }
            } else if(aiBotBetsizeBb <= 5) {
                if(handStrength >= 0.8) {
                    double random = Math.random();

                    if(random < 0.7) {
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

                    if(random < 0.01) {
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

                        if(random < 0.01) {
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

                        if(random < 0.01) {
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
                if(aiBotStackBb == 0 || ((aiBotStackBb + aiBotBetsizeBb) <= ruleBotStackBb)) {
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
                        if(handStrength > 0.96) {
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
                                    double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb) {
        String action;
        if(aiBotAction.contains("bet") || aiBotAction.contains("raise")) {
            action = doFoldCallRaiseAction(handStrength, strongDraw, aiBotBetsizeBb, ruleBotBetsizeBb, aiBotStackBb, ruleBotStackBb);
        } else {
            action = doCheckBetAction(handStrength, strongDraw);
        }
        return action;
    }

    private String doFoldCallRaiseAction(double handStrength, boolean strongDraw, double aiBotBetsizeBb,
                                         double ruleBotBetsizeBb, double aiBotStackBb, double ruleBotStackBb) {
        String action;
        double callAmountBb = getCallAmountBb(aiBotBetsizeBb, ruleBotBetsizeBb, ruleBotStackBb);

        if(aiBotStackBb == 0 || ((aiBotStackBb + aiBotBetsizeBb) <= ruleBotStackBb)) {
            if(callAmountBb > 70) {
                if(handStrength > 0.89) {
                    action = "call";
                } else {
                    if(handStrength > 0.80 && strongDraw) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else if(callAmountBb > 40) {
                if(handStrength > 0.85) {
                    action = "call";
                } else {
                    if(handStrength > 0.75 && strongDraw) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else if(callAmountBb > 20) {
                if(handStrength > 0.83) {
                    action = "call";
                } else {
                    if(handStrength > 0.73 && strongDraw) {
                        action = "call";
                    } else {
                        action = "fold";
                    }
                }
            } else if(callAmountBb > 10) {
                if(handStrength > 0.77) {
                    action = "call";
                } else {
                    if(handStrength > 0.67 && strongDraw) {
                        action = "call";
                    } else {
                        if(strongDraw) {
                            double random = Math.random();

                            if(random < 0.3) {
                                action = "call";
                            } else {
                                action = "fold";
                            }
                        } else {
                            action = "fold";
                        }
                    }
                }
            } else {
                if(handStrength > 0.63) {
                    action = "call";
                } else {
                    if(handStrength > 0.40 && strongDraw) {
                        action = "call";
                    } else {
                        if(strongDraw) {
                            double random = Math.random();

                            if(random < 0.67) {
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
            if(callAmountBb > 70) {
                if(handStrength >= 0.96) {
                    action = "raise";
                } else {
                    if(handStrength > 0.89) {
                        action = "call";
                    } else {
                        if(handStrength > 0.80 && strongDraw) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                }
            } else if(callAmountBb > 40) {
                if(handStrength >= 0.96) {
                    action = "raise";
                } else {
                    if(handStrength > 0.85) {
                        action = "call";
                    } else {
                        if(handStrength > 0.75 && strongDraw) {
                            action = "call";
                        } else {
                            action = "fold";
                        }
                    }
                }
            } else if(callAmountBb > 20) {
                if(handStrength >= 0.95) {
                    action = "raise";
                } else {
                    if(handStrength > 0.83) {
                        action = "call";
                    } else {
                        if(handStrength > 0.73 && strongDraw) {
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
                    if(handStrength > 0.77) {
                        action = "call";
                    } else {
                        if(handStrength > 0.67 && strongDraw) {
                            action = "call";
                        } else {
                            if(strongDraw) {
                                double random = Math.random();

                                if(random < 0.3) {
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
                if(handStrength >= 0.95) {
                    action = "raise";
                } else {
                    if(handStrength > 0.63) {
                        action = "call";
                    } else {
                        if(handStrength > 0.40 && strongDraw) {
                            action = "call";
                        } else {
                            if(strongDraw) {
                                double random = Math.random();

                                if(random < 0.67) {
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

    private String doCheckBetAction(double handStrength, boolean strongDraw) {
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
                double random = Math.random();

                if(random < 0.75) {
                    action = "check";
                } else {
                    action ="bet75pct";
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
}
