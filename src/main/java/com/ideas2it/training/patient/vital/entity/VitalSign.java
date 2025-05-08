package com.ideas2it.training.patient.vital.entity;

import com.ideas2it.training.patient.vital.domain.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "vital_signs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VitalSign extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pulse")
    private Integer pulse;

    @Column(name = "blood_pressure")
    private String bloodPressure;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "respirations")
    private Integer respirations;

    @Column(name = "blood_sugar")
    private Double bloodSugar;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "height")
    private Double height;

    @Column(name = "spo2_sat")
    private Double spo2Sat;

    @Column(name = "pt_inr")
    private Double ptInr;

    @Column(name = "patient_id", nullable = false)
    @JsonIgnore
    private Long patientId;

    @Column(name = "documented_by", nullable = false)
    @JsonIgnore
    private Long documentedBy;

}


