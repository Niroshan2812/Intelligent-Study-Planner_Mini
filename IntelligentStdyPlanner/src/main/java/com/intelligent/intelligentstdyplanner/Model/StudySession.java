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
}
