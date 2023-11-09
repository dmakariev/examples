package com.makariev.examples.ai.neuralnet;

import java.util.Objects;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class TrainingData {

    private final long upToLine;
    private final String zipFileName;
    private final String trainFileName;
    private final String testFileName;

    private TrainingData(long upToLine, String zipFileName, String trainFileName, String testFileName) {
        this.upToLine = upToLine;
        this.zipFileName = zipFileName;
        this.trainFileName = trainFileName;
        this.testFileName = testFileName;
    }

    public static TrainingData mnistTrainData() {
        return mnistTrainData(-1);
    }

    public static TrainingData mnistTrainData(long upToLine) {
        return new TrainingData(upToLine, "dataset/dataset-MNIST.zip", "mnist_train.csv", "mnist_test.csv");
    }

    public static TrainingData xorTrainData() {
        return new TrainingData(-1, null, "dataset/dataset-xor_train.csv", "dataset/dataset-xor_test.csv");
    }

    @FunctionalInterface
    public interface TrainingDataConsumer {

        boolean accept(double[] inputVals, double[] targetValue);
    }

    @FunctionalInterface
    public interface TrainingDataChunkConsumer {

        void acceptChunk(List<double[]> inputVals, List<double[]> targetVals);
    }

    public void testPredictChunk(int chunkSize, TrainingDataConsumer consumer) {
        testPredictChunk(chunkSize, "", consumer);
    }

    public void testPredictChunk(int chunkSize, String messagePrefix, TrainingDataConsumer consumer) {
        try (BufferedReader br = newBufferedReader(testFileName)) {
            br.readLine(); //reading and skipping the first line 
            int trainingPass = 0;
            int correctPredictions = 0;
            for (int i = 0; i < chunkSize && br.ready(); i++) {
                ++trainingPass;
                final LabeledData labeledData = parseDoubleLine(br.readLine());
                final double[] target = new double[]{labeledData.label()};
                if (consumer.accept(labeledData.data(), target)) {
                    correctPredictions++;
                }
            }
            double accuracy = (double) correctPredictions / (double) trainingPass;
            System.out.println(messagePrefix + "Accuracy: %.2f%% ".formatted(accuracy * 100));

        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to process training data, exception accured:", ex);
        }
    }

    public void trainChunk(int chunkSize, boolean displayDebug, TrainingDataChunkConsumer consumer) {
        try (BufferedReader br = newBufferedReader(trainFileName)) {
            br.readLine(); //reading and skipping the first line 
            int trainingPass = 0;
            int lineCount = 0;
            while (br.ready()) {
                ++trainingPass;
                final List<double[]> inputVals = new ArrayList<>();
                final List<double[]> targetVals = new ArrayList<>();
                for (int i = 0; i < chunkSize && br.ready() && (upToLine == -1 || lineCount < upToLine); i++) {
                    ++lineCount;
                    final LabeledData labeledData = parseDoubleLine(br.readLine());
                    inputVals.add(labeledData.data());

                    final double[] target = new double[]{labeledData.label()};
                    targetVals.add(target);
                }
                final long startTimeCalc = System.currentTimeMillis();
                if (inputVals.size() < chunkSize || targetVals.size() < chunkSize) {
                    System.out.println(" **** warning: values less then chunk size " + chunkSize + ", inputVals.size()=" + inputVals.size() + ", targetVals.size()=" + targetVals.size());
                }
                if (inputVals.isEmpty() || targetVals.isEmpty()) {
                    System.out.println("Chunk " + trainingPass + ", exiting because inputVals.size()=" + inputVals.size() + ", targetVals.size()=" + targetVals.size());
                    break;
                }

                consumer.acceptChunk(inputVals, targetVals);

                if (displayDebug) {
                    System.out.println("Chunk " + trainingPass + ", lineCount=" + lineCount + ", calculated for=" + (System.currentTimeMillis() - startTimeCalc));
                }
            }

        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to process training data, exception accured:", ex);
        }
    }

    public void trainLine(TrainingDataConsumer consumer) {
        try (BufferedReader br = newBufferedReader(trainFileName)) {
            br.readLine(); //reading and skipping the first line 
            while (br.ready()) {
                final LabeledData labeledData = parseDoubleLine(br.readLine());
                final double[] inputVals = labeledData.data();
                final double[] targetVals = new double[]{labeledData.label()};

                consumer.accept(inputVals, targetVals);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to process training data, exception accured:", ex);
        }
    }

    public static int getMaxIndex(double[] prediction) {
        int maxIndex = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < prediction.length; i++) {
            if (prediction[i] >= maxValue) {
                maxIndex = i;
                maxValue = prediction[i];
            }
        }
        return maxIndex;
    }

    private static record LabeledData(double label, double[] data) {

    }

    private static LabeledData parseDoubleLine(final String line) {
        final String[] array = line.split(",");
        final double[] result = new double[array.length - 1];
        for (int i = 1; i < array.length; i++) {
            double value = Double.parseDouble(array[i]);
            result[i - 1] = value;
        }
        return new LabeledData(Double.parseDouble(array[0]), result);
    }

    private static int[] getTopology(List<double[][]> weights) {
        final int[] topology = new int[weights.size() + 1];
        for (int i = 0; i < weights.size(); i++) {
            topology[i] = weights.get(i)[0].length;
        }
        topology[weights.size()] = weights.get(weights.size() - 1).length;
        return topology;
    }

    public static void saveTo(String fileName, List<double[][]> weights, List<double[][]> biases) {
        getSaver(fileName).accept(fileName, writer -> {
            try {

                final int[] topology = getTopology(weights);
                for (int i = 0; i < topology.length; i++) {
                    writer.write(Integer.toString(topology[i]));
                    if (i < topology.length - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
                for (double[][] matrix : weights) {
                    writeMatrix(matrix, writer);
                    writer.newLine();
                }
                for (double[][] matrix : biases) {
                    writeMatrix(matrix, writer);
                    writer.newLine();
                }
            } catch (IOException ex) {
                throw new RuntimeException("unable to save " + fileName, ex);
            }
        });
    }

    public static void saveTo(String fileName, double[][] inputWeights, double[][] outputWeights, double[][] biases) {
        getSaver(fileName).accept(fileName, writer -> {
            try {

                final int numInputNodes = inputWeights[0].length;
                final int numHiddenNodes = inputWeights.length;
                final int numOutputNodes = outputWeights.length;
                final int[] topology = new int[]{numInputNodes, numHiddenNodes, numOutputNodes};
                for (int i = 0; i < topology.length; i++) {
                    writer.write(Integer.toString(topology[i]));
                    if (i < topology.length - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();
                writeMatrix(inputWeights, writer);
                writer.newLine();
                writeMatrix(outputWeights, writer);
                writer.newLine();
                writeMatrix(biases, writer);
                writer.newLine();
            } catch (IOException ex) {
                throw new RuntimeException("unable to save " + fileName, ex);
            }
        });
    }

    private static void writeMatrix(double[][] matrix, final BufferedWriter writer) throws IOException {
        for (double[] row : matrix) {
            for (int i = 0; i < row.length; i++) {
                writer.write(Double.toString(row[i]));
                if (i < row.length - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();
        }
    }

    public static <T> T loadFrom(String filePath, BiFunction<List<double[][]>, List<double[][]>, T> biFunction) {
        return TrainingData.<T>getLoader(filePath).apply(filePath, br -> {
            try {
                //read topology; layerSizes
                final List<Integer> topology = Arrays.asList(br.readLine().split(",")).stream().map(Integer::parseInt).toList();

                final List<double[][]> weights = new ArrayList<>();
                for (int n = 0; n < topology.size() - 1; n++) {
                    double[][] weight = new double[topology.get(n + 1)][topology.get(n)];
                    for (int row = 0; row < topology.get(n + 1); row++) {
                        final String line = br.readLine();
                        final String[] values = line.split(",");
                        for (int col = 0; col < topology.get(n); col++) {
                            weight[row][col] = Double.parseDouble(values[col]);
                        }
                    }
                    weights.add(weight);
                    br.readLine();//skip empty line 
                }

                final List<double[][]> biases = new ArrayList<>();
                for (int n = 0; n < topology.size() - 1; n++) {
                    double[][] bias = new double[topology.get(n + 1)][1];
                    for (int row = 0; row < topology.get(n + 1); row++) {
                        final double value = Double.parseDouble(br.readLine());
                        bias[row][0] = value;
                    }
                    biases.add(bias);
                    br.readLine();//skip empty line 
                }

                return biFunction.apply(weights, biases);
            } catch (IOException ex) {
                throw new IllegalStateException("unable to load from " + filePath, ex);
            }
        });
    }

    public static <T> T loadFrom(String zipFilePath, Function<double[][], Function<double[][], Function<double[][], T>>> triFunction) {
        return TrainingData.<T>getLoader(zipFilePath).apply(zipFilePath, br -> {
            try {

                // Read topology; layerSizes
                final List<Integer> topology = Arrays.stream(br.readLine().split(","))
                        .map(Integer::parseInt)
                        .toList();
                final int numInputNodes = topology.get(0);
                final int numHiddenNodes = topology.get(1);
                final int numOutputNodes = topology.get(2);

                final double[][] inputWeights = readMatrix(numHiddenNodes, numInputNodes, br);
                br.readLine(); // Skip empty line
                final double[][] outputWeights = readMatrix(numOutputNodes, numHiddenNodes, br);
                br.readLine(); // Skip empty line
                final double[][] biases = readMatrix(numHiddenNodes, 1, br);
                br.readLine(); // Skip empty line

                return triFunction.apply(inputWeights).apply(outputWeights).apply(biases);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static double[][] readMatrix(int rows, int cols, BufferedReader br) throws IOException {
        final double[][] matrix = new double[rows][cols];
        for (int row = 0; row < rows; row++) {
            final String line = br.readLine();
            final String[] values = line.split(",");//values.length should be == numInputNodes
            if (values.length != cols) {
                throw new IllegalStateException("line.length=" + values.length + " should be " + cols + "! line=" + line);
            }
            for (int col = 0; col < cols; col++) {
                matrix[row][col] = Double.parseDouble(values[col]);
            }
        }
        return matrix;
    }

    private BufferedReader newBufferedReader(String fileName) throws IOException {
        Objects.requireNonNull(fileName);
        if (null == zipFileName) {
            return Files.newBufferedReader(Paths.get(fileName));
        } else {
            final ZipFile zipFile = new ZipFile(zipFileName);
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                if (fileName.equals(entry.getName())) {
                    final InputStream is = zipFile.getInputStream(entry);
                    return new BufferedReader(new InputStreamReader(is, "UTF-8"));
                }
            }
        }
        throw new IllegalArgumentException("unable to open filename=" + fileName + ", zipFileName=" + zipFileName);
    }

    private static BiConsumer<String, Consumer<BufferedWriter>> getSaver(String filePath) {
        final BiConsumer<String, Consumer<BufferedWriter>> loader
                = filePath.endsWith(".zip")
                ? TrainingData::saveToZIP
                : TrainingData::saveToPlain;
        return loader;
    }

    private static void saveToPlain(String fileName, Consumer<BufferedWriter> consumer) {
        System.out.println("Saving " + fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE)) {

            consumer.accept(writer);
        } catch (IOException ex) {
            System.out.println("unable to save " + fileName);
        }
    }

    private static void saveToZIP(String fileName, Consumer<BufferedWriter> consumer) {
        System.out.println("Saving " + fileName);
        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(Paths.get(fileName))); BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zipOut, StandardCharsets.UTF_8))) {
            zipOut.putNextEntry(new ZipEntry("neural_network.csv"));

            consumer.accept(writer);
        } catch (IOException ex) {
            System.out.println("unable to save " + fileName);
        }
    }

    private static <T> BiFunction<String, Function<BufferedReader, T>, T> getLoader(String filePath) {
        final BiFunction<String, Function<BufferedReader, T>, T> loader
                = filePath.endsWith(".zip")
                ? TrainingData::loadFromZIP
                : TrainingData::loadFromPlain;
        return loader;
    }

    private static <T> T loadFromPlain(String fileName, Function<BufferedReader, T> function) {
        System.out.println("Loading " + fileName);
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            return function.apply(br);
        } catch (IOException ex) {
            throw new IllegalStateException("unable to load from " + fileName, ex);
        }
    }

    private static <T> T loadFromZIP(String fileName, Function<BufferedReader, T> function) {
        System.out.println("Loading " + fileName);
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(fileName)))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory() && "neural_network.csv".equals(zipEntry.getName())) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8))) {
                        return function.apply(br);
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            throw new IllegalStateException("CSV file not found in zip archive: " + fileName);
        } catch (IOException | RuntimeException ex) {
            throw new IllegalStateException("Unable to load from " + fileName, ex);
        }
    }

}
