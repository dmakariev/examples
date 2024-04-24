package com.makariev.examples.spring.bookstore;

import static org.assertj.core.api.Assertions.assertThatCode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookstoreApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void applicationStarts() {
        assertThatCode(() -> BookstoreApplication.main(new String[]{})).doesNotThrowAnyException();
    }

}
