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
        System.out.println("Generating schedule for student: " + studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Exam> allExams = examRepository.findByDeadlineAfter(LocalDateTime.now());
        // Filter exams for the specific student
        List<Exam> upcomingExams = allExams.stream()
                .filter(e -> e.getSubject().getStudent() != null
                        && e.getSubject().getStudent().getId().equals(studentId))
                .toList();

        System.out.println("Found " + upcomingExams.size() + " upcoming exams for student " + studentId);

        List<Availability> availabilities = availabilityRepository.findByStudentId(studentId);
        System.out.println("Found " + availabilities.size() + " availability slots for student " + studentId);

        List<StudySession> newSessions = new ArrayList<>();

        for (Exam exam : upcomingExams) {
            Subject subject = exam.getSubject();
            float predictedHours = predictionService.predictStudyHours(
                    student.getStream(),
                    student.getDistrict(),
                    subject.getDifficaltyLevel(),
                    (float) subject.getCurrentScore(),
                    student.getEnglishFluency(),
                    student.getTuitionHoursWeekly(),
                    student.getCommuteFatigue());

            System.out.println("Predicted hours for subject " + subject.getName() + ": " + predictedHours);

            // Simple allocation logic: distribute hours across available slots
            // This is a simplified greedy approach
            float hoursAllocated = 0;
            LocalDate currentDate = LocalDate.now();

            // FIX: Changed isBefore to !isAfter to include the deadline day
            while (hoursAllocated < predictedHours && !currentDate.isAfter(exam.getDeadline().toLocalDate())) {
                boolean allocatedInDay = false;
                for (Availability slot : availabilities) {
                    if (slot.getDayOfWeek() == currentDate.getDayOfWeek()) {
                        LocalDateTime start = LocalDateTime.of(currentDate, slot.getStartTime());
                        LocalDateTime end = LocalDateTime.of(currentDate, slot.getEndTime());

                        // If today is the deadline, ensure we don't schedule past the deadline time?
                        // For now, assuming deadline is end of day or specific time.
                        // If specific time, we should cap 'end' to exam.getDeadline() if on same day.
                        if (currentDate.isEqual(exam.getDeadline().toLocalDate())) {
                            if (start.isAfter(exam.getDeadline())) {
                                continue; // Slot is after exam
                            }
                            if (end.isAfter(exam.getDeadline())) {
                                end = exam.getDeadline(); // Cap slot at exam time
                            }
                        }

                        // Check for clashes with existing sessions (including newly created ones)
                        if (isSlotAvailable(start, end, newSessions)) {
                            // Cap session duration to remaining needed hours or slot duration
                            long slotDurationMinutes = java.time.Duration.between(start, end).toMinutes();
                            if (slotDurationMinutes <= 0)
                                continue;

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
                                allocatedInDay = true;
                                System.out.println("Allocated " + hoursToBook + " hours for " + subject.getName()
                                        + " on " + start);

                                if (hoursAllocated >= predictedHours)
                                    break;
                            }
                        } else {
                            // System.out.println("Slot not available: " + start + " - " + end);
                        }
                    }
                }
                currentDate = currentDate.plusDays(1);
            }
            if (hoursAllocated < predictedHours) {
                System.out.println("Warning: Could not allocate all needed hours for " + subject.getName()
                        + ". Allocated: " + hoursAllocated + "/" + predictedHours);
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
