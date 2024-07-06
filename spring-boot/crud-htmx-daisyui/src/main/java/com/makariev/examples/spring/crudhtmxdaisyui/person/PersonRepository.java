package com.makariev.examples.spring.crudhtmxdaisyui.person;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}