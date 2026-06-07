package com.medicalrecords.api.controller;

import com.medicalrecords.api.dto.DoctorDto;
import com.medicalrecords.api.dto.DoctorRequest;
import com.medicalrecords.model.Doctor;
import com.medicalrecords.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorApiController {

    private final DoctorService doctorService;

    @GetMapping
    public List<DoctorDto> list() {
        return doctorService.findAll().stream().map(DoctorDto::from).toList();
    }

    @GetMapping("/{id}")
    public DoctorDto getById(@PathVariable Long id) {
        return DoctorDto.from(doctorService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto> create(@Valid @RequestBody DoctorRequest req) {
        Doctor doctor = new Doctor();
        doctor.setIdentificationNumber(req.identificationNumber());
        doctor.setName(req.name());
        doctor.setSpecialty(req.specialty());
        doctor.setCanBeGp(req.canBeGp());
        return ResponseEntity.status(HttpStatus.CREATED).body(DoctorDto.from(doctorService.save(doctor)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorDto update(@PathVariable Long id, @Valid @RequestBody DoctorRequest req) {
        Doctor updated = new Doctor();
        updated.setName(req.name());
        updated.setSpecialty(req.specialty());
        updated.setCanBeGp(req.canBeGp());
        return DoctorDto.from(doctorService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        doctorService.delete(id);
    }
}
