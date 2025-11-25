package com.intelligent.modeltest;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tribuo.*;
import org.tribuo.impl.ArrayExample;
import org.tribuo.regression.*;
import org.tribuo.provenance.SimpleDataSourceProvenance;
import org.tribuo.math.optimisers.SGD;
import org.tribuo.regression.sgd.linear.LinearSGDTrainer;
import org.tribuo.regression.sgd.objectives.SquaredLoss;
import org.tribuo.MutableDataset;

@Component
public class Scorepredictor implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Spring Boot Started: Training Model ---");

        // 1. Setup data socuese
        var regressionFactory = new RegressionFactory();
        var dataSourceProvenance = new SimpleDataSourceProvenance("TrainingData", regressionFactory);


        MutableDataset<Regressor> dataset = new MutableDataset<>(dataSourceProvenance, regressionFactory);

        // Add Training Samples
        dataset.add(createRow(1.0, 20.0));
        dataset.add(createRow(2.0, 40.0));
        dataset.add(createRow(4.0, 80.0));
        dataset.add(createRow(2.3, 30.0));
        dataset.add(createRow(5.0, 90.0));
        dataset.add(createRow(5.1, 91.0));
        dataset.add(createRow(5.2, 94.0));
        dataset.add(createRow(6.0, 80.0));
        dataset.add(createRow(2.2, 20.0));
        dataset.add(createRow(3.3, 50.0));
        dataset.add(createRow(4.0, 75.0));
        dataset.add(createRow(4.0, 70.0));
        dataset.add(createRow(2.2, 22.0));
        dataset.add(createRow(5.0, 70.0));
        dataset.add(createRow(2.0, 80.0));
        dataset.add(createRow(2.7, 39.0));
        dataset.add(createRow(4.0, 70.0));
        dataset.add(createRow(1.1, 91.0));
        dataset.add(createRow(4.2, 100.0));
        dataset.add(createRow(5.0, 10.0));
        dataset.add(createRow(1.1, 20.0));
        dataset.add(createRow(3.8, 50.0));
        dataset.add(createRow(2.9, 75.0));
        dataset.add(createRow(3.5, 65.0));


        //Configure the Trainer
        Trainer<Regressor> trainer = new LinearSGDTrainer(
                new SquaredLoss(),// Objective function
                SGD.getLinearDecaySGD(0.01), // Optimizer
                1000, // Epochs
                1000,  // Logging Interval
                1,   // Minibatch size
                1L  // Seed (For consistent results)
        );

        //Train the model
          Model<Regressor> model = trainer.train(dataset);

        //Make a Prediction
        double hoursToStudy = 6.0;
        Example<Regressor> input = createRow(hoursToStudy, Double.NaN);
        Prediction<Regressor> result = model.predict(input);

        System.out.println("\n--- Model 'Regression Table' (Manual) ---");
        var featureWeight = model.getTopFeatures(10);

        for (var entry : featureWeight.entrySet()) {
            String outputName = entry.getKey();
            System.out.println("Target Variable: " + outputName);
            for (var feature : entry.getValue()) {
                System.out.println("  Feature: " + feature.getA() + " | Coefficient (Weight): " + feature.getB());
            }
        }
        //Output Result
        double predictionScore = result.getOutput().getValues()[0];
        System.out.println("--- Prediction Result ---");
        System.out.println("If you study for " + hoursToStudy + " hours, AI predicts: "
                + String.format("%.2f", predictionScore) + " marks.");
    }
    //create data rows
    private Example<Regressor> createRow(Double hours, double marks) {
        String[] featureName = {"StudyHours"};
        double[] featureValues = {hours};

        if (Double.isNaN(marks)) {
            return new ArrayExample<>(new Regressor("Marks", Double.NaN), featureName, featureValues);
        } else {
            return new ArrayExample<>(new Regressor("Marks", marks), featureName, featureValues);
        }
    }


}
