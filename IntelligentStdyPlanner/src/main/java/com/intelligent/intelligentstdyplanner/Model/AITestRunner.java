package com.intelligent.intelligentstdyplanner.Model;

import com.intelligent.intelligentstdyplanner.Service.PredictionService;
import org.springframework.boot.CommandLineRunner;

public class AITestRunner implements CommandLineRunner {

    private final PredictionService predictionService;

    public AITestRunner(PredictionService predictionService) {
        this.predictionService = predictionService;

    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("-------------------------------------------");

        float hpurs = predictionService.predictStudyHours("Bio Science", "Colombo", 8.0f, 40.0f, 3.0f, 0.0f, 9.0f);
        System.out.println("Ai predition studnet needs: " + hpurs);

    }
}
