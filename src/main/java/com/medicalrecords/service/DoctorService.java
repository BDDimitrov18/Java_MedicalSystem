package com.medicalrecords.service;

import com.medicalrecords.exception.ResourceNotFoundException;
import com.medicalrecords.model.Doctor;
import com.medicalrecords.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    public Doctor findById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", id));
    }

    public Doctor findByIdentificationNumber(String number) {
        return doctorRepository.findByIdentificationNumber(number)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor with ID number " + number + " not found"));
    }

    public List<Doctor> findAllGps() {
        return doctorRepository.findByCanBeGpTrue();
    }

    public Doctor save(Doctor doctor) {
        if (doctor.getIdentificationNumber() == null || doctor.getIdentificationNumber().isBlank()) {
            doctor.setIdentificationNumber(generateIdentificationNumber(doctor.getSpecialty()));
        }
        return doctorRepository.save(doctor);
    }

    public Doctor update(Long id, Doctor updated) {
        Doctor existing = findById(id);
        existing.setName(updated.getName());
        existing.setSpecialty(updated.getSpecialty());
        existing.setCanBeGp(updated.isCanBeGp());
        return doctorRepository.save(existing);
    }

    private String generateIdentificationNumber(String specialty) {
        String prefix = buildPrefix(specialty);
        long count = doctorRepository.countByIdentificationNumberStartingWith(prefix);
        return String.format("%s%03d", prefix, count + 1);
    }

    private String buildPrefix(String specialty) {
        String[] words = specialty.trim().split("\\s+");
        if (words.length == 1) {
            return specialty.substring(0, Math.min(3, specialty.length())).toUpperCase();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(3, words.length); i++) {
            if (!words[i].isEmpty()) sb.append(words[i].charAt(0));
        }
        return sb.toString().toUpperCase();
    }

    public void delete(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Doctor", id);
        }
        doctorRepository.deleteById(id);
    }
}
