package com.lennart.controller;

import com.lennart.model.Card;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@RestController
public class Controller {

    @RequestMapping(value = "/bertus", method = RequestMethod.POST)
    public @ResponseBody List<Card> testje(@RequestBody List<Card> cardList) {

        System.out.println(cardList.get(0).getSuit());
        System.out.println(cardList.get(0).getRank());

        System.out.println(cardList.get(1).getSuit());
        System.out.println(cardList.get(1).getRank());

        return cardList;
    }

    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

