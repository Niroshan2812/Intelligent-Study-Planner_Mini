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
    public ResponseEntity<List<StudySession>> generateSchedule(@PathVariable Long studentId) {
        try {
            List<StudySession> sessions = scheduleService.generateSchedule(studentId);
            System.out.println(sessions);
            return ResponseEntity.ok(sessions);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
