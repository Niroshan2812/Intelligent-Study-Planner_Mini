package com.intelligent.intelligentstdyplanner.DTO;

import lombok.Data;

@Data
public class StudentDTO {
    private Long id;
    private String name;
    private float englishFluency;
    private float tuitionHoursWeekly;
    private float commuteFatigue;
}
