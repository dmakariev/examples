package com.makariev.examples.spring.bookstore;

import com.fasterxml.jackson.databind.JsonSerializer;
import io.swagger.v3.core.jackson.mixin.Schema31Mixin;
import io.swagger.v3.oas.models.media.JsonSchema;
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
@RegisterReflectionForBinding({
    JoinedSubclassEntityPersister.class,
    Schema31Mixin.class,
    Schema31Mixin.TypeSerializer.class,
    JsonSerializer.class,
    JsonSchema.class
})
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
