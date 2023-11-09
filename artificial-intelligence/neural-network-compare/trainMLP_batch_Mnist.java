//usr/bin/env jbang "$0" "$@" ; exit $?
//SOURCES SimpleMLPBatch.java
//SOURCES TrainingData.java
package com.makariev.examples.ai.neuralnet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class trainMLP_batch_Mnist {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final TrainingData trainData = TrainingData.mnistTrainData();

        //1500, 848sec (~14min), 98.20%
        // Example: Assuming the data has 784 inputs, 64 hidden neurons, and 10 output
        final SimpleMLPBatch myMLP = new SimpleMLPBatch(784, 64, 10);
//        final SimpleMLPBatch myMLP = TrainingData.loadFrom("model/mlp_batch_mnist-2000-1000-500-200.zip", (weights, biases) -> {
//            return new SimpleMLPBatch(weights, biases);
//        });

        double initialLearningRate = 0.03;
        double decayRate = 0.95;  // e.g., reduce learning rate by 5%
        int decayStep = 2;  // e.g., reduce learning rate every 2 epochs

        // Train
        for (int epoch = 0; epoch < 5; epoch++) {
            if (epoch % decayStep == 0 && epoch != 0) {
                initialLearningRate *= decayRate;
            }
            final double learningRate = initialLearningRate;

            //final int numThreads = Runtime.getRuntime().availableProcessors(); // Number of available CPU cores
            //final ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

            trainData.trainChunk(5, false, (inputVals, targetVals) -> {

                executorService.submit(() -> {
                    final List<double[]> inputBatch = new ArrayList<>();
                    final List<double[]> targetBatch = new ArrayList<>();

                    for (int i = 0; i < inputVals.size(); i++) {
                        double[] input = inputVals.get(i);
                        for (int n = 0; n < input.length; n++) {
                            // normalization
                            // Scale the pixel values to the range [0,1]
                            input[n] = input[n] / 255;
                        }
                        inputBatch.add(input);

                        double[] target = new double[10];
                        target[(int) targetVals.get(i)[0]] = 1.0;  // One-hot encoding
                        targetBatch.add(target);
                    }

                    // Train the MLP with the current sample
                    myMLP.trainBatch(inputBatch, targetBatch, learningRate);
                });
            });

            // Wait for all threads to finish processing the current mini-batch
            try {
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Test and Calculate Accuracy
            trainData.testPredictChunk(
                    12_000,
                    "Epoch %d, Learning Rate: %.4f ".formatted(epoch, learningRate),
                    (inputVals, targetVals) -> {

                        double[] target = new double[10];
                        target[(int) targetVals[0]] = 1.0;  // One-hot encoding

                        for (int i = 0; i < inputVals.length; i++) {
                            inputVals[i] = inputVals[i] / 255;
                        }

                        final double[] predictions = myMLP.predict(inputVals);
                        final int predictedLabel = TrainingData.getMaxIndex(predictions);
                        final int targetLabel = TrainingData.getMaxIndex(target);
                        return predictedLabel == targetLabel;
                    }
            );

        }

        System.out.println();

        trainData.testPredictChunk(12_000, (inputVals, targetVals) -> {
            double[] target = new double[10];
            target[(int) targetVals[0]] = 1.0;  // One-hot encoding

            for (int i = 0; i < inputVals.length; i++) {
                inputVals[i] = inputVals[i] / 255;
            }

            final double[] predictions = myMLP.predict(inputVals);
            final int predictedLabel = TrainingData.getMaxIndex(predictions);
            final int targetLabel = TrainingData.getMaxIndex(target);
            return predictedLabel == targetLabel;
        });

        System.out.println("\nexecution time: " + (System.currentTimeMillis() - startTime) + "ms\n");
        
        myMLP.saveModel((weights, biases) -> {
            TrainingData.saveTo("model/mlp_batch_mnist.csv", weights, biases);
        });
    }

}
