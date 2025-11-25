package com.intelligent.intelligentstdyplanner.DTO;

import lombok.Data;

@Data
public class SubjectDTO {
    private Long id;
    private String name;
    private int difficaltyLevel;
    private double currentScore;
    private Long studentId;
}
