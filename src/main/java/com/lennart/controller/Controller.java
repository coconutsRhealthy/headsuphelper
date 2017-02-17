package com.lennart.controller;

import com.lennart.model.botgame.BotGame;
import com.lennart.model.computergame.ComputerGame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;


@Configuration
@EnableAutoConfiguration
@RestController
public class Controller {

    @RequestMapping(value = "/startGame", method = RequestMethod.GET)
    public @ResponseBody ComputerGame startGame() {
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

    //Botgame:
    @RequestMapping(value = "/startBotGame", method = RequestMethod.GET)
    public @ResponseBody BotGame startBotGame() {
        BotGame botGame = new BotGame();
        return botGame;
    }

    @RequestMapping(value = "/getNewBotAction", method = RequestMethod.POST)
    public @ResponseBody BotGame getNewBotAction(@RequestBody BotGame botGame) {
        botGame.getNewBotAction();
        return botGame;
    }


    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

