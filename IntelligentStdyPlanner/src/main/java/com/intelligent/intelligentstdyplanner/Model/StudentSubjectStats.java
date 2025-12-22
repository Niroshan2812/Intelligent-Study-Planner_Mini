package com.intelligent.intelligentstdyplanner.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "student_subject_stats")
public class StudentSubjectStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    // Efficiency Factor:
    // > 1.0 means student is SLOWER than predicted (Needs more time).
    // < 1.0 means FASTER (Needs less time).
    // Default 1.0
    private double efficiencyFactor = 1.0;

    private int totalSessionsCompleted = 0;
}
