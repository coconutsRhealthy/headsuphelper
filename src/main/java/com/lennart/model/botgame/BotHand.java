package com.lennart.model.botgame;

import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;

import java.util.*;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class BotHand {

    private GameVariablesFiller gameVariablesFiller;

    private double potSize;
    private double botStack;
    private double opponentStack;
    private double botTotalBetSize;
    private double opponentTotalBetSize;
    private double smallBlind;
    private double bigBlind;

    private boolean botIsButton;
    private String opponentPlayerName;
    private String opponentAction;
    private String street;

    private Card botHoleCard1;
    private Card botHoleCard2;
    private Card flopCard1;
    private Card flopCard2;
    private Card flopCard3;
    private Card turnCard;
    private Card riverCard;

    private List<Card> botHoleCards;
    private List<Card> board;
    private String streetAtPreviousActionRequest;

    private List<String> botActionHistory;

    private double botStackAtBeginningOfHand;
    private double opponentStackAtBeginningOfHand;

    private List<String> opponentActionHistory;

    private String botAction;

    public BotHand() {
        //default constructor
    }

    public BotHand(BotTable botTable) {
        gameVariablesFiller = new GameVariablesFiller(this);

        setSmallBlind();
        setBigBlind();
        setBotStack(true);
        setOpponentStack(true);
        setPotSize();
        setBotTotalBetSize();
        setOpponentTotalBetSize();
        setOpponentPlayerName();
        setBotIsButton(botTable);
        setBotHoleCard1();
        setBotHoleCard2();
        setBotHoleCards();
        setStreetAndPreviousStreet();
        setOpponentAction();
    }

    public void updateVariables(BotTable botTable) {
        gameVariablesFiller = new GameVariablesFiller(this);

        if(foldOrShowdownOccured()) {
            botTable.setBotHand(new BotHand(botTable));
        }

        setFlopCard1IfNecessary();
        setFlopCard2IfNecessary();
        setFlopCard3IfNecessary();
        setTurnCardIfNecessary();
        setRiverCardIfNecessary();
        setBoard();
        setBotStack(false);
        setOpponentStack(false);
        setPotSize();
        setBotTotalBetSize();
        setOpponentTotalBetSize();
        setStreetAndPreviousStreet();
        setOpponentAction();

        System.out.println("opponent action: " + opponentAction + " " + opponentTotalBetSize);
    }

    public void getNewBotAction() {
        //botAction = poker.getAction(eligibleActions, getStreet(), aiBotIsButton, getPotSizeInBb(), ruleBotAction, getFacingOdds(), getEffectiveStackInBb(), aiBotHasStrongDraw, aiBotHandStrength, getOpponentTypeString(ruleBot), getRuleBotBetSizeInBb(), getAiBotBetSizeInBb(), ruleBotStack / bigBlind, aiBotStack / bigBlind, board.isEmpty(), board);
        botAction = "";
        updateBotActionHistory(botAction);
        NetBetTableReader.performActionOnSite(botAction, 0);
    }

    private void updateBotActionHistory(String action) {
        if(botActionHistory == null) {
            botActionHistory = new ArrayList<>();
        }

        botActionHistory.add(street + " " + action);
    }

    private void updateOpponentActionHistory(String action) {
        if(opponentActionHistory == null) {
            opponentActionHistory = new ArrayList<>();
        }

        if(action == null) {
            opponentActionHistory.add(street + " null");
        } else {
            opponentActionHistory.add(street + " " + action);
        }
    }

    private void setBotHoleCard1() {
        botHoleCard1 = gameVariablesFiller.getBotHoleCard1();
    }

    private void setBotHoleCard2() {
        botHoleCard2 = gameVariablesFiller.getBotHoleCard2();
    }



    //main variables
    private void setBotStack(boolean initialize) {
        botStack = gameVariablesFiller.getBotStack();
    }

    private void setOpponentStack(boolean initialize) {
        opponentStack = gameVariablesFiller.getOpponentStack();
    }

    private void setPotSize() {
        potSize = gameVariablesFiller.getPotSize();
    }

    private void setBotTotalBetSize() {
        botTotalBetSize = gameVariablesFiller.getBotTotalBetSize();
    }

    private void setOpponentTotalBetSize() {
        opponentTotalBetSize = gameVariablesFiller.getOpponentTotalBetSize();
    }

    private void setOpponentPlayerName() {
        opponentPlayerName = gameVariablesFiller.getOpponentPlayerName();
    }

    private void setOpponentAction() {
        Map<String, String> actionsFromLastThreeChatLines = gameVariablesFiller.getActionsFromLastThreeChatLines();

        if(street.equals(streetAtPreviousActionRequest)) {
            if(street.equals("preflop") && botIsButton) {
                if(botActionHistory == null) {
                    opponentAction = null;
                } else {
                    opponentAction = actionsFromLastThreeChatLines.get("bottom");
                }
            } else {
                opponentAction = actionsFromLastThreeChatLines.get("bottom");
            }
        } else {
            if(botIsButton) {
                opponentAction = actionsFromLastThreeChatLines.get("bottom");
            } else {
                opponentAction = null;
            }
        }

        updateOpponentActionHistory(opponentAction);
    }

    private boolean foldOrShowdownOccured() {
        Map<String, String> actionsFromLastThreeChatLines = gameVariablesFiller.getActionsFromLastThreeChatLines();

        for (Map.Entry<String, String> entry : actionsFromLastThreeChatLines.entrySet()) {
            if(entry.getValue() != null && entry.getValue().equals("deal")) {
                System.out.println("Fold or showdown occured: true");
                return true;
            }
        }
        System.out.println("Fold or showdown occured: false");
        return false;
    }

    private void setSmallBlind() {
        smallBlind = gameVariablesFiller.getSmallBlind();
    }

    private void setBigBlind() {
        bigBlind = gameVariablesFiller.getBigBlind();
    }

    private void setBotIsButton(BotTable botTable) {
        botIsButton = gameVariablesFiller.isBotIsButton();
        botTable.addBooleanToBotIsButtonHistoryPerOpponentMap(opponentPlayerName, botIsButton);
    }

    private void setFlopCard1IfNecessary() {
        if(flopCard1 == null) {
            flopCard1 = gameVariablesFiller.getFlopCard1();
        }
    }

    private void setFlopCard2IfNecessary() {
        if(flopCard2 == null) {
            flopCard2 = gameVariablesFiller.getFlopCard2();
        }
    }

    private void setFlopCard3IfNecessary() {
        if(flopCard3 == null) {
            flopCard3 = gameVariablesFiller.getFlopCard3();
        }
    }

    private void setTurnCardIfNecessary() {
        if(turnCard == null) {
            turnCard = gameVariablesFiller.getTurnCard();
        }
    }

    private void setRiverCardIfNecessary() {
        if(riverCard == null) {
            riverCard = gameVariablesFiller.getRiverCard();
        }
    }

    private void setBotHoleCards() {
        botHoleCards = new ArrayList<>();
        botHoleCards.add(botHoleCard1);
        botHoleCards.add(botHoleCard2);
    }

    private void setBoard() {
        if(board == null && flopCard1 != null) {
            board = new ArrayList<>();
        }

        if(flopCard1 != null && !board.contains(flopCard1)) {
            board.add(flopCard1);
            board.add(flopCard2);
            board.add(flopCard3);
        }

        if(turnCard != null && !board.contains(turnCard)) {
            board.add(turnCard);
        }

        if(riverCard != null && !board.contains(riverCard)) {
            board.add(riverCard);
        }
    }

    private void setStreetAndPreviousStreet() {
        if(flopCard1 == null) {
            streetAtPreviousActionRequest = "preflop";
            street = "preflop";
        }
        if(flopCard1 != null && turnCard == null) {
            streetAtPreviousActionRequest = street;
            street = "flop";
        }
        if(turnCard != null && riverCard == null) {
            streetAtPreviousActionRequest = street;
            street = "turn";
        }
        if(riverCard != null) {
            streetAtPreviousActionRequest = street;
            street = "river";
        }
    }

    //default getters and setters
    public GameVariablesFiller getGameVariablesFiller() {
        return gameVariablesFiller;
    }

    public void setGameVariablesFiller(GameVariablesFiller gameVariablesFiller) {
        this.gameVariablesFiller = gameVariablesFiller;
    }

    public double getPotSize() {
        return potSize;
    }

    public void setPotSize(double potSize) {
        this.potSize = potSize;
    }

    public double getBotStack() {
        return botStack;
    }

    public void setBotStack(double botStack) {
        this.botStack = botStack;
    }

    public double getOpponentStack() {
        return opponentStack;
    }

    public void setOpponentStack(double opponentStack) {
        this.opponentStack = opponentStack;
    }

    public double getBotTotalBetSize() {
        return botTotalBetSize;
    }

    public void setBotTotalBetSize(double botTotalBetSize) {
        this.botTotalBetSize = botTotalBetSize;
    }

    public double getOpponentTotalBetSize() {
        return opponentTotalBetSize;
    }

    public void setOpponentTotalBetSize(double opponentTotalBetSize) {
        this.opponentTotalBetSize = opponentTotalBetSize;
    }

    public double getSmallBlind() {
        return smallBlind;
    }

    public void setSmallBlind(double smallBlind) {
        this.smallBlind = smallBlind;
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(double bigBlind) {
        this.bigBlind = bigBlind;
    }

    public boolean isBotIsButton() {
        return botIsButton;
    }

    public void setBotIsButton(boolean botIsButton) {
        this.botIsButton = botIsButton;
    }

    public String getOpponentPlayerName() {
        return opponentPlayerName;
    }

    public void setOpponentPlayerName(String opponentPlayerName) {
        this.opponentPlayerName = opponentPlayerName;
    }

    public String getOpponentAction() {
        return opponentAction;
    }

    public void setOpponentAction(String opponentAction) {
        this.opponentAction = opponentAction;
    }

    public Card getBotHoleCard1() {
        return botHoleCard1;
    }

    public void setBotHoleCard1(Card botHoleCard1) {
        this.botHoleCard1 = botHoleCard1;
    }

    public Card getBotHoleCard2() {
        return botHoleCard2;
    }

    public void setBotHoleCard2(Card botHoleCard2) {
        this.botHoleCard2 = botHoleCard2;
    }

    public Card getFlopCard1() {
        return flopCard1;
    }

    public void setFlopCard1(Card flopCard1) {
        this.flopCard1 = flopCard1;
    }

    public Card getFlopCard2() {
        return flopCard2;
    }

    public void setFlopCard2(Card flopCard2) {
        this.flopCard2 = flopCard2;
    }

    public Card getFlopCard3() {
        return flopCard3;
    }

    public void setFlopCard3(Card flopCard3) {
        this.flopCard3 = flopCard3;
    }

    public Card getTurnCard() {
        return turnCard;
    }

    public Card getRiverCard() {
        return riverCard;
    }

    public List<Card> getBotHoleCards() {
        return botHoleCards;
    }

    public void setBotHoleCards(List<Card> botHoleCards) {
        this.botHoleCards = botHoleCards;
    }

    public List<Card> getBoard() {
        return board;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetAtPreviousActionRequest() {
        return streetAtPreviousActionRequest;
    }

    public void setStreetAtPreviousActionRequest(String streetAtPreviousActionRequest) {
        this.streetAtPreviousActionRequest = streetAtPreviousActionRequest;
    }

    public List<String> getBotActionHistory() {
        return botActionHistory;
    }

    public void setBotActionHistory(List<String> botActionHistory) {
        this.botActionHistory = botActionHistory;
    }
}
