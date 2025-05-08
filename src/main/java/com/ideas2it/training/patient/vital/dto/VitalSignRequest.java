package com.ideas2it.training.patient.vital.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for creating/updating vital signs")
public class VitalSignRequest {

    @Schema(example = "72", description = "Heart pulse rate")
    private Integer pulse;

    @Schema(example = "120/80", description = "Blood pressure")
    private String bloodPressure;

    @Schema(example = "98.6", description = "Body temperature in Fahrenheit or Celsius")
    private Double temperature;

    @Schema(example = "16", description = "Respiration rate")
    private Integer respirations;

    @Schema(example = "110.5", description = "Blood sugar level")
    private Double bloodSugar;

    @Schema(example = "70.0", description = "Weight in kg or lbs")
    private Double weight;

    @Schema(example = "170.0", description = "Height in cm or inches")
    private Double height;

    @Schema(example = "98", description = "Oxygen saturation (SPO2)")
    private Integer spo2Sat;

    @Schema(example = "1.1", description = "Prothrombin time / INR")
    private Double ptInr;

    @Schema(example = "1001", description = "ID of the patient")
    private Long patientId;

    @Schema(example = "501", description = "ID of the user documenting the vital signs")
    private Long documentedBy;
}


