package com.ideas2it.training.patient.vital.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideas2it.training.patient.vital.dto.AuditPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class VitalKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(VitalKafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public VitalKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendVital(String message) {
        logger.info("Publishing vital sign message: {}", message);
        kafkaTemplate.send("vital-signs-topic", message);
    }

    public void publishAuditData(AuditPayload payload) throws JsonProcessingException {
        kafkaTemplate.send("audit-data", objectMapper.writeValueAsString(payload));
    }
}

