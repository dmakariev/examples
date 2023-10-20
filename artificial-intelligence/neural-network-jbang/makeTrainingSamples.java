//usr/bin/env jbang "$0" "$@" ; exit $?
package com.makariev.examples.ai.neuralnet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class makeTrainingSamples {

    private static final Path FILE_PATH = Paths.get("simple_test.txt");

    public static void main(String[] args) {
        final List<String> lines = new ArrayList<>();

        lines.add("topology: 2 4 1");

        for (int i = 0; i < 2_000; i++) {
            int n1 = (int) Math.round(Math.random());
            int n2 = (int) Math.round(Math.random());
            int t = n1 ^ n2;
            lines.add("in: %s.0 %s.0".formatted(n1, n2));
            lines.add("out: %s.0".formatted(t));
        }

        try {
            Files.write(FILE_PATH, lines);
            System.out.println("File " + FILE_PATH + " created successfully!");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}
