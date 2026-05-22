package com.medicalrecords.repository;

import com.medicalrecords.model.Doctor;
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
class DoctorRepositoryIntegrationTest {

    @Autowired
    private DoctorRepository doctorRepository;

    private Doctor gp;
    private Doctor specialist;

    @BeforeEach
    void setUp() {
        gp = doctorRepository.save(new Doctor("GP001", "Dr. Anna GP", "General Practice", true));
        specialist = doctorRepository.save(new Doctor("SP001", "Dr. Boris Cardio", "Cardiology", false));
    }

    @Test
    void findByCanBeGpTrue_returnsOnlyGpDoctors() {
        List<Doctor> gps = doctorRepository.findByCanBeGpTrue();

        assertThat(gps).hasSize(1);
        assertThat(gps.get(0).getIdentificationNumber()).isEqualTo("GP001");
    }

    @Test
    void findByIdentificationNumber_existingNumber_returnsDoctor() {
        var result = doctorRepository.findByIdentificationNumber("GP001");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Dr. Anna GP");
    }

    @Test
    void findByIdentificationNumber_unknownNumber_returnsEmpty() {
        assertThat(doctorRepository.findByIdentificationNumber("UNKNOWN")).isEmpty();
    }

    @Test
    void existsByIdentificationNumber_existing_returnsTrue() {
        assertThat(doctorRepository.existsByIdentificationNumber("SP001")).isTrue();
    }

    @Test
    void existsByIdentificationNumber_missing_returnsFalse() {
        assertThat(doctorRepository.existsByIdentificationNumber("MISSING")).isFalse();
    }

    @Test
    void save_duplicateIdentificationNumber_throwsDataIntegrityViolation() {
        Doctor duplicate = new Doctor("GP001", "Dr. Duplicate", "Neurology", false);

        assertThatThrownBy(() -> {
            doctorRepository.save(duplicate);
            doctorRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findAll_returnsAllSavedDoctors() {
        assertThat(doctorRepository.findAll()).hasSize(2);
    }

    @Test
    void delete_removesDoctor() {
        doctorRepository.delete(specialist);
        assertThat(doctorRepository.findAll()).hasSize(1);
        assertThat(doctorRepository.findByIdentificationNumber("SP001")).isEmpty();
    }
}
