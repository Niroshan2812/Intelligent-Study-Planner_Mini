package com.intelligent.intelligentstdyplanner.Service;

import com.intelligent.intelligentstdyplanner.DTO.StudentDTO;
import com.intelligent.intelligentstdyplanner.Model.Student;
import com.intelligent.intelligentstdyplanner.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentSearvice {

    private final StudentRepository studentRepository;

    public StudentSearvice(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public StudentDTO createNewStudent(StudentDTO studentDTO) {
        Student student = new Student();
        student.setName(studentDTO.getName());
        student.setCommuteFatigue(studentDTO.getCommuteFatigue());
        student.setTuitionHoursWeekly(studentDTO.getTuitionHoursWeekly());
        student.setEnglishFluency(studentDTO.getEnglishFluency());

        Student savedStudent = studentRepository.save(student);
        return convertDTO(savedStudent);
    }

    public List<StudentDTO> getAllStudents(){
        return studentRepository.findAll()
                .stream()
                .map(this::convertDTO)
                .collect(Collectors.toList());
    }

    private StudentDTO convertDTO(Student student) {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setName(student.getName());
        studentDTO.setTuitionHoursWeekly(student.getTuitionHoursWeekly());
        studentDTO.setCommuteFatigue(student.getCommuteFatigue());
        studentDTO.setEnglishFluency(student.getEnglishFluency());
        return studentDTO;
    }
}
