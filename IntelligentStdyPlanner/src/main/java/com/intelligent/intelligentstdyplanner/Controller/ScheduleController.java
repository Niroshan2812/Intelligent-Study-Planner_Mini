package com.intelligent.intelligentstdyplanner.Controller;

import com.intelligent.intelligentstdyplanner.Model.StudySession;
import com.intelligent.intelligentstdyplanner.Service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/generate/{studentId}")
    public ResponseEntity<com.intelligent.intelligentstdyplanner.DTO.ScheduleResponseDTO> generateSchedule(
            @PathVariable Long studentId) {
        try {
            com.intelligent.intelligentstdyplanner.DTO.ScheduleResponseDTO response = scheduleService
                    .generateSchedule(studentId);
            System.out.println(response);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
