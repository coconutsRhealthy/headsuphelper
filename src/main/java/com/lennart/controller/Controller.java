package com.lennart.controller;

import com.lennart.model.SjaakClass;
import com.lennart.model.TestClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableAutoConfiguration
@ComponentScan("com.lennart")
@RestController
public class Controller {

    @RequestMapping("/resource")
    public TestClass home() {
        System.out.println("test");
        TestClass testClass = new TestClass();

//        Map<String,Object> model = new HashMap<String,Object>();
//        model.put("id", UUID.randomUUID().toString());
//        model.put("content", "Hello World");
//        return model;
        return testClass;
    }


    @RequestMapping("/aappost")
    public void post(@RequestBody TestClass testClass) {
        System.out.println("werktdit");

        String hoihoi = testClass.getfName();

        System.out.println(hoihoi);


        //System.out.println(string);
//        TestClass testClass = new TestClass();
//
////        Map<String,Object> model = new HashMap<String,Object>();
////        model.put("id", UUID.randomUUID().toString());
////        model.put("content", "Hello World");
////        return model;
//        return testClass;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody void addRailwayStation(@RequestBody SjaakClass sjaakClass) {
        System.out.println("hallo");
        System.out.println(sjaakClass.getName());
        System.out.println(sjaakClass.getAchternaam());
        System.out.println(sjaakClass.getAge());

        int eije = sjaakClass.getAge() + 10;

        System.out.println(eije);

//        System.out.println(railwayStation.getName());
//        railwayStationsService.addRailwayStation(railwayStation);
    }








    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

