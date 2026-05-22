package com.medicalrecords.service;

import com.medicalrecords.exception.DuplicateResourceException;
import com.medicalrecords.exception.ResourceNotFoundException;
import com.medicalrecords.model.Diagnosis;
import com.medicalrecords.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;

    public List<Diagnosis> findAll() {
        return diagnosisRepository.findAll();
    }

    public Diagnosis findById(Long id) {
        return diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis", id));
    }

    public Diagnosis save(Diagnosis diagnosis) {
        if (diagnosis.getId() == null && diagnosisRepository.existsByCode(diagnosis.getCode())) {
            throw new DuplicateResourceException("Diagnosis with code " + diagnosis.getCode() + " already exists");
        }
        return diagnosisRepository.save(diagnosis);
    }

    public Diagnosis update(Long id, Diagnosis updated) {
        Diagnosis existing = findById(id);
        if (!existing.getCode().equals(updated.getCode())
                && diagnosisRepository.existsByCode(updated.getCode())) {
            throw new DuplicateResourceException("Diagnosis code " + updated.getCode() + " already exists");
        }
        existing.setCode(updated.getCode());
        existing.setDescription(updated.getDescription());
        return diagnosisRepository.save(existing);
    }

    public void delete(Long id) {
        if (!diagnosisRepository.existsById(id)) {
            throw new ResourceNotFoundException("Diagnosis", id);
        }
        diagnosisRepository.deleteById(id);
    }
}
