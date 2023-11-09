//usr/bin/env jbang "$0" "$@" ; exit $?
//SOURCES SimpleMLP.java
//SOURCES TrainingData.java
package com.makariev.examples.ai.neuralnet;

public class loadMLP_Mnist {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final SimpleMLP myMLP = TrainingData.loadFrom("model/mlp_batch_mnist-400-30x.zip", (weights, biases) -> {
            return new SimpleMLP(weights, biases);
        });

        // Example: Assuming the data has 784 inputs, 64 hidden neurons, and 10 output
        final TrainingData trainData = TrainingData.mnistTrainData();

        // Test and Calculate Accuracy 
        trainData.testPredictChunk(12_000, (inputVals, targetVals) -> {
            double[] input = inputVals;
            for (int n = 0; n < input.length; n++) {
                // normalization
                // Scale the pixel values to the range [0,1]
                input[n] = input[n] / 255;
            }
            final double[] predictions = myMLP.predict(input);
            final int predictedLabel = TrainingData.getMaxIndex(predictions);
            return predictedLabel == (int) targetVals[0];
        });

        System.out.println("\nexecution time: " + (System.currentTimeMillis() - startTime) + "ms\n");
    }

}
