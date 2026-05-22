package com.medicalrecords.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "diagnoses")
@Data
@NoArgsConstructor
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Code is required")
    @Column(unique = true, nullable = false)
    private String code;

    @NotBlank(message = "Description is required")
    @Column(nullable = false)
    private String description;

    public Diagnosis(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
