package com.makariev.examples.ai.neuralnet;

import java.util.Arrays;
import java.util.List;

public class FeatureNormalizer {

    private final NormalizationParameters params;

    public static FeatureNormalizer minMaxNormalizer(int numberOfFeatures) {
        return new FeatureNormalizer(new MinMaxNormalizationParameters(numberOfFeatures));
    }

    public static FeatureNormalizer standardNormalizer(int numberOfFeatures) {
        return new FeatureNormalizer(new ZScoreNormalizationParameters(numberOfFeatures));
    }

    private FeatureNormalizer(NormalizationParameters params) {
        this.params = params;
    }

    //single row at a time
    public void updateStatistics(double[] featureValues) {
        this.params.updateStatistics(featureValues);
    }

    public void updateStatistics(List<double[]> featureValues) {
        featureValues.stream().forEach(this.params::updateStatistics);
    }

    public List<double[]> normalize(List<double[]> features) {
        return features.stream().map(this::normalize).toList();
    }

    public double[] normalize(double[] features) {
        double[] normalized = new double[features.length];
        for (int i = 0; i < features.length; i++) {
            normalized[i] = params.normalize(i, features[i]);
        }
        return normalized;
    }

    // Denormalize if needed (e.g., to convert predictions back to original scale)
    public double[] denormalize(double[] normalizedFeatures) {
        double[] original = new double[normalizedFeatures.length];
        for (int i = 0; i < normalizedFeatures.length; i++) {
            original[i] = params.denormalize(i, normalizedFeatures[i]);
        }
        return original;
    }

    interface NormalizationParameters {

        //could work like online normalizer, because it updates the stats on every call
        public void updateStatistics(double[] featureValues);

        public double normalize(int featureIndex, double featureValue);

        // Denormalize if needed (e.g., to convert predictions back to original scale)
        public double denormalize(int featureIndex, double featureValue);
        //save method, accepting lambda

    }

    static class MinMaxNormalizationParameters implements NormalizationParameters {

        private final double[] minValues;
        private final double[] maxValues;

        public MinMaxNormalizationParameters(int numberOfFeatures) {
            this.minValues = new double[numberOfFeatures];
            this.maxValues = new double[numberOfFeatures];
            Arrays.fill(minValues, Double.MAX_VALUE);
            Arrays.fill(maxValues, -Double.MAX_VALUE);
        }

        @Override
        public void updateStatistics(double[] featureValues) {
            for (int i = 0; i < featureValues.length; i++) {
                if (featureValues[i] < minValues[i]) {
                    minValues[i] = featureValues[i];
                }
                if (featureValues[i] > maxValues[i]) {
                    maxValues[i] = featureValues[i];
                }
            }
        }

        @Override
        public double normalize(int featureIndex, double featureValue) {
            return (featureValue - this.minValues[featureIndex])
                    / (this.maxValues[featureIndex] - this.minValues[featureIndex]);
        }

        // Denormalize if needed (e.g., to convert predictions back to original scale)
        @Override
        public double denormalize(int featureIndex, double normalizedFeatureValue) {
            return normalizedFeatureValue * (this.maxValues[featureIndex] - this.minValues[featureIndex])
                    + this.minValues[featureIndex];
        }
    }

    static class ZScoreNormalizationParameters implements NormalizationParameters {

        private final double[] meanValues;
        private final double[] stdValues;
        private int count;

        public ZScoreNormalizationParameters(int numberOfFeatures) {
            this.meanValues = new double[numberOfFeatures];
            this.stdValues = new double[numberOfFeatures];
            this.count = 0;
        }

        @Override
        public double normalize(int featureIndex, double featureValue) {
            return (featureValue - this.meanValues[featureIndex]) / this.stdValues[featureIndex];
        }

        // Denormalize if needed (e.g., to convert predictions back to original scale)
        @Override
        public double denormalize(int featureIndex, double normalizedFeatureValue) {
            return normalizedFeatureValue * this.stdValues[featureIndex] + this.meanValues[featureIndex];
        }

        @Override
        public void updateStatistics(double[] featureValues) {
            // Incremental update of mean and variance (using Welford's algorithm for numerical stability)
            count++;
            for (int i = 0; i < featureValues.length; i++) {
                double delta = featureValues[i] - meanValues[i];
                meanValues[i] += delta / count;
                stdValues[i] += delta * (featureValues[i] - meanValues[i]);
            }
            finalizeStatistics();
        }

        private void finalizeStatistics() {
            // Finalize the calculation of the standard deviation
            for (int i = 0; i < stdValues.length; i++) {
                stdValues[i] = Math.sqrt(stdValues[i] / (count - 1));
            }
        }

    }

}
