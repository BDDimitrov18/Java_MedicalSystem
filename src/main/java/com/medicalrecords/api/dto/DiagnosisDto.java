package com.medicalrecords.api.dto;

import com.medicalrecords.model.Diagnosis;

public record DiagnosisDto(Long id, String code, String description) {
    public static DiagnosisDto from(Diagnosis d) {
        return new DiagnosisDto(d.getId(), d.getCode(), d.getDescription());
    }
}
