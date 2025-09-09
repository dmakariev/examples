package com.makariev.examples.spring.bookstore.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author dmakariev
 */
@Configuration
public class SpringdocConfig {

    @Bean
    public OpenAPI springShopOpenAPI(
            @Value("${api.version}") String apiVersion
    ) {
        return new OpenAPI()
                .info(new Info().title("Bookstore API")
                        .description("Spring Bookstore sample application")
                        .version(apiVersion)
                        .license(new License().name("MIT").url("https://makariev.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Bookstore  Documentation")
                        .url("https://makariev.com"));
    }

}
