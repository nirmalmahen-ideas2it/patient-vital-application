package com.ideas2it.training.patient.vital.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideas2it.training.patient.vital.dto.AuditPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class VitalKafkaProducerTest {

    @InjectMocks
    private VitalKafkaProducer vitalKafkaProducer;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendVital_ValidMessage() {
        // Arrange
        String message = "Test vital sign message";

        // Act
        vitalKafkaProducer.sendVital(message);

        // Assert
        verify(kafkaTemplate, times(1)).send("vital-signs-topic", message);
    }

    @Test
    void testSendVital_NullMessage() {
        // Act
        vitalKafkaProducer.sendVital(null);

        // Assert
        verify(kafkaTemplate, times(1)).send("vital-signs-topic", null);
    }

    @Test
    void testPublishAuditData_ValidPayload() throws JsonProcessingException {
        // Arrange
        AuditPayload payload = new AuditPayload();
        String payloadJson = "{\"key\":\"value\"}";
        when(objectMapper.writeValueAsString(payload)).thenReturn(payloadJson);

        // Act
        vitalKafkaProducer.publishAuditData(payload);

        // Assert
        verify(kafkaTemplate, times(1)).send("audit-data", payloadJson);
    }

    @Test
    void testPublishAuditData_JsonProcessingException() throws JsonProcessingException {
        // Arrange
        AuditPayload payload = new AuditPayload();
        when(objectMapper.writeValueAsString(payload)).thenThrow(JsonProcessingException.class);

        // Act & Assert
        assertThrows(JsonProcessingException.class, () -> vitalKafkaProducer.publishAuditData(payload));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}
