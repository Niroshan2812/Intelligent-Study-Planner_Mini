package com.intelligent.intelligentstdyplanner.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
/*
To store AI predictions for future usage
 */
@Entity
@Data
@Table(name = "performance_logs")
public class PerformanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double score;
    private int hourseStudies;
    private LocalDate sateRecorded;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}
