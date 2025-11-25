package com.intelligent.intelligentstdyplanner.Controller;

import com.intelligent.intelligentstdyplanner.DTO.StudentDTO;
import com.intelligent.intelligentstdyplanner.Model.Student;
import com.intelligent.intelligentstdyplanner.Repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO dto) {
        Student student = new Student();
        student.setName(dto.getName());
        student.setEnglishFluency(dto.getEnglishFluency());
        student.setTuitionHoursWeekly(dto.getTuitionHoursWeekly());
        student.setCommuteFatigue(dto.getCommuteFatigue());

        Student savedStudent = studentRepository.save(student);

        dto.setId(savedStudent.getId());
        return ResponseEntity.ok(dto);
    }
}
