package com.lennart.model.action.actionbuilders.ai;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 31/12/2017.
 */
public class Poker {

    //input
        //hand strength (5% groups)
        //strong draw yes / no
        //opponent type
        //board texture
        //handpath
        //opponent sizing ( < 50% pot, 50 - 100 / 100 - 150 / > 150 )
        //stacks
        //pot odds







    //input
        //opponent type
        //hand strength
        //strong draw
        //position
        //potsize - different sizes
        //your bet - different sizes
        //opponent bet - different sizes
        //effective stack
        //board texture

        //een TAG, jij hebt 82%, geen draw, IP, pot is 8, hij bet 4, eff. stack 40, droog board

    //process


    //output
        //-fold
        //-call
        //check
        //bet 0.25
        //bet 0.5
        //bet 0.75
        //bet 1
        //bet 1.5
        //bet 2
        //raise

    protected Connection con;

    public static void main(String[] args) throws Exception {
        new Poker().theMethod();
    }

    private void theMethod() throws Exception {
        Map<String, Double> eije = retrieveRouteDataFromDb("Handstrength50-55StrongDrawNoPositionBTNPotsize60-100bbComputerBetsize10-15bbOpponentBetsize10-15bbEffectiveStack75-110bbBoardTextureDry");

        System.out.println("jaja");
//        List<String> routesFromDb = retrieveAllRoutesFromDb();
//
//        List<String> routesNormal = generateTable();
//
//        System.out.println("wacht");
//
//        routesFromDb.removeAll(routesNormal);
//
//        System.out.println("wacht2");
//        List<String> routes = generateTable();
//        storeRoutesInDb(routes);
    }

    private List<String> generateTable() {
        List<String> handStrength = new ArrayList<>();
        List<String> strongDraw = new ArrayList<>();
        List<String> position = new ArrayList<>();
        List<String> potSize = new ArrayList<>();
        List<String> computerBetSize = new ArrayList<>();
        List<String> opponentBetSize = new ArrayList<>();
        List<String> effectiveStack = new ArrayList<>();
        List<String> boardTexture = new ArrayList<>();

        handStrength.add("Handstrength0-5");
        handStrength.add("Handstrength5-10");
        handStrength.add("Handstrength10-15");
        handStrength.add("Handstrength15-20");
        handStrength.add("Handstrength20-25");
        handStrength.add("Handstrength25-30");
        handStrength.add("Handstrength30-35");
        handStrength.add("Handstrength35-40");
        handStrength.add("Handstrength40-45");
        handStrength.add("Handstrength45-50");
        handStrength.add("Handstrength50-55");
        handStrength.add("Handstrength55-60");
        handStrength.add("Handstrength60-65");
        handStrength.add("Handstrength65-70");
        handStrength.add("Handstrength70-75");
        handStrength.add("Handstrength75-80");
        handStrength.add("Handstrength80-85");
        handStrength.add("Handstrength85-90");
        handStrength.add("Handstrength90-95");
        handStrength.add("Handstrength95-100");

        strongDraw.add("StrongDrawYes");
        strongDraw.add("StrongDrawNo");

        position.add("PositionBTN");
        position.add("PositionBB");

        potSize.add("Potsize0-5bb");
        potSize.add("Potsize5-10bb");
        potSize.add("Potsize10-15bb");
        potSize.add("Potsize15-20bb");
        potSize.add("Potsize20-25bb");
        potSize.add("Potsize25-40bb");
        potSize.add("Potsize40-60bb");
        potSize.add("Potsize60-100bb");
        potSize.add("Potsize100-150bb");
        potSize.add("Potsize>150bb");

        computerBetSize.add("ComputerBetsize0-5bb");
        computerBetSize.add("ComputerBetsize5-10bb");
        computerBetSize.add("ComputerBetsize10-15bb");
        computerBetSize.add("ComputerBetsize15-20bb");
        computerBetSize.add("ComputerBetsize20-25bb");
        computerBetSize.add("ComputerBetsize25-40bb");
        computerBetSize.add("ComputerBetsize40-60bb");
        computerBetSize.add("ComputerBetsize60-100bb");
        computerBetSize.add("ComputerBetsize100-150bb");
        computerBetSize.add("ComputerBetsize>150bb");

        opponentBetSize.add("OpponentBetsize0-5bb");
        opponentBetSize.add("OpponentBetsize5-10bb");
        opponentBetSize.add("OpponentBetsize10-15bb");
        opponentBetSize.add("OpponentBetsize15-20bb");
        opponentBetSize.add("OpponentBetsize20-25bb");
        opponentBetSize.add("OpponentBetsize25-40bb");
        opponentBetSize.add("OpponentBetsize40-60bb");
        opponentBetSize.add("OpponentBetsize60-100bb");
        opponentBetSize.add("OpponentBetsize100-150bb");
        opponentBetSize.add("OpponentBetsize>150bb");

        effectiveStack.add("EffectiveStack0-20bb");
        effectiveStack.add("EffectiveStack20-50bb");
        effectiveStack.add("EffectiveStack50-75bb");
        effectiveStack.add("EffectiveStack75-110bb");
        effectiveStack.add("EffectiveStack110-150bb");
        effectiveStack.add("EffectiveStack>150bb");

        boardTexture.add("BoardTextureDry");
        boardTexture.add("BoardTextureMedium");
        boardTexture.add("BoardTextureWet");

        List<String> allRoutes = new ArrayList<>();

        for(String a : handStrength) {
            for(String b : strongDraw) {
                for(String c : position) {
                    for(String d : potSize) {
                        for(String e : computerBetSize) {
                            for(String f : opponentBetSize) {
                                for(String g : effectiveStack) {
                                    for(String h : boardTexture) {
                                        allRoutes.add(a + b + c + d + e + f + g + h);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return allRoutes;
    }

    private void storeRoutesInDb(List<String> routes) throws Exception {
        initializeDbConnection();

        for(String route : routes) {
            Statement st = con.createStatement();
            try {
                st.executeUpdate("INSERT INTO standard (route) VALUES ('" + route + "')");
            } catch (Exception e) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    st.executeUpdate("INSERT INTO standard (route) VALUES ('" + route + "')");
                } catch (Exception f) {
                    System.out.println("wacht");
                }
            }
            st.close();
        }
        closeDbConnection();
    }

    private Map<String, Double> retrieveRouteDataFromDb(String route) throws Exception {
        Map<String, Double> routeData = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM standard WHERE route = '" + route + "';");

        while(rs.next()) {
            routeData.put("fold", rs.getDouble("fold_times"));
            routeData.put("check", rs.getDouble("check_times"));
            routeData.put("call", rs.getDouble("call_times"));
            routeData.put("bet25%", rs.getDouble("bet25%_times"));
            routeData.put("bet50%", rs.getDouble("bet50%_times"));
            routeData.put("bet75%", rs.getDouble("bet75%_times"));
            routeData.put("bet100%", rs.getDouble("bet100%_times"));
            routeData.put("bet150%", rs.getDouble("bet150%_times"));
            routeData.put("bet200%", rs.getDouble("bet200%_times"));
            routeData.put("raise", rs.getDouble("raise_times"));
        }
        return routeData;
    }

    private List<String> retrieveAllRoutesFromDb() throws Exception {
        List<String> routesFromDb = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM standard");

        while(rs.next()) {
            routesFromDb.add(rs.getString("route"));
        }

        rs.close();
        st.close();
        closeDbConnection();

        return routesFromDb;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/poker", "root", "");
    }

    protected void closeDbConnection() throws SQLException {
        con.close();
    }
}
