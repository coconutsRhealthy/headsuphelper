package com.lennart.controller;

import com.lennart.model.botgame.BotTable;
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
    @RequestMapping(value = "/startBotTable", method = RequestMethod.GET)
    public @ResponseBody BotTable startBotTable() {
        BotTable botTable = new BotTable("initialize");
        botTable.getBotHand().setBotAction(null);
        botTable.getBotHand().setGameVariablesFiller(null);
        return botTable;
    }

    @RequestMapping(value = "/getNewBotActionInBotTable", method = RequestMethod.POST)
    public @ResponseBody BotTable getNewBotAction(@RequestBody BotTable botTable) {
        botTable.getNewBotActionInBotTable();
        botTable.getBotHand().setBotAction(null);
        botTable.getBotHand().setGameVariablesFiller(null);
        return botTable;
    }

    @RequestMapping(value = "/runBotTableContinuously", method = RequestMethod.GET)
    public void runBotTableContinuously() {
        new BotTable(true);
    }


    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

