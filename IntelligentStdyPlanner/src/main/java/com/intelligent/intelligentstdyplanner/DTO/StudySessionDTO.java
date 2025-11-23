package com.intelligent.intelligentstdyplanner.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudySessionDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String title;

    private Long subjectid;

}
