package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.card.Card;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 27/10/2018.
 */
public class RangeTracker {

    private double bluffAmount;
    private double valueAmount;
    private double nonBluffAmount;

    private Connection con;

    public static void main(String[] args) throws Exception {
        new RangeTracker().fillDbInitial();
    }

    private void fillDbInitial() throws Exception {
        List<String> allRoutes = getAllRangeRoutesExtensive();

        initializeDbConnection();

        for(String route : allRoutes) {
            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO rangetracker_extensive (route) VALUES ('" + route + "')");

            st.close();
        }

        closeDbConnection();
    }

    private void doDbMigration() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM rangetracker;");

        while(rs.next()) {
            String route = rs.getString("route");
            int bluffAmount = rs.getInt("bluff_amount");
            int valueAmount = rs.getInt("value_amount");

            int bbIndex = route.indexOf("bb");
            String newRoute = route.substring(0, bbIndex + 2);

            Statement st2 = con.createStatement();

            st2.executeUpdate("UPDATE rangetracker_cons SET bluff_amount = bluff_amount + " + bluffAmount + " WHERE route = '" + newRoute + "'");
            st2.executeUpdate("UPDATE rangetracker_cons SET value_amount = value_amount + " + valueAmount + " WHERE route = '" + newRoute + "'");

            st2.close();
        }

        rs.close();
        st.close();
        closeDbConnection();
    }

    public void updateRangeMapInDbSimple(String action, double sizing, double bigBlind, boolean position, double handStrength, List<Card> board) throws Exception {
        String streetString = getStreetString(board);
        String actionString = getActionString(action);
        String positionString = getPositionString(position);
        String sizingString = getSizingString(sizing, bigBlind);

        String route = streetString + actionString + positionString + sizingString;

        initializeDbConnection();

        Statement st = con.createStatement();

        if((action.equals("bet75pct")) || action.equals("raise")) {
            if(handStrength < 0.7) {
                st.executeUpdate("UPDATE rangetracker SET bluff_amount = bluff_amount + 1 WHERE route = '" + route + "'");
            } else {
                st.executeUpdate("UPDATE rangetracker SET value_amount = value_amount + 1 WHERE route = '" + route + "'");
            }
        } else {
            if(handStrength < 0.7) {
                st.executeUpdate("UPDATE rangetracker SET non_bluff_amount = non_bluff_amount + 1 WHERE route = '" + route + "'");
            }
        }

        st.close();
        closeDbConnection();
    }

    public void updateRangeMapInDbExtensive(String action, double sizing, double bigBlind, boolean position, double handStrength, List<Card> board, int drawWetness, int boatWetness) throws Exception {
        String streetString = getStreetString(board);
        String actionString = getActionString(action);
        String positionString = getPositionString(position);
        String sizingString = getSizingString(sizing, bigBlind);
        String drawWetnessString = getDrawWetnessString(streetString, drawWetness);
        String boatWetnessString = getBoatWetnessString(streetString, boatWetness);

        String route = streetString + actionString + positionString + sizingString + drawWetnessString + boatWetnessString;

        initializeDbConnection();

        Statement st = con.createStatement();

        if((action.equals("bet75pct")) || action.equals("raise")) {
            if(handStrength < 0.7) {
                st.executeUpdate("UPDATE rangetracker_extensive SET bluff_amount = bluff_amount + 1 WHERE route = '" + route + "'");
            } else {
                st.executeUpdate("UPDATE rangetracker_extensive SET value_amount = value_amount + 1 WHERE route = '" + route + "'");
            }
        } else {
            if(handStrength < 0.7) {
                st.executeUpdate("UPDATE rangetracker_extensive SET non_bluff_amount = non_bluff_amount + 1 WHERE route = '" + route + "'");
            }
        }

        st.close();
        closeDbConnection();
    }

    public String getRangeRoute(String action, boolean position, double sizing, double bigBlind, List<Card> board) {
        String streetString = getStreetString(board);
        String actionString = getActionString(action);
        String positionString = getPositionString(position);
        String sizingString = getSizingString(sizing, bigBlind);

        String route = streetString + actionString + positionString + sizingString;

        return route;
    }

    public String getRangeRouteExtensive(String action, boolean position, double sizing, double bigBlind, List<Card> board, int drawWetness, int boatWetness) {
        String streetString = getStreetString(board);
        String actionString = getActionString(action);
        String positionString = getPositionString(position);
        String sizingString = getSizingString(sizing, bigBlind);
        String drawWetnessString = getDrawWetnessString(streetString, drawWetness);
        String boatWetnessString = getBoatWetnessString(streetString, boatWetness);

        String route = streetString + actionString + positionString + sizingString + drawWetnessString + boatWetnessString;

        return route;
    }

    public double getRangeRouteBluffValueRatio(String rangeRoute, int plusAmount) {
        double rangeRouteBluffValueRatio = -1;

        try {
            initializeDbConnection();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM rangetracker WHERE route = '" + rangeRoute + "';");

            rs.next();

            bluffAmount = rs.getDouble("bluff_amount");
            nonBluffAmount = rs.getDouble("non_bluff_amount");
            valueAmount = rs.getDouble("value_amount");

            if(valueAmount != 0) {
                bluffAmount = bluffAmount + plusAmount;
                rangeRouteBluffValueRatio = bluffAmount / valueAmount;
            }

            rs.close();
            st.close();
            closeDbConnection();
        } catch (Exception e) {
            System.out.println("Error occured in getRangeRouteBluffValueRatio()");
            e.printStackTrace();
        }

        return rangeRouteBluffValueRatio;
    }

    public String balancePlayDoBluff(String action, double bigBlind, boolean position, double handStrength,
                              List<Card> board, boolean opponentHasInitiative, double facingBetSize,
                              double myBetSize, double myStack, double facingStack, double pot, boolean pre3betOrPostRaisedPot) {
        String actionToReturn;

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
                            double ratio = rangeTracker.getRangeRouteBluffValueRatio(rangeRoute, 1);

                            if(ratio >= 0) {
                                double limit;

                                if(position) {
                                    limit = 0.41;
                                } else {
                                    limit = 0.33;
                                }

                                if(ratio <= limit) {
                                    if(actionToUse.equals("raise")) {
                                        if(board.size() == 3 || board.size() == 4) {
                                            if(pre3betOrPostRaisedPot) {
                                                actionToReturn = action;
                                            } else {
                                                actionToReturn = actionToUse;
                                                System.out.println("Changed acton to " + actionToUse + " in balancePlayDoBluff()");
                                                System.out.println("rangeRoute: " + rangeRoute);
                                                System.out.println("ratio: " + ratio);
                                            }
                                        } else {
                                            actionToReturn = actionToUse;
                                            System.out.println("Changed acton to " + actionToUse + " in balancePlayDoBluff()");
                                            System.out.println("rangeRoute: " + rangeRoute);
                                            System.out.println("ratio: " + ratio);
                                        }
                                    } else {
                                        actionToReturn = actionToUse;
                                        System.out.println("Changed acton to " + actionToUse + " in balancePlayDoBluff()");
                                        System.out.println("rangeRoute: " + rangeRoute);
                                        System.out.println("ratio: " + ratio);
                                    }
                                } else {
                                    System.out.println("check or fold because ratio above limit. Ratio: " + ratio + " Limit: " + limit);
                                    actionToReturn = action;
                                }
                            } else {
                                System.out.println("check or fold because ratio below 0");
                                actionToReturn = action;
                            }
                        } else {
                            System.out.println("check or fold because bluffodds not ok");
                            actionToReturn = action;
                        }
                    } else {
                        System.out.println("check or fold with hand hs > 0.64");
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

    public String balancePlayPreventBluff(String action, ActionVariables actionVariables, List<String> eligibleActions, String street, boolean position, double potSizeBb, String opponentAction,
                                          double facingOdds, double effectiveStackBb, boolean strongDraw, double handStrength, String opponentType,
                                          double opponentBetSizeBb, double ownBetSizeBb, double opponentStackBb, double ownStackBb, boolean preflop, List<Card> board,
                                          boolean strongFlushDraw, boolean strongOosd, boolean strongGutshot, double bigBlind, boolean opponentDidPreflop4betPot,
                                          boolean pre3betOrPostRaisedPot, boolean strongOvercards, boolean strongBackdoorFd, boolean strongBackdoorSd,
                                          int boardWetness, boolean opponentHasInitiative) {
        String actionToReturn;

        if(board != null && board.size() >= 3) {
            if(action.equals("bet75pct") || action.equals("raise")) {
                if(handStrength < 0.64) {
                    double sizing = new Sizing().getAiBotSizing(opponentBetSizeBb * bigBlind, ownBetSizeBb * bigBlind,
                            ownStackBb * bigBlind, opponentStackBb * bigBlind, potSizeBb * bigBlind, bigBlind, board);

                    RangeTracker rangeTracker = new RangeTracker();
                    String rangeRoute = rangeTracker.getRangeRoute(action, position, sizing, bigBlind, board);
                    double ratio = rangeTracker.getRangeRouteBluffValueRatio(rangeRoute, 1);

                    if(ratio >= 0 || ratio == -1) {
                        double limit;

                        if(position) {
                            limit = 0.41;
                        } else {
                            limit = 0.33;
                        }

                        if(ratio >= limit || ratio == -1) {
                            if(action.equals("bet75pct")) {
                                actionToReturn = "check";

                                System.out.println("Changed acton to check in balancePlayPreventBluff()");
                                System.out.println("rangeRoute: " + rangeRoute);
                                System.out.println("ratio: " + ratio);
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

                                System.out.println("Changed acton to " + actionToReturn + " balancePlayPreventBluff()");
                                System.out.println("rangeRoute: " + rangeRoute);
                                System.out.println("ratio: " + ratio);
                            }
                        } else {
                            System.out.println("aggro action not prevented because ratio above limit or -1. Ratio: " + ratio + " limit: " + limit);
                            actionToReturn = action;
                        }
                    } else {
                        actionToReturn = action;
                    }
                } else {
                    System.out.println("aggro action not preventend because hs above 0.64");
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

    private String getActionString(String action) {
        String actionString;

        if(action.equals("bet75pct") || action.equals("check")) {
            actionString = "MyActionBet";
        } else {
            actionString = "MyActionRaise";
        }

        return actionString;
    }

    private String getPositionString(boolean position) {
        String positionString;

        if(position) {
            positionString = "PositionBTN";
        } else {
            positionString = "PositionBB";
        }

        return positionString;
    }

    private String getSizingString(double sizing, double bigBlind) {
        String sizingString;

        if(sizing / bigBlind <= 5) {
            sizingString = "Sizing0-5bb";
        } else if(sizing / bigBlind <= 10) {
            sizingString = "Sizing5-10bb";
        } else if(sizing / bigBlind <= 15) {
            sizingString = "Sizing10-15bb";
        } else if(sizing / bigBlind <= 20) {
            sizingString = "Sizing15-20bb";
        } else if(sizing / bigBlind <= 30) {
            sizingString = "Sizing20-30bb";
        } else if(sizing / bigBlind <= 40) {
            sizingString = "Sizing30-40bb";
        } else if(sizing / bigBlind <= 60) {
            sizingString = "Sizing40-60bb";
        } else if(sizing / bigBlind <= 100) {
            sizingString = "Sizing60-100bb";
        } else {
            sizingString = "Sizing>100bb";
        }

        return sizingString;
    }

    private String getStreetString(List<Card> board)  {
        String streetString;

        if(board.size() == 3) {
            streetString = "Flop";
        } else if(board.size() == 4) {
            streetString = "Turn";
        } else {
            streetString = "River";
        }

        return streetString;
    }

    private String getDrawWetnessString(String street, int drawWetness) {
        String drawWetnessString;

        if(street.equals("Flop")) {
            if(drawWetness < 112) {
                drawWetnessString = "DrawWetnessDry";
            } else if(drawWetness < 167) {
                drawWetnessString = "DrawWetnessMedium";
            } else {
                drawWetnessString = "DrawWetnessWet";
            }
        } else if(street.equals("Turn")) {
            if(drawWetness < 176) {
                drawWetnessString = "DrawWetnessDry";
            } else if(drawWetness < 419) {
                drawWetnessString = "DrawWetnessMedium";
            } else {
                drawWetnessString = "DrawWetnessWet";
            }
        } else {
            if(drawWetness < 32) {
                drawWetnessString = "DrawWetnessDry";
            } else if(drawWetness < 73) {
                drawWetnessString = "DrawWetnessMedium";
            } else {
                drawWetnessString = "DrawWetnessWet";
            }
        }

        return drawWetnessString;
    }

    private String getBoatWetnessString(String street, int boatWetness) {
        String boatWetnessString;

        if(street.equals("Flop")) {
            if(boatWetness < 9) {
                boatWetnessString = "BoatWetnessDry";
            } else if(boatWetness < 72) {
                boatWetnessString = "BoatWetnessMedium";
            } else {
                boatWetnessString = "BoatWetnessWet";
            }
        } else if(street.equals("Turn")) {
            if(boatWetness < 18) {
                boatWetnessString = "BoatWetnessDry";
            } else if(boatWetness < 180) {
                boatWetnessString = "BoatWetnessMedium";
            } else {
                boatWetnessString = "BoatWetnessWet";
            }
        } else {
            if(boatWetness < 27) {
                boatWetnessString = "BoatWetnessDry";
            } else if(boatWetness < 179) {
                boatWetnessString = "BoatWetnessMedium";
            } else {
                boatWetnessString = "BoatWetnessWet";
            }
        }

        return boatWetnessString;
    }

    private static List<String> getAllRangeRoutes() {
        List<String> street = new ArrayList<>();
        List<String> action = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> sizing = new ArrayList<>();

        street.add("Flop");
        street.add("Turn");
        street.add("River");

        action.add("MyActionBet");
        action.add("MyActionRaise");

        position.add("PositionBTN");
        position.add("PositionBB");

        sizing.add("Sizing0-5bb");
        sizing.add("Sizing5-10bb");
        sizing.add("Sizing10-15bb");
        sizing.add("Sizing15-20bb");
        sizing.add("Sizing20-30bb");
        sizing.add("Sizing30-40bb");
        sizing.add("Sizing40-60bb");
        sizing.add("Sizing60-100bb");
        sizing.add("Sizing>100bb");

        List<String> allRangeRoutes = new ArrayList<>();

        for(String a : street) {
            for(String b : action) {
                for(String c : position) {
                    for(String d : sizing) {
                        allRangeRoutes.add(a + b + c + d);
                    }
                }
            }
        }

        return allRangeRoutes;
    }

    private static List<String> getAllRangeRoutesExtensive() {
        List<String> street = new ArrayList<>();
        List<String> action = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> sizing = new ArrayList<>();
        List<String> drawWetnessString = new ArrayList<>();
        List<String> boatWetnessString = new ArrayList<>();

        street.add("Flop");
        street.add("Turn");
        street.add("River");

        action.add("MyActionBet");
        action.add("MyActionRaise");

        position.add("PositionBTN");
        position.add("PositionBB");

        sizing.add("Sizing0-5bb");
        sizing.add("Sizing5-10bb");
        sizing.add("Sizing10-15bb");
        sizing.add("Sizing15-20bb");
        sizing.add("Sizing20-30bb");
        sizing.add("Sizing30-40bb");
        sizing.add("Sizing40-60bb");
        sizing.add("Sizing60-100bb");
        sizing.add("Sizing>100bb");

        drawWetnessString.add("DrawWetnessDry");
        drawWetnessString.add("DrawWetnessMedium");
        drawWetnessString.add("DrawWetnessWet");

        boatWetnessString.add("BoatWetnessDry");
        boatWetnessString.add("BoatWetnessMedium");
        boatWetnessString.add("BoatWetnessWet");

        List<String> allRangeRoutes = new ArrayList<>();

        for(String a : street) {
            for(String b : action) {
                for(String c : position) {
                    for(String d : sizing) {
                        for(String e : drawWetnessString) {
                            for(String f : boatWetnessString) {
                                allRangeRoutes.add(a + b + c + d + e + f);
                            }
                        }
                    }
                }
            }
        }

        return allRangeRoutes;
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

    public double getBluffAmount() {
        return bluffAmount;
    }

    public double getValueAmount() {
        return valueAmount;
    }

    public double getNonBluffAmount() {
        return nonBluffAmount;
    }
}
