package com.lennart.model.action.actionbuilders.ai;

import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.card.Card;
import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LPO21630 on 5-3-2018.
 */
public class GameVariables {

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
    private boolean newHand;

    public GameVariables() {
        bigBlind = 0.02;

        //NetBetTableReader netBetTableReader = new NetBetTableReader(bigBlind);

        opponentName = "Sjaak";
        opponentStack = 2;
        opponentBetSize = 0.05;
        board = fillTheBoard();
        boardAsString = convertCardListToString(board);
        pot = 0;
        botBetSize = 0.02;
        botStack = 1.97;
        botHoleCards = fillBotHoleCards();
        botHoleCardsAsString = convertCardListToString(botHoleCards);
        botIsButton = false;
        opponentAction = "toFill";
        newHand = true;





//        bigBlind = 0.02;
//
//        NetBetTableReader netBetTableReader = new NetBetTableReader(bigBlind);
//
//        opponentName = netBetTableReader.getOpponentPlayerNameFromImage();
//        opponentStack = netBetTableReader.getOpponentStackFromImage();
//        opponentBetSize = netBetTableReader.getOpponentTotalBetSizeFromImage();
//        board = fillTheBoard();
//        pot = netBetTableReader.getPotSizeFromImage();
//        botBetSize = netBetTableReader.getBotTotalBetSizeFromImage();
//        botStack = netBetTableReader.getBotStackFromImage();
//        botHoleCards = fillBotHoleCards();
//        botIsButton = netBetTableReader.isBotButtonFromImage();
//        opponentAction = "toFill";
//        newHand = false;
    }

    public ActionVariables testName() {
        botHoleCards = convertStringToCardList(getBotHoleCardsAsString());
        board = convertStringToCardList(getBoardAsString());

        if(newHand) {
            OpponentIdentifier.updateNumberOfHandsPerOpponentMap(opponentName);
        }

        new OpponentIdentifier().updateCounts(opponentName, opponentAction,
                OpponentIdentifier.getNumberOfHandsPerOpponentMap().get(opponentName));

        return new ActionVariables(this);
    }

    private List<Card> fillTheBoard() {
        List<Card> board = new ArrayList<>();

//        board.add(new Card(8, 'd'));
//        board.add(new Card(7, 's'));
//        board.add(new Card(12, 'c'));

        return board;
    }

    private List<Card> fillBotHoleCards() {
        List<Card> holeCards = new ArrayList<>();

        holeCards.add(new Card(13, 'd'));
        holeCards.add(new Card(5, 's'));

        return holeCards;
    }

    private String convertCardListToString(List<Card> toConvert) {
        String convertedString = "";

        for(Card card : toConvert) {
            if(convertedString.length() == 0) {
                convertedString = convertedString + card.getRank() + card.getSuit();
            } else {
                convertedString = convertedString + " " + card.getRank() + card.getSuit();
            }
        }
        return convertedString;
    }

    private List<Card> convertStringToCardList(String cardsAsString) {
        List<Card> listToReturn = new ArrayList<>();

        if(cardsAsString.length() > 0) {
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

    public boolean isNewHand() {
        return newHand;
    }

    public void setNewHand(boolean newHand) {
        this.newHand = newHand;
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