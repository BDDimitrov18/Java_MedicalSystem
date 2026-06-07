package com.medicalrecords.api.controller;

import com.medicalrecords.api.dto.DiagnosisDto;
import com.medicalrecords.api.dto.DoctorDto;
import com.medicalrecords.api.dto.ExaminationDto;
import com.medicalrecords.api.dto.PatientDto;
import com.medicalrecords.model.Diagnosis;
import com.medicalrecords.model.Doctor;
import com.medicalrecords.service.DiagnosisService;
import com.medicalrecords.service.DoctorService;
import com.medicalrecords.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/statistics")
@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
@RequiredArgsConstructor
public class StatisticsApiController {

    private final StatisticsService statisticsService;
    private final DoctorService doctorService;
    private final DiagnosisService diagnosisService;

    @GetMapping("/most-frequent-diagnosis")
    public DiagnosisDto mostFrequentDiagnosis() {
        Diagnosis d = statisticsService.findMostFrequentDiagnosis();
        return d != null ? DiagnosisDto.from(d) : null;
    }

    @GetMapping("/total-patient-paid")
    public BigDecimal totalPatientPaid() {
        return statisticsService.totalPatientPaidAmount();
    }

    @GetMapping("/paid-by-doctor")
    public Map<Long, BigDecimal> paidByDoctor() {
        return doctorService.findAll().stream()
                .collect(Collectors.toMap(
                        Doctor::getId,
                        statisticsService::patientPaidAmountByDoctor
                ));
    }

    @GetMapping("/patients-by-diagnosis")
    public List<PatientDto> patientsByDiagnosis(@RequestParam Long diagnosisId) {
        Diagnosis diagnosis = diagnosisService.findById(diagnosisId);
        return statisticsService.findPatientsByDiagnosis(diagnosis).stream().map(PatientDto::from).toList();
    }

    @GetMapping("/patients-by-gp")
    public List<PatientDto> patientsByGp(@RequestParam Long doctorId) {
        Doctor gp = doctorService.findById(doctorId);
        return statisticsService.findPatientsByGp(gp).stream().map(PatientDto::from).toList();
    }

    @GetMapping("/examinations-by-doctor-period")
    public List<ExaminationDto> examinationsByDoctorAndPeriod(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Doctor doctor = doctorId != null ? doctorService.findById(doctorId) : null;
        return statisticsService.examinationsByDoctorAndPeriod(doctor, from, to)
                .stream().map(ExaminationDto::from).toList();
    }

    @GetMapping("/patient-count-per-gp")
    public Map<Long, Long> patientCountPerGp() {
        List<Doctor> gps = doctorService.findAllGps();
        return statisticsService.patientCountPerGp(gps).entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));
    }

    @GetMapping("/visit-count-per-doctor")
    public Map<Long, Long> visitCountPerDoctor() {
        List<Doctor> doctors = doctorService.findAll();
        return statisticsService.visitCountPerDoctor(doctors).entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));
    }

    @GetMapping("/month-most-sick-leaves")
    public String monthWithMostSickLeaves() {
        return statisticsService.findMonthWithMostSickLeaves();
    }

    @GetMapping("/doctors-most-sick-leaves")
    public List<DoctorDto> doctorsWithMostSickLeaves() {
        return statisticsService.findDoctorsWithMostSickLeaves().stream().map(DoctorDto::from).toList();
    }
}
