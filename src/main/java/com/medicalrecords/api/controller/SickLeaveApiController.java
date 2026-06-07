package com.medicalrecords.api.controller;

import com.medicalrecords.api.dto.SickLeaveDto;
import com.medicalrecords.api.dto.SickLeaveRequest;
import com.medicalrecords.model.Examination;
import com.medicalrecords.model.SickLeave;
import com.medicalrecords.service.ExaminationService;
import com.medicalrecords.service.SickLeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sickleaves")
@RequiredArgsConstructor
public class SickLeaveApiController {

    private final SickLeaveService sickLeaveService;
    private final ExaminationService examinationService;

    @GetMapping
    public List<SickLeaveDto> list() {
        return sickLeaveService.findAll().stream().map(SickLeaveDto::from).toList();
    }

    @GetMapping("/{id}")
    public SickLeaveDto getById(@PathVariable Long id) {
        return SickLeaveDto.from(sickLeaveService.findById(id));
    }

    @GetMapping("/patient/{patientId}")
    public List<SickLeaveDto> listByPatient(@PathVariable Long patientId) {
        return sickLeaveService.findByPatientId(patientId).stream().map(SickLeaveDto::from).toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<SickLeaveDto> create(@Valid @RequestBody SickLeaveRequest req) {
        Examination examination = examinationService.findById(req.examinationId());
        SickLeave sickLeave = new SickLeave(examination, req.startDate(), req.numberOfDays());
        return ResponseEntity.status(HttpStatus.CREATED).body(SickLeaveDto.from(sickLeaveService.save(sickLeave)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public SickLeaveDto update(@PathVariable Long id, @Valid @RequestBody SickLeaveRequest req) {
        Examination examination = examinationService.findById(req.examinationId());
        SickLeave updated = new SickLeave();
        updated.setExamination(examination);
        updated.setStartDate(req.startDate());
        updated.setNumberOfDays(req.numberOfDays());
        return SickLeaveDto.from(sickLeaveService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        sickLeaveService.delete(id);
    }
}
