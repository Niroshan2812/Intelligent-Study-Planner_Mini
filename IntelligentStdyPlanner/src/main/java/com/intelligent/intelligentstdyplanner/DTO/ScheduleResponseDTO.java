package com.intelligent.intelligentstdyplanner.DTO;

import com.intelligent.intelligentstdyplanner.Model.StudySession;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ScheduleResponseDTO {
    private List<StudySession> sessions;
    private Map<String, Float> predictedHoursPerSubject;
}
