package com.medicalrecords.repository;

import com.medicalrecords.model.Doctor;
import com.medicalrecords.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class PatientRepositoryIntegrationTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    private Doctor gp1;
    private Doctor gp2;

    @BeforeEach
    void setUp() {
        gp1 = doctorRepository.save(new Doctor("GP001", "Dr. First GP", "General Practice", true));
        gp2 = doctorRepository.save(new Doctor("GP002", "Dr. Second GP", "General Practice", true));

        patientRepository.save(new Patient("Alice Smith", "9001011234", gp1, true));
        patientRepository.save(new Patient("Bob Jones", "8505052345", gp1, false));
        patientRepository.save(new Patient("Carol White", "9210103456", gp2, true));
    }

    @Test
    void findByPersonalDoctor_returnsOnlyPatientsOfThatDoctor() {
        List<Patient> patients = patientRepository.findByPersonalDoctor(gp1);

        assertThat(patients).hasSize(2);
        assertThat(patients).extracting(Patient::getName)
                .containsExactlyInAnyOrder("Alice Smith", "Bob Jones");
    }

    @Test
    void findByPersonalDoctor_doctorWithNoPatients_returnsEmpty() {
        Doctor newGp = doctorRepository.save(new Doctor("GP003", "Dr. New GP", "General Practice", true));

        assertThat(patientRepository.findByPersonalDoctor(newGp)).isEmpty();
    }

    @Test
    void countByPersonalDoctor_returnsCorrectCount() {
        assertThat(patientRepository.countByPersonalDoctor(gp1)).isEqualTo(2);
        assertThat(patientRepository.countByPersonalDoctor(gp2)).isEqualTo(1);
    }

    @Test
    void findByEgn_existingEgn_returnsPatient() {
        var result = patientRepository.findByEgn("9001011234");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Alice Smith");
    }

    @Test
    void findByEgn_unknownEgn_returnsEmpty() {
        assertThat(patientRepository.findByEgn("0000000000")).isEmpty();
    }

    @Test
    void existsByEgn_existing_returnsTrue() {
        assertThat(patientRepository.existsByEgn("9001011234")).isTrue();
    }

    @Test
    void existsByEgn_missing_returnsFalse() {
        assertThat(patientRepository.existsByEgn("9999999999")).isFalse();
    }

    @Test
    void save_duplicateEgn_throwsDataIntegrityViolation() {
        Patient duplicate = new Patient("Duplicate Person", "9001011234", gp2, false);

        assertThatThrownBy(() -> {
            patientRepository.save(duplicate);
            patientRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void healthInsuranceStatus_persistedCorrectly() {
        Patient insured = patientRepository.findByEgn("9001011234").orElseThrow();
        Patient uninsured = patientRepository.findByEgn("8505052345").orElseThrow();

        assertThat(insured.isHealthInsured()).isTrue();
        assertThat(uninsured.isHealthInsured()).isFalse();
    }
}
