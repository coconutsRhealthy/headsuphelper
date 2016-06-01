package com.lennart.controller;

import com.lennart.model.Employee;
import com.lennart.model.SjaakClass;
import com.lennart.model.TestClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
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
    public @ResponseBody List<SjaakClass> addRailwayStation(@RequestBody List<SjaakClass> testList) {

        System.out.println(testList.get(0).getName());
        System.out.println(testList.get(0).getAchternaam());
        System.out.println(testList.get(0).getAge());
        System.out.println(testList.get(1).getName());
        System.out.println(testList.get(1).getAchternaam());
        System.out.println(testList.get(1).getAge());
        return testList;
    }

    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

