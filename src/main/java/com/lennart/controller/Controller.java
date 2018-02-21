package com.lennart.controller;

import com.lennart.model.action.actionbuilders.ai.Poker;
import com.lennart.model.action.actionbuilders.ai.SimulatedHand;
import com.lennart.model.botgame.BotTable;
import com.lennart.model.computergame.ComputerGameNew;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;


@Configuration
@EnableAutoConfiguration
@RestController
public class Controller extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Controller.class);
    }

    @RequestMapping(value = "/startGame", method = RequestMethod.GET)
    public @ResponseBody
    ComputerGameNew startGame() {
        ComputerGameNew computerGameNew = new ComputerGameNew("initialize");
        //computerGame.setComputerAction(null);
        return computerGameNew;
    }

    @RequestMapping(value = "/submitMyAction", method = RequestMethod.POST)
    public @ResponseBody ComputerGameNew submitMyAction(@RequestBody ComputerGameNew computerGame) {
        computerGame = computerGame.submitHumanActionAndDoComputerAction();
        //computerGame.setComputerAction(null);
        return computerGame;
    }

    @RequestMapping(value = "/proceedToNextHand", method = RequestMethod.POST)
    public @ResponseBody ComputerGameNew proceedToNextHand(@RequestBody ComputerGameNew computerGame) {
        computerGame = computerGame.proceedToNextHand();
        //computerGame.setComputerAction(null);
        return computerGame;
    }

    @RequestMapping(value = "/fillDbs", method = RequestMethod.GET)
    private void fillDbs() throws Exception {
        Poker poker = new Poker();
        List<String> routes = poker.getAllRoutes();
        poker.storeRoutesInDb(routes);
    }

    @RequestMapping(value = "/startHandSimulation", method = RequestMethod.GET)
    private void startHandSimulation() throws Exception {
        for(int i = 0; i < 4_000_000; i++) {
            Random rn = new Random();
            int y = rn.nextInt(2 - 1 + 1) + 1;

            SimulatedHand simulatedHand = new SimulatedHand(y);
            Map<String, Double> scores = simulatedHand.playHand();
            simulatedHand.updatePayoff(scores.get("aiBot"));
        }
    }

    //Botgame:
    @RequestMapping(value = "/startBotTable", method = RequestMethod.GET)
    public @ResponseBody BotTable startBotTable() {
        BotTable botTable = new BotTable("initialize");
        return botTable;
    }

    @RequestMapping(value = "/getNewBotActionInBotTable", method = RequestMethod.POST)
    public @ResponseBody BotTable getNewBotAction(@RequestBody BotTable botTable) {
        botTable.getNewBotActionInBotTable();
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

