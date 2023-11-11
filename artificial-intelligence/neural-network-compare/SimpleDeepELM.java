//DEPS org.apache.commons:commons-math3:3.6.1
//SOURCES ActivationFunction.java
package com.makariev.examples.ai.neuralnet;

import java.util.List;
import java.util.Random;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class SimpleDeepELM {

    private final int numInputNodes;
    private final int[] hiddenLayerSizes;
    private final int numOutputNodes;

    private final RealMatrix[] hiddenLayerWeights;
    private final RealMatrix[] hiddenLayerBiases;
    private RealMatrix outputWeights;

    private RealMatrix M;

    private static final Random random = new Random();
    private double lambda = 0.01; // Example value, needs to be tuned

    private static final ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.leakyReLU();
    //private static final ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.ReLU();
    //private final static ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.tanh();
    //private final static ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.sigmoid();

    public SimpleDeepELM(int numInputNodes, int[] hiddenLayerSizes, int numOutputNodes) {
        final long startTime = System.currentTimeMillis();
        this.numInputNodes = numInputNodes;
        this.hiddenLayerSizes = hiddenLayerSizes;
        this.numOutputNodes = numOutputNodes;

        this.hiddenLayerWeights = new RealMatrix[hiddenLayerSizes.length];
        this.hiddenLayerBiases = new RealMatrix[hiddenLayerSizes.length];

        // Initialize weights and biases for hidden layers
        int inputSize = numInputNodes;
        for (int i = 0; i < hiddenLayerSizes.length; i++) {
            int layerSize = hiddenLayerSizes[i];
            this.hiddenLayerWeights[i] = createRandomMatrix(layerSize, inputSize, -1.0, 1.0);
            this.hiddenLayerBiases[i] = createRandomMatrix(layerSize, 1, -1.0, 1.0);
            inputSize = layerSize;
        }

        // Initialize output weights
        this.outputWeights = MatrixUtils.createRealMatrix(numOutputNodes, hiddenLayerSizes[hiddenLayerSizes.length - 1]);

        // Initialize M matrix
        int lastHiddenLayerSize = hiddenLayerSizes[hiddenLayerSizes.length - 1];
        this.M = MatrixUtils.createRealIdentityMatrix(lastHiddenLayerSize).scalarMultiply(1000);

        System.out.printf("finished initialization in %dms\n", (System.currentTimeMillis() - startTime));
    }

    private static RealMatrix createRandomMatrix(int rows, int cols, double lowerBound, double upperBound) {
        return createRandomMatrixHe(rows, cols);
        //return createRandomMatrixOld(rows, cols, lowerBound, upperBound);
    }

    private static RealMatrix createRandomMatrixOld(int rows, int cols, double lowerBound, double upperBound) {
        double[][] data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = lowerBound + (upperBound - lowerBound) * random.nextDouble();
            }
        }
        return MatrixUtils.createRealMatrix(data);
    }

    private static RealMatrix createRandomMatrixHe(int rows, int cols) {
        double[][] data = new double[rows][cols];
        double stddev = Math.sqrt(2.0 / cols); // He initialization standard deviation

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = stddev * random.nextGaussian(); // Normal distribution with calculated stddev
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

//    private void train(RealMatrix X, RealMatrix T) {
//        RealMatrix H = activationFunction(inputWeights.multiply(X).add(repeatColumn(biases, X.getColumnDimension())));
//        RealMatrix K = H.transpose().multiply(M).multiply(H).add(MatrixUtils.createRealIdentityMatrix(H.getColumnDimension()));
//        RealMatrix inverseK = new LUDecomposition(K).getSolver().getInverse();
//
//        RealMatrix temp = M.multiply(H).multiply(inverseK);
//
//        RealMatrix betaUpdate = temp.multiply((T.subtract(outputWeights.multiply(H))).transpose());
//        outputWeights = outputWeights.add(betaUpdate.transpose());
//        M = M.subtract(temp.multiply(H.transpose()).multiply(M));
//    }
//    private void train(RealMatrix X, RealMatrix T) {
//        // Forward pass through each hidden layer
//        RealMatrix H = X;
//        for (int i = 0; i < hiddenLayerSizes.length; i++) {
//            H = activationFunction(hiddenLayerWeights[i].multiply(H).add(repeatColumn(hiddenLayerBiases[i], H.getColumnDimension())));
//        }
//        Objects.requireNonNull(M, "MMMMMIIIIIII");
//
//        // Now H is the output of the last hidden layer
//        RealMatrix K = H.transpose().multiply(M).multiply(H).add(MatrixUtils.createRealIdentityMatrix(H.getColumnDimension()));
//        RealMatrix inverseK = new LUDecomposition(K).getSolver().getInverse();
//
//        RealMatrix temp = M.multiply(H).multiply(inverseK);
//
//        RealMatrix betaUpdate = temp.multiply((T.subtract(outputWeights.multiply(H))).transpose());
//        outputWeights = outputWeights.add(betaUpdate.transpose());
//        M = M.subtract(temp.multiply(H.transpose()).multiply(M));
//    }
    private void train(RealMatrix X, RealMatrix T) {
        // Forward pass through each hidden layer
        RealMatrix H = X;
        for (int i = 0; i < hiddenLayerSizes.length; i++) {
            H = activationFunction(hiddenLayerWeights[i].multiply(H).add(repeatColumn(hiddenLayerBiases[i], H.getColumnDimension())));
        }

        // L2 Regularization: Add lambda * Identity matrix
        RealMatrix regularizationMatrix = MatrixUtils.createRealIdentityMatrix(H.getColumnDimension()).scalarMultiply(lambda);

        RealMatrix K = H.transpose().multiply(M).multiply(H).add(regularizationMatrix);
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

//    private RealMatrix predict(RealMatrix X) {
//        RealMatrix H = activationFunction(inputWeights.multiply(X).add(biases));
//        return outputWeights.multiply(H);
//    }
    private RealMatrix predict(RealMatrix X) {
        // Forward pass through each hidden layer
        RealMatrix H = X;
        for (int i = 0; i < hiddenLayerSizes.length; i++) {
            H = activationFunction(hiddenLayerWeights[i].multiply(H).add(repeatColumn(hiddenLayerBiases[i], H.getColumnDimension())));
        }

        // Now H is the output of the last hidden layer, use it with output weights for prediction
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

}
