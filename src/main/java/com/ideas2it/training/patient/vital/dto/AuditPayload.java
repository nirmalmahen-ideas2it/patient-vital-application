package com.ideas2it.training.patient.vital.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AuditPayload {
    private Long userId;
    private String username;
    private Long patientId;
    private String patientName;
    private String entityType;
    private Long entityId;
    private LocalDateTime logDate;
    private String action;
    private String description;
    private List<AttributeChange> attributeChanges;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttributeChange {
        private String attributeName;
        private String oldValue;
        private String newValue;
    }
}
