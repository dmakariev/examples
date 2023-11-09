//DEPS org.apache.commons:commons-math3:3.6.1
//SOURCES ActivationFunction.java
package com.makariev.examples.ai.neuralnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class SimpleMLP {

    private final RealMatrix[] weights;
    private final RealMatrix[] biases;
    private final Random random = new Random();
    private static final double LAMBDA = 0.001; // L2 regularization factor

    private static final ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.leakyReLU();
    //private static final ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.ReLU();    
    //private final static ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.tanh();
    //private final static ActivationFunction ACTIVATION_FUNCTION = ActivationFunction.sigmoid();

    private static final UnivariateFunction FUNCTION = ACTIVATION_FUNCTION::function;
    private static final UnivariateFunction FUNCTION_DERIVATIVE = ACTIVATION_FUNCTION::functionDerivative;

    public SimpleMLP(int... layerSizes) {
        final long startTime = System.currentTimeMillis();
        this.weights = new RealMatrix[layerSizes.length - 1];
        this.biases = new RealMatrix[layerSizes.length - 1];

        for (int i = 0; i < layerSizes.length - 1; i++) {
            weights[i] = MatrixUtils.createRealMatrix(layerSizes[i + 1], layerSizes[i]);
            biases[i] = MatrixUtils.createColumnRealMatrix(new double[layerSizes[i + 1]]);
            double stddev = Math.sqrt(2.0 / layerSizes[i]); // Standard deviation for He initialization
            for (int row = 0; row < layerSizes[i + 1]; row++) {
                for (int col = 0; col < layerSizes[i]; col++) {
                    weights[i].setEntry(row, col, random.nextGaussian() * stddev);
                }
                biases[i].setEntry(row, 0, 0.0);  // Biases can be initialized to 0
            }
        }
        System.out.printf("finished initialization in %dms\n", (System.currentTimeMillis() - startTime));
    }

    public SimpleMLP(List<double[][]> weightsList, List<double[][]> biasesList) {
        final long startTime = System.currentTimeMillis();
        if (weightsList.size() != biasesList.size()) {
            throw new IllegalArgumentException("weights.size=" + weightsList.size() + " and biases.size=" + biasesList.size() + " should be the same ");
        }
        this.weights = new RealMatrix[weightsList.size()];
        this.biases = new RealMatrix[weightsList.size()];

        for (int n = 0; n < weightsList.size(); n++) {
            this.weights[n] = MatrixUtils.createRealMatrix(weightsList.get(n));
            this.biases[n] = MatrixUtils.createRealMatrix(biasesList.get(n));
        }
        System.out.printf("finished loading in %dms\n", (System.currentTimeMillis() - startTime));
    }

    public double[] predict(double[] input) {
        return feedforward(new ArrayRealVector(input)).toArray();
    }

    private RealVector feedforward(RealVector input) {
        RealVector a = input;
        for (int i = 0; i < weights.length; i++) {
            a = weights[i].operate(a).add(biases[i].getColumnVector(0));
            a.mapToSelf(FUNCTION);
        }
        return a;
    }

    public void train(double[] input, double[] target, double learningRate) {
        train(new ArrayRealVector(input), new ArrayRealVector(target), learningRate);
    }

    public void saveModel(BiConsumer<List<double[][]>, List<double[][]>> biConsumer) {
        final List<double[][]> weightsList = new ArrayList<>();
        for (RealMatrix weight : weights) {
            weightsList.add(weight.getData());
        }

        final List<double[][]> biasesList = new ArrayList<>();
        for (RealMatrix bias : biases) {
            biasesList.add(bias.getData());
        }

        biConsumer.accept(weightsList, biasesList);
    }

    private void train(RealVector input, RealVector target, double learningRate) {
        // Forward pass
        List<RealVector> activations = new ArrayList<>();
        activations.add(input);

        List<RealVector> zs = new ArrayList<>();

        RealVector a = input;
        for (int i = 0; i < weights.length; i++) {
            RealVector z = weights[i].operate(a).add(biases[i].getColumnVector(0));
            zs.add(z);
            a = z.map(FUNCTION);
            activations.add(a);
        }

        // Backward pass
        RealVector delta = activations.get(activations.size() - 1).subtract(target);
        for (int i = weights.length - 1; i >= 0; i--) {
            RealMatrix weightGradient = delta.outerProduct(activations.get(i));

            // L2 regularization
            RealMatrix l2Reg = weights[i].scalarMultiply(LAMBDA);

            weights[i] = weights[i].subtract(weightGradient.add(l2Reg).scalarMultiply(learningRate));
            biases[i] = biases[i].subtract(MatrixUtils.createColumnRealMatrix(delta.toArray()).scalarMultiply(learningRate));

            if (i > 0) {
                RealVector sp = zs.get(i - 1).map(FUNCTION_DERIVATIVE);
                delta = weights[i].transpose().operate(delta).ebeMultiply(sp);
            }
        }
    }
}
