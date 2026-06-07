package com.medicalrecords.api.controller;

import com.medicalrecords.api.dto.PatientDto;
import com.medicalrecords.api.dto.PatientRequest;
import com.medicalrecords.config.CurrentUserHelper;
import com.medicalrecords.exception.AccessDeniedException;
import com.medicalrecords.model.Doctor;
import com.medicalrecords.model.Patient;
import com.medicalrecords.model.Role;
import com.medicalrecords.model.User;
import com.medicalrecords.service.DoctorService;
import com.medicalrecords.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientApiController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final CurrentUserHelper currentUserHelper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public List<PatientDto> list() {
        return patientService.findAll().stream().map(PatientDto::from).toList();
    }

    @GetMapping("/{id}")
    public PatientDto getById(@PathVariable Long id) {
        User currentUser = currentUserHelper.getCurrentUser();
        if (currentUser.getRole() == Role.PATIENT && !currentUser.getPatient().getId().equals(id)) {
            throw new AccessDeniedException("You can only view your own patient record");
        }
        return PatientDto.from(patientService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientDto> create(@Valid @RequestBody PatientRequest req) {
        Doctor doctor = doctorService.findById(req.personalDoctorId());
        Patient patient = new Patient(req.name(), req.egn(), doctor, req.healthInsured());
        return ResponseEntity.status(HttpStatus.CREATED).body(PatientDto.from(patientService.save(patient)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PatientDto update(@PathVariable Long id, @Valid @RequestBody PatientRequest req) {
        Doctor doctor = new Doctor();
        doctor.setId(req.personalDoctorId());
        Patient updated = new Patient();
        updated.setName(req.name());
        updated.setEgn(req.egn());
        updated.setPersonalDoctor(doctor);
        updated.setHealthInsured(req.healthInsured());
        return PatientDto.from(patientService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        patientService.delete(id);
    }
}
