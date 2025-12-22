package com.intelligent.intelligentstdyplanner.DTO;

import lombok.Data;

@Data
public class FeedbackRequest {
    private int actualDurationMinutes;
    private int comprehensionRating;
}
