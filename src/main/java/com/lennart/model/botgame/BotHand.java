package com.lennart.model.botgame;

import com.lennart.model.action.Action;
import com.lennart.model.action.Actionable;
import com.lennart.model.card.Card;
import com.lennart.model.rangebuilder.RangeBuildable;
import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class BotHand implements RangeBuildable, Actionable {

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

    private Card botHoleCard1;
    private Card botHoleCard2;
    private Card flopCard1;
    private Card flopCard2;
    private Card flopCard3;
    private Card turnCard;
    private Card riverCard;

    private Action botAction;

    //extra variables
    private List<Card> botHoleCards;
    private List<Card> flopCards;
    private Set<Card> knownGameCards;
    private List<Card> board;
    private Set<Set<Card>> opponentRange;
    private boolean opponentPreflopStatsDoneForHand;
    private double handsOpponentOopFacingPreflop2bet;
    private double handsOpponentOopCall2bet;
    private double handsOpponentOop3bet;
    private double opponentPreCall2betStat;
    private double opponentPre3betStat;
    private boolean opponentLastActionWasPreflop;
    private double opponentFormerTotalCallAmount;
    private String street;
    private boolean previousBluffAction;
    private boolean drawBettingActionDone;
    private String botWrittenAction;

    public BotHand() {
        //default constructor
    }

    public BotHand(String initialize) {
        gameVariablesFiller = new GameVariablesFiller();

        setPotSize();
        setBotStack();
        setOpponentStack();
        setBotTotalBetSize();
        setOpponentTotalBetSize();
        setOpponentPlayerName();
        setOpponentAction();
        setBotIsButton();
        setBotHoleCard1();
        setBotHoleCard2();
        setSmallBlind();
        setBigBlind();

        knownGameCards = new HashSet<>();
        knownGameCards.add(botHoleCard1);
        knownGameCards.add(botHoleCard2);

        botHoleCards = new ArrayList<>();
        botHoleCards.add(botHoleCard1);
        botHoleCards.add(botHoleCard2);

        setOpponentLastActionWasPreflop();
    }

    public void getNewBotAction() {
        RangeBuilder rangeBuilder = new RangeBuilder(this);
        opponentRange = rangeBuilder.getOpponentRange();
        botAction = new Action(this, rangeBuilder);
        botWrittenAction = botAction.getWrittenAction();
    }

    public BotHand updateVariables() {
        gameVariablesFiller.initializeAndRefreshRelevantVariables(street);

        setOpponentAction();

        if(opponentAction.equals("fold") || (opponentAction.equals("call") && street.equals("river"))) {
            return new BotHand("initialize");
        }

        setPotSize();
        setBotStack();
        setOpponentStack();
        setBotTotalBetSize();
        setOpponentTotalBetSize();

        setFlopCard1IfNecessary();
        setFlopCard2IfNecessary();
        setFlopCard3IfNecessary();
        setTurnCardIfNecessary();
        setRiverCardIfNecessary();

        setDerivedVariables();
        return this;
    }

    //main variables
    private void setPotSize() {
        potSize = gameVariablesFiller.getPotSize();
    }

    private void setBotStack() {
        botStack = gameVariablesFiller.getBotStack();
    }

    private void setOpponentStack() {
        opponentStack = gameVariablesFiller.getOpponentStack();
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
        opponentAction = gameVariablesFiller.getOpponentAction();
    }

    private void setBotHoleCard1() {
        botHoleCard1 = gameVariablesFiller.getBotHoleCard1();
    }

    private void setBotHoleCard2() {
        botHoleCard2 = gameVariablesFiller.getBotHoleCard2();
    }

    private void setSmallBlind() {
        smallBlind = gameVariablesFiller.getSmallBlind();
    }

    private void setBigBlind() {
        bigBlind = gameVariablesFiller.getBigBlind();
    }

    private void setBotIsButton() {
        botIsButton = gameVariablesFiller.isBotIsButton();
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

    //derived variables form main variables
    private void setDerivedVariables() {
        setBotHoleCards();
        setFlopCards();
        setKnownGameCards();
        setOpponentAction();
        setBoard();
        calculateOpponentPreflopStats();
        setOpponentLastActionWasPreflop();
        setOpponentFormerTotalCallAmount();
        setStreet();
    }

    private void setBotHoleCards() {
        if(botHoleCards == null && botHoleCard1 != null && botHoleCard2 != null) {
            botHoleCards = new ArrayList<>();
            botHoleCards.add(botHoleCard1);
            botHoleCards.add(botHoleCard2);
        }
    }

    private void setFlopCards() {
        if(flopCards == null && flopCard1 != null && flopCard2 != null && flopCard3 != null) {
            flopCards = new ArrayList<>();
            flopCards.add(flopCard1);
            flopCards.add(flopCard2);
            flopCards.add(flopCard3);
        }
    }

    private void setKnownGameCards() {
        if(flopCards != null) {
            knownGameCards.addAll(flopCards);
        }
        if(turnCard != null) {
            knownGameCards.add(turnCard);
        }
        if(riverCard != null) {
            knownGameCards.add(riverCard);
        }
    }

    private void setBoard() {
        if(board == null) {
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

    private void calculateOpponentPreflopStats() {
        if(!opponentPreflopStatsDoneForHand) {
            if(board == null && botIsButton && botAction.getWrittenAction().contains("raise") && opponentTotalBetSize == bigBlind) {
                handsOpponentOopFacingPreflop2bet++;
                if(botAction.equals("call")) {
                    handsOpponentOopCall2bet++;
                }
                if(botAction.equals("raise")) {
                    handsOpponentOop3bet++;
                }
            }
            opponentPreCall2betStat = handsOpponentOopCall2bet / handsOpponentOopFacingPreflop2bet;
            opponentPre3betStat = handsOpponentOop3bet / handsOpponentOopFacingPreflop2bet;
            opponentPreflopStatsDoneForHand = true;
        }
    }

    private void setOpponentLastActionWasPreflop() {
        if(flopCard1 == null) {
            opponentLastActionWasPreflop = true;
        } else {
            opponentLastActionWasPreflop = false;
        }
    }

    private void setOpponentFormerTotalCallAmount() {
        if(opponentAction.equals("call")) {
            opponentFormerTotalCallAmount = botTotalBetSize;
        }
    }

    private void setStreet() {
        if(flopCard1 == null) {
            street = "preflop";
        }
        if(flopCard1 != null && turnCard == null) {
            street = "flop";
        }
        if(turnCard != null && riverCard == null) {
            street = "turn";
        }
        if(riverCard != null) {
            street = "river";
        }
    }

    @Override
    public void removeHoleCardsFromKnownGameCards() {
        knownGameCards.removeAll(botHoleCards);
    }

    @Override
    public void addHoleCardsToKnownGameCards() {
        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(botHoleCards);

        knownGameCards.addAll(holeCardsAsSet);
    }

    public boolean isOnlyCallRangeNeeded() {
        return false;
    }

    //default getters and setters
    public GameVariablesFiller getGameVariablesFiller() {
        return gameVariablesFiller;
    }

    public void setGameVariablesFiller(GameVariablesFiller gameVariablesFiller) {
        this.gameVariablesFiller = gameVariablesFiller;
    }

    @Override
    public double getPotSize() {
        return potSize;
    }

    public void setPotSize(double potSize) {
        this.potSize = potSize;
    }

    @Override
    public double getBotStack() {
        return botStack;
    }

    public void setBotStack(double botStack) {
        this.botStack = botStack;
    }

    @Override
    public double getOpponentStack() {
        return opponentStack;
    }

    public void setOpponentStack(double opponentStack) {
        this.opponentStack = opponentStack;
    }

    @Override
    public double getBotTotalBetSize() {
        return botTotalBetSize;
    }

    public void setBotTotalBetSize(double botTotalBetSize) {
        this.botTotalBetSize = botTotalBetSize;
    }

    @Override
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

    @Override
    public double getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(double bigBlind) {
        this.bigBlind = bigBlind;
    }

    @Override
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

    @Override
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

    public void setTurnCard(Card turnCard) {
        this.turnCard = turnCard;
    }

    public Card getRiverCard() {
        return riverCard;
    }

    public void setRiverCard(Card riverCard) {
        this.riverCard = riverCard;
    }

    public Action getBotAction() {
        return botAction;
    }

    public void setBotAction(Action botAction) {
        this.botAction = botAction;
    }

    @Override
    public List<Card> getBotHoleCards() {
        return botHoleCards;
    }

    public void setBotHoleCards(List<Card> botHoleCards) {
        this.botHoleCards = botHoleCards;
    }

    @Override
    public List<Card> getFlopCards() {
        return flopCards;
    }

    public void setFlopCards(List<Card> flopCards) {
        this.flopCards = flopCards;
    }

    @Override
    public Set<Card> getKnownGameCards() {
        return knownGameCards;
    }

    public void setKnownGameCards(Set<Card> knownGameCards) {
        this.knownGameCards = knownGameCards;
    }

    @Override
    public List<Card> getBoard() {
        return board;
    }

    public void setBoard(List<Card> board) {
        this.board = board;
    }

    @Override
    public Set<Set<Card>> getOpponentRange() {
        return opponentRange;
    }

    public void setOpponentRange(Set<Set<Card>> opponentRange) {
        this.opponentRange = opponentRange;
    }

    public boolean isOpponentPreflopStatsDoneForHand() {
        return opponentPreflopStatsDoneForHand;
    }

    public void setOpponentPreflopStatsDoneForHand(boolean opponentPreflopStatsDoneForHand) {
        this.opponentPreflopStatsDoneForHand = opponentPreflopStatsDoneForHand;
    }

    @Override
    public double getHandsOpponentOopFacingPreflop2bet() {
        return handsOpponentOopFacingPreflop2bet;
    }

    public void setHandsOpponentOopFacingPreflop2bet(double handsOpponentOopFacingPreflop2bet) {
        this.handsOpponentOopFacingPreflop2bet = handsOpponentOopFacingPreflop2bet;
    }

    public double getHandsOpponentOopCall2bet() {
        return handsOpponentOopCall2bet;
    }

    public void setHandsOpponentOopCall2bet(double handsOpponentOopCall2bet) {
        this.handsOpponentOopCall2bet = handsOpponentOopCall2bet;
    }

    public double getHandsOpponentOop3bet() {
        return handsOpponentOop3bet;
    }

    public void setHandsOpponentOop3bet(double handsOpponentOop3bet) {
        this.handsOpponentOop3bet = handsOpponentOop3bet;
    }

    @Override
    public double getOpponentPreCall2betStat() {
        return opponentPreCall2betStat;
    }

    public void setOpponentPreCall2betStat(double opponentPreCall2betStat) {
        this.opponentPreCall2betStat = opponentPreCall2betStat;
    }

    @Override
    public double getOpponentPre3betStat() {
        return opponentPre3betStat;
    }

    public void setOpponentPre3betStat(double opponentPre3betStat) {
        this.opponentPre3betStat = opponentPre3betStat;
    }

    @Override
    public boolean isOpponentLastActionWasPreflop() {
        return opponentLastActionWasPreflop;
    }

    public void setOpponentLastActionWasPreflop(boolean opponentLastActionWasPreflop) {
        this.opponentLastActionWasPreflop = opponentLastActionWasPreflop;
    }

    @Override
    public double getOpponentFormerTotalCallAmount() {
        return opponentFormerTotalCallAmount;
    }

    public void setOpponentFormerTotalCallAmount(double opponentFormerTotalCallAmount) {
        this.opponentFormerTotalCallAmount = opponentFormerTotalCallAmount;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public boolean isPreviousBluffAction() {
        return previousBluffAction;
    }

    @Override
    public void setPreviousBluffAction(boolean previousBluffAction) {
        this.previousBluffAction = previousBluffAction;
    }

    @Override
    public boolean isDrawBettingActionDone() {
        return drawBettingActionDone;
    }

    @Override
    public void setDrawBettingActionDone(boolean drawBettingActionDone) {
        this.drawBettingActionDone = drawBettingActionDone;
    }

    public String getBotWrittenAction() {
        return botWrittenAction;
    }

    public void setBotWrittenAction(String botWrittenAction) {
        this.botWrittenAction = botWrittenAction;
    }
}
