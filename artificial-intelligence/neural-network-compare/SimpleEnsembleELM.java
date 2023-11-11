//DEPS org.apache.commons:commons-math3:3.6.1
//SOURCES ActivationFunction.java
package com.makariev.examples.ai.neuralnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class SimpleEnsembleELM {

    static class SimpleELM {

        private final int numInputNodes;
        private final int numHiddenNodes;
        private final int numOutputNodes;

        private final RealMatrix inputWeights;
        private RealMatrix outputWeights;
        private final RealMatrix biases;

        private RealMatrix M;

        private static final Random random = new Random();

        private static final ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.leakyReLU();
        //private static final ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.ReLU();
        //private final static ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.tanh();
        //private final static ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.sigmoid();

        public SimpleELM(int numInputNodes, int numHiddenNodes, int numOutputNodes) {
            final long startTime = System.currentTimeMillis();
            this.numInputNodes = numInputNodes;
            this.numHiddenNodes = numHiddenNodes;
            this.numOutputNodes = numOutputNodes;

            this.inputWeights = createRandomMatrix(numHiddenNodes, numInputNodes, -1.0, 1.0);
            this.biases = createRandomMatrix(numHiddenNodes, 1, -1.0, 1.0);
            this.outputWeights = MatrixUtils.createRealMatrix(numOutputNodes, numHiddenNodes);
            this.M = MatrixUtils.createRealIdentityMatrix(numHiddenNodes).scalarMultiply(1000);
            System.out.printf("finished initialization in %dms\n", (System.currentTimeMillis() - startTime));
        }

        private static RealMatrix createRandomMatrix(int rows, int cols, double lowerBound, double upperBound) {
            double[][] data = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    data[i][j] = lowerBound + (upperBound - lowerBound) * random.nextDouble();
                }
            }
            return MatrixUtils.createRealMatrix(data);
        }

        private RealMatrix activationFunction(RealMatrix matrix) {
            double[][] data = matrix.getData();
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                for (int j = 0; j < matrix.getColumnDimension(); j++) {
                    data[i][j] = ACTIVATION_FUNCTION.function(data[i][j]);
                }
            }
            return MatrixUtils.createRealMatrix(data);
        }

        public void train(List<double[]> inputBatch, List<double[]> targetBatch) {
            if (inputBatch.size() != targetBatch.size()) {
                throw new IllegalStateException("inputBatch and targetBatch should have the same size");
            }
            if (inputBatch.isEmpty()) {
                throw new IllegalStateException("inputBatch and targetBatch should not be empty");
            }
            final double[][] inputMatrix = new double[numInputNodes][inputBatch.size()];
            final double[][] targetMatrix = new double[numOutputNodes][inputBatch.size()];

            for (int i = 0; i < inputBatch.size(); i++) {
                final double[] input = inputBatch.get(i);
                final double[] target = targetBatch.get(i);
                if (input.length != numInputNodes || target.length != numOutputNodes) {
                    throw new IllegalArgumentException(
                            "Wrong number of element for training!"
                            + "\n input.length=" + input.length + " , but should have been " + numInputNodes
                            + "\ntarget.length=" + target.length + " ,but should have been " + numOutputNodes
                    );
                }

                for (int j = 0; j < numInputNodes; j++) {
                    inputMatrix[j][i] = input[j];
                }

                for (int j = 0; j < numOutputNodes; j++) {
                    targetMatrix[j][i] = target[j];
                }
            }
            final RealMatrix Xs = MatrixUtils.createRealMatrix(inputMatrix);
            final RealMatrix Ts = MatrixUtils.createRealMatrix(targetMatrix);
            train(Xs, Ts);
        }

        private void train(RealMatrix X, RealMatrix T) {
            RealMatrix H = activationFunction(inputWeights.multiply(X).add(repeatColumn(biases, X.getColumnDimension())));
            RealMatrix K = H.transpose().multiply(M).multiply(H).add(MatrixUtils.createRealIdentityMatrix(H.getColumnDimension()));
            RealMatrix inverseK = new LUDecomposition(K).getSolver().getInverse();

            RealMatrix temp = M.multiply(H).multiply(inverseK);

            RealMatrix betaUpdate = temp.multiply((T.subtract(outputWeights.multiply(H))).transpose());
            outputWeights = outputWeights.add(betaUpdate.transpose());
            M = M.subtract(temp.multiply(H.transpose()).multiply(M));

        }

        public double[] predict(double[] input) {
            RealMatrix X = MatrixUtils.createColumnRealMatrix(input);
            RealMatrix predicted = this.predict(X);
            return predicted.getColumn(0);
        }

        public RealMatrix predict(RealMatrix X) {
            RealMatrix H = activationFunction(inputWeights.multiply(X).add(biases));
            return outputWeights.multiply(H);
        }

        private RealMatrix repeatColumn(RealMatrix matrix, int times) {
            double[][] data = new double[matrix.getRowDimension()][matrix.getColumnDimension() * times];
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                for (int j = 0; j < matrix.getColumnDimension() * times; j++) {
                    data[i][j] = matrix.getEntry(i, j % matrix.getColumnDimension());
                }
            }
            return MatrixUtils.createRealMatrix(data);
        }

        // Method for multi-class classification prediction
        public int predictForMultiClassClassification(double[] input) {
            RealMatrix inputMatrix = MatrixUtils.createColumnRealMatrix(input);
            RealMatrix outputMatrix = predict(inputMatrix);

            return getMaxIndex(outputMatrix.getColumn(0));
        }

        private int getMaxIndex(double[] array) {
            int maxIndex = 0;
            for (int i = 1; i < array.length; i++) {
                if (array[i] > array[maxIndex]) {
                    maxIndex = i;
                }
            }
            return maxIndex;
        }

    }

    static class EnsembleELM {

        private List<SimpleELM> elmModels;

        public EnsembleELM(int numberOfELM, int numInputNodes, int numHiddenNodes, int numOutputNodes) {
            this.elmModels = new ArrayList<>();
            for (int i = 0; i < numberOfELM; i++) {
                elmModels.add(new SimpleELM(numInputNodes, numHiddenNodes, numOutputNodes));

            }
        }
        
        public EnsembleELM(List<SimpleELM> elms) {
            this.elmModels = elms;
        }

//        public void addELM(SimpleELM elm) {
//            elmModels.add(elm);
//        }
        public void trainAllModels(List<double[]> trainingDataList, List<double[]> trainingLabelsList) {
            if (trainingDataList.size() != trainingLabelsList.size()) {
                throw new IllegalArgumentException("Training data and labels must have the same number of instances");
            }

            double[][] trainingDataArray = trainingDataList.toArray(new double[0][]);
            double[][] trainingLabelsArray = trainingLabelsList.toArray(new double[0][]);

            final RealMatrix trainingDataMatrix = MatrixUtils.createRealMatrix(trainingDataArray).transpose();
            final RealMatrix trainingLabelsMatrix = MatrixUtils.createRealMatrix(trainingLabelsArray).transpose();

            final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
            for (SimpleELM elm : elmModels) {
                executorService.submit(() -> {
                    elm.train(trainingDataMatrix, trainingLabelsMatrix);
                });
            }

            // Wait for all threads to finish processing the current mini-batch
            try {
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        public double[] predict(RealMatrix testData) {
//            RealMatrix predictions = new Array2DRowRealMatrix(testData.getRowDimension(), elmModels.size());
//
//            for (int i = 0; i < elmModels.size(); i++) {
//                predictions.setColumn(i, elmModels.get(i).predict(testData));
//            }
//
//            // For regression, average the predictions
//            double[] averagedPredictions = new double[testData.getRowDimension()];
//            for (int i = 0; i < testData.getRowDimension(); i++) {
//                averagedPredictions[i] = predictions.getRowVector(i).getL1Norm() / elmModels.size();
//            }
//
//            return averagedPredictions;
//            // For classification, implement majority voting or another strategy
//        }
        public int predictForClassification(double[] testData) {
            // Array to hold the votes for each model
            int[] votes = new int[elmModels.size()];

            final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
            // Collect predictions (votes) from each ELM model
            for (int i = 0; i < elmModels.size(); i++) {
                final int index = i;
                executorService.submit(() -> {
                    SimpleELM elm = elmModels.get(index);
                    int prediction = elm.predictForMultiClassClassification(testData);
                    votes[index] = prediction;
                });
            }

            try {
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Perform majority voting
            return majorityVote(votes);
        }

        private int majorityVote(int[] votes) {
            Map<Integer, Integer> voteCount = new HashMap<>();
            for (int vote : votes) {
                voteCount.put(vote, voteCount.getOrDefault(vote, 0) + 1);
            }

            int majorityClass = -1;
            int maxCount = 0;
            for (Map.Entry<Integer, Integer> entry : voteCount.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    majorityClass = entry.getKey();
                }
            }

            return majorityClass;
        }

        // Other methods as needed...
    }

// Usage example
// EnsembleELM ensemble = new EnsembleELM();
// ensemble.addELM(new ELM(...));
// ensemble.addELM(new ELM(...));
// ... Add more ELMs as needed
// ensemble.trainAllModels(trainingData, trainingLabels);
// double[] predictions = ensemble.predict(testData);
}
