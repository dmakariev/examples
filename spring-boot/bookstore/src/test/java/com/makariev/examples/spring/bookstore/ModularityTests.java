package com.makariev.examples.spring.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 *
 * @author dmakariev
 */
public class ModularityTests {

    ApplicationModules modules = ApplicationModules.of(BookstoreApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        new Documenter(modules).writeDocumentation();
    }

}
