package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.sql.*;
import java.util.List;

public class Bluffer {

    private Connection con;

    public String doOpponentBluffSuccessAction(String action, String opponentName, double bigBlind, double handStrength,
                                                List<Card> board, boolean opponentHasInitiative, double facingBetSize,
                                                double myBetSize, double myStack, double facingStack, double pot) throws Exception {
        String actionToReturn;

        if(board != null && board.size() == 5) {
            if((action.equals("check") && !opponentHasInitiative) || action.equals("fold")) {
                if(handStrength < 0.64) {
                    double sizing = new Sizing().getAiBotSizing(facingBetSize, myBetSize, myStack, facingStack, pot, bigBlind, board);

                    if(bluffOddsAreOk(sizing, facingBetSize, facingStack, pot)) {
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
                                    actionToReturn = action;
                                }
                            } else {
                                actionToReturn = action;
                            }
                        } else if(bluffSuccessNumber == 2) {
                            if(random < 0.44) {
                                if(sizing <= 10) {
                                    actionToReturn = aggroActionToUse;
                                    System.out.println("Bluff 2, action: " + aggroActionToUse + " sizing: " + sizing + " opponentName: " + opponentName);
                                } else {
                                    actionToReturn = action;
                                }
                            } else {
                                actionToReturn = action;
                            }
                        } else if(bluffSuccessNumber == 3) {
                            if(random < 0.48) {
                                if(sizing <= 15) {
                                    actionToReturn = aggroActionToUse;
                                    System.out.println("Bluff 3, action: " + aggroActionToUse + " sizing: " + sizing + " opponentName: " + opponentName);
                                } else {
                                    actionToReturn = action;
                                }
                            } else {
                                actionToReturn = action;
                            }
                        } else if(bluffSuccessNumber == 4) {
                            if(random < 0.52) {
                                if(sizing <= 20) {
                                    actionToReturn = aggroActionToUse;
                                    System.out.println("Bluff 4, action: " + aggroActionToUse + " sizing: " + sizing + " opponentName: " + opponentName);
                                } else {
                                    actionToReturn = action;
                                }
                            } else {
                                actionToReturn = action;
                            }
                        } else if(bluffSuccessNumber == 5) {
                            if(random < 0.56) {
                                if(sizing <= 30) {
                                    actionToReturn = aggroActionToUse;
                                    System.out.println("Bluff 5, action: " + aggroActionToUse + " sizing: " + sizing + " opponentName: " + opponentName);
                                } else {
                                    actionToReturn = action;
                                }
                            } else {
                                actionToReturn = action;
                            }
                        } else {
                            if(random < 0.60) {
                                if(sizing <= 70) {
                                    actionToReturn = aggroActionToUse;
                                    System.out.println("Bluff 6, action: " + aggroActionToUse + " sizing: " + sizing + " opponentName: " + opponentName);
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
            } else {
                actionToReturn = action;
            }
        } else {
            actionToReturn = action;
        }

        return actionToReturn;
    }

    public void updateBluffDb(String opponentName, boolean successfulBluff) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM blufftracker WHERE playerName = '" + opponentName + "';");

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
