//usr/bin/env jbang "$0" "$@" ; exit $?
//SOURCES SimpleDeepELM.java
//SOURCES TrainingData.java
package com.makariev.examples.ai.neuralnet;

import java.util.ArrayList;
import java.util.List;

public class trainDeepELM_Mnist {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        // Example: Assuming the data has 784 inputs, 10 hidden neurons, and 1 output
        final SimpleDeepELM myELM = new SimpleDeepELM(784, new int[]{100, 400}, 10);

        //263sec, 400, 1000, 94.29% 
        //266sec, 300, 1000, 94.21%
        //284sec, 200, 1000, 94.43%
        //223sec, 100,  800, 93.71%
        //175sec, 200,  800, 93.56%
        //171sec, 300,  800, 93.54%
        //175sec, 400,  800, 93.60%
        //133sec, 300,  700, 93.32%
        //133sec, 200,  700, 92.96%
        //162sec, 100,  700, 93.43%
        //142sec, 400,  700, 93.00%
        //103sec, 300,  600, 92.94%
        // 14sec, 300,  100, 82.00%
        //  9sec,  40,  100, 82.17%
        // 17sec,  40,  200, 87.57%
        // 37sec,  40,  300, 89.94%
        // 69sec,  40,  400, 91.15%
        // 50sec, 100,  400, 90.85%
        // 57sec, 300,  400, 91.23%
        // 50sec, 200,  400, 90.95%
        // 53sec,  80,  400, 91.64%
        final TrainingData trainData = TrainingData.mnistTrainData();
        trainData.trainChunk(400, true, (inputVals, targetVals) -> {

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
                target[(int) targetVals.get(i)[0]] = 1;  // One-hot encoding
                targetBatch.add(target);
            }

            // Train the ELM with the current sample
            myELM.train(inputBatch, targetBatch);
        });

        System.out.println();

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

        System.out.println();

//        myELM.saveModel(inputWeights -> outputWeights -> biases -> {
//            TrainingData.saveTo("model/elm_mnist-1000.zip", inputWeights, outputWeights, biases);
//        });
        System.out.println("\nexecution time: " + (System.currentTimeMillis() - startTime) + "ms\n");
    }

}
