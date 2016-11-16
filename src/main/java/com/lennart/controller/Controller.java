package com.lennart.controller;

import com.lennart.model.boardevaluation.*;
import com.lennart.model.boardevaluation.draws.StraightDrawEvaluator;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.pokergame.Card;
import com.lennart.model.pokergame.GameCards;
import com.lennart.model.rangebuilder.RangeBuilder;
import com.lennart.model.rangebuilder.postflop.FlopRangeBuilder;
import com.lennart.model.rangebuilder.postflop.TurnRangeBuilder;
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
    //private Card turnCard = new Card();
    //private Card riverCard = new Card();
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
        cardList.clear();
        RangeBuilder rangeBuilder = new RangeBuilder();

        Card holeCard1 = rangeBuilder.getRandomCardForTest();
        Card holeCard2 = rangeBuilder.getRandomCardForTest();

        cardList.add(holeCard1);
        cardList.add(holeCard2);

        boardEvaluator.resetSortedCombos();
        if (holeCards.size() > 0) {
            holeCards.clear();
        }
        holeCards.add(cardList.get(0));
        holeCards.add(cardList.get(1));

        GameCards.setHoleCards(cardList);
        GameCards.setKnownGameCards(cardList);

        return holeCards;
    }

    @RequestMapping(value = "/postFlopCards", method = RequestMethod.POST)
    public @ResponseBody List<Card> postFlopCards(@RequestBody List<Card> cardList) {
        cardList.clear();
        RangeBuilder rangeBuilder = new RangeBuilder();

        Card flopCard1 = rangeBuilder.getRandomCardForTest();
        Card flopCard2 = rangeBuilder.getRandomCardForTest();
        Card flopCard3 = rangeBuilder.getRandomCardForTest();

        cardList.add(flopCard1);
        cardList.add(flopCard2);
        cardList.add(flopCard3);

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
        GameCards.setBoardCards(cardList);
        GameCards.setKnownGameCards(cardList);

        return flopCards;
    }

    @RequestMapping(value = "/postTurnCard", method = RequestMethod.POST)
    public @ResponseBody Card postTurnCard(@RequestBody Card card) {
        RangeBuilder rangeBuilder = new RangeBuilder();

        Card turnCard = rangeBuilder.getRandomCardForTest();

        card = turnCard;

        boardEvaluator.resetSortedCombos();
        //turnCard = card;
        board.add(turnCard);

        GameCards.setTurnCard(card);
        GameCards.setBoardCards(card);
        GameCards.setKnownGameCards(card);

        return board.get(board.size()-1);
    }

    @RequestMapping(value = "/postRiverCard", method = RequestMethod.POST)
    public @ResponseBody Card postRiverCard(@RequestBody Card card) {
        boardEvaluator.resetSortedCombos();
        //riverCard = card;
        //board.add(riverCard);

        GameCards.setRiverCard(card);
        GameCards.setKnownGameCards(card);

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
        TurnRangeBuilder turnRangeBuilder = new TurnRangeBuilder();
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
//        new HandEvaluator().getHandStrength(holeCards, board);
//        turnRangeBuilder.getCall2betCall1betCall1bet(board, holeCards);
        //flopRangeBuilder.get2bet1bet(board, holeCards);
        //flopRangeBuilder.getCall2betF1bet(board, holeCards);
        //flopRangeBuilder.getCall2betCheck(board, holeCards);
//        flopRangeBuilder.getCall2bet2bet(board, holeCards);
//        flopRangeBuilder.get3betCheck(board, holeCards);
//        flopRangeBuilder.getCall3bet1bet(board, holeCards);
//        flopRangeBuilder.getCall2betCheck(board, holeCards);
//        flopRangeBuilder.getCall3betCheck(board, holeCards);
//        flopRangeBuilder.getCall2betCheck(board, holeCards);
//        flopRangeBuilder.getCall2betCall1bet(board, holeCards);
//        flopRangeBuilder.getCall3betCall1bet(board, holeCards);
//        flopRangeBuilder.get2betCall2bet(board, holeCards);
//        flopRangeBuilder.get2bet1bet(board, holeCards);
//        flopRangeBuilder.get3bet1bet(board, holeCards);
//        flopRangeBuilder.getCall3betF1bet(board, holeCards);
//        new FlopRangeBuilder().get2betF2bet(board, holeCards);
//        new HandEvaluator().getHandStrength(holeCards, board);
//        new HighCardDrawEvaluator().getMediumTwoOvercards(board);

//        straightDrawEvaluator.getStrongOosdCombos(board);

//        double handStrength = new HandEvaluator().getHandStrength(holeCards, board);
//        System.out.println(handStrength);

        Map<Integer, List<Integer>> temp = new HashMap<>();
        return temp;
//        return straightEvaluator.getCombosThatGiveBackDoorStraightDraw(board);
    }

    public static void main(String[] args) {
        //SpringApplication.run(Controller.class, args);
        Controller controller = new Controller();
        FlopRangeBuilder flopRangeBuilder = new FlopRangeBuilder();
        //TurnRangeBuilder turnRangeBuilder = new TurnRangeBuilder();
        List<Double> percentages = new ArrayList<>();

        for(int i = 0; i < 100; i++) {
            controller.postHoleCards(new ArrayList<>());
            controller.postFlopCards(new ArrayList<>());
            controller.postTurnCard(new Card());

            new HandEvaluator().getHandStrength(GameCards.getHoleCards(), GameCards.getBoardCards());

            double percentage = flopRangeBuilder.get2betCall2bet(GameCards.getBoardCards(), GameCards.getHoleCards());
            percentages.add(percentage);

            GameCards.reset();
            RangeBuilder.setCompleteCardDeckForTest();
        }

        double sum = 0;

        for(Double d : percentages) {
            sum = sum + d;
        }

        double average = sum / percentages.size();

        System.out.println(average);
    }
}

