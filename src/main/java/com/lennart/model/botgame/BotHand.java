package com.lennart.model.botgame;

import com.lennart.model.action.Action;
import com.lennart.model.action.Actionable;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.imageprocessing.sites.netbet.NetBetTableReader;
import com.lennart.model.action.actionbuilders.ActionBuilderUtil;

import java.util.*;

/**
 * Created by LPO21630 on 16-2-2017.
 */
public class BotHand implements Actionable {

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
    private String street;
    private boolean previousBluffAction;
    private boolean drawBettingActionDone;
    private String botWrittenAction;
    private String streetAtPreviousActionRequest;

    private List<String> botActionHistory;

    private double botStackAtBeginningOfHand;
    private double opponentStackAtBeginningOfHand;

    private String opponentType;

    private boolean pre3betOrPostRaisedPot;

    private List<String> opponentActionHistory;

    private boolean bettingActionDoneByPassivePlayer;
    private double handsPlayedAgainstOpponent;

    private boolean instaFoldHappened;
    private boolean opponentIsDecentThinking;

    public BotHand() {
        //default constructor
    }

    public BotHand(BotTable botTable) {
        gameVariablesFiller = new GameVariablesFiller(this);

        setSmallBlind();
        setBigBlind();
        setBotStack(true);
        setOpponentStack(true);
        setPotSize();
        setBotTotalBetSize();
        setOpponentTotalBetSize();
        setOpponentPlayerName();
        setBotIsButton(botTable);
        setBotHoleCard1();
        setBotHoleCard2();
        setKnownGameCards();
        setBotHoleCards();
        setStreetAndPreviousStreet();
        setOpponentAction(botTable);
        checkIfPre3betOrPostRaisedPot();
        setTheInitialStackSizeOfOpponentIfNotYetSet(botTable);
        setOpponentType(botTable);
        checkIfOpponentIsDecentThinking(botTable);

        forceQuitIfEffectiveStackSizeAbove100bb();
    }

    public BotHand updateVariables(BotTable botTable) {
        gameVariablesFiller = new GameVariablesFiller(this);

        if(foldOrShowdownOccured()) {
            updateStats(botTable);
            return new BotHand(botTable);
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
        setOpponentAction(botTable);
        checkIfPre3betOrPostRaisedPot();

        System.out.println("opponent action: " + opponentAction + " " + opponentTotalBetSize);

        forceQuitIfEffectiveStackSizeAbove100bb();

        return this;
    }

    public void getNewBotAction() {
        if(potSize == -1) {
            potSize = NetBetTableReader.getPotSizeCheckFromImage();
        }

        if(defaultCheckActionAfterCallNeeded()) {
            doDefaultCheck();
        } else if (potSize == -1 || opponentStack == -1 || botStack == -1 || opponentTotalBetSize == -1 || botTotalBetSize == -1) {
            getActionWhenTableIsMisread();
        } else {
            if(opponentAction == null) {
                if(opponentTotalBetSize > 0 && !(opponentTotalBetSize == bigBlind && botTotalBetSize == smallBlind)) {
                    opponentAction = "bet";
                    System.out.println("opponentAction was wrongfully set to null. Changed to 'bet'");
                }
            }

            botAction = new Action(this);
            updateBotActionHistory(botAction);
            botWrittenAction = botAction.getWrittenAction();
        }

        checkIfPre3betOrPostRaisedPot();
        preflopFinalPreventFoldCheck();
        postFlopFinalPreventFoldCheck();
        NetBetTableReader.performActionOnSite(botAction);
    }

    private void preflopFinalPreventFoldCheck() {
        if(board == null) {
            if(botHoleCards != null && knownGameCards != null) {
                if(ActionBuilderUtil.handIsJjPlusOrAk(botHoleCards, knownGameCards)) {
                    if(botAction != null && botAction.getAction() != null && botAction.getAction().contains("fold")) {
                        System.out.println("changed action from: " + botAction.getAction() + " to 'call' in preflopFinalPreventFoldCheck()");
                        setActionToCall();
                    }
                }
            }
        }
    }

    private void postFlopFinalPreventFoldCheck() {
        if(board != null) {
            if(botAction != null && botAction.getAction() != null && botAction.getAction().contains("fold")) {
                System.out.println("doing final check before fold in postFlopFinalPreventFoldCheck()");
                extraCallCheckOnMisreadBoardPostFlop();
            }
        }
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
        botAction = null;
        updateBotActionHistory(null);
        botWrittenAction = "check";
        System.out.println("default check from doDefaultCheck()");
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

    private void updateOpponentActionHistory(String action) {
        if(opponentActionHistory == null) {
            opponentActionHistory = new ArrayList<>();
        }

        if(action == null) {
            opponentActionHistory.add(street + " null");
        } else {
            opponentActionHistory.add(street + " " + action);
        }
    }

    private void getActionWhenTableIsMisread() {
        boolean clickActionDone = false;
        botAction = new Action();
        double amountToCall = NetBetTableReader.getAmountToCall();

        if(board == null) {
            if(botHoleCards != null && knownGameCards != null) {
                if(ActionBuilderUtil.handIsJjPlusOrAk(botHoleCards, knownGameCards)) {
                    System.out.println("Call on misread board. Preflop, and hand is JJ+ or AK" );
                    setActionToCall();
                    clickActionDone = true;
                }
            }
        }

        if(!clickActionDone && board != null && amountToCall > 0) {
            BoardEvaluator boardEvaluatorMisreadTable = new BoardEvaluator(board);
            HandEvaluator handEvaluator = new HandEvaluator(boardEvaluatorMisreadTable);
            double handStrength = handEvaluator.getHandStrength(botHoleCards);
            System.out.println("Handstrength in getActionWhenTableIsMisread(): " + handStrength);

            if((amountToCall / bigBlind) < 10) {
                if(handStrength >= 0.67) {
                    System.out.println("Call on misread board. Opponent total betsize / bigblind < 10: " + (opponentTotalBetSize / bigBlind)
                            + " handstrength: " + handStrength);
                    setActionToCall();
                    clickActionDone = true;
                }
            } else if((amountToCall / bigBlind) < 20) {
                if(handStrength >= 0.77) {
                    System.out.println("Call on misread board. Opponent total betsize / bigblind < 20: " + (opponentTotalBetSize / bigBlind)
                            + " handstrength: " + handStrength);
                    setActionToCall();
                    clickActionDone = true;
                }
            } else if((amountToCall / bigBlind) < 40) {
                if(handStrength >= 0.90) {
                    System.out.println("Call on misread board. Opponent total betsize / bigblind < 40: " + (opponentTotalBetSize / bigBlind)
                            + " handstrength: " + handStrength);
                    setActionToCall();
                    clickActionDone = true;
                }
            } else if((amountToCall / bigBlind) < 70) {
                if(handStrength >= 0.97) {
                    System.out.println("Call on misread board. Opponent total betsize / bigblind < 70: " + (opponentTotalBetSize / bigBlind)
                            + " handstrength: " + handStrength);
                    setActionToCall();
                    clickActionDone = true;
                }
            }
        }

        if(!clickActionDone && board != null) {
            clickActionDone = extraCallCheckOnMisreadBoardPostFlop();
        }

        if(!clickActionDone && NetBetTableReader.readMiddleActionButton().contains("Check")) {
            botAction.setAction("check");
            updateBotActionHistory(botAction);
            System.out.println("check on misread board");
            clickActionDone = true;
        }

        if(!clickActionDone) {
            botAction.setAction("fold");
            System.out.println("fold on misread board");
        }
    }

    private boolean extraCallCheckOnMisreadBoardPostFlop() {
        if(board != null) {
            String middleActionButton = NetBetTableReader.readMiddleActionButton();
            String rightActionButton = NetBetTableReader.readRightActionButton();

            BoardEvaluator boardEvaluatorMisreadTable = new BoardEvaluator(board);
            HandEvaluator handEvaluator = new HandEvaluator(boardEvaluatorMisreadTable);
            double handStrength = handEvaluator.getHandStrength(botHoleCards);
            System.out.println("Handstrength in extraCallCheckOnMisreadBoardPostFlop(): " + handStrength);

            if(middleActionButton.contains("Call") || rightActionButton.contains("Call") ||
                    (NetBetTableReader.middleActionButtonIsNotPresent() && rightActionButton.contains("All"))) {
                if(handStrength >= 0.8) {
                    setActionToCall();
                    System.out.println("Handstrength >= 0.8: call in extraCallCheckOnMisreadBoardPostFlop()");
                    return true;
                } else if(botStack < 0.15 * botStackAtBeginningOfHand) {
                    setActionToCall();
                    System.out.println("Pot committed according to extaCallCheckOnMisreadBoardPostFlop()");
                    return true;
                }
            }
        }
        return false;
    }

    private void setActionToCall() {
        if(botAction == null) {
            botAction = new Action();
        }

        botAction.setAction("call");
        updateBotActionHistory(botAction);
        botWrittenAction = "call";
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
        if(botStack < 0) {
            System.out.println("set botstack to default value of 100bb in validateBotStack because botStack < 0");
            botStack = 100 * bigBlind;
        } else if(botStack > botStackAtBeginningOfHand) {
            System.out.println("set botstack to default value of 100bb in validateBotStack because botStack > botStackAtBeginningOfHand");
            botStack = 100 * bigBlind;
        } else if(botStack / bigBlind > 1000) {
            System.out.println("set botstack to default value of 100bb in validateBotStack because botStack > 1000bb");
            botStack = 100 * bigBlind;
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
        if(opponentStack < 0) {
            System.out.println("set opponentStack to default value of 100bb in validateOpponentStack because opponentStack < 0");
            opponentStack = 100 * bigBlind;
        } else if(opponentStack > opponentStackAtBeginningOfHand) {
            System.out.println("set opponentStack to default value of 100bb in validateOpponentStack because opponentStack >" +
                    " opponentStackAtBeginningOfHand");
            opponentStack = 100 * bigBlind;
        } else if(opponentStack / bigBlind > 1000) {
            System.out.println("set opponentStack to default value of 100bb in validateOpponentStack because opponentStack > 1000bb");
            opponentStack = 100 * bigBlind;
        }
    }

    private void setPotSize() {
        potSize = gameVariablesFiller.getPotSize();
        validatePotSize();
        System.out.println("Potsize: " + potSize);
    }

    private void validatePotSize() {
        if(potSize < 0) {
            System.out.println("set potSize to default value of 18bb in validatePotSize because potSize < 0");
            potSize = 18 * bigBlind;
        } else if(potSize > (botStackAtBeginningOfHand + opponentStackAtBeginningOfHand)) {
            System.out.println("set potSize to default value of 18bb in validatePotSize because potSize > " +
                    "(botStackAtBeginningOfHand + opponentStackAtBeginningOfHand)");
            potSize = 18 * bigBlind;
        } else if(potSize / bigBlind > 1000) {
            System.out.println("set potSize to default value of 18bb in validatePotSize because potSize / bigBlind > 1000");
            potSize = 18 * bigBlind;
        }
    }

    private void setBotTotalBetSize() {
        botTotalBetSize = gameVariablesFiller.getBotTotalBetSize();
        validateBotTotalBetSize();
    }

    private void validateBotTotalBetSize() {
        if(botTotalBetSize < 0) {
            System.out.println("set botTotalBetSize to default value of 0 in validateBotTotalBetSize because botTotalBetSize < 0");
            botTotalBetSize = 0;
        } else if(botTotalBetSize > botStackAtBeginningOfHand) {
            System.out.println("set botTotalBetSize to default value of 0 in validateBotTotalBetSize because botTotalBetSize > botStackAtBeginningOfHand");
            botTotalBetSize = 0;
        } else if(botTotalBetSize / bigBlind > 1000) {
            System.out.println("set botTotalBetSize to default value of 0 in validateBotTotalBetSize because botTotalBetSize / bigBlind > 1000");
            botTotalBetSize = 0;
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
        } else if(opponentTotalBetSize > botStack) {
            opponentTotalBetSize = botStack;
        }
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
        updateOpponentActionHistory(opponentAction);
        checkIfBettingActionIsDoneByPassivePlayer(opponentAction);

        if(!botIsButton && opponentActionHistory != null && opponentActionHistory.size() == 1 &&
                opponentActionHistory.get(0).contains("raise")) {
            botTable.setLastPfrSizingOfOpponent(opponentPlayerName, opponentTotalBetSize / bigBlind);
        }
    }

    private void checkIfBettingActionIsDoneByPassivePlayer(String action) {
        if(opponentType != null && opponentType.contains("Passive")) {
            if(action != null && (action.contains("bet") || action.contains("raise"))) {
                if(!botIsButton) {
                    if(opponentActionHistory != null && opponentActionHistory.size() > 1) {
                        //to check if the betting action done by villain is not an initial PFR, which we don't count
                        bettingActionDoneByPassivePlayer = true;
                        System.out.println("bettingActionDoneByPassivePlayer set to true");
                    }
                } else {
                    bettingActionDoneByPassivePlayer = true;
                    System.out.println("bettingActionDoneByPassivePlayer set to true");
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

    private void setBotIsButton(BotTable botTable) {
        botIsButton = gameVariablesFiller.isBotIsButton();
        botTable.addBooleanToBotIsButtonHistoryPerOpponentMap(opponentPlayerName, botIsButton);
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

    private void updateStats(BotTable botTable) {
        updateHandsEligibleForVpip(botTable);
        updateHandsVpip(botTable);
        updateHandsEligibleFor3bet(botTable);
        udpateHands3bet(botTable);
        updateHandsEligibleForIpPfr(botTable);
        updateHandsIpPfr(botTable);
    }

    private void updateHandsEligibleForVpip(BotTable botTable) {
        Map<String, List<Boolean>> botIsButtonHistoryPerOpponentMap = botTable.getBotIsButtonHistoryPerOpponentMap();
        boolean countActionDone = false;

        if(botIsButtonHistoryPerOpponentMap != null && botIsButtonHistoryPerOpponentMap.get(opponentPlayerName) != null) {
            if(botIsButtonHistoryPerOpponentMap.get(opponentPlayerName).size() >= 2) {
                List<Boolean> buttonHistoryAgainstOpponent = botIsButtonHistoryPerOpponentMap.get(opponentPlayerName);

                if(buttonHistoryAgainstOpponent.get(buttonHistoryAgainstOpponent.size() - 1).
                        equals(buttonHistoryAgainstOpponent.get(buttonHistoryAgainstOpponent.size() - 2))) {
                    instaFoldHappened = true;
                    botTable.addHandToHandsEligibleForVpip(opponentPlayerName);
                    System.out.println("botIsButton same as previous hand, so opponent insta folded last hand preflop. " +
                            "Hand added to vpip eligible count");
                    countActionDone = true;
                }
            }
        }

        if(!countActionDone) {
            if(!botIsButton) {
                botTable.addHandToHandsEligibleForVpip(opponentPlayerName);
            } else {
                if(botActionHistory.size() == 1 && botActionHistory.get(0).contains("fold")) {
                    //keep equal
                } else {
                    botTable.addHandToHandsEligibleForVpip(opponentPlayerName);
                }
            }
        }
    }

    private void updateHandsVpip(BotTable botTable) {
        if(instaFoldHappened) {
            System.out.println("no hand added to vpip count, insta fold happened previous hand");
        } else {
            if(opponentActionHistory.size() == 1 &&
                    (opponentActionHistory.get(0).contains("call") ||  opponentActionHistory.get(0).contains("raise"))) {
                botTable.addHandToHandsVpip(opponentPlayerName);
            } else if(opponentActionHistory.size() > 1) {
                botTable.addHandToHandsVpip(opponentPlayerName);
            }
        }
    }

    private void updateHandsEligibleFor3bet(BotTable botTable) {
        if(botIsButton) {
            if(botActionHistory.get(0).contains("raise")) {
                botTable.addHandToHandsEligibleFor3bet(opponentPlayerName);
            }
        }
    }

    private void udpateHands3bet(BotTable botTable) {
        if(botIsButton) {
            //opponentActionHistory.get(1) because get(0) is always 'preflop null' when botIsButton
            if(opponentActionHistory.size() >= 2) {
                if(opponentActionHistory.get(1).contains("raise")) {
                    botTable.addHandToHands3bet(opponentPlayerName);
                }
            }
        }
    }

    private void updateHandsEligibleForIpPfr(BotTable botTable) {
        if(instaFoldHappened || !botIsButton) {
            botTable.addHandToHandsEligibleForIpPfr(opponentPlayerName);
        }
    }

    private void updateHandsIpPfr(BotTable botTable) {
        if(!botIsButton && opponentActionHistory.get(0).contains("raise")) {
            botTable.addHandToHandsIpPfr(opponentPlayerName);
        }
    }

    private void setOpponentType(BotTable botTable) {
        double vpip;
        double _3bet;
        double opponentHands;

        if(botTable.getOpponentPlayerNamesAndStats() != null &&
                botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName) != null) {
            vpip = botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(1) / botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(0);
            _3bet = botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(3) / botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(2);
            opponentHands = botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(0);

            for (Map.Entry<String, List<Double>> entry : botTable.getOpponentPlayerNamesAndStats().entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
        } else {
            vpip = 0;
            _3bet = 0;
            opponentHands = 0;
            System.out.println("opponentPlayerNamesAndStats is null or the opponent is not yet in there");
        }

        handsPlayedAgainstOpponent = opponentHands;

        String tightness;
        String aggressiveness;

        System.out.println("vpip in setOpponentType(): " + vpip);
        System.out.println("_3bet in setOpponentType(): " + _3bet);
        System.out.println("opponentHands in setOpponentType(): " + opponentHands);

        if(opponentHands > 20) {
            if(vpip < 0) {
                tightness = "medium";
            } else if(vpip < 0.63) {
                tightness = "tight";
            } else if(vpip < 0.77) {
                tightness = "medium";
            } else {
                tightness = "loose";
            }

            if(_3bet < 0) {
                aggressiveness = "Medium";
            } else if(_3bet < 0.14) {
                aggressiveness = "Passive";
            } else if(_3bet < 0.26) {
                aggressiveness = "Medium";
            } else {
                aggressiveness = "Aggressive";
            }
        } else {
            tightness = "medium";
            aggressiveness = "Medium";
        }

        opponentType = tightness + aggressiveness;
        System.out.println("opponentType: " + opponentType);
    }

    private void checkIfOpponentIsDecentThinking(BotTable botTable) {
        double opponentHands = 0;
        double vpip = 0;
        double pfr = 0;
        double _3bet = 0;
        double pfrSizing = 0;
        double initialStackSize = 0;

        if(botTable.getOpponentPlayerNamesAndStats() != null &&
                botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName) != null) {
            opponentHands = botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(0);
            vpip = botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(1) / botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(0);
            pfr = botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(5) / botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(0);
            _3bet = botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(3) / botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(2);
            pfrSizing = botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(7);
            initialStackSize = botTable.getOpponentPlayerNamesAndStats().get(opponentPlayerName).get(6);
        }

        if(opponentHands > 20) {
            if(initialStackSize >= 90) {
                if(pfrSizing >= 2 && pfrSizing <= 5) {
                    if(vpip > 40 && vpip <= 90) {
                        if(pfr > 40 && pfr <= 85) {
                            if(_3bet >= 9 && _3bet < 40) {
                                opponentIsDecentThinking = false;
                                System.out.println("opponentIsDecentThinking is true! (but kept at false)");
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkIfPre3betOrPostRaisedPot() {
        if(!pre3betOrPostRaisedPot) {
            if(board == null) {
                if(botIsButton) {
                    if(opponentAction != null && opponentAction.contains("raise")) {
                        pre3betOrPostRaisedPot = true;
                    }
                } else {
                    if(botAction != null && botAction.getAction() != null && botAction.getAction().contains("raise")) {
                        if(opponentTotalBetSize != bigBlind) {
                            pre3betOrPostRaisedPot = true;
                        }
                    }
                }
            } else {
                if(opponentAction != null && opponentAction.contains("bet")) {
                    if(opponentTotalBetSize > potSize) {
                        //overbet occurred
                        pre3betOrPostRaisedPot = true;
                    }
                }

                if(!pre3betOrPostRaisedPot && street.equals(streetAtPreviousActionRequest)) {
                    if(opponentAction != null && opponentAction.contains("raise")) {
                        pre3betOrPostRaisedPot = true;
                    } else if(botAction != null && botAction.getAction() != null && botAction.getAction().contains("raise")) {
                        pre3betOrPostRaisedPot = true;
                    }
                }
            }
        }
        System.out.println("pre3betOrPostRaisedPot is: " + pre3betOrPostRaisedPot);
    }

    private void setTheInitialStackSizeOfOpponentIfNotYetSet(BotTable botTable) {
        botTable.setInitialStackSizeOfOpponent(opponentPlayerName, opponentStack / bigBlind);
    }

    private void forceQuitIfEffectiveStackSizeAbove100bb() {
        if(botStack / bigBlind >= 100 || opponentStack / bigBlind >= 100) {
            System.out.println("Effective stacksize became 100bb or more. Forced quit.");
            throw new RuntimeException();
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

    public Card getTurnCard() {
        return turnCard;
    }

    public Card getRiverCard() {
        return riverCard;
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

    public List<String> getBotActionHistory() {
        return botActionHistory;
    }

    public void setBotActionHistory(List<String> botActionHistory) {
        this.botActionHistory = botActionHistory;
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

    @Override
    public String getOpponentType() {
        return opponentType;
    }

    public void setOpponentType(String opponentType) {
        this.opponentType = opponentType;
    }

    @Override
    public boolean isPre3betOrPostRaisedPot() {
        return pre3betOrPostRaisedPot;
    }

    public void setPre3betOrPostRaisedPot(boolean pre3betOrPostRaisedPot) {
        this.pre3betOrPostRaisedPot = pre3betOrPostRaisedPot;
    }

    @Override
    public boolean isBettingActionDoneByPassivePlayer() {
        return bettingActionDoneByPassivePlayer;
    }

    public void setBettingActionDoneByPassivePlayer(boolean bettingActionDoneByPassivePlayer) {
        this.bettingActionDoneByPassivePlayer = bettingActionDoneByPassivePlayer;
    }

    @Override
    public double getHandsPlayedAgainstOpponent() {
        return handsPlayedAgainstOpponent;
    }

    public void setHandsPlayedAgainstOpponent(double handsPlayedAgainstOpponent) {
        this.handsPlayedAgainstOpponent = handsPlayedAgainstOpponent;
    }

    @Override
    public boolean isOpponentIsDecentThinking() {
        return opponentIsDecentThinking;
    }

    public void setOpponentIsDecentThinking(boolean opponentIsDecentThinking) {
        this.opponentIsDecentThinking = opponentIsDecentThinking;
    }
}
