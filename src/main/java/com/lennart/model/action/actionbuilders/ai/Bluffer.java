package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;
import com.lennart.model.card.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Bluffer {

    private Connection con;

    public String doOpponentBluffSuccessAction(String action, String opponentName, double bigBlind, double handStrength,
                                                List<Card> board, boolean opponentHasInitiative, double facingBetSize,
                                                double myBetSize, double myStack, double facingStack, double pot) throws Exception {
        String actionToReturn;

        if(board != null && board.size() == 5) {
            System.out.println("zzz1");
            if((action.equals("check") && !opponentHasInitiative) || action.equals("fold")) {
                System.out.println("zzz2");
                if(handStrength < 0.64) {
                    System.out.println("zzz3");
                    double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);

                    if(bluffOddsAreOk(sizing, facingBetSize, facingStack, pot)) {
                        System.out.println("zzz4");
                        int bluffSuccessNumber = getNumberOfSuccessfulBluffs(opponentName);
                        double random = Math.random();

                        String aggroActionToUse;

                        if(action.equals("check")) {
                            aggroActionToUse = "bet75pct";
                        } else {
                            aggroActionToUse = "raise";
                        }

                        if(bluffSuccessNumber == 0) {
                            actionToReturn = action;
                        } else if(bluffSuccessNumber == 1) {
                            if(random < 0.40) {
                                if(sizing <= 5) {
                                    actionToReturn = aggroActionToUse;
                                    System.out.println("Bluff 1, action: " + aggroActionToUse + " sizing: " + sizing + " opponentName: " + opponentName);
                                } else {
                                    System.out.println("zzz5");
                                    actionToReturn = action;
                                }
                            } else {
                                System.out.println("zzz6");
                                actionToReturn = action;
                            }
                        } else if(bluffSuccessNumber == 2) {
                            if(random < 0.44) {
                                if(sizing <= 10) {
                                    actionToReturn = aggroActionToUse;
                                    System.out.println("Bluff 2, action: " + aggroActionToUse + " sizing: " + sizing + " opponentName: " + opponentName);
                                } else {
                                    System.out.println("zzz7");
                                    actionToReturn = action;
                                }
                            } else {
                                System.out.println("zzz8");
                                actionToReturn = action;
                            }
                        } else if(bluffSuccessNumber == 3) {
                            if(random < 0.48) {
                                if(sizing <= 15) {
                                    actionToReturn = aggroActionToUse;
                                    System.out.println("Bluff 3, action: " + aggroActionToUse + " sizing: " + sizing + " opponentName: " + opponentName);
                                } else {
                                    System.out.println("zzz9");
                                    actionToReturn = action;
                                }
                            } else {
                                System.out.println("zzz10");
                                actionToReturn = action;
                            }
                        } else if(bluffSuccessNumber == 4) {
                            if(random < 0.52) {
                                if(sizing <= 20) {
                                    actionToReturn = aggroActionToUse;
                                    System.out.println("Bluff 4, action: " + aggroActionToUse + " sizing: " + sizing + " opponentName: " + opponentName);
                                } else {
                                    System.out.println("zzz11");
                                    actionToReturn = action;
                                }
                            } else {
                                System.out.println("zzz12");
                                actionToReturn = action;
                            }
                        } else if(bluffSuccessNumber == 5) {
                            if(random < 0.56) {
                                if(sizing <= 30) {
                                    actionToReturn = aggroActionToUse;
                                    System.out.println("Bluff 5, action: " + aggroActionToUse + " sizing: " + sizing + " opponentName: " + opponentName);
                                } else {
                                    System.out.println("zzz13");
                                    actionToReturn = action;
                                }
                            } else {
                                System.out.println("zzz14");
                                actionToReturn = action;
                            }
                        } else {
                            if(random < 0.60) {
                                if(sizing <= 70) {
                                    actionToReturn = aggroActionToUse;
                                    System.out.println("Bluff 6, action: " + aggroActionToUse + " sizing: " + sizing + " opponentName: " + opponentName);
                                } else {
                                    System.out.println("zzz15");
                                    actionToReturn = action;
                                }
                            } else {
                                System.out.println("zzz16");
                                actionToReturn = action;
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




    public String doBluffAccordingToFoldStat(String action, double bigBlind, boolean position, double handStrength,
                                             List<Card> board, boolean opponentHasInitiative, double facingBetSize,
                                             double myBetSize, double myStack, double facingStack, double pot,
                                             boolean pre3betOrPostRaisedPot, String opponentName) {
        String actionToReturn = null;

        if(board != null && board.size() >= 3) {
            if(action.equals("check") || action.equals("fold")) {
                if(action.equals("check") && opponentHasInitiative) {
                    actionToReturn = action;
                } else {
                    if(handStrength < 0.64) {
                        double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);
                        RangeTracker rangeTracker = new RangeTracker();

                        if(bluffOddsAreOk(sizing, facingBetSize, facingStack, pot)) {
                            String actionToUse;

                            if(action.equals("check")) {
                                actionToUse = "bet75pct";
                            } else {
                                actionToUse = "raise";
                            }

                            String rangeRoute = rangeTracker.getRangeRoute(actionToUse, position, sizing, bigBlind, board);
                            double ratio = rangeTracker.getRangeRouteBluffValueRatio(rangeRoute);

                            if(ratio >= 0) {
                                double targetRatio = getTargetRatio(opponentName, position);

                                //do adjustment here

                                double bluffAmount = 16;
                                double nonBluffAmount = 93;
                                double valueAmount = 40;
                                double currentRatio = 0.4;

                                if(currentRatio < targetRatio) {
                                    if(actionToUse.equals("raise")) {
                                        if (board.size() == 3 || board.size() == 4) {
                                            if (pre3betOrPostRaisedPot) {
                                                actionToReturn = action;
                                            }
                                        }
                                    }

                                    if(actionToReturn == null) {
                                        double targetBluffAmount = valueAmount * targetRatio;

                                        double difference = targetBluffAmount - bluffAmount;

                                        double extraBluffPercentage = difference / nonBluffAmount;

                                        double random = Math.random();

                                        if(random < extraBluffPercentage) {
                                            actionToReturn = actionToUse;
                                        } else {
                                            actionToReturn = action;
                                        }





                                        //hoe vaak moet je met je non bluffs bluffen om op de target ratio uit te komen?

                                        //je wil op 0.6 uitkomen

                                        //valueAmount * targetRatio

                                        //bluffAmount should be 24

                                        //dus 8 bluffs erbij

                                        //dus van de 93 keer had je 8 keer moeten bluffen

                                        //0.086

                                        //dus als random < 0.086 doe dan aggro action
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
                }
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public String preventBluffAccordingToFoldStat(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                                  double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                                  double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                                  boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                                  boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                                  int boardWetness, boolean opponentHasInitiative, String opponentName) {
        String actionToReturn;

        if(board != null && board.size() >= 3) {
            if(action.equals("bet75pct") || action.equals("raise")) {
                if(handStrength < 0.64) {
                    double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind, ownBetSizeBb * bigBlind,
                            ownStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, bigBlind, board);

                    RangeTracker rangeTracker = new RangeTracker();
                    String rangeRoute = rangeTracker.getRangeRoute(action, position, sizing, bigBlind, board);
                    double ratio = rangeTracker.getRangeRouteBluffValueRatio(rangeRoute);

                    if(ratio >= 0 || ratio == -1) {
                        double targetRatio = getTargetRatio(opponentName, position);

                        if(ratio >= targetRatio || ratio == -1) {

                            //hier moet je nagaan hoe vaak je blufft en je dit eigenlijk niet zou moeten doen om op de target ratio uit te komen...

                            double bluffAmount = 16;
                            double valueAmount = 40;

                            double targetBluffAmount = valueAmount * targetRatio;

                            double difference = bluffAmount - targetBluffAmount;

                            double preventBluffPercentage = difference / bluffAmount;

                            double random = Math.random();

                            if(random < preventBluffPercentage) {
                                if(action.equals("bet75pct")) {
                                    actionToReturn = "check";
                                } else {
                                    List<String> eligibleActionsNew = new ArrayList<>();
                                    eligibleActionsNew.add("fold");
                                    eligibleActionsNew.add("call");

                                    //set both opponentstack and effective stack to zero to force either fold or call
                                    actionToReturn = new Poker().getAction(actionVariables, eligibleActionsNew, street, position, potSizeBb,
                                            opponentAction, facingOdds, 0, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                                            ownBetSizeBb, 0, ownStackBb, preflop, board, strongFlushDraw, strongOosd, strongGutshot,
                                            bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                            strongBackdoorSd, boardWetness, opponentHasInitiative);
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
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }


    private double getTargetRatio(String opponentName, boolean position) {
        double targetRatio;
        double opponentFoldStat = FoldStatsKeeper.getFoldStat(opponentName);

        if(position) {
            if(opponentFoldStat <= 0.31) {
                targetRatio = 0.22;
            } else if(opponentFoldStat < 0.55) {
                targetRatio = opponentFoldStat - 0.31;
                targetRatio = targetRatio / 0.24;
                targetRatio = targetRatio * 38;
                targetRatio = targetRatio + 22;
            } else {
                targetRatio = 0.60;
            }
        } else {
            if(opponentFoldStat <= 0.31) {
                targetRatio = 0.14;
            } else if(opponentFoldStat < 0.55) {
                targetRatio = opponentFoldStat - 0.31;
                targetRatio = targetRatio / 0.24;
                targetRatio = targetRatio * 38;
                targetRatio = targetRatio + 14;
            } else {
                targetRatio = 0.52;
            }
        }

        return targetRatio;
    }


    public void updateBluffDb(String opponentName, boolean successfulBluff) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM blufftracker WHERE opponentName = '" + opponentName + "';");

        if(successfulBluff) {
            st.executeUpdate("UPDATE blufftracker SET bluff_success = bluff_success + 1 WHERE opponentName = '" + opponentName + "'");
        } else {
            st.executeUpdate("UPDATE blufftracker SET bluff_success = 0 WHERE opponentName = '" + opponentName + "'");
        }

        rs.close();
        st.close();
        closeDbConnection();
    }

    private int getNumberOfSuccessfulBluffs(String opponentName) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM blufftracker WHERE opponentName = '" + opponentName + "';");

        int bluffSuccessNumber;

        if(rs.next()) {
            bluffSuccessNumber = rs.getInt("bluff_success");
        } else {
            st.executeUpdate("INSERT INTO blufftracker (opponentName) VALUES ('" + opponentName + "')");
            bluffSuccessNumber = 1;
        }

        return bluffSuccessNumber;
    }

    private boolean bluffOddsAreOk(double sizing, double facingBetSize, double facingStackSize, double pot) {
        double sizingInMethod;

        if(sizing > (facingBetSize + facingStackSize)) {
            sizingInMethod = facingBetSize + facingStackSize;
        } else {
            sizingInMethod = sizing;
        }

        double odds = (sizingInMethod - facingBetSize) / (facingBetSize + sizingInMethod + pot);
        return odds > 0.36;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

}
