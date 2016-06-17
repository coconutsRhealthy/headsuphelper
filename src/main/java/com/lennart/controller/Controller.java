package com.lennart.controller;

import com.lennart.model.Card;
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

    private List<Card> holeCards = new ArrayList<Card>();
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
//        if (allSelectedCards.size() > 0) {
//            allSelectedCards.clear();
//        }

        //System.out.println(cardList.get(0));


        allSelectedCards.add(cardList.get(0));
        allSelectedCards.add(cardList.get(1));
        allSelectedCards.add(cardList.get(2));

        return allSelectedCards;
    }







    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

