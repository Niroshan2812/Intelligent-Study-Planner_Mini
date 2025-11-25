package com.intelligent.intelligentstdyplanner.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subject_id;

    @Column(nullable = false)
    private String name;

    private int difficaltyLevel;

    private double currentScore;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

}
