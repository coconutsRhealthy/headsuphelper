package com.lennart.controller;

import com.lennart.model.boardevaluation.*;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.pokergame.Action;
import com.lennart.model.pokergame.Card;
import com.lennart.model.pokergame.Game;
import com.lennart.model.pokergame.HandPath;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Configuration
@EnableAutoConfiguration
@RestController
public class Controller {

    private List<Card> holeCards = new ArrayList<Card>();
    private List<Card> flopCards = new ArrayList<Card>();
    private Card turnCard = new Card();
    private Card riverCard = new Card();
    private List<Card> board = new ArrayList<Card>();
    private BoardEvaluator boardEvaluator = new BoardEvaluator();
    private StraightEvaluator straightEvaluator = new StraightEvaluator();
    private FlushEvaluator flushEvaluator = new FlushEvaluator();
    private PairEvaluator pairEvaluator = new PairEvaluator();
    private TwoPairEvaluator twoPairEvaluator = new TwoPairEvaluator();
    private ThreeOfAKindEvaluator threeOfAKindEvaluator = new ThreeOfAKindEvaluator();
    private FullHouseEvaluator fullHouseEvaluator = new FullHouseEvaluator();
    private FourOfAKindEvaluator fourOfAKindEvaluator = new FourOfAKindEvaluator();
    private StraightFlushEvaluator straightFlushEvaluator = new StraightFlushEvaluator();
    private HighCardEvaluator highCardEvaluator = new HighCardEvaluator();

    private StraightDrawEvaluator straightDrawEvaluator = new StraightDrawEvaluator();

//    private String handPathPreflop;
//    private String handPathFlop;
//    private String handPathTurn;
//    private String handPathRiver;
//    private String handPath;

    private String myLastAction;
    private double myLastActionSize;
    private String opponentAction;
    private double opponentActionSize;

    @RequestMapping(value = "/postHoleCards", method = RequestMethod.POST)
    public @ResponseBody List<Card> postHoleCards(@RequestBody List<Card> cardList) {
        boardEvaluator.resetSortedCombos();
        if (holeCards.size() > 0) {
            holeCards.clear();
        }
        holeCards.add(cardList.get(0));
        holeCards.add(cardList.get(1));

        Game.setStreet("preflop");
        Game.setHoleCards(cardList);
        Game.setKnownGameCards(cardList);

        return holeCards;
    }

    @RequestMapping(value = "/postFlopCards", method = RequestMethod.POST)
    public @ResponseBody List<Card> postFlopCards(@RequestBody List<Card> cardList) {
        boardEvaluator.resetSortedCombos();
        if (flopCards.size() > 0) {
            flopCards.clear();
        }
        flopCards.add(cardList.get(0));
        flopCards.add(cardList.get(1));
        flopCards.add(cardList.get(2));

        if (board.size() > 0) {
            board.clear();
        }
        board.add(cardList.get(0));
        board.add(cardList.get(1));
        board.add(cardList.get(2));

        Game.setStreet("flop");
        Game.setFlopCards(cardList);
        Game.setBoardCards(cardList);
        Game.setKnownGameCards(cardList);

        return flopCards;
    }

    @RequestMapping(value = "/postTurnCard", method = RequestMethod.POST)
    public @ResponseBody Card postTurnCard(@RequestBody Card card) {
        boardEvaluator.resetSortedCombos();
        turnCard = card;
        board.add(turnCard);

        Game.setStreet("turn");
        Game.setTurnCard(card);
        Game.setBoardCards(card);
        Game.setKnownGameCards(card);

        return board.get(board.size()-1);
    }

    @RequestMapping(value = "/postRiverCard", method = RequestMethod.POST)
    public @ResponseBody Card postRiverCard(@RequestBody Card card) {
        boardEvaluator.resetSortedCombos();
        riverCard = card;
        board.add(riverCard);

        Game.setStreet("river");
        Game.setRiverCard(card);
        Game.setBoardCards(card);
        Game.setKnownGameCards(card);

        return board.get(board.size()-1);
    }

    @RequestMapping(value = "/postInitialGameVariables", method = RequestMethod.POST)
    public @ResponseBody List<String> postInitialGameVariables(@RequestBody List<String> initialGameVariables) {
        Game.resetPot();
        Game.setStakes(initialGameVariables.get(0));
        Game.setMyStack(Double.parseDouble(initialGameVariables.get(1)));
        Game.setOpponentStack(Double.parseDouble(initialGameVariables.get(2)));
        Game.setPosition(initialGameVariables.get(3));

        Game.setBlindsBasedOnStake(Game.getStakes());

        if(Game.getPosition().equals("IP")) {
            Game.setMyTotalBetSize(Game.getSmallBlind());
            Game.setOpponentTotalBetSize(Game.getBigBlind());
            Game.setMyIncrementalBetSize(Game.getSmallBlind());
            Game.setOpponentIncrementalBetsize(Game.getBigBlind());

            Game.setStacksAndPotBasedOnAction(Game.getMyIncrementalBetSize(), Game.getOpponentIncrementalBetsize());
            HandPath.setHandPathPreflop("05betF1bet");

            myLastAction = "0.5bet";
            myLastActionSize = Game.getSmallBlind();
            opponentAction = "1bet";
            opponentActionSize = Game.getBigBlind();
        } else if(Game.getPosition().equals("OOP")) {
            Game.setMyTotalBetSize(Game.getBigBlind());
            Game.setOpponentTotalBetSize(Game.getSmallBlind());
            Game.setMyIncrementalBetSize(Game.getBigBlind());
            Game.setOpponentIncrementalBetsize(Game.getSmallBlind());

            Game.setStacksAndPotBasedOnAction(Game.getMyIncrementalBetSize(), Game.getOpponentIncrementalBetsize());
            HandPath.setHandPathPreflop("1bet");

            myLastAction = "1bet";
            myLastActionSize = Game.getBigBlind();
            opponentAction = "...";
            opponentActionSize = 0;
        }

        List<String> gameState = new ArrayList<>();
        gameState.add(String.valueOf(Game.getMyStack()));
        gameState.add(String.valueOf(Game.getOpponentStack()));
        gameState.add(String.valueOf(Game.getMyTotalBetSize()));
        gameState.add(String.valueOf(Game.getOpponentTotalBetSize()));
        gameState.add(String.valueOf(Game.getPotSize()));
        gameState.add(HandPath.getHandPath());

        gameState.add(myLastAction);
        gameState.add(String.valueOf(myLastActionSize));
        gameState.add(opponentAction);
        gameState.add(String.valueOf(opponentActionSize));

        return gameState;
    }

    @RequestMapping(value = "/getAction", method = RequestMethod.GET)
    public @ResponseBody Action getAction() {
        Action action = new Action(HandPath.getHandPath());
        return action;
    }

    @RequestMapping(value = "/postYourAction", method = RequestMethod.POST)
    public @ResponseBody List<String> postYourAction(@RequestBody List<String> handPathIncrementalBetSizeMoveToNextStreet) {
        switch(Game.getStreet()) {
            case "preflop":
                HandPath.setHandPathPreflop(handPathIncrementalBetSizeMoveToNextStreet.get(0));
                break;
            case "flop":
                HandPath.setHandPathFlop(handPathIncrementalBetSizeMoveToNextStreet.get(0));
                break;
            case "turn":
                HandPath.setHandPathTurn(handPathIncrementalBetSizeMoveToNextStreet.get(0));
                break;
            case "river":
                HandPath.setHandPathRiver(handPathIncrementalBetSizeMoveToNextStreet.get(0));
                break;
        }

        if(handPathIncrementalBetSizeMoveToNextStreet.get(2).equals("true")) {
            Game.proceedToNextStreet();
        }

        Game.setMyIncrementalBetSize(Double.parseDouble(handPathIncrementalBetSizeMoveToNextStreet.get(1)));
        Game.setMyTotalBetSize(Game.getMyTotalBetSize() + Game.getMyIncrementalBetSize());
        Game.setStacksAndPotBasedOnAction(Game.getMyIncrementalBetSize(), 0);

        if(handPathIncrementalBetSizeMoveToNextStreet.get(2).equals("true")) {
            Game.setMyIncrementalBetSize(0);
            Game.setOpponentIncrementalBetsize(0);
            Game.setMyTotalBetSize(0);
            Game.setOpponentTotalBetSize(0);
        }

        List<String> gameState = new ArrayList<>();
        gameState.add(String.valueOf(Game.getMyStack()));
        gameState.add(String.valueOf(Game.getOpponentStack()));
        gameState.add(String.valueOf(Game.getMyTotalBetSize()));
        gameState.add(String.valueOf(Game.getOpponentTotalBetSize()));
        gameState.add(String.valueOf(Game.getPotSize()));
        gameState.add(HandPath.getHandPath());

        return gameState;
    }

    @RequestMapping(value = "/postOpponentAction", method = RequestMethod.POST)
    public @ResponseBody List<String> postOpponentAction(@RequestBody List<String> handPathIncrementalBetSizeMoveToNextStreet) {
        if(handPathIncrementalBetSizeMoveToNextStreet.get(2).equals("true")) {
            Game.proceedToNextStreet();
            switch(Game.getStreet()) {
                case "flop":
                    HandPath.setHandPathPreflop(HandPath.getHandPath());
                    break;
                case "turn":
                    HandPath.setHandPathFlop(HandPath.getHandPath());
                    break;
                case "river":
                    HandPath.setHandPathTurn(HandPath.getHandPath());
                    break;
            }
        } else {
            switch(Game.getStreet()) {
                case "preflop":
                    HandPath.setHandPathPreflop(handPathIncrementalBetSizeMoveToNextStreet.get(0));
                    break;
                case "flop":
                    HandPath.setHandPathFlop(handPathIncrementalBetSizeMoveToNextStreet.get(0));
                    break;
                case "turn":
                    HandPath.setHandPathTurn(handPathIncrementalBetSizeMoveToNextStreet.get(0));
                    break;
                case "river":
                    HandPath.setHandPathRiver(handPathIncrementalBetSizeMoveToNextStreet.get(0));
                    break;
            }
        }

        Game.setOpponentIncrementalBetsize(Double.parseDouble(handPathIncrementalBetSizeMoveToNextStreet.get(1)));
        Game.setOpponentTotalBetSize(Game.getOpponentTotalBetSize() + Game.getOpponentIncrementalBetsize());
        Game.setStacksAndPotBasedOnAction(0, Game.getOpponentIncrementalBetsize());

        if(handPathIncrementalBetSizeMoveToNextStreet.get(2).equals("true")) {
            Game.setMyIncrementalBetSize(0);
            Game.setOpponentIncrementalBetsize(0);
            Game.setMyTotalBetSize(0);
            Game.setOpponentTotalBetSize(0);
        }

        List<String> gameState = new ArrayList<>();
        gameState.add(String.valueOf(Game.getMyStack()));
        gameState.add(String.valueOf(Game.getOpponentStack()));
        gameState.add(String.valueOf(Game.getMyTotalBetSize()));
        gameState.add(String.valueOf(Game.getOpponentTotalBetSize()));
        gameState.add(String.valueOf(Game.getPotSize()));
        gameState.add(HandPath.getHandPath());

        return gameState;
    }

    @RequestMapping(value = "/getHandPath", method = RequestMethod.GET)
    public @ResponseBody HandPath getHandPath() {
        return new HandPath();
    }


    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

