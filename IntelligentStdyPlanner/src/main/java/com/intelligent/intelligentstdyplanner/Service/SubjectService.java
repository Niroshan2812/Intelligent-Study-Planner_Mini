package com.intelligent.intelligentstdyplanner.Service;

import com.intelligent.intelligentstdyplanner.DTO.SubjectDTO;
import com.intelligent.intelligentstdyplanner.Model.Student;
import com.intelligent.intelligentstdyplanner.Model.Subject;
import com.intelligent.intelligentstdyplanner.Repository.StudentRepository;
import com.intelligent.intelligentstdyplanner.Repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    public SubjectService(SubjectRepository subjectRepository, StudentRepository studentRepository) {
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
    }

    public SubjectDTO createSubject(SubjectDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Subject subject = new Subject();
        subject.setName(dto.getName());
        subject.setDifficaltyLevel(dto.getDifficaltyLevel());
        subject.setCurrentScore(dto.getCurrentScore());
        subject.setStudent(student);

        Subject savedSubject = subjectRepository.save(subject);
        return convertToDTO(savedSubject);
    }

    public List<SubjectDTO> getSubjectsByStudent(Long studentId) {
        return subjectRepository.findAll().stream()
                .filter(s -> s.getStudent().getId().equals(studentId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SubjectDTO convertToDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(subject.getSubject_id());
        dto.setName(subject.getName());
        dto.setDifficaltyLevel(subject.getDifficaltyLevel());
        dto.setCurrentScore(subject.getCurrentScore());
        dto.setStudentId(subject.getStudent().getId());
        return dto;
    }
}
