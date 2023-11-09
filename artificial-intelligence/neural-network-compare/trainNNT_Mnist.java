//usr/bin/env jbang "$0" "$@" ; exit $?
//SOURCES NeuralNetTutorial.java
//SOURCES TrainingData.java
package com.makariev.examples.ai.neuralnet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class trainNNT_Mnist {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        // Example: Assuming the data has 784 inputs, 64 hidden neurons, and 10 output
        final NeuralNetTutorial.Net myNet = new NeuralNetTutorial.Net(Arrays.asList(784, 64, 10));

        final TrainingData trainData = TrainingData.mnistTrainData();

        // Train
        for (int epoch = 0; epoch < 5; epoch++) {

            trainData.trainLine((inputVals, targetVals) -> {

                double[] input = inputVals;
                for (int n = 0; n < input.length; n++) {
                    // normalization
                    // Scale the pixel values to the range [0,1]
                    input[n] = input[n] / 255;
                }

                double[] target = new double[10];
                target[(int) targetVals[0]] = 1;  // One-hot encoding

                final List<Double> inputValues = new ArrayList<>();
                for (int i = 0; i < input.length; i++) {
                    inputValues.add(input[i]);
                }

                // Train the MLP with the current sample
                // Get new input data and feed it forward:
                myNet.feedForward(inputValues);

                final List<Double> targetValues = new ArrayList<>();
                for (int i = 0; i < target.length; i++) {
                    targetValues.add(target[i]);
                }

                // Train the net what the outputs should have been:
                myNet.backProp(targetValues);

                return true;
            });
            
            // Test and Calculate Accuracy
            trainData.testPredictChunk(
                    10_000,
                    "Epoch: %d, ".formatted(epoch),
                    (inputVals, targetVals) -> {
                        final List<Double> inputValues = new ArrayList<>();
                        for (int n = 0; n < inputVals.length; n++) {
                            // normalization
                            // Scale the pixel values to the range [0,1]
                            inputValues.add(inputVals[n] / 255);
                        }
                        myNet.feedForward(inputValues);
                        final double[] predictions = myNet.getResults().stream().mapToDouble(Double::doubleValue).toArray();
                        final int predictedLabel = TrainingData.getMaxIndex(predictions);
                        return predictedLabel == (int) targetVals[0];
                    }
            );
        }

        System.out.println();

        trainData.testPredictChunk(10_000, (inputVals, targetVals) -> {
            final List<Double> inputValues = new ArrayList<>();
            for (int n = 0; n < inputVals.length; n++) {
                // normalization
                // Scale the pixel values to the range [0,1]
                inputValues.add(inputVals[n] / 255);
            }
            myNet.feedForward(inputValues);
            final double[] predictions = myNet.getResults().stream().mapToDouble(Double::doubleValue).toArray();
            final int predictedLabel = TrainingData.getMaxIndex(predictions);
            return predictedLabel == (int) targetVals[0];
        });

        System.out.println("\nexecution time: " + (System.currentTimeMillis() - startTime) + "ms\n");
    }

}
