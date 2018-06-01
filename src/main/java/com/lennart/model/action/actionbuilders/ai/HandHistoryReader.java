package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LPO21630 on 9-3-2018.
 */
public class HandHistoryReader {

    public Map<String, List<String>> getOpponentActionsOfLastHand2() throws Exception {
        Map<String, List<String>> mapToReturn = new HashMap<>();

        List<String> total = readXmlFile();
        List<String> lastHand = getLinesOfLastGame(total);
        String playerName = getOpponentPlayerName(lastHand);

        //logic regarding foldstats
        if(playerDidFold(lastHand, "COCONUT555")) {
            FoldStatsKeeper.updateFoldCountMap("bot-V-" + playerName, "fold");
            FoldStatsKeeper.updateFoldCountMap(playerName, "nonFold");
        } else {
            FoldStatsKeeper.updateFoldCountMap("bot-V-" + playerName, "nonFold");

            if(playerDidFold(lastHand, playerName)) {
                FoldStatsKeeper.updateFoldCountMap(playerName, "fold");
            } else {
                FoldStatsKeeper.updateFoldCountMap(playerName, "nonFold");
            }
        }
        //logic regarding foldstats

        Map<Integer, List<String>> mapOfRounds = getLinesPerRoundOfLastGame(lastHand);

        List<String> actionLinesOfOpponent = getActionLinesOfOpponent(mapOfRounds, playerName);

        List<String> opponentActions = getOpponentActions(actionLinesOfOpponent);

        mapToReturn.put(playerName, opponentActions);
        return mapToReturn;
    }

    public Map<String, List<String>> getOpponentActionsOfLastHand() throws Exception {
        Map<String, List<String>> mapToReturn = new HashMap<>();

        List<String> total = readXmlFile();
        List<String> lastHand = getLinesOfLastGame(total);
        String playerName = getOpponentPlayerName(lastHand);
        List<String> actionLinesOfOpponent = getAtionLinesOfOpponent(lastHand, playerName);
        List<String> opponentActions = getOpponentActions(actionLinesOfOpponent);

        mapToReturn.put(playerName, opponentActions);
        return mapToReturn;
    }

    private List<String> readXmlFile() throws Exception  {
        File xmlFile = getLatestFilefromDir("C:/Users/Lennart/AppData/Local/NetBet Poker/data/COCONUT555/History/Data/Tables");

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

    private Map<Integer, List<String>> getLinesPerRoundOfLastGame(List<String> linesOfLastGame) {
        Map<Integer, List<String>> linesPerRoundOfLastGame = new HashMap<>();

        int counter = 0;
        boolean roundStarted = false;

        for(String line : linesOfLastGame) {
            if(line.contains("<round")) {
                counter++;
                linesPerRoundOfLastGame.put(counter, new ArrayList<>());
                roundStarted = true;
            }

            if(roundStarted) {
                linesPerRoundOfLastGame.get(counter).add(line);

                if(line.contains("/round")) {
                    roundStarted = false;
                }
            }
        }
        return linesPerRoundOfLastGame;
    }

    private String getOpponentPlayerName(List<String> lastHand) {
        String opponentPlayerName = "";

        for(String line : lastHand) {
            if(line.contains("<action") && line.contains("player=")) {
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
            if(line.contains("<action") && line.contains("player=")) {
                if(line.contains(opponentPlayerName)) {
                    if(line.contains("type=\"Flop\"") || line.contains("type=\"Turn\"") || line.contains("type=\"River\"")) {
                        actionLinesOfOpponent.add(line);
                    }
                }
            }
        }
        return actionLinesOfOpponent;
    }

    private List<String> getActionLinesOfOpponent(Map<Integer, List<String>> mapOfRounds, String opponentPlayerName) {
        List<String> actionLinesOfOpponent = new ArrayList<>();

        for (Map.Entry<Integer, List<String>> entry : mapOfRounds.entrySet()) {
           boolean postflop = false;

           for(String line : entry.getValue()) {
               if(line.contains("type=\"Flop\"") || line.contains("type=\"Turn\"") || line.contains("type=\"River\"")) {
                   postflop = true;
                   break;
               }
           }

            for(String line : entry.getValue()) {
                if(line.contains("<action") && line.contains("player=")) {
                    if(line.contains(opponentPlayerName)) {
                        if(postflop) {
                            actionLinesOfOpponent.add(line);
                        }
                    }
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

    public boolean lastModifiedFileIsLessThanTenMinutesAgo(String dirPath) {
        File file = getLatestFilefromDir(dirPath);
        long lastModifiedTime = file.lastModified();

        Date currentDate = new java.util.Date();
        long currentTime = currentDate.getTime();

        if(currentTime - lastModifiedTime < 600_000) {
            return true;
        }
        return false;
    }

    private boolean playerDidFold(List<String> linesOfLastHand, String playerName) {
        boolean playerDidFold = false;

        for(String line : linesOfLastHand) {
            if(line.contains("type=\"0\"")) {
                if(line.contains(playerName)) {
                    playerDidFold = true;
                }
                break;
            }
        }
        return playerDidFold;
    }
}
