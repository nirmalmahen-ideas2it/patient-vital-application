package com.ideas2it.training.patient.vital.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class VitalKafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(VitalKafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public VitalKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendVital(String message) {
        logger.info("Publishing vital sign message: {}", message);
        kafkaTemplate.send("vital-signs-topic", message);
    }
}

