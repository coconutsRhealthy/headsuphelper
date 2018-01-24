package com.lennart.model.computergame;

import com.lennart.model.action.actionbuilders.ai.Poker;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.LooseAggressive;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.LoosePassive;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.TightAggressive;
import com.lennart.model.action.actionbuilders.ai.opponenttypes.TightPassive;
import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.card.Card;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.handevaluation.PreflopHandStength;
import org.apache.commons.math3.util.Precision;

import java.util.*;

/**
 * Created by lennart on 11-12-16.
 */
public class ComputerGameNew {

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
    private String myAction;
    private String mySize;
    private List<Card> board;
    private String computerWrittenAction;
    private String handWinner;
    private int numberOfHandsPlayed;

    ////
    private double computerHandStrength;
    private boolean computerHasStrongDraw;

    private double totalHumanScore = 0;
    private double totalBotScore = 0;

    public ComputerGameNew() {
        //default constructor
    }

    public ComputerGameNew(String initialize) {
        numberOfHandsPlayed = 0;
        getNewCardDeck();
        dealHoleCards();
        decideWhoIsButton();
        myStack = 50;
        computerStack = 50;
        setBlinds();
        postBlinds();

        calculateHandStrengthsAndDraws();

        if(isComputerIsButton()) {
            doComputerAction();

            if(computerWrittenAction.contains("fold")) {
                updatePotSize("computer fold");
                resetAllBets();
            }
        }
    }

    public ComputerGameNew submitHumanActionAndDoComputerAction() {
        boolean computerActionNeeded = isComputerActionNeeded();

        if(board == null) {
            calculateHandStrengthsAndDraws();
        }

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
        roundToTwoDecimals();
    }

    private String getComputerActionFromAiBot() {
        List<String> eligibleActions = getEligibleComputerActions();
        double handStrength = computerHandStrength;
        boolean strongDraw = computerHasStrongDraw;
        boolean position = computerIsButton;
        double potSizeInMethodBb = potSize / bigBlind;
        double computerBetSizeBb = computerTotalBetSize / bigBlind;
        double opponentBetSizeBb = opponentTotalBetSize / bigBlind;
        double effectiveStack = getEffectiveStackInBb();

//        String action = new TightAggressive(potSizeInMethodBb, computerStack / bigBlind).doAction(
//                myAction, computerHandStrength, computerHasStrongDraw, opponentBetSizeBb, computerBetSizeBb,
//                (myStack / bigBlind), (computerStack / bigBlind), computerIsButton, board == null);

        String action = new TightPassive().doAction(
                myAction, computerHandStrength, computerHasStrongDraw, opponentBetSizeBb, computerBetSizeBb,
                (myStack / bigBlind), (computerStack / bigBlind), computerIsButton, board == null);

//        String action = new Poker().getAction(eligibleActions, handStrength, strongDraw, position, potSizeInMethodBb, computerBetSizeBb,
//                opponentBetSizeBb, effectiveStack, "BoardTextureMedium");

        return action;
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
        double sizing = 0;

        if(computerWrittenAction.contains("bet")) {
            double sizingInitial = 0.75 * potSize;

            if(sizingInitial <= myStack && sizingInitial <= computerStack) {
                sizing = sizingInitial;
            } else {
                if(myStack > computerStack) {
                    sizing = computerStack;
                } else {
                    sizing = myStack;
                }
            }
        } else if(computerWrittenAction.contains("raise")) {
            sizing = calculateRaiseAmountNewAi(computerTotalBetSize, opponentTotalBetSize, potSize, myStack, computerStack);
        }

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
        computerIncrementalBetSize = getComputerSizing() - computerTotalBetSize;
        computerStack = computerStack - computerIncrementalBetSize;
        computerTotalBetSize = getComputerSizing();
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

        myHoleCards.add(getAndRemoveRandomCardFromDeck());
        myHoleCards.add(getAndRemoveRandomCardFromDeck());
        computerHoleCards.add(getAndRemoveRandomCardFromDeck());
        computerHoleCards.add(getAndRemoveRandomCardFromDeck());

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
        totalHumanScore = totalHumanScore + (myStack - 50);
        totalBotScore = totalBotScore + (computerStack - 50);

        Precision.round(totalHumanScore, 2);
        Precision.round(totalBotScore, 2);

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
        computerWrittenAction = null;

        myAction = null;
    }

    public ComputerGameNew proceedToNextHand() {
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
    }

    private void dealTurnCard() {
        turnCard = getAndRemoveRandomCardFromDeck();
        board.add(turnCard);
    }

    private void dealRiverCard() {
        riverCard = getAndRemoveRandomCardFromDeck();
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

    private void calculateHandStrengthsAndDraws() {
        if(board == null) {
            computerHandStrength = new PreflopHandStength().getPreflopHandStength(computerHoleCards);
            computerHasStrongDraw = false;
        } else {
            BoardEvaluator boardEvaluator = new BoardEvaluator(board);
            HandEvaluator handEvaluator = new HandEvaluator(computerHoleCards, boardEvaluator);

            computerHandStrength = handEvaluator.getHandStrength(computerHoleCards);
            computerHasStrongDraw = hasStrongDraw(handEvaluator);
        }
    }

    private boolean hasStrongDraw(HandEvaluator handEvaluator) {
        return handEvaluator.hasDrawOfType("strongFlushDraw") || handEvaluator.hasDrawOfType("strongOosd")
                || handEvaluator.hasDrawOfType("strongGutshot");
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
}
