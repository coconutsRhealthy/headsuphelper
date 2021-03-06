package com.lennart.model.computergame;

import com.lennart.model.action.Action;
import com.lennart.model.action.Actionable;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.card.Card;
import org.apache.commons.math3.util.Precision;

import java.util.*;

/**
 * Created by lennart on 11-12-16.
 */
public class ComputerGame implements Actionable {

    private List<Card> deck;
    private List<Card> myHoleCards;
    private List<Card> computerHoleCards;
    private List<Card> flopCards;
    private Card turnCard;
    private Card riverCard;
    private double smallBlind;
    private double bigBlind;
    private double myStack;
    private double computerStack;
    private double opponentIncrementalBetSize;
    private double opponentTotalBetSize;
    private double computerIncrementalBetSize;
    private double computerTotalBetSize;
    private double potSize;
    private boolean computerIsButton;
    private Action computerAction;
    private Set<Card> knownGameCards;
    private String myAction;
    private String mySize;
    private List<Card> board;
    private String computerWrittenAction;
    private String handWinner;
    private int numberOfHandsPlayed;
    private boolean previousBluffAction;
    private boolean drawBettingActionDone;

    private List<String> botActionHistory;

    private String opponentType;

    private boolean pre3betOrPostRaisedPot;

    private double handsHumanOopFacingPreflop2bet;
    private double handsHumanOopCall2bet;
    private double handsHumanOop3bet;
    private double opponentPreCall2betStat;
    private double opponentPre3betStat;
    private boolean opponentPreflopStatsDoneForHand;

    private boolean bettingActionDoneByPassivePlayer;
    private double handsPlayedAgainstOpponent;

    private boolean opponentIsDecentThinking;

    private String floatAction;
    private boolean botIsPre3bettor;
    private boolean opponentBetsOrRaisesPostFlop;

    public ComputerGame() {
        //default constructor
    }

    public ComputerGame(String initialize) {
        numberOfHandsPlayed = 0;
        getNewCardDeck();
        dealHoleCards();
        decideWhoIsButton();
        myStack = 50;
        computerStack = 50;
        setBlinds();
        postBlinds();

        if(isComputerIsButton()) {
            doComputerAction();

            if(computerWrittenAction.contains("fold")) {
                updatePotSize("computer fold");
                resetAllBets();
            }
        }
    }

    public ComputerGame submitHumanActionAndDoComputerAction() {
        calculateOpponentPreflopStats();
        boolean computerActionNeeded = isComputerActionNeeded();

        if(myAction.equals("fold")) {
            processHumanFoldAction();
        } else if(myAction.equals("check")) {
            processHumanCheckAction();
        } else if(myAction.equals("call")) {
            processHumanCallAction();
        } else if(myAction.equals("bet") || myAction.equals("raise")) {
            processHumanBetOrRaiseAction();
        }

        if(computerActionNeeded && isComputerActionNeededIfPlayerIsAllIn()) {
            doComputerAction();
        }
        roundToTwoDecimals();
        return this;
    }

    private void doComputerAction() {
        computerAction = new Action(this);
        computerWrittenAction = computerAction.getWrittenAction();

        updateBotActionHistory(computerAction);

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
        roundToTwoDecimals();
    }

    private void updateBotActionHistory(Action action) {
        if(botActionHistory == null) {
            botActionHistory = new ArrayList<>();
        }

        if(board == null) {
            botActionHistory.add("preflop " + action.getAction());
        } else if(board.size() == 3) {
            botActionHistory.add("flop " + action.getAction());
        } else if(board.size() == 4) {
            botActionHistory.add("turn " + action.getAction());
        } else if(board.size() == 5) {
            botActionHistory.add("river " + action.getAction());
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
        if(computerStack - (opponentTotalBetSize - computerTotalBetSize) > 0) {
            computerStack = computerStack - (opponentTotalBetSize - computerTotalBetSize);
            computerTotalBetSize = opponentTotalBetSize;
        } else {
            computerTotalBetSize = computerStack;
            computerStack = 0;
        }

        updatePotSize("call");
        resetAllBets();

        if(board == null || board.size() < 5) {
            resetActions();
            proceedToNextStreet();
        } else if(board.size() == 5) {
            printWinnerAndHand();
        }

        if(board != null && board.size() != 5 && !computerIsButton) {
            addCheckToWrittenAction();
        }
    }

    private void processComputerBetAction() {
        computerIncrementalBetSize = computerAction.getSizing();
        computerStack = computerStack - computerIncrementalBetSize;
        computerTotalBetSize = computerIncrementalBetSize;
    }

    private void processComputerRaiseAction() {
        computerIncrementalBetSize = computerAction.getSizing() - computerTotalBetSize;
        computerStack = computerStack - computerIncrementalBetSize;
        computerTotalBetSize = computerAction.getSizing();
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
            //nothing
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
            opponentTotalBetSize = myStack;
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
            opponentIncrementalBetSize = opponentTotalBetSize - opponentIncrementalBetSize;
            myStack = myStack - opponentIncrementalBetSize;
        }
    }

    private void getNewCardDeck() {
        deck = BoardEvaluator.getCompleteCardDeck();
    }

    private void dealHoleCards() {
        myHoleCards = new ArrayList<>();
        computerHoleCards = new ArrayList<>();
        knownGameCards = new HashSet<>();

        myHoleCards.add(getAndRemoveRandomCardFromDeck());
        myHoleCards.add(getAndRemoveRandomCardFromDeck());
        computerHoleCards.add(getAndRemoveRandomCardFromDeck());
        computerHoleCards.add(getAndRemoveRandomCardFromDeck());

        System.out.println(computerHoleCards.get(0).getRank() + "" + computerHoleCards.get(0).getSuit() + "" +
                                computerHoleCards.get(1).getRank() + "" + computerHoleCards.get(1).getSuit());

        knownGameCards.addAll(computerHoleCards);
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

    @Override
    public void removeHoleCardsFromKnownGameCards() {
        knownGameCards.removeAll(computerHoleCards);
    }

    @Override
    public void addHoleCardsToKnownGameCards() {
        Set<Card> holeCardsAsSet = new HashSet<>();
        holeCardsAsSet.addAll(computerHoleCards);

        knownGameCards.addAll(holeCardsAsSet);
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
        if((myAction != null && (myAction.equals("bet") || myAction.equals("raise"))) && myStack == 0) {
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
        if(myStack < 50) {
            myStack = 50;
        }

        if(computerStack < 50) {
            computerStack = 50;
        }

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
        knownGameCards = null;
        handWinner = null;
        computerWrittenAction = null;
        opponentPreflopStatsDoneForHand = false;
        previousBluffAction = false;
        drawBettingActionDone = false;
    }

    private void addCheckToWrittenAction() {
        if(computerWrittenAction.contains("Preflop")) {
            computerWrittenAction = computerWrittenAction + " and Flop: Computer checks";
        } else if(computerWrittenAction.contains("Flop")) {
            computerWrittenAction = computerWrittenAction + " and Turn: Computer checks";
        } else if(computerWrittenAction.contains("Turn")) {
            computerWrittenAction = computerWrittenAction + " and River: Computer checks";
        }
    }

    public ComputerGame proceedToNextHand() {
        allocatePotToHandWinner();
        resetGameVariablesAfterFoldOrShowdown();
        numberOfHandsPlayed++;
        getNewCardDeck();
        dealHoleCards();
        postBlinds();

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

            printWinnerAndHand();
        }
    }

    private void dealFlopCards() {
        flopCards = new ArrayList<>();
        flopCards.add(getAndRemoveRandomCardFromDeck());
        flopCards.add(getAndRemoveRandomCardFromDeck());
        flopCards.add(getAndRemoveRandomCardFromDeck());

        board = new ArrayList<>();
        board.addAll(flopCards);
        knownGameCards.addAll(flopCards);
    }

    private void dealTurnCard() {
        turnCard = getAndRemoveRandomCardFromDeck();
        board.add(turnCard);
        knownGameCards.add(turnCard);
    }

    private void dealRiverCard() {
        riverCard = getAndRemoveRandomCardFromDeck();
        board.add(riverCard);
        knownGameCards.add(riverCard);
    }

    private void resetAllBets() {
        opponentIncrementalBetSize = 0;
        opponentTotalBetSize = 0;
        computerIncrementalBetSize = 0;
        computerTotalBetSize = 0;
    }

    private void resetActions() {
        myAction = null;
        computerAction = null;
    }

    private void roundToTwoDecimals() {
        potSize = Precision.round(potSize, 2);
        myStack = Precision.round(myStack, 2);
        computerStack = Precision.round(computerStack, 2);
        opponentTotalBetSize = Precision.round(opponentTotalBetSize, 2);
        computerTotalBetSize = Precision.round(computerTotalBetSize, 2);
    }

    private String determineWinnerAtShowdown() {
        double computerHandStrength;
        double opponentHandStrength;

        if(computerAction == null) {
            BoardEvaluator endOfHandBoardEvaluator = new BoardEvaluator(board);
            HandEvaluator endOfHandHandEvaluator = new HandEvaluator(endOfHandBoardEvaluator);

            computerHandStrength = endOfHandHandEvaluator.getHandStrength(computerHoleCards);
            opponentHandStrength = endOfHandHandEvaluator.getHandStrength(myHoleCards);
        } else {
            HandEvaluator endOfHandHandEvaluator = computerAction.getHandEvaluator();

            computerHandStrength = endOfHandHandEvaluator.getHandStrength(computerHoleCards);
            opponentHandStrength = endOfHandHandEvaluator.getHandStrength(myHoleCards);
        }

        if(computerHandStrength > opponentHandStrength) {
            return "computer";
        } else if (computerHandStrength == opponentHandStrength) {
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

    private void calculateOpponentPreflopStats() {
        if(!opponentPreflopStatsDoneForHand) {
            if(board == null && computerIsButton && computerWrittenAction.contains("raise") && opponentTotalBetSize == bigBlind) {
                handsHumanOopFacingPreflop2bet++;
                if(myAction.equals("call")) {
                    handsHumanOopCall2bet++;
                }
                if(myAction.equals("raise")) {
                    handsHumanOop3bet++;
                }
            }
            opponentPreCall2betStat = handsHumanOopCall2bet / handsHumanOopFacingPreflop2bet;
            opponentPre3betStat = handsHumanOop3bet / handsHumanOopFacingPreflop2bet;
            opponentPreflopStatsDoneForHand = true;
        }
    }

    private boolean isPreflopCheck() {
        if(board == null) {
            return true;
        }
        return false;
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

    @Override
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

    @Override
    public double getBigBlind() {
        return bigBlind;
    }

    public void setBigBlind(double bigBlind) {
        this.bigBlind = bigBlind;
    }

    @Override
    public double getOpponentStack() {
        return getMyStack();
    }

    public double getMyStack() {
        return myStack;
    }

    public void setMyStack(double myStack) {
        this.myStack = myStack;
    }

    @Override
    public double getBotStack() {
        return getComputerStack();
    }

    public double getComputerStack() {
        return computerStack;
    }

    public void setComputerStack(double computerStack) {
        this.computerStack = computerStack;
    }

    @Override
    public double getOpponentTotalBetSize() {
        return opponentTotalBetSize;
    }

    public void setOpponentTotalBetSize(double opponentTotalBetSize) {
        this.opponentTotalBetSize = opponentTotalBetSize;
    }

    @Override
    public double getBotTotalBetSize() {
        return getComputerTotalBetSize();
    }

    public double getComputerTotalBetSize() {
        return computerTotalBetSize;
    }

    public void setComputerTotalBetSize(double computerTotalBetSize) {
        this.computerTotalBetSize = computerTotalBetSize;
    }

    @Override
    public double getPotSize() {
        return potSize;
    }

    public void setPotSize(double potSize) {
        this.potSize = potSize;
    }

    public Action getComputerAction() {
        return computerAction;
    }

    public void setComputerAction(Action computerAction) {
        this.computerAction = computerAction;
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

    @Override
    public List<Card> getFlopCards() {
        return flopCards;
    }

    public Card getTurnCard() {
        return turnCard;
    }

    public Card getRiverCard() {
        return riverCard;
    }

    @Override
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

    public double getHandsHumanOopFacingPreflop2bet() {
        return handsHumanOopFacingPreflop2bet;
    }

    public void setHandsHumanOopFacingPreflop2bet(double handsHumanOopFacingPreflop2bet) {
        this.handsHumanOopFacingPreflop2bet = handsHumanOopFacingPreflop2bet;
    }

    public double getHandsHumanOopCall2bet() {
        return handsHumanOopCall2bet;
    }

    public void setHandsHumanOopCall2bet(double handsHumanOopCall2bet) {
        this.handsHumanOopCall2bet = handsHumanOopCall2bet;
    }

    public boolean isOpponentPreflopStatsDoneForHand() {
        return opponentPreflopStatsDoneForHand;
    }

    public void setOpponentPreflopStatsDoneForHand(boolean opponentPreflopStatsDoneForHand) {
        this.opponentPreflopStatsDoneForHand = opponentPreflopStatsDoneForHand;
    }

    public double getHandsHumanOop3bet() {
        return handsHumanOop3bet;
    }

    public void setHandsHumanOop3bet(double handsHumanOop3bet) {
        this.handsHumanOop3bet = handsHumanOop3bet;
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

    public List<String> getBotActionHistory() {
        return botActionHistory;
    }

    public void setBotActionHistory(List<String> botActionHistory) {
        this.botActionHistory = botActionHistory;
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

    @Override
    public String getFloatAction() {
        return floatAction;
    }

    @Override
    public void setFloatAction(String floatAction) {
        this.floatAction = floatAction;
    }

    @Override
    public boolean isBotIsPre3bettor() {
        return botIsPre3bettor;
    }

    @Override
    public void setBotIsPre3bettor(boolean botIsPre3bettor) {
        this.botIsPre3bettor = botIsPre3bettor;
    }

    @Override
    public boolean isOpponentBetsOrRaisesPostFlop() {
        return opponentBetsOrRaisesPostFlop;
    }

    public void setOpponentBetsOrRaisesPostFlop(boolean opponentBetsOrRaisesPostFlop) {
        this.opponentBetsOrRaisesPostFlop = opponentBetsOrRaisesPostFlop;
    }
}
