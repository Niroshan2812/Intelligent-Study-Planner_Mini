package com.intelligent.intelligentstdyplanner.Controller;

import com.intelligent.intelligentstdyplanner.DTO.AvailabilityDTO;
import com.intelligent.intelligentstdyplanner.Service.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public ResponseEntity<AvailabilityDTO> createAvailability(@RequestBody AvailabilityDTO dto) {
        return ResponseEntity.ok(availabilityService.createAvailability(dto));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AvailabilityDTO>> getAvailabilityByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(availabilityService.getAvailabilityByStudent(studentId));
    }
}
