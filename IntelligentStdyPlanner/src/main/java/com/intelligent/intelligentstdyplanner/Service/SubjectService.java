package com.intelligent.intelligentstdyplanner.Service;

import com.intelligent.intelligentstdyplanner.DTO.SubjectDTO;
import com.intelligent.intelligentstdyplanner.Model.Subject;
import com.intelligent.intelligentstdyplanner.Repository.SubjectReposoroty;
import org.springframework.stereotype.Service;

@Service
public class SubjectService {
    private final SubjectReposoroty subrepository;

    public SubjectService(SubjectReposoroty subrepository) {
        this.subrepository = subrepository;
    }

    public Subject saveSubject (SubjectDTO subjectDTO) {
        Subject subject = new Subject();

        subject.setName(subjectDTO.getName());
        subject.setDifficaltyLevel(subjectDTO.getDifficaltyLevel());
        subject.setCurrentScore(subjectDTO.getCurrentScore());

        return subrepository.save(subject);
    }
}
