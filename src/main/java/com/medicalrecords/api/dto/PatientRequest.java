package com.medicalrecords.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PatientRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "EGN is required")
        @Pattern(regexp = "\\d{10}", message = "EGN must be exactly 10 digits") String egn,
        @NotNull(message = "Personal doctor ID is required") Long personalDoctorId,
        boolean healthInsured
) {}
