package com.medicalrecords.repository;

import com.medicalrecords.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ExaminationRepositoryIntegrationTest {

    @Autowired private ExaminationRepository examinationRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private DiagnosisRepository diagnosisRepository;

    private Doctor gp1;
    private Doctor gp2;
    private Patient insuredPatient;
    private Patient uninsuredPatient;
    private Diagnosis flu;
    private Diagnosis hypertension;

    @BeforeEach
    void setUp() {
        gp1 = doctorRepository.save(new Doctor("GP001", "Dr. First", "General Practice", true));
        gp2 = doctorRepository.save(new Doctor("GP002", "Dr. Second", "Cardiology", false));

        insuredPatient   = patientRepository.save(new Patient("Insured Person",   "9001011234", gp1, true));
        uninsuredPatient = patientRepository.save(new Patient("Uninsured Person", "8505052345", gp1, false));

        flu          = diagnosisRepository.save(new Diagnosis("J06", "Upper respiratory infection"));
        hypertension = diagnosisRepository.save(new Diagnosis("I10", "Essential hypertension"));

        // insuredPatient has NHIF coverage → price NOT counted as patient-paid
        examinationRepository.save(new Examination(
                LocalDate.of(2026, 1, 10), gp1, insuredPatient, flu, "Rest", new BigDecimal("30.00")));
        examinationRepository.save(new Examination(
                LocalDate.of(2026, 2, 14), gp2, insuredPatient, hypertension, "Medication", new BigDecimal("50.00")));

        // uninsuredPatient pays out of pocket
        examinationRepository.save(new Examination(
                LocalDate.of(2026, 3, 5), gp1, uninsuredPatient, flu, "Antibiotics", new BigDecimal("25.00")));
        examinationRepository.save(new Examination(
                LocalDate.of(2026, 4, 1), gp1, uninsuredPatient, flu, "Follow-up", new BigDecimal("15.00")));
    }

    @Test
    void findByPatient_returnsOnlyThatPatientsExaminations() {
        List<Examination> result = examinationRepository.findByPatient(uninsuredPatient);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(e -> e.getPatient().getId().equals(uninsuredPatient.getId()));
    }

    @Test
    void findByDoctor_returnsOnlyThatDoctorsExaminations() {
        List<Examination> result = examinationRepository.findByDoctor(gp2);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDiagnosis().getCode()).isEqualTo("I10");
    }

    @Test
    void findByDiagnosis_returnsAllExaminationsWithThatDiagnosis() {
        List<Examination> result = examinationRepository.findByDiagnosis(flu);

        assertThat(result).hasSize(3);
    }

    @Test
    void findPatientHistory_returnsInDescendingDateOrder() {
        List<Examination> history = examinationRepository.findPatientHistory(uninsuredPatient);

        assertThat(history).hasSize(2);
        assertThat(history.get(0).getDate()).isAfterOrEqualTo(history.get(1).getDate());
    }

    @Test
    void sumPatientPaidExaminations_onlyCountsUninsuredPatients() {
        BigDecimal total = examinationRepository.sumPatientPaidExaminations();

        // Only uninsured patient's examinations: 25.00 + 15.00 = 40.00
        assertThat(total).isEqualByComparingTo("40.00");
    }

    @Test
    void sumPatientPaidExaminations_allInsured_returnsNull() {
        examinationRepository.deleteAll();
        examinationRepository.save(new Examination(
                LocalDate.now(), gp1, insuredPatient, flu, "Test", new BigDecimal("20.00")));

        BigDecimal total = examinationRepository.sumPatientPaidExaminations();
        assertThat(total).isNull();
    }

    @Test
    void sumPatientPaidByDoctor_onlyCountsThatDoctorsUninsuredExaminations() {
        // gp1 treated uninsuredPatient twice: 25.00 + 15.00 = 40.00
        BigDecimal gp1Total = examinationRepository.sumPatientPaidByDoctor(gp1);
        assertThat(gp1Total).isEqualByComparingTo("40.00");

        // gp2 only treated insuredPatient → 0
        BigDecimal gp2Total = examinationRepository.sumPatientPaidByDoctor(gp2);
        assertThat(gp2Total).isNull();
    }

    @Test
    void countByDoctor_returnsCorrectVisitCount() {
        assertThat(examinationRepository.countByDoctor(gp1)).isEqualTo(3);
        assertThat(examinationRepository.countByDoctor(gp2)).isEqualTo(1);
    }

    @Test
    void findDiagnosisFrequency_mostFrequentFirst() {
        List<Object[]> freq = examinationRepository.findDiagnosisFrequency();

        assertThat(freq).isNotEmpty();
        Diagnosis mostFrequent = (Diagnosis) freq.get(0)[0];
        // flu appears 3 times, hypertension once
        assertThat(mostFrequent.getCode()).isEqualTo("J06");
    }

    @Test
    void findByDateBetween_returnsOnlyExaminationsInRange() {
        List<Examination> result = examinationRepository.findByDateBetween(
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 3, 31));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(e ->
                !e.getDate().isBefore(LocalDate.of(2026, 2, 1)) &&
                !e.getDate().isAfter(LocalDate.of(2026, 3, 31)));
    }

    @Test
    void findByDoctorAndDateBetween_filtersOnBothCriteria() {
        List<Examination> result = examinationRepository.findByDoctorAndDateBetween(
                gp1,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 3, 31));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(e -> e.getDoctor().getId().equals(gp1.getId()));
    }

    @Test
    void paidByNhif_setBasedOnPatientInsuranceStatus() {
        Examination insuredExam = examinationRepository.findByPatient(insuredPatient).get(0);
        Examination uninsuredExam = examinationRepository.findByPatient(uninsuredPatient).get(0);

        assertThat(insuredExam.isPaidByNhif()).isTrue();
        assertThat(uninsuredExam.isPaidByNhif()).isFalse();
    }
}
