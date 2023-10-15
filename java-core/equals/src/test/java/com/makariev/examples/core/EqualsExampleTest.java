package com.makariev.examples.core;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EqualsExampleTest {

    @Test
    void testEquality() {
        // Example 1: Comparing Primitives with '=='
        int x = 5;
        int y = 5;
        assertThat(x == y).isTrue();

        // Example 2: Comparing Strings with '=='
        String str1 = "Hello";
        String str2 = "Hello";
        assertThat(str1 == str2).isTrue();

        // Example 3: Comparing Strings with 'equals()'
        String str3 = "Hello";
        String str4 = "Hello";
        assertThat(str3.equals(str4)).isTrue();

        // Example 4: Comparing Objects with '=='
        Object obj1 = new Object();
        Object obj2 = obj1;
        assertThat(obj1 == obj2).isTrue();

        // Example 5: Comparing Objects with 'equals()'
        String s1 = "Hello";
        String s2 = "Hello";
        assertThat(s1.equals(s2)).isTrue();

        // Example 6: Custom Class Comparison with '=='
        Person person1 = new Person("Alice");
        Person person2 = person1;
        assertThat(person1 == person2).isTrue();

        //Example 7: Custom Class Comparison with 'equals()'
        // Example 7.1: overrided 'equals()'
        Person person3 = new Person("Alice");
        Person person4 = new Person("Alice");
        assertThat(person3.equals(person4)).isTrue();

        // Example 7.2: no overrided 'equals()'
        PersonNoEquals person5 = new PersonNoEquals("Alice");
        PersonNoEquals person6 = new PersonNoEquals("Alice");
        assertThat(person5.equals(person6)).isFalse(); //different reference, no 'equals()'
        assertThat(person5 == person6).isFalse(); //different reference
        
        PersonNoEquals person7 = person5;
        assertThat(person5.equals(person7)).isTrue(); //same reference
        assertThat(person5 == person7).isTrue(); //same reference

        // Example 8: Null Comparison with '=='
        Object obj3 = null;
        Object obj4 = null;
        assertThat(obj3 == obj4).isTrue();

        // Example 9: Null Comparison with 'equals()', it will throw Exception !
        Object obj5 = null;
        Object obj6 = null;
        assertThatThrownBy(() -> {
            boolean result = obj5.equals(obj6);
            assertThat(result).isTrue(); // this line will never execute, because the previous will throw NullPointerException
        }).isInstanceOf(NullPointerException.class);

        // Example 10: Comparing Objects of Different Classes
        String str = "Hello";
        Integer num = 5;
        assertThat(str.equals(num)).isFalse();
    }

    static class Person {

        private String name;

        Person(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Person person = (Person) obj;
            return name.equals(person.name);
        }
    }

    static class PersonNoEquals {

        private String name;

        PersonNoEquals(String name) {
            this.name = name;
        }

    }
}
