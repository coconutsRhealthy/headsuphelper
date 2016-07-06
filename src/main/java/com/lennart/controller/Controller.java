package com.lennart.controller;

import com.lennart.model.BoardEvaluator;
import com.lennart.model.BooleanResult;
import com.lennart.model.Card;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableAutoConfiguration
@RestController
public class Controller {

    private List<Card> holeCards = new ArrayList<Card>();
    private List<Card> flopCards = new ArrayList<Card>();
    private Card turnCard = new Card();
    private List<Card> board = new ArrayList<Card>();
    private List<Card> allSelectedCards = new ArrayList<Card>();

    @RequestMapping(value = "/postHoleCards", method = RequestMethod.POST)
    public @ResponseBody List<Card> postHoleCards(@RequestBody List<Card> cardList) {
        if (holeCards.size() > 0) {
            holeCards.clear();
        }
        holeCards.add(cardList.get(0));
        holeCards.add(cardList.get(1));

        if (allSelectedCards.size() > 0) {
            allSelectedCards.clear();
        }
        allSelectedCards.add(cardList.get(0));
        allSelectedCards.add(cardList.get(1));

        return allSelectedCards;
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

        allSelectedCards.add(cardList.get(0));
        allSelectedCards.add(cardList.get(1));
        allSelectedCards.add(cardList.get(2));

        return allSelectedCards;
    }

    @RequestMapping(value = "/postTurnCard", method = RequestMethod.POST)
    public @ResponseBody Card postTurnCard(@RequestBody Card card) {
        turnCard = card;

        board.add(turnCard);

        allSelectedCards.add(turnCard);

        return board.get(board.size()-1);
    }




    @RequestMapping(value = "/getFunctionResults", method = RequestMethod.GET)
    public @ResponseBody List<BooleanResult> getFunctionResults() {

        return BoardEvaluator.allFunctions(board);
    }

    @RequestMapping(value = "/getStraightCombos", method = RequestMethod.GET)
    public @ResponseBody List<String> getStraightCombos() {

        return BoardEvaluator.getCombosThatMakeStraight(board);
    }






    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

