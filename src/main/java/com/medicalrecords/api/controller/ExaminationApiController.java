package com.medicalrecords.api.controller;

import com.medicalrecords.api.dto.ExaminationDto;
import com.medicalrecords.api.dto.ExaminationRequest;
import com.medicalrecords.config.CurrentUserHelper;
import com.medicalrecords.model.*;
import com.medicalrecords.service.DiagnosisService;
import com.medicalrecords.service.DoctorService;
import com.medicalrecords.service.ExaminationService;
import com.medicalrecords.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/examinations")
@RequiredArgsConstructor
public class ExaminationApiController {

    private final ExaminationService examinationService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final CurrentUserHelper currentUserHelper;

    @GetMapping
    public List<ExaminationDto> list() {
        User currentUser = currentUserHelper.getCurrentUser();
        List<Examination> examinations;
        if (currentUser.getRole() == Role.PATIENT) {
            examinations = examinationService.findPatientHistory(currentUser.getPatient());
        } else if (currentUser.getRole() == Role.DOCTOR) {
            examinations = examinationService.findByDoctor(currentUser.getDoctor());
        } else {
            examinations = examinationService.findAll();
        }
        return examinations.stream().map(ExaminationDto::from).toList();
    }

    @GetMapping("/{id}")
    public ExaminationDto getById(@PathVariable Long id) {
        return ExaminationDto.from(examinationService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ExaminationDto> create(@Valid @RequestBody ExaminationRequest req) {
        User currentUser = currentUserHelper.getCurrentUser();
        Doctor doctor = currentUser.getRole() == Role.DOCTOR
                ? currentUser.getDoctor()
                : doctorService.findById(req.doctorId());
        Patient patient = patientService.findById(req.patientId());
        Diagnosis diagnosis = diagnosisService.findById(req.diagnosisId());
        Examination examination = new Examination(
                req.date(),
                doctor,
                patient,
                diagnosis,
                req.prescribedTreatment(),
                req.price() != null ? req.price() : BigDecimal.ZERO
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ExaminationDto.from(examinationService.save(examination)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ExaminationDto update(@PathVariable Long id, @Valid @RequestBody ExaminationRequest req) {
        User currentUser = currentUserHelper.getCurrentUser();
        Doctor doctorCtx = currentUser.getRole() == Role.DOCTOR ? currentUser.getDoctor() : null;
        Diagnosis diagnosis = diagnosisService.findById(req.diagnosisId());
        Examination updated = new Examination();
        updated.setDate(req.date());
        updated.setDiagnosis(diagnosis);
        updated.setPrescribedTreatment(req.prescribedTreatment());
        updated.setPrice(req.price() != null ? req.price() : BigDecimal.ZERO);
        return ExaminationDto.from(examinationService.update(id, updated, doctorCtx));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        User currentUser = currentUserHelper.getCurrentUser();
        Examination examination = examinationService.findById(id);
        if (currentUser.getRole() == Role.DOCTOR
                && !examination.getDoctor().getId().equals(currentUser.getDoctor().getId())) {
            throw new com.medicalrecords.exception.AccessDeniedException("You can only delete your own examinations");
        }
        examinationService.delete(id);
    }
}
