package com.lennart.model.pokergame;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import org.apache.commons.math3.util.Precision;

import java.util.*;

/**
 * Created by lennart on 11-12-16.
 */
public class ComputerGame {

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
    private double myIncrementalBetSize;
    private double myTotalBetSize;
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
    private Set<Set<Card>> opponentRange;
    private List<String> actionHistory;
    private String handWinner;
    private int numberOfHandsPlayed;
    private double handsHumanOopFacingPreflop2bet;
    private double handsHumanOopCall2bet;
    private double handsHumanOop3bet;
    private double opponentPreCall2betStat;
    private double opponentPre3betStat;
    private boolean opponentPreflopStatsDoneForHand;
    private boolean onlyCallRangeNeeded;
    private boolean opponentLastActionWasPreflop;
    private double opponentFormerTotalCallAmount;

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
        opponentLastActionWasPreflop = true;

        if(isComputerIsButton()) {
            doComputerAction();

            if(computerWrittenAction.contains("fold")) {
                updatePotSize("computer fold");
                resetAllBets();
            }
        }
    }

    public ComputerGame submitHumanActionAndDoComputerAction() {
        if(board == null) {
            opponentLastActionWasPreflop = true;
        } else {
            opponentLastActionWasPreflop = false;
        }

        updateActionHistory(myAction);
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

        if(computerActionNeeded || onlyCallRangeNeeded) {
            doComputerAction();
        }
        roundToTwoDecimals();
        resetSpecificStreetVariables();
        return this;
    }

    private void doComputerAction() {
        computerAction = new Action(this);

        if(!onlyCallRangeNeeded) {
            computerWrittenAction = computerAction.getWrittenAction();
            updateActionHistory(computerWrittenAction);

            if(computerWrittenAction.contains("fold")) {
                processComputerFoldAction();
            } else if(computerWrittenAction.contains("check")) {
                processComputerCheckAction();
            } else if(computerWrittenAction.contains("call")) {
                if(!onlyCallRangeNeeded) {
                    processComputerCallAction();
                }
            } else if(computerWrittenAction.contains("bet")) {
                processComputerBetAction();
            } else if(computerWrittenAction.contains("raise")) {
                processComputerRaiseAction();
            }
            roundToTwoDecimals();
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
        }
    }

    private void processComputerCallAction() {
        if(computerStack - (myTotalBetSize - computerTotalBetSize) > 0) {
            computerStack = computerStack - (myTotalBetSize - computerTotalBetSize);
            computerTotalBetSize = myTotalBetSize;
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
        if(myStack - (computerTotalBetSize - myIncrementalBetSize) > 0) {
            myStack = myStack - (computerTotalBetSize - myIncrementalBetSize);
            myTotalBetSize = computerTotalBetSize;
        } else {
            myTotalBetSize = myStack;
            myStack = 0;
        }

        opponentFormerTotalCallAmount = myTotalBetSize;
        updatePotSize("call");
        resetAllBets();

        if(board == null || (board.size() < 5)) {
            resetActions();
            proceedToNextStreet();
        } else if(board.size() == 5) {
            printWinnerAndHand();
        }
    }

    private void processHumanBetOrRaiseAction() {
        myIncrementalBetSize = myTotalBetSize - myIncrementalBetSize;
        myStack = myStack - myIncrementalBetSize;
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
            myIncrementalBetSize = bigBlind;
            myTotalBetSize = bigBlind;
            computerStack = computerStack - smallBlind;
            computerIncrementalBetSize = smallBlind;
            computerTotalBetSize = smallBlind;
        } else {
            myStack = myStack - smallBlind;
            myIncrementalBetSize = smallBlind;
            myTotalBetSize = smallBlind;
            computerStack = computerStack - bigBlind;
            computerIncrementalBetSize = bigBlind;
            computerTotalBetSize = bigBlind;
        }
    }

    public void removeHoleCardsFromKnownGameCards() {
        knownGameCards.removeAll(computerHoleCards);
    }

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
            if(computerIsButton) {
                onlyCallRangeNeeded = true;
            }
        }
        return computerActionNeeded;
    }

    private void returnBetToPlayerAfterFold(String player) {
        if(player.equals("human")) {
            myStack = myStack + myTotalBetSize;
        } else if(player.equals("computer")) {
            computerStack = computerStack + computerTotalBetSize;
        }
    }

    private void updatePotSize(String action) {
        if(action.equals("call")) {
            potSize = potSize + myTotalBetSize + computerTotalBetSize;
        } else if(action.equals("computer fold")) {
            potSize = potSize + computerTotalBetSize;
        } else if(action.equals("human fold")) {
            potSize = potSize + myTotalBetSize;
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
        myIncrementalBetSize = 0;
        myTotalBetSize = 0;
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
        actionHistory = null;
        handWinner = null;
        opponentRange = null;
        computerWrittenAction = null;
        opponentPreflopStatsDoneForHand = false;
        opponentLastActionWasPreflop = true;
    }

    private void updateActionHistory(String action) {
        if(actionHistory == null) {
            actionHistory = new ArrayList<>();
        }
        actionHistory.add(action);
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
        if(flopCards == null) {
            dealFlopCards();
        } else if (turnCard == null) {
            dealTurnCard();
        } else if (riverCard == null) {
            dealRiverCard();
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
        myIncrementalBetSize = 0;
        myTotalBetSize = 0;
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
        myTotalBetSize = Precision.round(myTotalBetSize, 2);
        computerTotalBetSize = Precision.round(computerTotalBetSize, 2);
    }

    private void resetSpecificStreetVariables() {
        if(onlyCallRangeNeeded) {
            onlyCallRangeNeeded = false;
        }
        opponentFormerTotalCallAmount = 0;
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
            HandEvaluator handEvaluator = computerAction.getRangeBuilder().getHandEvaluator();
            computerHandStrength = handEvaluator.getHandStrength(computerHoleCards);
            opponentHandStrength = handEvaluator.getHandStrength(myHoleCards);
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
            if(board == null && computerIsButton && computerWrittenAction.contains("raise") && actionHistory.size() == 1) {
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

    public double getMyStack() {
        return myStack;
    }

    public void setMyStack(double myStack) {
        this.myStack = myStack;
    }

    public double getComputerStack() {
        return computerStack;
    }

    public void setComputerStack(double computerStack) {
        this.computerStack = computerStack;
    }

    public double getMyTotalBetSize() {
        return myTotalBetSize;
    }

    public void setMyTotalBetSize(double myTotalBetSize) {
        this.myTotalBetSize = myTotalBetSize;
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

    public Action getComputerAction() {
        return computerAction;
    }

    public void setComputerAction(Action computerAction) {
        this.computerAction = computerAction;
    }

    public Set<Card> getKnownGameCards() {
        return knownGameCards;
    }

    public void setKnownGameCards(Set<Card> knownGameCards) {
        this.knownGameCards = knownGameCards;
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

    public boolean isComputerIsButton() {
        return computerIsButton;
    }

    public void setComputerIsButton(boolean computerIsButton) {
        this.computerIsButton = computerIsButton;
    }

    public double getMyIncrementalBetSize() {
        return myIncrementalBetSize;
    }

    public void setMyIncrementalBetSize(double myIncrementalBetSize) {
        this.myIncrementalBetSize = myIncrementalBetSize;
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

    public void setFlopCards(List<Card> flopCards) {
        this.flopCards = flopCards;
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

    public String getComputerWrittenAction() {
        return computerWrittenAction;
    }

    public void setComputerWrittenAction(String computerWrittenAction) {
        this.computerWrittenAction = computerWrittenAction;
    }

    public Set<Set<Card>> getOpponentRange() {
        return opponentRange;
    }

    public void setOpponentRange(Set<Set<Card>> opponentRange) {
        this.opponentRange = opponentRange;
    }

    public List<String> getActionHistory() {
        return actionHistory;
    }

    public void setActionHistory(List<String> actionHistory) {
        this.actionHistory = actionHistory;
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

    public double getOpponentPreCall2betStat() {
        return opponentPreCall2betStat;
    }

    public void setOpponentPreCall2betStat(double opponentPreCall2betStat) {
        this.opponentPreCall2betStat = opponentPreCall2betStat;
    }

    public boolean isOpponentPreflopStatsDoneForHand() {
        return opponentPreflopStatsDoneForHand;
    }

    public void setOpponentPreflopStatsDoneForHand(boolean opponentPreflopStatsDoneForHand) {
        this.opponentPreflopStatsDoneForHand = opponentPreflopStatsDoneForHand;
    }

    public boolean isOnlyCallRangeNeeded() {
        return onlyCallRangeNeeded;
    }

    public void setOnlyCallRangeNeeded(boolean onlyCallRangeNeeded) {
        this.onlyCallRangeNeeded = onlyCallRangeNeeded;
    }

    public boolean isOpponentLastActionWasPreflop() {
        return opponentLastActionWasPreflop;
    }

    public void setOpponentLastActionWasPreflop(boolean opponentLastActionWasPreflop) {
        this.opponentLastActionWasPreflop = opponentLastActionWasPreflop;
    }

    public double getOpponentFormerTotalCallAmount() {
        return opponentFormerTotalCallAmount;
    }

    public void setOpponentFormerTotalCallAmount(double opponentFormerTotalCallAmount) {
        this.opponentFormerTotalCallAmount = opponentFormerTotalCallAmount;
    }

    public double getHandsHumanOop3bet() {
        return handsHumanOop3bet;
    }

    public void setHandsHumanOop3bet(double handsHumanOop3bet) {
        this.handsHumanOop3bet = handsHumanOop3bet;
    }

    public double getOpponentPre3betStat() {
        return opponentPre3betStat;
    }

    public void setOpponentPre3betStat(double opponentPre3betStat) {
        this.opponentPre3betStat = opponentPre3betStat;
    }
}
