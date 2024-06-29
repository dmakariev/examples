//usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 22
//DEPS org.springframework.boot:spring-boot-dependencies:3.3.0@pom
//DEPS org.springframework.boot:spring-boot-starter-web
//DEPS org.springframework.boot:spring-boot-starter-data-jpa
//DEPS org.springframework.boot:spring-boot-starter-actuator
//DEPS org.springframework.session:spring-session-jdbc
//DEPS com.h2database:h2
//DEPS org.postgresql:postgresql
//DEPS org.projectlombok:lombok
//DEPS org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0
//DEPS org.slf4j:slf4j-simple

//JAVA_OPTIONS -Dserver.port=8080
//JAVA_OPTIONS -Dspring.datasource.url=jdbc:h2:mem:person-db;MODE=PostgreSQL;
//JAVA_OPTIONS -Dspring.h2.console.enabled=true -Dspring.h2.console.settings.web-allow-others=true
//JAVA_OPTIONS -Dmanagement.endpoints.web.exposure.include=health,env,loggers

//JAVA_OPTIONS -Dspring.session.store-type=jdbc
//JAVA_OPTIONS -Dspring.session.jdbc.initialize-schema=always

//FILES META-INF/resources/index.html=index.html

//REPOS mavencentral,sb_snapshot=https://repo.spring.io/snapshot,sb_milestone=https://repo.spring.io/milestone


//SOURCES Person.java
//SOURCES PersonRepository.java

//SOURCES HtmxPersonController.java
//SOURCES VuePersonController.java
//SOURCES FacesPersonBean.java

package com.makariev.examples.jbang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@SpringBootApplication
@ComponentScan(basePackages = "com.makariev.examples.jbang")
public class springbootCompare {

    public static void main(String[] args) {
        SpringApplication.run(springbootCompare.class, args);
    }
    
}

@Component
@RequiredArgsConstructor
class InitialRecords {

    private final PersonRepository personRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void exercise() {

        if (personRepository.count() > 0) {
            return;
        }
        List.of(
                new Person(1L, "Ada", "Lovelace", 1815),
                new Person(2L, "Niklaus", "Wirth", 1934),
                new Person(3L, "Donald", "Knuth", 1938),
                new Person(4L, "Edsger", "Dijkstra", 1930),
                new Person(5L, "Grace", "Hopper", 1906),
                new Person(6L, "John", "Backus", 1924)
        ).forEach(personRepository::save);
    }
}




