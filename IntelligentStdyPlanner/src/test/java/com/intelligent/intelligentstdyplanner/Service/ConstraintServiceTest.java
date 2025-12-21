package com.intelligent.intelligentstdyplanner.Service;

import com.intelligent.intelligentstdyplanner.Model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ConstraintServiceTest {

    @Test
    void generateSchedule_shouldScheduleSimpleTask() {
        ConstraintService service = new ConstraintService();

        // Arrange
        Subject subject = new Subject();
        subject.setSubject_id(1L);
        subject.setName("Math");

        Exam exam = new Exam();
        exam.setSubject(subject);
        exam.setDeadline(LocalDateTime.now().plusDays(2));

        Availability availability = new Availability();
        availability.setDayOfWeek(LocalDateTime.now().getDayOfWeek());
        // Use fixed time to avoid midnight roll-over issues
        LocalDateTime todayNoon = LocalDateTime.now().withHour(12).withMinute(0).withSecond(0).withNano(0);
        availability.setStartTime(todayNoon.toLocalTime());
        availability.setEndTime(todayNoon.plusHours(2).toLocalTime());

        Map<Long, Float> predictedHours = Map.of(1L, 1.0f); // Need 1 hour (2 slots)

        // Act
        List<StudySession> sessions = service.generateSchedule(List.of(exam), List.of(availability), predictedHours);

        // Assert
        assertFalse(sessions.isEmpty());
        StudySession session = sessions.get(0);
        assertEquals("Study Math", session.getTitle());
        // Verify duration is 1 hour
        long minutes = java.time.Duration.between(session.getStartTime(), session.getEndTime())
                .toMinutes();
        assertEquals(60, minutes);
    }

    @Test
    void generateSchedule_shouldIncludeBreaks() {
        ConstraintService service = new ConstraintService();

        // Arrange
        Subject subject = new Subject();
        subject.setSubject_id(1L);
        subject.setName("Math");

        Exam exam = new Exam();
        exam.setSubject(subject);
        exam.setDeadline(LocalDateTime.now().plusDays(2));

        // Availability: 10:00 - 15:00 (5 hours)
        Availability availability = new Availability();
        availability.setDayOfWeek(LocalDateTime.now().getDayOfWeek());
        LocalDateTime start = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0);
        availability.setStartTime(start.toLocalTime());
        availability.setEndTime(start.plusHours(5).toLocalTime());

        // Need 3 hours study -> Chunk 1 (2h) + Break (30m) + Chunk 2 (1h) + Break (30m)
        // = Total 4h used in solver
        // Session 1: 2h. Session 2: 1h.
        Map<Long, Float> predictedHours = Map.of(1L, 3.0f);

        // Act
        List<StudySession> sessions = service.generateSchedule(List.of(exam), List.of(availability), predictedHours);

        // Assert
        assertFalse(sessions.isEmpty());
        // Should be at least 2 sessions (unless merged, but our merge logic shouldn't
        // merge them due to gaps)
        // Wait, logic says merge if contiguous.
        // Session 1 End (e.g. 12:00) != Session 2 Start (12:30).
        // So they should NOT merge.
        assertEquals(2, sessions.size(), "Should have 2 sessions due to break split");

        StudySession s1 = sessions.get(0);
        StudySession s2 = sessions.get(1);

        long s1Duration = java.time.Duration.between(s1.getStartTime(), s1.getEndTime()).toMinutes();
        long s2Duration = java.time.Duration.between(s2.getStartTime(), s2.getEndTime()).toMinutes();

        // Assert durations (heuristic: one should be 120, other 60)
        // Order is guaranteed by sort in mergeSessions? Yes.

        // Check Gap
        LocalDateTime s1End = s1.getEndTime();
        LocalDateTime s2Start = s2.getStartTime();

        long gapMinutes = java.time.Duration.between(s1End, s2Start).toMinutes();
        assertEquals(30, gapMinutes, "There should be a 30 min break between sessions");
    }
}
