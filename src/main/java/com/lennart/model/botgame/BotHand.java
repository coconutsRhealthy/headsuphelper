package com.lennart.model.botgame;

import com.lennart.model.action.Action;
import com.lennart.model.action.Actionable;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;
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

    private double botStackAtBeginningOfHand;
    private double opponentStackAtBeginningOfHand;

    public BotHand() {
        //default constructor
    }

    public BotHand(String initialize) {
        gameVariablesFiller = new GameVariablesFiller(this);

        setSmallBlind();
        setBigBlind();
        setBotStack(true);
        setOpponentStack(true);
        setPotSize();
        setBotTotalBetSize();
        setOpponentTotalBetSize();
        setOpponentPlayerName();
        setBotIsButton();
        setBotHoleCard1();
        setBotHoleCard2();
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
        setBotStack(false);
        setOpponentStack(false);
        setPotSize();
        setBotTotalBetSize();
        setOpponentTotalBetSize();
        setStreetAndPreviousStreet();
        setOpponentAction();

        return this;
    }

    public void getNewBotAction() {
        if(potSize == -1) {
            potSize = NetBetTableReader.getPotSizeCheckFromImage();
        }

        if (potSize == -1 || opponentStack == -1 || botStack == -1 || opponentTotalBetSize == -1 || botTotalBetSize == -1) {
            getActionWhenTableIsMisread();
        } else if(defaultCheckActionAfterCallNeeded()) {
            doDefaultCheck();
        } else {
            OpponentRangeSetter opponentRangeSetter = new OpponentRangeSetter();
            opponentRangeSetter.setCorrectOpponentRange(this);
            setOrInitializeRangeBuilder(opponentRangeSetter);

            botAction = new Action(this, rangeBuilder);
            updateBotActionHistory(botAction);
            botWrittenAction = botAction.getWrittenAction();
        }

        setPotSizeAfterLastBotAction();
        NetBetTableReader.performActionOnSite(botAction);
    }

    private void setPotSizeAfterLastBotAction() {
        if(botAction != null && botAction.getAction() != null && botAction.getAction().equals("call")) {
            potSizeAfterLastBotAction = potSize + (2 * opponentTotalBetSize);
        } else {
            potSizeAfterLastBotAction = potSize + getSizing() + opponentTotalBetSize;
        }
    }

    private double getSizing() {
        double sizing;
        if(botAction != null) {
            sizing = botAction.getSizing();
        } else {
            sizing = 0;
        }
        return sizing;
    }

    private boolean defaultCheckActionAfterCallNeeded() {
        if(botActionHistory != null) {
            String botLastAction = botActionHistory.get(botActionHistory.size() - 1);

            if(botLastAction.contains("call") && !botIsButton) {
                return true;
            }
        }
        return false;
    }

    private void doDefaultCheck() {
        updateBotActionHistory(null);
        botWrittenAction = "check";
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

    private void getActionWhenTableIsMisread() {
        boolean clickActionDone = false;
        botAction = new Action();

        if(board != null && opponentTotalBetSize != 0) {
            BoardEvaluator boardEvaluatorMisreadTable = new BoardEvaluator(board);
            HandEvaluator handEvaluator = new HandEvaluator(boardEvaluatorMisreadTable);
            double handStrength = handEvaluator.getHandStrength(botHoleCards);

            if((opponentTotalBetSize / bigBlind) < 10) {
                if(handStrength >= 0.72) {
                    System.out.println("Call on misread board. Opponent total betsize / bigblind < 10: " + (opponentTotalBetSize / bigBlind)
                            + " handstrength: " + handStrength);
                    setActionToCall();
                    clickActionDone = true;
                }
            } else if((opponentTotalBetSize / bigBlind) < 20) {
                if(handStrength >= 0.82) {
                    System.out.println("Call on misread board. Opponent total betsize / bigblind < 20: " + (opponentTotalBetSize / bigBlind)
                            + " handstrength: " + handStrength);
                    setActionToCall();
                    clickActionDone = true;
                }
            } else if((opponentTotalBetSize / bigBlind) < 40) {
                if(handStrength >= 0.94) {
                    System.out.println("Call on misread board. Opponent total betsize / bigblind < 40: " + (opponentTotalBetSize / bigBlind)
                            + " handstrength: " + handStrength);
                    setActionToCall();
                    clickActionDone = true;
                }
            } else if((opponentTotalBetSize / bigBlind) < 70) {
                if(handStrength >= 0.99) {
                    System.out.println("Call on misread board. Opponent total betsize / bigblind < 70: " + (opponentTotalBetSize / bigBlind)
                            + " handstrength: " + handStrength);
                    setActionToCall();
                    clickActionDone = true;
                }
            }
        }

        if(!clickActionDone && NetBetTableReader.readMiddleActionButton().contains("Check")) {
            botAction.setAction("check");
            System.out.println("check on misread board");
            clickActionDone = true;
        }

        if(!clickActionDone) {
            botAction.setAction("fold");
            System.out.println("fold on misread board");
        }
    }

    private void setActionToCall() {
        botAction.setAction("call");
        updateBotActionHistory(botAction);
        botWrittenAction = "call";
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
    private void setBotStack(boolean initialize) {
        botStack = gameVariablesFiller.getBotStack();

        if(initialize) {
            botStackAtBeginningOfHand = botStack;
        }
        validateBotStack();
    }

    private void validateBotStack() {
        if(botStack > botStackAtBeginningOfHand) {
            botStack = -1;
        } else if(botStack / bigBlind > 1000) {
            botStack = -1;
        }
    }

    private void setOpponentStack(boolean initialize) {
        opponentStack = gameVariablesFiller.getOpponentStack();

        if(initialize) {
            opponentStackAtBeginningOfHand = opponentStack;
        }
        validateOpponentStack();
    }

    private void validateOpponentStack() {
        if(opponentStack > opponentStackAtBeginningOfHand) {
            opponentStack = -1;
        } else if(opponentStack / bigBlind > 1000) {
            opponentStack = -1;
        }
    }

    private void setPotSize() {
        potSize = gameVariablesFiller.getPotSize();
        validatePotSize();
        System.out.println("Potsize: " + potSize);
    }

    private void validatePotSize() {
        if(potSize > (botStackAtBeginningOfHand + opponentStackAtBeginningOfHand)) {
            potSize = -1;
        } else if(potSize / bigBlind > 1000) {
            potSize = -1;
        }
    }

    private void setBotTotalBetSize() {
        botTotalBetSize = gameVariablesFiller.getBotTotalBetSize();
        validateBotTotalBetSize();
    }

    private void validateBotTotalBetSize() {
        if(botTotalBetSize > botStackAtBeginningOfHand) {
            botTotalBetSize = -1;
        } else if(botTotalBetSize / bigBlind > 1000) {
            botTotalBetSize = -1;
        }
    }

    private void setOpponentTotalBetSize() {
        opponentTotalBetSize = gameVariablesFiller.getOpponentTotalBetSize();
        validateOpponentTotalBetSize();
    }

    private void validateOpponentTotalBetSize() {
        if(opponentTotalBetSize > opponentStackAtBeginningOfHand) {
            opponentTotalBetSize = -1;
        } else if(opponentTotalBetSize / bigBlind > 1000) {
            opponentTotalBetSize = -1;
        }
    }

    private void setOpponentPlayerName() {
        opponentPlayerName = gameVariablesFiller.getOpponentPlayerName();
    }

    private void setOpponentAction() {
        Map<String, String> actionsFromLastThreeChatLines = gameVariablesFiller.getActionsFromLastThreeChatLines();

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
                opponentAction = null;
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

    public double getBotStackAtBeginningOfHand() {
        return botStackAtBeginningOfHand;
    }

    public void setBotStackAtBeginningOfHand(double botStackAtBeginningOfHand) {
        this.botStackAtBeginningOfHand = botStackAtBeginningOfHand;
    }

    public double getOpponentStackAtBeginningOfHand() {
        return opponentStackAtBeginningOfHand;
    }

    public void setOpponentStackAtBeginningOfHand(double opponentStackAtBeginningOfHand) {
        this.opponentStackAtBeginningOfHand = opponentStackAtBeginningOfHand;
    }
}
