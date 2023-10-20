//usr/bin/env jbang "$0" "$@" ; exit $?
package com.makariev.examples.ai.neuralnet;

import java.util.ArrayList;
import java.util.Random;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

//translated and adapted by Dimitar Makariev https://makariev.com
//
// this is based on the C++ implementation by David Miller, http://millermattson.com/dave
// neural-net-tutorial.cpp
// here is a link to the original article https://millermattson.com/dave/?p=54 
// 'Neural Net in C++ Tutorial' from David Miller on Vimeo.
// See the original associated video for instructions: http://vimeo.com/19569529
// There is a second video 'The Care and Training of Your Backpropagation Neural Net.'
// it's available here : http://vimeo.com/technotes/neural-net-care-and-training
public class NeuralNetTutorial {

    // Silly class to read training data from a text file -- Replace This.
    // Replace class TrainingData with whatever you need to get input data into the
    // program, e.g., connect to a database, or take a stream of data from stdin, or
    // from a file specified by a command line argument, etc.
    static class TrainingData {

        private final String fileName;
        private final List<Integer> topology;

        public TrainingData(String fileName) {
            this.fileName = fileName;
            this.topology = readTopology(fileName);
        }

        public List<Integer> getTopology() {
            return this.topology;
        }

        @FunctionalInterface
        public interface TrainingDataConsumer {

            void accept(int trainingPass, List<Double> inputVals, List<Double> targetVals);
        }

        public void apply(TrainingDataConsumer consumer) {
            try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
                validateTopologyLine(br.readLine()); //reading and skipping the first line 
                int trainingPass = 0;
                while (br.ready()) {
                    ++trainingPass;
                    final List<Double> inputVals = parseDoubleLine("in:", br.readLine(), trainingPass);
                    final List<Double> targetVals = parseDoubleLine("out:", br.readLine(), trainingPass);

                    consumer.accept(trainingPass, inputVals, targetVals);
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException("Unable to process training data, exception accured:", ex);
            }
        }

        private static List<Integer> readTopology(String fileName) {
            try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
                final String line = validateTopologyLine(br.readLine());
                return Arrays.asList(line.split(" ")).stream().skip(1).map(Integer::parseInt).toList();
            } catch (Exception ex) {
                throw new IllegalArgumentException("Expected topology data, but exception accured:", ex);
            }
        }

        private static String validateTopologyLine(final String line) {
            if (line == null || !line.startsWith("topology:")) {
                throw new IllegalArgumentException("Expected topology data, but found '" + line + "'");
            }
            return line;
        }

        private static List<Double> parseDoubleLine(final String prefix, final String line, int trainingPass) {
            if (line == null || !line.startsWith(prefix)) {
                throw new IllegalArgumentException("Expected '" + prefix + "' data, but found '" + line + "' on trainingPass=" + trainingPass);
            }

            return Arrays.asList(line.split(" ")).stream().skip(1).map(Double::parseDouble).toList();
        }
    }

    static class Connection {

        double weight;
        double deltaWeight;

        public Connection(double weight, double deltaWeight) {
            this.weight = weight;
            this.deltaWeight = deltaWeight;
        }
    }

    // ****************** class Neuron ******************
    static class Neuron {

        private static final double ETA = 0.15;    // overall net learning rate, [0.0..1.0]
        private static final double ALPHA = 0.5;   // momentum, multiplier of last deltaWeight, [0.0..1.0]
        private static final Random rand = new Random();

        private final List<Connection> outputWeights;
        private final int myIndex;
        private double outputVal;
        private double gradient;

        public Neuron(int numOutputs, int myIndex) {
            this.outputWeights = new ArrayList<>();
            this.myIndex = myIndex;

            for (int c = 0; c < numOutputs; ++c) {
                outputWeights.add(new Connection(randomWeight(), 0));
            }
        }

        public void setOutputVal(double val) {
            outputVal = val;
        }

        public double getOutputVal() {
            return outputVal;
        }

        public void feedForward(List<Neuron> prevLayer) {
            double sum = 0.0;

            // Sum the previous layer's outputs (which are our inputs)
            // Include the bias node from the previous layer.
            for (Neuron neuron : prevLayer) {
                sum += neuron.getOutputVal()
                        * neuron.outputWeights.get(myIndex).weight;
            }

            outputVal = transferFunction(sum);
        }

        public void calcOutputGradients(double targetVal) {
            double delta = targetVal - outputVal;
            gradient = delta * transferFunctionDerivative(outputVal);
        }

        public void calcHiddenGradients(List<Neuron> nextLayer) {
            double dow = sumDOW(nextLayer);
            gradient = dow * transferFunctionDerivative(outputVal);
        }

        public void updateInputWeights(List<Neuron> prevLayer) {

            // The weights to be updated are in the Connection container
            // in the neurons in the preceding layer
            for (Neuron neuron : prevLayer) {
                double oldDeltaWeight = neuron.outputWeights.get(myIndex).deltaWeight;

                double newDeltaWeight
                        = // Individual input, magnified by the gradient and train rate:
                        ETA
                        * neuron.getOutputVal()
                        * gradient
                        // Also add momentum = a fraction of the previous delta weight;
                        + ALPHA
                        * oldDeltaWeight;

                neuron.outputWeights.get(myIndex).deltaWeight = newDeltaWeight;
                neuron.outputWeights.get(myIndex).weight += newDeltaWeight;
            }
        }

        private static double transferFunction(double x) {
            // tanh - output range [-1.0..1.0]
            //return Math.tanh(x); //hyperbolic tangent activation function
            return (x > 0) ? x : x * 0.01; //Leaky rectified linear unit (Leaky ReLU) activation function
        }

        private static double transferFunctionDerivative(double x) {
            // tanh derivative
            //return 1.0 - x * x; //hyperbolic tangent function derivative
            return (x > 0) ? 1.0 : 0.01; //Leaky rectified linear unit (Leaky ReLU)
        }

        private static double randomWeight() {
            return rand.nextDouble();
        }

        private double sumDOW(List<Neuron> nextLayer) {
            double sum = 0.0;

            // Sum our contributions of the errors at the nodes we feed.
            for (int n = 0; n < nextLayer.size() - 1; ++n) {
                sum += outputWeights.get(n).weight * nextLayer.get(n).gradient;
            }

            return sum;
        }
    }

    // ****************** class Net ******************
    static class Net {

        private final List<List<Neuron>> layers; // m_layers[layerNum][neuronNum]
        private double error;
        private double recentAverageError;

        // Number of training samples to average over
        private static final double RECENT_AVERAGE_SMOOTHING_FACTOR = 100.0;

        public Net(List<Integer> topology) {
            layers = new ArrayList<>();

            for (int layerNum = 0; layerNum < topology.size(); ++layerNum) {
                layers.add(new ArrayList<>());
                int numOutputs = (layerNum == topology.size() - 1) ? 0 : topology.get(layerNum + 1);

                // We have a new layer, now fill it with neurons, and
                // add a bias neuron in each layer. ( pay attention to '<=' )
                for (int neuronNum = 0; neuronNum <= topology.get(layerNum); ++neuronNum) {
                    layers.get(layerNum).add(new Neuron(numOutputs, neuronNum));
                    System.out.println("Made a Neuron!");
                }

                // Force the bias node's output to 1.0 (it was the last neuron pushed in this layer):
                layers.get(layerNum).get(topology.get(layerNum)).setOutputVal(1.0);
            }
        }

        public void feedForward(List<Double> inputVals) {

            if (inputVals.size() != layers.get(0).size() - 1) {
                throw new IllegalStateException();
            }

            // Assign (latch) the input values into the input neurons
            for (int i = 0; i < inputVals.size(); ++i) {
                layers.get(0).get(i).setOutputVal(inputVals.get(i));
            }

            // forward propagate
            for (int layerNum = 1; layerNum < layers.size(); ++layerNum) {
                final List<Neuron> prevLayer = layers.get(layerNum - 1);
                for (int n = 0; n < layers.get(layerNum).size() - 1; ++n) {
                    layers.get(layerNum).get(n).feedForward(prevLayer);
                }
            }
        }

        public void backProp(List<Double> targetVals) {

            // Calculate overall net error (RMS of output neuron errors)
            final List<Neuron> outputLayer = layers.get(layers.size() - 1);
            error = 0.0;

            for (int n = 0; n < outputLayer.size() - 1; ++n) {
                double delta = targetVals.get(n) - outputLayer.get(n).getOutputVal();
                error += delta * delta;
            }

            error /= outputLayer.size() - 1; // get average error squared
            error = Math.sqrt(error); // RMS

            // Implement a recent average measurement
            recentAverageError
                    = (recentAverageError * RECENT_AVERAGE_SMOOTHING_FACTOR + error)
                    / (RECENT_AVERAGE_SMOOTHING_FACTOR + 1.0);

            // Calculate output layer gradients
            for (int n = 0; n < outputLayer.size() - 1; ++n) {
                outputLayer.get(n).calcOutputGradients(targetVals.get(n));
            }

            // Calculate hidden layer gradients
            for (int layerNum = layers.size() - 2; layerNum > 0; --layerNum) {
                final List<Neuron> hiddenLayer = layers.get(layerNum);
                final List<Neuron> nextLayer = layers.get(layerNum + 1);

                for (Neuron neuron : hiddenLayer) {
                    neuron.calcHiddenGradients(nextLayer);
                }
            }

            // For all layers from outputs to first hidden layer,
            // update connection weights
            for (int layerNum = layers.size() - 1; layerNum > 0; --layerNum) {
                final List<Neuron> thisLayer = layers.get(layerNum);
                final List<Neuron> prevLayer = layers.get(layerNum - 1);

                for (int n = 0; n < thisLayer.size() - 1; ++n) {
                    thisLayer.get(n).updateInputWeights(prevLayer);
                }
            }
        }

        public List<Double> getResults() {
            final List<Double> resultVals = new ArrayList<>();

            for (int n = 0; n < layers.get(layers.size() - 1).size() - 1; ++n) {
                resultVals.add(layers.get(layers.size() - 1).get(n).getOutputVal());
            }
            return resultVals;
        }

        public double getRecentAverageError() {
            return recentAverageError;
        }
    }

    public static void showVectorVals(String label, List<Double> v) {
        System.out.print(label + " ");
        for (Double val : v) {
            System.out.print((double) Math.round(val * 100_000) / 100_000 + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        final TrainingData trainData = new TrainingData("simple_test.txt");
        // topology e.g., { 3, 2, 1 }
        final Net myNet = new Net(trainData.getTopology());

        System.out.println();

        //iterating over the training data
        trainData.apply((trainingPass, inputVals, targetVals) -> {
            System.out.print("Pass " + trainingPass);

            // Get new input data and feed it forward:
            showVectorVals(": Inputs:", inputVals);
            myNet.feedForward(inputVals);

            // Collect the net's actual output results:
            final List<Double> resultVals = myNet.getResults();
            showVectorVals("Outputs:", resultVals);

            // Train the net what the outputs should have been:
            showVectorVals("Targets:", targetVals);
            myNet.backProp(targetVals);

            // Report how well the training is working, average over recent samples:
            System.out.println("Net recent average error: "
                    + ((double) Math.round(myNet.getRecentAverageError() * 10_000) / 10_000)
                    + "\n");
        });

        System.out.println("execution time: " + (System.currentTimeMillis() - startTime));
    }
}
