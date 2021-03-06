package com.lennart.model.computergame;

import com.lennart.model.action.actionbuilders.ActionBuilderUtil;
import com.lennart.model.action.actionbuilders.ai.*;
import com.lennart.model.action.actionbuilders.ai.foldstats.AdjustToFoldStats;
import com.lennart.model.action.actionbuilders.ai.foldstats.FoldStatsKeeper;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.*;
import com.lennart.model.action.actionbuilders.preflop.PreflopActionBuilder;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.handevaluation.PreflopHandStength;
import org.apache.commons.math3.util.Precision;

import java.util.*;

/**
 * Created by lennart on 11-12-16.
 */
public class ComputerGameNew implements GameVariable, ContinuousTableable {

    private List<Card> deck;
    private List<Card> myHoleCards;
    private List<Card> computerHoleCards;
    private List<Card> flopCards;
    private Card turnCard;
    private Card riverCard;
    private double smallBlind;
    private double bigBlind;
    private double myStack;
    private double myStackAtStartOfHand;
    private double computerStack;
    private double opponentIncrementalBetSize;
    private double opponentTotalBetSize;
    private double computerIncrementalBetSize;
    private double computerTotalBetSize;
    private double potSize;
    private boolean computerIsButton;
    private String myAction;
    private String mySize;
    private List<Card> board;
    private String computerWrittenAction;
    private String computerWrittenActionBeforeFoldStat;
    private String handWinner;
    private int numberOfHandsPlayed;

    ////
    private double computerHandStrength;
    private boolean computerHasStrongDraw;

    private double totalHumanScore = 0;
    private double totalBotScore = 0;

    private boolean strongFlushDraw;
    private boolean strongOosd;
    private boolean strongGutshot;
    private boolean strongOvercards;
    private boolean strongBackdoorFd;
    private boolean strongBackdoorSd;

    private boolean drawBettingActionDone;
    private boolean previousBluffAction;
    private static HandEvaluator handEvaluator;

    private boolean opponentHasInitiative;
    private boolean pre3betOrPostRaisedPot;

    private boolean copyPre3betOrPostRaisedPot;

    private boolean opponentDidPreflop4betPot;

    private List<Set<Card>> top10percentFlopCombos;
    private List<Set<Card>> top10percentTurnCombos;
    private List<Set<Card>> top10percentRiverCombos;

    private Map<Integer, List<Card>> botRange;
    private static BoardEvaluator boardEvaluator;

    private boolean balanceRuleMethodHasBeenCalled = false;

    private List<Card> computerHoleCardsCopy;
    private double computerHandStrengthCopy;

    private boolean strongFlushDrawCopy;
    private boolean strongOosdCopy;
    private boolean strongGutshotCopy;
    private boolean strongOvercardsCopy;
    private boolean strongBackdoorFdCopy;
    private boolean strongBackdoorSdCopy;

    public ComputerGameNew() {
        //default constructor
    }

    public ComputerGameNew(String initialize) {
        numberOfHandsPlayed = 0;
        getNewCardDeck();
        dealHoleCards();
        decideWhoIsButton();
        myStack = 50;
        myStackAtStartOfHand = myStack;
        computerStack = 50;
        setBlinds();
        postBlinds();

        calculateHandStrengthsAndDraws();

        botRange = ActionBuilderUtil.getAllStartHandsAsList();

        if(isComputerIsButton()) {
            doComputerAction();

            if(computerWrittenAction.contains("fold")) {
                processComputerFoldAction();
                updatePotSize("computer fold");
                resetAllBets();
            }
        }
    }

    public ComputerGameNew submitHumanActionAndDoComputerAction() {
        if(board != null && !board.isEmpty()) {
            new OpponentIdentifier().updateCounts("izo", myAction, numberOfHandsPlayed);

            if(myAction != null && myAction.equals("raise")) {
                pre3betOrPostRaisedPot = true;
            }
        }

        boolean computerActionNeeded = isComputerActionNeeded();

        //if(board == null) {
            calculateHandStrengthsAndDraws();
        //}

        if(myAction.equals("fold")) {
            processHumanFoldAction();
        } else if(myAction.equals("check")) {
            processHumanCheckAction();
        } else if(myAction.equals("call")) {
            processHumanCallAction();
        } else if(myAction.contains("bet") || myAction.equals("raise")) {
            processHumanBetOrRaiseAction();
        }

        setOpponentHasInitiativeVariable(myAction);

        if(computerActionNeeded && isComputerActionNeededIfPlayerIsAllIn()) {
            doComputerAction();
        }
        roundToTwoDecimals();
        return this;
    }

    private void setOpponentHasInitiativeVariable(String myAction) {
        if(myAction != null && (myAction.equals("bet75pct") || myAction.equals("raise"))) {
            opponentHasInitiative = true;
        } else {
            opponentHasInitiative = false;
        }
    }

    private void doComputerAction() {
        computerWrittenAction = getComputerActionFromAiBot();

        if(computerWrittenAction.contains("fold")) {
            processComputerFoldAction();
        } else if(computerWrittenAction.contains("check")) {
            boolean preflopCheck = isPreflopCheck();
            processComputerCheckAction();

            if(preflopCheck) {
                doComputerAction();
            }
        } else if(computerWrittenAction.contains("call")) {
            processComputerCallAction();
        } else if(computerWrittenAction.contains("bet")) {
            processComputerBetAction();
        } else if(computerWrittenAction.contains("raise")) {
            processComputerRaiseAction();
        }

        if(board != null && board.size() >= 3) {
            if(computerWrittenAction != null) {
                if(computerWrittenAction.equals("bet75pct")) {
                    if(computerHandStrengthCopy >= 0.8) {
                        System.out.println("*** value bet action done ***");
                    } else if(strongFlushDrawCopy || strongOosdCopy || strongGutshotCopy || strongOvercardsCopy || strongBackdoorFdCopy || strongBackdoorSdCopy) {
                        System.out.println("*** draw bet action done ***");
                    } else if(computerHandStrengthCopy < 0.55) {
                        System.out.println("*** bluff bet action done ***");
                    }
                }

                if(computerWrittenAction.equals("raise")) {
                    if(computerHandStrengthCopy >= 0.8) {
                        System.out.println("*** value raise action done ***");
                    } else if(strongFlushDrawCopy || strongOosdCopy || strongGutshotCopy || strongOvercardsCopy || strongBackdoorFdCopy || strongBackdoorSdCopy) {
                        System.out.println("*** draw raise action done ***");
                    } else if(computerHandStrengthCopy < 0.55) {
                        System.out.println("*** bluff raise action done ***");
                    }
                }
            }
        }

        roundToTwoDecimals();
    }

    private String getComputerActionFromAiBot() {
        List<String> eligibleActions = getEligibleComputerActions();
        double handStrength = computerHandStrength;
        boolean strongDraw = computerHasStrongDraw;
        boolean position = computerIsButton;
        double potSizeInMethodBb = getAaPotSizeInBb();
        double computerBetSizeBb = computerTotalBetSize / bigBlind;
        double opponentBetSizeBb = opponentTotalBetSize / bigBlind;
        double effectiveStack = getEffectiveStackInBb();
        double amountToCallBb = getAmountToCallBb(computerBetSizeBb, opponentBetSizeBb, computerStack / bigBlind);
        int boardWetness = getBoardWetness();

        String opponentType = new OpponentIdentifier().getOpponentType("izo", numberOfHandsPlayed);

        //String opponentType = "la";

        setMyActionToBetIfPreflopNecessary();

        //System.out.println("opponentType: " + opponentType);
        //String action = new Poker().getAction(null, eligibleActions, getStreet(), position, potSizeInMethodBb, myAction, getAiBotFacingOdds(), effectiveStack, strongDraw, handStrength, opponentType, opponentBetSizeBb, computerBetSizeBb, getOpponentStack() / bigBlind, computerStack / bigBlind, board == null || board.isEmpty(), board, strongFlushDraw, strongOosd, strongGutshot);

        copyPre3betOrPostRaisedPot = pre3betOrPostRaisedPot;

        String action;

        if(board == null || board.isEmpty()) {
            action = new PreflopActionBuilder().getAction(opponentTotalBetSize, computerTotalBetSize, myStack, bigBlind, computerHoleCards, computerIsButton, this, opponentType, amountToCallBb);
        } else {
            if(opponentHasInitiative && (myAction == null || myAction.equals("empty"))) {
                action = "check";
            } else {
                //hier equity
                doEquityLogic();
                strongDraw = computerHasStrongDraw;

                if(eligibleActions != null && (eligibleActions.contains("bet75pct") || eligibleActions.contains("raise"))) {
                    String actionAgainstLa = new Poker().getAction(null, eligibleActions, getStreet(), position, potSizeInMethodBb,
                            myAction, getFacingOdds(), effectiveStack, strongDraw, handStrength, "la", opponentBetSizeBb,
                            computerBetSizeBb, getOpponentStack() / bigBlind, computerStack / bigBlind, board == null || board.isEmpty(),
                            board, strongFlushDraw, strongOosd, strongGutshot, bigBlind, opponentDidPreflop4betPot,
                            pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd, strongBackdoorSd,
                            boardWetness, opponentHasInitiative, botRange, null, null, boardEvaluator, this);

                    if(opponentType.equals("la")) {
                        action = actionAgainstLa;
                    } else if(actionAgainstLa.equals("bet75pct") || actionAgainstLa.equals("raise")) {
                        action = actionAgainstLa;
                    } else {
                        action = new Poker().getAction(null, eligibleActions, getStreet(), position, potSizeInMethodBb,
                                myAction, getFacingOdds(), effectiveStack, strongDraw, handStrength, opponentType,
                                opponentBetSizeBb, computerBetSizeBb, getOpponentStack() / bigBlind, computerStack / bigBlind,
                                board == null || board.isEmpty(), board, strongFlushDraw, strongOosd, strongGutshot, bigBlind,
                                opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, null, null, boardEvaluator, this);
                    }
                } else {
                    action = new Poker().getAction(null, eligibleActions, getStreet(), position, potSizeInMethodBb, myAction,
                            getFacingOdds(), effectiveStack, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                            computerBetSizeBb, getOpponentStack() / bigBlind, computerStack / bigBlind, board == null || board.isEmpty(),
                            board, strongFlushDraw, strongOosd, strongGutshot, bigBlind, opponentDidPreflop4betPot,
                            pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd, strongBackdoorSd,
                            boardWetness, opponentHasInitiative, botRange, null, null, boardEvaluator, this);
                }
            }

            try {



                action = new RuleApplier().balancePlayWithBotRange(action, botRange, null, null, boardEvaluator,
                        opponentType, handStrength, opponentBetSizeBb, computerBetSizeBb, computerStack / bigBlind,
                        myStack / bigBlind, potSize / bigBlind, bigBlind, strongFlushDraw, strongOosd, strongGutshot,
                        strongBackdoorFd, strongBackdoorSd, myAction, board, this, position);
            } catch (Exception e) {

            }

            if(action.equals("raise")) {
                pre3betOrPostRaisedPot = true;
            }
        }


//        String action = new TightPassive().doAction(
//                myAction, computerHandStrength, computerHasStrongDraw, opponentBetSizeBb, computerBetSizeBb,
//                (myStack / bigBlind), (computerStack / bigBlind), computerIsButton, board == null, board);

//        String action = new Poker().getAction(eligibleActions, handStrength, strongDraw, position, potSizeInMethodBb, computerBetSizeBb,
//                opponentBetSizeBb, effectiveStack, "BoardTextureMedium");
        computerWrittenActionBeforeFoldStat = action;

        AdjustToFoldStats adjustToFoldStats = new AdjustToFoldStats();

//        action = adjustToFoldStats.adjustPlayToBotFoldStatRaise(action,
//                handStrength, opponentBetSizeBb * bigBlind, computerBetSizeBb * bigBlind,
//                computerStack, myStack, potSize, bigBlind, board, strongFlushDraw, strongOosd, strongGutshot, "izo");

        if(action.equals("fold")) {
            double botFoldStat = FoldStatsKeeper.getFoldStat("bot-V-izo");

            if(botFoldStat > 0.43) {
                double handStrengthRequiredToCall;

                if(board == null || board.isEmpty()) {
                    handStrengthRequiredToCall = -1;
                } else {
                    handStrengthRequiredToCall = adjustToFoldStats.getHandStrengthRequiredToCall(null, eligibleActions, getStreet(), position, potSizeInMethodBb, myAction, getFacingOdds(), effectiveStack, strongDraw, handStrength, opponentType, opponentBetSizeBb, computerBetSizeBb, getOpponentStack() / bigBlind, computerStack / bigBlind, board == null || board.isEmpty(), board, strongFlushDraw, strongOosd, strongGutshot, bigBlind, opponentDidPreflop4betPot, pre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd, strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, null, null, boardEvaluator, this);
                }

                try {
                    action = adjustToFoldStats.adjustPlayToBotFoldStat(action, handStrength, handStrengthRequiredToCall, computerHoleCards, board, position, "izo", computerBetSizeBb, opponentBetSizeBb, true);
                } catch (Exception e) {
                    System.out.println("Exception occorued in adjustPlayToBotFoldStat...");
                    e.printStackTrace();
                }


                if(action.equals("call") && getStreet().equals("preflop") && opponentBetSizeBb == 1) {
                    action = "fold";
                }

                if(action.equals("call")) {
                    System.out.println();
                    System.out.println("CHANGED FROM FOLD TO CALL!");
                    System.out.println("street: " + getStreet());
                    System.out.println();
                }

                if(action.equals("call") && getStreet().equals("preflop") && opponentBetSizeBb > 4) {
                    pre3betOrPostRaisedPot = true;
                }

                if(action.equals("call") && getStreet().equals("preflop") && opponentBetSizeBb > 16) {
                    opponentDidPreflop4betPot = true;
                }
            }
        }

//        if(action.equals("check") || action.equals("fold")) {
//            action = adjustToFoldStats.adjustPlayToOpponentFoldStat(action, opponentHasInitiative,
//                    opponentBetSizeBb * bigBlind, computerBetSizeBb * bigBlind, computerStack, myStack,
//                    potSize, bigBlind, board, "izo", handStrength,
//                    strongOosd,
//                    strongFlushDraw,
//                    strongGutshot,
//                    strongOvercards,
//                    strongBackdoorSd,
//                    strongBackdoorFd,
//                    boardWetness,
//                    myAction);
//        }

        boolean methodOpponentHasInitiative = opponentHasInitiative;
        boolean methodPre3betOrPostRaisedPot = pre3betOrPostRaisedPot;
        boolean methodOpponentDidPreflop4betPot = opponentDidPreflop4betPot;

        Map<Integer, List<Card>> newBotRange = new BotRange().updateBotRangeComputerGame(this.botRange, action, this, boardEvaluator);

        System.out.println("botrange before air: " + newBotRange.size());

        //vul je range aan met een beetje air, afhankelijk van 80+ combos..
        if((action.equals("bet75pct") || action.equals("raise")) && board != null && board.size() >= 3) {
            newBotRange = new BotRange().addAirToRange(newBotRange, this.botRange, boardEvaluator);
            System.out.println("botrange after air: " + newBotRange.size());
        }

        this.botRange = newBotRange;

        Poker.setRouteData(null);
        computerHoleCards = new ArrayList<>();
        computerHoleCards.addAll(computerHoleCardsCopy);

        computerHandStrength = computerHandStrengthCopy;

        opponentHasInitiative = methodOpponentHasInitiative;
        pre3betOrPostRaisedPot = methodPre3betOrPostRaisedPot;
        opponentDidPreflop4betPot = methodOpponentDidPreflop4betPot;
        balanceRuleMethodHasBeenCalled = false;


//        if(board != null && board.size() >= 3) {
//
//        }
//
//        if(action.equals("bet75pct")) {
//            if(handStrength >= 0.8) {
//                System.out.println("*** value bet action done ***");
//            } else if(strongDraw || strongFlushDrawCopy || strongOosdCopy || strongGutshotCopy || strongOvercardsCopy || strongBackdoorFdCopy || strongBackdoorSdCopy) {
//                System.out.println("*** draw bet action done ***");
//            } else if(handStrength < 0.55) {
//                System.out.println("*** bluff bet action done ***");
//            }
//        }
//
//        if(action.equals("raise")) {
//            if(handStrength >= 0.8) {
//                System.out.println("*** value raise action done ***");
//            } else if(strongDraw || strongFlushDrawCopy || strongOosdCopy || strongGutshotCopy || strongOvercardsCopy || strongBackdoorFdCopy || strongBackdoorSdCopy) {
//                System.out.println("*** draw raise action done ***");
//            } else if(handStrength < 0.55) {
//                System.out.println("*** bluff raise action done ***");
//            }
//        }

        return action;
    }

    public String monkeyHetComputerActionFromAiBotForBotRange() {
        List<String> eligibleActions = getEligibleComputerActions();
        double handStrength = computerHandStrength;
        boolean strongDraw = computerHasStrongDraw;
        boolean position = computerIsButton;
        double potSizeInMethodBb = getAaPotSizeInBb();
        double computerBetSizeBb = computerTotalBetSize / bigBlind;
        double opponentBetSizeBb = opponentTotalBetSize / bigBlind;
        double effectiveStack = getEffectiveStackInBb();
        double amountToCallBb = getAmountToCallBb(computerBetSizeBb, opponentBetSizeBb, computerStack / bigBlind);
        int boardWetness = getBoardWetness();

        String opponentType = new OpponentIdentifier().getOpponentType("izo", numberOfHandsPlayed);

        setMyActionToBetIfPreflopNecessary();

        //System.out.println("opponentType: " + opponentType);

        String action;

        if(board == null || board.isEmpty()) {
            action = new PreflopActionBuilder().getAction(opponentTotalBetSize, computerTotalBetSize, myStack, bigBlind, computerHoleCards, computerIsButton, this, opponentType, amountToCallBb);
        } else {
            if(opponentHasInitiative && (myAction == null || myAction.equals("empty"))) {
                action = "check";
            } else {
                strongDraw = computerHasStrongDraw;

                if(eligibleActions != null && (eligibleActions.contains("bet75pct") || eligibleActions.contains("raise"))) {
                    String actionAgainstLa = new Poker().getAction(null, eligibleActions, getStreet(), position, potSizeInMethodBb,
                            myAction, getFacingOdds(), effectiveStack, strongDraw, handStrength, "la", opponentBetSizeBb,
                            computerBetSizeBb, getOpponentStack() / bigBlind, computerStack / bigBlind, board == null || board.isEmpty(),
                            board, strongFlushDraw, strongOosd, strongGutshot, bigBlind, opponentDidPreflop4betPot,
                            copyPre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd, strongBackdoorSd,
                            boardWetness, opponentHasInitiative, botRange, null, null, boardEvaluator, this);

                    if(opponentType.equals("la")) {
                        action = actionAgainstLa;
                    } else if(actionAgainstLa.equals("bet75pct") || actionAgainstLa.equals("raise")) {
                        action = actionAgainstLa;
                    } else {
                        action = new Poker().getAction(null, eligibleActions, getStreet(), position, potSizeInMethodBb,
                                myAction, getFacingOdds(), effectiveStack, strongDraw, handStrength, opponentType,
                                opponentBetSizeBb, computerBetSizeBb, getOpponentStack() / bigBlind, computerStack / bigBlind,
                                board == null || board.isEmpty(), board, strongFlushDraw, strongOosd, strongGutshot, bigBlind,
                                opponentDidPreflop4betPot, copyPre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd,
                                strongBackdoorSd, boardWetness, opponentHasInitiative, botRange, null, null, boardEvaluator, this);
                    }
                } else {
                    action = new Poker().getAction(null, eligibleActions, getStreet(), position, potSizeInMethodBb, myAction,
                            getFacingOdds(), effectiveStack, strongDraw, handStrength, opponentType, opponentBetSizeBb,
                            computerBetSizeBb, getOpponentStack() / bigBlind, computerStack / bigBlind, board == null || board.isEmpty(),
                            board, strongFlushDraw, strongOosd, strongGutshot, bigBlind, opponentDidPreflop4betPot,
                            copyPre3betOrPostRaisedPot, strongOvercards, strongBackdoorFd, strongBackdoorSd,
                            boardWetness, opponentHasInitiative, botRange, null, null, boardEvaluator, this);
                }
            }
        }

        return action;
    }

    private int getBoardWetness() {
        List<Set<Card>> top10PercentFlopCombosCopy = new ArrayList<>();
        List<Set<Card>> top10PercentTurnCombosCopy = new ArrayList<>();
        List<Set<Card>> top10PercentRiverCombosCopy = new ArrayList<>();

        if(top10percentFlopCombos != null) {
            top10PercentFlopCombosCopy.addAll(top10percentFlopCombos);
        }

        if(top10percentTurnCombos != null) {
            top10PercentTurnCombosCopy.addAll(top10percentTurnCombos);
        }

        if(top10percentRiverCombos != null) {
            top10PercentRiverCombosCopy.addAll(top10percentRiverCombos);
        }

        int boardWetnessToReturn;
        int boardWetnessRiver = 200;
        int boardWetnessTurn = 200;

        if(top10percentRiverCombos != null && !top10percentRiverCombos.isEmpty()) {
            boardWetnessRiver = BoardEvaluator.getBoardWetnessGroup(top10PercentTurnCombosCopy, top10PercentRiverCombosCopy);
        }

        if(top10percentTurnCombos != null && !top10percentTurnCombos.isEmpty()) {
            boardWetnessTurn = BoardEvaluator.getBoardWetnessGroup(top10PercentFlopCombosCopy, top10PercentTurnCombosCopy);
        }

        if(boardWetnessRiver != 200) {
            boardWetnessToReturn = boardWetnessRiver;
        } else {
            boardWetnessToReturn = boardWetnessTurn;
        }

        //System.out.println("BoardWetness: " + boardWetnessToReturn);

        return boardWetnessToReturn;
    }

    private void setMyActionToBetIfPreflopNecessary() {
        if(board == null && myAction == null && computerTotalBetSize + opponentTotalBetSize == 0.75) {
            myAction = "bet";
        }
    }

    public double getAaPotSizeInBb() {
        if(board == null || board.isEmpty()) {
            return (computerTotalBetSize + opponentTotalBetSize) / bigBlind;
        } else {
            return potSize / bigBlind;
        }
    }

    private double getAmountToCallBb(double botBetSizeBb, double opponentBetSizeBb, double botStackBb) {
        double amountToCallBb = opponentBetSizeBb - botBetSizeBb;

        if(amountToCallBb > botStackBb) {
            amountToCallBb = botStackBb;
        }

        return amountToCallBb;
    }

    public String getStreet() {
        String street;

        if(board == null || board.isEmpty()) {
            street = "preflop";
        } else if(board.size() == 3 || board.size() == 4) {
            street = "flopOrTurn";
        } else if(board.size() == 5) {
            street = "river";
        } else {
            street = "wrong";
        }

        return street;
    }

    public double getFacingOdds() {
        if((opponentTotalBetSize - computerTotalBetSize) > computerStack) {
            opponentTotalBetSize = computerStack;
        }

        double facingOdds = (opponentTotalBetSize - computerTotalBetSize) / (potSize + computerTotalBetSize + opponentTotalBetSize);
        return facingOdds;
    }

    private List<String> getEligibleComputerActions() {
        List<String> eligibleActions = new ArrayList<>();

        if(myAction != null) {
            if(myAction.contains("bet") || myAction.contains("raise")) {
                if(myStack == 0 || (computerStack + computerTotalBetSize) <= opponentTotalBetSize) {
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

    private double getEffectiveStackInBb() {
        if(computerStack > myStack) {
            return myStack / bigBlind;
        }
        return computerStack / bigBlind;
    }

    private double getComputerSizing() {
        //double sizing = new Sizing().getRuleBotSizing(opponentTotalBetSize, computerTotalBetSize, computerStack, myStack, potSize, bigBlind, board);

        //double sizing = new Sizing().getRuleBotSizing(computerHandStrength, opponentTotalBetSize, computerTotalBetSize, myStack, computerStack, potSize, board);

        double sizing = new Sizing().getAiBotSizing(opponentTotalBetSize, computerTotalBetSize, computerStack, myStack, potSize, bigBlind, board);

//        double sizing = 0;
//
//        if(computerWrittenAction.contains("bet")) {
//            double sizingInitial = 0.75 * potSize;
//
//            if(sizingInitial <= myStack && sizingInitial <= computerStack) {
//                sizing = sizingInitial;
//            } else {
//                if(myStack > computerStack) {
//                    sizing = computerStack;
//                } else {
//                    sizing = myStack;
//                }
//            }
//        } else if(computerWrittenAction.contains("raise")) {
//            sizing = calculateRaiseAmountNewAi(computerTotalBetSize, opponentTotalBetSize, potSize, myStack, computerStack);
//        }

        return sizing;
    }

    private double calculateRaiseAmountNewAi(double computerBetSize, double facingBetSize, double potSize,
                                             double humanStack, double botStack) {
        double initial = computerBetSize + facingBetSize + potSize;
        double raiseAmount = 1.3 * initial;

        if(raiseAmount <= humanStack && raiseAmount <= botStack) {
            return raiseAmount;
        } else {
            if(humanStack > botStack) {
                return botStack;
            } else {
                return humanStack;
            }
        }
    }

    private void processComputerFoldAction() {
        updatePotSize("computer fold");
        returnBetToPlayerAfterFold("human");
        resetAllBets();
        handWinner = "human";
    }

    private void processComputerCheckAction() {
        if(computerIsButton) {
            if(board != null && board.size() == 5) {
                resetAllBets();
                printWinnerAndHand();
            } else {
                resetActions();
                proceedToNextStreet();
            }
        } else if(board == null && opponentTotalBetSize == bigBlind && computerTotalBetSize == bigBlind){
            String checkThatShouldBeTreatedAsCall = "call";
            updatePotSize(checkThatShouldBeTreatedAsCall);
            resetAllBets();
            resetActions();
            proceedToNextStreet();
        }
    }

    private void processComputerCallAction() {
        if(board == null && (opponentTotalBetSize / bigBlind == 1)) {
            //preflop limp
            computerTotalBetSize = bigBlind;
            computerStack = computerStack - smallBlind;
        } else {
            if(computerStack - (opponentTotalBetSize - computerTotalBetSize) > 0) {
                computerStack = computerStack - (opponentTotalBetSize - computerTotalBetSize);
                computerTotalBetSize = opponentTotalBetSize;
            } else {
                computerTotalBetSize = computerStack + computerTotalBetSize;
                computerStack = 0;
            }

            updatePotSize("call");
            resetAllBets();

            if(board != null && board.size() == 5) {
                printWinnerAndHand();
                return;
            } else if(board == null || board.size() < 5) {
                resetActions();
                proceedToNextStreet();
            }

            if(board != null && !computerIsButton) {
                doComputerAction();
            }
        }
    }

    private void processComputerBetAction() {
        computerIncrementalBetSize = getComputerSizing();
        computerStack = computerStack - computerIncrementalBetSize;
        computerTotalBetSize = computerIncrementalBetSize;
    }

    private void processComputerRaiseAction() {
        double sizingNew = getComputerSizing();

        computerIncrementalBetSize = sizingNew - computerTotalBetSize;
        computerStack = computerStack - computerIncrementalBetSize;
        computerTotalBetSize = sizingNew;
    }

    private void processHumanFoldAction() {
        computerWrittenAction = "You fold";
        updatePotSize("human fold");
        returnBetToPlayerAfterFold("computer");
        resetAllBets();
        handWinner = "computer";
    }

    private void processHumanCheckAction() {
        if(computerIsButton) {
            if(board == null) {
                String checkThatShouldBeTreatedAsCall = "call";
                updatePotSize(checkThatShouldBeTreatedAsCall);
                resetAllBets();
                resetActions();
                proceedToNextStreet();
            }
        } else {
            if(board != null && board.size() == 5) {
                resetAllBets();
                printWinnerAndHand();
            } else {
                resetActions();
                proceedToNextStreet();
            }
        }
    }

    private void processHumanCallAction() {
        if(myStack - (computerTotalBetSize - opponentIncrementalBetSize) >= 0) {
            myStack = myStack - (computerTotalBetSize - opponentIncrementalBetSize);
            opponentTotalBetSize = computerTotalBetSize;
        } else {
            opponentTotalBetSize = myStack + opponentTotalBetSize;
            myStack = 0;
        }

        //don't updatPotSize and resetAllBets if opponent is button and calls bb
        if(board != null || computerIsButton || opponentTotalBetSize != bigBlind || computerTotalBetSize != bigBlind) {
            updatePotSize("call");
            resetAllBets();
        }

        if(board == null) {
            //don't resetActions and proceedToNextStreet if opponent is button and calls bb
            if(computerIsButton || opponentTotalBetSize != bigBlind || computerTotalBetSize != bigBlind) {
                resetActions();
                proceedToNextStreet();
            }
        } else if(board.size() < 5) {
            resetActions();
            proceedToNextStreet();
        } else if(board.size() == 5) {
            printWinnerAndHand();
        }
    }

    private void processHumanBetOrRaiseAction() {
        if(opponentTotalBetSize > myStack) {
            opponentTotalBetSize = opponentIncrementalBetSize + myStack;
            myStack = 0;
        } else {
            if(opponentIncrementalBetSize == 0) {
                opponentIncrementalBetSize = opponentTotalBetSize;
            } else {
                opponentIncrementalBetSize = opponentTotalBetSize - (myStackAtStartOfHand - myStack);
            }

            myStack = myStack - opponentIncrementalBetSize;
        }
    }

    private void getNewCardDeck() {
        deck = BoardEvaluator.getCompleteCardDeck();
    }

    private void dealHoleCards() {
        myHoleCards = new ArrayList<>();
        computerHoleCards = new ArrayList<>();

        myHoleCards.add(getAndRemoveRandomCardFromDeck());
        myHoleCards.add(getAndRemoveRandomCardFromDeck());

//        myHoleCards.add(new Card(3, 's'));
//        myHoleCards.add(new Card(3, 'd'));

        computerHoleCards.add(getAndRemoveRandomCardFromDeck());
        computerHoleCards.add(getAndRemoveRandomCardFromDeck());

//        computerHoleCards.add(new Card(6, 'c'));
//        computerHoleCards.add(new Card(4, 'c'));

        computerHoleCardsCopy = new ArrayList<>();
        computerHoleCardsCopy.addAll(computerHoleCards);

        System.out.println(computerHoleCards.get(0).getRank() + "" + computerHoleCards.get(0).getSuit() + "" +
                computerHoleCards.get(1).getRank() + "" + computerHoleCards.get(1).getSuit());
    }

    private Card getAndRemoveRandomCardFromDeck() {
        Random randomGenerator = new Random();
        int random = randomGenerator.nextInt(deck.size());
        Card cardToReturn = deck.get(random);
        deck.remove(random);

        return cardToReturn;
    }

    private void decideWhoIsButton() {
        if(Math.random() < 0.5) {
            computerIsButton = true;
        } else {
            computerIsButton = false;
        }
    }

    private void setBlinds() {
        smallBlind = 0.25;
        bigBlind = 0.50;
    }

    private void postBlinds() {
        if(computerIsButton) {
            myStack = myStack - bigBlind;
            opponentIncrementalBetSize = bigBlind;
            opponentTotalBetSize = bigBlind;
            computerStack = computerStack - smallBlind;
            computerIncrementalBetSize = smallBlind;
            computerTotalBetSize = smallBlind;
        } else {
            myStack = myStack - smallBlind;
            opponentIncrementalBetSize = smallBlind;
            opponentTotalBetSize = smallBlind;
            computerStack = computerStack - bigBlind;
            computerIncrementalBetSize = bigBlind;
            computerTotalBetSize = bigBlind;
        }
    }

    private boolean isComputerActionNeeded() {
        boolean computerActionNeeded = true;

        if(myAction.equals("fold")) {
            computerActionNeeded = false;
        } else if(myAction.equals("check")) {
            if(!computerIsButton) {
                if(board != null && board.size() == 5) {
                    computerActionNeeded = false;
                }
            } else {
                if(board == null) {
                    computerActionNeeded = false;
                }
            }
        } else if(myAction.equals("call")) {
            if(computerIsButton) {
                computerActionNeeded = false;
                computerWrittenAction = null;
            }
            if(board != null && board.size() == 5) {
                computerActionNeeded = false;
            }
        }
        return computerActionNeeded;
    }

    private boolean isComputerActionNeededIfPlayerIsAllIn() {
        if((myAction != null && (myAction.contains("bet") || myAction.equals("raise"))) && myStack == 0) {
            return true;
        } else {
            if(playerIsAllIn()) {
                return false;
            } else {
                return true;
            }
        }
    }

    private void returnBetToPlayerAfterFold(String player) {
        if(player.equals("human")) {
            myStack = myStack + opponentTotalBetSize;
        } else if(player.equals("computer")) {
            computerStack = computerStack + computerTotalBetSize;
        }
    }

    private void updatePotSize(String action) {
        if(action.equals("call")) {
            potSize = potSize + opponentTotalBetSize + computerTotalBetSize;
        } else if(action.equals("computer fold")) {
            potSize = potSize + computerTotalBetSize;
        } else if(action.equals("human fold")) {
            potSize = potSize + opponentTotalBetSize;
        }
    }

    private void printWinnerAndHand() {
        handWinner = determineWinnerAtShowdown();

        if(handWinner.equals("computer")) {
            computerWrittenAction = ("Computer wins: " + getHoleCardsAsString(computerHoleCards));
        } else if(handWinner.equals("human")) {
            computerWrittenAction = ("You win: " + getHoleCardsAsString(computerHoleCards));
        } else if(handWinner.equals("draw")) {
            computerWrittenAction = ("Draw: " + getHoleCardsAsString(computerHoleCards));
        }
    }

    private void resetGameVariablesAfterFoldOrShowdown() {
        totalHumanScore = totalHumanScore + (myStack - 50);
        totalBotScore = totalBotScore + (computerStack - 50);

        totalHumanScore = Precision.round(totalHumanScore, 2);
        totalBotScore = Precision.round(totalBotScore, 2);

        myStack = 50;
        computerStack = 50;

        potSize = 0;
        opponentIncrementalBetSize = 0;
        opponentTotalBetSize = 0;
        computerIncrementalBetSize = 0;
        computerTotalBetSize = 0;

        if(isComputerIsButton()) {
            setComputerIsButton(false);
        } else {
            setComputerIsButton(true);
        }

        flopCards = null;
        turnCard = null;
        riverCard = null;
        board = null;
        handWinner = null;

        FoldStatsKeeper.updateFoldCountMap("izo", myAction);
        FoldStatsKeeper.updateFoldCountMap("bot-V-izo", computerWrittenActionBeforeFoldStat);

        computerWrittenAction = null;
        computerWrittenActionBeforeFoldStat = null;

        myAction = null;

        opponentHasInitiative = false;
        pre3betOrPostRaisedPot = false;
        opponentDidPreflop4betPot = false;

        top10percentFlopCombos = null;
        top10percentTurnCombos = null;
        top10percentRiverCombos = null;

        botRange = ActionBuilderUtil.getAllStartHandsAsList();
    }

    public ComputerGameNew proceedToNextHand() {
        allocatePotToHandWinner();
        resetGameVariablesAfterFoldOrShowdown();
        numberOfHandsPlayed++;
        getNewCardDeck();
        dealHoleCards();
        postBlinds();
        calculateHandStrengthsAndDraws();

        if(isComputerIsButton()) {
            doComputerAction();
        }
        return this;
    }

    private void proceedToNextStreet() {
        dealRestOfHandAndFinishHandWhenPlayerIsAllIn();

        if(!playerIsAllIn()) {
            if(flopCards == null) {
                dealFlopCards();
            } else if (turnCard == null) {
                dealTurnCard();
            } else if (riverCard == null) {
                dealRiverCard();
            }
        }

        calculateHandStrengthsAndDraws();
    }

    private boolean playerIsAllIn() {
        return computerStack == 0 || myStack == 0;
    }

    private void dealRestOfHandAndFinishHandWhenPlayerIsAllIn() {
        if(playerIsAllIn()) {
            if(flopCards == null) {
                dealFlopCards();
                dealTurnCard();
                dealRiverCard();
            } else if (turnCard == null) {
                dealTurnCard();
                dealRiverCard();
            } else if (riverCard == null) {
                dealRiverCard();
            }

            calculateHandStrengthsAndDraws();
            printWinnerAndHand();
        }
    }

    private void dealFlopCards() {
        flopCards = new ArrayList<>();
        flopCards.add(getAndRemoveRandomCardFromDeck());
        flopCards.add(getAndRemoveRandomCardFromDeck());
        flopCards.add(getAndRemoveRandomCardFromDeck());

//        flopCards.add(new Card(2, 'c'));
//        flopCards.add(new Card(10, 'c'));
//        flopCards.add(new Card(11, 'd'));

        board = new ArrayList<>();
        board.addAll(flopCards);
    }

    private void dealTurnCard() {
        turnCard = getAndRemoveRandomCardFromDeck();

        //turnCard = new Card(5, 's');
        board.add(turnCard);
    }

    private void dealRiverCard() {
        riverCard = getAndRemoveRandomCardFromDeck();

        //riverCard = (new Card(2, 'h'));

        board.add(riverCard);
    }

    private void resetAllBets() {
        opponentIncrementalBetSize = 0;
        opponentTotalBetSize = 0;
        computerIncrementalBetSize = 0;
        computerTotalBetSize = 0;
    }

    private void resetActions() {
        myAction = null;
        computerWrittenAction = null;
    }

    private void roundToTwoDecimals() {
        potSize = Precision.round(potSize, 2);
        myStack = Precision.round(myStack, 2);
        computerStack = Precision.round(computerStack, 2);
        opponentTotalBetSize = Precision.round(opponentTotalBetSize, 2);
        computerTotalBetSize = Precision.round(computerTotalBetSize, 2);
    }

    private String determineWinnerAtShowdown() {
        double computerHandStrength = this.computerHandStrength;

        BoardEvaluator endOfHandBoardEvaluator = new BoardEvaluator(board);
        HandEvaluator endOfHandHandEvaluator = new HandEvaluator(endOfHandBoardEvaluator);

        double humanHandstrength = endOfHandHandEvaluator.getHandStrength(myHoleCards);

        if(computerHandStrength > humanHandstrength) {
            return "computer";
        } else if(computerHandStrength == humanHandstrength) {
            return "draw";
        } else {
            return "human";
        }
    }

    private void allocatePotToHandWinner() {
        if(handWinner.equals("human")) {
            myStack = myStack + potSize;
        } else if(handWinner.equals("computer")) {
            computerStack = computerStack + potSize;
        } else if(handWinner.equals("draw")) {
            myStack = myStack + (potSize / 2);
            computerStack = computerStack + (potSize / 2);
        }
    }

    private String getHoleCardsAsString(List<Card> holeCards) {
        String holeCard1Rank = String.valueOf(holeCards.get(0).getRank());
        String holeCard1Suit = Character.toString(holeCards.get(0).getSuit());
        String holeCard2Rank = String.valueOf(holeCards.get(1).getRank());
        String holeCard2Suit = Character.toString(holeCards.get(1).getSuit());

        return holeCard1Rank + holeCard1Suit + holeCard2Rank + holeCard2Suit;
    }

    private boolean isPreflopCheck() {
        if(board == null) {
            return true;
        }
        return false;
    }


//    public static void main(String[] args) {
//        new ComputerGameNew().theMethod();
//    }
//
//    private void theMethod() {
//        board = new ArrayList<>();
//        board.add(new Card(6, 'd'));
//        board.add(new Card(8, 'h'));
//        board.add(new Card(10, 's'));
//        board.add(new Card(10, 'd'));
//        board.add(new Card(4, 'c'));
//
//        computerHoleCards = new ArrayList<>();
//        computerHoleCards.add(new Card(3, 'd'));
//        computerHoleCards.add(new Card(14, 'd'));
//
//        myHoleCards = new ArrayList<>();
//        myHoleCards.add(new Card(14, 'c'));
//        myHoleCards.add(new Card(7, 'h'));
//
//        calculateHandStrengthsAndDraws();
//        determineWinnerAtShowdown();
//
//    }


    private void calculateHandStrengthsAndDraws() {
        if(board == null) {
            computerHandStrength = new PreflopHandStength().getPreflopHandStength(computerHoleCards);
            computerHasStrongDraw = false;
        } else {
            boardEvaluator = new BoardEvaluator(board);

            if(board.size() == 3) {
                top10percentFlopCombos = boardEvaluator.getTop10percentCombos();
            } else if(board.size() == 4) {
                top10percentTurnCombos = boardEvaluator.getTop10percentCombos();
            } else if(board.size() == 5) {
                top10percentRiverCombos = boardEvaluator.getTop10percentCombos();
            }

            handEvaluator = new HandEvaluator(computerHoleCards, boardEvaluator);

            computerHandStrength = handEvaluator.getHandStrength(computerHoleCards);
            computerHasStrongDraw = hasStrongDraw(handEvaluator);
        }

        computerHandStrengthCopy = computerHandStrength;
        strongFlushDrawCopy = strongFlushDraw;
        strongOosdCopy = strongOosd;
        strongGutshotCopy = strongGutshot;
        strongOvercardsCopy = strongOvercards;
        strongBackdoorSdCopy = strongBackdoorSd;
        strongBackdoorFdCopy = strongBackdoorFd;
    }

    public void calculateHandStrengthsAndDraws(BoardEvaluator boardEvaluator) {
        if(board == null) {
            computerHandStrength = new PreflopHandStength().getPreflopHandStength(computerHoleCards);
            computerHasStrongDraw = false;
        } else {
            if(board.size() == 3) {
                top10percentFlopCombos = boardEvaluator.getTop10percentCombos();
            } else if(board.size() == 4) {
                top10percentTurnCombos = boardEvaluator.getTop10percentCombos();
            } else if(board.size() == 5) {
                top10percentRiverCombos = boardEvaluator.getTop10percentCombos();
            }

            handEvaluator = new HandEvaluator(computerHoleCards, boardEvaluator);

            computerHandStrength = handEvaluator.getHandStrength(computerHoleCards);
            computerHasStrongDraw = hasStrongDraw(handEvaluator);
        }
    }

    private boolean hasStrongDraw(HandEvaluator handEvaluator) {
        strongFlushDraw = handEvaluator.hasDrawOfType("strongFlushDraw");
        strongOosd = handEvaluator.hasDrawOfType("strongOosd");
        strongGutshot = handEvaluator.hasDrawOfType("strongGutshot");
        strongOvercards = handEvaluator.hasDrawOfType("strongOvercards");
        strongBackdoorFd = handEvaluator.hasDrawOfType("strongBackDoorFlush");
        strongBackdoorSd = handEvaluator.hasDrawOfType("strongBackDoorStraight");

        return strongFlushDraw || strongOosd || strongGutshot || strongBackdoorSd || strongBackdoorFd;
    }

    private void doEquityLogic() {
        if(board != null && (board.size() == 3 || board.size() == 4)) {
            Equity equity = new Equity();
            List<Card> computerHoleCardsCopy = new ArrayList<>();
            List<Card> boardCopy = new ArrayList<>();

            computerHoleCardsCopy.addAll(computerHoleCards);
            boardCopy.addAll(board);

            List<Double> handStrengthAtRiverList = equity.getHandstrengthAtRiverList(boardCopy, computerHoleCardsCopy, 25);

            int numberOfScoresAbove90 = equity.getNumberOfScoresAboveLimit(handStrengthAtRiverList, 0.90);

            if(board.size() == 3) {
                if(numberOfScoresAbove90 >= 8) {
                    if(!computerHasStrongDraw) {
                        System.out.println("equity aaa");
                    }

                    computerHasStrongDraw = true;
                    strongFlushDraw = true;
                } else if(numberOfScoresAbove90 >= 7) {
                    if(!computerHasStrongDraw) {
                        System.out.println("equity bbb");
                    }

                    computerHasStrongDraw = true;
                    strongOosd = true;
                } else if(numberOfScoresAbove90 >= 4) {
                    if(!computerHasStrongDraw) {
                        System.out.println("equity ccc");
                    }

                    computerHasStrongDraw = true;
                    strongGutshot = true;
                }
            } else {
                if(numberOfScoresAbove90 >= 5) {
                    if(!computerHasStrongDraw) {
                        System.out.println("equity ddd");
                    }

                    computerHasStrongDraw = true;
                    strongFlushDraw = true;
                } else if(numberOfScoresAbove90 >= 4) {
                    if(!computerHasStrongDraw) {
                        System.out.println("equity eee");
                    }

                    computerHasStrongDraw = true;
                    strongOosd = true;
                } else if(numberOfScoresAbove90 >= 3) {
                    if(!computerHasStrongDraw) {
                        System.out.println("equity fff");
                    }

                    computerHasStrongDraw = true;
                    strongGutshot = true;
                }
            }
        }
    }

    //getters and setters
    public List<Card> getDeck() {
        return deck;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }

    public List<Card> getMyHoleCards() {
        return myHoleCards;
    }

    public void setMyHoleCards(List<Card> myHoleCards) {
        this.myHoleCards = myHoleCards;
    }

    public List<Card> getBotHoleCards() {
        List<Card> computerHoleCardsCopy = new ArrayList<>();
        computerHoleCardsCopy.addAll(computerHoleCards);
        return computerHoleCardsCopy;
    }

    public List<Card> getComputerHoleCards() {
        return computerHoleCards;
    }

    public void setComputerHoleCards(List<Card> computerHoleCards) {
        this.computerHoleCards = computerHoleCards;
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

    public double getOpponentStack() {
        return getMyStack();
    }

    public double getMyStack() {
        return myStack;
    }

    public void setMyStack(double myStack) {
        this.myStack = myStack;
    }

    public double getBotStack() {
        return getComputerStack();
    }

    public double getComputerStack() {
        return computerStack;
    }

    public void setComputerStack(double computerStack) {
        this.computerStack = computerStack;
    }

    public double getOpponentTotalBetSize() {
        return opponentTotalBetSize;
    }

    public void setOpponentTotalBetSize(double opponentTotalBetSize) {
        this.opponentTotalBetSize = opponentTotalBetSize;
    }

    public double getBotTotalBetSize() {
        return getComputerTotalBetSize();
    }

    public double getComputerTotalBetSize() {
        return computerTotalBetSize;
    }

    public void setComputerTotalBetSize(double computerTotalBetSize) {
        this.computerTotalBetSize = computerTotalBetSize;
    }

    public double getPotSize() {
        return potSize;
    }

    public void setPotSize(double potSize) {
        this.potSize = potSize;
    }

    public String getOpponentAction() {
        return getMyAction();
    }

    public String getMyAction() {
        return myAction;
    }

    public void setMyAction(String myAction) {
        this.myAction = myAction;
    }

    public String getMySize() {
        return mySize;
    }

    public void setMySize(String mySize) {
        this.mySize = mySize;
    }

    @Override
    public boolean isBotIsButton() {
        return isComputerIsButton();
    }

    public boolean isComputerIsButton() {
        return computerIsButton;
    }

    public void setComputerIsButton(boolean computerIsButton) {
        this.computerIsButton = computerIsButton;
    }

    public double getOpponentIncrementalBetSize() {
        return opponentIncrementalBetSize;
    }

    public void setOpponentIncrementalBetSize(double opponentIncrementalBetSize) {
        this.opponentIncrementalBetSize = opponentIncrementalBetSize;
    }

    public double getComputerIncrementalBetSize() {
        return computerIncrementalBetSize;
    }

    public void setComputerIncrementalBetSize(double computerIncrementalBetSize) {
        this.computerIncrementalBetSize = computerIncrementalBetSize;
    }

    public List<Card> getFlopCards() {
        return flopCards;
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

    public String getComputerWrittenAction() {
        return computerWrittenAction;
    }

    public void setComputerWrittenAction(String computerWrittenAction) {
        this.computerWrittenAction = computerWrittenAction;
    }

    public String getHandWinner() {
        return handWinner;
    }

    public void setHandWinner(String handWinner) {
        this.handWinner = handWinner;
    }

    public int getNumberOfHandsPlayed() {
        return numberOfHandsPlayed;
    }

    public void setNumberOfHandsPlayed(int numberOfHandsPlayed) {
        this.numberOfHandsPlayed = numberOfHandsPlayed;
    }

    public double getComputerHandStrength() {
        return computerHandStrength;
    }

    public void setComputerHandStrength(double computerHandStrength) {
        this.computerHandStrength = computerHandStrength;
    }

    public boolean isComputerHasStrongDraw() {
        return computerHasStrongDraw;
    }

    public void setComputerHasStrongDraw(boolean computerHasStrongDraw) {
        this.computerHasStrongDraw = computerHasStrongDraw;
    }

    public double getTotalHumanScore() {
        return totalHumanScore;
    }

    public void setTotalHumanScore(double totalHumanScore) {
        this.totalHumanScore = totalHumanScore;
    }

    public double getTotalBotScore() {
        return totalBotScore;
    }

    public void setTotalBotScore(double totalBotScore) {
        this.totalBotScore = totalBotScore;
    }

    public void setFlopCards(List<Card> flopCards) {
        this.flopCards = flopCards;
    }

    public void setTurnCard(Card turnCard) {
        this.turnCard = turnCard;
    }

    public void setRiverCard(Card riverCard) {
        this.riverCard = riverCard;
    }

    public void setBoard(List<Card> board) {
        this.board = board;
    }

    public boolean isStrongFlushDraw() {
        return strongFlushDraw;
    }

    public void setStrongFlushDraw(boolean strongFlushDraw) {
        this.strongFlushDraw = strongFlushDraw;
    }

    public boolean isStrongOosd() {
        return strongOosd;
    }

    public void setStrongOosd(boolean strongOosd) {
        this.strongOosd = strongOosd;
    }

    public boolean isStrongGutshot() {
        return strongGutshot;
    }

    public void setStrongGutshot(boolean strongGutshot) {
        this.strongGutshot = strongGutshot;
    }

    public double getMyStackAtStartOfHand() {
        return myStackAtStartOfHand;
    }

    public void setMyStackAtStartOfHand(double myStackAtStartOfHand) {
        this.myStackAtStartOfHand = myStackAtStartOfHand;
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

    public static HandEvaluator getHandEvaluator() {
        return ComputerGameNew.handEvaluator;
    }

    public static void setHandEvaluator(HandEvaluator handEvaluator) {
        ComputerGameNew.handEvaluator = handEvaluator;
    }

    @Override
    public boolean isOpponentHasInitiative() {
        return opponentHasInitiative;
    }

    @Override
    public void setOpponentHasInitiative(boolean opponentHasInitiative) {
        this.opponentHasInitiative = opponentHasInitiative;
    }

    @Override
    public boolean isPre3betOrPostRaisedPot() {
        return pre3betOrPostRaisedPot;
    }

    @Override
    public void setPre3betOrPostRaisedPot(boolean pre3betOrPostRaisedPot) {
        this.pre3betOrPostRaisedPot = pre3betOrPostRaisedPot;
    }

    @Override
    public boolean isOpponentDidPreflop4betPot() {
        return opponentDidPreflop4betPot;
    }

    @Override
    public void setOpponentDidPreflop4betPot(boolean opponentDidPreflop4betPot) {
        this.opponentDidPreflop4betPot = opponentDidPreflop4betPot;
    }

    public List<Set<Card>> getTop10percentFlopCombos() {
        return top10percentFlopCombos;
    }

    public void setTop10percentFlopCombos(List<Set<Card>> top10percentFlopCombos) {
        this.top10percentFlopCombos = top10percentFlopCombos;
    }

    public List<Set<Card>> getTop10percentTurnCombos() {
        return top10percentTurnCombos;
    }

    public void setTop10percentTurnCombos(List<Set<Card>> top10percentTurnCombos) {
        this.top10percentTurnCombos = top10percentTurnCombos;
    }

    public List<Set<Card>> getTop10percentRiverCombos() {
        return top10percentRiverCombos;
    }

    public void setTop10percentRiverCombos(List<Set<Card>> top10percentRiverCombos) {
        this.top10percentRiverCombos = top10percentRiverCombos;
    }

    public String getComputerWrittenActionBeforeFoldStat() {
        return computerWrittenActionBeforeFoldStat;
    }

    public void setComputerWrittenActionBeforeFoldStat(String computerWrittenActionBeforeFoldStat) {
        this.computerWrittenActionBeforeFoldStat = computerWrittenActionBeforeFoldStat;
    }

    public Map<Integer, List<Card>> getBotRange() {
        return botRange;
    }

    public void setBotRange(Map<Integer, List<Card>> botRange) {
        this.botRange = botRange;
    }

    public boolean isBalanceRuleMethodHasBeenCalled() {
        return balanceRuleMethodHasBeenCalled;
    }

    public void setBalanceRuleMethodHasBeenCalled(boolean balanceRuleMethodHasBeenCalled) {
        this.balanceRuleMethodHasBeenCalled = balanceRuleMethodHasBeenCalled;
    }

    public List<Card> getComputerHoleCardsCopy() {
        return computerHoleCardsCopy;
    }

    public void setComputerHoleCardsCopy(List<Card> computerHoleCardsCopy) {
        this.computerHoleCardsCopy = computerHoleCardsCopy;
    }
}
