package com.medicalrecords.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "sick_leaves")
@Data
@NoArgsConstructor
public class SickLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "examination_id", nullable = false)
    @NotNull(message = "Examination is required")
    private Examination examination;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @Min(value = 1, message = "Number of days must be at least 1")
    @Column(nullable = false)
    private int numberOfDays;

    public SickLeave(Examination examination, LocalDate startDate, int numberOfDays) {
        this.examination = examination;
        this.startDate = startDate;
        this.numberOfDays = numberOfDays;
    }
}
