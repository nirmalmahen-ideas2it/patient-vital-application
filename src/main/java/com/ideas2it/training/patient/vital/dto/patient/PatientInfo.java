package com.ideas2it.training.patient.vital.dto.patient;

import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for returning Patient data.
 */
@Data
public class PatientInfo {
    private Long id;
    private String medicalRecordNumber;
    private LocalDate startOfCareDate;
    private String status;
    private String firstName;
    private String lastName;
    private String sex;
    private LocalDate birthDate;
    private ReferralInfo referralInfo;
    private Diagnoses diagnoses;
    private PhysicianInfo primaryPhysician;
}

