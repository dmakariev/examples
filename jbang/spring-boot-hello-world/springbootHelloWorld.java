//usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21
//DEPS org.springframework.boot:spring-boot-starter-web:3.1.4

package com.makariev.examples.jbang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
@RestController
public class springbootHelloWorld {

    public static void main(String[] args) {
        SpringApplication.run(springbootHelloWorld.class, args);
    }

    @GetMapping("/")
    public String sayHi(@RequestParam(required = false, defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }
}
