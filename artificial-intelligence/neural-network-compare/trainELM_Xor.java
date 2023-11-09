//usr/bin/env jbang "$0" "$@" ; exit $?
//SOURCES SimpleELM.java
//SOURCES TrainingData.java
package com.makariev.examples.ai.neuralnet;

public class trainELM_Xor {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        // Example: Assuming the data has 2 inputs, 10 hidden neurons, and 1 output
        final SimpleELM myELM = new SimpleELM(2, 10, 1);

        final TrainingData trainData = TrainingData.xorTrainData();
        trainData.trainChunk(1000, true, (inputVals, targetVals) -> {
            // Train the ELM with the current sample
            myELM.train(inputVals, targetVals);
        });

        System.out.println();

        trainData.testPredictChunk(30, (inputVals, targetVals) -> {
            final double[] predictions = myELM.predict(inputVals);
            return Math.round(predictions[0]) == targetVals[0];
        });

        System.out.println("\nexecution time: " + (System.currentTimeMillis() - startTime) + "ms\n");
    }

}
