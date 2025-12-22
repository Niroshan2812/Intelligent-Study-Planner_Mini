package com.intelligent.intelligentstdyplanner.Repository;

import com.intelligent.intelligentstdyplanner.Model.StudentSubjectStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface StudentSubjectStatsRepository extends JpaRepository<StudentSubjectStats, Long> {

    @Query("SELECT s FROM StudentSubjectStats s WHERE s.student.id = :studentId AND s.subject.subject_id = :subjectId")
    Optional<StudentSubjectStats> findByStudentAndSubject(@Param("studentId") Long studentId,
            @Param("subjectId") Long subjectId);
}
