package com.medicalrecords.service;

import com.medicalrecords.exception.DuplicateResourceException;
import com.medicalrecords.model.Doctor;
import com.medicalrecords.model.Patient;
import com.medicalrecords.model.Role;
import com.medicalrecords.model.User;
import com.medicalrecords.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByDoctor(Doctor doctor) {
        return userRepository.findByDoctor(doctor);
    }

    public Optional<User> findByPatient(Patient patient) {
        return userRepository.findByPatient(patient);
    }

    public User assignToDoctor(Doctor doctor, String username, String rawPassword) {
        Optional<User> existing = userRepository.findByDoctor(doctor);
        if (existing.isPresent()) {
            return updateUser(existing.get(), username, rawPassword);
        }
        checkUsernameAvailable(username);
        User user = new User(username, passwordEncoder.encode(rawPassword), Role.DOCTOR);
        user.setDoctor(doctor);
        return userRepository.save(user);
    }

    public User assignToPatient(Patient patient, String username, String rawPassword) {
        Optional<User> existing = userRepository.findByPatient(patient);
        if (existing.isPresent()) {
            return updateUser(existing.get(), username, rawPassword);
        }
        checkUsernameAvailable(username);
        User user = new User(username, passwordEncoder.encode(rawPassword), Role.PATIENT);
        user.setPatient(patient);
        return userRepository.save(user);
    }

    private User updateUser(User user, String username, String rawPassword) {
        if (!user.getUsername().equals(username)) {
            checkUsernameAvailable(username);
            user.setUsername(username);
        }
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        return userRepository.save(user);
    }

    private void checkUsernameAvailable(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username '" + username + "' is already taken");
        }
    }
}
