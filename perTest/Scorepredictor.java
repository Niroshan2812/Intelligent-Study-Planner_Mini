import org.tribuo.*;
import org.tribuo.impl.ArrayExample;
import org.tribuo.regression.*;
import org.tribuo.provenance.SimpleDataSourceProvenance;
import org.tribuo.math.optimisers.SGD;
import org.tribuo.regression.sgd.linear.*;
import org.tribuo.regression.sgd.objectives.SquaredLoss;
import org.tribuo.MutableDataset;

public class Scorepredictor {
    public static void main(String[] args) {

        var regressionFactory = new RegressionFactory();
        var dataSourceProvenance = new SimpleDataSourceProvenance("TrainingData", regressionFactory);

        MutableDataset<Regressor> dataset = new MutableDataset<>(dataSourceProvenance, regressionFactory);

        // Adding training Samples
        dataset.add(createRow(1.0, 20.0));
        dataset.add(createRow(2.0, 40.0));
        dataset.add(createRow(4.0, 80.0));

        System.out.println("Dataset size: " + dataset.size());

        // FIX: Use the full 6-argument constructor to avoid ambiguity
        // Arguments: Objective, Optimiser, Epochs, LoggingInterval, MinibatchSize, Seed
        LinearSGDTrainer trainer = new LinearSGDTrainer(
                new SquaredLoss(),           // Objective
                SGD.getLinearDecaySGD(0.01), // Optimiser
                1000,                        // Epochs
                1000,                        // Logging Interval
                1,                           // Minibatch Size (1 is safe for small data)
                1L                           // Seed
        );

        System.out.println("Training the AI...");

        // FIX: Directly call train on the concrete class if the interface fails
        Model<Regressor> model = trainer.train(dataset);

        // Prediction Input
        Example<Regressor> input = createRow(3.0, Double.NaN);

        Prediction<Regressor> result = model.predict(input);

        double predictionScore = result.getOutput().getValues()[0];

        System.out.println("--- Prediction Result ---");
        System.out.println("If you study for 3 hours, AI predicts: " + String.format("%.2f", predictionScore) + " marks.");
    }

    public static Example<Regressor> createRow(Double hours, double marks){
        String[] featureName  = {"StudyHours"};
        double[] featureValues = {hours};

        if(Double.isNaN(marks)){
            return new ArrayExample<>(new Regressor("Marks",Double.NaN), featureName, featureValues);
        } else {
            return new ArrayExample<>(new Regressor("Marks",marks), featureName, featureValues);
        }
    }
}