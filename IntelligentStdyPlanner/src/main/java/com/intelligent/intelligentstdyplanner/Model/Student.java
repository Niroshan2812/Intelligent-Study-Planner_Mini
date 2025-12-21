package com.intelligent.intelligentstdyplanner.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Factors for AI Prediction
    private float englishFluency; // 1.0 to 5.0
    private float tuitionHoursWeekly;
    private float commuteFatigue; // 1.0 to 5.0

    private String stream;
    private String district;

    // New AI Factors
    private float averageSleepHours;
    private String learningStyle; // e.g., Visual, Auditory, Text
}
