package com.ideas2it.training.patient.vital.dto;

import com.ideas2it.training.patient.vital.dto.patient.PatientInfo;
import com.ideas2it.training.patient.vital.dto.user.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing a Vital Sign record response")
public class VitalSignResponse {

    private Long id;

    @Schema(description = "Pulse rate of the patient")
    private Integer pulse;

    @Schema(description = "Blood pressure reading")
    private String bloodPressure;

    @Schema(description = "Body temperature in Fahrenheit")
    private Double temperature;

    @Schema(description = "Respiration rate")
    private Integer respirations;

    @Schema(description = "Blood sugar level")
    private Double bloodSugar;

    @Schema(description = "Weight in kilograms")
    private Double weight;

    @Schema(description = "Height in centimeters")
    private Double height;

    @Schema(description = "Oxygen saturation (SPO2)")
    private Integer spo2Sat;

    @Schema(description = "Prothrombin Time / International Normalized Ratio")
    private String ptInr;

    // Minimal patient info
    @Schema(description = "Basic patient information")
    private PatientInfo patient;

    // Minimal user info
    @Schema(description = "User who documented the vital sign")
    private UserInfo documentedBy;

}



