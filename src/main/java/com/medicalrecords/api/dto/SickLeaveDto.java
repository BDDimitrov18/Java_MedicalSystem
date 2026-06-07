package com.medicalrecords.api.dto;

import com.medicalrecords.model.SickLeave;

import java.time.LocalDate;

public record SickLeaveDto(
        Long id,
        Long examinationId,
        Long patientId,
        String patientName,
        LocalDate startDate,
        int numberOfDays
) {
    public static SickLeaveDto from(SickLeave s) {
        Long patientId = s.getExamination() != null && s.getExamination().getPatient() != null
                ? s.getExamination().getPatient().getId() : null;
        String patientName = s.getExamination() != null && s.getExamination().getPatient() != null
                ? s.getExamination().getPatient().getName() : null;
        return new SickLeaveDto(
                s.getId(),
                s.getExamination() != null ? s.getExamination().getId() : null,
                patientId,
                patientName,
                s.getStartDate(),
                s.getNumberOfDays()
        );
    }
}
