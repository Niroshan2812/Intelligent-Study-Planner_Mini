package com.intelligent.intelligentstdyplanner.Repository;

import com.intelligent.intelligentstdyplanner.Model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByDeadlineAfter(LocalDateTime date);
}
