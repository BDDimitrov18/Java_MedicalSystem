package com.medicalrecords.service;

import com.medicalrecords.exception.ResourceNotFoundException;
import com.medicalrecords.model.Examination;
import com.medicalrecords.model.SickLeave;
import com.medicalrecords.repository.SickLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SickLeaveService {

    private final SickLeaveRepository sickLeaveRepository;

    public List<SickLeave> findAll() {
        return sickLeaveRepository.findAll();
    }

    public SickLeave findById(Long id) {
        return sickLeaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sick leave", id));
    }

    public List<SickLeave> findByExamination(Examination examination) {
        return sickLeaveRepository.findByExamination(examination);
    }

    public List<SickLeave> findByPatientId(Long patientId) {
        return sickLeaveRepository.findByPatientId(patientId);
    }

    public SickLeave save(SickLeave sickLeave) {
        return sickLeaveRepository.save(sickLeave);
    }

    public SickLeave update(Long id, SickLeave updated) {
        SickLeave existing = findById(id);
        existing.setStartDate(updated.getStartDate());
        existing.setNumberOfDays(updated.getNumberOfDays());
        existing.setExamination(updated.getExamination());
        return sickLeaveRepository.save(existing);
    }

    public void delete(Long id) {
        if (!sickLeaveRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sick leave", id);
        }
        sickLeaveRepository.deleteById(id);
    }
}
