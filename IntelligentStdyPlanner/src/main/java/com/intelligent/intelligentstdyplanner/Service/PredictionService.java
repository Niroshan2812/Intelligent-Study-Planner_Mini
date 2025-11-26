package com.intelligent.intelligentstdyplanner.Service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.DoubleBuffer;
import java.nio.LongBuffer;
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
                        String modelPath = "src/main/resources/Study_predictor_improved.onnx";
                        session = environment.createSession(modelPath, new OrtSession.SessionOptions());
                        System.out.println("Model loaded complete");

                        // Log input info for debugging
                        System.out.println("Model Input Info: " + session.getInputInfo());
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public float predictStudyHours(String stream, String district, float difficulty, float score, float fluency,
                        float tutuion, float fatigue) {
                try {
                        // Prepare inputs
                        OnnxTensor streamTensor = OnnxTensor.createTensor(environment, new String[] { stream },
                                        new long[] { 1, 1 });
                        OnnxTensor districtTensor = OnnxTensor.createTensor(environment, new String[] { district },
                                        new long[] { 1, 1 });

                        // Numerical inputs
                        // difficulty_level: int64
                        // current_score: float (but model expects double)
                        // english_fluency: int64
                        // tuition_hours_weekly: int64
                        // commute_fatigue: float (but model expects double)

                        OnnxTensor difficultyTensor = OnnxTensor.createTensor(environment,
                                        LongBuffer.wrap(new long[] { (long) difficulty }), new long[] { 1, 1 });

                        OnnxTensor scoreTensor = OnnxTensor.createTensor(environment,
                                        DoubleBuffer.wrap(new double[] { (double) score }), new long[] { 1, 1 });

                        OnnxTensor fluencyTensor = OnnxTensor.createTensor(environment,
                                        LongBuffer.wrap(new long[] { (long) fluency }), new long[] { 1, 1 });

                        OnnxTensor tuitionTensor = OnnxTensor.createTensor(environment,
                                        LongBuffer.wrap(new long[] { (long) tutuion }), new long[] { 1, 1 });

                        OnnxTensor fatigueTensor = OnnxTensor.createTensor(environment,
                                        DoubleBuffer.wrap(new double[] { (double) fatigue }), new long[] { 1, 1 });

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
                                // Output is likely a float tensor
                                float[][] output = (float[][]) result.get(0).getValue();
                                return output[0][0];
                        }
                } catch (Exception e) {
                        System.out.println("ONNX Prediction Error: " + e.getMessage());
                        e.printStackTrace();
                        return 2.0f; // Fallback
                }
        }
}
