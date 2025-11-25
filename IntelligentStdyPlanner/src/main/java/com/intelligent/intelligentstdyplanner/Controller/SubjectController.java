package com.intelligent.intelligentstdyplanner.Controller;

import com.intelligent.intelligentstdyplanner.DTO.SubjectDTO;
import com.intelligent.intelligentstdyplanner.Service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<SubjectDTO> createSubject(@RequestBody SubjectDTO dto) {
        return ResponseEntity.ok(subjectService.createSubject(dto));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(subjectService.getSubjectsByStudent(studentId));
    }
}
