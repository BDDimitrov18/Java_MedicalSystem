package com.medicalrecords.api.controller;

import com.medicalrecords.api.dto.DiagnosisDto;
import com.medicalrecords.api.dto.DiagnosisRequest;
import com.medicalrecords.model.Diagnosis;
import com.medicalrecords.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/diagnoses")
@RequiredArgsConstructor
public class DiagnosisApiController {

    private final DiagnosisService diagnosisService;

    @GetMapping
    public List<DiagnosisDto> list() {
        return diagnosisService.findAll().stream().map(DiagnosisDto::from).toList();
    }

    @GetMapping("/{id}")
    public DiagnosisDto getById(@PathVariable Long id) {
        return DiagnosisDto.from(diagnosisService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiagnosisDto> create(@Valid @RequestBody DiagnosisRequest req) {
        Diagnosis diagnosis = new Diagnosis(req.code(), req.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(DiagnosisDto.from(diagnosisService.save(diagnosis)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DiagnosisDto update(@PathVariable Long id, @Valid @RequestBody DiagnosisRequest req) {
        Diagnosis updated = new Diagnosis(req.code(), req.description());
        return DiagnosisDto.from(diagnosisService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        diagnosisService.delete(id);
    }
}
