package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.*;

public class HandHistoryReaderStars {

    public List<String> getOpponentActionsOfLastHand(String opponentPlayerNameOfLastHand) throws Exception {
        List<String> total = readTextFile();
        List<String> lastHand = getLinesOfLastGame(total);

        //logic regarding foldstats
        FoldStatsKeeper foldStatsKeeper = new FoldStatsKeeper();

        if(botFolded(lastHand)) {
            foldStatsKeeper.updateFoldCountMapInDb("bot-V-" + opponentPlayerNameOfLastHand, "fold");
            foldStatsKeeper.updateFoldCountMapInDb(opponentPlayerNameOfLastHand, "nonFold");
        } else {
            foldStatsKeeper.updateFoldCountMapInDb("bot-V-" + opponentPlayerNameOfLastHand, "nonFold");

            if(opponentFolded(lastHand)) {
                foldStatsKeeper.updateFoldCountMapInDb(opponentPlayerNameOfLastHand, "fold");
            } else {
                foldStatsKeeper.updateFoldCountMapInDb(opponentPlayerNameOfLastHand, "nonFold");
            }
        }
        //logic regarding foldstats

        List<String> betweenflopAndShowdown = getLinesOfHandFromFlopUntilRiver(lastHand);
        List<String> opponentActions = getOpponentActions(betweenflopAndShowdown);

        return opponentActions;
    }

    public List<String> readTextFile() throws Exception  {
        File textFile = getLatestFilefromDir("/Users/LennartMac/Library/Application Support/PokerStarsEU/HandHistory/vegeta11223");

        List<String> textLines;
        try (Reader fileReader = new FileReader(textFile)) {
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = bufReader.readLine();

            textLines = new ArrayList<>();

            while (line != null) {
                textLines.add(line);
                line = bufReader.readLine();
            }

            bufReader.close();
            fileReader.close();
        }

        return textLines;
    }

    public List<String> getLinesOfLastGame(List<String> totalXml) {
        List<String> copyOfTotal = new ArrayList<>();
        List<String> lastGame = new ArrayList<>();

        copyOfTotal.addAll(totalXml);
        Collections.reverse(copyOfTotal);

        boolean firstHand = false;

        for(String line : copyOfTotal) {
            if(line.contains("Seat")) {
                firstHand = true;
            }

            if(line.contains("PokerStars")) {
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

    private List<String> getLinesOfHandFromFlopUntilRiver(List<String> lastGame) {
        List<String> flopUntilRiverLines = new ArrayList<>();

        boolean flopLinePassed = false;
        boolean showdownOrSummaryLinePassed = false;

        for(String line : lastGame) {
            if(line.contains("*** FLOP ***")) {
                flopLinePassed = true;
            }

            if(line.contains("*** SHOW DOWN ***") || line.contains("*** SUMMARY ***")) {
                showdownOrSummaryLinePassed = true;
            }

            if(flopLinePassed && !showdownOrSummaryLinePassed) {
                if(!line.contains("*** SHOW DOWN ***") || !line.contains("*** SUMMARY ***")) {
                    flopUntilRiverLines.add(line);
                }
            }

            if(showdownOrSummaryLinePassed) {
                break;
            }
        }

        return flopUntilRiverLines;
    }

    private List<String> getOpponentActions(List<String> linesFromFlopUntilRiver) {
        List<String> opponentActions = new ArrayList<>();

        for(String line : linesFromFlopUntilRiver) {
            if(!line.contains("vegeta11223")) {
                if(line.contains(" folds")) {
                    opponentActions.add("fold");
                } else if(line.contains(" calls ")) {
                    opponentActions.add("call");
                } else if(line.contains(" checks")) {
                    opponentActions.add("check");
                } else if(line.contains(" bets ")) {
                    opponentActions.add("bet75pct");
                } else if(line.contains(" raises ")) {
                    opponentActions.add("raise");
                }
            }
        }

        return opponentActions;
    }

    public String getHandNumber(String firstLine) {
        return firstLine.substring(firstLine.indexOf("#") + 1, firstLine.indexOf(":"));
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

    private boolean botFolded(List<String> linesOfLastHand) {
        boolean botFolded = false;

        for(String line : linesOfLastHand) {
            if(line.contains("vegeta11223: folds")) {
                botFolded = true;
                break;
            }
        }

        return botFolded;
    }

    private boolean opponentFolded(List<String> linesOfLastHand) {
        boolean opponentFolded = false;

        for(String line : linesOfLastHand) {
            if(line.contains(": folds")) {
                opponentFolded = true;
                break;
            }
        }

        return opponentFolded;
    }
}
