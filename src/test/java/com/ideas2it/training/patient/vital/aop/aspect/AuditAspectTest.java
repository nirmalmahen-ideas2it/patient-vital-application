package com.ideas2it.training.patient.vital.aop.aspect;

import com.ideas2it.training.patient.vital.aop.annotation.AuditLog;
import com.ideas2it.training.patient.vital.aop.enums.AuditAction;
import com.ideas2it.training.patient.vital.dto.AuditPayload;
import com.ideas2it.training.patient.vital.dto.VitalSignRequest;
import com.ideas2it.training.patient.vital.dto.VitalSignResponse;
import com.ideas2it.training.patient.vital.dto.patient.PatientInfo;
import com.ideas2it.training.patient.vital.dto.user.UserInfo;
import com.ideas2it.training.patient.vital.entity.VitalSign;
import com.ideas2it.training.patient.vital.producer.VitalKafkaProducer;
import com.ideas2it.training.patient.vital.repository.VitalSignRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditAspectTest {

    @InjectMocks
    private AuditAspect auditAspect;

    @Mock
    private VitalKafkaProducer auditProducer;

    @Mock
    private VitalSignRepository repository;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogExecutionTime_CreateAction() throws Throwable {
        // Arrange
        VitalSignRequest request = new VitalSignRequest();
        request.setPatientId(1L);

        VitalSignResponse response = new VitalSignResponse();
        response.setId(1L);
        response.setPulse(80);

        response.setDocumentedBy(UserInfo.builder().id(1L).firstName("John").lastName("Doe").build());
        response.setPatient(PatientInfo.builder().id(1L).firstName("Jane").lastName("Doe").build());

        when(joinPoint.getArgs()).thenReturn(new Object[]{request});
        when(joinPoint.proceed()).thenReturn(response);
        when(auditLog.action()).thenReturn(AuditAction.CREATE);
        when(repository.findTopByPatientIdOrderByCreatedDateDesc(1L)).thenReturn(null);

        // Act
        Object result = auditAspect.logExecutionTime(joinPoint, auditLog);

        // Assert
        assertNotNull(response);
        verify(auditProducer, times(1)).publishAuditData(any(AuditPayload.class));
    }

    @Test
    void testLogExecutionTime_UpdateAction_WithPreviousEntity() throws Throwable {
        // Arrange
        VitalSignRequest request = new VitalSignRequest();
        request.setPatientId(1L);

        VitalSignResponse response = new VitalSignResponse();
        response.setId(1L);
        response.setPulse(90);
        response.setDocumentedBy(UserInfo.builder().id(1L).firstName("John").lastName("Doe").build());
        response.setPatient(PatientInfo.builder().id(1L).firstName("Jane").lastName("Doe").build());

        VitalSign previous = new VitalSign();
        previous.setPulse(80);

        when(joinPoint.getArgs()).thenReturn(new Object[]{request});
        when(joinPoint.proceed()).thenReturn(response);
        when(auditLog.action()).thenReturn(AuditAction.UPDATE);
        when(repository.findTopByPatientIdOrderByCreatedDateDesc(1L)).thenReturn(previous);

        // Act
        Object result = auditAspect.logExecutionTime(joinPoint, auditLog);

        // Assert
        assertEquals(response, result);
        verify(auditProducer, times(1)).publishAuditData(any(AuditPayload.class));
    }

    @Test
    void testLogExecutionTime_NullRequest() throws Throwable {
        // Arrange
        when(joinPoint.getArgs()).thenReturn(new Object[]{null});
        when(joinPoint.proceed()).thenReturn(null);
        when(auditLog.action()).thenReturn(AuditAction.CREATE);

        // Act
        Object result = auditAspect.logExecutionTime(joinPoint, auditLog);

        // Assert
        assertNull(result);
        verify(auditProducer, never()).publishAuditData(any(AuditPayload.class));
    }

    @Test
    void testLogExecutionTime_ResponseNotVitalSignResponse() throws Throwable {
        // Arrange
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn("Not a VitalSignResponse");
        when(auditLog.action()).thenReturn(AuditAction.CREATE);

        // Act
        Object result = auditAspect.logExecutionTime(joinPoint, auditLog);

        // Assert
        assertEquals("Not a VitalSignResponse", result);
        verify(auditProducer, never()).publishAuditData(any(AuditPayload.class));
    }

    @Test
    void testBuildPayload_NullPreviousEntity() {
        // Arrange
        VitalSignResponse response = new VitalSignResponse();
        response.setId(1L);
        response.setPulse(80);
        response.setDocumentedBy(UserInfo.builder().id(1L).firstName("John").lastName("Doe").build());
        response.setPatient(PatientInfo.builder().id(1L).firstName("Jane").lastName("Doe").build());

        when(auditLog.action()).thenReturn(AuditAction.CREATE);

        // Act
        AuditPayload payload = ReflectionTestUtils.invokeMethod(auditAspect, "buildPayload", response, auditLog, null);

        // Assert
        assertNotNull(payload);
        assertEquals(1, payload.getAttributeChanges().size());
        assertEquals("Pulse", payload.getAttributeChanges().get(0).getAttributeName());
    }

    @Test
    void testBuildPayload_WithPreviousEntity() {
        // Arrange
        VitalSignResponse response = new VitalSignResponse();
        response.setId(1L);
        response.setPulse(90);
        response.setDocumentedBy(UserInfo.builder().id(1L).firstName("John").lastName("Doe").build());
        response.setPatient(PatientInfo.builder().id(1L).firstName("Jane").lastName("Doe").build());

        VitalSign previous = new VitalSign();
        previous.setPulse(80);

        when(auditLog.action()).thenReturn(AuditAction.UPDATE);

        // Act
        AuditPayload payload = ReflectionTestUtils.invokeMethod(auditAspect, "buildPayload", response, auditLog, previous);

        // Assert
        assertNotNull(payload);
        assertEquals(1, payload.getAttributeChanges().size());
        assertEquals("Pulse", payload.getAttributeChanges().get(0).getAttributeName());
        assertEquals("80", payload.getAttributeChanges().get(0).getOldValue());
        assertEquals("90", payload.getAttributeChanges().get(0).getNewValue());
    }
}
