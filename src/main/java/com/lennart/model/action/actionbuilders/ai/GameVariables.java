package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.botgame.MouseKeyboard;
import com.lennart.model.card.Card;
import com.lennart.model.handtracker.ActionRequest;
import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;
import com.lennart.model.imageprocessing.sites.stars.StarsTableReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by LPO21630 on 5-3-2018.
 */
public class GameVariables implements GameVariable {

    private double bigBlind;
    private String opponentName;
    private double opponentStack;
    private double opponentBetSize;
    private Card flopCard1;
    private Card flopCard2;
    private Card flopCard3;
    private Card turnCard;
    private Card riverCard;
    private List<Card> board = new ArrayList<>();
    private String boardAsString;
    private double pot;
    private double botBetSize;
    private double botStack;
    private Card botHoleCard1;
    private Card botHoleCard2;
    private List<Card> botHoleCards = new ArrayList<>();
    private String botHoleCardsAsString;
    private boolean botIsButton;
    private String opponentAction;
    private ActionVariables actionVariables;

    private boolean drawBettingActionDone;
    private boolean previousBluffAction;

    private List<ActionRequest> allActionRequestsOfHand;

    public GameVariables() {
        //default constructor
    }

    public GameVariables(boolean stars) throws Exception {
        bigBlind = 0.50;

        StarsTableReader starsTableReader = new StarsTableReader();

        botStack = starsTableReader.getBotStackFromImage();
        opponentStack = starsTableReader.getOpponentStackFromImage();
        botHoleCard1 = starsTableReader.getBotHoleCard1FromImage();
        botHoleCard2 = starsTableReader.getBotHoleCard2FromImage();
        opponentName = starsTableReader.getOpponentPlayerNameFromImage();
        botIsButton = starsTableReader.leftPlayerIsButton();
        botHoleCards.add(botHoleCard1);
        botHoleCards.add(botHoleCard2);

        double topPotSize = starsTableReader.getTopPotsizeFromImage();
        allActionRequestsOfHand = new ArrayList<>();
        ActionRequest actionRequest = new ActionRequest(new ArrayList<>(), topPotSize, board, botIsButton, bigBlind);
        allActionRequestsOfHand.add(actionRequest);

        opponentBetSize = actionRequest.getMostRecentActionRoundOfPLayer(actionRequest.getActionsSinceLastRequest(), "opponent").getTotalOpponentBetSize();
        botBetSize = actionRequest.getMostRecentActionRoundOfPLayer(actionRequest.getActionsSinceLastRequest(), "bot").getTotalOpponentBetSize();
        pot = topPotSize - opponentBetSize - botBetSize;
        opponentAction = actionRequest.getMostRecentActionRoundOfPLayer(actionRequest.getActionsSinceLastRequest(), "bot").getAction();
    }

    public void fillFieldsSubsequent(boolean stars) throws Exception {
        StarsTableReader starsTableReader = new StarsTableReader();

        botStack = starsTableReader.getBotStackFromImage();
        opponentStack = starsTableReader.getOpponentStackFromImage();
        fillTheStarsBoard();

        double topPotSize = starsTableReader.getTopPotsizeFromImage();

        List<ActionRequest> copyOfAllActionRequests = new ArrayList<>();
        copyOfAllActionRequests.addAll(allActionRequestsOfHand);
        ActionRequest actionRequest = new ActionRequest(copyOfAllActionRequests, topPotSize, board, botIsButton, bigBlind);
        allActionRequestsOfHand.add(actionRequest);

        opponentBetSize = actionRequest.getMostRecentActionRoundOfPLayer(actionRequest.getActionsSinceLastRequest(), "opponent").getTotalOpponentBetSize();
        botBetSize = actionRequest.getMostRecentActionRoundOfPLayer(actionRequest.getActionsSinceLastRequest(), "bot").getTotalOpponentBetSize();
        pot = topPotSize - opponentBetSize - botBetSize;
        opponentAction = actionRequest.getMostRecentActionRoundOfPLayer(actionRequest.getActionsSinceLastRequest(), "bot").getAction();
    }

    public GameVariables(String opponentName) throws Exception {
        bigBlind = 0.02;

        NetBetTableReader netBetTableReader = new NetBetTableReader(bigBlind);
        botStack = netBetTableReader.getBotStackFromImage();

        TimeUnit.MILLISECONDS.sleep(200);
        opponentStack = netBetTableReader.getOpponentStackFromImage();
        TimeUnit.MILLISECONDS.sleep(200);
        opponentBetSize = netBetTableReader.getOpponentTotalBetSizeFromImage();
        TimeUnit.MILLISECONDS.sleep(200);
        botBetSize = netBetTableReader.getBotTotalBetSizeFromImage();
        TimeUnit.MILLISECONDS.sleep(200);
        pot = netBetTableReader.getPotSizeFromImage(!board.isEmpty(), opponentBetSize, botBetSize);

        TimeUnit.MILLISECONDS.sleep(200);
        botIsButton = netBetTableReader.isBotButtonFromImage();

        TimeUnit.MILLISECONDS.sleep(200);
        opponentAction = netBetTableReader.getOpponentAction();

        //maak tafel klein
        mediumSizeTable();
        TimeUnit.MILLISECONDS.sleep(60);

        fillBotHoleCards();

        this.opponentName = opponentName;
        //OpponentIdentifier.updateNumberOfHandsPerOpponentMap(opponentName);
    }

    public void fillFieldsSubsequent() throws Exception {
        NetBetTableReader netBetTableReader = new NetBetTableReader(bigBlind);
        botStack = netBetTableReader.getBotStackFromImage();

        TimeUnit.MILLISECONDS.sleep(200);
        opponentStack = netBetTableReader.getOpponentStackFromImage();
        TimeUnit.MILLISECONDS.sleep(200);
        opponentBetSize = netBetTableReader.getOpponentTotalBetSizeFromImage();
        TimeUnit.MILLISECONDS.sleep(200);
        botBetSize = netBetTableReader.getBotTotalBetSizeFromImage();
        TimeUnit.MILLISECONDS.sleep(200);
        pot = netBetTableReader.getPotSizeFromImage(!board.isEmpty(), opponentBetSize, botBetSize);

        TimeUnit.MILLISECONDS.sleep(200);
        opponentAction = netBetTableReader.getOpponentAction();

        //maak tafel klein
        mediumSizeTable();
        TimeUnit.MILLISECONDS.sleep(60);

        double potNewStyle = netBetTableReader.getPotSizeFromImageNewStyle(!board.isEmpty(), opponentBetSize, botBetSize);
        System.out.println();
        System.out.println("potsize old style: " + pot);
        System.out.println("potsize new style: " + potNewStyle);
        System.out.println();

        if(potNewStyle == 0 && pot != 0) {
            //nothing
        } else {
            pot = potNewStyle;
        }

        TimeUnit.MILLISECONDS.sleep(60);

        TimeUnit.MILLISECONDS.sleep(500);

        fillTheBoard();
    }

    private void fillTheBoard() throws Exception {
        NetBetTableReader netBetTableReader = new NetBetTableReader(bigBlind);

        if(flopCard1 == null) {
            for(int i = 0; i < 10; i++) {
                flopCard1 = netBetTableReader.getFlopCard1FromImage();
                if(flopCard1 != null) {
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(50);
            }

            if(flopCard1 != null) {
                while(flopCard2 == null) {
                    flopCard2 = netBetTableReader.getFlopCard2FromImage();
                }

                while(flopCard3 == null) {
                    flopCard3 = netBetTableReader.getFlopCard3FromImage();
                }

                board.add(flopCard1);
                board.add(flopCard2);
                board.add(flopCard3);
            }
        } else if(turnCard == null) {
            TimeUnit.MILLISECONDS.sleep(300);
            turnCard = netBetTableReader.getTurnCardFromImage();

            if(turnCard != null) {
                board.add(turnCard);
            }
        } else if(riverCard == null) {
            TimeUnit.MILLISECONDS.sleep(300);
            riverCard = netBetTableReader.getRiverCardFromImage();

            if(riverCard != null) {
                board.add(riverCard);
            }
        }
    }

    private void fillTheStarsBoard() throws Exception {
        StarsTableReader starsTableReader = new StarsTableReader();

        if(flopCard1 == null) {
            for(int i = 0; i < 10; i++) {
                flopCard1 = starsTableReader.getFlopCard1FromImage();
                if(flopCard1 != null) {
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(50);
            }

            if(flopCard1 != null) {
                while(flopCard2 == null) {
                    flopCard2 = starsTableReader.getFlopCard2FromImage();
                }

                while(flopCard3 == null) {
                    flopCard3 = starsTableReader.getFlopCard3FromImage();
                }

                board.add(flopCard1);
                board.add(flopCard2);
                board.add(flopCard3);
            }
        } else if(turnCard == null) {
            TimeUnit.MILLISECONDS.sleep(300);
            turnCard = starsTableReader.getTurnCardFromImage();

            if(turnCard != null) {
                board.add(turnCard);
            }
        } else if(riverCard == null) {
            TimeUnit.MILLISECONDS.sleep(300);
            riverCard = starsTableReader.getRiverCardFromImage();

            if(riverCard != null) {
                board.add(riverCard);
            }
        }
    }

    private void fillBotHoleCards() throws Exception {
        if(botHoleCard1 == null) {
            NetBetTableReader netBetTableReader = new NetBetTableReader(bigBlind);

            TimeUnit.MILLISECONDS.sleep(200);
            botHoleCard1 = netBetTableReader.getBotHoleCard1FromImage();
            TimeUnit.MILLISECONDS.sleep(200);
            botHoleCard2 = netBetTableReader.getBotHoleCard2FromImage();
        }

        botHoleCards.add(botHoleCard1);
        botHoleCards.add(botHoleCard2);
    }

    private String convertCardListToString(List<Card> toConvert) {
        String convertedString = "";

        for(Card card : toConvert) {
            if(convertedString.length() == 0) {
                if(card == null) {
                    convertedString = convertedString + null;
                } else {
                    convertedString = convertedString + card.getRank() + card.getSuit();
                }
            } else {
                if(card == null) {
                    convertedString = convertedString + " " + null;
                } else {
                    convertedString = convertedString + " " + card.getRank() + card.getSuit();
                }
            }
        }
        return convertedString;
    }

    private List<Card> convertStringToCardList(String cardsAsString, String holeCardsOrBoard) {
        List<Card> listToReturn = new ArrayList<>();

        if(cardsAsString != null && cardsAsString.length() > 0) {
            String[] splitted = cardsAsString.split(" ");

            for(String s : splitted) {
                int rank = Integer.parseInt(s.substring(0, s.length() - 1));
                char suit = s.charAt(s.length() - 1);
                listToReturn.add(new Card(rank, suit));
            }
        }

        if(holeCardsOrBoard.equals("holeCards")) {
            setHoleCardsBasedOnString(listToReturn);
        } else if(holeCardsOrBoard.equals("board")) {
            setBoardCardsBasedOnString(listToReturn);
        }

        return listToReturn;
    }

    private void setHoleCardsBasedOnString(List<Card> list) {
        botHoleCard1 = list.get(0);
        botHoleCard2 = list.get(1);
    }

    private void setBoardCardsBasedOnString(List<Card> list) {
        int size = list.size();

        if(list.isEmpty()) {
            flopCard1 = null;
            flopCard2 = null;
            flopCard3 = null;
            turnCard = null;
            riverCard = null;
        } else if(size == 3) {
            flopCard1 = list.get(0);
            flopCard2 = list.get(1);
            flopCard3 = list.get(2);
            turnCard = null;
            riverCard = null;
        } else if(size == 4) {
            flopCard1 = list.get(0);
            flopCard2 = list.get(1);
            flopCard3 = list.get(2);
            turnCard = list.get(3);
            riverCard = null;
        } else if(size == 5) {
            flopCard1 = list.get(0);
            flopCard2 = list.get(1);
            flopCard3 = list.get(2);
            turnCard = list.get(3);
            riverCard = list.get(4);
        }
    }

    private void mediumSizeTable() {
        MouseKeyboard.click(1269, 25);
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(double bigBlind) {
        this.bigBlind = bigBlind;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public double getOpponentStack() {
        return opponentStack;
    }

    public void setOpponentStack(double opponentStack) {
        this.opponentStack = opponentStack;
    }

    public double getOpponentBetSize() {
        return opponentBetSize;
    }

    public void setOpponentBetSize(double opponentBetSize) {
        this.opponentBetSize = opponentBetSize;
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

    public void setTurnCard(Card turnCard) {
        this.turnCard = turnCard;
    }

    public Card getRiverCard() {
        return riverCard;
    }

    public void setRiverCard(Card riverCard) {
        this.riverCard = riverCard;
    }

    public List<Card> getBoard() {
        return board;
    }

    public void setBoard(List<Card> board) {
        this.board = board;
    }

    public double getPot() {
        return pot;
    }

    public void setPot(double pot) {
        this.pot = pot;
    }

    public double getBotBetSize() {
        return botBetSize;
    }

    public void setBotBetSize(double botBetSize) {
        this.botBetSize = botBetSize;
    }

    public double getBotStack() {
        return botStack;
    }

    public void setBotStack(double botStack) {
        this.botStack = botStack;
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

    public List<Card> getBotHoleCards() {
        return botHoleCards;
    }

    public void setBotHoleCards(List<Card> botHoleCards) {
        this.botHoleCards = botHoleCards;
    }

    @Override
    public boolean isBotIsButton() {
        return botIsButton;
    }

    public void setBotIsButton(boolean botIsButton) {
        this.botIsButton = botIsButton;
    }

    public String getOpponentAction() {
        return opponentAction;
    }

    public void setOpponentAction(String opponentAction) {
        this.opponentAction = opponentAction;
    }

    public String getBoardAsString() {
        return boardAsString;
    }

    public void setBoardAsString(String boardAsString) {
        this.boardAsString = boardAsString;
    }

    public String getBotHoleCardsAsString() {
        return botHoleCardsAsString;
    }

    public void setBotHoleCardsAsString(String botHoleCardsAsString) {
        this.botHoleCardsAsString = botHoleCardsAsString;
    }

    public ActionVariables getActionVariables() {
        return actionVariables;
    }

    public void setActionVariables(ActionVariables actionVariables) {
        this.actionVariables = actionVariables;
    }

    @Override
    public boolean isDrawBettingActionDone() {
        return drawBettingActionDone;
    }

    @Override
    public void setDrawBettingActionDone(boolean drawBettingActionDone) {
        this.drawBettingActionDone = drawBettingActionDone;
    }

    @Override
    public boolean isPreviousBluffAction() {
        return previousBluffAction;
    }

    @Override
    public void setPreviousBluffAction(boolean previousBluffAction) {
        this.previousBluffAction = previousBluffAction;
    }
}