package com.intelligent.intelligentstdyplanner.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "study_sessions")
public class StudySession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long std_session_id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String title;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    // --- Feedback / Learning Fields ---
    private boolean isCompleted; // Did the student finish it?
    private int actualDurationMinutes; // How long did it actually take?
    private int comprehensionRating; // 1-5 scale (1=Confused, 5=Mastered)
}
