package com.medicalrecords.repository;

import com.medicalrecords.model.Doctor;
import com.medicalrecords.model.Examination;
import com.medicalrecords.model.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {

    List<SickLeave> findByExamination(Examination examination);

    @Query("SELECT MONTH(sl.startDate), YEAR(sl.startDate), COUNT(sl) FROM SickLeave sl " +
           "GROUP BY YEAR(sl.startDate), MONTH(sl.startDate) ORDER BY COUNT(sl) DESC")
    List<Object[]> findMonthWithMostSickLeaves();

    @Query("SELECT sl.examination.doctor, COUNT(sl) as cnt FROM SickLeave sl " +
           "GROUP BY sl.examination.doctor ORDER BY cnt DESC")
    List<Object[]> findDoctorsWithMostSickLeaves();

    @Query("SELECT sl FROM SickLeave sl WHERE sl.examination.patient.id = :patientId")
    List<SickLeave> findByPatientId(Long patientId);
}
