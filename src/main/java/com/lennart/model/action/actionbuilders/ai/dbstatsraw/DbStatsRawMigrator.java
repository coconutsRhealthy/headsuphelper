package com.lennart.model.action.actionbuilders.ai.dbstatsraw;

import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 23/01/2019.
 */
public class DbStatsRawMigrator {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new DbStatsRawMigrator().ffiezSnel();
    }

    private void analysisMethod() throws Exception {
        Map<String, List<Double>> oppTypeBluffTotalMap = new HashMap<>();

        List<String> hmm = new ArrayList<>();
        List<String> hmm2 = new ArrayList<>();

        hmm.add("Tp");
        hmm.add("Ta");
        hmm.add("Lp");
        hmm.add("La");

        hmm2.add("tp");
        hmm2.add("ta");
        hmm2.add("lp");
        hmm2.add("la");

        //for(String s : hmm) {
            for(String g : hmm2) {
                List<Double> list = new ArrayList<>();
                list.add(0.0);
                list.add(0.0);

                oppTypeBluffTotalMap.put(g, list);
            }
        //}

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM testdbff;");

        while(rs.next()) {
            String route = rs.getString("route");

            for (Map.Entry<String, List<Double>> entry : oppTypeBluffTotalMap.entrySet()) {
                if(route.contains(entry.getKey()) && route.contains("Raise")) {
                    List<Double> listInMap = entry.getValue();

                    double successOld = listInMap.get(0);
                    double totalOld = listInMap.get(1);

                    double successNew = successOld + rs.getDouble("success");
                    double totalNew = totalOld + rs.getDouble("total");

                    listInMap.set(0, successNew);
                    listInMap.set(1, totalNew);
                }
            }
        }

        rs.close();
        st.close();

        Map<String, Double> sortedRatioMap = new HashMap<>();

        for (Map.Entry<String, List<Double>> entry : oppTypeBluffTotalMap.entrySet()) {
            List<Double> theList = entry.getValue();

            double ratio;

            if(theList.get(1) >= 20) {
                ratio = theList.get(0) /  theList.get(1);
            } else {
                ratio = -1;
            }

            sortedRatioMap.put(entry.getKey(), ratio);
        }

        sortedRatioMap = sortByValueHighToLow(sortedRatioMap);

        for (Map.Entry<String, Double> entry : sortedRatioMap.entrySet()) {
            System.out.println(entry.getKey() + "       " + entry.getValue());
        }
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private void ffiezSnel() throws Exception {
        Map<String, Double> testDbFfTotalMap = new HashMap<>();
        Map<String, Double> dbStatsBluffSngCompact2_0_Map = new HashMap<>();

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection con1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");

        Statement st1 = con1.createStatement();
        ResultSet rs1 = st1.executeQuery("SELECT * FROM testdbff;");

        while(rs1.next()) {
            testDbFfTotalMap.put(rs1.getString("route"), rs1.getDouble("success"));
        }

        rs1.close();
        st1.close();
        con1.close();

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection con2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker_2_0?&serverTimezone=UTC", "root", "");

        Statement st2 = con2.createStatement();
        ResultSet rs2 = st2.executeQuery("SELECT * FROM dbstats_bluff_sng_compact_2_0;");

        while(rs2.next()) {
            dbStatsBluffSngCompact2_0_Map.put(rs2.getString("route"), rs2.getDouble("success"));
        }

        Map<String, Double> differenceMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : testDbFfTotalMap.entrySet()) {
            double totalTestDbMap = entry.getValue();
            double totalDbStatsBluffSngCompact2_0_Map = dbStatsBluffSngCompact2_0_Map.get(entry.getKey());

            double difference = totalTestDbMap - totalDbStatsBluffSngCompact2_0_Map;

            if(difference != 0) {
                differenceMap.put(entry.getKey(), difference);
            }
        }

        differenceMap = sortByValueHighToLow(differenceMap);

        for (Map.Entry<String, Double> entry : differenceMap.entrySet()) {
            System.out.println(entry.getKey() + "     " + entry.getValue());
        }

        st2.close();
        rs2.close();
        con2.close();
    }

    private void migrateRawDataToBluffRouteCompact2_0() throws Exception {
        int counter = 0;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM dbstats_raw;");

        while(rs.next()) {
            if(rs.getDouble("entry") > 6403) {
                String board = rs.getString("board");

                if(!board.equals("")) {
                    String botAction = rs.getString("bot_action");

                    if(botAction.equals("bet75pct") || botAction.equals("raise")) {
                        double handStrength = rs.getDouble("handstrength");

                        if(handStrength < 0.7) {
                            String street = getStreetString(rs.getString("board"));
                            String bluffAction = getAction(rs.getString("bot_action"));
                            String position = rs.getString("position");
                            String sizingGroup = getSizingGroup(rs.getDouble("sizing"), rs.getDouble("bigblind"));
                            String opponentTypeGroup = getOpponentTypeGroup(rs.getString("opponent_data"));
                            String strongDraw = rs.getString("strongdraw");
                            String effectiveStack = getEffectiveStack(rs.getDouble("botstack"), rs.getDouble("opponentstack"), rs.getDouble("bigblind"));

                            String route = street + bluffAction + position + sizingGroup + opponentTypeGroup + strongDraw + effectiveStack;

                            Statement st2 = con.createStatement();

                            if(Boolean.valueOf(rs.getString("bot_won_hand"))) {
                                st2.executeUpdate("UPDATE testdbff SET success = success + 1 WHERE route = '" + route + "'");
                            }

                            st2.executeUpdate("UPDATE testdbff SET total = total + 1 WHERE route = '" + route + "'");

                            st2.close();

                            counter++;

                            System.out.println(counter);
                        }
                    }
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    public String getAction(String actionFromDb) {
        String action = null;

        if(actionFromDb.equals("bet75pct")) {
            action = "Bet";
        } else if(actionFromDb.equals("raise")) {
            action = "Raise";
        }

        return action;
    }

    public String getStreetString(String board) {
        String street;

        String boardOnlyNumbers = board.replaceAll("[^\\d.]", "");

        char numbersOfBoard [] = boardOnlyNumbers.toCharArray();

        for(char c : numbersOfBoard) {
            board = board.replace(c, ' ');
        }

        board = board.replaceAll(" ", "");

        if(board.length() == 3) {
            street = "Flop";
        } else if(board.length() == 4) {
            street = "Turn";
        } else if(board.length() == 5) {
            street = "River";
        } else {
            System.out.println("wrong boardstring!");
            throw new RuntimeException();
        }

        return street;
    }

    public String getSizingGroup(double sizing, double bigBlind) {
        String sizingGroup;

        double sizingBb = sizing / bigBlind;

        if(sizingBb <= 10) {
            sizingGroup = "Sizing_0-10bb";
        } else if(sizingBb <= 20) {
            sizingGroup = "Sizing_10-20bb";
        } else {
            sizingGroup = "Sizing_20bb_up";
        }

        return sizingGroup;
    }

    public String getOpponentTypeGroup(String opponentData) {
        String opponentType;

        String[] opponentDataSplitted = opponentData.split("\\.0");

        List<Double> correctValues = new ArrayList<>();

        for(String s : opponentDataSplitted) {
            String subS = s.substring(s.indexOf(" "), s.length());
            correctValues.add(Double.valueOf(subS));
        }

        double numberOfHands = correctValues.get(0);

        if(numberOfHands >= 20) {
            double preFoldCount = correctValues.get(1);
            double preCheckCount = correctValues.get(2);
            double preCallCount = correctValues.get(3);
            double preRaiseCount = correctValues.get(4);
            double postFoldCount = correctValues.get(5);
            double postCheckCount = correctValues.get(6);
            double postCallCount = correctValues.get(7);
            double postBetCount = correctValues.get(8);
            double postRaiseCount = correctValues.get(9);

            double preflopLooseness;
            double preflopAggressiveness;
            double postflopLooseness;
            double postflopAggressiveness;

            preflopLooseness = preCallCount / (preCallCount + preFoldCount);
            preflopAggressiveness = preRaiseCount / (preRaiseCount + preCheckCount + preCallCount);

            postflopLooseness = postCallCount / (postCallCount + postFoldCount);
            postflopAggressiveness = (postRaiseCount + postBetCount) / (postRaiseCount + postBetCount + postCheckCount + postCallCount);

            if(preflopLooseness < 0.7096774193548387) {
                opponentType = "t";
            } else {
                opponentType = "l";
            }

            if(preflopAggressiveness < 0.3958333333333333) {
                opponentType = opponentType + "p";
            } else {
                opponentType = opponentType + "a";
            }

            if(postflopLooseness < 0.5) {
                opponentType = opponentType + "t";
            } else {
                opponentType = opponentType + "l";
            }

            if(postflopAggressiveness < 0.3287671232876712) {
                opponentType = opponentType + "p";
            } else {
                opponentType = opponentType + "a";
            }
        } else {
            opponentType = "uuuu";
        }

        return opponentType;
    }

    public String getEffectiveStack(double botStack, double opponentStack, double bigBlind) {
        String effectiveStack;

        double botStackBb = botStack / bigBlind;
        double opponentStackBb = opponentStack / bigBlind;

        double effectiveStackBb;

        if(botStackBb > opponentStackBb) {
            effectiveStackBb = opponentStackBb;
        } else {
            effectiveStackBb = botStackBb;
        }

        if(effectiveStackBb <= 35) {
            effectiveStack = "EffStack_0_35_";
        } else {
            effectiveStack = "EffStack_35_up_";
        }

        return effectiveStack;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
