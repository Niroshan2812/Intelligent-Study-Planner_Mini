package com.intelligent.intelligentstdyplanner.Service;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PredictionServiceIntegrationTest {

    @Test
    public void testPrediction() {
        // Check if model file exists first
        File modelFile = new File("src/main/resources/Study_predictor_improved.onnx");
        if (!modelFile.exists()) {
            System.out.println("Model file not found, skipping integration test");
            return;
        }

        PredictionService service = new PredictionService();
        service.init();

        float hours = service.predictStudyHours("Bio Science", "Colombo", 8.0f, 40.0f, 3.0f, 0.0f, 9.0f, 7.0f,
                "Visual");
        System.out.println("Predicted hours: " + hours);

        assertTrue(hours > 0, "Predicted hours should be greater than 0");
    }
}
