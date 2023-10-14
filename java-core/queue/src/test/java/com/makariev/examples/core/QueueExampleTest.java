package com.makariev.examples.core;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedList;
import java.util.ArrayDeque;
import java.util.Queue;

public class QueueExampleTest {

    @Test
    void testQueueLinkedListArrayDeque() {
        // Create a queue
        final Queue<String> queue = new LinkedList<>();
        queue.add("Alan Turing");
        queue.add("Grace Hopper");
        queue.add("Linus Torvalds");
        queue.add("Ada Lovelace");
        queue.add("Donald Knuth");

        // Check the size of the queue
        assertThat(queue.size()).isEqualTo(5);

        // Poll elements from the queue and check their order (FIFO)
        assertThat(queue.poll()).isEqualTo("Alan Turing");
        assertThat(queue.poll()).isEqualTo("Grace Hopper");
        assertThat(queue.poll()).isEqualTo("Linus Torvalds");
        assertThat(queue.poll()).isEqualTo("Ada Lovelace");
        assertThat(queue.poll()).isEqualTo("Donald Knuth");

        // Create a LinkedList (used as a queue)
        final LinkedList<String> linkedList = new LinkedList<>();
        linkedList.addLast("Alan Turing");
        linkedList.addLast("Grace Hopper");
        linkedList.addLast("Linus Torvalds");
        linkedList.addLast("Ada Lovelace");
        linkedList.addLast("Donald Knuth");

        // Check the size of the linked list (queue)
        assertThat(linkedList.size()).isEqualTo(5);

        // Remove elements from the linked list (queue) and check their order (FIFO)
        assertThat(linkedList.poll()).isEqualTo("Alan Turing");
        assertThat(linkedList.poll()).isEqualTo("Grace Hopper");
        assertThat(linkedList.poll()).isEqualTo("Linus Torvalds");
        assertThat(linkedList.poll()).isEqualTo("Ada Lovelace");
        assertThat(linkedList.poll()).isEqualTo("Donald Knuth");

        // Create an ArrayDeque (used as a queue)
        final ArrayDeque<String> arrayDeque = new ArrayDeque<>();
        arrayDeque.addLast("Alan Turing");
        arrayDeque.addLast("Grace Hopper");
        arrayDeque.addLast("Linus Torvalds");
        arrayDeque.addLast("Ada Lovelace");
        arrayDeque.addLast("Donald Knuth");

        // Check the size of the ArrayDeque (queue)
        assertThat(arrayDeque.size()).isEqualTo(5);

        // Remove elements from the ArrayDeque (queue) and check their order (FIFO)
        assertThat(arrayDeque.poll()).isEqualTo("Alan Turing");
        assertThat(arrayDeque.poll()).isEqualTo("Grace Hopper");
        assertThat(arrayDeque.poll()).isEqualTo("Linus Torvalds");
        assertThat(arrayDeque.poll()).isEqualTo("Ada Lovelace");
        assertThat(arrayDeque.poll()).isEqualTo("Donald Knuth");
    }
}
