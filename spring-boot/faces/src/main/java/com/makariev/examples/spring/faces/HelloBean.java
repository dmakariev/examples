package com.makariev.examples.spring.faces;

import org.springframework.stereotype.Component;

/**
 *
 * @author dmakariev
 */
@Component
public class HelloBean {
    
    public String getHello() {
        return "Hello world, from JSF!";
    }
    
}
