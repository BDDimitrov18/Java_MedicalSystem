package com.medicalrecords.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identificationNumber;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Specialty is required")
    @Column(nullable = false)
    private String specialty;

    @Column(nullable = false)
    private boolean canBeGp = false;

    public Doctor(String identificationNumber, String name, String specialty, boolean canBeGp) {
        this.identificationNumber = identificationNumber;
        this.name = name;
        this.specialty = specialty;
        this.canBeGp = canBeGp;
    }

    public Doctor(String name, String specialty, boolean canBeGp) {
        this.name = name;
        this.specialty = specialty;
        this.canBeGp = canBeGp;
    }
}
