package com.medicalrecords.service;

import com.medicalrecords.exception.AccessDeniedException;
import com.medicalrecords.exception.ResourceNotFoundException;
import com.medicalrecords.model.*;
import com.medicalrecords.repository.ExaminationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExaminationService {

    private final ExaminationRepository examinationRepository;

    public List<Examination> findAll() {
        return examinationRepository.findAll();
    }

    public Examination findById(Long id) {
        return examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examination", id));
    }

    public List<Examination> findByPatient(Patient patient) {
        return examinationRepository.findByPatient(patient);
    }

    public List<Examination> findByDoctor(Doctor doctor) {
        return examinationRepository.findByDoctor(doctor);
    }

    public List<Examination> findByDiagnosis(Diagnosis diagnosis) {
        return examinationRepository.findByDiagnosis(diagnosis);
    }

    public List<Examination> findPatientHistory(Patient patient) {
        return examinationRepository.findPatientHistory(patient);
    }

    public List<Examination> findByDoctorAndPeriod(Doctor doctor, LocalDate from, LocalDate to) {
        return examinationRepository.findByDoctorAndDateBetween(doctor, from, to);
    }

    public List<Examination> findByPeriod(LocalDate from, LocalDate to) {
        return examinationRepository.findByDateBetween(from, to);
    }

    public Examination save(Examination examination) {
        return examinationRepository.save(examination);
    }

    public Examination update(Long id, Examination updated, Doctor currentDoctor) {
        Examination existing = findById(id);
        if (currentDoctor != null && !existing.getDoctor().getId().equals(currentDoctor.getId())) {
            throw new AccessDeniedException("You can only edit your own examinations");
        }
        existing.setDate(updated.getDate());
        existing.setDiagnosis(updated.getDiagnosis());
        existing.setPrescribedTreatment(updated.getPrescribedTreatment());
        existing.setPrice(updated.getPrice());
        return examinationRepository.save(existing);
    }

    public void delete(Long id) {
        if (!examinationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Examination", id);
        }
        examinationRepository.deleteById(id);
    }
}
