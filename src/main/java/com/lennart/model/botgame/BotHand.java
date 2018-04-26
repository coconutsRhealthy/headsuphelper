package com.lennart.model.botgame;

import com.lennart.model.action.actionbuilders.ai.Poker;
import com.lennart.model.action.actionbuilders.ai.Sizing;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.OpponentIdentifier;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.handevaluation.PreflopHandStength;
import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;

import java.util.*;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class BotHand {

    private double bigBlind;
    private double potSize;
    private double botStack;
    private double opponentStack;
    private double botTotalBetSize;
    private double opponentTotalBetSize;

    private boolean botIsButton;
    private String opponentPlayerName;
    private String opponentAction;

    private String street;
    private String streetAtPreviousActionRequest;

    private Card botHoleCard1;
    private Card botHoleCard2;
    private Card flopCard1;
    private Card flopCard2;
    private Card flopCard3;
    private Card turnCard;
    private Card riverCard;
    private List<Card> botHoleCards;
    private List<Card> flopCards;
    private List<Card> board;

    private double botHandStrength;
    private boolean botHasStrongDraw;

    String botAction = "xx";
    double sizing = 0;
    String route = "xx";
    String opponentType = "xx";

    public BotHand() {
        //default constructor
    }

    public BotHand(String initialize, BotTable botTable) {
        readVariablesFromTable();
        botTable.updateNumberOfHandsPerOpponentMap(opponentPlayerName);
        setStreetAndPreviousStreet();
        calculateHandStrengthAndDraws();
        updateOpponentIdentifier(botTable);
    }

    public void updateVariables(BotTable botTable) {
        if(foldOrShowdownOccured()) {
            botTable.setBotHand(new BotHand("initialize", botTable));
        } else {
            readVariablesFromTable();
            setStreetAndPreviousStreet();
            calculateHandStrengthAndDraws();
            updateOpponentIdentifier(botTable);
        }
    }

    private void readVariablesFromTable() {
        bigBlind = 0.02;

        NetBetTableReader netBetTableReader = new NetBetTableReader(bigBlind);

        try {
            botStack = netBetTableReader.getBotStackFromImage();
            opponentStack = netBetTableReader.getOpponentStackFromImage();
        } catch (Exception e) {

        }

        try {
            potSize = netBetTableReader.getPotSizeFromImage(false, 0, 0);
        } catch (Exception e) {

        }

        botTotalBetSize = netBetTableReader.getBotTotalBetSizeFromImage();
        opponentTotalBetSize = netBetTableReader.getOpponentTotalBetSizeFromImage();
        opponentPlayerName = netBetTableReader.getOpponentPlayerNameFromImage();
        botIsButton = netBetTableReader.isBotButtonFromImage();

        if(botHoleCard1 == null) {
            botHoleCard1 = netBetTableReader.getBotHoleCard1FromImage();
            botHoleCard2 = netBetTableReader.getBotHoleCard2FromImage();
            botHoleCards = new ArrayList<>();
            botHoleCards.add(botHoleCard1);
            botHoleCards.add(botHoleCard2);
        }

        board = new ArrayList<>();

        if(flopCard1 == null) {
            flopCard1 = netBetTableReader.getFlopCard1FromImage();

            if(flopCard1 != null) {
                flopCard2 = netBetTableReader.getFlopCard2FromImage();
                flopCard3 = netBetTableReader.getFlopCard3FromImage();

                board.add(flopCard1);
                board.add(flopCard2);
                board.add(flopCard3);

                flopCards = new ArrayList<>();

                flopCards.add(flopCard1);
                flopCards.add(flopCard2);
                flopCards.add(flopCard3);
            }
        }

        if(flopCard1 != null) {
            turnCard = netBetTableReader.getTurnCardFromImage();
        }

        if(turnCard != null) {
            board.add(turnCard);
            riverCard = netBetTableReader.getRiverCardFromImage();

            if(riverCard != null) {
                board.add(riverCard);
            }
        }

        opponentAction = netBetTableReader.getOpponentAction();
    }

    public void getNewBotAction(BotTable botTable) {
        List<String> eligibleActions = getEligibleActions();
        String streetInMethod = street;
        boolean botIsButtonInMethod = botIsButton;
        double potSizeBb = potSize / bigBlind;
        String opponentActionInMethod = opponentAction;
        double facingOdds = getFacingOdds();
        double effectiveStack = getEffectiveStackInBb();
        boolean botHasStrongDrawInMethod = botHasStrongDraw;
        double botHandStrengthInMethod = botHandStrength;
        opponentType = new OpponentIdentifier().getOpponentType(opponentPlayerName, botTable.getNumberOfHandsPerOpponentMap().get(opponentPlayerName));
        double opponentBetsizeBb = opponentTotalBetSize / bigBlind;
        double botBetsizeBb = botTotalBetSize / bigBlind;
        double opponentStackBb = opponentStack / bigBlind;
        double botStackBb = botStack / bigBlind;
        boolean preflop = board.isEmpty();
        List<Card> boardInMethod = board;

        //TODO: fix met specific drawtypes
        botAction = new Poker().getAction(null, eligibleActions, streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod, facingOdds, effectiveStack, botHasStrongDrawInMethod, botHandStrengthInMethod, opponentType, opponentBetsizeBb, botBetsizeBb, opponentStackBb, botStackBb, preflop, boardInMethod, false, false, false, bigBlind, false, false, false, false, false, 0);

        if(botAction.equals("bet75pct") || botAction.equals("raise")) {
            sizing = new Sizing().getAiBotSizing(opponentTotalBetSize, botTotalBetSize, botStack, opponentStack, potSize, bigBlind, board);
        }

        //NetBetTableReader.performActionOnSite(botAction, sizing);
    }

    private List<String> getEligibleActions() {
        List<String> eligibleActions = new ArrayList<>();

        if(opponentAction != null) {
            if(opponentAction.contains("bet") || opponentAction.contains("raise")) {
                if(opponentStack == 0 || (botStack + botTotalBetSize) <= opponentTotalBetSize) {
                    eligibleActions.add("fold");
                    eligibleActions.add("call");
                } else {
                    eligibleActions.add("fold");
                    eligibleActions.add("call");
                    eligibleActions.add("raise");
                }
            } else {
                eligibleActions.add("check");
                eligibleActions.add("bet75pct");
            }
        } else {
            if(board.isEmpty()) {
                eligibleActions.add("fold");
                eligibleActions.add("call");
                eligibleActions.add("raise");
            } else {
                eligibleActions.add("check");
                eligibleActions.add("bet75pct");
            }
        }

        return eligibleActions;
    }

    private double getFacingOdds() {
        double facingOdds = (opponentTotalBetSize - botTotalBetSize) / (potSize + botTotalBetSize + opponentTotalBetSize);
        return facingOdds;
    }

    private double getEffectiveStackInBb() {
        if(botStack > opponentStack) {
            return opponentStack / bigBlind;
        }
        return botStack / bigBlind;
    }

    private void calculateHandStrengthAndDraws() {
        if(!street.equals(streetAtPreviousActionRequest)) {
            if(board.isEmpty()) {
                PreflopHandStength preflopHandStength = new PreflopHandStength();
                botHandStrength = preflopHandStength.getPreflopHandStength(botHoleCards);
                botHasStrongDraw = false;
            } else {
                BoardEvaluator boardEvaluator = new BoardEvaluator(board);
                HandEvaluator handEvaluator = new HandEvaluator(botHoleCards, boardEvaluator);
                botHandStrength = handEvaluator.getHandStrength(botHoleCards);
                botHasStrongDraw = handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")
                        || handEvaluator.hasDrawOfType("strongGutshot");
            }
        }
    }

    private void updateOpponentIdentifier(BotTable botTable) {
        new OpponentIdentifier().updateCounts(opponentPlayerName, opponentAction,
                botTable.getNumberOfHandsPerOpponentMap().get(opponentPlayerName));
    }

    private boolean foldOrShowdownOccured() {
        NetBetTableReader netBetTableReader = new NetBetTableReader(bigBlind);

        double botBetSize = netBetTableReader.getBotTotalBetSizeFromImage();
        double opponentBetSize = netBetTableReader.getOpponentTotalBetSizeFromImage();

        return botBetSize == bigBlind / 2 || opponentBetSize == bigBlind / 2;
    }

    private void setStreetAndPreviousStreet() {
        if(flopCard1 == null) {
            if(streetAtPreviousActionRequest == null) {
                streetAtPreviousActionRequest = "";
                street = "preflop";
            } else {
                streetAtPreviousActionRequest = "preflop";
                street = "preflop";
            }
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


    public double getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(double bigBlind) {
        this.bigBlind = bigBlind;
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

    public void setTurnCard(Card turnCard) {
        this.turnCard = turnCard;
    }

    public Card getRiverCard() {
        return riverCard;
    }

    public void setRiverCard(Card riverCard) {
        this.riverCard = riverCard;
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

    public void setBoard(List<Card> board) {
        this.board = board;
    }

    public double getBotHandStrength() {
        return botHandStrength;
    }

    public void setBotHandStrength(double botHandStrength) {
        this.botHandStrength = botHandStrength;
    }

    public boolean isBotHasStrongDraw() {
        return botHasStrongDraw;
    }

    public void setBotHasStrongDraw(boolean botHasStrongDraw) {
        this.botHasStrongDraw = botHasStrongDraw;
    }

    public List<Card> getFlopCards() {
        return flopCards;
    }

    public void setFlopCards(List<Card> flopCards) {
        this.flopCards = flopCards;
    }

    public String getBotAction() {
        return botAction;
    }

    public void setBotAction(String botAction) {
        this.botAction = botAction;
    }

    public double getSizing() {
        return sizing;
    }

    public void setSizing(double sizing) {
        this.sizing = sizing;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getOpponentType() {
        return opponentType;
    }

    public void setOpponentType(String opponentType) {
        this.opponentType = opponentType;
    }
}
