package com.medicalrecords.repository;

import com.medicalrecords.model.Diagnosis;
import com.medicalrecords.model.Doctor;
import com.medicalrecords.model.Examination;
import com.medicalrecords.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    List<Examination> findByPatient(Patient patient);

    List<Examination> findByDoctor(Doctor doctor);

    List<Examination> findByDiagnosis(Diagnosis diagnosis);

    List<Examination> findByDoctorAndDateBetween(Doctor doctor, LocalDate from, LocalDate to);

    List<Examination> findByDateBetween(LocalDate from, LocalDate to);

    @Query("SELECT e FROM Examination e WHERE e.patient = :patient ORDER BY e.date DESC")
    List<Examination> findPatientHistory(@Param("patient") Patient patient);

    @Query("SELECT SUM(e.price) FROM Examination e WHERE e.paidByNhif = false")
    BigDecimal sumPatientPaidExaminations();

    @Query("SELECT SUM(e.price) FROM Examination e WHERE e.doctor = :doctor AND e.paidByNhif = false")
    BigDecimal sumPatientPaidByDoctor(@Param("doctor") Doctor doctor);

    @Query("SELECT COUNT(e) FROM Examination e WHERE e.doctor = :doctor")
    long countByDoctor(@Param("doctor") Doctor doctor);

    @Query("SELECT e.diagnosis, COUNT(e) as cnt FROM Examination e GROUP BY e.diagnosis ORDER BY cnt DESC")
    List<Object[]> findDiagnosisFrequency();

    List<Examination> findByDoctorAndDateBetweenOrderByDateDesc(Doctor doctor, LocalDate from, LocalDate to);
}
