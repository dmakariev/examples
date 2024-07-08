package com.makariev.examples.spring.crudhtmxdaisyui;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 *
 * @author dmakariev
 */
public class CrudHtmxRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
//		hints.resources().registerPattern("db/*"); // https://github.com/spring-projects/spring-boot/issues/32654
//		hints.resources().registerPattern("messages/*");
//		hints.resources().registerPattern("META-INF/resources/webjars/*");
//		hints.resources().registerPattern("mysql-default-conf");
        hints.reflection().registerType(org.thymeleaf.expression.Lists.class, MemberCategory.values());
//		hints.serialization().registerType(BaseEntity.class);
//		hints.serialization().registerType(Person.class);
//		hints.serialization().registerType(Vet.class);
    }

}
