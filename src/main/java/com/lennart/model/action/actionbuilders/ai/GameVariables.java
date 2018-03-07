package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by LPO21630 on 5-3-2018.
 */
public class GameVariables {

    private double bigBlind;
    private String opponentName;
    private double opponentStack;
    private double opponentBetSize;
    private static Card flopCard1;
    private static Card flopCard2;
    private static Card flopCard3;
    private static Card turnCard;
    private static Card riverCard;
    private List<Card> board = new ArrayList<>();
    private String boardAsString;
    private double pot;
    private double botBetSize;
    private double botStack;
    private static Card botHoleCard1;
    private static Card botHoleCard2;
    private List<Card> botHoleCards = new ArrayList<>();
    private String botHoleCardsAsString;
    private boolean botIsButton;
    private String opponentAction;

    public GameVariables() {
        //default constructor;
    }

    public GameVariables(boolean newHand) throws Exception {
        bigBlind = 0.02;

        NetBetTableReader netBetTableReader = new NetBetTableReader(bigBlind);

        TimeUnit.MILLISECONDS.sleep(100);
        opponentName = netBetTableReader.getOpponentPlayerNameFromImage();

        if(newHand) {
            OpponentIdentifier.updateNumberOfHandsPerOpponentMap(opponentName);
            clearHoleCardsAndBoardCards();
        }

        TimeUnit.MILLISECONDS.sleep(100);
        opponentStack = netBetTableReader.getOpponentStackFromImage();
        TimeUnit.MILLISECONDS.sleep(100);
        opponentBetSize = netBetTableReader.getOpponentTotalBetSizeFromImage();

        if(!newHand) {
            board = fillTheBoard();
            boardAsString = convertCardListToString(board);
        }

        TimeUnit.MILLISECONDS.sleep(100);
        pot = netBetTableReader.getPotSizeFromImage();
        TimeUnit.MILLISECONDS.sleep(100);
        botBetSize = netBetTableReader.getBotTotalBetSizeFromImage();
        TimeUnit.MILLISECONDS.sleep(100);
        botStack = netBetTableReader.getBotStackFromImage();
        botHoleCards = fillBotHoleCards();
        botHoleCardsAsString = convertCardListToString(botHoleCards);
        TimeUnit.MILLISECONDS.sleep(100);
        botIsButton = netBetTableReader.isBotButtonFromImage();
        opponentAction = "toFill";
    }

    public ActionVariables testName() {
        botHoleCards = convertStringToCardList(botHoleCardsAsString);
        board = convertStringToCardList(boardAsString);

        new OpponentIdentifier().updateCounts(opponentName, opponentAction,
                OpponentIdentifier.getNumberOfHandsPerOpponentMap().get(opponentName));

        return new ActionVariables(this);
    }

    private List<Card> fillTheBoard() throws Exception {
        List<Card> boardInMethod = new ArrayList<>();

        NetBetTableReader netBetTableReader = new NetBetTableReader(bigBlind);

        if(flopCard1 == null) {
            TimeUnit.MILLISECONDS.sleep(200);
            flopCard1 = netBetTableReader.getFlopCard1FromImage();

            if(flopCard1 != null) {
                System.out.println("c");
                TimeUnit.MILLISECONDS.sleep(100);
                flopCard2 = netBetTableReader.getFlopCard2FromImage();
                TimeUnit.MILLISECONDS.sleep(100);
                flopCard3 = netBetTableReader.getFlopCard3FromImage();

                boardInMethod.add(flopCard1);
                boardInMethod.add(flopCard2);
                boardInMethod.add(flopCard3);
            }
        } else if(turnCard == null) {
            TimeUnit.MILLISECONDS.sleep(300);
            turnCard = netBetTableReader.getTurnCardFromImage();

            if(turnCard != null) {
                boardInMethod.add(flopCard1);
                boardInMethod.add(flopCard2);
                boardInMethod.add(flopCard3);
                boardInMethod.add(turnCard);
            }
        } else if(riverCard == null) {
            TimeUnit.MILLISECONDS.sleep(300);
            riverCard = netBetTableReader.getRiverCardFromImage();

            if(riverCard != null) {
                boardInMethod.add(flopCard1);
                boardInMethod.add(flopCard2);
                boardInMethod.add(flopCard3);
                boardInMethod.add(turnCard);
                boardInMethod.add(riverCard);
            }
        }

        return boardInMethod;
    }

    private void clearHoleCardsAndBoardCards() {
        botHoleCard1 = null;
        botHoleCard2 = null;
        flopCard1 = null;
        flopCard2 = null;
        flopCard3 = null;
        turnCard = null;
        riverCard = null;
    }

    private List<Card> fillBotHoleCards() {
        List<Card> botHoleCardsInMethod = new ArrayList<>();

        if(botHoleCard1 == null) {
            NetBetTableReader netBetTableReader = new NetBetTableReader(bigBlind);

            botHoleCard1 = netBetTableReader.getBotHoleCard1FromImage();
            botHoleCard2 = netBetTableReader.getBotHoleCard2FromImage();
        }

        botHoleCardsInMethod.add(botHoleCard1);
        botHoleCardsInMethod.add(botHoleCard2);
        return botHoleCardsInMethod;
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

    private List<Card> convertStringToCardList(String cardsAsString) {
        List<Card> listToReturn = new ArrayList<>();

        if(cardsAsString != null && cardsAsString.length() > 0) {
            String[] splitted = cardsAsString.split(" ");

            for(String s : splitted) {
                int rank = Integer.parseInt(s.substring(0, s.length() - 1));
                char suit = s.charAt(s.length() - 1);
                listToReturn.add(new Card(rank, suit));
            }
        }

        return listToReturn;
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
}