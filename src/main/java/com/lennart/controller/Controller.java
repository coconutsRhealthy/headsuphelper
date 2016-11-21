package com.lennart.controller;

import com.lennart.model.boardevaluation.*;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.pokergame.Action;
import com.lennart.model.pokergame.Card;
import com.lennart.model.pokergame.Game;
import com.lennart.model.pokergame.HandPath;
import com.lennart.model.rangebuilder.postflop.FlopRangeBuilder;
import com.lennart.model.rangebuilder.postflop.TurnRangeBuilder;
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

    private String handPath;
    private String facing;

    @RequestMapping(value = "/postHoleCards", method = RequestMethod.POST)
    public @ResponseBody List<Card> postHoleCards(@RequestBody List<Card> cardList) {
        boardEvaluator.resetSortedCombos();
        if (holeCards.size() > 0) {
            holeCards.clear();
        }
        holeCards.add(cardList.get(0));
        holeCards.add(cardList.get(1));

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

        Game.setRiverCard(card);
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
            Game.setMyAdditionToPot(Game.getSmallBlind());
            Game.setOpponentAdditionToPot(Game.getBigBlind());
            Game.setStacksAndPotBasedOnAction(Game.getMyAdditionToPot(), Game.getOpponentAdditionToPot());
            handPath = "05betF1bet";
            facing = "1bet";
        } else if(Game.getPosition().equals("OOP")) {
            Game.setMyAdditionToPot(Game.getBigBlind());
            Game.setOpponentAdditionToPot(Game.getSmallBlind());
            Game.setStacksAndPotBasedOnAction(Game.getMyAdditionToPot(), Game.getOpponentAdditionToPot());
            handPath = "1bet";
            facing = "...";
        }

        List<String> gameState = new ArrayList<>();
        gameState.add(String.valueOf(Game.getMyStack()));
        gameState.add(String.valueOf(Game.getOpponentStack()));
        gameState.add(String.valueOf(Game.getMyAdditionToPot()));
        gameState.add(String.valueOf(Game.getOpponentAdditionToPot()));
        gameState.add(String.valueOf(Game.getPotSize()));
        gameState.add(handPath);
        gameState.add(facing);

        return gameState;
    }

    @RequestMapping(value = "/getAction", method = RequestMethod.GET)
    public @ResponseBody Action getAction() {
        Action action = new Action(handPath);
        return action;
    }

    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

