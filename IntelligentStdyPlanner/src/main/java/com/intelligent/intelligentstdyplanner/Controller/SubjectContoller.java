package com.intelligent.intelligentstdyplanner.Controller;

import com.intelligent.intelligentstdyplanner.DTO.SubjectDTO;
import com.intelligent.intelligentstdyplanner.Service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin(origins = "http://localhost:3000") // allow react to talk
public class SubjectContoller {

    private final SubjectService subjectService;

    public SubjectContoller(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    //Create - > Post
    @PostMapping
    public ResponseEntity<SubjectDTO> createSubhect(@RequestBody SubjectDTO subjectDTO) {
        return ResponseEntity.ok(subjectService.createSubject(subjectDTO));
    }

    //Read all
    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    //READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getsubjectById(id));
    }

    //Update
    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(@PathVariable Long id, @RequestBody SubjectDTO subjectDTO) {
        return ResponseEntity.ok(subjectService.updateSubject(id, subjectDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok("Deleted subject");
    }
}
