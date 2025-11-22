package com.intelligent.intelligentstdyplanner.Repository;

import com.intelligent.intelligentstdyplanner.Model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectReposoroty extends JpaRepository<Subject,Long> {

}
