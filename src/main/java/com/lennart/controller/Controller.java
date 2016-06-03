package com.lennart.controller;

import com.lennart.model.Card;
import com.lennart.model.SjaakClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Configuration
@EnableAutoConfiguration
@RestController
public class Controller {

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody List<SjaakClass> addRailwayStation(@RequestBody List<SjaakClass> testList) {

        System.out.println(testList.get(0).getName());
        System.out.println(testList.get(0).getAchternaam());
        System.out.println(testList.get(0).getAge());
        System.out.println(testList.get(1).getName());
        System.out.println(testList.get(1).getAchternaam());
        System.out.println(testList.get(1).getAge());
        return testList;
    }

    @RequestMapping(value = "/bertus", method = RequestMethod.POST)
    public @ResponseBody Card testje(@RequestBody Card card) {

        System.out.println(card.getSuit());
        System.out.println(card.getRank());

        return card;
    }



    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

