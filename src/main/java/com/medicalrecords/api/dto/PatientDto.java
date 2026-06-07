package com.medicalrecords.api.dto;

import com.medicalrecords.model.Patient;

public record PatientDto(
        Long id,
        String name,
        String egn,
        Long personalDoctorId,
        String personalDoctorName,
        boolean healthInsured
) {
    public static PatientDto from(Patient p) {
        return new PatientDto(
                p.getId(),
                p.getName(),
                p.getEgn(),
                p.getPersonalDoctor() != null ? p.getPersonalDoctor().getId() : null,
                p.getPersonalDoctor() != null ? p.getPersonalDoctor().getName() : null,
                p.isHealthInsured()
        );
    }
}
