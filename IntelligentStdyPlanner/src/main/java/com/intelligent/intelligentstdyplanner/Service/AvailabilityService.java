package com.intelligent.intelligentstdyplanner.Service;

import com.intelligent.intelligentstdyplanner.DTO.AvailabilityDTO;
import com.intelligent.intelligentstdyplanner.Model.Availability;
import com.intelligent.intelligentstdyplanner.Model.Student;
import com.intelligent.intelligentstdyplanner.Repository.AvailabilityRepository;
import com.intelligent.intelligentstdyplanner.Repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final StudentRepository studentRepository;

    public AvailabilityService(AvailabilityRepository availabilityRepository, StudentRepository studentRepository) {
        this.availabilityRepository = availabilityRepository;
        this.studentRepository = studentRepository;
    }

    public AvailabilityDTO createAvailability(AvailabilityDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Availability availability = new Availability();
        availability.setDayOfWeek(dto.getDayOfWeek());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        availability.setStudent(student);

        Availability savedAvailability = availabilityRepository.save(availability);
        return convertToDTO(savedAvailability);
    }

    public List<AvailabilityDTO> getAvailabilityByStudent(Long studentId) {
        return availabilityRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AvailabilityDTO convertToDTO(Availability availability) {
        AvailabilityDTO dto = new AvailabilityDTO();
        dto.setId(availability.getId());
        dto.setDayOfWeek(availability.getDayOfWeek());
        dto.setStartTime(availability.getStartTime());
        dto.setEndTime(availability.getEndTime());
        dto.setStudentId(availability.getStudent().getId());
        return dto;
    }
}
