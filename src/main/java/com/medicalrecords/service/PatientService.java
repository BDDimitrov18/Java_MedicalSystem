package com.medicalrecords.service;

import com.medicalrecords.exception.DuplicateResourceException;
import com.medicalrecords.exception.ResourceNotFoundException;
import com.medicalrecords.model.Doctor;
import com.medicalrecords.model.Patient;
import com.medicalrecords.repository.DoctorRepository;
import com.medicalrecords.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", id));
    }

    public Patient findByEgn(String egn) {
        return patientRepository.findByEgn(egn)
                .orElseThrow(() -> new ResourceNotFoundException("Patient with EGN " + egn + " not found"));
    }

    public List<Patient> findByDoctor(Doctor doctor) {
        return patientRepository.findByPersonalDoctor(doctor);
    }

    public long countByDoctor(Doctor doctor) {
        return patientRepository.countByPersonalDoctor(doctor);
    }

    public Patient save(Patient patient) {
        if (patient.getId() == null && patientRepository.existsByEgn(patient.getEgn())) {
            throw new DuplicateResourceException("Patient with EGN " + patient.getEgn() + " already exists");
        }
        resolveDoctor(patient);
        return patientRepository.save(patient);
    }

    public Patient update(Long id, Patient updated) {
        Patient existing = findById(id);
        if (!existing.getEgn().equals(updated.getEgn())
                && patientRepository.existsByEgn(updated.getEgn())) {
            throw new DuplicateResourceException("EGN " + updated.getEgn() + " is already registered");
        }
        existing.setName(updated.getName());
        existing.setEgn(updated.getEgn());
        existing.setPersonalDoctor(doctorRepository.getReferenceById(updated.getPersonalDoctor().getId()));
        existing.setHealthInsured(updated.isHealthInsured());
        return patientRepository.save(existing);
    }

    private void resolveDoctor(Patient patient) {
        if (patient.getPersonalDoctor() != null && patient.getPersonalDoctor().getId() != null) {
            patient.setPersonalDoctor(doctorRepository.getReferenceById(patient.getPersonalDoctor().getId()));
        }
    }

    public void delete(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient", id);
        }
        patientRepository.deleteById(id);
    }
}
