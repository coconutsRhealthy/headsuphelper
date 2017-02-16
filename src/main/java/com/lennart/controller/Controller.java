package com.lennart.controller;

import com.lennart.model.boardevaluation.BoardEvaluator;
import com.lennart.model.computergame.ComputerGame;
import com.lennart.model.handevaluation.HandEvaluator;
import com.lennart.model.card.*;
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

    @RequestMapping(value = "/startGame", method = RequestMethod.GET)
    public @ResponseBody
    ComputerGame startGame() {
        ComputerGame computerGame = new ComputerGame("initialize");
        computerGame.setComputerAction(null);
        return computerGame;
    }

    @RequestMapping(value = "/submitMyAction", method = RequestMethod.POST)
    public @ResponseBody ComputerGame submitMyAction(@RequestBody ComputerGame computerGame) {
        computerGame = computerGame.submitHumanActionAndDoComputerAction();
        computerGame.setComputerAction(null);
        return computerGame;
    }

    @RequestMapping(value = "/proceedToNextHand", method = RequestMethod.POST)
    public @ResponseBody ComputerGame proceedToNextHand(@RequestBody ComputerGame computerGame) {
        computerGame = computerGame.proceedToNextHand();
        computerGame.setComputerAction(null);
        return computerGame;
    }


    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }

    public static void methodForTesting() {
        List<Card> board = new ArrayList<>();
        board.add(new Card(9, 'h'));
        board.add(new Card(7, 'c'));
        board.add(new Card(14, 'd'));
        board.add(new Card(9, 's'));
        board.add(new Card(7, 'd'));

        List<Card> humanHand = new ArrayList<>();
        humanHand.add(new Card(12, 's'));
        humanHand.add(new Card(13, 'h'));

        List<Card> computerHand = new ArrayList<>();
        computerHand.add(new Card(11, 'h'));
        computerHand.add(new Card(14, 'c'));

        BoardEvaluator boardEvaluator = new BoardEvaluator(board);

        HandEvaluator handEvaluator = new HandEvaluator(boardEvaluator);

        double humanHandStrength = handEvaluator.getHandStrength(humanHand);
        double computerHandStrength = handEvaluator.getHandStrength(computerHand);

        System.out.println(humanHandStrength);
    }
}

