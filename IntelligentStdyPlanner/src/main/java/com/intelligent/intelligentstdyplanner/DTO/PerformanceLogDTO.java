package com.intelligent.intelligentstdyplanner.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PerformanceLogDTO {
    private double score;
    private int hourseStudies;
    private LocalDate sateRecorded;

    private Long subjectId;
}
