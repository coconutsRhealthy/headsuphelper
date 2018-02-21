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
    private List<Card> board;

    private double botHandStrength;
    private boolean botHasStrongDraw;

    public BotHand() {
        //default constructor
    }

    public BotHand(String initialize, BotTable botTable) {
        readVariablesFromTable();
        setStreetAndPreviousStreet();
        calculateHandStrengthAndDraws();
        updateOpponentIdentifier(botTable);
    }

    public void updateVariables(BotTable botTable) {
        if(foldOrShowdownOccured()) {
            botTable.updateNumberOfHandsPerOpponentMap(opponentPlayerName);
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

        botStack = netBetTableReader.getBotStackFromImage();
        opponentStack = netBetTableReader.getOpponentStackFromImage();
        potSize = netBetTableReader.getPotSizeFromImage();
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
        String opponentType = new OpponentIdentifier().getOpponentType(opponentPlayerName, botTable.getNumberOfHandsPerOpponentMap().get(opponentPlayerName));
        double opponentBetsizeBb = opponentTotalBetSize / bigBlind;
        double botBetsizeBb = botTotalBetSize / bigBlind;
        double opponentStackBb = opponentStack / bigBlind;
        double botStackBb = botStack / bigBlind;
        boolean preflop = board.isEmpty();
        List<Card> boardInMethod = board;

        String botAction = new Poker().getAction(eligibleActions, streetInMethod, botIsButtonInMethod, potSizeBb, opponentActionInMethod, facingOdds, effectiveStack, botHasStrongDrawInMethod, botHandStrengthInMethod, opponentType, opponentBetsizeBb, botBetsizeBb, opponentStackBb, botStackBb, preflop, boardInMethod);
        double sizing = new Sizing().getAiBotSizing(opponentTotalBetSize, botTotalBetSize, botStack, opponentStack, potSize, bigBlind, board);

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

    private void updateOpponentIdentifier(BotTable botTable) {
        new OpponentIdentifier().updateCounts(opponentPlayerName, opponentAction,
                botTable.getNumberOfHandsPerOpponentMap().get(opponentPlayerName));
    }

    private boolean foldOrShowdownOccured() {
        Map<String, String> actionsFromLastThreeChatLines = new NetBetTableReader(bigBlind).getActionsFromLastThreeChatLines();

        for (Map.Entry<String, String> entry : actionsFromLastThreeChatLines.entrySet()) {
            if(entry.getValue() != null && entry.getValue().equals("deal")) {
                System.out.println("Fold or showdown occured: true");
                return true;
            }
        }
        System.out.println("Fold or showdown occured: false");
        return false;
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
}
