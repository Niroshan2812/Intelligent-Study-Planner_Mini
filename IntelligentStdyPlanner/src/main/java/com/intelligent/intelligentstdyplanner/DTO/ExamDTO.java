package com.intelligent.intelligentstdyplanner.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamDTO {
    private String name;
    private LocalDateTime deadline;
    private Long subject_id;
}
