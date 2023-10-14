package com.makariev.examples.core;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class StackDequeExampleTest {

    @Test
    void testStackLinkedListDeque() {
        // Create a stack
        final Stack<String> stack = new Stack<>();
        stack.push("Alan Turing");
        stack.push("Grace Hopper");
        stack.push("Linus Torvalds");
        stack.push("Ada Lovelace");
        stack.push("Donald Knuth");

        // Check the size of the stack
        assertThat(stack.size()).isEqualTo(5);

        // Pop elements from the stack and check their order (FILO)
        assertThat(stack.pop()).isEqualTo("Donald Knuth");
        assertThat(stack.pop()).isEqualTo("Ada Lovelace");
        assertThat(stack.pop()).isEqualTo("Linus Torvalds");
        assertThat(stack.pop()).isEqualTo("Grace Hopper");
        assertThat(stack.pop()).isEqualTo("Alan Turing");

        // Create a LinkedList (used as a stack)
        final LinkedList<String> linkedList = new LinkedList<>();
        linkedList.push("Alan Turing");
        linkedList.push("Grace Hopper");
        linkedList.push("Linus Torvalds");
        linkedList.push("Ada Lovelace");
        linkedList.push("Donald Knuth");

        // Check the size of the linked list (stack)
        assertThat(linkedList.size()).isEqualTo(5);

        // Remove elements from the linked list (stack) and check their order (FILO)
        assertThat(linkedList.pop()).isEqualTo("Donald Knuth");
        assertThat(linkedList.pop()).isEqualTo("Ada Lovelace");
        assertThat(linkedList.pop()).isEqualTo("Linus Torvalds");
        assertThat(linkedList.pop()).isEqualTo("Grace Hopper");
        assertThat(linkedList.pop()).isEqualTo("Alan Turing");

        // Create a Deque (used as a stack)
        final Deque<String> deque = new ArrayDeque<>();
        deque.push("Alan Turing");
        deque.push("Grace Hopper");
        deque.push("Linus Torvalds");
        deque.push("Ada Lovelace");
        deque.push("Donald Knuth");

        // Check the size of the deque (stack)
        assertThat(deque.size()).isEqualTo(5);

        // Remove elements from the deque (stack) and check their order (FILO)
        assertThat(deque.pop()).isEqualTo("Donald Knuth");
        assertThat(deque.pop()).isEqualTo("Ada Lovelace");
        assertThat(deque.pop()).isEqualTo("Linus Torvalds");
        assertThat(deque.pop()).isEqualTo("Grace Hopper");
        assertThat(deque.pop()).isEqualTo("Alan Turing");
    }
}
