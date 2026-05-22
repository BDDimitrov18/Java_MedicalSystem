package com.medicalrecords.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "EGN is required")
    @Pattern(regexp = "\\d{10}", message = "EGN must be exactly 10 digits")
    @Column(unique = true, nullable = false)
    private String egn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "personal_doctor_id", nullable = false)
    private Doctor personalDoctor;

    // true if patient has paid health insurance for the last 6 months
    @Column(nullable = false)
    private boolean healthInsured = false;

    public Patient(String name, String egn, Doctor personalDoctor, boolean healthInsured) {
        this.name = name;
        this.egn = egn;
        this.personalDoctor = personalDoctor;
        this.healthInsured = healthInsured;
    }
}
