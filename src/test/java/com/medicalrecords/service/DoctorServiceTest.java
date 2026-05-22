package com.medicalrecords.service;

import com.medicalrecords.exception.ResourceNotFoundException;
import com.medicalrecords.model.Doctor;
import com.medicalrecords.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctor = new Doctor("GP001", "Dr. Test", "General Practice", true);
        doctor.setId(1L);
    }

    @Test
    void findAll_returnsAllDoctors() {
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        List<Doctor> result = doctorService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Dr. Test");
    }

    @Test
    void findById_existingId_returnsDoctor() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        Doctor result = doctorService.findById(1L);
        assertThat(result.getIdentificationNumber()).isEqualTo("GP001");
    }

    @Test
    void findById_missingId_throwsResourceNotFoundException() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> doctorService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void save_newDoctor_generatesIdentificationNumber() {
        Doctor newDoctor = new Doctor("Dr. New", "Cardiology", false);
        when(doctorRepository.countByIdentificationNumberStartingWith("CAR")).thenReturn(0L);
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(i -> i.getArgument(0));

        Doctor result = doctorService.save(newDoctor);

        assertThat(result.getIdentificationNumber()).isEqualTo("CAR001");
        verify(doctorRepository).save(newDoctor);
    }

    @Test
    void save_multiWordSpecialty_buildsInitialsPrefix() {
        Doctor newDoctor = new Doctor("Dr. Family", "General Practice", true);
        when(doctorRepository.countByIdentificationNumberStartingWith("GP")).thenReturn(2L);
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(i -> i.getArgument(0));

        Doctor result = doctorService.save(newDoctor);

        assertThat(result.getIdentificationNumber()).isEqualTo("GP003");
    }

    @Test
    void save_doctorWithExistingIdNumber_doesNotOverwrite() {
        Doctor existing = new Doctor("CUSTOM01", "Dr. Custom", "Neurology", false);
        when(doctorRepository.save(existing)).thenReturn(existing);

        Doctor result = doctorService.save(existing);

        assertThat(result.getIdentificationNumber()).isEqualTo("CUSTOM01");
        verify(doctorRepository, never()).countByIdentificationNumberStartingWith(any());
    }

    @Test
    void update_preservesIdentificationNumber() {
        Doctor updated = new Doctor("Dr. Updated", "Cardiology", false);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(i -> i.getArgument(0));

        Doctor result = doctorService.update(1L, updated);

        assertThat(result.getIdentificationNumber()).isEqualTo("GP001");
        assertThat(result.getName()).isEqualTo("Dr. Updated");
        assertThat(result.getSpecialty()).isEqualTo("Cardiology");
    }

    @Test
    void delete_existingDoctor_deletesSuccessfully() {
        when(doctorRepository.existsById(1L)).thenReturn(true);
        doctorService.delete(1L);
        verify(doctorRepository).deleteById(1L);
    }

    @Test
    void delete_missingDoctor_throwsResourceNotFoundException() {
        when(doctorRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> doctorService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findAllGps_returnsOnlyGps() {
        when(doctorRepository.findByCanBeGpTrue()).thenReturn(List.of(doctor));
        List<Doctor> gps = doctorService.findAllGps();
        assertThat(gps).allMatch(Doctor::isCanBeGp);
    }
}
