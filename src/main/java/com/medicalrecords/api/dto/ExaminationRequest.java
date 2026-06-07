package com.medicalrecords.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExaminationRequest(
        @NotNull(message = "Date is required") LocalDate date,
        @NotNull(message = "Doctor ID is required") Long doctorId,
        @NotNull(message = "Patient ID is required") Long patientId,
        @NotNull(message = "Diagnosis ID is required") Long diagnosisId,
        String prescribedTreatment,
        @DecimalMin(value = "0.0", message = "Price must be non-negative") BigDecimal price
) {}
