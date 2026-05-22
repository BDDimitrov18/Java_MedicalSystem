package com.medicalrecords.service;

import com.medicalrecords.model.Diagnosis;
import com.medicalrecords.model.Doctor;
import com.medicalrecords.model.Examination;
import com.medicalrecords.model.Patient;
import com.medicalrecords.repository.ExaminationRepository;
import com.medicalrecords.repository.PatientRepository;
import com.medicalrecords.repository.SickLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final ExaminationRepository examinationRepository;
    private final PatientRepository patientRepository;
    private final SickLeaveRepository sickLeaveRepository;

    public List<Patient> findPatientsByDiagnosis(Diagnosis diagnosis) {
        List<Examination> examinations = examinationRepository.findByDiagnosis(diagnosis);
        Set<Patient> patients = new LinkedHashSet<>();
        for (Examination e : examinations) {
            patients.add(e.getPatient());
        }
        return new ArrayList<>(patients);
    }

    public Diagnosis findMostFrequentDiagnosis() {
        List<Object[]> results = examinationRepository.findDiagnosisFrequency();
        if (results.isEmpty()) return null;
        return (Diagnosis) results.get(0)[0];
    }

    public List<Patient> findPatientsByGp(Doctor doctor) {
        return patientRepository.findByPersonalDoctor(doctor);
    }

    public BigDecimal totalPatientPaidAmount() {
        BigDecimal total = examinationRepository.sumPatientPaidExaminations();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal patientPaidAmountByDoctor(Doctor doctor) {
        BigDecimal total = examinationRepository.sumPatientPaidByDoctor(doctor);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Map<Doctor, Long> patientCountPerGp(List<Doctor> gps) {
        Map<Doctor, Long> result = new LinkedHashMap<>();
        for (Doctor gp : gps) {
            result.put(gp, patientRepository.countByPersonalDoctor(gp));
        }
        return result;
    }

    public Map<Doctor, Long> visitCountPerDoctor(List<Doctor> doctors) {
        Map<Doctor, Long> result = new LinkedHashMap<>();
        for (Doctor doctor : doctors) {
            result.put(doctor, examinationRepository.countByDoctor(doctor));
        }
        return result;
    }

    public List<Examination> patientVisitHistory(Patient patient) {
        return examinationRepository.findPatientHistory(patient);
    }

    public List<Examination> examinationsByDoctorAndPeriod(Doctor doctor, LocalDate from, LocalDate to) {
        if (doctor != null && from != null && to != null) {
            return examinationRepository.findByDoctorAndDateBetween(doctor, from, to);
        } else if (doctor != null) {
            return examinationRepository.findByDoctor(doctor);
        } else if (from != null && to != null) {
            return examinationRepository.findByDateBetween(from, to);
        }
        return examinationRepository.findAll();
    }

    public String findMonthWithMostSickLeaves() {
        List<Object[]> results = sickLeaveRepository.findMonthWithMostSickLeaves();
        if (results.isEmpty()) return "No data";
        Object[] top = results.get(0);
        int month = ((Number) top[0]).intValue();
        int year = ((Number) top[1]).intValue();
        long count = ((Number) top[2]).longValue();
        return java.time.Month.of(month).name() + " " + year + " (" + count + " sick leaves)";
    }

    public List<Doctor> findDoctorsWithMostSickLeaves() {
        List<Object[]> results = sickLeaveRepository.findDoctorsWithMostSickLeaves();
        if (results.isEmpty()) return List.of();
        long maxCount = ((Number) results.get(0)[1]).longValue();
        List<Doctor> doctors = new ArrayList<>();
        for (Object[] row : results) {
            if (((Number) row[1]).longValue() == maxCount) {
                doctors.add((Doctor) row[0]);
            } else {
                break;
            }
        }
        return doctors;
    }
}
