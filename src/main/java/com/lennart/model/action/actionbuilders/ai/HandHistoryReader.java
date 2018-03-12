package com.lennart.model.action.actionbuilders.ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LPO21630 on 9-3-2018.
 */
public class HandHistoryReader {

    public static void main(String[] args) throws Exception {

        List<String> total = new HandHistoryReader().readXmlFile();

        List<String> lastHand = new HandHistoryReader().getLinesOfLastGame(total);

        String playerName = new HandHistoryReader().getOpponentPlayerName(lastHand);

        List<String> actionsOfOpponent = new HandHistoryReader().getAtionLinesOfOpponent(lastHand, playerName);

        List<String> opponentActions = new HandHistoryReader().getOpponentActions(actionsOfOpponent);


//        String x = System.getProperty("user.home");
//
//        System.out.println(x);
//
//        File xmlFile = new File("D:/data/hhs/6798668642.xml");
//        Reader fileReader = new FileReader(xmlFile);
//        BufferedReader bufReader = new BufferedReader(fileReader);
//
//        String line = bufReader.readLine();
//
//        List<String> xmlLines = new ArrayList<>();
//
//        while(line != null) {
//            xmlLines.add(line);
//            line = bufReader.readLine();
//        }
//
//        System.out.println("wacht");
//
//
//        bufReader.close();
//        fileReader.close();

    }

    private List<String> readXmlFile() throws Exception  {
        //File xmlFile = new File("D:/hhs/6798668642.xml");
        File xmlFile = getLatestFilefromDir("D:/hhs");

        List<String> xmlLines;
        try (Reader fileReader = new FileReader(xmlFile)) {
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = bufReader.readLine();

            xmlLines = new ArrayList<>();

            while (line != null) {
                xmlLines.add(line);
                line = bufReader.readLine();
            }

            bufReader.close();
            fileReader.close();
        }

        return xmlLines;
    }

    private List<String> getLinesOfLastGame(List<String> totalXml) {
        List<String> copyOfTotal = new ArrayList<>();
        List<String> lastGame = new ArrayList<>();

        copyOfTotal.addAll(totalXml);
        Collections.reverse(copyOfTotal);

        boolean firstHand = false;

        for(String line : copyOfTotal) {
            if(line.contains("/game>")) {
                firstHand = true;
            }

            if(line.contains("game gamecode=")) {
                lastGame.add(line);
                break;
            }

            if(firstHand) {
                lastGame.add(line);
            }
        }

        Collections.reverse(lastGame);
        return lastGame;
    }

    private String getOpponentPlayerName(List<String> lastHand) {
        String opponentPlayerName = "";

        for(String line : lastHand) {
            if(line.contains("<action player=")) {
                if(!line.contains("COCONUT555")) {
                    String[] words = line.split(" ");

                    for(String word : words) {
                        if(word.contains("player=")) {
                            opponentPlayerName = word;
                            opponentPlayerName = opponentPlayerName.replaceAll("player=", "");
                            opponentPlayerName = opponentPlayerName.replaceFirst("\"", "");
                            opponentPlayerName = opponentPlayerName.substring(0, opponentPlayerName.length());
                            break;
                        }
                    }
                }
            }
        }

        return opponentPlayerName;
    }

    private List<String> getAtionLinesOfOpponent(List<String> lastHand, String opponentPlayerName) {
        List<String> actionLinesOfOpponent = new ArrayList<>();

        for(String line : lastHand) {
            if(line.contains("<action player=")) {
                if(line.contains(opponentPlayerName)) {
                    actionLinesOfOpponent.add(line);
                }
            }
        }
        return actionLinesOfOpponent;
    }

    private List<String> getOpponentActions(List<String> actionLinesOfOpponent) {
        List<String> opponentActions = new ArrayList<>();

        for(String line : actionLinesOfOpponent) {
            if(line.contains("type=\"0\"")) {
                opponentActions.add("fold");
            } else if(line.contains("type=\"3\"")) {
                opponentActions.add("call");
            } else if(line.contains("type=\"4\"")) {
                opponentActions.add("check");
            } else if(line.contains("type=\"5\"")) {
                opponentActions.add("bet75pct");
            } else if(line.contains("type=\"23\"")) {
                opponentActions.add("raise");
            }
        }
        return opponentActions;
    }

    private File getLatestFilefromDir(String dirPath){
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }
}
