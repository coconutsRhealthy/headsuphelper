package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.sites.stars.StarsTableReader;

import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * Created by Lennart on 3/12/2018.
 */
public class ContinuousTable implements ContinuousTableable {

    private boolean opponentHasInitiative = false;
    private boolean pre3betOrPostRaisedPot = false;
    private boolean opponentDidPreflop4betPot = false;
    private List<String> allHandsPlayedAndPlayerNames = new ArrayList<>();
    private String starsLastHandNumber = "0";

    private List<Set<Card>> top10percentFlopCombos;
    private List<Set<Card>> top10percentTurnCombos;
    private List<Set<Card>> top10percentRiverCombos;

    private List<Double> allHandStrenghts = new ArrayList<>();

    private boolean botBluffActionDone;

    private List<DbSave> dbSaveList = new ArrayList<>();

    private Connection con;

    public static void main(String[] args) throws Exception {
        double bigBlind = 100;
        new ContinuousTable().runTableContinously(bigBlind);
    }

    public void runTableContinously(double bigBlindFromMainMethod) throws Exception {
        GameVariables gameVariables = new GameVariables();
        int numberOfActionRequests = 0;
        int milliSecondsTotal = 0;
        int printDotTotal = 0;

        long startTime = new Date().getTime();

        while(true) {
            TimeUnit.MILLISECONDS.sleep(100);
            milliSecondsTotal = milliSecondsTotal + 100;
            if(StarsTableReader.botIsToAct()) {
                numberOfActionRequests++;

                boolean isNewHand = isNewHand(bigBlindFromMainMethod);

                if(isNewHand) {
                    System.out.println("^^^^a " + getNumberOfHsAbove85() + " ^^^^");
                    System.out.println("^^^^b " + allHandStrenghts.size() + " ^^^^");

                    long currentTime = new Date().getTime();

                    if(currentTime - startTime > 14_400_000) {
                        System.out.println("3.4 hours have passed, force quit");
                        throw new RuntimeException();
                    }

                    System.out.println("is new hand");
                    opponentDidPreflop4betPot = false;
                    pre3betOrPostRaisedPot = false;
                    top10percentFlopCombos = new ArrayList<>();
                    top10percentTurnCombos = new ArrayList<>();
                    top10percentRiverCombos = new ArrayList<>();

                    doDbSaveUpdate();
                    dbSaveList = new ArrayList<>();

                    if(botBluffActionDone) {
                        boolean bluffActionWasSuccessful = wasBluffSuccessful(bigBlindFromMainMethod);
                        String opponentPlayerNameOfLastHand = allHandsPlayedAndPlayerNames.get(allHandsPlayedAndPlayerNames.size() - 1);
                        new PlayerBluffer().updateBluffDb(opponentPlayerNameOfLastHand, bluffActionWasSuccessful);
                        botBluffActionDone = false;
                    }

                    if(!allHandsPlayedAndPlayerNames.isEmpty()) {
                        String opponentPlayerNameOfLastHand = allHandsPlayedAndPlayerNames.get(allHandsPlayedAndPlayerNames.size() - 1);
                        new OpponentIdentifier().updateCountsFromHandhistoryDbLogic(opponentPlayerNameOfLastHand, bigBlindFromMainMethod);
                    }

                    gameVariables = new GameVariables(true);
                    allHandsPlayedAndPlayerNames.add(gameVariables.getOpponentName());
                } else {
                    gameVariables.fillFieldsSubsequent(true);
                }

                ActionVariables actionVariables = new ActionVariables(gameVariables, this, isNewHand);
                String action = actionVariables.getAction();

                if(action.equals("bet75pct") || action.equals("raise")) {
                    opponentHasInitiative = false;
                }

                double sizing = actionVariables.getSizing();

                doLogging(gameVariables, actionVariables, numberOfActionRequests);

                System.out.println();
                System.out.println("********************");
                System.out.println("Counter: " + numberOfActionRequests);
                System.out.println("Opponent Name: " + gameVariables.getOpponentName());
                System.out.println("Suggested action: "+ action);
                System.out.println("Sizing: " + sizing);
                System.out.println("Route: " + actionVariables.getRoute());
                System.out.println("Table: " + actionVariables.getTable());
                System.out.println("********************");
                System.out.println();

                if(gameVariables.getBoard() != null && gameVariables.getBoard().size() >= 3) {
                    allHandStrenghts.add(actionVariables.getBotHandStrength());
                }

                StarsTableReader.performActionOnSite(action, sizing);

                TimeUnit.MILLISECONDS.sleep(100);
            }

            if(milliSecondsTotal == 5000) {
                milliSecondsTotal = 0;
                System.out.print(".");
                printDotTotal++;

                if(printDotTotal == 30) {
                    StarsTableReader.saveScreenshotOfEntireScreen(new Date().getTime());

                    MouseKeyboard.moveMouseToLocation(1565, 909);
                    TimeUnit.MILLISECONDS.sleep(300);
                    MouseKeyboard.click(1565, 909);
                    TimeUnit.MILLISECONDS.sleep(500);
                    MouseKeyboard.moveMouseToLocation(20, 20);

                    printDotTotal = 0;
                    System.out.println();
                }
            }
        }
    }

    private void doLogging(GameVariables gameVariables, ActionVariables actionVariables, int numberOfActionRequests) throws Exception {
        StarsTableReader.saveScreenshotOfEntireScreen(numberOfActionRequests);

        String opponentStack = String.valueOf(gameVariables.getOpponentStack());
        String opponentBetSize = String.valueOf(gameVariables.getOpponentBetSize());
        String board = getCardListAsString(gameVariables.getBoard());
        String potSize = String.valueOf(gameVariables.getPot());
        String botBetSize = String.valueOf(gameVariables.getBotBetSize());
        String botStack = String.valueOf(gameVariables.getBotStack());
        String botHoleCards = getCardListAsString(gameVariables.getBotHoleCards());
        String opponentAction = gameVariables.getOpponentAction();
        String route = actionVariables.getRoute();
        String table = actionVariables.getTable();
        String suggestedAction = actionVariables.getAction();
        String sizing = String.valueOf(actionVariables.getSizing());

        PrintWriter writer = new PrintWriter("/Users/LennartMac/Documents/logging/" + numberOfActionRequests + ".txt", "UTF-8");

        writer.println("OpponentStack: " + opponentStack);
        writer.println("OpponentBetSize: " + opponentBetSize);
        writer.println("Board: " + board);
        writer.println("Potsize: " + potSize);
        writer.println("BotBetSize: " + botBetSize);
        writer.println("BotStack: " + botStack);
        writer.println("BotHoleCards: " + botHoleCards);
        writer.println("OpponentAction: " + opponentAction);
        writer.println();

        writer.println("------------------------");
        writer.println();

        writer.println("Route: " + route);
        writer.println("Table: " + table);
        writer.println("Action: " + suggestedAction);
        writer.println("Sizing: " + sizing);

        writer.close();
    }

    private int getNumberOfHsAbove85() {
        int counter = 0;

        for(double d : allHandStrenghts) {
            if(d >= 0.85) {
                counter++;
            }
        }

        return counter;
    }

    private String getCardListAsString(List<Card> cardList) {
        String cardListAsString = "initial";

        if(cardList != null && !cardList.isEmpty()) {
            cardListAsString = "";

            for(Card card : cardList) {
                cardListAsString = cardListAsString + card.getRank() + card.getSuit() + " ";
            }
        }
        return cardListAsString;
    }

    private boolean isNewHand(double bigBlind) throws Exception {
        boolean isNewHand;

        HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();
        List<String> total = handHistoryReaderStars.readTextFile();
        List<String> lastHand = handHistoryReaderStars.getLinesOfLastGame(total, 1, bigBlind);
        String lastHandNumber = handHistoryReaderStars.getHandNumber(lastHand.get(0));

        isNewHand = !starsLastHandNumber.equals(lastHandNumber);
        starsLastHandNumber = lastHandNumber;

        return isNewHand;
    }

    private boolean wasBluffSuccessful(double bigBlind) throws Exception {
        boolean bluffSuccessful = false;

        HandHistoryReaderStars handHistoryReaderStars = new HandHistoryReaderStars();
        List<String> total = handHistoryReaderStars.readTextFile();
        List<String> lastHand = handHistoryReaderStars.getLinesOfLastGame(total, 1, bigBlind);
        Collections.reverse(lastHand);

        for(String line : lastHand) {
            if(line.contains("folds") && !line.contains("vegeta11223")) {
                bluffSuccessful = true;
                break;
            }
        }

        return bluffSuccessful;
    }

    private void doDbSaveUpdate() throws Exception {
        for(DbSave dbSave : dbSaveList) {
//            int entry = getHighestIntEntry("dbsave");
//            String action = dbSave.getAction();
//            String board = "";
//            double sizing = dbSave.getSizing();
//            double oppFoldStat = dbSave.getOppFoldStat();
//            String oppType = dbSave.getOppType();
//            int bluffSu

            initializeDbConnection();

            Statement st = con.createStatement();

            st.executeUpdate("INSERT INTO opponentidentifier (" +
                    "entry, " +
                    "action, " +
                    "board, " +
                    "sizing, " +
                    "opp_fold_stat, " +
                    "bluff_success, " +
                    "stake, " +
                    "number_of_hands, " +
                    "opp_looseness, " +
                    "opp_aggressiveness, " +
                    "handstrength, " +
                    "opp_name, " +
                    "date, " +
                    "opp_type, " +
                    "showdown, " +
                    "won_hand) " +
                        "VALUES ('" + "zzz" + "', '-1')");





//            private String action;
//            private List<Card> board;
//            private double sizing;
//            private double oppFoldStat;
//            private String oppType;
//            private int bluffSuccessNumber;
//            private String stake;
//            private double numberOfHands;
//            private double oppLooseness;
//            private double oppAggressiveness;
//            private double handStrength;
//            private String opponentName;
//            private String date;
//
//            private boolean showDown;
//            private boolean winLoss;

//            if(action.equals("call")) {
//
//
//
//            }



        }



    }

    private boolean showdownOccured() {
        return false;
    }

    private boolean botWonHand() {
        return false;
    }

    private int getHighestIntEntry(String database) throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM " + database + " ORDER BY entry DESC;");
        ResultSet rs = st.executeQuery(sql);

        if(rs.next()) {
            int highestIntEntry = rs.getInt("entry");
            st.close();
            rs.close();
            return highestIntEntry;
        }
        st.close();
        rs.close();
        return 0;
    }

    private boolean wasValueBetOrRaiseSuccessful() {
        return false;
    }

    private boolean wasCallSuccessful() {
        return false;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

    @Override
    public boolean isOpponentHasInitiative() {
        return opponentHasInitiative;
    }

    @Override
    public void setOpponentHasInitiative(boolean opponentHasInitiative) {
        this.opponentHasInitiative = opponentHasInitiative;
    }

    @Override
    public boolean isOpponentDidPreflop4betPot() {
        return opponentDidPreflop4betPot;
    }

    @Override
    public void setOpponentDidPreflop4betPot(boolean opponentDidPreflop4betPot) {
        this.opponentDidPreflop4betPot = opponentDidPreflop4betPot;
    }

    @Override
    public boolean isPre3betOrPostRaisedPot() {
        return pre3betOrPostRaisedPot;
    }

    @Override
    public void setPre3betOrPostRaisedPot(boolean pre3betOrPostRaisedPot) {
        this.pre3betOrPostRaisedPot = pre3betOrPostRaisedPot;
    }

    public void setStarsLastHandNumber(String starsLastHandNumber) {
        this.starsLastHandNumber = starsLastHandNumber;
    }

    public List<Set<Card>> getTop10percentFlopCombos() {
        return top10percentFlopCombos;
    }

    public void setTop10percentFlopCombos(List<Set<Card>> top10percentFlopCombos) {
        this.top10percentFlopCombos = top10percentFlopCombos;
    }

    public List<Set<Card>> getTop10percentTurnCombos() {
        return top10percentTurnCombos;
    }

    public void setTop10percentTurnCombos(List<Set<Card>> top10percentTurnCombos) {
        this.top10percentTurnCombos = top10percentTurnCombos;
    }

    public List<Set<Card>> getTop10percentRiverCombos() {
        return top10percentRiverCombos;
    }

    public void setTop10percentRiverCombos(List<Set<Card>> top10percentRiverCombos) {
        this.top10percentRiverCombos = top10percentRiverCombos;
    }

    public boolean isBotBluffActionDone() {
        return botBluffActionDone;
    }

    public void setBotBluffActionDone(boolean botBluffActionDone) {
        this.botBluffActionDone = botBluffActionDone;
    }

    public List<DbSave> getDbSaveList() {
        return dbSaveList;
    }

    public void setDbSaveList(List<DbSave> dbSaveList) {
        this.dbSaveList = dbSaveList;
    }
}
