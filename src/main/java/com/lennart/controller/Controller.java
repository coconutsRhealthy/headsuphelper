package com.lennart.controller;

import com.lennart.model.boardevaluation.*;
import com.lennart.model.boardevaluation.draws.HighCardDrawEvaluator;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.pokergame.GameCards;
import com.lennart.model.rangebuilder.postflop.FlopRangeBuilder;
import com.lennart.model.rangebuilder.preflop.oop.Call2betRangeBuilder;
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

    @RequestMapping(value = "/postHoleCards", method = RequestMethod.POST)
    public @ResponseBody List<Card> postHoleCards(@RequestBody List<Card> cardList) {
        boardEvaluator.resetSortedCombos();
        if (holeCards.size() > 0) {
            holeCards.clear();
        }
        holeCards.add(cardList.get(0));
        holeCards.add(cardList.get(1));

        GameCards.setHoleCards(cardList);

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

        GameCards.setFlopCards(cardList);

        return flopCards;
    }

    @RequestMapping(value = "/postTurnCard", method = RequestMethod.POST)
    public @ResponseBody Card postTurnCard(@RequestBody Card card) {
        boardEvaluator.resetSortedCombos();
        turnCard = card;
        board.add(turnCard);

        GameCards.setTurnCard(card);

        return board.get(board.size()-1);
    }

    @RequestMapping(value = "/postRiverCard", method = RequestMethod.POST)
    public @ResponseBody Card postRiverCard(@RequestBody Card card) {
        boardEvaluator.resetSortedCombos();
        riverCard = card;
        board.add(riverCard);

        GameCards.setRiverCard(card);

        return board.get(board.size()-1);
    }


    @RequestMapping(value = "/getFunctionResults", method = RequestMethod.GET)
    public @ResponseBody List<BooleanResult> getFunctionResults() {

        return boardEvaluator.allFunctions(board);
    }

    @RequestMapping(value = "/getOosdStraightCombos", method = RequestMethod.GET)
    public @ResponseBody Map<Integer, List<Integer>> getOosdStraightCombos() {
        return straightDrawEvaluator.getCombosThatGiveOosdOrDoubleGutter(board);
    }

    @RequestMapping(value = "/getGutshotStraightCombos", method = RequestMethod.GET)
    public @ResponseBody Map<Integer, List<Integer>> getGutshotStraightCombos() {
        return straightDrawEvaluator.getCombosThatGiveGutshot(board);
    }

    @RequestMapping(value = "/getBackdoorStraightCombos", method = RequestMethod.GET)
    public @ResponseBody Map<Integer, List<Integer>> getBackdoorStraightCombos() {

        FlopRangeBuilder flopRangeBuilder = new FlopRangeBuilder();
//        threeOfAKindEvaluator.getThreeOfAKindCombos(board);
//        twoPairEvaluator.getCombosThatMakeTwoPair(board);
//        pairEvaluator.getCombosThatMakePair(board);
//        flushEvaluator.getFlushDrawCombos(board);
//        flushEvaluator.getFlushCombos(board);
//        //flushEvaluator.getMapOfAllPossibleStartHands();
//        flushEvaluator.getSuitsOfBoard(board);
////        flushEvaluator.getMapOfAllPossibleCombosOfOneSuit('d');
//        flushEvaluator.getFlushCombos(board);

//        fourOfAKindEvaluator.getFourOfAKindCombos(board);
//        straightFlushEvaluator.getStraightFlushCombos(board);

//        straightEvaluator.getMapOfStraightCombos(board);
//        fullHouseEvaluator.getFullHouseCombos(board);
//        highCardEvaluator.getHighCardCombos(board);
//        Map<Integer, Set<Set<Card>>> sortedCombos = boardEvaluator.getSortedCombosInitialize(board);
//        System.out.println(sortedCombos.get(0));

//        straightDrawEvaluator.getWeakGutshotCombosFromAllGutshotCombos(board);

//        new RangeBuilder().getRange("2bet2betFcheck", board, holeCards);
//        new Call2betRangeBuilder().getOpponentCall2betRange();
        new HandEvaluator().getHandStrength(holeCards, board);
        flopRangeBuilder.getCall2betF1bet(holeCards, board);
        new FlopRangeBuilder().getCall3betF1bet(holeCards, board);

        new FlopRangeBuilder().get2betF2bet(board, holeCards);
        new HandEvaluator().getHandStrength(holeCards, board);
        new HighCardDrawEvaluator().getMediumTwoOvercards(board);

//        straightDrawEvaluator.getStrongOosdCombos(board);

//        double handStrength = new HandEvaluator().getHandStrength(holeCards, board);
//        System.out.println(handStrength);

        Map<Integer, List<Integer>> temp = new HashMap<>();
        return temp;
//        return straightEvaluator.getCombosThatGiveBackDoorStraightDraw(board);
    }

    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

