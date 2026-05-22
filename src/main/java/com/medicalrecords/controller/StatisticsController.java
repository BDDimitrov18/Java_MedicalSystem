package com.medicalrecords.controller;

import com.medicalrecords.model.Doctor;
import com.medicalrecords.model.Diagnosis;
import com.medicalrecords.service.DiagnosisService;
import com.medicalrecords.service.DoctorService;
import com.medicalrecords.service.PatientService;
import com.medicalrecords.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/statistics")
@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final DoctorService doctorService;
    private final DiagnosisService diagnosisService;
    private final PatientService patientService;

    @GetMapping
    public String index(Model model) {
        List<Doctor> allDoctors = doctorService.findAll();
        List<Doctor> allGps = doctorService.findAllGps();

        model.addAttribute("mostFrequentDiagnosis", statisticsService.findMostFrequentDiagnosis());
        model.addAttribute("totalPatientPaid", statisticsService.totalPatientPaidAmount());
        model.addAttribute("patientCountPerGp", statisticsService.patientCountPerGp(allGps));
        model.addAttribute("visitCountPerDoctor", statisticsService.visitCountPerDoctor(allDoctors));
        model.addAttribute("monthWithMostSickLeaves", statisticsService.findMonthWithMostSickLeaves());
        model.addAttribute("doctorsWithMostSickLeaves", statisticsService.findDoctorsWithMostSickLeaves());
        model.addAttribute("doctors", allDoctors);
        model.addAttribute("diagnoses", diagnosisService.findAll());
        model.addAttribute("gps", allGps);
        return "statistics/index";
    }

    @GetMapping("/by-diagnosis")
    public String byDiagnosis(@RequestParam(required = false) Long diagnosisId, Model model) {
        model.addAttribute("diagnoses", diagnosisService.findAll());
        if (diagnosisId != null) {
            Diagnosis diagnosis = diagnosisService.findById(diagnosisId);
            model.addAttribute("selectedDiagnosis", diagnosis);
            model.addAttribute("patients", statisticsService.findPatientsByDiagnosis(diagnosis));
        }
        return "statistics/by-diagnosis";
    }

    @GetMapping("/by-gp")
    public String byGp(@RequestParam(required = false) Long doctorId, Model model) {
        model.addAttribute("gps", doctorService.findAllGps());
        if (doctorId != null) {
            Doctor gp = doctorService.findById(doctorId);
            model.addAttribute("selectedGp", gp);
            model.addAttribute("patients", statisticsService.findPatientsByGp(gp));
        }
        return "statistics/by-gp";
    }

    @GetMapping("/by-doctor-period")
    public String byDoctorAndPeriod(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Model model) {

        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("fromDate", from);
        model.addAttribute("toDate", to);

        Doctor doctor = doctorId != null ? doctorService.findById(doctorId) : null;
        model.addAttribute("selectedDoctor", doctor);
        model.addAttribute("examinations", statisticsService.examinationsByDoctorAndPeriod(doctor, from, to));

        return "statistics/by-doctor-period";
    }

    @GetMapping("/paid-by-doctor")
    public String paidByDoctor(Model model) {
        List<Doctor> doctors = doctorService.findAll();
        model.addAttribute("doctors", doctors);
        model.addAttribute("paidAmounts", doctors.stream()
                .collect(java.util.stream.Collectors.toMap(
                        d -> d,
                        d -> statisticsService.patientPaidAmountByDoctor(d)
                )));
        return "statistics/paid-by-doctor";
    }
}
