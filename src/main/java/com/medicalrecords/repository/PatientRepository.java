package com.medicalrecords.repository;

import com.medicalrecords.model.Doctor;
import com.medicalrecords.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEgn(String egn);

    List<Patient> findByPersonalDoctor(Doctor doctor);

    long countByPersonalDoctor(Doctor doctor);

    boolean existsByEgn(String egn);
}
