//usr/bin/env jbang "$0" "$@" ; exit $?
//SOURCES SimpleELM.java
//SOURCES TrainingData.java
package com.makariev.examples.ai.neuralnet;

public class loadELM_Mnist {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        // Example: Assuming the data has 2 inputs, 10 hidden neurons, and 1 output
        final SimpleELM myELM = TrainingData.loadFrom("model/elm_mnist-2000.zip", inputWeights -> outputWeights -> biases -> {
            return new SimpleELM(inputWeights, outputWeights, biases);
        });

        final TrainingData trainData = TrainingData.mnistTrainData();

        trainData.testPredictChunk(12_000, (inputVals, targetVals) -> {
            for (int n = 0; n < inputVals.length; n++) {
                // normalization
                // Scale the pixel values to the range [0,1]
                inputVals[n] = inputVals[n] / 255;
            }
            final double[] predictions = myELM.predict(inputVals);
            final int predictedLabel = TrainingData.getMaxIndex(predictions);
            return predictedLabel == (int) targetVals[0];
        });

        System.out.println("\nexecution time: " + (System.currentTimeMillis() - startTime) + "ms\n");
    }

}
