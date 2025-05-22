package com.ideas2it.training.patient.vital.aop.aspect;

import com.ideas2it.training.patient.vital.aop.annotation.AuditLog;
import com.ideas2it.training.patient.vital.aop.enums.AuditAction;
import com.ideas2it.training.patient.vital.dto.AuditPayload;
import com.ideas2it.training.patient.vital.dto.VitalSignRequest;
import com.ideas2it.training.patient.vital.dto.VitalSignResponse;
import com.ideas2it.training.patient.vital.entity.VitalSign;
import com.ideas2it.training.patient.vital.producer.VitalKafkaProducer;
import com.ideas2it.training.patient.vital.repository.VitalSignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final VitalKafkaProducer auditProducer;
    private final VitalSignRepository repository;

    @Around("@annotation(auditLog)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        long start = System.currentTimeMillis();

        Object[] args = joinPoint.getArgs();
        VitalSignRequest request = extractRequest(args);
        VitalSign previous = null;
        if ((AuditAction.CREATE.equals(auditLog.action()) || AuditAction.UPDATE.equals(auditLog.action())) && request != null) {
            previous = repository.findTopByPatientIdOrderByCreatedDateDesc(request.getPatientId());
        }
        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;

        log.info("Method {} executed in {} ms", joinPoint.getSignature(), duration);

        // Only process for save/update/delete VitalSign
        if (result instanceof VitalSignResponse response) {
            AuditPayload payload = buildPayload(response, auditLog, previous);
            auditProducer.publishAuditData(payload);
        }

        return result;
    }

    private VitalSignRequest extractRequest(Object[] args) {
        return Arrays.stream(args)
            .filter(arg -> arg instanceof VitalSignRequest)
            .map(arg -> (VitalSignRequest) arg)
            .findFirst()
            .orElse(null);
    }

    private AuditPayload buildPayload(VitalSignResponse response, AuditLog auditLog, VitalSign oldEntity) {
        List<AuditPayload.AttributeChange> changes = new ArrayList<>();
        AuditAction currentAction = auditLog.action();
        if (AuditAction.CREATE.equals(auditLog.action()) || AuditAction.UPDATE.equals(auditLog.action())) {
            if (oldEntity == null) {
                if (response.getPulse() != null) {
                    changes.add(new AuditPayload.AttributeChange("Pulse", StringUtils.EMPTY, String.valueOf(response.getPulse())));
                }
                if (response.getBloodPressure() != null) {
                    changes.add(new AuditPayload.AttributeChange("Blood Pressure", StringUtils.EMPTY, String.valueOf(response.getBloodPressure())));
                }
                if (response.getTemperature() != null) {
                    changes.add(new AuditPayload.AttributeChange("Temperature", StringUtils.EMPTY, String.valueOf(response.getTemperature())));
                }
                if (response.getRespirations() != null) {
                    changes.add(new AuditPayload.AttributeChange("Respirations", StringUtils.EMPTY, String.valueOf(response.getRespirations())));
                }
                if (response.getBloodSugar() != null) {
                    changes.add(new AuditPayload.AttributeChange("Blood Sugar", StringUtils.EMPTY, String.valueOf(response.getBloodSugar())));
                }
                if (response.getWeight() != null) {
                    changes.add(new AuditPayload.AttributeChange("Weight", StringUtils.EMPTY, String.valueOf(response.getWeight())));
                }
                if (response.getHeight() != null) {
                    changes.add(new AuditPayload.AttributeChange("Height", StringUtils.EMPTY, String.valueOf(response.getHeight())));
                }
                if (response.getSpo2Sat() != null) {
                    changes.add(new AuditPayload.AttributeChange("SpO2 Saturation", StringUtils.EMPTY, String.valueOf(response.getSpo2Sat())));
                }
                if (response.getPtInr() != null) {
                    changes.add(new AuditPayload.AttributeChange("PT INR", StringUtils.EMPTY, String.valueOf(response.getPtInr())));
                }
            } else {
                // Fetch from DB and compare for actual change tracking
                if (oldEntity.getPulse() != null && !oldEntity.getPulse().equals(response.getPulse())) {
                    changes.add(new AuditPayload.AttributeChange("Pulse", String.valueOf(oldEntity.getPulse()), String.valueOf(response.getPulse())));
                }
                if (oldEntity.getBloodPressure() != null && !oldEntity.getBloodPressure().equals(response.getBloodPressure())) {
                    changes.add(new AuditPayload.AttributeChange("Blood Pressure", oldEntity.getBloodPressure(), response.getBloodPressure()));
                }
                if (oldEntity.getTemperature() != null && !oldEntity.getTemperature().equals(response.getTemperature())) {
                    changes.add(new AuditPayload.AttributeChange("Temperature", String.valueOf(oldEntity.getTemperature()), String.valueOf(response.getTemperature())));
                }
                if (oldEntity.getRespirations() != null && !oldEntity.getRespirations().equals(response.getRespirations())) {
                    changes.add(new AuditPayload.AttributeChange("Respirations", String.valueOf(oldEntity.getRespirations()), String.valueOf(response.getRespirations())));
                }
                if (oldEntity.getBloodSugar() != null && !oldEntity.getBloodSugar().equals(response.getBloodSugar())) {
                    changes.add(new AuditPayload.AttributeChange("Blood Sugar", String.valueOf(oldEntity.getBloodSugar()), String.valueOf(response.getBloodSugar())));
                }
                if (oldEntity.getWeight() != null && !oldEntity.getWeight().equals(response.getWeight())) {
                    changes.add(new AuditPayload.AttributeChange("Weight", String.valueOf(oldEntity.getWeight()), String.valueOf(response.getWeight())));
                }
                if (oldEntity.getHeight() != null && !oldEntity.getHeight().equals(response.getHeight())) {
                    changes.add(new AuditPayload.AttributeChange("Height", String.valueOf(oldEntity.getHeight()), String.valueOf(response.getHeight())));
                }
                if (oldEntity.getSpo2Sat() != null && !oldEntity.getSpo2Sat().toString().equals(response.getSpo2Sat().toString())) {
                    changes.add(new AuditPayload.AttributeChange("SpO2 Saturation", String.valueOf(oldEntity.getSpo2Sat()), String.valueOf(response.getSpo2Sat())));
                }
                if (oldEntity.getPtInr() != null && !oldEntity.getPtInr().toString().equals(response.getPtInr())) {
                    changes.add(new AuditPayload.AttributeChange("PT INR", String.valueOf(oldEntity.getPtInr()), String.valueOf(response.getPtInr())));
                }
                if (auditLog.action() == AuditAction.CREATE) {
                    currentAction = AuditAction.UPDATE;
                }
            }

        }
        return AuditPayload.builder()
            .userId(response.getDocumentedBy().getId())
            .username(response.getDocumentedBy().getFirstName() + " " + response.getDocumentedBy().getLastName())
            .patientId(response.getPatient().getId())
            .patientName(response.getPatient().getFirstName() + " " + response.getPatient().getLastName())
            .entityType("VitalSign")
            .entityId(response.getId())
            .logDate(LocalDateTime.now())
            .action(currentAction.name())
            .description(auditLog.description())
            .attributeChanges(changes)
            .build();
    }
}
