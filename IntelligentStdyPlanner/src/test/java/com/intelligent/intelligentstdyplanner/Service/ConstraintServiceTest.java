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
        availability.setStartTime(LocalTime.now().plusHours(1)); // Available in 1 hour
        availability.setEndTime(LocalTime.now().plusHours(3)); // For 2 hours

        Map<Long, Float> predictedHours = Map.of(1L, 1.0f); // Need 1 hour (2 slots)

        // Act
        List<StudySession> sessions = service.generateSchedule(List.of(exam), List.of(availability), predictedHours);

        // Assert
        assertFalse(sessions.isEmpty());
        assertEquals("Study Math", sessions.get(0).getTitle());
        // Verify duration is 1 hour
        long minutes = java.time.Duration.between(sessions.get(0).getStartTime(), sessions.get(0).getEndTime())
                .toMinutes();
        assertEquals(60, minutes);
    }
}
