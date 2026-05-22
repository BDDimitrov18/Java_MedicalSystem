package com.medicalrecords.service;

import com.medicalrecords.exception.AccessDeniedException;
import com.medicalrecords.exception.ResourceNotFoundException;
import com.medicalrecords.model.*;
import com.medicalrecords.repository.ExaminationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExaminationServiceTest {

    @Mock
    private ExaminationRepository examinationRepository;

    @InjectMocks
    private ExaminationService examinationService;

    private Doctor doctor1;
    private Doctor doctor2;
    private Patient patient;
    private Diagnosis diagnosis;
    private Examination examination;

    @BeforeEach
    void setUp() {
        doctor1 = new Doctor("GP001", "Dr. One", "General Practice", true);
        doctor1.setId(1L);
        doctor2 = new Doctor("SP001", "Dr. Two", "Cardiology", false);
        doctor2.setId(2L);

        patient = new Patient("Test Patient", "9001011234", doctor1, true);
        patient.setId(1L);

        diagnosis = new Diagnosis("J06", "Upper respiratory infection");
        diagnosis.setId(1L);

        examination = new Examination(LocalDate.now(), doctor1, patient, diagnosis, "Rest", new BigDecimal("30.00"));
        examination.setId(1L);
    }

    @Test
    void findById_existing_returnsExamination() {
        when(examinationRepository.findById(1L)).thenReturn(Optional.of(examination));
        Examination result = examinationService.findById(1L);
        assertThat(result.getPrice()).isEqualByComparingTo("30.00");
    }

    @Test
    void findById_missing_throwsException() {
        when(examinationRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> examinationService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_ownExamination_succeeds() {
        when(examinationRepository.findById(1L)).thenReturn(Optional.of(examination));
        when(examinationRepository.save(any())).thenReturn(examination);

        Examination updated = new Examination(LocalDate.now(), doctor1, patient, diagnosis, "Updated", new BigDecimal("40.00"));
        Examination result = examinationService.update(1L, updated, doctor1);
        assertThat(result).isNotNull();
    }

    @Test
    void update_anotherDoctorsExamination_throwsAccessDenied() {
        when(examinationRepository.findById(1L)).thenReturn(Optional.of(examination));

        Examination updated = new Examination(LocalDate.now(), doctor2, patient, diagnosis, "Hack", new BigDecimal("999.00"));
        assertThatThrownBy(() -> examinationService.update(1L, updated, doctor2))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void findByPatient_returnsList() {
        when(examinationRepository.findByPatient(patient)).thenReturn(List.of(examination));
        assertThat(examinationService.findByPatient(patient)).hasSize(1);
    }

    @Test
    void save_persistsExamination() {
        when(examinationRepository.save(examination)).thenReturn(examination);
        assertThat(examinationService.save(examination)).isEqualTo(examination);
    }
}
