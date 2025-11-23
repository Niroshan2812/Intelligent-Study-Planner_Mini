package com.intelligent.intelligentstdyplanner.DTO;

import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class AvailabilityDTO {
    private DayOfWeek day_of_week;
    private LocalTime start_time;
    private LocalTime end_time;
}
