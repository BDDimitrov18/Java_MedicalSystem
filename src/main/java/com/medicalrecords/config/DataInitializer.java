package com.medicalrecords.config;

import com.medicalrecords.model.*;
import com.medicalrecords.repository.*;
import com.medicalrecords.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorService doctorService;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final ExaminationRepository examinationRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        Doctor gp1 = doctorService.save(new Doctor("Dr. Ivan Petrov", "General Practice", true));
        Doctor gp2 = doctorService.save(new Doctor("Dr. Maria Georgieva", "General Practice", true));
        Doctor spec1 = doctorService.save(new Doctor("Dr. Todor Dimitrov", "Cardiology", false));
        Doctor spec2 = doctorService.save(new Doctor("Dr. Elena Ivanova", "Neurology", false));

        Patient p1 = patientRepository.save(new Patient("Georgi Stoev", "9001011234", gp1, true));
        Patient p2 = patientRepository.save(new Patient("Ana Kostadinova", "9205152345", gp1, false));
        Patient p3 = patientRepository.save(new Patient("Petar Nikolov", "8811203456", gp2, true));
        Patient p4 = patientRepository.save(new Patient("Iva Todorova", "9507074567", gp2, true));

        Diagnosis d1 = diagnosisRepository.save(new Diagnosis("J06", "Acute upper respiratory infection"));
        Diagnosis d2 = diagnosisRepository.save(new Diagnosis("I10", "Essential hypertension"));
        Diagnosis d3 = diagnosisRepository.save(new Diagnosis("G43", "Migraine"));
        Diagnosis d4 = diagnosisRepository.save(new Diagnosis("J18", "Pneumonia"));

        Examination e1 = examinationRepository.save(new Examination(
                LocalDate.of(2026, 1, 10), gp1, p1, d1, "Rest, fluids, paracetamol", new BigDecimal("20.00")));
        Examination e2 = examinationRepository.save(new Examination(
                LocalDate.of(2026, 2, 14), spec1, p1, d2, "Lisinopril 10mg daily", new BigDecimal("50.00")));
        Examination e3 = examinationRepository.save(new Examination(
                LocalDate.of(2026, 3, 5), gp1, p2, d1, "Antibiotics", new BigDecimal("20.00")));
        Examination e4 = examinationRepository.save(new Examination(
                LocalDate.of(2026, 3, 20), spec2, p3, d3, "Sumatriptan", new BigDecimal("60.00")));
        Examination e5 = examinationRepository.save(new Examination(
                LocalDate.of(2026, 4, 2), gp2, p4, d4, "Amoxicillin", new BigDecimal("25.00")));
        examinationRepository.save(new Examination(
                LocalDate.of(2026, 4, 15), gp1, p1, d1, "Symptomatic treatment", new BigDecimal("20.00")));

        sickLeaveRepository.save(new SickLeave(e1, LocalDate.of(2026, 1, 10), 5));
        sickLeaveRepository.save(new SickLeave(e3, LocalDate.of(2026, 3, 5), 7));
        sickLeaveRepository.save(new SickLeave(e4, LocalDate.of(2026, 3, 20), 3));
        sickLeaveRepository.save(new SickLeave(e5, LocalDate.of(2026, 4, 2), 10));

        User admin = new User("admin", passwordEncoder.encode("admin123"), Role.ADMIN);
        userRepository.save(admin);

        User doctorUser1 = new User("dr.petrov", passwordEncoder.encode("doctor123"), Role.DOCTOR);
        doctorUser1.setDoctor(gp1);
        userRepository.save(doctorUser1);

        User doctorUser2 = new User("dr.georgieva", passwordEncoder.encode("doctor123"), Role.DOCTOR);
        doctorUser2.setDoctor(gp2);
        userRepository.save(doctorUser2);

        User patientUser1 = new User("g.stoev", passwordEncoder.encode("patient123"), Role.PATIENT);
        patientUser1.setPatient(p1);
        userRepository.save(patientUser1);

        User patientUser2 = new User("a.kostadinova", passwordEncoder.encode("patient123"), Role.PATIENT);
        patientUser2.setPatient(p2);
        userRepository.save(patientUser2);
    }
}
