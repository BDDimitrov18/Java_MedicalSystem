package com.medicalrecords.api.dto;

import com.medicalrecords.model.Examination;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExaminationDto(
        Long id,
        LocalDate date,
        Long doctorId,
        String doctorName,
        Long patientId,
        String patientName,
        Long diagnosisId,
        String diagnosisCode,
        String diagnosisDescription,
        String prescribedTreatment,
        BigDecimal price,
        boolean paidByNhif
) {
    public static ExaminationDto from(Examination e) {
        return new ExaminationDto(
                e.getId(),
                e.getDate(),
                e.getDoctor() != null ? e.getDoctor().getId() : null,
                e.getDoctor() != null ? e.getDoctor().getName() : null,
                e.getPatient() != null ? e.getPatient().getId() : null,
                e.getPatient() != null ? e.getPatient().getName() : null,
                e.getDiagnosis() != null ? e.getDiagnosis().getId() : null,
                e.getDiagnosis() != null ? e.getDiagnosis().getCode() : null,
                e.getDiagnosis() != null ? e.getDiagnosis().getDescription() : null,
                e.getPrescribedTreatment(),
                e.getPrice(),
                e.isPaidByNhif()
        );
    }
}
