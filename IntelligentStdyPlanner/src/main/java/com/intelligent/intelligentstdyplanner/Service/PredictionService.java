package com.intelligent.intelligentstdyplanner.Service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Map;

@Service
public class PredictionService {
    private OrtSession session;
    private OrtEnvironment environment;

    @PostConstruct
    public void init() {
        try {
            // Initilize ONNX runtime
            environment = OrtEnvironment.getEnvironment();

            // load model
            // Use getResourceAsStream for better compatibility (though createSession takes
            // a path)
            // For file path in Spring Boot resources:
            String modelPath = "src/main/resources/Study_predictor_improved.onnx";
            session = environment.createSession(modelPath, new OrtSession.SessionOptions());
            System.out.println("Model loaded complete");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public float predictStudyHours(String stream, String district, float difficulty, float score, float fluency,
            float tutuion, float fatigue) {
        try {
            // Prepare inputs
            // Model expects named inputs: stream, district, difficulty_level,
            // current_score, english_fluency, tuition_hours_weekly, commute_fatigue

            // Create tensors
            // String tensors for categorical
            OnnxTensor streamTensor = OnnxTensor.createTensor(environment, new String[] { stream },
                    new long[] { 1, 1 });
            OnnxTensor districtTensor = OnnxTensor.createTensor(environment, new String[] { district },
                    new long[] { 1, 1 });

            // Numerical tensors
            // difficulty_level: int64
            // current_score: float
            // english_fluency: int64
            // tuition_hours_weekly: int64
            // commute_fatigue: float

            OnnxTensor difficultyTensor = OnnxTensor.createTensor(environment,
                    java.nio.LongBuffer.wrap(new long[] { (long) difficulty }), new long[] { 1, 1 });
            OnnxTensor scoreTensor = OnnxTensor.createTensor(environment,
                    java.nio.FloatBuffer.wrap(new float[] { score }), new long[] { 1, 1 });
            OnnxTensor fluencyTensor = OnnxTensor.createTensor(environment,
                    java.nio.LongBuffer.wrap(new long[] { (long) fluency }), new long[] { 1, 1 });
            OnnxTensor tuitionTensor = OnnxTensor.createTensor(environment,
                    java.nio.LongBuffer.wrap(new long[] { (long) tutuion }), new long[] { 1, 1 });
            OnnxTensor fatigueTensor = OnnxTensor.createTensor(environment,
                    java.nio.FloatBuffer.wrap(new float[] { fatigue }), new long[] { 1, 1 });

            Map<String, OnnxTensor> inputs = Map.of(
                    "stream", streamTensor,
                    "district", districtTensor,
                    "difficulty_level", difficultyTensor,
                    "current_score", scoreTensor,
                    "english_fluency", fluencyTensor,
                    "tuition_hours_weekly", tuitionTensor,
                    "commute_fatigue", fatigueTensor);

            try (OrtSession.Result result = session.run(inputs)) {
                // Extract Result
                // Output is likely a tensor named "variable" or similar, but we can access by
                // index 0
                float[][] output = (float[][]) result.get(0).getValue();
                return output[0][0];
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 2.0f; // Fallback
        }
    }
}
