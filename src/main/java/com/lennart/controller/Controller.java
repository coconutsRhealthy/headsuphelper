package com.lennart.controller;

import com.lennart.model.BoardEvaluator;
import com.lennart.model.BooleanResult;
import com.lennart.model.Card;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAutoConfiguration
@RestController
public class Controller {

    private List<Card> holeCards = new ArrayList<Card>();
    private List<Card> flopCards = new ArrayList<Card>();
    private Card turnCard = new Card();
    private Card riverCard = new Card();
    private List<Card> board = new ArrayList<Card>();

    @RequestMapping(value = "/postHoleCards", method = RequestMethod.POST)
    public @ResponseBody List<Card> postHoleCards(@RequestBody List<Card> cardList) {
        if (holeCards.size() > 0) {
            holeCards.clear();
        }
        holeCards.add(cardList.get(0));
        holeCards.add(cardList.get(1));

        return holeCards;
    }

    @RequestMapping(value = "/postFlopCards", method = RequestMethod.POST)
    public @ResponseBody List<Card> postFlopCards(@RequestBody List<Card> cardList) {
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

        return flopCards;
    }

    @RequestMapping(value = "/postTurnCard", method = RequestMethod.POST)
    public @ResponseBody Card postTurnCard(@RequestBody Card card) {
        turnCard = card;
        board.add(turnCard);
        return board.get(board.size()-1);
    }

    @RequestMapping(value = "/postRiverCard", method = RequestMethod.POST)
    public @ResponseBody Card postRiverCard(@RequestBody Card card) {
        riverCard = card;
        board.add(riverCard);
        return board.get(board.size()-1);
    }


    @RequestMapping(value = "/getFunctionResults", method = RequestMethod.GET)
    public @ResponseBody List<BooleanResult> getFunctionResults() {

        return BoardEvaluator.allFunctions(board);
    }

    @RequestMapping(value = "/getStraightCombos", method = RequestMethod.GET)
    public @ResponseBody List<List<Integer>> getStraightCombos() {

        BoardEvaluator.getCombosThatMakeWheelStraight(board);
//        return BoardEvaluator.getCombosThatMakeStraight(board);
        return BoardEvaluator.getCombosThatGiveOOSD(board);
    }




    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

