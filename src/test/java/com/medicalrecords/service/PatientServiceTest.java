package com.medicalrecords.service;

import com.medicalrecords.exception.DuplicateResourceException;
import com.medicalrecords.exception.ResourceNotFoundException;
import com.medicalrecords.model.Doctor;
import com.medicalrecords.model.Patient;
import com.medicalrecords.repository.DoctorRepository;
import com.medicalrecords.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private PatientService patientService;

    private Doctor gp;
    private Patient patient;

    @BeforeEach
    void setUp() {
        gp = new Doctor("GP001", "Dr. Test", "General Practice", true);
        gp.setId(1L);
        patient = new Patient("Test Patient", "9001011234", gp, true);
        patient.setId(1L);
    }

    @Test
    void findAll_returnsAllPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));
        assertThat(patientService.findAll()).hasSize(1);
    }

    @Test
    void findById_found_returnsPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        Patient result = patientService.findById(1L);
        assertThat(result.getEgn()).isEqualTo("9001011234");
    }

    @Test
    void findById_notFound_throwsException() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> patientService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void save_duplicateEgn_throwsDuplicateException() {
        Patient dup = new Patient("Dup", "9001011234", gp, false);
        when(patientRepository.existsByEgn("9001011234")).thenReturn(true);
        assertThatThrownBy(() -> patientService.save(dup))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void countByDoctor_returnCount() {
        when(patientRepository.countByPersonalDoctor(gp)).thenReturn(3L);
        assertThat(patientService.countByDoctor(gp)).isEqualTo(3L);
    }
}
