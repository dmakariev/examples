//usr/bin/env jbang "$0" "$@" ; exit $?
//SOURCES SimpleMLP.java
//SOURCES TrainingData.java
package com.makariev.examples.ai.neuralnet;

public class trainMLP_Mnist {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        // Example: Assuming the data has 784 inputs, 64 hidden neurons, and 10 output
        final SimpleMLP myMLP = new SimpleMLP(784, 64, 10);

        final TrainingData trainData = TrainingData.mnistTrainData();

        double initialLearningRate = 0.001;
        double decayRate = 0.9;  // e.g., reduce learning rate by 10%
        int decayStep = 2;  // e.g., reduce learning rate every 2 epochs

        // Train
        for (int epoch = 0; epoch < 10; epoch++) {
            if (epoch % decayStep == 0 && epoch != 0) {
                initialLearningRate *= decayRate;
            }
            final double learningRate = initialLearningRate;
            trainData.trainLine((inputVals, targetVals) -> {

                double[] input = inputVals;
                for (int n = 0; n < input.length; n++) {
                    // normalization
                    // Scale the pixel values to the range [0,1]
                    input[n] = input[n] / 255;
                }

                double[] target = new double[10];
                target[(int) targetVals[0]] = 1;  // One-hot encoding

                // Train the MLP with the current sample
                myMLP.train(input, target, learningRate);
                return true;
            });

            // Test and Calculate Accuracy for the Epoch
            trainData.testPredictChunk(
                    12_000,
                    "Epoch: %d, Learning Rate: %.6f, ".formatted(epoch, initialLearningRate),
                    (inputVals, targetVals) -> {
                        double[] input = inputVals;
                        for (int n = 0; n < input.length; n++) {
                            // normalization
                            // Scale the pixel values to the range [0,1]
                            input[n] = input[n] / 255;
                        }
                        final double[] predictions = myMLP.predict(input);
                        final int predictedLabel = TrainingData.getMaxIndex(predictions);
                        return predictedLabel == (int) targetVals[0];
                    }
            );
        }

        System.out.println();

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

        myMLP.saveModel((weights, biases) -> {
            TrainingData.saveTo("model/mlp_mnist.zip", weights, biases);
        });

    }

}
