package com.intelligent.intelligentstdyplanner.Service;

import com.intelligent.intelligentstdyplanner.Model.*;
import com.intelligent.intelligentstdyplanner.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ExamRepository examRepository;
    @Mock
    private AvailabilityRepository availabilityRepository;
    @Mock
    private StudySessionRepository studySessionRepository;
    @Mock
    private PredictionService predictionService;

    @InjectMocks
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateSchedule_shouldCreateSessions() {
        // Arrange
        Long studentId = 1L;
        Student student = new Student();
        student.setId(studentId);
        student.setEnglishFluency(3.0f);
        student.setTuitionHoursWeekly(10.0f);
        student.setCommuteFatigue(2.0f);

        Subject subject = new Subject();
        subject.setStudent(student);
        subject.setDifficaltyLevel(5);
        subject.setCurrentScore(60.0);
        subject.setName("Math");

        Exam exam = new Exam();
        exam.setSubject(subject);
        exam.setDeadline(LocalDateTime.now().plusDays(5));

        Availability availability = new Availability();
        availability.setDayOfWeek(LocalDateTime.now().getDayOfWeek());
        availability.setStartTime(LocalTime.of(10, 0));
        availability.setEndTime(LocalTime.of(12, 0));

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(examRepository.findByDeadlineAfter(any())).thenReturn(List.of(exam));
        when(availabilityRepository.findByStudentId(studentId)).thenReturn(List.of(availability));
        when(predictionService.predictStudyHours(any(), any(), anyFloat(), anyFloat(), anyFloat(), anyFloat(),
                anyFloat()))
                .thenReturn(1.5f);
        when(studySessionRepository.saveAll(any())).thenAnswer(i -> i.getArguments()[0]);

        // Act
        List<StudySession> sessions = scheduleService.generateSchedule(studentId);

        // Assert
        assertEquals(1, sessions.size());
        assertEquals("Study Math", sessions.get(0).getTitle());
        verify(predictionService).predictStudyHours(any(), any(), eq(5.0f), eq(60.0f), eq(3.0f), eq(10.0f), eq(2.0f));
    }
}
