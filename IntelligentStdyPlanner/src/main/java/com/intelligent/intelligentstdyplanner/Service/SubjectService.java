package com.intelligent.intelligentstdyplanner.Service;

import com.intelligent.intelligentstdyplanner.DTO.SubjectDTO;
import com.intelligent.intelligentstdyplanner.Model.Subject;
import com.intelligent.intelligentstdyplanner.Repository.SubjectReposoroty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {
    private final SubjectReposoroty subrepository;

    public SubjectService(SubjectReposoroty subrepository) {
        this.subrepository = subrepository;
    }

    // Create
    public SubjectDTO createSubject(SubjectDTO dto) {
        Subject subject = new Subject();
        subject.setName(dto.getName());
        subject.setDifficaltyLevel(dto.getDifficaltyLevel());
        subject.setCurrentScore(dto.getCurrentScore());

        Subject savedSubject = subrepository.save(subject);
        return convertToDTO(savedSubject);
    }

    // Read one
    public SubjectDTO getsubjectById(Long id){
        Subject subject = subrepository.findById(id)
                .orElseThrow(()->new RuntimeException("Subject not found with the Id" + id));
        return convertToDTO(subject);
    }

    // Read all
    public List<SubjectDTO> getAllSubjects(){
        return subrepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    // update
    public SubjectDTO updateSubject(Long id, SubjectDTO dto){
        Subject subject = subrepository.findById(id)
                .orElseThrow(()->new RuntimeException("Subject not found with the Id" + id));

        // only update fields allowed by the DTO
        subject.setName(dto.getName());
        subject.setDifficaltyLevel(dto.getDifficaltyLevel());
        subject.setCurrentScore(dto.getCurrentScore());

        Subject updatedSubject = subrepository.save(subject);
        return convertToDTO(updatedSubject);

    }

    //Delete
    public void deleteSubject(Long id){
        if(!subrepository.existsById(id)){
            throw new RuntimeException("Subject not found with the Id" + id);
        }
        subrepository.deleteById(id);
    }


    // convert entity Dto
    private SubjectDTO convertToDTO(Subject subject) {
        SubjectDTO subjectDTO = new SubjectDTO();
        // NOT INCLUDE ID HERE IF NEED ADD SUBJECT ID INTO sUBJECTdTO
        subjectDTO.setName(subject.getName());
        subjectDTO.setDifficaltyLevel(subject.getDifficaltyLevel());
        subjectDTO.setCurrentScore(subject.getCurrentScore());

        return subjectDTO;
    }
}
