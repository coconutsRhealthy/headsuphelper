package com.lennart.model.botgame;

import com.lennart.model.action.Action;
import com.lennart.model.action.Actionable;
import com.lennart.model.card.Card;
import com.lennart.model.rangebuilder.OpponentRangeSetter;
import com.lennart.model.rangebuilder.RangeBuildable;
import com.lennart.model.rangebuilder.RangeBuilder;

import java.util.*;

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
    private String street;
    private boolean previousBluffAction;
    private boolean drawBettingActionDone;
    private String botWrittenAction;
    private String streetAtPreviousActionRequest;

    private List<String> botActionHistory;
    private RangeBuilder rangeBuilder;
    private double potSizeAfterLastBotAction;

    public BotHand() {
        //default constructor
    }

    public BotHand(String initialize) {
        gameVariablesFiller = new GameVariablesFiller(this);

        setPotSize();
        setBotStack();
        setOpponentStack();
        setBotTotalBetSize();
        setOpponentTotalBetSize();
        setOpponentPlayerName();
        setBotIsButton();
        setBotHoleCard1();
        setBotHoleCard2();
        setSmallBlind();
        setBigBlind();
        setKnownGameCards();
        setBotHoleCards();
        setStreetAndPreviousStreet();
        setOpponentAction();
    }

    public BotHand updateVariables() {
        gameVariablesFiller = new GameVariablesFiller(this);

        if(foldOrShowdownOccured()) {
            return new BotHand("initialize");
        }

        setFlopCard1IfNecessary();
        setFlopCard2IfNecessary();
        setFlopCard3IfNecessary();
        setTurnCardIfNecessary();
        setRiverCardIfNecessary();
        setFlopCards();
        setKnownGameCards();
        setBoard();
        setPotSize();
        setBotStack();
        setOpponentStack();
        setBotTotalBetSize();
        setOpponentTotalBetSize();
        setStreetAndPreviousStreet();
        setOpponentAction();
        calculateOpponentPreflopStats();

        return this;
    }

    public void getNewBotAction() {
        if(defaultCheckActionAfterCallNeeded()) {
            updateBotActionHistory(null);
            botWrittenAction = "check";
        } else {
            OpponentRangeSetter opponentRangeSetter = new OpponentRangeSetter();
            opponentRangeSetter.setCorrectOpponentRange(this);
            setOrInitializeRangeBuilder(opponentRangeSetter);

            botAction = new Action(this, rangeBuilder);
            updateBotActionHistory(botAction);
            botWrittenAction = botAction.getWrittenAction();
        }
    }

    public void performActionOnSite() {
        if(botAction.getSizing() != 0) {
            MouseKeyboard.click(0, 0);
            MouseKeyboard.enterText(String.valueOf(botAction.getSizing()));
        }

        //en nog mouse/click logic
    }

    private boolean defaultCheckActionAfterCallNeeded() {
        if(botActionHistory != null) {
            String botLastAction = botActionHistory.get(botActionHistory.size() - 1);

            if(botLastAction.contains("call")) {
                return true;
            }
        }
        return false;
    }

    private void updateBotActionHistory(Action action) {
        if(botActionHistory == null) {
            botActionHistory = new ArrayList<>();
        }

        if(action == null) {
            botActionHistory.add(street + " check");
        } else {
            botActionHistory.add(street + " " + action.getAction());
        }
    }

    private void setOrInitializeRangeBuilder(OpponentRangeSetter opponentRangeSetter) {
        if(rangeSetterChangedBoard(opponentRangeSetter)) {
            rangeBuilder = new RangeBuilder(this, false);
        } else {
            rangeBuilder = opponentRangeSetter.getRangeBuilder();
        }
    }

    private boolean rangeSetterChangedBoard(OpponentRangeSetter opponentRangeSetter) {
        if(opponentRangeSetter.getRangeBuilder().getBoard() != null) {
            List<Card> rangeSetterBoard = opponentRangeSetter.getRangeBuilder().getBoard();
            Set<Card> rangeSetterBoardAsSet = new HashSet<>();
            rangeSetterBoardAsSet.addAll(rangeSetterBoard);

            Set<Card> currentBoardAsSet = new HashSet<>();
            currentBoardAsSet.addAll(board);

            if(rangeSetterBoardAsSet.equals(currentBoardAsSet)) {
                return false;
            }
            return true;
        } else {
            if(board == null) {
                return false;
            }
            return true;
        }
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
        Map<String, String> actionsFromLastThreeChatLines = gameVariablesFiller.getActionsFromLastThreeChatLines();

//        for (Map.Entry<String, String> entry : actionsFromLastThreeChatLines.entrySet()) {
//            if(entry.getValue() != null && entry.getValue().equals("post")) {
//                opponentAction = "post";
//                return;
//            }
//        }

        if(street.equals(streetAtPreviousActionRequest)) {
            if(street.equals("preflop") && botIsButton) {
                opponentAction = null;
            } else {
                opponentAction = actionsFromLastThreeChatLines.get("bottom");
            }
        } else {
            if(botIsButton) {
                opponentAction = actionsFromLastThreeChatLines.get("bottom");
            } else {
                String botLastAction = botActionHistory.get(botActionHistory.size() - 1);

                if(botLastAction.contains("call")) {
                    opponentAction = null;
                } else if(botLastAction.contains("check")) {
                    opponentAction = null;
                } else if(botLastAction.contains("bet") || botLastAction.contains("raise")) {
                    opponentAction = null;
                }
            }
        }
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

    private void setBotHoleCards() {
        botHoleCards = new ArrayList<>();
        botHoleCards.add(botHoleCard1);
        botHoleCards.add(botHoleCard2);
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
        if(knownGameCards == null) {
            knownGameCards = new HashSet<>();
            knownGameCards.add(botHoleCard1);
            knownGameCards.add(botHoleCard2);
        }

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

    private void calculateOpponentPreflopStats() {
        if(!opponentPreflopStatsDoneForHand) {
            if(board == null && botIsButton && botWrittenAction.contains("raise") && opponentTotalBetSize == bigBlind) {
                handsOpponentOopFacingPreflop2bet++;
                if(opponentAction.equals("call")) {
                    handsOpponentOopCall2bet++;
                }
                if(opponentAction.equals("raise")) {
                    handsOpponentOop3bet++;
                }
            }
            opponentPreCall2betStat = handsOpponentOopCall2bet / handsOpponentOopFacingPreflop2bet;
            opponentPre3betStat = handsOpponentOop3bet / handsOpponentOopFacingPreflop2bet;
            opponentPreflopStatsDoneForHand = true;
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

    @Override
    public Card getTurnCard() {
        return turnCard;
    }

    @Override
    public void setTurnCard(Card turnCard) {
        this.turnCard = turnCard;
    }

    @Override
    public Card getRiverCard() {
        return riverCard;
    }

    @Override
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

    @Override
    public void setFlopCards(List<Card> flopCards) {
        this.flopCards = flopCards;
    }

    @Override
    public Set<Card> getKnownGameCards() {
        return knownGameCards;
    }

    @Override
    public void setKnownGameCards(Set<Card> knownGameCards) {
        this.knownGameCards = knownGameCards;
    }

    @Override
    public List<Card> getBoard() {
        return board;
    }

    @Override
    public void setBoard(List<Card> board) {
        this.board = board;
    }

    @Override
    public Set<Set<Card>> getOpponentRange() {
        return opponentRange;
    }

    @Override
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

    public String getStreetAtPreviousActionRequest() {
        return streetAtPreviousActionRequest;
    }

    public void setStreetAtPreviousActionRequest(String streetAtPreviousActionRequest) {
        this.streetAtPreviousActionRequest = streetAtPreviousActionRequest;
    }

    @Override
    public List<String> getBotActionHistory() {
        return botActionHistory;
    }

    public void setBotActionHistory(List<String> botActionHistory) {
        this.botActionHistory = botActionHistory;
    }

    public RangeBuilder getRangeBuilder() {
        return rangeBuilder;
    }

    @Override
    public void setRangeBuilder(RangeBuilder rangeBuilder) {
        this.rangeBuilder = rangeBuilder;
    }

    @Override
    public double getPotSizeAfterLastBotAction() {
        return potSizeAfterLastBotAction;
    }

    public void setPotSizeAfterLastBotAction(double potSizeAfterLastBotAction) {
        this.potSizeAfterLastBotAction = potSizeAfterLastBotAction;
    }
}
