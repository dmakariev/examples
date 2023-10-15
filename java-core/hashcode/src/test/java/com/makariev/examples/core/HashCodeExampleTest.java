package com.makariev.examples.core;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class HashCodeExampleTest {

    @Test
    void testHashCode() {
        // Example 1: Default `hashCode()` Method
        ExampleObject obj1 = new ExampleObject("example");
        ExampleObject obj2 = new ExampleObject("example");
        assertThat(obj1.hashCode()).isNotEqualTo(obj2.hashCode());

        // Example 2: Custom `hashCode()` Method
        CustomHashCodeObject customObj1 = new CustomHashCodeObject("example");
        CustomHashCodeObject customObj2 = new CustomHashCodeObject("example");
        assertThat(customObj1.hashCode()).isEqualTo(customObj2.hashCode());

        // Example 3: `hashCode()` for Strings
        String str1 = "example";
        String str2 = "example";
        assertThat(str1.hashCode()).isEqualTo(str2.hashCode());

        // Example 4: Custom `hashCode()` for Complex Objects
        ComplexObject complexObj1 = new ComplexObject(42, "example");
        ComplexObject complexObj2 = new ComplexObject(42, "example");
        assertThat(complexObj1.hashCode()).isEqualTo(complexObj2.hashCode());

        // Example 5: `hashCode()` for Enums
        Color color1 = Color.RED;
        Color color2 = Color.RED;
        assertThat(color1.hashCode()).isEqualTo(color2.hashCode());

        // Example 6: HashMap with Custom `hashCode()`
        HashMap<CustomHashCodeObject, String> hashMap = new HashMap<>();
        CustomHashCodeObject obj = new CustomHashCodeObject("example");
        hashMap.put(obj, "Value");
        String value = hashMap.get(obj);
        assertThat(value).isEqualTo("Value");

        // Example 7: HashSet with Custom `hashCode()`
        HashSet<CustomHashCodeObject> hashSet = new HashSet<>();
        CustomHashCodeObject setObj = new CustomHashCodeObject("example");
        hashSet.add(setObj);
        boolean contains = hashSet.contains(setObj);
        assertThat(contains).isTrue();

        // Example 8: HashMap Collision Handling
        HashMap<CustomHashCodeObject, String> collisionHashMap = new HashMap<>();
        CustomHashCodeObject collisionObj1 = new CustomHashCodeObject("example1");
        CustomHashCodeObject collisionObj2 = new CustomHashCodeObject("example2");
        collisionHashMap.put(collisionObj1, "Value1");
        collisionHashMap.put(collisionObj2, "Value2");
        String collisionValue1 = collisionHashMap.get(collisionObj1);
        String collisionValue2 = collisionHashMap.get(collisionObj2);
        assertThat(collisionValue1).isEqualTo("Value1");
        assertThat(collisionValue2).isEqualTo("Value2");

        // Example 9: HashSet with Collisions
        HashSet<CustomHashCodeObject> collisionHashSet = new HashSet<>();
        CustomHashCodeObject collisionObjA = new CustomHashCodeObject("example1");
        CustomHashCodeObject collisionObjB = new CustomHashCodeObject("example2");
        collisionHashSet.add(collisionObjA);
        collisionHashSet.add(collisionObjB);
        boolean collisionContains1 = collisionHashSet.contains(collisionObjA);
        boolean collisionContains2 = collisionHashSet.contains(collisionObjB);
        assertThat(collisionContains1).isTrue();
        assertThat(collisionContains2).isTrue();
    }

    static class ExampleObject {

        private String data;

        // Default `hashCode()` method
        ExampleObject(String data) {
            this.data = data;
        }
    }

    class ComplexObject {

        private int id;
        private String name;

        ComplexObject(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }
    }

    static class CustomHashCodeObject {

        private String data;

        CustomHashCodeObject(String data) {
            this.data = data;
        }

        @Override
        public int hashCode() {
            return data.hashCode();
        }
    }

    enum Color {
        RED, GREEN, BLUE
    }

}
