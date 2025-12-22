package com.intelligent.intelligentstdyplanner.Service;

import com.intelligent.intelligentstdyplanner.Model.*;
import com.intelligent.intelligentstdyplanner.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleService {

        private final StudentRepository studentRepository;
        private final ExamRepository examRepository;
        private final AvailabilityRepository availabilityRepository;
        private final StudySessionRepository studySessionRepository;
        private final PredictionService predictionService;
        private final ConstraintService constraintService;

        public ScheduleService(StudentRepository studentRepository,
                        ExamRepository examRepository,
                        AvailabilityRepository availabilityRepository,
                        StudySessionRepository studySessionRepository,
                        PredictionService predictionService,
                        ConstraintService constraintService) {
                this.studentRepository = studentRepository;
                this.examRepository = examRepository;
                this.availabilityRepository = availabilityRepository;
                this.studySessionRepository = studySessionRepository;
                this.predictionService = predictionService;
                this.constraintService = constraintService;
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

                //Predict hours for all exams
                Map<Long, Float> predictedHoursMap = new HashMap<>();
                for (Exam exam : upcomingExams) {
                        Subject subject = exam.getSubject();
                        float predictedHours = predictionService.predictStudyHours(
                                        student.getStream(),
                                        student.getDistrict(),
                                        subject.getDifficaltyLevel(),
                                        (float) subject.getCurrentScore(),
                                        student.getEnglishFluency(),
                                        student.getTuitionHoursWeekly(),
                                        student.getCommuteFatigue(),
                                        student.getAverageSleepHours(),
                                        student.getLearningStyle());

                        System.out.println("Predicted hours for subject " + subject.getName() + ": " + predictedHours);
                        predictedHoursMap.put(subject.getSubject_id(), predictedHours);
                }

                //Use Choco-Solver to generate schedule
                List<StudySession> newSessions = constraintService.generateSchedule(upcomingExams, availabilities,
                                predictedHoursMap);

                System.out.println("Generated " + newSessions.size() + " sessions using Choco-Solver");

                return studySessionRepository.saveAll(newSessions);
        }
}
