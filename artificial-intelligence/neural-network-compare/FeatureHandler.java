package com.makariev.examples.ai.neuralnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class FeatureHandler {

    public record TrainTestSplit(List<double[]> trainData, List<double[]> testData) {

    }

    public static TrainTestSplit splitData(List<double[]> data, double trainPercentage) {
        // Shuffling the data to ensure randomness
        Collections.shuffle(data);

        int totalSize = data.size();
        int trainSize = (int) (totalSize * trainPercentage / 100.0);

        List<double[]> trainData = new ArrayList<>(data.subList(0, trainSize));
        List<double[]> testData = new ArrayList<>(data.subList(trainSize, totalSize));

        return new TrainTestSplit(trainData, testData);
    }

    public static List<double[]> removeColumns(List<double[]> data, int[] removeColumns) {
        List<double[]> newData = new ArrayList<>();

        for (double[] row : data) {
            double[] newRow = IntStream.range(0, row.length)
                    .filter(index -> Arrays.binarySearch(removeColumns, index) < 0)
                    .mapToDouble(index -> row[index])
                    .toArray();
            newData.add(newRow);
        }

        return newData;
    }

    public static List<double[]> extractColumns(List<double[]> data, int[] extractColumns) {
        List<double[]> newData = new ArrayList<>();

        for (double[] row : data) {
            double[] newRow = new double[extractColumns.length];
            for (int i = 0; i < extractColumns.length; i++) {
                newRow[i] = row[extractColumns[i]];
            }
            newData.add(newRow);
        }

        return newData;
    }

    public static void processInChunks(List<double[]> data, int chunkSize, Consumer<List<double[]>> consumer) {
        List<double[]> chunk = new ArrayList<>(chunkSize);
        for (double[] item : data) {
            chunk.add(item);
            if (chunk.size() == chunkSize) {
                consumer.accept(chunk);
                chunk = new ArrayList<>(chunkSize); // Reset for next chunk
            }
        }
        if (!chunk.isEmpty()) {
            consumer.accept(chunk); // Process the final chunk
        }
    }

    public static List<double[]> readCSV(String fileName, int[] columns) {
        try {
            return readCSV(fileName, columns, true, ",");
        } catch (IOException ex) {
            throw new RuntimeException("unable to read from '" + fileName + "'", ex);
        }
    }

    public static List<double[]> readCSV(String fileName, int[] columns, boolean skipFirstRow, String delimiter) throws IOException {
        List<double[]> data = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName));) {
            String line;
            boolean isFirstRow = true;

            while ((line = br.readLine()) != null) {
                if (isFirstRow && skipFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                String[] values = line.split(delimiter);
                double[] numericValues = new double[columns.length];
                for (int i = 0; i < columns.length; i++) {
                    numericValues[i] = values[columns[i]].equals("NA") ? Double.NaN : Double.parseDouble(values[columns[i]]);
                }
                data.add(numericValues);
            }
        }
        return data;
    }

    public static List<double[]> removeNaN(List<double[]> data) {
        final List<double[]> result = new ArrayList<>();
        for (double[] row : data) {
            boolean shouldRemove = false;
            for (int i = 0; i < row.length; i++) {
                if (Double.isNaN(row[i])) {
                    shouldRemove = true;
                    break;
                }
            }
            if (!shouldRemove) {
                result.add(row);
            }
        }
        return result;
    }

    // Calculate mean or median for each column
    public static List<double[]> replaceWithMeans(List<double[]> data) {
        double[] calculated = calculate(data, FeatureHandler::calculateMean);
        return replaceMissingValues(calculated, data);
    }

    // Calculate mean or median for each column
    public static List<double[]> replaceWithMedians(List<double[]> data) {
        double[] calculated = calculate(data, FeatureHandler::calculateMedian);
        return replaceMissingValues(calculated, data);
    }

    private static double[] calculate(List<double[]> data, Function<double[], Double> function) {

        int numFeatures = data.get(0).length;
        double[] meansOrMedians = new double[numFeatures];

        for (int i = 0; i < numFeatures; i++) {
            final int index = i;
            double[] columnData = data.stream().mapToDouble(row -> row[index]).filter(d -> !Double.isNaN(d)).toArray();
            meansOrMedians[i] = function.apply(columnData);
        }
        return meansOrMedians;
    }

    private static double calculateMedian(double[] data) {
        Arrays.sort(data);
        int middle = data.length / 2;
        if (data.length % 2 == 0) {
            return (data[middle - 1] + data[middle]) / 2.0;
        } else {
            return data[middle];
        }
    }

    private static double calculateMean(double[] data) {
        return DoubleStream.of(data).average().orElse(Double.NaN);
    }

    // Replace missing values with mean or median
    private static List<double[]> replaceMissingValues(double[] calculated, List<double[]> data) {
        final List<double[]> result = new ArrayList<>();
        for (double[] row : data) {
            final double[] replacedRow = new double[row.length];
            for (int i = 0; i < row.length; i++) {
                if (Double.isNaN(row[i])) {
                    replacedRow[i] = calculated[i];
                } else {
                    replacedRow[i] = row[i];
                }
            }
            result.add(replacedRow);
        }
        return result;
    }
}
