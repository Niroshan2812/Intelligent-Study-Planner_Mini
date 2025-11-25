package com.intelligent.intelligentstdyplanner.Repository;

import com.intelligent.intelligentstdyplanner.Model.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    List<StudySession> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
