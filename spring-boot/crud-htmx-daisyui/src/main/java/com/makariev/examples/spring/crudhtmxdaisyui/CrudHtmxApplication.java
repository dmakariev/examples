package com.makariev.examples.spring.crudhtmxdaisyui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication
@ImportRuntimeHints(CrudHtmxRuntimeHints.class)
public class CrudHtmxApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudHtmxApplication.class, args);
	}

}
