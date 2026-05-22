package com.medicalrecords.repository;

import com.medicalrecords.model.Doctor;
import com.medicalrecords.model.Patient;
import com.medicalrecords.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByDoctor(Doctor doctor);

    Optional<User> findByPatient(Patient patient);
}
