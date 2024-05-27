package com.makariev.examples.spring.bookstore;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

/**
 *
 * @author dmakariev
 */
@SpringBootApplication
@RegisterReflectionForBinding({JoinedSubclassEntityPersister.class})
public class BookstoreApplication {

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

    @PostConstruct
    public void printConfigProperties() {
        final List<String> propertyKeys = Arrays.asList(
                "management.endpoints.web.exposure.include",
                "management.endpoint.env.show-values",
                "management.tracing.sampling.probability",
                "management.tracing.enabled",
                "management.zipkin.tracing.endpoint",
                "spring.datasource.url",
                "spring.h2.console.enabled",
                "spring.h2.console.path",
                "spring.h2.console.settings.web-allow-others"
        );

        for (String key : propertyKeys) {
            System.out.println(key + " = '" + env.getProperty(key) + "'");
        }
    }

}
