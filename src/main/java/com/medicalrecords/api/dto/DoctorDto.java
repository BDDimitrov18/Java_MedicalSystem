package com.medicalrecords.api.dto;

import com.medicalrecords.model.Doctor;

public record DoctorDto(
        Long id,
        String identificationNumber,
        String name,
        String specialty,
        boolean canBeGp
) {
    public static DoctorDto from(Doctor d) {
        return new DoctorDto(d.getId(), d.getIdentificationNumber(), d.getName(), d.getSpecialty(), d.isCanBeGp());
    }
}
