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

    private GameVariablesFiller gameVariablesFiller;

    private double potSize;
    private double botStack;
    private double opponentStack;
    private double botTotalBetSize;
    private double opponentTotalBetSize;
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

    private double botHandStrength;
    private boolean botHasStrongDraw;

    public BotHand() {
        //default constructor
    }

    public BotHand(String initialize, BotTable botTable) {
        gameVariablesFiller = new GameVariablesFiller(this);

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
        setBotHoleCards();
        setStreetAndPreviousStreet();
        setOpponentAction(botTable);
        calculateHandStrengthAndDraws();
    }

    public void updateVariables(BotTable botTable) {
        gameVariablesFiller = new GameVariablesFiller(this);

        if(foldOrShowdownOccured()) {
            botTable.updateNumberOfHandsPerOpponentMap(opponentPlayerName);
            botTable.setBotHand(new BotHand());
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
        setOpponentAction(botTable);
        calculateHandStrengthAndDraws();

        System.out.println("opponent action: " + opponentAction + " " + opponentTotalBetSize);
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
        String opponentType = new OpponentIdentifier().getOpponentType(opponentPlayerName, botTable.getNumberOfHandsPerOpponentMap().get(opponentPlayerName));
        double opponentBetsizeBb = opponentTotalBetSize / bigBlind;
        double botBetsizeBb = botTotalBetSize / bigBlind;
        double opponentStackBb = opponentStack / bigBlind;
        double botStackBb = botStack / bigBlind;
        boolean preflop = board.isEmpty();
        List<Card> boardInMethod = board;

        String botAction = new Poker().getAction(eligibleActions, streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod, facingOdds, effectiveStack, botHasStrongDrawInMethod, botHandStrengthInMethod, opponentType, opponentBetsizeBb, botBetsizeBb, opponentStackBb, botStackBb, preflop, boardInMethod);
        double sizing = new Sizing().getAiBotSizing(opponentTotalBetSize, botTotalBetSize, botStack, opponentStack, potSize, bigBlind, board);

        updateBotActionHistory(botAction);
        NetBetTableReader.performActionOnSite(botAction, sizing);
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
            if(board == null) {
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

    private void updateBotActionHistory(String action) {
        if(botActionHistory == null) {
            botActionHistory = new ArrayList<>();
        }

        botActionHistory.add(street + " " + action);
    }

    private void setBotHoleCard1() {
        botHoleCard1 = gameVariablesFiller.getBotHoleCard1();
    }

    private void setBotHoleCard2() {
        botHoleCard2 = gameVariablesFiller.getBotHoleCard2();
    }

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

    private void setOpponentAction(BotTable botTable) {
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

        new OpponentIdentifier().updateCounts(opponentPlayerName, opponentAction,
                botTable.getNumberOfHandsPerOpponentMap().get(opponentPlayerName));
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
    public double getPotSize() {
        return potSize;
    }

    public void setPotSize(double potSize) {
        this.potSize = potSize;
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(double bigBlind) {
        this.bigBlind = bigBlind;
    }

    public Card getBotHoleCard1() {
        return botHoleCard1;
    }

    public Card getBotHoleCard2() {
        return botHoleCard2;
    }

    public Card getFlopCard1() {
        return flopCard1;
    }

    public Card getFlopCard2() {
        return flopCard2;
    }

    public Card getFlopCard3() {
        return flopCard3;
    }

    public Card getTurnCard() {
        return turnCard;
    }

    public Card getRiverCard() {
        return riverCard;
    }

    public List<Card> getBoard() {
        return board;
    }

    public void setGameVariablesFiller(GameVariablesFiller gameVariablesFiller) {
        this.gameVariablesFiller = gameVariablesFiller;
    }
}
