//DEPS org.apache.commons:commons-math3:3.6.1
//SOURCES ActivationFunction.java
package com.makariev.examples.ai.neuralnet;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class SimpleELM {

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

    public SimpleELM(double[][] inputWeights, double[][] outputWeights, double[][] biases) {
        final long startTime = System.currentTimeMillis();
        this.numInputNodes = inputWeights[0].length;
        this.numHiddenNodes = inputWeights.length;
        this.numOutputNodes = outputWeights.length;

        this.inputWeights = MatrixUtils.createRealMatrix(inputWeights);
        this.biases = MatrixUtils.createRealMatrix(biases);
        this.outputWeights = MatrixUtils.createRealMatrix(outputWeights);
        this.M = MatrixUtils.createRealIdentityMatrix(numHiddenNodes).scalarMultiply(1000);
        System.out.printf("finished loading in %dms\n", (System.currentTimeMillis() - startTime));
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
        RealMatrix betaUpdate = M.multiply(H).multiply(inverseK).multiply((T.subtract(outputWeights.multiply(H))).transpose());
        outputWeights = outputWeights.add(betaUpdate.transpose());
        M = M.subtract(M.multiply(H).multiply(inverseK).multiply(H.transpose()).multiply(M));
    }

    public double[] predict(double[] input) {
        RealMatrix X = MatrixUtils.createColumnRealMatrix(input);
        RealMatrix predicted = this.predict(X);
        return predicted.getColumn(0);
    }

    private RealMatrix predict(RealMatrix X) {
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

    public void saveModel(Function<double[][], Function<double[][], Consumer<double[][]>>> triConsumer) {

        triConsumer.apply(inputWeights.getData()).apply(outputWeights.getData()).accept(biases.getData());
    }

}
