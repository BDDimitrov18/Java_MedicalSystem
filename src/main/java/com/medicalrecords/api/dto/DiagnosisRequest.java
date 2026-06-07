package com.medicalrecords.api.dto;

import jakarta.validation.constraints.NotBlank;

public record DiagnosisRequest(
        @NotBlank(message = "Code is required") String code,
        @NotBlank(message = "Description is required") String description
) {}
