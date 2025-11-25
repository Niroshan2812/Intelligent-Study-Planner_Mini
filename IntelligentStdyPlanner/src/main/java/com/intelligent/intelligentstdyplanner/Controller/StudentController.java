package com.intelligent.intelligentstdyplanner.Controller;

import com.intelligent.intelligentstdyplanner.DTO.StudentDTO;
import com.intelligent.intelligentstdyplanner.Model.Student;
import com.intelligent.intelligentstdyplanner.Repository.StudentRepository;
import com.intelligent.intelligentstdyplanner.Service.StudentSearvice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentSearvice  studentSearvice;

    public StudentController(StudentSearvice studentSearvice) {
        this.studentSearvice = studentSearvice;
    }


    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO dto) {
       return ResponseEntity.ok(studentSearvice.createNewStudent(dto));
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents(){
        return ResponseEntity.ok(studentSearvice.getAllStudents());
    }

    // convert into DTO

}

