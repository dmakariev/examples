package com.makariev.examples.ai.neuralnet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class makeTrainingSamplesXor {

    private static final Path FILE_PATH_TRAIN = Paths.get("dataset/dataset-xor_train.csv");
    private static final int NUMBER_OF_SAMPLES_TRAIN = 2_000;

    private static final Path FILE_PATH_TEST = Paths.get("dataset/dataset-xor_test.csv");
    private static final int NUMBER_OF_SAMPLES_TEST = 30;

    public static void main(String[] args) {
        generateFile(FILE_PATH_TRAIN, NUMBER_OF_SAMPLES_TRAIN);
        generateFile(FILE_PATH_TEST, NUMBER_OF_SAMPLES_TEST);
    }

    private static void generateFile(final Path filePath, int numberOfSamples) {
        final List<String> lines = new ArrayList<>();
        lines.add("output,n1,n2");

        for (int i = 0; i < numberOfSamples; i++) {
            int n1 = (int) Math.round(Math.random());
            int n2 = (int) Math.round(Math.random());
            int output = n1 ^ n2;
            lines.add("%s.0,%s.0,%s.0".formatted(output, n1, n2));
        }

        try {
            Files.write(filePath, lines);
            System.out.println("File " + filePath + " created successfully!");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}
