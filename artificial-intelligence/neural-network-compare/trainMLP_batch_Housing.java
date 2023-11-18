//usr/bin/env jbang "$0" "$@" ; exit $?
//SOURCES SimpleMLPBatch.java
//SOURCES FeatureHandler.java
//SOURCES FeatureNormalizer.java
//SOURCES ActivationFunction.java
package com.makariev.examples.ai.neuralnet;

import java.util.List;

public class trainMLP_batch_Housing {

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();

        // "dataset/HousingData.csv";
        List<double[]> allData = FeatureHandler.readCSV("dataset/HousingData.csv", new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13});
        FeatureHandler.TrainTestSplit tts = FeatureHandler.splitData(allData, 80);

        List<double[]> trainData = FeatureHandler.removeNaN(tts.trainData());
        List<double[]> testData = FeatureHandler.removeNaN(tts.testData());

        FeatureNormalizer normalizer = FeatureNormalizer.minMaxNormalizer(13);

        normalizer.updateStatistics(FeatureHandler.removeColumns(trainData, new int[]{13}));

        final SimpleMLPBatch myMLP = new SimpleMLPBatch(
                new SimpleMLPBatch.Layer[]{
                    SimpleMLPBatch.layer(ActivationFunction.leakyReLU(),13),
                    SimpleMLPBatch.layer(ActivationFunction.leakyReLU(),60),
                    SimpleMLPBatch.layer(ActivationFunction.linear(),1)
                });

        for (int epoch = 0; epoch < 1_000; epoch++) {
            double learningRate = 0.001;
            final int finalEpoch = epoch;
            FeatureHandler.processInChunks(trainData, 2, chunk -> {
                List<double[]> features = normalizer.normalize(FeatureHandler.removeColumns(chunk, new int[]{13}));
                List<double[]> target = FeatureHandler.extractColumns(chunk, new int[]{13});
                myMLP.trainBatch(features, target, learningRate);
                //double error = myMLP.computeMeanSquaredError(features, target);
                //System.out.println("Epoch :%d, error %.6f".formatted(finalEpoch, error));
            });
        }

        List<double[]> testDataNormalized = normalizer.normalize(FeatureHandler.removeColumns(testData, new int[]{13}));
        List<double[]> targetBatch = FeatureHandler.extractColumns(testData, new int[]{13});

        double[] house_1 = testDataNormalized.get(0);
        double price_1 = targetBatch.get(0)[0];

        double[] house_2 = testDataNormalized.get(5);
        double price_2 = targetBatch.get(5)[0];

        double[] house_3 = testDataNormalized.get(10);
        double price_3 = targetBatch.get(10)[0];

        final double predicted_price_1 = myMLP.predict(house_1)[0];
        final double predicted_price_2 = myMLP.predict(house_2)[0];
        final double predicted_price_3 = myMLP.predict(house_3)[0];

        System.out.println("price_1=" + price_1 + ", predicted_price_1=" + predicted_price_1);
        System.out.println("price_2=" + price_2 + ", predicted_price_2=" + predicted_price_2);
        System.out.println("price_3=" + price_3 + ", predicted_price_3=" + predicted_price_3);
        
        System.out.println("");
        double error = myMLP.computeMeanSquaredError(testDataNormalized, targetBatch);
        System.out.println("MLP MeanSquaredError="+error);
        System.out.println("");
        System.out.println("executed for "+(System.currentTimeMillis()-startTime)+"ms");
        
        

    }

}
