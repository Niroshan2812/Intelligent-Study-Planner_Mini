package com.intelligent.intelligentstdyplanner.Service;

import com.intelligent.intelligentstdyplanner.DTO.ExamDTO;
import com.intelligent.intelligentstdyplanner.Model.Exam;
import com.intelligent.intelligentstdyplanner.Model.Subject;
import com.intelligent.intelligentstdyplanner.Repository.ExamRepository;
import com.intelligent.intelligentstdyplanner.Repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;

    public ExamService(ExamRepository examRepository, SubjectRepository subjectRepository) {
        this.examRepository = examRepository;
        this.subjectRepository = subjectRepository;
    }

    public ExamDTO createExam(ExamDTO dto) {
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        Exam exam = new Exam();
        exam.setName(dto.getName());
        exam.setDeadline(dto.getDeadline());
        exam.setSubject(subject);

        Exam savedExam = examRepository.save(exam);
        return convertToDTO(savedExam);
    }

    public List<ExamDTO> getExamsByStudent(Long studentId) {
        return examRepository.findAll().stream()
                .filter(e -> e.getSubject().getStudent().getId().equals(studentId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ExamDTO convertToDTO(Exam exam) {
        ExamDTO dto = new ExamDTO();
        dto.setId(exam.getExam_id());
        dto.setName(exam.getName());
        dto.setDeadline(exam.getDeadline());
        dto.setSubjectId(exam.getSubject().getSubject_id());
        return dto;
    }
}
