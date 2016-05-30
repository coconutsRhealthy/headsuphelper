package com.lennart.controller;

import com.lennart.model.TestClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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







    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}

