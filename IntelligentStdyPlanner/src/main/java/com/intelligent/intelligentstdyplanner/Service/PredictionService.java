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
            String modelPath = "src/main/resources/Study_predictor.onnx";
            session = environment.createSession(modelPath, new OrtSession.SessionOptions());
            System.out.println("Model loaded complete");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public float predictStudyHours(float difficulty, float score, float fluency, float tutuion, float fatigue) {
        try {
            long[] shape = new long[] { 1, 5 };

            // order for fallow
            // ['difficulty_level', 'current_score', 'english_fluency',
            // 'tuition_hours_weekly', 'commute_fatigue']
            float[] data = new float[] { difficulty, score, fluency, tutuion, fatigue };

            // create tensor
            OnnxTensor tensor = OnnxTensor.createTensor(environment, FloatBuffer.wrap(data), shape);
            Map<String, OnnxTensor> inputs = Collections.singletonMap("float_input", tensor);

            try (OrtSession.Result result = session.run(inputs)) {
                // Extract Result
                float[][] output = (float[][]) result.get(0).getValue();
                return output[0][0];
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 2.0f;
        }
    }
}
