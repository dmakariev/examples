//usr/bin/env jbang "$0" "$@" ; exit $?
//SOURCES SimpleMLPBatch.java
//SOURCES TrainingData.java
package com.makariev.examples.ai.neuralnet;

public class loadMLP_batch_Mnist {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final TrainingData trainData = TrainingData.mnistTrainData();

        //1500, 848sec (~14min), 98.20%
        // Example: Assuming the data has 784 inputs, 64 hidden neurons, and 10 output
        //784-2500-2000-1500-1000-500-10
        final SimpleMLPBatch myMLP = TrainingData.loadFrom("model/mlp_batch_mnist-400-30x.zip", (weights, biases) -> {
            return new SimpleMLPBatch(weights, biases);
        });

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

    }

}
