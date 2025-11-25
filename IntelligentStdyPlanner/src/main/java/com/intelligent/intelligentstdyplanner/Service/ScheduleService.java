package com.intelligent.intelligentstdyplanner.Service;

import com.intelligent.intelligentstdyplanner.Model.*;
import com.intelligent.intelligentstdyplanner.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final AvailabilityRepository availabilityRepository;
    private final StudySessionRepository studySessionRepository;
    private final PredictionService predictionService;

    public ScheduleService(StudentRepository studentRepository,
            ExamRepository examRepository,
            AvailabilityRepository availabilityRepository,
            StudySessionRepository studySessionRepository,
            PredictionService predictionService) {
        this.studentRepository = studentRepository;
        this.examRepository = examRepository;
        this.availabilityRepository = availabilityRepository;
        this.studySessionRepository = studySessionRepository;
        this.predictionService = predictionService;
    }

    public List<StudySession> generateSchedule(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Exam> allExams = examRepository.findByDeadlineAfter(LocalDateTime.now());
        // Filter exams for the specific student
        List<Exam> upcomingExams = allExams.stream()
                .filter(e -> e.getSubject().getStudent() != null
                        && e.getSubject().getStudent().getId().equals(studentId))
                .toList();
        List<Availability> availabilities = availabilityRepository.findByStudentId(studentId);
        List<StudySession> newSessions = new ArrayList<>();

        for (Exam exam : upcomingExams) {
            Subject subject = exam.getSubject();
            float predictedHours = predictionService.predictStudyHours(
                    subject.getDifficaltyLevel(),
                    (float) subject.getCurrentScore(),
                    student.getEnglishFluency(),
                    student.getTuitionHoursWeekly(),
                    student.getCommuteFatigue());

            // Simple allocation logic: distribute hours across available slots
            // This is a simplified greedy approach
            float hoursAllocated = 0;
            LocalDate currentDate = LocalDate.now();

            while (hoursAllocated < predictedHours && currentDate.isBefore(exam.getDeadline().toLocalDate())) {
                for (Availability slot : availabilities) {
                    if (slot.getDayOfWeek() == currentDate.getDayOfWeek()) {
                        LocalDateTime start = LocalDateTime.of(currentDate, slot.getStartTime());
                        LocalDateTime end = LocalDateTime.of(currentDate, slot.getEndTime());

                        // Check for clashes with existing sessions (including newly created ones)
                        if (isSlotAvailable(start, end, newSessions)) {
                            // Cap session duration to remaining needed hours or slot duration
                            long slotDurationMinutes = java.time.Duration.between(start, end).toMinutes();
                            float slotDurationHours = slotDurationMinutes / 60.0f;

                            float hoursToBook = Math.min(predictedHours - hoursAllocated, slotDurationHours);

                            if (hoursToBook > 0) {
                                LocalDateTime sessionEnd = start.plusMinutes((long) (hoursToBook * 60));

                                StudySession session = new StudySession();
                                session.setSubject(subject);
                                session.setStartTime(start);
                                session.setEndTime(sessionEnd);
                                session.setTitle("Study " + subject.getName());

                                newSessions.add(session);
                                hoursAllocated += hoursToBook;

                                if (hoursAllocated >= predictedHours)
                                    break;
                            }
                        }
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
        }

        return studySessionRepository.saveAll(newSessions);
    }

    private boolean isSlotAvailable(LocalDateTime start, LocalDateTime end, List<StudySession> newSessions) {
        // Check database for existing sessions
        List<StudySession> existingSessions = studySessionRepository.findByStartTimeBetween(start.minusDays(1),
                end.plusDays(1));

        // Combine with new sessions being built in memory
        List<StudySession> allSessions = new ArrayList<>(existingSessions);
        allSessions.addAll(newSessions);

        for (StudySession session : allSessions) {
            // Check for overlap
            if (start.isBefore(session.getEndTime()) && end.isAfter(session.getStartTime())) {
                return false;
            }
        }
        return true;
    }
}
