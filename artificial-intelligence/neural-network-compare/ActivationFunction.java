package com.makariev.examples.ai.neuralnet;

public interface ActivationFunction {

    double function(double x);

    double functionDerivative(double x);

    static ActivationFunction sigmoid() {
        return new Sigmoid();
    }

    static ActivationFunction tanh() {
        return new Tanh();
    }

    static ActivationFunction leakyReLU() {
        return new LeakyReLU();
    }

    static ActivationFunction ReLU() {
        return new ReLU();
    }

    static class Sigmoid implements ActivationFunction {

        @Override
        public double function(double x) {
            return 1 / (1 + Math.exp(-x));
        }

        @Override
        public double functionDerivative(double x) {
            return function(x) * (1 - function(x));
        }

    }

    static class Tanh implements ActivationFunction {

        @Override
        public double function(double x) {
            return Math.tanh(x);
        }

        @Override
        public double functionDerivative(double x) {
            return 1.0 - Math.tanh(x) * Math.tanh(x);
        }

    }

    static class LeakyReLU implements ActivationFunction {

        private static final double ALPHA = 0.01;  // Leaky factor

        @Override
        public double function(double x) {
            return x > 0 ? x : ALPHA * x;
        }

        @Override
        public double functionDerivative(double x) {
            return x > 0 ? 1 : ALPHA;
        }

    }

    static class ReLU implements ActivationFunction {

        @Override
        public double function(double x) {
            return Math.max(0, x);
        }

        @Override
        public double functionDerivative(double x) {
            return x > 0 ? 1 : 0;
        }

    }
}
