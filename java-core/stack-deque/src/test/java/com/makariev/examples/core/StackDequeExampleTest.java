package com.makariev.examples.core;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class StackDequeExampleTest {

    @Test
    void testStackAndDequeOperations() {
        // Create a stack
        final Stack<String> stack = new Stack<>();
        stack.push("Alan Turing");
        stack.push("Grace Hopper");
        stack.push("Linus Torvalds");
        stack.push("Ada Lovelace");
        stack.push("Donald Knuth");

        // Check the size of the stack
        assertThat(stack.size()).isEqualTo(5);

        // Pop elements from the stack and check their order
        assertThat(stack.pop()).isEqualTo("Donald Knuth");
        assertThat(stack.pop()).isEqualTo("Ada Lovelace");
        assertThat(stack.pop()).isEqualTo("Linus Torvalds");
        assertThat(stack.pop()).isEqualTo("Grace Hopper");
        assertThat(stack.pop()).isEqualTo("Alan Turing");

        // Create a deque
        final Deque<String> deque = new ArrayDeque<>();
        deque.push("Alan Turing");
        deque.push("Grace Hopper");
        deque.push("Linus Torvalds");
        deque.push("Ada Lovelace");
        deque.push("Donald Knuth");

        // Check the size of the deque
        assertThat(deque.size()).isEqualTo(5);

        // Pop elements from the deque and check their order
        assertThat(deque.pop()).isEqualTo("Donald Knuth");
        assertThat(deque.pop()).isEqualTo("Ada Lovelace");
        assertThat(deque.pop()).isEqualTo("Linus Torvalds");
        assertThat(deque.pop()).isEqualTo("Grace Hopper");
        assertThat(deque.pop()).isEqualTo("Alan Turing");
    }
}