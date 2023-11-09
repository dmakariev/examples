//usr/bin/env jbang "$0" "$@" ; exit $?
//SOURCES NeuralNetTutorial.java
//SOURCES TrainingData.java
package com.makariev.examples.ai.neuralnet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

public class trainNNT_Xor {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        // Example: Assuming the data has 2 inputs, 10 hidden neurons, and 1 output
        final NeuralNetTutorial.Net myNet = new NeuralNetTutorial.Net(Arrays.asList(2, 10, 1));

        final TrainingData trainData = TrainingData.xorTrainData();
        trainData.trainLine((inputVals, targetVals) -> {
            final List<Double> inputValues = DoubleStream.of(inputVals).boxed().toList();
            // Train the NNT with the current sample
            myNet.feedForward(inputValues);
            myNet.backProp(List.of(targetVals[0]));
            return true;
        });

        System.out.println();

        trainData.testPredictChunk(30, (inputVals, targetVals) -> {
            final List<Double> inputValues = DoubleStream.of(inputVals).boxed().toList();
            myNet.feedForward(inputValues);
            final double[] predictions = myNet.getResults().stream().mapToDouble(Double::doubleValue).toArray();
            return Math.round(predictions[0]) == targetVals[0];
        });

        System.out.println("\nexecution time: " + (System.currentTimeMillis() - startTime) + "ms\n");
    }

}
