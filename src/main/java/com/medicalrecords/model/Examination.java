package com.medicalrecords.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "examinations")
@Data
@NoArgsConstructor
public class Examination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required")
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "diagnosis_id", nullable = false)
    @NotNull(message = "Diagnosis is required")
    private Diagnosis diagnosis;

    @Column
    private String prescribedTreatment;

    @DecimalMin(value = "0.0", message = "Price must be non-negative")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    // true = paid by NHIF (patient has insurance), false = paid by patient
    @Column(nullable = false)
    private boolean paidByNhif = false;

    @PrePersist
    @PreUpdate
    private void updatePaymentStatus() {
        if (patient != null) {
            this.paidByNhif = patient.isHealthInsured();
        }
    }

    public Examination(LocalDate date, Doctor doctor, Patient patient, Diagnosis diagnosis,
                       String prescribedTreatment, BigDecimal price) {
        this.date = date;
        this.doctor = doctor;
        this.patient = patient;
        this.diagnosis = diagnosis;
        this.prescribedTreatment = prescribedTreatment;
        this.price = price;
        this.paidByNhif = patient != null && patient.isHealthInsured();
    }
}
