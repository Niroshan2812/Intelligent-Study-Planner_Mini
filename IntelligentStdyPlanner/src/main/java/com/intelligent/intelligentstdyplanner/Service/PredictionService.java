package com.intelligent.intelligentstdyplanner.Service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.DoubleBuffer;
import java.nio.LongBuffer;
import java.util.Map;

/*
In here,
Use per train model call- load training model into this
 */
@Service
public class PredictionService {
        private OrtSession session;
        private OrtEnvironment environment;

        // This run application start -
        // loading model then create OrtSession - so can respond immediately witout
        // reloading file everytime.

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
                        float tutuion, float fatigue, float avgSleepHours, String learningStyle) {
                try {
                        // Prepare inputs
                        OnnxTensor streamTensor = OnnxTensor.createTensor(environment, new String[] { stream },
                                        new long[] { 1, 1 });
                        OnnxTensor districtTensor = OnnxTensor.createTensor(environment, new String[] { district },
                                        new long[] { 1, 1 });

                        // ---------------------- Numerical inputs
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

                        float basePrediction;
                        try (OrtSession.Result result = session.run(inputs)) {
                                // Extract Result
                                // Output is likely a float tensor
                                float[][] output = (float[][]) result.get(0).getValue();
                                basePrediction = output[0][0];
                        }

                        // Post-Processing ---------------------------------------------------
                        float multiplier = 1.0f;

                        // Sleep Logic: < 6h -> +20% (Low efficiency), > 8h -> -10% (High efficiency)
                        if (avgSleepHours > 0) { // check if set
                                if (avgSleepHours < 6.0f) {
                                        multiplier += 0.2f;
                                        System.out.println("Adjusting prediction: Sleep penalty applied (Low Sleep: "
                                                        + avgSleepHours + ")");
                                } else if (avgSleepHours > 8.0f) {
                                        multiplier -= 0.1f;
                                        System.out.println("Adjusting prediction: Sleep bonus applied (Good Sleep: "
                                                        + avgSleepHours + ")");
                                }
                        }

                        // Learning Style Logic
                        // Synergy between Learning Style and Stream
                        if (learningStyle != null && stream != null) {
                                if (learningStyle.equalsIgnoreCase("Visual") &&
                                                (stream.contains("Science") || stream.contains("Technology"))) {
                                        multiplier -= 0.10f; // 10% faster for Visual learners in Science/Tech
                                        System.out.println("Adjusting prediction: Visual + Science Synergy (-10%)");
                                } else if (learningStyle.equalsIgnoreCase("Text") &&
                                                (stream.contains("Arts") || stream.contains("Commerce"))) {
                                        multiplier -= 0.10f; // 10% faster for Text learners in Arts/Commerce
                                        System.out.println("Adjusting prediction: Text + Arts/Commerce Synergy (-10%)");
                                } else if (learningStyle.equalsIgnoreCase("Auditory")) {
                                        multiplier -= 0.05f; // General 5% boost for auditory (assuming
                                                             // lectures/discussion)
                                        System.out.println("Adjusting prediction: Auditory General Bonus (-5%)");
                                }
                        }

                        return basePrediction * multiplier;

                } catch (Exception e) {
                        System.out.println("ONNX Prediction Error: " + e.getMessage());
                        e.printStackTrace();
                        return 2.0f; // Fallback
                }
        }

        // Overloaded method to include student/subject context for learning
        public float predictStudyHours(com.intelligent.intelligentstdyplanner.Model.Student student,
                        com.intelligent.intelligentstdyplanner.Model.Subject subject) {
                float base = predictStudyHours(
                                student.getStream(),
                                student.getDistrict(),
                                subject.getDifficaltyLevel(),
                                (float) subject.getCurrentScore(),
                                student.getEnglishFluency(),
                                student.getTuitionHoursWeekly(),
                                student.getCommuteFatigue(),
                                student.getAverageSleepHours(),
                                student.getLearningStyle());

                // Apply Learning Factor
                if (statsRepository != null) {
                        java.util.Optional<com.intelligent.intelligentstdyplanner.Model.StudentSubjectStats> statsOpt = statsRepository
                                        .findByStudentAndSubject(student.getId(), subject.getSubject_id());

                        if (statsOpt.isPresent()) {
                                float factor = (float) statsOpt.get().getEfficiencyFactor();
                                System.out.println("Applying Personal Efficiency Factor: " + factor);
                                base *= factor;
                        }
                }

                return base;
        }

        @org.springframework.beans.factory.annotation.Autowired
        private com.intelligent.intelligentstdyplanner.Repository.StudentSubjectStatsRepository statsRepository;
}
