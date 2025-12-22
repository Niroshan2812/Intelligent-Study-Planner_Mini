package com.intelligent.intelligentstdyplanner.Service;

import com.intelligent.intelligentstdyplanner.Model.Student;
import com.intelligent.intelligentstdyplanner.Model.StudentSubjectStats;
import com.intelligent.intelligentstdyplanner.Model.StudySession;
import com.intelligent.intelligentstdyplanner.Model.Subject;
import com.intelligent.intelligentstdyplanner.Repository.StudentSubjectStatsRepository;
import com.intelligent.intelligentstdyplanner.Repository.StudySessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
public class FeedbackService {

    private final StudySessionRepository studySessionRepository;
    private final StudentSubjectStatsRepository statsRepository;

    public FeedbackService(StudySessionRepository studySessionRepository,
            StudentSubjectStatsRepository statsRepository) {
        this.studySessionRepository = studySessionRepository;
        this.statsRepository = statsRepository;
    }

    @Transactional
    public void submitFeedback(Long sessionId, int actualDurationMinutes, int comprehensionRating) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.isCompleted()) {
            session.setActualDurationMinutes(actualDurationMinutes);
            session.setComprehensionRating(comprehensionRating);
            studySessionRepository.save(session);
            return;
        }

        session.setCompleted(true);
        session.setActualDurationMinutes(actualDurationMinutes);
        session.setComprehensionRating(comprehensionRating);

        // Update Stats
        updateStudentSubjectStats(session);

        studySessionRepository.save(session);
    }

    private void updateStudentSubjectStats(StudySession session) {
        Subject subject = session.getSubject();
        // Assuming Subject has a Student
        Student student = subject.getStudent();

        if (student == null)
            return;

        StudentSubjectStats stats = statsRepository.findByStudentAndSubject(student.getId(), subject.getSubject_id())
                .orElseGet(() -> {
                    StudentSubjectStats newStats = new StudentSubjectStats();
                    newStats.setStudent(student);
                    newStats.setSubject(subject);
                    return newStats;
                });


        long scheduledMinutes = Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();
        if (scheduledMinutes == 0)
            scheduledMinutes = 30;

        double currentFactor = (double) session.getActualDurationMinutes() / scheduledMinutes;


        // NewAvg = (OldAvg * Count + NewFactor) / (Count + 1)
        double oldEfficiency = stats.getEfficiencyFactor();
        int count = stats.getTotalSessionsCompleted();

        double newEfficiency = ((oldEfficiency * count) + currentFactor) / (count + 1);


        newEfficiency = Math.max(0.5, Math.min(newEfficiency, 3.0));

        stats.setEfficiencyFactor(newEfficiency);
        stats.setTotalSessionsCompleted(count + 1);

        statsRepository.save(stats);

        System.out.println(
                "Updated stats for Subject " + subject.getName() + ": New Efficiency Factor = " + newEfficiency);
    }
}
