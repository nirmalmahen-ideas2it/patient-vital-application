package com.ideas2it.training.patient.vital.dto.patient;

import lombok.Data;

/**
 * DTO for returning Physician data.
 */
@Data
public class PhysicianInfo {
    private Long id;
    private String name;
    private String contactNumber;
    private String specialization;
    private String licenseNumber;
    private String hospital;
}

