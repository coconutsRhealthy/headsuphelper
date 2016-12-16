package com.lennart.controller;

import com.lennart.model.pokergame.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

@Configuration
@EnableAutoConfiguration
@RestController
public class Controller {

    private ComputerGame computerGame;

    @RequestMapping(value = "/startGame", method = RequestMethod.GET)
    public @ResponseBody ComputerGame startGame() {
        computerGame = new ComputerGame();

        return computerGame;
    }

    @RequestMapping(value = "/submitMyAction", method = RequestMethod.GET)
    public @ResponseBody ComputerGame submitMyAction() {
        System.out.println("wacht");
        return computerGame;
    }

    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

