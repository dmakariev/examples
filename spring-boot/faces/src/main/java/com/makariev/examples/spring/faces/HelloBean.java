package com.makariev.examples.spring.faces;

import org.springframework.stereotype.Component;

/**
 *
 * @author dmakariev
 */
@Component("helloBean")
public class HelloBean {

    private String name;
    private String message;

    public String getHello() {
        return "Hello world, from JSF!";
    }

    public void createMessage() {
        message = "Hello, " + name + "!";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

}
