package com.medicalrecords.api.dto;

import jakarta.validation.constraints.NotBlank;

public record DoctorRequest(
        String identificationNumber,
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Specialty is required") String specialty,
        boolean canBeGp
) {}
