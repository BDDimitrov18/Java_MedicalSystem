package com.medicalrecords.repository;

import com.medicalrecords.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByIdentificationNumber(String identificationNumber);

    List<Doctor> findByCanBeGpTrue();

    boolean existsByIdentificationNumber(String identificationNumber);

    long countByIdentificationNumberStartingWith(String prefix);
}
