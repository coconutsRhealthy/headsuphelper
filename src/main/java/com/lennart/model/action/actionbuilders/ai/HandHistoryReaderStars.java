package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.*;

public class HandHistoryReaderStars {

    public List<String> getOpponentActionsOfLastHand(String opponentPlayerNameOfLastHand, double bigBlind) throws Exception {
        List<String> total = readTextFile();
        List<String> lastHand = getLinesOfLastGame(total, 1, bigBlind);

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

    public List<String> getOpponentActionsOfLastHand(boolean postflop, double bigBlind) throws Exception {
        List<String> total = readTextFile();
        List<String> lastHand = getLinesOfLastGame(total, 1, bigBlind);

        List<String> relevantLines;

        if(postflop) {
            relevantLines = getLinesOfHandFromFlopUntilRiver(lastHand);
        } else {
            relevantLines = getPreflopLines(lastHand);
        }

        List<String> opponentActions = getOpponentActions(relevantLines);

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

    public List<String> getLinesOfLastGame(List<String> totalXml, int handNumber, double bigBlind) {
        List<String> copyOfTotal = new ArrayList<>();
        List<String> lastGame = new ArrayList<>();

        copyOfTotal.addAll(totalXml);
        Collections.reverse(copyOfTotal);

        boolean firstHand = false;

        int counter = 0;

        for(String line : copyOfTotal) {
            if(line.contains("Seat 2") && (line.contains("big blind") || line.contains("(button)"))) {
                counter++;

                if(handNumber == counter) {
                    firstHand = true;
                }
            }

            if(line.contains("PokerStars")) {
                if(handNumber == counter) {
                    lastGame.add(line);
                    break;
                }
            }

            if(firstHand) {
                if(handNumber == counter) {
                    lastGame.add(line);
                }
            }
        }

        String smallBlind = String.valueOf(bigBlind / 2);
        if(smallBlind.endsWith(".0")) {
            smallBlind = smallBlind.substring(0, smallBlind.indexOf("."));
        }

        for(String line : lastGame) {
            if(line.contains("Uncalled bet (" + smallBlind + ") returned to vegeta11223")) {
                lastGame = getLinesOfLastGame(totalXml, handNumber + 1, bigBlind);
                Collections.reverse(lastGame);
            }
        }

        Collections.reverse(lastGame);
        return lastGame;
    }

    public List<String> getLinesOfLastGameNonRecursive(List<String> totalXml) {
        List<String> copyOfTotal = new ArrayList<>();
        List<String> lastGame = new ArrayList<>();

        copyOfTotal.addAll(totalXml);
        Collections.reverse(copyOfTotal);

        boolean firstHand = false;

        for(String line : copyOfTotal) {
            if(line.contains("Seat 2") && (line.contains("big blind") || line.contains("(button)"))) {
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

    public double getBigBlindFromLastHandHh(List<String> lastHand) {
        double bigBlind = -1;

        String bigBlindLineIdentifier = "posts big blind ";

        for(String line : lastHand) {
            if(line.contains(bigBlindLineIdentifier)) {
                String bigBlindString = line.substring(line.indexOf(bigBlindLineIdentifier));
                bigBlindString = bigBlindString.replace(bigBlindLineIdentifier, "");

                if(bigBlindString.contains(" ")) {
                    bigBlindString = bigBlindString.substring(0, bigBlindString.indexOf(" "));
                }

                bigBlindString = bigBlindString.replaceAll("\\s+","");

                bigBlind = Double.valueOf(bigBlindString);
                break;
            }
        }

        return bigBlind;
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

    private List<String> getPreflopLines(List<String> lastGame) {
        List<String> preflopLines = new ArrayList<>();

        for(String line : lastGame) {
            if(line.contains("*** FLOP ***")) {
                break;
            } else {
                preflopLines.add(line);
            }
        }

        return preflopLines;
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
            if(line.contains("folds") && !line.contains("vegeta11223")) {
                opponentFolded = true;
                break;
            }
        }

        return opponentFolded;
    }
}
