package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HandHistoryReaderParty {

    public List<String> getOpponentActionsOfLastHand(String opponentPlayerNameOfLastHand, double bigBlind) throws Exception {
        return null;
    }

    public List<String> getOpponentActionsOfLastHand(boolean postflop, double bigBlind) throws Exception {
        return null;
    }

    public List<String> readTextFile() throws Exception  {
        return null;
    }

    public List<String> getLinesOfLastGame(List<String> totalXml, int handNumber, double bigBlind) {
        return null;
    }

    public List<String> getLinesOfLastGameNonRecursive(List<String> totalXml) {
        return null;
    }

    public double getBigBlindFromLastHandHh(List<String> lastHand) {
        return -1;
    }

    private List<String> getLinesOfHandFromFlopUntilRiver(List<String> lastGame) {
        return null;
    }

    private List<String> getPreflopLines(List<String> lastGame) {
        return null;
    }

    private List<String> getOpponentActions(List<String> linesFromFlopUntilRiver) {
        return null;
    }

    public String getHandNumber(String firstLine) {
        return null;
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
        return false;
    }

    private boolean opponentFolded(List<String> linesOfLastHand) {
        return false;
    }
}
