//usr/bin/env jbang "$0" "$@" ; exit $?
//SOURCES SimpleMLP.java
//SOURCES TrainingData.java
package com.makariev.examples.ai.neuralnet;

public class trainMLP_Xor {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        // Example: Assuming the data has 2 inputs, 10 hidden neurons, and 1 output
        final SimpleMLP myMLP = new SimpleMLP(2, 10, 1);

        final TrainingData trainData = TrainingData.xorTrainData();
        trainData.trainLine((inputVals, targetVals) -> {
            // Train the MLP with the current sample
            double learningRate = 0.001;
            myMLP.train(inputVals, targetVals, learningRate);
            return true;
        });

        System.out.println();

        trainData.testPredictChunk(30, (inputVals, targetVals) -> {
            final double[] predictions = myMLP.predict(inputVals);
            return Math.round(predictions[0]) == targetVals[0];
        });

        System.out.println("\nexecution time: " + (System.currentTimeMillis() - startTime) + "ms\n");
    }

}
