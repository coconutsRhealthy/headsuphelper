package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.sites.netbet.NetBetTableOpener;
import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;

import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by Lennart on 3/12/2018.
 */
public class ContinuousTable {

    private boolean opponentHasInitiative = false;

    public static void main(String[] args) throws Exception {
        new ContinuousTable().runTableContinously();
    }

    public void runTableContinously() throws Exception {
        GameVariables gameVariables = new GameVariables();
        int numberOfActionRequests = 0;
        int milliSecondsTotal = 0;
        int printDotTotal = 0;

        while(true) {
            TimeUnit.MILLISECONDS.sleep(100);
            milliSecondsTotal = milliSecondsTotal + 100;
            if(NetBetTableReader.botIsToAct()) {
                numberOfActionRequests++;

                if(NetBetTableReader.isNewHand()) {
                    System.out.println("is new hand");
                    String opponentName = String.valueOf(Math.random());

                    try {
                        if(new HandHistoryReader().lastModifiedFileIsLessThanTenMinutesAgo
                                ("C:/Users/Lennart/AppData/Local/NetBet Poker/data/COCONUT555/History/Data/Tables")) {
                            opponentName = new OpponentIdentifier().updateCountsFromHandhistoryAndGetOpponentPlayerName();
                        }
                    } catch (Exception e) {
                        System.out.println("error in xml reader");
                        opponentName = "asdfgghhj";
                    }

                    gameVariables = new GameVariables(opponentName);
                } else {
                    gameVariables.fillFieldsSubsequent();
                }

                ActionVariables actionVariables = new ActionVariables(gameVariables, this);
                String action = actionVariables.getAction();
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

                if(forceQuitIfOpponentStackIsPlus200bbBiggerThanBotStack(gameVariables.getBotStack() / gameVariables.getBigBlind(),
                        gameVariables.getOpponentStack() / gameVariables.getBigBlind())) {
                    runTableContinously();
                }

                NetBetTableReader.performActionOnSite(action, sizing);

                TimeUnit.MILLISECONDS.sleep(200);
                maximizeTable();
                TimeUnit.MILLISECONDS.sleep(100);
            }

            if(milliSecondsTotal == 5000) {
                milliSecondsTotal = 0;
                System.out.print(".");
                printDotTotal++;

                if(printDotTotal == 30) {
                    NetBetTableReader.saveScreenshotOfEntireScreen(new Date().getTime());

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
        NetBetTableReader.saveScreenshotOfEntireScreen(numberOfActionRequests);

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

        PrintWriter writer = new PrintWriter("C:/Users/Lennart/Documents/develop/logging/" + numberOfActionRequests + ".txt", "UTF-8");

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

    private void maximizeTable() {
        MouseKeyboard.click(983, 16);
    }

    private boolean forceQuitIfOpponentStackIsPlus200bbBiggerThanBotStack(double botStackBb, double opponentStackBb) {
        if(opponentStackBb - botStackBb > 200 && opponentStackBb != 0 && botStackBb != 0) {
            System.out.println("Difference between opponentStack and botStack got more than 200bb. Force table quit.");

            try {
                TimeUnit.SECONDS.sleep(60);
                NetBetTableOpener.startNewTable();
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean isOpponentHasInitiative() {
        return opponentHasInitiative;
    }

    public void setOpponentHasInitiative(boolean opponentHasInitiative) {
        this.opponentHasInitiative = opponentHasInitiative;
    }
}
