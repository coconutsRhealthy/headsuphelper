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

    @RequestMapping(value = "/startGame", method = RequestMethod.GET)
    public @ResponseBody ComputerGame startGame() {
        return new ComputerGame("initialize");
    }

    @RequestMapping(value = "/submitMyAction", method = RequestMethod.POST)
    public @ResponseBody ComputerGame submitMyAction(@RequestBody ComputerGame computerGame) {
        return computerGame;
    }


    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

